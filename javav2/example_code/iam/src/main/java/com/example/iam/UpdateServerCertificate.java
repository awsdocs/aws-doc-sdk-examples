//snippet-sourcedescription:[UpdateServerCertificate.java demonstrates how to update the name of an IAM server certificate.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
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
package com.example.iam;

// snippet-start:[iam.java2.update_server_certificate.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.UpdateServerCertificateRequest;
import software.amazon.awssdk.services.iam.model.UpdateServerCertificateResponse;
// snippet-end:[iam.java2.update_server_certificate.import]

/**
 * Updates a server certificate name
 */
public class UpdateServerCertificate {
    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply the current certificate name and\n" +
                        "a new name. Ex:\n\n" +
                        "UpdateServerCertificate <current-name> <new-name>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String curName = args[0];
        String newName = args[1];

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        updateCertificate(iam, curName, newName) ;
    }

    // snippet-start:[iam.java2.update_server_certificate.main]
    public static void updateCertificate(IamClient iam, String curName, String newName) {

        try {
            UpdateServerCertificateRequest request =
                UpdateServerCertificateRequest.builder()
                        .serverCertificateName(curName)
                        .newServerCertificateName(newName)
                        .build();

            UpdateServerCertificateResponse response =
                iam.updateServerCertificate(request);


            System.out.printf("Successfully updated server certificate to name %s",
                newName);

        } catch (IamException e) {
             System.err.println(e.awsErrorDetails().errorMessage());
             System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[iam.java2.update_server_certificate.main]
}
// snippet-end:[iam.java2.update_server_certificate.complete]
