/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.resilient;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateLaunchTemplateRequest;
import software.amazon.awssdk.services.ec2.model.CreateLaunchTemplateResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.LaunchTemplate;
import software.amazon.awssdk.services.ec2.model.LaunchTemplateIamInstanceProfileSpecificationRequest;
import software.amazon.awssdk.services.ec2.model.RequestLaunchTemplateData;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AddRoleToInstanceProfileRequest;
import software.amazon.awssdk.services.iam.model.AttachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.CreateInstanceProfileRequest;
import software.amazon.awssdk.services.iam.model.CreatePolicyRequest;
import software.amazon.awssdk.services.iam.model.CreatePolicyResponse;
import software.amazon.awssdk.services.iam.model.CreateRoleRequest;
import software.amazon.awssdk.services.iam.model.EntityAlreadyExistsException;
import software.amazon.awssdk.services.iam.model.GetInstanceProfileRequest;
import software.amazon.awssdk.services.iam.model.GetInstanceProfileResponse;
import software.amazon.awssdk.services.iam.model.GetRoleRequest;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.ListPoliciesRequest;
import software.amazon.awssdk.services.iam.model.ListPoliciesResponse;
import software.amazon.awssdk.services.iam.model.NoSuchEntityException;
import software.amazon.awssdk.services.iam.model.Policy;
import software.amazon.awssdk.services.iam.model.PolicyScopeType;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class LaunchTemplateCreator {

    private static Ec2Client ec2Client;
    private static SsmClient ssmClient;
    private static IamClient iamClient;

    private Ec2Client getEc2Client() {
        if (ec2Client == null) {
            ec2Client = Ec2Client.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return ec2Client;
    }

    private static IamClient getIAMClient() {
        if (iamClient == null) {
            iamClient = IamClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return iamClient;
    }

    private SsmClient getSSMClient() {
        if (ssmClient == null) {
            ssmClient = SsmClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return ssmClient;
    }

    // snippet-start:[javav2.cross_service.resilient_service.ec2.CreateLaunchTemplate]
    public void createTemplate(String policyFile, String policyName, String profileName, String startScript, String templateName, String roleName) {
        String profileArn = createInstanceProfile(policyFile, policyName, profileName, roleName);
        String amiId = getLatestAmazonLinuxAmiId();

        try {
            String userData = getBase64EncodedUserData(startScript);
            LaunchTemplateIamInstanceProfileSpecificationRequest specification = LaunchTemplateIamInstanceProfileSpecificationRequest.builder()
                .arn(profileArn)
                .build();

            RequestLaunchTemplateData templateData = RequestLaunchTemplateData.builder()
                .instanceType(InstanceType.T3_MICRO) // Replace with your desired instance type.
                .imageId(amiId)
                .iamInstanceProfile(specification)
                .userData(userData)
                .build();

            CreateLaunchTemplateRequest templateRequest = CreateLaunchTemplateRequest.builder()
                .launchTemplateName(templateName)
                .launchTemplateData(templateData)
                .build();

            CreateLaunchTemplateResponse templateResponse = getEc2Client().createLaunchTemplate(templateRequest);
            LaunchTemplate template = templateResponse.launchTemplate();
            System.out.println("\nCreated launch template named " + template.launchTemplateName());

        } catch (Ec2Exception e) {
            System.out.println("An exception occurred "+e.getMessage());
        }
    }
    // snippet-end:[javav2.cross_service.resilient_service.ec2.CreateLaunchTemplate]

    // snippet-start:[javav2.cross_service.resilient_service.iam.CreateInstanceProfile]
    public String createInstanceProfile(String policyFile, String policyName, String profileName, String roleName) {
        boolean instacneProfileExists = false;
        String assumeRoleDoc = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {"Service": "ec2.amazonaws.com"},
                        "Action": "sts:AssumeRole"
                    }
                ]
            }""";

        // Read the policy document for the role
        String instancePolicyDoc = "";
        try {
            instancePolicyDoc = Files.readString(Paths.get(policyFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Checks if the policy exists, if not creates it.
        String policyArn = createPolicy(policyName, instancePolicyDoc);

        // Checks if the role exists, if not creates it.
        if (!createRoleExists(roleName, assumeRoleDoc)) {

            // Attach role policy.
            AttachRolePolicyRequest attachRequest = AttachRolePolicyRequest.builder()
                .roleName(roleName)
                .policyArn(policyArn)
                .build();

            getIAMClient().attachRolePolicy(attachRequest);
        } else {
            System.out.printf("Role %s already exists, nothing to do.", roleName);
        }

        try {
            CreateInstanceProfileRequest instanceProfileRequest = CreateInstanceProfileRequest.builder()
                .instanceProfileName(profileName)
                .build();

            getIAMClient().createInstanceProfile(instanceProfileRequest);

        } catch (EntityAlreadyExistsException e){
            // This exception is thrown if the instance profile exists.
            System.out.println(profileName + "already exists - moving on");
            instacneProfileExists = true;
        }

        String profileArn = getInstanceProfile(profileName);

        // Only call addRoleToInstanceProfile if it's new instance profile.
        if (!instacneProfileExists) {
            AddRoleToInstanceProfileRequest profileRequest = AddRoleToInstanceProfileRequest.builder()
                .instanceProfileName(profileName)
                .roleName(roleName)
                .build();

            getIAMClient().addRoleToInstanceProfile(profileRequest);
            System.out.printf("Created profile %s and added role %s.", profileName, roleName);
        }
        return profileArn;
    }
    // snippet-end:[javav2.cross_service.resilient_service.iam.CreateInstanceProfile]

    private String getInstanceProfile(String profileName) {
        GetInstanceProfileRequest profileRequest = GetInstanceProfileRequest.builder()
            .instanceProfileName(profileName)
            .build();

        GetInstanceProfileResponse resp = getIAMClient().getInstanceProfile(profileRequest);
        return resp.instanceProfile().arn();

    }

    public String createPolicy(String policyName, String policyDocument) {
        String policyArn;
        // Determine if policy exists already.
        policyArn = checkPolicyExists(policyName);
        if (policyArn.isEmpty()) {
            try {
                CreatePolicyRequest policyRequest = CreatePolicyRequest.builder()
                    .policyName(policyName)
                    .policyDocument(policyDocument)
                    .build();

                CreatePolicyResponse policyResponse = getIAMClient().createPolicy(policyRequest);
                policyArn = policyResponse.policy().arn();
                System.out.println("Created policy with ARN " + policyArn);

            } catch (IamException e) {
                System.out.println("Error creating IAM policy: " + e.getMessage());
            }
        }

        return policyArn;
    }

    public String checkPolicyExists(String polName) {
        String polARN = "";
        ListPoliciesRequest policiesRequest = ListPoliciesRequest.builder()
            .scope(PolicyScopeType.LOCAL)
            .build();

        ListPoliciesResponse policiesResponse = getIAMClient().listPolicies(policiesRequest);
        List<Policy> policyList = policiesResponse.policies();
        for (Policy pol: policyList) {
            if (pol.policyName().compareTo(polName)==0) {
                return pol.arn();
            }
        }
        return ""; // Pol does not exist
    }

    private boolean doesRoleExist(String roleName) {
        GetRoleRequest request = GetRoleRequest.builder()
            .roleName(roleName)
            .build();

        try {
            getIAMClient().getRole(request);
            return true;
        } catch (NoSuchEntityException e) {
            return false;
        }
    }

    private boolean createRoleExists(String roleName, String assumeRoleDoc) {
        if (!doesRoleExist(roleName)) {
            CreateRoleRequest roleRequest = CreateRoleRequest.builder()
                .roleName(roleName)
                .assumeRolePolicyDocument(assumeRoleDoc)
                .build();

            getIAMClient().createRole(roleRequest);
            System.out.println(roleName + " created");
            return false;
        } else {
            System.out.println(roleName + " exists");
            return true;
        }
    }

    public String getLatestAmazonLinuxAmiId() {
        String parameterName = "/aws/service/ami-amazon-linux-latest/amzn2-ami-hvm-x86_64-gp2";
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
            .name(parameterName)
            .build();

        GetParameterResponse parameterResponse = getSSMClient().getParameter(parameterRequest);
        return parameterResponse.parameter().value();
    }

    public String getBase64EncodedUserData(String scriptPath) {
        try {
            String scriptContent = new String(Files.readAllBytes(Paths.get(scriptPath)), StandardCharsets.UTF_8);
            byte[] encodedBytes = Base64.getEncoder().encode(scriptContent.getBytes(StandardCharsets.UTF_8));
            return new String(encodedBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
