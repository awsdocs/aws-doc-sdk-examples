/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_smithy_client::{conns, erase::DynConnector, hyper_ext};
// Note: `TriState` is used to distinguish between configurations that are
// intentionally set, disabled, or unset.
//
// If you're familiar with languages like SQL or JavaScript, it might be
// helpful to think of the `TriState` definitions as follows:
//
// - `TriState::Set(value)` is a set value
// - `TriState::Disabled` is similar to `null`
// - `TriState::Unset` is similar to `undefined`
//
// With these distinctions, it's less complicated to merge configurations
// from multiple sources. Set or disabled values are kept, while unset values are overwritten.
use aws_smithy_types::{timeout, tristate::TriState};
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
    // set each time we pass in this configuration.
    let timeout_config = timeout::Config::new()
        .with_api_timeouts(
            timeout::Api::new()
                // This timeout acts at the "Request to a service" level. When the SDK makes a request to a
                // service, that "request" can contain several HTTP requests. This way, you can retry
                // failures that are likely spurious, or refresh credentials.
                .with_call_timeout(TriState::Set(Duration::from_secs(2)))
                // This timeout acts at the "HTTP request" level and sets a separate timeout for each
                // HTTP request made as part of a "service request."
                .with_call_attempt_timeout(TriState::Set(Duration::from_secs(2))),
        )
        .with_http_timeouts(
            timeout::Http::new()
                // A limit on the amount of time an application takes to attempt to read the first byte over
                // an established, open connection after a write request.
                // Also known as the "time to first byte" timeout.
                .with_read_timeout(TriState::Set(Duration::from_secs(2)))
                // A time limit for completing the connect-handshake. The time starts when
                // making an initial connect attempt on a socket.
                .with_connect_timeout(TriState::Set(Duration::from_secs(2))),
        );

    // You can define timeouts in your environment or your AWS profile, but in the following
    // example, we overrule any previously set timeouts.
    // NOTE: The two API call timeouts get set here.
    let shared_config = aws_config::from_env()
        .timeout_config(timeout_config.clone())
        .load()
        .await;

    // These timeouts must also be passed to create the `Connector` that will handle our HTTP requests.
    // If a timeout needs to be changed after this, we'd have to create a new `Connector`.
    // NOTE: The read and connect timeouts get set here.
    let conn = DynConnector::new(
        hyper_ext::Adapter::builder()
            .timeout(&timeout_config.http)
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
