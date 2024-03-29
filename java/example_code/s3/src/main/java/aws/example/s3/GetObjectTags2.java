// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.java.getobjecttags.complete]

package aws.example.s3;

// snippet-start:[s3.java.getobjecttags.import]
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingResult;
import com.amazonaws.services.s3.model.Tag;
import java.util.Iterator;
import java.util.List;
// snippet-end:[s3.java.getobjecttags.import]

public class GetObjectTags2 {

    public static void main(String[] args) {

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
        // snippet-end:[s3.java.getobjecttags.main]
    }
}
// snippet-end:[s3.java.getobjecttags.complete]
