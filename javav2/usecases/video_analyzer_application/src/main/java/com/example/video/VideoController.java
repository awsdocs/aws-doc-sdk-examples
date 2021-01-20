/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
public class VideoController {

    @Autowired
    S3Service s3Client;

    @Autowired
    WriteExcel excel ;

    @Autowired
    SendMessages sendMessage;

    @Autowired
    VideoDetectFaces detectFaces;

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/video")
    public String photo() {
        return "upload";
    }

    @GetMapping("/process")
    public String process() {
        return "process";
    }

    private String bucketName = "scottexamplevideo";


    @RequestMapping(value = "/getvideo", method = RequestMethod.GET)
    @ResponseBody
    String getImages(HttpServletRequest request, HttpServletResponse response) {

        return s3Client.ListAllObjects(bucketName);
    }

    // Upload a MP4 to an Amazon S3 bucket
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file) {

        try {
            byte[] bytes = file.getBytes();
            String name =  file.getOriginalFilename() ;

            // Put the MP4 file into an Amazon S3 bucket
            int yy = 0;
            s3Client.putObject(bytes, bucketName, name);
            // return "You have placed " +name + " into the S3 bucket";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView(new RedirectView("video"));
    }

    // generates a report after analyzing a video in an Amazon S3 bucket
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    String report(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter("email");
        String myKey = s3Client.getKeyName(bucketName);
        String jobNum = detectFaces.StartFaceDetection(bucketName, myKey);
        List<FaceItems> items = detectFaces.GetFaceResults(jobNum);

        InputStream excelData = excel.exportExcel(items);

        try {
            //email the report
            sendMessage.sendReport(excelData, email);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return "The "+ myKey +" video has been successfully analyzed and the report is sent to "+email;
    }
}
