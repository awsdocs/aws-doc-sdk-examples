# Creating a publish/subscription web application that translates messages

## Overview

| Heading      | Description |
| ----------- | ----------- |
| Description | Discusses how to develop a dynamic web MVC application that has subscription and publish functionality by using the AWS SDK for Java (v2).    |
| Audience   |  Developer (beginner / intermediate)        |
| Updated   | 5/10/2022        |
| Required skills   | Java, Maven  |

## Purpose

You can create a web application that has subscription and publish functionality by using Amazon Simple Notification Service (Amazon SNS). The application created in this AWS tutorial is a Spring Boot web application that lets a user subscribe to an Amazon SNS topic by entering a valid email address. A user can enter multiple email address values to subscribe them to the given SNS topic (after the email recipients confirm the subscription). The user can publish a message that results in all subscribed email addresses receiving the message. 

**Note**: Amazon SNS is a managed service that provides message delivery from publishers to subscribers (also known as producers and consumers). For more information, see [What is Amazon SNS?](https://docs.aws.amazon.com/sns/latest/dg/welcome.html)

#### Topics

+ Prerequisites
+ Understand the Publish/Subscription application
+ Create an IntelliJ project 
+ Add the POM dependencies to your project
+ Create the Java classes
+ Create the HTML files
+ Run the application
 
## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Java IDE (this tutorial uses the IntelliJ IDE)
+ Java JDK 1.8
+ Maven 3.6 or later

## Important

+ The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to delete all of the resources that you create during this tutorial so that you won't be charged.

### Creating the resources

Create an Amazon SNS queue that is used in the Java code. For information, see [Creating an Amazon SNS topic](https://docs.aws.amazon.com/sns/latest/dg/sns-create-topic.html). 

In addition, set up your development environment. For information, see [Setting up the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html).

## Understand the Publish/Subscription application

To subscribe to an Amazon SNS topic, the user enters a valid email address into the web application. 

![AWS Tracking Application](images/pic1.png)

The specified email address recieves an email message that lets the recipient confirm the subscription. 

![AWS Tracking Application](images/pic2.png)

After the email recipient accepts the confirmation, their email address is subscribed to the specific SNS topic and recieves published messages. To publish a message, a user enters the message into the web application and then chooses the **Publish** button. 

![AWS Tracking Application](images/pic3.png)

This application lets a user specify the language of the message that is sent. For example, the user can select **French** from the dropdown and then the message appears in that language to all subscribed users. 

![AWS Tracking Application](images/french.png)

**Note**: Amazon Translate is used to translate the body of the message. The code is shown later in this document. 

This example application lets you view all of the subscribed email recipients by choosing the **List Subscriptions** button, as shown in the following image.

![AWS Tracking Application](images/pic4.png)


## Create an IntelliJ project

Create an IntelliJ project that is used to create the web application.

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.

2. In the **New Project** dialog box, choose **Maven**.

3. Choose **Next**.

4. For **GroupId**, enter **spring-aws**.

5. For **ArtifactId**, enter **SpringSubscribeApp**.

6. Choose **Next**.

7. Choose **Finish**.

## Add the POM dependencies to your project

At this point, you have a new project named **SpringSubscribeApp**. Make sure that the pom.xml file resembles the following code.

```xml
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
        <version>2.6.1</version>
        <relativePath/>
     </parent>
     <properties>
        <java.version>1.8</java.version>
     </properties>
     <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>software.amazon.awssdk</groupId>
                <artifactId>bom</artifactId>
                <version>2.17.136</version>
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
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>translate</artifactId>
        </dependency>
      </dependencies>
     <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${project.parent.version}</version>
            </plugin>
        </plugins>
      </build>
   </project>
```  

 ## Create the Java classes
 
 Create a Java package in the main/java folder named **com.spring.sns**. The Java classes go into this package. 
 
 ![AWS Lex](images/project.png)
 
 Create the following Java classes:

+ **SubApplication** - Used as the base class for the Spring Boot application.
+ **SubController** - Used as the Spring Boot controller that handles HTTP requests. 
+ **SnsService** - Used to invoke Amazon SNS operations by using the Amazon SNS Java API V2.  

### SubApplication class

The following Java code represents the **SubApplication** class.

```java
     package com.spring.sns;

     import org.springframework.boot.SpringApplication;
     import org.springframework.boot.autoconfigure.SpringBootApplication;

     @SpringBootApplication
     public class SubApplication {

     public static void main(String[] args) {
        SpringApplication.run(SubApplication.class, args);
     }
    }
```

### SubController class

The following Java code represents the **SubController** class.

```java
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

     @RequestMapping(value = "/addEmail", method = RequestMethod.POST)
     @ResponseBody
     String addItems(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter("email");
        return sns.subEmail(email);
     }

     @RequestMapping(value = "/delSub", method = RequestMethod.POST)
     @ResponseBody
     String delSub(HttpServletRequest request, HttpServletResponse response) {

        String email = request.getParameter("email");
        sns.unSubEmail(email);
        return email +" was successfully deleted!";
     }

     @RequestMapping(value = "/addMessage", method = RequestMethod.POST)
     @ResponseBody
     String addMessage(HttpServletRequest request, HttpServletResponse response) {

        String body = request.getParameter("body");
        String lang = request.getParameter("lang");
        return sns.pubTopic(body,lang);
     }

     @RequestMapping(value = "/getSubs", method = RequestMethod.GET)
     @ResponseBody
     String getSubs(HttpServletRequest request, HttpServletResponse response) {

        String mySub = sns.getAllSubscriptions();
        return mySub;
     }
    }
```

### SnsService class

The following Java code represents the **SnsService** class. This class uses the [SnsClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/sns/SnsClient.html) to interact with Amazon SNS. For example, the **subEmail** method uses the email address to subscribe to the Amazon SNS topic. Likewise, the **unSubEmail** method unsubscibes from the Amazon SNS topic. The **pubTopic** publishes a message. 

```java
package com.spring.sns;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
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

    String topicArn = "<Enter Value>";

    private SnsClient getSnsClient() {

        Region region = Region.US_WEST_2;
        return SnsClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
                .build();
    }

    public String pubTopic(String message, String lang) {

        try {
            String body;
            Region region = Region.US_WEST_2;
            TranslateClient translateClient = TranslateClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(region)
                    .build();


            if (lang.compareTo("English")==0) {
                    body = message;

            } else if(lang.compareTo("French")==0) {

                    TranslateTextRequest textRequest = TranslateTextRequest.builder()
                            .sourceLanguageCode("en")
                            .targetLanguageCode("fr")
                            .text(message)
                            .build();

                    TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                    body = textResponse.translatedText();

            } else  {

                TranslateTextRequest textRequest = TranslateTextRequest.builder()
                        .sourceLanguageCode("en")
                        .targetLanguageCode("es")
                        .text(message)
                        .build();

                TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                body = textResponse.translatedText();
            }

            SnsClient snsClient = getSnsClient();
            PublishRequest request = PublishRequest.builder()
                    .message(body)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            return " Message sent in " +lang +". Status was " + result.sdkHttpResponse().statusCode();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "Error - msg not sent";
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

  // Return the subscription Amazon Resource Name (ARN) based on the given endpoint.
  private String getTopicArnValue(String endpoint){

        SnsClient snsClient =  getSnsClient();
        try {
            String subArn;
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

    // Create a subscription.
    public String subEmail(String email) {

       try {
            SnsClient snsClient = getSnsClient();
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

        List<String> subList = new ArrayList<>() ;

        try {
            SnsClient snsClient =  getSnsClient();
            ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                    .topicArn(topicArn)
                    .build();

            ListSubscriptionsByTopicResponse result = snsClient.listSubscriptionsByTopic(request);
            List<Subscription> allSubs = result.subscriptions();

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

                // Set email.
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

```

**Note:** Make sure that you assign the SNS topic ARN to the **topicArn** data member. Otherwise, your code does not work. 

## Create the HTML file

At this point, you have created all of the Java files required for this example application. Now, create HTML files that are required for the application's view. Under the resource folder, create a **templates** folder, and then create the following HTML files:

+ index.html
+ layout.html
+ sub.html

### index.html
The **index.html** file is the application's home view. 

```html
    <!DOCTYPE html>
    <html xmlns:th="https://www.thymeleaf.org">
    <head>
     <meta charset="utf-8" />
     <meta http-equiv="X-UA-Compatible" content="IE=edge" />
     <meta name="viewport" content="width=device-width, initial-scale=1" />
     <script th:src="|https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js|"></script>
     <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css|"/>
     <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
     <link rel="icon" href="../public/img/favicon.ico" th:href="@{/img/favicon.ico}" />

    <title>AWS job posting example</title>
    </head> 

     <body>
     <header th:replace="layout :: site-header"/>
     <div class="container">

     <h3>Welcome to the Amazon Simple Notification Service example app</h3>
     <p>Now is: <b th:text="${execInfo.now.time}"></b></p>
     <p>The Amazon Simple Notification Service example uses multiple AWS services and the Java V2 API. Perform the following steps:<p>
     <ol>
        <li>You can subscribe to an SNS topic by choosing the <i>Manage Subscriptions</i> menu item.</li>
        <li>Enter a valid email address and then choose <i>Subscribe</i>.</li>
        <li>The sample application subscribes to the endpoint by using the SNS Java API V2.</li>
        <li>You can view all the email addresses that have subscribed by choosing the <i>List Subscriptions</i> menu item.</li>
        <li>You can unSubscribe by entering the email address and choosing <i>UnSubscribe</i>. </li>
        <li>You can publish a message by entering a message and choosing <i>Publish</i>.
        <li>All subscribed email recipients will receive the published message.</li>
       </ol>
      <div>
     </body>
    </html>
```
	   	

### layout.html
The following code represents the **layout.html** file that represents the application's menu.

```html
      <!DOCTYPE html>
      <html xmlns:th="http://www.thymeleaf.org">
     <head th:fragment="site-head">
     <meta charset="UTF-8" />
     <link rel="icon" href="../public/img/favicon.ico" th:href="@{/img/favicon.ico}" />
     <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
     <meta th:include="this :: head" th:remove="tag"/>
    </head>
    <header th:fragment="site-header">
     <a href="#" style="color: white" th:href="@{/}">Home</a>
     <a href="#" style="color: white" th:href="@{/subscribe}">Manage Subscriptions</a>
     </header>
    </html>
```

### add.html
The **sub.html** file is the application's view that manages Amazon SNS subscriptions. 

```html
     <!DOCTYPE html>
     <html xmlns:th="https://www.thymeleaf.org" lang="">
    <head>
     <meta charset="UTF-8" />
     <title>Subscription</title>

     <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
     <script th:src="|https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js|"></script>
     <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css|"/>
     <script src="../public/js/contact_me.js" th:src="@{/js/contact_me.js}"></script>
      <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
    </head>
    <body>
    <header th:replace="layout :: site-header"/>
    <div class="container">
     <p>Now is: <b th:text="${execInfo.now.time}"></b></p>
     <div class="row">
         <div class="col">
            <h4>Enter an email address<h3>
                <input type="email" class="form-control" id="inputEmail1" aria-describedby="emailHelp" placeholder="Enter email">
                <div class="clearfix mt-40">

                    <!-- Button trigger modal -->
                    <button type="button"  onclick="subEmail() "class="btn btn-primary" >
                        Subscribe
                    </button>
                    <button type="button" class="btn btn-primary" onclick="getSubs()">
                        List Subscriptions
                    </button>
                    <button type="button" onclick="delSub()" class="btn btn-primary" >
                        UnSubscribe
                    </button>

                    <!-- Modal -->
                    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="exampleModalLongTitle">SNS email subscriptions</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
           </div>
         </div>
         <hr style="width:50%;text-align:left;margin-left:0">
         <h4>Enter a message to publish</h4>
        <div class="col-lg-12 mx-auto">
        <div class="control-group">
            <div class="form-group floating-label-form-group controls mb-0 pb-2">
                <textarea class="form-control" id="body" rows="5" placeholder="Body" required="required" data-validation-required-message="Please enter a description."></textarea>
                <p class="help-block text-danger"></p>
            </div>
        </div>
        <br>
        <div>
            <label for="lang">Select a language:</label>
            <select name="lang" id="lang">
                <option>English</option>
                <option>French</option>
                <option>Spanish</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary btn-xl" id="SendButton">Publish</button>
    </div>
    </div>
    </body>
    </html>
  ```
### Create the JS File

This application has a **contact_me.js** file that is used to send requests to the Spring Controller. Place this file in the **resources\public\js** folder. 

```javascript
    $(function() {
    $("#SendButton" ).click(function($e) {

        var body = $('#body').val();
        var lang = $('#lang option:selected').text();
        if (body == '' ){
            alert("Please enter text");
            return;
        }

        $.ajax('/addMessage', {
            type: 'POST',
            data: 'lang=' + lang+'&body=' + body,
            success: function (data, status, xhr) {

                alert(data)
                $('#body').val("");
            },
            error: function (jqXhr, textStatus, errorMessage) {
                $('p').append('Error' + errorMessage);
            }
        });
      } );
    } );
    
    function subEmail(){
     var mail = $('#inputEmail1').val();
     var result = validate(mail)
     if (result == false) {
        alert (mail + " is not valid. Please specify a valid email.");
        return;
     }

     $.ajax('/addEmail', {
        type: 'POST',
        data: 'email=' + mail,
        success: function (data, status, xhr) {
            alert("Subscription validation is "+data)
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
      });
     }

     function getSubs() {
      $.ajax('/getSubs', {
        type: 'GET', 
        success: function (data, status, xhr) {

            $('.modal-body').empty();
            var xml = data;
            $(xml).find('Sub').each(function ()  {

                var $field = $(this);
                var email = $field.find('email').text();

                // Append this data to the main list.
                $('.modal-body').append("<p><b>"+email+"</b></p>");
            });
            $("#myModal").modal();
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
       });
      }

     function delSub(event) {
       var mail = $('#inputEmail1').val();
       var result = validate(mail)

      if (result == false) {
       alert (mail + " is not valid. Please specify a valid email");
       return;
      }

     $.ajax('/delSub', {
        type: 'POST',  // http GET method
        data: 'email=' + mail,
        success: function (data, status, xhr) {

            alert("Subscription validation is "+data);
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
      });
    }

     function validateEmail(email) {
       const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
       return re.test(email);
      }

     function validate(email) {
      const $result = $("#result");

     if (validateEmail(email)) {
        return true ;
      } else {
        return false ;
     }
    }
 ```

## Run the application

Using the IntelliJ IDE, you can run your application. The first time you run the Spring Boot application, choose the run icon in the Spring Boot main class, as shown in the following image. 

![AWS Tracking Application](images/run.png)

**Note**: You can deploy this Spring Boot application by using AWS Elastic Beanstalk. For more information, see [Creating your first AWS Java web application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_first_project).

### Next steps
Congratulations! You have created a Spring Boot application that contains subscription and publish functionality. As stated at the beginning of this tutorial, be sure to delete all of the resources that you created during this tutorial so that you won't be charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).

