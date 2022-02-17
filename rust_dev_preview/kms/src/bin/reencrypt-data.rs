/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_kms::types::Blob;
use aws_sdk_kms::{Client, Error, Region, PKG_VERSION};
use std::fs;
use std::fs::File;
use std::io::Write;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The original encryption key.
    #[structopt(short, long)]
    first_key: String,

    /// The new encryption key.
    #[structopt(short, long)]
    new_key: String,

    /// The name of the input file containing the text to re-encrypt.
    #[structopt(short, long)]
    input_file: String,

    /// The name of the output file containing the re-encrypted text.
    #[structopt(short, long)]
    output_file: String,

    /// Whether to display additional runtime information.
    #[structopt(short, long)]
    verbose: bool,
}

// Re-encrypts a string.
// snippet-start:[kms.rust.reencrypt-data]
async fn reencrypt_string(
    verbose: bool,
    client: &Client,
    input_file: &str,
    output_file: &str,
    first_key: &str,
    new_key: &str,
) -> Result<(), Error> {
    // Get blob from input file
    // Open input text file and get contents as a string
    // input is a base-64 encoded string, so decode it:
    let data = fs::read_to_string(input_file)
        .map(|input_file| base64::decode(input_file).expect("invalid base 64"))
        .map(Blob::new);

    let resp = client
        .re_encrypt()
        .ciphertext_blob(data.unwrap())
        .source_key_id(first_key)
        .destination_key_id(new_key)
        .send()
        .await?;

    // Did we get an encrypted blob?
    let blob = resp.ciphertext_blob.expect("Could not get encrypted text");
    let bytes = blob.as_ref();

    let s = base64::encode(&bytes);
    let o = &output_file;

    let mut ofile = File::create(o).expect("unable to create file");
    ofile.write_all(s.as_bytes()).expect("unable to write");

    if verbose {
        println!("Wrote the following to {}:", output_file);
        println!("{}", s);
    } else {
        println!("Wrote base64-encoded output to {}", output_file);
    }

    Ok(())
}
// snippet-end:[kms.rust.reencrypt-data]

/// Re-encrypts a string with an AWS KMS key.
/// # Arguments
///
/// * `[-f FIRST-KEY]` - The first key used to originally encrypt the string.
/// * `[-n NEW-KEY]` - The new key used to re-encrypt the string.
/// * `[-i INPUT-FILE]` - The file containing the encrypted string.
/// * `[-o OUTPUT-FILE]` - The file containing the re-encrypted string.
/// * `[-r REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        first_key,
        new_key,
        input_file,
        output_file,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("KMS client version:     {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Input key:              {}", &first_key);
        println!("Output key:             {}", &new_key);
        println!("Input filename:         {}", &input_file);
        println!("Output filename:        {}", &output_file);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    reencrypt_string(
        verbose,
        &client,
        &input_file,
        &output_file,
        &first_key,
        &new_key,
    )
    .await
}
