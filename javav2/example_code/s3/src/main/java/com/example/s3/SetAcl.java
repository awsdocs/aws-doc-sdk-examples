/*
Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
package com.example.s3;
import java.util.List;

import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Grantee;
import software.amazon.awssdk.services.s3.model.AccessControlPolicy;
import software.amazon.awssdk.services.s3.model.GetBucketAclRequest;
import software.amazon.awssdk.services.s3.model.GetBucketAclResponse;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAclResponse;
import software.amazon.awssdk.services.s3.model.Grant;
import software.amazon.awssdk.services.s3.model.Permission;
import software.amazon.awssdk.services.s3.model.PutBucketAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.core.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
* Add a bucket policy to an existing S3 bucket.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
*/
public class SetAcl
{
    public static void setBucketAcl(String bucket_name, String email, String access)
    {
        System.out.format("Setting %s access for %s\n", access, email);
        System.out.println("on bucket: " + bucket_name);

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();
        
        GetBucketAclRequest bucketAclReq = GetBucketAclRequest.builder()
        		.bucket(bucket_name)
        		.build();
        
        try {
            // get the current ACL
        	GetBucketAclResponse getAclRes = s3.getBucketAcl(bucketAclReq);
            // add new grantee to acl
            Grantee grantee = Grantee.builder().emailAddress(email).build();
            Permission permission = Permission.valueOf(access);
            List<Grant> grants = getAclRes.grants();
            Grant newGrantee = Grant.builder()
            		.grantee(grantee)
            		.permission(permission)
            		.build();
            grants.add(newGrantee);
            AccessControlPolicy acl = AccessControlPolicy.builder()
            		.grants(grants)
            		.build();
            
            //put the new acl 
            PutBucketAclRequest putAclReq = PutBucketAclRequest.builder()
            		.bucket(bucket_name)
            		.accessControlPolicy(acl)
            		.build();
            s3.putBucketAcl(putAclReq);
        } catch (S3Exception e) {
            System.err.println(e.errorMessage());
            System.exit(1);
        }
    }

    public static void setObjectAcl(String bucket_name, String object_key, String email, String access)
    {
        System.out.format("Setting %s access for %s\n", access, email);
        System.out.println("for object: " + object_key);
        System.out.println(" in bucket: " + bucket_name);

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();
        
        try {
            // get the current ACL
        	GetObjectAclRequest objectAclReq = GetObjectAclRequest.builder()
            		.bucket(bucket_name)
            		.key(object_key)
            		.build();
        	
        	GetObjectAclResponse getAclRes = s3.getObjectAcl(objectAclReq);
            // set access for the grantee  in acl
            Grantee grantee = Grantee.builder().emailAddress(email).build();
            Permission permission = Permission.valueOf(access);
            List<Grant> grants = getAclRes.grants();
            Grant newGrantee = Grant.builder()
            		.grantee(grantee)
            		.permission(permission)
            		.build();
            grants.add(newGrantee);

            //put the new acl 
            AccessControlPolicy acl = AccessControlPolicy.builder()
            		.grants(grants)
            		.build();
            PutObjectAclRequest putAclReq = PutObjectAclRequest.builder()
            		.bucket(bucket_name)
            		.key(object_key)
            		.accessControlPolicy(acl)
            		.build();
            s3.putObjectAcl(putAclReq);
        } catch (S3Exception e) {
            System.err.println(e.errorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "  SetAcl <bucket> [object] <email> <permission>\n\n" +
            "Where:\n" +
            "  bucket     - the bucket to grant permissions on\n" +
            "  object     - (optional) the object grant permissions on\n" +
            "               If object is specified, granted permissions will be\n" +
            "               for the object, not the bucket.\n" +
            "  email      - The email of the user to set permissions for\n" +
            "  permission - The permission(s) to set. Can be one of:\n" +
            "               FullControl, Read, Write, ReadAcp, WriteAcp\n\n" +
            "Examples:\n" +
            "    SetAcl testbucket user@example.com read\n" +
            "    SetAcl testbucket testobject user@example.com write\n\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        int cur_arg = 0;

        String bucket_name = args[cur_arg++];
        String object_key = (args.length > 3) ? args[cur_arg++] : null;
        String email = args[cur_arg++];
        String access = args[cur_arg++];

        if (object_key != null) {
            setObjectAcl(bucket_name, object_key, email, access);
        } else {
            setBucketAcl(bucket_name, email, access);
        }

        System.out.println("Done!");
    }
}

