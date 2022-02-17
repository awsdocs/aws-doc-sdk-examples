/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.messages;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RDSGetStudents {

    public String getStudentsRDS(String date ) throws SQLException {

        Connection c = null;
        String query = "";

        try {

            c = ConnectionHelper.getConnection();
            ResultSet rs = null;

            // Use prepared statements.
            PreparedStatement pstmt = null;
            query = "Select first, phone, mobile, email FROM students where date = '" +date +"'";
            pstmt = c.prepareStatement(query);
            rs = pstmt.executeQuery();

            List<Student> studentList = new ArrayList<>();
            while (rs.next()) {

                Student student = new Student();

                String name = rs.getString(1);
                String phone = rs.getString(2);
                String mobile = rs.getString(3);
                String email = rs.getString(4);

                student.setFirstName(name);
                student.setMobileNumber(mobile);
                student.setPhoneNunber(phone);
                student.setEmail(email);

                // Push the Student object to the list.
                studentList.add(student);
            }

            return convertToString(toXml(studentList));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            c.close();
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


    // Convert the list to XML.
    private Document toXml(List<Student> itemList) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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

                // Set Phone.
                Element phone = doc.createElement( "Phone" );
                phone.appendChild( doc.createTextNode(myStudent.getPhoneNunber() ) );
                item.appendChild( phone );

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
}
