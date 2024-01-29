// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-collection-java-delete-collection.complete]
package aws.example.rekognition.collection;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;

public class DeleteCollection {

      public static void main(String[] args) throws Exception {

            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
            // Replace collectionId with the ID of the collection that you want to delete.
            String collectionId = "MyCollection";

            System.out.println("Deleting collections");

            DeleteCollectionRequest request = new DeleteCollectionRequest()
                        .withCollectionId(collectionId);
            DeleteCollectionResult deleteCollectionResult = rekognitionClient.deleteCollection(request);

            System.out.println(collectionId + ": " + deleteCollectionResult.getStatusCode()
                        .toString());

      }

}
// snippet-end:[rekognition.java.rekognition-collection-java-delete-collection.complete]
