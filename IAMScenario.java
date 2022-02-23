//snippet-sourcedescription:[IAMScenario.java demonstrates how to perform various AWS Identity and Access Management (IAM) operations.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[IAM]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[01/20/2022]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.iam;

// snippet-start:[iam.java2.scenario.import]
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.waiters.IamWaiter;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.StsException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.TimeUnit;
// snippet-end:[iam.java2.scenario.import]

/*
 To run this Java V2 code example, ensure that you have set up your development environment, including your credentials.

  For information, see this documentation topic:

  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html

  This example performs these operations:

  1. Creates a user that has no permissions.
  2. Creates a role and policy that grants Amazon S3 permissions.
  3. Grants the user permissions.
  4. Gets temporary credentials by assuming the role.
  5. Creates an Amazon S3 Service client object with the temporary credentials and list objects in an Amazon S3 bucket.
  6. Gets various IAM resources.
  7. Deletes the resources.
 */

// snippet-start:[iam.java2.scenario.main]
public class IAMScenario {

    public static final String PolicyDocument =
            "{" +
                    "  \"Version\": \"2012-10-17\"," +
                    "  \"Statement\": [" +
                    "    {" +
                    "        \"Effect\": \"Allow\"," +
                    "        \"Action\": [" +
                    "            \"s3:*\"" +
                    "       ]," +
                    "       \"Resource\": \"*\"" +
                    "    }" +
                    "   ]" +
                    "}";

    public static void main(String[] args) throws InterruptedException {

        final String usage = "\n" +
                "Usage:\n" +
                "    <username> <policyName> <roleName> <roleSessionName> <fileLocation> <bucketName> \n\n" +
                "Where:\n" +
                "    username - the name of the IAM user to create. \n\n" +
                "    policyName - the name of the policy to create. \n\n" +
                "    roleName - the name of the role to create. \n\n" +
                "    roleSessionName - the name of the session required for the assumeRole operation. \n\n" +
                "    fileLocation - the file location to the JSON required to create the role (see Readme). \n\n" +
                "    bucketName - the name of the Amazon S3 bucket from which objects are read. \n\n" ;

        if (args.length != 6) {
            System.out.println(usage);
           System.exit(1);
        }

        String userName =  args[0];
        String policyName = args[1];
        String roleName = args[2];
        String roleSessionName = args[3];
        String fileLocation = args[4];
        String bucketName = args[5];

        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder()
                .region(region)
                .build();

        // Create the IAM user.
       Boolean createUser = createIAMUser(iam, userName);


       if (createUser) {
           System.out.println(userName + " was successfully created.");

           String polArn = createIAMPolicy(iam, policyName);
           System.out.println("The policy " + polArn + " was successfully created.");

           String roleArn = createIAMRole(iam, roleName, fileLocation);
           System.out.println(roleArn + " was successfully created.");
           attachIAMRolePolicy(iam, roleName, polArn);
           String name = createServiceLinkedRole(iam);
           System.out.println("The Service Linked Role name is "+ name );

           System.out.println("*** Wait for 1 MIN so the resource is available");
           TimeUnit.MINUTES.sleep(1);
           assumeGivenRole(roleArn, roleSessionName, bucketName);

           System.out.println("*** Get the AWS resources");
           getPolicy(iam, polArn);
           getRole(iam, roleName);
           getSAMLProviders(iam);
           getGroups(iam);
           getPolicies(iam);
           getAttachedRolePolicies(iam, roleName);
           getRoles(iam);


           System.out.println("*** Getting ready to delete the AWS resources");
           deleteServiceLinkedRole(iam,name);
           deleteRole(iam, roleName, polArn);
           deleteIAMUser(iam, userName);
           System.out.println("This IAM Scenario has successfully completed");
       } else {
           System.out.println(userName +" was not successfully created.");
       }

    }


    public static String createServiceLinkedRole(IamClient iam) {

        try {

            CreateServiceLinkedRoleRequest request = CreateServiceLinkedRoleRequest.builder()
                    .awsServiceName("email.cognito-idp.amazonaws.com")
                    .description("An email.cognito role")
                    .build();

            CreateServiceLinkedRoleResponse response = iam.createServiceLinkedRole(request);
            return response.role().roleName();

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "" ;
    }

    public static void  deleteServiceLinkedRole(IamClient iam, String roleName) {

        try {

            DeleteServiceLinkedRoleRequest request = DeleteServiceLinkedRoleRequest.builder()
                    .roleName(roleName)
                    .build();

            iam.deleteServiceLinkedRole(request);
            System.out.println("Deleted the service linked role");

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    public static Boolean createIAMUser(IamClient iam, String username ) {

        try {
            // Create an IamWaiter object
            IamWaiter iamWaiter = iam.waiter();
            CreateUserRequest request = CreateUserRequest.builder()
                    .userName(username)
                    .build();

            // Wait until the user is created.
            CreateUserResponse response = iam.createUser(request);
            GetUserRequest userRequest = GetUserRequest.builder()
                    .userName(response.user().userName())
                    .build();

            WaiterResponse<GetUserResponse> waitUntilUserExists = iamWaiter.waitUntilUserExists(userRequest);
            waitUntilUserExists.matched().response().ifPresent(System.out::println);
            return true;

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return false;
    }

    public static String createIAMRole(IamClient iam, String rolename, String fileLocation ) {

        try {

            JSONObject jsonObject = (JSONObject) readJsonSimpleDemo(fileLocation);
            CreateRoleRequest request = CreateRoleRequest.builder()
                    .roleName(rolename)
                    .assumeRolePolicyDocument(jsonObject.toJSONString())
                    .description("Created using the AWS SDK for Java")
                    .build();

            CreateRoleResponse response = iam.createRole(request);
            System.out.println("The ARN of the role is "+response.role().arn());
            return response.role().arn();

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String createIAMPolicy(IamClient iam, String policyName ) {

        try {
            // Create an IamWaiter object
            IamWaiter iamWaiter = iam.waiter();
            CreatePolicyRequest request = CreatePolicyRequest.builder()
                    .policyName(policyName)
                    .policyDocument(PolicyDocument).build();

            CreatePolicyResponse response = iam.createPolicy(request);

            // Wait until the policy is created
            GetPolicyRequest polRequest = GetPolicyRequest.builder()
                    .policyArn(response.policy().arn())
                    .build();

            WaiterResponse<GetPolicyResponse> waitUntilPolicyExists = iamWaiter.waitUntilPolicyExists(polRequest);
            waitUntilPolicyExists.matched().response().ifPresent(System.out::println);
            return response.policy().arn();

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "" ;
    }

    public static void attachIAMRolePolicy(IamClient iam, String roleName, String policyArn ) {

        try {

            ListAttachedRolePoliciesRequest request = ListAttachedRolePoliciesRequest.builder()
                    .roleName(roleName)
                    .build();

            ListAttachedRolePoliciesResponse  response = iam.listAttachedRolePolicies(request);
            List<AttachedPolicy> attachedPolicies = response.attachedPolicies();

            String polArn;
            for (AttachedPolicy policy: attachedPolicies) {
                polArn = policy.policyArn();
                if (polArn.compareTo(policyArn)==0) {
                    System.out.println(roleName +
                            " policy is already attached to this role.");
                    return;
                }
            }

            AttachRolePolicyRequest attachRequest =
                    AttachRolePolicyRequest.builder()
                            .roleName(roleName)
                            .policyArn(policyArn)
                            .build();

            iam.attachRolePolicy(attachRequest);
            System.out.println("Successfully attached policy " + policyArn +
                    " to role " + roleName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    // Invoke an Amazon S3 operation using the Assumed Role.
    public static void assumeGivenRole(String roleArn, String roleSessionName, String bucketName) {

        StsClient stsClient = StsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        try {
            AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName(roleSessionName)
                    .build();

            AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
            Credentials myCreds = roleResponse.credentials();
            String key = myCreds.accessKeyId();
            String secKey = myCreds.secretAccessKey();
            String secToken = myCreds.sessionToken();

            // List all objects in an Amazon S3 bucket using the temp creds.
            Region region = Region.US_EAST_1;
            S3Client s3 = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(key, secKey, secToken)))
                    .region(region)
                    .build();

            System.out.println("Created a S3Client using temp credentials.");
            System.out.println("Listing objects in "+bucketName);
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                System.out.println("The name of the key is " + myValue.key());
                System.out.println("The owner is " + myValue.owner());
            }

        } catch (StsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void deleteRole(IamClient iam, String roleName, String polArn) {

        try {

            // First the policy needs to be detached.
            DetachRolePolicyRequest rolePolicyRequest = DetachRolePolicyRequest.builder()
                    .policyArn(polArn)
                    .roleName(roleName)
                    .build();

            iam.detachRolePolicy(rolePolicyRequest);

            // Delete the policy.
            DeletePolicyRequest request = DeletePolicyRequest.builder()
                    .policyArn(polArn)
                    .build();

            iam.deletePolicy(request);
            System.out.println("*** Successfully deleted "+polArn);

            // Delete the role.
            DeleteRoleRequest roleRequest = DeleteRoleRequest.builder()
                    .roleName(roleName)
                    .build();
            iam.deleteRole(roleRequest);
            System.out.println("*** Successfully deleted " +roleName);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void deleteIAMUser(IamClient iam, String userName) {

        try {
            DeleteUserRequest request = DeleteUserRequest.builder()
                    .userName(userName)
                    .build();

            iam.deleteUser(request);
            System.out.println("*** Successfully deleted " + userName);
        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void getPolicies(IamClient iam) {

        try {

            ListPoliciesResponse response = iam.listPolicies();
            List<Policy> policies = response.policies();
            for (Policy policy: policies) {
                System.out.println("The policy name is "+policy.policyName());
            }

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void getSAMLProviders(IamClient iam){

        try {

            ListSamlProvidersResponse response = iam.listSAMLProviders();
            List<SAMLProviderListEntry> providers = response.samlProviderList();
            for (SAMLProviderListEntry provider: providers) {
                System.out.println("The ARN of the provider is : "+provider.arn());
            }
        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    public static void getPolicy(IamClient iam, String policyArn) {

        try {

            GetPolicyRequest request = GetPolicyRequest.builder()
                    .policyArn(policyArn).build();

            GetPolicyResponse response = iam.getPolicy(request);
            System.out.format("Successfully retrieved policy %s",
                    response.policy().policyName());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void getRole(IamClient iam, String roleName) {

        try {

            GetRoleRequest roleRequest = GetRoleRequest.builder()
                    .roleName(roleName)
                    .build();

            GetRoleResponse response = iam.getRole(roleRequest) ;
            System.out.println("The ARN of the role is " +response.role().arn());

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void getAttachedRolePolicies(IamClient iam, String roleName) {

        try {

            ListAttachedRolePoliciesRequest request = ListAttachedRolePoliciesRequest .builder()
                    .roleName(roleName)
                    .maxItems(10)
                    .build();

            ListAttachedRolePoliciesResponse response = iam.listAttachedRolePolicies(request) ;
            List<AttachedPolicy> policies = response.attachedPolicies();
            for (AttachedPolicy policy: policies) {
                System.out.println("The name of the attached policy is  "+policy.policyName());
            }

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void getGroups(IamClient iam) {

        try {

            ListGroupsRequest request = ListGroupsRequest.builder()
                    .maxItems(10)
                    .build();

            ListGroupsResponse response = iam.listGroups(request) ;
            List<Group> groups = response.	groups();
            for (Group group: groups) {
                System.out.println("The group name is "+group.groupName());
            }

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

    }

    public static void getRoles(IamClient iam) {

        try {

            ListRolesResponse response = iam.listRoles() ;
            List<Role> roles = response.roles();
            for (Role role: roles) {
                System.out.println("The role name is "+role.roleName());
            }

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }


    public static Object readJsonSimpleDemo(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(reader);
    }
}
// snippet-end:[iam.java2.scenario.main]