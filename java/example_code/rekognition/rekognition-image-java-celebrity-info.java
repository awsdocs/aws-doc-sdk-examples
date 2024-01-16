// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-image-java-celebrity-info.]
package aws.example.rekognition.image;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.GetCelebrityInfoRequest;
import com.amazonaws.services.rekognition.model.GetCelebrityInfoResult;

public class CelebrityInfo {

   public static void main(String[] args) {
      // Change the value of id to an ID value returned by RecognizeCelebrities or
      // GetCelebrityRecognition
      String id = "nnnnnnnn";

      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

      GetCelebrityInfoRequest request = new GetCelebrityInfoRequest()
            .withId(id);

      System.out.println("Getting information for celebrity: " + id);

      GetCelebrityInfoResult result = rekognitionClient.getCelebrityInfo(request);

      // Display celebrity information
      System.out.println("celebrity name: " + result.getName());
      System.out.println("Further information (if available):");
      for (String url : result.getUrls()) {
         System.out.println(url);
      }
   }
}
// snippet-end:[rekognition.java.rekognition-image-java-celebrity-info.]
