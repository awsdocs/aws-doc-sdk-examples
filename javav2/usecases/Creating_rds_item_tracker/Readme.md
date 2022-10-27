# Building an Amazon RDS for MySQL Web Application with Spring Boot

## Overview

| Heading      | Description |
| ----------- | ----------- |
| Description | Discusses how to develop a Spring REST API that queries Amazon Relational Database Service (Amazon RDS) for MySQL data. The Spring REST API uses the JDBC API and the AWS SDK for Java (v2) to invoke AWS services and is used by a React application that displays the data. The React application uses Cloudscape. For information, see [Cloudscape](https://cloudscape.design/).  |
| Audience   |  Developer (intermediate)        |
| Updated   | 10/27/2022        |
| Required skills   | Java, Maven, JavaScript  |

## Purpose

You can develop a dynamic web application that tracks and reports on work items by using the following AWS services:

+ Amazon RDS for MySQL
+ Amazon Simple Email Service (Amazon SES). (The SDK for Java (v2) is used to access Amazon SES.)

The application you create is a decoupled React application that uses a Spring REST API to return Amazon RDS for MySQL data. That is, the React application is a single-page application (SPA) that interacts with a Spring REST API by making RESTful GET and POST requests. The Spring REST API uses the Java JDBC API to perform CRUD operations on the Amazon RDS for MySQL database. Then the Spring REST API returns JSON data in an HTTP response, as shown in the following illustration. 

![AWS Tracking Application](images/overview.png)

#### Topics

+ Prerequisites
+ Understand the AWS Tracker application
+ Create an IntelliJ project named ItemTrackerRDS
+ Add the Spring POM dependencies to your project
+ Create the Java classes
+ Create the React front end

## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account.
+ A Java IDE to build the Spring REST API. This tutorial uses the IntelliJ IDE.
+ Java JDK 11.
+ Maven 3.6 or later.
+ Set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 

### Important

+ The AWS services in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to delete all of the resources that you create during this tutorial so that you won't be charged.

### Creating the resources

Create an Amazon RDS for MySQL DB instance that has a table named **Work** and a primary key named **idwork** that contains the following fields:

+ **idwork** - A VARCHAR(45) value that represents the PK.
+ **date** - A date value that specifies the date the item was created.
+ **description** - A VARCHAR(400) value that describes the item.
+ **guide** - A VARCHAR(45) value that represents the deliverable being worked on.
+ **status** - A VARCHAR(400) value that describes the status.
+ **username** - A VARCHAR(45) value that represents the user who entered the item.
+ **archive** - A TINYINT(4) value that represents whether this is an active or archive item.

The following figure shows the **Work** table in the MySQL Workbench.

![AWS Tracking Application](images/MySqlTable.png)

For information about creating an Amazon RDS database, see [Creating a MySQL DB instance and connecting to a database on a MySQL DB instance](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_GettingStarted.CreatingConnecting.MySQL.html). 

After you create the database, in the **Connectivity & security** section, view the **Endpoint** and **Port** of the DB instance. You need these values when you create a connection to the database using the Java JDBC API (this is shown later in this tutorial). 

![AWS Tracking Application](images/trackEndpoint2.png)
      
**Note:** You must set up an inbound rule for the security group to connect to the database from your development environment. Setting up an inbound rule essentially means enabling an IP address to use the database. After you set up an inbound rule, you can connect to the database from a client such as MySQL Workbench. For more information, see [Controlling Access with Security Groups](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Overview.RDSSecurityGroups.html).  

## Understand the AWS Tracker React application 

A user can perform the following tasks using the React application:

+ View all active items.
+ View archived items that are complete.
+ Add a new item. 
+ Convert an active item into an archived item.
+ Send a report to an email recipient.

The React application displays *active* and *archive* items. For example, the following illustration shows the React application displaying active data.

![AWS Tracking Application](images/elapp1.png)

Likewise, the following illustration shows the React application displaying archived data.

![AWS Tracking Application](images/elappArc2.png)

**Note**: Notice that the **Archived** button is disabled. 

The React application lets a user convert an active item to an archived item by clicking the **Archive** button. 

![AWS Tracking Application](images/elappArcAll.png)

The React application also lets a user enter a new item. 

![AWS Tracking Application](images/react3.png)

The user can enter an email recipient into the **Email Report** text field and choose **Send report**.

![AWS Tracking Application](images/elapp4.png)

Active items are queried from the database and used to dynamically create an Excel document. Then, the application uses Amazon SES to email the document to the selected email recipient. The following image shows an example of a report.

![AWS Tracking Application](images/report.png)

## Create an IntelliJ project named ItemTrackerMySQLRest

1. In the IntelliJ IDE, choose **File**, **New**, **Project**. 
2. In the **Project SDK**, choose **11**. 
3. In the **New Project** dialog box, choose **Maven**, and then choose **Next**.
4. For **GroupId**, enter **aws-spring**.
5. For **ArtifactId**, enter **ItemTrackerMySQLRest**.
6. Choose **Next**.
7. Choose **Finish**.

## Add the POM dependencies to your project

At this point, you have a new project named **ItemTrackerRDSRest**.

**Note:** Make sure to use Java 11 (as shown in the following example).

Confirm that the **pom.xml** file looks like the following example.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.example</groupId>
    <artifactId>ItemTrackerMySQLRest</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.4.RELEASE</version>
        <relativePath /> <!-- lookup parent from repository -->
    </parent>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>11</java.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>software.amazon.awssdk</groupId>
                <artifactId>bom</artifactId>
                <version>2.17.146</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.8.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.8.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-commons</artifactId>
            <version>1.8.2</version>
        </dependency>
        <dependency>
            <groupId>org.junit.platform</groupId>
            <artifactId>junit-platform-launcher</artifactId>
            <version>1.8.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>ses</artifactId>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.22.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>rdsdata</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>protocol-core</artifactId>
        </dependency>
        <dependency>
            <groupId>jakarta.mail</groupId>
            <artifactId>jakarta.mail-api</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>com.sun.mail</groupId>
            <artifactId>jakarta.mail</artifactId>
            <version>1.6.5</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>net.sourceforge.jexcelapi</groupId>
            <artifactId>jxl</artifactId>
            <version>2.6.10</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## Create the Java classes

Create a Java package in the **main/java** folder named **com.aws.rest**. The following Java files go into this package:

+ **App** - The entry point into the Spring boot application.  
+ **MainController** - Represents the Spring Controller that handles HTTP requests to handle data operations.
+ **ReportController** - Represents a second Spring Controller that handles HTTP requests that generates a report.
+ **ConnectionHelper** - Establishes a connection to the Amazon RDS for MySQL database.
+ **DatabaseService** - A Spring class that extends **CrudRepository** and uses the AWS SDK for Java (v2) that performs database operations. 
+ **WorkItem** - Represents the application's data model.
+ **WriteExcel** - Uses the Java Excel API to dynamically create a report. (This does not use AWS SDK for Java API operations).

### App class 

The following Java code represents the **App** class. This is the entry point into a Spring boot application. 

```java
package com.aws.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.regions.Region;

@SpringBootApplication
public class App {
    public static final Region region = Region.US_EAST_1;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```    

### MainController class

The following Java code represents the **MainController** class, which handles HTTP requests for the application. Notice the use of the **CrossOrigin** annotation. This annotation lets the controller accept requests from different domains. 

```java
package com.aws.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ComponentScan(basePackages = {"com.aws.rest"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/items")
public class MainController {
    private final DatabaseService dbService;

    @Autowired
    MainController(
        DatabaseService dbService
     ) {
        this.dbService = dbService;
     }

    @GetMapping("" )
    public List<WorkItem> getItems(@RequestParam(required=false) String archived) {
        Iterable<WorkItem> result;
        if (archived != null && archived.compareTo("false")==0)
            result = dbService.getItemsDataSQLReport(0);
        else if (archived != null && archived.compareTo("true")==0)
            result = dbService.getItemsDataSQLReport(1);
        else
            result = dbService.getItemsDataSQLReport(-1);

        return StreamSupport.stream(result.spliterator(), false)
            .collect(Collectors.toUnmodifiableList());
    }

    // Notice the : character which is used for custom methods. More information can be found here:
    // https://cloud.google.com/apis/design/custom_methods
    @PutMapping("{id}:archive")
    public String modUser(@PathVariable String id) {
        dbService.flipItemArchive(id);
        return id +" was archived";
    }

    @PostMapping("")
    public List<WorkItem> addItem(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String guide = payload.get("guide");
        String description = payload.get("description");
        String status = payload.get("status");

        WorkItem item = new WorkItem();
        String workId = UUID.randomUUID().toString();
        String date = LocalDateTime.now().toString();
        item.setId(workId);
        item.setGuide(guide);
        item.setDescription(description);
        item.setName(name);
        item.setDate(date);
        item.setStatus(status);
        dbService.injestNewSubmission(item);
        Iterable<WorkItem> result= dbService.getItemsDataSQLReport(0);
        return StreamSupport.stream(result.spliterator(), false)
            .collect(Collectors.toUnmodifiableList());
    }
}
```

### ReportController class

The following Java code represents the **ReportController** class. 

```java
package com.aws.rest;

import jxl.write.WriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/items:report")
public class ReportController {

    private final DatabaseService dbService;
    private final WriteExcel writeExcel;
    private final WriteExcel.SendMessages sm;

    @Autowired()
    ReportController(
        DatabaseService dbService,
        WriteExcel writeExcel,
        WriteExcel.SendMessages sm
    ) {
        this.dbService = dbService;
        this.writeExcel = writeExcel;
        this.sm = sm;
    }

    @PostMapping("")
    public String sendReport(@RequestBody Map<String, String> body) {
        var list = dbService.getItemsDataSQLReport(0);
        try {
            InputStream is = writeExcel.write(list);
            sm.sendReport(is, body.get("email"));
            return "Report generated & sent";
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        }
        return "Failed to generate report";
    }
}
```
### ConnectionHelper class 

The following class connects to the Amazon RDS for MySQL database. Enter your endpoint, the user name, and the password. Otherwise, your code won't work. Replace the URL endpoint value with the endpoint value you obtained while setting up the Amazon RDS database.

```java

package com.aws.rest;

    import java.sql.Connection;
    import java.sql.DriverManager;
    import java.sql.SQLException;

    public class ConnectionHelper {

      private String url;
      private static ConnectionHelper instance;

      private ConnectionHelper() {
        url = "jdbc:mysql://<Enter DataBase URL>:3306/mydb?useSSL=false";
      }

      public static Connection getConnection() throws SQLException {
        if (instance == null) {
            instance = new ConnectionHelper();
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(instance.url, "root","root1234");

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.getStackTrace();
        }
        return null;
    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

```

### DatabaseService class

The following Java code represents the **DatabaseService** class. This class uses the JDBC API to perform CRUD operations in the Amazon RDS MySQL database. Notice the use of [Interface PreparedStatement](https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html) when using SQL statements. For example, in the **getItemsDataSQLReport** method, you use this object to query data from the **work** table.

```java
package com.aws.rest;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class DatabaseService {

    // Set the specified item to archive.
    public void flipItemArchive(String id) {
        Connection c = null;
        String query;
        try {
            c = ConnectionHelper.getConnection();
            query = "update work set archive = ? where idwork ='" +id + "' ";
            PreparedStatement updateForm = c.prepareStatement(query);
            updateForm.setBoolean(1, true);
            updateForm.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
    }

    // Get Items data from MySQL.
    public List<WorkItem> getItemsDataSQLReport(int flag) {
        Connection c = null;
        List<WorkItem> itemList = new ArrayList<>();
        String query;
        String username = "user";
        WorkItem item;

        try {
            c = ConnectionHelper.getConnection();
            ResultSet rs = null;
            PreparedStatement pstmt = null;
            if (flag == 0) {
                // Retrieves active data from the MySQL database
                int arch = 0;
                query = "Select idwork,username,date,description,guide,status,archive FROM work where username=? and archive=?;";
                pstmt = c.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setInt(2, arch);
                rs = pstmt.executeQuery();
            }else if (flag == 1)  {
                // Retrieves archive data from the MySQL database
                int arch = 1;
                query = "Select idwork,username,date,description,guide,status, archive  FROM work where username=? and archive=?;";
                pstmt = c.prepareStatement(query);
                pstmt.setString(1, username);
                pstmt.setInt(2, arch);
                rs = pstmt.executeQuery();
            } else {
                // Retrieves all data from the MySQL database
                query = "Select idwork,username,date,description,guide,status, archive FROM work";
                pstmt = c.prepareStatement(query);
                rs = pstmt.executeQuery();
            }

            while (rs.next()) {
                item = new WorkItem();
                item.setId(rs.getString(1));
                item.setName(rs.getString(2));
                item.setDate(rs.getDate(3).toString().trim());
                item.setDescription(rs.getString(4));
                item.setGuide(rs.getString(5));
                item.setStatus(rs.getString(6));
                item.setArchived(rs.getBoolean(7));

                // Push the WorkItem Object to the list.
                itemList.add(item);
            }
            return itemList;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
        return null;
    }

    // Inject a new submission.
    public void injestNewSubmission(WorkItem item) {
        Connection c = null;
        try {
            c = ConnectionHelper.getConnection();
            PreparedStatement ps;

            // Convert rev to int.
            String name = item.getName();
            String guide = item.getGuide();
            String description = item.getDescription();
            String status = item.getStatus();

            // Generate the work item ID.
            UUID uuid = UUID.randomUUID();
            String workId = uuid.toString();

            // Date conversion.
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date( date1.getTime());

            // Inject an item into the system.
            String insert = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES(?,?, ?,?,?,?,?);";
            ps = c.prepareStatement(insert);
            ps.setString(1, workId);
            ps.setString(2, name);
            ps.setDate(3, sqlDate);
            ps.setString(4, description);
            ps.setString(5, guide );
            ps.setString(6, status );
            ps.setBoolean(7, false);
            ps.execute();

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
    }
}


```


### WorkItem class

The following Java code represents the **WorkItem** class.   

```java

package com.aws.rest;

public class WorkItem {

    private String id;
    private String name;
    private String guide ;
    private String date;
    private String description;
    private String status;
    private boolean archived ;

    public void setId (String id) {
        this.id = id;
    }

    public boolean getArchived() {
        return this.archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getId() {
        return this.id;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDate (String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setGuide (String guide) {
        this.guide = guide;
    }

    public String getGuide() {
        return this.guide;
    }
}
```
### WriteExcel class

The **WriteExcel** class dynamically creates an Excel report with the data marked as active. In addition, notice the use of the **SendMessage** class that uses the Amazon SES Java API to send email messages. The following code represents this class.

```java
 package com.aws.rest;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Properties;

@Component
public class WriteExcel {
    static WritableCellFormat times ;
    static WritableCellFormat timesBoldUnderline;

    static {
        try {
            WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
            times = new WritableCellFormat(times10pt);
            times.setWrap(true);

            WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.SINGLE);
            timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
            timesBoldUnderline.setWrap(true);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    public InputStream write(Iterable<WorkItem> items) throws IOException, WriteException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "US"));

        WritableWorkbook workbook = Workbook.createWorkbook(os, wbSettings);
        workbook.createSheet("Work Item Report", 0);
        WritableSheet excelSheet = workbook.getSheet(0);

        addLabels(excelSheet);
        fillContent(excelSheet, items);

        workbook.write();
        workbook.close();

        return new ByteArrayInputStream(os.toByteArray());
    }

    private void addLabels(WritableSheet sheet) throws WriteException {
        CellView cv = new CellView();
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        addCaption(sheet, 0, 0, "Writer");
        addCaption(sheet, 1, 0, "Date");
        addCaption(sheet, 2, 0, "Guide");
        addCaption(sheet, 3, 0, "Description");
        addCaption(sheet, 4, 0, "Status");
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label = new Label(column, row, s, timesBoldUnderline);
        int cc = s.length();
        sheet.setColumnView(column, cc);
        sheet.addCell(label);
    }

    private void addField(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label = new Label(column, row, s, timesBoldUnderline);
        int cc = s.length();
        cc = cc > 200 ? 150 : cc + 6;
        sheet.setColumnView(column, cc);
        sheet.addCell(label);
    }

    private void fillContent(WritableSheet sheet, Iterable<WorkItem> items) throws WriteException {
        int row = 2;
        for (WorkItem item : items) {
            addField(sheet, 0, row, item.getName());
            addField(sheet, 1, row, item.getDate());
            addField(sheet, 2, row, item.getGuide());
            addField(sheet, 3, row, item.getDescription());
            addField(sheet, 4, row, item.getStatus());
            row += 1;
        }
    }

    @Component
    public static class SendMessages {
        private static String sender = "<Enter value>";
        private static String subject = "Weekly AWS Status Report";
        private static String bodyText = "Hello,\r\n\r\nPlease see the attached file for a weekly update.";
        private static String bodyHTML = "<!DOCTYPE html><html lang=\"en-US\"><body><h1>Hello!</h1><p>Please see the attached file for a weekly update.</p></body></html>";
        private static String attachmentName = "WorkReport.xls";

        public void sendReport(InputStream is, String emailAddress) throws IOException {
            byte[] fileContent = IOUtils.toByteArray(is);

            try {
                send(makeEmail(fileContent, emailAddress));
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        public void send(MimeMessage message) throws MessagingException, IOException {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());
            byte[] arr = new byte[buf.remaining()];
            buf.get(arr);
            SdkBytes data = SdkBytes.fromByteArray(arr);
            RawMessage rawMessage = RawMessage.builder().data(data).build();
            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder().rawMessage(rawMessage).build();

            try {
                System.out.println("Attempting to send an email through Amazon SES...");
                SesClient client = SesClient.builder().region(Region.US_WEST_2).build();
                client.sendRawEmail(rawEmailRequest);
            } catch (SesException e) {
                e.printStackTrace();
            }
        }

        private MimeMessage makeEmail(byte[] attachment, String emailAddress) throws MessagingException {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);

            message.setSubject(subject, "UTF-8");
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(bodyText, "text/plain; charset=UTF-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

            MimeMultipart msgBody = new MimeMultipart("alternative");
            msgBody.addBodyPart(textPart);
            msgBody.addBodyPart(htmlPart);

            MimeBodyPart wrap = new MimeBodyPart();
            wrap.setContent(msgBody);

            MimeMultipart msg = new MimeMultipart("mixed");
            msg.addBodyPart(wrap);

            MimeBodyPart att = new MimeBodyPart();
            DataSource fds = new ByteArrayDataSource(attachment, "application/vnc.openxmlformats-officedocument.spreadsheetml.sheet");
            att.setDataHandler(new DataHandler(fds));
            att.setFileName(attachmentName);

            msg.addBodyPart(att);
            message.setContent(msg);
            return message;
        }
    }
}
```
**Note:** Notice that the **SendMessages** is part of this Java file. You must update the email **sender** address with a verified email address. Otherwise, the email is not sent. For more information, see [Verifying email addresses in Amazon SES](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/verify-email-addresses.html).       

## Run the application 

Using the IntelliJ IDE, you can run your Spring REST API. The first time you run it, choose the run icon in the main class. The Spring API supports the following URLs. 

- /api/items - A GET request that returns all data items from the **Work** table. 
- /api/items?archived=true - A GET request that returns either active or archive data items from the **Work** table. 
- /api/items/{id}:archive - A PUT request that converts the specified data item to an archived item. 
- /api/items - A POST request that adds a new item to the database. 
- api/items:report - A POST request that creates a report of active items and emails the report. 

**Note**: The React application created in the next section consumes all of these URLs. 

Confirm that the Spring REST API works by viewing the Active items. Enter the following URL into a browser. 

http://localhost:8080/api/items

The following illustration shows the JSON data returned from the Spring REST API. 

![AWS Tracking Application](images/json2.png)

## Create the React front end

You can create the React application that consumes the JSON data returned from the Spring REST API. To create the React application, you can download files from the following GitHub repository. Included in this repository are instructions on how to set up the project. Click the following link to access the GitHub location [Work item tracker web client](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/clients/react/elwing).  

### Update BASE_URL

You must ensure that the **BASE_URL** is correct. In the **config.json** file, ensure this value references your Spring application.

```javascript
{
  "BASE_URL": "http://localhost:8080/api"
}
```
  
### Next steps
Congratulations, you have created a decoupled React application that consumes data from a Spring REST API. The Spring REST API uses the AWS SDK for Java (v2) to invoke AWS services. As stated at the beginning of this tutorial, be sure to delete all of the resources that you create during this tutorial so that you won't continue to be charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).

