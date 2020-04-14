//snippet-sourcedescription:[SetAcl.java demonstrates how to set a new access control list (ACL) to an Amazon S3 bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/6/2020]
//snippet-sourceauthor:[scmacdon-aws]
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
package com.example.s3;

// snippet-start:[s3.java2.set_acl.import]
import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.services.s3.model.Grantee;
import software.amazon.awssdk.services.s3.model.Permission;
import software.amazon.awssdk.services.s3.model.Grant;
import software.amazon.awssdk.services.s3.model.AccessControlPolicy;
import software.amazon.awssdk.services.s3.model.Type;
import software.amazon.awssdk.services.s3.model.PutBucketAclRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
// snippet-end:[s3.java2.set_acl.import]
/**
 * Add an ACL to an existing S3 bucket object.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */

public class SetAcl {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "  SetAcl <bucket> <object> < owner id> <permission>\n\n" +
                "Where:\n" +
                "  bucket     - the bucket to grant permissions on\n" +
                "  object     - the object grant permissions on\n" +
                "  owner id   - The ID of the owner of this bucket (you can get this value from the AWS Console)\n" +
                "  permission - The permission(s) to set. Can be one of:\n" +
                "               FULL_CONTROL, READ, WRITE, READ_ACP, WRITE_ACP\n\n" +
                "Examples:\n" +
                "   SetAcl testbucket testobject <uda0cb5d2c39f310136ead6278...> WRITE\n\n";

        if (args.length < 3) {
             System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String objectKey = args[1];
        String id = args[2];
        String access = args[3];

        System.out.format("Setting %s access for %s\n", access, id);
        System.out.println("for object: " + objectKey);
        System.out.println(" in bucket: " + bucketName);

        //Create the S3Client object
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();

        SetBucketAcl(s3, bucketName, objectKey, id,access );
        System.out.println("Done!");
    }

    // snippet-start:[s3.java2.set_acl.main]
    public static void SetBucketAcl(S3Client s3,  String bucketName, String objectKey, String id,String access  ) {

       try {
            // set access for the grantee  in acl
            Grantee grantee = Grantee.builder().emailAddress(id).build();
            Permission permission = Permission.valueOf(access);

            Grant ownerGrant = Grant.builder()
                    .grantee(builder -> {
                        builder.id(id)
                                .type(Type.CANONICAL_USER);
                    })
                    .permission(Permission.FULL_CONTROL)
                    .build();

            List<Grant> grantList2 = new ArrayList<>();
            grantList2.add(ownerGrant);

            //put the new acl
            AccessControlPolicy acl = AccessControlPolicy.builder()
                    .owner(builder -> builder.id(id))
                    .grants(grantList2)
                    .build();
            //put the new acl
            PutBucketAclRequest putAclReq = PutBucketAclRequest.builder()
                    .bucket(bucketName)
                    .accessControlPolicy(acl)
                    .build();

            s3.putBucketAcl(putAclReq);

        } catch (S3Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
// snippet-end:[s3.java2.set_acl.main]
