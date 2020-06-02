#  Create AWS Serverless workflows using the Java SDK

You can create an AWS Serverless workflow by using the AWS Java SDK and AWS Step Functions. 
Each workflow step is implemented by using a Lambda function. AWS Lambda is a compute service that lets you run 
code without provisioning or managing servers.

**Note**: You can create Lambda functions in various programming languages. For this tutorial, Lambda functions are 
implemented by using the Lambda Java API. For more information about Lambda, see 
[What is AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html).

In this tutorial, you create a workflow that creates support tickets for an organization. Each workflow step performs an operation on the ticket. This tutorial teaches you how process workflow data by using Java. For example, you will learn how to read data that is passed to the workflow, how to pass data between steps, and how to invoke AWS Services from a workflow. 

#### Prerequisites
To follow along with the tutorial, you need the following:
+ An AWS Account.
+ A Java IDE (for this tutorial, the IntelliJ IDE is used).
+ Java 1.8 JDK. 
+ Maven 3.6 or higher.

**Cost to complete**: The AWS Services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc). 

**Note**: Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re no longer charged.

#### Topics

+ Understand the workflow.
+ Create an IAM role that is used to execute Lambda functions.
+	Create a workflow by using AWS Step functions.
+	Create an IntelliJ project named LambdaFunctions.
+	Add the POM dependencies to your project.
+	Create Lambda functions by using the Java Lambda API.
+	Package the project that contains Lambda functions. 
+	Deploy Lambda functions.
+	Add Lambda functions to workflows.
+ Invoke the workflow from the AWS Console.

**Note**: Before following this tutorial, create an Amazon DynamoDB table named Cases with a key named Id. To learn how to create a DynamoDB table, see [Create a Table](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/getting-started-step-1.html).

## Understand the workflow

The following figure shows the workflow that is created by following this tutorial. 

![AWS Tracking Application](images/lambda1.png)

The following describes each step: 
+	**Open Case** – Handles a support ticket Id value (the id value is passed to the workflow). 
+	**Assign Case** – Assigns the support case to an employee and stores the data in a DynamoDB table. 
+	**Send Email** – Sends the employee an email message by using the Amazon Simple Email Service (SES) to inform them there is a new ticket. 

## Create an IAM role that is used to execute Lambda functions

Create two IAM roles:
+ **lambda-support** - Used to invoke Lamdba functions.
+ **workflow-support** - Used to AWS Step functions to invoke workflow.

The AWS Services used in this tutorial are Amazon DynamoDB and Amazon SES. The lambda-support role has to have policies that enables it to invoke these services. This is how you can invoke AWS Services from a Lambda function. 

#### Create an IAM role

1. Open the AWS Management Console. When the screen loads, type **IAM** in the search bar, then select **IAM** to open the service console.

2.  Choose **Roles** from the left column, and then choose **Create Role**. 

3.	Choose **AWS Service** and choose **Lambda**.

![AWS Tracking Application](images/lambda21.png)

4. Choose **Permissions**.

5. Search for **AWSLambdaBasicExecutionRole**.

6. Choose **Next Tags**.

7. Choose **Review**.

8. Name the role **lambda-support**.

![AWS Tracking Application](images/lambda17.png)

9.	Choose **Create role**. 

10.	Click on **lambda-support** to view the overview page. 

11.	Choose **Attach Policies**.

12.	Search for **AmazonDynamoDBFullAccess** and choose **Attach policy**.

13.	Search for **AmazonSESFullAccess** and choose **Attach policy**. Once done, you will see the permissions. 

![AWS Tracking Application](images/lambda16.png)

**Note**: Repeat this process to create **workflow-support**. For step three, instead of choosing **Lambda**, choose **Step Functions**. It’s not necessary to perform steps 11-13. 

## Create a serverless workflow by using AWS Step functions

You can create a workflow that processes support tickets. To define a workflow by using AWS Step functions, you create an Amazon States Language (JSON-based) document to define your state machine. An Amazon States Language document describes each step. Once you define the document, AWS Step functions presents you with a visual representation of the workflow. The following figure shows the Amazon States Language document and the visual representation of the workflow. 

![AWS Tracking Application](images/Lambda2.png)

Workflows can pass data between steps. For example, the **Open Case** step processes a case ID value (passed to the workflow) and passes that value to the **Assign Case** step. You create application logic in the Lambda function to read and process the data values (this is shown later in this tutorial). 

#### Create a workflow

1.	Open the AWS Step Functions console at https://us-west-2.console.aws.amazon.com/states/home.

2.	Choose **Create State Machine**. 

3.	Choose **Author with code snippets**. In the **Type** area, choose **Standard**.

![AWS Tracking Application](images/lambda3.png)

4.	Specify the Amazon States Language document by entering the following code. 

        {
        "Comment": "A simple AWS Step Functions state machine that automates a call center support session.",
        "StartAt": "Open Case",
        "States": {
        "Open Case": {
        "Type": "Task",
        "Resource": "arn:aws:lambda:REGION:ACCOUNT_ID:function:FUNCTION_NAME",
        "Next": "Assign Case"
          }, 
         "Assign Case": {
         "Type": "Task",
         "Resource": "arn:aws:lambda:REGION:ACCOUNT_ID:function:FUNCTION_NAME",
         "Next": "Send Email"
         },
         "Send Email": {
         "Type": "Task",
         "Resource": "arn:aws:lambda:REGION:ACCOUNT_ID:function:FUNCTION_NAME",
         "End": true
          }
          }
         }
**Note**: Do not worry about the errors related to the Lambda resource values. These values will be updated later in this tutorial. 

5.	Choose **Next**. 

6.	In the name field, enter **SupportStateMachine**. 

7.	Under the **Permission** section, choose **Choose an existing role**. 

8.	Choose **workflow-support** (the IAM role that you created).

![AWS Tracking Application](images/lambda19.png)

9.	Choose **Create state machine**. You will see a message that states the state machine was successfully created.

![AWS Tracking Application](images/lambda7.png)

## Create an IntelliJ project named LambdaFunctions

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.

2. In the **New Project** dialog box, choose **Maven**, and then choose **Next**.

3. For **GroupId**, enter **LambdaFunctions**.

4. For **ArtifactId**, enter **LambdaFunctions**.

5. Choose **Next**.

6. Choose **Finish**.

## Add the POM dependencies to your project

At this point, you have a new project named **LambdaFunctions**.

![AWS Tracking Application](images/lambda8.png)

Add the following dependency for the Amazon SES API (AWS Java SDK version 2).

     <dependency>
       <groupId>software.amazon.awssdk</groupId>
       <artifactId>ses</artifactId>
       <version>2.10.41</version>
     </dependency>
     
Add the following dependencies for the Amazon DynamoDB API (AWS Java SDK version 2).

     <dependency>
       <groupId>software.amazon.awssdk</groupId>
       <artifactId>dynamodb-enhanced</artifactId>
       <version>2.11.4-PREVIEW</version>
     </dependency>
     <dependency>
       <groupId>software.amazon.awssdk</groupId>
       <artifactId>dynamodb</artifactId>
       <version>2.5.10</version>
     </dependency>

Ensure that the pom.xml file looks like the following.

      <?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>LambdaFunctions</groupId>
      <artifactId>LambdaFunctions</artifactId>
      <version>1.0-SNAPSHOT</version>
      <packaging>jar</packaging>
      <name>java-basic-function</name>
      <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
      </properties>
      <dependencies>
        <!-- https://mvnrepository.com/artifact/com.amazonaws/aws-lambda-java-core -->
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-core</artifactId>
            <version>1.2.1</version>
        </dependency>

        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.8.6</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>2.10.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.13.0</version>
            <scope>test</scope>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-slf4j18-impl -->
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j18-impl</artifactId>
            <version>2.13.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.6.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.6.0</version>
            <scope>test</scope>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple -->
        <dependency>
            <groupId>com.googlecode.json-simple</groupId>
            <artifactId>json-simple</artifactId>
            <version>1.1.1</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb-enhanced</artifactId>
            <version>2.11.4-PREVIEW</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb</artifactId>
            <version>2.5.10</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>ses</artifactId>
            <version>2.10.41</version>
        </dependency>
        <dependency>
            <groupId>javax.mail</groupId>
            <artifactId>javax.mail-api</artifactId>
            <version>1.5.5</version>
        </dependency>
        <dependency>
            <groupId>com.sun.mail</groupId>
            <artifactId>javax.mail</artifactId>
            <version>1.5.5</version>
        </dependency>
       </dependencies>
       <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.2</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.2</version>
                <configuration>
                    <createDependencyReducedPom>false</createDependencyReducedPom>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
           </plugins>
          </build>
        </project>
## Create Lambda functions by using the Java Lambda API

Create the Java classes that define the Lamdba functions by using the Lambda run-time API. In this example, there are three workflow steps where each step has a corresponding Java class. In addition, there are two extra classes which invoke the Amazon DynamoDB service and the Amazon SES service. 

The following figure shows the Java classes in the project. Notice that all Java classes are located in a package named **example**. 

![AWS Tracking Application](images/lambda9.png)
