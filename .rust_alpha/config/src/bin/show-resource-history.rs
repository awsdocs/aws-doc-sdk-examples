/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_config::model::ResourceType;
use aws_sdk_config::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The ID of the resource.
    #[structopt(short, long)]
    id: String,

    /// The resource type.
    #[structopt(long)]
    resource_type: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Shows the history for a resource.
// snippet-start:[config.rust.show-resource-history]
async fn show_history(client: &Client, id: &str, res: ResourceType) -> Result<(), Error> {
    let rsp = client
        .get_resource_config_history()
        .resource_id(id)
        .resource_type(res)
        .send()
        .await?;
    println!("configuration history for {}:", id);
    for item in rsp.configuration_items().unwrap_or_default() {
        println!("item: {:?}", item);
    }

    Ok(())
}
// snippet-end:[config.rust.show-resource-history]

/// Displays the configuration history for a resource in the Region.
///
/// NOTE: AWS Config must be enabled to discover resources.
/// # Arguments
///
/// * `-i ID` - The ID of the resource.
/// * `--resource-type RESOURCE-TYPE` - The resource type, such as `AWS::EC2::SecurityGroup`.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        id,
        resource_type,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("Config client version: {}", PKG_VERSION);
        println!(
            "Region:                {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Resource ID:           {}", &id);
        println!("Resource type:         {}", &resource_type);
        println!();
    }

    // Parse resource type from user input.
    let parsed = ResourceType::from(resource_type.as_str());

    // Make sure it's a known type.
    if matches!(parsed, ResourceType::Unknown(_)) {
        panic!(
            "unknown resource type: `{}`. Valid resource types: {:#?}",
            &resource_type,
            ResourceType::values()
        )
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_history(&client, &id, parsed).await
}
