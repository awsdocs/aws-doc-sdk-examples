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
// snippet-keyword:[Java]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-09]
// snippet-sourceauthor:[AWS-scmacdon]

// snippet-start:[s3.java.getobjecttags.complete]

package aws.example.s3;

// snippet-start:[s3.java.getobjecttags.import]
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
// snippet-end:[s3.java.getobjecttags.import]
public class GetObjectTags {


    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.out.println("Please specify a bucket name and key name");
            System.exit(1);
        }

        // snippet-start:[s3.java.getobjecttags.main]
        String bucketName = args[0];
        String keyName = args[1];

        System.out.println("Retrieving Object Tags for  " + keyName);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

        try {

            GetObjectTaggingRequest getTaggingRequest = new GetObjectTaggingRequest(bucketName, keyName);

            GetObjectTaggingResult tags = s3.getObjectTagging(getTaggingRequest);

            List<Tag> tagSet= tags.getTagSet();

            //Iterate through the list
            Iterator<Tag> tagIterator = tagSet.iterator();

            while(tagIterator.hasNext()) {

                Tag tag = (Tag)tagIterator.next();

                System.out.println(tag.getKey());
                System.out.println(tag.getValue());
            }

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        // snippet-end:[s3.java.getobjecttags.main]
    }
}

// snippet-end:[s3.java.getobjecttags.complete]
