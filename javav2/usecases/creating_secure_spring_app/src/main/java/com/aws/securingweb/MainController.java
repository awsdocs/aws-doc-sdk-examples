package com.aws.securingweb;

import com.aws.entities.WorkItem;
import com.aws.jdbc.RetrieveItems;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.aws.jdbc.InjectWorkService;
import com.aws.services.WriteExcel;
import com.aws.services.SendMessages;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    // Adds a new item to the database
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    String addItems(HttpServletRequest request, HttpServletResponse response) {

        // Get the Logged in User
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user2.getUsername();

        String guide = request.getParameter("guide");
        String description = request.getParameter("description");
        String status = request.getParameter("status");

        InjectWorkService iw = new InjectWorkService();

        // Create a Work Item object to pass to the injestNewSubmission method
        WorkItem myWork = new WorkItem();
        myWork.SetGuide(guide);
        myWork.SetDescription(description);
        myWork.SetStatus(status);
        myWork.SetName(name);

        try {

            iw.injestNewSubmission(myWork);
        }
        catch (Exception e){
            e.getStackTrace();
        }
        return "Report is created";
    }

    // Builds and emails a report
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    String getReport(HttpServletRequest request, HttpServletResponse response) {

        // Get the Logged in User
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user2.getUsername();

        String email = request.getParameter("email");
        RetrieveItems ri = new RetrieveItems();
        List<WorkItem> theList =  ri.getItemsDataSQLReport(name);

        WriteExcel writeExcel = new WriteExcel();
        SendMessages sm = new SendMessages();
        java.io.InputStream is = writeExcel.exportExcel(theList);

        try {
           sm.SendReport(is, email);
        } catch (Exception e){
            e.getStackTrace();
        }
        return "Report is created";
    }

    // Archives a work item
    @RequestMapping(value = "/archive", method = RequestMethod.POST)
    @ResponseBody
    String ArchieveWorkItem(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");

        RetrieveItems ri = new RetrieveItems();
        ri.FlipItemArchive(id );
        return id ;
    }

    // Modifies the value of a work item
    @RequestMapping(value = "/changewi", method = RequestMethod.POST)
    @ResponseBody
    String ChangeWorkItem(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String description = request.getParameter("description");
        String status   = request.getParameter("status");

        InjectWorkService ws = new InjectWorkService();
        String value = ws.modifySubmission(id, description, status);
        return value;
    }

    // Retrieve all items for a given user
    @RequestMapping(value = "/retrieve", method = RequestMethod.POST)
    @ResponseBody
    String retrieveItems(HttpServletRequest request, HttpServletResponse response) {

        //Get the Logged in User
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user2.getUsername();

        RetrieveItems ri = new RetrieveItems();
        String type = request.getParameter("type");

        //Pass back all data from the database
        String xml="";

        if (type.equals("active")) {
            xml = ri.getItemsDataSQL(name);
            return xml;
        }
        else {
            xml = ri.getArchiveData(name);
            return xml;
        }
    }

    // Returns a work item to modify
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    @ResponseBody
    String modifyWork(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        RetrieveItems ri = new RetrieveItems();
        String xmlRes = ri.GetItemSQL(id) ;
        return  xmlRes;
     }

    private String getLoggedUser()
    {
        //Get the Logged in User
        org.springframework.security.core.userdetails.User user2 = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String name = user2.getUsername();
        return name;
    }
}
