#  Creating scheduled events to invoke Lambda functions

You can create a scheduled event that invokes AWS Lambda functions by using Amazon CloudWatch Events. You can configure CloudWatch Events to use Cron expressions to schedule when a Lambda function is invoked. For example, you can schedule a CloudWatch Event to invoke an AWS Lambda function every weekday. AWS Lambda is a compute service that enables you to run code without provisioning or managing servers.

**Note**: You can create Lambda functions in various programming languages. For this tutorial, Lambda functions are implemented by using the AWS Lambda Java runtime API. For more information about AWS Lambda, see
[What is AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html).

In this tutorial, you create a Lambda function by using the AWS Lambda Java runtime API. This example invokes different AWS services to perform a specific use case. For example, assume that an organization sends out a mobile text message to its employees that congratulates them at the one year anniversary date, as shown in this illustration.

![AWS Tracking Application](images/picPhone.png)

This tutorial shows you how to use Java logic to create a solution that performs this use case.  For example, you'll learn how to read a database to determine which employees have reached the one year anniversary date, how to process the data, and send out a text message all by using a Lambda function. Then you’ll learn how to use a Cron expression to invoke the Lambda function every weekday. 

This AWS tutorial uses an Amazon DynamoDB table named **Employee** that contains these fields. 
-	**Id** – the key for the table.
-	**first** – the employee’s first name.
-	**phone** – the employee’s phone number.
-	**startDate** – the employee’s start date.

![AWS Tracking Application](images/pic0.png)

**Cost to complete**: The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).

**Note**: Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re no longer charged.

#### Topics
+	Prerequisites
+	Create an IAM role that is used to execute Lambda functions
+	Create an IntelliJ project named **LambdaCronFunctions**
+	Add the POM dependencies to your project
+	Create Lambda functions by using the AWS Lambda runtime API
+	Package the project that contains the AWS Lambda function
+	Deploy the AWS Lambda function
+	Configure CloudWatch Events to use a Cron expression to invoke the AWS Lambda function

## Prerequisites
To follow along with the tutorial, you need the following:
+ An AWS Account.
+ A Java IDE (for this tutorial, the IntelliJ IDE is used).
+ Java 1.8 JDK.
+ Maven 3.6 or higher.
+ An Amazon DynamoDB table named **Employee** with a key named Id and the fields shown in the previous illustration. Make sure you enter the correct data, including a valid mobile phone that you want to test this use case with. To learn how to create a DynamoDB table, see Create a Table. To learn how to create a DynamoDB table, see [Create a Table](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/getting-started-step-1.html).

## Create an IAM role that's used to execute Lambda functions

Create the following IAM role:

+ **lambda-support** - Used to invoke Lamdba functions.

This tutorial uses the DynamoDB and Amazon SNS services. The **lambda-support** role has to have policies that enable it to invoke these services from a Lambda function.  

#### To create an IAM role

1. Open the AWS Management Console. When the page loads, enter **IAM** in the search box, and then choose **IAM** to open the IAM console.

2. In the navigation pane, choose **Roles**, and on the **Roles** page, choose **Create Role**.

3. Choose **AWS service**, and then choose **Lambda**.

![AWS Tracking Application](images/pic1.png)

4. Choose **Permissions**.

5. Search for **AWSLambdaBasicExecutionRole**.

6. Choose **Next Tags**.

7. Choose **Review**.

8. Name the role **lambda-support**.

![AWS Tracking Application](images/pic2.png)

9. Choose **Create role**.

10. Choose **lambda-support** to view the overview page.

11. Choose **Attach Policies**.

12. Search for **AmazonDynamoDBFullAccess**, and then choose **Attach policy**.

13. Search for **AmazonSNSFullAccess**, and then choose **Attach policy**. When you're done, you can see the permissions.

![AWS Tracking Application](images/pic3.png)

## Create an IntelliJ project named LambdaCronFunctions

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.

2. In the **New Project** dialog box, choose **Maven**, and then choose **Next**.

3. For **GroupId**, enter **LambdaCronFunctions**.

4. For **ArtifactId**, enter **LambdaCronFunctions**.

5. Choose **Next**.

6. Choose **Finish**.

## Add the POM dependencies to your project

At this point, you have a new project named **LambdaCronFunctions**.

![AWS Tracking Application](images/pic4.png)

Add the following dependency for the Amazon SNS API (AWS SDK for Java version 2).

     <dependency>
      <groupId>software.amazon.awssdk</groupId>
      <artifactId>sns</artifactId>
      <version>2.10.41</version>
    </dependency>


Add the following dependencies for the Amazon DynamoDB API (AWS SDK for Java version 2).

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

The pom.xml file looks like the following.

     <?xml version="1.0" encoding="UTF-8"?>
      <project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
       <modelVersion>4.0.0</modelVersion>
       <groupId>LambdaCronFunctions</groupId>
       <artifactId>LambdaCronFunctions</artifactId>
       <version>1.0-SNAPSHOT</version>
       <packaging>jar</packaging>
        <name>java-basic-function</name>
        <properties>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <maven.compiler.source>1.8</maven.compiler.source>
          <maven.compiler.target>1.8</maven.compiler.target>
        </properties>
        <dependencies>
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
        <version>2.10.41</version>
      </dependency>
      <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>sns</artifactId>
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
    
## Create Lambda functions by using the AWS Lambda runtime Java API

Use the AWS Lambda runtime Java API to create the Java class that defines the Lamdba function. In this example, there is one Java class for the Lambda function and two extra classes required for this use case. The following figure shows the Java classes in the project. Notice that all Java classes are located in a package named **com.aws.example**. 

The following figure shows the Java classes in the project. Notice that all Java classes are located in a package named **example**.

![AWS Tracking Application](images/lambda9.png)

To create a Lambda function by using the Lambda runtime API, you implement **com.amazonaws.services.lambda.runtime.RequestHandler**. The application logic that's executed when the workflow step is invoked is located in the **handleRequest** method. The return value of this method is passed to the next step in a workflow.

Create these Java classes, which are described in the following sections:
+ **Handler** - Used as the first step in the workflow that processes the ticket ID value.  
+ **Handler2** - Used as the second step in the workflow that assigns the ticket to an employee and stores the data in a database.
+ **Handler3** - Used as the third step in the workflow that sends an email message to the employee to notify them about the ticket.
+ **PersistCase** - Uses the Amazon DynamoDB API to store the data in a DynamoDB table.
+ **SendMessage** - Uses the Amazon SES API to send an email message.


