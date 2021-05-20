/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use secretsmanager::{Client, Config, Region};

use aws_types::region::{ProvideRegion};

use structopt::StructOpt;

use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    default_region: Option<String>,

    /// The name of the secret
    #[structopt(short, long)]
    name: String,

    /// The content of the secret
    #[structopt(short, long)]
    content: String,
    
    /// Whether to display additonal information
    #[structopt(short, long)]
    verbose: bool,
}

/// Creates a Secrets Manager secret.
/// # Arguments
///
/// * `[-n NAME]` - The name of the secret.
/// * `[-c CONTENT]` - The contents of the secret.
/// * `[-d DEFAULT-REGION]` - The region containing the voices.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        content,
        name,
        default_region,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!(
            "SecretsManager client version: {}\n",
            secretsmanager::PKG_VERSION
        );
        println!("Region:       {:?}", &region);
        println!("Secret name:  {}", name);
        println!("Secret value: {}", content);

        SubscriberBuilder::default()
            .with_env_filter("verbose")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();

    let client = Client::from_conf(config);

    match client
        .create_secret()
        .name(name)
        .secret_string(content)
        .send()
        .await
    {
        Ok(_) => println!("Created secret"),
        Err(e) => panic!("Failed to create secret: {}", e),
    };
}
