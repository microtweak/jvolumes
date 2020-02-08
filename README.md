# jVolumes

[![][maven img]][maven]
[![][javadoc img]][javadoc]
[![][release img]][release]
[![][license img]][license]

[maven]:http://search.maven.org/#search|gav|1|g:"com.github.microtweak"%20AND%20a:"jvolumes"
[maven img]:https://maven-badges.herokuapp.com/maven-central/com.github.microtweak/jvolumes/badge.svg

[javadoc]:https://javadoc.io/doc/com.github.microtweak/jvolumes-parent
[javadoc img]:https://javadoc.io/badge/com.github.microtweak/jvolumes-parent.svg

[release]:https://github.com/microtweak/jvolumes/releases
[release img]:https://img.shields.io/github/release/microtweak/jvolumes.svg

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-MIT-yellow.svg

--------------------------------------------

An abstraction to handle different types of file storage

## Problem
Storing files in the cloud (AWS S3, Google Cloud Storage, etc.) requires the use of the provider's SDK. Although these APIs are not complex, it is still a little tiring to use them.

The Spring Framework presents a very cool abstraction called [Resource](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/core/io/Resource.html), which, in addition to the Spring Cloud,
adds support for Amazon S3, Google Cloud Storage and Azure Blob Storage.

Unfortunately, Spring's Resource class does not have some operations like copying, moving and deleting files from these locations. And finally, if your project is not based on Spring, you can not the abstraction of the Spring Framework.

## Solution

A small API inspired by the Resource of [Spring Framework](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#resources), but independent of Spring.
This means that you can use it in any environment (Java EE/Jakarta EE, Spring).

```java
FileResource.of("file://C:/images/book.jpg")
    .copyTo("s3://[your-s3-bucket]/book.jpg");
```