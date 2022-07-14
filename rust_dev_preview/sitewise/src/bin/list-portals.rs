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

// List the portals under AWS IoT SiteWise.
// snippet-start:[sitewise.rust.list-portals]
async fn list_portals(client: &Client) -> Result<(), Error> {
    let resp = client.list_portals().send().await?;

    println!("Portals:");

    for asset in resp.portal_summaries.unwrap() {
        println!("  ID:  {}", asset.id().unwrap_or_default());
        println!("  Role ARN:  {}", asset.role_arn().unwrap_or_default());
        println!("  Name:   {}", asset.name().unwrap_or_default());
        println!(
            "  Description:   {}",
            asset.description().unwrap_or_default()
        );
        println!(
            "  Creation Date:   {}",
            asset.creation_date().unwrap().to_chrono_utc()
        );
        println!(
            "  Last Update Date:   {}",
            asset.last_update_date().unwrap().to_chrono_utc()
        );
        println!("  Start Url:   {}", asset.start_url().unwrap_or_default());
        println!(
            "  Current Status:   {}",
            asset.status().unwrap().state().unwrap().as_str()
        );

        println!();
    }

    println!();

    Ok(())
}
// snippet-end:[sitewise.rust.list-portals]

/// Lists the ID, Role Amazon Resource Name (ARN), name, description, creation_date,
/// last_update_data, start_url and status of your AWS IoT SiteWise asset models in
/// the Region.
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

    list_portals(&client).await
}
