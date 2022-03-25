import com.aws.example.*;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ElasticBeanstalkTest {

    private static ElasticBeanstalkClient beanstalkClient;
    private static String appName="";
    private static String envName="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        beanstalkClient = ElasticBeanstalkClient.builder()
                .region(region)
                .build();

        try (InputStream input = ElasticBeanstalkTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            appName = prop.getProperty("appName");
            envName = prop.getProperty("envName");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(beanstalkClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateApp() {
        String appArn = CreateApplication.createApp(beanstalkClient, appName);
        assertTrue(!appArn.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateEnvironment() {
        String environmentArn = CreateEnvironment.createEBEnvironment(beanstalkClient, envName, appName);
        assertTrue(!environmentArn.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeApplications() {
        DescribeApplications.describeApps(beanstalkClient);
        assertTrue(true);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DescribeEnvironment() {
        DescribeEnvironment.describeEnv(beanstalkClient, appName);
        System.out.println("Test 5 passed");
    }

  @Test
  @Order(6)
  public void DeleteApplication() throws InterruptedException {

      System.out.println("*** Wait for 5 MIN so the app can be deleted");
      TimeUnit.MINUTES.sleep(5);
      DeleteApplication.deleteApp(beanstalkClient, appName);
      System.out.println("Test 6 passed");
  }
}
