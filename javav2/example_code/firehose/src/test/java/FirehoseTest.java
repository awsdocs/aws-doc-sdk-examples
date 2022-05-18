import com.example.firehose.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import java.io.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FirehoseTest {

    private static FirehoseClient firehoseClient;
    private static Region region;
    private static String bucketARN = "";
    private static String roleARN = "";
    private static String newStream = "";
    private static String existingStream = "";
    private static String textValue = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_WEST_2;
        firehoseClient = FirehoseClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = FirehoseTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            bucketARN = prop.getProperty("bucketARN");
            roleARN = prop.getProperty("roleARN");
            newStream = prop.getProperty("newStream");
            textValue = prop.getProperty("textValue");
            existingStream = prop.getProperty("existingStream");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSFirehoseService_thenNotNull() {
        assertNotNull(firehoseClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateDeliveryStream() {

        CreateDeliveryStream.createStream(firehoseClient, bucketARN, roleARN, newStream);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void PutRecord() {

        PutRecord.putSingleRecord(firehoseClient, textValue, existingStream);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void PutBatchRecords() {

        PutBatchRecords.addStockTradeData(firehoseClient, existingStream);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListDeliveryStreams() {

        ListDeliveryStreams.listStreams(firehoseClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DeleteStream() {

        DeleteStream.delStream(firehoseClient, existingStream);
        System.out.println("Test 6 passed");
  }
}
