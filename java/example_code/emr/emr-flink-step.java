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

// snippet-sourcedescription:[emr-flink-step.java demonstrates how to submit work as a step to a long-running Flink cluster using the YARN application ID.]
// snippet-service:[elasticmapreduce]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[RunJobFlowRequest]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.runstep.flink]
List<StepConfig> stepConfigs = new ArrayList<StepConfig>();

// The application id specified below is retrieved from the YARN cluster, for example, by running 'yarn application -list' from the master node command line 
HadoopJarStepConfig flinkWordCountConf = new HadoopJarStepConfig()
    .withJar("command-runner.jar")
    .withArgs("flink", "run", "-m", "yarn-cluster", "-yid", "application_1473169569237_0002", "-yn", "2", "/usr/lib/flink/examples/streaming/WordCount.jar", 
      "--input", "s3://bucket/for/my/textfile.txt", "--output", "s3://bucket/for/my/output/");

StepConfig flinkRunWordCount = new StepConfig()
  .withName("Flink add a wordcount step")
  .withActionOnFailure("CONTINUE")
  .withHadoopJarStep(flinkWordCountConf);
  
stepConfigs.add(flinkRunWordCount); 

// Specify the cluster ID of the YARN cluster instead of j-xxxxxxxxx
AddJobFlowStepsResult res = emr.addJobFlowSteps(new AddJobFlowStepsRequest()
   .withJobFlowId("j-xxxxxxxxxx")
   .withSteps(stepConfigs));
// snippet-end:[emr.java.runstep.flink]
