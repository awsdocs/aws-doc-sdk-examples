/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::timeout::TimeoutConfig;
use std::time::Duration;

/// The timeouts in this example are set to one second which may or may not be fast enough to
/// trigger them based on your connection speeds. If you want to ensure a timeout gets triggered
/// so you can see what the resulting error looks like, change the durations from
/// `Duration::from_secs(2)` to `Duration::from_millis(2)`.
///
/// You could _also_ trigger the timeouts by replacing the HTTPS connector _(`conns::https()`)_
/// with a `NeverConnector`. That's how we consistently trigger timeouts during testing.
#[tokio::main]
async fn main() -> Result<(), aws_sdk_s3::Error> {
    tracing_subscriber::fmt::init();

    // Here we create an object that holds timeout-related configuration.
    let timeout_config = TimeoutConfig::builder()
        // This timeout acts at the "Request to a service" level. When the SDK makes a request to a
        // service, that "request" can contain several HTTP requests. This way, you can retry
        // failures that are likely spurious, or refresh credentials.
        .operation_timeout(Duration::from_secs(2))
        // This timeout acts at the "HTTP request" level and sets a separate timeout for each
        // HTTP request made as part of a "service request."
        .operation_attempt_timeout(Duration::from_secs(2))
        // A limit on the amount of time an application takes to attempt to read the first byte over
        // an established, open connection after a write request.
        // Also known as the "time to first byte" timeout.
        .read_timeout(Duration::from_secs(2))
        // A time limit for completing the connect-handshake. The time starts when
        // making an initial connect attempt on a socket.
        .connect_timeout(Duration::from_secs(2))
        .build();

    let config = aws_config::from_env()
        .timeout_config(timeout_config.clone())
        .load()
        .await;
    let client = aws_sdk_s3::Client::new(&config);

    match client.list_buckets().send().await {
        Ok(response) => {
            for bucket in response.buckets().unwrap_or_default() {
                println!("bucket: {:?}", bucket.name().unwrap_or_default())
            }
        }
        Err(err) => {
            println!("{err}");
        }
    }

    Ok(())
}
