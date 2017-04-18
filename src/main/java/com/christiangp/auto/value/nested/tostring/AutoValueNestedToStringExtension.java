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

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(AutoValueExtension.class)
public final class AutoValueNestedToStringExtension
        extends AutoValueExtension {

    @Override
    public boolean applicable(Context context) {
        final boolean nested = context.autoValueClass()
                                      .getNestingKind()
                                      .isNested();
        return nested && getAnnotations(context.autoValueClass()).contains("Nested");
    }

    @Override
    public String generateClass(Context context, String className, String classToExtend,
                                boolean isFinal) {
        final String packageName = context.packageName();
        final Map<String, ExecutableElement> properties = context.properties();

        final TypeSpec subclass = TypeSpec.classBuilder(className)
                                          .addModifiers(isFinal ? Modifier.FINAL : Modifier.ABSTRACT)
                                          .superclass(ClassName.get(packageName, classToExtend))
                                          .addMethod(generateConstructor(properties))
                                          .addMethod(generateToString(context.autoValueClass()))
                                          .build();

        return JavaFile.builder(packageName, subclass)
                       .build()
                       .toString();
    }

    private static MethodSpec generateConstructor(Map<String, ExecutableElement> properties) {
        final List<ParameterSpec> params = new ArrayList<>();
        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            final TypeName typeName = TypeName.get(entry.getValue()
                                                        .getReturnType());
            params.add(ParameterSpec.builder(typeName, entry.getKey())
                                    .build());
        }

        final StringBuilder body = new StringBuilder("super(");
        for (int i = properties.size(); i > 0; i--) {
            body.append("$N");
            if (i > 1) {
                body.append(", ");
            }
        }
        body.append(")");

        return MethodSpec.constructorBuilder()
                         .addParameters(params)
                         .addStatement(body.toString(), properties.keySet()
                                                                  .toArray())
                         .build();
    }

    private static MethodSpec generateToString(TypeElement autoValueClass) {

        final MethodSpec.Builder builder = MethodSpec.methodBuilder("toString")
                                                     .addAnnotation(Override.class)
                                                     .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                                     .returns(String.class);
        builder.addCode("return ");
        builder.addCode("\"");
        builder.addCode("$L", buildEnclosingClassNames(autoValueClass));
        builder.addCode("\"");
        builder.addCode("+super.toString()");
        return builder.addStatement("")
                      .build();
    }

    private static String buildEnclosingClassNames(TypeElement autoValueClass) {
        final List<String> enclosingSimpleNames = ClassName.get(autoValueClass)
                                                           .enclosingClassName()
                                                           .simpleNames();
        final StringBuilder sb = new StringBuilder();
        for (String simpleName : enclosingSimpleNames) {
            sb.append(simpleName)
              .append(".");
        }
        return sb.toString();
    }

    private static Set<String> getAnnotations(Element element) {
        final Set<String> set = new LinkedHashSet<>();

        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            set.add(annotation.getAnnotationType()
                              .asElement()
                              .getSimpleName()
                              .toString());
        }

        return Collections.unmodifiableSet(set);
    }
}