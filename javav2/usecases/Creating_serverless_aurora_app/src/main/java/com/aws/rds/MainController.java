/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class MainController {

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

    @Autowired
    InjectWorkService iw;

    @Autowired
    WriteExcel writeExcel;

    @Autowired
    SendMessage sm;

    // Adds a new item to the database.
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    String addItems(HttpServletRequest request, HttpServletResponse response) {

        //Get the Logged in User.
        String name = getLoggedUser();

        String guide = request.getParameter("guide");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        // Create a Work Item object to pass to the injestNewSubmission method.
        WorkItem myWork = new WorkItem();
        myWork.setGuide(guide);
        myWork.setDescription(description);
        myWork.setStatus(status);
        myWork.setName(name);

        iw.injestNewSubmission(myWork);
        return "Item added";
    }

    // Builds and emails a report.
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    String getReport(HttpServletRequest request, HttpServletResponse response) {

        // Get the logged in user.
        String name = getLoggedUser();
        String email = request.getParameter("email");
        RetrieveItems ri = new RetrieveItems();
        List<WorkItem> theList = ri.getItemsDataSQLReport(name);
        java.io.InputStream is = writeExcel.exportExcel(theList);

        try {
            sm.sendReport(is, email);

        }catch (IOException e) {
            e.getStackTrace();
        }
        return "Report is created";
    }

    // Archives a work item.
    @RequestMapping(value = "/archive", method = RequestMethod.POST)
    @ResponseBody
    String archieveWorkItem(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        RetrieveItems ri = new RetrieveItems();
        ri.flipItemArchive(id );
        return id ;
    }

    // Modifies the value of a work item.
    @RequestMapping(value = "/changewi", method = RequestMethod.POST)
    @ResponseBody
    String changeWorkItem(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String description = request.getParameter("description");
        String status = request.getParameter("status");
        String value = iw.modifySubmission(id, description, status);
        return value;
    }

    // Retrieve all items for a given user.
    @RequestMapping(value = "/retrieve", method = RequestMethod.POST)
    @ResponseBody
    String retrieveItems(HttpServletRequest request, HttpServletResponse response) {

        //Get the Logged in user.
        String name = getLoggedUser();
        String type = request.getParameter("type");

        //Pass back all data from the database.
        RetrieveItems ri = new RetrieveItems();
        String xml="";

        if (type.equals("active")) {
            xml = ri.getItemsDataSQL(name);
            return xml;
        } else {
            xml = ri.getArchiveData(name);
            return xml;
        }
    }

    // Returns a work item to modify.
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    String modifyWork(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        RetrieveItems ri = new RetrieveItems();
        String xmlRes = ri.getItemSQL(id) ;
        return xmlRes;
    }

    private String getLoggedUser() {

        // Get the logged-in user.
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user2.getUsername();
        return name;
    }
}
