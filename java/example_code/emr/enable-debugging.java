// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[emr.java.stepfactory.enabledebugging]
    StepFactory stepFactory = new StepFactory(); 
	StepConfig enabledebugging = new StepConfig()
   		.withName("Enable debugging")
   		.withActionOnFailure("TERMINATE_JOB_FLOW")
   		.withHadoopJarStep(stepFactory.newEnableDebuggingStep());
// snippet-end:[emr.java.stepfactory.enabledebugging]
