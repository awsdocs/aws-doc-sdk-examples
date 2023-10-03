use phf::{phf_set, Set};
use secrecy::{ExposeSecret, SecretString};
use std::{
    collections::HashMap,
    fmt::Display,
    time::{Duration, SystemTime},
};

use aws_config::SdkConfig;
use aws_sdk_rds::{
    error::ProvideErrorMetadata,
    types::{self, DbCluster, DbClusterParameterGroup, DbClusterSnapshot, DbInstance, Parameter},
    Client,
};
use tracing::{info, trace, warn};

const DB_CLUSTER_PARAMETER_GROUP_NAME: &str = "RustSDKCodeExamplesDBParameterGroup";
const DB_CLUSTER_PARAMETER_GROUP_DESCRIPTION: &str =
    "Parameter Group created by Rust SDK Code Example";
const DB_CLUSTER_IDENTIFIER: &str = "RustSDKCodeExamplesDBCluster";
const DB_INSTANCE_IDENTIFIER: &str = "RustSDKCodeExamplesDBInstance";

const MAX_WAIT: Duration = Duration::from_secs(5 * 60); // Wait at most 25 seconds.
const WAIT_TIME: Duration = Duration::from_millis(500); // Wait half a second at a time.
static FILTER_PARAMETER_NAMES: Set<&'static str> = phf_set! {
    "auto_increment_offset",
    "auto_increment_increment",
};

struct Waiter {
    start: SystemTime,
    max: Duration,
}

impl Waiter {
    fn new() -> Self {
        Waiter {
            start: SystemTime::now(),
            max: MAX_WAIT,
        }
    }

    async fn sleep(&self) -> Result<(), ScenarioError> {
        if SystemTime::now()
            .duration_since(self.start)
            .unwrap_or(Duration::MAX)
            > self.max
        {
            Err(ScenarioError::with(
                "Exceeded maximum wait duration for stable group",
            ))
        } else {
            tokio::time::sleep(WAIT_TIME).await;
            Ok(())
        }
    }
}

#[derive(Debug)]
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

#[derive(Debug)]
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

pub struct AuroraScenarioDescription {
    auto_increment_offset: AuroraScenarioParameter,
    auto_increment_incrememt: AuroraScenarioParameter,
}
impl Display for AuroraScenarioDescription {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "Auto Increment Offset: {}", self.auto_increment_offset)?;
        writeln!(
            f,
            "            increment: {}",
            self.auto_increment_incrememt
        )?;
        Ok(())
    }
}

pub struct AuroraScenario {
    client: aws_sdk_rds::Client,
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
    pub async fn prepare_scenario(sdk_config: &SdkConfig) -> Self {
        AuroraScenario {
            client: Client::new(sdk_config),
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
        let describe_db_engine_versions = self
            .client
            .describe_db_engine_versions()
            .engine("aurora-mysql")
            .send()
            .await;
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
            .client
            .describe_orderable_db_instance_options()
            .set_engine(self.engine_family.clone())
            .set_engine_version(self.engine_version.clone())
            .into_paginator()
            .items()
            .send()
            .try_collect()
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
            .client
            .create_db_cluster_parameter_group()
            .db_cluster_parameter_group_name(DB_CLUSTER_PARAMETER_GROUP_NAME)
            .description(DB_CLUSTER_PARAMETER_GROUP_DESCRIPTION)
            .db_parameter_group_family(engine)
            .send()
            .await;

        if let Err(error) = create_db_cluster_parameter_group {
            return Err(ScenarioError::new(
                "Could not create Cluster Parameter Group",
                &error,
            ));
        }

        let create_db_cluster_parameter_group_output = create_db_cluster_parameter_group.unwrap();
        self.db_cluster_parameter_group =
            create_db_cluster_parameter_group_output.db_cluster_parameter_group;
        if self.db_cluster_parameter_group.is_none() {
            return Err(ScenarioError::with(
                "CreateDBClusterParameterGroup had empty response",
            ));
        };

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
        warn!("TODO! connection_string");
        let cluster = self.get_cluster().await?;
        let endpoint = cluster.endpoint().unwrap_or_default();
        let port = cluster.port().unwrap_or_default();
        let username = cluster.master_username().unwrap_or_default();
        Ok(format!("mysql -h {endpoint} -P {port} -u {username} -p"))
    }

    pub async fn get_cluster(&self) -> Result<DbCluster, ScenarioError> {
        let describe_db_clusters_output = self
            .client
            .describe_db_clusters()
            .set_db_cluster_identifier(self.db_cluster_identifier.clone())
            .send()
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
        let mut describe_db_cluster_parameters_paginator = self
            .client
            .describe_db_cluster_parameters()
            .db_cluster_parameter_group_name(DB_CLUSTER_PARAMETER_GROUP_NAME)
            .into_paginator()
            .send();

        let mut parameters: Vec<aws_sdk_rds::types::Parameter> = vec![];

        loop {
            let output = describe_db_cluster_parameters_paginator.next().await;
            match output {
                Some(output) => match output {
                    Ok(output) => {
                        for parameter in output.parameters() {
                            parameters.push(parameter.clone());
                        }
                    }
                    Err(err) => {
                        return Err(ScenarioError::new(
                            format!(
                            "Failed to retrieve parameters for {DB_CLUSTER_PARAMETER_GROUP_NAME}"
                        ),
                            &err,
                        ));
                    }
                },
                None => break,
            }
        }

        Ok(parameters
            .into_iter()
            .filter(|p| FILTER_PARAMETER_NAMES.contains(p.parameter_name().unwrap_or_default()))
            .map(AuroraScenarioParameter::from)
            .collect())
    }

    // Modify both the auto_increment_offset and auto_increment_increment parameters in one call in the custom parameter group. Set their ParameterValue fields to a new allowable value. rds.ModifyDbClusterParameterGroup.
    pub async fn update_auto_increment(
        &self,
        offset: u8,
        increment: u8,
    ) -> Result<(), ScenarioError> {
        let modify_db_cluster_parameter_group = self
            .client
            .modify_db_cluster_parameter_group()
            .db_cluster_parameter_group_name(DB_CLUSTER_PARAMETER_GROUP_NAME)
            .parameters(
                Parameter::builder()
                    .parameter_name("auto_increment_offset")
                    .parameter_value(format!("{offset}"))
                    .apply_method(aws_sdk_rds::types::ApplyMethod::Immediate)
                    .build(),
            )
            .parameters(
                Parameter::builder()
                    .parameter_name("auto_increment_increment")
                    .parameter_value(format!("{increment}"))
                    .apply_method(aws_sdk_rds::types::ApplyMethod::Immediate)
                    .build(),
            )
            .send()
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
    pub async fn start_cluster_and_instance(&mut self) -> Result<DbInstance, ScenarioError> {
        if self.password.is_none() {
            return Err(ScenarioError::with(
                "Must set Secret Password before starting a cluster",
            ));
        }
        let create_db_cluster = self
            .client
            .create_db_cluster()
            .db_cluster_identifier(DB_CLUSTER_IDENTIFIER)
            .db_cluster_parameter_group_name(DB_CLUSTER_PARAMETER_GROUP_NAME)
            .set_engine(self.engine_family.clone())
            .set_engine_version(self.engine_version.clone())
            .set_master_username(self.username.clone())
            .master_user_password(self.password.as_ref().unwrap().expose_secret())
            .send()
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
            .client
            .create_db_instance()
            .set_db_cluster_identifier(self.db_cluster_identifier.clone())
            .db_instance_identifier(DB_INSTANCE_IDENTIFIER)
            .set_engine(self.engine_family.clone())
            .set_db_instance_class(self.instance_class.clone())
            .send()
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

        Ok(DbInstance::builder().build())
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
            .client
            .delete_db_instance()
            .set_db_instance_identifier(self.db_cluster_identifier.clone())
            .send()
            .await;
        if let Err(err) = delete_db_instance {
            let identifier = self
                .db_instance_identifier
                .clone()
                .unwrap_or("Missing Instance Identifier".to_string());
            let message = format!("failed to delete db instance {identifier}");
            clean_up_errors.push(ScenarioError::new(message, &err));
        }

        // Wait for the instance to delete
        let waiter = Waiter::new();
        while waiter.sleep().await.is_ok() {
            let describe_db_instances = self
                .client
                .describe_db_instances()
                .set_db_instance_identifier(self.db_instance_identifier.clone())
                .send()
                .await;
            if let Err(err) = describe_db_instances {
                clean_up_errors.push(ScenarioError::new(
                    "Failed to check instance state during deletion",
                    &err,
                ));
                continue;
            }
            let describe_db_instances = describe_db_instances.unwrap();
            let db_instances = describe_db_instances.db_instances();
            if db_instances.is_empty() {
                info!("Delete Instance waited and no instances were found");
                continue;
            }
            match db_instances.first().unwrap().db_instance_status() {
                Some("Deleting") => continue,
                Some(status) => {
                    info!("Attempting to delete but instances is in {status}");
                    continue;
                }
                None => {
                    warn!("No status for DB instance");
                }
            }
        }

        // Delete the DB cluster. rds.DeleteDbCluster.
        let delete_db_cluster = self
            .client
            .delete_db_cluster()
            .set_db_cluster_identifier(self.db_cluster_identifier.clone())
            .send()
            .await;

        if let Err(err) = delete_db_cluster {
            let identifier = self
                .db_cluster_identifier
                .clone()
                .unwrap_or("Missing DB Cluster Identifier".to_string());
            let message = format!("failed to delete db cluster {identifier}");
            clean_up_errors.push(ScenarioError::new(message, &err));
        }

        // Wait for the instance and cluster to fully delete. rds.DescribeDbInstances and rds.DescribeDbClusters until both are not found.
        let waiter = Waiter::new();
        while waiter.sleep().await.is_ok() {
            let describe_db_clusters = self
                .client
                .describe_db_clusters()
                .set_db_cluster_identifier(self.db_cluster_identifier.clone())
                .send()
                .await;
            if let Err(err) = describe_db_clusters {
                clean_up_errors.push(ScenarioError::new(
                    "Failed to check cluster state during deletion",
                    &err,
                ));
                continue;
            }
            let describe_db_clusters = describe_db_clusters.unwrap();
            let db_clusters = describe_db_clusters.db_clusters();
            if db_clusters.is_empty() {
                info!("Delete cluster waited and no clusters were found");
            }
            match db_clusters.first().unwrap().status() {
                Some("Deleting") => continue,
                Some(status) => {
                    info!("Attempting to delete but clusters is in {status}");
                    continue;
                }
                None => {
                    warn!("No status for DB cluster");
                }
            }
        }

        // Delete the DB cluster parameter group. rds.DeleteDbClusterParameterGroup.
        let delete_db_cluster_parameter_group = self
            .client
            .delete_db_cluster_parameter_group()
            .set_db_cluster_parameter_group_name(
                self.db_cluster_parameter_group
                    .map(|g| g.db_cluster_parameter_group_name.unwrap_or_default()),
            )
            .send()
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

pub fn connection_string(cluster: &types::DbCluster) -> String {
    warn!("TODO! connection_string");
    let endpoint = cluster.endpoint().unwrap_or_default();
    let port = cluster.port().unwrap_or_default();
    let username = cluster.master_username().unwrap_or_default();
    format!("mysql -h {endpoint} -P {port} -u {username} -p")
}
