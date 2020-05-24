// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetObjectTags.java demonstrates how to read tags that belong to an object located in an Amazon S3 bucket]
// snippet-service:[Amazon S3]
// snippet-keyword:[SDK for Java 2.0]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/6/2020]
//snippet-sourceauthor:[scmacdon-aws]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.example.s3;

// snippet-start:[s3.java2.getobjecttags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;
import java.util.Iterator;
import java.util.List;
// snippet-end:[s3.java2.getobjecttags.import]

public class GetObjectTags {

    public static void main(String[] args) {

         if (args.length < 2) {
              System.out.println("Please specify a bucket name and key name");
              System.exit(1);
          }

        String bucketName = args[0];
        String keyName = args[1];

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        ListTags(s3,bucketName,keyName );
    }

    // snippet-start:[s3.java2.getobjecttags.main]
    public static void ListTags (S3Client s3, String bucketName, String keyName ) {

         try {
           // create a GetObjectTaggingRequest instance
            GetObjectTaggingRequest getTaggingRequest = GetObjectTaggingRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            // get the tags for this AWS S3 object
            GetObjectTaggingResponse tags = s3.getObjectTagging(getTaggingRequest);
            List<Tag> tagSet= tags.tagSet();

            // write out the tags
            Iterator<Tag> tagIterator = tagSet.iterator();
            while(tagIterator.hasNext()) {

                Tag tag = (Tag)tagIterator.next();
                System.out.println(tag.key());
                System.out.println(tag.value());
            }
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[s3.java2.getobjecttags.main]
    }
}
