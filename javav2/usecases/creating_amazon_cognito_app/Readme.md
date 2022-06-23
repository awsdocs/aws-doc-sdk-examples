# Using Amazon Cognito to require a user to log into a web application

## Purpose
Amazon Cognito lets you add user sign-up, sign-in, and access control to your web applications. Amazon Cognito scales to millions of users and supports sign-in with social identity providers, such as Facebook, Google, and enterprise identity providers such as OAuth2. In this tutorial, OAuth2 and Amazon Cognito are used to protect a web application. This means a user has to log into the application by using the credentials of a user defined in an Amazon Cognito User Pool. For example, when a user accesses a web application, they see a web page that lets anonymous users view a log in page, as shown in the following illustration.   

![AWS Tracking Application](images/pic1a.png)

When the user clicks the log in button, they are presented with a log in form where they can enter their user credentials.

![AWS Tracking Application](images/pic2.png)

After the user enters their credentials, they can access the web application. 

![AWS Tracking Application](images/pic3.png)

The following illustration shows the project files created in this tutorial (most of these files were created by following the tutorial referenced in the **Creating the resources** section). The files circled in red are the new files specific to this tutorial. 

![AWS Tracking Application](images/pic4.png)

**Topics**

+ Prerequisites
+ Update the POM file
+ Create an Amazon Cognito User Pool
+ Define a client application within the User Pool
+ Configure the client application
+ Configure a domain name
+ Create a user
+ Modify your web application


## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Java IDE (this tutorial uses the IntelliJ IDE)
+ Java JDK 1.8
+ Maven 3.6 or later+ 

### Important

+ The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re not charged.

### Creating the resources

Complete the **Creating your first AWS Java web application** tutorial. For information, see [Creating your first AWS Java web application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases/creating_first_project).


## Update the POM file

The first step in this tutorial is to update the POM file in your project to ensure you have the required dependencies (this is the project you created by following the Creating your first AWS Java web application tutorial). Ensure your project has the following POM dependencies. 

```xml
      <?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     <modelVersion>4.0.0</modelVersion>
     <groupId>GreetingCognito</groupId>
     <artifactId>GreetingCognito</artifactId>
     <version>1.0-SNAPSHOT</version>
     <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.0.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
     </parent>
     <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
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
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb-enhanced</artifactId>
            <version>2.11.0-PREVIEW</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb</artifactId>
         </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>sns</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity5</artifactId>
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
```

## Create an Amazon Cognito user pool and app client

Create a User Pool in the AWS Management Console named **spring-example**. Once the User Pool is successfully created, you see a confirmation message.

![AWS Tracking Application](images/pic5-updated.png)

1. Open the Amazon Cognito console at https://console.aws.amazon.com/cognito/home.

2. Choose **User Pools** from the left navigation pane. 

3. Choose the **Create user pool** button and provide your preferred settings through the wizard.

4. In the **User pool name** field, enter **spring-example**. 

5. In the **App client name** field, enter **spring-boot**.

![AWS Tracking Application](images/pic7-updated.png)

6. Choose **Next**, review your choices, then choose **Create user pool**. 

## Configure the app client

You must configure the app client. For example, you need to define the allowed OAuth scope values, as shown in this illustration.

![AWS Tracking Application](images/pic8-updated.png)

1. Choose your **spring-example** user pool from the **User pools** page. 

2. Choose the **App integration** tab.

3. Choose your **spring-boot** app client under **App clients and analytics**.

4. Under **Hosted UI**, choose **Edit**.

![AWS Tracking Application](images/pic6a-updated.png)

5. Add an **Allowed callback URL**. For example, with Spring Security, you can define the path as *http://localhost:8080/login/oauth2/code/cognito*. For local development, the localhost URL is all that is required. 

**Note**: For production applications, you can choose **Add another URL** to enter additional production callback URLs.

6. For the **Allowed sign-out URLs**, add *http://localhost:8080/logout*. 

7. Add **Authorization code grant** to **OAuth 2.0 grant types**.

8. Add **email** and **openid** to **OpenID Connect scopes**.

9. Choose **Save Changes**. 

## Configure a domain name

In order for a Spring Boot application to use the log in form that is provided by Amazon Cognito, define a domain name in the AWS Management Console. 

1. Choose your **spring-example** user pool from the **User pools** page. 

2. Choose the **App integration** tab. 

3. Next to **Domain**, choose **Actions**, then **Create Cognito domain**.

![AWS Tracking Application](images/pic9-updated.png)

2. Enter a domain name.

3. Choose **Save Changes**. 

## Create a user

Create a user that you can use to log into the application. In this example, the user has a user name and a password. 

1. Choose the **Users** tab. 

2. Choose **Create User**.

![AWS Tracking Application](images/pic10-updated.png)

3. In the **Create user** dialog, enter the user name and any additional information you want to provide. Choose **Mark email address as verified**. 

![AWS Tracking Application](images/pic11-updated.png)

4. Choose **Create user**.

5. Set the user's password permanently with the following CLI command:
aws cognito-idp admin-set-user-password --user-pool-id us-east-1_EXAMPLE --username UserFoo --password abc123EXAMPLEpassword! --permanent

6. Review the **Users** tab. Ensure that your new user is verified and confirmed.

![AWS Tracking Application](images/pic12-updated.png)

At this point, you need the following values to proceed: client id, client secret, pool id value, and the AWS region you are using. Without all of these values, you cannot use Amazon Cognito to require a user to log into your web application. 

## Modify your web application

If you do not have a web project, create one by following [Creating your first AWS Java web application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases/creating_first_project).

### Create the WebSecurityConfig Java class

Add the **WebSecurityConfig** class to the **com.example.handlingformsubmission** package. This file ensures that the application rerquires a user to log into it. The following Java code represents this class. 

```java
     package com.example.handlingformsubmission;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

    @Configuration
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .and()
                .authorizeRequests(authorize ->
                        authorize.mvcMatchers("/").permitAll()
                                .anyRequest().authenticated())
                .oauth2Login()
                .and()
                .logout()
                .logoutSuccessUrl("/");
      }
    }
```

### Add an application YML file to your project

Under your project’s resource folder, add a new file named **application.yml**. This file contains the information that is required to use Amazon Cognito. In this file, you specify the values that you obtained from the AWS Management Console, such as the client id, client secret, pool id values. The following code represents this file. 

     spring:
       security:
         oauth2:
          client:
           registration:
            cognito:
            clientId: <enter your client id value>
            clientSecret: <enter your client secret value>
            scope: openid, email
            redirectUriTemplate: http://localhost:8080/login/oauth2/code/cognito <your call back URL>
            clientName: spring-boot <The client app value you defined>
         provider:
          cognito:
            issuerUri: https://cognito-idp.<AWS Region>.amazonaws.com/<pool id value>
            
 ## Modify the greeting HTML file
 
The final step in the AWS tutorial is to modify the **greeting.html** file located under resources/templates folder. You have to add logic to inform the application what content is available for anonymous users and what content can be viewed by authenticated users. Add the following code to the **greeting.html** file. 

```html
     <!DOCTYPE HTML>
     <html lang="en"
       xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
       xmlns:th="http://www.thymeleaf.org">
    <head>
     <title>Getting Started: Spring Boot and the Enhanced DynamoDB Client</title>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
     <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css|"/>
    </head>
    <body>
    <div class="container">

    <div sec:authorize="isAnonymous()">
        <p>You must log in with Amazon Connito to access this AWS Web Application.</p>
        <a class="btn btn-primary" th:href="@{/oauth2/authorization/cognito}" role="button">
            Log in using <b>Amazon Cognito</b>
        </a>
    </div>

    <div sec:authorize="isAuthenticated()">
     <h1>A secure AWS Web application</h1>
     <p>Hello user <strong th:text="${#authentication.getName()}"></strong></p>
     <p>You can submit data to a DynamoDB table by using the Enhanced Client. A mobile notification is sent alerting a user a new submission occurred.</p>
     <form action="#" th:action="@{/greeting}" th:object="${greeting}" method="post">
       <div class="form-group">
        <p>Id: <input type="text"  class="form-control" th:field="*{id}" /></p>
        </div>

        <div class="form-group">
            <p>Title: <input type="text" class="form-control" th:field="*{title}" /></p>
        </div>

        <div class="form-group">
            <p>Name: <input type="text" class="form-control" th:field="*{name}" /></p>
        </div>

        <div class="form-group">
            <p>Body: <input type="text" class="form-control" th:field="*{body}"/></p>
        </div>

        <p><input type="submit" value="Submit" /> <input type="reset" value="Reset" /></p>
      </form>
        <div sec:authorize="isAuthenticated()">
            <form method="post" th:action="@{/logout}">
                <input type="submit" class="btn btn-danger" value="Logout"/>
            </form>
        </div>
     </div>
     </div>
     </body>
    </html>
```

### Next steps
Congratulations, you have required a user to log into a web application by using Amazon Cognito. As stated at the beginning of this tutorial, be sure to delete all of the resources you created while going through this tutorial to ensure that you’re not charged.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).

