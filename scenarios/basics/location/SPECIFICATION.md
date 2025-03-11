# Specification for the AWS Location Service Scenario

## Overview

This SDK Basics scenario demonstrates how to interact with the AWS Location Service using an AWS SDK. It demonstrates various AWS Location tasks such as creating a map, creating a key, creating a tracker, and so on using `LocationAsyncClient`. 

This scenario uses the higher level `GeoPlacesAsyncClient` to perform additional location search and geocoding capabilities. Finally, it demonstrates how to clean up resources.

## Resources

This Basics scenario requires no additional resources.

## Hello AWS Location Service

This program is intended for users not familiar with the AWS Location Service to easily get up and running. The program uses `listGeofencesPaginator` to demonstrate how you can read through
Geofences information.

## Basics Scenario Program Flow

The AWS Location Service Basics scenario executes the following operations.

1. **Create a map**:
    - Description: Creates a map by invoking the `createMap` method.
    - Exception Handling: Check to see if a `ServiceQuotaExceededException` is thrown, which
      indicates that the operation was denied because the request would exceed the maximum quota. If the exception is thrown, display the information and end the program.

2. **Create an AWS Location API key**:
    - Description: Creates an API key required to embed a map in a web app or website by invoking the `createKey` method.
    - Exception Handling: Handle `ThrottlingException`, which occurs when request throttling is detected. If this exception is thrown, display the error message and terminate the program.

3. **Display Map URL**:
    - Description: Show the syntax of a MAP URL in the console. This uses the map name and key value.
    - Exception Handling: N/A.

4. **Create a geofence collection**:
    - Description: Create a geofence collection, which manages and stores geofences by invoking the `createGeofenceCollection` method.
    - Exception Handling: Check to see if an `ConflictException ` is
      thrown, which indicates that a conflict occurred. If the
      exception is thrown, display the message and end the program.

5. **Store a geofence geometry in a given geofence collection**:
    - Description: Store a geofence geometry in a given geofence collection by invoking the `putGeofence` method. Included in this call is how to constuct a polygon.
    - Exception Handling: Check to see if a `ValidationException ` is
      thrown due to an invalid polygon. If so, display the message and end the program.

6. **Create a tracker resource**:
    - Description: Create a tracker resource which lets you retrieve current and historical location of devices by invoking the `createTracker` method.
    - Exception Handling: Check to see if an `LocationException` is thrown. If
      so, display the message and end the program.

7. **Update the position of a device**:
    - Description: Update the position of a device in the location tracking system by invoking the`getDevicePosition` method.
    - Exception Handling: Check to see if a `LocationException` is
      thrown. If so, display the message and end the program

8. **Retrieve the most recent position**:
    - Description: Retrieve the most recent position update for a specified device by invoking the
      `getMatchingJob` method.
    - Exception Handling: Check to see if an `LocationException` is thrown. If
      so, display the message and end the program.

9. **Create a route calculator**:
    - Description: Create a route calculator by invoking the
      `createRouteCalculator` method.
    - Exception Handling: Check to see if an `LocationException` is thrown. If
      so, display the message and end the program.   

10. **Determine the distance between two cities and Vancouver**:
    - Description: Determine the distance between Seattle and Vancouver by invoking the `calculateRoute` method.
    - Exception Handling: Check to see if an `LocationException` is thrown. If so, display the message and end the program.  

11. **Use AWS Location Services higher level API**
    - Description: Use the `GeoPlacesAsyncClient`client to perform these tasks:
         - Reverse Geocoding (reverseGeocode): Converts geographic coordinates into addresses.
         - Place Search (searchText): Finds places based on search queries.
         - Nearby Search (searchNearby): Finds places near a specific location. 
    - Exception Handling: Check to see if an `GeoPlacesException` is thrown. If so, display the message and end the program.      

12. **Delete AWS resources**:
    - Description: Delete the various resources by invoking the corresponding delete methods.
    - Exception Handling: Check to see if an `ResourceNotFoundException` is thrown. If so, display the message and end the program.        

### Program execution

The following shows the output of the AWS Location Basics scenario in
the console.

```
AWS Location Service is a fully managed service offered by Amazon Web Services (AWS) that
provides location-based services for developers. This service simplifies
the integration of location-based features into applications, making it
easier to build and deploy location-aware applications.

The AWS Location Service offers a range of location-based services,
including:

Maps: The service provides access to high-quality maps, satellite imagery, 
and geospatial data from various providers, allowing developers to 
easily embed maps into their applications.

Tracking: The Location Service enables real-time tracking of mobile devices, 
assets, or other entities, allowing developers to build applications 
that can monitor the location of people, vehicles, or other objects.

Geocoding: The service provides the ability to convert addresses or 
location names into geographic coordinates (latitude and longitude), 
and vice versa, enabling developers to integrate location-based search 
and routing functionality into their applications.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Create a map
 An AWS Location map can enhance the user experience of your
 application by providing accurate and personalized location-based
 features. For example, you could use the geocoding capabilities to
 allow users to search for and locate businesses, landmarks, or
 other points of interest within a specific region.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The Map ARN is arn:aws:geo:us-east-1:814548047983:map/MyMap40

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Create an AWS Location API key
When you embed a map in a web app or website, the API key is
included in the map tile URL to authenticate requests. You can
restrict API keys to specific AWS Location operations (e.g., only
maps, not geocoding). API keys can expire, ensuring temporary
access control.

The API key was successfully created: arn:aws:geo:us-east-1:814548047983:api-key/MyLocationKey40

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Display Map URL
In order to get the MAP URL, you need to get the API Key value.
You can get the key value using the AWS Management Console under
Location Services. This operation cannot be completed using the
AWS SDK.

Embed this URL in your Web app: https://maps.geo.aws.amazon.com/maps/v0/maps/{MapName}/tiles/{z}/{x}/{y}?key={KeyValue}


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Create a geofence collection, which manages and stores geofences.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The ARN is arn:aws:geo:us-east-1:814548047983:geofence-collection/AWSLocationCollection40

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Store a geofence geometry in a given geofence collection.
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


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Successfully created geofence: geoId40

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
6. Create a tracker resource which lets you retrieve current and historical location of devices..

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The tracker ARN is arn:aws:geo:us-east-1:814548047983:tracker/geoTracker40

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Update the position of a device in the location tracking system.
The AWS location service does not enforce a strict format for deviceId, but it must:
  - Be a string (case-sensitive).
  - Be 1–100 characters long.
  - Contain only:
    - Alphanumeric characters (A-Z, a-z, 0-9)
    - Underscores (_)
    - Hyphens (-)
    - Be the same ID used when sending and retrieving positions.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

iPhone-112359 was updated in the location tracking system

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
8. Retrieve the most recent position update for a specified device..

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Device Position: [-122.4194, 37.7749]
Received at: 2025-03-11T18:02:27.401Z

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
9. Create a route calculator.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Route calculator created: arn:aws:geo:us-east-1:814548047983:route-calculator/AWSRouteCalc40

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
10. Determine the distance between Seattle and Vancouver using the route calculator.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Total Distance: 229.4919562976832 km
Estimated Duration by car is: 162.17861734523333 minutes

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
11. AWS Location Services exposes higher level APIs to perform additional operations.
This scenario will show use of the GeoPlacesClient that enables
location search and geocoding capabilities for your applications. 

We are going to use this client to perform these tasks:
 - Reverse Geocoding (reverseGeocode): Converts geographic coordinates into addresses.
 - Place Search (searchText): Finds places based on search queries.
 - Nearby Search (searchNearby): Finds places near a specific location.

First we will perform a Reverse Geocoding operation

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Use latitude 37.7749 and longitude -122.4194
The address is: FedEx Office, 1967 Market St, San Francisco, CA 94103, United States
Now we are going to perform a text search using coffee shop.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Found Place with id: AQAAAFUARI2OrflhvYzXbmuuOadKpTSJ6ynsZSLcXI2xbU2X2D5PdcHmlQ-jrs-hn-yhF7jOC9hMgrXFP5neo_lK480qGCAO-zdYrsKSBoWVm8DhRNFLF_Eyua-vUTfG9SGbSGJmUfwxSkPKIYRYOnRnocKOT6CsGt_h
Detailed Place Information:
Name: POINT_OF_INTEREST
Address: Cafe Creme, 50 Oak St, San Francisco, CA 94102-6011, United States
Food Types:
  - FoodType(LocalizedName=Thai, Id=thai, Primary=true)
-------------------------
Now we are going to perform a nearby Search.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Place Name: POINT_OF_INTEREST
Address: Limpar Cleaning Services, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Chuin Phang, Massage Practitioner, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Benjamin Franklin, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Elite Bridal Artists, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Miss Clean Scene, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: New Life Remodeling, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Parking Panda, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Inky Binky Bonky, Market St, San Francisco, CA 94103, United States
Distance: 2 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Sport-Tec, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Timberwood Tree Service, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Wonderbread5, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Weaving Stories Therapy, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Chef Greg / Personal Chef, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Illuminate Bodywork, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Book My Limo Trip, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Cardona Landscaping, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: C.H. Burnham Construction, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Edward Dougherty, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: Ripertonwest Consulting, Market St, San Francisco, CA 94103, United States
Distance: 3 meters
-------------------------
Place Name: POINT_OF_INTEREST
Address: FedEx Office, 1967 Market St, San Francisco, CA 94103, United States
Distance: 4 meters
-------------------------
--------------------------------------------------------------------------------
12. Delete the AWS Location Services resources.
Would you like to delete the AWS Location Services resources? (y/n)
y
The map MyMap40 was deleted.
The key MyLocationKey40 was deleted.
The geofence collection AWSLocationCollection40 was deleted.
The tracker geoTracker40 was deleted.
The route calculator AWSRouteCalc40 was deleted.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
 This concludes the AWS Location Service scenario.
--------------------------------------------------------------------------------

```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.

| action                 | metadata file                  metadata key                               |
|------------------------|---------------------------|--------------------------------------------|
| `createKey`            | location_metadata.yaml    |location_CreateKey                          |
| `createMap`            | location_metadata.yaml    |location_CreateMap                          |
| `createCollection`     | location_metadata.yaml    |location_CreateGeofenceCollection           |
| `putGeofence   `       | location_metadata.yaml    |location_PutGeofence                        |
| `createTracker`        | location_metadata.yaml    |location_CreateTracker                      |
| `updateDevicePosition` | location_metadata.yaml    |location_UpdateDevicePosition               |
| `getDevicePosition`    | location_metadata.yaml    |location_GetDevicePosition                  |
| `createRouteCalculator`| location_metadata.yaml    |location_CreateRouteCalculator              |
| `calcDistance `        | location_metadata.yaml    |location_CalcuateDistance                   |
| `deleteMap`            | location_metadata.yaml    |location_DeleteMap                          |
| `deleteKey`            | location_metadata.yaml    |location_DeleteKey                          |
| `deleteKey`            | location_metadata.yaml    |location_DeleteKey                          |
| `deleteKey`            | location_metadata.yaml    |location_DeleteKey                          |
| `deleteCollection`     | location_metadata.yaml    |location_deleteCollection                   |
| `deleteTracker`        | location_metadata.yaml    |location_DeleteTracker                      |
| `deleteTracker`        | location_metadata.yaml    |location_DeleteTracker                      |
| `deleteCalculator`     | location_metadata.yaml    |location_DeleteCalculator                   |
| `scenario`             | location_metadata.yaml    |location_Scenario                           |
| `hello`                |  location_metadata.yaml   |location_Hello                              |




