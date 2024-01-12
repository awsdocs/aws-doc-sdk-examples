// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use photo_asset_management::{
    common::{init_tracing_subscriber, Common},
    handlers::download,
};

#[tokio::main]
async fn main() -> Result<(), lambda_runtime::Error> {
    eprintln!("Inside download::main");

    init_tracing_subscriber();

    let common = Common::load_from_env().await;

    lambda_runtime::run(lambda_runtime::service_fn(|request| async {
        eprintln!("Inside download::main::run");
        download::handler(&common, request).await
    }))
    .await
}
