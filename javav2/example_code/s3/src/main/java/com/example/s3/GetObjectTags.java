/**
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetObjectTags.java demonstrates how to read tags that belong to an object located in an S3 bucket]
// snippet-service:[S3]
// snippet-keyword:[SDK for Java 2.0]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-09]
// snippet-sourceauthor:[AWS-scmacdon]

// snippet-start:[s3.java2.getobjecttags.complete]

package com.example.s3;

// snippet-start:[s3.java2.getobjecttags.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Iterator;
import java.util.List;
// snippet-end:[s3.java2.getobjecttags.import]


public class GetObjectTags {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please specify a bucket name and key name");
            System.exit(1);
        }

        // snippet-start:[s3.java2.getobjecttags.main]
        String bucketName = args[0];
        String keyName = args[1];

        try {
            Region region = Region.US_WEST_2;
            S3Client s3 = S3Client.builder().region(region).build();

            // create a GetObjectTaggingRequest instance
            GetObjectTaggingRequest  getTaggingRequest = GetObjectTaggingRequest
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
        }
        catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[s3.java2.getobjecttags.main]
    }
}

// snippet-end:[s3.java2.getobjecttags.complete]
