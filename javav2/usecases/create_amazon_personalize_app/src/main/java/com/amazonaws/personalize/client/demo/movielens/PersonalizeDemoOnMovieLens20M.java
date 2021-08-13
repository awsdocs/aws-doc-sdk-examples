/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.demo.movielens;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;

import com.amazonaws.personalize.client.datasets.DatasetProvider;
import com.amazonaws.personalize.client.datasets.DatasetProvider.DatasetType;
import com.amazonaws.personalize.client.datasets.MovieLensDatasetProvider;
import com.amazonaws.personalize.client.resource.CampaignManager;
import com.amazonaws.personalize.client.resource.DatasetGroupManager;
import com.amazonaws.personalize.client.resource.DatasetManager;
import com.amazonaws.personalize.client.resource.EventTrackerManager;
import com.amazonaws.personalize.client.resource.ResourceException;
import com.amazonaws.personalize.client.resource.SchemaManager;
import com.amazonaws.personalize.client.resource.SolutionManager;
import com.amazonaws.personalize.client.resource.SolutionVersionManager;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeevents.PersonalizeEventsClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.iam.IamClient;

public class PersonalizeDemoOnMovieLens20M {

    private static final String PREFIX = "your-project-name-here";  // replace with your project name.
    private static final String S3_BUCKET = PREFIX.toLowerCase() + "-tutorial-bucket";
    private static final Region region = Region.US_EAST_1; // change to the region where you want to create your resources


    public static void main(String[] args) throws Exception {

        // Pre-flight step - 1
        // Set credentials provider, s3 and personalize clients
        //AWSStaticCredentialsProvider cred = getCredentialsProvider();

        S3Client s3Client = S3Client.builder()
                .region(region)
                .build();

        PersonalizeEventsClient personalizeEventsClient = PersonalizeEventsClient.builder()
                .region(region)
                .build();
        PersonalizeClient personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();
        PersonalizeRuntimeClient personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
        IamClient iamClient = IamClient.builder()
                .region(Region.AWS_GLOBAL)
                .build();

        // Pre-flight step - 2
        // Identify your datasets and export them to S3 bucket
        DatasetProvider datasetProvider = new MovieLensDatasetProvider();
        datasetProvider.exportDatasetToS3(DatasetType.INTERACTIONS, s3Client, S3_BUCKET, true);


        // Pre-flight step - 3
        // Personalize needs the ability to assume Roles in AWS in order to have the permissions to execute certain tasks, the lines below grant that.
        DemoUtils.ensurePersonalizePermissionsOnS3Bucket(s3Client, S3_BUCKET);


        // Pre-flight step - 4
        // Ensure S3 bucket is accessible by Personalize
        String role = PREFIX + "-role";
        String roleArn = DemoUtils.createPersonalizeRole(iamClient, role);


        // Step 1
        // Create dataset group
        // Create dataset schemas
        // Create datasets
        String datasetGroupArn = createSchemaAndDatasets(personalizeClient, datasetProvider, roleArn);

        // step 2
        // create solution and solution version
        final String userPersonalizationRecipeArn = "arn:aws:personalize:::recipe/aws-user-personalization";
        final String userPersonalizationSolutionName = PREFIX + "-user-personalization-solution";
        final String awsUserPersonalizeSVArn = createSolutionAndSolutionVersion(personalizeClient, datasetGroupArn, userPersonalizationRecipeArn, userPersonalizationSolutionName);
        System.out.println("AWS User Personalization solution and solution version created");

        final String simsRecipeArn = "arn:aws:personalize:::recipe/aws-sims";
        final String simsSolutionName = PREFIX + "-sims-solution";
        final String simsSVArn = createSolutionAndSolutionVersion(personalizeClient, datasetGroupArn, simsRecipeArn, simsSolutionName);
        System.out.println("SIMS solution and solution version created");

        // step 3
        // setup campaign
        final String userPersonalizationCampaignName = PREFIX + "-user-personalization-campaign";
        final CampaignManager userPersonalizationCM = new CampaignManager(personalizeClient, userPersonalizationCampaignName, awsUserPersonalizeSVArn);
        final String userPersonalizationCampaignArn = userPersonalizationCM.createAndWaitForResource(true);
        System.out.println("AWS User Personalization campaign deployed");

        final String simsCampaignName = PREFIX + "-sims-campaign";
        final CampaignManager simsCM = new CampaignManager(personalizeClient, simsCampaignName, simsSVArn);
        final String simsCampaignArn = simsCM.createAndWaitForResource(true);
        System.out.println("SIMS campaign deployed");


        // Step 4
        // Create event tracker for real time events
        //AmazonPersonalizeEvents personalizeEvents = AmazonPersonalizeEventsClientBuilder.standard().withCredentials(cred).build();
        final String eventTrackerName = PREFIX + "-event-tracker";
        final EventTrackerManager etm = new EventTrackerManager(personalizeClient, eventTrackerName, datasetGroupArn);
        final String eventTackerArn = etm.createAndWaitForResource(true);
        final String eventTrackingId = etm.getTrackingId(eventTackerArn);
        System.out.println("Event tracker created");

        // step 5
        // create runtime client for demo
        runWebDemo(personalizeRuntimeClient, personalizeEventsClient, userPersonalizationCampaignArn, simsCampaignArn, eventTrackingId, datasetProvider);
    }

    private static void runWebDemo(PersonalizeRuntimeClient personalizeRuntimeClient,
                                   PersonalizeEventsClient personalizeEventsClient,
                                   String userPersonalizationCampaignArn,
                                   String simsCampaignArn,
                                   String eventTrackingId,
                                   DatasetProvider datasetProvider) throws Exception {

        RecommendationsInterface recommender = new AmazonPersonalizeRecommender(personalizeRuntimeClient, personalizeEventsClient,
                userPersonalizationCampaignArn, simsCampaignArn, eventTrackingId, datasetProvider);

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setWelcomeFiles(new String[]{"homepage.html"});
        resource_handler.setResourceBase("static/web/");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, new WebHandler(recommender)});
        server.setHandler(handlers);

        server.start();

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        }

        server.join();
    }

    private static String createSolutionAndSolutionVersion(PersonalizeClient personalizeClient, String datasetGroupArn, String recipeArn, String solutionName) throws IOException, ResourceException {

        final SolutionManager sm = new SolutionManager(personalizeClient, solutionName, datasetGroupArn, recipeArn);
        final String solutionArn = sm.createAndWaitForResource(true);
        System.out.println("Solution created!");

        final SolutionVersionManager svm = new SolutionVersionManager(personalizeClient, solutionName + "-v1", solutionArn);
        final String solutionVersionArn = svm.createAndWaitForResource(true);
        System.out.println("Solution Version Created!");

        return solutionVersionArn;

    }

    private static String createSchemaAndDatasets(PersonalizeClient personalizeClient, DatasetProvider datasetProvider, String roleArn) throws IOException, ResourceException {

        // Step 1: create interactions schema
        final String interactionSchemaName = PREFIX + "-interactions-schema";
        final String interactionsSchemaString = datasetProvider.getSchema(DatasetType.INTERACTIONS);
        final SchemaManager interactionSchemaManager = new SchemaManager(personalizeClient, interactionSchemaName, interactionsSchemaString);
        final String interactionSchema = interactionSchemaManager.createAndWaitForResource(true);
        System.out.println("Interactions Schema created!");


        // Step 2: create dataset group
        final String datasetGroupName = PREFIX + "-datasetgroup";
        final DatasetGroupManager datasetGroupManager = new DatasetGroupManager(personalizeClient, datasetGroupName);
        final String datasetGroupArn = datasetGroupManager.createAndWaitForResource(true);
        System.out.println("Dataset group created!");

        // Step 3: create interactions dataset
        final String interactionsDatasetName = PREFIX + "-dataset-interactions";
        final DatasetManager datasetManager = new DatasetManager(personalizeClient, interactionsDatasetName, datasetGroupArn, interactionSchema, "interactions");
        @SuppressWarnings("unused") final String interactionsDatasetArn = datasetManager.createAndWaitForResource(true);
        datasetManager.importDataset(roleArn, S3_BUCKET, datasetProvider.getS3Path(DatasetType.INTERACTIONS));
        System.out.println("Interactions dataset created!");

        return datasetGroupArn;

    }
}
