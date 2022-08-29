/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.sns.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSNSTest {

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
}