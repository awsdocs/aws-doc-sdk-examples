# Create a React and Spring REST application that queries Amazon Redshift data using the SDK for Kotlin

## Overview

| Heading      | Description |
| ----------- | ----------- |
| Description | Discusses how to develop a Spring Boot application that queries Amazon Redshift data. The Spring Boot application uses the AWS SDK for Kotlin to invoke AWS services and is used by a React application that displays the data. The React application uses Cloudscape. For information, see [Cloudscape](https://cloudscape.design/).    |
| Audience   |  Developer (intermediate)        |
| Updated   | 11/14/2022        |
| Required skills   | Kotlin, Gradle, JavaScript  |

## Purpose

You can develop a dynamic web application that tracks and reports on work items by using the following AWS services:

+ Amazon Redshift
+ Amazon Simple Email Service (Amazon SES)

The application you create is a decoupled React application that uses a Spring REST API to return Amazon Redshift data. That is, the React application interacts with a Spring API by making RESTful GET and POST requests. The Spring API uses an [RedshiftDataClient](https://sdk.amazonaws.com/kotlin/api/latest/redshiftdata/aws.sdk.kotlin.services.redshiftdata/-redshift-data-client/index.html) object to perform CRUD operations on the database. Then, the Spring REST API returns JSON data in an HTTP response, as shown in the following illustration. 

![AWS Tracking Application](images/overviewred.png)

#### Topics

+ Prerequisites
+ Understand the AWS Tracker application
+ Create an IntelliJ project
+ Add the dependencies to your Gradle build file
+ Create the Kotlin classes
+ Create the React front end

## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account.
+ A Kotlin IDE (this tutorial uses the IntelliJ IDE).
+ Java 1.8 JDK.
+ Gradle 6.8 or higher.
+ You must also set up your development environment. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html). 

### Important

+ The AWS services in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to delete all of the resources that you create during this tutorial so that you won't be charged.

### Create the resources

To create the required resources, create an Amazon Redshift cluster and then create a database named **dev**. Next, create a table named **Work** that contains the following fields:

+ **idwork** - A VARCHAR(45) value that represents the PK
+ **date** - A date value that specifies the date the item was created
+ **description** - A VARCHAR(400) value that describes the item
+ **guide** - A VARCHAR(45) value that represents the deliverable being worked on
+ **status** - A VARCHAR(400) value that describes the status
+ **username** - A VARCHAR(45) value that represents the user who entered the item
+ **archive** - A TINYINT(4) value that represents whether this is an active or archive item

The following image shows the Amazon Redshift **Work** table.

![AWS Tracking Application](images/worktable.png)

To use the **RedshiftDataClient** object, you must have the following Amazon Redshift values: 

+ The name of the database (for example, dev)
+ The name of the database user that you configured
+ The name of the Amazon Redshift cluster (for example, redshift-cluster-1)

For more information, see [Getting started with Amazon Redshift clusters and data loading](https://docs.aws.amazon.com/redshift/latest/gsg/database-tasks.html).


## Understand the AWS Tracker React application 

A user can perform the following tasks using the React application:

+ View all active items
+ View archived items that are complete
+ Add a new item
+ Convert an active item into an archived item
+ Send a report to an email recipient

The React application displays *active* and *archive* items. For example, the following illustration shows the React application displaying active data.

![AWS Tracking Application](images/activeNew.png)

Likewise, the following illustration shows the React application displaying archived data.

![AWS Tracking Application](images/archiveShow.png)

The React application lets a user convert an active item to an archived item by choosing the **Archive item(s)** button. 

![AWS Tracking Application](images/archiveNew.png)

The React application also lets a user enter a new item. 

![AWS Tracking Application](images/newItem.png)

The user can enter an email recipient into the text field and choose **Send Report**.

![AWS Tracking Application](images/newReport.png)

The application queries active items from the database and sends the data to the selected email recipient. 

## Create an IntelliJ project named ItemTrackerKotlinRedshiftRest

Perform the following steps. 

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.
2. In the **New Project** dialog box, choose **Kotlin**.
3. Enter the name **ItemTrackerKotlinRedshiftRest**. 
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
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("aws.sdk.kotlin:redshiftdata:0.17.1-beta")
    implementation("aws.sdk.kotlin:ses:0.17.1-beta")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.5")
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

+ **App** - Used as the base class and Controller for the Spring Boot application
+ **RedshiftService** - Uses the **RedshiftDataClient** object to perform CRUD operations on the database
+ **SendMessage** - Uses the **SesClient** object to send email messages
+ **WorkItem** - Represents the application model

**Note:** The **MessageResource** class is located in the **App** file.

### App class 

The following Kotlin code represents the **App** class. This is the entry point into a Spring boot application.  

```kotlin
package com.aws.rest

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

@SpringBootApplication
open class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
class MessageResource {

    @Autowired
    private lateinit var redshiftService: RedshiftService

    @Autowired
    private lateinit var sendMsg: SendMessage

    // Add a new item.
    @PostMapping("api/items")
    fun addItems(@RequestBody payLoad: Map<String, Any>): String = runBlocking {
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
        val id = redshiftService.injestNewSubmission(myWork)
        return@runBlocking "Item $id added successfully!"
    }

    // Retrieve items.
    @GetMapping("api/items")
    fun getItems(@RequestParam(required = false) archived: String?): MutableList<WorkItem> = runBlocking {
        val list: MutableList<WorkItem>
        if (archived != null) {
            list = redshiftService.getData(archived)
        } else {
            list = redshiftService.getData("")
        }
        return@runBlocking list
    }

    // Flip an item from Active to Archive.
    @PutMapping("api/items/{id}:archive")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun modUser(@PathVariable id: String) = runBlocking {
        redshiftService.flipItemArchive(id)
        return@runBlocking
    }

    // Send a report through Amazon SES.
    @PostMapping("api/items:report")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun sendReport(@RequestBody body: Map<String, String>) = runBlocking {
        val email = body.get("email")
        val xml = redshiftService.getDataXML()
        try {
            if (email != null && xml != null) {
                sendMsg.send(email, xml)
            }
        } catch (e: IOException) {
            e.stackTrace
        }
        return@runBlocking
    }
}


```    

### RedshiftService class
The following Kotlin code represents the **RedshiftService** class. You are required to specify three values (database, database user, and clusterId value) to use the **RedshiftDataClient** object (as discussed in the Create the resources section). Without all of these values, your code won't work. To use the **RedshiftDataClient**, you must create an ExecuteStatementRequest object and specify these values.

Also notice the use of [SqlParameter](https://sdk.amazonaws.com/kotlin/api/latest/redshiftdata/aws.sdk.kotlin.services.redshiftdata.model/-sql-parameter/index.html) when using SQL statements. For example, in the **injestNewSubmission** method, you build a list of **SqlParameter** objects that are used to add a new record to the database.

```kotlin
package com.aws.rest

import aws.sdk.kotlin.services.redshiftdata.RedshiftDataClient
import aws.sdk.kotlin.services.redshiftdata.model.DescribeStatementRequest
import aws.sdk.kotlin.services.redshiftdata.model.ExecuteStatementRequest
import aws.sdk.kotlin.services.redshiftdata.model.Field
import aws.sdk.kotlin.services.redshiftdata.model.GetStatementResultRequest
import aws.sdk.kotlin.services.redshiftdata.model.SqlParameter
import kotlinx.coroutines.delay
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import java.io.StringWriter
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.collections.ArrayList

@Component
class RedshiftService {

    private val databaseVal = "dev"
    private val dbUserVal = "awsuser"
    private val clusterId = "redshift-cluster-1"

    fun param(nameVal: String, valueVal: Boolean): SqlParameter {
        val myPar = SqlParameter {
            name = nameVal
            value = valueVal.toString()
        }
        return myPar
    }

    fun param2(nameVal: String, valueVal: String): SqlParameter {
        val myPar = SqlParameter {
            name = nameVal
            value = valueVal
        }
        return myPar
    }

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String {
        val name = item.name
        val guide = item.guide
        val description = item.description
        val status = item.status
        val arc = "0"

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

        // Inject an item into the database.
        val sqlStatement =
            "INSERT INTO work (idwork, username, date, description, guide, status, archive) VALUES" +
                "(:idwork, :username, :date, :description, :guide, :status, :arch);"

        val parametersVal = listOf(
            param2("arch", arc),
            param2("username", name.toString()),
            param2("status", status.toString()),
            param2("date", sqlDate.toString()),
            param2("description", description.toString()),
            param2("guide", guide.toString()),
            param2("idwork", workId)
        )

        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
            parameters = parametersVal
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
            return workId
        }
    }

    // Return items from the work table.
    suspend fun getData(status: String): MutableList<WorkItem> {
        val isArc: Boolean
        val statementRequest: ExecuteStatementRequest
        val sqlStatement: String
        if (status.compareTo("true") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work WHERE archive = :arch ;"
            isArc = true
            val parametersVal = listOf(param("arch", isArc))
            statementRequest = ExecuteStatementRequest {
                this.clusterIdentifier = clusterId
                this.database = databaseVal
                this.dbUser = dbUserVal
                this.parameters = parametersVal
                sql = sqlStatement
            }
        } else if (status.compareTo("false") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work WHERE archive = :arch ;"
            isArc = false
            val parametersVal = listOf(param("arch", isArc))
            statementRequest = ExecuteStatementRequest {
                this.clusterIdentifier = clusterId
                this.database = databaseVal
                this.dbUser = dbUserVal
                this.parameters = parametersVal
                sql = sqlStatement
            }
        } else {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work ;"
            statementRequest = ExecuteStatementRequest {
                this.clusterIdentifier = clusterId
                this.database = databaseVal
                this.dbUser = dbUserVal
                sql = sqlStatement
            }
        }

        val id = performSQLStatement(statementRequest)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResults(id)
    }

    // Return items from the work table.
    suspend fun getDataXML(): String? {
        val sqlStatement = "SELECT idwork, date, description, guide, status, username, archive " +
            "FROM work WHERE archive = :arch ;"
        val isArc = false
        val parametersVal = listOf(param("arch", isArc))
        val statementRequest = ExecuteStatementRequest {
            this.clusterIdentifier = clusterId
            this.database = databaseVal
            this.dbUser = dbUserVal
            this.parameters = parametersVal
            sql = sqlStatement
        }
        val id = performSQLStatement(statementRequest)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResultsXML(id)
    }

    // Returns items.
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
            var value: String
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        when (index) {
                            0 -> {
                                value = parseValue(field)
                                workItem.id = value
                            }
                            1 -> {
                                value = parseValue(field)
                                workItem.date = value
                            }
                            2 -> {
                                value = parseValue(field)
                                workItem.description = value
                            }
                            3 -> {
                                value = parseValue(field)
                                workItem.guide = value
                            }
                            4 -> {
                                value = parseValue(field)
                                workItem.status = value
                            }
                            5 -> {
                                value = parseValue(field)
                                workItem.name = value
                            }
                            6 -> {
                                value = parseBooleanValue(field)
                                workItem.archived = value != "false"
                            }
                        }
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
            var value: String
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        when (index) {
                            0 -> {
                                value = parseValue(field)
                                workItem.id = value
                            }
                            1 -> {
                                value = parseValue(field)
                                workItem.date = value
                            }
                            2 -> {
                                value = parseValue(field)
                                workItem.description = value
                            }
                            3 -> {
                                value = parseValue(field)
                                workItem.guide = value
                            }
                            4 -> {
                                value = parseValue(field)
                                workItem.status = value
                            }
                            5 -> {
                                value = parseValue(field)
                                workItem.name = value
                            }
                        }

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
        val arc = "1"
        // Specify the SQL statement to query data.
        val sqlStatement = "update work set archive = (:arch) where idwork =(:id);"
        val parametersVal = listOf(
            param2("arch", arc),
            param2("id", id)
        )
        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
            parameters = parametersVal
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
        }
    }

    // Convert Work Item data into XML.
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

    fun parseBooleanValue(myField: Field): String {
        val ss = myField.toString()
        if ("BooleanValue" in ss) {
            var str = ss.substringAfterLast("=")
            str = str.substring(0, str.length - 1)
            return str
        }
        return ""
    }

    suspend fun performSQLStatement(statementRequest: ExecuteStatementRequest): String? {
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

### SendMessage class
The **SendMessage** class uses the [SesClient](https://sdk.amazonaws.com/kotlin/api/latest/ses/aws.sdk.kotlin.services.ses/-ses-client/index.html) to send an email message. 

Before you can send the email message, the email address that you're sending it to must be verified. For more information, see [Verifying an email address](https://docs.aws.amazon.com/ses/latest/DeveloperGuide//verify-email-addresses-procedure.html).

The following Kotlin code represents the **SendMessage** class. 

```kotlin
package com.example.demo

import kotlin.system.exitProcess
import aws.sdk.kotlin.services.ses.SesClient
import aws.sdk.kotlin.services.ses.model.SesException
import aws.sdk.kotlin.services.ses.model.Destination
import aws.sdk.kotlin.services.ses.model.Content
import aws.sdk.kotlin.services.ses.model.Body
import aws.sdk.kotlin.services.ses.model.Message
import aws.sdk.kotlin.services.ses.model.SendEmailRequest

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
    var archived: Boolean? = null
}

```

## Run the application 

Using the IntelliJ IDE, you can run your Spring REST API. The first time you run it, choose the run icon in the main class. The Spring API supports the following URLs. 

- /api/items - A GET request that returns all data items from the **Work** table
- /api/items?archived=true - A GET request that returns either active or archive data items from the **Work** table
- /api/items/{id}:archive - A PUT request that converts the specified data item to an archived item
- /api/items - A POST request that adds a new item to the database
- api/items:report - A POST request that creates a report of active items and emails the report

**Note**: The React application created in the next section consumes all of the preceding URLs. 

Confirm that the Spring REST API works by viewing the Active items. Enter the following URL into a browser. 

http://localhost:8080/api/items

The following illustration shows the JSON data returned from the Spring REST API. 

![AWS Tracking Application](images/json2.png)

## Create the React front end

You can create the React application that consumes the JSON data returned from the Spring REST API. To create the React application, download files from the following GitHub repository. Included in this repository are instructions on how to set up the project. To access the GitHub location, see [Work item tracker web client](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/resources/clients/react/elwing).  

### Update BASE_URL

In the **config.json** file, you must make sure that the **BASE_URL** value references your Spring application.

```javascript
{
  "BASE_URL": "http://localhost:8080/api"
}
```
  
### Next steps
Congratulations, you have created a decoupled React application that consumes data from a Spring REST API. The Spring REST API uses the AWS SDK for Kotlin to invoke AWS services. As stated at the beginning of this tutorial, be sure to delete all of the resources that you create during this tutorial so that you won't continue to be charged.
