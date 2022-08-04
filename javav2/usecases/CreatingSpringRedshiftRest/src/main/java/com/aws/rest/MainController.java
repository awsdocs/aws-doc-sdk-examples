/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping("items/{state}")
    public List< WorkItem > getItems(@PathVariable String state) {
        if (state.compareTo("active") == 0)
               return ri.getData(0) ;
        else
               return ri.getData(1) ;
    }

    // Flip an item from Active to Archive.
    @PutMapping("mod/{id}")
    public String modUser(@PathVariable String id) {
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

    @PutMapping("report/{email}")
    public String sendReport(@PathVariable String email){
        List<WorkItem> theList = ri.getData(0);
        java.io.InputStream is = writeExcel.exportExcel(theList);

        try {
            sm.sendReport(is, email);
        }catch (IOException e) {
            e.getStackTrace();
        }
        return "Report is created";
    }
}
