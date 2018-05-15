/*
 * Copyright 2012-2017 the original author or authors.
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

import static io.spring.initializr.test.generator.ProjectAssert.DEFAULT_APPLICATION_NAME;
import static io.spring.initializr.test.generator.ProjectAssert.DEFAULT_PACKAGE_NAME;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.io.ClassPathResource;

import io.spring.initializr.test.generator.ProjectAssert;

/**
 * Project generator tests for supported languages.
 *
 * @author Stephane Nicoll
 */
@RunWith(Parameterized.class)
@Ignore
public class ProjectGeneratorLanguageTests extends AbstractProjectGeneratorTests {

    private final String language;
    private final String extension;
    private final String expectedExtension;
    public ProjectGeneratorLanguageTests(String language, String extension) {
        this.language = language;
        this.extension = extension;
        this.expectedExtension = extension + ".gen";
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] parameters() {
        Object[] java = new Object[]{"java", "java"};
        return new Object[]{java};
    }

    @Test
    public void standardJar() {
        ProjectRequest request = createProjectRequest();
        request.setLanguage(language);
        generateProject(request).isGenericProject(DEFAULT_PACKAGE_NAME,
                DEFAULT_APPLICATION_NAME, language, extension);
    }

    @Test
    public void standardWar() {
        ProjectRequest request = createProjectRequest("web");
        request.setLanguage(language);
        request.setPackaging("war");
        generateProject(request).isGenericWarProject(DEFAULT_PACKAGE_NAME,
                DEFAULT_APPLICATION_NAME, language, extension);
    }

    @Test
    public void standardMainClass() {
        ProjectRequest request = createProjectRequest();
        request.setLanguage(language);

        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(
                "src/main/" + language + "/com/fluig/card/CardApplication." + extension)
                .equalsTo(new ClassPathResource("project/" + language
                        + "/standard/CardApplication." + expectedExtension));
    }

    @Test
    public void standardTestClass() {
        ProjectRequest request = createProjectRequest();
        request.setLanguage(language);

        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(
                "src/test/" + language + "/com/fluig/card/CardApplicationTests." + extension)
                .equalsTo(new ClassPathResource("project/" + language
                        + "/standard/CardApplicationTests." + expectedExtension));
    }

    @Test
    public void standardTestClassWeb() {
        ProjectRequest request = createProjectRequest("web");
        request.setLanguage(language);

        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(
                "src/test/" + language + "/com/fluig/card/CardApplicationTests." + extension)
                .equalsTo(new ClassPathResource("project/" + language
                        + "/standard/CardApplicationTestsWeb." + expectedExtension));
    }

    @Test
    public void standardServletInitializer() {
        testServletInitializr(null, "standard");
    }

    @Test
    public void springBoot14M2ServletInitializer() {
        testServletInitializr("1.4.0.M2", "standard");
    }

    @Test
    public void springBoot14ServletInitializer() {
        testServletInitializr("1.4.0.M3", "spring-boot-1.4");
    }

    @Test
    public void springBoot2ServletInitializer() {
        testServletInitializr("2.0.0.M3", "spring-boot-2.0");
    }


    private void testServletInitializr(String bootVersion, String expectedOutput) {
        ProjectRequest request = createProjectRequest();
        request.setLanguage(language);
        request.setPackaging("war");
        if (bootVersion != null) {
            request.setBootVersion(bootVersion);
        }
        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(
                "src/main/" + language + "/com/fluig/card/ServletInitializer." + extension)
                .equalsTo(new ClassPathResource("project/" + language
                        + "/" + expectedOutput + "/ServletInitializer." + expectedExtension));
    }

    @Test
    public void springBoot14M1TestClass() {
        ProjectRequest request = createProjectRequest();
        request.setLanguage(language);
        request.setBootVersion("1.4.0.M1");

        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(
                "src/test/" + language + "/com/fluig/card/CardApplicationTests." + extension)
                .equalsTo(new ClassPathResource("project/" + language
                        + "/standard/CardApplicationTests." + expectedExtension));
    }

    @Test
    public void springBoot14TestClass() {
        ProjectRequest request = createProjectRequest();
        request.setLanguage(language);
        request.setBootVersion("1.4.0.M2");

        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(
                "src/test/" + language + "/com/fluig/card/CardApplicationTests." + extension)
                .equalsTo(new ClassPathResource("project/" + language
                        + "/spring-boot-1.4/CardApplicationTests." + expectedExtension));
    }

    @Test
    public void springBoot14TestClassWeb() {
        ProjectRequest request = createProjectRequest("web");
        request.setLanguage(language);
        request.setBootVersion("1.4.0.M2");

        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert(
                "src/test/" + language + "/com/fluig/card/CardApplicationTests." + extension)
                .equalsTo(new ClassPathResource("project/" + language
                        + "/spring-boot-1.4/CardApplicationTests." + expectedExtension));
    }

}
