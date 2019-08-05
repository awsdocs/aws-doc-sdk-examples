// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.EnableDynamoDBAutoscaling] 
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
package com.amazonaws.codesamples.autoscaling;

import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScalingClient;
import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScalingClientBuilder;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalableTargetsRequest;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalableTargetsResult;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalingPoliciesRequest;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalingPoliciesResult;
import com.amazonaws.services.applicationautoscaling.model.MetricType;
import com.amazonaws.services.applicationautoscaling.model.PolicyType;
import com.amazonaws.services.applicationautoscaling.model.PredefinedMetricSpecification;
import com.amazonaws.services.applicationautoscaling.model.PutScalingPolicyRequest;
import com.amazonaws.services.applicationautoscaling.model.RegisterScalableTargetRequest;
import com.amazonaws.services.applicationautoscaling.model.ScalableDimension;
import com.amazonaws.services.applicationautoscaling.model.ServiceNamespace;
import com.amazonaws.services.applicationautoscaling.model.TargetTrackingScalingPolicyConfiguration;

public class EnableDynamoDBAutoscaling {

    static AWSApplicationAutoScalingClient aaClient = (AWSApplicationAutoScalingClient) AWSApplicationAutoScalingClientBuilder.standard().build();

    public static void main(String args[]) {

	ServiceNamespace ns = ServiceNamespace.Dynamodb;
	ScalableDimension tableWCUs = ScalableDimension.DynamodbTableWriteCapacityUnits;
	String resourceID = "table/TestTable";

	// Define the scalable target
	RegisterScalableTargetRequest rstRequest = new RegisterScalableTargetRequest()
		.withServiceNamespace(ns)
		.withResourceId(resourceID)
		.withScalableDimension(tableWCUs)
		.withMinCapacity(5)
		.withMaxCapacity(10)
		.withRoleARN("SERVICE_ROLE_ARN_GOES_HERE");
	
	try {
	    aaClient.registerScalableTarget(rstRequest);
	} catch (Exception e) {
	    System.err.println("Unable to register scalable target: ");
	    System.err.println(e.getMessage());
	}

	// Verify that the target was created
	DescribeScalableTargetsRequest dscRequest = new DescribeScalableTargetsRequest()
		.withServiceNamespace(ns)
		.withScalableDimension(tableWCUs)
		.withResourceIds(resourceID);
	try {
	    DescribeScalableTargetsResult dsaResult = aaClient.describeScalableTargets(dscRequest);
	    System.out.println("DescribeScalableTargets result: ");
	    System.out.println(dsaResult);
	    System.out.println();
	} catch (Exception e) {
	    System.err.println("Unable to describe scalable target: ");
	    System.err.println(e.getMessage());
	}
	
	System.out.println();

	// Configure a scaling policy
	TargetTrackingScalingPolicyConfiguration targetTrackingScalingPolicyConfiguration = 
		new TargetTrackingScalingPolicyConfiguration()
		.withPredefinedMetricSpecification(
			new PredefinedMetricSpecification()
			.withPredefinedMetricType(MetricType. DynamoDBWriteCapacityUtilization))
		.withTargetValue(50.0)
		.withScaleInCooldown(60)
		.withScaleOutCooldown(60);

	// Create the scaling policy, based on your configuration
	PutScalingPolicyRequest pspRequest = new PutScalingPolicyRequest()
		.withServiceNamespace(ns)
		.withScalableDimension(tableWCUs)
		.withResourceId(resourceID)
		.withPolicyName("MyScalingPolicy")
		.withPolicyType(PolicyType.TargetTrackingScaling)
		.withTargetTrackingScalingPolicyConfiguration(targetTrackingScalingPolicyConfiguration);
	
	try {
	    aaClient.putScalingPolicy(pspRequest);
	} catch (Exception e) {
	    System.err.println("Unable to put scaling policy: ");
	    System.err.println(e.getMessage());
	}

	// Verify that the scaling policy was created
	DescribeScalingPoliciesRequest dspRequest = new DescribeScalingPoliciesRequest()
		.withServiceNamespace(ns)
		.withScalableDimension(tableWCUs)
		.withResourceId(resourceID);
	
	try {
	    DescribeScalingPoliciesResult dspResult = aaClient.describeScalingPolicies(dspRequest);
	    System.out.println("DescribeScalingPolicies result: ");
	    System.out.println(dspResult);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.err.println("Unable to describe scaling policy: ");
	    System.err.println(e.getMessage());
	}

    }

}

// snippet-end:[dynamodb.java.codeexample.EnableDynamoDBAutoscaling] 