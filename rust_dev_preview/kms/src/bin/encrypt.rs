/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_kms::types::Blob;
use aws_sdk_kms::{Client, Error, Region, PKG_VERSION};
use std::fs::File;
use std::io::Write;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The encryption key.
    #[structopt(short, long)]
    key: String,

    /// The text to encrypt.
    #[structopt(short, long)]
    text: String,

    /// The name of the file to store the encrypted text in.
    #[structopt(short, long)]
    out_file: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Encrypts a string.
// snippet-start:[kms.rust.encrypt]
async fn encrypt_string(
    verbose: bool,
    client: &Client,
    text: &str,
    key: &str,
    out_file: &str,
) -> Result<(), Error> {
    let blob = Blob::new(text.as_bytes());

    let resp = client.encrypt().key_id(key).plaintext(blob).send().await?;

    // Did we get an encrypted blob?
    let blob = resp.ciphertext_blob.expect("Could not get encrypted text");
    let bytes = blob.as_ref();

    let s = base64::encode(&bytes);

    let mut ofile = File::create(&out_file).expect("unable to create file");
    ofile.write_all(s.as_bytes()).expect("unable to write");

    if verbose {
        println!("Wrote the following to {:?}", out_file);
        println!("{}", s);
    }

    Ok(())
}
// snippet-end:[kms.rust.encrypt]

/// Encrypts a string using an AWS KMS key.
/// # Arguments
///
/// * `-k KEY` - The KMS key.
/// * `-o OUT-FILE` - The name of the file to store the encrypted key in.
/// * `-t TEXT` - The string to encrypt.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        key,
        out_file,
        region,
        text,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("KMS client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Key:                {}", &key);
        println!("Text:               {}", &text);
        println!("Output file:        {}", &out_file);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    encrypt_string(verbose, &client, &text, &key, &out_file).await
}
