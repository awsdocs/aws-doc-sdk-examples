# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon EMR API to create
two kinds of clusters:
    * A short-lived cluster that runs a single step to estimate the value of pi and
      then automatically terminates.
    * A long-lived cluster that runs several steps to query for top-rated items from
      historical Amazon review data. The cluster is manually terminated at the end of
      the demo.

The demos also create several other AWS resources:
    * An Amazon S3 bucket to store demo scripts and data.
    * AWS Identity and Access Management (IAM) security roles used by the demo.
    * Amazon Elastic Compute Cloud (Amazon EC2) security groups used by the demo.
"""

import argparse
import logging
import json
import sys
import time

import boto3
from botocore.exceptions import ClientError

import emr_basics

logger = logging.getLogger(__name__)


def status_poller(intro, done_status, func):
    """
    Polls a function for status, sleeping for 10 seconds between each query,
    until the specified status is returned.

    :param intro: An introductory sentence that informs the reader what we're
                  waiting for.
    :param done_status: The status we're waiting for. This function polls the status
                        function until it returns the specified status.
    :param func: The function to poll for status. This function must eventually
                 return the expected done_status or polling will continue indefinitely.
    """
    emr_basics.logger.setLevel(logging.WARNING)
    status = None
    print(intro)
    print("Current status: ", end='')
    while status != done_status:
        prev_status = status
        status = func()
        if prev_status == status:
            print('.', end='')
        else:
            print(status, end='')
        sys.stdout.flush()
        time.sleep(10)
    print()
    emr_basics.logger.setLevel(logging.INFO)


def setup_bucket(bucket_name, script_file_name, script_key, s3_resource):
    """
    Creates an Amazon S3 bucket and uploads the specfied script file to it.

    :param bucket_name: The name of the bucket to create.
    :param script_file_name: The name of the script file to upload.
    :param script_key: The key of the script object in the Amazon S3 bucket.
    :param s3_resource: The Boto3 Amazon S3 resource object.
    :return: The newly created bucket.
    """
    try:
        bucket = s3_resource.create_bucket(
            Bucket=bucket_name,
            CreateBucketConfiguration={
                'LocationConstraint': s3_resource.meta.client.meta.region_name
            }
        )
        bucket.wait_until_exists()
        logger.info("Created bucket %s.", bucket_name)
    except ClientError:
        logger.exception("Couldn't create bucket %s.", bucket_name)
        raise

    try:
        bucket.upload_file(script_file_name, script_key)
        logger.info(
            "Uploaded script %s to %s.", script_file_name,
            f'{bucket_name}/{script_key}')
    except ClientError:
        logger.exception("Couldn't upload %s to %s.", script_file_name, bucket_name)
        raise

    return bucket


def delete_bucket(bucket):
    """
    Deletes all objects in the specified bucket and deletes the bucket.

    :param bucket: The bucket to delete.
    """
    try:
        bucket.objects.delete()
        bucket.delete()
        logger.info("Emptied and removed bucket %s.", bucket.name)
    except ClientError:
        logger.exception("Couldn't remove bucket %s.", bucket.name)
        raise


def create_roles(job_flow_role_name, service_role_name, iam_resource):
    """
    Creates IAM roles for the job flow and for the service.

    The job flow role is assumed by the cluster's Amazon EC2 instances and grants
    them broad permission to use services like Amazon DynamoDB and Amazon S3.

    The service role is assumed by Amazon EMR and grants it permission to use various
    Amazon EC2, Amazon S3, and other actions.

    For demo purposes, these roles are fairly permissive. In practice, it's more
    secure to restrict permissions to the minimum needed to perform the required
    tasks.

    :param job_flow_role_name: The name of the job flow role.
    :param service_role_name: The name of the service role.
    :param iam_resource: The Boto3 IAM resource object.
    :return: The newly created roles.
    """
    try:
        job_flow_role = iam_resource.create_role(
            RoleName=job_flow_role_name,
            AssumeRolePolicyDocument=json.dumps({
                "Version": "2008-10-17",
                "Statement": [{
                        "Effect": "Allow",
                        "Principal": {
                            "Service": "ec2.amazonaws.com"
                        },
                        "Action": "sts:AssumeRole"
                }]
            })
        )
        waiter = iam_resource.meta.client.get_waiter('role_exists')
        waiter.wait(RoleName=job_flow_role_name)
        logger.info("Created job flow role %s.", job_flow_role_name)
    except ClientError:
        logger.exception("Couldn't create job flow role %s.", job_flow_role_name)
        raise

    try:
        job_flow_role.attach_policy(
            PolicyArn=
            "arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceforEC2Role"
        )
        logger.info("Attached policy to role %s.", job_flow_role_name)
    except ClientError:
        logger.exception("Couldn't attach policy to role %s.", job_flow_role_name)
        raise

    try:
        job_flow_inst_profile = iam_resource.create_instance_profile(
            InstanceProfileName=job_flow_role_name)
        job_flow_inst_profile.add_role(RoleName=job_flow_role_name)
        logger.info(
            "Created instance profile %s and added job flow role.", job_flow_role_name)
    except ClientError:
        logger.exception("Couldn't create instance profile %s.", job_flow_role_name)
        raise

    try:
        service_role = iam_resource.create_role(
            RoleName=service_role_name,
            AssumeRolePolicyDocument=json.dumps({
                "Version": "2008-10-17",
                "Statement": [{
                        "Sid": "",
                        "Effect": "Allow",
                        "Principal": {
                            "Service": "elasticmapreduce.amazonaws.com"
                        },
                        "Action": "sts:AssumeRole"
                }]
            })
        )
        waiter = iam_resource.meta.client.get_waiter('role_exists')
        waiter.wait(RoleName=service_role_name)
        logger.info("Created service role %s.", service_role_name)
    except ClientError:
        logger.exception("Couldn't create service role %s.", service_role_name)
        raise

    try:
        service_role.attach_policy(
            PolicyArn='arn:aws:iam::aws:policy/service-role/AmazonElasticMapReduceRole'
        )
        logger.info("Attached policy to service role %s.", service_role_name)
    except ClientError:
        logger.exception(
            "Couldn't attach policy to service role %s.", service_role_name)
        raise

    return job_flow_role, service_role


def delete_roles(roles):
    """
    Deletes the roles created for this demo.

    :param roles: The roles to delete.
    """
    try:
        for role in roles:
            for policy in role.attached_policies.all():
                role.detach_policy(PolicyArn=policy.arn)
            for inst_profile in role.instance_profiles.all():
                inst_profile.remove_role(RoleName=role.name)
                inst_profile.delete()
            role.delete()
            logger.info("Detached policies and deleted role %s.", role.name)
    except ClientError:
        logger.exception("Couldn't delete roles %s.", [role.name for role in roles])
        raise


def create_security_groups(prefix, ec2_resource):
    """
    Creates Amazon EC2 security groups for the instances contained in the cluster.

    When the cluster is created, Amazon EMR adds all required rules to these
    security groups. Because this demo needs only the default rules, it creates
    empty security groups and lets Amazon EMR fill them in.

    :param prefix: The name prefix for the security groups.
    :param ec2_resource: The Boto3 Amazon EC2 resource object.
    :return: The newly created security groups.
    """
    try:
        default_vpc = list(ec2_resource.vpcs.filter(
            Filters=[{'Name': 'isDefault', 'Values': ['true']}]))[0]
        logger.info("Got default VPC %s.", default_vpc.id)
    except ClientError:
        logger.exception("Couldn't get VPCs.")
        raise
    except IndexError:
        logger.exception("No default VPC in the list.")
        raise

    groups = {'manager': None, 'worker': None}
    for group in groups.keys():
        try:
            groups[group] = default_vpc.create_security_group(
                GroupName=f'{prefix}-{group}', Description=f"EMR {group} group.")
            logger.info(
                "Created security group %s in VPC %s.",
                groups[group].id, default_vpc.id)
        except ClientError:
            logger.exception("Couldn't create security group.")
            raise

    return groups


def delete_security_groups(security_groups):
    """
    Deletes the security groups used by the demo. When there are dependencies
    on a security group, it cannot be deleted. Because it can take some time
    to release all dependencies after a cluster is terminated, this function retries
    the delete until it succeeds.

    :param security_groups: The security groups to delete.
    """
    try:
        for sg in security_groups.values():
            sg.revoke_ingress(IpPermissions=sg.ip_permissions)
        max_tries = 5
        while True:
            try:
                for sg in security_groups.values():
                    sg.delete()
                break
            except ClientError as error:
                max_tries -= 1
                if max_tries > 0 and \
                        error.response['Error']['Code'] == 'DependencyViolation':
                    logger.warning(
                        "Attempt to delete security group got DependencyViolation. "
                        "Waiting for 10 seconds to let things propagate.")
                    time.sleep(10)
                else:
                    raise
        logger.info("Deleted security groups %s.", security_groups)
    except ClientError:
        logger.exception("Couldn't delete security groups %s.", security_groups)
        raise


def add_top_product_step(
        count, category, keyword, cluster_id, bucket, script_key, emr_client):
    """
    Adds a step to a cluster that queries historical Amazon review data to find
    the top products in the specified category that contain a keyword.

    This function then polls the cluster until the step completes.

    After the step completes, this function gets the output from the bucket
    and prints it in the command window.

    :param count: The number of results to return.
    :param category: The product category, such as Books or Grocery.
    :param keyword: Each returned product must contain this keyword in its title.
    :param cluster_id: The ID of the cluster that runs the step.
    :param bucket: The Amazon S3 bucket that contains the script for the step and
                   that stores the output from the step.
    :param script_key: The object key of the script that identifies it in the bucket.
    :param emr_client: The Boto3 Amazon EMR client object.
    """
    print(f"Adding a step to calculate the top {count} products in {category} that "
          f"contain the word '{keyword}'...")
    output_folder = f'top-{count}-{category}-{keyword}'
    step_id = emr_basics.add_step(
        cluster_id, f'Calculate {output_folder}',
        f's3://{bucket.name}/{script_key}',
        ['--category', category, '--title_keyword', keyword,
         '--count', count, '--output_uri', f's3://{bucket.name}/{output_folder}'],
        emr_client)

    status_poller(
        "Waiting for step to complete...",
        'COMPLETED',
        lambda:
        emr_basics.describe_step(cluster_id, step_id, emr_client)['Status']['State'])

    print(f"The output for this step is in Amazon S3 bucket "
          f"{bucket.name}/{output_folder}.")
    print('-'*88)
    for obj in bucket.objects.filter(Prefix=output_folder):
        print(obj.get()['Body'].read().decode())
    print('-'*88)


def demo_short_lived_cluster():
    """
    Shows how to create a short-lived cluster that runs a step and automatically
    terminates after the step completes.
    """
    print('-'*88)
    print(f"Welcome to the Amazon EMR short-lived cluster demo.")
    print('-'*88)

    prefix = f'demo-short-emr'

    s3_resource = boto3.resource('s3')
    iam_resource = boto3.resource('iam')
    emr_client = boto3.client('emr')
    ec2_resource = boto3.resource('ec2')

    # Set up resources for the demo.
    bucket_name = f'{prefix}-{time.time_ns()}'
    script_file_name = 'pyspark_estimate_pi.py'
    script_key = f'scripts/{script_file_name}'
    bucket = setup_bucket(bucket_name, script_file_name, script_key, s3_resource)
    job_flow_role, service_role = create_roles(
        f'{prefix}-ec2-role', f'{prefix}-service-role', iam_resource)
    security_groups = create_security_groups(prefix, ec2_resource)

    # Run the job.
    output_prefix = 'pi-calc-output'
    pi_step = {
        'name': 'estimate-pi-step',
        'script_uri': f's3://{bucket_name}/{script_key}',
        'script_args':
            ['--partitions', '3', '--output_uri',
             f's3://{bucket_name}/{output_prefix}']
    }
    print("Wait for 10 seconds to give roles and profiles time to propagate...")
    time.sleep(10)
    max_tries = 5
    while True:
        try:
            cluster_id = emr_basics.run_job_flow(
                f'{prefix}-cluster', f's3://{bucket_name}/logs',
                False, ['Hadoop', 'Hive', 'Spark'], job_flow_role, service_role,
                security_groups, [pi_step], emr_client)
            print(f"Running job flow for cluster {cluster_id}...")
            break
        except ClientError as error:
            max_tries -= 1
            if max_tries > 0 and \
                    error.response['Error']['Code'] == 'ValidationException':
                print("Instance profile is not ready, let's give it more time...")
                time.sleep(10)
            else:
                raise

    status_poller(
        "Waiting for cluster, this typically takes several minutes...",
        'RUNNING',
        lambda: emr_basics.describe_cluster(cluster_id, emr_client)['Status']['State'],
    )
    status_poller(
        "Waiting for step to complete...",
        'PENDING',
        lambda: emr_basics.list_steps(cluster_id, emr_client)[0]['Status']['State'])
    status_poller(
        "Waiting for cluster to terminate.",
        'TERMINATED',
        lambda: emr_basics.describe_cluster(cluster_id, emr_client)['Status']['State']
    )

    print(f"Job complete!. The script, logs, and output for this demo are in "
          f"Amazon S3 bucket {bucket_name}. The output is:")
    for obj in bucket.objects.filter(Prefix=output_prefix):
        print(obj.get()['Body'].read().decode())

    # Clean up demo resources (if you want to).
    remove_everything = input(
            f"Do you want to delete the security roles, groups, and bucket (y/n)? ")
    if remove_everything.lower() == 'y':
        delete_security_groups(security_groups)
        delete_roles([job_flow_role, service_role])
        delete_bucket(bucket)
    else:
        print(
            f"Remember that objects kept in an Amazon S3 bucket can incur charges"
            f"against your account.")
    print("Thanks for watching!")


def demo_long_lived_cluster():
    """
    Shows how to create a long-lived cluster that waits after all steps are run so
    that more steps can be run. At the end of the demo, the cluster is optionally
    terminated.
    """
    print('-'*88)
    print(f"Welcome to the Amazon EMR long-lived cluster demo.")
    print('-'*88)

    prefix = 'demo-long-emr'

    s3_resource = boto3.resource('s3')
    iam_resource = boto3.resource('iam')
    emr_client = boto3.client('emr')
    ec2_resource = boto3.resource('ec2')

    # Set up resources for the demo.
    bucket_name = f'{prefix}-{time.time_ns()}'
    script_file_name = 'pyspark_top_product_keyword.py'
    script_key = f'scripts/{script_file_name}'
    bucket = setup_bucket(bucket_name, script_file_name, script_key, s3_resource)
    job_flow_role, service_role = \
        create_roles(f'{prefix}-ec2-role', f'{prefix}-service-role', iam_resource)
    security_groups = create_security_groups(prefix, ec2_resource)
    print("Wait for 10 seconds to give roles and profiles time to propagate...")
    time.sleep(10)

    max_tries = 5
    while True:
        try:
            cluster_id = emr_basics.run_job_flow(
                f'{prefix}-cluster', f's3://{bucket_name}/logs',
                True, ['Hadoop', 'Hive', 'Spark'], job_flow_role, service_role,
                security_groups, [], emr_client)
            print(f"Running job flow for cluster {cluster_id}...")
            break
        except ClientError as error:
            max_tries -= 1
            if max_tries > 0 and \
                    error.response['Error']['Code'] == 'ValidationException':
                print("Instance profile is not ready, let's give it more time...")
                time.sleep(10)
            else:
                raise
    status_poller(
        "Waiting for cluster, this typically takes several minutes...",
        'WAITING',
        lambda: emr_basics.describe_cluster(cluster_id, emr_client)['Status']['State'],
    )

    add_top_product_step(
        '20', 'Books', 'fire', cluster_id, bucket, script_key, emr_client)

    add_top_product_step(
        '20', 'Grocery', 'cheese', cluster_id, bucket, script_key, emr_client)

    review_bucket_folders = s3_resource.meta.client.list_objects_v2(
        Bucket='demo-reviews-pds', Prefix='parquet/', Delimiter='/', MaxKeys=100)
    categories = [
        cat['Prefix'].split('=')[1][:-1] for cat in
        review_bucket_folders['CommonPrefixes']]
    while True:
        while True:
            input_cat = input(
                f"Your turn! Possible categories are: {categories}. Which category "
                f"would you like to search (enter 'none' when you're done)? ")
            if input_cat.lower() == 'none' or input_cat in categories:
                break
            elif input_cat not in categories:
                print(f"Sorry, {input_cat} is not an allowed category!")
        if input_cat.lower() == 'none':
            break
        else:
            input_keyword = input("What keyword would you like to search for? ")
            input_count = input("How many items would you like to list? ")
            add_top_product_step(
                input_count, input_cat, input_keyword, cluster_id, bucket, script_key,
                emr_client)

    # Clean up demo resources (if you want to).
    remove_everything = input(
            f"Do you want to terminate the cluster and delete the security roles, "
            f"groups, bucket, and all of its contents (y/n)? ")
    if remove_everything.lower() == 'y':
        emr_basics.terminate_cluster(cluster_id, emr_client)
        status_poller(
            "Waiting for cluster to terminate.",
            'TERMINATED',
            lambda: emr_basics.describe_cluster(cluster_id, emr_client)['Status'][
                'State']
        )
        delete_security_groups(security_groups)
        delete_roles([job_flow_role, service_role])
        delete_bucket(bucket)
    else:
        print(
            f"Remember that running Amazon EMR clusters and objects kept in an "
            f"Amazon S3 bucket can incur charges against your account.")
    print("Thanks for watching!")


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    parser = argparse.ArgumentParser()
    parser.add_argument('demo_type', choices=['short-lived', 'long-lived'])
    args = parser.parse_args()
    if args.demo_type == 'short-lived':
        demo_short_lived_cluster()
    elif args.demo_type == 'long-lived':
        demo_long_lived_cluster()
