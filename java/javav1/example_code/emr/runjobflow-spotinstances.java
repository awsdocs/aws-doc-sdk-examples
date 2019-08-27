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

// snippet-sourcedescription:[runjobflow-spotinstances.java demonstrates how to specify the Spot instance purchasing option for instance groups using RunJobFlowRequest.]
// snippet-service:[elasticmapreduce]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[RunJobFlowRequest]
// snippet-keyword: [InstanceGroupConfig]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.createcluster.specifyspotinstances]
InstanceGroupConfig instanceGroupConfigMaster = new InstanceGroupConfig()
	.withInstanceCount(1)
	.withInstanceRole("MASTER")
	.withInstanceType("m4.large")
	.withMarket("SPOT")
	.withBidPrice("0.25"); 
	
InstanceGroupConfig instanceGroupConfigCore = new InstanceGroupConfig()
	.withInstanceCount(4)
	.withInstanceRole("CORE")
	.withInstanceType("m4.large")
	.withMarket("SPOT")
	.withBidPrice("0.03");
	
InstanceGroupConfig instanceGroupConfigTask = new InstanceGroupConfig()
	.withInstanceCount(2)
	.withInstanceRole("TASK")
	.withInstanceType("m4.large")
	.withMarket("SPOT")
	.withBidPrice("0.10");
// snippet-end:[emr.java.createcluster.specifyspotinstances]
