// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3.express;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;
import software.amazon.awssdk.services.ec2.model.CreateVpcEndpointRequest;
import software.amazon.awssdk.services.ec2.model.CreateVpcEndpointResponse;
import software.amazon.awssdk.services.ec2.model.CreateVpcRequest;
import software.amazon.awssdk.services.ec2.model.CreateVpcResponse;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.waiters.Ec2Waiter;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyRequest;
import software.amazon.awssdk.services.iam.model.CreateAccessKeyResponse;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.IntStream;

public class S3DirectoriesScenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static final Logger logger = LoggerFactory.getLogger(S3DirectoriesScenario.class);
    static Scanner scanner = new Scanner(System.in);

    private static S3AsyncClient mS3RegularClient;
    private static S3AsyncClient mS3ExpressClient;

    private IamClient iam;

    private static String mdirectoryBucketName;
    private static String mregularBucketName;

    private static String stackName = "cfn-stack-s3-express-basics--" + UUID.randomUUID();

    private static String regularUser = "";
    private static String vpcId = "";
    private static String expressUser = "";

    private static String vpcEndpointId = "";

    private static S3DirectoriesActions s3DirectoriesActions = new S3DirectoriesActions();

    public static void main(String[] args) {
        s3ExpressScenario();
    }

    // Runs the scenario.
    private static void s3ExpressScenario() {
        logger.info(DASHES);
        logger.info("Welcome to the Amazon S3 Express Basics demo using AWS SDK for Java V2");
        logger.info("""
            Let's get started! First, please note that S3 Express One Zone works best when working within the AWS infrastructure,
            specifically when working in the same Availability Zone. To see the best results in this example and when you implement
            Directory buckets into your infrastructure, it is best to put your compute resources in the same AZ as your Directory
            bucket.
            """);
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        // Create an optional VPC and create 2 IAM users.
        UserNames userNames = createVpcUsers();
        String expressUserName = userNames.getExpressUserName();
        String regularUserName = userNames.getRegularUserName();

        //  Set up two S3 clients, one regular and one express,
        //  and two buckets, one regular and one express.
        setupClientsAndBuckets(expressUserName, regularUserName);

        // Create an S3 session for the express S3 client and add objects to the buckets.
        logger.info("Create an S3 session for the express S3 client and add objects to the buckets");
        waitForInputToContinue(scanner);
        String bucketObject = createSessionAddObjects();

        // Demonstrate performance differences between regular and express buckets.
        demonstratePerformance(bucketObject);

        // Populate the buckets to show the lexicographical difference between
        // regular and express buckets.
        showLexicographicalDifferences(bucketObject);

        logger.info("");
        logger.info("That's it for our tour of the basic operations for S3 Express One Zone.");
        logger.info("Would you like to cleanUp the AWS resources? (y/n): ");
        String response = scanner.next().trim().toLowerCase();
        if (response.equals("y")) {
            cleanUp();
        }
    }

    /*
      Delete resources created by this scenario.
    */
    private static void cleanUp() {
        if (mdirectoryBucketName != null) {
            s3DirectoriesActions.deleteBucketAndObjectsAsync(mS3ExpressClient, mdirectoryBucketName).join();
        }
        logger.info("Deleted directory bucket " + mdirectoryBucketName);
        mdirectoryBucketName = null;

        if (mregularBucketName != null) {
            s3DirectoriesActions.deleteBucketAndObjectsAsync(mS3RegularClient, mregularBucketName).join();
        }

        logger.info("Deleted regular bucket " + mregularBucketName);
        mregularBucketName = null;
        CloudFormationHelper.destroyCloudFormationStack(stackName);
    }

    private static void showLexicographicalDifferences(String bucketObject) {
        logger.info("""
            7. Populate the buckets to show the lexicographical difference.
            Now let's explore how Directory buckets store objects in a different 
            manner to regular buckets. The key is in the name 
            "Directory". Where regular buckets store their key/value pairs in a 
            flat manner, Directory buckets use actual directories/folders. 
            This allows for more rapid indexing, traversing, and therefore 
            retrieval times! 
                        
            The more segmented your bucket is, with lots of 
            directories, sub-directories, and objects, the more efficient it becomes. 
            This structural difference also causes ListObjects to behave differently, 
            which can cause unexpected results. Let's add a few more 
            objects with layered directories to see how the output of 
            ListObjects changes.
            """);

        waitForInputToContinue(scanner);

        //  Populate a few more files in each bucket so that we can use
        //  ListObjects and show the difference.
        String otherObject = "other/" + bucketObject;
        String altObject = "alt/" + bucketObject;
        String otherAltObject = "other/alt/" + bucketObject;

        try {
            s3DirectoriesActions.putObjectAsync(mS3RegularClient, mregularBucketName, otherObject, "").join();
            s3DirectoriesActions.putObjectAsync(mS3ExpressClient, mdirectoryBucketName, otherObject, "").join();
            s3DirectoriesActions.putObjectAsync(mS3RegularClient, mregularBucketName, altObject, "").join();
            s3DirectoriesActions.putObjectAsync(mS3ExpressClient, mdirectoryBucketName, altObject, "").join();
            s3DirectoriesActions.putObjectAsync(mS3RegularClient, mregularBucketName, otherAltObject, "").join();
            s3DirectoriesActions.putObjectAsync(mS3ExpressClient, mdirectoryBucketName, otherAltObject, "").join();

        } catch (CompletionException e) {
            logger.error("Async operation failed: {} ", e.getCause().getMessage());
            return;
        }

        try {
            // List objects in both S3 buckets.
            List<String> dirBucketObjects = s3DirectoriesActions.listObjectsAsync(mS3ExpressClient, mdirectoryBucketName).join();
            List<String> regBucketObjects = s3DirectoriesActions.listObjectsAsync(mS3RegularClient, mregularBucketName).join();

            logger.info("Directory bucket content");
            for (String obj : dirBucketObjects) {
                logger.info(obj);
            }

            logger.info("Regular bucket content");
            for (String obj : regBucketObjects) {
                logger.info(obj);
            }
        } catch (CompletionException e) {
            logger.error("Async operation failed: {} ", e.getCause().getMessage());
            return;
        }

        logger.info("""
            Notice how the normal bucket lists objects in lexicographical order, while the directory bucket does not. This is 
            because the normal bucket considers the whole "key" to be the object identifier, while the directory bucket actually 
            creates directories and uses the object "key" as a path to the object.
            """);
        waitForInputToContinue(scanner);
    }

    /**
     * Demonstrates the performance difference between downloading an object from a directory bucket and a regular bucket.
     *
     * <p>This method:
     * <ul>
     *     <li>Prompts the user to choose the number of downloads (default is 1,000).</li>
     *     <li>Downloads the specified object from the directory bucket and measures the total time.</li>
     *     <li>Downloads the same object from the regular bucket and measures the total time.</li>
     *     <li>Compares the time differences and prints the results.</li>
     * </ul>
     *
     * <p>Note: The performance difference will be more pronounced if this example is run on an EC2 instance
     * in the same Availability Zone as the buckets.
     *
     * @param bucketObject the name of the object to download
     */
    private static void demonstratePerformance(String bucketObject) {
        logger.info("6. Demonstrate performance difference.");
        logger.info("""
            Now, let's do a performance test. We'll download the same object from each 
            bucket 'downloads' times and compare the total time needed. Note: 
            the performance difference will be much more pronounced if this
            example is run in an EC2 instance in the same Availability Zone as 
            the bucket.
            """);
        waitForInputToContinue(scanner);

        int downloads = 1000; // Default value
        logger.info("The number of downloads of the same object for this example is set at " + downloads + ".");

        // Ask if the user wants to download a different number.
        logger.info("Would you like to download a different number? (y/n): ");
        String response = scanner.next().trim().toLowerCase();
        if (response.equals("y")) {
            int maxDownloads = 1_000_000;

            // Ask for a valid number of downloads.
            while (true) {
                logger.info("Enter a number between 1 and " + maxDownloads + " for the number of downloads: ");
                if (scanner.hasNextInt()) {
                    downloads = scanner.nextInt();
                    if (downloads >= 1 && downloads <= maxDownloads) {
                        break;
                    } else {
                        logger.info("Please enter a number between 1 and " + maxDownloads + ".");
                    }
                } else {
                    logger.info("Invalid input. Please enter a valid integer.");
                    scanner.next();
                }
            }

            logger.info("You have chosen to download {}  items.", downloads);
        } else {
            logger.info("No changes made. Using default downloads: {}", downloads);
        }
        // Simulating the download process for the Directory bucket.
        logger.info("Downloading from the Directory bucket.");
        long directoryTimeStart = System.nanoTime();
        for (int index = 0; index < downloads; index++) {
            if (index % 10 == 0) {
                logger.info("Download " + index + " of " + downloads);
            }

            try {
                // Get the object from the Directory bucket.
                s3DirectoriesActions.getObjectAsync(mS3ExpressClient, mdirectoryBucketName, bucketObject).join();
            } catch (CompletionException e) {
                logger.error("Async operation failed: {} ", e.getCause().getMessage());
                return;
            }
        }

        long directoryTimeDifference = System.nanoTime() - directoryTimeStart;

        // Simulating the download process for the normal bucket.
        logger.info("Downloading from the regular bucket.");
        long normalTimeStart = System.nanoTime();
        for (int index = 0; index < downloads; index++) {
            if (index % 10 == 0) {
                logger.info("Download " + index + " of " + downloads);
            }

            // Get the object from the normal bucket.
            s3DirectoriesActions.getObjectAsync(mS3RegularClient, mregularBucketName, bucketObject).join();
        }

        long normalTimeDifference = System.nanoTime() - normalTimeStart;
        logger.info("The directory bucket took " + directoryTimeDifference + " nanoseconds, while the normal bucket took " + normalTimeDifference + " nanoseconds.");
        long difference = normalTimeDifference - directoryTimeDifference;
        logger.info("That's a difference of " + difference + " nanoseconds, or");
        logger.info(difference / 1_000_000_000.0 + " seconds.");

        if (difference < 0) {
            logger.info("The directory buckets were slower. This can happen if you are not running on the cloud within a VPC.");
        }
        waitForInputToContinue(scanner);
    }

    private static String createSessionAddObjects() {
        logger.info("""    
            5. Create an object and copy it over.
            We'll create a basic object consisting of some text and upload it to the 
            normal bucket. 
            Next we'll copy the object into the Directory bucket using the regular client. 
            This works fine because copy operations are not restricted for Directory buckets.
            """);
        waitForInputToContinue(scanner);

        String bucketObject = "basic-text-object.txt";
        s3DirectoriesActions.putObjectAsync(mS3RegularClient, mregularBucketName, bucketObject, "Look Ma, I'm a bucket!").join();
        s3DirectoriesActions.createSessionAsync(mS3ExpressClient, mdirectoryBucketName).join();

        // Copy the object to the destination S3 bucket.
        s3DirectoriesActions.copyObjectAsync(mS3ExpressClient, mregularBucketName, bucketObject, mdirectoryBucketName, bucketObject).join();

        logger.info(""" 
            It worked! It's important to remember the user permissions when interacting with 
            Directory buckets. Instead of validating permissions on every call as 
            normal buckets do, Directory buckets utilize the user credentials and session 
            token to validate. This allows for much faster connection speeds on every call. 
            For single calls, this is low, but for many concurrent calls 
            this adds up to a lot of time saved.
            """);
        waitForInputToContinue(scanner);
        return bucketObject;
    }

    /**
     * Creates VPC users for the S3 Express One Zone scenario.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Optionally creates a new VPC and VPC Endpoint if the application is running in an EC2 instance in the same Availability Zone as the Directory buckets.</li>
     *     <li>Creates two IAM users: one with S3 Express One Zone permissions and one without.</li>
     * </ol>
     *
     * @return a {@link UserNames} object containing the names of the created IAM users
     */
    /**
     * Creates VPC users for the S3 Express One Zone scenario.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Optionally creates a new VPC and VPC Endpoint if the application is running in an EC2 instance in the same Availability Zone as the Directory buckets.</li>
     *     <li>Creates two IAM users: one with S3 Express One Zone permissions and one without.</li>
     * </ol>
     *
     * @return a {@link UserNames} object containing the names of the created IAM users
     */
    public static UserNames createVpcUsers() {
        /*
        Optionally create a VPC.
        Create two IAM users, one with S3 Express One Zone permissions and one without.
        */
        logger.info("""
            1. First, we'll set up a new VPC and VPC Endpoint if this program is running in an EC2 instance in the same AZ as your\s
            Directory buckets will be. Are you running this in an EC2 instance located in the same AZ as your intended Directory buckets?
            """);

        logger.info("Do you want to setup a VPC Endpoint? (y/n)");
        String endpointAns = scanner.nextLine().trim();
        if (endpointAns.equalsIgnoreCase("y")) {
            logger.info("""
                    "Great! Let's set up a VPC, retrieve the Route Table from it, and create a VPC Endpoint to connect the S3 Client to."
                """);

            setupVPC();
            waitForInputToContinue(scanner);
        } else {
            logger.info("Skipping the VPC setup. Don't forget to use this in production!");
        }
        logger.info("""            
            2. Policies, users, and roles with CDK.
            Now, we'll set up some policies, roles, and a user. This user will 
            only have permissions to do S3 Express One Zone actions.
            """);

        waitForInputToContinue(scanner);
        logger.info("Use AWS CloudFormation to create IAM roles that is required for this scenario.");
        CloudFormationHelper.deployCloudFormationStack(stackName);
        Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputsAsync(stackName).join();

        // Create two users required for this scenario.
        regularUser = stackOutputs.get("RegularUser");
        expressUser = stackOutputs.get("ExpressUser");

        UserNames names = new UserNames();
        names.setRegularUserName(regularUser);
        names.setExpressUserName(expressUser);
        return names;
    }

    /**
     * Sets up a Virtual Private Cloud (VPC) in AWS.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Creates a VPC with a CIDR block of "10.0.0.0/16".</li>
     *   <li>Waits for the VPC to become available.</li>
     *   <li>Retrieves the route table associated with the VPC.</li>
     *   <li>Creates a VPC endpoint for the S3 service in the VPC.</li>
     * </ol>
     * <p>
     * The method uses the AWS SDK for Java to interact with the EC2 service.
     *
     * @throws RuntimeException if the VPC wait fails
     * @throws Ec2Exception     if there is an error creating the VPC endpoint
     */
    private static void setupVPC() {
        /*
            CIDR (Classless Inter-Domain Routing) is a notation used to
            define IP address ranges in AWS VPC and EC2 networking.
            It determines the network size and available IP addresses in a
            given range.
         */
        String cidr = "10.0.0.0/16";
        CreateVpcRequest vpcRequest = CreateVpcRequest.builder()
            .cidrBlock(cidr)
            .build();

        CreateVpcResponse vpcResponse = getEC2Client().createVpc(vpcRequest);
        vpcId = vpcResponse.vpc().vpcId();
        try (Ec2Waiter waiter = getEC2Client().waiter()) {
            DescribeVpcsRequest request = DescribeVpcsRequest.builder()
                .vpcIds(vpcId)
                .build();

            waiter.waitUntilVpcAvailable(request);
            logger.info("Created VPC {}",vpcId);
        } catch (Ec2Exception ex) {
            throw new RuntimeException("VPC wait failed: " + ex.getMessage(), ex);
        }

        try {
            Filter filter = Filter.builder()
                .name("vpc-id")
                .values(vpcId)
                .build();

            DescribeRouteTablesRequest describeRouteTablesRequest = DescribeRouteTablesRequest.builder()
                .filters(filter)
                .build();

            DescribeRouteTablesResponse routeTablesResponse = getEC2Client().describeRouteTables(describeRouteTablesRequest);
            String routeTableId = routeTablesResponse.routeTables().get(0).routeTableId();
            Region region = getEC2Client().serviceClientConfiguration().region();
            String serviceName = String.format("com.amazonaws.%s.s3express", region.id());

            CreateVpcEndpointRequest endpointRequest = CreateVpcEndpointRequest.builder()
                .vpcId(vpcId)
                .routeTableIds(routeTableId)
                .serviceName(serviceName)
                .build();

            CreateVpcEndpointResponse vpcEndpointResponse = getEC2Client().createVpcEndpoint(endpointRequest);
            vpcEndpointId = vpcEndpointResponse.vpcEndpoint().vpcEndpointId();

        } catch (Ec2Exception ex) {
            logger.error(
                "Couldn't create the vpc endpoint. Here's why: %s",
                ex.getCause()
            );
        }
    }

    /**
     * Sets up the necessary clients and buckets for the S3 Express service.
     *
     * @param expressUserName the username for the user with S3 Express permissions
     * @param regularUserName the username for the user with regular S3 permissions
     */
    public static void setupClientsAndBuckets(String expressUserName, String regularUserName) {
        Scanner locscanner = new Scanner(System.in);  // Open the scanner here
        CreateAccessKeyResponse keyResponse = createAccessKey(regularUserName);
        String accessKeyIdforRegUser = keyResponse.accessKey().accessKeyId();
        String secretAccessforRegUser = keyResponse.accessKey().secretAccessKey();

        CreateAccessKeyResponse keyResponseExpress = createAccessKey(expressUserName);
        String accessKeyIdforExpressUser = keyResponseExpress.accessKey().accessKeyId();
        String secretAccessforExpressUser = keyResponseExpress.accessKey().secretAccessKey();

        // Create an additional client using the credentials
        // with S3 Express permissions.
        logger.info("""            
            3. Create an additional client using the credentials with S3 Express permissions. This client is created with the 
            credentials associated with the user account with the S3 Express policy attached, so it can perform S3 Express operations.
            """);
        waitForInputToContinue(locscanner);

        // Populate the two S3 data member clients.
        mS3RegularClient = createS3ClientWithAccessKeyAsync(accessKeyIdforRegUser, secretAccessforRegUser).join();
        mS3ExpressClient = createS3ClientWithAccessKeyAsync(accessKeyIdforExpressUser, secretAccessforExpressUser).join();
        logger.info("""
            All the roles and policies were created and attached to the user. Then a new S3 Client were created using 
            that user's credentials. We can now use this client to make calls to S3 Express operations. Keeping permissions in mind
            (and adhering to least-privilege) is crucial to S3 Express.
              """);
        waitForInputToContinue(locscanner);

        logger.info("""
            4. Create two buckets.
            Now we will create a Directory bucket which is the linchpin of the S3 Express One Zone service. Directory buckets 
            behave in different ways from regular S3 buckets which we will explore here. We'll also create a normal bucket, put 
            an object into the normal bucket, and copy it over to the Directory bucket.
            """);

        logger.info("""
            Now, let's choose an availability zone for the Directory bucket. We'll choose one 
            that is supported.
            """);
        selectAvailabilityZoneId(String.valueOf(Region.US_EAST_1));
        String regularBucketName = "reg-bucket-" + System.currentTimeMillis();

        logger.info("""
            Now, let's create the actual Directory bucket, as well as a regular 
            bucket."
             """);
        String directoryBucketName = "test-bucket-" + System.currentTimeMillis() + "--usw2-az1--x-s3";
        String zone = "usw2-az1";
        s3DirectoriesActions.createDirectoryBucketAsync(mS3ExpressClient, directoryBucketName, zone).join();
        logger.info("Created directory bucket, " + directoryBucketName);

        // Assign to the data member.
        mdirectoryBucketName = directoryBucketName;

        s3DirectoriesActions.createBucketAsync(mS3RegularClient, regularBucketName).join();
        logger.info("Created regular bucket, " + regularBucketName);
        mregularBucketName = regularBucketName;
        logger.info("Great! Both buckets were created.");
        waitForInputToContinue(locscanner);
    }

    /**
     * Selects an availability zone ID based on the specified AWS region.
     *
     * @param region The AWS region to retrieve the availability zones from.
     * @return A map containing the selected availability zone details, including the zone name, zone ID, region name, and state.
     */
    public static Map<String, Object> selectAvailabilityZoneId(String region) {
        Ec2Client ec2Client = Ec2Client.create();

        // Define filter for region
        Filter myFilter = Filter.builder()
            .name("region-name")
            .values(region)
            .build();

        // Request available zones
        DescribeAvailabilityZonesRequest zonesRequest = DescribeAvailabilityZonesRequest.builder()
            .filters(myFilter)
            .build();
        DescribeAvailabilityZonesResponse response = ec2Client.describeAvailabilityZones(zonesRequest);
        List<AvailabilityZone> zonesList = response.availabilityZones();

        if (zonesList.isEmpty()) {
            logger.info("No availability zones found.");
            return null;
        }

        // Extract zone names
        List<String> zoneNames = zonesList.stream()
            .map(AvailabilityZone::zoneName)
            .toList();

        // Prompt user to select an availability zone
        Scanner scanner = new Scanner(System.in);
        int index = -1;

        while (index < 0 || index >= zoneNames.size()) {
            logger.info("Select an availability zone:");
            IntStream.range(0, zoneNames.size()).forEach(i ->
                System.out.println(i + ": " + zoneNames.get(i))
            );

            logger.info("Enter the number corresponding to your choice: ");
            if (scanner.hasNextInt()) {
                index = scanner.nextInt();
            } else {
                scanner.next(); // Consume invalid input
            }
        }

        AvailabilityZone selectedZone = zonesList.get(index);
        logger.info("You selected: " + selectedZone.zoneName());

        // Convert selected AvailabilityZone to a Map<String, Object>
        Map<String, Object> selectedZoneMap = new HashMap<>();
        selectedZoneMap.put("ZoneName", selectedZone.zoneName());
        selectedZoneMap.put("ZoneId", selectedZone.zoneId());
        selectedZoneMap.put("RegionName", selectedZone.regionName());
        selectedZoneMap.put("State", selectedZone.stateAsString());
        return selectedZoneMap;
    }

    /*
        Creates an S3 client with access key credentials.
        :param access_key: The access key for the user.
        :return: The S3 Express One Zone client.

     */
    private static CompletableFuture<S3AsyncClient> createS3ClientWithAccessKeyAsync(String accessKeyId, String secretAccessKey) {
        return CompletableFuture.supplyAsync(() -> {
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
            return S3AsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.US_WEST_2)
                .build();
        });
    }


    private static CreateAccessKeyResponse createAccessKey(String userName) {
        CreateAccessKeyRequest request = CreateAccessKeyRequest.builder()
            .userName(userName)
            .build();

        try {
            return getIAMClient().createAccessKey(request);

        } catch (IamException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }


    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            logger.info("");
            logger.info("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                logger.info("Continuing with the program...");
                logger.info("");
                break;
            } else {
                logger.info("Invalid input. Please try again.");
            }
        }
    }

    private static IamClient getIAMClient() {
        return IamClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }

    private static Ec2Client getEC2Client() {
        return Ec2Client.builder()
            .region(Region.US_EAST_1)
            .build();
    }
}
