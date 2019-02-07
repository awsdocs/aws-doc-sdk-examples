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

// snippet-sourcedescription:[enable-debugging.java demonstrates how to use the StepFactory helper class to configure a step for debugging that you can specify using .withSteps when you create a cluster using RunJobFlowRequest.]
// snippet-service:[elasticmapreduce]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[stepFactory]
// snippet-keyword:[enableDebugging]
// snippet-keyword:[StepConfig]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.stepfactory.enabledebugging]
    StepFactory stepFactory = new StepFactory(); 
	StepConfig enabledebugging = new StepConfig()
   		.withName("Enable debugging")
   		.withActionOnFailure("TERMINATE_JOB_FLOW")
   		.withHadoopJarStep(stepFactory.newEnableDebuggingStep());
// snippet-end:[emr.java.stepfactory.enabledebugging]
