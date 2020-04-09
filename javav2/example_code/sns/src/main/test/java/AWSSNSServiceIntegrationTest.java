import com.example.sns.*;
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

        try {
            topicArn = CreateTopic.createSNSTopic(snsClient, topicName);
        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void ListTopics() {

       try {
               ListTopics.listSNSTopics(snsClient);
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

            SetTopicAttributes.setTopAttr(snsClient, attributeName, topicArn, attributeValue );
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void GetTopicAttributes() {

        try {
            GetTopicAttributes.getSNSTopicAttributes(snsClient, topicArn);
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void SubscribeEmail() {

        try {
            SubscribeEmail.subEmail(snsClient, topicArn, email);
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

            subArn = SubscribeLambda.subLambda(snsClient, topicArn, lambdaarn);
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

            Unsubscribe.unSub(snsClient, subArn);
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
            PublishTopic.pubTopic(snsClient, message, topicArn);
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
            SubscribeTextSMS.subTextSNS(snsClient, topicArn, phone);
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
            PublishTextSMS.pubTextSMS(snsClient, message, phone);
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
            ListSubscriptions.listSNSSubscriptions(snsClient);
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
            DeleteTopic.deleteSNSTopic(snsClient, topicArn);
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 13 passed");
    }
}
