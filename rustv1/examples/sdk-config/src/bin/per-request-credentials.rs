// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use std::time::{Duration, SystemTime};

use aws_config::BehaviorVersion;
use aws_credential_types::{credential_fn::provide_credentials_fn, Credentials};
use aws_sdk_s3::{Client, Config, Error};

/// Displays how many Amazon S3 buckets you have.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let shared_config = aws_config::defaults(BehaviorVersion::latest()).load().await;
    let client = Client::new(&shared_config);

    // snippet-start:[rust.per_operation_credentials]
    let resp = client
        .list_buckets()
        .customize()
        .config_override(
            Config::builder().credentials_provider(provide_credentials_fn(move || {
                // This snippet is for example purposes only. Production applications are responsible
                // for developing secure methods to provide Credentials at this point.
                let access_key_id = "access_key_id".to_string();
                let secret_access_key = "secret_access_key".to_string();
                let session_token = "session_token".to_string();
                let expires_after = SystemTime::now() + Duration::from_secs(60);
                async move {
                    Ok(Credentials::new(
                        access_key_id,
                        secret_access_key,
                        Some(session_token),
                        Some(expires_after),
                        "example_provider",
                    ))
                }
            })),
        )
        .send()
        .await?;
    // snippet-end:[rust.per_operation_credentials]
    let buckets = resp.buckets();

    println!("Found {} buckets in all regions.", buckets.len());

    Ok(())
}
