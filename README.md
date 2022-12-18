# Short Maven Tutorial

Very basic tutorial for using Maven to build Java applications.
This covers only the very basics using examples and tries to explain how the most relevant parts work. This does not try
to be a coding guideline or example of good or proper coding, rather a quick quide on how Maven can be used in a project
and testing.

## 1. Project Structure

The project structure for Maven projects usually follows the basic Maven template

```
root/
├── pom.xml             (project configuration file)
├── .mvn/               (optional - contains Maven wrapper)
└── src/
    ├── main/
    │   ├── java/       (java sources)
    │   └── resources/
    └── test/
        ├── java/       (java test sources)
        └── resources/             
```

This is usually the simplest and easiest to use file structure as it is the default structure for Maven. Using this
template will make your life a lot easier. It is possible to create project from templates using
Maven [Archetypes](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html), but the is not covered
here. Basically you just type `mvn archetype:generate` and follow the instructions.

The source folders are quite self-explanatory, but there are some things to note. By default, Maven will pick up
any `*.java` files under any subdirectory in `src/main/java`, but it will exclude anything else. This means that if you
are using for example JavaFX and are including the `*.fxml` templates next to the sources you will need to additional
configuration to the `pom.xml` file. This additional configuration applies for anything except `*.java` files.

The resources folder is the opposite; everything gets copied to the target folder and into the Jar file under the exact
same path they are under in resources. This means that if you for example wish to include files in `META-INF` in the
resulting jar, you will need to create a `META-INF` folder directly under resources.

<details>
   <summary><b>Example: Including txt files in build output</b></summary>

```xml

<build>
    <testResources>
        <testResource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.*</include>
            </includes>
        </testResource>
        <testResource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.txt</include>
            </includes>
        </testResource>
    </testResources>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.*</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.txt</include>
            </includes>
        </resource>
    </resources>
</build>
```

This addition to the `pom.xml` file build section instructs maven to grab all `*.txt` files from the source folder and
place them in the right place in the output. Do note that this requires re-specifying the default resources folder to
not ignore stuff under it. This same example can be used to exclude certain files from build output if desired or
include files in testing based on name patterns. Including additional source folders is done in a similar manner.

</details>

<details>
  <summary><b>Example: Alternative Structures</b></summary>

You shouldn't be surprised if you happen to see this variant too

```
root/
├── pom.xml
├── .mvn/
└── src/
│   ├── java/
│   └── resources/
└── test/
    ├── java/
    └── resources/
```

sometimes you can even see Kotlin included

```
root/
├── pom.xml             (project configuration file)
├── .mvn/               (optional - contains Maven wrapper)
└── src/
    ├── main/
    │   ├── java/       (java sources)
    │   ├── kotlin/     (kotlin sources)
    │   └── resources/
    └── test/
        ├── java/       (java test sources)
        ├── kotlin/     (kotlin test sources)
        └── resources/             
```

and sometimes the Kotlin sources are mixed in with the Java ones.

All of these structures will have subtle changes in the `pom.xml` build section where the source sets are specified for
tests and compilation. If the structure is different from what you are used to it is best to look at the POM file before
doing anything else.

</details>

<details>
  <summary><b>Example: Multimodule Projects</b></summary>

```
root/
├── pom.xml                 (parent project configuration file)
├── .mvn/                   (optional - contains Maven wrapper)
└── module-1/
    ├── pom.xml             (project configuration file for module)
    ├── .mvn/
    └── src/
        ├── main/
        │   ├── java/
        │   └── resources/
        └── test/
            ├── java/
            └── resources/

```

For multimodule projects the root contains only a `pom.xml` and subfolders for each submodule with their own `pom.xml`
files. In multimodule projects most of the stuff in the parent POM gets shared into the individual modules, but plugins
and such might need their configuration per module. Submodules can depend on other submodules.


</details>

## 2. Running Maven

If you have Maven installed locally you can use

> mvn --version

otherwise you can use the Maven wrapper included in the project

> ./mvnw --version

on Linux

> .\mvnw.cmd --version

on Windows (PowerShell).

Also, you can use any bundled version in your IDE.

If you use the wrapper you can set an alias for the wrapper

> alias mvn='./mvnw'

on Linux

> Set-Alias -name mvn -value .\mvnw.cmd

on Windows (PowerShell)

> doskey mvn=.\mvnw.cmd $*

on Windows (Classic Shell).

## 3. Basic maven commands

This section assumes you are using the `mvn` command.

Here is a basic list of commands you will often use:

1. `--version` Displays version information
    - Mostly used for debugging and verifying Maven works.
2. `clean` Cleans the project output
    - this is used to clean files before building the project again.
3. `compile` Compiles the project
    - This compiles all source files under the `src/main` directory and moves any __missing__ resources to the target
      folder.
    - If you change resources it is many times easier to use `mvn clean compile` to ensure the resources get updated.
      Especially if they are generated during compilation as Maven compiles only changed sources and this can mess up
      some tools that generate resource files from annotations in Java sources.
4. `test` Runs unit tests for the project
    - Runs all test classes under `src/test` which have a name of the following formats:
        - `**/Test*.java`
        - `**/*Test.java`
        - `**/*Tests.java`
        - `**/*TestCase.java`
    - This most often requires the
      Maven [Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/) plugin to be specified in
      the `pom.xml`.
    - This specifically runs __unit__ tests and not __integration__ tests. Integration tests are run in the `verify`
      target using the Maven [Failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/) plugin.
5. `package` Packages the project into a jar
    - You can use the [Assembly](https://maven.apache.org/plugins/maven-assembly-plugin/) plugin to create an executable
      jar with all dependencies included.
6. `verify` Runs integration tests on the jar
    - Runs all test classes under `src/test` which have a name of the following formats:
        - `**/IT*.java`
        - `**/*IT.java`
        - `**/*ITCase.java`
7. `install` Installs the jar into the local Maven repository

These commands all depend on the command above them and are run in the order shown here. If you invoke `mvn install` all
the previous stages get executed too. There are some very useful command line parameters you can give maven and you can
see them using

> mvn --help

You can skip tests by adding `-DskipTests` to any command

> mvn -DskipTests package

## 4. The POM file

Take a look at the [POM Reference](https://maven.apache.org/pom.html) to get a good overview of the `pom.xml`.

Here is a short summary nevertheless

### Basic Properties

```xml

<modelVersion>4.0.0</modelVersion>

<groupId>dev.jonium</groupId>
<artifactId>maven-and-testing</artifactId>
<version>1.0-SNAPSHOT</version>
```

These properties define the coordinates for the artifact. This basically creates a unique name for the resulting jar and
an associated version. This will be used when using this project as a dependency or when deploying somewhere.

### properties

```xml

<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <!-- versions -->
    <mockito.version>4.10.0</mockito.version>
    <junit.version>5.9.1</junit.version>
</properties>
```

This section can be used to define common attributes and create new attributes to use in the POM. Any value defined here
can be referenced via `${value}`. Useful for setting the Java version and source encoding along with dependency versions
where many share the same version.

### build

This section defines plugins, resources and sources for the project. This section has a lot of uses and it is too long
to explain here. Some common plugins that require configuration in projects:

- [Compiler](https://maven.apache.org/plugins/maven-compiler-plugin/)
- [Failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/)
- [Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [JAR](https://maven.apache.org/plugins/maven-jar-plugin/)
- [WAR](https://maven.apache.org/plugins/maven-war-plugin/)
- [Shade](https://maven.apache.org/plugins/maven-shade-plugin/)
- [Assembly](https://maven.apache.org/plugins/maven-assembly-plugin/)

### dependencies

This contains any other libraries that should be included in the build.
Any dependency with scope `compile` will be included in the build and if you use a plugin to create a jar with
dependencies it will be included in there. `test` scope is only for tests and `provided` scope is something needed only
during compile and will be later be already in the environment when this library is deployed or used. There is
also `system` scope for including jar files from somewhere in your system, but it is not recommended to be used.

The place to find dependencies is [Maven Central](https://search.maven.org/) another good place to easily search
dependencies is the [mvnrepository](https://mvnrepository.com/repos/central) site.

### dependencyManagement

This is like the dependency block, but anything here will override anything in the dependency block. This is a place to
import BOM files for example. These BOM files contain versions for multiple dependencies and allow for easier dependency
management for libraries consisting of multiple dependencies. If a BOM is defined here with type `POM` and
scope `import`, any dependency contained in it can be referenced in the dependency section without defining its version.

### Other Sections

Please see the link in the beginning of this section. There are multiple other sections in the POM to deploy, release,
ci/cd and others, but they are not required for a simple project. The sections here should be enough to get you going.

Here is a guide to [make your jar executable](https://maven.apache.org/shared/maven-archiver/examples/classpath.html)

## 5. Example Project Summary

The sample project contains a basic calculator that evaluates expressions of the form `1 + 2`. The calculator has some
unit tests and a couple of integration tests. There are plenty of edge-cases where the calculator does not work and a
failing integration test. You can practise using maven by fixing the failing test and improving the existing tests in
the
project. The tests have some examples on how to use Junit5 and Mockito to create very basic tests and also contain a
test harness for the application _main_ method.

The integration tests are not that useful in this project as there is not really anything that would require them. The
real use case would be to test WAR files in deployment or to test that any generated files were included correctly.

### Use Maven to create the app

First see if the project builds

> mvn compile

Then you can run the unit tests

> mvn test

If all tests pass you can package the JAR and run any integration tests against it

> mvn package verify

Fix any failing tests (or ignore tests)

Then you can run the resulting jar file

> java -jar target/Calculator-0.0.1.jar

on Linux

> java -jar target\Calculator-0.0.1.jar

on Windows

You can regenerate all files in the `target` folder by first running

> mvn clean

before any other stages

### What to do now?

Here are some ideas you can implement with this to practise using Maven:

- Use the Maven Shade plugin instead of the Assembly plugin
- Add some more dependencies to the project
- Try and create a `*.txt` file that the program reads from inside the jar to create a greeting
- Create more unit tests for the Calculator
- Create more operations for the Calculator

Going even further you can:

- Refactor the project to contain the Calculator API interfaces in another module and use this as a dependency in the
  CalculatorImpl module
- Use [JLink](https://openjdk.org/jeps/282) and [JPackage](https://openjdk.org/jeps/392) (java 17) to create
  a self-contained executable using Maven.
    - Here is a working example with JavaFX [github link](https://github.com/dlemmermann/JPackageScriptFX)
- Use JavaFX to create an UI for the Calculator and include the FXML files in the JAR
