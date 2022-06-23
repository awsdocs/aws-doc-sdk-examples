package com.aws.blog;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateException;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.sql.*;

@Component
public class RetrieveDataRDS {

    // Add a new record to the Amazon Aurora table.
    public String addRecord(String author, String title, String body) {

        Connection c = null;

        try {

            // Create a Connection object
            c = ConnectionHelper.getConnection();

            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();

            // Date conversion.
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date( date1.getTime());

            // Use prepared statements
            PreparedStatement ps = null;

            // Inject an item into the system
            String insert = "INSERT INTO jobs (idjobs, date,title,body, author) VALUES(?,?,?,?,?);";
            ps = c.prepareStatement(insert);
            ps.setString(1, id);
            ps.setDate(2, sqlDate);
            ps.setString(3, title);
            ps.setString(4, body);
            ps.setString(5, author );
            ps.execute();

            return id;

        } catch (ParseException | SQLException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }


    // Returns a collection that returns the latest five posts from the Redshift table.
    public String getPosts(String lang, int num) {

        Connection c = null;

        try {

            // Create a Connection object
            c = ConnectionHelper.getConnection();

            String sqlStatement="";
            if (num ==5)
                sqlStatement = "Select * from jobs order by date DESC LIMIT 5 ; ";
            else if (num ==10)
                sqlStatement = "Select * from jobs order by date DESC LIMIT 10 ; ";
            else
                sqlStatement = "Select * from jobs order by date DESC" ;

            PreparedStatement pstmt = null;
            ResultSet rs = null;
            List<Post> posts = new ArrayList<>();
            Post post = null;

            pstmt = c.prepareStatement(sqlStatement);
            rs = pstmt.executeQuery();
            String title = "";
            String body = "";

            while (rs.next()) {

                post = new Post();
                post.setId(rs.getString(1));
                post.setDate(rs.getDate(2).toString());

                title = rs.getString(3);
                if (!lang.equals("English"))
                    title = translateText(title, lang);

                post.setTitle(title);

                body= rs.getString(4);

                if (!lang.equals("English"))
                    body = translateText(body, lang);

                post.setBody(body);

                post.setAuthor(rs.getString(5));

                // Push post to the list
                posts.add(post);
            }

            return convertToString(toXml(posts));

        } catch ( SQLException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }


    private String translateText(String text, String lang) {

        Region region = Region.US_WEST_2;
        TranslateClient translateClient = TranslateClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
                .build();
        String transValue = "";
        try {

            if (lang.compareTo("French")==0) {

                TranslateTextRequest textRequest = TranslateTextRequest.builder()
                        .sourceLanguageCode("en")
                        .targetLanguageCode("fr")
                        .text(text)
                        .build();

                TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                transValue = textResponse.translatedText();

            } else if (lang.compareTo("Russian")==0) {

                TranslateTextRequest textRequest = TranslateTextRequest.builder()
                        .sourceLanguageCode("en")
                        .targetLanguageCode("ru")
                        .text(text)
                        .build();

                TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                transValue = textResponse.translatedText();


            } else if (lang.compareTo("Japanese")==0) {

                TranslateTextRequest textRequest = TranslateTextRequest.builder()
                        .sourceLanguageCode("en")
                        .targetLanguageCode("ja")
                        .text(text)
                        .build();

                TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                transValue = textResponse.translatedText();


            } else if (lang.compareTo("Spanish")==0) {

                TranslateTextRequest textRequest = TranslateTextRequest.builder()
                        .sourceLanguageCode("en")
                        .targetLanguageCode("es")
                        .text(text)
                        .build();

                TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                transValue = textResponse.translatedText();

            } else {

                TranslateTextRequest textRequest = TranslateTextRequest.builder()
                        .sourceLanguageCode("en")
                        .targetLanguageCode("zh")
                        .text(text)
                        .build();

                TranslateTextResponse textResponse = translateClient.translateText(textRequest);
                transValue = textResponse.translatedText();
            }

            return transValue;

        } catch (TranslateException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return "";
    }

    // Convert the list to XML to pass back to the view.
    private Document toXml(List<Post> itemsList) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML.
            Element root = doc.createElement("Items");
            doc.appendChild(root);

            // Iterate through the collection.
            for (Post post : itemsList) {

                Element item = doc.createElement("Item");
                root.appendChild(item);

                // Set Id.
                Element id = doc.createElement("Id");
                id.appendChild(doc.createTextNode(post.getId()));
                item.appendChild(id);

                // Set Date.
                Element name = doc.createElement("Date");
                name.appendChild(doc.createTextNode(post.getDate()));
                item.appendChild(name);

                // Set Title.
                Element date = doc.createElement("Title");
                date.appendChild(doc.createTextNode(post.getTitle()));
                item.appendChild(date);

                // Set Content.
                Element desc = doc.createElement("Content");
                desc.appendChild(doc.createTextNode(post.getBody()));
                item.appendChild(desc);

                // Set Author.
                Element guide = doc.createElement("Author");
                guide.appendChild(doc.createTextNode(post.getAuthor()));
                item.appendChild(guide);
            }

            return doc;

        }catch(ParserConfigurationException e){
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
