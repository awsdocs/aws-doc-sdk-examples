import com.example.cloudwatch.DeleteAlarm;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import com.example.cloudwatch.*;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudWatchServiceIntegrationTest {

    private static CloudWatchClient cw = null;
    private static CloudWatchLogsClient cloudWatchLogsClient = null;
    private static String logGroup="";
    private static String alarmName="";
    private static String streamName ="";
    private static String metricId = "";
    private static String instanceId="";
    private static String ruleResource = "";
    private static String filterName="";
    private static String destinationArn="";
    private static String roleArn ="";
    private static String filterPattern = "";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;;
        cw = CloudWatchClient.builder()
                .region(region)
                .build();
        cloudWatchLogsClient = CloudWatchLogsClient.builder().build();

        try (InputStream input = CloudWatchServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            logGroup = prop.getProperty("logGroup");
            alarmName = prop.getProperty("alarmName");
            streamName = prop.getProperty("streamName");
            ruleResource = prop.getProperty("ruleResource");
            metricId = prop.getProperty("metricId");
            filterName = prop.getProperty("filterName");
            destinationArn = prop.getProperty("destinationArn");
            roleArn= prop.getProperty("roleArn");
            filterPattern= prop.getProperty("filterPattern");
            instanceId= prop.getProperty("instanceId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSCWService_thenNotNull() {
        assertNotNull(cw);
        assertNotNull(cloudWatchLogsClient);
        System.out.printf("\n Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateAlarm() {

        try {
            PutMetricAlarm.putMetricAlarm(cw,alarmName,instanceId );
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.printf("\n Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeAlarms() {

        try {
            DescribeAlarms.deleteCWAlarms(cw);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.printf("\n Test 3 passed");
    }

    @Test
    @Order(4)
    public void CreateSubscriptionFilters() {

       try {
         PutSubscriptionFilter.putSubFilters(cloudWatchLogsClient, filterName, filterPattern, logGroup, roleArn, destinationArn);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.printf("\n Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeSubscriptionFilters() {

        try {
            DescribeSubscriptionFilters.describeFilters(cloudWatchLogsClient,logGroup);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.printf("\n Test 5 passed");
    }


    @Test
    @Order(6)
    public void DisableAlarmActions() {

       try {
           DisableAlarmActions.disableActions(cw, alarmName);
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 6 passed");
    }

    @Test
    @Order(7)
    public void EnableAlarmActions() {

       try {
            EnableAlarmActions.enableActions(cw, alarmName) ;
        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 7 passed");
    }

    @Test
    @Order(8)
    public void GetLogEvents() {

        try {
                GetLogEvents.getCWLogEvebts(cloudWatchLogsClient,logGroup,streamName);
            } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 8 passed");
    }


    @Test
    @Order(9)
    void PutCloudWatchEvent() {

        CloudWatchEventsClient cwe =
                CloudWatchEventsClient.builder().build();

        try {
            PutEvents.putCWEvents(cwe,ruleResource );

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("\n Test 9 passed");
    }

    @Test
    @Order(10)
    public void GetMetricData() {

       try {

        GetMetricData.getMetData(cw);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 10 passed");
    }

    @Test
    @Order(11)
    public void DeleteSubscriptionFilter() {

        try {
           DeleteSubscriptionFilter.deleteSubFilter(cloudWatchLogsClient, filterName,logGroup );

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 11 passed");
    }

    @Test
    @Order(12)
    public void DeleteAlarm() {

      try {
          DeleteAlarm.deleteCWAlarm(cw, alarmName);

        } catch (CloudWatchException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("\n Test 12 passed");
    }
}





