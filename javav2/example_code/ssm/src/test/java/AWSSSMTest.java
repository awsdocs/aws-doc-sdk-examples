import com.example.ssm.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import software.amazon.awssdk.services.ssm.SsmClient;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSSMTest {

    private static SsmClient ssmClient;
    private static String paraName="";
    private static String title="";
    private static String source="";
    private static String category="";
    private static String severity="";
    private static String opsItemId ="";

    @BeforeAll
    public static void setUp(){

        Region region = Region.US_EAST_1;
        ssmClient = SsmClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AWSSSMTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            paraName = prop.getProperty("paraName");
            title = prop.getProperty("title");
            source = prop.getProperty("source");
            category = prop.getProperty("category");
            severity = prop.getProperty("severity");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(ssmClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateOpsItem(){
        opsItemId = CreateOpsItem.createNewOpsItem(ssmClient, title, source, category, severity) ;
        assertTrue(!opsItemId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void GetOpsItem(){
        GetOpsItem.getOpsItem(ssmClient, opsItemId);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeOpsItems() {
        DescribeOpsItems.describeItems(ssmClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeParameters() {
        DescribeParameters.describeParams(ssmClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void GetParameter() {
        GetParameter.getParaValue(ssmClient, paraName);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ResolveOpsItem() {
        ResolveOpsItem.setOpsItemStatus(ssmClient, opsItemId);
        System.out.println("Test 7 passed");
    }

}
