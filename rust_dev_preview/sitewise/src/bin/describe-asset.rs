/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_iotsitewise::types::DisplayErrorContext;
use aws_sdk_iotsitewise::{Client, Region, PKG_VERSION};
use aws_smithy_types_convert::date_time::DateTimeExt;
use sitewise_code_examples::Error;
use std::process;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The asset id.
    #[structopt(short, long)]
    asset_id: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Describe an asset under AWS IoT SiteWise.
// snippet-start:[sitewise.rust.describe-asset]
async fn list_assets(client: &Client, asset_id: Option<String>) -> Result<(), Error> {
    let asset = client
        .describe_asset()
        .set_asset_id(asset_id)
        .send()
        .await?;

    println!("Your Asset:");

    println!("  Asset ID:  {}", asset.asset_id().unwrap_or_default());
    println!("  Asset ARN:  {}", asset.asset_arn().unwrap_or_default());
    println!("  Asset Name:   {}", asset.asset_name().unwrap_or_default());
    println!(
        "  Asset Model ID:   {}",
        asset.asset_model_id().unwrap_or_default()
    );
    println!(
        "  Asset Creation Date:   {}",
        asset.asset_creation_date().unwrap().to_chrono_utc()?
    );
    println!(
        "  Asset Last Update Date:   {}",
        asset.asset_last_update_date().unwrap().to_chrono_utc()?
    );
    println!(
        "  Asset Status:   {}",
        asset.asset_status().unwrap().state().unwrap().as_str()
    );

    println!("  Assets Hierarchies:");

    for asset_hierarchy in asset.asset_hierarchies().unwrap() {
        println!("    ID:   {}", asset_hierarchy.id().unwrap_or_default());
        println!("    Name:   {}", asset_hierarchy.name().unwrap_or_default());
    }

    println!("  Assets Properties:");

    for asset_property in asset.asset_properties().unwrap() {
        println!(
            "    Alias:   {}",
            asset_property.alias().unwrap_or_default()
        );
        println!(
            "    Data Type:   {}",
            asset_property.data_type().unwrap().as_str()
        );
        println!(
            "    Data Type Spec:   {}",
            asset_property.data_type_spec().unwrap_or_default()
        );
        println!("    ID:   {}", asset_property.id().unwrap_or_default());
        println!("    Name:   {}", asset_property.name().unwrap_or_default());
        println!(
            "    Notification State:   {}",
            asset_property
                .notification()
                .as_ref()
                .unwrap()
                .state()
                .as_ref()
                .unwrap()
                .as_str()
        );
        println!(
            "    Notification Topic:   {}",
            asset_property
                .notification()
                .as_ref()
                .unwrap()
                .topic()
                .as_ref()
                .unwrap()
        );
        println!(
            "    Unit:   {}",
            asset_property.unit.as_deref().unwrap_or_default()
        );
    }

    println!("  Assets Composite Models:");

    for asset_composite_model in asset.asset_composite_models().unwrap() {
        println!(
            "    Description:   {}",
            asset_composite_model.description().unwrap_or_default()
        );
        println!(
            "    Name:   {}",
            asset_composite_model.name().unwrap_or_default()
        );

        println!("    Properties:");

        for property in asset_composite_model.properties().unwrap() {
            println!("      Alias:   {}", property.alias().unwrap_or_default());
            println!(
                "      Data Type:   {}",
                property.data_type().unwrap().as_str()
            );
            println!(
                "      Data Type Spec:   {}",
                property.data_type_spec().unwrap_or_default()
            );
            println!("      ID:   {}", property.id.as_deref().unwrap_or_default());
            println!("      Name:   {}", property.name().unwrap_or_default());
            println!(
                "      Notification State:   {}",
                property
                    .notification()
                    .as_ref()
                    .unwrap()
                    .state()
                    .as_ref()
                    .unwrap()
                    .as_str()
            );
            println!(
                "      Notification Topic:   {}",
                property
                    .notification()
                    .as_ref()
                    .unwrap()
                    .topic()
                    .as_ref()
                    .unwrap()
            );
            println!("      Unit:   {}", property.unit().unwrap_or_default());
        }
    }

    println!();

    Ok(())
}
// snippet-end:[sitewise.rust.describe-asset]

/// Show the asset_id, asset_arn, asset_name, asset_creation_date,
/// asset_last_update_data, asset_model_id, status, asset_properties,
/// asset_composite_models and asset_hierarchies of a specific AWS IoT SiteWise asset
/// in the Region.
///
/// # Arguments
///
/// * `[-a ASSET-ID]` - The ID of the asset by which to locate the specific asset.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();

    if let Err(err) = run_example(Opt::from_args()).await {
        eprintln!("Error: {}", DisplayErrorContext(err));
        process::exit(1);
    }
}

async fn run_example(
    Opt {
        region,
        asset_id,
        verbose,
    }: Opt,
) -> Result<(), Error> {
    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("IoT client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    list_assets(&client, asset_id).await
}
