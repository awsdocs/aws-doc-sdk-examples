// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.location.scenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

// snippet-start:[location.java2.scenario.main]
public class LocationScenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static final Logger logger = LoggerFactory.getLogger(LocationScenario.class);
    static Scanner scanner = new Scanner(System.in);

    static LocationActions locationActions = new LocationActions() ;

    public static void main(String[] args) {
        String mapName = "ScottMap32";
        String keyName = "ScottApiKeyName32";
        String collectionName = "ScottCollection32";
        String geoId = "geoId32";
        String trackerName = "geoTracker32";
        String calculatorName = "ScottRouteCalc32";
        String deviceId = "iPhone-112356"; // Use the iPhone's identifier from Swift

        logger.info("""
            AWS Location Service is a fully managed service offered by Amazon Web Services (AWS) that
            provides location-based services for developers. This service simplifies
            the integration of location-based features into applications, making it
            easier to build and deploy location-aware applications.
                        
            The AWS Location Service offers a range of location-based services,
            including:
                        
            Maps: The service provides access to high-quality maps, satellite imagery,\s
            and geospatial data from various providers, allowing developers to\s
            easily embed maps into their applications.
                        
            Tracking: The Location Service enables real-time tracking of mobile devices,\s
            assets, or other entities, allowing developers to build applications\s
            that can monitor the location of people, vehicles, or other objects.
                        
            Geocoding: The service provides the ability to convert addresses or\s
            location names into geographic coordinates (latitude and longitude),\s
            and vice versa, enabling developers to integrate location-based search\s
            and routing functionality into their applications.
            """);
        waitForInputToContinue(scanner);
        logger.info(DASHES);


        logger.info(DASHES);
        logger.info("1. Create a map");
        logger.info("""
        An AWS Location map can enhance the user experience of your
        application by providing accurate and personalized location-based
        features. For example, you could use the geocoding capabilities to
        allow users to search for and locate businesses, landmarks, or
        other points of interest within a specific region.
       """);

        waitForInputToContinue(scanner);
        String mapArn = locationActions.createMap(mapName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("2. Create an AWS Location API key");
        logger.info("""
            When you embed a map in a web app or website, the API key is
            included in the map tile URL to authenticate requests. You can
            restrict API keys to specific AWS Location operations (e.g., only
            maps, not geocoding). API keys can expire, ensuring temporary
            access control.
            """);

        locationActions.createKey(keyName, mapArn).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("3. Display Map URL");
        logger.info("""
            In order to get the MAP URL, you need to get the API Key value.
            You can get the key value using the AWS Management Console under
            Location Services. This operation cannot be completed using the
            AWS SDK.
            """);
        String mapUrl = "https://maps.geo.aws.amazon.com/maps/v0/maps/{MapName}/tiles/{z}/{x}/{y}?key={KeyValue}" ;
        logger.info("Embed this URL in your Web app: " + mapUrl);
        logger.info("");
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. Create a geofence collection, which manages and stores geofences.");
        waitForInputToContinue(scanner);
        locationActions.createGeofenceCollection(collectionName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("5. Store a geofence geometry in a given geofence collection.");
        logger.info("""
            An AWS Location geofence is a virtual boundary that defines a geographic area
            on a map. It is a useful feature for tracking the location of
            assets or monitoring the movement of objects within a specific region.
            
            To define a geofence, you need to specify the coordinates of a
            polygon that represents the area of interest. The polygon must be
            defined in a counter-clockwise direction, meaning that the points of
            the polygon must be listed in a counter-clockwise order.
            
            This is a requirement for the AWS Location service to correctly
            interpret the geofence and ensure that the location data is
            accurately processed within the defined area.
            """);

        waitForInputToContinue(scanner);
        locationActions.putGeofence(collectionName, geoId).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("6. List geofences stored in a given geofence collection.");
        waitForInputToContinue(scanner);
        locationActions.listGeofences(collectionName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info("7. Create a tracker resource which lets you retrieve current and historical location of devices..");
        waitForInputToContinue(scanner);
        locationActions.createTracker(trackerName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("8. Update the position of a device in the location tracking system.");
        logger.info("""
            The AWS location service does not enforce a strict format for deviceId, but it must:
              - Be a string (case-sensitive).
              - Be 1–100 characters long.
              - Contain only:
                - Alphanumeric characters (A-Z, a-z, 0-9)
                - Underscores (_)
                - Hyphens (-)
                - Be the same ID used when sending and retrieving positions.
            """);

        waitForInputToContinue(scanner);
        locationActions.updateDevicePosition(trackerName, deviceId).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info("9. Retrieve the most recent position update for a specified device..");
        waitForInputToContinue(scanner);
        locationActions.getDevicePosition(trackerName, deviceId).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info("10. Create a route calculator.");
        waitForInputToContinue(scanner);
        locationActions.createRouteCalculator(calculatorName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info("11. Determine the distance between Seattle and Vancouver..");
        waitForInputToContinue(scanner);
        locationActions.calcDistanceAsync(calculatorName).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info("12. Delete the AWS Location Services resources.");
        logger.info("Would you like to delete the AWS Location Services resources? (y/n)");
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            locationActions.createRouteCalculator(calculatorName).join();
        } else {
            logger.info("The AWS resources will not be deleted");
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info(" This concluded the AWS Location Service scenario.");
        logger.info(DASHES);
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
}
// snippet-end:[location.java2.scenario.main]