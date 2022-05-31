import com.example.kinesis.CreateDataStream;
import com.example.kinesis.DescribeLimits;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import java.io.*;
import com.example.kinesis.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KinesisServiceTest {

    private static KinesisClient kinesisClient;
    private static String streamName = "";

    @BeforeAll
    public static void setUp() {

        Region region = Region.US_EAST_1;
        kinesisClient = KinesisClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        try (InputStream input = KinesisServiceTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            streamName = prop.getProperty("streamName");
            //existingDataStream = prop.getProperty("existingDataStream");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingKinesisService_thenNotNull() {
        assertNotNull(kinesisClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateDataStream() {

        CreateDataStream.createStream(kinesisClient, streamName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeLimits() {

        DescribeLimits.describeKinLimits(kinesisClient);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListShards() {

        try {
            //Wait 60 secs for table to complete
            TimeUnit.SECONDS.sleep(60);
            ListShards.listKinShards(kinesisClient, streamName);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void PutRecords() {

        StockTradesWriter.setStockData(kinesisClient, streamName);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void GetRecords() {

        GetRecords.getStockTrades(kinesisClient, streamName);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void DeleteDataStreem() {

        //    DeleteDataStream.deleteStream(kinesisClient, streamName);
        System.out.println("Test 7 passed");
    }
}
