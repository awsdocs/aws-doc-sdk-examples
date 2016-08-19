/*
   Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package aws.example.s3;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;

/**
 * List objects within an Amazon S3 bucket.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class ListObjects
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of a bucket to list!\n" +
            "\n" +
            "Ex: ListObjects <bucket-name>\n";

        if (args.length < 1)
        {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucket_name = args[0];

        System.out.format("Objects in S3 bucket %s:\n", bucket_name);
        final AmazonS3 s3 = new AmazonS3Client();
        ObjectListing ol = s3.listObjects(bucket_name);
        List<S3ObjectSummary> objects = ol.getObjectSummaries();
        for (S3ObjectSummary os: objects)
        {
            System.out.println("* " + os.getKey());
        }
    }
}
