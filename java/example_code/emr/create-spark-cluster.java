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
*/

// snippet-sourcedescription:[create-spark-cluster.java demonstrates how to create a cluster with Spark installed.]
// snippet-service:[Amazon EMR]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[RunJobFlowRequest]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.createcluster.spark]

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.*;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;

public class Main {

	public static void main(String[] args) {
		AWSCredentials credentials_profile = null;		
		try {
			credentials_profile = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load credentials from .aws/credentials file. " +
                    "Make sure that the credentials file exists and the profile name is specified within it.",
                    e);
        }
        
        AmazonElasticMapReduce emr = AmazonElasticMapReduceClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials_profile))
			.withRegion(Regions.US_WEST_1)
			.build();
        
        // create a step to enable debugging in the AWS Management Console
		StepFactory stepFactory = new StepFactory(); 
		StepConfig enabledebugging = new StepConfig()
   			.withName("Enable debugging")
   			.withActionOnFailure("TERMINATE_JOB_FLOW")
   			.withHadoopJarStep(stepFactory.newEnableDebuggingStep());
        
        Application spark = new Application().withName("Spark");

        RunJobFlowRequest request = new RunJobFlowRequest()
            .withName("Spark Cluster")
            .withReleaseLabel("emr-5.20.0")
            .withSteps(enabledebugging)
            .withApplications(spark)
            .withLogUri("s3://path/to/my/logs/")
	       	.withServiceRole("EMR_DefaultRole") 
	       	.withJobFlowRole("EMR_EC2_DefaultRole") 
            .withInstances(new JobFlowInstancesConfig()
                .withEc2SubnetId("subnet-12ab3c45")
                .withEc2KeyName("myEc2Key")
                .withInstanceCount(3)
                .withKeepJobFlowAliveWhenNoSteps(true)
                .withMasterInstanceType("m4.large")
                .withSlaveInstanceType("m4.large")
            );			
        RunJobFlowResult result = emr.runJobFlow(request);  
	    System.out.println("The cluster ID is " + result.toString());
	}
}
// snippet-end:[emr.java.createcluster.spark]
