/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.metadata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for {@link InitializrConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class InitializrConfigurationTests {

    private final InitializrConfiguration properties = new InitializrConfiguration();

    @Test
    public void generateApplicationNameSimple() {
        assertEquals("CardApplication", this.properties.generateApplicationName("card"));
    }

    @Test
    public void generateApplicationNameSimpleApplication() {
        assertEquals("CardApplication", this.properties.generateApplicationName("cardApplication"));
    }

    @Test
    public void generateApplicationNameSimpleCamelCase() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("myCard"));
    }

    @Test
    public void generateApplicationNameSimpleUnderscore() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("my_card"));
    }

    @Test
    public void generateApplicationNameSimpleColon() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("my:card"));
    }

    @Test
    public void generateApplicationNameSimpleSpace() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("my card"));
    }

    @Test
    public void generateApplicationNameSimpleDash() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("my-card"));
    }

    @Test
    public void generateApplicationNameUpperCaseUnderscore() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("MY_CARD"));
    }

    @Test
    public void generateApplicationNameUpperCaseDash() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("MY-CARD"));
    }

    @Test
    public void generateApplicationNameMultiSpaces() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("   my    card "));
    }

    @Test
    public void generateApplicationNameMultiSpacesUpperCase() {
        assertEquals("MyCardApplication", this.properties.generateApplicationName("   MY    CARD "));
    }

    @Test
    public void generateApplicationNameNull() {
        assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName(null));
    }

    @Test
    public void generateApplicationNameInvalidStartCharacter() {
        assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("1MyCard"));
    }

    @Test
    public void generateApplicationNameInvalidPartCharacter() {
        assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("MyDe|mo"));
    }

    @Test
    public void generateApplicationNameInvalidApplicationName() {
        assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("SpringBoot"));
    }

    @Test
    public void generateApplicationNameAnotherInvalidApplicationName() {
        assertEquals(this.properties.getEnv().getFallbackApplicationName(), this.properties.generateApplicationName("Spring"));
    }

    @Test
    public void generatePackageNameSimple() {
        assertEquals("com.foo", this.properties.cleanPackageName("com.foo", "com.fluig"));
    }

    @Test
    public void generatePackageNameSimpleUnderscore() {
        assertEquals("com.my_foo", this.properties.cleanPackageName("com.my_foo", "com.fluig"));
    }

    @Test
    public void generatePackageNameSimpleColon() {
        assertEquals("com.foo", this.properties.cleanPackageName("com:foo", "com.fluig"));
    }

    @Test
    public void generatePackageNameMultipleDashers() {
        assertEquals("com.foobar", this.properties.cleanPackageName("com.foo--bar", "com.fluig"));
    }

    @Test
    public void generatePackageNameMultipleSpaces() {
        assertEquals("com.foo", this.properties.cleanPackageName("  com   foo  ", "com.fluig"));
    }

    @Test
    public void generatePackageNameNull() {
        assertEquals("com.fluig", this.properties.cleanPackageName(null, "com.fluig"));
    }

    @Test
    public void generatePackageNameInvalidStartCharacter() {
        assertEquals("com.foo", this.properties.cleanPackageName("0com.foo", "com.fluig"));
    }

    @Test
    public void generatePackageNameVersion() {
        assertEquals("com.foo.test145", this.properties.cleanPackageName(
                "com.foo.test-1.4.5", "com.fluig"));
    }

    @Test
    public void generatePackageNameInvalidPackageName() {
        assertEquals("com.fluig", this.properties.cleanPackageName("org.springframework", "com.fluig"));
    }

    @Test
    public void validateArtifactRepository() {
        this.properties.getEnv().setArtifactRepository("http://foo/bar");
        assertEquals("http://foo/bar/", this.properties.getEnv().getArtifactRepository());
    }

}
