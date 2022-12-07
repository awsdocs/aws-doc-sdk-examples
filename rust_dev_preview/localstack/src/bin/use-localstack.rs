/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

// snippet-start:[localstack.rust.use-localstack]
use aws_smithy_http::endpoint::Endpoint;
use std::error::Error;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    tracing_subscriber::fmt::init();

    let shared_config = aws_config::from_env().load().await;
    let sqs_client = sqs_client(&shared_config);
    let s3_client = s3_client(&shared_config);

    let resp = s3_client.list_buckets().send().await?;
    let buckets = resp.buckets().unwrap_or_default();
    let num_buckets = buckets.len();

    println!("Buckets:");
    for bucket in buckets {
        println!("  {}", bucket.name().unwrap_or_default());
    }

    println!();
    println!("Found {} buckets.", num_buckets);
    println!();

    let repl = sqs_client.list_queues().send().await?;
    let queues = repl.queue_urls().unwrap_or_default();
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

fn localstack_endpoint() -> Endpoint {
    Endpoint::immutable("http://localhost:4566/").expect("valid endpoint")
}

fn sqs_client(conf: &aws_types::SdkConfig) -> aws_sdk_sqs::Client {
    let mut sqs_config_builder = aws_sdk_sqs::config::Builder::from(conf);
    if use_localstack() {
        sqs_config_builder = sqs_config_builder.endpoint_resolver(localstack_endpoint())
    }
    aws_sdk_sqs::Client::from_conf(sqs_config_builder.build())
}

fn s3_client(conf: &aws_types::SdkConfig) -> aws_sdk_s3::Client {
    let mut s3_config_builder = aws_sdk_s3::config::Builder::from(conf);
    if use_localstack() {
        s3_config_builder = s3_config_builder.endpoint_resolver(localstack_endpoint());
    }
    aws_sdk_s3::Client::from_conf(s3_config_builder.build())
}
// snippet-end:[localstack.rust.use-localstack]
