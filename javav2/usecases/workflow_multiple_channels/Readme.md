#  Using AWS Step Functions and the AWS SDK for Java to build workflows that send notifications over multiple channels

## Overview

| Heading      | Description |
| ----------- | ----------- |
| Description | Discusses how to develop a workflow with AWS Step Functions to send notifications over multiple channels. Also discusses how to use the Amazon Relational Database Service (Amazon RDS) within an AWS Lambda function.     |
| Audience   |  Developer (beginner / intermediate)        |
| Updated   | 5/15/2022        |
| Required skills   | Java, Maven  |

## Purpose
You can use Amazon Web Services to create a workflow that sends notifications over multiple channels. There are many practical business needs for this type of functionality. For example, a weather agency might need to warn many people about a storm, or a school might want to send parents alerts when kids are missing. 

The use case for this AWS tutorial assumes that you work at a school and you need to alert parents when a student skips school. Do you send an email message, do you phone the parents, or do you send a text message to a mobile device? The AWS workflow created in this tutorial sends messages over multiple channels, including email, as shown in the following illustration. 

![AWS Tracking Application](images/message.png)

In this AWS tutorial, you create a serverless workflow by using the AWS SDK for Java (v2) and AWS Step Functions. Each workflow step is implemented by using an AWS Lambda function. Lambda is a compute service that you can use to run code without provisioning or managing servers. For more information about Lambda, see
[What is AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html).

To send notifications over multiple channels, you can use the following AWS services:

+ Amazon Pinpoint
+ Amazon Simple Notification Service (Amazon SNS)
+ Amazon Simple Email Service (Amazon SES)

#### Topics

+ Prerequisites
+ Understand the workflow
+ Create an AWS Identity and Access Management (IAM) role that is used to run Lambda functions
+ Create a workflow by using Step Functions
+ Create an IntelliJ project 
+ Add the POM dependencies to your project
+ Create Lambda functions by using the Lambda Java API
+ Package the project that contains Lambda functions
+ Deploy Lambda functions
+ Create the Amazon RDS database 
+ Add Lambda functions to workflows
+ Invoke the workflow from the AWS Management Console

## Prerequisites
To use this tutorial, you need the following:

+ An AWS account.
+ A Java IDE. (The IntelliJ IDE is used for this tutorial.)
+ Java 1.8 JDK.
+ Maven 3.6 or later.

### Important

+ The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to delete all of the resources that you create during this tutorial so that you won't be charged.

## Understand the workflow

The following figure shows the workflow you'll create with this tutorial, which is able to send out multiple messages over multiple channels. 

![AWS Tracking Application](images/workflowmodelA.png)

The following describes each step in the workflow:
+ **Start** - Initiates the workflow and passes in a date value.
+ **Determine the missing students** – Determines the students that are absent for the given day. For this AWS tutorial, a MySQL database is queried to track the students that are absent. This workflow step dynamically creates XML that contains the students queried from the database and passes the XML to the next step. This example shows how a Lambda function can query data from an Amazon RDS table.
+ **Send all notifications** – Parses the XML that contains all absent students. For each student, this step invokes Amazon SNS to send a mobile text message, Amazon Pinpoint to send a voice message, and Amazon SES to send an email message.  
+ **End** - Stops the workflow.

In this AWS tutorial, an Amazon RDS MySQL database is used to track the students who are absent. The MySQL table is named **students** and contains the following fields:

+ **idstudents** - An int value that represents the PK.
+ **date** - A date value that specifies the date when the student was absent.
+ **first** - A VARCHAR(45) value that specifies the student's first name.
+ **last** - A VARCHAR(45) value that specifies the student's last name.
+ **mobile** - A VARCHAR(45) value that specifies the mobile number.
+ **phone** - A VARCHAR(45) value that specifies the home phone number.
+ **email** - A VARCHAR(45) value that specifies the email address.

The workflow queries the **students** table to get all absent students, and dynamically creates XML that contains the absent students.  

```xml
       <?xml version="1.0" encoding="UTF-8"?>
       <Students>
         <Student>
          <Name>Sam</Name>
          <Mobile>15558397418</Mobile>
          <Phone>155538397418</Phone>
          <Email>scmacdon@noserver.com</Email>
         </Student>
         <Student>
          <Name>Laurie</Name>
          <Mobile>15554621058</Mobile>
          <Phone>155558397418</Phone>
          <Email>lmccue@cnoserver.com</Email>
         </Student>
       </Students>
```

The second workflow step parses the XML, and for each student, it invokes multiple AWS services to send messages over different channels.   

## Create an IAM role that's used to run Lambda functions

Create the following two IAM roles:

+ **lambda-support** - Used to invoke Lamdba functions.
+ **workflow-support** - Used to enable Step Functions to invoke the workflow.

This tutorial uses Amazon SNS, Amazon SES, and Amazon Pinpoint to send messages. The **lambda-support** role has to have policies that enable it to invoke these AWS services from a Lambda function.

#### To create an IAM role

1. Open the AWS Management Console. When the page loads, enter **IAM** in the search box, and then choose **IAM** to open the IAM console.

2. In the navigation pane, choose **Roles**, and on the **Roles** page, choose **Create Role**.

3. Choose **AWS service**, and then choose **Lambda**.

![AWS Tracking Application](images/Lambda1.png)

4. Choose **Permissions**.

5. Search for **AWSLambdaBasicExecutionRole**.

6. Choose **Next Tags**.

7. Choose **Review**.

8. Name the role **lambda-support**.

![AWS Tracking Application](images/LambdaName.png)

9. Choose **Create role**.

10. Choose **lambda-support** to view the overview page.

11. Choose **Attach Policies**.

12. Search for **AmazonSESFullAccess**, and then choose **Attach policy**.

13. Search for **AmazonSNSFullAccess**, and then choose **Attach policy**. When you're done, you can see the permissions.

![AWS Tracking Application](images/Policies2.png)

**Note**: Repeat this process to create **workflow-support**. For step three, instead of choosing **Lambda**, choose **Step Functions**. You don't need to perform steps 11-13.  

### Create a custom policy for Pinpoint voice

Because the Lambda function invokes the Pinpoint **sendVoiceMessage** API operation, the **lambda-support** role needs permission to invoke this operation. To perform this task, you need to create a custom policy using the following JSON.

    {
     "Version": "2012-10-17",
     "Statement": [
       {
            "Sid": "FullAccess",
            "Effect": "Allow",
            "Action": [
                "sms-voice:*"
            ],
            "Resource": "*"
        }
     ]
    } 

**Note**: To create a custom policy, see [Policies and permissions in IAM ](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies.html).

## Create a serverless workflow by using AWS Step Functions

To define a workflow that sends notifications over multiple channels by using AWS Step Functions, create an Amazon States Language (JSON-based) document to define your state machine. An Amazon States Language document describes each step. After you define the document, Step Functions provides a visual representation of the workflow. The following figure shows a visual representation of the workflow.

![AWS Tracking Application](images/workflowmodelA.png)

Workflows can pass data between steps. For example, the **Determine the missing students** step queries the **students** table, dynamically creates XML, and passes XML to the **Send All Notifications** step. 

**Note**: Later in this tutorial, you'll create application logic in the Lambda function to read data from the Amazon RDS table.  

#### To create a workflow

1. Open the Step Functions console at https://us-west-2.console.aws.amazon.com/states/home.

2. Choose **Create State Machine**.

3. Choose **Write your workflow in code**. In the **Type** area, select **Standard**.

![AWS Tracking Application](images/statemachine2.png)

4. Specify the Amazon States Language document by entering the following code.

        {
        "Comment": "A simple Step Functions state machine that sends mass notifications over multiple channels.",
        "StartAt": "Determine the missing students",
        "States": {
         "Determine the missing students": {
         "Type": "Task",
         "Resource": " arn:aws:lambda:REGION:ACCOUNT_ID:function:FUNCTION_NAME ",
         "Next": "Send All Notifications"
         },
        "Send All Notifications": {
         "Type": "Task",
         "Resource": " arn:aws:lambda:REGION:ACCOUNT_ID:function:FUNCTION_NAME ",
         "End": true
         }
        }
       }

**Note:** Don't worry about the errors related to the Lambda resource values. You update these values later in this tutorial.

5. Choose **Next**.

6. In the name field, enter **NotificationsStateMachine**.

7. In the **Permission** section, select **Choose an existing role**.  

8. Choose **workflow-support** (the IAM role that you created).

![AWS Tracking Application](images/workflowuser.png)

9. Choose **Create state machine**. A message appears that states the state machine was successfully created.

## Create an IntelliJ project named LambdaNotifications

Create a Java project to develop Lambda functions by using the Lambda Java runtime API. 

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.

2. In the **New Project** dialog box, choose **Maven**, and then choose **Next**.

3. For **GroupId**, enter **LambdaNotifications**.

4. For **ArtifactId**, enter **LambdaNotifications**.

5. Choose **Next**.

6. Choose **Finish**.

## Add the POM dependencies to your project

At this point, you have a new project named **LambdaNotifications**. Add the following code to your project's pom.xml file. 

```xml
        <?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>LambdaNotifications</groupId>
    <artifactId>LambdaNotifications</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>java-basic-function</name>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>software.amazon.awssdk</groupId>
                <artifactId>bom</artifactId>
                <version>2.17.136</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-core</artifactId>
            <version>1.2.1</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>pinpointsmsvoice</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>sns</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>pinpoint</artifactId>
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
            <groupId>org.jdom</groupId>
            <artifactId>jdom</artifactId>
            <version>2.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.36</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.41</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>ses</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.9.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>2.17.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.17.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j18-impl</artifactId>
            <version>2.17.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.8.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.6.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.googlecode.json-simple</groupId>
            <artifactId>json-simple</artifactId>
            <version>1.1.1</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.2</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Create Lambda functions by using the AWS SDK for Java

Use the Lambda runtime API to create the Java classes that define the Lamdba functions. In this example, there are two workflow steps that each correspond to a Java class. There are also extra classes that invoke the AWS services. The following figure shows the Java classes in the project. All Java classes are located in a package named **com.example.messages**.

![AWS Tracking Application](images/projectfiles2.png)

To create a Lambda function by using the Lambda runtime API, implement **com.amazonaws.services.lambda.runtime.RequestHandler**. The application logic that's executed when the workflow step is invoked is located in the **handleRequest** method. The return value of this method is passed to the next step in a workflow.

Create these Java classes, which are described in the following sections:
+ **ConnectionHelper** - Used to connect to the Amazon RDS instance.  
+ **ListMissingStudentsHandler** - Used as the first step in the workflow. This class queries data from the Amazon RDS instance. 
+ **HandlerVoiceNot** - Used as the second step in the workflow. Sends out messages over multiple channels.
+ **RDSGetStudents** - Queries data from the student table using the Java Database Connectivity (JDBC) API. 
+ **SendNotifications** - Uses the AWS SDK for Java (v2) to invoke the Amazon SNS, Amazon Pinpoint, and Amazon SES services.
+ **Student** - A Java class that defines data members to store student data. 

### ConnectionHelper class

The following Java code represents the **ConnectionHelper** class.

```java
     package com.example.messages;

     import java.sql.Connection;
     import java.sql.DriverManager;
     import java.sql.SQLException;

     public class ConnectionHelper {

      private static ConnectionHelper instance;
      private String url;
      private ConnectionHelper() {
        url = "jdbc:mysql://localhost:3306/mydb?useSSL=false";
      }

      public static Connection getConnection() throws SQLException {
        if (instance == null)
            instance = new ConnectionHelper();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(instance.url, "root", "root1234");
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.getStackTrace();
        }
        return null;
      }
    }
 ```

**Note**: The URL value is **localhost:3306**. This value is modified after the Amazon RDS instance is created. The Lambda function uses this URL to communicate with the database. You must also be sure to specify the user name and password for your Amazon RDS instance.

### ListMissingStudentsHandler class

This Java code represents the **ListMissingStudentsHandler** class. The class creates a Lamdba function that reads the passed in date value and queries the **student** table using this value.  The **handleRequest** method returns XML that specifies all of the absent students. The XML is passed to the second step in the workflow.

```java
     package com.example.messages;

     import com.amazonaws.services.lambda.runtime.Context;
     import com.amazonaws.services.lambda.runtime.RequestHandler;
     import com.amazonaws.services.lambda.runtime.LambdaLogger;
     import java.util.Map;
    
     public class ListMissingStudentsHandler implements RequestHandler<Map<String,String>, String> {

      @Override
      public String handleRequest(Map<String,String> event, Context context) {
        
	LambdaLogger logger = context.getLogger();
        String date = event.get("date");
        logger.log("DATE: " + date);

        // Query the student table and get back XML.
        RDSGetStudents students = new RDSGetStudents();
         String xml = null;
        try {
            xml = students.getStudentsRDS(date);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.log("XML: " + xml);
        return xml;
      }
     }
 ```

### HandlerVoiceNot class

The **HandlerVoiceNot** class is the second step in the workflow. It creates a **SendNotifications** object and passes the XML to the following methods: 

+ **handleTextMessage** 
+ **handleVoiceMessage**
+ **handleEmailMessage**

The following code represents the **HandlerVoiceNot** method. In this example, the XML that is passed to the Lambda function is stored in the **xml** variable. 

```java
     package com.example.messages;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.jdom2.JDOMException;
import javax.mail.MessagingException;
import java.io.IOException;

public class HandlerVoiceNot implements RequestHandler<String, String> {

    @Override
    public String handleRequest(String event, Context context) {

        LambdaLogger logger = context.getLogger();
        String xml = event;
        int num =0;
        SendNotifications sn = new SendNotifications();

        try {

           sn.handleTextMessage(xml);
           sn.handleVoiceMessage(xml);
           num = sn.handleEmailMessage(xml);
           logger.log("The workflow sent "+num +" email messages");
        } catch (JDOMException | IOException | MessagingException e) {
            e.printStackTrace();
        }
        return "The workflow sent "+num +" email messages";
    }
}

 ```

### RDSGetStudents class

The **RDSGetStudents** class uses the JDBC API to query data from the Amazon RDS instance. The result set is stored in XML which is passed to the second step in the worlkflow. 

```java
       package com.example.messages;

       import org.w3c.dom.Document;
       import org.w3c.dom.Element;
       import javax.xml.parsers.DocumentBuilder;
       import javax.xml.parsers.DocumentBuilderFactory;
       import javax.xml.parsers.ParserConfigurationException;
       import javax.xml.transform.Transformer;
       import javax.xml.transform.TransformerException;
       import javax.xml.transform.TransformerFactory;
       import javax.xml.transform.dom.DOMSource;
       import javax.xml.transform.stream.StreamResult;
       import java.io.StringWriter;
       import java.sql.PreparedStatement;
       import java.sql.Connection;
       import java.sql.SQLException;
       import java.sql.ResultSet;
       import java.util.ArrayList;
       import java.util.List;

       public class RDSGetStudents {

       public String getStudentsRDS(String date ) throws SQLException {

        Connection c = null;
        String query = "";

        try {

            c = ConnectionHelper.getConnection();
            ResultSet rs = null;

            // Use prepared statements.
            PreparedStatement pstmt = null;
            query = "Select first, phone, mobile, email FROM students where date = '" +date +"'";
            pstmt = c.prepareStatement(query);
            rs = pstmt.executeQuery();

            List<Student> studentList = new ArrayList<>();
            while (rs.next()) {

                Student student = new Student();

                String name = rs.getString(1);
                String phone = rs.getString(2);
                String mobile = rs.getString(3);
                String email = rs.getString(4);

                student.setFirstName(name);
                student.setMobileNumber(mobile);
                student.setPhoneNunber(phone);
                student.setEmail(email);

                // Push the Student object to the list.
                studentList.add(student);
            }

            return convertToString(toXml(studentList));

          } catch (SQLException e) {
            e.printStackTrace();
         } finally {
            c.close();
         }
         return null;
        }

       private String convertToString(Document xml) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch(TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
       }


       // Convert the list to XML.
       private Document toXml(List<Student> itemList) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement( "Students" );
            doc.appendChild( root );

            // Loop through the list.
            for (Student myStudent: itemList) {

                Element item = doc.createElement( "Student" );
                root.appendChild( item );

                // Set Name.
                Element name = doc.createElement( "Name" );
                name.appendChild( doc.createTextNode(myStudent.getFirstName()) );
                item.appendChild( name );

                // Set Mobile.
                Element mobile = doc.createElement( "Mobile" );
                mobile.appendChild( doc.createTextNode(myStudent.getMobileNumber()) );
                item.appendChild( mobile );

                // Set Phone.
                Element phone = doc.createElement( "Phone" );
                phone.appendChild( doc.createTextNode(myStudent.getPhoneNunber() ) );
                item.appendChild( phone );

                // Set Email.
                Element email = doc.createElement( "Email" );
                email.appendChild( doc.createTextNode(myStudent.getEmail() ) );
                item.appendChild( email );
            }

         return doc;
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
        }  
       }
```

### SendNotifications class

The **SendNotifications** class uses the Amazon SES API, the Amazon SNS API, and the Amazon Pinpoint API to send messages. Each student in the XML is sent a message. 

```java
       package com.example.messages;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpointsmsvoice.PinpointSmsVoiceClient;
import software.amazon.awssdk.services.pinpointsmsvoice.model.SSMLMessageType;
import software.amazon.awssdk.services.pinpointsmsvoice.model.VoiceMessageContent;
import software.amazon.awssdk.services.pinpointsmsvoice.model.SendVoiceMessageRequest;
import software.amazon.awssdk.services.pinpointsmsvoice.model.PinpointSmsVoiceException;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SesException;
import javax.mail.MessagingException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.StringReader;

public class SendNotifications {

    public int handleEmailMessage(String myDom) throws JDOMException, IOException, MessagingException {

        String myEmail = "";
        SesClient client = SesClient.builder()
                .region(Region.US_EAST_1)
                .build();

        SAXBuilder builder = new SAXBuilder();
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // Get the list of children elements.
        int countStudents = 0;
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {

            myEmail = element.getChildText("Email");
            sendEmail(client, myEmail);
            countStudents++;
        }
        client.close();
        return countStudents;
    }

    public String handleTextMessage(String myDom) throws JDOMException, IOException{

        String mobileNum = "";
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        SAXBuilder builder = new SAXBuilder();
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // Get the list of children agent elements.
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {

            mobileNum = element.getChildText("Mobile");
            publishTextSMS(snsClient, mobileNum);
        }
        snsClient.close();
        return mobileNum;
    }

    public String handleVoiceMessage(String myDom) throws JDOMException, IOException{

        String mobileNum = "";
        List<String> listVal = new ArrayList<>();
        listVal.add("application/json");

        Map<String, List<String>> values = new HashMap<>();
        values.put("Content-Type", listVal);

        ClientOverrideConfiguration config2 = ClientOverrideConfiguration.builder()
                .headers(values)
                .build();

        PinpointSmsVoiceClient client = PinpointSmsVoiceClient.builder()
                .overrideConfiguration(config2)
                .region(Region.US_EAST_1)
                .build();

        SAXBuilder builder = new SAXBuilder();
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // Get a list of children elements.
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {

            mobileNum = element.getChildText("Phone");
            sendVoiceMsg( client, mobileNum);
        }
        client.close();
        return mobileNum;
      }

    private void sendVoiceMsg(PinpointSmsVoiceClient client, String mobileNumber) {

        String languageCode = "en-US";
        String voiceName = "Matthew";

        String originationNumber = "<Enter valid number>";
        String ssmlMessage = "<speak>Please be advised that your student was marked absent from school today.</speak>";

        // Send a voice message from a Lambda function.
        try {
            SSMLMessageType ssmlMessageType = SSMLMessageType.builder()
                    .languageCode(languageCode)
                    .text(ssmlMessage)
                    .voiceId(voiceName)
                    .build();

            VoiceMessageContent content = VoiceMessageContent.builder()
                    .ssmlMessage(ssmlMessageType)
                    .build();

            SendVoiceMessageRequest voiceMessageRequest = SendVoiceMessageRequest.builder()
                    .destinationPhoneNumber(mobileNumber)
                    .originationPhoneNumber(originationNumber)
                    .content(content)
                    .build();

            client.sendVoiceMessage(voiceMessageRequest);

        } catch (PinpointSmsVoiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private void publishTextSMS(SnsClient snsClient, String phoneNumber) {

        String message = "Please be advised that your student was marked absent from school today.";

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            snsClient.publish(request);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public void sendEmail(SesClient client, String recipient) {

             // The HTML body of the email.
            String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                    + "<p>Please be advised that your student was marked absent from school today.</p>" + "</body>" + "</html>";

            String sender = "<Enter valid email address>";
            String subject = "School Attendance";

            Destination destination = Destination.builder()
                    .toAddresses(recipient)
                    .build();

            Content content = Content.builder()
                    .data(bodyHTML)
                    .build();

            Content sub = Content.builder()
                    .data(subject)
                    .build();

            Body body = Body.builder()
                    .html(content)
                    .build();

            software.amazon.awssdk.services.ses.model.Message msg = software.amazon.awssdk.services.ses.model.Message.builder()
                    .subject(sub)
                    .body(body)
                    .build();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(destination)
                    .message(msg)
                    .source(sender)
                    .build();

            try {
                System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
                client.sendEmail(emailRequest);

            } catch (SesException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
        }
    }


```

**NOTE** You need to specify a valid email for the sender that has been validated. For information, see [Verifying an email address](https://docs.aws.amazon.com/ses/latest/DeveloperGuide//verify-email-addresses-procedure.html). In addition, you need to assign the **originationNumber** variable a valid origination number that's associated with your AWS account. 

### Student class

The following Java class represents the **Student** class. 

```java
     package com.example.messages;

     public class Student {

      private String firstName;
      private String email;
      private String mobileNumber ;
      private String phoneNunber;

      public void setPhoneNunber(String phoneNunber) {
        this.phoneNunber = phoneNunber;
      }

      public String getPhoneNunber() {
        return this.phoneNunber;
      }

      public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
      }

      public String getMobileNumber() {
        return this.mobileNumber;
      }

      public void setEmail(String email) {
        this.email = email;
      }

      public String getEmail() {
        return this.email;
      }

      public void setFirstName(String firstName) {
        this.firstName = firstName;
      }

      public String getFirstName() {
        return this.firstName;
      }
     }
```

## Set up the Amazon RDS instance

In this step, you create an Amazon RDS MySQL instance that is used by the Lambda function.

1. Sign in to the AWS Management Console and open the Amazon RDS console at https://console.aws.amazon.com/rds/.

2. In the upper-right corner of the AWS Management Console, choose the AWS Region in which you want to create the DB instance. This example uses the US West (Oregon) Region.

3. In the navigation pane, choose **Databases**.

4. Choose **Create database**.

5. On the **Create database** page, make sure that the **Standard Create** option is chosen, and then select **MySQL**.

![AWS Tracking Application](images/RDS.png)

6. In the **Templates** section, select **Free tier**.

![AWS Tracking Application](images/TemplateRDS.png)

7. In the **Settings** section, set the following values:

+ **DB instance identifier** – awstracker
+ **Master username** – root
+ **Auto generate a password** – Turn off this option.
+ **Master password** – root1234
+ **Confirm password** – root1234
	
![AWS Tracking Application](images/RDSSettings.png)

8. In the **DB instance size** section, set the following values:

+ **DB instance performance type** – Burstable
+ **DB instance class** – db.t2.micro

9. In the **Storage** section, use the default values.

10. In the **Connectivity** section, open **Additional connectivity configuration** and set the following values:

+ **Virtual Private Cloud (VPC)** – Choose the default.
+ **Subnet group** – Choose the default.
+ **Publicly accessible** – Yes
+ **VPC security groups** – Choose an existing VPC security group that is configured for access.
+ **Availability Zone** – No Preference
+ **Database port** – 3306

11. Open the **Additional configuration** section, and enter **awstracker** for the initial database name. Keep the default settings for the other options.

12. To create your Amazon RDS MySQL DB instance, choose **Create database**. Your new DB instance appears in the **Databases** list with the status **Creating**.

13. Wait for the status of your new DB instance to show as **Available**. Then, choose the DB instance name to show its details.

**Note**: You must set up inbound rules for the security group to connect to the database. You can set up an inbound rule for your development environment. Setting up an inbound rule essentially means enabling an IP address to use the database. After you set up the inbound rules, you can connect to the database from a client such as MySQL Workbench. For information about setting up security group inbound rules, see [Controlling Access with Security Groups](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Overview.RDSSecurityGroups.html).

### Get the endpoint

In the **Connectivity & security** section, view the endpoint and port of the DB instance.

![AWS Tracking Application](images/RDSEndpoint.png)

### Modify the ConnectionHelper class

Modify the **ConnectionHelper** class by updating the url value with the endpoint of the database.

     url = "jdbc:mysql://awstracker.<url to rds>.amazonaws.com/awstracker";

In the previous line of code, **awstracker** is the database schema. You must also update this line of code with the correct user name and password.

      Class.forName("com.mysql.jdbc.Driver").newInstance();
         return DriverManager.getConnection(instance.url, "root","root1234");

**Note**: If you do not modify the **ConnectionHelper** class, your Lambda function cannot interact with the Amazon RDS database.

### Create the database schema and table

You can use [MySQL Workbench](https://www.mysql.com/products/workbench/) to connect to the Amazon RDS MySQL instance and create a database schema and the **student** table. To connect to the database, open MySQL Workbench and connect to database.

![AWS Tracking Application](images/MySQL.png)

**Note**: If you have issues connecting to the database, be sure to recheck your inbound rules.

Create a schema named **awstracker** by using the following SQL command.

     CREATE SCHEMA awstracker;
     
In the **awstracker** schema, create a table named **student** by using the following SQL command:     

     CREATE TABLE `mydb`.`student` (
      `idstudent` INT NOT NULL AUTO_INCREMENT,
       `first` VARCHAR(45) NULL,
       `last` VARCHAR(45) NULL,
       `date` DATE NULL,
       `mobile` VARCHAR(45) NULL,
       `phone` VARCHAR(45) NULL,
       `email` VARCHAR(45) NULL,
       PRIMARY KEY (`idstudent`));
       
After you're done, you see a new table in your database.

![AWS Tracking Application](images/database.png)

Using MySQL Workbench, enter some records that you will use to test your Lambda functions. Enter the date value for some records as 2021-02-01. 

## Package the project that contains the Lambda functions

Package up the project into a .jar (JAR) file that you can deploy as a Lambda function by using the following Maven command.

    mvn package

The JAR file is located in the **target** folder (which is a child folder of the project folder).

![AWS Tracking Application](images/LambdaJAR.png)

## Deploy the Lambda functions

1. Open the Lambda console at https://us-west-2.console.aws.amazon.com/lambda/home.

2. Choose **Create Function**.

3. Choose **Author from scratch**.

4. In the **Basic** information section, enter **School** as the name.

5. In the **Runtime**, choose **Java 8**.

6. Choose **Use an existing role**, and then choose **lambda-support** (the IAM role that you created).

7. Choose **Create function**.

8. For **Code entry type**, choose **Upload a .zip or .jar file**.

9. Choose **Upload**, and then browse to the JAR file that you created.  

10. For **Handler**, enter the fully qualified name of the function, for example, **com.example.messages.ListMissingStudentsHandler::handleRequest** (**com.example.messages.Handler** specifies the package and class followed by :: and method name).

![AWS Tracking Application](images/Settings2.png)

11. Choose **Save.**

12. Test your Lambda function. Pass in the JSON data that contains the date to query ("date": "2021-02-01"). When successful, you see XML that contains the data queried from the database.  

![AWS Tracking Application](images/testfunction3.png)

**Note** Repeat this procedure for the **HandlerVoiceNot** class. Name the corresponding Lambda functions **HandlerVoice**. When you finish, you will have two Lambda functions that you can reference in the Amazon States Language document.  

**IMPORTANT**: To connect to the Amazon RDS instance from a Lambda function, you must set the inbound rules using the same security group as the Amazon RDS instance. For details, see [How do I configure a Lambda function to connect to an RDS instance](https://aws.amazon.com/premiumsupport/knowledge-center/connect-lambda-to-an-rds-instance/). 

## Add the Lambda functions to workflows

Open the Lambda console. You can view the Lambda Amazon Resource Name (ARN) value in the upper-right corner.

![AWS Tracking Application](images/LambdaEndpoint.png)

Copy the Lambda ARN value. Then, open the Step Functions console. In the **Determine the missing students** step of the Amazon States Language document, paste the Lambda ARN value. Also, update the Lambda ARN value for the **Send All Notifications** step. This is how you hook in Lambda functions created by using the SDK for Java into a workflow created by using Step Functions.

![AWS Tracking Application](images/LambdaResources.png)

## Execute your workflow by using the Step Functions console

You can invoke the workflow on the Step Functions console. An execution receives JSON input. For this example, you can pass the following JSON data to the workflow.  

     {
     "date": "2021-02-01"
     }

**Note**: Change the date value to match the date values in the **students** table. Otherwise, you will receive an empty result set. 

#### To execute your workflow

1. On the Step Functions console, choose **Start execution**.

2. In the **Input** section, pass the JSON data. As each step is completed, it turns green.

![AWS Tracking Application](images/workflowGreen.png)


If the step turns red, an error occurred. You can choose the step and view the logs that are accessible from the right side.

![AWS Tracking Application](images/workflowLogs2.png)


### Next steps
Congratulations, you have created an AWS serverless workflow that sends messages over multiple channels. As stated at the beginning of this tutorial, be sure to delete all of the resources you created during this tutorial so that you won't be charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).


