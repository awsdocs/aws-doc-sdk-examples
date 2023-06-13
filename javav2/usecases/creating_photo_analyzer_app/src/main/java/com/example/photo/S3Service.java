/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.photo;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
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

@Component
public class S3Service {

    S3Client s3 ;

    // Create the S3Client object.
    private S3Client getClient() {
        return S3Client.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_WEST_2)
                .build();
    }

    // Get the byte[] from this Amazon S3 object.
    public byte[] getObjectBytes (String bucketName, String keyName) {
        s3 = getClient();
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            return objectBytes.asByteArray();

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    // Returns the names of all images and data within an XML document.
    public String ListAllObjects(String bucketName) {
        s3 = getClient();
        long sizeLg;
        Instant DateIn;
        BucketItem myItem ;

        List<BucketItem> bucketItems = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();

            for (S3Object myValue : objects) {
                myItem = new BucketItem();
                myItem.setKey(myValue.key());
                myItem.setOwner(myValue.owner().displayName());
                sizeLg = myValue.size() / 1024;
                myItem.setSize(String.valueOf(sizeLg));
                DateIn = myValue.lastModified();
                myItem.setDate(String.valueOf(DateIn));

                // Push the items to the list.
                bucketItems.add(myItem);
            }

            return convertToString(toXml(bucketItems));

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null ;
    }

    // Returns the names of all images in the given bucket.
    public ArrayList<String> ListBucketObjects(String bucketName) {
        s3 = getClient();
        String keyName ;
        ArrayList<String> keys = new ArrayList<String>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                keyName = myValue.key();
                keys.add(keyName);
            }
            return keys;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null ;
    }


    // Places an image into a S3 bucket.
    public void putObject(byte[] data, String bucketName, String objectKey) {
        s3 = getClient();
        try {
            s3.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                    .key(objectKey)
                    .build(),
                RequestBody.fromBytes(data));

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
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
                // Get the WorkItem object from the collection.
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
