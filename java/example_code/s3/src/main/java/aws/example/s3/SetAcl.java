// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.Permission;

/**
 * Add a bucket policy to an existing S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class SetAcl {
    public static void setBucketAcl(String bucket_name, String email, String access) {
        System.out.format("Setting %s access for %s\n", access, email);
        System.out.println("on bucket: " + bucket_name);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            // get the current ACL
            AccessControlList acl = s3.getBucketAcl(bucket_name);
            // set access for the grantee
            EmailAddressGrantee grantee = new EmailAddressGrantee(email);
            Permission permission = Permission.valueOf(access);
            acl.grantPermission(grantee, permission);
            s3.setBucketAcl(bucket_name, acl);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public static void setObjectAcl(String bucket_name, String object_key, String email, String access) {
        System.out.format("Setting %s access for %s\n", access, email);
        System.out.println("for object: " + object_key);
        System.out.println(" in bucket: " + bucket_name);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            // get the current ACL
            AccessControlList acl = s3.getObjectAcl(bucket_name, object_key);
            // set access for the grantee
            EmailAddressGrantee grantee = new EmailAddressGrantee(email);
            Permission permission = Permission.valueOf(access);
            acl.grantPermission(grantee, permission);
            s3.setObjectAcl(bucket_name, object_key, acl);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
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
