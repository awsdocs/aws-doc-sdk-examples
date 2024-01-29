// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.ListServerCertificatesRequest;
import com.amazonaws.services.identitymanagement.model.ListServerCertificatesResult;
import com.amazonaws.services.identitymanagement.model.ServerCertificateMetadata;

/**
 * Lists all server certificates associated with an AWS account
 */
public class ListServerCertificates {
    public static void main(String[] args) {

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        boolean done = false;
        ListServerCertificatesRequest request = new ListServerCertificatesRequest();

        while (!done) {

            ListServerCertificatesResult response = iam.listServerCertificates(request);

            for (ServerCertificateMetadata metadata : response.getServerCertificateMetadataList()) {
                System.out.printf("Retrieved server certificate %s",
                        metadata.getServerCertificateName());
            }

            request.setMarker(response.getMarker());

            if (!response.getIsTruncated()) {
                done = true;
            }
        }
    }
}
