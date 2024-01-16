// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-collection-java-create-collection]
package aws.example.rekognition.collection;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;

public class CreateCollection {

      public static void main(String[] args) throws Exception {

            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

            // Replace collectionId with the name of the collection that you want to create.

            String collectionId = "MyCollection";
            System.out.println("Creating collection: " +
                        collectionId);

            CreateCollectionRequest request = new CreateCollectionRequest()
                        .withCollectionId(collectionId);

            CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(request);
            System.out.println("CollectionArn : " +
                        createCollectionResult.getCollectionArn());
            System.out.println("Status code : " +
                        createCollectionResult.getStatusCode().toString());

      }

}
// snippet-end:[rekognition.java.rekognition-collection-java-create-collection]
