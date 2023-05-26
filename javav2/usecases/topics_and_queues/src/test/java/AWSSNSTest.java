/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.sns.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import java.util.*;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSNSTest {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void TestWorkflowFIFO() throws InterruptedException {
        SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        String accountId = "814548047983" ;
        String duplication = "n";
        String topicName;

        String topicArn;
        String sqsQueueName;
        String sqsQueueUrl;
        String sqsQueueArn ;
        String subscriptionArn;
        boolean selectFIFO = false;
        String groupId = "group1";
        String deduplicationID = "dup100";

        String message ;
        List<Message> messageList;
        List<String> filterList = new ArrayList<>();
        String msgAttValue = "";

        System.out.println(DASHES);
        System.out.println("Welcome to messaging with topics and queues.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        selectFIFO = true;
        duplication = "n";
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Create a  topic.");
        topicName = "topic1000";
        topicName = topicName+".fifo";
        topicArn = SNSWorkflow.createFIFO(snsClient, topicName, "n");
        System.out.println("The ARN of the FIFO topic is "+topicArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create an SQS queue.");
        sqsQueueName = "queue1000";
        sqsQueueName = sqsQueueName + ".fifo";
        sqsQueueUrl = SNSWorkflow.createQueue(sqsClient, sqsQueueName, selectFIFO);
        System.out.println("The Queue URL is "+sqsQueueUrl);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Get the SQS queue ARN attribute.");
        sqsQueueArn = SNSWorkflow.getSQSQueueAttrs(sqsClient, sqsQueueUrl);
        System.out.println("The ARN of the new queue is "+sqsQueueArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Attach an IAM policy to the queue.");

        // Define the policy to use.
        String policy = "{\n" +
            "     \"Statement\": [\n" +
            "     {\n" +
            "         \"Effect\": \"Allow\",\n" +
            "                 \"Principal\": {\n" +
            "             \"Service\": \"sns.amazonaws.com\"\n" +
            "         },\n" +
            "         \"Action\": \"sqs:SendMessage\",\n" +
            "                 \"Resource\": \"arn:aws:sqs:us-east-1:"+accountId+":"+sqsQueueName+"\",\n" +
            "                 \"Condition\": {\n" +
            "             \"ArnEquals\": {\n" +
            "                 \"aws:SourceArn\": \"arn:aws:sns:us-east-1:"+accountId+":"+topicName+"\"\n" +
            "             }\n" +
            "         }\n" +
            "     }\n" +
            "     ]\n" +
            " }";

        SNSWorkflow.setQueueAttr(sqsClient, sqsQueueUrl, policy);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Subscribe to the SQS queue.");
        subscriptionArn = SNSWorkflow.subQueue(snsClient, topicArn, sqsQueueArn, filterList);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Publish a message to the topic.");
        message = "Hello there";
        SNSWorkflow.pubMessageFIFO(snsClient, message, topicArn, msgAttValue, duplication, groupId, deduplicationID);
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        messageList = SNSWorkflow.receiveMessages(sqsClient, sqsQueueUrl, msgAttValue);
        for (Message mes :messageList) {
            System.out.println("Message Id: " +mes.messageId());
            System.out.println("Full Message: " +mes.body());
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.deleteMessages(sqsClient, sqsQueueUrl, messageList);
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.unSub(snsClient, subscriptionArn);
        SNSWorkflow.deleteSQSQueue(sqsClient, sqsQueueName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.deleteSNSTopic(snsClient, topicArn);

        System.out.println(DASHES);
        System.out.println("The SNS/SQS Workflow has completed successfully.");
        System.out.println(DASHES);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void TestWorkflowNonFIFO() throws InterruptedException {
        SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        String accountId = "814548047983" ;
        String useFIFO;
        String duplication = "n";
        String topicName;

        String topicArn;
        String sqsQueueName;
        String sqsQueueUrl;
        String sqsQueueArn ;
        String subscriptionArn;
        boolean selectFIFO = false;

        String message ;
        List<Message> messageList;
        List<String> filterList = new ArrayList<>();
        String msgAttValue = "";

        System.out.println(DASHES);
        System.out.println("Welcome to messaging with topics and queues.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        duplication = "n";
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Create a topic.");
        topicName = "topic1003";
        topicArn = SNSWorkflow.createSNSTopic(snsClient, topicName);
        System.out.println("The ARN of the FIFO topic is "+topicArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create an SQS queue.");
        sqsQueueName = "queue1003";
        sqsQueueUrl = SNSWorkflow.createQueue(sqsClient, sqsQueueName, selectFIFO);
        System.out.println("The Queue URL is "+sqsQueueUrl);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Get the SQS queue ARN attribute.");
        sqsQueueArn = SNSWorkflow.getSQSQueueAttrs(sqsClient, sqsQueueUrl);
        System.out.println("The ARN of the new queue is "+sqsQueueArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Attach an IAM policy to the queue.");

        // Define the policy to use.
        String policy = "{\n" +
            "     \"Statement\": [\n" +
            "     {\n" +
            "         \"Effect\": \"Allow\",\n" +
            "                 \"Principal\": {\n" +
            "             \"Service\": \"sns.amazonaws.com\"\n" +
            "         },\n" +
            "         \"Action\": \"sqs:SendMessage\",\n" +
            "                 \"Resource\": \"arn:aws:sqs:us-east-1:"+accountId+":"+sqsQueueName+"\",\n" +
            "                 \"Condition\": {\n" +
            "             \"ArnEquals\": {\n" +
            "                 \"aws:SourceArn\": \"arn:aws:sns:us-east-1:"+accountId+":"+topicName+"\"\n" +
            "             }\n" +
            "         }\n" +
            "     }\n" +
            "     ]\n" +
            " }";

        SNSWorkflow.setQueueAttr(sqsClient, sqsQueueUrl, policy);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Subscribe to the SQS queue.");
        subscriptionArn = SNSWorkflow.subQueue(snsClient, topicArn, sqsQueueArn, filterList);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Publish a message to the topic.");
        message = "Hello there";
        SNSWorkflow.pubMessage(snsClient, message, topicArn);
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        messageList = SNSWorkflow.receiveMessages(sqsClient, sqsQueueUrl, msgAttValue);
        for (Message mes :messageList) {
            System.out.println("Message Id: " +mes.messageId());
            System.out.println("Full Message: " +mes.body());
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.deleteMessages(sqsClient, sqsQueueUrl, messageList);
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.unSub(snsClient, subscriptionArn);
        SNSWorkflow.deleteSQSQueue(sqsClient, sqsQueueName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.deleteSNSTopic(snsClient, topicArn);

        System.out.println(DASHES);
        System.out.println("The SNS/SQS workflow has completed successfully.");
        System.out.println(DASHES);
    }
}