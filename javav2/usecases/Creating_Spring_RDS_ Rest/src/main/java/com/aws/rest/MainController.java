/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/")
public class MainController {

    @Autowired
    RetrieveItems ri;

    @Autowired
    WriteExcel writeExcel;

    @Autowired
    SendMessage sm;

    @Autowired
    InjectWorkService iw;

    @GetMapping("items/active")
    public List< WorkItem > getActive() {
        return ri.getItemsDataSQLReport(0);
    }

    @GetMapping("items/archive")
    public List< WorkItem > getArchive() {
        return ri.getItemsDataSQLReport(1);
    }

    // Flip an item from Active to Archive.
    @PostMapping("mod")
    public String modUser(@RequestBody Map<String, Object> payLoad) {
        String id = (String)payLoad.get("id");
        ri.flipItemArchive(id);
        return id +" was archived";
    }

    // Adds a new item to the database.
    @PostMapping("add")
    String addItems(@RequestBody Map<String, Object> payLoad) {
        String name = "user";
        String guide = (String)payLoad.get("guide");
        String description = (String)payLoad.get("description");
        String status = (String)payLoad.get("status");

        // Create a Work Item object to pass to the injestNewSubmission method.
        WorkItem myWork = new WorkItem();
        myWork.setGuide(guide);
        myWork.setDescription(description);
        myWork.setStatus(status);
        myWork.setName(name);

        iw.injestNewSubmission(myWork);
        return "Item added";
    }

    @PostMapping("report")
    public String sendReport(@RequestBody Map<String, Object> payLoad){
        String email = (String)payLoad.get("email");
        List<WorkItem> theList = ri.getItemsDataSQLReport(0);
        java.io.InputStream is = writeExcel.exportExcel(theList);

        try {
            sm.sendReport(is, email);

        }catch (IOException e) {
            e.getStackTrace();
        }
        return "Report is created";
    }
}
