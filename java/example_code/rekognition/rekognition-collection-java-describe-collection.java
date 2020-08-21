// snippet-sourcedescription:[rekognition-collection-java-describe-collection.java demonstrates how to get a description of an Amazon Rekognition collection.]
// snippet-service:[rekognition]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-keyword:[DescribeCollection]
// snippet-keyword:[Collection]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-18]
// snippet-sourceauthor:[reesch(AWS)]
// snippet-start:[rekognition.java.rekognition-collection-java-describe-collection.complete]

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

package aws.example.rekognition.collection;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DescribeCollectionRequest;
import com.amazonaws.services.rekognition.model.DescribeCollectionResult;


public class DescribeCollection {

   public static void main(String[] args) throws Exception {
      //Change collectionID to the name of the desired collection.
      String collectionId = "CollectionID";
      
      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

            
      System.out.println("Describing collection: " +
         collectionId );
         
            
        DescribeCollectionRequest request = new DescribeCollectionRequest()
                    .withCollectionId(collectionId);
           
      DescribeCollectionResult describeCollectionResult = rekognitionClient.describeCollection(request); 
      System.out.println("Collection Arn : " +
         describeCollectionResult.getCollectionARN());
      System.out.println("Face count : " +
         describeCollectionResult.getFaceCount().toString());
      System.out.println("Face model version : " +
         describeCollectionResult.getFaceModelVersion());
      System.out.println("Created : " +
         describeCollectionResult.getCreationTimestamp().toString());

   } 

}

// snippet-end:[rekognition.java.rekognition-collection-java-describe-collection.complete]
    

