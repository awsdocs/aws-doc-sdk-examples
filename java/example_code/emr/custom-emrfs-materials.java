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

// snippet-sourcedescription:[custom-emrfs-materials.java demonstrates how to create a cluster, specifying values in the emrfs-site configuration classification for a custom encryption materials provider.]
// snippet-service:[elasticmapreduce]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[withClassification]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.create-cluster.emrfsconfiguration]
import java.util.HashMap;
import java.util.Map;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.*;

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
		
		Map<String,String> emrfsProperties = new HashMap<String,String>();
			emrfsProperties.put("fs.s3.cse.encryptionMaterialsProvider.uri","s3://mybucket/MyCustomEncryptionMaterialsProvider.jar");
	    	emrfsProperties.put("fs.s3.cse.enabled","true");
	    	emrfsProperties.put("fs.s3.consistent","true");
		    emrfsProperties.put("fs.s3.cse.encryptionMaterialsProvider","full.class.name.of.EncryptionMaterialsProvider");

		Configuration myEmrfsConfig = new Configuration()
	    	.withClassification("emrfs-site")
	    	.withProperties(emrfsProperties);
		
		Application hive = new Application().withName("Hive");
		Application spark = new Application().withName("Spark");
		Application ganglia = new Application().withName("Ganglia");
		Application zeppelin = new Application().withName("Zeppelin");

		RunJobFlowRequest request = new RunJobFlowRequest()
			.withName("ClusterWithCustomEMRFSEncryptionMaterialsProvider")
			.withReleaseLabel("emr-5.20.0")
			.withApplications(hive,spark,ganglia,zeppelin)
			.withConfigurations(myEmrfsConfig)
			.withServiceRole("EMR_DefaultRole")
			.withJobFlowRole("EMR_EC2_DefaultRole")
			.withLogUri("s3://path/to/emr/logs")
			.withInstances(new JobFlowInstancesConfig()
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
// snippet-end:[emr.java.create-cluster.emrfsconfiguration]
