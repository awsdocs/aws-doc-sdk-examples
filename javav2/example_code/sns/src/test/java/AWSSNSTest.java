/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.sns.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import com.google.gson.Gson;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSNSTest {
    private static  SnsClient snsClient;
    private static String topicName = "";
    private static String topicArn = "";
    private static String subArn = "";
    private static String attributeName= "";
    private static String attributeValue = "";
    private static String  email="";
    private static String lambdaarn="";
    private static String phone="";
    private static String message="";

    @BeforeAll
    public static void setUp() {

        snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        TestValues myValues = gson.fromJson(String.valueOf(getSecretValues()), TestValues.class);
        topicName = myValues.getTopicName()+randomNum;
        attributeName= myValues.getAttributeName();
        attributeValue = myValues.getAttributeValue();
        email= myValues.getEmail();
        lambdaarn = myValues.getLambdaarn();
        phone = myValues.getPhone();
        message = myValues.getMessage();

       // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AWSSNSTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
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
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createTopicTest() {
        topicArn = CreateTopic.createSNSTopic(snsClient, topicName);
        assertFalse(topicArn.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void listTopicsTest() {
        assertDoesNotThrow(() ->ListTopics.listSNSTopics(snsClient));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void setTopicAttributesTest() {
        assertDoesNotThrow(() ->SetTopicAttributes.setTopAttr(snsClient, attributeName, topicArn, attributeValue));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void getTopicAttributesTest() {
        assertDoesNotThrow(() ->GetTopicAttributes.getSNSTopicAttributes(snsClient, topicArn));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void subscribeEmailTest() {
        assertDoesNotThrow(() ->SubscribeEmail.subEmail(snsClient, topicArn, email));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void subscribeLambdaTest() {
        subArn = SubscribeLambda.subLambda(snsClient, topicArn, lambdaarn);
        assertFalse(subArn.isEmpty());
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void useMessageFilterPolicyTest() {
        assertDoesNotThrow(() ->UseMessageFilterPolicy.usePolicy(snsClient, subArn));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void addTagsTest() {
        assertDoesNotThrow(() ->AddTags.addTopicTags(snsClient,topicArn ));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void listTagsTest() {
        assertDoesNotThrow(() ->ListTags.listTopicTags(snsClient,topicArn));
        System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void deleteTagTest() {
        assertDoesNotThrow(() ->DeleteTag.removeTag(snsClient,topicArn, "Environment"));
        System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void unsubscribeTest() {
        assertDoesNotThrow(() -> Unsubscribe.unSub(snsClient, subArn));
        System.out.println("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void publishTopicTest() {
        assertDoesNotThrow(() ->PublishTopic.pubTopic(snsClient, message, topicArn));
        System.out.println("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void subscribeTextSMSTest() {
        assertDoesNotThrow(() ->SubscribeTextSMS.subTextSNS(snsClient, topicArn, phone));
        System.out.println("Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void publishTextSMSTest() {
        assertDoesNotThrow(() ->PublishTextSMS.pubTextSMS(snsClient, message, phone));
        System.out.println("Test 14 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void listSubscriptionsTest() {
        assertDoesNotThrow(() ->ListSubscriptions.listSNSSubscriptions(snsClient));
        System.out.println("Test 15 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
    public void DeleteTopic() {
        assertDoesNotThrow(() ->DeleteTopic.deleteSNSTopic(snsClient, topicArn));
        System.out.println("Test 16 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/sns";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/sns, an AWS Secrets Manager secret")
    class TestValues {
        private String topicName;
        private String attributeName;

        private String attributeValue;

        private String lambdaarn;

        private String phone;

        private String message;

       private String email;

        TestValues() {
        }


        //getter
        String getTopicName(){
            return this.topicName;
        }

        String getAttributeName(){
            return this.attributeName;
        }

        String getAttributeValue(){
            return this.attributeValue;
        }


        String getLambdaarn(){
            return this.lambdaarn;
        }

        String getPhone(){
            return this.phone;
        }

        String getMessage(){
            return this.message;
        }

        String getEmail(){
            return this.email;
        }
    }
}