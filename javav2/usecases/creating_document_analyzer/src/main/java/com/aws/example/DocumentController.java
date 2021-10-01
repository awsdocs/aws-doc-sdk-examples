/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.aws.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import java.io.IOException;


@Controller
public class DocumentController {

    private String bucketName = "scottdocbucket";

    @Autowired
    S3Service s3Service;

    @Autowired
    TextractService textractService;


    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/process")
    public String process() {
        return "process";
    }


    @RequestMapping(value = "/getdocs", method = RequestMethod.GET)
    @ResponseBody
    String getDoc(HttpServletRequest request, HttpServletResponse response) {
        return s3Service.ListAllObjects(bucketName);
    }


    @RequestMapping(value = "/analyzeDoc", method = RequestMethod.POST)
    @ResponseBody
    String getImages(HttpServletRequest request, HttpServletResponse response) {

        String name = request.getParameter("name");

        // Get the byte[] from a PDF document in an Amazon S3 bucket.
        byte[] obBytes = s3Service.getObjectBytes(bucketName, name);

        // Analyzes the PDF document.
        String xmlResults = textractService.analyzeDoc(obBytes);
        return xmlResults ;
    }


    // Upload a Document to analyze.
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file) {

        try {

            byte[] bytes = file.getBytes();
            String name =  file.getOriginalFilename() ;

           // Put the posted PDF file into the bucket.
           s3Service.putObject(bytes, bucketName, name);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView(new RedirectView("process"));
    }
}
