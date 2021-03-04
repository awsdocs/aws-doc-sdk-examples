//snippet-sourcedescription:[RebootDBInstance.java demonstrates how to reboot an Amazon Relational Database Service (RDS) instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/6/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.rds;

// snippet-start:[rds.java2.reboot_instance.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.RebootDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.RebootDbInstanceResponse;
import software.amazon.awssdk.services.rds.model.RdsException;
// snippet-end:[rds.java2.reboot_instance.import]


public class RebootDBInstance {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    RebootDBInstance <dbInstanceIdentifier> \n\n" +
                "Where:\n" +
                "    dbInstanceIdentifier - the database instance identifier \n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String dbInstanceIdentifier = args[0];
        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
                .region(region)
                .build();

        rebootInstance(rdsClient, dbInstanceIdentifier) ;
        rdsClient.close();
    }

    // snippet-start:[rds.java2.reboot_instance.main]
    public static void rebootInstance(RdsClient rdsClient, String dbInstanceIdentifier ) {

        try {
            // For a demo - modify the DB instance by modifying the master password
            RebootDbInstanceRequest rebootDbInstanceRequest = RebootDbInstanceRequest.builder()
                    .dbInstanceIdentifier(dbInstanceIdentifier)
                    .build();

            RebootDbInstanceResponse instanceResponse = rdsClient.rebootDBInstance(rebootDbInstanceRequest);
            System.out.print("The database "+ instanceResponse.dbInstance().dbInstanceArn() +" was rebooted");

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }

        // snippet-end:[rds.java2.reboot_instance.main]
    }
}

