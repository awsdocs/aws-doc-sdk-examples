import com.example.translate.BatchTranslation;
import com.example.translate.DescribeTextTranslationJob;
import com.example.translate.ListTextTranslationJobs;
import com.example.translate.TranslateText;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonTranslateTest {

    private static TranslateClient translateClient;
    private static Region region;
    private static String s3Uri = "";
    private static String s3UriOut = "";
    private static String jobName = "";
    private static String dataAccessRoleArn = "";
    private static String jobId = "";

    @BeforeAll
    public static void setUp() throws IOException {

       region = Region.US_WEST_2;
       translateClient = TranslateClient.builder()
                .region(region)
               .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AmazonTranslateTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            s3Uri = prop.getProperty("s3Uri");
            s3UriOut = prop.getProperty("s3UriOut");
            jobName = prop.getProperty("jobName");
            dataAccessRoleArn = prop.getProperty("dataAccessRoleArn");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingTranslateService_thenNotNull() {
        assertNotNull(translateClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void TranslateText() {

        TranslateText.textTranslate(translateClient);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void BatchTranslation() {

        jobId = BatchTranslation.translateDocuments(translateClient, s3Uri, s3UriOut, jobName, dataAccessRoleArn);
        assertTrue(!jobId.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListTextTranslationJobs() {
        ListTextTranslationJobs.getTranslationJobs(translateClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeTextTranslationJob() {
        DescribeTextTranslationJob.describeTextTranslationJob(translateClient, jobId);
        System.out.println("Test 5 passed");
    }
}
