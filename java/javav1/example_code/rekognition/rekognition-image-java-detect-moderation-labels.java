// snippet-sourcedescription:[rekognition-image-java-detect-moderation-labels.java demonstrates how to detect unsafe content in an image loaded from an S3 Bucket.]
// snippet-service:[rekognition]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-keyword:[DetectModerationLabels]
// snippet-keyword:[S3 Bucket]
// snippet-keyword:[Image]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-18]
// snippet-sourceauthor:[reesch(AWS)]
// snippet-start:[rekognition.java.rekognition-image-java-detect-moderation-labels.complete]

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

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

public class DetectModerationLabels
{
   public static void main(String[] args) throws Exception
   {
     //Change the values of photo and bucket to your values.
      String photo = "input.jpg";
      String bucket = "bucket";
      
      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
      
      DetectModerationLabelsRequest request = new DetectModerationLabelsRequest()
        .withImage(new Image().withS3Object(new S3Object().withName(photo).withBucket(bucket)))
        .withMinConfidence(60F);
      try
      {
           DetectModerationLabelsResult result = rekognitionClient.detectModerationLabels(request);
           List<ModerationLabel> labels = result.getModerationLabels();
           System.out.println("Detected labels for " + photo);
           for (ModerationLabel label : labels)
           {
              System.out.println("Label: " + label.getName()
               + "\n Confidence: " + label.getConfidence().toString() + "%"
               + "\n Parent:" + label.getParentName());
          }
       }
       catch (AmazonRekognitionException e)
       {
         e.printStackTrace();
       }
    }
}
// snippet-end:[rekognition.java.rekognition-image-java-detect-moderation-labels.complete]
 
      