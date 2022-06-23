// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListEmailIdentities.java demonstrates how to get a list of email identities that are associated with your Amazon Web Services account.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sesv2;

// snippet-start:[ses.java2.identities.sesv2.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.IdentityInfo;
import software.amazon.awssdk.services.sesv2.model.ListEmailIdentitiesRequest;
import software.amazon.awssdk.services.sesv2.model.ListEmailIdentitiesResponse;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import java.util.List;
// snippet-end:[ses.java2.identities.sesv2.import]


/**
 * Before running this AWS SDK for Java (v2) code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListEmailIdentities {

        public static void main(String[] args) {

                Region region = Region.US_EAST_1;
                SesV2Client sesv2Client = SesV2Client.builder()
                        .region(region)
                        .credentialsProvider(ProfileCredentialsProvider.create())
                        .build();

                listSESIdentities(sesv2Client);
        }

        // snippet-start:[ses.java2.identities.sesv2.main]
        public static void listSESIdentities(SesV2Client sesV2Client) {

                ListEmailIdentitiesRequest identitiesRequest = ListEmailIdentitiesRequest.builder()
                        .pageSize(5)
                        .build();

                try {
                        ListEmailIdentitiesResponse response = sesV2Client.listEmailIdentities(identitiesRequest);
                        final List<IdentityInfo> identities = response.emailIdentities();
                        identities.forEach(identity -> System.out.println(identity.identityName()));

                } catch (SesV2Exception e) {
                        System.err.println(e.awsErrorDetails().errorMessage());
                        System.exit(1);
                }
        }
        // snippet-end:[ses.java2.identities.sesv2.main]
}
