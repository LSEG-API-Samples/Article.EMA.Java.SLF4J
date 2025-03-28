# How to integrate Enterprise Message API Java with Log4j Logging Framework using Maven
## Overview

**Update**: March 2025

**As of December 2021**: There are new serious vulnerabilities that were identified impacting the Apache Log4j utility. Please update the library to the latest version. You can find more detail regarding the vulnerability and the fix from the [Apache Log4j Security Vulnerabilities](https://logging.apache.org/log4j/2.x/security.html) page.
 
The [Enterprise Message API - Java Edition (EMA API)](https://developers.lseg.com/en/api-catalog/real-time-opnsrc/rt-sdk-java) (formerly known as Elektron Message API) implements the logging mechanism with the [Simple Logging Facade for Java (SLF4J)](https://www.slf4j.org/) as a facade for logging utility and bind it to the [Java Logging API](https://docs.oracle.com/javase/8/docs/technotes/guides/logging/overview.html) as a default logger. The usage of SLF4J allows developers integrate the EMA Java application with other Logging APIs like a de facto standard logging framework for Java-based application [Apache Log4j2](https://logging.apache.org/log4j/2.x/index.html) at deployment time.

The EMA API Java edition has been mavenized to support [Apache Maven](https://maven.apache.org/) and [Gradle](https://gradle.org/) build tools since Real-Time SDK Java (formerly known as Elektron SDK) version 1.2, therefore this article will show how to integrate your EMA Java 2.x application with Log4j2 using Maven.

## Introduction to SLF4J

Let’s start with overview of SLF4J framework. [SLF4J](https://www.slf4j.org/) or Simple Logging Facade for Java (SLF4J) serves as a simple facade or abstraction for various logging frameworks such as [java.util.logging](https://docs.oracle.com/en/java/javase/11/docs/api/java.logging/java/util/logging/package-summary.html), [logback](https://logback.qos.ch/), [log4j2](https://logging.apache.org/log4j/2.x/), etc. allowing the end user to plug in the desired logging framework at deployment time.

## Introduction to Log4J version 2

As part of [Apache Logging Services](https://logging.apache.org/), [Apache Log4j](https://logging.apache.org/log4j/2.x/) is a Java-based logging framework. It is versatile, industrial-grade framework that has been widely-used by developers for both open-source and enterprise projects.

Log4j is one of the first logging framework on Java.

That’s all I have to say about the logging framework.

## <a id="prerequisite"></a>Demo Prerequisite

Before I am going further, there is some prerequisite, dependencies, and libraries that the project is needed.

### Docker Desktop Application

You can build and run each EMA Java Provider and Consumer applications manually. However, it is easier to build and run with a simple ```docker compose``` command. 

The [Docker Desktop](https://www.docker.com/products/docker-desktop/) application is required to run all projects.

### Internet Access

The EMA Java library is also available on the [Maven Central](https://central.sonatype.com/) repository.

This project downloads the EMA libraries over internet to build and run applications.

### Java SDK

For the Java project, you need Java SDK version 11, 17, or 21 (either Oracle JDK or OpenJDK). Please see more detail on the [API Compatibility Matrix](https://developers.lseg.com/en/api-catalog/real-time-opnsrc/rt-sdk-java/documentation#api-compatibility-matrix) page.

### Apache Maven

The Java project uses [Apache Maven](https://maven.apache.org/) as a project build automation tool. 

That covers the prerequisite of this project.

## <a id="run"></a>How to run

Now we come to running a demo project. Firstly, open the ```docker-compose.yml``` file in the *ema_example*, and edit the ```volumes``` to match your machine project logs folder path (example: ```C:\\logs```).

```yml
name: emajava_log4j

services:
  provider:
    build:
      dockerfile: Dockerfile-provider
    volumes:
      - "<Path>\\logs:/app/logs"
  consumer:
    build:
      dockerfile: Dockerfile-consumer
    volumes:
      - "<Path>\\logs:/app/logs"
    depends_on:
      provider:
        condition: service_started
```

Then, build and run the Provider and Consumer projects with Docker. Please go to the *ema_example* folder via a command prompt application and run the following [Docker Compose](https://docs.docker.com/compose/) command.

```bash
docker compose up
```

To stop the projects, use the following Docker Compose command inside the same folder on a command prompt.

```bash
docker compose down
```

### Demo Example Results

#### Consumer_App result

```bash
07:10:48.600 [main] INFO  com.refinitiv.ema.consumer.Consumer_App - Starting Consumer_App application
07:10:53.810 [main] INFO  com.refinitiv.ema.consumer.Consumer_App - Consumer_App: Register Login stream
07:10:53.813 [main] INFO  com.refinitiv.ema.consumer.Consumer_App - Consumer_App: Register Directory stream
07:10:53.813 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Consumer_App.AppClient: Receives Market Price Refresh message
07:10:53.815 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Item Name: root
07:10:53.815 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Service Name: <not set>
07:10:53.816 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Item State: Open / Ok / None / 'Login accepted'
07:10:53.817 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - RefreshMsg
    streamId="1"
    domain="Login Domain"
    solicited
    RefreshComplete
    state="Open / Ok / None / 'Login accepted'"
    itemGroup="00 00"
    name="root"
    nameType="1"
    Attrib dataType="ElementList"
        ElementList
        ElementListEnd
    AttribEnd
RefreshMsgEnd

07:10:53.818 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - 

07:10:53.821 [main] INFO  com.refinitiv.ema.consumer.Consumer_App - Consumer_App: Send item request message
07:10:53.823 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Consumer_App.AppClient: Receives Market Price Refresh message
07:10:53.824 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Item Name: <not set>
07:10:53.825 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Service Name: <not set>
07:10:53.825 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Item State: Open / Ok / None / ''
07:10:53.829 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - RefreshMsg
    streamId="5"
    domain="Directory Domain"
    solicited
    RefreshComplete
    state="Open / Ok / None / ''"
    itemGroup="00 00"
    filter="0"
    Payload dataType="Map"
        Map
            MapEntry action="Add" key dataType="UInt" value="1" dataType="FilterList"
                FilterList
                    FilterEntry action="Set" filterId="1 dataType="ElementList"
                        ElementList
                            ElementEntry name="Name" dataType="Ascii" value="ELEKTRON_DD"
                            ....
                            ElementEntry name="AcceptingConsumerStatus" dataType="UInt" value="0"
                        ElementListEnd
                    FilterEntryEnd
                    FilterEntry action="Set" filterId="2 dataType="ElementList"
                        ElementList
                            ElementEntry name="ServiceState" dataType="UInt" value="1"
                            ElementEntry name="AcceptingRequests" dataType="UInt" value="1"
                        ElementListEnd
                    FilterEntryEnd
                FilterListEnd
            MapEntryEnd
        MapEnd
    PayloadEnd
RefreshMsgEnd

07:10:53.830 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - 

07:10:54.758 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Consumer_App.AppClient: Receives Market Price Refresh message
07:10:54.759 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Item Name: /EUR=
07:10:54.759 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Service Name: ELEKTRON_DD
07:10:54.760 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Item State: Open / Ok / None / 'Refresh Completed'
07:10:54.763 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - RefreshMsg
    streamId="6"
    domain="MarketPrice Domain"
    solicited
    RefreshComplete
    state="Open / Ok / None / 'Refresh Completed'"
    itemGroup="00 00"
    name="/EUR="
    serviceId="1"
    serviceName="ELEKTRON_DD"
    Payload dataType="FieldList"
        FieldList
            FieldEntry fid="3" name="DSPLY_NAME" dataType="Rmtes" value="/EUR="
            FieldEntry fid="15" name="CURRENCY" dataType="Enum" value="840"
            FieldEntry fid="21" name="HST_CLOSE" dataType="Real" value="39.00"
            FieldEntry fid="22" name="BID" dataType="Real" value="39.90"
            FieldEntry fid="25" name="ASK" dataType="Real" value="39.94"
            FieldEntry fid="30" name="BIDSIZE" dataType="Real" value="9.0"
            FieldEntry fid="31" name="ASKSIZE" dataType="Real" value="19.0"
        FieldListEnd
    PayloadEnd
RefreshMsgEnd

07:10:54.763 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - 

07:10:55.760 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Consumer_App.AppClient: Receives Market Price Update message
07:10:55.764 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Item Name: /EUR=
07:10:55.766 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - Service Name: ELEKTRON_DD
07:10:55.768 [pool-3-thread-1] INFO  com.refinitiv.ema.consumer.AppClient - UpdateMsg
    streamId="6"
    domain="MarketPrice Domain"
    updateTypeNum="0"
    name="/EUR="
    serviceId="1"
    serviceName="ELEKTRON_DD"
    Payload dataType="FieldList"
        FieldList
            FieldEntry fid="22" name="BID" dataType="Real" value="39.91"
            FieldEntry fid="25" name="ASK" dataType="Real" value="39.94"
            FieldEntry fid="30" name="BIDSIZE" dataType="Real" value="10.0"
            FieldEntry fid="31" name="ASKSIZE" dataType="Real" value="19.0"
        FieldListEnd
    PayloadEnd
UpdateMsgEnd
...
```

#### IProvider_App result

```bash
07:10:48.565 [main] INFO  com.refinitiv.ema.provider.IProvider_App - Starting IProvider_App application, waiting for a consumer application
07:10:51.624 [main] INFO  com.refinitiv.ema.provider.AppClient - IProvider_App.AppClient: Received Consumer Login Request Message
07:10:51.630 [main] INFO  com.refinitiv.ema.provider.AppClient - IProvider_App.AppClient: Sent Login Refresh message
07:10:54.754 [main] INFO  com.refinitiv.ema.provider.AppClient - IProvider_App.AppClient: Received Market Price Item Request message
07:10:54.757 [main] INFO  com.refinitiv.ema.provider.AppClient - IProvider_App.AppClient: Sent Market Price Refresh message
07:10:55.759 [main] INFO  com.refinitiv.ema.provider.IProvider_App - IProvider_App: Sent Market Price Update message
07:10:56.761 [main] INFO  com.refinitiv.ema.provider.IProvider_App - IProvider_App: Sent Market Price Update message
07:10:57.763 [main] INFO  com.refinitiv.ema.provider.IProvider_App - IProvider_App: Sent Market Price Update message
07:10:58.764 [main] INFO  com.refinitiv.ema.provider.IProvider_App - IProvider_App: Sent Market Price Update message
07:10:59.766 [main] INFO  com.refinitiv.ema.provider.IProvider_App - IProvider_App: Sent Market Price Update message
```

#### EMA Java result

EMA Java log messages from both demo applications will be in ema_log4j.log file.

```bash
2025-03-28 07:10:48,850 LEVEL-TRACE Thread-[main]  Method-initialize() Class name-com.refinitiv.ema.access.OmmBaseImpl   Message-loggerMsg
    ClientName: Consumer_1_1
    Severity: Trace
    Text:    Print out active configuration detail.
	 itemCountHint: 100000
	 serviceCountHint: 513
	 requestTimeout: 15000
	 dispatchTimeoutApiThread: 0
	 maxDispatchCountApiThread: 100
	 ...
	 restProxyPort: null
	 sessionEnhancedItemRecovery: true
loggerMsgEnd


2025-03-28 07:10:48,868 LEVEL-TRACE Thread-[main]  Method-initialize() Class name-com.refinitiv.ema.access.OmmBaseImpl   Message-loggerMsg
    ClientName: Consumer_1_1
    Severity: Trace
    Text:    Successfully open Selector.
loggerMsgEnd

....

2025-03-28 07:10:49,413 LEVEL-TRACE Thread-[main]  Method-initialize() Class name-com.refinitiv.ema.access.OmmServerBaseImpl   Message-loggerMsg
    ClientName: Provider_1_1
    Severity: Trace
    Text:    Print out active configuration detail.
	 itemCountHint: 10000
	 serviceCountHint: 10000
	 requestTimeout: 15000
	 dispatchTimeoutApiThread: 500
	 maxDispatchCountApiThread: 500
	 maxDispatchCountUserThread: 500
	 ...
	 maxFieldDictFragmentSize: 8192
	 maxEnumTypeFragmentSize: 12800
loggerMsgEnd


2025-03-28 07:10:49,419 LEVEL-TRACE Thread-[main]  Method-initialize() Class name-com.refinitiv.ema.access.OmmServerBaseImpl   Message-loggerMsg
    ClientName: Provider_1_1
    Severity: Trace
    Text:    Successfully open Selector.
loggerMsgEnd


2025-03-28 07:10:49,439 LEVEL-TRACE Thread-[main]  Method-initialize() Class name-com.refinitiv.ema.access.OmmServerBaseImpl   Message-loggerMsg
    ClientName: Provider_1_1
    Severity: Trace
    Text:    Successfully created Reactor.
loggerMsgEnd
...
```

## Conclusion

The EMA Java API is implemented on top of SLF4J API as a facade for logging utility. The latest version of API allows developers to integrate EMA Java application with the preferred Logging framework by changing the repository, dependencies via their Java project management (Maven or Gradle), and then set the log configuration files without touching the application source code. 

That covers all I wanted to say today.

## References

For further details, please check out the following resources:

- [Real-time Java SDK page](https://developers.lseg.com/en/api-catalog/real-time-opnsrc/rt-sdk-java) on the [LSEG Developer Community](https://developers.lseg.com/) website.
- [Simple Logging Facade for Java (SLF4J)](https://www.slf4j.org/) website.
- [Apache Log4j 2](https://logging.apache.org/log4j/2.x/) website.
- [Developer Webinar: Introduction to Enterprise App Creation With Open-Source Enterprise Message API](https://www.youtube.com/watch?v=2pyhYmgHxlU)
- [Learn how to direct EMA Java log to Java Logging API](https://developers.lseg.com/en/article-catalog/article/learn-how-to-use-ema-java-log-with-java-logging-api) article.

For any question related to this article or Enterprise Message API page, please use the Developer Community [Q&A Forum](https://community.developers.refinitiv.com/).