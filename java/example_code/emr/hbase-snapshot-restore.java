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

// snippet-sourcedescription:[hbase-snapshot-restore.java demonstrates how to configure a step that restores a table from a snapshot.]
// snippet-service:[elasticmapreduce]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon EMR]
// snippet-keyword:[Code Sample]
// snippet-keyword:[HadoopJarStepConfig]
// snippet-keyword:[HBase snapshot]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-31]
// snippet-sourceauthor:[AWS]
// snippet-start:[emr.java.hbase.snapshotrestoretable]
HadoopJarStepConfig hbaseRestoreSnapshotConf = new HadoopJarStepConfig()
  .withJar("command-runner.jar")
  .withArgs("bash","-c","echo $'disable \"tableName\"; restore_snapshot \"snapshotName\"; enable \"snapshotName\"' | hbase shell");
// snippet-end:[emr.java.hbase.snapshotrestoretable]
