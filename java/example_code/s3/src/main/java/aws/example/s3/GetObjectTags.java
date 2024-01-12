// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class GetObjectTags {

    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.out.println("Please specify a bucket name and key name");
            System.exit(1);
        }

        String bucketName = args[0];
        String keyName = args[1];

        System.out.println("Retrieving Object Tags for  " + keyName);

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

        try {

            GetObjectTaggingRequest getTaggingRequest = new GetObjectTaggingRequest(bucketName, keyName);

            GetObjectTaggingResult tags = s3.getObjectTagging(getTaggingRequest);

            List<Tag> tagSet = tags.getTagSet();

            // Iterate through the list
            Iterator<Tag> tagIterator = tagSet.iterator();

            while (tagIterator.hasNext()) {

                Tag tag = (Tag) tagIterator.next();

                System.out.println(tag.getKey());
                System.out.println(tag.getValue());
            }

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}
