// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.secretsmanager;

import java.nio.ByteBuffer;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.secretsmanager.*;
import com.amazonaws.services.secretsmanager.model.*;

public class GetSecretValue {
    public static void main(String[] args) {
        getSecret();
    }

    public static void getSecret() {

        String secretName = "testSecret";
        String endpoint = "secretsmanager.us-west-2.amazonaws.com";
        String region = "us-west-2";

        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
        clientBuilder.setEndpointConfiguration(config);
        AWSSecretsManager client = clientBuilder.build();

        String secret;
        ByteBuffer binarySecretData;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName).withVersionStage("AWSCURRENT");
        GetSecretValueResult getSecretValueResult = null;
        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        } catch (ResourceNotFoundException e) {
            System.out.println("The requested secret " + secretName + " was not found");
        } catch (InvalidRequestException e) {
            System.out.println("The request was invalid due to: " + e.getMessage());
        } catch (InvalidParameterException e) {
            System.out.println("The request had invalid params: " + e.getMessage());
        }

        if (getSecretValueResult == null) {
            return;
        }

        // Depending on whether the secret was a string or binary, one of these fields
        // will be populated
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            System.out.println(secret);
        } else {
            binarySecretData = getSecretValueResult.getSecretBinary();
            System.out.println(binarySecretData.toString());
        }

    }

}
