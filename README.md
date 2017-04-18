# AutoValue: Nested toString()
[![Build Status](https://travis-ci.org/ChristianGarcia/auto-value-nested-tostring.svg?branch=master)](https://travis-ci.org/ChristianGarcia/auto-value-nested-tostring)
[![](https://img.shields.io/maven-central/v/com.christiangp/auto-value-nested-tostring.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.christiangp%22%20a%3A%22auto-value-nested-tostring%22)

An extension for Google's [AutoValue](https://github.com/google/auto/tree/master/value) that prepends the enclosing classes names to a `@Nested` annotated nested 
class's `toString()`.

## Usage
Define a `@Nested` annotation in your project and apply it to any nested class whose `toString()` you wish to include 
the encosling class.

```java
@Retention(SOURCE)
@Target(TYPE)
public @interface Nested {
}
```

```java
public class TopLevelClass {

  public static class NestedClass {
  
    @AutoValue
    @Nested
    public static abstract class NestedInNestedClass {
       public abstract String property();
    }
  }
}
```

When you call a `NestedInNestedClass` instance's `toString()` this will include the enclosing classes in the beginning

```
TopLevelClass.NestedClass.NestedInNestedClass{property=name} //autov-value-nested-tostring
```

as opposed to AutoValue's default (simple class name)

```
NestedInNestedClass{property=name} //AutoValue's default
```

## Download

Add a Gradle dependency:
```groovy
annotationProcessor 'com.christiangp:auto-value-nested-tostring:<latest_version>'
```

or Maven:
```xml
<dependency>
  <groupId>com.christiangp</groupId>
  <artifactId>auto-value-nested-tostring</artifactId>
  <version>$latest_version</version>
  <scope>provided</scope>
</dependency>
```

## License

```
Copyright (c) 2017 Christian García

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
