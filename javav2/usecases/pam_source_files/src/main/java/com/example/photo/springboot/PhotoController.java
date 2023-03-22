/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.springboot;

import com.example.photo.PhotoApplicationResources;
import com.example.photo.endpoints.UploadEndpoint;
import com.example.photo.services.AnalyzePhotos;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@ComponentScan(basePackages = { "com.example.photo.services" })
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/photo")
public class PhotoController {


    @GetMapping("")
    public String getItems() {
        DynamoDBService dbService2 = new DynamoDBService();
        Map map = dbService2.scanPhotoTable();
        Gson gson = new Gson();
        Map m = new TreeMap();
        m.put("labels", map);
        String json = gson.toJson(m);
        return json;
    }

    // Upload a video to analyze.
    @RequestMapping(value = "/upload2", method = RequestMethod.PUT)
    String uploadFile(HttpServletRequest request, HttpServletResponse response) {

        S3Service s3Service2 = new S3Service();
        try {
            String fileName = request.getParameter("filename");
            return s3Service2.signObjectToUpload(fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String singleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            UUID uuid = UUID.randomUUID();
            String unqueFileName = uuid + "-" + fileName;

            DynamoDBService dbService = new DynamoDBService();
            S3Service s3Service = new S3Service();
            AnalyzePhotos analyzePhotos = new AnalyzePhotos();
            UploadEndpoint endpoint = new UploadEndpoint(analyzePhotos, dbService, s3Service);
            endpoint.upload(bytes, fileName);
            return "You have uploaded "+fileName;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "File was not uploaded";
    }
}
