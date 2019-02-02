#
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.
#
# snippet-sourcedescription:[emrfs-boto-step.py demonstrates how to add a step to an EMR cluster that adds objects in an Amazon S3 bucket to the default EMRFS metadata table.]
# snippet-service:[Amazon EMR]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon EMR]
# snippet-keyword:[Code Sample]
# snippet-keyword:[add_jobflow_steps]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[AWS]
# snippet-start:[emr.python.addstep.emrfs]
from boto.emr import EmrConnection,connect_to_region,JarStep

emr=EmrConnection()
connect_to_region("us-west-1")

myStep = JarStep(name='Boto EMRFS Sync',
               jar='s3://elasticmapreduce/libs/script-runner/script-runner.jar',
               action_on_failure="CONTINUE",
               step_args=['/home/hadoop/bin/emrfs',
                          'sync',
                          's3://elasticmapreduce/samples/cloudfront'])


stepId = emr.add_jobflow_steps("j-2AL4XXXXXX5T9",
                          steps=[myStep]).stepids[0].value
# snippet-end:[emr.python.addstep.emrfs]
