// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

// snippet-start:[localstack.rust.use-localstack]
use aws_config::BehaviorVersion;
use std::error::Error;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    tracing_subscriber::fmt::init();

    let mut shared_config = aws_config::defaults(BehaviorVersion::latest());
    if use_localstack() {
        shared_config = shared_config.endpoint_url(LOCALSTACK_ENDPOINT);
    };
    let shared_config = shared_config.load().await;

    let sqs_client = sqs_client(&shared_config);
    let s3_client = s3_client(&shared_config);

    let resp = s3_client.list_buckets().send().await?;
    let buckets = resp.buckets();
    let num_buckets = buckets.len();

    println!("Buckets:");
    for bucket in buckets {
        println!("  {}", bucket.name().unwrap_or_default());
    }

    println!();
    println!("Found {} buckets.", num_buckets);
    println!();

    let repl = sqs_client.list_queues().send().await?;
    let queues = repl.queue_urls();
    let num_queues = queues.len();

    println!("Queue URLs:");
    for queue in queues {
        println!("  {}", queue);
    }

    println!();
    println!("Found {} queues.", num_queues);
    println!();

    if use_localstack() {
        println!("Using the local stack.");
    }

    Ok(())
}

/// If LOCALSTACK environment variable is true, use LocalStack endpoints.
/// You can use your own method for determining whether to use LocalStack endpoints.
fn use_localstack() -> bool {
    std::env::var("LOCALSTACK").unwrap_or_default() == "true"
}

const LOCALSTACK_ENDPOINT: &str = "http://localhost:4566/";

fn sqs_client(conf: &aws_config::SdkConfig) -> aws_sdk_sqs::Client {
    // Copy config from aws_config::SdkConfig to aws_sdk_sqs::Config
    let sqs_config_builder = aws_sdk_sqs::config::Builder::from(conf);
    aws_sdk_sqs::Client::from_conf(sqs_config_builder.build())
}

fn s3_client(conf: &aws_config::SdkConfig) -> aws_sdk_s3::Client {
    // Copy config from aws_config::SdkConfig to aws_sdk_s3::Config
    let s3_config_builder = aws_sdk_s3::config::Builder::from(conf);
    aws_sdk_s3::Client::from_conf(s3_config_builder.build())
}
// snippet-end:[localstack.rust.use-localstack]
