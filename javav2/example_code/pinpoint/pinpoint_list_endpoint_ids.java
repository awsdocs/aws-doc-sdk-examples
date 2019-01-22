/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[pinpoint_list_endpoint_ids demonstrates how to produce a list of endpoint IDs that are associated with an Amazon Pinpoint project/application. This code example only works if you've already exported a list of endpoints by using the pinpoint_export_endpoints example.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[CreateExportJob]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-08-07]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_list_endpoint_ids.complete]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class ListEndpointIds {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "ListEndpointIds - Prints all of the endpoint IDs that belong to an Amazon " +
                "Pinpoint application. This program performs the following steps:\n\n" +

                "1) Exports the endpoints to an Amazon S3 bucket.\n" +
                "2) Downloads the exported endpoints files from Amazon S3.\n" +
                "3) Parses the endpoints files to obtain the endpoint IDs and prints them.\n" +
                "4) Cleans up by deleting the objects that Amazon Pinpoint created in the S3 " +
                "bucket.\n\n" +

                "Usage: ListEndpointIds <applicationId> <s3BucketName> <iamExportRoleArn>\n\n" +

                "Where:\n" +
                "  applicationId - The ID of the Amazon Pinpoint application that has the " +
                "endpoint.\n" +
                "  s3BucketName - The name of the Amazon S3 bucket to export the JSON file to. If" +
                " the bucket doesn't exist, a new bucket is created.\n" +
                "  iamExportRoleArn - The ARN of an IAM role that grants Amazon Pinpoint write " +
                "permissions to the S3 bucket.";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String applicationId = args[0];
        String s3BucketName = args[1];
        String iamExportRoleArn = args[2];

        // Exports the endpoints to Amazon S3 and stores the keys of the new objects.
        List<String> objectKeys =
                ExportEndpoints.exportEndpointsToS3(s3BucketName, iamExportRoleArn, applicationId);

        // Filters the keys to only those objects that have the endpoint definitions.
        // These objects have the .gz extension.
        List<String> endpointFileKeys = objectKeys
                .stream()
                .filter(o -> o.endsWith(".gz"))
                .collect(Collectors.toList());

        // Gets the endpoint IDs from the exported endpoints files.
        List<String> endpointIds = getEndpointIds(s3BucketName, endpointFileKeys);

        System.out.println("Endpoint IDs:");

        for (String endpointId : endpointIds) {
            System.out.println("\t- " + endpointId);
        }

        // Deletes the objects that Amazon Pinpoint created in the S3 bucket.
        deleteS3Objects(s3BucketName, objectKeys);
    }

    private static List<String> getEndpointIds(String s3bucketName, List<String> endpointFileKeys) {

        List<String> endpointIds = new ArrayList<>();

        // Initializes the Amazon S3 client.
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        // Gets the endpoint IDs from the exported endpoints files.
        try {
            for (String key : endpointFileKeys) {
                S3Object endpointFile = s3Client.getObject(s3bucketName, key);
                endpointIds.addAll(getEndpointIdsFromFile(endpointFile));
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return endpointIds;
    }

    private static List<String> getEndpointIdsFromFile(S3Object endpointsFile) {

        List<String> endpointIdsFromFile = new ArrayList<>();

        // The Google Gson library is used to parse the exported endpoint JSON.
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        // Reads each endpoint entry in the file and adds the ID to the list.
        try (GZIPInputStream gzipInputStream =
                     new GZIPInputStream(endpointsFile.getObjectContent());
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(
                             gzipInputStream, "UTF-8"))) {
            String endpointString;
            while ((endpointString = reader.readLine()) != null) {
                JsonObject endpointJson = gson.fromJson(endpointString, JsonObject.class);
                endpointIdsFromFile.add(endpointJson
                        .getAsJsonPrimitive("Id")
                        .getAsString());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return endpointIdsFromFile;
    }

    private static void deleteS3Objects(String s3BucketName, List<String> keys) {

        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        String[] keysArray = keys.toArray(new String[keys.size()]);
        DeleteObjectsRequest request = new DeleteObjectsRequest(s3BucketName).withKeys(keysArray);

        System.out.println("Deleting the following Amazon S3 objects created by Amazon Pinpoint:");

        for (String key : keys) {
            System.out.println("\t- " + key);
        }

        try {
            s3Client.deleteObjects(request);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        System.out.println("Finished deleting objects.");
    }
}

// snippet-end:[pinpoint.java.pinpoint_list_endpoint_ids.complete]
