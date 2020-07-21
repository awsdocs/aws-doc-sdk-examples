//snippet-sourcedescription:[DescribeDBInstances.java demonstrates how to describe Amazon RDS instances.]
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

// snippet-start:[rds.java2.describe_instances.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.RdsException;
import java.util.List;
// snippet-end:[rds.java2.describe_instances.import]

public class DescribeDBInstances {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        RdsClient rdsClient = RdsClient.builder()
                .region(region)
                .build();

        describeInstances(rdsClient) ;
    }

    // snippet-start:[rds.java2.describe_instances.main]
    public static void describeInstances(RdsClient rdsClient) {

        try {

            DescribeDbInstancesResponse response = rdsClient.describeDBInstances();

            List<DBInstance> instanceList = response.dbInstances();

            for (DBInstance instance: instanceList) {
                System.out.println("Instance identifier is: "+instance.dbInstanceIdentifier());
                System.out.println("The engine is " +instance.engine());
                System.out.println("Connection endpoint is" +instance.endpoint().address());
            }

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        // snippet-end:[rds.java2.describe_instances.main]
    }
}
