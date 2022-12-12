/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

//! Use lazy_static and AsyncOnce to allow on demand access to a global config and client.
//! Configs and clients are both cheap to clone (they are Tower stacks).
//! This example will log "Initializing SdkConfig" and "Initializing S3 Client" once each.
//! The requests then use clones of the globally initialized values.

use async_once::AsyncOnce;
use aws_config::SdkConfig;
use aws_sdk_s3::Client;
use lazy_static::lazy_static;
use tracing::{error, info};

lazy_static! {
    static ref SDK_CONFIG: AsyncOnce<SdkConfig> = AsyncOnce::new(async {
        info!("Initializing SdkConfig");
        aws_config::load_from_env().await
    });
    static ref S3_CLIENT: AsyncOnce<Client> = AsyncOnce::new(async {
        info!("Initializing S3 Client");
        let config = SDK_CONFIG.get().await;
        aws_sdk_s3::Client::new(config)
    });
}

async fn request(name: &str) {
    info!(name, "Starting request");

    // Get a clone of the client.
    let client = S3_CLIENT.get().await.clone();
    info!(name, "Client retrieved");

    // Use our clone to make a request.
    match client.list_buckets().send().await {
        Ok(response) => {
            info!(
                name,
                len = response.buckets().unwrap_or_default().len(),
                "Got buckets"
            )
        }
        Err(err) => {
            error!(name, %err, "Had an error");
        }
    }
}

#[tokio::main]
async fn main() {
    // Basic logging. Always log at info.
    std::env::set_var("RUST_LOG", "info");
    tracing_subscriber::fmt::init();

    // Run the request three times, but initialization only happens once.
    tokio::join!(request("a"), request("b"), request("c"),);
}
