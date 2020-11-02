//snippet-sourcedescription:[UpdateServerCertificate.java demonstrates how to update the name of an AWS Identity and Access Management (IAM) server certificate.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.update_server_certificate.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.UpdateServerCertificateRequest;
import software.amazon.awssdk.services.iam.model.UpdateServerCertificateResponse;
// snippet-end:[iam.java2.update_server_certificate.import]

public class UpdateServerCertificate {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    UpdateServerCertificate <curName> <newName> \n\n" +
                "Where:\n" +
                "    curName - the current certificate name. \n\n" +
                "    newName - an updated certificate name. \n\n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // Read the command line arguments
        String curName = args[0];
        String newName = args[1];

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .build();

        updateCertificate(iam, curName, newName) ;
        System.out.println("Done");
        iam.close();
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
     }
    // snippet-end:[iam.java2.update_server_certificate.main]
}
// snippet-end:[iam.java2.update_server_certificate.complete]