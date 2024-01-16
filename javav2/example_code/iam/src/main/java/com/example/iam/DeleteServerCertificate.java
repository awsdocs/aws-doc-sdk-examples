// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.iam;

// snippet-start:[iam.java2.delete_server_certificate.main]
// snippet-start:[iam.java2.delete_server_certificate.import]
import software.amazon.awssdk.services.iam.model.DeleteServerCertificateRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;
// snippet-end:[iam.java2.delete_server_certificate.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteServerCertificate {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <certName>\s

                Where:
                    certName - A certificate name to delete.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String certName = args[0];
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .build();

        deleteCert(iam, certName);
        System.out.println("Done");
        iam.close();
    }

    public static void deleteCert(IamClient iam, String certName) {
        try {
            DeleteServerCertificateRequest request = DeleteServerCertificateRequest.builder()
                    .serverCertificateName(certName)
                    .build();

            iam.deleteServerCertificate(request);
            System.out.println("Successfully deleted server certificate " + certName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[iam.java2.delete_server_certificate.main]
