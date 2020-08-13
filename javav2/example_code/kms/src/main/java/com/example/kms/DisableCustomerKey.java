//snippet-sourcedescription:[DisableCustomerKey.java demonstrates how to disable a key.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/10/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.*
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.kms;

// snippet-start:[kms.java2_disable_key.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DisableKeyRequest;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_disable_key.import]

public class DisableCustomerKey {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a key ID value\n" +
                        "Usage: DisableCustomerKey <key-id>\n" +
                        "Example: DisableCustomerKey 1234abcd-12ab-34cd-56ef-1234567890ab \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        disableKey(kmsClient, keyId);
    }

    // snippet-start:[kms.java2_disable_key.main]
    public static void disableKey( KmsClient kmsClient, String keyId) {

       try {

           DisableKeyRequest keyRequest = DisableKeyRequest.builder()
                .keyId(keyId)
                .build();

           kmsClient.disableKey(keyRequest);
        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[kms.java2_disable_key.main]
    }
}
