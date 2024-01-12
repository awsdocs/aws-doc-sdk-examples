// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.DeleteServerCertificateRequest;
import com.amazonaws.services.identitymanagement.model.DeleteServerCertificateResult;

/**
 * Deletes an IAM server certificate
 */
public class DeleteServerCertificate {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a certificate name\n" +
                "Ex: DeleteServerCertificate <certificate-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String cert_name = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        DeleteServerCertificateRequest request = new DeleteServerCertificateRequest()
                .withServerCertificateName(cert_name);

        DeleteServerCertificateResult response = iam.deleteServerCertificate(request);

        System.out.println("Successfully deleted server certificate " +
                cert_name);
    }
}
