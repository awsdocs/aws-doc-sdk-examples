# Building an Amazon Lex Chatbot that engages users in multiple languages

You can create an Amazon Lex bot within a web application to engage your web site visitors. An Amazon Lex bot is functionality that performs on-line chat conversation with users without providing direct contact with a person. For example, the following illustration shows an Amazon Lex bot that engages a user about a hotel room. 

![AWS Video Analyzer](images/pic1.png)

The Amazon Lex Chatbot created in this AWS tutorial is able to handle multiple languages. For example, a user who speaks French can enter French text and get back a response in French. 

![AWS Video Analyzer](images/LanChatBot2.png)

Likewise, a user can communicate with the Amazon Lex chatbot in Italian.

![AWS Video Analyzer](images/LanChatBot3.png)

This AWS tutorial guides you through creating an Amazon Lex box and integrating it into a Spring Boot web application. The AWS SDK for Java (version 2) is used to invoke these  AWS services:

+ Amazon Lex
+ Amazon Comprehend
+ Amazon Translate

**Cost to complete:** The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).

**Note:** Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re no longer charged for them.

#### Topics

+ Prerequisites
+ Create an IntelliJ project named Greetings
+ Add the Spring POM dependencies to your project
+ Set up the Java packages in your project
+ Create the Java logic for the main Boot class
+ Create the HTML files
+ Package the Greetings application into a JAR file


## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Java IDE (this tutorial uses the IntelliJ IDE)
+ Java JDK 1.8
+ Maven 3.6 or later

## Create an Amazon Lex bot

The first step is to create an Amazon Lex bot by using the AWS Management Console. In this example, the Amazon Lex **BookTrip** example is used.

1. Sign in to the AWS Management Console and open the Amazon Lex console at https://console.aws.amazon.com/lex/.

2. On the Bots page, choose **Create**.

3. Choose **BookTrip** blueprint (leave the default bot name **BookTrip**).

![AWS Video Analyzer](images/pic2.png)

4. Choose **Create** (the console shows the **BookTrip** bot). On the Editor tab, review the details of the preconfigured intents (BookCar and BookHotel).

5. Test the bot in the test window. Start the test by typing *I want to book a hotel*. 

**Note**: For more information about the Book Trip example, see [Book Trip](https://docs.aws.amazon.com/lex/latest/dg/ex-book-trip.html).

## Create an Amazon Cognito identity pool

You can use Amazon Cognito to manage permissions for a web application by creating an identity pool. An Amazon Cognito identity pool (federated identities) enables you to create unique identities for your users and federate them with identity providers.

1. Sign in to the AWS Management Console and open the Cognito console at https://console.aws.amazon.com/cognito.

2. Choose **Manage new identity pool**.

3. Choose **Create new identity pool**.

4. Specify a pool name (**examplepool**) and then choose **Enable access to unauthenticated identities**.

![AWS Lex](images/pic3.png)

5. Choose **Create Pool**.

6. Expand the **Hide Details** section. 

7. Note the AWS Identity and Access Management (IAM) name specified in the **Role Name** field (you need to provide additional permissions to this role). 

![AWS Lex](images/pic4.png)

8. Choose **Allow**. 

9. Note the **Identity pool ID** value (this value is specified in the **index.html** file created later in this tutorial).

![AWS Lex](images/pic5.png)

## Add permissions to the IAM roles

You must provide the IAM role that you noted in the previous section with these permissions: 
+ 	AmazonLexRunBotsOnly
+ 	AmazonPollyReadOnlyAccess

## Create an IntelliJ project named SpringChatbot

Create an IntelliJ project that is used to create a web site that uses the Amazon Lex bot.

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.

2. In the New Project dialog box, choose **Maven**.

3. Choose **Next**.

4. In **GroupId**, enter **spring-aws**.

5. In **ArtifactId**, enter **SpringChatbot**.

6.	Choose **Next**.

7.	Choose **Finish**.

## Add the Spring POM dependencies to your project

At this point, you have a new project named SpringChatbot.

![AWS Lex](images/pic6.png)

Ensure that the pom.xml file resembles the following code.

     <?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     <modelVersion>4.0.0</modelVersion>
     <groupId>SpringChatbot</groupId>
     <artifactId>SpringChatbot</artifactId>
     <version>1.0-SNAPSHOT</version>
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
                <version>2.10.54</version>
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
 
 Create a Java package in the main/java folder named **com.aws.spring**.
 
 ![AWS Lex](images/pic7.png)
 
 The Java files go into this package.
 
  ![AWS Lex](images/pic8.png)
 
 Create these Java classes:

+ **BotExample** - Used as the base class for the Spring Boot application..
+ **BotController** - Used as the Spring Boot controller that handles HTTP requests.

### BotExample class

The following Java code represents the **BotExample** class.

     package com.aws.spring;

     import org.springframework.stereotype.Controller;
     import org.springframework.ui.Model;
     import org.springframework.web.bind.annotation.GetMapping;

     @Controller
     public class BotController {

     @GetMapping("/")
     public String greetingForm(Model model) {
        return "index";
     }
    }


### BotController class

The following Java code represents the **BotController** class.

     package com.aws.spring;

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;

    @Controller
    public class BotController {

     @GetMapping("/")
     public String greetingForm(Model model) {
        return "index";
     }
   }

## Create the HTML file

At this point, you have created all of the Java files required for this example Spring Boot application. Now you create a HTML file that are required for the application's view. Under the resource folder, create a **templates** folder, and then create the following HTML file:

+ index.html

The **index.html** file is the application's home view that displays the Amazon Lex bot. The following HTML represents the **index.html** file. In the following code, ensure that you specify your **IdentityPoolId** value and bot alias value. 

     <!DOCTYPE html>
     <html>

     <head>
     <title>Amazon Lex for JavaScript - Sample Application (BookTrip)</title>
     <script src="https://sdk.amazonaws.com/js/aws-sdk-2.41.0.min.js"></script>
     <style language="text/css">
        input#wisdom {
            padding: 4px;
            font-size: 1em;
            width: 400px
        }

        input::placeholder {
            color: #ccc;
            font-style: italic;
        }

        p.userRequest {
            margin: 4px;
            padding: 4px 10px 4px 10px;
            border-radius: 4px;
            min-width: 50%;
            max-width: 85%;
            float: left;
            background-color: #7d7;
        }

        p.lexResponse {
            margin: 4px;
            padding: 4px 10px 4px 10px;
            border-radius: 4px;
            text-align: right;
            min-width: 50%;
            max-width: 85%;
            float: right;
            background-color: #bbf;
            font-style: italic;
         }

         p.lexError {
            margin: 4px;
            padding: 4px 10px 4px 10px;
            border-radius: 4px;
            text-align: right;
            min-width: 50%;
            max-width: 85%;
            float: right;
            background-color: #f77;
        }
       </style>
       </head>

       <body>
        <h1 style="text-align:  left">Amazon Lex - BookTrip</h1>
        <p style="width: 400px">
         This little chatbot shows how easy it is to incorporate
         <a href="https://aws.amazon.com/lex/" title="Amazon Lex (product)" target="_new">Amazon Lex</a> into your web pages.  Try it out.
         </p>
         <div id="conversation" style="width: 400px; height: 400px; border: 1px solid #ccc; background-color: #eee; padding: 4px; overflow: scroll"></div>
         <form id="chatform" style="margin-top: 10px" onsubmit="return pushChat();">
         <input type="text" id="wisdom" size="80" value="" placeholder="I need a hotel room">
         </form>

      <script type="text/javascript">
       // set the focus to the input box
        document.getElementById("wisdom").focus();

       // Initialize the Amazon Cognito credentials provider
        AWS.config.region = 'us-east-1'; // Region
         AWS.config.credentials = new AWS.CognitoIdentityCredentials({
        
        // Provide your Pool Id here
        IdentityPoolId: '<IdentityPoolId>',
        });

      var lexruntime = new AWS.LexRuntime();
      var lexUserId = 'chatbot-demo' + Date.now();
      var sessionAttributes = {};

      function pushChat() {

        // if there is text to be sent...
        var wisdomText = document.getElementById('wisdom');
        if (wisdomText && wisdomText.value && wisdomText.value.trim().length > 0) {

            // disable input to show we're sending it
            var wisdom = wisdomText.value.trim();
            wisdomText.value = '...';
            wisdomText.locked = true;

            // send it to the Lex runtime
            var params = {
                botAlias: '<Bot alias>',
                botName: 'BookTrip',
                inputText: wisdom,
                userId: lexUserId,
                sessionAttributes: sessionAttributes
            };
            showRequest(wisdom);
            lexruntime.postText(params, function(err, data) {
                if (err) {
                    console.log(err, err.stack);
                    showError('Error:  ' + err.message + ' (see console for details)')
                }
                if (data) {
                    // capture the sessionAttributes for the next cycle
                    sessionAttributes = data.sessionAttributes;
                    // show response and/or error/dialog status
                    showResponse(data);
                }
                // re-enable input
                wisdomText.value = '';
                wisdomText.locked = false;
              });
              }
             // we always cancel form submission
             return false;
            }

    
        function showRequest(daText) {

         var conversationDiv = document.getElementById('conversation');
         var requestPara = document.createElement("P");
         requestPara.className = 'userRequest';
         requestPara.appendChild(document.createTextNode(daText));
         conversationDiv.appendChild(requestPara);
         conversationDiv.scrollTop = conversationDiv.scrollHeight;
         }

        function showError(daText) {

         var conversationDiv = document.getElementById('conversation');
         var errorPara = document.createElement("P");
         errorPara.className = 'lexError';
         errorPara.appendChild(document.createTextNode(daText));
         conversationDiv.appendChild(errorPara);
         conversationDiv.scrollTop = conversationDiv.scrollHeight;
       }

       function showResponse(lexResponse) {

        var conversationDiv = document.getElementById('conversation');
        var responsePara = document.createElement("P");
        responsePara.className = 'lexResponse';
        if (lexResponse.message) {
            responsePara.appendChild(document.createTextNode(lexResponse.message));
            responsePara.appendChild(document.createElement('br'));
        }
        if (lexResponse.dialogState === 'ReadyForFulfillment') {
            responsePara.appendChild(document.createTextNode(
                'Ready for fulfillment'));
            // TODO:  show slot values
         } else {
            responsePara.appendChild(document.createTextNode(
                '(' + lexResponse.dialogState + ')'));
         }
         conversationDiv.appendChild(responsePara);
         conversationDiv.scrollTop = conversationDiv.scrollHeight;
        }
      </script>
     </body>
     </html>

### Next steps
Congratulations! You have created a Spring Boot application that uses Amazon Lex to create an interactive user experience. As stated at the beginning of this tutorial, be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re not charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).
