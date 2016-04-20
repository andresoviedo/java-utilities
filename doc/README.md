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