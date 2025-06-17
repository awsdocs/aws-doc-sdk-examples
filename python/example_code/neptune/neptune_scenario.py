# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0


# snippet-start:[neptune.python.scenario.main]
import boto3
import time
import botocore.exceptions

# Constants used in this scenario
POLL_INTERVAL_SECONDS = 10
TIMEOUT_SECONDS = 1200  # 20 minutes

# snippet-start:[neptune.python.delete.cluster.main]
from botocore.exceptions import ClientError

def delete_db_cluster(neptune_client, cluster_id: str):
    """
    Deletes a Neptune DB cluster and throws exceptions to the caller.

    Args:
        neptune_client (boto3.client): The Neptune client object.
        cluster_id (str): The ID of the Neptune DB cluster to be deleted.

    Raises:
        ClientError: If the delete operation fails.
    """
    request = {
        'DBClusterIdentifier': cluster_id,
        'SkipFinalSnapshot': True
    }

    try:
        print(f"Deleting DB Cluster: {cluster_id}")
        neptune_client.delete_db_cluster(**request)
    except ClientError as e:
        raise


# snippet-end:[neptune.python.delete.cluster.main]

def format_elapsed_time(seconds: int) -> str:
    mins, secs = divmod(seconds, 60)
    hours, mins = divmod(mins, 60)
    return f"{hours:02}:{mins:02}:{secs:02}"


# snippet-start:[neptune.python.delete.instance.main]
def delete_db_instance(neptune_client, instance_id: str):
    """
    Deletes a Neptune DB instance and waits for its deletion to complete.
    Raises exception to be handled by calling code.
    """
    print(f"Initiating deletion of DB Instance: {instance_id}")
    try:
        neptune_client.delete_db_instance(
            DBInstanceIdentifier=instance_id,
            SkipFinalSnapshot=True
        )

        print(f"Waiting for DB Instance '{instance_id}' to be deleted...")
        waiter = neptune_client.get_waiter('db_instance_deleted')
        waiter.wait(
            DBInstanceIdentifier=instance_id,
            WaiterConfig={
                'Delay': 30,
                'MaxAttempts': 40
            }
        )

        print(f"DB Instance '{instance_id}' successfully deleted.")
    except ClientError as e:
        raise


# snippet-end:[neptune.python.delete.instance.main]

# snippet-start:[neptune.python.delete.subnet.group.main]
def delete_db_subnet_group(neptune_client, subnet_group_name):
    """
    Deletes a Neptune DB subnet group synchronously using Boto3.

    :param subnet_group_name: The name of the DB subnet group to delete.
    """
    delete_group_request = {
        'DBSubnetGroupName': subnet_group_name
    }
    try:
        neptune_client.delete_db_subnet_group(**delete_group_request)
        print(f"️ Deleting Subnet Group: {subnet_group_name}")
    except ClientError as e:
        raise


# snippet-end:[neptune.python.delete.subnet.group.main]

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


# snippet-start:[neptune.python.start.cluster.main]
def start_db_cluster(neptune_client, cluster_identifier: str):
    """
    Starts an Amazon Neptune DB cluster and waits until it reaches 'available'.

    Args:
        neptune_client (boto3.client): The Neptune client.
        cluster_identifier (str): The DB cluster identifier.

    Raises:
        ClientError: Propagates AWS API issues like resource not found.
        RuntimeError: If cluster doesn't reach 'available' within timeout.
    """
    try:
        # Initial wait in case the cluster was just stopped
        time.sleep(30)
        neptune_client.start_db_cluster(DBClusterIdentifier=cluster_identifier)
    except ClientError:
        # Immediately propagate any AWS API error
        raise

    # Poll until cluster status is 'available'
    start_time = time.time()
    paginator = neptune_client.get_paginator('describe_db_clusters')

    while True:
        try:
            pages = paginator.paginate(DBClusterIdentifier=cluster_identifier)
            clusters = []
            for page in pages:
                clusters.extend(page.get('DBClusters', []))
        except ClientError:
            raise

        status = clusters[0].get('Status') if clusters else None
        elapsed = time.time() - start_time

        print(f"\rElapsed: {int(elapsed)}s – Cluster status: {status}", end="", flush=True)

        if status and status.lower() == 'available':
            print(f"\n🎉 Cluster '{cluster_identifier}' is available.")
            return

        if elapsed > TIMEOUT_SECONDS:
            raise RuntimeError(f"Timeout waiting for cluster '{cluster_identifier}' to become available.")

        time.sleep(POLL_INTERVAL_SECONDS)


# snippet-end:[neptune.python.start.cluster.main]

# snippet-start:[neptune.python.stop.cluster.main]
def stop_db_cluster(neptune_client, cluster_identifier: str):
    """
    Stops an Amazon Neptune DB cluster and waits until it's fully stopped.

    Args:
        neptune_client (boto3.client): The Neptune client.
        cluster_identifier (str): The DB cluster identifier.

    Raises:
        ClientError: For AWS API errors (e.g., resource not found).
        RuntimeError: If the cluster doesn't stop within the timeout.
    """
    try:
        neptune_client.stop_db_cluster(DBClusterIdentifier=cluster_identifier)
    except ClientError:
        # Propagate AWS-level exceptions immediately
        raise

    start_time = time.time()
    paginator = neptune_client.get_paginator('describe_db_clusters')

    while True:
        try:
            pages = paginator.paginate(DBClusterIdentifier=cluster_identifier)
            clusters = []
            for page in pages:
                clusters.extend(page.get('DBClusters', []))
        except ClientError:
            # For example, cluster might be already deleted/not found
            raise

        status = clusters[0].get('Status') if clusters else None
        elapsed = time.time() - start_time

        print(f"\rElapsed: {int(elapsed)}s – Cluster status: {status}", end="", flush=True)

        if status and status.lower() == 'stopped':
            print(f"\n Cluster '{cluster_identifier}' is now stopped.")
            return

        if elapsed > TIMEOUT_SECONDS:
            raise RuntimeError(f"Timeout waiting for cluster '{cluster_identifier}' to stop.")

        time.sleep(POLL_INTERVAL_SECONDS)


# snippet-end:[neptune.python.stop.cluster.main]

# snippet-start:[neptune.python.describe.cluster.main]
def describe_db_clusters(neptune_client, cluster_id: str):
    """
    Describes details of a Neptune DB cluster, paginating if needed.

    Args:
        neptune_client (boto3.client): The Neptune client.
        cluster_id (str): The ID of the cluster to describe.

    Raises:
        ClientError: If there's an AWS API error (e.g., cluster not found).
    """
    paginator = neptune_client.get_paginator('describe_db_clusters')
    try:
        pages = paginator.paginate(DBClusterIdentifier=cluster_id)
    except ClientError:
        raise

    found = False
    for page in pages:
        for cluster in page.get('DBClusters', []):
            found = True
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
            print(f"IAM Auth Enabled: {cluster.get('IAMDatabaseAuthenticationEnabled')}")
            print(f"Backup Retention Period: {cluster.get('BackupRetentionPeriod')} days")
            print(f"Preferred Backup Window: {cluster.get('PreferredBackupWindow')}")
            print(f"Preferred Maintenance Window: {cluster.get('PreferredMaintenanceWindow')}")
            print("------")

    if not found:
        # Handle empty result set as not found
        raise ClientError(
            {"Error": {"Code": "DBClusterNotFound", "Message": f"No cluster found with ID '{cluster_id}'"}},
            "DescribeDBClusters"
        )


# snippet-end:[neptune.python.describe.cluster.main]

# snippet-start:[neptune.python.describe.dbinstance.main]
def check_instance_status(neptune_client, instance_id: str, desired_status: str):
    """
    Polls the status of a Neptune DB instance until it reaches desired_status.
    Uses pagination via describe_db_instances — even for a single instance.

    Raises:
      ClientError: If describe_db_instances fails (e.g., instance not found).
      RuntimeError: If timeout expires before reaching desired status.
    """
    paginator = neptune_client.get_paginator('describe_db_instances')
    start_time = time.time()

    while True:
        try:
            # Paginate responses for the specified instance ID
            pages = paginator.paginate(DBInstanceIdentifier=instance_id)
            instances = []
            for page in pages:
                instances.extend(page.get('DBInstances', []))
        except ClientError:
            # Let the calling code handle errors such as ResourceNotFound
            raise

        current_status = instances[0].get('DBInstanceStatus') if instances else None
        elapsed = int(time.time() - start_time)

        print(f"\rElapsed: {format_elapsed_time(elapsed)}  Status: {current_status}", end="", flush=True)

        if current_status and current_status.lower() == desired_status.lower():
            print(f"\nInstance '{instance_id}' reached '{desired_status}' in {format_elapsed_time(elapsed)}.")
            return

        if elapsed > TIMEOUT_SECONDS:
            raise RuntimeError(f"Timeout waiting for '{instance_id}' to reach '{desired_status}'")

        time.sleep(POLL_INTERVAL_SECONDS)


# snippet-end:[neptune.python.describe.dbinstance.main]

# snippet-start:[neptune.python.create.dbinstance.main]
def create_db_instance(neptune_client, db_instance_id: str, db_cluster_id: str) -> str:
    try:
        request = {
            'DBInstanceIdentifier': db_instance_id,
            'DBInstanceClass': 'db.r5.large',
            'Engine': 'neptune',
            'DBClusterIdentifier': db_cluster_id
        }

        print(f"Creating Neptune DB Instance: {db_instance_id}")
        response = neptune_client.create_db_instance(**request)

        instance = response.get('DBInstance')
        if not instance or 'DBInstanceIdentifier' not in instance:
            raise RuntimeError("Instance creation succeeded but no ID returned.")

        # Wait for it to become available
        print(f"Waiting for DB Instance '{db_instance_id}' to become available...")
        waiter = neptune_client.get_waiter('db_instance_available')
        waiter.wait(
            DBInstanceIdentifier=db_instance_id,
            WaiterConfig={'Delay': 30, 'MaxAttempts': 40}
        )

        print(f"DB Instance '{db_instance_id}' is now available.")
        return instance['DBInstanceIdentifier']

    except ClientError as e:
        raise ClientError(
            {
                "Error": {
                    "Code": e.response["Error"]["Code"],
                    "Message": f"Failed to create DB instance '{db_instance_id}': {e.response['Error']['Message']}"
                }
            },
            e.operation_name
        ) from e

    except Exception as e:
        raise RuntimeError(f"Unexpected error creating DB instance '{db_instance_id}': {e}") from e


# snippet-end:[neptune.python.create.dbinstance.main]

# snippet-start:[neptune.python.create.cluster.main]
def create_db_cluster(neptune_client, db_name: str) -> str:
    """
    Creates a Neptune DB cluster and returns its identifier.

    Args:
        neptune_client (boto3.client): The Neptune client object.
        db_name (str): The desired cluster identifier.

    Returns:
        str: The DB cluster identifier.

    Raises:
        ClientError: Wraps any AWS-side error for the calling code to handle.
        RuntimeError: If the call succeeds but no identifier is returned.
    """
    request = {
        'DBClusterIdentifier': db_name,
        'Engine': 'neptune',
        'DeletionProtection': False,
        'BackupRetentionPeriod': 1
    }

    try:
        response = neptune_client.create_db_cluster(**request)
        cluster = response.get('DBCluster') or {}

        cluster_id = cluster.get('DBClusterIdentifier')
        if not cluster_id:
            raise RuntimeError("Cluster created but no ID returned.")

        print(f"DB Cluster created: {cluster_id}")
        return cluster_id

    except ClientError as e:
        # enrich the message,
        # keep the AWS error code for downstream handling
        raise ClientError(
            {
                "Error": {
                    "Code": e.response["Error"]["Code"],
                    "Message": f"Failed to create DB cluster '{db_name}': {e.response['Error']['Message']}"
                }
            },
            e.operation_name
        ) from e

    except Exception as e:
        raise RuntimeError(f"Unexpected error creating DB cluster '{db_name}': {e}") from e


# snippet-end:[neptune.python.create.cluster.main]

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


# snippet-start:[neptune.python.create.subnet.main]
def create_subnet_group(neptune_client, group_name: str):
    """
    Creates a Neptune DB subnet group and returns its name and ARN.

    Args:
        neptune_client (boto3.client): The Neptune client object.
        group_name (str): The desired name of the subnet group.

    Returns:
        tuple(str, str): (subnet_group_name, subnet_group_arn)

    Raises:
        ClientError: If AWS returns an error.
        RuntimeError: For unexpected internal errors.
    """
    vpc_id = get_default_vpc_id()
    subnet_ids = get_subnet_ids(vpc_id)

    request = {
        'DBSubnetGroupName': group_name,
        'DBSubnetGroupDescription': 'My Neptune subnet group',
        'SubnetIds': subnet_ids,
        'Tags': [{'Key': 'Environment', 'Value': 'Dev'}]
    }

    try:
        response = neptune_client.create_db_subnet_group(**request)
        sg = response.get("DBSubnetGroup", {})

        name = sg.get("DBSubnetGroupName")
        arn = sg.get("DBSubnetGroupArn")

        if not name or not arn:
            raise RuntimeError("Response missing subnet group name or ARN.")

        print(f"Subnet group created: {name}")
        print(f"ARN: {arn}")

        return name, arn

    except ClientError as e:
        # Repackage with context, then throw
        raise ClientError(
            {
                "Error": {
                    "Code": e.response["Error"]["Code"],
                    "Message": f"Failed to create subnet group '{group_name}': {e.response['Error']['Message']}"
                }
            },
            e.operation_name
        ) from e

    except Exception as e:
        raise RuntimeError(f"Unexpected error creating subnet group '{group_name}': {e}") from e


# snippet-end:[neptune.python.create.subnet.main]

def wait_for_input_to_continue():
    input("\nPress <ENTER> to continue...")
    print("Continuing with the program...\n")


def run_scenario(neptune_client, subnet_group_name: str, db_instance_id: str, cluster_name: str):
    print("-" * 88)
    print("1. Create a Neptune DB Subnet Group")
    wait_for_input_to_continue()

    try:
        name, arn = create_subnet_group(neptune_client, subnet_group_name)
        print(f"Subnet group successfully created: {name}")

    except ClientError as ce:
        code = ce.response["Error"]["Code"]
        if code == "ServiceQuotaExceededException":
            print("You've hit the subnet group quota.")
        else:
            msg = ce.response["Error"]["Message"]
            print(f"AWS error [{code}]: {msg}")
            raise

    except RuntimeError as re:
        print(f"Runtime issue: {re}")

    print("-" * 88)

    print("2. Create a Neptune Cluster")
    wait_for_input_to_continue()
    try:
        db_cluster_id = create_db_cluster(neptune_client, cluster_name)
    except ClientError as ce:
        code = ce.response["Error"]["Code"]
        if code in ("ServiceQuotaExceededException", "DBClusterQuotaExceededFault"):
            print("You have exceeded the quota for Neptune DB clusters.")
        else:
            msg = ce.response["Error"]["Message"]
            print(f"AWS error [{code}]: {msg}")

    except RuntimeError as re:
        print(f"Runtime issue: {re}")

    except Exception as e:
        print(f" Unexpected error: {e}")
    print("-" * 88)

    print("-" * 88)
    print("3. Create a Neptune DB Instance")
    wait_for_input_to_continue()
    try:
        create_db_instance(neptune_client, db_instance_id, cluster_name)

    except ClientError as ce:
        error_code = ce.response["Error"]["Code"]
        if error_code == "ServiceQuotaExceededException":
            print("You have exceeded the quota for Neptune DB instances.")
        else:
            print(f"AWS error [{error_code}]: {ce.response['Error']['Message']}")
            raise  # Optionally rethrow

    except RuntimeError as re:
        print(f"Runtime error: {str(re)}")
    print("-" * 88)

    print("-" * 88)
    print("4. Check the status of the Neptune DB Instance")
    print("""
    Even though you're targeting a single DB instance, 
    describe_db_instances supports pagination and can return multiple pages. 

    Handling paginated responses ensures your method continues to work reliably 
    even if AWS returns large or paged results.
    """)
    wait_for_input_to_continue()

    try:
        check_instance_status(neptune_client, db_instance_id, "available")
    except ClientError as ce:
        code = ce.response['Error']['Code']
        if code in ('DBInstanceNotFound', 'DBInstanceNotFoundFault', 'ResourceNotFound'):
            print(f"Instance '{db_instance_id}' not found.")
        else:
            print(f"AWS error [{code}]: {ce.response['Error']['Message']}")
            raise
    except RuntimeError as re:
        print(f" Timeout: {re}")
    print("-" * 88)

    print("-" * 88)
    print("5. Show Neptune Cluster details")
    wait_for_input_to_continue()

    try:
        describe_db_clusters(neptune_client, db_cluster_id)
    except ClientError as ce:
        code = ce.response["Error"]["Code"]
        if code in ("DBClusterNotFound", "DBClusterNotFoundFault", "ResourceNotFound"):
            print(f"Cluster '{db_cluster_id}' not found.")
        else:
            print(f"AWS error [{code}]: {ce.response['Error']['Message']}")
            raise
    print("-" * 88)

    print("-" * 88)
    print("6. Stop the Amazon Neptune cluster")
    print("""
        Boto3 doesn't currently offer a 
        built-in waiter for stop_db_cluster, 
        This example implements a custom polling 
        strategy until the cluster is in a stopped state.
    
    """)
    wait_for_input_to_continue()
    try:
        stop_db_cluster(neptune_client, db_cluster_id)
        check_instance_status(neptune_client, db_instance_id, "stopped")
    except ClientError as ce:
        code = ce.response["Error"]["Code"]
        if code in ("DBClusterNotFoundFault", "DBClusterNotFound", "ResourceNotFoundFault"):
            print(f"Cluster '{db_cluster_id}' not found.")
        else:
            print(f"AWS error [{code}]: {ce.response['Error']['Message']}")
            raise
    print("-" * 88)

    print("-" * 88)
    print("7. Start the Amazon Neptune cluster")
    print("""
        Boto3 doesn't currently offer a 
        built-in waiter for start_db_cluster, 
        This example implements a custom polling 
        strategy until the cluster is in an available state.
        """)
    wait_for_input_to_continue()
    try:
        start_db_cluster(neptune_client, db_cluster_id)
        wait_for_cluster_status(neptune_client, db_cluster_id, "available")
        check_instance_status(neptune_client, db_instance_id, "available")

    except ClientError as ce:
        code = ce.response["Error"]["Code"]
        if code in ("DBClusterNotFoundFault", "DBClusterNotFound", "ResourceNotFoundFault"):
            print(f"Cluster '{db_cluster_id}' not found.")
        else:
            print(f"AWS error [{code}]: {ce.response['Error']['Message']}")
            raise

    except RuntimeError as re:
        # Handles timeout or other runtime issues
        print(f"Timeout or runtime error: {re}")

    else:
        # No exceptions occurred
        print("All Neptune resources are now available.")
    print("-" * 88)

    print("-" * 88)
    print("8. Delete the Neptune Assets")
    print("Would you like to delete the Neptune Assets? (y/n)")
    del_ans = input().strip().lower()

    if del_ans == "y":
        print("You selected to delete the Neptune assets.")
        try:
            delete_db_instance(neptune_client, db_instance_id)
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code == "DBInstanceNotFoundFault":
                print(f"Instance '{db_instance_id}' already deleted or doesn't exist.")
            else:
                raise  # re-raise if it's a different error

        try:
            delete_db_cluster(neptune_client, db_cluster_id)
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code == "DBClusterNotFoundFault":
                print(f"Cluster '{db_cluster_id}' already deleted or doesn't exist.")
            else:
                raise

        try:
            delete_db_subnet_group(neptune_client, subnet_group_name)
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code == "DBSubnetGroupNotFoundFault":
                print(f"Subnet group '{subnet_group_name}' already deleted or doesn't exist.")
            else:
                raise

        print("Neptune resources deleted successfully")

        print("-" * 88)


def main():
    neptune_client = boto3.client('neptune')

    # Customize the following names to match your Neptune setup
    # (You must change these to unique values for your environment)
    subnet_group_name = "neptuneSubnetGroup106"
    cluster_name = "neptuneCluster106"
    db_instance_id = "neptuneDB106"

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
# snippet-end:[neptune.python.scenario.main]
