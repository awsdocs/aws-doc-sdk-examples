// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[emr.java.hbase.snapshotrestoretable]
HadoopJarStepConfig hbaseRestoreSnapshotConf = new HadoopJarStepConfig()
  .withJar("command-runner.jar")
  .withArgs("bash","-c","echo $'disable \"tableName\"; restore_snapshot \"snapshotName\"; enable \"snapshotName\"' | hbase shell");
// snippet-end:[emr.java.hbase.snapshotrestoretable]
