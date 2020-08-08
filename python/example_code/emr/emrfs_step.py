# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to run an EMRFS command as a job step on an Amazon EMR cluster. This
can be used to automate ERMFS commands and is an alternate to connecting through
SSH to run the commands manually.
"""

# snippet-start:[emr.python.addstep.emrfs]
import boto3
from botocore.exceptions import ClientError


def add_emrfs_step(command, bucket_url, cluster_id, emr_client):
    """
    Add an EMRFS command as a job flow step to an existing cluster.

    :param command: The EMRFS command to run.
    :param bucket_url: The URL of a bucket that contains tracking metadata.
    :param cluster_id: The ID of the cluster to update.
    :param emr_client: The Boto3 Amazon EMR client object.
    :return: The ID of the added job flow step. Status can be tracked by calling
             the emr_client.describe_step() function.
    """
    job_flow_step = {
        'Name': 'Example EMRFS Command Step',
        'ActionOnFailure': 'CONTINUE',
        'HadoopJarStep': {
            'Jar': 'command-runner.jar',
            'Args': [
                '/usr/bin/emrfs',
                command,
                bucket_url
            ]
        }
    }

    try:
        response = emr_client.add_job_flow_steps(
            JobFlowId=cluster_id, Steps=[job_flow_step])
        step_id = response['StepIds'][0]
        print(f"Added step {step_id} to cluster {cluster_id}.")
    except ClientError:
        print(f"Couldn't add a step to cluster {cluster_id}.")
        raise
    else:
        return step_id


def usage_demo():
    emr_client = boto3.client('emr')
    # Assumes the first waiting cluster has EMRFS enabled and has created metadata
    # with the default name of 'EmrFSMetadata'.
    cluster = emr_client.list_clusters(ClusterStates=['WAITING'])['Clusters'][0]
    add_emrfs_step(
        'sync', 's3://elasticmapreduce/samples/cloudfront', cluster['Id'], emr_client)


if __name__ == '__main__':
    usage_demo()
# snippet-end:[emr.python.addstep.emrfs]
