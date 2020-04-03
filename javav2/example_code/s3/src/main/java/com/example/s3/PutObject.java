//snippet-sourcedescription:[PutObject.java demonstrates how to upload an object to an Amazon S3 bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-06]
//snippet-sourceauthor:[scmacdon-aws]
/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at
http://aws.amazon.com/apache2.0/
This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
package com.example.s3;

// snippet-start:[s3.java2.s3_object_operations.upload.import]
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
// snippet-end:[s3.java2.s3_object_operations.upload.import]

public class PutObject {

   public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "  PutObject <bucket> <object> <path> \n\n" +
                "Where:\n" +
                "  bucket - the bucket to upload an object into\n" +
                "  object - the object to upload (ie, book.pdf)\n" +
                "  path -  the path where the file is located (C:/AWS/book2.pdf) \n\n" +
                "Examples:\n" +
                "    PutObject mybucket book.pdf C:/AWS/book2.pdf";

          if (args.length < 3) {
              System.out.println(USAGE);
            System.exit(1);
         }

        String bucketName = args[0];
        String objectKey =  args[1];
        String objectPath = args[2];

        System.out.println("Putting object " + objectKey +" into bucket "+bucketName);
        System.out.println("  in bucket: " + bucketName);

        // Create the S3Client object
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        String result = putS3Object(s3, bucketName, objectKey, objectPath);
        System.out.println("Tag information: "+result);
    }

    public static  String putS3Object(S3Client s3, String bucketName, String objectKey, String objectPath) {

        try {
            //Put a file into the bucket
            // snippet-start:[s3.java2.s3_object_operations.upload]
            PutObjectResponse response = s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build(),
                    RequestBody.fromBytes(getObjectFile(objectPath)));

            return response.eTag();

        } catch (S3Exception | FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return "";
    }

    public static byte[] getObjectFile(String path) throws FileNotFoundException {

        byte[] bFile = readBytesFromFile(path);
        return bFile;
    }

    private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }
    // snippet-end:[s3.java2.s3_object_operations.upload]
}
