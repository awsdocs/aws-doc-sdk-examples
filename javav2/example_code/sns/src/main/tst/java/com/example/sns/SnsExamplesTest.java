package com.example.sns;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SnsExamplesTest {

    /**
     * The entry point, which results in calls to all test methods.
     *
     * @param args Command line arguments (ignored).
     */
    public static void main(String[] args) {
        SnsExamplesTest tester = new SnsExamplesTest();
        tester.runAllTests();
    }

    SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_1).build();
    Subscription subscription =  snsClient.listSubscriptions(ListSubscriptionsRequest.builder().build()).subscriptions().get(0);
    String topicArn = subscription.topicArn();
    String subscriptionToken = "";
    String validSubscriptionEndpoint = subscription.endpoint();
    String subscriptionArn = subscription.subscriptionArn();

    @Test
    public void runAllTests() {
        CheckOptOut_returnsSuccessful();
        ConfirmSubscription_returnsSuccessful();
        CreateTopic_returnsSuccessful();
        DeleteTopic_returnsSuccessful();

    }

    @BeforeEach
    private void setup() {


    }


    @Test
    public void CheckOptOut_returnsSuccessful() {
        //GIVEN
        CheckOptOut checkOptOut = new CheckOptOut();
        String phoneNumber = "+155555555555";
        String[] args = new String[]{phoneNumber};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        checkOptOut.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "CheckOptOut should print a response");


    }

    @Test
    public void ConfirmSubscription_returnsSuccessful() {
        //GIVEN
        ConfirmSubscription confirmSubscription = new ConfirmSubscription();
        String[] args = new String[]{subscriptionToken, topicArn};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        confirmSubscription.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "ConfirmSubscription should print a response");


    }

    @Test
    public void CreateTopic_returnsSuccessful() {
        //GIVEN
        CreateTopic createTopic = new CreateTopic();
        String topicName = "test";
        String[] args = new String[]{topicName};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        int totalTopics = snsClient.listTopics(ListTopicsRequest.builder().build()).topics().size();

        //WHEN - compute the average
        createTopic.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        int newTotalTopics = snsClient.listTopics(ListTopicsRequest.builder().build()).topics().size();

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "CreateTopic should print a response");
        assertEquals("CreateTopic should add a new Topic", totalTopics +1, newTotalTopics );



    }

    @Test
    public void DeleteTopic_returnsSuccessful() {
        //GIVEN
        DeleteTopic deleteTopic = new DeleteTopic();
        String topicName = "test";
        String topicArn =  snsClient.createTopic(CreateTopicRequest.builder().name(topicName).build()).topicArn();
        String[] args = new String[]{topicArn};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        int totalTopics = snsClient.listTopics(ListTopicsRequest.builder().build()).topics().size();

        //WHEN - compute the average
        deleteTopic.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        int newTotalTopics = snsClient.listTopics(ListTopicsRequest.builder().build()).topics().size();

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "DeleteTopic should print a response");
        assertEquals("DeleteTopic should remove a Topic", totalTopics - 1, newTotalTopics );



    }

    @Test
    public void GetSMSAtrributes_returnsSuccessful() {
        //GIVEN
        GetSMSAtrributes getSMSAtrributes = new GetSMSAtrributes();
        String[] args = new String[]{};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        getSMSAtrributes.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "GetSMSAtrributes should print a response");


    }

    @Test
    public void GetTopicAttributes_returnsSuccessful() {
        //GIVEN
        GetTopicAttributes getTopicAttributes = new GetTopicAttributes();
        String topicName = "test";
        String topicArn =  snsClient.createTopic(CreateTopicRequest.builder().name(topicName).build()).topicArn();
        String[] args = new String[]{topicArn};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        getTopicAttributes.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "GetTopicAttributes should print a response");


    }

    @Test
    public void ListOptOut_returnsSuccessful() {
        //GIVEN
        ListOptOut listOptOut = new ListOptOut();
        String[] args = new String[]{};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        listOptOut.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "ListOptOut should print a response");


    }

    @Test
    public void ListSubscriptions_returnsSuccessful() {
        //GIVEN
        ListSubscriptions listSubscriptions = new ListSubscriptions();
        String[] args = new String[]{};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        listSubscriptions.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "ListSubscriptions should print a response");


    }

    @Test
    public void ListTopics_returnsSuccessful() {
        //GIVEN
        ListTopics listTopics = new ListTopics();
        String[] args = new String[]{};
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));

        //WHEN - compute the average
        listTopics.main(args);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = buffer.toString();
        buffer.reset();
        System.out.println(output);

        //THEN - throw Illegal Argument Exception
        assertTrue(output.length() > 0 , "ListTopics should print a response");


    }





}