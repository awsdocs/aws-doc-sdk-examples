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

// snippet-sourcedescription:[runjobflow-configurationclassification.java demonstrates how to specify configuration classification properties when creating a cluster using RunJobFlowRequest.]
// snippet-service:[elasticmapreduce]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[RunJobFlowRequest]
// snippet-keyword:[withConfigurations]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.create-cluster.specifyconfigurationclassification]
Application hive = new Application().withName("Hive");

Map<String,String> hiveProperties = new HashMap<String,String>();
	hiveProperties.put("hive.join.emit.interval","1000");
	hiveProperties.put("hive.merge.mapfiles","true");
	    
Configuration myHiveConfig = new Configuration()
	.withClassification("hive-site")
	.withProperties(hiveProperties);

RunJobFlowRequest request = new RunJobFlowRequest()
	.withName("Create cluster with ReleaseLabel")
	.withReleaseLabel("emr-5.20.0")
	.withApplications(hive)
	.withConfigurations(myHiveConfig)
	.withServiceRole("EMR_DefaultRole")
	.withJobFlowRole("EMR_EC2_DefaultRole")
	.withInstances(new JobFlowInstancesConfig()
		.withEc2KeyName("myEc2Key")
		.withInstanceCount(3)
		.withKeepJobFlowAliveWhenNoSteps(true)
		.withMasterInstanceType("m4.large")
		.withSlaveInstanceType("m4.large")
	);				
// snippet-end:[emr.java.create-cluster.specifyconfigurationclassification]
