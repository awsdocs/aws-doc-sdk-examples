/**
 * Copyright 2018-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

// snippet-sourcedescription:[CrossRegionReplication.java demonstrates how to set and get cross-region replication rules.]
// snippet-service:[s3]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[PUT Bucket replication]
// snippet-keyword:[GET Bucket replication]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-28]
// snippet-sourceauthor:[AWS]
// snippet-start:[s3.java.cross_region_replication.complete]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.CreateRoleRequest;
import com.amazonaws.services.identitymanagement.model.PutRolePolicyRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteMarkerReplication;
import com.amazonaws.services.s3.model.DeleteMarkerReplicationStatus;
import com.amazonaws.services.s3.model.ReplicationDestinationConfig;
import com.amazonaws.services.s3.model.ReplicationRule;
import com.amazonaws.services.s3.model.ReplicationRuleStatus;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.replication.ReplicationFilter;
import com.amazonaws.services.s3.model.replication.ReplicationFilterPredicate;
import com.amazonaws.services.s3.model.replication.ReplicationPrefixPredicate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrossRegionReplication {

    public static void main(String[] args) throws IOException {
        Regions clientRegion = Regions.DEFAULT_REGION;
        String accountId = "*** Account ID ***";
        String roleName = "*** Role name ***";
        String sourceBucketName = "*** Source bucket name ***";
        String destBucketName = "*** Destination bucket name ***";
        String prefix = "Tax/";

        String roleARN = String.format("arn:aws:iam::%s:role/%s", accountId, roleName);
        String destinationBucketARN = "arn:aws:s3:::" + destBucketName;

        AmazonS3 s3Client = AmazonS3Client.builder()
            .withCredentials(new ProfileCredentialsProvider())
            .withRegion(clientRegion)
            .build();

        createBucket(s3Client, clientRegion, sourceBucketName);
        createBucket(s3Client, clientRegion, destBucketName);
        assignRole(roleName, clientRegion, sourceBucketName, destBucketName);


        try {


            // Create the replication rule.
            List<ReplicationFilterPredicate> andOperands = new ArrayList<ReplicationFilterPredicate>();
            andOperands.add(new ReplicationPrefixPredicate(prefix));


            Map<String, ReplicationRule> replicationRules = new HashMap<String, ReplicationRule>();
            replicationRules.put("ReplicationRule1",
                new ReplicationRule()
                    .withPriority(0)
                    .withStatus(ReplicationRuleStatus.Enabled)
                    .withDeleteMarkerReplication(new DeleteMarkerReplication().withStatus(DeleteMarkerReplicationStatus.DISABLED))
                    .withFilter(new ReplicationFilter().withPredicate(new ReplicationPrefixPredicate(prefix)))
                    .withDestinationConfig(new ReplicationDestinationConfig()
                        .withBucketARN(destinationBucketARN)
                        .withStorageClass(StorageClass.Standard)));

            // Save the replication rule to the source bucket.
            s3Client.setBucketReplicationConfiguration(sourceBucketName,
                new BucketReplicationConfiguration()
                    .withRoleARN(roleARN)
                    .withRules(replicationRules));

            // Retrieve the replication configuration and verify that the configuration
            // matches the rule we just set.
            BucketReplicationConfiguration replicationConfig = s3Client.getBucketReplicationConfiguration(sourceBucketName);
            ReplicationRule rule = replicationConfig.getRule("ReplicationRule1");
            System.out.println("Retrieved destination bucket ARN: " + rule.getDestinationConfig().getBucketARN());
            System.out.println("Retrieved priority: " + rule.getPriority());
            System.out.println("Retrieved source-bucket replication rule status: " + rule.getStatus());
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    private static void createBucket(AmazonS3 s3Client, Regions region, String bucketName) {
        CreateBucketRequest request = new CreateBucketRequest(bucketName, region.getName());
        s3Client.createBucket(request);
        BucketVersioningConfiguration configuration = new BucketVersioningConfiguration().withStatus(BucketVersioningConfiguration.ENABLED);

        SetBucketVersioningConfigurationRequest enableVersioningRequest = new SetBucketVersioningConfigurationRequest(bucketName, configuration);
        s3Client.setBucketVersioningConfiguration(enableVersioningRequest);


    }

    private static void assignRole(String roleName, Regions region, String sourceBucket, String destinationBucket) {
        AmazonIdentityManagement iamClient = AmazonIdentityManagementClientBuilder.standard()
            .withRegion(region)
            .withCredentials(new ProfileCredentialsProvider())
            .build();
        StringBuilder trustPolicy = new StringBuilder();
        trustPolicy.append("{\\r\\n   ");
        trustPolicy.append("\\\"Version\\\":\\\"2012-10-17\\\",\\r\\n   ");
        trustPolicy.append("\\\"Statement\\\":[\\r\\n      {\\r\\n         ");
        trustPolicy.append("\\\"Effect\\\":\\\"Allow\\\",\\r\\n         \\\"Principal\\\":{\\r\\n            ");
        trustPolicy.append("\\\"Service\\\":\\\"s3.amazonaws.com\\\"\\r\\n         },\\r\\n         ");
        trustPolicy.append("\\\"Action\\\":\\\"sts:AssumeRole\\\"\\r\\n      }\\r\\n   ]\\r\\n}");

        CreateRoleRequest createRoleRequest = new CreateRoleRequest()
            .withRoleName(roleName)
            .withAssumeRolePolicyDocument(trustPolicy.toString());

        iamClient.createRole(createRoleRequest);

        StringBuilder permissionPolicy = new StringBuilder();
        permissionPolicy.append("{\\r\\n   \\\"Version\\\":\\\"2012-10-17\\\",\\r\\n   \\\"Statement\\\":[\\r\\n      {\\r\\n         ");
        permissionPolicy.append("\\\"Effect\\\":\\\"Allow\\\",\\r\\n         \\\"Action\\\":[\\r\\n             ");
        permissionPolicy.append("\\\"s3:GetObjectVersionForReplication\\\",\\r\\n            ");
        permissionPolicy.append("\\\"s3:GetObjectVersionAcl\\\"\\r\\n         ],\\r\\n         \\\"Resource\\\":[\\r\\n            ");
        permissionPolicy.append("\\\"arn:aws:s3:::");
        permissionPolicy.append(sourceBucket);
        permissionPolicy.append("/*\\\"\\r\\n         ]\\r\\n      },\\r\\n      {\\r\\n         ");
        permissionPolicy.append("\\\"Effect\\\":\\\"Allow\\\",\\r\\n         \\\"Action\\\":[\\r\\n            ");
        permissionPolicy.append("\\\"s3:ListBucket\\\",\\r\\n            \\\"s3:GetReplicationConfiguration\\\"\\r\\n         ");
        permissionPolicy.append("],\\r\\n         \\\"Resource\\\":[\\r\\n            \\\"arn:aws:s3:::");
        permissionPolicy.append(sourceBucket);
        permissionPolicy.append("\\r\\n         ");
        permissionPolicy.append("]\\r\\n      },\\r\\n      {\\r\\n         \\\"Effect\\\":\\\"Allow\\\",\\r\\n         ");
        permissionPolicy.append("\\\"Action\\\":[\\r\\n            \\\"s3:ReplicateObject\\\",\\r\\n            ");
        permissionPolicy.append("\\\"s3:ReplicateDelete\\\",\\r\\n            \\\"s3:ReplicateTags\\\",\\r\\n            ");
        permissionPolicy.append("\\\"s3:GetObjectVersionTagging\\\"\\r\\n\\r\\n         ],\\r\\n         ");
        permissionPolicy.append("\\\"Resource\\\":\\\"arn:aws:s3:::");
        permissionPolicy.append(destinationBucket);
        permissionPolicy.append("/*\\\"\\r\\n      }\\r\\n   ]\\r\\n}");

        PutRolePolicyRequest putRolePolicyRequest = new PutRolePolicyRequest()
            .withRoleName(roleName)
            .withPolicyDocument(permissionPolicy.toString())
            .withPolicyName("crrRolePolicy");

        iamClient.putRolePolicy(putRolePolicyRequest);


    }
}

// snippet-end:[s3.java.cross_region_replication.complete]