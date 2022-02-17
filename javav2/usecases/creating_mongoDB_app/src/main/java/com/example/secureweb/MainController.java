/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.secureweb;

import com.example.entities.WorkItem;
import com.example.services.MongoDBService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import com.example.services.WriteExcel;
import com.example.services.SendMessages;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    MongoDBService dbService;

    @Autowired
    SendMessages sendMsg;

    @Autowired
    WriteExcel excel;

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/add")
    public String designer() {
        return "add";
    }

    @GetMapping("/items")
    public String items() {
        return "items";
    }

    // Adds a new item to the DynamoDB database.
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    String addItems(HttpServletRequest request, HttpServletResponse response) {

        // Get the logged-in user.
        String name = getLoggedUser();

        String guide = request.getParameter("guide");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        // Create a Work Item object to pass to the injectNewSubmission method.
        WorkItem myWork = new WorkItem();
        myWork.setGuide(guide);
        myWork.setDescription(description);
        myWork.setStatus(status);
        myWork.setName(name);

        dbService.putRecord(myWork);
        return "Item added";
    }

    // Builds and emails a report with all items.
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    String getReport(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter("email");
        List<WorkItem> theList = dbService.getListItemsReport();
        java.io.InputStream is = excel.exportExcel(theList);

        try {
            sendMsg.sendReport(is, email);
        }catch (Exception e) {
            e.getStackTrace();
        }
        return "Report is created";
    }

    // Modifies the value of a work item..
    @RequestMapping(value = "/changewi", method = RequestMethod.POST)
    @ResponseBody
    String changeWorkItem(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        String status = request.getParameter("status");
        dbService.updateItemId(id, status);
        return id;
    }

    // Retrieve items.
    @RequestMapping(value = "/retrieve", method = RequestMethod.POST)
    @ResponseBody
    String retrieveItems(HttpServletRequest request, HttpServletResponse response) {

        String type = request.getParameter("type");

        // Retrieve items from MongoDB.
        String xml="";
        xml = dbService.getListItems();
        return xml;
    }

    // Returns a work item to modify.
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    String modifyWork(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        String xmlRes = dbService.findDocumentById(id) ;
        return xmlRes;
    }

    private String getLoggedUser() {

        // Get the logged-in user.
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) 			SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user2.getUsername();
        return name;
    }
}
