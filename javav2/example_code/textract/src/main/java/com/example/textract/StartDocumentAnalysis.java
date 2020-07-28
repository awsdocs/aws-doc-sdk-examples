// snippet-sourcedescription:[StartDocumentAnalysis.java demonstrates how to start the asynchronous analysis of a document.]
// snippet-service:[Amazon Textract]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Textract]
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

// snippet-start:[textract.java2._start_doc_analysis.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.model.S3Object;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.StartDocumentAnalysisRequest;
import software.amazon.awssdk.services.textract.model.DocumentLocation;
import software.amazon.awssdk.services.textract.model.TextractException;
import software.amazon.awssdk.services.textract.model.StartDocumentAnalysisResponse;
import software.amazon.awssdk.services.textract.model.GetDocumentAnalysisRequest;
import software.amazon.awssdk.services.textract.model.GetDocumentAnalysisResponse;
import software.amazon.awssdk.services.textract.model.FeatureType;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[textract.java2._start_doc_analysis.import]

public class StartDocumentAnalysis {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    StartDocumentAnalysis <bucketName> <docName> \n\n" +
                "Where:\n" +
                "    bucketName - The name of the Amazon S3 bucket that contains the document \n\n" +
                "    docName - The document name (must be an image, i.e., book.png) \n";

        Region region = Region.US_WEST_2;
        TextractClient textractClient = TextractClient.builder()
                .region(region)
                .build();

        String bucketName = args[0];
        String docName = args[1];

        startDocAnalysisS3 (textractClient,  bucketName, docName);
    }

 // snippet-start:[textract.java2._start_doc_analysis.main]
 public static void startDocAnalysisS3 (TextractClient textractClient,  String bucketName, String docName) {

    try {

        List<FeatureType> myList = new ArrayList<FeatureType>();
        myList.add(FeatureType.TABLES);
        myList.add(FeatureType.FORMS);

        S3Object s3Object = S3Object.builder()
                .bucket(bucketName)
                .name(docName)
                .build();

        DocumentLocation location = DocumentLocation.builder()
                .s3Object(s3Object)
                .build();

        StartDocumentAnalysisRequest documentAnalysisRequest =  StartDocumentAnalysisRequest.builder()
                .documentLocation(location)
                .featureTypes(myList)
                .build();

        StartDocumentAnalysisResponse response = textractClient.startDocumentAnalysis(documentAnalysisRequest) ;

        // Get the job ID
        String jobId = response.jobId();

        String result = getJobResults(textractClient,jobId);

        System.out.println("The status of the job is: "+result);

    } catch (TextractException e) {

        System.err.println(e.getMessage());
        System.exit(1);
    }
  }

  private static String getJobResults(TextractClient textractClient, String jobId)
  {
      GetDocumentAnalysisRequest analysisRequest = GetDocumentAnalysisRequest.builder()
              .jobId(jobId)
              .maxResults(1000)
              .build();

      GetDocumentAnalysisResponse response =  textractClient.getDocumentAnalysis(analysisRequest);
      String status = response.jobStatus().toString();

      return status;
  }
    // snippet-end:[textract.java2._start_doc_analysis.main]
}
