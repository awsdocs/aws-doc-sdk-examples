/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_ecs::{Error, Region, PKG_VERSION};
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

// List your clusters.
// snippet-start:[ecs.rust.describe-clusters]
async fn show_clusters(client: &aws_sdk_ecs::Client) -> Result<(), aws_sdk_ecs::Error> {
    let resp = client.describe_clusters().send().await?;

    let clusters = resp.clusters().unwrap_or_default();
    println!("Found {} clusters:", clusters.len());

    for cluster in clusters {
        println!("  ARN:  {}", cluster.cluster_arn().unwrap());
        println!("  Name: {}", cluster.cluster_name().unwrap());
    }
    Ok(())
}
// snippet-end:[ecs.rust.describe-clusters]

/// Lists your Amazon Elastic Container Service clusters in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt { region, verbose } = Opt::from_args();

    if verbose {
        tracing_subscriber::fmt::init();
    }

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    if verbose {
        println!();
        println!("ECS client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = aws_sdk_ecs::Client::new(&shared_config);

    show_clusters(&client).await
}
