#  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#  SPDX-License-Identifier: Apache-2.0


import boto3
import time
from datetime import timedelta
import botocore.exceptions

# Constants
POLL_INTERVAL_SECONDS = 10
TIMEOUT_SECONDS = 1200  # 20 minutes


def delete_db_subnet_group(neptune_client, subnet_group_name: str):
    request = {
        'DBSubnetGroupName': subnet_group_name
    }

    try:
        neptune_client.delete_db_subnet_group(**request)
        print(f"  Deleting Subnet Group: {subnet_group_name}")
    except (botocore.ClientError, botocore.BotoCoreError) as e:
        print(f" Failed to delete subnet group '{subnet_group_name}': {e}")


def delete_db_cluster(neptune_client, cluster_id: str):
    request = {
        'DBClusterIdentifier': cluster_id,
        'SkipFinalSnapshot': True
    }

    try:
        neptune_client.delete_db_cluster(**request)
        print(f" Deleting DB Cluster: {cluster_id}")
    except Exception as e:
        print(f" Failed to delete DB Cluster '{cluster_id}': {e}")


def format_elapsed_time(seconds: int) -> str:
    mins, secs = divmod(seconds, 60)
    hours, mins = divmod(mins, 60)
    return f"{hours:02}:{mins:02}:{secs:02}"


def wait_until_instance_deleted(
        neptune_client,
        instance_id: str,
        timeout_seconds: int = 20 * 60,
        poll_interval_seconds: int = 10
) -> bool:
    print(f" Waiting for instance '{instance_id}' to be deleted...")

    start_time = time.time()

    while True:
        try:
            describe_db_instances_request = {
                'DBInstanceIdentifier': instance_id
            }

            response = neptune_client.describe_db_instances(**describe_db_instances_request)
            instances = response.get('DBInstances', [])
            status = instances[0].get('DBInstanceStatus') if instances else "Unknown"
            elapsed = int(time.time() - start_time)

            print(f"\r  Waiting: Instance {instance_id} status: {status.ljust(10)} ({elapsed}s elapsed)", end="",
                  flush=True)

        except botocore.exceptions.ClientError as e:
            error_code = e.response['Error'].get('Code')
            if error_code == "DBInstanceNotFound":
                elapsed = int(time.time() - start_time)
                print(f"\n Instance '{instance_id}' deleted after {elapsed}s.")
                return True
            else:
                print(f"\n Error polling DB instance '{instance_id}': {error_code or 'Unknown'} — {e}")
                return False
        except Exception as e:
            print(f"\n Unexpected error while polling DB instance '{instance_id}': {e}")
            return False

        elapsed_seconds = time.time() - start_time
        if elapsed_seconds > timeout_seconds:
            print(f"\n Timeout: Instance '{instance_id}' was not deleted after {timeout_seconds // 60} minutes.")
            return False

        time.sleep(poll_interval_seconds)


def delete_db_instance(neptune_client, instance_id: str):
    delete_db_instance_request = {
        'DBInstanceIdentifier': instance_id,
        'SkipFinalSnapshot': True
    }

    neptune_client.delete_db_instance(**delete_db_instance_request)
    print(f"Deleting DB Instance: {instance_id}")


def wait_for_cluster_status(
        neptune_client,
        cluster_id: str,
        desired_status: str,
        timeout_seconds: int = TIMEOUT_SECONDS,
        poll_interval_seconds: int = POLL_INTERVAL_SECONDS
):
    """
    Waits for a Neptune DB cluster to reach a desired status.

    Args:
        neptune_client (boto3.client): The Amazon Neptune client.
        cluster_id (str): The identifier of the Neptune DB cluster.
        desired_status (str): The target status (e.g., "available", "stopped").
        timeout_seconds (int): Max time to wait in seconds (default: 1200).
        poll_interval_seconds (int): Polling interval in seconds (default: 10).

    Raises:
        RuntimeError: If the desired status is not reached before timeout.
    """
    print(f"Waiting for cluster '{cluster_id}' to reach status '{desired_status}'...")
    start_time = time.time()

    while True:
        # Prepare request object
        describe_cluster_request = {
            'DBClusterIdentifier': cluster_id
        }

        # Call the Neptune API
        response = neptune_client.describe_db_clusters(**describe_cluster_request)
        clusters = response.get('DBClusters', [])
        current_status = clusters[0].get('Status') if clusters else None
        elapsed_seconds = int(time.time() - start_time)

        status_str = current_status if current_status else "Unknown"
        print(
            f"\r Elapsed: {format_elapsed_time(elapsed_seconds):<20}  Cluster status: {status_str:<20}",
            end="", flush=True
        )

        if current_status and current_status.lower() == desired_status.lower():
            print(
                f"\nNeptune cluster reached desired status '{desired_status}' after {format_elapsed_time(elapsed_seconds)}."
            )
            return

        if elapsed_seconds > timeout_seconds:
            raise RuntimeError(f"Timeout waiting for Neptune cluster to reach status: {desired_status}")

        time.sleep(poll_interval_seconds)


def start_db_cluster(neptune_client, cluster_identifier):
    """
    Starts an Amazon Neptune DB cluster.

    Args:
        neptune_client (boto3.client): The Amazon Neptune client.
        cluster_identifier (str): The identifier of the DB cluster to start.
    """

    # Create the request dictionary
    start_db_cluster_request = {
        'DBClusterIdentifier': cluster_identifier
    }

    # Call the API to start the DB cluster
    neptune_client.start_db_cluster(**start_db_cluster_request)
    print(f"DB Cluster started: {cluster_identifier}")


def stop_db_cluster(neptune_client, cluster_identifier: str):
    """
    Stops an Amazon Neptune DB cluster.

    Args:
        neptune_client (boto3.client): The Amazon Neptune client.
        cluster_identifier (str): The identifier of the DB cluster to stop.
    """

    # Create the request dictionary
    stop_db_cluster_request = {
        'DBClusterIdentifier': cluster_identifier
    }

    # Call the API to stop the DB cluster
    neptune_client.stop_db_cluster(**stop_db_cluster_request)
    print(f"DB Cluster stopped: {cluster_identifier}")


def describe_db_clusters(neptune_client, cluster_id: str):
    """
    Describes the details of a specific Neptune DB cluster.

    Args:
        neptune_client (boto3.client): The Neptune client.
        cluster_id (str): The ID of the cluster to describe.
    """

    # Create the request dictionary
    describe_db_clusters_request = {
        'DBClusterIdentifier': cluster_id
    }

    # Call the service
    response = neptune_client.describe_db_clusters(**describe_db_clusters_request)
    clusters = response.get('DBClusters', [])

    for cluster in clusters:
        print(f"Cluster Identifier: {cluster.get('DBClusterIdentifier')}")
        print(f"Status: {cluster.get('Status')}")
        print(f"Engine: {cluster.get('Engine')}")
        print(f"Engine Version: {cluster.get('EngineVersion')}")
        print(f"Endpoint: {cluster.get('Endpoint')}")
        print(f"Reader Endpoint: {cluster.get('ReaderEndpoint')}")
        print(f"Availability Zones: {cluster.get('AvailabilityZones')}")
        print(f"Subnet Group: {cluster.get('DBSubnetGroup')}")
        print("VPC Security Groups:")
        for vpc_group in cluster.get('VpcSecurityGroups', []):
            print(f"  - {vpc_group.get('VpcSecurityGroupId')}")
        print(f"Storage Encrypted: {cluster.get('StorageEncrypted')}")
        print(f"IAM DB Auth Enabled: {cluster.get('IAMDatabaseAuthenticationEnabled')}")
        print(f"Backup Retention Period: {cluster.get('BackupRetentionPeriod')} days")
        print(f"Preferred Backup Window: {cluster.get('PreferredBackupWindow')}")
        print(f"Preferred Maintenance Window: {cluster.get('PreferredMaintenanceWindow')}")
        print("------")


def check_instance_status(neptune_client, instance_id: str, desired_status: str):
    start_time = time.time()

    while True:
        describe_instances_request = {
            'DBInstanceIdentifier': instance_id
        }

        response = neptune_client.describe_db_instances(**describe_instances_request)
        instances = response.get('DBInstances', [])
        current_status = instances[0].get('DBInstanceStatus') if instances else None
        elapsed_seconds = int(time.time() - start_time)

        print(f"\r Elapsed: {format_elapsed_time(elapsed_seconds)}  Status: {current_status}", end="", flush=True)

        if current_status and current_status.lower() == desired_status.lower():
            print(
                f"\nNeptune instance reached desired status '{desired_status}' after {format_elapsed_time(elapsed_seconds)}.")
            break

        if elapsed_seconds > TIMEOUT_SECONDS:
            raise RuntimeError(f"Timeout waiting for Neptune instance to reach status: {desired_status}")

        time.sleep(POLL_INTERVAL_SECONDS)


def create_db_instance(neptune_client, db_instance_id: str, db_cluster_id: str) -> str:
    create_db_instance_request = {
        'DBInstanceIdentifier': db_instance_id,
        'DBInstanceClass': 'db.r5.large',
        'Engine': 'neptune',
        'DBClusterIdentifier': db_cluster_id
    }

    response = neptune_client.create_db_instance(**create_db_instance_request)
    instance = response.get('DBInstance')
    if not instance or 'DBInstanceIdentifier' not in instance:
        raise RuntimeError("Instance creation succeeded but no ID returned.")

    instance_id = instance['DBInstanceIdentifier']
    print(f"Created Neptune DB Instance: {instance_id}")
    return instance_id


def create_db_cluster(neptune_client, db_name: str) -> str:
    create_db_cluster_request = {
        'DBClusterIdentifier': db_name,
        'Engine': 'neptune',
        'DeletionProtection': False,
        'BackupRetentionPeriod': 1
    }

    response = neptune_client.create_db_cluster(**create_db_cluster_request)
    cluster = response.get('DBCluster')
    if not cluster or 'DBClusterIdentifier' not in cluster:
        raise RuntimeError("Cluster creation succeeded but no ID returned.")

    cluster_id = cluster['DBClusterIdentifier']
    print(f"DB Cluster created: {cluster_id}")
    return cluster_id


def get_subnet_ids(vpc_id: str) -> list[str]:
    ec2_client = boto3.client('ec2')

    describe_subnets_request = {
        'Filters': [{'Name': 'vpc-id', 'Values': [vpc_id]}]
    }

    response = ec2_client.describe_subnets(**describe_subnets_request)
    subnets = response.get('Subnets', [])
    subnet_ids = [subnet['SubnetId'] for subnet in subnets if 'SubnetId' in subnet]
    return subnet_ids


def get_default_vpc_id() -> str:
    ec2_client = boto3.client('ec2')
    describe_vpcs_request = {
        'Filters': [{'Name': 'isDefault', 'Values': ['true']}]
    }

    response = ec2_client.describe_vpcs(**describe_vpcs_request)
    vpcs = response.get('Vpcs', [])
    if not vpcs:
        raise RuntimeError("No default VPC found in this region.")

    default_vpc_id = vpcs[0]['VpcId']
    print(f"Default VPC ID: {default_vpc_id}")
    return default_vpc_id


def create_subnet_group(neptune_client, group_name: str):
    vpc_id = get_default_vpc_id()
    subnet_ids = get_subnet_ids(vpc_id)

    create_subnet_group_request = {
        'DBSubnetGroupName': group_name,
        'DBSubnetGroupDescription': 'My Neptune subnet group',
        'SubnetIds': subnet_ids,
        'Tags': [{'Key': 'Environment', 'Value': 'Dev'}]
    }

    response = neptune_client.create_db_subnet_group(**create_subnet_group_request)
    subnet_group = response.get("DBSubnetGroup", {})
    name = subnet_group.get("DBSubnetGroupName")
    arn = subnet_group.get("DBSubnetGroupArn")

    print(f"Subnet group created: {name}")
    print(f"ARN: {arn}")


def wait_for_input_to_continue():
    while True:
        print("\nEnter 'c' followed by <ENTER> to continue:")
        user_input = input()
        if user_input.strip().lower() == "c":
            print("Continuing with the program...\n")
            break
        else:
            print("Invalid input. Please try again.")


def run_scenario(neptune_client, subnet_group_name: str, db_instance_id: str, cluster_name: str):
    print("-" * 88)
    print("1. Create a Neptune DB Subnet Group")
    wait_for_input_to_continue()
    create_subnet_group(neptune_client, subnet_group_name)
    print("-" * 88)

    print("2. Create a Neptune Cluster")
    wait_for_input_to_continue()
    db_cluster_id = create_db_cluster(neptune_client, cluster_name)
    print("-" * 88)

    print("3. Create a Neptune DB Instance")
    wait_for_input_to_continue()
    create_db_instance(neptune_client, db_instance_id, db_cluster_id)
    print("-" * 88)

    print("-" * 88)
    print("4. Check the status of the Neptune DB Instance")
    print("This may take several minutes...")
    wait_for_input_to_continue()
    check_instance_status(neptune_client, db_instance_id, "available")
    print("-" * 88)

    print("-" * 88)
    print("5. Show Neptune Cluster details")
    wait_for_input_to_continue()
    describe_db_clusters(neptune_client, db_cluster_id)
    print("-" * 88)

    print("-" * 88)
    print("6. Stop the Amazon Neptune cluster")
    print("""
        Once stopped, this step polls the status 
        until the cluster is in a stopped state.
    """)
    wait_for_input_to_continue()
    stop_db_cluster(neptune_client, db_cluster_id)
    check_instance_status(neptune_client, db_instance_id, "stopped")
    print("-" * 88)

    print("-" * 88)
    print("7. Start the Amazon Neptune cluster")
    print("""
        Once started, this step polls the clusters 
        status until it's in an available state.
        We will also poll the instance status.
        """)
    wait_for_input_to_continue()
    start_db_cluster(neptune_client, db_cluster_id)
    wait_for_cluster_status(neptune_client, db_cluster_id, "available")
    check_instance_status(neptune_client, db_instance_id, "available")
    print("-" * 88)

    print("-" * 88)
    print("8. Delete the Neptune Assets")
    print("Would you like to delete the Neptune Assets? (y/n)")
    del_ans = input().strip()
    if del_ans == "y":
        print("You selected to delete the Neptune assets.")
        delete_db_instance(neptune_client, db_instance_id)
        wait_until_instance_deleted(neptune_client, db_instance_id)
        delete_db_cluster(neptune_client, db_cluster_id)
        print("Neptune resources deleted successfully")

    print("-" * 88)


def main():
    neptune_client = boto3.client('neptune')
    subnet_group_name = "neptuneSubnetGroup75"
    cluster_name = "neptuneCluster75"
    db_instance_id = "neptuneDB75"

    print("""
Amazon Neptune is a fully managed graph database service by AWS...
Let's get started!
""")
    wait_for_input_to_continue()
    run_scenario(neptune_client, subnet_group_name, db_instance_id, cluster_name)

    print("""
    Thank you for checking out the Amazon Neptune Service Use demo.
    For more AWS code examples, visit:
    https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html
    """)


if __name__ == "__main__":
    main()
