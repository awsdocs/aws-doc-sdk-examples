/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::provider_config::ProviderConfig;
use aws_smithy_client::erase::DynConnector;
use aws_smithy_client::http_connector::HttpConnector;
use aws_smithy_client::hyper_ext;
use rustls::RootCertStore;
use std::sync::Arc;

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
    let sdk_config = aws_config::from_env()
        .configure(provider_config)
        .http_connector(HttpConnector::ConnectorFn(Arc::new(
            move |connector_settings, sleep_impl| {
                let mut builder =
                    hyper_ext::Adapter::builder().connector_settings(connector_settings.clone());
                builder.set_sleep_impl(sleep_impl);
                let conn = builder.build(rustls_connector.clone());

                Some(DynConnector::new(conn))
            },
        )))
        .load()
        .await;
    // however, for generated clients, they are constructed from a Hyper adapter directly:
    let s3_client = aws_sdk_s3::Client::new(&sdk_config);

    match s3_client.list_buckets().send().await {
        Ok(res) => {
            println!("Your buckets: {:?}", res.buckets().unwrap_or_default())
        }
        Err(err) => {
            println!("an error occurred when trying to list buckets: {}", err)
        }
    }
}
