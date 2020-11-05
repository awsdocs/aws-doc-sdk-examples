//snippet-sourcedescription:[SetBucketPolicy.java demonstrates how to add a bucket policy to an existing Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/28/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
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

public class SetBucketPolicy {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    SetBucketPolicy <bucketName> <polFile>\n\n" +
                "Where:\n" +
                "    bucketName - the Amazon S3 bucket to set the policy on.\n" +
                "    polFile - a JSON file containing the policy (see the S3 Readme for an example). \n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
         }

        String bucketName = args[0];
        String polFile = args[1] ;
        String policyText = getBucketPolicyFromFile(polFile);

        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        setPolicy(s3, bucketName, policyText);
        s3.close();
    }

    // snippet-start:[s3.java2.set_bucket_policy.main]
    public static void setPolicy(S3Client s3, String bucketName, String policyText) {

        System.out.println("Setting policy:");
        System.out.println("----");
        System.out.println(policyText);
        System.out.println("----");
        System.out.format("On Amazon S3 bucket: \"%s\"\n", bucketName);

        try {
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
    public static String getBucketPolicyFromFile(String policyFile) {

        StringBuilder fileText = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(policyFile), Charset.forName("UTF-8"));
            for (String line : lines) {
                fileText.append(line);
            }
        } catch (IOException e) {
            System.out.format("Problem reading file: \"%s\"", policyFile);
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
