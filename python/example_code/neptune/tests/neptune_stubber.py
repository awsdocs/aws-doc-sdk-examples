# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.stub import Stubber

class Neptune:
    def __init__(self, client):
        self.client = client
        self.stubber = Stubber(client)
        self.stubber.activate()

    def stub_create_db_subnet_group(self, group_name, subnet_ids, group_arn=None, error_code=None, description=None, tags=None):
        expected_params = {
            "DBSubnetGroupName": group_name,
            "DBSubnetGroupDescription": description or f"Subnet group for {group_name}",
            "SubnetIds": subnet_ids,
        }
        if tags:
            expected_params["Tags"] = tags

        if error_code:
            self.stubber.add_client_error(
                "create_db_subnet_group",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params
            )
        else:
            response = {
                "DBSubnetGroup": {
                    "DBSubnetGroupName": group_name,
                }
            }
            if group_arn:
                response["DBSubnetGroup"]["DBSubnetGroupArn"] = group_arn

            self.stubber.add_response(
                "create_db_subnet_group",
                response,
                expected_params
            )

    def stub_create_db_cluster(self, cluster_id=None, error_code=None,
                               backup_retention_period=None, deletion_protection=None, engine=None):
        expected_params = {
            "DBClusterIdentifier": cluster_id,
        }
        if backup_retention_period is not None:
            expected_params["BackupRetentionPeriod"] = backup_retention_period
        if deletion_protection is not None:
            expected_params["DeletionProtection"] = deletion_protection
        if engine is not None:
            expected_params["Engine"] = engine

        if error_code:
            self.stubber.add_client_error(
                "create_db_cluster",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params
            )
        else:
            response = {
                "DBCluster": {
                    "DBClusterIdentifier": cluster_id,
                }
            }
            self.stubber.add_response(
                "create_db_cluster",
                response,
                expected_params
            )

    def stub_create_db_instance(self, instance_id, cluster_id, error_code=None):
        expected_params = {
            "DBInstanceIdentifier": instance_id,
            "DBInstanceClass": "db.r5.large",
            "Engine": "neptune",
            "DBClusterIdentifier": cluster_id
        }

        if error_code:
            self.stubber.add_client_error(
                "create_db_instance",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params
            )
        else:
            response = {
                "DBInstance": {
                    "DBInstanceIdentifier": instance_id
                }
            }
            self.stubber.add_response(
                "create_db_instance",
                response,
                expected_params
            )

    def stub_describe_db_instance_status(self, instance_id, statuses, error_code=None):
        pages = [{"DBInstances": [{"DBInstanceIdentifier": instance_id, "DBInstanceStatus": status}]} for status in statuses]

        if error_code:
            self.stubber.add_client_error(
                "describe_db_instances",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params={"DBInstanceIdentifier": instance_id}
            )
        else:
            for page in pages:
                self.stubber.add_response(
                    "describe_db_instances",
                    page,
                    expected_params={"DBInstanceIdentifier": instance_id}
                )

    def stub_stop_db_cluster(self, cluster_id, error_code=None):
        expected_params = {"DBClusterIdentifier": cluster_id}
        if error_code:
            self.stubber.add_client_error(
                "stop_db_cluster",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "stop_db_cluster",
                {"DBCluster": {"DBClusterIdentifier": cluster_id}},
                expected_params
            )

    def stub_describe_db_cluster_status(self, cluster_id, statuses, error_code=None):
        expected_params = {"DBClusterIdentifier": cluster_id}

        if error_code:
            self.stubber.add_client_error(
                "describe_db_clusters",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params
            )
        else:
            for status in statuses:
                response = {
                    "DBClusters": [{
                        "DBClusterIdentifier": cluster_id,
                        "Status": status
                    }]
                }
                self.stubber.add_response(
                    "describe_db_clusters",
                    response,
                    expected_params
                )

    def stub_start_db_cluster(self, cluster_id, statuses, error_code=None):
        start_params = {"DBClusterIdentifier": cluster_id}

        if error_code:
            self.stubber.add_client_error(
                "start_db_cluster",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=start_params,
            )
            return

        self.stubber.add_response(
            "start_db_cluster",
            {},
            expected_params=start_params,
        )

        describe_params = {"DBClusterIdentifier": cluster_id}
        for status in statuses:
            response = {
                "DBClusters": [{
                    "DBClusterIdentifier": cluster_id,
                    "Status": status
                }]
            }
            self.stubber.add_response(
                "describe_db_clusters",
                response,
                expected_params=describe_params,
            )

    def stub_delete_db_instance(self, instance_id, statuses=None, error_code=None):
        expected_params = {
            "DBInstanceIdentifier": instance_id,
            "SkipFinalSnapshot": True,
        }

        if error_code:
            self.stubber.add_client_error(
                "delete_db_instance",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params,
            )
            return

        self.stubber.add_response(
            "delete_db_instance",
            {},
            expected_params
        )

        if statuses:
            for status in statuses:
                response = {
                    "DBInstances": [
                        {
                            "DBInstanceIdentifier": instance_id,
                            "DBInstanceStatus": status
                        }
                    ]
                }
                self.stubber.add_response(
                    "describe_db_instances",
                    response,
                    expected_params={"DBInstanceIdentifier": instance_id}
                )

    def stub_delete_db_cluster(self, cluster_id, error_code=None):
        expected_params = {
            "DBClusterIdentifier": cluster_id,
            "SkipFinalSnapshot": True,
        }

        if error_code:
            self.stubber.add_client_error(
                "delete_db_cluster",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "delete_db_cluster",
                {},
                expected_params
            )

    def stub_delete_db_subnet_group(self, group_name, error_code=None):
        expected_params = {
            "DBSubnetGroupName": group_name
        }

        if error_code:
            self.stubber.add_client_error(
                "delete_db_subnet_group",
                service_error_code=error_code,
                service_message=f"{error_code} error",
                expected_params=expected_params,
            )
        else:
            self.stubber.add_response(
                "delete_db_subnet_group",
                {},
                expected_params
            )
