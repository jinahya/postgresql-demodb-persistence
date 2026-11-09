package com.github.jinahya.postgresql.demodb.persistence.mapped;

/*-
 * #%L
 * postgres-demonstration-persistence
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.format.jackson.Jackson3JsonFormatMapper;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing the {@link LocaleNameWrapper}, on its own, through Jackson, and as an
 * attribute Hibernate ORM maps as JSON.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class LocaleNameWrapper_Test {

    /**
     * An entity of a single {@link LocaleNameWrapper} attribute, for this test's own persistence
     * unit only.
     */
    @Entity
    @Table(name = "named")
    public static class Named {

        @Id
        private Integer id;

        @JdbcTypeCode(SqlTypes.JSON)
        private LocaleNameWrapper names;
    }

    private static LocaleNameWrapper anaa() {
        final var names = new LinkedHashMap<Locale, String>();
        names.put(Locale.of("ru"), "Анаа");
        names.put(Locale.ENGLISH, "Anaa");
        return LocaleNameWrapper.of(names);
    }

    @Test
    void of_Ordered_ByLanguage() {
        final var wrapper = anaa();
        assertThat(wrapper.getEntries()).containsExactly(
                Map.entry(Locale.ENGLISH, "Anaa"), Map.entry(Locale.of("ru"), "Анаа"));
        assertThat(wrapper.getName(Locale.US)).isEqualTo("Anaa");
        assertThat(wrapper.getName(Locale.KOREAN)).isNull();
    }

    @Test
    void of_IllegalArgumentException_TwoNamesOfALanguage() {
        assertThatThrownBy(() -> LocaleNameWrapper.of(Map.of(Locale.US, "a", Locale.UK, "b")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withName_withoutName_NewInstances() {
        final var wrapper = anaa();
        final var renamed = wrapper.withName(Locale.ENGLISH, "Anaa Airport");
        assertThat(renamed).isNotEqualTo(wrapper);
        assertThat(wrapper.getName(Locale.ENGLISH)).isEqualTo("Anaa");
        assertThat(renamed.getName(Locale.ENGLISH)).isEqualTo("Anaa Airport");
        assertThat(wrapper.withoutName(Locale.KOREAN)).isSameAs(wrapper);
        assertThat(wrapper.withoutName(Locale.ENGLISH).getEntries()).containsOnlyKeys(Locale.of("ru"));
    }

    /**
     * Verifies that an instance is serialized and read back as it was, its order kept.
     */
    @Test
    void serializable_RoundTrip() throws IOException, ClassNotFoundException {
        final var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(anaa());
        }
        try (var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            final var read = (LocaleNameWrapper) ois.readObject();
            assertThat(read).isEqualTo(anaa());
            assertThat(read.withName(Locale.KOREAN, "아나").getEntries().keySet())
                    .containsExactly(Locale.ENGLISH, Locale.KOREAN, Locale.of("ru"));
        }
    }

    /**
     * Verifies that Jackson writes an instance as a JSON object keyed by language, and reads it
     * back as it was.
     */
    @Test
    void jackson_RoundTrip() {
        final var mapper = JsonMapper.builder().build();
        final var json = mapper.writeValueAsString(anaa());
        assertThat(json).isEqualTo("{\"en\":\"Anaa\",\"ru\":\"Анаа\"}");
        assertThat(mapper.readValue(json, LocaleNameWrapper.class)).isEqualTo(anaa());
    }

    /**
     * Verifies that Hibernate ORM, with Jackson 3 as its JSON format mapper, writes an attribute
     * of the type as a JSON object keyed by language, reads it back, and sees a replaced instance
     * as a change.
     */
    @Test
    void hibernate_RoundTrip() {
        try (var factory = new PersistenceConfiguration("locale-name-wrapper")
                .managedClass(Named.class)
                .property(PersistenceConfiguration.JDBC_URL,
                          "jdbc:h2:mem:locale-name-wrapper;DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
                .property(PersistenceConfiguration.JDBC_USER, "sa")
                .property(PersistenceConfiguration.JDBC_PASSWORD, "")
                .property("hibernate.hbm2ddl.auto", "create-drop")
                .createEntityManagerFactory()) {
            assertThat(factory.unwrap(SessionFactoryImplementor.class).getSessionFactoryOptions()
                               .getJsonFormatMapper())
                    .isInstanceOf(Jackson3JsonFormatMapper.class);
            // @Immutable; Hibernate ORM keeps no copy of a value
            assertThat(factory.unwrap(SessionFactoryImplementor.class).getMappingMetamodel()
                               .getEntityDescriptor(Named.class).findAttributeMapping("names")
                               .getAttributeMetadata().getMutabilityPlan().isMutable())
                    .isFalse();
            factory.runInTransaction(em -> {
                final var named = new Named();
                named.id = 1;
                named.names = anaa();
                em.persist(named);
            });
            factory.runInTransaction(em -> {
                final var text = (String) em.createNativeQuery(
                        "SELECT CAST(names AS VARCHAR) FROM named WHERE id = 1", String.class)
                        .getSingleResult();
                log.debug("text: {}", text);
                assertThat(text).isEqualTo("{\"en\":\"Anaa\",\"ru\":\"Анаа\"}");
                final var named = em.find(Named.class, 1);
                assertThat(named.names).isEqualTo(anaa());
                named.names = named.names.withName(Locale.KOREAN, "아나");
            });
            factory.runInTransaction(em -> {
                assertThat(em.find(Named.class, 1).names.getName(Locale.KOREAN)).isEqualTo("아나");
            });
        }
    }
}

