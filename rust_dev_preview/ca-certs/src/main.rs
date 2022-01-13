/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::provider_config::ProviderConfig;
use aws_smithy_client::hyper_ext;
use rustls::RootCertStore;

#[tokio::main]
async fn main() {
    // insert your root CAs
    let root_store = RootCertStore::empty();
    let config = rustls::ClientConfig::builder()
        .with_safe_defaults()
        .with_root_certificates(root_store)
        .with_no_client_auth();
    let rustls_connector = hyper_rustls::HttpsConnectorBuilder::new()
        .with_tls_config(config)
        .https_only()
        .enable_http1()
        .enable_http2()
        .build();

    // Currently, aws_config connectors are buildable directly from something that implements `hyper::Connect`.
    // This enables different providers to construct clients with different timeouts.
    let provider_config = ProviderConfig::default().with_tcp_connector(rustls_connector.clone());
    let shared_conf = aws_config::from_env()
        .configure(provider_config)
        .load()
        .await;
    let s3_config = aws_sdk_s3::Config::from(&shared_conf);
    // however, for generated clients, they are constructred from a Hyper adapter directly:
    let s3_client = aws_sdk_s3::Client::from_conf_conn(
        s3_config,
        hyper_ext::Adapter::builder().build(rustls_connector),
    );
    let _ = s3_client.list_buckets().send().await;
}
