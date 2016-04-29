Documentation
============= 

maven
=====

log4j2 dependencies
===================

These are the required dependencies to move from log4j 1.2 to log4j 2.
Remember to put also the new log4j2.xml file in the classpath.
log4j2 requires java version 7


    <dependency>
    	<groupId>org.apache.logging.log4j</groupId>
    	<artifactId>log4j-api</artifactId>
    	<version>2.5</version>
    </dependency>
    <dependency>
    	<groupId>org.apache.logging.log4j</groupId>
    	<artifactId>log4j-core</artifactId>
    	<version>2.5</version>
    </dependency>
    <!-- this to make slf4j to log to log4j 2 -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>2.5</version>
    </dependency>
    <dependency>
    <!-- this to make log4j-1.2 call to log to log4j 2 bridge -->
	    <groupId>org.apache.logging.log4j</groupId>
	    	<artifactId>log4j-1.2-api</artifactId>
	    	<version>2.5</version>
    </dependency>
    <!-- this to make jul to log to log4j 2 -->
    <dependency>
    	<groupId>org.apache.logging.log4j</groupId>
    	<artifactId>log4j-jul</artifactId>
    	<version>2.5</version>
    </dependency>


This is an example of log4j-test.xml


    <?xml version="1.0" encoding="UTF-8"?>
    <Configuration status="WARN">
      <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
          <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
      </Appenders>
      <Loggers>
        <Root level="info">
          <AppenderRef ref="Console"/>
        </Root>
      </Loggers>
    </Configuration>


Mongo
=====

Settings to make mongo jul logger log to log4j2:


	// redirect mongo jul calls to log4j
    System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager");


Gradle
======

To install gradle in ubuntu (version may not be the latest)

    $ sudo add-apt-repository ppa:cwchien/gradle
    $ sudo apt-get update
    $ sudo apt-cache search gradle
    $ sudo apt-get install gradle-2.13

To build with gradle. Output will be at /bin folder

    $ gradle
    
To assemble with gradle. Output will be at /build

    $ gradle assemble
    


