package com.example.demo.entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import jakarta.persistence.Entity;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class EntityArchitectureTest {

    private static final JavaClasses ENTITY_CLASSES = importEntityClasses();

    @Test
    void entityPackageContainsOnlyJpaEntities() {
        classes()
                .that().resideInAPackage("..entity..")
                .should().beAnnotatedWith(Entity.class)
                .check(ENTITY_CLASSES);
    }

    @Test
    void entityFieldsAreNotPublic() {
        fields()
                .that().areDeclaredInClassesThat().resideInAPackage("..entity..")
                .should().notBePublic()
                .check(ENTITY_CLASSES);
    }

    private static JavaClasses importEntityClasses() {
        try {
            return new ClassFileImporter().importPath(
                    Paths.get(OrderEntity.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Unable to locate compiled entity classes", exception);
        }
    }
}
