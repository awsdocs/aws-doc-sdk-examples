import com.example.resilient.AutoScaler;
import com.example.resilient.Database;
import com.example.resilient.LaunchTemplateCreator;
import com.example.resilient.LoadBalancer;
import com.example.resilient.ParameterHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.ec2.model.Subnet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResilientTests {

    public static String fileName = "" ;
    public static String tableName = "" ;
    public static String startScript = "" ;
    public static String policyFile = "" ;
    public static String ssmJSON = "" ;
    public static String failureResponse = "" ;
    public static String healthCheck = "" ;
    public static String templateName = "" ;
    public static String roleName = "" ;
    public static String policyName = "" ;
    public static String profileName = "" ;

    public static String badCredsProfileName= "" ;

    public static String targetGroupName = "" ;
    public static String autoScalingGroupName = "" ;
    public static String lbName = "" ;
    public static String protocol = "" ;
    public static int port ;

    String elbDnsName = "";

    static LoadBalancer loadBalancer = null;

    static AutoScaler autoScaler = null ;

    static Database database = null;

    @BeforeAll
    public static void setUp() {

        loadBalancer = new LoadBalancer();
        autoScaler = new AutoScaler();
        database = new Database();

        try (InputStream input = ResilientTests.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            fileName = prop.getProperty("fileName");
            tableName = prop.getProperty("tableName");
            startScript = prop.getProperty("startScript");
            policyFile = prop.getProperty("policyFile");
            ssmJSON = prop.getProperty("ssmJSON");
            failureResponse = prop.getProperty("failureResponse");
            healthCheck = prop.getProperty("healthCheck");
            templateName = prop.getProperty("templateName");
            roleName = prop.getProperty("roleName");
            policyName = prop.getProperty("policyName");
            profileName = prop.getProperty("profileName");
            badCredsProfileName = prop.getProperty("badCredsProfileName");
            targetGroupName = prop.getProperty("targetGroupName");
            autoScalingGroupName = prop.getProperty("autoScalingGroupName");
            lbName = prop.getProperty("lbName");
            protocol = prop.getProperty("protocol");
            port = Integer.parseInt(prop.getProperty("port"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void setUpTable() throws IOException {
        Database database = new Database();
        database.createTable(tableName, fileName);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void createTemplate() {
        LaunchTemplateCreator templateCreator = new LaunchTemplateCreator();
        templateCreator.createTemplate(policyFile, policyName, profileName, startScript, templateName, roleName);
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void setupResources() throws InterruptedException {
        TimeUnit.SECONDS.sleep(30);
        AutoScaler autoScaler = new AutoScaler();
        String[] zones = autoScaler.createGroup(3, templateName, autoScalingGroupName);

        ParameterHelper paramHelper = new ParameterHelper();
        paramHelper.reset();

        String vpcId = autoScaler.getDefaultVPC();
        List<Subnet> subnets = autoScaler.getSubnets(vpcId, zones);
        System.out.println("You have retrieved a list with "+subnets.size() +" subnets");
        String targetGroupArn = loadBalancer.createTargetGroup(protocol, port, vpcId, targetGroupName);
        elbDnsName = loadBalancer.createLoadBalancer(subnets, targetGroupArn, lbName, port, protocol);
        assertNotNull(elbDnsName);
    }


    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void destroyResources() throws InterruptedException {
        loadBalancer.deleteLoadBalancer(lbName);
        System.out.println("*** Wait 30 secs for resource to be deleted");
        TimeUnit.SECONDS.sleep(30);
        loadBalancer.deleteTargetGroup(targetGroupName);
        autoScaler.deleteAutoScaleGroup(autoScalingGroupName);
        autoScaler.deleteRolesPolicies(policyName, roleName, profileName );
        autoScaler.deleteTemplate(templateName);
        database.deleteTable(tableName);

    }
}
