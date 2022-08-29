import com.example.secrets.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecretManagerTest {

    private static SecretsManagerClient secretsClient;
    private static String newSecretName="";
    private static String secretValue="";
    private static String secretARN="";
    private static String modSecretValue="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        secretsClient = SecretsManagerClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = SecretManagerTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            newSecretName = prop.getProperty("newSecretName");
            secretValue = prop.getProperty("secretValue");
            modSecretValue = prop.getProperty("modSecretValue");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(secretsClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateSecret() {
        secretARN = CreateSecret.createNewSecret(secretsClient, newSecretName,secretValue);
        assertTrue(!secretARN.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeSecret() {
        DescribeSecret.describeGivenSecret(secretsClient, secretARN );
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void GetSecretValue() {
        GetSecretValue.getValue(secretsClient, secretARN );
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void UpdateSecret() {
        UpdateSecret.updateMySecret(secretsClient,secretARN, modSecretValue);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListSecrets() {
        ListSecrets.listAllSecrets(secretsClient);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void DeleteSecret() {
        DeleteSecret.deleteSpecificSecret(secretsClient, secretARN);
        System.out.println("Test 7 passed");
    }

}
