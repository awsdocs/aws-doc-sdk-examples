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
// snippet-sourcedescription:[GetObjectUsingPresignedUrl.java demonstrates how to get an object located in a S4 bucket by using the S3Presigner client object]
// snippet-service:[S3]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon S3
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-12-05]
// snippet-sourceauthor:[AWS - scmacdon]

// snippet-start:[presigned.java2.getobjectpresigned.complete]
package com.example.s3;

// snippet-start:[presigned.java2.getobjectpresigned.import]
import java.io.*;
import java.net.HttpURLConnection;
import java.time.Duration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.utils.IoUtils;
// snippet-end:[presigned.java2.getobjectpresigned.import]

public class GetObjectPresignedUrl {

    public static void main(String[] args)
    {
        if (args.length < 2) {
            System.out.println("Please specify a bucket name and a key name");
            System.exit(1);
        }

        // snippet-start:[presigned.java2.getobjectpresigned.main]
        String bucketName = args[0];
        String keyName = args[1];

        // Create an S3Presigner by using the default AWS Region and credentials
        S3Presigner presigner = S3Presigner.create();

        try{

            // Create a GetObjectRequest to be pre-signed
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .build();

            // Create a GetObjectPresignRequest to specify the signature duration
            GetObjectPresignRequest getObjectPresignRequest =
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(10))
                            .getObjectRequest(getObjectRequest)
                            .build();

            // Generate the presigned request
            PresignedGetObjectRequest presignedGetObjectRequest =
                    presigner.presignGetObject(getObjectPresignRequest);

            // Log the presigned URL
            System.out.println("Presigned URL: " + presignedGetObjectRequest.url());

            // Create a JDK HttpURLConnection for communicating with S3
            HttpURLConnection connection = (HttpURLConnection) presignedGetObjectRequest.url().openConnection();

            // Specify any headers that the service needs (not needed when isBrowserExecutable is true)
            presignedGetObjectRequest.httpRequest().headers().forEach((header, values) -> {
                values.forEach(value -> {
                    connection.addRequestProperty(header, value);
                });
            });

            // Send any request payload that the service needs (not needed when isBrowserExecutable is true)
            if (presignedGetObjectRequest.signedPayload().isPresent()) {
                connection.setDoOutput(true);
                try (InputStream signedPayload = presignedGetObjectRequest.signedPayload().get().asInputStream();
                     OutputStream httpOutputStream = connection.getOutputStream()) {
                    IoUtils.copy(signedPayload, httpOutputStream);
                }
            }

            // Download the result of executing the request
            try (InputStream content = connection.getInputStream()) {
                System.out.println("Service returned response: ");
                IoUtils.copy(content, System.out);
            }

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
        // snippet-end:[presigned.java2.getobjectpresigned.main]
    }
}
// snippet-end:[presigned.java2.getobjectpresigned.complete]
