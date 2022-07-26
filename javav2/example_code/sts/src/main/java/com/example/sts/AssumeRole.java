// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[AssumeRole.java demonstrates how to obtain a set of temporary security credentials by using AWS Security Token Service (AWS STS).]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[AWS Security Token Service (AWS STS)]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sts;

// snippet-start:[sts.java2.assume_role.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
// snippet-end:[sts.java2.assume_role.import]

/**
 * To make this code example work, create a Role that you want to assume.
 * Then define a Trust Relationship in the AWS Console. You can use this as an example:
 *
 * {
 *   "Version": "2012-10-17",
 *   "Statement": [
 *     {
 *       "Effect": "Allow",
 *       "Principal": {
 *         "AWS": "<Specify the ARN of your IAM user you are using in this code example>"
 *       },
 *       "Action": "sts:AssumeRole"
 *     }
 *   ]
 * }
 *
 *  For more information, see "Editing the Trust Relationship for an Existing Role" in the AWS Directory Service guide.
 *
 *  Also, set up your development environment, including your credentials.
 *
 *  For information, see this documentation topic:
 *
 *  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class AssumeRole {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <roleArn> <roleSessionName> \n\n" +
            "Where:\n" +
            "    roleArn - The Amazon Resource Name (ARN) of the role to assume (for example, rn:aws:iam::000008047983:role/s3role). \n"+
            "    roleSessionName - An identifier for the assumed role session (for example, mysession). \n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String roleArn = args[0];
        String roleSessionName = args[1];
        Region region = Region.US_EAST_1;
        StsClient stsClient = StsClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        assumeGivenRole(stsClient, roleArn, roleSessionName);
        stsClient.close();
    }

    // snippet-start:[sts.java2.assume_role.main]
    public static void assumeGivenRole(StsClient stsClient, String roleArn, String roleSessionName) {

        try {
            AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(roleSessionName)
                .build();

           AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
           Credentials myCreds = roleResponse.credentials();

           // Display the time when the temp creds expire.
           Instant exTime = myCreds.expiration();
           String tokenInfo = myCreds.sessionToken();

           // Convert the Instant to readable date.
           DateTimeFormatter formatter =
                   DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                           .withLocale( Locale.US)
                           .withZone( ZoneId.systemDefault() );

           formatter.format( exTime );
           System.out.println("The token "+tokenInfo + "  expires on " + exTime );

       } catch (StsException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
   }
   // snippet-end:[sts.java2.assume_role.main]
}