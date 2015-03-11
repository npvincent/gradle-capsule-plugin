# gradle-capsule-plugin

A Gradle plugin for [Capsule], the packaging and deployment tool for JVM apps.

Capsule allows you to package your app and it's dependencies into a single jar for easy and efficient deployment.

This readme assumes some familiarity with the [Capsule] project.

[Capsule]:https://github.com/puniverse/capsule

# Adding the Plugin

### In Gradle 2.1 and later

```groovy
plugins {
  id "us.kirchmeier.capsule" version "1.0-rc1"
}
```

### In earlier versions

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'us.kirchmeier:gradle-capsule-plugin:1.0-rc1'
    }
}

apply plugin: 'us.kirchmeier.capsule'
```


# Quick Start

Two convenience task types are provided for reasonable behavior with minimal configuration:

* `FatCapsule`, which embeds your application and all dependencies into one jar.
* `MavenCapsule` which contains your application and downloads your dependencies on startup. It uses the [maven caplet](https://github.com/puniverse/capsule-maven).

Additional, the base `Capsule` type is provided with very few defaults and may be used for advanced use cases.

```groovy
task fatCapsule(type: FatCapsule) {
  applicationClass 'com.foo.CoolCalculator'
}

task mavenCapsule(type: MavenCapsule) {
  applicationClass 'com.foo.CoolCalculator'
}
```

# Documentation

To build a capsule, one of the following properties must be defined:

* `applicationClass` - The Main class
* `application` - A maven dependency containing a main class
* `capsuleManifest.applicationScript` - A startup script to run instead of applicationClass

## Manifest Options

`capsuleManifest` is a helper for defining the properties for configuring the capsule.
It is an instance of the [`CapsuleManifest`][src] class.

Please refer to the [source file][src] for a list of all possible properties.
Refer to the [Capsule] documentation for documentation on the properties.

[src]: https://github.com/danthegoodman/gradle-capsule-plugin/blob/master/src/main/groovy/us/kirchmeier/capsule/manifest/CapsuleManifest.groovy

```groovy
task myCapsule(type:MavenCapsule){
  applicationClass 'com.foo.CoolCalculator'

  capsuleManifest {
    systemProperties['java.awt.headless'] = true
    repositories << 'jcenter'
  }
}
```

## Manifest Mode, Platform and JVM specific options

DRK write

## Application Source

`applicationSource` defines how the application is brought into the capsule.

The `FatCapsule` defaults to output from the `jar` task.
The `MavenCapsule` defaults to the compiled sources from `sourceSets.main.output`.

It is passed directly into a `from(...)` on the underlying implementation, so it may be a task, file, sourceset or more.

```groovy
task myCapsule(type:FatCapsule){
  applicationClass 'com.foo.FancyCalculator'
  applicationSource myFancyJar
}
```

## Embedding Jars

`embedConfiguration` defines which configuration contains the dependencies to embed.

The `FatCapsule` defaults to the `runtime` configuration. The `MavenCapsule` has no default.

```groovy
task myCapsule(type:FatCapsule){
  applicationClass 'com.foo.FancyCalculator'
  embedConfiguration configurations.runtime
}
```

## Downloadable Dependencies

`capsuleManifest.dependencyConfiguration` defines which configuration contains the dependencies to download on startup.

`capsuleManifest.dependencies` is a list of strings which are also downloaded on startup.
You may use this if you have a dependency you don't need gradle to care about.

The `MavenCapsule` defaults the dependencyConfiguration to the `runtime` configuration. The `FatCapsule` has no default.

```groovy
task myCapsule(type:MavenCapsule){
  applicationClass 'com.foo.BeautifulCalculator'
  capsuleManifest {
    dependencyConfiguration configurations.runtime
    dependencies << 'log4j:log4j:1.2.17'
  }
}
```

## "Really Executable" Capsules

`reallyExecutable` will make a capsule executable as a script in unix environments.
You may read more in the [capsule documentation][reallyexec].

`reallyExecutable.regular()` is the default and uses a plan execution script.
`reallyExecutable.trampoline()` will use the trompoline script.
`reallyExecutable.script(file)` may be set to define your own script.

[reallyexec]:https://github.com/puniverse/capsule#really-executable-capsules

```groovy
task executableCapsule(type:FatCapsule){
  applicationClass 'com.foo.CoolCalculator'
  reallyExecutable //implies regular()
}

task trampolineCapsule(type:MavenCapsule){
  applicationClass 'com.foo.CoolCalculator'
  reallyExecutable { trampoline() }
}

task myExecutableCapsule(type:FatCapsule){
  applicationClass 'com.foo.CoolCalculator'
  reallyExecutable {
    script file('my_script.sh')
  }
}
```

## Changing the capsule implementation

For advanced usage, `capsuleConfiguration` and `capsuleFilter` control where the capsule implementation comes from.
You may override them to change implementations, or set them to null and provide your own implemntation somehow else.
If you override these, you should also change the `capsuleManifest.mainClass` property.

The base `Capsule` type has a default `capsuleConfiguration` of `configurations.capsule`, which is provided by this plugin.

The `FatCapsule` type includes a `capsuleFilter` to include only the 'Capsule.class'file.

The `MavenCapsule` type has a default `capsuleConfiguration` of `configurations.mavenCapsule`, which is provided by this plugin.

```groovy

configurations {
  myCapsule
}

dependencies {
  myCapsule 'com.foo:MyCapsuleImplementation:0.8'
}

task myCapsule(type: MavenCapsule){
  applicationClass 'com.foo.CoolCalculator'
  capsuleConfiguration configurations.myCapsule
}
```


## Type Heirarchy

`Capsule` is the base class for both `FatCapsule` and `MavenCapsule`.
It comes with almost no defaults, and is an ideal starting ground for advanced use cases.


## Task Defaults

By default, all capsules have the 'capsule' classifier and use the main implementation of the capsule library.

`FatCapsule` and `MavenCapsule` are task types which provide reasonable behavior with minimal configuration.
Aside from these default values, there is no distinction between them and the base `Capsule` task type.

```groovy
task fatCapsuleDefaults(type:Capsule){
  // Include the application's jar in the capsule
  applicationSource jar

  // Embed all runtime dependencies
  embedConfiguration = configurations.runtime

  // Limit the capsule library, since the dependencies are embedded
  capsuleFilter = { include 'Capsule.class' }
}

task mavenCapsuleDefaults(type:Capsule){
  // Include the application source in the capsule
  applicationSource sourceSets.main.outputs

  capsuleManifest {
    // Add all runtime dependencies as downloadable dependencies
    dependencyConfiguration = configurations.runtime
  }
}
```
