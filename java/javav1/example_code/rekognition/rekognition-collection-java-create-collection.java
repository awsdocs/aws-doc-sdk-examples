// snippet-sourcedescription:[rekognition-collection-java-create-collection.java demonstrates how to delete an Amazon Rekognition collection.]
// snippet-service:[rekognition]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-keyword:[CreateCollection]
// snippet-keyword:[Collection]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-18]
// snippet-sourceauthor:[reesch(AWS)]
// snippet-start:[rekognition.java.rekognition-collection-java-create-collection]

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
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;


public class CreateCollection {

   public static void main(String[] args) throws Exception {


      AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();
      
      //Replace collectionId with the name of the collection that you want to create.
      
      String collectionId = "MyCollection";
            System.out.println("Creating collection: " +
         collectionId );
            
        CreateCollectionRequest request = new CreateCollectionRequest()
                    .withCollectionId(collectionId);
           
      CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(request); 
      System.out.println("CollectionArn : " +
         createCollectionResult.getCollectionArn());
      System.out.println("Status code : " +
         createCollectionResult.getStatusCode().toString());

   } 

}
// snippet-end:[rekognition.java.rekognition-collection-java-create-collection]
    

