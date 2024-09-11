// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.java.codeexample.DisableDynamoDBAutoscaling]
package com.amazonaws.codesamples.autoscaling;

import com.amazonaws.services.applicationautoscaling.AWSApplicationAutoScalingClient;
import com.amazonaws.services.applicationautoscaling.model.DeleteScalingPolicyRequest;
import com.amazonaws.services.applicationautoscaling.model.DeregisterScalableTargetRequest;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalableTargetsRequest;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalableTargetsResult;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalingPoliciesRequest;
import com.amazonaws.services.applicationautoscaling.model.DescribeScalingPoliciesResult;
import com.amazonaws.services.applicationautoscaling.model.ScalableDimension;
import com.amazonaws.services.applicationautoscaling.model.ServiceNamespace;

public class DisableDynamoDBAutoscaling {

	static AWSApplicationAutoScalingClient aaClient = new AWSApplicationAutoScalingClient();

	public static void main(String args[]) {

		ServiceNamespace ns = ServiceNamespace.Dynamodb;
		ScalableDimension tableWCUs = ScalableDimension.DynamodbTableWriteCapacityUnits;
		String resourceID = "table/TestTable";

		// Delete the scaling policy
		DeleteScalingPolicyRequest delSPRequest = new DeleteScalingPolicyRequest()
				.withServiceNamespace(ns)
				.withScalableDimension(tableWCUs)
				.withResourceId(resourceID)
				.withPolicyName("MyScalingPolicy");

		try {
			aaClient.deleteScalingPolicy(delSPRequest);
		} catch (Exception e) {
			System.err.println("Unable to delete scaling policy: ");
			System.err.println(e.getMessage());
		}

		// Verify that the scaling policy was deleted
		DescribeScalingPoliciesRequest descSPRequest = new DescribeScalingPoliciesRequest()
				.withServiceNamespace(ns)
				.withScalableDimension(tableWCUs)
				.withResourceId(resourceID);

		try {
			DescribeScalingPoliciesResult dspResult = aaClient.describeScalingPolicies(descSPRequest);
			System.out.println("DescribeScalingPolicies result: ");
			System.out.println(dspResult);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to describe scaling policy: ");
			System.err.println(e.getMessage());
		}

		System.out.println();

		// Remove the scalable target
		DeregisterScalableTargetRequest delSTRequest = new DeregisterScalableTargetRequest()
				.withServiceNamespace(ns)
				.withScalableDimension(tableWCUs)
				.withResourceId(resourceID);

		try {
			aaClient.deregisterScalableTarget(delSTRequest);
		} catch (Exception e) {
			System.err.println("Unable to deregister scalable target: ");
			System.err.println(e.getMessage());
		}

		// Verify that the scalable target was removed
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

	}

}

// snippet-end:[dynamodb.java.codeexample.DisableDynamoDBAutoscaling]