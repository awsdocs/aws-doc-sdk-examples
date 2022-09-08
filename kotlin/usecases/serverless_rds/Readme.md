# Create a React and Spring REST application that queries Amazon Aurora Serverless data

## Overview

| Heading      | Description |
| ----------- | ----------- |
| Description | Discusses how to develop a Spring REST API that queries Amazon Aurora Serverless data. The Spring REST API uses the AWS SDK for Kotlin to invoke AWS services and is used by a React application that displays the data.   |
| Audience   |  Developer (intermediate)        |
| Updated   | 9/7/2022        |
| Required skills   | Java, Gradle, JavaScript  |

## Purpose

You can develop a dynamic web application that tracks and reports on work items by using the following AWS services:

+ Amazon Aurora Serverless database.
+ Amazon Simple Email Service (Amazon SES). 

The application you create is a decoupled React application that uses a Spring REST API to return Amazon Aurora Serverless data. That is, the React application is a single-page application (SPA) that interacts with a Spring REST API by making RESTful GET and POST requests. The Spring REST API uses an **RdsDataClient** object to perform CRUD operations on the Aurora Serverless database. Then, the Spring REST API returns JSON data in an HTTP response, as shown in the following illustration. 

![AWS Tracking Application](images/overview.png)

**Note:** You can only use the **RdsDataClient** object for an Aurora Serverless DB cluster or Aurora PostgreSQL. For more information, see [Using the Data API for Aurora Serverless](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/data-api.html).  

#### Topics

+ Prerequisites
+ Understand the AWS Tracker application
+ Create an IntelliJ project
+ Add the dependencies to your project
+ Create the Kotlin classes
+ Create the React front end

## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Kotlin IDE (this tutorial uses the IntelliJ IDE)
+ Java 1.8 JDK
+ Gradle 6.8 or higher

**Note**: Make sure that you have installed the Kotlin plug-in for IntelliJ. 

### Important

+ The AWS services in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+ This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to delete all of the resources that you create during this tutorial so that you won't be charged.
+ Be sure to set up your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html).

### Create the resources

You can use one of the following ways to create the required AWS resources:

- Use the AWS Management Console
- Use the WS Cloud Development Kit (AWS CDK)

#### Use the AWS Management Console

Create an Aurora Serverless database named **jobs**. Next, create a table named **Work** that contains the following fields:

+ **idwork** - A VARCHAR(45) value that represents the PK.
+ **date** - A date value that specifies the date the item was created.
+ **description** - A VARCHAR(400) value that describes the item.
+ **guide** - A VARCHAR(45) value that represents the deliverable being worked on.
+ **status** - A VARCHAR(400) value that describes the status.
+ **username** - A VARCHAR(45) value that represents the user who entered the item.
+ **archive** - A TINYINT(4) value that represents whether this is an active or archive item.

The following figure shows the **Work** table in the Amazon Relational Database Service (Amazon RDS) console.

![AWS Tracking Application](images/database.png)

For more information, see [Creating an Aurora Serverless v1 DB cluster](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/aurora-serverless.create.html).

To successfully connect to the database by using the **RdsDataClient** object, set up an AWS Secrets Manager secret to use for authentication. For more information, see [Rotate Amazon RDS database credentials automatically with AWS Secrets Manager](https://aws.amazon.com/blogs/security/rotate-amazon-rds-database-credentials-automatically-with-aws-secrets-manager/). 

To use the **RdsDataClient** object, you must have the following two Amazon Resource Name (ARN) values: 

+ The ARN of an Aurora Serverless database.
+ The ARN of a Secrets Manager secret to use for database access.

**Note:** You must set up inbound rules for the security group to connect to the database. You can set up an inbound rule for your development environment. Setting up an inbound rule essentially means enabling an IP address to use the database. After you set up the inbound rules, you can connect to the database from the REST endpoint. For information about setting up security group inbound rules, see [Controlling access with security groups](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Overview.RDSSecurityGroups.html).  

#### Use the AWS CDK

You can set up the resources required for this tutorial by using the AWS CDK. For more information, see [CDK instructions](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/cdk/aurora_serverless_app/README.md).

## Understand the AWS Tracker React application 

A user can perform the following tasks by using the React application:

+ View all active items.
+ View archived items that are complete.
+ Add a new item. 
+ Convert an active item into an archived item.
+ Send a report to an email recipient.

The React SPA displays *active* and *archive* items. For example, the following illustration shows the React application displaying active data.

![AWS Tracking Application](images/Active2.png)

Likewise, the following illustration shows the React application displaying archived data.

![AWS Tracking Application](images/archive3.png)

The React SPA also lets a user enter a new item. 

![AWS Tracking Application](images/newitem.png)

The user can enter an email recipient into the **Manager** text field and choose **Send Report**.

![AWS Tracking Application](images/report2.png)

Active items are queried from the database and used to dynamically create a report.

## Create an IntelliJ project named ItemTrackerKotlinRDSRest

Perform these steps. 

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.
2. In the **New Project** dialog box, choose **Kotlin**.
3. Enter the name **ItemTrackerKotlinRDSRest**. 
4. Select **Gradle Kotlin** for the Build System.
5. Select your JVM option and choose **Next**.
6. Choose **Finish**.

## Add the dependencies to your Gradle build file

At this point, you have a new project. Confirm that the **build.gradle.kts** file looks like the following.

```yaml
  import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "me.scmacdon"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.3.0")
    }
}

repositories {
    mavenCentral()
    jcenter()
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("aws.sdk.kotlin:rdsdata:0.17.1-beta")
    implementation("aws.sdk.kotlin:ses:0.17.1-beta")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


```

## Create the Kotlin classes

Create a new package in the **main/kotlin** folder named **com.example.demo**. The following Kotlin classes go into this package.

+ **DemoApplication** - Used as the base class and Controller for the Spring Boot application. 
+ **InjectWorkService** - Uses the **RDSDataClient** to submit a new record into the work table.
+ **RetrieveItems** -  Uses the **RDSDataClient** to retrieve a data set from the work table.
+ **SendMessage** - Uses the Amazon SES Kotlin API to send email messages.
+ **WorkItem** - Represents the application model.

**Note:** The **MessageResource** class is located in the **DemoApplication** file.

### DemoApplication class 

The following Kotlin code represents the **DemoApplication** class. This is the entry point into a Spring boot application.  

```kotlin
package com.example.demo

import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@SpringBootApplication
open class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("api/")
class MessageResource {

    // Add a new item.
    @PostMapping("/add")
    fun addItems(@RequestBody payLoad: Map<String, Any>): String = runBlocking {
        val injectWorkService = InjectWorkService()
        val nameVal = "user"
        val guideVal = payLoad.get("guide").toString()
        val descriptionVal = payLoad.get("description").toString()
        val statusVal = payLoad.get("status").toString()

        // Create a Work Item object.
        val myWork = WorkItem()
        myWork.guide = guideVal
        myWork.description = descriptionVal
        myWork.status = statusVal
        myWork.name = nameVal
        val id = injectWorkService.injestNewSubmission(myWork)
        return@runBlocking "Item $id added successfully!"
    }

    // Retrieve items.
    @GetMapping("items/{state}")
    fun getItems(@PathVariable state: String): MutableList<WorkItem> = runBlocking {
        val retrieveItems = RetrieveItems()
        val list: MutableList<WorkItem>
        val name = "user"
        if (state.compareTo("archive") == 0) {
            list = retrieveItems.getItemsDataSQL(name, 1)
        } else {
            list = retrieveItems.getItemsDataSQL(name, 0)
        }
        return@runBlocking list
    }

    // Flip an item from Active to Archive.
    @PutMapping("mod/{id}")
    fun modUser(@PathVariable id: String): String = runBlocking {
        val retrieveItems = RetrieveItems()
        retrieveItems.flipItemArchive(id)
        return@runBlocking id
    }

    // Send a report through Amazon SES.
    @PutMapping("report/{email}")
    fun sendReport(@PathVariable email: String): String = runBlocking {
        val retrieveItems = RetrieveItems()
        val nameVal = "user"
        val sendMsg = SendMessage()
        val xml = retrieveItems.getItemsDataSQLReport(nameVal, 0)
        try {
            sendMsg.send(email, xml)
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking "Report was sent"
    }
}

```    

### RetrieveItems class

The following Kotlin code represents the **RetrieveItems** class that retrieves data from the **Work** table. Notice that you are required to specify ARN values for Secrets Manager and the Amazon Aurora Serverless database (as discussed in the *Create the resources* section). You must have both of these values for your code to work. To use the **RDSDataClient**, create an **ExecuteStatementRequest** object and specify both ARN values, the database name, and the SQL statement used to retrieve data from the **Work** table.

```java
package com.example.demo

import aws.sdk.kotlin.services.rdsdata.RdsDataClient
import aws.sdk.kotlin.services.rdsdata.model.ExecuteStatementRequest
import aws.sdk.kotlin.services.rdsdata.model.Field
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@Component
class RetrieveItems {

    private val secretArnVal = "<Enter value>"
    private val resourceArnVal = "<Enter value>"

    // Archive the specific item.
    suspend fun flipItemArchive(id: String): String {
        val sqlStatement: String
        val arc = 1

        // Specify the SQL statement to query data.
        sqlStatement = "update work set archive = '$arc' where idwork ='$id' "
        val sqlRequest = ExecuteStatementRequest {
            secretArn = secretArnVal
            sql = sqlStatement
            database = "jobs"
            resourceArn = resourceArnVal
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            rdsDataClient.executeStatement(sqlRequest)
        }
        return id
    }

    // Get items from the database.
    suspend fun getItemsDataSQL(username: String, arch: Int): MutableList<WorkItem> {
        val records = mutableListOf<WorkItem>()
        val sqlStatement = "Select * FROM work where username = '$username ' and archive = $arch"
        val sqlRequest = ExecuteStatementRequest {
            secretArn = secretArnVal
            sql = sqlStatement
            database = "jobs"
            resourceArn = resourceArnVal
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            val response = rdsDataClient.executeStatement(sqlRequest)
            val dataList: List<List<Field>>? = response.records
            var workItem: WorkItem
            var index: Int

            // Get the records.
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (myField in list) {
                        val field: Field = myField
                        val result = field.toString()
                        val value = result.substringAfter("=").substringBefore(')')
                        if (index == 0) {
                            workItem.id = value
                        } else if (index == 1) {
                            workItem.date = value
                        } else if (index == 2) {
                            workItem.description = value
                        } else if (index == 3) {
                            workItem.guide = value
                        } else if (index == 4) {
                            workItem.status = value
                        } else if (index == 5) {
                            workItem.name = value
                        }
                        index++
                    }

                    // Push the object to the list.
                    records.add(workItem)
                }
            }
        }
        return records
    }

    // Get Items data.
    suspend fun getItemsDataSQLReport(username: String, arch: Int): String? {
        val records = mutableListOf<WorkItem>()
        val sqlStatement: String = "Select * FROM work where username = '" + username + "' and archive = " + arch + ""
        val sqlRequest = ExecuteStatementRequest {
            secretArn = secretArnVal
            sql = sqlStatement
            database = "jobs"
            resourceArn = resourceArnVal
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            val response = rdsDataClient.executeStatement(sqlRequest)
            val dataList: List<List<Field>>? = response.records
            var workItem: WorkItem
            var index: Int

            // Get the records.
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (myField in list) {
                        val field: Field = myField
                        val result = field.toString()
                        val value = result.substringAfter("=").substringBefore(')')
                        if (index == 0) {
                            workItem.id = value
                        } else if (index == 1) {
                            workItem.date = value
                        } else if (index == 2) {
                            workItem.description = value
                        } else if (index == 3) {
                            workItem.guide = value
                        } else if (index == 4) {
                            workItem.status = value
                        } else if (index == 5) {
                            workItem.name = value
                        }
                        index++
                    }

                    // Push the object to the list.
                    records.add(workItem)
                }
            }
        }
        return convertToString(toXml(records))
    }

    // Convert Work data into XML to use in the report.
    fun toXml(itemList: List<WorkItem>): Document? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.newDocument()
            val root = doc.createElement("Items")
            doc.appendChild(root)

            // Get the elements from the collection.
            val custCount = itemList.size

            // Iterate through the collection.
            for (index in 0 until custCount) {

                // Get the WorkItem object from the collection.
                val myItem = itemList[index]
                val item = doc.createElement("Item")
                root.appendChild(item)

                // Set Id.
                val id = doc.createElement("Id")
                id.appendChild(doc.createTextNode(myItem.id))
                item.appendChild(id)

                // Set Name.
                val name = doc.createElement("Name")
                name.appendChild(doc.createTextNode(myItem.name))
                item.appendChild(name)

                // Set Date.
                val date = doc.createElement("Date")
                date.appendChild(doc.createTextNode(myItem.date))
                item.appendChild(date)

                // Set Description.
                val desc = doc.createElement("Description")
                desc.appendChild(doc.createTextNode(myItem.description))
                item.appendChild(desc)

                // Set Guide.
                val guide = doc.createElement("Guide")
                guide.appendChild(doc.createTextNode(myItem.guide))
                item.appendChild(guide)

                // Set Status.
                val status = doc.createElement("Status")
                status.appendChild(doc.createTextNode(myItem.status))
                item.appendChild(status)
            }
            return doc
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return null
    }

    fun convertToString(xml: Document?): String? {
        try {
            val transformer = TransformerFactory.newInstance().newTransformer()
            val result = StreamResult(StringWriter())
            val source = DOMSource(xml)
            transformer.transform(source, result)
            return result.writer.toString()
        } catch (ex: TransformerException) {
            ex.printStackTrace()
        }
        return null
    }
}


```

### InjectWorkService class

The following Kotlin code represents the **InjectWorkService** class. Notice that you need to specify ARN values for Secrets Manager and the Amazon Serverless Aurora database (as discussed in the *Create the resources* section). You must have both of these values for your code to work. To use the **RDSDataClient**, create an **ExecuteStatementRequest** object and specify both ARN values, the database name, and the SQL statement used to submit data to the work table.

```kotlin
package com.example.demo

import aws.sdk.kotlin.services.rdsdata.RdsDataClient
import aws.sdk.kotlin.services.rdsdata.model.ExecuteStatementRequest
import org.springframework.stereotype.Component
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class InjectWorkService {

    private val secretArnVal = "<Enter value>"
    private val resourceArnVal = "<Enter value>"

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String? {
        val arc = 0
        val name = item.name
        val guide = item.guide
        val description = item.description
        val status = item.status

        // Generate the work item ID.
        val uuid = UUID.randomUUID()
        val workId = uuid.toString()

        // Date conversion.
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val now = LocalDateTime.now()
        val sDate1 = dtf.format(now)
        val date1 = SimpleDateFormat("yyyy/MM/dd").parse(sDate1)
        val sqlDate = Date(date1.time)

        // Inject an item into the system.
        val sqlStatement = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES('$workId', '$name', '$sqlDate','$description','$guide','$status','$arc');"
        val sqlRequest = ExecuteStatementRequest {
            secretArn = secretArnVal
            sql = sqlStatement
            database = "jobs"
            resourceArn = resourceArnVal
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            rdsDataClient.executeStatement(sqlRequest)
        }
        return workId
    }
}


```

### SendMessage class
The **SendMessage** class uses the AWS SDK for Kotlin SES API to send an email message. Before you can send the email message, the email address that you're sending it to must be verified. For more information, see [Verifying an email address](https://docs.aws.amazon.com/ses/latest/DeveloperGuide//verify-email-addresses-procedure.html).

The following Java code represents the **SendMessage** class. 

```kotlin
package com.example.demo

import org.springframework.stereotype.Component
import kotlin.system.exitProcess
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.SesException
import aws.sdk.kotlin.services.ses.model.Destination
import aws.sdk.kotlin.services.ses.model.Content
import aws.sdk.kotlin.services.ses.model.Body
import aws.sdk.kotlin.services.ses.model.Message
import aws.sdk.kotlin.services.ses.model.SendEmailRequest

@Component
class SendMessage {

    suspend fun send(
        recipient: String,
        strValue: String?
    ) {
        val sesClient = SesClient { region = "us-east-1" }
        // The HTML body of the email.
        val bodyHTML = ("<html>" + "<head></head>" + "<body>" + "<h1>Amazon RDS Items!</h1>"
                + "<textarea>$strValue</textarea>" + "</body>" + "</html>")

        val destinationOb = Destination {
            toAddresses = listOf(recipient)
        }

        val contentOb = Content {
            data = bodyHTML
        }

        val subOb = Content {
            data = "Item Report"
        }

        val bodyOb= Body {
            html = contentOb
        }

        val msgOb = Message {
            subject = subOb
            body = bodyOb
        }

        val emailRequest = SendEmailRequest {
            destination = destinationOb
            message = msgOb
            source = "<Enter email>"
        }

        try {
            println("Attempting to send an email through Amazon SES using the AWS SDK for Kotlin...")
            sesClient.sendEmail(emailRequest)

        } catch (e: SesException) {
            println(e.message)
            sesClient.close()
            exitProcess(0)
        }
    }
}
```

**Note:** You must update the email **sender** address with a verified email address. Otherwise, the email is not sent. For more information, see [Verifying email addresses in Amazon SES](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/verify-email-addresses.html).       


### WorkItem class

The following Kotlin code represents the **WorkItem** class.   

```kotlin
    package com.example.demo

class WorkItem {
    var id: String? = null
    var name: String? = null
    var guide: String? = null
    var date: String? = null
    var description: String? = null
    var status: String? = null
}
```

## Run the application 

Using the IntelliJ IDE, you can run your Spring REST API. The first time you run it, choose the run icon in the main class. The Spring API supports the following URLs. 

- /api/items/{state} - A GET request that returns all active or archive data items from the **Work** table. 
- /api/mod/{id} - A PUT request that converts the specified data item to an archived item. 
- /api/add - A POST request that adds a new item to the database. 
- /api/report/{email} - A PUT request that creates a report of active items and emails the report. 

**Note**: The React SPA created in the next section consumes all of these URLs. 

Confirm that the Spring REST API works by viewing the Active items. Enter the following URL into a browser. 

http://localhost:8080/api/items/active

The following illustration shows the JSON data returned from the Spring REST API. 

![AWS Tracking Application](images/browser.png)

## Create the React front end

You can create the React SPA that consumes the JSON data returned from the Spring REST API. To create the React SPA, you can download files from the following GitHub repository. Included in this repository are instructions on how to set up the project. Click the following link to access the GitHub location [Work item tracker web client](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/clients/react/item-tracker/README.md).  

### Update WorkItem.js

You must modify the **WorkItem.js** file so that your React requests work with your Java backend. Update this file to include this code.

```javascript
import React, {useEffect, useState} from "react";
import Button from "react-bootstrap/Button";
import Form from 'react-bootstrap/Form';
import Modal from "react-bootstrap/Modal";

import * as service from './RestService';

/**
 * An element that displays an 'Add item' button that lets you add an item to the work
 * item list. When you choose the 'Add item' button, a modal form is displayed that
 * includes form fields that you can use to define the work item. When you choose the
 * 'Add' button on the form, your new work item is sent to the server so it can be
 * added to the database.
 *
 * @returns {JSX.Element}
 */
export const WorkItem = () => {
  const [user, setUser] = useState('');
  const [guide, setGuide] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('');
  const [show, setShow] = useState(false);
  const [canAdd, setCanAdd] = useState(false);

  useEffect(() => {
    let can = user.length > 0 && guide.length > 0 && description.length > 0 && status.length > 0;
    setCanAdd(can);
  }, [user, guide, description, status]);

  const handleAdd = () => {
    service.addWorkItem({name: user, guide: guide, description: description, status: status})
      .catch(console.error);
    setShow(false);
  };

  const handleClose = () => {
    setShow(false);
  };

  return (
    <>
      <Button onClick={() => setShow(true)} variant="primary">Add item</Button>

      <Modal show={show} onHide={handleClose} dialogClassName="modal-90w">
        <Modal.Header closeButton>
          <Modal.Title>Add a new work item</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group>
              <Form.Label htmlFor='userField'>User</Form.Label>
              <Form.Control id='userField' type="text" placeholder="User name"
                            onChange={(event) => setUser(event.target.value)}/>
            </Form.Group>
            <Form.Group>
              <Form.Label htmlFor='guideField'>Guide</Form.Label>
              <Form.Control id='guideField' type="text" placeholder="Developer guide"
                            onChange={(event) => setGuide(event.target.value)}/>
            </Form.Group>
            <Form.Group>
              <Form.Label htmlFor='descriptionField'>Description</Form.Label>
              <Form.Control as="textarea" rows={3} id='descriptionField'
                            onChange={(event) => setDescription(event.target.value)}/>
            </Form.Group>
            <Form.Group>
              <Form.Label htmlFor='statusField'>Status</Form.Label>
              <Form.Control as="textarea" rows={3} id='statusField'
                            onChange={(event) => setStatus(event.target.value)}/>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>Close</Button>
          <Button variant="primary" disabled={!canAdd} onClick={handleAdd}>Add</Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

```
### Update WorkItems.js

You must modify the **WorkItems.js** file so that your React requests work with your Java backend. Update this file to include this code.

```javascript
import React, {useEffect, useState} from 'react';
import * as service from './RestService';
import Alert from "react-bootstrap/Alert";
import Button from "react-bootstrap/Button";
import Col from "react-bootstrap/Col";
import FloatingLabel from "react-bootstrap/FloatingLabel";
import FormControl from "react-bootstrap/FormControl";
import Form from "react-bootstrap/Form";
import InputGroup from "react-bootstrap/InputGroup";
import Placeholder from "react-bootstrap/Placeholder";
import Row from "react-bootstrap/Row";
import Table from "react-bootstrap/Table";
import {WorkItem} from "./WorkItem";

/**
 * An element that displays a list of work items that are retrieved from a REST service.
 *
 * * Select Active or Archived to display work items with the specified state.
 * * Select the wastebasket icon to archive and active item.
 * * Select 'Add item' to add a new item.
 * * Enter a recipient email and select 'Send report' to send a report of work items.
 *
 * @returns {JSX.Element}
 */

export const WorkItems = () => {
  const [email, setEmail] = useState('');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState('active');
  const [error, setError] = useState('');

  const getItems = async () => {
    setError('');
    setLoading(true);
    const response = await service.getWorkItems(status).catch((e) => {setError(e.message)});
    setItems(response ? await response.data : []);
    setLoading(false);
  };

  useEffect(() => {
    getItems().catch((e) => {setError(e.message)});
  }, [status]);

  const archiveItem = async (itemId) => {
    service.archiveItem(itemId).catch((e) => {setError(e.message)});
    getItems().catch((e) => {setError(e.message)});
  }

  const sendReport = async () => {
    service.mailItem(email).catch((e) => {setError(e.message)});
  }

  const handleStatusChange = (newStatus) => {
    setStatus(newStatus);
  }

  return (
    <>
      {error !== ''
        ?
        <Row>
          <Col>
            <Alert variant="danger">{error}</Alert>
          </Col>
        </Row>
        : null
      }
      <Row>
        <Col className="col-3">
          <FloatingLabel controlId="floatingSelect" label="State">
            <Form.Select aria-label="Status" onChange={(event) => handleStatusChange(event.target.value)}>
              <option value="active">Active</option>
              <option value="archive">Archived</option>
            </Form.Select>
          </FloatingLabel>
        </Col>
        <Col className="col-5">
          <InputGroup>
            <FormControl onChange={(event) => setEmail(event.target.value)}
              placeholder="Recipient's email"
              aria-label="Recipient's email"
              aria-describedby="basic-addon2"
            />
            <Button
              variant="outline-secondary"
              id="button-addon2"
              disabled={email === ''}
              onClick={() => sendReport()}>
                Send report
            </Button>
          </InputGroup>
          <Form.Text className="text-muted">
            You must first register the recipient's email with Amazon SES.
          </Form.Text>
        </Col>
      </Row>
      <hr/>
      <Row>
        <h3>Work items</h3>
        <p>Click the 🗑 icon to Archive an item.</p>
      </Row>
      <Row style={{maxHeight: `calc(100vh - 400px)`, overflowY: "auto"}}>
        <Col>
          {!loading && items.length === 0
            ? <Alert variant="info">No work items found.</Alert>
            : <Table striped>
              <thead>
              <tr>
                <th>Item Id</th>
                <th>User</th>
                <th>Guide</th>
                <th>Description</th>
                <th>Status</th>
                <th/>
              </tr>
              </thead>
              {loading
                ? <tbody>{
                  [1, 2, 3].map(item =>
                    <tr key={item}>
                      <td><Placeholder animation="glow"><Placeholder xs={3}/></Placeholder></td>
                      <td><Placeholder animation="glow"><Placeholder xs={3}/></Placeholder></td>
                      <td><Placeholder animation="glow"><Placeholder xs={3}/></Placeholder></td>
                      <td><Placeholder animation="glow"><Placeholder xs={3}/></Placeholder></td>
                      <td><Placeholder animation="glow"><Placeholder xs={3}/></Placeholder></td>
                    </tr>
                  )
                }
                </tbody>
                : <tbody>{
                  items.map(item =>
                    <tr key={item.id}>
                      <td>{item.id}</td>
                      <td>{item.name}</td>
                      <td>{item.guide}</td>
                      <td>{item.description}</td>
                      <td>{item.status}</td>
                      <td>{
                        status === 'active' ?
                          <Button variant="outline-secondary" size="sm" onClick={() => archiveItem(item.id)}>🗑</Button>
                          : null
                      }
                      </td>
                    </tr>
                  )
                }
                </tbody>
              }
            </Table>
          }
        </Col>
      </Row>
      <Row>
        <Col>
          <WorkItem />
        </Col>
      </Row>
    </>
  )
};
```


### Update RestService.js

You must modify the **RestService.js** file so that your React requests work with your Java backend. Update the file to include this code.

```javascript

/**
 * Sends REST requests to get work items, add new work items, modify work items,
 * and send an email report.
 *
 * The base URL of the REST service is stored in config.json. If necessary, update this
 * value to your endpoint.
 */

 import axios from 'axios'
 import configData from './config.json'
 
 /**
  * Sends a POST request to add a new work item.
  *
  * @param item: The work item to add.
  * @returns {Promise<void>}
  */
 export const addWorkItem = async (item) => {
        let status = item.status;
        let description = item.description;
        let guide = item.guide;
        let payload = { status: item.status, description: item.description , guide: item.guide};
        await axios.post(`${configData.BASE_URL}/api/add`, payload);
 };
 
 /**
  * Sends a GET request to retrieve work items that are in the specified state.
  *
  * @param state: The state of work items to retrieve. Can be either 'active' or 'archive'.
  * @returns {Promise<AxiosResponse<any>>}: The list of work items that have the
  *                                         specified state.
  */
 export const getWorkItems = async (state) => {
   return await axios.get(`${configData.BASE_URL}/api/items/${state}`);
 };
 
 /**
  * Sends a PUT request to archive an active item.
  *
  * @param itemId: The ID of the item to archive.
  * @returns {Promise<void>}
  */
 export const archiveItem = async (itemId) => {
   await axios.put(`${configData.BASE_URL}/api/mod/${itemId}`);
 }
 
 /**
  * Sends a POST request to email a report of work items.
  *
  * @param email: The report recipient's email address.
  * @returns {Promise<void>}
  */
 export const mailItem = async (email) => {
   await axios.put(`${configData.BASE_URL}/api/report/${email}`);
 }

```
  
### Next steps
Congratulations, you have created a decoupled React application that consumes data from a Spring REST API. The Spring REST API uses the AWS SDK for Java (v2) to invoke AWS services. As stated at the beginning of this tutorial, be sure to delete all of the resources that you create during this tutorial so that you won't continue to be charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).
