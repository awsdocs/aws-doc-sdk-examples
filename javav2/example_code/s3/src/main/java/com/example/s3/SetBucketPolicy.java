//snippet-sourcedescription:[SetBucketPolicy.java demonstrates how to add a bucket policy to an existing Amazon S3 bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/6/2020]
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

// snippet-start:[s3.java2.set_bucket_policy.import]
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
// snippet-end:[s3.java2.set_bucket_policy.import]

/**
 * Add a bucket policy to an existing S3 bucket.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */

public class SetBucketPolicy {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    SetBucketPolicy <bucket> [policyfile]\n\n" +
                "Where:\n" +
                "    bucket     - the bucket to set the policy on.\n" +
                "    policyfile - an optional JSON file containing the policy\n" +
                "                 description.\n\n" +
                "If no policyfile is given, a generic public-read policy will be set on\n" +
                "the bucket.\n\n" +
                "Example:\n" +
                "    SetBucketPolicy testbucket mypolicy.json\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bucketName = args[0];
        String policyText = "";
        policyText = getBucketPolicyFromFile(args[1]);

        System.out.println("Setting policy:");
        System.out.println("----");
        System.out.println(policyText);
        System.out.println("----");
        System.out.format("On S3 bucket: \"%s\"\n", bucketName);

        // Create the S3Client object
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        // Set the Bucket Policy
        SetPolicy(s3, bucketName, policyText);
    }

    // snippet-start:[s3.java2.set_bucket_policy.main]
    public static void SetPolicy(S3Client s3, String bucketName, String policyText) {

        try {

            policyText = getBucketPolicyFromFile(policyText);
            PutBucketPolicyRequest policyReq = PutBucketPolicyRequest.builder()
                    .bucket(bucketName)
                    .policy(policyText)
                    .build();
            s3.putBucketPolicy(policyReq);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Done!");
    }


    // Loads a JSON-formatted policy from a file, verifying it with the Policy
    // class.
    private static String getBucketPolicyFromFile(String policFile) {

        StringBuilder fileText = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(policFile), Charset.forName("UTF-8"));
            for (String line : lines) {
                fileText.append(line);
            }
        } catch (IOException e) {
            System.out.format("Problem reading file: \"%s\"", policFile);
            System.out.println(e.getMessage());
        }

        try {
            final JsonParser parser = new ObjectMapper().getFactory().createParser(fileText.toString());
            while (parser.nextToken() != null) {
            }

        } catch (JsonParseException jpe) {
            jpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return fileText.toString();
    }
}
 // snippet-end:[s3.java2.set_bucket_policy.main]
