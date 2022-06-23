/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.etl.example;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;


public class DynamoDBService {

    int recNum = 1;

    private DynamoDbClient getClient() {

        // Create a DynamoDbClient object.
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        return ddb;
    }

    public void injectETLData(String myDom)  throws JDOMException, IOException {

        SAXBuilder builder = new SAXBuilder();
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = ((org.jdom2.Document) jdomDocument).getRootElement();
        PopData pop = new PopData();
        List<org.jdom2.Element> items = root.getChildren("Item");

        for (org.jdom2.Element element : items) {

            pop.setName(element.getChildText("Name"));
            pop.setCode(element.getChildText("Code"));
            pop.set2010(element.getChildText("Date2010"));
            pop.set2011(element.getChildText("Date2011"));
            pop.set2012(element.getChildText("Date2012"));
            pop.set2013(element.getChildText("Date2013"));
            pop.set2014(element.getChildText("Date2014"));
            pop.set2015(element.getChildText("Date2015"));
            pop.set2016(element.getChildText("Date2016"));
            pop.set2017(element.getChildText("Date2017"));
            pop.set2018(element.getChildText("Date2018"));
            pop.set2019(element.getChildText("Date2019"));
            setItem(pop) ;
        }
    }

    public void setItem(PopData pop) {

        // Create a DynamoDbEnhancedClient.
        DynamoDbClient ddb = getClient();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        try {

            // Create a DynamoDbTable object.
            DynamoDbTable<Population> workTable = enhancedClient.table("Country", TableSchema.fromBean(Population.class));

             // Populate the table.
            Population record = new Population();
            String name = pop.getName();
            String code = pop.getCode();

            record.setId(name);
            record.setCode(code);
            record.set2010(pop.get2010());
            record.set2011(pop.get2011());
            record.set2012(pop.get2012());
            record.set2013(pop.get2013());
            record.set2014(pop.get2014());
            record.set2015(pop.get2015());
            record.set2016(pop.get2016());
            record.set2017(pop.get2017());
            record.set2018(pop.get2018());
            record.set2019(pop.get2019());

            // Put the customer data into a DynamoDB table.
            workTable.putItem(record);
            System.out.println("Added record "+recNum);
            recNum ++;

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
