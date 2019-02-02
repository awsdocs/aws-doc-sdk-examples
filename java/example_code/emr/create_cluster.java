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

// snippet-sourcedescription:[create_cluster.java demonstrates how to create a long-running Amazon EMR cluster with a list of applications installed and debugging enabled.]
// snippet-service:[Amazon EMR]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[RunJobFlowRequest]
// snippet-keyword:[AmazonElasticMapReduceClientBuilder]
// snippet-keyword:[newEnableDebuggingStep]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.create-cluster.runjobflow]
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
			credentials_profile = new ProfileCredentialsProvider("default").getCredentials(); // specifies any named profile in .aws/credentials as the credentials provider
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load credentials from .aws/credentials file. " +
                    "Make sure that the credentials file exists and that the profile name is defined within it.",
                    e);
        }
		
		// create an EMR client using the credentials and region specified in order to create the cluster
		AmazonElasticMapReduce emr = AmazonElasticMapReduceClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials_profile))
			.withRegion("us-west-1")
			.build();
        
        // create a step to enable debugging in the AWS Management Console
		StepFactory stepFactory = new StepFactory(); 
		StepConfig enabledebugging = new StepConfig()
   			.withName("Enable debugging")
   			.withActionOnFailure("TERMINATE_JOB_FLOW")
   			.withHadoopJarStep(stepFactory.newEnableDebuggingStep());
        
        // specify applications to be installed and configured when EMR creates the cluster
		Application hive = new Application().withName("Hive");
		Application spark = new Application().withName("Spark");
		Application ganglia = new Application().withName("Ganglia");
		Application zeppelin = new Application().withName("Zeppelin");
		
		// create the cluster
		RunJobFlowRequest request = new RunJobFlowRequest()
	       		.withName("MyClusterCreatedFromJava")
	       		.withReleaseLabel("emr-5.20.0") // specifies the EMR release version label, we recommend the latest release
	       		.withSteps(enabledebugging)
	       		.withApplications(hive,spark,ganglia,zeppelin)
	       		.withLogUri("s3://path/to/my/emr/logs") // a URI in S3 for log files is required when debugging is enabled
	       		.withServiceRole("EMR_DefaultRole") // replace the default with a custom IAM service role if one is used
	       		.withJobFlowRole("EMR_EC2_DefaultRole") // replace the default with a custom EMR role for the EC2 instance profile if one is used
	       		.withInstances(new JobFlowInstancesConfig()
	       	   		.withEc2SubnetId("subnet-12ab34c56")
	           		.withEc2KeyName("myEc2Key") 
	           		.withInstanceCount(3) 
	           		.withKeepJobFlowAliveWhenNoSteps(true)    
	           		.withMasterInstanceType("m4.large")
	           		.withSlaveInstanceType("m4.large"));

	   RunJobFlowResult result = emr.runJobFlow(request);  
	   System.out.println("The cluster ID is " + result.toString());

	}

}
// snippet-end:[emr.java.create-cluster.runjobflow]