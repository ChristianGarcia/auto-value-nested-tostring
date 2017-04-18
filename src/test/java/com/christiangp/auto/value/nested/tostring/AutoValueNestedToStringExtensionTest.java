/*
 * Copyright (c) 2017 Christian García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.christiangp.auto.value.nested.tostring;

import com.google.auto.value.processor.AutoValueProcessor;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

public class AutoValueNestedToStringExtensionTest {

    private JavaFileObject nested;

    @Before
    public void setUp()
            throws Exception {
        nested = JavaFileObjects.forSourceString("test.Nested", "" +
                "package test;\n" +
                "import java.lang.annotation.ElementType;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.RetentionPolicy;\n" +
                "import java.lang.annotation.Target;\n" +
                "@Retention(RetentionPolicy.SOURCE)\n" +
                "@Target(ElementType.TYPE)\n" +
                "public @interface Nested {\n" +
                "}"
        );
    }

    @Test
    public void allEnclosingClassesPrepended() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "test.TopLevelClass",
                "" +
                        "package test;\n" +
                        "import com.google.auto.value.AutoValue;\n" +
                        "public class TopLevelClass {\n" +
                        "    public static class NestedClass {\n" +
                        "        @AutoValue\n" +
                        "        @Nested\n" +
                        "        public static abstract class NestedNestedClass {\n" +
                        "        }\n" +
                        "    }\n" +
                        "}"
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test/AutoValue_TopLevelClass_NestedClass_NestedNestedClass",
                ""
                        + "package test;\n"
                        + "import java.lang.Override;\n"
                        + "import java.lang.String;\n"
                        + "final class AutoValue_TopLevelClass_NestedClass_NestedNestedClass extends $AutoValue_TopLevelClass_NestedClass_NestedNestedClass {\n"
                        + "  AutoValue_TopLevelClass_NestedClass_NestedNestedClass() {\n"
                        + "    super();\n"
                        + "  }\n"
                        + "  @Override public final String toString() {\n"
                        + "    return \"TopLevelClass.NestedClass.\"+super.toString();\n"
                        + "  }\n"
                        + "}\n"
        );

        assertAbout(javaSources())
                .that(Arrays.asList(nested, source))
                .processedWith(new AutoValueProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }
}