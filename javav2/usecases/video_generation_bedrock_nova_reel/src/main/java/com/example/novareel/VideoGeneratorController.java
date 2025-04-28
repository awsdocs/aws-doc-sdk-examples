// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.novareel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video")
public class VideoGeneratorController {

    @Autowired
    VideoGenerationService videoGenerationService;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenerateVideoResponse> generateVideo(@RequestParam String prompt) {
        return ResponseEntity.ok(videoGenerationService.generateVideo(prompt));
    }

    @PostMapping(value = "/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenerateVideoResponse> checkStatus(@RequestParam String invocationArn) {
        return ResponseEntity.ok(videoGenerationService.checkGenerationStatus(invocationArn));
    }

}
