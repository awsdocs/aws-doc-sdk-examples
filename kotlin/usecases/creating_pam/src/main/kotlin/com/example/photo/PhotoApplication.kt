/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo

import com.example.photo.endpoints.DownloadEndpoint
import com.example.photo.endpoints.UploadEndpoint
import com.example.photo.services.DynamoDBService
import com.example.photo.services.S3Service
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList

@SpringBootApplication
open class PhotoApplication

fun main(args: Array<String>) {
    runApplication<PhotoApplication>(*args)
}

@CrossOrigin(origins = ["*"])
@RestController
class MessageResource {

    @GetMapping("api/photo")
    fun getItems(@RequestParam(required = false) archived: String?): String = runBlocking {
        val dbService = DynamoDBService()
        val map: Map<String, WorkCount> = dbService.scanPhotoTable()
        val gson = Gson()
        val m = mutableMapOf<String, Map<String, WorkCount>>()
        m["labels"] = map
        val json = gson.toJson(m)
        return@runBlocking json
    }

    // Upload a video to analyze.
    @RequestMapping(value = ["api/photo/upload2"], method = [RequestMethod.PUT])
    fun uploadFile(request: HttpServletRequest, response: HttpServletResponse?): String? = runBlocking {
        val s3Service2 = S3Service()
        val fileName = request.getParameter("filename")
        return@runBlocking s3Service2.signObjectToUpload(fileName)
    }

    // Upload a photo to an Amazon S3 bucket.
    @RequestMapping(value = ["api/photo/upload"], method = [RequestMethod.POST])
    fun singleFileUpload(@RequestParam("file") file: MultipartFile): String = runBlocking {
        val bytes = file.bytes
        val fileName = file.originalFilename
        val uuid = UUID.randomUUID()
        val uniqueFileName = "$uuid-$fileName"

        var upload = UploadEndpoint()
        if (fileName != null) {
            upload.upload(bytes, uniqueFileName)
        }

        return@runBlocking "You have successfully uploaded $fileName"
    }

    @PostMapping("api/photo/restore")
    @ResponseBody
    fun startRestore(@RequestBody createRestoreRequest: CreateRestore): String = runBlocking {
        val tags: List<String> = createRestoreRequest.tags
        val downloadEndpoint = DownloadEndpoint()
        return@runBlocking downloadEndpoint.downloadFiles(tags)
    }
}

class CreateRestore {
    var tags: List<String> = ArrayList()
    var notify = ""
}
