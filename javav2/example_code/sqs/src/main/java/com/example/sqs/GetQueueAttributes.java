//snippet-sourcedescription:[GetQueueAttributes.java demonstrates how to retrieve Amazon Simple Queue Service (Amazon SQS) queue attributes.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Queue Service]

package com.example.sqs;

// snippet-start:[sqs.java2.get_attributes.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
// snippet-end:[sqs.java2.get_attributes.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetQueueAttributes {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "   <queueName>\n\n" +
            "Where:\n" +
            "   queueName - The name of the queue.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String queueName = args[0];
        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getAttributes(sqsClient, queueName);
        sqsClient.close();
    }

    // snippet-start:[sqs.java2.get_attributes.main]
    public static void getAttributes(SqsClient sqsClient, String queueName) {

        try {
            GetQueueUrlResponse getQueueUrlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
            String queueUrl = getQueueUrlResponse.queueUrl();

            // Specify the attributes to retrieve.
            List<QueueAttributeName> atts = new ArrayList<>();
            atts.add(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES);

            GetQueueAttributesRequest attributesRequest= GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(atts)
                .build();

            GetQueueAttributesResponse response = sqsClient.getQueueAttributes(attributesRequest);
            Map<String,String> queueAtts = response.attributesAsStrings();
            for (Map.Entry<String,String> queueAtt : queueAtts.entrySet())
                System.out.println("Key = " + queueAtt.getKey() +
                        ", Value = " + queueAtt.getValue());

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[sqs.java2.get_attributes.main]
}