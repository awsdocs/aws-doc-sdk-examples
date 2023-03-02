/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.springboot;

import com.example.photo.Job;
import com.example.photo.endpoints.RestoreEndpoint;
import com.example.photo.services.DynamoDBService;
import com.example.photo.services.S3Service;
import com.example.photo.services.SnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@ComponentScan(basePackages = {"com.aws.services"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/photo")
public class RestoreController {
    final RestoreEndpoint restoreEndpoint;

    @Autowired
    RestoreController(DynamoDBService dynamoDBService, S3Service s3Service, SnsService snsService) {
        this.restoreEndpoint = new RestoreEndpoint(dynamoDBService, s3Service, snsService);
    }

    @PostMapping("/restore")
    @ResponseBody
    public Job startRestore(@RequestBody CreateRestore createRestoreRequest) {
        String notify = createRestoreRequest.getNotify();
        List<String> tags = createRestoreRequest.getTags();
        return restoreEndpoint.restore(notify, tags);
    }

}

class CreateRestore {
    private List<String> tags = new ArrayList<>();
    private String notify = "";

    public CreateRestore() {}

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }
}

