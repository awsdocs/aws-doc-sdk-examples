/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use phf::{phf_set, Set};
use secrecy::SecretString;
use std::{collections::HashMap, fmt::Display, time::Duration};

use aws_sdk_rds::{
    error::ProvideErrorMetadata,
    operation::create_db_cluster_parameter_group::CreateDbClusterParameterGroupOutput,
    types::{DbCluster, DbClusterParameterGroup, DbClusterSnapshot, DbInstance, Parameter},
};
use sdk_examples_test_utils::waiter::Waiter;
use tracing::{info, trace, warn};

const DB_ENGINE: &str = "aurora-mysql";
const DB_CLUSTER_PARAMETER_GROUP_NAME: &str = "RustSDKCodeExamplesDBParameterGroup";
const DB_CLUSTER_PARAMETER_GROUP_DESCRIPTION: &str =
    "Parameter Group created by Rust SDK Code Example";
const DB_CLUSTER_IDENTIFIER: &str = "RustSDKCodeExamplesDBCluster";
const DB_INSTANCE_IDENTIFIER: &str = "RustSDKCodeExamplesDBInstance";

static FILTER_PARAMETER_NAMES: Set<&'static str> = phf_set! {
    "auto_increment_offset",
    "auto_increment_increment",
};

#[derive(Debug, PartialEq, Eq)]
struct MetadataError {
    message: Option<String>,
    code: Option<String>,
}

impl MetadataError {
    fn from(err: &dyn ProvideErrorMetadata) -> Self {
        MetadataError {
            message: err.message().map(|s| s.to_string()),
            code: err.code().map(|s| s.to_string()),
        }
    }
}

impl Display for MetadataError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        let display = match (&self.message, &self.code) {
            (None, None) => "Unknown".to_string(),
            (None, Some(code)) => format!("({code})"),
            (Some(message), None) => message.to_string(),
            (Some(message), Some(code)) => format!("{message} ({code})"),
        };
        write!(f, "{display}")
    }
}

#[derive(Debug, PartialEq, Eq)]
pub struct ScenarioError {
    message: String,
    context: Option<MetadataError>,
}

impl ScenarioError {
    pub fn with(message: impl Into<String>) -> Self {
        ScenarioError {
            message: message.into(),
            context: None,
        }
    }

    pub fn new(message: impl Into<String>, err: &dyn ProvideErrorMetadata) -> Self {
        ScenarioError {
            message: message.into(),
            context: Some(MetadataError::from(err)),
        }
    }
}

impl std::error::Error for ScenarioError {}
impl Display for ScenarioError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match &self.context {
            Some(c) => write!(f, "{}: {}", self.message, c),
            None => write!(f, "{}", self.message),
        }
    }
}

// Parse the ParameterName, Description, and AllowedValues values and display them.
#[derive(Debug)]
pub struct AuroraScenarioParameter {
    name: String,
    allowed_values: String,
    current_value: String,
}

impl Display for AuroraScenarioParameter {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "{}: {} (allowed: {})",
            self.name, self.current_value, self.allowed_values
        )
    }
}

impl From<aws_sdk_rds::types::Parameter> for AuroraScenarioParameter {
    fn from(value: aws_sdk_rds::types::Parameter) -> Self {
        AuroraScenarioParameter {
            name: value.parameter_name.unwrap_or_default(),
            allowed_values: value.allowed_values.unwrap_or_default(),
            current_value: value.parameter_value.unwrap_or_default(),
        }
    }
}

pub struct AuroraScenario {
    rds: crate::rds::Rds,
    engine_family: Option<String>,
    engine_version: Option<String>,
    instance_class: Option<String>,
    db_cluster_parameter_group: Option<DbClusterParameterGroup>,
    db_cluster_identifier: Option<String>,
    db_instance_identifier: Option<String>,
    username: Option<String>,
    password: Option<SecretString>,
}

impl AuroraScenario {
    pub fn new(client: crate::rds::Rds) -> Self {
        AuroraScenario {
            rds: client,
            engine_family: None,
            engine_version: None,
            instance_class: None,
            db_cluster_parameter_group: None,
            db_cluster_identifier: None,
            db_instance_identifier: None,
            username: None,
            password: None,
        }
    }

    // Get available engine families for Aurora MySql. rds.DescribeDbEngineVersions(Engine='aurora-mysql') and build a set of the 'DBParameterGroupFamily' field values. I get {aurora-mysql8.0, aurora-mysql5.7}.
    pub async fn get_engines(&self) -> Result<HashMap<String, Vec<String>>, ScenarioError> {
        let describe_db_engine_versions = self.rds.describe_db_engine_versions(DB_ENGINE).await;
        let version_count = describe_db_engine_versions
            .as_ref()
            .map(|o| o.db_engine_versions().len())
            .unwrap_or_default();
        info!(version_count, "got list of versions");
        trace!(versions=?describe_db_engine_versions, "full list of versions");

        if let Err(err) = describe_db_engine_versions {
            return Err(ScenarioError::new(
                "Failed to retrieve DB Engine Versions",
                &err,
            ));
        };

        // Create a map of engine families to their available versions.
        let mut versions = HashMap::<String, Vec<String>>::new();
        describe_db_engine_versions
            .unwrap()
            .db_engine_versions()
            .iter()
            .filter_map(
                |v| match (&v.db_parameter_group_family, &v.engine_version) {
                    (Some(family), Some(version)) => Some((family.clone(), version.clone())),
                    _ => None,
                },
            )
            .for_each(|(family, version)| versions.entry(family).or_default().push(version));

        Ok(versions)
    }

    pub async fn get_instance_classes(&self) -> Result<Vec<String>, ScenarioError> {
        let describe_orderable_db_instance_options_items = self
            .rds
            .describe_orderable_db_instance_options(
                DB_ENGINE,
                self.engine_version
                    .as_ref()
                    .expect("engine version for db instance options")
                    .as_str(),
            )
            .await;

        describe_orderable_db_instance_options_items
            .map(|options| {
                options
                    .iter()
                    .map(|o| o.db_instance_class().unwrap_or_default().to_string())
                    .collect::<Vec<String>>()
            })
            .map_err(|err| ScenarioError::new("Could not get available instance classes", &err))
    }

    // Select an engine family and create a custom DB cluster parameter group. rds.CreateDbClusterParameterGroup(DBParameterGroupFamily='aurora-mysql8.0')
    pub async fn set_engine(&mut self, engine: &str, version: &str) -> Result<(), ScenarioError> {
        self.engine_family = Some(engine.to_string());
        self.engine_version = Some(version.to_string());
        let create_db_cluster_parameter_group = self
            .rds
            .create_db_cluster_parameter_group(
                DB_CLUSTER_PARAMETER_GROUP_NAME,
                DB_CLUSTER_PARAMETER_GROUP_DESCRIPTION,
                engine,
            )
            .await;

        match create_db_cluster_parameter_group {
            Ok(CreateDbClusterParameterGroupOutput {
                db_cluster_parameter_group: None,
                ..
            }) => {
                return Err(ScenarioError::with(
                    "CreateDBClusterParameterGroup had empty response",
                ));
            }
            Err(error) => {
                if error.code() == Some("DBParameterGroupAlreadyExists") {
                    info!("Cluster Parameter Group already exists, nothing to do");
                } else {
                    return Err(ScenarioError::new(
                        "Could not create Cluster Parameter Group",
                        &error,
                    ));
                }
            }
            _ => {
                info!("Created Cluster Parameter Group");
            }
        }

        Ok(())
    }

    pub fn set_instance_class(&mut self, instance_class: Option<String>) {
        self.instance_class = instance_class;
    }

    pub fn set_login(&mut self, username: Option<String>, password: Option<SecretString>) {
        self.username = username;
        self.password = password;
    }

    pub async fn connection_string(&self) -> Result<String, ScenarioError> {
        let cluster = self.get_cluster().await?;
        let endpoint = cluster.endpoint().unwrap_or_default();
        let port = cluster.port().unwrap_or_default();
        let username = cluster.master_username().unwrap_or_default();
        Ok(format!("mysql -h {endpoint} -P {port} -u {username} -p"))
    }

    pub async fn get_cluster(&self) -> Result<DbCluster, ScenarioError> {
        let describe_db_clusters_output = self
            .rds
            .describe_db_clusters(
                self.db_cluster_identifier
                    .as_ref()
                    .expect("cluster identifier")
                    .as_str(),
            )
            .await;
        if let Err(err) = describe_db_clusters_output {
            return Err(ScenarioError::new("Failed to get cluster", &err));
        }

        let db_cluster = describe_db_clusters_output
            .unwrap()
            .db_clusters
            .and_then(|output| output.first().cloned());

        db_cluster.ok_or_else(|| ScenarioError::with("Did not find the cluster"))
    }

    // Get the parameter group. rds.DescribeDbClusterParameterGroups
    // Get parameters in the group. This is a long list so you will have to paginate. Find the auto_increment_offset and auto_increment_increment parameters (by ParameterName). rds.DescribeDbClusterParameters
    // Parse the ParameterName, Description, and AllowedValues values and display them.
    pub async fn cluster_parameters(&self) -> Result<Vec<AuroraScenarioParameter>, ScenarioError> {
        let parameters_output = self
            .rds
            .describe_db_cluster_parameters(DB_CLUSTER_PARAMETER_GROUP_NAME)
            .await;

        if let Err(err) = parameters_output {
            return Err(ScenarioError::new(
                format!("Failed to retrieve parameters for {DB_CLUSTER_PARAMETER_GROUP_NAME}"),
                &err,
            ));
        }

        let parameters = parameters_output
            .unwrap()
            .into_iter()
            .flat_map(|p| p.parameters.unwrap_or_default().into_iter())
            .filter(|p| FILTER_PARAMETER_NAMES.contains(p.parameter_name().unwrap_or_default()))
            .map(AuroraScenarioParameter::from)
            .collect::<Vec<_>>();

        Ok(parameters)
    }

    // Modify both the auto_increment_offset and auto_increment_increment parameters in one call in the custom parameter group. Set their ParameterValue fields to a new allowable value. rds.ModifyDbClusterParameterGroup.
    pub async fn update_auto_increment(
        &self,
        offset: u8,
        increment: u8,
    ) -> Result<(), ScenarioError> {
        let modify_db_cluster_parameter_group = self
            .rds
            .modify_db_cluster_parameter_group(
                DB_CLUSTER_PARAMETER_GROUP_NAME,
                vec![
                    Parameter::builder()
                        .parameter_name("auto_increment_offset")
                        .parameter_value(format!("{offset}"))
                        .apply_method(aws_sdk_rds::types::ApplyMethod::Immediate)
                        .build(),
                    Parameter::builder()
                        .parameter_name("auto_increment_increment")
                        .parameter_value(format!("{increment}"))
                        .apply_method(aws_sdk_rds::types::ApplyMethod::Immediate)
                        .build(),
                ],
            )
            .await;

        if let Err(error) = modify_db_cluster_parameter_group {
            return Err(ScenarioError::new(
                "Failed to modify cluster parameter group",
                &error,
            ));
        }

        Ok(())
    }

    // Get a list of allowed engine versions. rds.DescribeDbEngineVersions(Engine='aurora-mysql', DBParameterGroupFamily=<the family used to create your parameter group in step 2>)
    // Create an Aurora DB cluster database cluster that contains a MySql database and uses the parameter group you created.
    // Wait for DB cluster to be ready. Call rds.DescribeDBClusters and check for Status == 'available'.
    // Get a list of instance classes available for the selected engine and engine version. rds.DescribeOrderableDbInstanceOptions(Engine='mysql', EngineVersion=).

    // Create a database instance in the cluster.
    // Wait for DB instance to be ready. Call rds.DescribeDbInstances and check for DBInstanceStatus == 'available'.
    pub async fn start_cluster_and_instance(&mut self) -> Result<(), ScenarioError> {
        if self.password.is_none() {
            return Err(ScenarioError::with(
                "Must set Secret Password before starting a cluster",
            ));
        }
        let create_db_cluster = self
            .rds
            .create_db_cluster(
                DB_CLUSTER_IDENTIFIER,
                DB_CLUSTER_PARAMETER_GROUP_NAME,
                DB_ENGINE,
                self.engine_version.as_deref().expect("engine version"),
                self.username.as_deref().expect("username"),
                self.password
                    .replace(SecretString::new("".to_string()))
                    .expect("password"),
            )
            .await;
        if let Err(err) = create_db_cluster {
            return Err(ScenarioError::new(
                "Failed to create DB Cluster with cluster group",
                &err,
            ));
        }

        self.db_cluster_identifier = create_db_cluster
            .unwrap()
            .db_cluster
            .and_then(|c| c.db_cluster_identifier);

        if self.db_cluster_identifier.is_none() {
            return Err(ScenarioError::with("Created DB Cluster missing Identifier"));
        }

        info!(
            "Started a db cluster: {}",
            self.db_cluster_identifier
                .clone()
                .unwrap_or("Missing ARN".to_string())
        );

        let create_db_instance = self
            .rds
            .create_db_instance(
                self.db_cluster_identifier.as_deref().expect("cluster name"),
                DB_INSTANCE_IDENTIFIER,
                self.instance_class.as_deref().expect("instance class"),
                DB_ENGINE,
            )
            .await;
        if let Err(err) = create_db_instance {
            return Err(ScenarioError::new(
                "Failed to create Instance in DB Cluster",
                &err,
            ));
        }

        self.db_instance_identifier = create_db_instance
            .unwrap()
            .db_instance
            .and_then(|i| i.db_instance_identifier);

        // Cluster creation can take up to 20 minutes to become available
        let cluster_max_wait = Duration::from_secs(20 * 60);
        let waiter = Waiter::builder().max(cluster_max_wait).build();
        while waiter.sleep().await.is_ok() {
            let cluster = self
                .rds
                .describe_db_clusters(
                    self.db_cluster_identifier
                        .as_deref()
                        .expect("cluster identifier"),
                )
                .await;

            if let Err(err) = cluster {
                warn!(?err, "Failed to describe cluster while waiting for ready");
                continue;
            }

            let instance = self
                .rds
                .describe_db_instance(
                    self.db_instance_identifier
                        .as_deref()
                        .expect("instance identifier"),
                )
                .await;
            if let Err(err) = instance {
                return Err(ScenarioError::new(
                    "Failed to find instance for cluster",
                    &err,
                ));
            }

            let instances_available = instance
                .unwrap()
                .db_instances()
                .iter()
                .all(|instance| instance.db_instance_status() == Some("Available"));

            let endpoints = self
                .rds
                .describe_db_cluster_endpoints(
                    self.db_cluster_identifier
                        .as_deref()
                        .expect("cluster identifier"),
                )
                .await;

            if let Err(err) = endpoints {
                return Err(ScenarioError::new(
                    "Failed to find endpoint for cluster",
                    &err,
                ));
            }

            let endpoints_available = endpoints
                .unwrap()
                .db_cluster_endpoints()
                .iter()
                .all(|endpoint| endpoint.status() == Some("available"));

            if instances_available && endpoints_available {
                return Ok(());
            }
        }

        Err(ScenarioError::with("timed out waiting for cluster"))
    }

    // Display the connection string that can be used to connect a 'mysql' shell to the cluster. In Python:

    // Create a snapshot of the DB cluster. rds.CreateDbClusterSnapshot.
    // Wait for the snapshot to create. rds.DescribeDbClusterSnapshots until Status == 'available'.
    pub async fn snapshot(&self) -> Result<DbClusterSnapshot, ScenarioError> {
        warn!("TODO! snapshot");
        Ok(DbClusterSnapshot::builder().build())
    }

    pub async fn clean_up(self) -> Result<(), Vec<ScenarioError>> {
        let mut clean_up_errors: Vec<ScenarioError> = vec![];

        // Delete the instance. rds.DeleteDbInstance.
        let delete_db_instance = self
            .rds
            .delete_db_instance(
                self.db_instance_identifier
                    .as_deref()
                    .expect("instance identifier"),
            )
            .await;
        if let Err(err) = delete_db_instance {
            let identifier = self
                .db_instance_identifier
                .clone()
                .unwrap_or("Missing Instance Identifier".to_string());
            let message = format!("failed to delete db instance {identifier}");
            clean_up_errors.push(ScenarioError::new(message, &err));
        } else {
            // Wait for the instance to delete
            let waiter = Waiter::default();
            while waiter.sleep().await.is_ok() {
                let describe_db_instances = self.rds.describe_db_instances().await;
                if let Err(err) = describe_db_instances {
                    clean_up_errors.push(ScenarioError::new(
                        "Failed to check instance state during deletion",
                        &err,
                    ));
                    break;
                }
                let db_instances = describe_db_instances
                    .unwrap()
                    .db_instances()
                    .iter()
                    .filter(|instance| instance.db_cluster_identifier == self.db_cluster_identifier)
                    .cloned()
                    .collect::<Vec<DbInstance>>();

                if db_instances.is_empty() {
                    trace!("Delete Instance waited and no instances were found");
                    break;
                }
                match db_instances.first().unwrap().db_instance_status() {
                    Some("Deleting") => continue,
                    Some(status) => {
                        info!("Attempting to delete but instances is in {status}");
                        continue;
                    }
                    None => {
                        warn!("No status for DB instance");
                        break;
                    }
                }
            }
        }

        // Delete the DB cluster. rds.DeleteDbCluster.
        let delete_db_cluster = self
            .rds
            .delete_db_cluster(
                self.db_cluster_identifier
                    .as_deref()
                    .expect("cluster identifier"),
            )
            .await;

        if let Err(err) = delete_db_cluster {
            let identifier = self
                .db_cluster_identifier
                .clone()
                .unwrap_or("Missing DB Cluster Identifier".to_string());
            let message = format!("failed to delete db cluster {identifier}");
            clean_up_errors.push(ScenarioError::new(message, &err));
        } else {
            // Wait for the instance and cluster to fully delete. rds.DescribeDbInstances and rds.DescribeDbClusters until both are not found.
            let waiter = Waiter::default();
            while waiter.sleep().await.is_ok() {
                let describe_db_clusters = self
                    .rds
                    .describe_db_clusters(
                        self.db_cluster_identifier
                            .as_deref()
                            .expect("cluster identifier"),
                    )
                    .await;
                if let Err(err) = describe_db_clusters {
                    clean_up_errors.push(ScenarioError::new(
                        "Failed to check cluster state during deletion",
                        &err,
                    ));
                    break;
                }
                let describe_db_clusters = describe_db_clusters.unwrap();
                let db_clusters = describe_db_clusters.db_clusters();
                if db_clusters.is_empty() {
                    trace!("Delete cluster waited and no clusters were found");
                    break;
                }
                match db_clusters.first().unwrap().status() {
                    Some("Deleting") => continue,
                    Some(status) => {
                        info!("Attempting to delete but clusters is in {status}");
                        continue;
                    }
                    None => {
                        warn!("No status for DB cluster");
                        break;
                    }
                }
            }
        }

        // Delete the DB cluster parameter group. rds.DeleteDbClusterParameterGroup.
        let delete_db_cluster_parameter_group = self
            .rds
            .delete_db_cluster_parameter_group(
                self.db_cluster_parameter_group
                    .map(|g| {
                        g.db_cluster_parameter_group_name
                            .unwrap_or_else(|| DB_CLUSTER_PARAMETER_GROUP_NAME.to_string())
                    })
                    .as_deref()
                    .expect("cluster parameter group name"),
            )
            .await;
        if let Err(error) = delete_db_cluster_parameter_group {
            clean_up_errors.push(ScenarioError::new(
                "Failed to delete the db cluster parameter group",
                &error,
            ))
        }

        if clean_up_errors.is_empty() {
            Ok(())
        } else {
            Err(clean_up_errors)
        }
    }
}

#[cfg(test)]
mod test {
    use crate::rds::MockRdsImpl;

    use super::*;

    use std::io::{Error, ErrorKind};

    use assert_matches::assert_matches;
    use aws_sdk_rds::{
        error::SdkError,
        operation::{
            create_db_cluster::{CreateDBClusterError, CreateDbClusterOutput},
            create_db_cluster_parameter_group::CreateDBClusterParameterGroupError,
            create_db_instance::{CreateDBInstanceError, CreateDbInstanceOutput},
            delete_db_cluster::DeleteDbClusterOutput,
            delete_db_cluster_parameter_group::DeleteDbClusterParameterGroupOutput,
            delete_db_instance::DeleteDbInstanceOutput,
            describe_db_cluster_endpoints::DescribeDbClusterEndpointsOutput,
            describe_db_cluster_parameters::{
                DescribeDBClusterParametersError, DescribeDbClusterParametersOutput,
            },
            describe_db_clusters::{DescribeDBClustersError, DescribeDbClustersOutput},
            describe_db_engine_versions::{
                DescribeDBEngineVersionsError, DescribeDbEngineVersionsOutput,
            },
            describe_db_instances::DescribeDbInstancesOutput,
            describe_orderable_db_instance_options::DescribeOrderableDBInstanceOptionsError,
            modify_db_cluster_parameter_group::{
                ModifyDBClusterParameterGroupError, ModifyDbClusterParameterGroupOutput,
            },
        },
        types::{
            error::DbParameterGroupAlreadyExistsFault, DbClusterEndpoint, DbEngineVersion,
            OrderableDbInstanceOption,
        },
    };
    use aws_smithy_http::body::SdkBody;
    use aws_smithy_runtime_api::client::orchestrator::HttpResponse;
    use mockall::predicate::*;
    use secrecy::ExposeSecret;

    #[tokio::test]
    async fn test_scenario_set_engine() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster_parameter_group()
            .with(
                eq("RustSDKCodeExamplesDBParameterGroup"),
                eq("Parameter Group created by Rust SDK Code Example"),
                eq("aurora-mysql"),
            )
            .return_once(|_, _, _| {
                Ok(CreateDbClusterParameterGroupOutput::builder()
                    .db_cluster_parameter_group(DbClusterParameterGroup::builder().build())
                    .build())
            });

        let mut scenario = AuroraScenario::new(mock_rds);

        let set_engine = scenario.set_engine("aurora-mysql", "aurora-mysql8.0").await;

        assert_eq!(set_engine, Ok(()));
        assert_eq!(Some("aurora-mysql"), scenario.engine_family.as_deref());
        assert_eq!(Some("aurora-mysql8.0"), scenario.engine_version.as_deref());
    }

    #[tokio::test]
    async fn test_scenario_set_engine_not_create() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster_parameter_group()
            .with(
                eq("RustSDKCodeExamplesDBParameterGroup"),
                eq("Parameter Group created by Rust SDK Code Example"),
                eq("aurora-mysql"),
            )
            .return_once(|_, _, _| Ok(CreateDbClusterParameterGroupOutput::builder().build()));

        let mut scenario = AuroraScenario::new(mock_rds);

        let set_engine = scenario.set_engine("aurora-mysql", "aurora-mysql8.0").await;

        assert!(set_engine.is_err());
    }

    #[tokio::test]
    async fn test_scenario_set_engine_param_group_exists() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster_parameter_group()
            .withf(|_, _, _| true)
            .return_once(|_, _, _| {
                Err(SdkError::service_error(
                    CreateDBClusterParameterGroupError::DbParameterGroupAlreadyExistsFault(
                        DbParameterGroupAlreadyExistsFault::builder().build(),
                    ),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let mut scenario = AuroraScenario::new(mock_rds);

        let set_engine = scenario.set_engine("aurora-mysql", "aurora-mysql8.0").await;

        assert!(set_engine.is_err());
    }

    #[tokio::test]
    async fn test_scenario_get_engines() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_describe_db_engine_versions()
            .with(eq("aurora-mysql"))
            .return_once(|_| {
                Ok(DescribeDbEngineVersionsOutput::builder()
                    .db_engine_versions(
                        DbEngineVersion::builder()
                            .db_parameter_group_family("f1")
                            .engine_version("f1a")
                            .build(),
                    )
                    .db_engine_versions(
                        DbEngineVersion::builder()
                            .db_parameter_group_family("f1")
                            .engine_version("f1b")
                            .build(),
                    )
                    .db_engine_versions(
                        DbEngineVersion::builder()
                            .db_parameter_group_family("f2")
                            .engine_version("f2a")
                            .build(),
                    )
                    .db_engine_versions(DbEngineVersion::builder().build())
                    .build())
            });

        let scenario = AuroraScenario::new(mock_rds);

        let versions_map = scenario.get_engines().await;

        assert_eq!(
            versions_map,
            Ok(HashMap::from([
                ("f1".to_string(), vec!["f1a".to_string(), "f1b".to_string()]),
                ("f2".to_string(), vec!["f2a".to_string()])
            ]))
        );
    }

    #[tokio::test]
    async fn test_scenario_get_engines_failed() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_describe_db_engine_versions()
            .with(eq("aurora-mysql"))
            .return_once(|_| {
                Err(SdkError::service_error(
                    DescribeDBEngineVersionsError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "describe_db_engine_versions error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let scenario = AuroraScenario::new(mock_rds);

        let versions_map = scenario.get_engines().await;
        assert_matches!(
            versions_map,
            Err(ScenarioError { message, context: _ }) if message == "Failed to retrieve DB Engine Versions"
        );
    }

    #[tokio::test]
    async fn test_scenario_get_instance_classes() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster_parameter_group()
            .return_once(|_, _, _| {
                Ok(CreateDbClusterParameterGroupOutput::builder()
                    .db_cluster_parameter_group(DbClusterParameterGroup::builder().build())
                    .build())
            });

        mock_rds
            .expect_describe_orderable_db_instance_options()
            .with(eq("aurora-mysql"), eq("aurora-mysql8.0"))
            .return_once(|_, _| {
                Ok(vec![
                    OrderableDbInstanceOption::builder()
                        .db_instance_class("t1")
                        .build(),
                    OrderableDbInstanceOption::builder()
                        .db_instance_class("t2")
                        .build(),
                    OrderableDbInstanceOption::builder()
                        .db_instance_class("t3")
                        .build(),
                ])
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario
            .set_engine("aurora-mysql", "aurora-mysql8.0")
            .await
            .expect("set engine");

        let instance_classes = scenario.get_instance_classes().await;

        assert_eq!(
            instance_classes,
            Ok(vec!["t1".to_string(), "t2".to_string(), "t3".to_string()])
        );
    }

    #[tokio::test]
    async fn test_scenario_get_instance_classes_error() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_describe_orderable_db_instance_options()
            .with(eq("aurora-mysql"), eq("aurora-mysql8.0"))
            .return_once(|_, _| {
                Err(SdkError::service_error(
                    DescribeOrderableDBInstanceOptionsError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "describe_orderable_db_instance_options_error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.engine_family = Some("aurora-mysql".to_string());
        scenario.engine_version = Some("aurora-mysql8.0".to_string());

        let instance_classes = scenario.get_instance_classes().await;

        assert_matches!(
            instance_classes,
            Err(ScenarioError {message, context: _}) if message == "Could not get available instance classes"
        );
    }

    #[tokio::test]
    async fn test_scenario_get_cluster() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_describe_db_clusters()
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .return_once(|_| {
                Ok(DescribeDbClustersOutput::builder()
                    .db_clusters(DbCluster::builder().build())
                    .build())
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.db_cluster_identifier = Some("RustSDKCodeExamplesDBCluster".to_string());
        let cluster = scenario.get_cluster().await;

        assert!(cluster.is_ok());
    }

    #[tokio::test]
    async fn test_scenario_get_cluster_missing_cluster() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster_parameter_group()
            .return_once(|_, _, _| {
                Ok(CreateDbClusterParameterGroupOutput::builder()
                    .db_cluster_parameter_group(DbClusterParameterGroup::builder().build())
                    .build())
            });

        mock_rds
            .expect_describe_db_clusters()
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .return_once(|_| Ok(DescribeDbClustersOutput::builder().build()));

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.db_cluster_identifier = Some("RustSDKCodeExamplesDBCluster".to_string());
        let cluster = scenario.get_cluster().await;

        assert_matches!(cluster, Err(ScenarioError { message, context: _ }) if message == "Did not find the cluster");
    }

    #[tokio::test]
    async fn test_scenario_get_cluster_error() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster_parameter_group()
            .return_once(|_, _, _| {
                Ok(CreateDbClusterParameterGroupOutput::builder()
                    .db_cluster_parameter_group(DbClusterParameterGroup::builder().build())
                    .build())
            });

        mock_rds
            .expect_describe_db_clusters()
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .return_once(|_| {
                Err(SdkError::service_error(
                    DescribeDBClustersError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "describe_db_clusters_error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.db_cluster_identifier = Some("RustSDKCodeExamplesDBCluster".to_string());
        let cluster = scenario.get_cluster().await;

        assert_matches!(cluster, Err(ScenarioError { message, context: _ }) if message == "Failed to get cluster");
    }

    #[tokio::test]
    async fn test_scenario_connection_string() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_describe_db_clusters()
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .return_once(|_| {
                Ok(DescribeDbClustersOutput::builder()
                    .db_clusters(
                        DbCluster::builder()
                            .endpoint("test_endpoint")
                            .port(3306)
                            .master_username("test_username")
                            .build(),
                    )
                    .build())
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.db_cluster_identifier = Some("RustSDKCodeExamplesDBCluster".to_string());
        let connection_string = scenario.connection_string().await;

        assert_eq!(
            connection_string,
            Ok("mysql -h test_endpoint -P 3306 -u test_username -p".to_string())
        );
    }

    #[tokio::test]
    async fn test_scenario_cluster_parameters() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_describe_db_cluster_parameters()
            .with(eq("RustSDKCodeExamplesDBParameterGroup"))
            .return_once(|_| {
                Ok(vec![DescribeDbClusterParametersOutput::builder()
                    .parameters(Parameter::builder().parameter_name("a").build())
                    .parameters(Parameter::builder().parameter_name("b").build())
                    .parameters(
                        Parameter::builder()
                            .parameter_name("auto_increment_offset")
                            .build(),
                    )
                    .parameters(Parameter::builder().parameter_name("c").build())
                    .parameters(
                        Parameter::builder()
                            .parameter_name("auto_increment_increment")
                            .build(),
                    )
                    .parameters(Parameter::builder().parameter_name("d").build())
                    .build()])
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.db_cluster_identifier = Some("RustSDKCodeExamplesDBCluster".to_string());

        let params = scenario.cluster_parameters().await.expect("cluster params");
        let names: Vec<String> = params.into_iter().map(|p| p.name).collect();
        assert_eq!(
            names,
            vec!["auto_increment_offset", "auto_increment_increment"]
        );
    }

    #[tokio::test]
    async fn test_scenario_cluster_parameters_error() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_describe_db_cluster_parameters()
            .with(eq("RustSDKCodeExamplesDBParameterGroup"))
            .return_once(|_| {
                Err(SdkError::service_error(
                    DescribeDBClusterParametersError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "describe_db_cluster_parameters_error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.db_cluster_identifier = Some("RustSDKCodeExamplesDBCluster".to_string());
        let params = scenario.cluster_parameters().await;
        assert_matches!(params, Err(ScenarioError { message, context: _ }) if message == "Failed to retrieve parameters for RustSDKCodeExamplesDBParameterGroup");
    }

    #[tokio::test]
    async fn test_scenario_update_auto_increment() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_modify_db_cluster_parameter_group()
            .withf(|name, params| {
                assert_eq!(name, "RustSDKCodeExamplesDBParameterGroup");
                assert_eq!(
                    params,
                    &vec![
                        Parameter::builder()
                            .parameter_name("auto_increment_offset")
                            .parameter_value("10".to_string())
                            .apply_method(aws_sdk_rds::types::ApplyMethod::Immediate)
                            .build(),
                        Parameter::builder()
                            .parameter_name("auto_increment_increment")
                            .parameter_value("20".to_string())
                            .apply_method(aws_sdk_rds::types::ApplyMethod::Immediate)
                            .build(),
                    ]
                );
                true
            })
            .return_once(|_, _| Ok(ModifyDbClusterParameterGroupOutput::builder().build()));

        let scenario = AuroraScenario::new(mock_rds);

        scenario
            .update_auto_increment(10, 20)
            .await
            .expect("update auto increment");
    }

    #[tokio::test]
    async fn test_scenario_update_auto_increment_error() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_modify_db_cluster_parameter_group()
            .return_once(|_, _| {
                Err(SdkError::service_error(
                    ModifyDBClusterParameterGroupError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "modify_db_cluster_parameter_group_error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let scenario = AuroraScenario::new(mock_rds);

        let update = scenario.update_auto_increment(10, 20).await;
        assert_matches!(update, Err(ScenarioError { message, context: _}) if message == "Failed to modify cluster parameter group");
    }

    #[tokio::test]
    async fn test_start_cluster_and_instance() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster()
            .withf(|id, params, engine, version, username, password| {
                assert_eq!(id, "RustSDKCodeExamplesDBCluster");
                assert_eq!(params, "RustSDKCodeExamplesDBParameterGroup");
                assert_eq!(engine, "aurora-mysql");
                assert_eq!(version, "aurora-mysql8.0");
                assert_eq!(username, "test username");
                assert_eq!(password.expose_secret(), "test password");
                true
            })
            .return_once(|id, _, _, _, _, _| {
                Ok(CreateDbClusterOutput::builder()
                    .db_cluster(DbCluster::builder().db_cluster_identifier(id).build())
                    .build())
            });

        mock_rds
            .expect_create_db_instance()
            .withf(|cluster, name, class, engine| {
                assert_eq!(cluster, "RustSDKCodeExamplesDBCluster");
                assert_eq!(name, "RustSDKCodeExamplesDBInstance");
                assert_eq!(class, "m5.large");
                assert_eq!(engine, "aurora-mysql");
                true
            })
            .return_once(|cluster, name, class, _| {
                Ok(CreateDbInstanceOutput::builder()
                    .db_instance(
                        DbInstance::builder()
                            .db_cluster_identifier(cluster)
                            .db_instance_identifier(name)
                            .db_instance_class(class)
                            .build(),
                    )
                    .build())
            });

        mock_rds
            .expect_describe_db_clusters()
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .return_once(|id| {
                Ok(DescribeDbClustersOutput::builder()
                    .db_clusters(DbCluster::builder().db_cluster_identifier(id).build())
                    .build())
            });

        mock_rds
            .expect_describe_db_instance()
            .with(eq("RustSDKCodeExamplesDBInstance"))
            .return_once(|name| {
                Ok(DescribeDbInstancesOutput::builder()
                    .db_instances(
                        DbInstance::builder()
                            .db_instance_identifier(name)
                            .db_instance_status("Available")
                            .build(),
                    )
                    .build())
            });

        mock_rds
            .expect_describe_db_cluster_endpoints()
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .return_once(|_| {
                Ok(DescribeDbClusterEndpointsOutput::builder()
                    .db_cluster_endpoints(DbClusterEndpoint::builder().status("available").build())
                    .build())
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.engine_version = Some("aurora-mysql8.0".to_string());
        scenario.instance_class = Some("m5.large".to_string());
        scenario.username = Some("test username".to_string());
        scenario.password = Some(SecretString::new("test password".to_string()));

        tokio::time::pause();
        let assertions = tokio::spawn(async move {
            let create = scenario.start_cluster_and_instance().await;
            assert!(create.is_ok());
            assert_eq!(
                scenario
                    .password
                    .replace(SecretString::new("BAD SECRET".to_string()))
                    .unwrap()
                    .expose_secret(),
                &"".to_string()
            );
            assert_eq!(
                scenario.db_cluster_identifier,
                Some("RustSDKCodeExamplesDBCluster".to_string())
            );
        });
        tokio::time::advance(Duration::from_secs(1)).await;
        tokio::time::resume();
        let _ = assertions.await;
    }

    #[tokio::test]
    async fn test_start_cluster_and_instance_cluster_create_error() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster()
            .return_once(|_, _, _, _, _, _| {
                Err(SdkError::service_error(
                    CreateDBClusterError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "create db cluster error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.engine_version = Some("aurora-mysql8.0".to_string());
        scenario.instance_class = Some("m5.large".to_string());
        scenario.username = Some("test username".to_string());
        scenario.password = Some(SecretString::new("test password".to_string()));

        let create = scenario.start_cluster_and_instance().await;
        assert_matches!(create, Err(ScenarioError { message, context: _}) if message == "Failed to create DB Cluster with cluster group")
    }

    #[tokio::test]
    async fn test_start_cluster_and_instance_cluster_create_missing_id() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster()
            .return_once(|_, _, _, _, _, _| {
                Ok(CreateDbClusterOutput::builder()
                    .db_cluster(DbCluster::builder().build())
                    .build())
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.engine_version = Some("aurora-mysql8.0".to_string());
        scenario.instance_class = Some("m5.large".to_string());
        scenario.username = Some("test username".to_string());
        scenario.password = Some(SecretString::new("test password".to_string()));

        let create = scenario.start_cluster_and_instance().await;
        assert_matches!(create, Err(ScenarioError { message, context:_ }) if message == "Created DB Cluster missing Identifier");
    }

    #[tokio::test]
    async fn test_start_cluster_and_instance_instance_create_error() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster()
            .withf(|id, params, engine, version, username, password| {
                assert_eq!(id, "RustSDKCodeExamplesDBCluster");
                assert_eq!(params, "RustSDKCodeExamplesDBParameterGroup");
                assert_eq!(engine, "aurora-mysql");
                assert_eq!(version, "aurora-mysql8.0");
                assert_eq!(username, "test username");
                assert_eq!(password.expose_secret(), "test password");
                true
            })
            .return_once(|id, _, _, _, _, _| {
                Ok(CreateDbClusterOutput::builder()
                    .db_cluster(DbCluster::builder().db_cluster_identifier(id).build())
                    .build())
            });

        mock_rds
            .expect_create_db_instance()
            .return_once(|_, _, _, _| {
                Err(SdkError::service_error(
                    CreateDBInstanceError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "create db instance error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.engine_version = Some("aurora-mysql8.0".to_string());
        scenario.instance_class = Some("m5.large".to_string());
        scenario.username = Some("test username".to_string());
        scenario.password = Some(SecretString::new("test password".to_string()));

        let create = scenario.start_cluster_and_instance().await;
        assert_matches!(create, Err(ScenarioError { message, context: _ }) if message == "Failed to create Instance in DB Cluster")
    }

    #[tokio::test]
    async fn test_start_cluster_and_instance_wait_hiccup() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_create_db_cluster()
            .withf(|id, params, engine, version, username, password| {
                assert_eq!(id, "RustSDKCodeExamplesDBCluster");
                assert_eq!(params, "RustSDKCodeExamplesDBParameterGroup");
                assert_eq!(engine, "aurora-mysql");
                assert_eq!(version, "aurora-mysql8.0");
                assert_eq!(username, "test username");
                assert_eq!(password.expose_secret(), "test password");
                true
            })
            .return_once(|id, _, _, _, _, _| {
                Ok(CreateDbClusterOutput::builder()
                    .db_cluster(DbCluster::builder().db_cluster_identifier(id).build())
                    .build())
            });

        mock_rds
            .expect_create_db_instance()
            .withf(|cluster, name, class, engine| {
                assert_eq!(cluster, "RustSDKCodeExamplesDBCluster");
                assert_eq!(name, "RustSDKCodeExamplesDBInstance");
                assert_eq!(class, "m5.large");
                assert_eq!(engine, "aurora-mysql");
                true
            })
            .return_once(|cluster, name, class, _| {
                Ok(CreateDbInstanceOutput::builder()
                    .db_instance(
                        DbInstance::builder()
                            .db_cluster_identifier(cluster)
                            .db_instance_identifier(name)
                            .db_instance_class(class)
                            .build(),
                    )
                    .build())
            });

        mock_rds
            .expect_describe_db_clusters()
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .times(1)
            .returning(|_| {
                Err(SdkError::service_error(
                    DescribeDBClustersError::unhandled(Box::new(Error::new(
                        ErrorKind::Other,
                        "describe cluster error",
                    ))),
                    HttpResponse::new(SdkBody::empty()),
                ))
            })
            .with(eq("RustSDKCodeExamplesDBCluster"))
            .times(1)
            .returning(|id| {
                Ok(DescribeDbClustersOutput::builder()
                    .db_clusters(DbCluster::builder().db_cluster_identifier(id).build())
                    .build())
            });

        mock_rds.expect_describe_db_instance().return_once(|name| {
            Ok(DescribeDbInstancesOutput::builder()
                .db_instances(
                    DbInstance::builder()
                        .db_instance_identifier(name)
                        .db_instance_status("Available")
                        .build(),
                )
                .build())
        });

        mock_rds
            .expect_describe_db_cluster_endpoints()
            .return_once(|_| {
                Ok(DescribeDbClusterEndpointsOutput::builder()
                    .db_cluster_endpoints(DbClusterEndpoint::builder().status("available").build())
                    .build())
            });

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.engine_version = Some("aurora-mysql8.0".to_string());
        scenario.instance_class = Some("m5.large".to_string());
        scenario.username = Some("test username".to_string());
        scenario.password = Some(SecretString::new("test password".to_string()));

        tokio::time::pause();
        let assertions = tokio::spawn(async move {
            let create = scenario.start_cluster_and_instance().await;
            assert!(create.is_ok());
        });

        tokio::time::advance(Duration::from_secs(1)).await;
        tokio::time::advance(Duration::from_secs(1)).await;
        tokio::time::resume();
        let _ = assertions.await;
    }

    #[tokio::test]
    async fn test_scenario_clean_up() {
        let mut mock_rds = MockRdsImpl::default();

        mock_rds
            .expect_delete_db_instance()
            .with(eq("MockInstance"))
            .return_once(|_| Ok(DeleteDbInstanceOutput::builder().build()));

        let mut describe_instance_call = 0;
        mock_rds
            .expect_describe_db_instances()
            .withf(|| true)
            .returning(move || {
                describe_instance_call += 1;
                if describe_instance_call == 1 {
                    Ok(DescribeDbInstancesOutput::builder()
                        .db_instances(
                            DbInstance::builder()
                                .db_cluster_identifier("MockCluster")
                                .db_instance_status("Deleting")
                                .build(),
                        )
                        .build())
                } else {
                    Ok(DescribeDbInstancesOutput::builder().build())
                }
            });

        mock_rds
            .expect_delete_db_cluster()
            .with(eq("MockCluster"))
            .return_once(|_| Ok(DeleteDbClusterOutput::builder().build()));

        mock_rds
            .expect_describe_db_clusters()
            .with(eq("MockCluster"))
            .times(1)
            .returning(|id| {
                Ok(DescribeDbClustersOutput::builder()
                    .db_clusters(
                        DbCluster::builder()
                            .db_cluster_identifier(id)
                            .status("Deleting")
                            .build(),
                    )
                    .build())
            })
            .with(eq("MockCluster"))
            .times(1)
            .returning(|_| Ok(DescribeDbClustersOutput::builder().build()));

        mock_rds
            .expect_delete_db_cluster_parameter_group()
            .with(eq("MockParamGroup"))
            .return_once(|_| Ok(DeleteDbClusterParameterGroupOutput::builder().build()));

        let mut scenario = AuroraScenario::new(mock_rds);
        scenario.db_cluster_identifier = Some(String::from("MockCluster"));
        scenario.db_instance_identifier = Some(String::from("MockInstance"));
        scenario.db_cluster_parameter_group = Some(
            DbClusterParameterGroup::builder()
                .db_cluster_parameter_group_name("MockParamGroup")
                .build(),
        );

        tokio::time::pause();
        let assertions = tokio::spawn(async move {
            let clean_up = scenario.clean_up().await;
            assert!(clean_up.is_ok());
        });

        tokio::time::advance(Duration::from_secs(1)).await; // Wait for first Describe Instances
        tokio::time::advance(Duration::from_secs(1)).await; // Wait for second Describe Instances
        tokio::time::advance(Duration::from_secs(1)).await; // Wait for first Describe Cluster
        tokio::time::advance(Duration::from_secs(1)).await; // Wait for second Describe Cluster
        tokio::time::resume();
        let _ = assertions.await;
    }
}
