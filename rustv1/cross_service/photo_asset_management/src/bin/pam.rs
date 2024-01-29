// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use photo_asset_management::{
    common::{init_tracing_subscriber, Common},
    handlers::{detect_labels, download, hello, labels, upload},
};
use tracing::log::info;

#[tokio::main]
async fn main() -> Result<(), lambda_runtime::Error> {
    init_tracing_subscriber();

    info!("tracing_subscriber initialized");

    let common = Common::load_from_env().await;

    let handler = std::env::var("_HANDLER").expect("_HANDLER provided");

    info!("Using handler {handler}");

    match handler.as_str() {
        "detect_labels" => {
            lambda_runtime::run(lambda_runtime::service_fn(|request| async {
                detect_labels::handler(&common, request).await
            }))
            .await
        }
        "download" => {
            lambda_runtime::run(lambda_runtime::service_fn(|request| async {
                download::handler(&common, request).await
            }))
            .await
        }
        "labels" => {
            lambda_runtime::run(lambda_runtime::service_fn(|request| async {
                labels::handler(&common, request).await
            }))
            .await
        }
        "hello" => {
            lambda_runtime::run(lambda_runtime::service_fn(|request| async {
                hello::handler(&common, request).await
            }))
            .await
        }
        "upload" => {
            lambda_runtime::run(lambda_runtime::service_fn(|request| async {
                upload::handler(&common, request).await
            }))
            .await
        }
        s => panic!("Missing handler for {s}"),
    }?;

    Ok(())
}
