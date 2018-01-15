/*
Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at

http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/
package com.example.s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.regions.Region;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
* Add a bucket policy to an existing S3 bucket.
*
* This code expects that you have AWS credentials set up per:
* http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
*/
public class SetBucketPolicy
{
    // Loads a JSON-formatted policy from a file, verifying it with the Policy
    // class.
    private static String getBucketPolicyFromFile(String policy_file)
    {
        StringBuilder file_text = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(
            Paths.get(policy_file), Charset.forName("UTF-8"));
            for (String line : lines) {
                file_text.append(line);
            }
        } catch (IOException e) {
            System.out.format("Problem reading file: \"%s\"", policy_file);
            System.out.println(e.getMessage());
        }
        
        try {
            final JsonParser parser = new ObjectMapper().getFactory().createParser(file_text.toString());
            while (parser.nextToken() != null) {
            }

         } catch (JsonParseException jpe) {
            jpe.printStackTrace();
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
        
        return file_text.toString();

    }

    public static void setBucketPolicy(String bucket_name, String policy_text)
    {
    	Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder().region(region).build();
        try {
        	PutBucketPolicyRequest policyReq = PutBucketPolicyRequest.builder()
        			.bucket(bucket_name)
        			.policy(policy_text)
        			.build();
            s3.putBucketPolicy(policyReq);
        } catch (S3Exception e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

   public static void main(String[] args)
   {
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

      String bucket_name = args[0];
      String policy_text = null;

      policy_text = getBucketPolicyFromFile(args[1]);
      
      System.out.println("Setting policy:");
      System.out.println("----");
      System.out.println(policy_text);
      System.out.println("----");
      System.out.format("On S3 bucket: \"%s\"\n", bucket_name);

      setBucketPolicy(bucket_name, policy_text);

      System.out.println("Done!");
   }
}

