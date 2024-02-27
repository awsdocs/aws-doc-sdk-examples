// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.Tag;
import org.w3c.dom.Document;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedOutputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.http.HttpHeaders;

@Service
public class VideoStreamService {

    public static final String VIDEO_CONTENT = "video/";

    private S3Client getClient() {

        return S3Client.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.US_WEST_2)
            .build();
    }

    // Places a new video into an Amazon S3 bucket.
    public void putVideo(byte[] bytes, String bucketName, String fileName, String description) {
        S3Client s3 = getClient();
        try {
            // Set the tags to apply to the object.
            String theTags = "name="+fileName+"&description="+description;

            PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .tagging(theTags)
                .build();

            s3.putObject(putOb, RequestBody.fromBytes(bytes));

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    // Returns a schema that describes all tags for all videos in the given bucket.
    public String getTags(String bucketName){
        S3Client s3 = getClient();

        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket(bucketName)
                .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            List<String> keys = new ArrayList<>();
            for (S3Object myValue: objects) {
                String key = myValue.key(); // We need the key to get the tags.
                GetObjectTaggingRequest getTaggingRequest = GetObjectTaggingRequest.builder()
                    .key(key)
                    .bucket(bucketName)
                    .build();

                GetObjectTaggingResponse tags = s3.getObjectTagging(getTaggingRequest);
                List<Tag> tagSet= tags.tagSet();
                for (Tag tag : tagSet) {
                    keys.add(tag.value());
                }
            }

            List<Tags> tagList = modList(keys);
            return convertToString(toXml(tagList));

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    // Return a List where each element is a Tags object.
    private List<Tags> modList(List<String> myList) {
        int count = myList.size();
        return IntStream.range(0, count / 2)
            .mapToObj(index -> {
                Tags myTag = new Tags();
                myTag.setName(myList.get(index * 2));
                myTag.setDesc(myList.get(index * 2 + 1));
                return myTag;
            })
            .collect(Collectors.toList());
    }


    // Reads a video from a bucket and returns a ResponseEntity.
    public ResponseEntity<StreamingResponseBody> getObjectBytes(String bucketName, String keyName) {
        S3Client s3 = getClient();
        try {
            // Create an S3 object request.
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

            // Get the S3 object stream.
            ResponseInputStream<GetObjectResponse> objectStream = s3.getObject(objectRequest);

            // Set content type and length headers.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(VIDEO_CONTENT + "mp4"));

            // Set content length if available.
            Long contentLength = objectStream.response().contentLength();
            if (contentLength != null) {
                headers.setContentLength(contentLength);
            }

            // Set disposition as inline to display content in the browser.
            headers.setContentDispositionFormData("inline", keyName);

            // Create a StreamingResponseBody to stream the content.
            StreamingResponseBody responseBody = outputStream -> {
                try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                    byte[] buffer = new byte[1024 * 1024];
                    int bytesRead;
                    while ((bytesRead = objectStream.read(buffer)) != -1) {
                        bufferedOutputStream.write(buffer, 0, bytesRead);
                    }
                    bufferedOutputStream.flush();
                } finally {
                    objectStream.close();
                }
            };

            return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return an appropriate ResponseEntity.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Convert a LIST to XML data.
    private Document toXml(List<Tags> itemList) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement( "Tags" );
            doc.appendChild( root );

            // Iterate through the list.
            for (Tags myItem: itemList) {
                Element item = doc.createElement( "Tag" );
                root.appendChild( item );

                // Set Name.
                Element id = doc.createElement( "Name" );
                id.appendChild( doc.createTextNode(myItem.getName() ) );
                item.appendChild( id );

                // Set Description.
                Element name = doc.createElement( "Description" );
                name.appendChild( doc.createTextNode(myItem.getDesc() ) );
                item.appendChild( name );
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
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch (TransformerException ex) {
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