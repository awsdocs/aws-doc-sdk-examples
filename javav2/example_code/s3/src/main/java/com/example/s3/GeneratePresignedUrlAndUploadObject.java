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
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GeneratePresignedUrlAndUploadObject.java demonstrates how to use the S3Presigner client object to create a presigned URL and upload an object to a S3 bucket]
// snippet-service:[S3]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-12-05]
// snippet-sourceauthor:[AWS - scmacdon]

// snippet-start:[presigned.java2.generatepresignedurl.complete]
package com.example.s3;

// snippet-start:[presigned.java2.generatepresignedurl.import]
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
// snippet-end:[presigned.java2.generatepresignedurl.import]

public class GeneratePresignedUrlAndUploadObject {

    public static void main(String[] args)
    {
        if (args.length < 2) {
            System.out.println("Please specify a bucket name and a key name");
            System.exit(1);
        }

        // snippet-start:[presigned.java2.generatepresignedurl.main]
        String bucketName = args[0];
        String keyName = args[1];

        // Create an S3Presigner using the default AWS Region and credentials
        S3Presigner presigner = S3Presigner.create();

        try{
            PresignedPutObjectRequest presignedRequest =
                    presigner.presignPutObject(z -> z.signatureDuration(Duration.ofMinutes(10))
                            .putObjectRequest(por -> por.bucket(bucketName).key(keyName)));

            System.out.println("Pre-signed URL to upload a file to: " +
                    presignedRequest.url());
            System.out.println("Which HTTP method needs to be used when uploading a file: " +
                    presignedRequest.httpRequest().method());


            //Upload content to the bucket by using this URL
            URL url = presignedRequest.url();

            // Create the connection and use it to upload the new object by using the pre-signed URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","text/plain");
            connection.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write("This text uploaded as an object via presigned URL.");
            out.close();

            connection.getResponseCode();
            System.out.println("HTTP response code: " + connection.getResponseCode());

            // It's recommended that you close the S3Presigner when it is done being used, because some credential
            // providers (e.g. if your AWS profile is configured to assume an STS role) require system resources
            // that need to be freed. If you are using one S3Presigner per application (as recommended), this
            // usually isn't needed
            presigner.close();

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
        // snippet-end:[presigned.java2.generatepresignedurl.main]
    }
}
// snippet-end:[presigned.java2.generatepresignedurl.complete]
