/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.springboot;

import com.example.photo.PhotoApplicationResources;
import com.example.photo.endpoints.UploadEndpoint;
import com.example.photo.WorkItem;
import com.example.photo.services.AnalyzePhotos;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ComponentScan(basePackages = {"com.example.photo.services"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/photo")
public class PhotoController {
    private final S3Service s3Service;
    private final DynamoDBService dbService;
    private final AnalyzePhotos analyzePhotos;
    private final UploadEndpoint uploadEndpoint;

    @Autowired
    public PhotoController(S3Service s3Service, DynamoDBService dbService, AnalyzePhotos analyzePhotos){
        this.s3Service = s3Service;
        this.dbService = dbService;
        this.analyzePhotos = analyzePhotos;
        this.uploadEndpoint = new UploadEndpoint(this.analyzePhotos, this.dbService, this.s3Service);
    }

    @GetMapping("" )
    public List<WorkItem> getItems() {
        return dbService.scanPhotoTable();
    }


    // Upload a video to analyze.
    @RequestMapping(value = "/upload2", method = RequestMethod.PUT)
    String uploadFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            String fileName = request.getParameter("filename");
            return s3Service.signObjectToUpload(fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    // Upload a video to analyze.
    // Upload a video to analyze.
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String singleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String fileName =  file.getOriginalFilename() ;
            UUID uuid = UUID.randomUUID();
            String unqueFileName = uuid +"-"+fileName;

            UploadEndpoint endpoint = new UploadEndpoint(analyzePhotos, dbService, s3Service);
            endpoint.upload(bytes,fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    // Copies all .jpg images from the specified bucket to Amazon S3 STORAGE_BUCKET.
    @RequestMapping(value = "/s3_copy", method = RequestMethod.PUT)
    @ResponseBody
    String copyFiles(HttpServletRequest request, HttpServletResponse response) {
       String sourceBucket = request.getParameter("source");
       int numFiles= uploadEndpoint.copyFiles(sourceBucket);
       return "You copied " +numFiles +" files from "+sourceBucket;
    }

    // This controller method downloads the given image from the Amazon S3 bucket.
    @RequestMapping(value = "/move", method = RequestMethod.POST)
    @ResponseBody
    String getPresignedZIP(HttpServletRequest request, HttpServletResponse response) {
        try {
            String tag = request.getParameter("tag");
            List<String> myObjects = dbService.getImagesTag(tag);
            Map<String, byte[]> mapReport = new HashMap<>();
            for (String obName:myObjects){
                System.out.println(obName);
                byte[] photoBytes = s3Service.getObjectBytes(PhotoApplicationResources.STORAGE_BUCKET, obName) ;
                mapReport.put(obName,photoBytes);

            }
            byte[] zipContent = s3Service.listBytesToZip(mapReport);
            String zipName = tag+".zip";
            return s3Service.putS3Object(PhotoApplicationResources.MANIFEST_BUCKET, zipName, zipContent);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
