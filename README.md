java-utilities
==============

Series of common utility classes (sometimes mini-frameworks) very useful to use in any app.

Each utility was built along the time, and it was implemented because I didn't find any similar
on Internet, or maybe because it was not available at that time... whatever. I still keep adding new utilities.

I would be glad if you find any of the classes useful. Here are some:

* **MySQLPhoneCallAlert.java**: Make a select to MySQL and if it's not OK the class will make a phone call to your cell phone.
* **BeanUtils.java**: Print any POJO into a pretty printable String (recursive). Also set (or get) nested properties (bean1.prop1.val2)
 into a Bean. Maps, Lists, Enums, etc are supported.
* **CyclicBuffer.java**: LIFO queue wich stores the N most recent items.   
* **SortedHashList.java**: A merge of List,Maps & Comparator 
* **UnicodeInputStream.java**: Removes unicode chars from a stream
* **EasyFTPsClient.java**: Connect to FTPS servers
* **JarUtils.java**: Browser JAR contents
* **LazyRollingFileAppender.java**: Don't create log4j log files if nothing logged.
* **ModemManager.java**: Controls PC Modem
* **ZipHelper (MultipartZipHelper).java**: Download & unzip multipart zip files in parallel. 
* **WindowsUtils.java**: Create & delete start menu items in Windows XP & Windows 7
* **DependantTasksExecutor.java**: Execute multiple tasks in parallel even when any task depended on the result of another tasks(s).
* **StructuredStringSerializer.java**: Write/read any POJO to(from) a String with an annotated structured format
* **TaskScheduler & TasksControlPanel.java**: Schedule tasks & manage them with a Swing GUI control panel
* **RunHelper.java**: Run commands
* **IOHelper.java**: Copy files (zips) and filter it's contents
