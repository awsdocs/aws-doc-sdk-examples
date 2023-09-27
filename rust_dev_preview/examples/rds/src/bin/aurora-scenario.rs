use std::fmt::Display;

use anyhow::anyhow;
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
    if available_engines.is_err() {
        return Err(anyhow!(
            "Failed to get available engines: {}",
            available_engines.err()
        ));
    }

    // Select an engine family and create a custom DB cluster parameter group. rds.CreateDbClusterParameterGroup(DBParameterGroupFamily='aurora-mysql8.0')
    let engine = inquire::Select::new(
        "Select an Aurora engine family",
        available_engines.map(|s| s.to_str()),
    )
    .prompt();

    if engine.is_err() {
        return Err(anyhow!("Invalid engine selection: {}", engine.err()));
    }

    let set_engine = scenario.set_engine(engine.unwrap()).await;
    if set_engine.is_err() {
        return Err(anyhow!("Could not set engine: {}", set_engine.err()));
    }

    // At this point, the scenario has things in AWS and needs to get cleaned up.
    let mut warnings = Warnings::new();

    show_parameters(&scenario, &mut warnings).await;

    let mut updated_auto_increment_offset = inquire::Text::new("Updated auto_increment_offset:")
        .with_validator(make_u8_validator())
        .prompt();
    let updated_auto_increment_increment = inquire::Text::new("Updated auto_increment_increment:")
        .with_validator(make_u8_validator())
        .prompt();

    if updated_auto_increment_offset.is_err() {
        warnings.push(
            "Invalid updated auto_increment_offset (using 5 instead)",
            updated_auto_increment_offset.err(),
        );
        updated_auto_increment_increment = Ok(5)
    }
    if updated_auto_increment_increment.is_err() {
        warnings.push(
            "Invalid updated auto_increment_increment (using 3 instead)",
            updated_auto_increment_increment.err(),
        );
        updated_auto_increment_increment = Ok(3)
    }

    // Modify both the auto_increment_offset and auto_increment_increment parameters in one call in the custom parameter group. Set their ParameterValue fields to a new allowable value. rds.ModifyDbClusterParameterGroup.
    let update_auto_increment = scenario
        .update_auto_increment(
            updated_auto_increment_offset.unwrap(),
            updated_auto_increment_increment.unwrap(),
        )
        .await;
    if update_auto_increment.is_err() {
        warnings.push(
            "Failed to update auto increment",
            update_auto_increment.err(),
        );
    }

    // Get and display the updated parameters. Specify Source of 'user' to get just the modified parameters. rds.DescribeDbClusterParameters(Source='user')
    show_parameters(&scenario, &mut warnings).await;

    // Create an Aurora DB cluster database cluster that contains a MySql database and uses the parameter group you created.
    // Create a database instance in the cluster.
    // Wait for DB instance to be ready. Call rds.DescribeDbInstances and check for DBInstanceStatus == 'available'.
    let start_instance = scenario.start_cluster_and_instance().await;
    match start_instance {
        Ok(instance) => todo!(),
        Err(err) => warnings.push(
            "Failed to create and instantiate a cluster and instance",
            instance.err(),
        ),
    }

    let _ = inquire::Confirm::new("Use the database with the connection string. When you're finished, press enter key to continue.").prompt();

    // Create a snapshot of the DB cluster. rds.CreateDbClusterSnapshot.
    // Wait for the snapshot to create. rds.DescribeDbClusterSnapshots until Status == 'available'.
    let snapshot = scenario.snapshot().await;
    match snapshot {
        Ok(snapshot) => todo!(),
        Err(err) => warnings.push("Failed to create a snapshot", err),
    }

    // Delete the instance. rds.DeleteDbInstance.
    // Delete the DB cluster. rds.DeleteDbCluster.
    // Wait for the instance and cluster to fully delete. rds.DescribeDbInstances and rds.DescribeDbClusters until both are not found.
    // Delete the DB cluster parameter group. rds.DeleteDbClusterParameterGroup.
    let clean_up = scenario.clean_up().await;
    if clean_up.is_err() {
        return anyhow!("Failed to clean up scenario", clean_up.err());
    }

    Ok(())
}

fn make_u8_validator() {
    |input: &str| {
        if u8::try_from(input).is_err() {
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
    if parameters.is_err() {
        warnings.push("Could not find cluster parameters", parameters.err());
    }

    // Get parameters in the group. This is a long list so you will have to paginate. Find the auto_increment_offset and auto_increment_increment parameters (by ParameterName). rds.DescribeDbClusterParameters
    println!("Current parameters");
    for parameter in parameters.unwrap() {
        println!("\t{parameter}");
    }
}
