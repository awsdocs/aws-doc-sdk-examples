package com.example.demo.services;

import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.example.demo.entities.WorkItem;
import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

@Component
public class MongoDBService {

 private String mongoUri = "mongodb://127.0.0.1:27017/?directConnection=true&serverSelectionTimeoutMS=2000&appName=mongosh+1.10.0";

 private MongoClient getConnection() throws UnknownHostException {

	 MongoClient mongoClient = MongoClients.create(new ConnectionString(mongoUri));
	 return mongoClient;
 }

 private String now() {
     String dateFormat = "yyyy-MM-dd HH:mm:ss";
     Calendar cal = Calendar.getInstance();
     SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
     return sdf.format(cal.getTime());
 }

 // Put an item into the MongoBD collection.
 public void putRecord(WorkItem item) throws UnknownHostException {

     try {

         // Create a MongoClient object.
         MongoClient mongoClient = getConnection();
         String myGuid = java.util.UUID.randomUUID().toString();

         // Populate the collection.
         MongoDatabase database = mongoClient.getDatabase("local");

         org.bson.Document document = new org.bson.Document();
         document.put("_id", myGuid);
         document.put("archive", "Open");
         document.put("date", now());
         document.put("description", item.getDescription());
         document.put("guide", item.getGuide());
         document.put("status", item.getStatus());
         document.put("username", item.getName());
         database.getCollection("items").insertOne(document);

     } catch (MongoException e) {
         System.err.println(e.getMessage());
         System.exit(1);
     }
 }

 // Updates an item in the collection.
 public void updateItemId(String id, String status) throws UnknownHostException {

     try {

         MongoClient mongoClient = getConnection();
         MongoDatabase database = mongoClient.getDatabase("local");        
         org.bson.Document document = (org.bson.Document) database.getCollection("items").
        		 find(new org.bson.Document("_id", id)).first();        
         org.bson.Document newDocument = new  org.bson.Document();
         newDocument.append("$set", new  org.bson.Document().append("status", status));
         database.getCollection("items").updateMany((Bson) document, newDocument);

     } catch (MongoException e) {
         System.out.println(e.getMessage());
     }
 }

 public String findDocumentById(String id) throws UnknownHostException {

     try {
         MongoClient mongoClient = getConnection();

         // Get the database name
         MongoDatabase database = mongoClient.getDatabase("local");    
         FindIterable<org.bson.Document> document = (FindIterable<org.bson.Document>) database.getCollection("items").
        		 find(new org.bson.Document("_id", id));
         // Return an item based on the ID -- need this for Item Tracker
         MongoCursor<org.bson.Document> cur = document.cursor();
         ArrayList<WorkItem> itemList = new ArrayList<WorkItem>();
         WorkItem item = new WorkItem();
         org.bson.Document doc = null;
         while(cur.hasNext()) {
             doc = cur.next();
             Set<String> keys = doc.keySet();
             for(String key : keys)
             {
            	 String value = (String) doc.get(key).toString();
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
         }
         return convertToString(toXml(itemList));
     } catch (MongoException e){

         System.out.println(e.getMessage());
     }
     return "";
 }

 // Retrieves all items from MongoDB.
 public String getAllItems() throws UnknownHostException {

     MongoClient mongoClient = getConnection();

     List<String> databases = (List<String>) mongoClient.listDatabaseNames();
     for (String db: databases) {
         System.out.println("Database name is: "+db);
     }

     return "";
 }

 // Retrieves all items from MongoDB
 public String getListItems() throws UnknownHostException {

     MongoClient mongoClient = getConnection();

     // Get the database name
     MongoDatabase database = mongoClient.getDatabase("local");
     FindIterable<org.bson.Document> document = (FindIterable<org.bson.Document>) database.getCollection("items").find();
     MongoCursor<org.bson.Document> cur = document.cursor();
     org.bson.Document doc = null;
     ArrayList<WorkItem> itemList = new ArrayList();
     WorkItem item = null;
     while (cur.hasNext()) {
         item = new WorkItem();
         doc = cur.next();
         Set<String> keys = doc.keySet();
         for (String key : keys) {
             String value = (String) doc.get(key).toString();
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
 public ArrayList<WorkItem> getListItemsReport() throws UnknownHostException {

     MongoClient mongoClient = getConnection();

     // Get the database name
     MongoDatabase database = mongoClient.getDatabase("local");
     FindIterable<org.bson.Document> document = (FindIterable<org.bson.Document>) database.getCollection("items").find();
     MongoCursor<org.bson.Document> cur = document.cursor();
     org.bson.Document dbo = null;
     ArrayList<WorkItem> itemList = new ArrayList();
     WorkItem item = null;
     while (cur.hasNext()) {
         item = new WorkItem();
         dbo = (org.bson.Document) cur.next();
         Set<String> keys = dbo.keySet();
         Iterator<String> iterator = keys.iterator();
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
