// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

// snippet-start:[localstack.rust.use-localstack]
use aws_config::BehaviorVersion;

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();

    // This creates a ConfigLoader with the default settings, and optionally overrides the
    // endpoint_url for any client created with this config. The overridden endpoint_url
    // can be used to make requests against a running localstack environment.
    let mut shared_config = aws_config::defaults(BehaviorVersion::latest());
    if let Some(url) = localstack_endpoint() {
        shared_config = shared_config.endpoint_url(url).test_credentials();
    };
    let shared_config = shared_config.load().await;

    // With the loaded configuration, create two clients to show simple operations-
    // list SQS queues and S3 buckets. Take note of the different crate names for the Client
    // and config::Builder items.
    let sqs_client =
        aws_sdk_sqs::Client::from_conf(aws_sdk_sqs::config::Builder::from(&shared_config).build());
    let s3_client =
        aws_sdk_s3::Client::from_conf(aws_sdk_s3::config::Builder::from(&shared_config).build());

    let list_buckets_response = s3_client.list_buckets().send().await;
    match list_buckets_response {
        Ok(response) => {
            let buckets = response.buckets();
            println!("{} Buckets:", buckets.len());
            for bucket in buckets {
                println!("  {}", bucket.name().unwrap_or_default());
            }
        }
        Err(err) => eprintln!("Failed to list S3 buckets: {err:?}"),
    }

    let list_queues_response = sqs_client.list_queues().send().await;
    match list_queues_response {
        Ok(response) => {
            let queues = response.queue_urls();
            println!("{} Queue URLs:", queues.len());
            for queue in queues {
                println!("  {}", queue);
            }
        }
        Err(err) => eprintln!("Failed to list SQS queues: {err:?}"),
    }

    println!(
        "Results came from {}",
        shared_config.endpoint_url().unwrap_or("(unknown endpoint)")
    );
}

/// If the LOCALSTACK environment variable is 'true' or a URL, use LocalStack endpoints.
/// Attempt to use the default localstack port 4566 if the variable is "true", or
/// if it's not empty, use that directly as the endpoint URL.
fn localstack_endpoint() -> Option<String> {
    let env_localstack = std::env::var("LOCALSTACK")
        .unwrap_or_default()
        .to_ascii_lowercase();
    match env_localstack.as_str() {
        "" => None,
        "true" => Some(LOCALSTACK_ENDPOINT.into()),
        _ => Some(env_localstack),
    }
}

const LOCALSTACK_ENDPOINT: &str = "http://localhost:4566/";
// snippet-end:[localstack.rust.use-localstack]
