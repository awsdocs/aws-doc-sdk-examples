 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
package aws.example.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Grant;
import java.util.List;

/**
* Add a bucket policy to an existing S3 bucket.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
*/
public class GetAcl
{
    public static void getBucketAcl(String bucket_name)
    {
        System.out.println("Retrieving ACL for bucket: " + bucket_name);

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            AccessControlList acl = s3.getBucketAcl(bucket_name);
            List<Grant> grants = acl.getGrantsAsList();
            for (Grant grant : grants) {
                System.out.format("  %s: %s\n", grant.getGrantee().getIdentifier(),
                        grant.getPermission().toString());
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public static void getObjectAcl(String bucket_name, String object_key)
    {
        System.out.println("Retrieving ACL for object: " + object_key);
        System.out.println("                in bucket: " + bucket_name);

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            AccessControlList acl = s3.getObjectAcl(bucket_name, object_key);
            List<Grant> grants = acl.getGrantsAsList();
            for (Grant grant : grants) {
                System.out.format("  %s: %s\n", grant.getGrantee().getIdentifier(),
                        grant.getPermission().toString());
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "  GetAcl <bucket> [object]\n\n" +
            "Where:\n" +
            "  bucket - the bucket to get the access control list (ACL) for\n" +
            "  object - (optional) the object to get the ACL for.\n" +
            "           If object is specified, the retrieved ACL will be\n" +
            "           for the object, not the bucket.\n\n" +
            "Examples:\n" +
            "    GetAcl testbucket\n" +
            "    GetAcl testbucket testobject\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];
        String object_key = (args.length > 1) ? args[1] : null;

        if (object_key != null) {
            getObjectAcl(bucket_name, object_key);
        } else {
            getBucketAcl(bucket_name);
        }

        System.out.println("Done!");
    }
}

