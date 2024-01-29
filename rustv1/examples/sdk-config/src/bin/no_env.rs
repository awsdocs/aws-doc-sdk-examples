// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
#![allow(clippy::result_large_err)]

// File overview: This example shows how to create an SdkConfig without using the
// default AWS environment configuration.

use aws_config::meta::region::RegionProviderChain;
use aws_credential_types::provider::{ProvideCredentials, SharedCredentialsProvider};
use aws_sdk_s3::config::retry::RetryConfig;
use aws_sdk_s3::config::Credentials;
use aws_sdk_s3::{config::Region, meta::PKG_VERSION, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The number of (re)tries.
    #[structopt(short, long, default_value = "2")]
    tries: u32,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Shows your buckets.
async fn show_num_buckets(client: &Client) -> Result<(), Error> {
    let resp = client.list_buckets().send().await?;
    let buckets = resp.buckets();

    println!("Found {} buckets in all regions.", buckets.len());

    Ok(())
}

/// A Credentials provider that uses non-standard environment variables `MY_ID`
/// and `MY_KEY` instead of `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY`. This
/// could alternatively load credentials from another secure source of environment
/// secrets, if traditional AWS SDK for Rust credentials providers do not suit
/// your use case.
///
/// WARNING: This example is for demonstration purposes only. Your code should always
/// load credentials in a secure fashion.
#[derive(Debug)]
struct StaticCredentials {
    access_key_id: String,
    secret_access_key: String,
}

impl StaticCredentials {
    pub fn new() -> Self {
        let access_key_id = std::env::var("MY_ID").expect("access key id");
        let secret_access_key = std::env::var("MY_KEY").expect("secret access key");
        Self {
            access_key_id: access_key_id.trim().to_string(),
            secret_access_key: secret_access_key.trim().to_string(),
        }
    }

    async fn load_credentials(&self) -> aws_credential_types::provider::Result {
        Ok(Credentials::new(
            self.access_key_id.clone(),
            self.secret_access_key.clone(),
            None,
            None,
            "StaticCredentials",
        ))
    }
}

impl ProvideCredentials for StaticCredentials {
    fn provide_credentials<'a>(
        &'a self,
    ) -> aws_credential_types::provider::future::ProvideCredentials<'a>
    where
        Self: 'a,
    {
        aws_credential_types::provider::future::ProvideCredentials::new(self.load_credentials())
    }
}

/// Displays how many Amazon S3 buckets you have.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-t TRIES]` - The number of times to (re)try the request.
/// * `[-v]` - Whether to display additional information.
///
/// # Environment
///
/// * MY_ID - an AWS IAM Access Key ID to use for this request.
/// * MY_KEY - an AWS IAM Secret Access Key to use for this request.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        tries,
        verbose,
    } = Opt::parse();

    let region = RegionProviderChain::first_try(region.map(Region::new))
        .or_else(Region::new("us-west-2"))
        .region()
        .await
        .expect("parsed region");
    println!();

    if verbose {
        println!("S3 client version: {PKG_VERSION}");
        println!("Region:            {region}");
        println!("Retries:           {tries}");
        println!();
    }

    assert_ne!(tries, 0, "You cannot set zero retries.");

    let shared_config = aws_config::SdkConfig::builder()
        .region(region)
        .credentials_provider(SharedCredentialsProvider::new(StaticCredentials::new()))
        // Set max attempts.
        // If tries is 1, there are no retries.
        .retry_config(RetryConfig::standard().with_max_attempts(tries))
        .build();

    // Construct an S3 client with customized retry configuration.
    let client = Client::new(&shared_config);

    show_num_buckets(&client).await
}
