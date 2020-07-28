// snippet-sourcedescription:[DetectDocumentText.java demonstrates how to detect text in the input document.]
// snippet-service:[Amazon Textract]
// snippet-keyword:[Java]
// snippet-keyword:[mazon Textract]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[7-8-2020]
// snippet-sourceauthor:[scmacdon - AWS]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.example.textract;

// snippet-start:[textract.java2._detect_doc_text.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.DocumentMetadata;
import software.amazon.awssdk.services.textract.model.TextractException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
// snippet-end:[textract.java2._detect_doc_text.import]

public class DetectDocumentText {

 public static void main(String[] args) {

     final String USAGE = "\n" +
             "Usage:\n" +
             "    DetectDocumentText <sourceDoc> \n\n" +
             "Where:\n" +
             "    sourceDoc - The path where the document is located (must be an image, i.e., C:/AWS/book.png) \n";

      if (args.length < 1) {
          System.out.println(USAGE);
          System.exit(1);
      }

     String sourceDoc = args[0];

     Region region = Region.US_EAST_2;
     TextractClient textractClient = TextractClient.builder()
             .region(region)
             .build();

     detectDocText(textractClient, sourceDoc);
 }

 // snippet-start:[textract.java2._detect_doc_text.main]
 public static void detectDocText(TextractClient textractClient,String sourceDoc) {

     try {

         InputStream sourceStream = new FileInputStream(new File(sourceDoc));
         SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

         // Get the input Document object as bytes
         Document myDoc = Document.builder()
                 .bytes(sourceBytes)
                 .build();

         DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
                 .document(myDoc)
                 .build();

         // Invoke the Detect operation
         DetectDocumentTextResponse textResponse = textractClient.detectDocumentText(detectDocumentTextRequest);

         List<Block> docInfo = textResponse.blocks();

         Iterator<Block> blockIterator = docInfo.iterator();

         while(blockIterator.hasNext()) {
             Block block = blockIterator.next();
             System.out.println("The block type is " +block.blockType().toString());
         }

         DocumentMetadata documentMetadata = textResponse.documentMetadata();
         System.out.println("The number of pages in the document is " +documentMetadata.pages());

     } catch (TextractException | FileNotFoundException e) {

         System.err.println(e.getMessage());
         System.exit(1);
     }
     // snippet-end:[textract.java2._detect_doc_text.main]
   }
}
