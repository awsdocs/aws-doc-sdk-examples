 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
package aws.example.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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

        // Verify the policy by trying to load it into a Policy object.
        Policy bucket_policy = null;
        try {
            bucket_policy = Policy.fromJson(file_text.toString());
        } catch (IllegalArgumentException e) {
            System.out.format("Invalid policy text in file: \"%s\"",
                    policy_file);
            System.out.println(e.getMessage());
        }

        return bucket_policy.toJson();
    }

    // Sets a public read policy on the bucket.
    public static String getPublicReadPolicy(String bucket_name)
    {
        Policy bucket_policy = new Policy().withStatements(
            new Statement(Statement.Effect.Allow)
                .withPrincipals(Principal.AllUsers)
                .withActions(S3Actions.GetObject)
                .withResources(new Resource(
                    "arn:aws:s3:::" + bucket_name + "/*")));
        return bucket_policy.toJson();
    }

    public static void setBucketPolicy(String bucket_name, String policy_text)
    {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            s3.setBucketPolicy(bucket_name, policy_text);
        } catch (AmazonServiceException e) {
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

      // if a second argument was given (JSON file name), then load the policy
      // from that file.
      if (args.length > 1) {
         policy_text = getBucketPolicyFromFile(args[1]);
      } else {
         policy_text = getPublicReadPolicy(bucket_name);
      }

      System.out.println("Setting policy:");
      System.out.println("----");
      System.out.println(policy_text);
      System.out.println("----");
      System.out.format("On S3 bucket: \"%s\"\n", bucket_name);

      setBucketPolicy(bucket_name, policy_text);

      System.out.println("Done!");
   }
}

