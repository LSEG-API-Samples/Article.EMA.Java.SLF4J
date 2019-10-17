mvn archetype:generate -DgroupId=com.refinitiv.ema -DartifactId=esdk131_maven -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false

# How to integrate Elektron Message API Java with Log4j Logging Framework using Maven
## Overview
 
[Elektron Message API - Java Edition (EMA API)](https://developers.refinitiv.com/elektron/elektron-sdk-java) allows developers integrate the EMA Java application with [Apache Log4j](https://logging.apache.org/log4j/2.x/) which is a de facto standard logging framework for Java-based application at deployment time by using the [Simple Logging Facade for Java (SLF4J)](https://www.slf4j.org/) API as a facade for logging utility. The [previous article](https://developers.refinitiv.com/article/how-integrate-elektron-message-api-java-edition-log4j-logging-framework) shows how to integrate Log4j with EMA Java application in a manual way which is suitable for earlier versions of EMA Java API. However, the API has been mavenized to support [Apache Marven](https://maven.apache.org/) and [Gradle](https://gradle.org/) build tools since Elektron SDK version 1.2, therefore this article will show how to integrate  your EMA Java 1.3.x application with Log4j in a Maven way.

<!--
Even though the EMA Java API binds the logging mechanism with [Java Logging API](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) by default, developers can change the binding library and logging configuration files to bind the EMA Java aplication with Log4j or others framework that supported SLF4J without modify the application source code. 
-->

## How to integrate EMA Java Application with Logging Framework in Maven
The Elektron SDK Java are now available in [Maven Central Repository](https://search.maven.org/). The [EMA Java library](https://search.maven.org/artifact/com.thomsonreuters.ema/ema/) can be downloaded by defining the following dependency in Maven's POM.xml file.
```
<dependency>
  <groupId>com.thomsonreuters.ema</groupId>
  <artifactId>ema</artifactId>
  <version>3.3.1.0</version>
</dependency>
``` 
Note: This article is based on EMA Java version 3.3.1 L1. You can change the library version in ```<version>``` configuration to match your project.

The above POM.xml configuration automatic resolves the API dependencies by downloading the following required libraries for the application. 

![](.\images\article15\ema_dependencies.png "EMA Java Dependencies")

You will see that Maven automatic downloads **slf4j-api** and **slf4j-jdk14** libraries for binding the logging mechanism with [Java Logging API](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) by default. Developers can perform the following steps to integrate the EMA Java Maven application log with Log4j framework. 
1. Configure POM.xml file's EMA dependency declaration to not load slf4j-jdk14 libary.
2. Add SLF4J-Log4j and Log4j dependencies in POM.xml file.
3. Configure configurations file to Java classpath or JVM option.

## Integration with Log4j 2 framework
Developers can configure the EMA Java dependency declaration in POM.xml file to exclude the SLF4J-JDK14 library using [Maven Dependency Exclusions](https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html) feature.

```
<dependencies>
    <dependency>
        <groupId>com.thomsonreuters.ema</groupId>
        <artifactId>ema</artifactId>
        <version>3.3.1.0</version>
        <exclusions>
            <exclusion>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-jdk14</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

The Log4j 2 framework requires the following dependencies to intergrate with SLF4J framework, so the applicaion needs to add the following Log4j and SLF4J binding dependencies in POM.xml file.
- log4j-api
- log4j-core
- log4j-slf4j-impl

```
<dependencies>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>2.12.1</version>
    </dependency>

    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.12.1</version>
    </dependency>

    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>2.12.1</version>
    </dependency>
</dependencies>
```

Note: This article is based on Log4j version 2.12.1. You can change the library version in ```<version>``` configuration to match your project.

Then developers can configure Log4j 2 configurations file to Java classpath or JVM option ```-Dlog4j.configurationFile``` at runtime to let the EMA Java application uses Log4j 2 configurations file. 

### Example Log4j 2 configurations file (in XML format)
The example file is saved as "\resource\log4j2.xml" file.
```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="current date-%d LEVEL-%-5p Thread-[%t]  Method-%M()   Class name-%C   Message-%m%n"/>
        </Console>
        <File name="File" fileName="../logs/ema_log4j.log" immediateFlush="false" append="false">
            <PatternLayout pattern="current date-%d LEVEL-%-5p Thread-[%t]  Method-%M()   Class name-%C   Message-%m%n"/>
        </File>
    </Appenders>
    <loggers>
        <Logger name="com.thomsonreuters.ema" level="TRACE"/>
        <root level="TRACE">
            <appender-ref ref="Console"/>
            <appender-ref ref="File"/>
        </root>
    </loggers>
</Configuration>
```

Please find the full detail of Log4j configuration in [Log4j manual](https://logging.apache.org/log4j/2.x/manual/configuration.html).

### Running the application
You can run the EMA Java example with JVM ```-Dlog4j.configurationFile``` option points to the log4j2.xml file. Please note that if you do not build the application in to a single-all-depencies jar file, you need to include the Log4j 2 libraries files in the Java classpath too. 

### Build and running Demo

The EMA Java Interactive-Provider and Consumer demo examples are available in *ema_example* folder. The demo applications utlize Maven and Log4J to log EMA Java API and application activities to console and log file.

```
java -Dlog4j.configurationFile=./resources/log4j2.xml -cp .;target/esdk131_log4j-1.0-jar-with-dependencies.jar com.thomsonreuters.ema.examples.training.consumer.series100.example100__MarketPrice__Streaming.Consumer
```
An example result log messages with Log4j 2 is shown below:

```
current date-2019-09-17 17:43:23,228 LEVEL-TRACE Thread-[main]  Method-log()   Class name-com.thomsonreuters.ema.access.ConfigErrorTracker   Message-loggerMsg
    ClientName: EmaConfig
    Severity: Trace
    Text:    reading configuration file [EmaConfig.xml]; working directory is [D:\Project\Code\SLF4J\esdk131_project\maven_console]
loggerMsgEnd

....

current date-2019-09-17 17:43:23,344 LEVEL-TRACE Thread-[main]  Method-<init>()   Class name-com.thomsonreuters.ema.access.CallbackClient   Message-loggerMsg
    ClientName: LoginCallbackClient
    Severity: Trace
    Text:    Created LoginCallbackClient
loggerMsgEnd


current date-2019-09-17 17:43:23,345 LEVEL-TRACE Thread-[main]  Method-initialize()   Class name-com.thomsonreuters.ema.access.LoginCallbackClient   Message-loggerMsg
    ClientName: LoginCallbackClient
    Severity: Trace
    Text:    RDMLogin request message was populated with this info: 
	LoginRequest: 
	streamId: 1
	userName: user
	streaming: true
	nameType: 1
	applicationId: 256
	applicationName: ema
	position: 10.42.68.117/U8004042-TPL-A

loggerMsgEnd


current date-2019-09-17 17:43:23,347 LEVEL-TRACE Thread-[main]  Method-<init>()   Class name-com.thomsonreuters.ema.access.CallbackClient   Message-loggerMsg
    ClientName: DictionaryCallbackClient
    Severity: Trace
    Text:    Created DictionaryCallbackClient
loggerMsgEnd
```

## Conclusion
The EMA Java API is implemented on top of SLF4J API as a facade for logging utility. It allows developers integrate EMA Java application with their prefer Logging framework by replacing the Logging library and configurations files without touching the application source code. 

## References
For further details, please check out the following resources:
* [Elektron Java API page](https://developers.refinitiv.com/elektron/elektron-sdk-java/) on the [Thomson Reuters Developer Community](https://developers.refinitiv.com/) web site.
* [Simple Logging Facade for Java (SLF4J)](https://www.slf4j.org/) web site.
* [Apache Log4j 2](https://logging.apache.org/log4j/2.x/) web site.
* [Elektron Message API Java Quick Start](https://developers.refinitiv.com/elektron/elektron-sdk-java/quick-start)
* [Developer Webinar: Introduction to Enterprise App Creation With Open-Source Elektron Message API](https://www.youtube.com/watch?v=2pyhYmgHxlU)

For any question related to this article or Elektron Message API page, please use the Developer Community [Q&A Forum](https://community.developers.refinitiv.com/spaces/72/index.html).


# Build and Compile Project

```
mvn clean dependency:copy-dependencies package
```

Then, all dependencies libraries will be downloaded to *target\dependency* folder.

# Running Project

```
java -Dlog4j.configurationFile=./resources/log4j2.xml -cp .;target/dependency/commons-collections-3.2.2.jar;;target/dependency/commons-configuration-1.10.jar;target/dependency/commons-lang-2.6.jar;target/dependency/commons-logging-1.2.jar;target/dependency/slf4j-api-1.7.12.jar;target/dependency/log4j-api-2.12.1.jar;target/dependency/log4j-core-2.12.1.jar;target/dependency/log4j-slf4j-impl-2.12.1.jar;target/dependency/ema-3.3.1.0.jar;target/dependency/upa-3.3.1.0.jar;target/dependency/upaValueAdd-3.3.1.0.jar;target/dependency/;target/esdk131_log4j-1.0-SNAPSHOT.jar com.thomsonreuters.ema.examples.training.consumer.series100.example100__MarketPrice__Streaming.Consumer
```

```
java -Dlog4j.configurationFile=./resources/log4j2.xml -cp .;target/esdk131_log4j-1.0-jar-with-dependencies.jar com.thomsonreuters.ema.examples.training.consumer.series100.example100__MarketPrice__Streaming.Consumer
```

```
java -Dlog4j.configurationFile=./resources/log4j2.xml -cp .;target/esdk131_log4j-1.0-jar-with-dependencies.jar com.thomsonreuters.ema.examples.training.consumer.series100.example120__MarketPrice__FieldListWalk.Consumer
```

```
java -Dlog4j.configurationFile=./resources/log4j2.xml -cp .;target/esdk131_log4j-1.0-jar-with-dependencies.jar com.thomsonreuters.ema.examples.training.iprovider.series200.example200__MarketPrice__Streaming.IProvider
```

```
java -Dlog4j.configurationFile=./resources/log4j2.xml -cp .;target/esdk131_maven-1.0-SNAPSHOT-jar-with-dependencies.jar com.refinitiv.ema.consumer.Consumer_App
```



```
java -Dlog4j.configurationFile=./resources/log4j2.xml -cp .;target/esdk131_maven-1.0-SNAPSHOT-jar-with-dependencies.jar com.refinitiv.ema.provider.IProvider_App
```



