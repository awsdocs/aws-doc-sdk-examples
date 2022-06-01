import com.example.route.*;
import software.amazon.awssdk.services.route53.Route53Client;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Route53Test {

    private static String domainName = "";
    private static String healthCheckId = "";
    private static String hostedZoneId = "";
    private static Route53Client route53Client;

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.AWS_GLOBAL;
        route53Client = Route53Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = Route53Test.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            domainName = prop.getProperty("domainName");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(route53Client);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateHealthCheck() {
        healthCheckId= CreateHealthCheck.createCheck(route53Client, domainName);
        assertTrue(!healthCheckId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateHostedZone() {
        hostedZoneId= CreateHostedZone.createZone(route53Client, domainName);
        assertTrue(!hostedZoneId.isEmpty());
        System.out.println("Test 3 passed");
    }


    @Test
    @Order(4)
    public void GetHealthCheckStatus() {

    try{
        TimeUnit.SECONDS.sleep(20); // wait for the new health check
        GetHealthCheckStatus.getHealthStatus(route53Client, healthCheckId);

      } catch (InterruptedException e) {
        e.printStackTrace();
       }
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void ListHealthChecks() {
        ListHealthChecks.listAllHealthChecks(route53Client);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void UpdateHealthCheck() {
        UpdateHealthCheck.updateSpecificHealthCheck(route53Client, healthCheckId );
        System.out.println("Test 6 passed");
   }

    @Test
    @Order(7)
   public void ListHostedZones() {
       ListHostedZones.listZones(route53Client);
        System.out.println("Test 7 passed");
   }

    @Test
    @Order(8)
    public void DeleteHealthCheck() {
        DeleteHealthCheck.delHealthCheck(route53Client, healthCheckId);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void DeleteHostedZone() {
        DeleteHostedZone.delHostedZone(route53Client, hostedZoneId);
        System.out.println("Test 9 passed");
    }
}
