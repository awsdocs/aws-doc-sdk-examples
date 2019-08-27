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

// snippet-sourcedescription:[emr-flink-cluster-transient-step.java demonstrates how to create a Flink cluster, and then run a step that shuts down the cluster when it completes.]
// snippet-service:[elasticmapreduce]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[RunJobFlowRequest]
// snippet-keyword:[StepConfig]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.createflinkcluster.runstep]
import java.util.ArrayList;
import java.util.List;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.*;

public class Main_test {

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
        
		List<StepConfig> stepConfigs = new ArrayList<StepConfig>();
	    HadoopJarStepConfig flinkWordCountConf = new HadoopJarStepConfig()
	      .withJar("command-runner.jar")
	      .withArgs("bash","-c", "flink", "run", "-m", "yarn-cluster", "-yn", "2", "/usr/lib/flink/examples/streaming/WordCount.jar", "--input", "s3://path/to/input-file.txt", "--output", "s3://path/to/output/");
	    
	    StepConfig flinkRunWordCountStep = new StepConfig()
	      .withName("Flink add a wordcount step and terminate")
	      .withActionOnFailure("CONTINUE")
	      .withHadoopJarStep(flinkWordCountConf);
	    
	    stepConfigs.add(flinkRunWordCountStep); 
	    
	    Application flink = new Application().withName("Flink");
	    
	    RunJobFlowRequest request = new RunJobFlowRequest()
	      .withName("flink-transient")
	      .withReleaseLabel("emr-5.20.0")
	      .withApplications(flink)
	      .withServiceRole("EMR_DefaultRole")
	      .withJobFlowRole("EMR_EC2_DefaultRole")
	      .withLogUri("s3://path/to/my/logfiles")
	      .withInstances(new JobFlowInstancesConfig()
	          .withEc2KeyName("myEc2Key")
	          .withEc2SubnetId("subnet-12ab3c45")
	          .withInstanceCount(3)
	          .withKeepJobFlowAliveWhenNoSteps(false)
	          .withMasterInstanceType("m4.large")
	          .withSlaveInstanceType("m4.large"))
	      .withSteps(stepConfigs);
	    
	    RunJobFlowResult result = emr.runJobFlow(request);  
		System.out.println("The cluster ID is " + result.toString());

	}

}
// snippet-end:[emr.java.createflinkcluster.runstep]
