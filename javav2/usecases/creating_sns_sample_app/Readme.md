# Creating a Spring Boot Application that has publish/subscription functionality

You can create a web application that has subscription and publish functionality by using the Simple Notification Service. The application created in this AWS tutorial is a Spring Boot web application that lets a user subscribe to a SNS topic by entering a valid email address. In fact, a user can enter many emails and all the emails are subscribed to the given SNS topic. The user can publish a message that results in all emails that are subscribed receiving the published message. Therefore a published message can reach many subcribed email recipients. 

**Note**: Amazon SNS is a managed service that provides message delivery from publishers to subscribers (also known as producers and consumers). For more information, see [What is Amazon SNS?](https://docs.aws.amazon.com/sns/latest/dg/welcome.html).

To subscribe to a SNS topic, the user enters a valid email address into the web application. 

![AWS Tracking Application](images/pic1.png)

The email address that is entered into the web application recieves a confirmation email that lets the email recipient confirm the subscription. 

![AWS Tracking Application](images/pic2.png)

Once the email recipient accepts the confirmation, that email is subscribed to the specific SNS topic and recieves published messages. To publish as message, a user enter the messages into the web applicaiton and chooses the **Publish** button. 

![AWS Tracking Application](images/pic3.png)

This example application lets you view all of the subscribed email recipients by choosing the **List Subscriptions** button, as shown in the following illustration.

![AWS Tracking Application](images/pic4.png)

**Cost to complete:** The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).

**Note:** Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re not charged.

#### Topics

+ Prerequisites
+ Create an IntelliJ project 
+ Add the POM dependencies to your project
+ Set up the Java packages in your project
+ Create the Java classes
+ Create the HTML files
+ Package the application into a JAR file
+ Deploy the application to Elastic Beanstalk


## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Java IDE (this tutorial uses the IntelliJ IDE)
+ Java JDK 1.8
+ Maven 3.6 or later
+ An Amazon SNS topic that you use in the Java code. For information, see [Creating an Amazon SNS topic](https://docs.aws.amazon.com/sns/latest/dg/sns-create-topic.html). 

## Create an IntelliJ project named BlogAurora

Create an IntelliJ project that is used to create the web application.

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.

2. In the New Project dialog box, choose **Maven**.

3. Choose **Next**.

4. In **GroupId**, enter **spring-aws**.

5. In **ArtifactId**, enter **SpringSubscribeApp**.

6. Choose **Next**.

7. Choose **Finish**.

## Add the Spring POM dependencies to your project

At this point, you have a new project named **SpringSubscribeApp**. Ensure that the pom.xml file resembles the following code.

     <?xml version="1.0" encoding="UTF-8"?>
     <project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     <modelVersion>4.0.0</modelVersion>
     <groupId>org.example</groupId>
     <artifactId>SpringSubscribeApp</artifactId>
     <version>1.0-SNAPSHOT</version>
     <description>Demo project for Spring Boot that shows Pub/Sub functionality</description>
     <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.5.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
     </parent>
     <properties>
        <java.version>1.8</java.version>
     </properties>
     <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>software.amazon.awssdk</groupId>
                <artifactId>bom</artifactId>
                <version>2.16.29</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
     </dependencyManagement>
     <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>sns</artifactId>
        </dependency>
     </dependencies>
     <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
      </build>
    </project>
     
 ## Create the Java classes
 
 Create a Java package in the main/java folder named **com.spring.sns**. This Java classes go into this package. 
 
 ![AWS Lex](images/pic5.png)
 
 Create these Java classes:

+ **SubApplication** - Used as the base class for the Spring Boot application.
+ **SubController** - Used as the Spring Boot controller that handles HTTP requests. 
+ **SnsService** - Used to invoke AMazon SNS operation using the Amazon SNS Java API V2.  

### SubApplication class

The following Java code represents the **SubApplication** class.

     package com.spring.sns;

     import org.springframework.boot.SpringApplication;
     import org.springframework.boot.autoconfigure.SpringBootApplication;

     @SpringBootApplication
     public class SubApplication {

     public static void main(String[] args) {
        SpringApplication.run(SubApplication.class, args);
     }
    }

### SubController class

The following Java code represents the **SubController** class.

     package com.spring.sns;

     import org.springframework.beans.factory.annotation.Autowired;
     import org.springframework.stereotype.Controller;
     import org.springframework.web.bind.annotation.*;
     import javax.servlet.http.HttpServletRequest;
     import javax.servlet.http.HttpServletResponse;

     @Controller
     public class SubController {

     @Autowired
     SnsService sns;

     @GetMapping("/")
     public String root() {
        return "index";
     }


     @GetMapping("/subscribe")
     public String add() {
        return "sub";
     }

     // Adds a new item to the database.
     @RequestMapping(value = "/addEmail", method = RequestMethod.POST)
     @ResponseBody
     String addItems(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter("email");
        return sns.subEmail(email);
     }

     // Adds a new item to the database.
     @RequestMapping(value = "/delSub", method = RequestMethod.POST)
     @ResponseBody
     String delSub(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter("email");
        sns.unSubEmail(email);
        return email +" was successfully deleted!";
     }

     // Handles posting a message to all subscriptions.
     @RequestMapping(value = "/addMessage", method = RequestMethod.POST)
     @ResponseBody
     String addMessage(HttpServletRequest request, HttpServletResponse response) {

        String body = request.getParameter("body");
        sns.pubTopic(body);
        return "Message sent";
     }

     // Adds a new item to the database.
     @RequestMapping(value = "/getSubs", method = RequestMethod.GET)
     @ResponseBody
     String getSubs(HttpServletRequest request, HttpServletResponse response) {

        String mySub = sns.getAllSubscriptions();
        return mySub;
     }
    }

### SnsService class

The following Java code represents the **SnsService** class. This class uses the Java V2 SNS API to interact with Amazon SNS. For example, the **subEmail** method uses the email address to subscribe to the Amazon SNS topic. Likewise, the **unSubEmail** method unsubscibes to the Amazon SNS topic. The **pubTopic** publishes a message. 

     package com.spring.sns;

     import org.springframework.stereotype.Component;
     import org.w3c.dom.Document;
     import org.w3c.dom.Element;
     import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
     import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicRequest;
     import software.amazon.awssdk.regions.Region;
     import software.amazon.awssdk.services.sns.SnsClient;
     import software.amazon.awssdk.services.sns.model.*;
     import javax.xml.parsers.DocumentBuilder;
     import javax.xml.parsers.DocumentBuilderFactory;
     import javax.xml.parsers.ParserConfigurationException;
     import javax.xml.transform.Transformer;
     import javax.xml.transform.TransformerException;
     import javax.xml.transform.TransformerFactory;
     import javax.xml.transform.dom.DOMSource;
     import javax.xml.transform.stream.StreamResult;
     import java.io.StringWriter;
     import java.util.ArrayList;
     import java.util.List;

     @Component
     public class SnsService {

     String topicArn = "arn:aws:sns:us-west-2:814548047983:MyMailTopic";

     private SnsClient getSnsClient() {

        Region region = Region.US_WEST_2;
        SnsClient snsClient = SnsClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
                .build();

        return snsClient;
     }

    public void pubTopic(String message) {

        try {
            SnsClient snsClient =  getSnsClient();;
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
      }

      public void unSubEmail(String emailEndpoint) {

      try {

         String subscriptionArn = getTopicArnValue(emailEndpoint);
         SnsClient snsClient =  getSnsClient();

         UnsubscribeRequest request = UnsubscribeRequest.builder()
                 .subscriptionArn(subscriptionArn)
                 .build();

         snsClient.unsubscribe(request);

     } catch (SnsException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
      }
     }

     // Returns the Topic ARN based on the given endpoint
     private String getTopicArnValue(String endpoint){

        SnsClient snsClient =  getSnsClient();
        try {
            String subArn = "";
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                    .topicArn(topicArn)
                    .build();


            ListSubscriptionsByTopicResponse result = snsClient.listSubscriptionsByTopic(request);
            List<Subscription> allSubs  = result.subscriptions();

            for (Subscription sub: allSubs) {

            if (sub.endpoint().compareTo(endpoint)==0) {

                subArn = sub.subscriptionArn();
                return subArn;
             }
           }
          } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
          }
         return "";
        }

       // Create a Subsciption of the given email address.
       public String subEmail(String email) {

       try {
            SnsClient snsClient =  getSnsClient();
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(email)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);
            return result.subscriptionArn() ;

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
      }

     public String getAllSubscriptions() {
        List subList = new ArrayList<String>() ;

        try {
            SnsClient snsClient =  getSnsClient();
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                    .topicArn(topicArn)
                    .build();

            ListSubscriptionsByTopicResponse result = snsClient.listSubscriptionsByTopic(request);
            List<Subscription> allSubs  = result.subscriptions();

            for (Subscription sub: allSubs) {
                subList.add(sub.endpoint());
            }

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return convertToString(toXml(subList));
      }

      // Convert the list to XML to pass back to the view.
      private Document toXml(List<String> subsList) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement("Subs");
            doc.appendChild(root);

            // Iterate through the collection.
            for (String sub : subsList) {

                Element item = doc.createElement("Sub");
                root.appendChild(item);

                // Set email
                Element email = doc.createElement("email");
                email.appendChild(doc.createTextNode(sub));
                item.appendChild(email);
            }

             return doc;
 
          }catch(ParserConfigurationException e){
            e.printStackTrace();
          }
         return null;
        }

       private String convertToString(Document xml) {
         try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch(TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
       }
     }


**Note:** Make sure that you assign the SNS topic to the **topicArn** data member. Otherwise, your code does not work. 

## Create the HTML file

At this point, you have created all of the Java files required for this example application. Now create HTML files that are required for the application's view. Under the resource folder, create a **templates** folder, and then create the following HTML files:

+ index.html
+ layout.html
+ post.html
+ add.html
+ login.html

### index.html
The **index.html** file is the application's home view. 

    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

    <head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css|"/>
    <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
    <link rel="icon" href="../public/img/favicon.ico" th:href="@{/img/favicon.ico}" />

    <title>AWS Job Posting Example</title>
    </head>

    <body>
    <header th:replace="layout :: site-header"/>
    <div class="container">

    <h3>Welcome <span sec:authentication="principal.username">User</span> to the Amazon Redshift Job Posting example app</h3>
    <p>Now is: <b th:text="${execInfo.now.time}"></b></p>

    <h2>Amazon Redshift Job Posting Example</h2>

    <p>The Amazon Redshift Job Posting Example application uses multiple AWS Services and the Java V2 API. Perform these steps:<p>

    <ol>
        <li>Enter work items into the system by choosing the <i>Add Posts</i> menu item. Fill in the form and then choose <i>Create Item</i>.</li>
        <li>The sample application stores the data by using the Amazon Redshift Java API V2.</li>
        <li>You can view the items by choosing the <i>Get Posts</i> menu item. Next, select a language.</li>
        <li>You can view the items by chooing either the <b>Five Posts</b>, <b>Ten Posts</b>, or <b>All Posts</b> button. </li>
        <li>The items appear in the page from newest to oldest.</li>
    </ol>
    <div>
    </body>
    </html>

### layout.html
The following code represents the **layout.html** file that represents the application's menu.

     <!DOCTYPE html>
     <html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
     <head th:fragment="site-head">
     <meta charset="UTF-8" />
     <link rel="icon" href="../public/img/favicon.ico" th:href="@{/img/favicon.ico}" />
     <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
     <meta th:include="this :: head" th:remove="tag"/>
    </head>
    <body>
     <!-- th:hef calls a controller method - which returns the view -->
     <header th:fragment="site-header">
     <a href="index.html" th:href="@{/}"><img src="../public/img/site-logo.png" th:src="@{/img/site-logo.png}" /></a>
     <a href="#" style="color: white" th:href="@{/}">Home</a>
     <a href="#" style="color: white" th:href="@{/add}">Add Post</a>
     <a href="#"  style="color: white" th:href="@{/posts}">Get Posts</a>
     <div id="logged-in-info">

        <form method="post" th:action="@{/logout}">
            <input type="submit"  value="Logout"/>
        </form>
    </div>
    </header>
    <h1>Welcome</h1>
    <body>
    <p>Welcome to  AWS Blog application.</p>
    </body>
    </html>

### add.html
The **add.html** file is the application's view that lets users post new items. 

      <!DOCTYPE html>
      <html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

     <head>
     <meta charset="UTF-8" />
     <title>Blog</title>

     <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
     <script src="../public/js/contact_me.js" th:src="@{/js/contact_me.js}"></script>
     <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css|"/>
     <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
     <link rel="icon" href="../public/img/favicon.ico" th:href="@{/img/favicon.ico}" />
     </head>

     <body>
     <header th:replace="layout :: site-header"/>
     <div class="container">
     <h3>Welcome <span sec:authentication="principal.username">User</span> to the Amazon Redshift Job Posting example app</h3>
     <p>Now is: <b th:text="${execInfo.now.time}"></b></p>
     <p>Add a new job posting by filling in this table and clicking <i>Create Item</i></p>

     <div class="row">
        <div class="col-lg-8 mx-auto">
                <div class="control-group">
                    <div class="form-group floating-label-form-group controls mb-0 pb-2">
                        <label>Title</label>
                        <input class="form-control" id="title" placeholder="Title" required="required" data-validation-required-message="Please enter the AWS Guide.">
                        <p class="help-block text-danger"></p>
                    </div>
                </div>
                <div class="control-group">
                    <div class="form-group floating-label-form-group controls mb-0 pb-2">
                        <label>Body</label>
                        <textarea class="form-control" id="body" rows="5" placeholder="Body" required="required" data-validation-required-message="Please enter a description."></textarea>
                        <p class="help-block text-danger"></p>
                    </div>
                </div>
                <br>
                <button type="submit" class="btn btn-primary btn-xl" id="SendButton">Create Item</button>
            </div>
       </div>
       </div>
      </body>
     </html>

### post.html
The **post.html** file is the application's view that displays the items in the specific language. 

    <!DOCTYPE html>
     <html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

     <head>
     <meta charset="UTF-8" />
     <title>Blog</title>

     <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
     <script th:src="|https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js|"></script>
     <script th:src="|https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js|"></script>
     <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css|"/>
     <script src="../public/js/contact_me.js" th:src="@{/js/contact_me.js}"></script>
     <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
     <link rel="icon" href="../public/img/favicon.ico" th:href="@{/img/favicon.ico}" />
     </head>

     <body>
     <header th:replace="layout :: site-header"/>

    <div class="container">
     <h3>Welcome <span sec:authentication="principal.username">User</span> to the Amazon Redshift Job Posting example app</h3>
     <p>Now is: <b th:text="${execInfo.now.time}"></b></p>

     <div id= "progress" class="progress">
        <div class="progress-bar progress-bar-striped progress-bar-animated" role="progressbar" aria-valuenow="75" aria-valuemin="0" aria-valuemax="100" style="width: 75%"></div>
     </div>

    <div class="row">
        <div class="col">
            <div class="col-lg-10">
                <div class="clearfix mt-40">
                    <ul class="xsearch-items">
                    </ul>
                </div>
            </div>
        </div>
        <div class="col-4">
            <label for="lang">Select a Language:</label>
            <select name="lang" id="lang">
                <option>English</option>
                <option>French</option>
                <option>Spanish</option>
                <option>Russian</option>
                <option>Chinese</option>
                <option>Japanese</option>
            </select>
        </div>
        <div>
            <button type="button" onclick="getPosts(5)">Five Posts</button>
            <button type="button" onclick="getPosts(10)">Ten Posts</button>
            <button type="button" onclick="getPosts(0)">All Posts</button>
        </div>
     </div>
     </div>
     </div>
     </body>
    </html>

### login.html
The **login.html** file is the application's login page. 

     <!DOCTYPE html>
     <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org">

    <head>
     <title>AWS Blog Example</title>
     <style>
        body {font-family: Arial, Helvetica, sans-serif;}
        form {border: 3px solid #f1f1f1;}

        input[type=text], input[type=password] {
            width: 100%;
            padding: 12px 20px;
            margin: 8px 0;
            display: inline-block;
            border: 1px solid #ccc;
            box-sizing: border-box;
        }

        button {
            background-color: #4CAF50;
            color: white;
            padding: 14px 20px;
            margin: 8px 0;
            border: none;
            cursor: pointer;
            width: 100%;
        }

        button:hover {
            opacity: 0.8;
        }

        .cancelbtn {
            width: auto;
            padding: 10px 18px;
            background-color: #f44336;
        }

        .imgcontainer {
            text-align: center;
            margin: 24px 0 12px 0;
        }

        img.avatar {
            width: 40%;
            border-radius: 50%;
        }

        .container {
            padding: 16px;
        }

        span.psw {
            float: right;
            padding-top: 16px;
        }

        /* Change styles for span and cancel button on extra small screens */
        @media screen and (max-width: 300px) {
            span.psw {
                display: block;
                float: none;
            }
            .cancelbtn {
                width: 100%;
            }
        }
    </style>
    </head>
    <body>
    <div th:if="${param.error}">
     Invalid username and password.
    </div>
    <div th:if="${param.logout}">
     You have been logged out.
    </div>
    <form th:action="@{/login}" method="post">
     <div class="container">
        <label for="username"><b>Username</b></label>
        <input type="text" placeholder="Enter Username" id="username" name="username" value ="user" required>

        <label for="password"><b>Password</b></label>
        <input type="password" placeholder="Enter Password" id ="password" name="password" value ="password" required>

        <button type="submit">Login</button>

     </div>

     <div class="container" style="background-color:#f1f1f1">
        <button type="button" class="cancelbtn">Cancel</button>
        <span class="psw">Forgot <a href="#">password?</a></span>
      </div>
    </form>
    </body>
    </html>
    
### Create the JS File

This application has a **contact_me.js** file that is used to send requests to the Spring Controller. Place this file in the **resources\public\js** folder. 

      $(function() {

       $('#progress').hide();

        $("#SendButton" ).click(function($e) {

         var title = $('#title').val();
         var body = $('#body').val();
        
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", loadNewItems, false);
        xhr.open("POST", "../addPost", true);   //buildFormit -- a Spring MVC controller
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
        xhr.send("title=" + title + "&body=" + body);
        } );// END of the Send button click

       function loadNewItems(event) {
        var msg = event.target.responseText;
        alert("You have successfully added item "+msg)

        $('#title').val("");
        $('#body').val("");
        }
       } );

    function getPosts(num){

     $('.xsearch-items').empty()
     $('#progress').show();
     var lang = $('#lang option:selected').text();
    
    var xhr = new XMLHttpRequest();
    xhr.addEventListener("load", loadItems, false);
    xhr.open("POST", "../getPosts", true);   //buildFormit -- a Spring MVC controller
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");//necessary
    xhr.send("lang=" + lang +"&number=" + num );
    }

    function loadItems(event) {

    $('#progress').hide();
    var xml = event.target.responseText;
    $(xml).find('Item').each(function ()  {

        var $field = $(this);
        var id = $field.find('Id').text();
        var date = $field.find('Date').text();
        var title = $field.find('Title').text();
        var body = $field.find('Content').text();
        var author = $field.find('Author').text();
        
        // Append this data to the main list.
        $('.xsearch-items').append("<className='search-item'>");
        $('.xsearch-items').append("<div class='search-item-content'>");
        $('.xsearch-items').append("<h3 class='search-item-caption'><a href='#'>"+title+"</a></h3>");
        $('.xsearch-items').append("<className='search-item-meta mb-15'>");
        $('.xsearch-items').append("<className='list-inline'>");
        $('.xsearch-items').append("<p><b>"+date+"</b></p>");
        $('.xsearch-items').append("<p><b>'Posted by "+author+"</b></p>");
        $('.xsearch-items').append("<div>");
        $('.xsearch-items').append("<h6>"+body +"</h6>");
        $('.xsearch-items').append("</div>");
       });
      }

## Create a JAR file for the application

Package up the project into a .jar (JAR) file that you can deploy to Elastic Beanstalk by using the following Maven command.

	mvn package

The JAR file is located in the target folder.

![AWS Tracking Application](images/Target.png)

The POM file contains the **spring-boot-maven-plugin** that builds an executable JAR file that includes the dependencies. Without the dependencies, the application does not run on Elastic Beanstalk. For more information, see [Spring Boot Maven Plugin](https://www.baeldung.com/executable-jar-with-maven).

## Deploy the application to Elastic Beanstalk

Sign in to the AWS Management Console, and then open the Elastic Beanstalk console. An application is the top-level container in Elastic Beanstalk that contains one or more application environments (for example prod, qa, and dev, or prod-web, prod-worker, qa-web, qa-worker).

If this is your first time accessing this service, you will see a **Welcome to AWS Elastic Beanstalk** page. Otherwise, you’ll see the Elastic Beanstalk Dashboard, which lists all of your applications.

#### To deploy the application to Elastic Beanstalk

1. Open the Elastic Beanstalk console at https://console.aws.amazon.com/elasticbeanstalk/home.
2. In the navigation pane, choose  **Applications**, and then choose **Create a new application**. This opens a wizard that creates your application and launches an appropriate environment.
3. On the **Create New Application** page, enter the following values:
   + **Application Name** - Redshift App
   + **Description** - A description for the application
4. Choose **Create**.
5. Choose **Create a new environment**.
6. Choose **Web server environment**.
7. Choose **Select**.
8. In the **Environment information** section, leave the default values.
9. In the **Platform** section, choose **Managed platform**.
10. For **Platform**, choose **Java** (accept the default values for the other fields).
11. In the **Application code** section, choose **Upload your code**.
12. Choose **Local file**, and then select **Choose file**. Browse to the JAR file that you created.  
13. Choose **Create environment**. You'll see the application being created. When you’re done, you will see the application state the **Health** is **Ok** .
14. To change the port that Spring Boot listens on, add an environment variable named **SERVER_PORT**, with the value **5000**.
11. Add a variable named **AWS_ACCESS_KEY_ID**, and then specify your access key value.
12. Add a variable named **AWS_SECRET_ACCESS_KEY**, and then specify your secret key value. After the variables are configured, you'll see the URL for accessing the application.

![AWS Tracking Application](images/Beanstalk.png)

**Note:** If you don't know how to set variables, see [Environment properties and other software settings](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environments-cfg-softwaresettings.html).

To access the application, open your browser and enter the URL for your application. You will see the Home page for your application.

### Next steps
Congratulations! You have created a Spring Boot application that uses the Amazon Redshift data client to create an example job posting application. As stated at the beginning of this tutorial, be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re not charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).

