# Creating a React and Spring REST application that queries Amazon Redshift data

## Overview

| Heading      | Description |
| ----------- | ----------- |
| Description | Discusses how to develop a Spring REST API that queries Amazon Redshift data. The Spring REST API uses the AWS SDK for Kotlin to invoke AWS services and is used by a React application that displays the data.   |
| Audience   |  Developer (intermediate)        |
| Updated   | 9/02/2022        |
| Required skills   | Kotlin, Maven, JavaScript  |

## Purpose

You can develop a dynamic web application that tracks and reports on work items by using the following AWS services:

+ Amazon DynamoDB
+ Amazon Simple Email Service (Amazon SES). 

The application you create is a decoupled React application that uses a Spring REST API to return Amazon Redshift data. That is, the React application is a single-page application (SPA) that interacts with a Spring REST API by making RESTful GET and POST requests. The Spring REST API uses the Amazon Redshift Kotlin API to perform CRUD operations on the Amazon Redshift database. Then, the Spring REST API returns JSON data in an HTTP response, as shown in the following illustration. 

![AWS Tracking Application](images/overviewred.png)

#### Topics

+ Prerequisites
+ Understand the AWS Tracker application
+ Create an IntelliJ project named ItemTrackerDynamoDBRest
+ Add the Spring POM dependencies to your project
+ Create the Java classes
+ Create the React front end

## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Kotlin IDE (this tutorial uses the IntelliJ IDE)
+ Java 1.8 JDK
+ Gradle 6.8 or higher

**Note**: Make sure that you have installed the Kotlin plug-in for IntelliJ. 

### ⚠️ Important

+ The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re not charged.

### Creating the resources

Using the AWS Management Console, create an Amazon Redshift cluster and then create a database named **dev**. Next, create a table named **Work** that contains the following fields:

+ **idwork** - A VARCHAR(45) value that represents the PK.
+ **date** - A date value that specifies the date the item was created.
+ **description** - A VARCHAR(400) value that describes the item.
+ **guide** - A VARCHAR(45) value that represents the deliverable being worked on.
+ **status** - A VARCHAR(400) value that describes the status.
+ **username** - A VARCHAR(45) value that represents the user who entered the item.
+ **archive** - A TINYINT(4) value that represents whether this is an active or archive item.

The following image shows the Amazon Redshift **Work** table.

![AWS Tracking Application](images/worktable.png)

To use the **RedshiftDataClient** object, you must have the following Amazon Redshift values: 

+ The name of the database (for example, dev)
+ The name of the database user that you configured
+ The name of the Amazon Redshift cluster (for example, redshift-cluster-1)

For more information, see [Getting started with Amazon Redshift clusters and data loading](https://docs.aws.amazon.com/redshift/latest/gsg/database-tasks.html).

**Note**: After you create the **Work** table, place some records into it; otherwise, your Rest API returns an empty result set. 

## Understand the AWS Tracker React application 

A user can perform the following tasks using the React application:

+ View all active items.
+ View archived items that are complete.
+ Add a new item. 
+ Convert an active item into an archived item.
+ Send a report to an email recipient.

The React SPA displays *active* and *archive* items. For example, the following illustration shows the React application displaying active data.

![AWS Tracking Application](images/activeItems.png)

Likewise, the following illustration shows the React application displaying archived data.

![AWS Tracking Application](images/arcItems.png)

The React SPA also lets a user enter a new item. 

![AWS Tracking Application](images/clientAddItem.png)

The user can enter an email recipient into the text field and choose **Send report**.

![AWS Tracking Application](images/clientReport.png)

Active items are queried from the database and used to dynamically create a report. Then, the application uses Amazon SES to email the report the selected email recipient. 

## Create an IntelliJ project named ItemTrackerKotlinDynamoDBRest

Perform these steps. 

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.
2. In the **New Project** dialog box, choose **Kotlin**.
3. Enter the name **ItemTrackerKotlinDynamoDBRest**. 
4. Select **Gradle Kotlin** for the Build System.
5. Select your JVM option and choose **Next**.
6. Choose **Finish**.

## Add the dependencies to your Gradle build file

At this point, you have a new project named **ItemTrackerKotlinDynamoDBRest**.

Ensure that the **build.gradle.kts** file looks like the following.

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
    implementation ("javax.mail:javax.mail-api:1.6.2")
    implementation ("com.sun.mail:javax.mail:1.6.2")
    implementation("aws.sdk.kotlin:dynamodb:0.17.1-beta")
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

Create a new package in the **main/kotlin** folder named **com.aws.rest**. The following Kotlin classes go into this package.

+ **App** - Used as the base class for the Spring Boot application. 
+ **MessageResource** - Represents the controller used in this application that handles HTTP requests.
+ **InjectWorkService** - Uses the Amazon Redshift Kotlin API to add data to the **Work** table.
+ **RetrieveItems** - Uses the Amazon Redshift Kotlin API to query data from the **Work** table.
+ **SendMessage** - Uses the Amazon SES Kotlin API to send email messages.
+ **WorkItem** - Represents the application model.

**Note:** The **MessageResource** class is located in the **App** file.

### Create the App class

The following Kotlin code represents the **App** and the **MessageResource** classes. Notice that **App** uses the **@SpringBootApplication** annotation while the **MessageResource** class uses the **@RestController** annotation. In addition, the Spring Controller uses **runBlocking**, which is part of Kotlin Coroutine functionality. For more information, see [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html).  

```kotlin
  package com.aws.rest

import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@SpringBootApplication
open class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("api/")
class MessageResource {
    // Add a new item.
    @RequestMapping(value = ["/add"], method = [RequestMethod.POST])
    @ResponseBody
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
        if (state.compareTo("archive") == 0) {
            list = retrieveItems.getData(1)!!
        } else {
            list = retrieveItems.getData(0)!!
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
        val sendMsg = SendMessage()
        val xml= retrieveItems.getDataXML(0)
        try {
            sendMsg.send(email, xml)
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking "Report was sent"
    }
}
```

### Create the RetrieveItems class

The **RetrieveItems** class uses the AWS SDK for Kotlin API to retrieve data from the **Work** table. For example, the **getData** method returns a collection of **WorkItem** objects that represent the result set.  

```kotlin
package com.aws.rest

import aws.sdk.kotlin.services.redshiftdata.RedshiftDataClient
import aws.sdk.kotlin.services.redshiftdata.model.DescribeStatementRequest
import aws.sdk.kotlin.services.redshiftdata.model.ExecuteStatementRequest
import aws.sdk.kotlin.services.redshiftdata.model.Field
import aws.sdk.kotlin.services.redshiftdata.model.GetStatementResultRequest
import kotlinx.coroutines.delay
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class RetrieveItems {

    private val databaseVal = "dev"
    private val dbUserVal = "awsuser"
    private val clusterId = "redshift-cluster-1"

    // Return items from the work table.
    suspend fun getData(arch: Int): MutableList<WorkItem> {
        val username = "user"
        val sqlStatement = "Select * FROM work where username = '$username' and archive = $arch"
        val id = performSQLStatement(sqlStatement)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResults(id)
    }

    // Return items from the work table.
    suspend fun getDataXML(arch: Int): String? {
        val username = "user"
        val sqlStatement = "Select * FROM work where username = '$username' and archive = $arch"
        val id = performSQLStatement(sqlStatement)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResultsXML(id)
    }
    
    // Returns items within a collection.
    suspend fun getResults(statementId: String?): MutableList<WorkItem> {
        val records = mutableListOf<WorkItem>()
        val resultRequest = GetStatementResultRequest {
            id = statementId
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            val response = redshiftDataClient.getStatementResult(resultRequest)
            var workItem: WorkItem
            var index: Int

            // Iterate through the List.
            val dataList: List<List<Field>>? = response.records

            // Get the records.
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        val value = parseValue(field)
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

                        // Increment the index.
                        index++
                    }

                    // Push the object to the List.
                    records.add(workItem)
                }
            }
            return records
        }
    }

    // Returns open items within XML.
    suspend fun getResultsXML(statementId: String?): String? {
        val records: MutableList<WorkItem> = ArrayList()
        val resultRequest = GetStatementResultRequest {
            id = statementId
        }
        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            val response = redshiftDataClient.getStatementResult(resultRequest)
            var workItem: WorkItem
            var index: Int

            // Iterate through the List element where each element is a List object.
            val dataList: List<List<Field>>? = response.records
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        val value = parseValue(field)
                        if (index == 0)
                            workItem.id = value
                        else if (index == 1)
                            workItem.date = value
                        else if (index == 2)
                            workItem.description = value
                        else if (index == 3)
                            workItem.guide = value
                        else if (index == 4)
                            workItem.status = value
                        else if (index == 5)
                            workItem.name = value

                        // Increment the index.
                        index++
                    }

                    // Push the object to the List.
                    records.add(workItem)
                }
            }
            return toXml(records)?.let { convertToString(it) }
        }
    }

    // Update the work table.
    suspend fun flipItemArchive(id: String) {
        val arc = 1
        val sqlStatement = "update work set archive = '$arc' where idwork ='$id' "

        val statementRequest = ExecuteStatementRequest {
            this.clusterIdentifier = clusterId
            this.database = databaseVal
            this.dbUser = dbUserVal
            sql = sqlStatement
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
        }
    }

    // Convert Work item data into XML.
    private fun toXml(itemList: MutableList<WorkItem>): Document? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.newDocument()

            // Start building the XML.
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

    private fun convertToString(xml: Document): String? {
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

    // Return the String value of the field.
    fun parseValue(myField: Field): String {
        val ss = myField.toString()
        if ("StringValue" in ss) {
            var str = ss.substringAfterLast("=")
            str = str.substring(0, str.length - 1)
            return str
        }
        return ""
    }

    suspend fun performSQLStatement(sqlStatement: String?): String? {
        val statementRequest = ExecuteStatementRequest {
            this.clusterIdentifier = clusterId
            this.database = databaseVal
            this.dbUser = dbUserVal
            sql = sqlStatement
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            val response = redshiftDataClient.executeStatement(statementRequest)
            return response.id
        }
    }

    suspend fun checkStatement(sqlId: String?) {
        val statementRequest = DescribeStatementRequest {
            id = sqlId
        }

        // Wait until the sql statement processing is finished.
        var finished = false
        var status: String
        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            while (!finished) {
                val response = redshiftDataClient.describeStatement(statementRequest)
                status = response.status.toString()
                println("...$status")

                if (status.compareTo("FINISHED") == 0) {
                    finished = true
                } else {
                    delay(500)
                }
            }
        }
        println("The statement is finished!")
    }
}

```

### Create InjectWorkService class

The **InjectWorkService** class uses the AWS SDK for Kotlin API to add data to the **Work** table. It adds new items, updates items, and performs queries. In the following code example, notice the use of an **Expression** object. This object is used to query either Open or Closed items. For example, in the **getOpenItems** method, if the value **true** is passed to this method, then only Open items are retrieved from the Amazon DynamoDB table. 

```kotlin
 package com.aws.rest

import aws.sdk.kotlin.services.redshiftdata.RedshiftDataClient
import aws.sdk.kotlin.services.redshiftdata.model.ExecuteStatementRequest
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class InjectWorkService {

    private val databaseVal = "dev"
    private val dbUserVal = "awsuser"
    private val clusterId = "redshift-cluster-1"

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String {
        val name = item.name
        val guide = item.guide
        val description = item.description
        val status = item.status
        val arc = 0

        // Generate the work item ID.
        val uuid = UUID.randomUUID()
        val workId = uuid.toString()

        // Date conversion.
        SimpleDateFormat("yyyy-MM-dd")
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val now = LocalDateTime.now()
        val sDate1 = dtf.format(now)
        val date1 = SimpleDateFormat("yyyy/MM/dd").parse(sDate1)
        val sqlDate = Date(date1.time)

        // Inject an item into the system.
        val sqlStatement = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES('$workId', '$name', '$sqlDate','$description','$guide','$status','$arc');"

        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
            return workId
        }
    }
}

```

### Create the SendMessage class

The **SendMessage** class uses the AWS SDK for Kotlin SES API to send an email message that contains the data queried from the Amazon Redshift table to an email recipient. An email address that you send an email message to must be verified. For information, see [Verifying an email address](https://docs.aws.amazon.com/ses/latest/DeveloperGuide//verify-email-addresses-procedure.html).

```kotlin
 package com.aws.rest

import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.Body
import aws.sdk.kotlin.services.ses.model.Content
import aws.sdk.kotlin.services.ses.model.Destination
import aws.sdk.kotlin.services.ses.model.Message
import aws.sdk.kotlin.services.ses.model.SendEmailRequest
import org.springframework.stereotype.Component

@Component
class SendMessage {

    suspend fun send(recipient: String, strValue: String?) {
        // The HTML body of the email.
        val bodyHTML = (
            "<html>" + "<head></head>" + "<body>" + "<h1>Amazon DynamoDB Items!</h1>" +
                "<textarea>$strValue</textarea>" + "</body>" + "</html>"
            )

        val destinationOb = Destination {
            toAddresses = listOf(recipient)
        }

        val contentOb = Content {
            data = bodyHTML
        }

        val subOb = Content {
            data = "Item Report"
        }

        val bodyOb = Body {
            html = contentOb
        }

        val msgOb = Message {
            subject = subOb
            body = bodyOb
        }

        val emailRequest = SendEmailRequest {
            destination = destinationOb
            message = msgOb
            source = "<Enter Email Address>"
        }

        SesClient { region = "us-east-1" }.use { sesClient ->
            println("Attempting to send an email through Amazon SES using the AWS SDK for Kotlin...")
            sesClient.sendEmail(emailRequest)
        }
    }
}

```

### Create the WorkItem class

The following Kotlin code represents the **WorkItem** class.

```kotlin
    package com.example.demo

    class WorkItem {

     var id: String? = null
     var arc: String? = null
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
 * item list. When you click the 'Add item' button, a modal form is displayed that
 * includes form fields that you can use to define the work item. When you click the
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

You must modify the **RestService.js** file so that your React requests work with your Java backend. Update this file to include this code.

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
Congratulations, you have created a decoupled React application that consumes data from a Spring REST API. The Spring REST API uses the AWS SDK for Kotlin to invoke AWS services. As stated at the beginning of this tutorial, be sure to delete all of the resources that you create during this tutorial so that you won't continue to be charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).


