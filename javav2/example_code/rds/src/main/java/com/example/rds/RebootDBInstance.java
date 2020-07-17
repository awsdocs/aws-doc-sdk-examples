//snippet-sourcedescription:[RebootDBInstance.java demonstrates how to reboot a RDS instance.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Relational Database Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[7/6/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
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


        String dbInstanceIdentifier = args[0];

        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
                .region(region)
                .build();

        rebootInstance(rdsClient, dbInstanceIdentifier) ;
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

