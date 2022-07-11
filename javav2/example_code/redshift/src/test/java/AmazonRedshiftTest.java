import com.example.redshift.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonRedshiftTest {

    private static RedshiftClient redshiftClient;
    private static Region region;
    private static String clusterId = "";
    private static String masterUsername = "";
    private static String masterUserPassword = "";
    private static String eventSourceType = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_WEST_2;
        redshiftClient = RedshiftClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        try (InputStream input = AmazonRedshiftTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            clusterId = prop.getProperty("clusterId");
            masterUsername = prop.getProperty("masterUsername");
            masterUserPassword = prop.getProperty("masterUserPassword");
            eventSourceType = prop.getProperty("eventSourceType");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSRedshiftService_thenNotNull() {
        assertNotNull(redshiftClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateCluster() {

        CreateAndModifyCluster.createCluster(redshiftClient, clusterId, masterUsername, masterUserPassword);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void WaitForClusterReady() {

        CreateAndModifyCluster.waitForClusterReady(redshiftClient, clusterId);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ModifyClusterReady() {

        CreateAndModifyCluster.modifyCluster(redshiftClient, clusterId);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeClusters() {

        DescribeClusters.describeRedshiftClusters(redshiftClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void FindReservedNodeOffer() {

        FindReservedNodeOffer.listReservedNodes(redshiftClient);
        FindReservedNodeOffer.findReservedNodeOffer(redshiftClient);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListEvents() {

        ListEvents.listRedShiftEvents(redshiftClient, clusterId, eventSourceType);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteCluster() {
        DeleteCluster.deleteRedshiftCluster(redshiftClient, clusterId);
        System.out.println("Test 8 passed");
    }
}
