// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.sns.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AWSSNSTest.class);
    private static SnsClient snsClient;
    private static String topicName = "";
    private static String topicArn = "";
    private static String subArn = "";
    private static String attributeName = "";
    private static String attributeValue = "";
    private static String email = "";
    private static String lambdaarn = "";
    private static String phone = "";
    private static String message = "";

    @BeforeAll
    public static void setUp() {

        snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        TestValues myValues = gson.fromJson(String.valueOf(getSecretValues()), TestValues.class);
        topicName = myValues.getTopicName() + randomNum;
        attributeName = myValues.getAttributeName();
        attributeValue = myValues.getAttributeValue();
        email = myValues.getEmail();
        lambdaarn = myValues.getLambdaarn();
        phone = myValues.getPhone();
        message = myValues.getMessage();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateTopicTest() {
        topicArn = CreateTopic.createSNSTopic(snsClient, topicName);
        assertFalse(topicArn.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testListTopicsTest() {
        assertDoesNotThrow(() -> ListTopics.listSNSTopics(snsClient));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testSetTopicAttributesTest() {
        assertDoesNotThrow(() -> SetTopicAttributes.setTopAttr(snsClient, attributeName, topicArn, attributeValue));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetTopicAttributesTest() {
        assertDoesNotThrow(() -> GetTopicAttributes.getSNSTopicAttributes(snsClient, topicArn));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testSubscribeEmailTest() {
        assertDoesNotThrow(() -> SubscribeEmail.subEmail(snsClient, topicArn, email));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testSubscribeLambdaTest() {
        subArn = SubscribeLambda.subLambda(snsClient, topicArn, lambdaarn);
        assertFalse(subArn.isEmpty());
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testUseMessageFilterPolicyTest() {
        assertDoesNotThrow(() -> UseMessageFilterPolicy.usePolicy(snsClient, subArn));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testAddTagsTest() {
        assertDoesNotThrow(() -> AddTags.addTopicTags(snsClient, topicArn));
        logger.info("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testListTagsTest() {
        assertDoesNotThrow(() -> ListTags.listTopicTags(snsClient, topicArn));
        logger.info("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testDeleteTagTest() {
        assertDoesNotThrow(() -> DeleteTag.removeTag(snsClient, topicArn, "Environment"));
        logger.info("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testUnsubscribeTest() {
        assertDoesNotThrow(() -> Unsubscribe.unSub(snsClient, subArn));
        logger.info("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testPublishTopicTest() {
        assertDoesNotThrow(() -> PublishTopic.pubTopic(snsClient, message, topicArn));
        logger.info("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void testSubscribeTextSMSTest() {
        assertDoesNotThrow(() -> SubscribeTextSMS.subTextSNS(snsClient, topicArn, phone));
        logger.info("Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void testPublishTextSMSTest() {
        assertDoesNotThrow(() -> PublishTextSMS.pubTextSMS(snsClient, message, phone));
        logger.info("Test 14 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void testListSubscriptionsTest() {
        assertDoesNotThrow(() -> ListSubscriptions.listSNSSubscriptions(snsClient));
        logger.info("Test 15 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
    public void testDeleteTopic() {
        assertDoesNotThrow(() -> DeleteTopic.deleteSNSTopic(snsClient, topicArn));
        logger.info("Test 16 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
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

        // getter
        String getTopicName() {
            return this.topicName;
        }

        String getAttributeName() {
            return this.attributeName;
        }

        String getAttributeValue() {
            return this.attributeValue;
        }

        String getLambdaarn() {
            return this.lambdaarn;
        }

        String getPhone() {
            return this.phone;
        }

        String getMessage() {
            return this.message;
        }

        String getEmail() {
            return this.email;
        }
    }
}