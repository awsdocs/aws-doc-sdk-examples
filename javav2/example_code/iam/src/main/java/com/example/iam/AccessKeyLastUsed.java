//snippet-sourcedescription:[AccessKeyLastUsed.java demonstrates how to display the time that an access key was last used.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[IAM]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.iam;

// snippet-start:[iam.java2.access_key_last_used.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GetAccessKeyLastUsedRequest;
import software.amazon.awssdk.services.iam.model.GetAccessKeyLastUsedResponse;
import software.amazon.awssdk.services.iam.model.IamException;
// snippet-end:[iam.java2.access_key_last_used.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AccessKeyLastUsed {
    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <accessId> \n\n" +
            "Where:\n" +
            "    accessId - An access key id that you can obtain from the AWS Management Console. \n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String accessId = args[0];
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getAccessKeyLastUsed(iam, accessId) ;
        iam.close();
    }

    // snippet-start:[iam.java2.access_key_last_used.main]
    public static void getAccessKeyLastUsed(IamClient iam, String accessId ){

        try {
            GetAccessKeyLastUsedRequest request = GetAccessKeyLastUsedRequest.builder()
                .accessKeyId(accessId)
                .build();

            GetAccessKeyLastUsedResponse response = iam.getAccessKeyLastUsed(request);
            System.out.println("Access key was last used at: " +
                    response.accessKeyLastUsed().lastUsedDate());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Done");
    }
    // snippet-end:[iam.java2.access_key_last_used.main]
}
