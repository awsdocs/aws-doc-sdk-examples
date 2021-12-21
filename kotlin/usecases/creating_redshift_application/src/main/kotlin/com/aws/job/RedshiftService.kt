/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.job

import org.springframework.stereotype.Component
import java.util.*
import aws.sdk.kotlin.services.redshiftdata.RedshiftDataClient
import aws.sdk.kotlin.services.redshiftdata.model.*
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.TranslateTextRequest
import kotlinx.coroutines.delay
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

@Component
class RedshiftService {
    val clusterId = "redshift-cluster-1"
    val databaseVal = "dev"
    val dbUserVal = "awsuser"


    // Add a new record to the Amazon Redshift table.
    suspend fun addRecord(author: String, title: String, body: String): String? {

        val uuid = UUID.randomUUID()
        val id = uuid.toString()

        // Date conversion.
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val now = LocalDateTime.now()
        val sDate1 = dtf.format(now)
        val date1 = SimpleDateFormat("yyyy/MM/dd").parse(sDate1)
        val sqlDate = Date(date1.time)

        // Inject an item into the system.
        val sqlStatement = "INSERT INTO blog (idblog, date, title, body, author) VALUES( '$uuid' ,'$sqlDate','$title' , '$body', '$author');"
        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
         }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
            return id
        }
    }

    // Returns a collection that returns the latest five posts from the Redshift table.
    suspend fun getPosts(lang: String, num: Int): String? {

        val sqlStatement = if (num == 5) "SELECT TOP 5 * FROM blog ORDER BY date DESC" else if (num == 10) "SELECT TOP 10 * FROM blog ORDER BY date DESC" else "SELECT * FROM blog ORDER BY date DESC"
        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            val response = redshiftDataClient.executeStatement(statementRequest)
            val myId = response.id
            checkStatement(redshiftDataClient, myId)
            val posts = getResults(redshiftDataClient, myId, lang)!!
            redshiftDataClient.close()
            return convertToString(toXml(posts))
        }
    }

    suspend fun getResults(redshiftDataClient: RedshiftDataClient, statementId: String?, lang: String): List<Post>? {

        val records = mutableListOf<Post>()
        val resultRequest = GetStatementResultRequest {
            id = statementId
        }

        val response = redshiftDataClient.getStatementResult(resultRequest)

        // Iterate through the List element where each element is a List object.
         val dataList = response.records
         var post: Post
         var index: Int

         if (dataList != null) {
                for (list in dataList) {

                    post = Post()
                    index = 0
                    for (myField in list) {
                        var value = parseValue(myField)

                        if (index == 0)
                            post.id= value
                        else if (index == 1)
                            post.date =value
                        else if (index == 2) {
                            if (lang != "English")
                                value = translateText(value, lang)
                                post.title = value
                        } else if (index == 3) {
                            if (lang != "English")
                                value = translateText(value, lang)
                                post.body = value
                        } else if (index == 4)
                            post.author= value

                        // Increment the index.
                        index++
                    }

                    // Push the Post object to the List.
                    records.add(post)
                }
            }

            return records
     }

    // Return the String value of the field.
    fun parseValue(myField:Field) :String {

        val ss = myField.toString()
        if ("StringValue" in ss) {

            var str  = ss.substringAfterLast("=")
            str = str.substring(0, str.length - 1)
            return str

        }
        return ""
    }

    suspend fun checkStatement(redshiftDataClient: RedshiftDataClient, sqlId: String?) {

        val statementRequest = DescribeStatementRequest {
            id = sqlId
        }

        // Wait until the sql statement processing is finished.
        val finished = false
        var status: String
        while (!finished) {
             val response = redshiftDataClient.describeStatement(statementRequest)
             status = response.status.toString()
             println("...$status")
            if (status.compareTo("FINISHED") == 0) {
                break
            }
               delay(500)
            }
            println("The statement is finished!")
    }

    // Convert the list to XML to pass back to the view.
    private fun toXml(itemsList: List<Post>): Document {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.newDocument()

            // Start building the XML.
            val root = doc.createElement("Items")
            doc.appendChild(root)

            // Iterate through the collection.
            for (post in itemsList) {
                val item = doc.createElement("Item")
                root.appendChild(item)

                // Set Id.
                val id = doc.createElement("Id")
                id.appendChild(doc.createTextNode(post.id))
                item.appendChild(id)

                // Set Date.
                val name = doc.createElement("Date")
                name.appendChild(doc.createTextNode(post.date))
                item.appendChild(name)

                // Set Title.
                val date = doc.createElement("Title")
                date.appendChild(doc.createTextNode(post.title))
                item.appendChild(date)

                // Set Content.
                val desc = doc.createElement("Content")
                desc.appendChild(doc.createTextNode(post.body))
                item.appendChild(desc)

                // Set Author.
                val guide = doc.createElement("Author")
                guide.appendChild(doc.createTextNode(post.author))
                item.appendChild(guide)
            }
            return doc
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
            exitProcess(1)
        }
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

    private suspend fun translateText(textVal: String, lang: String): String {

        val transValue: String
        if (lang.compareTo("French") == 0) {

                val textRequest = TranslateTextRequest {
                    sourceLanguageCode = "en"
                    targetLanguageCode = "fr"
                    text = textVal
                }

                TranslateClient { region = "us-east-1" }.use { translateClient ->
                    val textResponse = translateClient.translateText(textRequest)
                    transValue = textResponse.translatedText.toString()
                    return transValue
                }
        } else if (lang.compareTo("Russian") == 0) {

                val textRequest = TranslateTextRequest {
                    sourceLanguageCode = "en"
                    targetLanguageCode = "ru"
                    text = textVal
                }

               TranslateClient { region = "us-east-1" }.use { translateClient ->
                   val textResponse = translateClient.translateText(textRequest)
                   transValue = textResponse.translatedText.toString()
                   return transValue
               }
        } else if (lang.compareTo("Japanese") == 0) {

                val textRequest = TranslateTextRequest {
                    sourceLanguageCode = "en"
                    targetLanguageCode = "ja"
                    text = textVal
                }
                TranslateClient { region = "us-east-1" }.use { translateClient ->
                  val textResponse = translateClient.translateText(textRequest)
                   transValue = textResponse.translatedText.toString()
                   return transValue
                }
        } else if (lang.compareTo("Spanish") == 0) {

                val textRequest = TranslateTextRequest {
                    sourceLanguageCode = "en"
                    targetLanguageCode = "es"
                    text = textVal
                }

               TranslateClient { region = "us-east-1" }.use { translateClient ->
                  val textResponse = translateClient.translateText(textRequest)
                  transValue = textResponse.translatedText.toString()
                  return transValue
               }

        } else {

                val textRequest = TranslateTextRequest {
                    sourceLanguageCode = "en"
                    targetLanguageCode = "zh"
                    text = textVal
                }

               TranslateClient { region = "us-east-1" }.use { translateClient ->
                val textResponse = translateClient.translateText(textRequest)
                transValue = textResponse.translatedText.toString()
                  return transValue
               }
        }
    }
}