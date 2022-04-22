/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_iotsitewise::{Client, Error, Region, PKG_VERSION};
use aws_smithy_types_convert::date_time::DateTimeExt;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// List the asset models under AWS IoT SiteWise.
// snippet-start:[sitewise.rust.list-asset-models]
async fn list_asset_models(client: &Client) -> Result<(), Error> {
    let resp = client.list_asset_models().send().await?;

    println!("Asset Models:");

    for asset in resp.asset_model_summaries.unwrap() {
        println!("  ID:  {}", asset.id.as_deref().unwrap_or_default());
        println!("  ARN:  {}", asset.arn.as_deref().unwrap_or_default());
        println!("  Name:   {}", asset.name.as_deref().unwrap_or_default());
        println!(
            "  Description:   {}",
            asset.description.as_deref().unwrap_or_default()
        );
        println!(
            "  Creation Date:   {}",
            asset.creation_date.unwrap().to_chrono_utc()
        );
        println!(
            "  Last Update Date:   {}",
            asset.last_update_date.unwrap().to_chrono_utc()
        );
        println!(
            "  Current Status:   {}",
            asset.status.unwrap().state.unwrap().as_str()
        );

        println!();
    }

    println!();

    Ok(())
}
// snippet-end:[sitewise.rust.list-asset-models]

/// Lists the ID, Amazon Resource Name (ARN), name, description, creation_date, last_update_data,
/// and status of your AWS IoT SiteWise asset models in the Region.
///
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt { region, verbose } = Opt::from_args();

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

    list_asset_models(&client).await
}
