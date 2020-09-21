kotlin-hocon-mapper
===================

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.uharaqo.kotlin-hocon-mapper/kotlin-hocon-mapper/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.uharaqo.kotlin-hocon-mapper/kotlin-hocon-mapper)
[![Build Status](https://travis-ci.org/uharaqo/kotlin-hocon-mapper.svg?branch=master)](https://travis-ci.org/uharaqo/kotlin-hocon-mapper)
[![codecov](https://codecov.io/gh/uharaqo/kotlin-hocon-mapper/branch/master/graph/badge.svg)](https://codecov.io/gh/uharaqo/kotlin-hocon-mapper)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=com.lapots.breed.judge:judge-rule-engine&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.lapots.breed.judge:judge-rule-engine)
[![license](https://img.shields.io/badge/license-Apache%202-blue")](./LICENSE)

Overview
--------
A lightweight [Typesafe Config](https://github.com/lightbend/config) ([HOCON](https://github.com/lightbend/config/blob/master/HOCON.md)) mapper for Kotlin classes based on [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
- Provides serializers and deserializers based on `kotlinx.serialization`
  - Deserializers convert `com.typesafe.config.Config` into a class annotated with `@Serializable`
  - Serializers convert a serializable object into a JSON (Beta: intended to support writing HOCON config during development)
  - No reflection at runtime. Those serializers and deserializers are generated at compile time.
- Supports basic types such as `String`, `Boolean`, `Int`, `Long`, `Float`, `Double`, `Enum`, `List`, `Map` and nested object
- Supports additional types for [unit conversion](https://github.com/lightbend/config/blob/master/HOCON.md#units-format) such as `Period`, `Duration` and `ConfigMemorySize`

Core Dependencies
-------------

- version 1.4.10:
  - kotlin: 1.4.10
  - kotlinx.serialization: 1.0.0-RC
  - typesafe.config: 1.4.0

- version 0.9.0: 
  - kotlin: 1.3.72
  - kotlinx.serialization: 0.20.0
  - typesafe.config: 1.4.0

- version 0.1.0: 
  - kotlin: 1.3.50
  - kotlinx.serialization: 0.13.0
  - typesafe.config: 1.3.4

Getting Started
---------------

- Gradle
  ```gradle
  implementation "com.github.uharaqo.kotlin-hocon-mapper:kotlin-hocon-mapper:$hocon_mapper_version"
  ```

- Maven
  ```xml
  <dependency>
    <groupId>com.github.uharaqo.kotlin-hocon-mapper</groupId>
    <artifactId>kotlin-hocon-mapper</artifactId>
    <version>${hocon.mapper.version}</version>
  </dependency>
  ```

- Setup the [kotlinx.serializer compiler plugin](https://github.com/Kotlin/kotlinx.serialization#setup)

- To convert a `Config` object into a data object,
  ```kotlin
  @Serializable
  data class BasicTypes(
      val char: Char, val string: String, val bool: Boolean,
      val byte: Byte, val int: Int, val long: Long,
      val short: Short, val float: Float, val double: Double,
      val enum: BasicModels.SampleEnum, val nullable: String?,
      val list: List<Int>, val map: Map<String, Int>,
      val nested: Nested
  )
  @Serializable
  data class Nested(val value: String)

  // Config and ConfigFactory are found in com.typesafe.config package
  val config: Config = ConfigFactory.parseString(
      """{
       |  char: a,
       |  string: abc,
       |  bool: true,
       |  byte: 1,
       |  int: ${Int.MAX_VALUE},
       |  long: ${Long.MAX_VALUE},
       |  short: ${Short.MAX_VALUE},
       |  float: ${Float.MAX_VALUE},
       |  double: ${Double.MAX_VALUE},
       |  enum: ${BasicModels.SampleEnum.ELEMENT}
       |  nullable: null,
       |  list: [1, 2, 3]
       |  map: { first: 1, second: 2}
       |  nested: { value: nested }
       |  unknown: "this value is ignored becuase 'unknown' is not defined in the data object"
       |}
       """.trimMargin()
  )
  val obj = BasicTypes.serializer().load(config)
  ```

- To convert additional types for [unit conversion](https://github.com/lightbend/config/blob/master/HOCON.md#units-format),
  ```kotlin
  // file targeted annotation to enable these deserializers
  @file:UseSerializers(
      DurationSerializer::class,
      PeriodSerializer::class,
      ConfigMemorySizeSerializer::class
  )
  
  @Serializable
  data class UnitConversion(
    val duration: Duration,        // -> Duration.ofMinutes(10)
    val period: Period,            // -> Period.ofWeeks(1)
    val memSize: ConfigMemorySize, // -> ConfigMemorySize.ofBytes(1024)
    // this is another option to enable an additional serializer for a field
    @Serializable(with = StringBooleanSerializer::class)
    val textBoolean: Boolean       // -> true
  )
  
  val config: com.typesafe.config.Config = ConfigFactory.parseString(
      """{
       |  duration: 10m
       |  period: 1w
       |  memSize: 1KiB
       |  textBoolean: yes
       |}
       """.trimMargin()
  )
  val converted = UnitConversion.serializer().load(conf)
  ```
  More examples can be found in [test code](kotlin-hocon-mapper/src/test/kotlin/com/github/uharaqo/hocon/mapper/SerializerDeserializerTest.kt)

- To serialize an object into a JSON String (Beta)
  ```kotlin
  val json: String = BasicTypes.serializer().stringify(data)
  // or ConfigSerializer.stringify(BasicTypes.serializer(), data)
  ```

Deserialization details
-----------------------

All the arguments in a constructor should be provided by a `Config` object
- When a key is not found in the `Config`, `MissingFieldException` will be thrown 
- `null` can be injected into a nullable argument, but it should be explicitly defined like `{"path.to.prop": null}`)
- Default values in a constructor are ignored
  - [How to handle defaults](https://github.com/lightbend/config#how-to-handle-defaults)
    explains why we should not define configs in various places

Example usage
-------------
Production code
1. Create a data class with the `@Serializable` annotation
2. [Load config files by using the Typesafe Config](https://github.com/lightbend/config#standard-behavior)
3. Load the `Config` object into the data class by using the deserializer

Setup the config by temporary code
1. Instantiate the class with default values
2. Generate a JSON with the object by using the serializer
3. Put the JSON into a config source such as application.conf

Links
-----

- [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md)
  - Human-Optimized Config Object Notation
  - JSON-like intuitive configuration syntax
  - Supports lots of useful features
  - Adopted by popular libraries such as [Ktor](https://ktor.io/servers/configuration.html#hocon-file)

- [Typesafe Config](https://github.com/lightbend/config)
  - A library for managing HOCON files
  - merge configs from multiple source such as files, system properties, environment variables

- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
  - A compiler plugin which generates code to serialize objects without reflection.
  - A runtime library which provides APIs for Encoder and Decoder

- [Config4k](https://github.com/config4k/config4k)
  - Kotlin extension functions for Typesafe Config
  - Object mapper: `config.extract<MyData>("path.to.mydata")`
  - Serializer: `MyData("foo", 42).toConfig("path.to.mydata")`
  - Unfortunately, this project has been inactive since Jan 2019 (as of Sep 2019).
    It didn't work with the latest kotlin reflection library.
