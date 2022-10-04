/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_kms::{Client, Region};

// snippet-start:[kms.rust.kms-helloworld]
/// Creates a random byte string that is cryptographically secure in __us-east-1__.
#[tokio::main]
async fn main() -> Result<(), aws_sdk_kms::Error> {
    let config = aws_config::from_env()
        // region can also be loaded from AWS_DEFAULT_REGION, just remove this line.
        .region(Region::new("us-east-1"))
        .load()
        .await;
    let client = Client::new(&config);

    let response = client.generate_random().number_of_bytes(64).send().await?;
    println!("{:?}", response);
    assert_eq!(
        64,
        response
            .plaintext()
            .expect("should have data")
            .as_ref()
            .len()
    );
    Ok(())
}
// snippet-end:[kms.rust.kms-helloworld]
