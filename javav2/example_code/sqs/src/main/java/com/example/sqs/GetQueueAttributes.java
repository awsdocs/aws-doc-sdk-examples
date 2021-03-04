//snippet-sourcedescription:[GetQueueAttributes.java demonstrates how to retrieve Amazon Simple Queue Service (Amazon SQS) queue attributes.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[01/12/2021]
//snippet-sourceauthor:[scmacdon-aws]

package com.example.sqs;

// snippet-start:[sqs.java2.get_attributes.import]
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

public class GetQueueAttributes {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage: GetQueueAttributes <queueName>\n\n" +
                "Where:\n" +
                "  queueName - the name of the queue.\n\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String queueName = args[0];
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        getAttributes(sqsClient, queueName);
        sqsClient.close();
    }

    // snippet-start:[sqs.java2.get_attributes.main]
    public static void getAttributes(SqsClient sqsClient, String queueName) {

        try {
        GetQueueUrlResponse getQueueUrlResponse =
                sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());

        String queueUrl = getQueueUrlResponse.queueUrl();


        // Specify the attributes to retrieve.
        List<QueueAttributeName> atts = new ArrayList();
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