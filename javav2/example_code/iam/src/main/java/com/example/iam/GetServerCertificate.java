//snippet-sourcedescription:[GetServerCertificate.java demonstrates how to get information about an IAM server certificate.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing * permissions and limitations under the License.
 */
package com.example.iam;
// snippet-start:[iam.java2.get_server_certificate.complete]
// snippet-start:[iam.java2.get_server_certificate.import]
import software.amazon.awssdk.services.iam.model.GetServerCertificateRequest;
import software.amazon.awssdk.services.iam.model.GetServerCertificateResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.IamException;

// snippet-end:[iam.java2.get_server_certificate.import]
/**
 * Gets a server certificate
 */
public class GetServerCertificate {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a certificate name\n" +
                        "Ex: GetServerCertificate <certificate-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String certName = args[0];

        // snippet-start:[iam.java2.get_server_certificate.main]
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        try {
            GetServerCertificateRequest request = GetServerCertificateRequest.builder()
                .serverCertificateName(certName).build();

            GetServerCertificateResponse response = iam.getServerCertificate(request);
            // snippet-end:[iam.java2.get_server_certificate.main]
            System.out.format("Successfully retrieved certificate with body %s",
                response.serverCertificate().certificateBody());

    } catch (
    IamException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
        System.out.println("Done");
    }
}

// snippet-end:[iam.java2.get_server_certificate.complete]
