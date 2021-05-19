/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::fs;
use std::process;

use kms::{Blob, Client, Config, Region};

use aws_types::region::ProvideRegion;

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region. Overrides environment variable AWS_DEFAULT_REGION.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Specifies the encryption key
    #[structopt(short, long)]
    key: String,

    /// The name of the input file with encrypted text to decrypt
    #[structopt(short, long)]
    input_file: String,

    /// Specifies whether to display additonal runtime informmation
    #[structopt(short, long)]
    verbose: bool,
}

/// Decrypts a string encrypted by AWS KMS key.
/// # Arguments
///
/// * `-k KEY` - The encryption key.
/// * `-i INPUT-FILE` - The file containing the text encrypted by the key.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        key,
        input_file,
        default_region,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("KMS client version: {}\n", kms::PKG_VERSION);
        println!("Region:             {:?}", &region);
        println!("Key:                {}", key);
        println!("Input filename:     {}", input_file);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let conf = Config::builder().region(region).build();
    let conn = aws_hyper::conn::Standard::https();
    let client = Client::from_conf_conn(conf, conn);

    // Open input text file and get contents as a string
    // input is a base-64 encoded string, so decode it:
    let data = fs::read_to_string(input_file)
        .map(|input| {
            base64::decode(input).expect("Input file does not contain valid base 64 characters.")
        })
        .map(Blob::new);

    let resp = match client
        .decrypt()
        .key_id(key)
        .ciphertext_blob(data.unwrap())
        .send()
        .await
    {
        Ok(output) => output,
        Err(e) => {
            eprintln!("Encryption failure: {}", e);
            process::exit(1);
        }
    };

    let inner = resp.plaintext.unwrap();
    let bytes = inner.as_ref();

    let s = match String::from_utf8(bytes.to_vec()) {
        Ok(v) => v,
        Err(e) => panic!("Invalid UTF-8 sequence: {}", e),
    };

    println!();
    println!("Decoded string:");
    println!("{}", s);
}
