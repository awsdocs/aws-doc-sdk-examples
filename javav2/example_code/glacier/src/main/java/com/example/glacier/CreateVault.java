//snippet-sourcedescription:[CreateVault.java demonstrates how to create an Amazon Glacier vault.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Glacier]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/17/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package com.example.glacier;

// snippet-start:[glacier.java2.create.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.CreateVaultRequest;
import software.amazon.awssdk.services.glacier.model.CreateVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;
// snippet-end:[glacier.java2.create.import]

public class CreateVault {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "CreateVault - create an Amazon Glacier vault\n\n" +
                "Usage: CreateVault <vaultName>\n\n" +
                "Where:\n" +
                "  vaultName - the name of the vault to create.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String vaultName = args[0];

        // Create a GlacierClient object
        GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createGlacierVault(glacier, vaultName );
    }

    // snippet-start:[glacier.java2.create.main]
    public static void createGlacierVault(GlacierClient glacier, String vaultName ) {

       try {
           CreateVaultRequest vaultRequest = CreateVaultRequest.builder()
                   .vaultName(vaultName)
                   .build();

           CreateVaultResponse createVaultResult = glacier.createVault(vaultRequest);
           System.out.println("The URI of the new vault is " + createVaultResult.location());
       } catch(GlacierException e) {
           e.getStackTrace();

       }
        // snippet-end:[glacier.java2.create.main]
    }
}
