/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rds;

import java.io.StringWriter;
import java.util.ArrayList ;
import java.util.List;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Component
public class RetrieveItems {

    private String secretArn = "arn:aws:secretsmanager:us-east-1:814548047983:secret:sqlscott2-WEJX1b" ;
    private String resourceArn = "arn:aws:rds:us-east-1:814548047983:cluster:database-4" ;

    private RdsDataClient getClient() {

        Region region = Region.US_EAST_1;
        RdsDataClient dataClient = RdsDataClient.builder()
                .region(region)
                .build();

        return dataClient;
    }

    // Retrieves archive data from the database.
    public String getArchiveData(String username) {

        RdsDataClient dataClient = getClient();
        int arch = 1;
        List<WorkItem>records = new ArrayList<>();

        try {

            String  sqlStatement = "Select * FROM work where username = '" +username +"' and archive = " + arch +"";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(sqlStatement)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();

            ExecuteStatementResponse response = dataClient.executeStatement(sqlRequest);
            List<List<Field>> dataList = response.records();

            WorkItem workItem ;
            int index = 0 ;

            // Get the records.
            for (List list: dataList) {

                // New WorkItem object.
                workItem = new WorkItem();
                index = 0;
                for (Object myField : list) {

                    Field field = (Field) myField;
                    String value = field.stringValue();

                    if (index == 0)
                        workItem.setId(value);

                    else if (index == 1)
                        workItem.setDate(value);

                    else if (index == 2)
                        workItem.setDescription(value);

                    else if (index == 3)
                        workItem.setGuide(value);

                    else if (index == 4)
                        workItem.setStatus(value);

                    else if (index == 5)
                        workItem.setName(value);

                    // Increment the index.
                    index++;
                }

                // Push the object to the List.
                records.add(workItem);
            }

            return convertToString(toXml(records));

        } catch (RdsDataException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void flipItemArchive(String id ) {

        RdsDataClient dataClient = getClient();
        int arc = 1;

        try {
            // Specify the SQL statement to query data.
            String sqlStatement = "update work set archive = '"+arc+"' where idwork ='" +id + "' ";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(sqlStatement)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();

            dataClient.executeStatement(sqlRequest);
        } catch (RdsDataException e) {
            e.printStackTrace();
        }
    }

    // Retrieves an item based on the ID.
    public String getItemSQL(String id ) {

        RdsDataClient dataClient = getClient();

        // Define a list in which all work items are stored.
        String sqlStatement = "";
        String status="" ;
        String description="";

        try {

            //Specify the SQL statement to query data.
            sqlStatement = "Select description, status FROM work where idwork ='" +id + "' ";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(sqlStatement)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();

            ExecuteStatementResponse response = dataClient.executeStatement(sqlRequest);
            List<List<Field>> dataList = response.records();
            int index = 0 ;
            // Get the records.
            for (List list: dataList) {

                for (Object myField : list) {

                    Field field = (Field) myField;
                    String value = field.stringValue();

                    if (index == 0)
                        description  = (value);

                    else if (index == 1)
                        status = value;
                    // Increment the index.
                    index++;
                }
            }
            return convertToString(toXmlItem(id,description,status));

        } catch (RdsDataException e) {
            e.printStackTrace();
        } finally {
            //ConnectionHelper.close(c);
        }
        return null;
    }

    // Get Items data from the database.
    public List<WorkItem> getItemsDataSQLReport(String username) {

        RdsDataClient dataClient = getClient();
        int arch = 0;
        List<WorkItem>records = new ArrayList<>();

        try {
            String  sqlStatement = "Select * FROM work where username = '" +username +"' and archive = " + arch +"";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(sqlStatement)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();

            ExecuteStatementResponse response = dataClient.executeStatement(sqlRequest);
            List<List<Field>> dataList = response.records();

            WorkItem workItem ;
            int index = 0 ;

            // Get the records.
            for (List list: dataList) {

                // New WorkItem object.
                workItem = new WorkItem();
                index = 0;
                for (Object myField : list) {

                    Field field = (Field) myField;
                    String value = field.stringValue();

                    if (index == 0)
                        workItem.setId(value);

                    else if (index == 1)
                        workItem.setDate(value);

                    else if (index == 2)
                        workItem.setDescription(value);

                    else if (index == 3)
                        workItem.setGuide(value);

                    else if (index == 4)
                        workItem.setStatus(value);

                    else if (index == 5)
                        workItem.setName(value);

                    // Increment the index.
                    index++;
                }

                // Push the object to the List.
                records.add(workItem);
            }

            return records;

        } catch (RdsDataException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Get Items Data from the database.
    public String getItemsDataSQL(String username) {

        RdsDataClient dataClient = getClient();
        int arch = 0;
        List<WorkItem>records = new ArrayList<>();

        try {
            String  sqlStatement = "Select * FROM work where username = '" +username +"' and archive = " + arch +"";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(sqlStatement)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();

            ExecuteStatementResponse response = dataClient.executeStatement(sqlRequest);
            List<List<Field>> dataList = response.records();

            WorkItem workItem ;
            int index = 0 ;

            // Get the records.
            for (List list: dataList) {

                // New WorkItem object.
                workItem = new WorkItem();
                index = 0;
                for (Object myField : list) {

                    Field field = (Field) myField;
                    String value = field.stringValue();

                    if (index == 0)
                        workItem.setId(value);

                    else if (index == 1)
                        workItem.setDate(value);

                    else if (index == 2)
                        workItem.setDescription(value);


                    else if (index == 3)
                        workItem.setGuide(value);

                    else if (index == 4)
                        workItem.setStatus(value);

                    else if (index == 5)
                        workItem.setName(value);

                    // Increment the index.
                    index++;
                }

                // Push the object to the List.
                records.add(workItem);
            }

            return convertToString(toXml(records));

        } catch (RdsDataException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Convert Work item data into XML to pass back to client.
    private Document toXml(List<WorkItem> itemList) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML
            Element root = doc.createElement( "Items" );
            doc.appendChild( root );

            // Iterate through the collection
            for (WorkItem myItem : itemList) {

                // Get the WorkItem object from the collection
                Element item = doc.createElement("Item");
                root.appendChild(item);

                // Set Id
                Element id = doc.createElement("Id");
                id.appendChild(doc.createTextNode(myItem.getId()));
                item.appendChild(id);

                // Set Name
                Element name = doc.createElement("Name");
                name.appendChild(doc.createTextNode(myItem.getName()));
                item.appendChild(name);

                // Set Date
                Element date = doc.createElement("Date");
                date.appendChild(doc.createTextNode(myItem.getDate()));
                item.appendChild(date);

                // Set Description
                Element desc = doc.createElement("Description");
                desc.appendChild(doc.createTextNode(myItem.getDescription()));
                item.appendChild(desc);

                // Set Guide
                Element guide = doc.createElement("Guide");
                guide.appendChild(doc.createTextNode(myItem.getGuide()));
                item.appendChild(guide);

                // Set Status
                Element status = doc.createElement("Status");
                status.appendChild(doc.createTextNode(myItem.getStatus()));
                item.appendChild(status);
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

    // Convert Work item data into XML to pass back to client.
    private Document toXmlItem(String id2, String desc2, String status2) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML
            Element root = doc.createElement( "Items" );
            doc.appendChild( root );

            Element item = doc.createElement( "Item" );
            root.appendChild( item );

            // Set Id
            Element id = doc.createElement( "Id" );
            id.appendChild( doc.createTextNode(id2 ) );
            item.appendChild( id );

            // Set Description
            Element desc = doc.createElement( "Description" );
            desc.appendChild( doc.createTextNode(desc2 ) );
            item.appendChild( desc );

            // Set Status
            Element status = doc.createElement( "Status" );
            status.appendChild( doc.createTextNode(status2 ) );
            item.appendChild( status );

            return doc;

        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}