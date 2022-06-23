/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.blog;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class BlogController {

    @Autowired
    RetrieveDataRDS rdsData;

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/add")
    public String add() {
        return "add";
    }

    @GetMapping("/posts")
    public String post() {
        return "post";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    // Adds a new item to the database.
    @RequestMapping(value = "/addPost", method = RequestMethod.POST)
    @ResponseBody
    String addItems(HttpServletRequest request, HttpServletResponse response) {

        String name = getLoggedUser();
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        return rdsData.addRecord(name, title, body);
    }


    // Queries items from the Redshift database.
    @RequestMapping(value = "/getPosts", method = RequestMethod.POST)
    @ResponseBody
    String getPosts(HttpServletRequest request, HttpServletResponse response) {

        String num = request.getParameter("number");
        String lang = request.getParameter("lang");
        return rdsData.getPosts(lang,Integer.parseInt(num));
    }

    private String getLoggedUser() {

        // Get the logged-in user.
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user2.getUsername();
    }
}
