#  Creating a  Feedback Sentiment Analyzer application using the AWS SDK for Java

## Overview

| Heading      | Description |
| -----------  | ----------- |
| Description  | Discusses how to develop a Feedback Sentiment Analyzer application that lets users manage comment cards. This application is developed by using the AWS SDK for Java (v2).     |
| Audience     |  Developer (beginner / intermediate)        |
| Updated      | 9/01/2023        |
| Required skills   | Java, Maven  |

## Purpose

You can create a Feedback Sentiment Analyzer (FSA) sample application that performs these tasks:

1. The static website assets are hosted in an Amazon S3 bucket and served using Amazon CloudFront.
2. Amazon Cognito allows authenticated access to Amazon API Gateway.
3. Amazon API Gateway puts objects in an Amazon S3 bucket. This triggers an EventBridge rule that starts a Step Functions workflow.
4. The Step Functions workflow uses AWS Lambda, Amazon Textract, Amazon Comprehend, Amazon Translate, and Amazon Polly to perform the business logic.
5. Metadata is stored in Amazon DynamoDB. Audio files are stored in the same Amazon S3 bucket used in step 3.
6. Amazon API Gateway fetches the metadata from Amazon DynamoDB.


![AWS Photo Analyzer](images/overview.png)

As displayed in this illustration, the FSA application uses the following AWS services:

* Amazon Rekognition
* Amazon DynamoDB 
* Amazon S3
* Amazon SNS
* AWS Lambda 
* Amazon Cognito
* Amazon API Gateway

#### Topics

+ Prerequisites
+ Understand the photo asset management application
+ Create an IntelliJ project
+ Add the POM dependencies to your project
+ Create the Java classes
+ Deploy the AWS resources

## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Java IDE (this tutorial uses the IntelliJ IDE)
+ Java JDK 17
+ Maven 3.6 or later

### Important

+ The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to delete all of the resources you create while going through this tutorial so that you won't be charged.
+ Also make sure to properly set up your development environment. For information, see [Setting up the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html).

### Creating the resources

The required AWS resources are created by using an AWS Cloud Development Kit (AWS CDK) script. This is discussed later in the document. There is no need to create any resources by using the AWS Management Console. 

## Understand the Feedback Sentiment Analyzer application

The front end of the FSA application is a React application that uses the [Cloudscape Design System](https://cloudscape.design/). The application supports uploading images that contains text to an S3 bucket. The text represents comments made by a customer in various languages such as French.

After a user authenticates by using Amazon Cognito, the application displays all uploaded images, the translated text, and a button that lets the user hear the audio (which was created by using Amazon Polly). 

![AWS Photo Analyzer](images/app.png)

### What happens after an image is uploaded to an S3 bucket

After an image is uploaded into the storage bucket, an EventBridge rule is triggered that starts a Step Functions workflow.

![AWS Photo Analyzer](images/workflow.png)

The following descibes each step in the workflow. 

- **ExtractText** - Extracts text from the image. The text can be in another language such as French. 
- **AnalyzeSentiment** - Retrieves the sentiment of the text. For example, it can determine if the text is positive or negative.
- **ContinueIfPositive** - Routes the workflow based on the sentiment value. If the value is positive, the the next step is TranslateText.
- **TranslateText** - Translates the text into English.
- **SynthesizeAudio** - Converts the English text into an MP3 audio file and places the audio file into an S3 bucket.
- **PutPositiveComment** - Places the data into an Amazon DynamoDB table.

**Note**: The client React application does not display any data until the workflow successfully completes and data is stored in the S3 bucket and Amazon DynamoDB table. 

The following illustration shows the Amazon DynamoDB table storing the values. 

![AWS Photo Analyzer](images/dbtable.png)

**Note**: This Amazon DynamoDB table is created when you run the AWS CDK script to set up the resources. This is discussed later in this document. 	

### Understand the AWS resources used by the FSA application 	

This section describes the AWS resources that the FSA application uses. You do not have to manually deploy any of these AWS resources, such as the AWS Lambda functions, by using the AWS Management Console. Instead, you can deploy all of them by running a provided AWS CDK script. Instructions on how to deploy these AWS resources are provided later in this document. 

#### AWS Lambda functions

The backend of the FSA application is implemented by using these AWS Lambda functions created by using the AWS SDK for Java v2:

- **ExtractText** - The Lambda function used in the **ExtractText** step.
- **AnalyzeSentiment** - The Lambda function used in the **AnalyzeSentiment** step. 
- **TranslateText** - The Lambda function used in the **TranslateText** step. 
- **fnSynthesizeAudio** - The Lambda function used in the **SynthesizeAudio** step. 

**Note**: These AWS Lambda names are short names. The full names that appear in the AWS Management Console depend on how you configure the provided AWS CDK script. Full names appear as NAME}{Function Name}. For example, **fsa-scmacdon-javascript-fnSynthesizeAudio134971D4-x8Q5178Y4ZBH**.

## Create an IntelliJ project

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.
2. In the **New Project** dialog box, choose **Maven**, and then choose **Next**.
3. For **GroupId**, enter **aws-spring**.
4. For **ArtifactId**, enter **fsa_app**.
6. Choose **Next**.
7. Choose **Finish**.

## Add the POM dependencies to your project

At this point, you have a new project named **fsa_app**.

**Note:** Be sure to use Java 17 (as shown in the following **pom.xml** file).

Make sure that the **pom.xml** file looks like the following.

```xml
   <?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.example</groupId>
    <artifactId>fsa_app</artifactId>
    <version>1.0-SNAPSHOT</version>
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>software.amazon.awssdk</groupId>
                <artifactId>bom</artifactId>
                <version>2.20.45</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.apache.logging.log4j</groupId>
                <artifactId>log4j-bom</artifactId>
                <version>2.19.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb</artifactId>
        </dependency>
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.12.5</version> <!-- Use the latest version -->
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb-enhanced</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>apache-client</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>commons-logging</groupId>
                    <artifactId>commons-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j2-impl</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-1.2-api</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>ses</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>rekognition</artifactId>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-events</artifactId>
            <version>3.11.1</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>s3</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>textract</artifactId>
        </dependency>
         <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>s3control</artifactId>
        </dependency>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-lambda-java-core</artifactId>
            <version>1.2.2</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>translate</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>comprehend</artifactId>
        </dependency>
        <dependency>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
            <version>20230227</version>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>polly</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>dynamodb-enhanced</artifactId>
        </dependency>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>sns</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.maven.surefire</groupId>
            <artifactId>surefire-booter</artifactId>
            <version>3.0.0-M3</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.5</version>
        </dependency>
        <dependency>
            <groupId>com.googlecode.json-simple</groupId>
            <artifactId>json-simple</artifactId>
            <version>1.1</version>
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
                <version>3.3.0</version>
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
        </plugins>
    </build>
</project>
```

## Create the Java classes

Create a Java package in the **main/java** folder named **com.example.fsa**. The Java files go into sub-packages.

### Handlers package

Create these Java classes in the **com.example.fsa.handlers** package. These Java classes use the AWS Lambda Java runtime API to build the AWS Lambda functions described earlier in this document. Each class represents a handler for a separate AWS Lambda function. For more information about the AWS Lambda Java runtime API, see [AWS Lambda function handler in Java](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html).

+ **PollyHandler** - The handler for the **fnSynthesizeAudio** Lambda function. 
+ **S3Handler** - The handler for the **ExtractText** Lambda function.
+ **SentimentHandler** - The handler for the ****AnalyzeSentiment** Lambda function. 
+ **TranslateHandler** -The handler for the **TranslateText** Lambda function. 

#### PollyHandler class

The following Java code represents the **PollyHanlder** class. 

```java
package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.PollyService;
import com.example.fsa.services.S3Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

public class PollyHandler implements RequestHandler<Map<String, Object>, String> {
    @Override
    public String handleRequest(Map<String, Object> requestObject, Context context) {
        S3Service s3Service = new S3Service();
        PollyService pollyService = new PollyService();
        String myValues = requestObject.toString();
        context.getLogger().log("*** ALL values: " +myValues  );
        String translatedText = getTranslatedText(myValues);
        String key = getKeyName(myValues);
        String newFileName = convertFileEx(key);
        context.getLogger().log("*** Translated Text: " +translatedText +" and new key is "+newFileName);
        try {
            InputStream is = pollyService.synthesize(translatedText);
            String audioFile = s3Service.putAudio(is, newFileName);
            context.getLogger().log("You have successfully added the " +audioFile +"  in the S3 bucket");
            return audioFile ;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method extracts the value following translated_text using Reg Exps.
    private String getTranslatedText(String myString) {
        // Define the regular expression pattern to match key-value pair.
        Pattern pattern = Pattern.compile("translated_text\\s*=\\s*([^,}]*)");
        Matcher matcher = pattern.matcher(myString);

        String extractedValue = null;
        if (matcher.find()) {
            // Find the second occurrence.
            if (matcher.find()) {
                extractedValue = matcher.group(1);
            }
        }

        if (extractedValue != null) {
            return extractedValue;
        }
        return "";
    }

    // This method extracts the key using Reg Exps.
    private static String getKeyName(String input) {
        String pattern = "object=([^,}]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);

        if (m.find()) {
            System.out.println("Found value: " + m.group(1));
            return m.group(1);
        }
        return "";
    }

    // Replaces the file extension to mp3.
    public static String convertFileEx(String originalFileName) {
        String newExtension = "mp3";

        // Get the index of the last dot (.) in the filename.
        int lastIndex = originalFileName.lastIndexOf(".");
        if (lastIndex > 0) {
            // Extract the file name without extension.
            String fileNameWithoutExtension = originalFileName.substring(0, lastIndex);
            return fileNameWithoutExtension + "." + newExtension;
        }
        return "";
    }
}

```

#### S3Handler class

The following Java code represents the **S3Handler** class. 
```java
package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.FSAApplicationResources;
import com.example.fsa.services.ExtractTextService;
import java.util.Map;

public class S3Handler implements  RequestHandler<Map<String, Object>, String>{

    @Override
    public String handleRequest(Map<String, Object> requestObject, Context context) {
        // Get the Amazon Simple Storage Service (Amazon S3) bucket and object key from the Amazon S3 event.
        ExtractTextService textService = new ExtractTextService();
        String bucket = (String) requestObject.getOrDefault("bucket", "");
        String fileName = (String) requestObject.getOrDefault("object", "");
        context.getLogger().log("*** Bucket: " + bucket + ", fileName: " + fileName);
        String myText =  textService.getCardText(FSAApplicationResources.STORAGE_BUCKET, fileName);
        context.getLogger().log("*** Text: " + myText);
        return myText;
    }
}
```

### SentimentHandler class

The following is the **SentimentHandler** class. 

```java
package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.DetectSentimentService;
import org.json.simple.JSONObject;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentimentHandler implements RequestHandler<Map<String, Object>, JSONObject> {

    @Override
    public JSONObject handleRequest(Map<String, Object> requestObject, Context context) {
        // Log the entire JSON input.
        String inputString = requestObject.toString();
        context.getLogger().log("Received JSON: " + inputString);
        String value = extractValueFromRequestObject(inputString);
        context.getLogger().log("Extracted text: " + value);
        DetectSentimentService detectSentimentService = new DetectSentimentService();
        JSONObject jsonOb = detectSentimentService.detectSentiments(value);
        context.getLogger().log("NEW JSON: " + jsonOb.toJSONString());
        return jsonOb;
    }

    private static String extractValueFromRequestObject(String inputString) {
        Matcher matcher = Pattern.compile("source_text=([^,}]+)").matcher(inputString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}

```

### TranslateHandler class

The following Java code represents the **TranslateHandler** class. 

```java
package com.example.fsa.handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.fsa.services.DetectSentimentService;
import com.example.fsa.services.TranslateService;
import org.json.simple.JSONObject;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateHandler  implements RequestHandler<Map<String, Object>, JSONObject> {

    @Override
    public JSONObject handleRequest(Map<String, Object> requestObject, Context context) {
        TranslateService translateService = new TranslateService();
        String preValStr = requestObject.toString();
        context.getLogger().log("Pre Value: " + preValStr);
        String sourceText = getTranslatedText(preValStr);
        context.getLogger().log("NEW Value: " + sourceText);

        // We have the source text - need to figure out what language its in.
        DetectSentimentService sentimentService = new DetectSentimentService();
        String lanCode = sentimentService.detectTheDominantLanguage(sourceText);
        String translatedText;
        translatedText = translateService.translateText(lanCode, sourceText);
        context.getLogger().log("Translated text : " + translatedText);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("translated_text", translatedText);
        return jsonResponse;
        }

    private String getTranslatedText(String myString) {
        String extractedValue;
        Pattern pattern = Pattern.compile("extracted_text\\s*=\\s*([^,}]*)");
        Matcher matcher = pattern.matcher(myString);
        if (matcher.find()) {
            extractedValue = matcher.group(1);
            return extractedValue;
        }
        return "";
    }
}
```

### Services package

Create these Java classes in the **com.example.fsa.services** package. These Java classes use the AWS SDK for Java (v2) service clients to perform various AWS operations. For example, the **TranslateService** class translates the given text into english.  

+ **DetectSentimentService** - Uses the **ComprehendClient** to detect the sentimant of text.   
+ **ExtractTextService** - Uses the **TextractClient** to extract text from an image located in an S3 bucket.  
+ **PollyService** -  Uses the **PollyClient** to convert text into an mp3 audio file.
+ **S3Service** - Uses the **S3Client** to place an audio file into an S3 bucket.
+ **TranslateService** - Uses the **TranslateClient** to translate text into english. 

 ### DetectSentimentService class

 The following Java code represents the **DetectSentimentService** class.

```java
package com.example.fsa.services;
import org.json.simple.JSONObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageRequest;
import software.amazon.awssdk.services.comprehend.model.DetectDominantLanguageResponse;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.DominantLanguage;
import java.util.List;

public class DetectSentimentService {

    private ComprehendClient getClient() {
        return ComprehendClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }

    public JSONObject detectSentiments(String text){
        try {
            String languageCode = detectTheDominantLanguage(text);
            DetectSentimentRequest detectSentimentRequest = DetectSentimentRequest.builder()
                .text(text)
                .languageCode(languageCode)
                .build();

            DetectSentimentResponse detectSentimentResult = getClient().detectSentiment(detectSentimentRequest);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sentiment", detectSentimentResult.sentimentAsString());
            jsonObject.put("language_code", languageCode);
            return jsonObject;

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public String detectTheDominantLanguage(String text){
        try {
            DetectDominantLanguageRequest request = DetectDominantLanguageRequest.builder()
                .text(text)
                .build();

            String lanCode ="";
            DetectDominantLanguageResponse resp = getClient().detectDominantLanguage(request);
            List<DominantLanguage> allLanList = resp.languages();
            for (DominantLanguage lang : allLanList) {
                System.out.println("Language is " + lang.languageCode());
                lanCode = lang.languageCode();
            }
            return lanCode;

        } catch (ComprehendException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}

```

### ExtractTextService class

The following Java code represents the **ExtractTextService** class. 

```java
 package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.S3Object;

public class ExtractTextService {

    public String getCardText(String bucketName, String obName) {
        Region region = Region.US_EAST_1;
        TextractClient textractClient = TextractClient.builder()
            .region(region)
            .build();

        S3Object s3Object = S3Object.builder()
            .bucket(bucketName)
            .name(obName)
            .build();

        // Create a Document object and reference the s3Object instance.
        Document myDoc = Document.builder()
            .s3Object(s3Object)
            .build();

        DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
            .document(myDoc)
            .build();

        // Use StringBuilder to build the complete text.
        StringBuilder completeText = new StringBuilder();
        DetectDocumentTextResponse textResponse = textractClient.detectDocumentText(detectDocumentTextRequest);
        for (Block block : textResponse.blocks()) {
            if (block.blockType() == BlockType.WORD) {
                if (completeText.length() == 0) {
                    completeText.append(block.text());
                } else {
                    completeText.append(" ").append(block.text());
                }
            }
        }
       return completeText.toString();
    }
}

```

 ### PollyService class

 The following Java code represents the **PollyService** class.

```java
package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.Voice;
import software.amazon.awssdk.services.polly.model.DescribeVoicesResponse;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import java.io.IOException;
import java.io.InputStream;


public class PollyService {

    public InputStream synthesize(String text) throws IOException {
        PollyClient polly = PollyClient.builder()
            .region(Region.US_EAST_1)
            .build();

        DescribeVoicesRequest describeVoiceRequest = DescribeVoicesRequest.builder()
            .engine("neural")
            .build();

        DescribeVoicesResponse describeVoicesResult = polly.describeVoices(describeVoiceRequest);
        Voice voice = describeVoicesResult.voices().stream()
            .filter(v -> v.name().equals("Joanna"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Voice not found"));

        SynthesizeSpeechRequest synthReq = SynthesizeSpeechRequest.builder()
            .text(text)
            .outputFormat(OutputFormat.MP3)
            .voiceId(voice.id())
            .build();

        return polly.synthesizeSpeech(synthReq);
    }
}

```

### S3Service class

The following Java code represents the **S3Service** class.

```java
 package com.example.fsa.services;

import com.example.fsa.FSAApplicationResources;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class S3Service {

    // Put the audio file into the Amazon S3 bucket.
    public String putAudio(InputStream is, String key) throws IOException {
        S3Client s3 = S3Client.builder()
            .region(Region.US_EAST_1)
            .build();

        PutObjectRequest putOb = PutObjectRequest.builder()
            .bucket(FSAApplicationResources.STORAGE_BUCKET)
            .contentType("audio/mp3")
            .key(key)
            .build();

        s3.putObject(putOb, RequestBody.fromBytes(inputStreamToBytes(is)));
        return key;
    }

    public static byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}


```

### TranslateService class

The following Java code represents the **TranslateService** class.

```java
 package com.example.fsa.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

public class TranslateService {

    public String translateText(String lanCode, String text)  {
        TranslateClient translateClient = TranslateClient.builder()
            .region(Region.US_EAST_1)
            .build();

        TranslateTextRequest textRequest = TranslateTextRequest.builder()
            .sourceLanguageCode(lanCode)
            .targetLanguageCode("en")
            .text(text)
            .build();

        TranslateTextResponse textResponse = translateClient.translateText(textRequest);
        return textResponse.translatedText();
    }
}

```



### FSAApplicationResources class

Add the The following Java code to the **com.example.fsa** package. This represents the **FSAApplicationResources** class.

```java
package com.example.fsa;

public class FSAApplicationResources {

    public static final String STORAGE_BUCKET = "<Update the bucket name>";
}

```

### ModifyLambda class

Add the following Java code to the **com.example.fsa** package. This represents the **ModifyLambda** class that is used to update all four Lambda functions created using the CDK script with the Lambda functions created using the AWS SDK for Java V2. Specify the correct Lambda function names and the name of the FAT JAR that is placed in the S3 bucket (see the instructions later in this document). 

```java
package com.example.fsa;

import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionConfigurationResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeRequest;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeResponse;
import software.amazon.awssdk.services.lambda.waiters.LambdaWaiter;

public class ModifyLambda {

    public static void main(String[]args) {
        String analyzeSentimentLambda = "<Enter value>" ;
        String synthesizeAudioLambda = "<Enter value>"" ;
        String extractTextLambda = "<Enter value>" ;
        String translateTextLambda = "<Enter value>"" ;
        String bucketName = FSAApplicationResources.STORAGE_BUCKET;
        String key = "<Enter value>";

        Region region = Region.US_EAST_1;
        LambdaClient awsLambda = LambdaClient.builder()
            .region(region)
            .build();

        // Update all four Lambda functions.
        updateFunctionCode(awsLambda, analyzeSentimentLambda, bucketName, key);
        updateFunctionCode(awsLambda, synthesizeAudioLambda, bucketName, key);
        updateFunctionCode(awsLambda, extractTextLambda, bucketName, key);
        updateFunctionCode(awsLambda, translateTextLambda, bucketName, key);
        System.out.println("You have successfully updated the AWS Lambda functions");
    }

    public static void updateFunctionCode(LambdaClient awsLambda, String functionName, String bucketName, String key) {
        try {
            LambdaWaiter waiter = awsLambda.waiter();
            UpdateFunctionCodeRequest functionCodeRequest = UpdateFunctionCodeRequest.builder()
                .functionName(functionName)
                .publish(true)
                .s3Bucket(bucketName)
                .s3Key(key)
                .build();

            UpdateFunctionCodeResponse response = awsLambda.updateFunctionCode(functionCodeRequest) ;
            GetFunctionConfigurationRequest getFunctionConfigRequest = GetFunctionConfigurationRequest.builder()
                .functionName(functionName)
                .build();

            WaiterResponse<GetFunctionConfigurationResponse> waiterResponse = waiter.waitUntilFunctionUpdated(getFunctionConfigRequest);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println("The last modified value is " +response.lastModified());

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}


```


## Deploy the AWS resources

At this point, you have completed all of the application Java business logic required for the PAM application to work. Now you need to deploy the AWS resources, including the AWS Lambda functions and API Gateway endpoints in order for the application to work. Instead of deploying all of the resources manually by using the AWS Management Console, you can use a provided AWS CDK script. Using the CDK script makes it more efficient to deploy the resources. 

**Note**: For information about the AWS CDK, see [What is the AWS CDK](https://docs.aws.amazon.com/cdk/v2/guide/home.html).
		
For complete instuctions on how to run the supplied AWS CDK script, see [PAFeedback Sentiment Analyzer (FSA)](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/applications/feedback_sentiment_analyzer/README.md).

### Update the Lambda functions

After you execute the AWS CDK script, the Lambda functions are created. However, you need to replace the ones installed using the AWS CDK with the ones you created by using the AWS SDK for Java. To perform this task, you can run Java code that automatically updates the Lambda functions using a FAT JAR. Perform the following tasks:

1. Update the FSAApplicationResources with the AWS resource names that the AWS CDK script created. Make sure to update the **STORAGE_BUCKET** value. 

2. Create the FAT JAR by running **mvn package**. The JAR file can be located in the target folder.

3. Use the AWS Management Console to place the FAT JAR into the Amazon S3 bucket named **STORAGE_BUCKET**. (This was created by the AWS CDK script.)  

![AWS Photo Analyzer](images/kotlinJarS3.png)

4. Run the ModifyLambda Java class. 
 
5. Add the following Java code to the project and update the Java variable names with the resources created by the AWS CDK script. Make sure to specify the exact names, which are provided in the AWS Lambda console. Otherwise, the code does not work. Run the following code.  

```java
package com.pam

import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.UpdateFunctionCodeRequest
import aws.sdk.kotlin.services.lambda.waiters.waitUntilFunctionUpdated

suspend fun main() {
    val key = "PhotoAssetRestKotlin-1.0-SNAPSHOT-all.jar"
    val bucketName = "<Update value>"
    val functionNamesLabels = "<Update value>"
    val uploadFunction = "<Update value>"
    val downloadFunction = "<Update value>"
    val s3Handle = "<Update value>"

    // Replace all four AWS Lambda functions.
    updateFunctionCode(functionNamesLabels, bucketName, key)
    updateFunctionCode( uploadFunction, bucketName, key)
    updateFunctionCode(downloadFunction, bucketName, key)
    updateFunctionCode(s3Handle, bucketName, key)
    println("You have replaced all PAM Application Lambda functions!")
}

suspend fun updateFunctionCode(functionNameVal: String?, bucketName: String?, key: String?) {
    val functionCodeRequest = UpdateFunctionCodeRequest {
        functionName = functionNameVal
        publish = true
        s3Bucket = bucketName
        s3Key = key
    }

    LambdaClient { region = "us-east-1" }.use { awsLambda ->
        val response = awsLambda.updateFunctionCode(functionCodeRequest)
        awsLambda.waitUntilFunctionUpdated {
            functionName = functionNameVal
        }
        println("The last modified value is " + response.lastModified)
    }
}

```

When done, you see a message that the AWS Lambda functions have been updated with the Kotlin FAT JAR. 

![AWS Photo Analyzer](images/kotlinProgram.png)



### Run the application

When you run the AWS CDK script, you can run the client application by using the Amazon Cloudfront distribution URL as specified in the supplied AWS CDK instructions. 

### Next steps
Congratulations! You have created and deployed the Photo Asset Management application. As stated at the beginning of this tutorial, be sure to delete all of the resources by following the AWS CDK instructions so that you won't continue to be charged for them.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).

