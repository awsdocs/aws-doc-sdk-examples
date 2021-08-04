/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_auth::provider::{CredentialsError, ProvideCredentials};
use aws_sdk_dynamodb::Error;
use aws_sdk_sts::{Credentials, Region};
use aws_types::region;
use aws_types::region::ProvideRegion;
use std::sync::{Arc, Mutex};
use std::time::{Duration, SystemTime};
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

/// Implements a basic version of ProvideCredentials with AWS STS
/// and lists the tables in the region based on those credentials.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("STS client version:      {}", aws_sdk_sts::PKG_VERSION);
        println!("DynamoDB client version: {}", aws_sdk_dynamodb::PKG_VERSION);
        println!(
            "Region:                  {}",
            region.region().unwrap().as_ref()
        );
        println!();
    }

    let config = aws_sdk_sts::Config::builder().region(region).build();
    let client = aws_sdk_sts::Client::from_conf(config);

    let sts_provider = StsCredentialsProvider {
        client,
        credentials: Arc::new(Mutex::new(None)),
    };

    sts_provider.spawn_refresh_loop().await;

    let dynamodb_conf = aws_sdk_dynamodb::Config::builder()
        .credentials_provider(sts_provider)
        .build();

    let client = aws_sdk_dynamodb::Client::from_conf(dynamodb_conf);

    println!("tables: {:?}", client.list_tables().send().await?);
    Ok(())
}

/// This is a rough example of how you could implement ProvideCredentials with Amazon STS.
///
/// Do not use this in production! A high quality implementation is in the roadmap.
#[derive(Clone)]
struct StsCredentialsProvider {
    client: aws_sdk_sts::Client,
    credentials: Arc<Mutex<Option<Credentials>>>,
}

impl ProvideCredentials for StsCredentialsProvider {
    fn provide_credentials(&self) -> Result<Credentials, CredentialsError> {
        let inner = self.credentials.lock().unwrap().clone();
        inner.ok_or(CredentialsError::CredentialsNotLoaded)
    }
}

impl StsCredentialsProvider {
    pub async fn spawn_refresh_loop(&self) {
        let _ = self
            .refresh()
            .await
            .map_err(|e| eprintln!("failed to load credentials! {}", e));
        let this = self.clone();
        tokio::spawn(async move {
            loop {
                let needs_refresh = {
                    let creds = this.credentials.lock().unwrap();
                    let expiry = creds.as_ref().and_then(|creds| creds.expiry());
                    if creds.is_none() {
                        true
                    } else {
                        expiry
                            .map(|expiry| SystemTime::now() > expiry)
                            .unwrap_or(false)
                    }
                };
                if needs_refresh {
                    let _ = this
                        .refresh()
                        .await
                        .map_err(|e| eprintln!("failed to load credentials! {}", e));
                }
                tokio::time::sleep(Duration::from_secs(5)).await;
            }
        });
    }
    pub async fn refresh(&self) -> Result<(), aws_sdk_sts::Error> {
        let session_token = self.client.get_session_token().send().await?;
        let sts_credentials = session_token
            .credentials
            .expect("should include credentials");
        *self.credentials.lock().unwrap() = Some(Credentials::new(
            sts_credentials.access_key_id.unwrap(),
            sts_credentials.secret_access_key.unwrap(),
            sts_credentials.session_token,
            sts_credentials
                .expiration
                .map(|expiry| expiry.to_system_time().expect("sts sent a time < 0")),
            "Sts",
        ));
        Ok(())
    }
}
