// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
