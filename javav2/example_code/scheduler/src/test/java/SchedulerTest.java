// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.eventbrideschedule.scenario.CloudFormationHelper;
import com.example.eventbrideschedule.scenario.EventbridgeSchedulerActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SchedulerTest {

    private static final String STACK_NAME = "workflow-stack-name23";

    private static String emailAddress = "foo@example.com";

    private static String scheduleGroupName = "myScheduleGroup";
    private static String roleArn = "";
    private static String snsTopicArn = "";

    private static String oneTimeScheduleName = "testOneTime1";

    private static String recurringScheduleName = "recurringSchedule1";

    private static final EventbridgeSchedulerActions eventbridgeActions = new EventbridgeSchedulerActions();

    @BeforeAll
    public static void setUp() {
        CloudFormationHelper.deployCloudFormationStack(STACK_NAME, emailAddress);
        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(STACK_NAME);
        roleArn = stackOutputs.get("RoleARN");
        snsTopicArn = stackOutputs.get("SNStopicARN");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateScheduleGroup() {
        assertDoesNotThrow(() -> {
            eventbridgeActions.createScheduleGroup(scheduleGroupName).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testOneTimeSchedule() {
        assertDoesNotThrow(() -> {
            LocalDateTime scheduledTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            String scheduleExpression = "at(" + scheduledTime.format(formatter) + ")";
            eventbridgeActions.createScheduleAsync(
                oneTimeScheduleName,
                scheduleExpression,
                scheduleGroupName,
                snsTopicArn,
                roleArn,
                "One time scheduled event test from schedule",
                true,
                true).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testReoccuringSchedule() {
        assertDoesNotThrow(() -> {
            int scheduleRateInMinutes = 10;
            String scheduleExpression = "rate(" + scheduleRateInMinutes + " minutes)";
            return eventbridgeActions.createScheduleAsync(
                recurringScheduleName,
                scheduleExpression,
                scheduleGroupName,
                snsTopicArn,
                roleArn,
                "Recurrent event test from schedule " + recurringScheduleName,
                true,
                true).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testDeleteScheduleGroup() {
        assertDoesNotThrow(() -> {
        eventbridgeActions.deleteScheduleGroupAsync(scheduleGroupName).join();

        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testDelOneTimeSchedule() {
        assertDoesNotThrow(() -> {
            eventbridgeActions.deleteScheduleAsync(oneTimeScheduleName, scheduleGroupName).join();

        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testDelReoccringSchedule() {
        assertDoesNotThrow(() -> {
            eventbridgeActions.deleteScheduleAsync(recurringScheduleName, scheduleGroupName).join();
        });
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testDelStack() {
        assertDoesNotThrow(() -> {
            CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
        });
    }
}