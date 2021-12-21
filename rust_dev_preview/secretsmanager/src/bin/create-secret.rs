/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_secretsmanager::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the secret.
    #[structopt(short, long)]
    name: String,

    /// The value of the secret.
    #[structopt(short, long)]
    secret_value: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Create a secret.
// snippet-start:[secretsmanager.rust.create-secret]
async fn make_secret(client: &Client, name: &str, value: &str) -> Result<(), Error> {
    client
        .create_secret()
        .name(name)
        .secret_string(value)
        .send()
        .await?;

    println!("Created secret");

    Ok(())
}
// snippet-end:[secretsmanager.rust.create-secret]

/// Creates a secret.
/// # Arguments
///
/// * `-n NAME` - The name of the secret.
/// * `-s SECRET_VALUE` - The secret value.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        name,
        region,
        secret_value,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("SecretsManager client version: {}", PKG_VERSION);
        println!(
            "Region:                        {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Secret name:                   {}", &name);
        println!("Secret value:                  {}", &secret_value);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    make_secret(&client, &name, &secret_value).await
}
