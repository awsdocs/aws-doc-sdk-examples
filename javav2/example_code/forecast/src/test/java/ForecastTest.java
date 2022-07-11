import com.example.forecast.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import software.amazon.awssdk.services.forecast.ForecastClient;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ForecastTest {

    private static ForecastClient forecast;
    private static Region region;
    private static String predictorARN="" ;
    private static String forecastArn=""; // set in test 3
    private static String forecastARNToDelete="";
    private static String forecastName="" ;
    private static String dataSet="";
    private static String myDataSetARN =""; // set in test 2

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_WEST_2;
        forecast = ForecastClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = ForecastTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            predictorARN = prop.getProperty("predictorARN");
            forecastName = prop.getProperty("forecastName");
            dataSet = prop.getProperty("dataSet");
            forecastARNToDelete = prop.getProperty("forecastARNToDelete");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(forecast);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateDataSet() {

        myDataSetARN = CreateDataSet.createForecastDataSet(forecast, dataSet);
        assertTrue(!myDataSetARN.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateForecast() {

        forecastArn =CreateForecast.createNewForecast(forecast, forecastName, predictorARN);
        assertTrue(!forecastArn.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListDataSets() {

        ListDataSets.listForecastDataSets(forecast);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListDataSetGroups(){

        ListDataSetGroups.listDataGroups(forecast);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListForecasts() {

        ListForecasts.listAllForeCasts(forecast);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void DeleteDataSet() {

        DeleteDataset.deleteForecastDataSet(forecast, myDataSetARN);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteForecast() {

        DeleteForecast.delForecast(forecast, forecastARNToDelete);
        System.out.println("Test 8 passed");
    }
}
