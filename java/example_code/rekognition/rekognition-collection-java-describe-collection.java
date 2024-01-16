// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-collection-java-describe-collection.complete]
package aws.example.rekognition.collection;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DescribeCollectionRequest;
import com.amazonaws.services.rekognition.model.DescribeCollectionResult;

public class DescribeCollection {

      public static void main(String[] args) throws Exception {
            // Change collectionID to the name of the desired collection.
            String collectionId = "CollectionID";

            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

            System.out.println("Describing collection: " +
                        collectionId);

            DescribeCollectionRequest request = new DescribeCollectionRequest()
                        .withCollectionId(collectionId);

            DescribeCollectionResult describeCollectionResult = rekognitionClient.describeCollection(request);
            System.out.println("Collection Arn : " +
                        describeCollectionResult.getCollectionARN());
            System.out.println("Face count : " +
                        describeCollectionResult.getFaceCount().toString());
            System.out.println("Face model version : " +
                        describeCollectionResult.getFaceModelVersion());
            System.out.println("Created : " +
                        describeCollectionResult.getCreationTimestamp().toString());

      }

}

// snippet-end:[rekognition.java.rekognition-collection-java-describe-collection.complete]
