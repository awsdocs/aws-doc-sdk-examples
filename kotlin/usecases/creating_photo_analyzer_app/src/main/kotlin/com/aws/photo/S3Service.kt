/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.photo

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toByteArray
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.system.exitProcess

@Component
class S3Service {

    var myBytes: ByteArray? = null

    // Returns the names of all images in the given bucket.
    suspend fun listBucketObjects(bucketName: String?): List<*>? {

        var keyName: String
        val keys  = mutableListOf<String>()

        val listObjects = ListObjectsRequest {
            bucket = bucketName
        }

        S3Client { region = "us-west-2" }.use { s3Client ->
            val response = s3Client.listObjects(listObjects)
            response.contents?.forEach { myObject ->
                   keyName = myObject.key.toString()
                   keys.add(keyName)
            }
            return keys
        }
    }

    // Returns the names of all images and data within an XML document.
    suspend fun ListAllObjects(bucketName: String?): String? {

        var sizeLg: Long
        var dateIn: aws.smithy.kotlin.runtime.time.Instant?
        val bucketItems = mutableListOf<BucketItem>()

        val listObjects = ListObjectsRequest {
            bucket = bucketName
         }

        S3Client { region = "us-west-2" }.use { s3Client ->
            val res = s3Client.listObjects(listObjects)
            res.contents?.forEach { myObject ->
                val myItem = BucketItem()
                myItem.key = myObject.key
                myItem.owner = myObject.owner?.displayName.toString()
                sizeLg = (myObject.size / 1024)
                myItem.size = (sizeLg.toString())
                dateIn = myObject.lastModified
                myItem.date = dateIn.toString()

                // Push the items to the list.
                bucketItems.add(myItem)
            }
            return convertToString(toXml(bucketItems))
        }
    }

    // Places an image into an Amazon S3 bucket.
    suspend fun putObject(data: ByteArray, bucketName: String?, objectKey: String?): String? {
        val request =  PutObjectRequest{
                bucket = bucketName
                key = objectKey
                body = ByteStream.fromBytes(data)
         }

        S3Client { region = "us-west-2" }.use { s3Client ->
            val response = s3Client.putObject(request)
            return response.eTag
        }
    }

    // Get the byte[] from this Amazon S3 object.
    suspend fun getObjectBytes(bucketName: String?, keyName: String?): ByteArray? {
           val objectRequest = GetObjectRequest {
                key = keyName
                bucket = bucketName
            }

           S3Client { region = "us-west-2" }.use { s3Client ->
             s3Client.getObject(objectRequest) { resp ->
                myBytes = resp.body?.toByteArray()
             }
            return myBytes
           }
    }

    // Convert items into XML to pass back to the view.
    private fun toXml(itemList: List<BucketItem>): Document {
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

                // Set Key.
                val id = doc.createElement("Key")
                id.appendChild(doc.createTextNode(myItem.key))
                item.appendChild(id)

                // Set Owner.
                val name = doc.createElement("Owner")
                name.appendChild(doc.createTextNode(myItem.owner))
                item.appendChild(name)

                // Set Date.
                val date = doc.createElement("Date")
                date.appendChild(doc.createTextNode(myItem.date))
                item.appendChild(date)

                // Set Size.
                val desc = doc.createElement("Size")
                desc.appendChild(doc.createTextNode(myItem.size))
                item.appendChild(desc)
            }
            return doc
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
            exitProcess(0)
        }
   }

    private fun convertToString(xml: Document): String {
        try {
            val transformer = TransformerFactory.newInstance().newTransformer()
            val result = StreamResult(StringWriter())
            val source = DOMSource(xml)
            transformer.transform(source, result)
            return result.writer.toString()

        } catch (ex: TransformerException) {
            ex.printStackTrace()
            exitProcess(0)
        }
    }
}
