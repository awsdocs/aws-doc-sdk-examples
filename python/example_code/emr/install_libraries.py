# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to copy a shell script to Amazon EMR cluster instances and run them
to install additional libraries on the instances. This can be used to automate
instance management and is an alternative to connecting through SSH to run the
script manually.
"""

# snippet-start:[emr.python.jupyterhub.installlibraries]
import argparse
import time
import boto3


def install_libraries_on_core_nodes(
        cluster_id, script_path, emr_client, ssm_client):
    """
    Copies and runs a shell script on the core nodes in the cluster.

    :param cluster_id: The ID of the cluster.
    :param script_path: The path to the script, typically an Amazon S3 object URL.
    :param emr_client: The Boto3 Amazon EMR client.
    :param ssm_client: The Boto3 AWS Systems Manager client.
    """
    core_nodes = emr_client.list_instances(
        ClusterId=cluster_id, InstanceGroupTypes=['CORE'])['Instances']
    core_instance_ids = [node['Ec2InstanceId'] for node in core_nodes]
    print(f"Found core instances: {core_instance_ids}.")

    commands = [
        # Copy the shell script from Amazon S3 to each node instance.
        f"aws s3 cp {script_path} /home/hadoop",
        # Run the shell script to install libraries on each node instance.
        "bash /home/hadoop/install_libraries.sh"]
    for command in commands:
        print(f"Sending '{command}' to core instances...")
        command_id = ssm_client.send_command(
            InstanceIds=core_instance_ids,
            DocumentName='AWS-RunShellScript',
            Parameters={"commands": [command]},
            TimeoutSeconds=3600)['Command']['CommandId']
        while True:
            # Verify the previous step succeeded before running the next step.
            cmd_result = ssm_client.list_commands(
                CommandId=command_id)['Commands'][0]
            if cmd_result['StatusDetails'] == 'Success':
                print(f"Command succeeded.")
                break
            elif cmd_result['StatusDetails'] in ['Pending', 'InProgress']:
                print(f"Command status is {cmd_result['StatusDetails']}, waiting...")
                time.sleep(10)
            else:
                print(f"Command status is {cmd_result['StatusDetails']}, quitting.")
                raise RuntimeError(
                    f"Command {command} failed to run. "
                    f"Details: {cmd_result['StatusDetails']}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('cluster_id', help="The ID of the cluster.")
    parser.add_argument('script_path', help="The path to the script in Amazon S3.")
    args = parser.parse_args()

    emr_client = boto3.client('emr')
    ssm_client = boto3.client('ssm')

    install_libraries_on_core_nodes(
        args.cluster_id, args.script_path, emr_client, ssm_client)


if __name__ == '__main__':
    main()
# snippet-end:[emr.python.jupyterhub.installlibraries]
