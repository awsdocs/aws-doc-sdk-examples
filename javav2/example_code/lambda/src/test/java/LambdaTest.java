/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.lambda.CreateFunction;
import com.example.lambda.DeleteFunction;
import com.example.lambda.GetAccountSettings;
import com.example.lambda.LambdaScenario;
import com.example.lambda.LambdaInvoke;
import com.example.lambda.ListLambdaFunctions;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.lambda.LambdaClient;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeAll;
import software.amazon.awssdk.regions.Region;
import java.io.InputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LambdaTest {

    private static LambdaClient awsLambda;
    private static String functionName="";
    private static String functionNameSc="";
    private static String filePath="";
    private static String role="";
    private static String handler="";
    private static String bucketName="";
    private static String key="";

    @BeforeAll
    public static void setUp()  {

        Region region = Region.US_WEST_2;
        awsLambda = LambdaClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = LambdaTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            functionName = prop.getProperty("functionName");
            filePath = prop.getProperty("filePath");
            role = prop.getProperty("role");
            handler = prop.getProperty("handler");
            functionNameSc = prop.getProperty("functionNameSc");
            bucketName = prop.getProperty("bucketName");
            key = prop.getProperty("key");

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
    public void LambdaInvoke() throws InterruptedException {
        System.out.println("*** Wait for 2 MIN so the resource is available");
        TimeUnit.MINUTES.sleep(2);
        LambdaInvoke.invokeFunction(awsLambda, functionName);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DeleteFunction() {
        DeleteFunction.deleteLambdaFunction(awsLambda, functionName);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(7)
    public void LambdaScenario() {

        String funArn = LambdaScenario.createLambdaFunction(awsLambda, functionNameSc, filePath, role, handler);
        System.out.println("The function ARN is "+funArn);

        // Get the Lambda function.
        System.out.println("Getting the " +functionNameSc +" Lambda function.");
        LambdaScenario.getFunction(awsLambda, functionNameSc);

        // List the Lambda functions.
        System.out.println("Listing all functions.");
        LambdaScenario.listFunctions(awsLambda);

        System.out.println("*** Invoke the Lambda function.");
        LambdaScenario.invokeFunction(awsLambda, functionNameSc);

        System.out.println("*** Update the Lambda function code.");
        LambdaScenario.updateFunctionCode(awsLambda, functionNameSc, bucketName, key);

        System.out.println("*** Invoke the Lambda function again with the updated code.");
        LambdaScenario.invokeFunction(awsLambda, functionNameSc);

        System.out.println("Delete the AWS Lambda function.");
        LambdaScenario.deleteLambdaFunction(awsLambda, functionNameSc );
    }
}
