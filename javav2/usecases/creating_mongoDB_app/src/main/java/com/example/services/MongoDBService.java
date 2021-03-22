/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.services;

import com.example.entities.WorkItem;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoException;
import org.springframework.stereotype.Component;
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
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.Iterator;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

@Component
public class MongoDBService {

    private String mongoUri = "mongodb://<URL TO EC2 Hosting MongoDB>.com:27017" ;

    private MongoClient getConnection() {

        try {
            MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoUri));
            return mongoClient;

        } catch (UnknownHostException e) {
            e.getStackTrace();
        }
        return null;
    }

    private String now() {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    // Put an item into the MongoBD collection.
    public void putRecord(WorkItem item) {

        try {

            // Create a MongoClient object.
            MongoClient mongoClient = getConnection();
            String myGuid = java.util.UUID.randomUUID().toString();

            // Populate the collection.
            DB database = mongoClient.getDB("local");

            BasicDBObject document = new BasicDBObject();
            document.put("_id", myGuid);
            document.put("archive", "Open");
            document.put("date", now());
            document.put("description", item.getDescription());
            document.put("guide", item.getGuide());
            document.put("status", item.getStatus());
            document.put("username", item.getName());

            database.getCollection("items").insert(document);

        } catch (MongoException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Updates an item in the collection.
    public void updateItemId(String id, String status) {

        try {

            MongoClient mongoClient = getConnection();
            DB database = mongoClient.getDB("local");

            DBCollection collection = database.getCollection("items");
            DBObject ob1 = collection.findOne(id);
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.append("$set", new BasicDBObject().append("status", status));

            collection.update(ob1, newDocument);

        } catch (MongoException e) {
            System.out.println(e.getMessage());
        }
    }

    public String findDocumentById(String id) {

        try {

            MongoClient mongoClient = getConnection();

            // Get the database name
            DB database = mongoClient.getDB("local");

            DBCollection collection = database.getCollection("items");

            // Return an item based on the ID -- need this for Item Tracker
            DBObject ob1 = collection.findOne(id);
            Set<String> keys = ob1.keySet();
            Iterator iterator = keys.iterator();

            ArrayList itemList = new ArrayList();
            WorkItem item = null;
            item = new WorkItem();
            while(iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = (String) ob1.get(key).toString();
                if (key.compareTo("_id") == 0)
                    item.setId(value);
                else if (key.compareTo("archive") == 0)
                    item.setArc(value);
                else if (key.compareTo("date") == 0)
                    item.setDate(value);
                else if (key.compareTo("description") == 0)
                    item.setDescription(value);
                else if (key.compareTo("guide") == 0)
                    item.setGuide(value);
                else if (key.compareTo("status") == 0)
                    item.setStatus(value);

                else if (key.compareTo("username") == 0) {
                    item.setName(value);
                    itemList.add(item); // last item read
                }
            }
            return convertToString(toXml(itemList));
        } catch (MongoException e){

            System.out.println(e.getMessage());
        }
        return "";
    }

    // Retrieves all items from MongoDB.
    public String getAllItems() {

        MongoClient mongoClient = getConnection();

        List<String> databases = mongoClient.getDatabaseNames();
        for (String db: databases) {
            System.out.println("Database name is: "+db);
        }

        return "";
    }

    // Retrieves all items from MongoDB
    public String getListItems() {

        MongoClient mongoClient = getConnection();

        // Get the database name
        DB database = mongoClient.getDB("local");
        DBCursor cur = database.getCollection("items").find();
        DBObject dbo = null;
        ArrayList<WorkItem> itemList = new ArrayList();
        WorkItem item = null;
        int index = 0;
        while (cur.hasNext()) {

            index = 0;
            item = new WorkItem();
            dbo = cur.next();
            Set<String> keys = dbo.keySet();

            for (String key : keys) {
                String value = (String) dbo.get(key).toString();
                if (key.compareTo("_id") == 0)
                    item.setId(value);
                else if (key.compareTo("archive") == 0)
                    item.setArc(value);
                else if (key.compareTo("date") == 0)
                    item.setDate(value);
                else if (key.compareTo("description") == 0)
                    item.setDescription(value);
                else if (key.compareTo("guide") == 0)
                    item.setGuide(value);
                else if (key.compareTo("status") == 0)
                    item.setStatus(value);

                else if (key.compareTo("username") == 0) {
                    item.setName(value);
                    itemList.add(item); // last item read
                }
            }
        } // end of while

        return convertToString(toXml(itemList));
    }

    // Retrieves all items from MongoDB
    public ArrayList<WorkItem> getListItemsReport() {

        MongoClient mongoClient = getConnection();

        // Get the database name
        DB database = mongoClient.getDB("local");

        DBCursor cur = database.getCollection("items").find();

        DBObject dbo = null;
        ArrayList<WorkItem> itemList = new ArrayList();
        WorkItem item = null;
        int index = 0;
        while (cur.hasNext()) {

            index = 0;
            item = new WorkItem();
            dbo = cur.next();
            Set<String> keys = dbo.keySet();
            Iterator iterator = keys.iterator();


            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = (String) dbo.get(key).toString();
                if (key.compareTo("_id") == 0)
                    item.setId(value);
                else if (key.compareTo("archive") == 0)
                    item.setArc(value);
                else if (key.compareTo("date") == 0)
                    item.setDate(value);
                else if (key.compareTo("description") == 0)
                    item.setDescription(value);
                else if (key.compareTo("guide") == 0)
                    item.setGuide(value);
                else if (key.compareTo("status") == 0)
                    item.setStatus(value);

                else if (key.compareTo("username") == 0) {
                    item.setName(value);
                    itemList.add(item); // last item read
                }
            }
        } // end of while


        return itemList;
    }

    // Convert Work item data into XML to pass back to the view.
    private Document toXml(List<WorkItem> itemList) {

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
                WorkItem myItem = itemList.get(index);

                Element item = doc.createElement( "Item" );
                root.appendChild( item );

                // Set Id.
                Element id = doc.createElement( "Id" );
                id.appendChild( doc.createTextNode(myItem.getId() ) );
                item.appendChild( id );

                // Set Name.
                Element name = doc.createElement( "Name" );
                name.appendChild( doc.createTextNode(myItem.getName() ) );
                item.appendChild( name );

                // Set Date.
                Element date = doc.createElement( "Date" );
                date.appendChild( doc.createTextNode(myItem.getDate() ) );
                item.appendChild( date );

                // Set Description.
                Element desc = doc.createElement( "Description" );
                desc.appendChild( doc.createTextNode(myItem.getDescription() ) );
                item.appendChild( desc );

                // Set Guide.
                Element guide = doc.createElement( "Guide" );
                guide.appendChild( doc.createTextNode(myItem.getGuide() ) );
                item.appendChild( guide );

                // Set Status.
                Element status = doc.createElement( "Status" );
                status.appendChild( doc.createTextNode(myItem.getStatus() ) );
                item.appendChild( status );
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
