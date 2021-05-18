/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_hyper::StandardClient;
use kms::operation::GenerateRandom;
use kms::Region;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

/// Creates a random, 64-byte string that is cryptographically secure.
#[tokio::main]
async fn main() {
    SubscriberBuilder::default()
        .with_env_filter("info")
        .with_span_events(FmtSpan::CLOSE)
        .init();
    let config = kms::Config::builder()
        // region can also be loaded from AWS_DEFAULT_REGION, just remove this line.
        .region(Region::new("us-east-1"))
        // creds loaded from environment variables, or they can be hard coded.
        // Other credential providers not currently supported
        .build();
    // NB: This example uses the "low level internal API" for demonstration purposes
    // This is sometimes necessary to get precise control over behavior, but in most cases
    // using `kms::Client` is recommended.
    let client: StandardClient = aws_hyper::Client::https();
    let data = client
        .call(
            GenerateRandom::builder()
                .number_of_bytes(64)
                .build()
                .expect("valid operation")
                .make_operation(&config)
                .expect("valid operation"),
        )
        .await
        .expect("failed to generate random data");
    println!("{:?}", data);
    assert_eq!(data.plaintext.expect("should have data").as_ref().len(), 64);
}
