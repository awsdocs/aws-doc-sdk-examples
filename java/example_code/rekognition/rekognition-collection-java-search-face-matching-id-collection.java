// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-collection-java-search-face-matching-id-collection.complete]
package aws.example.rekognition.collection;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.SearchFacesRequest;
import com.amazonaws.services.rekognition.model.SearchFacesResult;
import java.util.List;

public class SearchFaceMatchingIdCollection {
        // Replace collectionId and faceId with the values you want to use.
        public static final String collectionId = "MyCollection";
        public static final String faceId = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";

        public static void main(String[] args) throws Exception {

                AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

                ObjectMapper objectMapper = new ObjectMapper();
                // Search collection for faces matching the face id.

                SearchFacesRequest searchFacesRequest = new SearchFacesRequest()
                                .withCollectionId(collectionId)
                                .withFaceId(faceId)
                                .withFaceMatchThreshold(70F)
                                .withMaxFaces(2);

                SearchFacesResult searchFacesByIdResult = rekognitionClient.searchFaces(searchFacesRequest);

                System.out.println("Face matching faceId " + faceId);
                List<FaceMatch> faceImageMatches = searchFacesByIdResult.getFaceMatches();
                for (FaceMatch face : faceImageMatches) {
                        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(face));

                        System.out.println();
                }
        }

}
// snippet-end:[rekognition.java.rekognition-collection-java-search-face-matching-id-collection.complete]
