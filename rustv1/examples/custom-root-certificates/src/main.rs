// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use aws_config::BehaviorVersion;
use aws_smithy_runtime::client::http::hyper_014::HyperClientBuilder;
use rustls::RootCertStore;

#[tokio::main]
async fn main() {
    // insert your root CAs
    let root_store = RootCertStore::empty();
    let config = rustls::ClientConfig::builder()
        .with_safe_defaults()
        .with_root_certificates(root_store)
        .with_no_client_auth();
    let tls_connector = hyper_rustls::HttpsConnectorBuilder::new()
        .with_tls_config(config)
        .https_only()
        .enable_http1()
        .enable_http2()
        .build();

    let hyper_client = HyperClientBuilder::new().build(tls_connector);
    let sdk_config = aws_config::defaults(BehaviorVersion::latest())
        .http_client(hyper_client)
        .load()
        .await;

    // however, for generated clients, they are constructed from a Hyper adapter directly:
    let s3_client = aws_sdk_s3::Client::new(&sdk_config);

    match s3_client.list_buckets().send().await {
        Ok(res) => {
            println!("Your buckets: {:?}", res.buckets())
        }
        Err(err) => {
            println!("an error occurred when trying to list buckets: {}", err)
        }
    }
}
