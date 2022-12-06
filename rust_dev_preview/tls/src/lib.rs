/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/*
Purpose

Shows how to use rustls, hyper-rustls, and webpki-roots to set the minimum TLS version to 1.3 for
outgoing connections.
Then, uses TLS 1.3 to make a sample call to AWS Key Management Service (AWS KMS) as proof of concept.

This example assumes you have set up environment variables for authentication.

*/

use aws_config::provider_config::ProviderConfig;
use aws_sdk_kms::Error;
use aws_smithy_client::hyper_ext;
use rustls::RootCertStore;

// snippet-start:[rust.example_code.tls.ConnectWithTLS13]
pub async fn connect_via_tls_13() -> Result<(), Error> {
    println!("Attempting to connect to KMS using TLS 1.3: ");

    // Let webpki load the Mozilla root certificates.
    let mut root_store = RootCertStore::empty();
    root_store.add_server_trust_anchors(webpki_roots::TLS_SERVER_ROOTS.0.iter().map(|ta| {
        rustls::OwnedTrustAnchor::from_subject_spki_name_constraints(
            ta.subject,
            ta.spki,
            ta.name_constraints,
        )
    }));

    // The .with_protocol_versions call is where we set TLS1.3. You can add rustls::version::TLS12 or replace them both with rustls::ALL_VERSIONS
    let config = rustls::ClientConfig::builder()
        .with_safe_default_cipher_suites()
        .with_safe_default_kx_groups()
        .with_protocol_versions(&[&rustls::version::TLS13])
        .expect("It looks like your system doesn't support TLS1.3")
        .with_root_certificates(root_store)
        .with_no_client_auth();

    // Finish setup of the rustls connector.
    let rustls_connector = hyper_rustls::HttpsConnectorBuilder::new()
        .with_tls_config(config)
        .https_only()
        .enable_http1()
        .enable_http2()
        .build();

    let provider_config = ProviderConfig::default().with_tcp_connector(rustls_connector.clone());
    let shared_conf = aws_config::from_env()
        .configure(provider_config)
        .http_connector(hyper_ext::Adapter::builder().build(rustls_connector))
        .load()
        .await;

    let kms_client = aws_sdk_kms::Client::new(&shared_conf);
    let response = kms_client.list_keys().send().await?;

    println!("{:?}", response);

    Ok(())
}
// snippet-end:[rust.example_code.tls.ConnectWithTLS13]
