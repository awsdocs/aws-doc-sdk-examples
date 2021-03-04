// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListIdentities.java demonstrates how to obtain a list of identities for your AWS account.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/06/2020]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ses;

// snippet-start:[ses.java2.identities.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.ListIdentitiesResponse;
import software.amazon.awssdk.services.ses.model.SesException;
import java.io.IOException;
import java.util.List;
// snippet-end:[ses.java2.identities.import]

public class ListIdentities {

    public static void main(String[] args) throws IOException {

        Region region = Region.US_WEST_2;
        SesClient client = SesClient.builder()
                .region(region)
                .build();

        listSESIdentities(client);
    }

    // snippet-start:[ses.java2.identities.main]
    public static void listSESIdentities(SesClient client) {

       try {
            ListIdentitiesResponse identitiesResponse = client.listIdentities();
            List<String> identities = identitiesResponse.identities();

            for (String identity: identities) {
                System.out.println("The identity is "+identity);
            }
       } catch (SesException e) {
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
       }
    }
    // snippet-end:[ses.java2.identities.main]
}