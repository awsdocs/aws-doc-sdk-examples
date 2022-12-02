/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.route.CreateHealthCheck;
import com.example.route.CreateHostedZone;
import com.example.route.DeleteHealthCheck;
import com.example.route.DeleteHostedZone;
import com.example.route.GetHealthCheckStatus;
import com.example.route.ListHealthChecks;
import com.example.route.ListHostedZones;
import com.example.route.Route53Scenario;
import com.example.route.UpdateHealthCheck;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.route53.Route53Client;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53domains.Route53DomainsClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Route53Test {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static String domainName = "";
    private static String healthCheckId = "";
    private static String hostedZoneId = "";
    private static Route53Client route53Client;
    private static Route53DomainsClient route53DomainsClient;
    private static String domainSuggestionSc = "" ;
    private static String domainTypeSc = "" ;
    private static String phoneNumerSc = "" ;
    private static String emailSc = "" ;
    private static String firstNameSc = "" ;
    private static String lastNameSc = "" ;
    private static String citySc = "" ;


    @BeforeAll
    public static void setUp() {
        Region region = Region.AWS_GLOBAL;
        route53Client = Route53Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        route53DomainsClient = Route53DomainsClient.builder()
            .region(Region.US_EAST_1)
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
            domainSuggestionSc = prop.getProperty("domainSuggestionSc");
            domainTypeSc = prop.getProperty("domainTypeSc");
            phoneNumerSc = prop.getProperty("phoneNumerSc");
            emailSc = prop.getProperty("emailSc");
            firstNameSc = prop.getProperty("firstNameSc");
            lastNameSc = prop.getProperty("lastNameSc");
            citySc = prop.getProperty("citySc");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void createHealthCheck() {
        healthCheckId= CreateHealthCheck.createCheck(route53Client, domainName);
        assertFalse(healthCheckId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void createHostedZone() {
        hostedZoneId= CreateHostedZone.createZone(route53Client, domainName);
        assertFalse(hostedZoneId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void getHealthCheckStatus() {
    try{
        TimeUnit.SECONDS.sleep(20); // wait for the new health check
        GetHealthCheckStatus.getHealthStatus(route53Client, healthCheckId);

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void listHealthChecks() {
        ListHealthChecks.listAllHealthChecks(route53Client);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void updateHealthCheck() {
        UpdateHealthCheck.updateSpecificHealthCheck(route53Client, healthCheckId );
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void listHostedZones() {
       ListHostedZones.listZones(route53Client);
       System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void deleteHealthCheck() {
        DeleteHealthCheck.delHealthCheck(route53Client, healthCheckId);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void deleteHostedZone() {
        DeleteHostedZone.delHostedZone(route53Client, hostedZoneId);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void scenarioTest() {
        System.out.println(DASHES);
        System.out.println("1. List current domains.");
        Route53Scenario.listDomains(route53DomainsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. List operations in the past year.");
        Route53Scenario.listOperations(route53DomainsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. View billing for the account in the past year.");
        Route53Scenario.listBillingRecords(route53DomainsClient);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. View prices for domain types.");
        Route53Scenario.listPrices(route53DomainsClient, domainTypeSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Get domain suggestions.");
        Route53Scenario.listDomainSuggestions(route53DomainsClient, domainSuggestionSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Check domain availability.");
        Route53Scenario.checkDomainAvailability(route53DomainsClient, domainSuggestionSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Check domain transferability.");
        Route53Scenario.checkDomainTransferability(route53DomainsClient, domainSuggestionSc);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Request a domain registration.");
        String opId = Route53Scenario.requestDomainRegistration(route53DomainsClient, domainSuggestionSc, phoneNumerSc, emailSc, firstNameSc, lastNameSc, citySc);
        assertFalse(opId.isEmpty());
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Get an operation detail.");
        Route53Scenario.getOperationalDetail(route53DomainsClient, opId);
        System.out.println(DASHES);
        System.out.println("Test 9 Passed");
    }
}
