/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.sns.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSNSTest {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static  SnsClient snsClient;
    private static String topicName = "";
    private static String topicArn = ""; //This value is dynamically set
    private static String subArn = ""; //This value is dynamically set
    private static String attributeName= "";
    private static String attributeValue = "";
    private static String  email="";
    private static String lambdaarn="";
    private static String phone="";
    private static String message="";

    @BeforeAll
    public static void setUp() throws IOException {

        snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AWSSNSTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            //load a properties file from class path, inside static method
            prop.load(input);
            topicName = prop.getProperty("topicName");
            attributeName= prop.getProperty("attributeName");
            attributeValue = prop.getProperty("attributeValue");
            email= prop.getProperty("email");
            lambdaarn = prop.getProperty("lambdaarn");
            phone = prop.getProperty("phone");
            message = prop.getProperty("message");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(snsClient);
        System.out.println("Running SNS Test 1");
    }

    @Test
    @Order(2)
    public void createTopicTest() {

        topicArn = CreateTopic.createSNSTopic(snsClient, topicName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void listTopicsTest() {

       ListTopics.listSNSTopics(snsClient);
       System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void setTopicAttributesTest() {

      SetTopicAttributes.setTopAttr(snsClient, attributeName, topicArn, attributeValue );
      System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void getTopicAttributesTest() {

       GetTopicAttributes.getSNSTopicAttributes(snsClient, topicArn);
       System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void subscribeEmailTest() {

     SubscribeEmail.subEmail(snsClient, topicArn, email);
     System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void subscribeLambdaTest() {

     subArn = SubscribeLambda.subLambda(snsClient, topicArn, lambdaarn);
     System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void useMessageFilterPolicyTest() {

        UseMessageFilterPolicy.usePolicy(snsClient, subArn);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void addTagsTest() {
        AddTags.addTopicTags(snsClient,topicArn );
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void listTagsTest() {
        ListTags.listTopicTags(snsClient,topicArn);
        System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void deleteTagTest() {

        DeleteTag.removeTag(snsClient,topicArn, "Environment");
        System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void unsubscribeTest() {

        Unsubscribe.unSub(snsClient, subArn);
        System.out.println("Test 12 passed");
    }

    @Test
    @Order(13)
    public void publishTopicTest() {
        PublishTopic.pubTopic(snsClient, message, topicArn);
        System.out.println("Test 13 passed");
    }

    @Test
    @Order(14)
    public void subscribeTextSMSTest() {
       SubscribeTextSMS.subTextSNS(snsClient, topicArn, phone);
       System.out.println("Test 14 passed");
    }

    @Test
    @Order(15)
    public void publishTextSMSTest() {
        PublishTextSMS.pubTextSMS(snsClient, message, phone);
        System.out.println("Test 15 passed");
    }

    @Test
    @Order(16)
    public void listSubscriptionsTest() {
        ListSubscriptions.listSNSSubscriptions(snsClient);
        System.out.println("Test 16 passed");
    }

    @Test
    @Order(17)
    public void DeleteTopic() {
        DeleteTopic.deleteSNSTopic(snsClient, topicArn);
        System.out.println("Test 17 passed");
    }

    @Test
    @Order(18)
    public void TestWorkflow() throws InterruptedException {
        SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        Scanner in = new Scanner(System.in);
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
        System.out.println("In this workflow, you will create an SNS topic and subscribe 2 SQS queues to the topic.\n" +
            "You can select from several options for configuring the topic and the subscriptions for the 2 queues.\n" +
            "You can then post to the topic and see the results in the queues.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("SNS topics can be configured as FIFO (First-In-First-Out).\n" +
            "FIFO topics deliver messages in order and support deduplication and message filtering.\n" +
            "Would you like to work with FIFO topics? (y/n)");
        useFIFO = "y";
        selectFIFO = true;
        System.out.println("You have selected to use FIFO");
        System.out.println(" Because you have chosen a FIFO topic, deduplication is supported.\n" +
            "        Deduplication IDs are either set in the message or automatically generated from content using a hash function.\n" +
            "        If a message is successfully published to an SNS FIFO topic, any message published and determined to have the same deduplication ID,\n" +
            "        within the five-minute deduplication interval, is accepted but not delivered.\n" +
            "        For more information about deduplication, see https://docs.aws.amazon.com/sns/latest/dg/fifo-message-dedup.html.");

        System.out.println("Would you like to use content-based deduplication instead of entering a deduplication ID?");
        duplication = "n";
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Create a  topic.");
        System.out.println("Enter a name for your SNS topic.");
        topicName = "topic1000";
        System.out.println("Because you have selected a FIFO topic, '.fifo' must be appended to the topic name.");
        topicName = topicName+".fifo";
        System.out.println("The name of the topic is "+topicName);
        topicArn = SNSWorkflow.createFIFO(snsClient, topicName);
        System.out.println("The ARN of the FIFO topic is "+topicArn);

        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. Create an SQS queue.");
        System.out.println("Enter a name for your SQS queue.");
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
        if (selectFIFO) {
            System.out.println("If you add a filter to this subscription, then only the filtered messages will be received in the queue.\n" +
                "For information about message filtering, see https://docs.aws.amazon.com/sns/latest/dg/sns-message-filtering.html\n" +
                "For this example, you can filter messages by a \"tone\" attribute.");
            System.out.println("Would you like to filter messages for " + sqsQueueName + "'s subscription to the topic " + topicName + "?  (y/n)");
            String filterAns = "n";
            if (filterAns.compareTo("y") == 0) {
                boolean moreAns = false;
                System.out.println("You can filter messages by one or more of the following \"tone\" attributes.");
                System.out.println("1. cheerful");
                System.out.println("2. funny");
                System.out.println("3. serious");
                System.out.println("4. sincere");
                while (!moreAns) {
                    System.out.println("Select a number or choose 0 to end.");
                    String ans = in.nextLine();
                    switch (ans) {
                        case "1":
                            filterList.add("cheerful");
                            break;
                        case "2":
                            filterList.add("funny");
                            break;
                        case "3":
                            filterList.add("serious");
                            break;
                        case "4":
                            filterList.add("sincere");
                            break;
                        default:
                            moreAns = true;
                            break;
                    }
                }
            }
        }
        subscriptionArn = SNSWorkflow.subQueue(snsClient, topicArn, sqsQueueArn, filterList);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Publish a message to the topic.");
        if (selectFIFO) {
            System.out.println("Would you like to add an attribute to this message?  (y/n)");
            String msgAns = "n";
            if (msgAns.compareTo("y") == 0) {
                System.out.println("You can filter messages by one or more of the following \"tone\" attributes.");
                System.out.println("1. cheerful");
                System.out.println("2. funny");
                System.out.println("3. serious");
                System.out.println("4. sincere");
                System.out.println("Select a number or choose 0 to end.");
                String ans = in.nextLine();
                switch (ans) {
                    case "1":
                        msgAttValue = "cheerful";
                        break;
                    case "2":
                        msgAttValue = "funny";
                        break;
                    case "3":
                        msgAttValue = "serious";
                        break;
                    default:
                        msgAttValue = "sincere";
                        break;
                }

                System.out.println("Selected value is " + msgAttValue);
            }
            System.out.println("Enter a message.");
            message = "Hello there";
            SNSWorkflow.pubMessageFIFO(snsClient, message, topicArn, msgAttValue, duplication);

        } else {
            System.out.println("Enter a message.");
            message =  "Hello there";
            SNSWorkflow.pubMessage(snsClient, message, topicArn) ;
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Display the message. Enter any key to continue.");
        TimeUnit.SECONDS.sleep(2);
        messageList = SNSWorkflow.receiveMessages(sqsClient, sqsQueueUrl, msgAttValue);
        for (Message mes :messageList) {
            System.out.println("Message Id: " +mes.messageId());
            System.out.println("Full Message: " +mes.body());
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Delete the received message. Enter any key to continue.");
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.deleteMessages(sqsClient, sqsQueueUrl, messageList);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("10. Unsubscribe from the topic and delete the queue.");
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.unSub(snsClient, subscriptionArn);
        SNSWorkflow.deleteSQSQueue(sqsClient, sqsQueueName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("11. Delete the topic.");
        TimeUnit.SECONDS.sleep(2);
        SNSWorkflow.deleteSNSTopic(snsClient, topicArn);

        System.out.println(DASHES);
        System.out.println("The SNS/SQS Workflow has completed successfully.");
        System.out.println(DASHES);
    }

}