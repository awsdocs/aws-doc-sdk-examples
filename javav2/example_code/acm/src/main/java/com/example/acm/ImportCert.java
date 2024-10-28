// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.acm;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.acm.AcmClient;
import software.amazon.awssdk.services.acm.model.ImportCertificateRequest;
import software.amazon.awssdk.services.acm.model.ImportCertificateResponse;
import software.amazon.awssdk.utils.IoUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

// snippet-start:[acm.java2.import_cert.main]
/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ImportCert {

    public static void main(String[] args) {

        final String usage = """

            Usage:    <certificatePath> <privateKeyPath>

            Where:
                certificatePath - the path to the SSL/TLS certificate file.
                privateKeyPath - the path to the private key file associated with the SSL/TLS certificate.
            """;

        if (args.length != 2) {
            System.out.println(usage);
            return;
        }

        String certificatePath = args[0];
        String privateKeyPath = args[1];
        String certificateArn = importCertificate(certificatePath, privateKeyPath);
        System.out.println("Certificate imported with ARN: " + certificateArn);
    }

    /**
     * Imports an SSL/TLS certificate and private key into AWS Certificate Manager (ACM) for use with
     * AWS services.
     *
     * @param certificatePath the file path to the SSL/TLS certificate
     * @param privateKeyPath  the file path to the private key associated with the certificate
     * @throws IOException if there is an error reading the certificate or private key files
     */
    public static String importCertificate(String certificatePath, String privateKeyPath) {
        AcmClient acmClient = AcmClient.create();
        try {
            byte[] certificateBytes = readFileBytes(certificatePath);
            byte[] privateKeyBytes = readFileBytes(privateKeyPath);

            ImportCertificateRequest request = ImportCertificateRequest.builder()
                .certificate(SdkBytes.fromByteBuffer(ByteBuffer.wrap(certificateBytes)))
                .privateKey(SdkBytes.fromByteBuffer(ByteBuffer.wrap(privateKeyBytes)))
                .build();

            ImportCertificateResponse response = acmClient.importCertificate(request);
            String certificateArn = response.certificateArn();
            return certificateArn;
        } catch (IOException e) {
            System.err.println("Error reading certificate or private key file: " + e.getMessage());
        }
        return "";
    }

    private static byte[] readFileBytes(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return IoUtils.toByteArray(inputStream);
        }
    }
}
// snippet-end:[acm.java2.import_cert.main]
