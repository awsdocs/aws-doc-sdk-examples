/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
use std::process;

use kms::model::DataKeySpec;

use kms::{Client, Config, Region};

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

    /// Specifies whether to display additonal runtime information
    #[structopt(short, long)]
    verbose: bool,
}

/// Creates an AWS KMS data key, showing the plaintext public key but not the plaintext private key.
/// # Arguments
///
/// * `[-k KEY]` - The name of the AWS KMS data key.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        key,
        default_region,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("GenerateDataKeyWithoutPlaintext called with options:");
        println!("Region:  {:?}", &region);
        println!("KMS key: {}", key);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let conf = Config::builder().region(region).build();
    let conn = aws_hyper::conn::Standard::https();
    let client = Client::from_conf_conn(conf, conn);

    let resp = match client
        .generate_data_key_without_plaintext()
        .key_id(key)
        .key_spec(DataKeySpec::Aes256)
        .send()
        .await
    {
        Ok(output) => output,
        Err(e) => {
            eprintln!("Encryption failure: {}", e);
            process::exit(1);
        }
    };

    // Did we get an encrypted blob?
    let blob = resp.ciphertext_blob.expect("Could not get encrypted text");
    let bytes = blob.as_ref();

    let s = base64::encode(&bytes);

    println!("\nData key:");
    println!("{}", s);
}
