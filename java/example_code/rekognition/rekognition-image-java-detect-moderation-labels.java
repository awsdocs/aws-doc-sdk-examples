// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[rekognition.java.rekognition-image-java-detect-moderation-labels.complete]
package aws.example.rekognition.image;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectModerationLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectModerationLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.ModerationLabel;
import com.amazonaws.services.rekognition.model.S3Object;

import java.util.List;

public class DetectModerationLabels {
  public static void main(String[] args) throws Exception {
    // Change the values of photo and bucket to your values.
    String photo = "input.jpg";
    String bucket = "bucket";

    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

    DetectModerationLabelsRequest request = new DetectModerationLabelsRequest()
        .withImage(new Image().withS3Object(new S3Object().withName(photo).withBucket(bucket)))
        .withMinConfidence(60F);
    try {
      DetectModerationLabelsResult result = rekognitionClient.detectModerationLabels(request);
      List<ModerationLabel> labels = result.getModerationLabels();
      System.out.println("Detected labels for " + photo);
      for (ModerationLabel label : labels) {
        System.out.println("Label: " + label.getName()
            + "\n Confidence: " + label.getConfidence().toString() + "%"
            + "\n Parent:" + label.getParentName());
      }
    } catch (AmazonRekognitionException e) {
      e.printStackTrace();
    }
  }
}
// snippet-end:[rekognition.java.rekognition-image-java-detect-moderation-labels.complete]
