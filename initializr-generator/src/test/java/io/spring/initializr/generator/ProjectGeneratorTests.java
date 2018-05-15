/*
 * Copyright 2012-2018 the original author or authors.
 *
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
 */

package io.spring.initializr.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.test.generator.ProjectAssert;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import io.spring.initializr.util.VersionProperty;

/**
 * Tests for {@link ProjectGenerator}
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
@Ignore
public class ProjectGeneratorTests extends AbstractProjectGeneratorTests {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void defaultMavenPom() {
        ProjectRequest request = createProjectRequest("web");
        generateMavenPom(request).hasNoRepository().hasSpringBootStarterDependency("web");
        verifyProjectSuccessfulEventFor(request);
    }

    @Test
    public void defaultProject() {
        ProjectRequest request = createProjectRequest("web");
        generateProject(request).isJavaProject().isMavenProject().pomAssert()
                .hasNoRepository().hasSpringBootStarterDependency("web");
        verifyProjectSuccessfulEventFor(request);
    }

    @Test
    public void noDependencyAddsRootStarter() {
        ProjectRequest request = createProjectRequest();
        generateProject(request).isJavaProject().isMavenProject().pomAssert()
                .hasSpringBootStarterRootDependency();
    }

    @Test
    public void mavenPomWithBootSnapshot() {
        ProjectRequest request = createProjectRequest("web");
        request.setBootVersion("1.0.1.BUILD-SNAPSHOT");
        generateMavenPom(request).hasSnapshotRepository()
                .hasSpringBootParent("1.0.1.BUILD-SNAPSHOT")
                .hasSpringBootStarterDependency("web");
    }

    @Test
    public void mavenPomWithTarDependency() {
        Dependency dependency = Dependency.withId("custom-artifact", "org.foo",
                "custom-artifact");
        dependency.setType("tar.gz");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("test", dependency).build();
        applyMetadata(metadata);

        ProjectRequest request = createProjectRequest("custom-artifact");
        generateMavenPom(request).hasDependency(dependency).hasDependenciesCount(2);
    }

    @Test
    public void mavenPomWithWebFacet() {
        Dependency dependency = Dependency.withId("thymeleaf", "org.foo", "thymeleaf");
        dependency.getFacets().add("web");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("core", "web", "security", "data-jpa")
                .addDependencyGroup("test", dependency).build();
        applyMetadata(metadata);

        ProjectRequest request = createProjectRequest("thymeleaf");
        generateMavenPom(request).hasDependency("org.foo", "thymeleaf")
                .hasDependenciesCount(2);
    }

    //@Test
    public void mavenWarWithWebFacet() {
        Dependency dependency = Dependency.withId("thymeleaf", "org.foo", "thymeleaf");
        dependency.getFacets().add("web");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("core", "web", "security", "data-jpa")
                .addDependencyGroup("test", dependency).build();
        applyMetadata(metadata);

        ProjectRequest request = createProjectRequest("thymeleaf");
        request.setPackaging("war");
        generateProject(request).isJavaWarProject().isMavenProject().pomAssert()
                .hasSpringBootStarterTomcat()
                // This is tagged as web facet so it brings the web one
                .hasDependency("org.foo", "thymeleaf").hasSpringBootStarterTest()
                .hasDependenciesCount(3);
    }

//    @Test
    public void mavenWarPomWithoutWebFacet() {
        ProjectRequest request = createProjectRequest("data-jpa");
        request.setPackaging("war");
        generateMavenPom(request).hasSpringBootStarterTomcat()
                .hasSpringBootStarterDependency("data-jpa")
                .hasSpringBootStarterDependency("web") // Added by war packaging
                .hasSpringBootStarterTest().hasDependenciesCount(4);
    }

//    @Test
    public void groupIdAndArtifactIdInferPackageName() {
        ProjectRequest request = createProjectRequest("web");
        request.setGroupId("org.acme");
        request.setArtifactId("42foo");
        generateProject(request)
                .isJavaProject("org/acme/foo", "CardApplication");
    }

//    @Test
    public void cleanPackageNameWithGroupIdAndArtifactIdWithVersion() {
        ProjectRequest request = createProjectRequest("web");
        request.setGroupId("org.acme");
        request.setArtifactId("foo-1.4.5");
        assertProjectWithPackageNameWithVersion(request);
    }

    @Test
    public void cleanPackageNameWithInvalidPackageName() {
        ProjectRequest request = createProjectRequest("web");
        request.setGroupId("org.acme");
        request.setArtifactId("foo");
        request.setPackageName("org.acme.foo-1.4.5");
        assertProjectWithPackageNameWithVersion(request);
    }

    private void assertProjectWithPackageNameWithVersion(ProjectRequest request) {
        generateProject(request)
                .isJavaProject("org/acme/foo145", "CardApplication")
                .sourceCodeAssert(
                        "src/main/java/org/acme/foo145/CardApplication.java")
                .contains("package org.acme.foo145;");
    }

    @Test
    public void springBoot11UseEnableAutoConfigurationJava() {
        ProjectRequest request = createProjectRequest("web");
        request.setBootVersion("1.1.9.RELEASE");
        request.setName("MyCard");
        request.setPackageName("foo");
        generateProject(request)
                .sourceCodeAssert("src/main/java/foo/MyCardApplication.java")
                .hasImports(EnableAutoConfiguration.class.getName(),
                        ComponentScan.class.getName(), Configuration.class.getName())
                .doesNotHaveImports(SpringBootApplication.class.getName())
                .contains("@EnableAutoConfiguration", "@Configuration", "@ComponentScan")
                .doesNotContain("@SpringBootApplication");
    }

    @Test
    public void springBootUseSpringBootApplicationJava() {
        ProjectRequest request = createProjectRequest("web");
        request.setBootVersion("1.2.0.RC1");
        request.setName("MyCard");
        request.setPackageName("foo");
        generateProject(request)
                .sourceCodeAssert("src/main/java/foo/MyCardApplication.java")
                .hasImports(SpringBootApplication.class.getName())
                .doesNotHaveImports(EnableAutoConfiguration.class.getName(),
                        ComponentScan.class.getName(), Configuration.class.getName())
                .contains("@SpringBootApplication").doesNotContain(
                "@EnableAutoConfiguration", "@Configuration", "@ComponentScan");
    }

    @Test
    public void customBaseDirectory() {
        ProjectRequest request = createProjectRequest();
        request.setBaseDir("my-project");
        generateProject(request).hasBaseDir("my-project").isJavaProject()
                .isMavenProject();
    }

    @Test
    public void customBaseDirectoryNested() {
        ProjectRequest request = createProjectRequest();
        request.setBaseDir("foo-bar/my-project");
        generateProject(request).hasBaseDir("foo-bar/my-project").isJavaProject()
                .isMavenProject();
    }

    @Test
    public void mavenPomWithCustomVersion() {
        Dependency whatever = Dependency.withId("whatever", "org.acme", "whatever",
                "1.2.3");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("core", "web", "security", "data-jpa")
                .addDependencyGroup("foo", whatever).build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("whatever", "data-jpa", "web");
        generateMavenPom(request).hasDependency(whatever)
                .hasSpringBootStarterDependency("data-jpa")
                .hasSpringBootStarterDependency("web");
    }

    @Test
    public void defaultMavenPomHasSpringBootParent() {
        ProjectRequest request = createProjectRequest("web");
        generateMavenPom(request).hasSpringBootParent(request.getBootVersion());
    }

    @Test
    public void mavenPomWithCustomParentPom() {
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("core", "web", "security", "data-jpa")
                .setMavenParent("com.foo", "foo-parent", "1.0.0-SNAPSHOT", false).build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("web");
        generateMavenPom(request).hasParent("com.foo", "foo-parent", "1.0.0-SNAPSHOT")
                .hasBomsCount(0);
    }

    @Test
    public void mavenPomWithCustomParentPomAndSpringBootBom() {
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("core", "web", "security", "data-jpa")
                .setMavenParent("com.foo", "foo-parent", "1.0.0-SNAPSHOT", true).build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("web");
        request.setBootVersion("1.0.2.RELEASE");
        generateMavenPom(request).hasParent("com.foo", "foo-parent", "1.0.0-SNAPSHOT")
                .hasProperty("spring-boot.version", "1.0.2.RELEASE")
                .hasBom("org.springframework.boot", "spring-boot-dependencies",
                        "${spring-boot.version}")
                .hasBomsCount(1);
    }

    @Test
    public void mavenPomWithCustomScope() {
        Dependency h2 = Dependency.withId("h2", "org.h2", "h2");
        h2.setScope("runtime");
        Dependency hamcrest = Dependency.withId("hamcrest", "org.hamcrest", "hamcrest");
        hamcrest.setScope("test");
        Dependency servlet = Dependency.withId("servlet-api", "javax.servlet",
                "servlet-api");
        servlet.setScope("provided");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("core", "web", "security", "data-jpa")
                .addDependencyGroup("database", h2)
                .addDependencyGroup("container", servlet)
                .addDependencyGroup("test", hamcrest).build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("hamcrest", "h2", "servlet-api",
                "data-jpa", "web");
        generateMavenPom(request).hasDependency(h2).hasDependency(hamcrest)
                .hasDependency(servlet).hasSpringBootStarterDependency("data-jpa")
                .hasSpringBootStarterDependency("web");
    }

    @Test
    public void mavenBom() {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setBom("foo-bom");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("foo", foo)
                .addBom("foo-bom", "org.acme", "foo-bom", "1.2.3").build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("foo");
        generateMavenPom(request).hasDependency(foo).hasBom("org.acme", "foo-bom",
                "1.2.3");
    }

    @Test
    public void mavenBomWithSeveralDependenciesOnSameBom() {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setBom("the-bom");
        Dependency bar = Dependency.withId("bar", "org.acme", "bar");
        bar.setBom("the-bom");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("group", foo, bar)
                .addBom("the-bom", "org.acme", "the-bom", "1.2.3").build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("foo", "bar");
        generateMavenPom(request).hasDependency(foo)
                .hasBom("org.acme", "the-bom", "1.2.3").hasBomsCount(1);
    }

    @Test
    public void mavenBomWithVersionMapping() {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setBom("the-bom");
        BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
        bom.getMappings()
                .add(BillOfMaterials.Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.0.0"));
        bom.getMappings().add(BillOfMaterials.Mapping.create("1.3.0.M1", "1.2.0"));
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("foo", foo).addBom("the-bom", bom).build();
        applyMetadata(metadata);

        // First version
        ProjectRequest request = createProjectRequest("foo");
        request.setBootVersion("1.2.5.RELEASE");
        generateMavenPom(request).hasDependency(foo).hasSpringBootParent("1.2.5.RELEASE")
                .hasBom("org.acme", "foo-bom", "1.0.0");

        // Second version
        ProjectRequest request2 = createProjectRequest("foo");
        request2.setBootVersion("1.3.0.M1");
        generateMavenPom(request2).hasDependency(foo).hasSpringBootParent("1.3.0.M1")
                .hasBom("org.acme", "foo-bom", "1.2.0");
    }

    @Test
    public void mavenBomWithVersionMappingAndExtraRepositories() {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setBom("the-bom");
        BillOfMaterials bom = BillOfMaterials.create("org.acme", "foo-bom");
        bom.getRepositories().add("foo-repo");
        bom.getMappings()
                .add(BillOfMaterials.Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", "1.0.0"));
        bom.getMappings().add(BillOfMaterials.Mapping.create("1.3.0.M1", "1.2.0",
                "foo-repo", "bar-repo"));
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("foo", foo).addBom("the-bom", bom)
                .addRepository("foo-repo", "repo", "http://example.com/foo", true)
                .addRepository("bar-repo", "repo", "http://example.com/bar", false)
                .build();
        applyMetadata(metadata);

        // Second version
        ProjectRequest request = createProjectRequest("foo");
        request.setBootVersion("1.3.0.RELEASE");
        generateMavenPom(request).hasDependency(foo).hasSpringBootParent("1.3.0.RELEASE")
                .hasBom("org.acme", "foo-bom", "1.2.0")
                .hasRepository("foo-repo", "repo", "http://example.com/foo", true)
                .hasRepository("bar-repo", "repo", "http://example.com/bar", false)
                .hasRepositoriesCount(2);
    }

    @Test
    public void mavenRepository() {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setRepository("foo-repo");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("foo", foo)
                .addRepository("foo-repo", "foo", "http://example.com/repo", false)
                .build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("foo");
        generateMavenPom(request).hasDependency(foo).hasRepository("foo-repo", "foo",
                "http://example.com/repo", false);
    }

    @Test
    public void mavenRepositoryWithSeveralDependenciesOnSameRepository() {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.setRepository("the-repo");
        Dependency bar = Dependency.withId("bar", "org.acme", "bar");
        foo.setRepository("the-repo");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("group", foo, bar)
                .addRepository("the-repo", "repo", "http://example.com/repo", true)
                .build();
        applyMetadata(metadata);
        ProjectRequest request = createProjectRequest("foo", "bar");
        generateMavenPom(request).hasDependency(foo)
                .hasRepository("the-repo", "repo", "http://example.com/repo", true)
                .hasRepositoriesCount(1);
    }

//    @Test
    public void projectWithOnlyStarterDependency() {
        Dependency foo = Dependency.withId("foo", "org.foo", "custom-my-starter");
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("foo", foo).build();
        applyMetadata(metadata);

        ProjectRequest request = createProjectRequest("foo");
        generateMavenPom(request).hasDependency("org.foo", "custom-my-starter")
                .hasSpringBootStarterTest().hasDependenciesCount(2);
    }

    //@Test
    public void projectWithOnlyNonStarterDependency() {
        Dependency foo = Dependency.withId("foo", "org.foo", "foo");
        foo.setStarter(false);
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("foo", foo).build();
        applyMetadata(metadata);

        ProjectRequest request = createProjectRequest("foo");
        generateMavenPom(request).hasDependency("org.foo", "foo")
                .hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
                .hasDependenciesCount(3);
    }

    @Test
    public void buildPropertiesMaven() {
        ProjectRequest request = createProjectRequest("web");
        request.getBuildProperties().getMaven().put("name", () -> "test");
        request.getBuildProperties().getVersions().put(
                new VersionProperty("foo.version"), () -> "1.2.3");
        request.getBuildProperties().getGradle().put("ignore.property", () -> "yes");

        generateMavenPom(request).hasProperty("name", "test")
                .hasProperty("foo.version", "1.2.3").hasNoProperty("ignore.property");
    }

    @Test
    public void versionRangeWithPostProcessor() {
        Dependency foo = Dependency.withId("foo", "org.acme", "foo");
        foo.getMappings().add(Dependency.Mapping.create("[1.2.0.RELEASE,1.3.0.M1)", null,
                null, "1.0.0"));
        foo.getMappings().add(Dependency.Mapping.create("1.3.0.M1", null, null, "1.2.0"));
        InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
                .addDependencyGroup("foo", foo).build();
        applyMetadata(metadata);

        // First without processor, get the correct version
        ProjectRequest request = createProjectRequest("foo");
        request.setBootVersion("1.2.5.RELEASE");
        generateMavenPom(request)
                .hasDependency(Dependency.withId("foo", "org.acme", "foo", "1.0.0"));

        // First after processor that flips Spring Boot version
        projectGenerator.setRequestResolver(new ProjectRequestResolver(
                Collections.singletonList(new ProjectRequestPostProcessor() {
                    @Override
                    public void postProcessBeforeResolution(ProjectRequest r,
                            InitializrMetadata m) {
                        r.setBootVersion("1.3.0.M2");
                    }
                })));
        generateMavenPom(request)
                .hasDependency(Dependency.withId("foo", "org.acme", "foo", "1.2.0"));
    }

    @Test
    public void gitIgnoreMaven() {
        ProjectRequest request = createProjectRequest();
        request.setType("maven-project");
        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(".gitignore")
                .equalsTo(new ClassPathResource("project/maven/gitignore.gen"));
    }

    @Test
    public void invalidDependency() {
        ProjectRequest request = createProjectRequest("foo-bar");
        try {
            generateMavenPom(request);
            fail("Should have failed to generate project");
        } catch (InvalidProjectRequestException ex) {
            assertThat(ex.getMessage()).contains("foo-bar");
            verifyProjectFailedEventFor(request, ex);
        }
    }

    @Test
    public void invalidType() {
        ProjectRequest request = createProjectRequest("web");
        request.setType("foo-bar");
        try {
            generateProject(request);
            fail("Should have failed to generate project");
        } catch (InvalidProjectRequestException ex) {
            assertThat(ex.getMessage()).contains("foo-bar");
            verifyProjectFailedEventFor(request, ex);
        }
    }

    @Test
    public void invalidLanguage() {
        ProjectRequest request = createProjectRequest("web");
        request.setLanguage("foo-bar");
        try {
            generateProject(request);
            fail("Should have failed to generate project");
        } catch (InvalidProjectRequestException ex) {
            assertThat(ex.getMessage()).contains("foo-bar");
            verifyProjectFailedEventFor(request, ex);
        }
    }

}
