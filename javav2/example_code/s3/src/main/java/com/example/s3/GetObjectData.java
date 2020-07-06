// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetObjectData.java demonstrates how to read data from an Amazon S3 object.]
// snippet-service:[Amazon S3]
// snippet-keyword:[SDK for Java 2.0]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/29/2020]
//snippet-sourceauthor:[scmacdon-aws]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.example.s3;

// snippet-start:[s3.java2.getobjectdata.import]
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
// snippet-end:[s3.java2.getobjectdata.import]

public class GetObjectData {

    public static void main(String[] args) {

      if (args.length < 3) {
            System.out.println("Please specify a bucket name, a key name that represents a PDF file (ie, book.pdf), and a path (ie, C:\\AWS\\AdobePDF.pdf)");
            System.exit(1);
        }

        String bucketName = args[0];
        String keyName = args[1];
        String path = args[2];

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        getObjectBytes(s3,bucketName,keyName, path);
    }

    // snippet-start:[s3.java2.getobjectdata.main]
    public static void getObjectBytes (S3Client s3, String bucketName, String keyName, String path ) {

        try {
            // create a GetObjectRequest instance
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            // get the byte[] this AWS S3 object
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            //Write the data to a local file
            File myFile = new File(path );
            OutputStream os = new FileOutputStream(myFile);
            os.write(data);
            System.out.println("Successfully obtained bytes from an S3 object");

            // Close the file
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (S3Exception e) {
          System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
        }
        // snippet-end:[s3.java2.getobjectdata.main]
    }
}
