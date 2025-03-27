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
        String groupName = "ScottASG606" ; //rgs[0];
        String launchTemplateName = "MyTemplate5" ;//args[1];
        String vpcZoneId = "subnet-0ddc451b8a8a1aa44" ; //args[2];
        String instanceType= "t2.2xlarge" ;
        String imageId = "ami-0f6832b69407e9746" ;
        String keyName = "TestKeyPair";

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