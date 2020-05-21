package com.aws.jdbc;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList ;
import java.util.List;
import com.aws.entities.WorkItem;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Component
public class RetrieveItems {

    // Retrieves an item based on the ID
    public String FlipItemArchive(String id ) {

        Connection c = null;
        String query = "";

        try {
            // Create a Connection object
            c =  ConnectionHelper.getConnection();

            ResultSet rs = null;
            Statement s = c.createStatement();
            Statement scount = c.createStatement();

            // Use prepared statements
            PreparedStatement pstmt = null;
            PreparedStatement ps = null;

            //Specify the SQL Statement to query data from, the empployee table
            query = "update work set archive = ? where idwork ='" +id + "' ";

            PreparedStatement updateForm = c.prepareStatement(query);

            updateForm.setBoolean(1, true);
            updateForm.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
        return null;
    }


    // Retrieves archive data from the MySQL database
    public String getArchiveData(String username) {

        Connection c = null;

        // Define a list in which work items are stored
        List<WorkItem> itemList = new ArrayList<WorkItem>();
        int rowCount = 0;
        String query = "";
        WorkItem item = null;
        try {
            // Create a Connection object
            c =  ConnectionHelper.getConnection();

            ResultSet rs = null;
            Statement s = c.createStatement();
            Statement scount = c.createStatement();

            //Use prepared statements to protected against SQL injection attacks
            PreparedStatement pstmt = null;
            PreparedStatement ps = null;

            int arch = 1;

            // Specify the SQL Statement to query data from, the work table
            query = "Select idwork,username,date,description,guide,status FROM work where username = '" +username +"' and archive = " +arch +"";
            pstmt = c.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                // For each record, create a WorkItem object
                item = new WorkItem();

                // Populate the WorkItem object with data from MySQL
                item.SetId(rs.getString(1));
                item.SetName(rs.getString(2));
                item.SetDate(rs.getDate(3).toString().trim());
                item.SetDescription(rs.getString(4));
                item.SetGuide(rs.getString(5));
                item.SetStatus(rs.getString(6));

                // Push the WorkItem object to the list
                itemList.add(item);
            }

            return convertToString(toXml(itemList));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
        return null;
    }

    // Retrieves an item based on the ID
    public String GetItemSQL(String id ) {

        Connection c = null;

        //Define a list in which all work items are stored
        String query = "";
        String status="" ;
        String description="";

        try {
            // Create a Connection object
            c =  ConnectionHelper.getConnection();

            ResultSet rs = null;
            Statement s = c.createStatement();
            Statement scount = c.createStatement();

            //Use prepared statements to protected against SQL injection attacks
            PreparedStatement pstmt = null;
            PreparedStatement ps = null;

            //Specify the SQL Statement to query data from, the empployee table
            query = "Select description, status FROM work where idwork ='" +id + "' ";
            pstmt = c.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                description = rs.getString(1);
                status = rs.getString(2);
            }
            return convertToString(toXmlItem(id,description,status));


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
        return null;
    }

    //Get Items Data from MySQL
    public List<WorkItem> getItemsDataSQLReport(String username) {

        Connection c = null;

        //Define a list in which all work items are stored
        List<WorkItem> itemList = new ArrayList<WorkItem>();
        int rowCount = 0;
        String query = "";
        WorkItem item = null;
        try {
            // Create a Connection object
            c =  ConnectionHelper.getConnection();

            ResultSet rs = null;
            Statement s = c.createStatement();
            Statement scount = c.createStatement();

            //Use prepared statements to protected against SQL injection attacks
            PreparedStatement pstmt = null;
            PreparedStatement ps = null;

            int arch = 0;

            //Specify the SQL Statement to query data from, the work table
            query = "Select idwork,username,date,description,guide,status FROM work where username = '" +username +"' and archive = " +arch +"";
            pstmt = c.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                //For each record-- create a WorkItem instance
                item = new WorkItem();

                //Populate Employee object with data from MySQL
                item.SetId(rs.getString(1));
                item.SetName(rs.getString(2));
                item.SetDate(rs.getDate(3).toString().trim());
                item.SetDescription(rs.getString(4));
                item.SetGuide(rs.getString(5));
                item.SetStatus(rs.getString(6));

                // Push the WorkItem Object to the list
                itemList.add(item);
            }
            return itemList;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
        return null;
    }


    // Get Items Data from MySQL
    public String getItemsDataSQL(String username) {

        Connection c = null;

        // Define a list in which all work items are stored
        List<WorkItem> itemList = new ArrayList<WorkItem>();
        int rowCount = 0;
        String query = "";
        WorkItem item = null;
        try {
            // Create a Connection object
            c =  ConnectionHelper.getConnection();

            ResultSet rs = null;
            Statement s = c.createStatement();
            Statement scount = c.createStatement();

            // Use prepared statements
            PreparedStatement pstmt = null;
            PreparedStatement ps = null;

            int arch = 0;

            //Specify the SQL Statement to query data
            query = "Select idwork,username,date,description,guide,status FROM work where username = '" +username +"' and archive = " +arch +"";
            pstmt = c.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                //For each record-- create a WorkItem instance
                item = new WorkItem();

                //Populate WorkItem object with data
                item.SetId(rs.getString(1));
                item.SetName(rs.getString(2));
                item.SetDate(rs.getDate(3).toString().trim());
                item.SetDescription(rs.getString(4));
                item.SetGuide(rs.getString(5));
                item.SetStatus(rs.getString(6));

                //Push the WorkItem Object to the list
                itemList.add(item);
            }

            return convertToString(toXml(itemList));


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionHelper.close(c);
        }
        return null;
    }

    // Convert Work item data retrieved from MySQL
    // into XML to pass back to the view
    private Document toXml(List<WorkItem> itemList) {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            //Start building the XML
            Element root = doc.createElement( "Items" );
            doc.appendChild( root );

            //Get the elements from the collection
            int custCount = itemList.size();

            // Iterate through the collection
            for ( int index=0; index < custCount; index++) {

                //Get the WorkItem object from the collection
                WorkItem myItem = itemList.get(index);

                Element Item = doc.createElement( "Item" );
                root.appendChild( Item );

                //Set Id
                Element id = doc.createElement( "Id" );
                id.appendChild( doc.createTextNode(myItem.getId() ) );
                Item.appendChild( id );

                //Set Name
                Element name = doc.createElement( "Name" );
                name.appendChild( doc.createTextNode(myItem.getName() ) );
                Item.appendChild( name );

                //Set Date
                Element date = doc.createElement( "Date" );
                date.appendChild( doc.createTextNode(myItem.getDate() ) );
                Item.appendChild( date );

                //Set Description
                Element desc = doc.createElement( "Description" );
                desc.appendChild( doc.createTextNode(myItem.getDescription() ) );
                Item.appendChild( desc );

                //Set Guide
                Element guide = doc.createElement( "Guide" );
                guide.appendChild( doc.createTextNode(myItem.getGuide() ) );
                Item.appendChild( guide );

                //Set Status
                Element status = doc.createElement( "Status" );
                status.appendChild( doc.createTextNode(myItem.getStatus() ) );
                Item.appendChild( status );
            }

            return doc;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String convertToString(Document xml)
    {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    // Convert Work item data retrieved from MySQL
    // into an XML schema to pass back to client
    private Document toXmlItem(String id2, String desc2, String status2) {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            //Start building the XML
            Element root = doc.createElement( "Items" );
            doc.appendChild( root );

            Element Item = doc.createElement( "Item" );
            root.appendChild( Item );

            //Set Id
            Element id = doc.createElement( "Id" );
            id.appendChild( doc.createTextNode(id2 ) );
            Item.appendChild( id );

            //Set Description
            Element desc = doc.createElement( "Description" );
            desc.appendChild( doc.createTextNode(desc2 ) );
            Item.appendChild( desc );

            //Set Status
            Element status = doc.createElement( "Status" );
            status.appendChild( doc.createTextNode(status2 ) );
            Item.appendChild( status );

            return doc;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
