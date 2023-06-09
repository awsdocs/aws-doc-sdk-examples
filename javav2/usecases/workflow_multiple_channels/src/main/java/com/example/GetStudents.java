/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetStudents {

    private DynamoDbClient getDynamoDBClient() {
        Region region = Region.US_WEST_2;
        return DynamoDbClient.builder()
            .region(region)
            .build();
    }

    public String getStudentsData(String date) {
        DynamoDbClient ddbClient = getDynamoDBClient();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddbClient)
            .build();

        DynamoDbTable<StudentData> table = enhancedClient.table("Students", TableSchema.fromBean(StudentData.class));
        AttributeValue attr = AttributeValue.builder()
            .s(date)
            .build();

        Map<String, AttributeValue> myMap = new HashMap<>();
        myMap.put(":val1",attr);

        Map<String, String> myExMap = new HashMap<>();
        myExMap.put("#mydate", "date");

        // Set the Expression so only active items are queried from the Work table.
        Expression expression = Expression.builder()
            .expressionValues(myMap)
            .expressionNames(myExMap)
            .expression("#mydate = :val1")
            .build();

        ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
            .filterExpression(expression)
            .limit(15)
            .build();

        List<Student> studentList = new ArrayList<>();
        for (StudentData singleStudent : table.scan(enhancedRequest).items()) {
            Student student = new Student();
            student.setFirstName(singleStudent.getFirstName());
            student.setMobileNumber(singleStudent.getMobileNumber());
            student.setEmail(singleStudent.getEmail());

            // Push the Student object to the list.
            studentList.add(student);
        }
        return convertToString(toXml(studentList));
    }

    // Convert the list to XML.
    private Document toXml(List<Student> itemList) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement( "Students" );
            doc.appendChild( root );

            // Loop through the list.
            for (Student myStudent: itemList) {
                Element item = doc.createElement( "Student" );
                root.appendChild( item );

                // Set Name.
                Element name = doc.createElement( "Name" );
                name.appendChild( doc.createTextNode(myStudent.getFirstName()) );
                item.appendChild( name );

                // Set Mobile.
                Element mobile = doc.createElement( "Mobile" );
                mobile.appendChild( doc.createTextNode(myStudent.getMobileNumber()) );
                item.appendChild( mobile );

                // Set Email.
                Element email = doc.createElement( "Email" );
                email.appendChild( doc.createTextNode(myStudent.getEmail() ) );
                item.appendChild( email );
            }

         return doc;
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertToString(Document xmlDocument) {
        try {
            TransformerFactory transformerFactory = getSecureTransformerFactory();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xmlDocument);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch(TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private TransformerFactory getSecureTransformerFactory() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        return transformerFactory;
    }
}
