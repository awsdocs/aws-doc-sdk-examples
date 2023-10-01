use std::fmt::Display;

use anyhow::anyhow;
use inquire::{validator::StringValidator, CustomUserError};
use rds_code_examples::aurora::{AuroraScenario, ScenarioError};
use tracing::warn;

#[derive(Default, Debug)]
struct Warnings(Vec<String>);

impl Warnings {
    fn new() -> Self {
        Warnings(Vec::with_capacity(5))
    }

    fn push(&mut self, warning: &str, error: ScenarioError) {
        let formatted = format!("{warning}: {error}");
        warn!("{formatted}");
        self.0.push(formatted);
    }

    fn is_empty(&self) -> bool {
        self.0.is_empty()
    }
}

impl Display for Warnings {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        writeln!(f, "Warnings:")?;
        for warning in &self.0 {
            writeln!(f, "{: >4}- {warning}", "")?;
        }
        Ok(())
    }
}

#[tokio::main]
async fn main() -> Result<(), anyhow::Error> {
    tracing_subscriber::fmt::init();

    let sdk_config = aws_config::from_env().load().await;
    let mut scenario = AuroraScenario::prepare_scenario(&sdk_config).await;

    // Get available engine families for Aurora MySql. rds.DescribeDbEngineVersions(Engine='aurora-mysql') and build a set of the 'DBParameterGroupFamily' field values. I get {aurora-mysql8.0, aurora-mysql5.7}.
    let available_engines = scenario.engines().await;
    if let Err(error) = available_engines {
        return Err(anyhow!("Failed to get available engines: {}", error));
    }
    let available_engines = available_engines.unwrap();

    // Select an engine family and create a custom DB cluster parameter group. rds.CreateDbClusterParameterGroup(DBParameterGroupFamily='aurora-mysql8.0')
    let engine = inquire::Select::new(
        "Select an Aurora engine family",
        available_engines.iter().map(|s| s.as_str()).collect(),
    )
    .prompt();

    if let Err(error) = engine {
        return Err(anyhow!("Invalid engine selection: {}", error));
    }

    let set_engine = scenario.set_engine(engine.unwrap()).await;
    if let Err(error) = set_engine {
        return Err(anyhow!("Could not set engine: {}", error));
    }

    // At this point, the scenario has things in AWS and needs to get cleaned up.
    let mut warnings = Warnings::new();

    show_parameters(&scenario, &mut warnings).await;

    let offset = prompt_number_or_default(&mut warnings, "auto_increment_offset", 5);
    let increment = prompt_number_or_default(&mut warnings, "auto_increment_increment", 3);

    // Modify both the auto_increment_offset and auto_increment_increment parameters in one call in the custom parameter group. Set their ParameterValue fields to a new allowable value. rds.ModifyDbClusterParameterGroup.
    let update_auto_increment = scenario.update_auto_increment(offset, increment).await;

    if let Err(error) = update_auto_increment {
        warnings.push("Failed to update auto increment", error);
    }

    // Get and display the updated parameters. Specify Source of 'user' to get just the modified parameters. rds.DescribeDbClusterParameters(Source='user')
    show_parameters(&scenario, &mut warnings).await;

    // Create an Aurora DB cluster database cluster that contains a MySql database and uses the parameter group you created.
    // Create a database instance in the cluster.
    // Wait for DB instance to be ready. Call rds.DescribeDbInstances and check for DBInstanceStatus == 'available'.
    let start_instance = scenario.start_cluster_and_instance().await;
    match start_instance {
        Ok(instance) => {
            println!(
                "Instance ready at: {}",
                instance.db_instance_arn().unwrap_or("Missing ARN")
            );
        }
        Err(err) => warnings.push(
            "Failed to create and instantiate a cluster and instance",
            err,
        ),
    }

    println!("Database ready: {}", scenario.connection_string());

    let _ = inquire::Text::new("Use the database with the connection string. When you're finished, press enter key to continue.").prompt();

    // Create a snapshot of the DB cluster. rds.CreateDbClusterSnapshot.
    // Wait for the snapshot to create. rds.DescribeDbClusterSnapshots until Status == 'available'.
    let snapshot = scenario.snapshot().await;
    match snapshot {
        Ok(snapshot) => {
            println!(
                "Snapshot is available: {}",
                snapshot.db_cluster_snapshot_arn().unwrap_or("Missing ARN")
            )
        }
        Err(err) => warnings.push("Failed to create a snapshot", err),
    }

    // Delete the instance. rds.DeleteDbInstance.
    // Delete the DB cluster. rds.DeleteDbCluster.
    // Wait for the instance and cluster to fully delete. rds.DescribeDbInstances and rds.DescribeDbClusters until both are not found.
    // Delete the DB cluster parameter group. rds.DeleteDbClusterParameterGroup.
    let clean_up = scenario.clean_up().await;
    if let Err(errors) = clean_up {
        for error in errors {
            warnings.push("Failed to clean up scenario", error);
        }
    }

    if warnings.is_empty() {
        Ok(())
    } else {
        println!("There were problems running the scenario:");
        println!("{warnings}");
        Err(anyhow!("There were problems running the scenario"))
    }
}

#[derive(Clone)]
struct U8Validator {}
impl StringValidator for U8Validator {
    fn validate(&self, input: &str) -> Result<inquire::validator::Validation, CustomUserError> {
        if input.parse::<u8>().is_err() {
            Ok(inquire::validator::Validation::Invalid(
                "Can't parse input as number".into(),
            ))
        } else {
            Ok(inquire::validator::Validation::Valid)
        }
    }
}

async fn show_parameters(scenario: &AuroraScenario, warnings: &mut Warnings) {
    let parameters = scenario.cluster_parameters().await;

    match parameters {
        Ok(parameters) => {
            println!("Current parameters");
            for parameter in parameters {
                println!("\t{parameter}");
            }
        }
        Err(error) => warnings.push("Could not find cluster parameters", error),
    }
}

fn prompt_number_or_default(warnings: &mut Warnings, name: &str, default: u8) -> u8 {
    let input = inquire::Text::new(format!("Updated {name}:").as_str())
        .with_validator(U8Validator {})
        .prompt();

    match input {
        Ok(increment) => match increment.parse::<u8>() {
            Ok(increment) => increment,
            Err(error) => {
                warnings.push(
                    format!("Invalid updated {name} (using {default} instead)").as_str(),
                    ScenarioError::with(format!("{error}")),
                );
                default
            }
        },
        Err(error) => {
            warnings.push(
                format!("Invalid updated {name} (using {default} instead)").as_str(),
                ScenarioError::with(format!("{error}")),
            );
            default
        }
    }
}
