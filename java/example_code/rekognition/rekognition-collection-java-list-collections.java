// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-collection-java-list-collections.complete]
package aws.example.rekognition.collection;

import java.util.List;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;

public class ListCollections {

   public static void main(String[] args) throws Exception {

      AmazonRekognition amazonRekognition = AmazonRekognitionClientBuilder.defaultClient();

      System.out.println("Listing collections");
      int limit = 10;
      ListCollectionsResult listCollectionsResult = null;
      String paginationToken = null;
      do {
         if (listCollectionsResult != null) {
            paginationToken = listCollectionsResult.getNextToken();
         }
         ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest()
               .withMaxResults(limit)
               .withNextToken(paginationToken);
         listCollectionsResult = amazonRekognition.listCollections(listCollectionsRequest);

         List<String> collectionIds = listCollectionsResult.getCollectionIds();
         for (String resultId : collectionIds) {
            System.out.println(resultId);
         }
      } while (listCollectionsResult != null && listCollectionsResult.getNextToken() != null);

   }
}
// snippet-end:[rekognition.java.rekognition-collection-java-list-collections.complete]