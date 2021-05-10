package com.example.ec2;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import java.util.List;

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DescribeInstanceTags {

    public static void main(String[] args) {

        String resourceId = args[0];
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        describeEC2Tags(ec2, resourceId);
        ec2.close();
    }

    public static void describeEC2Tags(Ec2Client ec2,  String resourceId ) {

        try {

            Filter filter = Filter.builder()
                    .name("resource-id")
                    .values(resourceId)
                    .build();

            DescribeTagsResponse describeTagsResponse = ec2.describeTags(DescribeTagsRequest.builder().filters(filter).build());
            List<TagDescription> tags = describeTagsResponse.tags();
            for (TagDescription tag: tags) {
                System.out.println("Tag key is: "+tag.key());
                System.out.println("Tag value is: "+tag.value());
            }

        } catch ( Ec2Exception e) {
         System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
