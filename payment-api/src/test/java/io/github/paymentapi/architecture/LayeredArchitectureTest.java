package io.github.paymentapi.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packages = "io.github.paymentapi",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule layered_architecture_is_respected = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Application").definedBy("io.github.paymentapi.application..")
            .layer("Domain").definedBy("io.github.paymentapi.domain..")
            .optionalLayer("Infrastructure").definedBy("io.github.paymentapi.infrastructure..")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule domain_must_not_depend_on_application_or_infrastructure = noClasses()
            .that().resideInAPackage("io.github.paymentapi.domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                    "io.github.paymentapi.application..",
                    "io.github.paymentapi.infrastructure.."
            );

    @ArchTest
    static final ArchRule packages_must_be_free_of_cycles =
            slices().matching("io.github.paymentapi.(*)..").should().beFreeOfCycles();

    @ArchTest
    static final ArchRule infrastructure_must_use_approved_subpackages = classes()
            .that().resideInAPackage("io.github.paymentapi.infrastructure..")
            .should().resideInAnyPackage(
                    "io.github.paymentapi.infrastructure",
                    "io.github.paymentapi.infrastructure.config..",
                    "io.github.paymentapi.infrastructure.adapter.."
            );
}
