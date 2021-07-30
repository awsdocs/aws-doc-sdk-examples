/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_cognitoidentity::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The ID of the identity pool to describe.
    #[structopt(short, long)]
    identity_pool_id: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the identities in an Amazon Cognito identity pool.
/// # Arguments
///
/// * `-i IDENTITY-POOL-ID` - The ID of the identity pool.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        identity_pool_id,
        region,
        verbose,
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Cognito client version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region.region().unwrap().as_ref()
        );
        println!("Identity pool ID:       {}", identity_pool_id);
        println!();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    let response = client
        .list_identities()
        .identity_pool_id(identity_pool_id)
        .max_results(10)
        .send()
        .await?;

    if let Some(ids) = response.identities {
        println!("Identitities:");
        for id in ids {
            let creation_timestamp = id.creation_date.unwrap().to_chrono();
            let idid = id.identity_id.unwrap_or_default();
            let mod_timestamp = id.last_modified_date.unwrap().to_chrono();
            println!("  Creation date:      {}", creation_timestamp);
            println!("  ID:                 {}", idid);
            println!("  Last modified date: {}", mod_timestamp);

            println!("  Logins:");
            for login in id.logins.unwrap_or_default() {
                println!("    {}", login);
            }

            println!();
        }
    }

    println!("Next token: {:?}", response.next_token);

    println!();

    Ok(())
}
