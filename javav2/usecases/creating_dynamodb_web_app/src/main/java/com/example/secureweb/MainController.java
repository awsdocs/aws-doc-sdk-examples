package com.example.secureweb;

import com.example.entities.WorkItem;
import com.example.services.DynamoDBService;
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

    // Adds a new item to the DynamoDB database
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    String addItems(HttpServletRequest request, HttpServletResponse response) {

        //Get the Logged in User
        String name = getLoggedUser();

        String guide = request.getParameter("guide");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        // Add the data to the DynamoDB table
        DynamoDBService iw = new DynamoDBService();

        // Create a Work Item object to pass to the injestNewSubmission method
        WorkItem myWork = new WorkItem();
        myWork.setGuide(guide);
        myWork.setDescription(description);
        myWork.setStatus(status);
        myWork.setName(name);

        iw.setItem(myWork);
        return "Item added";
    }

    // Builds and emails a report
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    String getReport(HttpServletRequest request, HttpServletResponse response) {

        //Get the Logged in User
        String name = getLoggedUser();

        DynamoDBService ri = new DynamoDBService();

        String email = request.getParameter("email");
        DynamoDBService iw = new DynamoDBService();

        List<WorkItem> theList = iw.getListItems();
        WriteExcel writeExcel = new WriteExcel();
        SendMessages sm = new SendMessages();
        java.io.InputStream is = writeExcel.exportExcel(theList);

        try {
            sm.sendReport(is, email);
        }catch (IOException e) {
           e.getStackTrace();
       }
        return "Report is created";
    }

    // Archives a work item
    @RequestMapping(value = "/archive", method = RequestMethod.POST)
    @ResponseBody
    String archieveWorkItem(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        DynamoDBService dbService = new DynamoDBService();
        dbService.archiveItem(id );
        return id ;
    }

    // Modifies the value of a work item
    @RequestMapping(value = "/changewi", method = RequestMethod.POST)
    @ResponseBody
    String changeWorkItem(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        String status = request.getParameter("status");

        DynamoDBService dbService = new DynamoDBService();
        dbService.UpdateItem(id, status);
        return id;
    }

    // Retrieve items
    @RequestMapping(value = "/retrieve", method = RequestMethod.POST)
    @ResponseBody
    String retrieveItems(HttpServletRequest request, HttpServletResponse response) {

        //Get the Logged in User
        String name = getLoggedUser();
        String type = request.getParameter("type");

        //Pass back items from the DynamoDB table
        String xml="";
        DynamoDBService iw = new DynamoDBService();

         if (type.compareTo("archive") ==0)
            xml = iw.getClosedItems();
         else
            xml = iw.getOpenItems();

         return xml;
    }


    // Returns a work item to modify
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    String modifyWork(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        DynamoDBService dbService = new DynamoDBService();
        String xmlRes = dbService.getItem(id) ;
        return xmlRes;
    }

    private String getLoggedUser() {

        // Get the logged-in Useruser
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) 			SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user2.getUsername();
        return name;
    }
}
