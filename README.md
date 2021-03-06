Java-1-Class-Utilities
======================

![codeship badge](https://codeship.com/projects/84b26ce0-646d-0134-7bcf-46895dbeddb7/status?branch=master)

**Last update** 01/03/2017


Description
===========

Series of common utility classes (sometimes mini-frameworks), each packed in a single class, very useful to any app.
You can copy freely the class you need into your project.

Each utility was built along the time, and it was implemented because I didn't find any similar
on Internet, or maybe because it was not available at that time... whatever. I still keep adding new utilities.


Utilities
========= 

I would be glad if you find any of the classes useful. Here are some:

* [SpringTTLCache.java](src/main/java/org/andresoviedo/util/cache/SpringTTLCache.java): Spring/JDK Cache with TTL + Junit
* [TilesWithPropertiesView.java](src/main/java/org/andresoviedo/util/spring/mvc/TilesWithPropertiesView.java): TilesView with exposed attributes
* [ConfigurationUtils.java](src/main/java/org/andresoviedo/util/configuration/ConfigurationUtils.java): Bind *.properties file to Pojo
* [ProgramUtils.java](src/main/java/org/andresoviedo/util/program/ProgramUtils.java): Only 1 process running. Save program status 
* [EmbeddedMongo.java](src/main/java/org/andresoviedo/util/mongo/EmbeddedMongo.java): Embedded mongo to use in any junit
* [ExpressionEngine.java](src/main/java/org/andresoviedo/util/expression/ExpressionEngine.java): Expression engine to evaluate javascript expressions
* [RequestsPerSecondController.java](src/main/java/org/andresoviedo/util/program/ProgramUtils.java): Dont exceed number of requests per second in your program
* [JSonUtils.java](src/main/java/org/andresoviedo/util/json/JsonUtils.java):  Compare org.json.JSONObject & org.json.JSONArray no matter which is the order of the inner elements
* [ProvincesUtil.java](src/main/java/org/andresoviedo/util/model/ModelUtils.java):  Enumeration with all the Spain provinces with it's respective code
* [MySQLPhoneCallAlert.java](src/main/java/org/andresoviedo/util/alert/MySQLPhoneCallAlert.java): Make a select to MySQL and if it's not OK the class will make a phone call to your cell phone
* [BeanUtils.java](src/main/java/org/andresoviedo/util/bean/BeanUtils.java): Print any POJO into a pretty printable String (recursive). Also set (or get) nested properties (bean1.prop1.val2)
 into a Bean. Maps, Lists, Enums, etc are supported
* [CyclicBuffer.java](src/main/java/org/andresoviedo/util/data/CyclicBuffer.java): LIFO queue wich stores the N most recent items   
* [SortedHashList.java](src/main/java/org/andresoviedo/util/data/SortedHashList.java): A merge of List,Maps & Comparator 
* [UnicodeInputStream.java](src/main/java/org/andresoviedo/util/encoding/UnicodeInputStream.java): Removes unicode chars from a stream
* [EasyFTPsClient.java](src/main/java/org/andresoviedo/util/ftp/EasyFTPsClient.java): Connect to FTPS servers
* [JarUtils.java](src/main/java/org/andresoviedo/util/jar/JarUtils.java): Browser JAR contents
* [LazyRollingFileAppender.java](src/main/java/org/andresoviedo/util/log4j/LazyRollingFileAppender.java): Don't create log4j log files if nothing logged
* [ModemManager.java](src/main/java/org/andresoviedo/util/modem/ModemManager.java): Controls PC Modem
* [ZipHelper.java](src/main/java/org/andresoviedo/util/zip/ZipHelper.java): Download & unzip multipart zip files in parallel 
* [WindowsUtils.java](src/main/java/org/andresoviedo/util/windows/WindowsUtils.java): Create & delete start menu items in Windows XP & Windows 7
* [DependantTasksExecutor.java](src/main/java/org/andresoviedo/util/tasks/DependantTasksExecutor.java): Execute multiple tasks in parallel even when any task depended on the result of another tasks(s).
* [StructuredStringSerializer.java](src/main/java/org/andresoviedo/util/serialization/api2/StructuredStringSerializer.java): Write/read any POJO to(from) a String with an annotated structured format
* [TasksScheduler.java](src/main/java/org/andresoviedo/util/schedule/api1/TasksScheduler.java): Schedule tasks & manage them with a Swing GUI control panel
* [RunHelper.java](src/main/java/org/andresoviedo/util/run/RunHelper.java): Run external programs
* [IOHelper.java](src/main/java/org/andresoviedo/util/io/IOHelper.java): Copy files (zips) and filter it's contents

Series of scripts useful to any app:
* [Windows executable zip](src/main/resources/org/andresoviedo/util/windows/7zip_installer/make.bat): Windows script to make executable zips with 7zip.


ModemManager
============

This utility depends on the javax.comm API, but that's not available on maven repository because of legal issues.
So, if you want to use the ModemManager utility, download the javax.comm library and install it into your local maven repository.
Then, uncomment the ModemManager class, uncomment the maven dependency from the pom.xml and recompile the project. 

To install the javax.comm library execute the following command:

    mvn install:install-file -Dfile=./comm-3.0-u1.jar -DgroupId=javax.comm -DartifactId=comm -Dversion=3.0-u1 -Dpackaging=jar
    

Tests
=====

There are some junits implemented. Somes are @ignored to speed up compilation. These:
* [ProgramUtilsTest.java](src/test/java/org/andresoviedo/util/program/ProgramUtilsTest.java)
* [MessengerTest.java](src/test/java/org/andresoviedo/util/messaging/api1/MessengerTest.java)
* [JGroupsClusterTest.java](src/test/java/org/andresoviedo/util/jgroups/JGroupsClusterTest.java)
* [DependantTaskExecutorTest.java](src/test/java/org/andresoviedo/util/tasks/DependantTaksExecutorTest.java)

