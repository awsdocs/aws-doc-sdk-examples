import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import com.example.sage.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SageMakerpipelineTest {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static SageMakerClient sageMakerClient ;
    private static IamClient iam ;

    private static LambdaClient lambdaClient ;

    private static SqsClient sqsClient ;

    private static S3Client s3Client ;

    private static String sageMakerRoleName = "";
    private static String lambdaRoleName = "";
    private static String functionFileLocation = "";
    private static String functionName = "";
    private static String queueName = "" ;
    private static String bucketName = "";
    private static String lnglatData = "" ;
    private static String spatialPipelinePath = "";
    private static String pipelineName = "";

    @BeforeAll
    public static void setUp() throws IOException {
        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;
        Region region = Region.US_WEST_2;
        sageMakerClient = SageMakerClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        iam = IamClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        lambdaClient = LambdaClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        sqsClient = SqsClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        try (InputStream input = SageMakerpipelineTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            sageMakerRoleName = prop.getProperty("sageMakerRoleName");
            lambdaRoleName = prop.getProperty("lambdaRoleName");
            functionFileLocation = prop.getProperty("functionFileLocation");
            functionName = prop.getProperty("functionName");
            queueName = prop.getProperty("queueName");
            bucketName = prop.getProperty("bucketName");
            lnglatData = prop.getProperty("lnglatData");
            spatialPipelinePath = prop.getProperty("spatialPipelinePath");
            pipelineName = prop.getProperty("pipelineName")+randomNum;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testSagemakerWorkflow() throws InterruptedException {
        String handlerName = "SageMakerLambda::SageMakerLambda.SageMakerLambdaFunction::FunctionHandler";
        System.out.println(DASHES);
        System.out.println("Welcome to the Amazon SageMaker pipeline example scenario.");
        System.out.println(
            "\nThis example workflow will guide you through setting up and running an" +
                "\nAmazon SageMaker pipeline. The pipeline uses an AWS Lambda function and an" +
                "\nAmazon SQS Queue. It runs a vector enrichment reverse geocode job to" +
                "\nreverse geocode addresses in an input file and store the results in an export file.");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("First, we will set up the roles, functions, and queue needed by the SageMaker pipeline.");
        String lambdaRoleArn = SagemakerWorkflow.checkSageMakerRole(iam, sageMakerRoleName);
        String sageMakerRoleArn = SagemakerWorkflow.checkLambdaRole(iam, lambdaRoleName);

        String functionArn = SagemakerWorkflow.checkFunction(lambdaClient, functionName, functionFileLocation, lambdaRoleArn, handlerName);
        String queueUrl = SagemakerWorkflow.checkQueue(sqsClient, lambdaClient, queueName, functionName);
        System.out.println("The queue URL is "+queueUrl);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Setting up bucket "+bucketName);
        if (!SagemakerWorkflow.checkBucket(s3Client, bucketName)) {
            SagemakerWorkflow.setupBucket(s3Client, bucketName);
            System.out.println("Put "+lnglatData +" into "+bucketName);
            SagemakerWorkflow.putS3Object(s3Client, bucketName, "latlongtest.csv", lnglatData);
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Now we can create and run our pipeline.");
        SagemakerWorkflow.setupPipeline(sageMakerClient, spatialPipelinePath, sageMakerRoleArn, functionArn, pipelineName);
        String pipelineExecutionARN = SagemakerWorkflow.executePipeline(sageMakerClient, bucketName, queueUrl, sageMakerRoleArn, pipelineName);
        System.out.println("The pipeline execution ARN value is "+pipelineExecutionARN);
        SagemakerWorkflow.waitForPipelineExecution(sageMakerClient, pipelineExecutionARN);
        System.out.println("Getting output results "+bucketName);
        SagemakerWorkflow.getOutputResults(s3Client, bucketName);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("The pipeline has completed. To view the pipeline and runs " +
            "in SageMaker Studio, follow these instructions:" +
            "\nhttps://docs.aws.amazon.com/sagemaker/latest/dg/pipelines-studio.html");
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("Do you want to delete the AWS resources used in this Workflow? (y/n)");
        Scanner in = new Scanner(System.in);
        String delResources = in.nextLine();
        if (delResources.compareTo("y") == 0) {
            System.out.println("Lets clean up the AWS resources. Wait 30 seconds");
            TimeUnit.SECONDS.sleep(30);
            SagemakerWorkflow.deleteEventSourceMapping(lambdaClient);
            SagemakerWorkflow.deleteSQSQueue(sqsClient, queueName);
            SagemakerWorkflow.listBucketObjects(s3Client, bucketName);
            SagemakerWorkflow.deleteBucket(s3Client, bucketName);
            SagemakerWorkflow.deleteLambdaFunction(lambdaClient, functionName);
            SagemakerWorkflow.deleteLambdaRole(iam, lambdaRoleName);
            SagemakerWorkflow.deleteSagemakerRole(iam, sageMakerRoleName);
            SagemakerWorkflow.deletePipeline(sageMakerClient, pipelineName);
        } else {
            System.out.println("The AWS Resources were not deleted!");
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("SageMaker pipeline scenario is complete.");
        System.out.println(DASHES);
    }
}
