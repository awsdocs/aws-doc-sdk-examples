// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.ec2;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateLaunchTemplateRequest;
import software.amazon.awssdk.services.ec2.model.CreateLaunchTemplateResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.RequestLaunchTemplateData;

public class CreateLaunchTemplate {

    public static void main(String[] args) {
        final String usage = """
            Usage:
               <launchTemplateName> <instanceType> <imageId> <keyName>
               
            Where:
               launchTemplateName - The name of the launch template to create.
               instanceType       - The EC2 instance type (e.g., t2.2xlarge).
               imageId            - The AMI ID for the instance (e.g., ami-0f6832b69407e9746).
               keyName            - The name of the key pair for SSH access.
            """;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String launchTemplateName = args[0];
        String instanceType = args[1];
        String imageId = args[2];
        String keyName = args[3];

        Ec2Client ec2 = Ec2Client.builder()
                .region(Region.US_EAST_1)
                .build();

        createLaunchTemplate(ec2, launchTemplateName, instanceType, imageId, keyName);
    }

    public static void createLaunchTemplate(Ec2Client ec2, String launchTemplateName, String instanceType, String imageId, String keyName) {
        try {
            RequestLaunchTemplateData launchTemplateData = RequestLaunchTemplateData.builder()
                    .instanceType(instanceType)
                    .imageId(imageId)
                    .keyName(keyName)
                    .build();

            CreateLaunchTemplateRequest launchTemplateRequest = CreateLaunchTemplateRequest.builder()
                    .launchTemplateName(launchTemplateName)
                    .launchTemplateData(launchTemplateData)
                    .versionDescription("Initial version with instance type")
                    .build();

            CreateLaunchTemplateResponse response = ec2.createLaunchTemplate(launchTemplateRequest);
            System.out.println("Launch Template created successfully: " + response.launchTemplate().launchTemplateId());

        } catch (Ec2Exception e) {
            System.err.println("Failed to create launch template: " + e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

}