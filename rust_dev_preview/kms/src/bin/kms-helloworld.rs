/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_kms::middleware::DefaultMiddleware;
use aws_sdk_kms::operation::GenerateRandom;
use aws_sdk_kms::{Config, Region};
use aws_smithy_client::erase::DynConnector;

// snippet-start:[kms.rust.kms-helloworld]
/// Creates a random byte string that is cryptographically secure in __us-east-1__.
#[tokio::main]
async fn main() {
    let config = Config::builder()
        // region can also be loaded from AWS_DEFAULT_REGION, just remove this line.
        .region(Region::new("us-east-1"))
        // creds loaded from environment variables, or they can be hard coded.
        // Other credential providers not currently supported
        .build();
    // NB: This example uses the "low level internal API" for demonstration purposes
    // This is sometimes necessary to get precise control over behavior, but in most cases
    // using `kms::Client` is recommended.
    let client = aws_smithy_client::Client::<DynConnector, DefaultMiddleware>::dyn_https();

    let data = client
        .call(
            GenerateRandom::builder()
                .number_of_bytes(64)
                .build()
                .expect("valid operation")
                .make_operation(&config)
                .await
                .expect("valid operation"),
        )
        .await
        .expect("failed to generate random data");
    println!("{:?}", data);
    assert_eq!(data.plaintext.expect("should have data").as_ref().len(), 64);
}
// snippet-end:[kms.rust.kms-helloworld]
