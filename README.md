kotlin-hocon-mapper
===================

[![Build Status](https://travis-ci.org/uharaqo/kotlin-hocon-mapper.svg?branch=master)](https://travis-ci.org/uharaqo/kotlin-hocon-mapper)
[![codecov](https://codecov.io/gh/uharaqo/kotlin-hocon-mapper/branch/master/graph/badge.svg)](https://codecov.io/gh/uharaqo/kotlin-hocon-mapper)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=com.lapots.breed.judge:judge-rule-engine&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.lapots.breed.judge:judge-rule-engine)
[![license](https://img.shields.io/badge/license-Apache%202-blue")](./LICENSE)

Overview
--------
A Typesafe Config ([HOCON](https://github.com/lightbend/config/blob/master/HOCON.md)) mapper for Kotlin classes.
- Provides deserializers based on [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
  - Converts `com.typesafe.config.Config` in [Typesafe Config](https://github.com/lightbend/config) into a class annotated with `@Serializable`
  - No reflection. Deserializers are generated at compile time.
- Supports basic types such as `String`, `Boolean`, `Int`, `Long`, `Float`, `Double`, `Enum` and nested objects
- Supports additional types such as `Period`, `Duration` and `ConfigMemorySize`
- Beta support for JSON serializers

Getting Started
---------------

- Dependencies:
  - Gradle
    ```gradle
    implementation "com.github.uharaqo.kotlin-hocon-mapper:kotlin-hocon-mapper:$version"
    ```
  
  - Maven
    ```xml
    <dependency>
      <groupId>com.github.uharaqo.kotlin-hocon-mapper</groupId>
      <artifactId>kotlin-hocon-mapper</artifactId>
      <version>$version</version>
    </dependency>
    ```
- Set up kotlinx.serializer compilation. See [Setup section](https://github.com/Kotlin/kotlinx.serialization#setup) for details
  
- Converting a `Config` object into a data object
  ```kotlin
  @Serializable
  data class BasicTypes(
    val char: Char, val string: String, val bool: Boolean,
    val byte: Byte, val int: Int, val long: Long,
    val short: Short, val float: Float, val double: Double,
    val enum: SampleEnum
  )
  
  val config: com.typesafe.config.Config =  ConfigFactory.parseString(
    """
    basics: {
      // values have possible types: string, number, object, array, boolean, null
      string: string
      number: 123
      array: [1, 2, 3]
      boolean: true
      nullable: null
      unknown: "not defined in the data object. should be ignored by the parser"
      }
    """
  )
  val obj = BasicTypes.serializer().load(config)
  ```

- Additional deserializers for [unit conversion](https://github.com/lightbend/config/blob/master/HOCON.md#units-format)
  ```kotlin
  // file targeted annotation to enable these deserializers
  @file:UseSerializers(
      DurationSerializer::class,
      PeriodSerializer::class,
      ConfigMemorySizeSerializer::class
  )
  
  @Serializable
  data class UnitConversion(
    val duration: Duration,        // e.g. "10m"  <-> Duration.ofMinutes(10)
    val period: Period,            // e.g. "1w"   <-> Period.ofWeeks(10)
    val memSize: ConfigMemorySize, // e.g. "1KiB" <-> ConfigMemorySize.ofBytes(1024)
    // this is another option to enable an additional serializer for a field
    @Serializable(with = StringBooleanSerializer::class)
    val textBoolean: Boolean       // e.g. "yes"   -> true
  )
  
  val converted = UnitConversion.serializer().parse(conf)
  ```

- Serializing a data object into a JSON String
  ```kotlin
  val json: String = BasicTypes.serializer().stringify(data)
  // or ConfigSerializer.stringify(BasicTypes.serializer(), data)
  ```

Deserialization details
-----------------------

- All constructor argument names should be defined in a config object
  - When a key is not found, `MissingKeyException` will be thrown
  - Even when a field is nullable, `null` should be explicitly defined (e.g. `{"path.to.field": null}`)

- Default values in a constructor are ignored for deserialization
  - Typesafe Config discourages putting config values outside of config files. See: [How to handle defaults](https://github.com/lightbend/config#how-to-handle-defaults)
  - `SerializationException` will be thrown

Links
-----

- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Typesafe Config](https://github.com/lightbend/config)
- [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md#hocon-human-optimized-config-object-notation)
- [config4k](https://github.com/config4k/config4k)
  - Unfortunately, this project has been inactive since Jan 2019.
    It doesn't work with the latest kotlin reflection library due to a class not found exception.

Example usage
-------------
1. Define a data class with default values
2. Create an object and Serialize it as JSON (dump it into application.conf)
3. Load the file by using `ConfigFactory.load()` in the Typesafe config library
4. Convert the `Config` object into the deserializer for the data class.

Note
----

- [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md)
  - Human-Optimized Config Object Notation
  - JSON-like configuration syntax
    - intuitive and readable
    - comments
    - reference to another property
    - and so on

- [Typesafe Config](https://github.com/lightbend/config)
  - A library for managing HOCON files
  - merge configs from multiple source such as files, system properties, environment variables

- [Config4k](https://github.com/config4k/config4k)
  - Kotlin extension functions for Typesafe Config
  - Object mapper: e.g. `config.extract<MyData>("path.to.mydata")`
  - Serializer: e.g. `MyData("foo", 42).toConfig("path.to.mydata")`
