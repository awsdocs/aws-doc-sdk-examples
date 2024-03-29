// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-image-java-detect-text.complete]
package aws.example.rekognition.image;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.TextDetection;
import java.util.List;

public class DetectText {

   public static void main(String[] args) throws Exception {

      // Change the value of bucket to the S3 bucket that contains your image file.
      // Change the value of photo to your image file name.
      String photo = "inputtext.jpg";
      String bucket = "bucket";

      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

      DetectTextRequest request = new DetectTextRequest()
            .withImage(new Image()
                  .withS3Object(new S3Object()
                        .withName(photo)
                        .withBucket(bucket)));

      try {
         DetectTextResult result = rekognitionClient.detectText(request);
         List<TextDetection> textDetections = result.getTextDetections();

         System.out.println("Detected lines and words for " + photo);
         for (TextDetection text : textDetections) {

            System.out.println("Detected: " + text.getDetectedText());
            System.out.println("Confidence: " + text.getConfidence().toString());
            System.out.println("Id : " + text.getId());
            System.out.println("Parent Id: " + text.getParentId());
            System.out.println("Type: " + text.getType());
            System.out.println();
         }
      } catch (AmazonRekognitionException e) {
         e.printStackTrace();
      }
   }
}
// snippet-end:[rekognition.java.rekognition-image-java-detect-text.complete]