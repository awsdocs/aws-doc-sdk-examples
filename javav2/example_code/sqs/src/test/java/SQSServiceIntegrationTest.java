import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.io.*;
import java.util.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQSServiceIntegrationTest {

    private static SqsClient sqsClient;

    private static String queueName ="";
    private static String message ="";
    private static String dlqueueName ="";

    @BeforeAll
    public static void setUp() throws IOException {

        sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try (InputStream input = SQSServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            queueName = prop.getProperty("QueueName");
            message = prop.getProperty("Message");
            dlqueueName=prop.getProperty("DLQueueName");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSCWService_thenNotNull() {
        assertNotNull(sqsClient);
        System.out.println("Running SQS Test 1");
    }


    @Test
    @Order(2)
    public void CreateSQSQueue() {

        try {
            CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            CreateQueueResponse createResult = sqsClient.createQueue(request);

            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

            System.out.println("Queue URL is "+queueUrl);

        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Test 2 Passed");
    }

    @Test
    @Order(3)
    public void SendMessage() {

        try {

            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(message)
                    .delaySeconds(5)
                    .build();

            sqsClient.sendMessage(sendMsgRequest);

        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Test 3 Passed");
    }

    @Test
    @Order(4)
    public void SendBatchMessages() {

         try {
        // Send multiple messages to the queue

        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

        SendMessageBatchRequest sendBatchRequest = SendMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(
                        SendMessageBatchRequestEntry.builder()
                                .messageBody("Hello from message 1")
                                .id("msg_1")
                                .build()
                        ,
                        SendMessageBatchRequestEntry.builder()
                                .messageBody("Hello from message 2")
                                .id("msg_2")
                                .build())
                .build();
        sqsClient.sendMessageBatch(sendBatchRequest);

        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Test 4 Passed");
    }

    @Test
    @Order(5)
    public void GetMessage() {

        try {
            // receive messages from the queue
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
               .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            // print out the messages
            for (Message m : messages) {
                System.out.println("\n" +m.body());
        }
        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Test 5 Passed");

    }

    @Test
    @Order(6)
    public void DeleteMessages() {

        try {

            // receive messages from the queue
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            for (Message message : messages) {

                DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();

                sqsClient.deleteMessage(deleteMessageRequest);
            }
        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Test 6 Passed");

    }

    @Test
    @Order(7)
    public void LongPolling()
    {
       try {
           // Enable long polling when creating a queue
           HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
           attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");

           CreateQueueRequest createRequest = CreateQueueRequest.builder()
                   .queueName(queueName)
                   .attributes(attributes)
                   .build();


           GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

            // Enable long polling on an existing queue
            SetQueueAttributesRequest setAttrsRequest = SetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributes(attributes)
                .build();

        sqsClient.setQueueAttributes(setAttrsRequest);

        // Enable long polling on a message receipt
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .build();

        sqsClient.receiveMessage(receiveRequest);

       } catch (QueueNameExistsException e) {
           e.printStackTrace();
           System.exit(1);
       }
        System.out.println("Test 7 Passed");

    }

    @Test
    @Order(8)
    public void DeadLetterQueues() {

        try {
            CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName).build();

            CreateQueueRequest dlrequest = CreateQueueRequest.builder()
                    .queueName(dlqueueName).build();

            sqsClient.createQueue(dlrequest);

            GetQueueUrlRequest getRequest = GetQueueUrlRequest.builder()
                    .queueName(dlqueueName)
                    .build();

            // Get dead-letter queue ARN
            String dlQueueUrl = sqsClient.getQueueUrl(getRequest)
                    .queueUrl();

            GetQueueAttributesResponse queueAttrs = sqsClient.getQueueAttributes(
                    GetQueueAttributesRequest.builder()
                            .queueUrl(dlQueueUrl)
                            .attributeNames(QueueAttributeName.QUEUE_ARN).build());

            String dlQueueArn = queueAttrs.attributes().get(QueueAttributeName.QUEUE_ARN);

            // Set dead letter queue with redrive policy on source queue.
            GetQueueUrlRequest getRequestSource = GetQueueUrlRequest.builder()
                    .queueName(dlqueueName)
                    .build();

            String srcQueueUrl = sqsClient.getQueueUrl(getRequestSource)
                    .queueUrl();

            HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
            attributes.put(QueueAttributeName.REDRIVE_POLICY, "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\""
                    + dlQueueArn + "\"}");

            SetQueueAttributesRequest setAttrRequest = SetQueueAttributesRequest.builder()
                    .queueUrl(srcQueueUrl)
                    .attributes(attributes)
                    .build();

            SetQueueAttributesResponse setAttrResponse = sqsClient.setQueueAttributes(setAttrRequest);

        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Test 8 Passed");
    }

    @Test
    @Order(9)
    public void DeleteQueue() {

        try {

            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();


            DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();

        sqsClient.deleteQueue(deleteQueueRequest);


        //Delete the DL Queue
        getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(dlqueueName)
                    .build();

       queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();


       deleteQueueRequest = DeleteQueueRequest.builder()
                    .queueUrl(queueUrl)
                    .build();

        sqsClient.deleteQueue(deleteQueueRequest);

        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Test 9 Passed");
    }

}
