//snippet-sourcedescription:[GetAcl.java demonstrates how to the access control list (ACL) for an S3 bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-06]
//snippet-sourceauthor:[scmacdon-aws]
/*
Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at
http://aws.amazon.com/apache2.0/
This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
package com.example.s3;
// snippet-start:[s3.java2.get_acl.complete]
// snippet-start:[s3.java2.get_acl.import]
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAclRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAclResponse;
import software.amazon.awssdk.services.s3.model.Grant;
import java.util.List;

// snippet-end:[s3.java2.get_acl.import]
/**
 * Get the ACL for an existing S3 bucket.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */

// snippet-start:[s3.java2.get_acl.main]
public class GetAcl {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "  GetAcl <bucket> [object]\n\n" +
                "Where:\n" +
                "  bucket - the bucket to get the access control list (ACL) for\n" +
                "  object - (optional) the object to get the ACL for.\n" +
                "           If object is specified, the retrieved ACL will be\n" +
                "           for the object, not the bucket.\n\n" +
                "Examples:\n" +
                "    GetAcl  bucket1\n" +
                "    GetAcl  bucket1 book.pdf\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String objectKey = args[1];

        System.out.println("Retrieving ACL for object: " + objectKey);
        System.out.println("                in bucket: " + bucketName);

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        GetObjectAclRequest aclReq = GetObjectAclRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try {
            GetObjectAclResponse aclRes = s3.getObjectAcl(aclReq);
            List<Grant> grants = aclRes.grants();
            for (Grant grant : grants) {
                System.out.format("  %s: %s\n", grant.grantee().id(),
                        grant.permission());
            }
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Done!");
    }
  }
// snippet-end:[s3.java2.get_acl.main]
// snippet-end:[s3.java2.get_acl.complete]
