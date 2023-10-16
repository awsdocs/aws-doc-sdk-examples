/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0 */

package aws.example.s3;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class GetPDFandSave {

    private static final String ACCESS_KEY = "ACCESS_KEY";
    private static final String SECRET_KEY = "SECRET_KEY";
    private static final String REGION_NAME = "us-west-2";
    private static final String BUCKET_NAME = "BUCKET_NAME";
    private static final String OBJECT_KEY = "file-example_PDF_1MB.pdf";

    public static void main(String[] args) {

        // Set up AWS credentials and S3 client
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.fromName(REGION_NAME))
                .build();

        try {
            
            // Download the PDF file from S3
            S3Object s3object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, OBJECT_KEY));
            InputStream inputStream = s3object.getObjectContent();

            // Save the PDF file to disk
            File outputFile = new File("output.pdf");
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            // Copy the input stream to the output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}
