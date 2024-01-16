// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-image-java-recognize-celebrities.complete]
package aws.example.rekognition.image;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Celebrity;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesRequest;
import com.amazonaws.services.rekognition.model.RecognizeCelebritiesResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import com.amazonaws.util.IOUtils;
import java.util.List;

public class RecognizeCelebrities {

   public static void main(String[] args) {
      // Change photo to the path and filename of your image.
      String photo = "moviestars.jpg";

      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

      ByteBuffer imageBytes = null;
      try (InputStream inputStream = new FileInputStream(new File(photo))) {
         imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
      } catch (Exception e) {
         System.out.println("Failed to load file " + photo);
         System.exit(1);
      }

      RecognizeCelebritiesRequest request = new RecognizeCelebritiesRequest()
            .withImage(new Image()
                  .withBytes(imageBytes));

      System.out.println("Looking for celebrities in image " + photo + "\n");

      RecognizeCelebritiesResult result = rekognitionClient.recognizeCelebrities(request);

      // Display recognized celebrity information
      List<Celebrity> celebs = result.getCelebrityFaces();
      System.out.println(celebs.size() + " celebrity(s) were recognized.\n");

      for (Celebrity celebrity : celebs) {
         System.out.println("Celebrity recognized: " + celebrity.getName());
         System.out.println("Celebrity ID: " + celebrity.getId());
         BoundingBox boundingBox = celebrity.getFace().getBoundingBox();
         System.out.println("position: " +
               boundingBox.getLeft().toString() + " " +
               boundingBox.getTop().toString());
         System.out.println("Further information (if available):");
         for (String url : celebrity.getUrls()) {
            System.out.println(url);
         }
         System.out.println();
      }
      System.out.println(result.getUnrecognizedFaces().size() + " face(s) were unrecognized.");
   }
}
// snippet-end:[rekognition.java.rekognition-image-java-recognize-celebrities.complete]