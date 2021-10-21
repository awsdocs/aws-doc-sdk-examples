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
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class S3Service {

    S3AsyncClient s3AsyncClient;

    // Create a  thread-safe, object.
    private S3AsyncClient getClient() {

        Region region = Region.US_WEST_2;

        return S3AsyncClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
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

            // Get the Object from the Amazon S3 bucket using the Amazon S3 Async Client
            final CompletableFuture<ResponseBytes<GetObjectResponse>>[] futureGet = new CompletableFuture[]{s3AsyncClient.getObject(objectRequest,
                    AsyncResponseTransformer.toBytes())};

            futureGet[0].whenComplete((resp, err) -> {
                try {

                    if (resp != null) {

                        //  Set the AtomicReference object
                         reference.set(resp.asByteArray());

                    } else {
                        err.printStackTrace();
                    }
                } finally {

                    // Only close the client when you are completely done with it
                    s3AsyncClient.close();
                }
            });
            futureGet[0].join();

            // Read the AtomicReference object and return the byte[] value
            return reference.get();

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    // Returns the names of all images in the given bucket.
    public List ListBucketObjects(String bucketName) {

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

                    List keys = new ArrayList<String>();
                    String keyName ;
                    if (resp != null) {

                        List<S3Object> objects = resp.contents();
                        for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext(); ) {
                            S3Object myValue = (S3Object) iterVals.next();
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
            List keys2 = reference.get();
            return keys2;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null ;
    }

    // Places an image into a S3 bucket.
    public String putObject(byte[] data, String bucketName, String objectKey) {

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

        return objectKey +" was successfully uploaded into the Amazon S3 bucket.";

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    // Returns the names of all images and data within an XML document.
    public String ListAllObjects(String bucketName) {

        s3AsyncClient = getClient();
        final AtomicReference<List<BucketItem>> reference = new AtomicReference<>();


        List bucketItems = new ArrayList<BucketItem>();
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

                            // Push the items to the list
                            bucketItems.add(myItem);
                        }

                        reference.set(bucketItems) ;

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

            List<BucketItem> bucketItems2 =  reference.get();
            return convertToString(toXml(bucketItems2));

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
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement( "Items" );
            doc.appendChild( root );

            // Get the elements from the collection.
            int custCount = itemList.size();

            // Iterate through the collection.
            for ( int index=0; index < custCount; index++) {

                // Get the WorkItem object from the collection.
                BucketItem myItem = itemList.get(index);

                Element item = doc.createElement( "Item" );
                root.appendChild( item );

                // Set Key.
                Element id = doc.createElement( "Key" );
                id.appendChild( doc.createTextNode(myItem.getKey()) );
                item.appendChild( id );

                // Set Owner.
                Element name = doc.createElement( "Owner" );
                name.appendChild( doc.createTextNode(myItem.getOwner() ) );
                item.appendChild( name );

                // Set Date.
                Element date = doc.createElement( "Date" );
                date.appendChild( doc.createTextNode(myItem.getDate() ) );
                item.appendChild( date );

                // Set Size.
                Element desc = doc.createElement( "Size" );
                desc.appendChild( doc.createTextNode(myItem.getSize() ) );
                item.appendChild( desc );
            }

            return doc;
        } catch(ParserConfigurationException e) {
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
