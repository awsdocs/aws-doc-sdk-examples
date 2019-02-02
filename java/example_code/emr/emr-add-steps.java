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

// snippet-sourcedescription:[emr-add-steps.java demonstrates how to add steps to a running EMR cluster. Steps perform work for data processing jobs.]
// snippet-service:[Amazon EMR]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[StepFactory]
// snippet-keyword:[newScriptRunnerStep]
// snippet-keyword:[HadoopJarStepConfig]
// snippet-keyword:[AddJobFlowStepsRequest]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.add_steps.bashandjar]
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
			.withRegion("us-west-1")
			.build();
        
		// Run a bash script using a predefined step in the StepFactory helper class
	    StepFactory stepFactory = new StepFactory();
	    StepConfig runBashScript = new StepConfig()
	    		.withName("Run a bash script") 
	    		.withHadoopJarStep(stepFactory.newScriptRunnerStep("s3://jeffgoll/emr-scripts/create_users.sh"))
	    		.withActionOnFailure("CONTINUE");

	    // Run a custom jar file as a step
	    HadoopJarStepConfig hadoopConfig1 = new HadoopJarStepConfig()
	       .withJar("s3://path/to/my/jarfolder") // replace with the location of the jar to run as a step
	       .withMainClass("com.my.Main1") // optional main class, this can be omitted if jar above has a manifest
	       .withArgs("--verbose"); // optional list of arguments to pass to the jar
	    StepConfig myCustomJarStep = new StepConfig("RunHadoopJar", hadoopConfig1);

	    AddJobFlowStepsResult result = emr.addJobFlowSteps(new AddJobFlowStepsRequest()
		  .withJobFlowId("j-xxxxxxxxxxxx") // replace with cluster id to run the steps
		  .withSteps(runBashScript,myCustomJarStep));
	    
          System.out.println(result.getStepIds());

	}
}
// snippet-end:[emr.java.add_steps.bashandjar]