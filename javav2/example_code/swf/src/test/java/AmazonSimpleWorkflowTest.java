import com.example.helloswf.ActivityWorker;
import com.example.helloswf.SWFWorkflowDemo;
import com.example.helloswf.WorkflowStarter;
import com.example.helloswf.WorkflowWorker;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.swf.SwfClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonSimpleWorkflowTest {

    private static SwfClient swf ;
    private static String workflowInput = "";
    private static String domain = "";
    private static String taskList = "";
    private static String workflow = "";
    private static String workflowVersion = "";
    private static String activity = "";
    private static String activityVersion = "";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = software.amazon.awssdk.regions.Region.US_EAST_1;
        swf = SwfClient.builder()
                .region(region)
                .build();
        try (InputStream input = AmazonSimpleWorkflowTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            domain = prop.getProperty("domain");
            taskList = prop.getProperty("taskList");
            workflow = prop.getProperty("workflow");
            workflowVersion = prop.getProperty("workflowVersion");
            activity = prop.getProperty("activity");
            activityVersion = prop.getProperty("activityVersion");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingSimpleWorkflowService_thenNotNull() {
        assertNotNull(swf);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void registerDomain() {

        SWFWorkflowDemo.registerDomain(swf, domain);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void RegisterWorkflowType() {

        SWFWorkflowDemo.registerWorkflowType(swf, domain, workflow, workflowVersion, taskList);
        System.out.println("Test 3 passed");
   }

    @Test
    @Order(4)
   public void registerActivityType() {
        SWFWorkflowDemo.registerActivityType(swf, domain, activity, activityVersion, taskList);
        System.out.println("Test 4 passed");
   }

    @Test
    @Order(5)
   public void WorkflowStarter() {
       WorkflowStarter.startWorkflow(swf, workflowInput, domain, workflow,workflowVersion);
        System.out.println("Test 5 passed");
   }

    @Test
    @Order(6)
   public void WorkflowWorker(){

        WorkflowWorker.pollADecision(swf, domain, taskList, activity, activityVersion);
        System.out.println("Test 6 passed");
   }

    @Test
    @Order(7)
   public void ActivityWorker() {
       ActivityWorker.getPollData(swf, domain, taskList);
        System.out.println("Test 7 passed");
   }
}
