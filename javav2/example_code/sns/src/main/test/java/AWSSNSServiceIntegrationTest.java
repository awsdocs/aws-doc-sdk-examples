import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSNSServiceIntegrationTest {

    private static  SnsClient snsClient;
    private static String topicName = "";
    private static String topicArn = ""; //This value is dynamically set
    private static String subArn = "";
    private static String attributeName= "";
    private static String attributeValue = "";
    private static String  email="";
    private static String lambdaarn="";
    private static String phone="";
    private static String message="";


    @BeforeAll
    public static void setUp() throws IOException {

        snsClient = SnsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try (InputStream input = AWSSNSServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

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
    public void CreateTopic() {

        CreateTopicResponse result = null;

        try {

            CreateTopicRequest request = CreateTopicRequest.builder()
                    .name(topicName)
                    .build();

            result = snsClient.createTopic(request);

            //Set the new topic ARN value - its used in later tests
            topicArn = result.topicArn();

        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n\nTest Passed - Status was " + result.sdkHttpResponse().statusCode() + "\n\nCreated topic " + topicName + "with Arn: " + result.topicArn());
    }

    @Test
    @Order(3)
    public void ListTopics() {

        System.out.println("Running SNS Test 3");
        try {
            ListTopicsRequest request = ListTopicsRequest.builder()
                    .build();

            ListTopicsResponse result = snsClient.listTopics(request);
            System.out.println("Status was " + result.sdkHttpResponse().statusCode() + "\n\nTopics\n\n" + result.topics());
        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void SetTopicAttributes() {

        try {

            SetTopicAttributesRequest request = SetTopicAttributesRequest.builder()
                    .attributeName(attributeName)
                    .attributeValue(attributeValue)
                    .topicArn(topicArn)
                    .build();

            SetTopicAttributesResponse result = snsClient.setTopicAttributes(request);

            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode() + "\n\nTopic " + request.topicArn()
                    + " updated " + request.attributeName() + " to " + request.attributeValue());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
         System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void GetTopicAttributes() {

        System.out.println("Running SNS Test 5");
        try {
            GetTopicAttributesRequest request = GetTopicAttributesRequest.builder()
                    .topicArn(topicArn)
                    .build();

            GetTopicAttributesResponse result = snsClient.getTopicAttributes(request);
            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode() + "\n\nAttributes: \n\n" + result.attributes());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void SubscribeEmail() {

        System.out.println("Running SNS Test 6");
        try {
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("email")
                    .endpoint(email)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);
            System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void SubscribeLambda() {

        try {

            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("lambda")
                    .endpoint(lambdaarn)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);
            subArn = result.subscriptionArn();
            System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void Unsubscribe() {

        try {

            UnsubscribeRequest request = UnsubscribeRequest.builder()
                    .subscriptionArn(subArn)
                    .build();

            UnsubscribeResponse result = snsClient.unsubscribe(request);

            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode()
                    + "\n\nSubscription was removed for " + request.subscriptionArn());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void PublishTopic() {

      try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .topicArn(topicArn)
                    .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 9 passed");
    }


    @Test
    @Order(10)
    public void SubscribeTextSMS() {

        try {
            SubscribeRequest request = SubscribeRequest.builder()
                    .protocol("sms")
                    .endpoint(phone)
                    .returnSubscriptionArn(true)
                    .topicArn(topicArn)
                    .build();

            SubscribeResponse result = snsClient.subscribe(request);

            System.out.println("Subscription ARN: " + result.subscriptionArn() + "\n\n Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void PublishTextSMS() {

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phone)
                    .build();

            PublishResponse result = snsClient.publish(request);

            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void ListSubscriptions() {

        try {

            ListSubscriptionsRequest request = ListSubscriptionsRequest.builder()
                    .build();

            ListSubscriptionsResponse result = snsClient.listSubscriptions(request);
            System.out.println(result.subscriptions());

        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 12 passed");

    }

    @Test
    @Order(13)
    public void DeleteTopic() {

        try {

            DeleteTopicRequest request = DeleteTopicRequest.builder()
                    .topicArn(topicArn)
                    .build();

            DeleteTopicResponse result = snsClient.deleteTopic(request);
            System.out.println("\n\nStatus was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 13 passed");
    }
}
