import com.example.lambda.*;
import software.amazon.awssdk.services.lambda.LambdaClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LambdaTest {

    private static LambdaClient awsLambda;
    private static String functionName="";
    private static String completeFunctionName="";
    private static String filePath="";
    private static String role="";
    private static String handler="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        awsLambda = LambdaClient.builder()
                .region(region)
                .build();

        try (InputStream input = LambdaTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            functionName = prop.getProperty("functionName");
            completeFunctionName = prop.getProperty("completeFunctionName");
            filePath = prop.getProperty("filePath");
            role = prop.getProperty("role");
            handler = prop.getProperty("handler");
            completeFunctionName = prop.getProperty("completeFunctionName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(awsLambda);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateFunction() {
        CreateFunction.createLambdaFunction(awsLambda, functionName, filePath, role, handler);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void GetAccountSettings() {
        GetAccountSettings.getSettings(awsLambda);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListLambdaFunctions() {
        ListLambdaFunctions.listFunctions(awsLambda);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void LambdaInvoke() {

        LambdaInvoke.invokeFunction(awsLambda, completeFunctionName);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DeleteFunction() {
        DeleteFunction.deleteLambdaFunction(awsLambda, functionName);
        System.out.println("Test 5 passed");
    }

}
