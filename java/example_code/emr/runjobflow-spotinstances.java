// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
