/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_kms::{Client, Error, Region, PKG_VERSION};
use std::process;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The # of bytes. Must be less than 1024.
    #[structopt(short, long)]
    length: i32,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Create a random string.
// snippet-start:[kms.rust.generate-random]
async fn make_string(client: &Client, length: i32) -> Result<(), Error> {
    let resp = client
        .generate_random()
        .number_of_bytes(length)
        .send()
        .await?;

    // Did we get an encrypted blob?
    let blob = resp.plaintext.expect("Could not get encrypted text");
    let bytes = blob.as_ref();

    let s = base64::encode(&bytes);

    println!();
    println!("Data key:");
    println!("{}", s);

    Ok(())
}
// snippet-end:[kms.rust.generate-random]

/// Creates a random byte string that is cryptographically secure.
/// # Arguments
///
/// * `[-l LENGTH]` - The number of bytes to generate. Must be less than 1024.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        length,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    // Trap out-of-range-values:
    match length {
        1...1024 => {
            println!("Generating a {} byte random string", length);
        }
        _ => {
            println!("Length {} is not within range 1-1024", length);
            process::exit(1);
        }
    }

    println!();

    if verbose {
        println!("KMS client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Length:             {}", &length);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    make_string(&client, length).await
}
