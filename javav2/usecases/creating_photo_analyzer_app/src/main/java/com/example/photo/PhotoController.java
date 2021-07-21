/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
public class PhotoController {

    @Autowired
    S3Service s3Client;

    @Autowired
    AnalyzePhotos photos;

    @Autowired
    WriteExcel excel ;

    @Autowired
    SendMessages sendMessage;

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/process")
    public String process() {
        return "process";
    }

    @GetMapping("/photo")
    public String photo() {
        return "upload";
    }



    @RequestMapping(value = "/getimages", method = RequestMethod.GET)
    @ResponseBody
    String getImages(HttpServletRequest request, HttpServletResponse response) {

    return s3Client.ListAllObjects("scottphoto");
    }

    // generates a report that analyzes photos in a given bucket
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    String report(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter("email");

       // Get a list of key names in the given bucket
       List myKeys =  s3Client.ListBucketObjects("scottphoto");

       // Create a List to store the data
       List<List> myList = new ArrayList<List>();

       // loop through each element in the List
       int len = myKeys.size();
       for (int z=0 ; z < len; z++) {

           String key = (String) myKeys.get(z);
           byte[] keyData = s3Client.getObjectBytes ("scottphoto", key);
           //myMap.put(key, keyData);

           //Analyze the photo
          ArrayList item =  photos.DetectLabels(keyData, key);
          myList.add(item);
       }

       // Now we have a list of WorkItems that have all of the analytical data describing the photos in the S3 bucket
       InputStream excelData = excel.exportExcel(myList);

       try {
           //email the report
           sendMessage.sendReport(excelData, email);

       } catch (Exception e) {

           e.printStackTrace();
       }
        return "The photos have been analyzed and the report is sent";
    }

    //Upload a video to analyze
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file) {

        try {

            // Now i can add this to an S3 Bucket
            byte[] bytes = file.getBytes();
            String name =  file.getOriginalFilename() ;

           // Put the file into the bucket
            s3Client.putObject(bytes, "scottphoto", name);
       //     return "You have placed " +name + " into the S3 bucket";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView(new RedirectView("photo"));
    }


    // This controller method downloads the given image from the Amazon S3 bucket
    @RequestMapping(value = "/downloadphoto", method = RequestMethod.GET)
    void buildDynamicReportDownload(HttpServletRequest request, HttpServletResponse response) {
        try {

            //Get the form id from the submitted form
            String photoKey = request.getParameter("photoKey");
            byte[] photoBytes = s3Client.getObjectBytes("scottphoto", photoKey) ;
            InputStream is = new ByteArrayInputStream(photoBytes);

            //define the required information here
            response.setContentType("image/png");
            response.setHeader("Content-disposition", "attachment; filename="+photoKey);
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
