// snippet-sourcedescription:[ComprehendMedicalSample.java demonstrates getting medical entities from text.]
// snippet-service:[comprehend]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Comprehend Medical]
// snippet-keyword:[Code Sample]
// snippet-keyword:[AWSComprehendMedical]
// snippet-keyword:[DetectEntitiesRequest]
// snippet-keyword:[DetectEntitiesResult]
// snippet-keyword:[detectEntities]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-10]
// snippet-sourceauthor:[AWS]
/**
 * COPYRIGHT:
 *
 * Copyright 2018-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
// snippet-start:[comprehend-medical.java-detect-entities]
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClient;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesResult;
 
public class SampleAPICall {
 
    public static void main() {
 
        AWSCredentialsProvider credentials
                = new AWSStaticCredentialsProvider(new BasicAWSCredentials("YOUR AWS ACCESS KEY", "YOUR AWS SECRET"));
 
        AWSComprehendMedical client = AWSComprehendMedicalClient.builder()
                                                                .withCredentials(credentials)
                                                                .withRegion("YOUR REGION")
                                                                .build();
 
 
        DetectEntitiesRequest request = new DetectEntitiesRequest();
        request.setText("cerealx 84 mg daily");
 
        DetectEntitiesResult result = client.detectEntities(request);
        result.getEntities().forEach(System.out::println);
    }
}
// snippet-end:[comprehend-medical.java-detect-entities]