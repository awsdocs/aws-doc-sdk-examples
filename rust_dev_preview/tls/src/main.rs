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

#[tokio::main]
async fn main() {
    tls::connect_via_tls_13()
        .await
        .expect("Could not connect via TLS 1.3");
}
