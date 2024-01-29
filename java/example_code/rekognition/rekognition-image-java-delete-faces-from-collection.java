// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-image-java-delete-faces-from-collection.complete]
package aws.example.rekognition.image;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DeleteFacesRequest;
import com.amazonaws.services.rekognition.model.DeleteFacesResult;

import java.util.List;

public class DeleteFacesFromCollection {
   // Change collectionID to the collection that contains the face.
   // Change "xxxxxx..." to the ID of the face that you want to delete.
   public static final String collectionId = "MyCollection";
   public static final String faces[] = { "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" };

   public static void main(String[] args) throws Exception {

      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

      DeleteFacesRequest deleteFacesRequest = new DeleteFacesRequest()
            .withCollectionId(collectionId)
            .withFaceIds(faces);

      DeleteFacesResult deleteFacesResult = rekognitionClient.deleteFaces(deleteFacesRequest);

      List<String> faceRecords = deleteFacesResult.getDeletedFaces();
      System.out.println(Integer.toString(faceRecords.size()) + " face(s) deleted:");
      for (String face : faceRecords) {
         System.out.println("FaceID: " + face);
      }
   }
}
// snippet-end:[rekognition.java.rekognition-image-java-delete-faces-from-collection.complete]
