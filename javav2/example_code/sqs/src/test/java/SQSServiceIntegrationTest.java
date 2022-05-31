import com.example.sqs.DeadLetterQueues;
import com.example.sqs.LongPolling;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.io.*;
import java.util.*;
import com.example.sqs.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQSServiceIntegrationTest {

    private static SqsClient sqsClient;

    private static String queueName ="";
    private static String queueUrl ="" ; // set dynamically in the test
    private static String message ="";
    private static String dlqueueName ="";
    private static List<Message> messages = null; // set dynamically in the test

    @BeforeAll
    public static void setUp() throws IOException {

        sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
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
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(sqsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateSQSQueue() {

        queueUrl = SQSExample.createQueue(sqsClient, queueName);
        assertTrue(!queueUrl.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void SendMessage() {

        SendMessages.sendMessage(sqsClient,queueName, message);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void GetMessage() {

        messages = SQSExample.receiveMessages(sqsClient, queueUrl);
        assertNotNull(messages);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void GetQueueAttributes() {
        GetQueueAttributes.getAttributes(sqsClient, queueName);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DeleteMessages() {

        SQSExample.deleteMessages(sqsClient, queueUrl,messages );
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void LongPolling()
    {
       LongPolling.setLongPoll(sqsClient);
       System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeadLetterQueues() {

        DeadLetterQueues.setDeadLetterQueue(sqsClient);
        System.out.println("Test 8 passed");
   }

    @Test
    @Order(9)
    public void DeleteQueue() {

        DeleteQueue.deleteSQSQueue(sqsClient, queueName);
        System.out.println("Test 9 passed");
    }
}
