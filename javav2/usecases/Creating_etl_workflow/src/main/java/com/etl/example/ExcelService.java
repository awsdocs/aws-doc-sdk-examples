/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.etl.example;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import jxl.read.biff.BiffException;
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
import java.io.*;

public class ExcelService {

    public String getData(String bucketName, String object) throws IOException, BiffException {

    // Get the Excel speadsheet from the Amazon S3 bucket.
    S3Service s3Service = new S3Service();
    byte[] data = s3Service.getObjectBytes(bucketName, object);
    InputStream inputStrean = new ByteArrayInputStream(data);

    List<PopData> myList = new ArrayList() ;
    System.out.println("Retrieving data from the Excel Spreadsheet");
    Workbook wb = Workbook.getWorkbook(inputStrean);
    Sheet sheet = wb.getSheet(0);

    try{

        // Read the data from the excel spreadsheet.
        Sheet s=wb.getSheet(0);
        int b = s.getColumns();
        System.out.println("The No. of Columns in the Sheet are = " + b);
        int a = s.getRows();
        System.out.println("The No. of Rows in the sheet are = " +a);

        PopData popData = null;

        // Loop through the rows in the spreadsheet.
        for (int zz = 0 ; zz <a; zz++) {

            // Get the first cell.
            System.out.println(zz);

            Cell[] row = sheet.getRow(zz);

            if (zz ==0)
               System.out.println("Not 1st row");
            else {
              popData = new PopData();

            for (Cell cell : row) {

                int colIndex =  cell.getColumn();
                String val = cell.getContents();

                switch(colIndex) {
                    case 0:
                        popData.setName(val);
                        break;

                    case 1:
                        popData.setCode(val);
                        break;

                    case 2:
                        popData.set2010(val);
                        break;

                    case 3:
                        popData.set2011(val);
                        break;

                    case 4:
                        popData.set2012(val);
                        break;

                    case 5:
                        popData.set2013(val);
                        break;

                    case 6:
                        popData.set2014(val);
                        break;

                    case 7:
                        popData.set2015(val);
                        break;

                    case 8:
                        popData.set2016(val);
                        break;

                    case 9:
                        popData.set2017(val);
                        break;

                    case 10:
                        popData.set2018(val);
                        break;

                    default: {
                        popData.set2019(val);
                        myList.add(popData);
                    }
                }
            }
        }
    }

        myList.sort(Comparator.comparing(PopData::getName));
        String transformXML  = convertToString(toXml(myList));
        return transformXML;

    }catch (Exception e) {
        e.printStackTrace();
    }

    return "";
    }

// Convert population data into XML.
private static Document toXml(List<PopData> itemList) {

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
            PopData myItem = itemList.get(index);

            Element item = doc.createElement( "Item" );
            root.appendChild( item );

            // Set Name.
            Element id = doc.createElement( "Name" );
            id.appendChild( doc.createTextNode(myItem.getName() ) );
            item.appendChild( id );

            // Set Name.
            Element name = doc.createElement( "Code" );
            name.appendChild( doc.createTextNode(myItem.getCode()) );
            item.appendChild( name );

            // Set 2010.
            Element ob2010 = doc.createElement( "Date2010" );
            ob2010.appendChild( doc.createTextNode(myItem.get2010() ) );
            item.appendChild( ob2010 );

            // Set 2011.
            Element ob2011 = doc.createElement( "Date2011" );
            ob2011.appendChild( doc.createTextNode(myItem.get2011()) );
            item.appendChild( ob2011 );

            // Set 2012.
            Element ob2012 = doc.createElement( "Date2012" );
            ob2012.appendChild( doc.createTextNode(myItem.get2012() ) );
            item.appendChild( ob2012 );

            // Set 2013.
            Element ob2013 = doc.createElement( "Date2013" );
            ob2013.appendChild( doc.createTextNode(myItem.get2013()) );
            item.appendChild( ob2013 );

            // Set 2014.
            Element ob2014 = doc.createElement( "Date2014" );
            ob2014.appendChild( doc.createTextNode(myItem.get2014()) );
            item.appendChild( ob2014 );

            // Set 2015.
            Element ob2015 = doc.createElement( "Date2015" );
            ob2015.appendChild( doc.createTextNode(myItem.get2015()) );
            item.appendChild( ob2015 );

            // Set 2016.
            Element ob2016 = doc.createElement( "Date2016" );
            ob2016.appendChild( doc.createTextNode(myItem.get2016()) );
            item.appendChild( ob2016 );

            // Set 2017.
            Element ob2017 = doc.createElement( "Date2017" );
            ob2017.appendChild( doc.createTextNode(myItem.get2017()) );
            item.appendChild( ob2017 );

            // Set 2018.
            Element ob2018 = doc.createElement( "Date2018" );
            ob2018.appendChild( doc.createTextNode(myItem.get2018()) );
            item.appendChild( ob2018 );

            // Set 2019.
            Element ob2019 = doc.createElement( "Date2019" );
            ob2019.appendChild( doc.createTextNode(myItem.get2019()) );
            item.appendChild( ob2019 );
        }

        return doc;
        } catch(ParserConfigurationException e) {
        e.printStackTrace();
        }
        return null;
        }

private static String convertToString(Document xml) {
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


