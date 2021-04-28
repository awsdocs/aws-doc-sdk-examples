/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.blog;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.*;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
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

@Component
public class RedshiftService {

    String clusterId = "redshift-cluster-1";
    String database = "dev";
    String dbUser = "awsuser";

    private RedshiftDataClient getClient() {

        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
                .build();

        return redshiftDataClient;
    }


    // Returns a collection that returns the latest five posts from the Redshift table.
    public String getPosts(String lang, int num) {

        try {

            RedshiftDataClient redshiftDataClient = getClient();
            String sqlStatement="";
            if (num ==5)
                sqlStatement = "SELECT TOP 5 * FROM blog ORDER BY date DESC";
            else if (num ==10)
                sqlStatement = "SELECT TOP 10 * FROM blog ORDER BY date DESC";
            else
                sqlStatement = "SELECT * FROM blog ORDER BY date DESC" ;

            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterId)
                    .database(database)
                    .dbUser(dbUser)
                    .sql(sqlStatement)
                    .build();

            ExecuteStatementResponse response = redshiftDataClient.executeStatement(statementRequest);
            String myId = response.id();
            checkStatement(redshiftDataClient,myId );
            List<Post> posts = getResults(redshiftDataClient, myId, lang);
            return convertToString(toXml(posts));

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    // Add a new record to the Amazon Redshift table.
    public String addRecord(String author, String title, String body) {

        try {

            RedshiftDataClient redshiftDataClient = getClient();
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();

            // Date conversion.
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date( date1.getTime());


            // Inject an item into the system.
            String sqlStatement = "INSERT INTO blog (idblog, date, title, body, author) VALUES( '"+uuid+"' ,'"+sqlDate +"','"+title +"' , '"+body +"', '"+author +"');";
            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterId)
                    .database(database)
                    .dbUser(dbUser)
                    .sql(sqlStatement)
                    .build();

            redshiftDataClient.executeStatement(statementRequest);
            return id;

        } catch (RedshiftDataException | ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public void checkStatement(RedshiftDataClient redshiftDataClient,String sqlId ) {

        try {

            DescribeStatementRequest statementRequest = DescribeStatementRequest.builder()
                    .id(sqlId)
                    .build() ;

            // Wait until the sql statement processing is finished.
            boolean finished = false;
            String status = "";
            while (!finished) {

                DescribeStatementResponse response = redshiftDataClient.describeStatement(statementRequest);
                status = response.statusAsString();
                System.out.println("..."+status);

                if (status.compareTo("FINISHED") == 0) {
                    break;
                }
                Thread.sleep(500);
            }

            System.out.println("The statement is finished!");

        } catch (RedshiftDataException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


    public List<Post> getResults(RedshiftDataClient redshiftDataClient, String statementId, String lang) {

        try {

            List<Post>records = new ArrayList<>();
            GetStatementResultRequest resultRequest = GetStatementResultRequest.builder()
                    .id(statementId)
                    .build();

            GetStatementResultResponse response = redshiftDataClient.getStatementResult(resultRequest);

            // Iterate through the List element where each element is a List object.
            List<List<Field>> dataList = response.records();

            Post post ;
            int index = 0 ;
            // Print out the records.
            for (List list: dataList) {

                // new Post object here
                post = new Post();
                index = 0 ;
                for (Object myField:list) {

                    Field field = (Field) myField;
                    String value = field.stringValue();

                    if (index == 0)
                        post.setId(value);

                    else if (index == 1)
                        post.setDate(value);

                    else if (index == 2) {

                        if (!lang.equals("English"))
                            value = translateText(value, lang);

                        post.setTitle(value);
                    }

                    else if (index == 3) {
                        if (!lang.equals("English"))
                             value = translateText(value, lang);

                        post.setBody(value);
                    }

                    else if (index == 4)
                        post.setAuthor(value);

                    // Increment the index.
                    index ++;
               }

                // Push the Post object to the List.
                records.add(post);
            }

            return records;

        } catch (RedshiftDataException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return null ;
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
}
