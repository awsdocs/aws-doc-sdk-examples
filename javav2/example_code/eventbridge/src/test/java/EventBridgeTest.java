import com.example.eventbridge.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventBridgeTest {

    private static  EventBridgeClient eventBrClient;
    private static Region region;
    private static String ruleName = "";

    @BeforeAll
    public static void setUp() throws IOException {

        region = Region.US_WEST_2;
        eventBrClient = EventBridgeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = EventBridgeTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            ruleName = prop.getProperty("ruleName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingEBervice_thenNotNull() {
        assertNotNull(eventBrClient);
        System.out.println("Test 1 passed");
    }


    @Test
    @Order(2)
    public void CreateRule() {

        CreateRule.createEBRule(eventBrClient, ruleName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeRule() {

        DescribeRule.describeSpecificRule(eventBrClient, ruleName);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListRules() {

        ListRules.listAllRules(eventBrClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListEventBuses() {

        ListEventBuses.listBuses(eventBrClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DeleteRule() {

        DeleteRule.deleteEBRule(eventBrClient, ruleName);
        System.out.println("Test 6 passed");
    }
}
