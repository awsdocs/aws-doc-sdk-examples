//snippet-sourcedescription:[PutObject.java demonstrates how to upload a file to an AWS Elemental MediaStore container.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mediastore]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-06]
//snippet-sourceauthor:[rhcarvalho]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package aws.example.mediastore;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.mediastore.AWSMediaStore;
import com.amazonaws.services.mediastore.AWSMediaStoreClientBuilder;
import com.amazonaws.services.mediastore.model.DescribeContainerRequest;
import com.amazonaws.services.mediastore.model.DescribeContainerResult;
import com.amazonaws.services.mediastore.model.AWSMediaStoreException;
import com.amazonaws.services.mediastoredata.AWSMediaStoreData;
import com.amazonaws.services.mediastoredata.AWSMediaStoreDataClientBuilder;
import com.amazonaws.services.mediastoredata.model.PutObjectRequest;
import com.amazonaws.services.mediastoredata.model.PutObjectResult;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Upload a file to an AWS Elemental MediaStore container.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class PutObject
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of a container and a file to\n" +
            "upload to it.\n" +
            "\n" +
            "Ex: PutObject <container-name> <file-path>\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        final String containerName = args[0];
        final String filePath = args[1];

        System.out.format("Uploading %s to MediaStore container %s...\n", filePath, containerName);

        try (final InputStream is = new FileInputStream(filePath)) {
            final PutObjectResult result = putObject(containerName, filePath, is);
            System.out.printf("Saved object: %s\n", result);
            System.out.println("Done!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static PutObjectResult putObject(String containerName, String filePath, InputStream body) throws IOException {
        final String endpoint = getContainerEndpoint(containerName);
        if (endpoint == null || endpoint.isEmpty()) {
            System.err.println("Could not determine container endpoint!");
            System.exit(1);
        }
        final String region = new DefaultAwsRegionProviderChain().getRegion();
        final EndpointConfiguration endpointConfig = new EndpointConfiguration(endpoint, region);

        final AWSMediaStoreData mediastoredata = AWSMediaStoreDataClientBuilder
            .standard()
            .withEndpointConfiguration(endpointConfig)
            .build();
        final PutObjectRequest request = new PutObjectRequest()
            .withContentType("application/octet-stream")
            .withBody(body)
            .withPath(filePath);

        try {
            return mediastoredata.putObject(request);
        } catch (AWSMediaStoreException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        return null;
    }

    public static String getContainerEndpoint(String name) {
        final AWSMediaStore mediastore = AWSMediaStoreClientBuilder.defaultClient();
        final DescribeContainerRequest request = new DescribeContainerRequest()
            .withContainerName(name.trim());
        try {
            final DescribeContainerResult result = mediastore.describeContainer(request);
            return result.getContainer().getEndpoint();
        } catch (AWSMediaStoreException e) {
            System.err.println(e.getErrorMessage());
        }
        return null;
    }
}
