/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::TimeoutConfig;
use aws_smithy_client::{conns, erase::DynConnector, hyper_ext};
use std::time::Duration;

/// The SDK divides timeouts into two groups:
///
/// - Timeouts that occur at the client level _(outside of a `Connector`)_, hereafter referred to
///   as "first group" timeouts
/// - Timeouts that occur at the connector level _(inside a `Connector`)_, hereafter referred to
///   as "second group" timeouts
///
/// In the future, all timeouts will be set in the same way. In the present, these two groups of
/// timeouts must be set separately. This app provides an example of how to set both groups of
/// timeouts.
///
/// **TLS negotiation timeouts will eventually be included with the second group but are
/// not yet supported**
///
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

    // Here we create an object that holds timeout-related configuration. We'll have to pass this
    // config into two places. This is because different timeouts are handled at different
    // "levels" of the AWS SDK Client networking stack. We'll note which timeouts are getting
    // set each time we pass in this cofiguration.
    let timeout_config = TimeoutConfig::new()
        // This timeout acts at the "Request to a service" level. When the SDK makes a request to a
        // service, that "request" may actually comprise of several HTTP requests in order to retry
        // failures that are likely spurious or to refresh credentials.
        .with_api_call_timeout(Some(Duration::from_secs(2)))
        // This timeout acts at the "HTTP request" level and will set a separate timeout for each
        // HTTP request made as part of a "service request"
        .with_api_call_attempt_timeout(Some(Duration::from_secs(2)))
        // A limit on the amount of time an application takes to attempt to read the first byte over
        // an established, open connection after write request.
        // Also known as the "time to first byte" timeout
        .with_read_timeout(Some(Duration::from_secs(2)))
        // A limit on the amount of time after making an initial connect attempt on a socket to
        // complete the connect-handshake
        .with_connect_timeout(Some(Duration::from_secs(2)));

    // Timeouts can be defined in your environment or AWS profile but in this example we
    // overrule any that happen to be set
    // NOTE: The two API call timeouts get set here
    let shared_config = aws_config::from_env()
        .timeout_config(timeout_config.clone())
        .load()
        .await;

    // These timeouts must also be passed to create the `Connector` that will handle our HTTP requests.
    // If a timeout needs to be changed after this, we'd have to create a new `Connector`.
    // NOTE: The read and connect timeouts get set here
    let conn = DynConnector::new(
        hyper_ext::Adapter::builder()
            .timeout(&timeout_config)
            .build(conns::https()),
    );
    let s3_config = aws_sdk_s3::Config::from(&shared_config);
    let client = aws_sdk_s3::Client::from_conf_conn(s3_config, conn);

    let resp = client.list_buckets().send().await?;

    for bucket in resp.buckets().unwrap_or_default() {
        println!("bucket: {:?}", bucket.name().unwrap_or_default())
    }

    Ok(())
}
