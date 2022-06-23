/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_cognitoidentity::{Client, Error, Region, PKG_VERSION};
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

// Lists your identity pools.
// snippet-start:[cognitoidentity.rust.list-identity-pools]
async fn list_pools(client: &Client) -> Result<(), Error> {
    let response = client.list_identity_pools().max_results(10).send().await?;

    // Print IDs and names of pools.
    if let Some(pools) = response.identity_pools() {
        println!("Identity pools:");
        for pool in pools {
            let id = pool.identity_pool_id().unwrap_or_default();
            let name = pool.identity_pool_name().unwrap_or_default();
            println!("  Identity pool ID:   {}", id);
            println!("  Identity pool name: {}", name);
            println!();
        }
    }

    println!("Next token: {:?}", response.next_token());

    Ok(())
}
// snippet-end:[cognitoidentity.rust.list-identity-pools]

/// Lists your Amazon Cognito identity pools in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("Cognito client version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region_provider.region().await.unwrap().as_ref()
        );

        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    list_pools(&client).await
}
