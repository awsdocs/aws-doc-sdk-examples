import com.example.rds.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rds.RdsClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonRDSTest {

    private static  RdsClient rdsClient ;
    private static Region region;
    private static String dbInstanceIdentifier = "" ;
    private static String dbSnapshotIdentifier = "" ;
    private static String dbName = "" ;
    private static String masterUsername = "" ;
    private static String masterUserPassword = "" ;
    private static String newMasterUserPassword = "" ;

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_WEST_2;
        rdsClient = RdsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AmazonRDSTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            dbInstanceIdentifier = prop.getProperty("dbInstanceIdentifier");
            dbSnapshotIdentifier = prop.getProperty("dbSnapshotIdentifier");
            dbName = prop.getProperty("dbName");
            masterUsername = prop.getProperty("masterUsername");
            masterUserPassword = prop.getProperty("masterUserPassword");
            newMasterUserPassword = prop.getProperty("newMasterUserPassword");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSRdsService_thenNotNull() {
        assertNotNull(rdsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateDBInstance() {
        CreateDBInstance.createDatabaseInstance(rdsClient, dbInstanceIdentifier, dbName, masterUsername, masterUserPassword);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void waitForInstanceReady() {

        CreateDBInstance.waitForInstanceReady(rdsClient, dbInstanceIdentifier);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeAccountAttributes() {
        DescribeAccountAttributes.getAccountAttributes(rdsClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeDBInstances() {

        DescribeDBInstances.describeInstances(rdsClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ModifyDBInstance() {
        ModifyDBInstance.updateIntance(rdsClient, dbInstanceIdentifier, newMasterUserPassword);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void CreateDBSnapshot() {
        CreateDBSnapshot.createSnapshot(rdsClient, dbInstanceIdentifier, dbSnapshotIdentifier);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteDBInstance() {

        DeleteDBInstance.deleteDatabaseInstance(rdsClient, dbInstanceIdentifier);
        System.out.println("Test 8 passed");
    }

 }



