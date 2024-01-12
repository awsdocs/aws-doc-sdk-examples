// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
The increment Lambda handler is straightforward:

1. It accepts a number, increments it, and returns the new value.
2. It performs simple logging of the result.
 */
use lambda_runtime::{service_fn, Error, LambdaEvent};
use serde_json::Value;
use tracing::info;
use tracing_subscriber::EnvFilter;

#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt()
        .json()
        .with_env_filter(EnvFilter::from_default_env())
        .init();
    let func = service_fn(increment_handler);
    lambda_runtime::run(func).await
}

async fn increment_handler(event: LambdaEvent<Value>) -> Result<Value, Error> {
    let number = event.payload.as_i64().unwrap_or(0);
    let incremented_number = number + 1;

    info!("Incremented number: {}", incremented_number);

    Ok(Value::from(incremented_number))
}
