/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.photo

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
class PhotoApp

fun main(args: Array<String>) {
    runApplication<PhotoApp>(*args)
}

@Controller
class MessageResource {

    // Change to your Bucket Name.
    private val bucketName = "scottphoto"

    @Autowired
    var s3Service: S3Service? = null

    @Autowired
    var recService: AnalyzePhotos? = null

    @Autowired
    var excel: WriteExcel? = null

    @GetMapping("/process")
    fun process(): String? {
        return "process"
    }

    @GetMapping("/photo")
    fun photo(): String? {
        return "upload"
    }

    @GetMapping("/")
    fun root(): String? {
        return "index"
    }

    @RequestMapping(value = ["/getimages"], method = [RequestMethod.GET])
    @ResponseBody
    fun getImages(request: HttpServletRequest?, response: HttpServletResponse?): String? = runBlocking{
        return@runBlocking s3Service?.ListAllObjects(bucketName)
    }

    // Generates a report that analyzes photos in a given bucket.
    @RequestMapping(value = ["/report"], method = [RequestMethod.GET])
    @ResponseBody
    fun report(request: HttpServletRequest, response: HttpServletResponse) = runBlocking {

        // Get a list of key names in the given bucket.
        val myKeys = s3Service?.listBucketObjects(bucketName)

        // loop through each element in the List.
        val myList = mutableListOf<List<*>>()
        val len = myKeys?.size
        for (z in 0 until len!!) {
            val key = myKeys?.get(z) as String
            val keyData = s3Service?.getObjectBytes(bucketName, key)

            //Analyze the photo.
            val item = recService?.DetectLabels(keyData, key)
            if (item != null) {
                myList.add(item)
            }
        }

        // Now we have a list of WorkItems describing the photos in the S3 bucket.
        val excelData = excel?.exportExcel(myList)
        try {

            // Download the report.
            val reportName  = "ExcelReport.xls"
            response.contentType  = "application/vnd.ms-excel"
            response.setHeader("Content-disposition", "attachment; filename=$reportName")
            org.apache.commons.io.IOUtils.copy(excelData, response?.outputStream)
            response.flushBuffer()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Downloads the given image from the Amazon S3 bucket.
    @RequestMapping(value = ["/downloadphoto"], method = [RequestMethod.GET])
    fun fileDownload(request: HttpServletRequest, response: HttpServletResponse)  = runBlocking {
        try {
            val photoKey = request.getParameter("photoKey")
            val photoBytes: ByteArray? = s3Service?.getObjectBytes(bucketName, photoKey)
            val `is`: InputStream = ByteArrayInputStream(photoBytes)

            // Define the required information here.
            response.contentType = "image/png"
            response.setHeader("Content-disposition", "attachment; filename=$photoKey")
            org.apache.commons.io.IOUtils.copy(`is`, response.outputStream)
            response.flushBuffer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Upload a photo to an Amazon S3 bucket.
    @RequestMapping(value = ["/upload"], method = [RequestMethod.POST])
    @ResponseBody
    fun singleFileUpload(@RequestParam("file") file: MultipartFile): ModelAndView? = runBlocking {
        try {
            val bytes = file.bytes
            val name = file.originalFilename

            // Put the file into the bucket.
            s3Service?.putObject(bytes, bucketName, name)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@runBlocking ModelAndView(RedirectView("photo"))
    }
}
