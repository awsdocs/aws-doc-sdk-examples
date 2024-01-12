// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-collection-java-search-face-matching-image-collection.complete]
package aws.example.rekognition.collection;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchFaceMatchingImageCollection {
        // Replace bucket, collectionId and photo with your values.
        public static final String collectionId = "MyCollection";
        public static final String bucket = "bucket";
        public static final String photo = "input.jpg";

        public static void main(String[] args) throws Exception {

                AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

                ObjectMapper objectMapper = new ObjectMapper();

                // Get an image object from S3 bucket.
                Image image = new Image()
                                .withS3Object(new S3Object()
                                                .withBucket(bucket)
                                                .withName(photo));

                // Search collection for faces similar to the largest face in the image.
                SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                                .withCollectionId(collectionId)
                                .withImage(image)
                                .withFaceMatchThreshold(70F)
                                .withMaxFaces(2);

                SearchFacesByImageResult searchFacesByImageResult = rekognitionClient
                                .searchFacesByImage(searchFacesByImageRequest);

                System.out.println("Faces matching largest face in image from" + photo);
                List<FaceMatch> faceImageMatches = searchFacesByImageResult.getFaceMatches();
                for (FaceMatch face : faceImageMatches) {
                        System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(face));
                        System.out.println();
                }
        }
}
// snippet-end:[rekognition.java.rekognition-collection-java-search-face-matching-image-collection.complete]
