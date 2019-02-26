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
# snippet-service:[elasticmapreduce]
# snippet-keyword:[Python]
# snippet-keyword:[Amazon EMR]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-01-31]
# snippet-sourceauthor:[AWS]
# snippet-start:[emr.python.addstep.emrfs]

import json
import boto3
from botocore.exceptions import ClientError

# Assign the ID of an existing cluster to the following variable
job_flow_id = 'CLUSTER_ID'

# Define a job flow step. Assign appropriate values as desired.
job_flow_step_01 = {
    'Name': 'Example EMRFS Sync Step',
    'ActionOnFailure': 'CONTINUE',
    'HadoopJarStep': {
        'Jar': 's3://elasticmapreduce/libs/script-runner/script-runner.jar',
        'Args': [
            '/home/hadoop/bin/emrfs',
            'sync',
            's3://elasticmapreduce/samples/cloudfront'
        ]
    }
}

# Add the step(s)
emr_client = boto3.client('emr')
try:
    response = emr_client.add_job_flow_steps(JobFlowId=job_flow_id,
                                             Steps=[json.dumps(job_flow_step_01)])
except ClientError as e:
    print(e.response['Error']['Message'])
    exit(1)

# Output the IDs of the added steps
print('Step IDs:')
for stepId in response['StepIds']:
    print(stepId)
# snippet-end:[emr.python.addstep.emrfs]
