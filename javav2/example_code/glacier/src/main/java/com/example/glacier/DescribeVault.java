//snippet-sourcedescription:[DescribeVault.java demonstrates how to describe an Amazon Glacier vault.]
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

// snippet-start:[glacier.java2.describe.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeVaultRequest;
import software.amazon.awssdk.services.glacier.model.DescribeVaultResponse;
import software.amazon.awssdk.services.glacier.model.GlacierException;
// snippet-end:[glacier.java2.describe.import]

public class DescribeVault {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "DescribeVault - describes an Amazon Glacier vault\n\n" +
                "Usage: DescribeVault <vaultName>\n\n" +
                "Where:\n" +
                "  vaultName - the name of the vault.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
       }

      String vaultName = args[0];

        // Create a GlacierClient object
      GlacierClient glacier = GlacierClient.builder()
                .region(Region.US_EAST_1)
                .build();

      describeGlacierVault(glacier, vaultName );
    }

    // snippet-start:[glacier.java2.describe.main]
    public static void describeGlacierVault(GlacierClient glacier, String vaultName) {

        try {
            DescribeVaultRequest describeVaultRequest = DescribeVaultRequest.builder()
                    .vaultName(vaultName)
                    .build();

            DescribeVaultResponse desVaultResult = glacier.describeVault(describeVaultRequest);
            System.out.println("Describing the vault: " + vaultName);
            System.out.print(
                    "CreationDate: " + desVaultResult.creationDate() +
                            "\nLastInventoryDate: " + desVaultResult.lastInventoryDate() +
                            "\nNumberOfArchives: " + desVaultResult.numberOfArchives() +
                            "\nSizeInBytes: " + desVaultResult.sizeInBytes() +
                            "\nVaultARN: " + desVaultResult.vaultARN() +
                            "\nVaultName: " + desVaultResult.vaultName());
        } catch(GlacierException e) {
            e.getStackTrace();
            System.exit(1);
        }
        // snippet-end:[glacier.java2.describe.main]
    }
}
