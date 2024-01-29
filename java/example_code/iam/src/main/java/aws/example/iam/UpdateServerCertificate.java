// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.UpdateServerCertificateRequest;
import com.amazonaws.services.identitymanagement.model.UpdateServerCertificateResult;

/**
 * Updates a server certificate name
 */
public class UpdateServerCertificate {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply the current certificate name and\n" +
                "a new name. Ex:\n\n" +
                "UpdateServerCertificate <current-name> <new-name>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String cur_name = args[0];
        String new_name = args[1];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        UpdateServerCertificateRequest request = new UpdateServerCertificateRequest()
                .withServerCertificateName(cur_name)
                .withNewServerCertificateName(new_name);

        UpdateServerCertificateResult response = iam.updateServerCertificate(request);

        System.out.printf("Successfully updated server certificate to name %s",
                new_name);
    }
}
