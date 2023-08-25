// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[PutParameter.java demonstrates how to add a parameter to the system.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Systems Management]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ssm;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.ParameterType;
import software.amazon.awssdk.services.ssm.model.PutParameterRequest;
import software.amazon.awssdk.services.ssm.model.PutParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

public class PutParameter {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <paraName>\n\n" +
            "Where:\n" +
            "    paraName - The name of the parameter.\n" +
            "    paraValue - The value of the parameter.\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String paraName = args[0];
        String paraValue = args[1];
        Region region = Region.US_EAST_1;
        SsmClient ssmClient = SsmClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        putParaValue(ssmClient, paraName, paraValue);
        ssmClient.close();
    }

    // snippet-start:[ssm.Java2.put_para_value.main]
    public static void putParaValue(SsmClient ssmClient, String paraName, String value) {
        try {
             PutParameterRequest parameterRequest = PutParameterRequest.builder()
                 .name(paraName)
                 .type(ParameterType.STRING)
                 .value(value)
                 .build();

             ssmClient.putParameter(parameterRequest);
             System.out.println("The parameter was successfully added.");

        } catch (SsmException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ssm.Java2.put_para_value.main]
}
