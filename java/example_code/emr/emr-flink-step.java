// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
