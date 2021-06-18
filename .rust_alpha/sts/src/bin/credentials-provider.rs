/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_auth::{CredentialsError, ProvideCredentials};
use std::sync::{Arc, Mutex};
use std::time::{Duration, SystemTime};
use sts::Credentials;

/// Implements a basic version of ProvideCredentials with AWS STS
/// and lists the tables in the region based on those credentials.
#[tokio::main]
async fn main() -> Result<(), dynamodb::Error> {
    tracing_subscriber::fmt::init();
    let client = sts::Client::from_env();
    let sts_provider = StsCredentialsProvider {
        client,
        credentials: Arc::new(Mutex::new(None)),
    };
    sts_provider.spawn_refresh_loop().await;

    let dynamodb_conf = dynamodb::Config::builder()
        .credentials_provider(sts_provider)
        .build();
    let client = dynamodb::Client::from_conf(dynamodb_conf);
    println!("tables: {:?}", client.list_tables().send().await?);
    Ok(())
}

/// This is a rough example of how you could implement ProvideCredentials with Amazon STS.
///
/// Do not use this in production! A high quality implementation is in the roadmap.
#[derive(Clone)]
struct StsCredentialsProvider {
    client: sts::Client,
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
    pub async fn refresh(&self) -> Result<(), sts::Error> {
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
