#  Creating a dynamic web application that asynchronously analyzes photos using the AWS SDK for Java 

## Overview

| Heading      | Description |
| ----------- | ----------- |
| Description | Discusses how to develop a dynamic web application that analyzes nature images located in an Amazon Simple Storage Service (Amazon S3) bucket by using the AWS SDK for Java V2. This tutorial uses AWS SDK asynchronous clients.  |
| Audience   |  Developer (beginner / intermediate)        |
| Required Skills   | Java, Maven  |

## Purpose
You can create a dynamic web application that analyzes nature images located in an Amazon Simple Storage Service (Amazon S3) bucket by using the Amazon Rekognition service. The application analyzes multiple images and generates a report that breaks down each image into a series of labels. For example, the following image shows a lake.

![AWS Photo Analyzer](images/lakesun.png)

After the application analyzes all images in the Amazon S3 bucket, it uses the Amazon Simple Email Service (Amazon SES) to send a dynamically created report to a given email recipient. The report is a Microsoft Excel document that contains label data for each image. 

![AWS Photo Analyzer](images/excel2.png)

In this tutorial, you create a Spring Boot application named **AWS Photo Analyzer**. The Spring Boot APIs are used to build a model, different views, and a controller. For more information, see [Spring Boot](https://www.tutorialspoint.com/spring_boot/index.htm).

This application uses the following AWS services:
*	Amazon Rekognition
*	Amazon S3
*	Amazon SES

**Note**: This example application uses the Asynchronous client that belongs to the AWS SDK for Java V2 for the Amazon S3 and Amazon Rekognition services. For more information, see [Asynchronous programming in the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/asynchronous.html).

**Note** To read a similiar use case that uses the AWS SDK for Java V2 synchronous clients, see [Creating a dynamic web application that analyzes photos using the AWS SDK for Java](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_photo_analyzer_app).

#### Topics

+ Prerequisites
+ Understand the AWS Photo Analyzer application
+ Create an IntelliJ project 
+ Add the POM dependencies to your project
+ Create the Java classes
+ Create the HTML files
+ Create the script files
+ Run the application

## Prerequisites

To complete the tutorial, you need the following:

+ An AWS account
+ A Java IDE (this tutorial uses the IntelliJ IDE)
+ Java JDK 17
+ Maven 3.6 or later

### Important

+ The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).
+  This code has not been tested in all AWS Regions. Some AWS services are available only in specific regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services). 
+ Running this code might result in charges to your AWS account. 
+ Be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re not charged.

### Creating the resources

Create an Amazon S3 bucket named **photos[somevalue]**. Be sure to use this bucket name in your Amazon S3 Java code. For information, see [Creating a bucket](https://docs.aws.amazon.com/AmazonS3/latest/gsg/CreatingABucket.html).

In addition, make sure that you have properly setup your development environment. For information, see [Setting up the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html).

## Understand the AWS Photo Analyzer application

The AWS Photo Analyzer application supports uploading images to an Amazon S3 bucket. After the images are uploaded, you can view the images that are analyzed.

![AWS Photo Analyzer](images/client.png)

To generate a report, enter an email address and choose **Analyze Photos**.

![AWS Photo Analyzer](images/client2.png)

You can also download a given image from the Amazon S3 bucket by using this application. Simply specify the image name and choose the **Download Photo** button. The image is downloaded to your browser, as shown in this illustration. 

![AWS Photo Analyzer](images/client3.png)

## Create an IntelliJ project named SpringPhotoAnalyzerAsync

1. In the IntelliJ IDE, choose **File**, **New**, **Project**.
2. In the **New Project** dialog box, choose **Maven**, and then choose **Next**.
3. For **GroupId**, enter **aws-spring**.
4. For **ArtifactId**, enter **SpringPhotoAnalyzerAsync**.
6. Choose **Next**.
7. Choose **Finish**.

## Add the POM dependencies to your project

At this point, you have a new project named **SpringPhotoAnalyzerAsync**.

![AWS Photo Analyzer](images/project.png)

**Note:** Ensure that you are using Java 17 (as shown in the following **pom.xml** file).

Make sure that your project's pom.xml file looks like the POM file in this Github repository.

## Create the Java classes

Create a Java package in the **main/java** folder named **com.example.photo**. The Java files go into this package.

![AWS Photo Analyzer](images/project1.png)

Create these Java classes:

+ **AnalyzePhotos** - Uses the Amazon Rekognition API to analyze the images.
+ **BucketItem** - Used as a model that stores Amazon S3 bucket information.   
+ **PhotoApplication** - Used as the base class for the Spring Boot application.
+ **PhotoController** - Used as the Spring Boot controller that handles HTTP requests.
+ **SendMessages** - Uses the Amazon SES API to send an email message with an attachment.
+ **S3Service** - Uses the Amazon S3 API to perform S3 operations.
+ **WorkItem** - Used as a model that stores Amazon Rekognition data.
+ **WriteExcel** – Uses the JXL API (this is not an AWS API) to dynamically generate a report.     

### AnalyzePhotos class

The following Java code represents the **AnalyzePhotos** class. This class uses the Amazon Rekognition API to analyze the images. Notice the use of the **RekognitionAsyncClient** object. For more information, see [Interface RekognitionAsyncClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/rekognition/RekognitionAsyncClient.html).

When working with the **RekognitionAsyncClient**, you use a **CompletableFuture** object that allows you to access the response when it’s ready. You can access the **resp** object by calling the **futureGet.whenComplete** method. Then you can get service data by invoking the applicable method that belongs to the **resp** object. For example, you can get label data generated by the Amazon Rekognition service by invoking the **resp.labels** method. This returns a list where each element is a **Label** object. 

To return data that you read from the **resp** object (for example, label data), you must use an **AtomicReference** object. You cannot return data from within the **futureGet.whenComplete** method. If you attempt to perform this task, you get a compile error. You can set the data by using the **AtomicReference** object's **set** method. You can then access the **AtomicReference** object from outside the **futureGet.whenComplete** method to get the data by using the **AtomicReference** object's **get** method. Then you can return the data from a Java method, as shown in the following Java code example.    

```java
package com.example.photo;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionAsyncClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;

@Component
public class AnalyzePhotos {
    public ArrayList<WorkItem> DetectLabels(byte[] bytes, String key) {
        RekognitionAsyncClient rekAsyncClient = RekognitionAsyncClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_EAST_2)
                .build();
        try {
            final AtomicReference<ArrayList<WorkItem>> reference = new AtomicReference<>();
            SdkBytes sourceBytes = SdkBytes.fromByteArray(bytes);

            // Create an Image object for the source image.
            Image souImage = Image.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                    .image(souImage)
                    .maxLabels(10)
                    .build();

            CompletableFuture<DetectLabelsResponse> futureGet = rekAsyncClient.detectLabels(detectLabelsRequest);
            futureGet.whenComplete((resp, err) -> {
             try {
                if (resp != null) {
                    List<Label> labels =  resp.labels();
                    System.out.println("Detected labels for the given photo");
                    ArrayList<WorkItem> list = new ArrayList<>();
                    WorkItem item ;
                    for (Label label: labels) {
                        item = new WorkItem();
                        item.setKey(key); // identifies the photo.
                        item.setConfidence(label.confidence().toString());
                        item.setName(label.name());
                        list.add(item);
                    }
                    reference.set(list);

                } else {
                    err.printStackTrace();
                }

            } finally {
                // Only close the client when you are completely done with it.
                rekAsyncClient.close();
            }
          });
          futureGet.join();

          // Use the AtomicReference object to return the ArrayList<WorkItem> collection.
          return reference.get();

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null ;
    }
}

```

**Note:** In this example, an **EnvironmentVariableCredentialsProvider** is used for the credentials.

### BucketItem class

The following Java code represents the **BucketItem** class that stores Amazon S3 object data.

```java
    package com.example.photo;

    public class BucketItem {

    private String key;
    private String owner;
    private String date ;
    private String size ;


    public void setSize(String size) {
        this.size = size ;
    }

    public String getSize() {
        return this.size ;
    }

    public void setDate(String date) {
        this.date = date ;
    }

    public String getDate() {
        return this.date ;
    }

    public void setOwner(String owner) {
        this.owner = owner ;
    }

    public String getOwner() {
        return this.owner ;
    }


    public void setKey(String key) {
        this.key = key ;
    }

    public String getKey() {
        return this.key ;
    }
    }
```

### PhotoApplication class

The following Java code represents the **PhotoApplication** class.

```java
    package com.example.photo;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication
    public class PhotoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoApplication.class, args);
      }
     }
```

### PhotoController class

The following Java code represents the **PhotoController** class that handles HTTP requests. For example, when a new image is posted (uploaded to an S3 bucket), the **singleFileUpload** method handles the request.

**Note**: Be sure that you change the **bucketName** variable to your Amazon S3 bucket name. 

```java
package com.example.photo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
public class PhotoController {

    // Change to your Bucket Name
    private final String bucketName = "<Enter your S3 bucket name>";
    private final S3Service s3Service;
    private final AnalyzePhotos photos;
    private final  WriteExcel excel;

    private final SendMessages sendMessage;

    @Autowired
    PhotoController(
        S3Service s3Service,
        AnalyzePhotos photos,
        WriteExcel excel,
        SendMessages sendMessage
    ) {
        this.s3Service = s3Service;
        this.photos = photos;
        this.excel = excel;
        this.sendMessage = sendMessage;
    }

    @GetMapping("/")
    public String root() {
        return "index";
    }

    @GetMapping("/process")
    public String process() {
        return "process";
    }

    @GetMapping("/photo")
    public String photo() {
        return "upload";
    }

    // Generates a report that analyzes photos in a given bucket.
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    @ResponseBody
    String report (HttpServletRequest request, HttpServletResponse response) {
        // Get a list of key names in the given bucket.
        String email = request.getParameter("email");
        ArrayList<String> myKeys = (ArrayList<String>) s3Service.ListBucketObjects(bucketName);
        ArrayList<List<WorkItem>> myList = new ArrayList<>();
        for (String myKey : myKeys) {
            byte[] keyData = s3Service.getObjectBytes(bucketName, myKey);
            ArrayList<WorkItem> item = photos.DetectLabels(keyData, myKey);
            myList.add(item);
        }

        // Now we have a list of WorkItems describing the photos in the S3 bucket.
        InputStream excelData = excel.exportExcel(myList);
        try {
            // Email the report.
            sendMessage.sendReport(excelData, email);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "The photos have been analyzed and the report is sent";
    }

    // Upload an image to an Amazon S3 bucket.
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView singleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String name =  file.getOriginalFilename() ;

            // Put the file into the bucket.
            s3Service.putObject(bytes, bucketName, name);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView(new RedirectView("photo"));
    }

    @RequestMapping(value = "/getimages", method = RequestMethod.GET)
    @ResponseBody
    String getImages(HttpServletRequest request, HttpServletResponse response) {
        return s3Service.ListAllObjects(bucketName);
    }

    // Downloads the given image from the Amazon S3 bucket.
    @RequestMapping(value = "/downloadphoto", method = RequestMethod.GET)
    void buildDynamicReportDownload(HttpServletRequest request, HttpServletResponse response) {
        try {
            String photoKey = request.getParameter("photoKey");
            byte[] photoBytes = s3Service.getObjectBytes(bucketName, photoKey) ;
            InputStream is = new ByteArrayInputStream(photoBytes);

            // Define the required information here.
            response.setContentType("image/png");
            response.setHeader("Content-disposition", "attachment; filename="+photoKey);
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

**Note** - Be sure to replace the bucket name in this code example with your bucket name.

### S3Service class

The following class uses the Amazon S3 Java API to perform Amazon S3 operations. For example, the **getObjectBytes** method returns a byte array that represents the image. Notice the use of the **S3AsyncClient** object. For more information, see [Interface S3AsyncClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/S3AsyncClient.html). 

```java
package com.example.photo;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class S3Service {
    S3AsyncClient s3AsyncClient;
    private S3AsyncClient getClient() {
        return S3AsyncClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_WEST_2)
                .build();
    }

    public byte[] getObjectBytes (String bucketName, String keyName) {
        s3AsyncClient = getClient();
        final AtomicReference<byte[]> reference = new AtomicReference<>();
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            // Get the Object from the Amazon S3 bucket using the Amazon S3 Async Client.
            final CompletableFuture<ResponseBytes<GetObjectResponse>>[] futureGet = new CompletableFuture[]{s3AsyncClient.getObject(objectRequest,
                    AsyncResponseTransformer.toBytes())};

            futureGet[0].whenComplete((resp, err) -> {
                try {
                    if (resp != null) {
                        //  Set the AtomicReference object.
                         reference.set(resp.asByteArray());

                    } else {
                        err.printStackTrace();
                    }
                } finally {
                    // Only close the client when you are completely done with it.
                    s3AsyncClient.close();
                }
            });
            futureGet[0].join();

            // Read the AtomicReference object and return the byte[] value.
            return reference.get();

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    // Returns the names of all images in the given bucket.
    public List<String> ListBucketObjects(String bucketName) {
        s3AsyncClient = getClient();
        final AtomicReference<List<String>> reference = new AtomicReference<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            CompletableFuture<ListObjectsResponse> futureGet  = s3AsyncClient.listObjects(listObjects);
            futureGet.whenComplete((resp, err) -> {
                try {
                    List<String> keys = new ArrayList<>();
                    String keyName ;
                    if (resp != null) {
                        List<S3Object> objects = resp.contents();
                        for (S3Object myValue : objects) {
                            keyName = myValue.key();
                            keys.add(keyName);
                        }

                        //  Set the AtomicReference object.
                        reference.set(keys) ;
                    } else {
                        err.printStackTrace();
                    }
                } finally {
                    // Only close the client when you are completely done with it.
                    s3AsyncClient.close();
                }
            });
            futureGet.join();

            // Read the AtomicReference object.
            return reference.get();

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null ;
    }

    // Places an image into a S3 bucket.
    public void putObject(byte[] data, String bucketName, String objectKey) {
        s3AsyncClient = getClient();
        try {
            PutObjectRequest objectRequest =  PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            // Put the object into the bucket.
            CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(objectRequest,
                    AsyncRequestBody.fromBytes(data));
            future.whenComplete((resp, err) -> {
                try {
                    if (resp != null) {
                        System.out.println("Object uploaded. Details: " + resp);
                    } else {
                        // Handle error
                        err.printStackTrace();
                    }
                } finally {
                    // Only close the client when you are completely done with it
                    s3AsyncClient.close();
                }
            });
            future.join();

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Returns the names of all images and data within XML.
    public String ListAllObjects(String bucketName) {
        s3AsyncClient = getClient();
        final AtomicReference<List<BucketItem>> reference = new AtomicReference<>();
        List<BucketItem> bucketItems = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            CompletableFuture<ListObjectsResponse> future = s3AsyncClient.listObjects(listObjects);
            future.whenComplete((resp, err) -> {
                try {
                    if (resp != null) {
                        BucketItem myItem ;
                        long sizeLg;
                        Instant DateIn;
                        List<S3Object> objects = resp.contents();
                        for (S3Object myValue: objects) {
                            myItem = new BucketItem();
                            myItem.setKey(myValue.key());
                            myItem.setOwner(myValue.owner().displayName());
                            sizeLg = myValue.size() / 1024 ;
                            myItem.setSize(String.valueOf(sizeLg));
                            DateIn = myValue.lastModified();
                            myItem.setDate(String.valueOf(DateIn));

                            // Push the items to the list.
                            bucketItems.add(myItem);
                        }
                        reference.set(bucketItems) ;

                    } else {
                        err.printStackTrace();
                    }
                } finally {
                    // Only close the client when you are completely done with it.
                    s3AsyncClient.close();
                }
            });
            future.join();
            return convertToString(toXml(reference.get()));

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null ;
    }

    // Convert items into XML to pass back to the view.
    private Document toXml(List<BucketItem> itemList) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement( "Items" );
            doc.appendChild( root );

            // Iterate through the collection.
            for (BucketItem myItem : itemList) {
                Element item = doc.createElement("Item");
                root.appendChild(item);

                // Set Key.
                Element id = doc.createElement("Key");
                id.appendChild(doc.createTextNode(myItem.getKey()));
                item.appendChild(id);

                // Set Owner.
                Element name = doc.createElement("Owner");
                name.appendChild(doc.createTextNode(myItem.getOwner()));
                item.appendChild(name);

                // Set Date.
                Element date = doc.createElement("Date");
                date.appendChild(doc.createTextNode(myItem.getDate()));
                item.appendChild(date);

                // Set Size.
                Element desc = doc.createElement("Size");
                desc.appendChild(doc.createTextNode(myItem.getSize()));
                item.appendChild(desc);
            }

            return doc;
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertToString(Document xml) {
        try {
            TransformerFactory transformerFactory = getSecureTransformerFactory();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch(TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static TransformerFactory getSecureTransformerFactory() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        return transformerFactory;
    }
}

```

### SendMessage class

The following Java code represents the **SendMessage** class. This class uses the Amazon SES Java API to send an email message with an attachment that represents the report.

```java
     package com.example.photo;

    import org.apache.commons.io.IOUtils;
    import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
    import software.amazon.awssdk.regions.Region;
    import software.amazon.awssdk.services.ses.SesClient;
    import javax.activation.DataHandler;
    import javax.activation.DataSource;
    import javax.mail.Message;
    import javax.mail.MessagingException;
    import javax.mail.Session;
    import javax.mail.internet.InternetAddress;
    import javax.mail.internet.MimeMessage;
    import javax.mail.internet.MimeMultipart;
    import javax.mail.internet.MimeBodyPart;
    import javax.mail.util.ByteArrayDataSource;
    import java.io.ByteArrayOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.ByteBuffer;
    import java.util.Properties;
    import software.amazon.awssdk.core.SdkBytes;
    import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
    import software.amazon.awssdk.services.ses.model.RawMessage;
    import software.amazon.awssdk.services.ses.model.SesException;
    import org.springframework.stereotype.Component;

    @Component
    public class SendMessages {

    private String sender = "<enter email address>";

    // The subject line for the email.
    private String subject = "Analyzed photos report";

    // The email body for recipients with non-HTML email clients.
    private String bodyText = "Hello,\r\n" + "See the attached file for the analyzed photos report.";

    // The HTML body of the email.
    private String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
            + "<p>Please see the attached file for the report that analyzed photos in the S3 bucket.</p>" + "</body>" + "</html>";

    public void sendReport(InputStream is, String emailAddress ) throws IOException {

        // Convert the InputStream to a byte[].
        byte[] fileContent = IOUtils.toByteArray(is);

        try {
            send(fileContent,emailAddress);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
     }

     public void send(byte[] attachment, String emailAddress) throws MessagingException, IOException {

        MimeMessage message = null;
        Session session = Session.getDefaultInstance(new Properties());

        // Create a new MimeMessage object.
        message = new MimeMessage(session);

        // Add subject, from, and to lines.
        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));

        // Create a multipart/alternative child container.
        MimeMultipart msgBody = new MimeMultipart("alternative");

        // Create a wrapper for the HTML and text parts.
        MimeBodyPart wrap = new MimeBodyPart();

        // Define the text part.
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(bodyText, "text/plain; charset=UTF-8");

        // Define the HTML part.
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

        // Add the text and HTML parts to the child container.
        msgBody.addBodyPart(textPart);
        msgBody.addBodyPart(htmlPart);

        // Add the child container to the wrapper object.
        wrap.setContent(msgBody);

        // Create a multipart/mixed parent container.
        MimeMultipart msg = new MimeMultipart("mixed");

        // Add the parent container to the message.
        message.setContent(msg);

        // Add the multipart/alternative part to the message.
        msg.addBodyPart(wrap);

        // Define the attachment.
        MimeBodyPart att = new MimeBodyPart();
        DataSource fds = new ByteArrayDataSource(attachment, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        att.setDataHandler(new DataHandler(fds));

        String reportName = "PhotoReport.xls";
        att.setFileName(reportName);

        // Add the attachment to the message.
        msg.addBodyPart(att);

        // Try to send the email.
        try {
            System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");

            Region region = Region.US_WEST_2;
            SesClient client = SesClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(region)
                    .build();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());
            byte[] arr = new byte[buf.remaining()];
            buf.get(arr);

            SdkBytes data = SdkBytes.fromByteArray(arr);
            RawMessage rawMessage = RawMessage.builder()
                    .data(data)
                    .build();

            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

            client.sendRawEmail(rawEmailRequest);

        } catch (SesException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Email sent with attachment.");
        }
       }
```

 ### WorkItem class

 The following Java code represents the **WorkItem** class.

```java
     package com.example.photo;

    public class WorkItem {

     private String key;
     private String name;
     private String confidence ;

     public void setKey (String key) {
        this.key = key;
     }

     public String getKey() {
        return this.key;
     }

     public void setName (String name) {
        this.name = name;
     }

     public String getName() {
        return this.name;
     }

     public void setConfidence (String confidence) {
        this.confidence = confidence;
     }

     public String getConfidence() {
        return this.confidence;
      }
     }
```

### WriteExcel class

The following Java code represents the **WriteExcel** class.

```java
package com.example.photo;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

@Component
public class WriteExcel {
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;

    // Returns an InputStream that represents the Excel Report.
    public InputStream exportExcel(List<List<WorkItem>> list) {
        try {
            return write(list);
        } catch (WriteException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Generates the report and returns an inputstream.
    public InputStream write(List<List<WorkItem>> list) throws IOException, WriteException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook = Workbook.createWorkbook(os, wbSettings);

        int size = list.size();
        for (int i = 0; i < size; i++) {
            // Get the WorkItem from each list.
            List<WorkItem> innerList = list.get(i);
            workbook.createSheet("Sheet " + (i + 1), i);
            WritableSheet excelSheet = workbook.getSheet(i);
            createLabel(excelSheet);
            createContent(excelSheet, innerList);
        }

        // Close the workbook.
        workbook.write();
        workbook.close();

        // Get an InputStream that represents the Report.
        byte[] myBytes = os.toByteArray();
        return new ByteArrayInputStream(myBytes);
    }

    // Create Headings in the Excel spreadsheet.
    private void createLabel(WritableSheet sheet) throws WriteException {
        // Create a times font.
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format.
        times = new WritableCellFormat(times10pt);
        // Let's automatically wrap the cells.
        times.setWrap(true);

        // Create a bold font with underlines.
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
            UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);

        // Let's automatically wrap the cells.
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        // Write a few headers.
        addCaption(sheet, 0, 0, "Photo");
        addCaption(sheet, 1, 0, "Label");
        addCaption(sheet, 2, 0, "Confidence");
    }

    // Write the WorkItem Data to the Excel Report.
    private void createContent(WritableSheet sheet, List<WorkItem> list) throws WriteException {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            WorkItem wi = list.get(i);
            String key = wi.getKey();
            String label = wi.getName();
            String confidence = wi.getConfidence();

            // First column.
            addLabel(sheet, 0, i + 1, key);

            // Second column.
            addLabel(sheet, 1, i + 1, label);

            // Third column.
            addLabel(sheet, 2, i + 1, confidence);
        }
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label = new Label(column, row, s, timesBoldUnderline);
        int cc = countString(s);
        sheet.setColumnView(column, cc);
        sheet.addCell(label);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label = new Label(column, row, s, times);
        int cc = countString(s);
        if (cc > 200) {
            sheet.setColumnView(column, 150);
        } else {
            sheet.setColumnView(column, cc + 6);
        }
        sheet.addCell(label);
    }

    private int countString(String ss) {
        int count = 0;
        for (int i = 0; i < ss.length(); i++) {
            if (ss.charAt(i) != ' ') {
                count++;
            }
        }
        return count;
    }
}
```

## Create the HTML files

At this point, you have created all of the Java files required for the AWS Photo Analyzer application. Now you create the HTML files that are required for the application's graphical user interface (GUI). Under the **resource** folder, create a **template** folder, and then create the following HTML files:

+ index.html
+ process.html
+ upload.html
+ layout.html

The **index.html** file is the application's home view. The **process.html** file represents the view for creating a report. The **upload.html** file represents the view for uploading image files to an S3 bucket. The **layout.html** file represents the menu that's visible in all views.

### index.html

The following HTML represents the **index.html** file.

```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">

    <head>
     <meta charset="utf-8" />
     <meta http-equiv="X-UA-Compatible" content="IE=edge" />
     <meta name="viewport" content="width=device-width, initial-scale=1" />
     <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css|"/>
     <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
     <script th:src="|https://code.jquery.com/ui/1.11.4/jquery-ui.min.js|"></script>
     <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
     <link rel="icon" href="../public/images/favicon.ico" th:href="@{/images/favicon.ico}" />

    <title>AWS Photo Analyzer</title>
    </head>
    <body>
    <header th:replace="layout :: site-header"/>
    <div class="container">

    <h2>AWS Asynchronous Photo Analyzer Application</h2>

    <p>The AWS Photo Analyzer application is a sample application that uses the Amazon Rekognition Service as well as other AWS Services and the Java V2 SDK.
        <b>This example application uses the Asynchronous client for the Amazon S3 and Amazon Rekognition Services</b>. Analyzing nature photographs has never been easier! Simply perform these steps:<p>
     <ol>
        <li>Upload a nature photograph to an Amazon S3 bucket by choosing the <b>Upload Photos</b> menu item.</li>
        <li>Choose <b>Choose File</b> and browse to a nature image located on your desktop.</li>
        <li>Choose <b>Upload</b> to upload your image to an S3 bucket.</li>
        <li>Choose <b>Get Images</b> to view the images located in the S3 bucket. All images in the bucket are displayed in the table. </li>
        <li>Analyze the photographs and produce a report by choosing the <b>Analyze Photos</b> menu item. </li>
        <li>Enter an email address in the email field and choose <b>Analyze Photos</b>.  </li>
        <li>Amazon SES is used to send an email with an Excel report to the specified email recipient.</li>
    </ol>
    </div>
    </body>
    </html>
```

### process.html

The following HTML represents the **process.html** file.

```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
     <head>
     <meta charset="utf-8" />
     <meta http-equiv="X-UA-Compatible" content="IE=edge" />
     <meta name="viewport" content="width=device-width, initial-scale=1" />

     <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
     <script th:src="|https://code.jquery.com/ui/1.11.4/jquery-ui.min.js|"></script>
     <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
     <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet"/>
     <script src="../public/js/message.js" th:src="@{/js/message.js}"></script>

     <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
     <link rel="icon" href="../public/images/favicon.ico" th:href="@{/images/favicon.ico}" />

     <title>AWS Photo Analyzer</title>

     <script>
        function myFunction() {
            alert("The form was submitted");
        }
      </script>
      </head>

      <body>
      <header th:replace="layout :: site-header"/>

      <div class="container">

      <h2>AWS Photo Analyzer Application</h2>
      <p>You can generate a report that analyzes the images in the S3 bucket. You can send the report to the following email address. </p>
      <label for="email">Email address:</label><br>
      <input type="text" id="email" name="email" value=""><br>
      <div>
        <br>
        <p>Click the following button to obtain a report</p>
        <button onclick="ProcessImages()">Analyze Photos</button>
       </div>
       <div  id ="bar"  class="progress">
        <div class="progress-bar progress-bar-striped active" role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:90%">
            Generating Report
         </div>
        </div>
       <div>
         <h3>Download a photo to your browser</h3>
         <p>Specify the photo to download from an Amazon S3 bucket</p>
         <label for="photo">Photo Name:"</label><br>
         <input type="text" id="photo" name="photo" value=""><br>
         <p>Click the following button to download a photo</p>
         <button onclick="DownloadImage()">Download Photo</button>
      </div>
     </div>
     </body>
    </html>
```

### upload.html

The following HTML represents the **upload.html** file.

```html
    <!DOCTYPE html>
    <html xmlns:th="http://www.thymeleaf.org">
    <head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
    <script th:src="|https://code.jquery.com/ui/1.11.4/jquery-ui.min.js|"></script>
    <script th:src="|https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.js|"></script>
    <script src="../public/js/items.js" th:src="@{/js/items.js}"></script>

    <link rel="stylesheet" th:href="|https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css|"/>
    <link rel="stylesheet" th:href="|https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.css|"/>
    <link rel="stylesheet" href="../public/css/styles.css" th:href="@{/css/styles.css}" />
    <link rel="icon" href="../public/images/favicon.ico" th:href="@{/images/favicon.ico}" />


    <title>AWS Photo Analyzer</title>

    <script>
        function myFunction() {
            alert("The form was submitted");
        }
    </script>
    </head>

    <body>
    <header th:replace="layout :: site-header"/>

    <div class="container">
     <h2>AWS Photo Analyzer application</h2>
     <p>Upload images to an Amazon S3 bucket. Each image will be analyzed!</p>

     <form method="POST" onsubmit="myFunction()" action="/upload" enctype="multipart/form-data">
      <input type="file" name="file" /><br/><br/>
      <input type="submit" value="Submit" />
     </form>
    <div>
    <br>

    <p>Choose the following button to determine the number of images in the bucket.</p>

    <button onclick="getImages()">Get Images</button>
    <div  id ="bars3"  class="progress">
        <div class="progress-bar progress-bar-striped active" role="progressbar"
             aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width:90%">
            Retrieving Amazon S3 images...
        </div>
    </div>
    <table id="myTable" class="display" style="width:100%">
        <thead>
        <tr>
            <th>Name</th>
            <th>Owner</th>
            <th>Date</th>
            <th>Size</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>No Data</td>
            <td>No Data</td>
            <td>No Data </td>
            <td>No Data</td>
        </tr>
        </tbody>
        <tfoot>
        <tr>
            <th>Name</th>
            <th>Owner</th>
            <th>Date</th>
            <th>Size</th>
        </tr>
        </tfoot>
        <div id="success3"></div>
    </table>
    </div>
    </div>
    </body>
    </html>
```

### layout.html

The following HTML represents the **layout.html** file for the application's menu.

```html
     <!DOCTYPE html>
     <html xmlns:th="http://www.thymeleaf.org">
     <head th:fragment="site-head">
      <meta charset="UTF-8" />
      <link rel="icon" href="../public/images/favicon.ico" th:href="@{/images/favicon.ico}" />
      <script th:src="|https://code.jquery.com/jquery-1.12.4.min.js|"></script>
      <meta th:include="this :: head" th:remove="tag"/>
     </head>
     <body>
      <!-- th:hef calls a controller method - which returns the view -->
      <header th:fragment="site-header">
       <a href="#" style="color: white" th:href="@{/}">Home</a>
       <a href="#" style="color: white" th:href="@{/photo}">Upload Photos</a>
       <a href="#"  style="color: white" th:href="@{/process}">Analyze Photos</a>
      </header>
     </html>
```

## Create script files

Both the upload and process views use script files to communicate with the Spring controller. You have to ensure that these files are part of your project; otherwise, your application won't work.

+ items.js
+ message.js

Both files contain application logic that sends a request to the Spring controller. In addition, these files handle the response and set the data in the view.

### items.js

The following JavaScript represents the **items.js** file.

```javascript
    $(function() {

    $("#bars3").hide()
    $('#myTable').DataTable( {
        scrollY:        "500px",
        scrollX:        true,
        scrollCollapse: true,
        paging:         true,
        columnDefs: [
            { width: 200, targets: 0 }
        ],
        fixedColumns: true
     } );
    } );

   function getImages() {

    $("#bars3").show()

      $.ajax('/getimages', {
        type: 'GET',  // http method
        success: function (data, status, xhr) {

            $("#bars3").hide()
            var xml = data
            var oTable = $('#myTable').dataTable();
            oTable.fnClearTable(true);
            $(xml).find('Item').each(function () {

                var $field = $(this);
                var key = $field.find('Key').text();
                var name = $field.find('Owner').text();
                var date = $field.find('Date').text();
                var size = $field.find('Size').text();

                //Set the new data
                oTable.fnAddData( [
                    key,
                    name,
                    date,
                    size]
                );
            });
        },
      });
    }

```

### message.js

The following JavaScript represents the **message.js** file. The **ProcessImages** function sends a request to the **/report** handler in the controller that generates a report. Notice that an email address is posted to the **Controller** method.

```javascript
    $(function() {

    $("#bar").hide()


   } );

  function ProcessImages() {

    //Post the values to the controller
    $("#bar").show()
    var email =  $('#email').val();

    $.ajax('/report', {
        type: 'POST',  // http method
        data: 'email=' + email ,  // data to submit
        success: function (data, status, xhr) {

            $("#bar").hide()
             alert(data) ;
        },
        error: function (jqXhr, textStatus, errorMessage) {
            $('p').append('Error' + errorMessage);
        }
    });
    }

    function DownloadImage(){

     //Post the values to the controller
     var photo =  $('#photo').val();
     window.location="../downloadphoto?photoKey=" + photo ;
    }

```

**Note:** There are other CSS files located in the GitHub repository that you must add to your project. Ensure all of the files under the **resources** folder are included in your project.   

## Run the application

Using the IntelliJ IDE, you can run your application. The first time you run the Spring Boot application, you can run the application by clicking the run icon in the Spring Boot main class, as shown in this illustration. 

![AWS Tracking Application](images/run.png)

**Note**: You can deploy this Spring Boot application by using AWS Elastic Beanstalk. For information, see the following document [Creating your first AWS Java web application](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/usecases/creating_first_project).

### Next steps
Congratulations! You have created and deployed the AWS Photo Analyzer application. As stated at the beginning of this tutorial, be sure to terminate all of the resources you create while going through this tutorial to ensure that you’re no longer charged for them.

For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).
