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

// snippet-sourcedescription:[pinpoint_export_endpoints demonstrates how to export information about several existing endpoints to an Amazon S3 bucket that you specify.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[CreateExportJob]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-08-07]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_export_endpoints.complete]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ExportEndpoints {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "ExportEndpoints - Downloads endpoints from an Amazon Pinpoint application by: \n" +
                "1.) Exporting the endpoint definitions to an Amazon S3 bucket. \n" +
                "2.) Downloading the endpoint definitions to the specified file path.\n\n" +

                "Usage: ExportEndpoints <s3BucketName> <iamExportRoleArn> <downloadDirectory> " +
                "<applicationId>\n\n" +

                "Where:\n" +
                "  s3BucketName - The name of the Amazon S3 bucket to export the endpoints files " +
                "to. If the bucket doesn't exist, a new bucket is created.\n" +
                "  iamExportRoleArn - The ARN of an IAM role that grants Amazon Pinpoint write " +
                "permissions to the S3 bucket.\n" +
                "  downloadDirectory - The directory to download the endpoints files to.\n" +
                "  applicationId - The ID of the Amazon Pinpoint application that has the " +
                "endpoints.";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String s3BucketName = args[0];
        String iamExportRoleArn = args[1];
        String downloadDirectory = args[2];
        String applicationId = args[3];

        // Exports the endpoints to Amazon S3 and stores the keys of the new objects.
        List<String> objectKeys =
                exportEndpointsToS3(s3BucketName, iamExportRoleArn, applicationId);

        // Filters the keys to only those objects that have the endpoint definitions.
        // These objects have the .gz extension.
        List<String> endpointFileKeys = objectKeys
                .stream()
                .filter(o -> o.endsWith(".gz"))
                .collect(Collectors.toList());

        // Downloads the exported endpoints files to the specified directory.
        downloadFromS3(s3BucketName, endpointFileKeys, downloadDirectory);
    }

    public static List<String> exportEndpointsToS3(String s3BucketName, String iamExportRoleArn,
                                                   String applicationId) {

        // The S3 path that Amazon Pinpoint exports the endpoints to.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm:ss.SSS_z");
        String endpointsKeyPrefix = "exports/" + applicationId + "_" + dateFormat.format(new Date
                ());
        String s3UrlPrefix = "s3://" + s3BucketName + "/" + endpointsKeyPrefix + "/";

        // Defines the export job that Amazon Pinpoint runs.
        ExportJobRequest exportJobRequest = new ExportJobRequest()
                .withS3UrlPrefix(s3UrlPrefix)
                .withRoleArn(iamExportRoleArn);
        CreateExportJobRequest createExportJobRequest = new CreateExportJobRequest()
                .withApplicationId(applicationId)
                .withExportJobRequest(exportJobRequest);

        // Initializes the Amazon Pinpoint client.
        AmazonPinpoint pinpointClient = AmazonPinpointClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();

        System.out.format("Exporting endpoints from Amazon Pinpoint application %s to Amazon S3 " +
                "bucket %s . . .\n", applicationId, s3BucketName);

        List<String> objectKeys = null;

        try {
            // Runs the export job with Amazon Pinpoint.
            CreateExportJobResult exportResult =
                    pinpointClient.createExportJob(createExportJobRequest);

            // Prints the export job status to the console while the job runs.
            String jobId = exportResult.getExportJobResponse().getId();
            printExportJobStatus(pinpointClient, applicationId, jobId);

            // Initializes the Amazon S3 client.
            AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

            // Lists the objects created by Amazon Pinpoint.
            objectKeys = s3Client
                    .listObjectsV2(s3BucketName, endpointsKeyPrefix)
                    .getObjectSummaries()
                    .stream()
                    .map(S3ObjectSummary::getKey)
                    .collect(Collectors.toList());

        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return objectKeys;
    }

    private static void printExportJobStatus(AmazonPinpoint pinpointClient,
                                             String applicationId, String jobId) {

        GetExportJobResult getExportJobResult;
        String jobStatus;

        try {
            // Checks the job status until the job completes or fails.
            do {
                getExportJobResult = pinpointClient.getExportJob(new GetExportJobRequest()
                        .withJobId(jobId)
                        .withApplicationId(applicationId));
                jobStatus = getExportJobResult.getExportJobResponse().getJobStatus();
                System.out.format("Export job %s . . .\n", jobStatus.toLowerCase());
                TimeUnit.SECONDS.sleep(3);
            } while (!jobStatus.equals("COMPLETED") && !jobStatus.equals("FAILED"));

            if (jobStatus.equals("COMPLETED")) {
                System.out.println("Finished exporting endpoints.");
            } else {
                System.err.println("Failed to export endpoints.");
                System.exit(1);
            }

            // Checks for entries that failed to import.
            // getFailures provides up to 100 of the first failed entries for the job, if any exist.
            List<String> failedEndpoints = getExportJobResult.getExportJobResponse().getFailures();
            if (failedEndpoints != null) {
                System.out.println("Failed to import the following entries:");
                for (String failedEndpoint : failedEndpoints) {
                    System.out.println(failedEndpoint);
                }
            }
        } catch (AmazonServiceException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void downloadFromS3(String s3BucketName, List<String> objectKeys,
                                      String downloadDirectory) {

        // Initializes the Amazon S3 client.
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

        try {
            // Downloads each object to the specified file path.
            for (String key : objectKeys) {
                S3Object object = s3Client.getObject(s3BucketName, key);
                String endpointsFileName = key.substring(key.lastIndexOf("/"));
                Path filePath = Paths.get(downloadDirectory + endpointsFileName);

                System.out.format("Downloading %s to %s . . .\n",
                        filePath.getFileName(), filePath.getParent());

                writeObjectToFile(filePath, object);
            }
            System.out.println("Download finished.");
        } catch (AmazonServiceException | NullPointerException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    private static void writeObjectToFile(Path filePath, S3Object object) {

        // Writes the contents of the S3 object to a file.
        File endpointsFile = new File(filePath.toAbsolutePath().toString());
        try (FileOutputStream fos = new FileOutputStream(endpointsFile);
             S3ObjectInputStream s3is = object.getObjectContent()) {
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

// snippet-start:[pinpoint.java.pinpoint_export_endpoints.complete]
