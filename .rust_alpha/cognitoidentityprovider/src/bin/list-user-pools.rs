/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
use aws_sdk_cognitoidentityprovider::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
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

/// Lists your Amazon Cognito user pools in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The region containing the buckets.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    if verbose {
        println!("Cognito client version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region.region().unwrap().as_ref()
        );
        println!();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    let response = client.list_user_pools().max_results(10).send().await?;
    if let Some(pools) = response.user_pools {
        println!("User pools:");
        for pool in pools {
            println!("  ID:              {}", pool.id.unwrap_or_default());
            println!("  Name:            {}", pool.name.unwrap_or_default());
            println!("  Status:          {:?}", pool.status);
            println!("  Lambda Config:   {:?}", pool.lambda_config.unwrap());
            println!(
                "  Last modified:   {}",
                pool.last_modified_date.unwrap().to_chrono()
            );
            println!(
                "  Creation date:   {:?}",
                pool.creation_date.unwrap().to_chrono()
            );
            println!();
        }
    }
    println!("Next token: {}", response.next_token.unwrap_or_default());

    Ok(())
}
