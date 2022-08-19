/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.aws.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/")
public class MainController {

    @Autowired
    DynamoDBService dbService;

    @Autowired
    SendMessage sendMsg;

    @Autowired
    WriteExcel excel;

    // Adds a new item to the Amazon DynamoDB database.
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    String addItems(HttpServletRequest request, HttpServletResponse response) {
        String guide = request.getParameter("guide");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        // Create a Work Item object.
        WorkItem myWork = new WorkItem();
        myWork.setGuide(guide);
        myWork.setDescription(description);
        myWork.setStatus(status);
        myWork.setName("User");

        dbService.setItem(myWork);
        return "Item added";
    }

    // Builds and emails a report with all items.
    @PutMapping("report/{email}")
    public String sendReport(@PathVariable String email){
        List<WorkItem> theList = dbService.getListItems();
        java.io.InputStream is = excel.exportExcel(theList);

        try {
            sendMsg.sendReport(is, email);

        }catch (IOException e) {
            e.getStackTrace();
        }
        return "Report is created";
    }

    // Archives a work item.
    @PutMapping("mod/{id}")
    public String modUser(@PathVariable String id) {
        dbService.archiveItemEC(id );
        return id ;
    }

    // Retrieve items based on state.
    @GetMapping("items/{state}")
    public List< WorkItem > getItems(@PathVariable String state) {
        if (state.compareTo("active") == 0)
            return dbService.getClosedItems();
        else
            return dbService.getOpenItems();
    }
}
