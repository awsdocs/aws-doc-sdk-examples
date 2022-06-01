import com.example.sage.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SageMakerTest {

    private static SageMakerClient sageMakerClient ;

    private static String image = "";
    private static String modelDataUrl = "";
    private static String executionRoleArn = "";
    private static String modelName = "";
    private static String s3UriData = "";
    private static String s3Uri = "";
    private static String trainingJobName = "";
    private static String roleArn = "";
    private static String s3OutputPath = "";
    private static String channelName = "";
    private static String trainingImage = "";

    @BeforeAll
    public static void setUp() throws IOException {


        Region region = Region.US_WEST_2;
        sageMakerClient = SageMakerClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = SageMakerTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            image = prop.getProperty("image");
            modelDataUrl = prop.getProperty("modelDataUrl");
            executionRoleArn = prop.getProperty("executionRoleArn");
            modelName = prop.getProperty("modelName");
            s3UriData = prop.getProperty("s3UriData");
            s3Uri = prop.getProperty("s3Uri");
            roleArn = prop.getProperty("roleArn");
            trainingJobName = prop.getProperty("trainingJobName");
            s3OutputPath = prop.getProperty("s3OutputPath");
            channelName = prop.getProperty("channelName");
            trainingImage = prop.getProperty("trainingImage");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(sageMakerClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateModel() {

        CreateModel.createSagemakerModel(sageMakerClient, modelDataUrl, image, modelName,executionRoleArn);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateTrainingJob() {

        CreateTrainingJob.trainJob(sageMakerClient, s3UriData, s3Uri, trainingJobName, roleArn, s3OutputPath, channelName, trainingImage);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeTrainingJob() {
        DescribeTrainingJob.describeTrainJob(sageMakerClient, trainingJobName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListModels() {
        ListModels.listAllModels(sageMakerClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListNotebooks() {

    ListNotebooks.listBooks(sageMakerClient);
    System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListAlgorithms() {
        ListAlgorithms.listAlgs(sageMakerClient);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void ListTrainingJobs() {
        ListTrainingJobs.listJobs(sageMakerClient);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void DeleteModel() {
        DeleteModel.deleteSagemakerModel(sageMakerClient, modelName);
        System.out.println("Test 9 passed");
    }
}
