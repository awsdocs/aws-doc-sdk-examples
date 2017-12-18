/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.ec2;
import software.amazon.awssdk.services.ec2.EC2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

/**
 * Describes all EC2 instances associated with an AWS account
 */
public class DescribeInstances
{
    public static void main(String[] args)
    {
        EC2Client ec2 = EC2Client.create();
        boolean done = false;

        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();

        while(!done) {
            DescribeInstancesResponse response = ec2.describeInstances(request);

            for(Reservation reservation : response.reservations()) {
                for(Instance instance : reservation.instances()) {
                    System.out.printf(
                        "Found reservation with id %s, " +
                        "AMI %s, " +
                        "type %s, " +
                        "state %s " +
                        "and monitoring state %s",
                        instance.instanceId(),
                        instance.imageId(),
                        instance.instanceType(),
                        instance.state().name(),
                        instance.monitoring().state());
                    System.out.println("");
                }
            }

            if(response.nextToken() == null) {
                done = true;
            }
        }
    }
}

