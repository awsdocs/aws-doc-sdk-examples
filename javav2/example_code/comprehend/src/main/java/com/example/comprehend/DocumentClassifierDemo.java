// snippet-sourcedescription:[DocumentClassifierDemo demonstrates how to train a custom classifier.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Comprehend]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.comprehend;

//snippet-start:[comprehend.java2.classifier.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.ComprehendException;
import software.amazon.awssdk.services.comprehend.model.CreateDocumentClassifierRequest;
import software.amazon.awssdk.services.comprehend.model.CreateDocumentClassifierResponse;
import software.amazon.awssdk.services.comprehend.model.DocumentClassifierInputDataConfig;
//snippet-end:[comprehend.java2.classifier.import]

/**
 * Before running this code example, you can setup the necessary resources, such as the CSV file and IAM Roles, by following this document:
 *  https://aws.amazon.com/blogs/machine-learning/building-a-custom-classifier-using-amazon-comprehend/
 *
 * Also, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DocumentClassifierDemo {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "   <dataAccessRoleArn> <s3Uri> <documentClassifierName>\n\n" +
            "Where:\n" +
            "  dataAccessRoleArn - The ARN value of the role used for this operation.\n\n" +
            "  s3Uri - The Amazon S3 bucket that contains the CSV file.\n\n" +
            "  documentClassifierName - The name of the document classifier.\n\n";

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String dataAccessRoleArn = args[0];
        String s3Uri = args[1];
        String documentClassifierName = args[2];

        Region region = Region.US_EAST_1;
        ComprehendClient comClient = ComprehendClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createDocumentClassifier(comClient, dataAccessRoleArn, s3Uri, documentClassifierName);
        comClient.close();
    }

    //snippet-start:[comprehend.java2.classifier.main]
    public static void createDocumentClassifier(ComprehendClient comClient, String dataAccessRoleArn, String s3Uri, String documentClassifierName){

         try {
             DocumentClassifierInputDataConfig config = DocumentClassifierInputDataConfig.builder()
                 .s3Uri(s3Uri)
                 .build();

             CreateDocumentClassifierRequest createDocumentClassifierRequest = CreateDocumentClassifierRequest.builder()
                 .documentClassifierName(documentClassifierName)
                 .dataAccessRoleArn(dataAccessRoleArn)
                 .languageCode("en")
                 .inputDataConfig(config)
                 .build();

             CreateDocumentClassifierResponse createDocumentClassifierResult = comClient.createDocumentClassifier(createDocumentClassifierRequest);
             String documentClassifierArn = createDocumentClassifierResult.documentClassifierArn();
             System.out.println("Document Classifier ARN: " + documentClassifierArn);

         } catch (ComprehendException e) {
             System.err.println(e.awsErrorDetails().errorMessage());
             System.exit(1);
         }
     }
    //snippet-end:[comprehend.java2.classifier.main]
}