import com.example.guardduty.GetDetector;
import com.example.guardduty.GetFindings;
import com.example.guardduty.ListDetectors;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GuarddutyTest {

    private static GuardDutyClient guardDutyClient ;
    private static String detectorId = "";
    private static String findingId = "";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        guardDutyClient = GuardDutyClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = GuarddutyTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            detectorId = prop.getProperty("detectorId");
            findingId = prop.getProperty("findingId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(guardDutyClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void GetDetector() {

        GetDetector.getSpecificDetector(guardDutyClient, detectorId);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void GetFindings() {
        GetFindings.getSpecificFinding(guardDutyClient, findingId, detectorId);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListDetectors() {
        ListDetectors.listAllDetectors(guardDutyClient);
        System.out.println("Test 4 passed");
    }

}
