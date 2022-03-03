/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/*
Purpose

Shows how to use rustls, hyper-rustls, and webpki-roots to set up a connection using forcing a
minimum version of TLS 1.3.
A sample call is then made using TLS 1.3 to AWS Key Management System (KMS) for a proof of concept.

This example assumes you have set up environment variables for authentication.

*/


#[tokio::main]
async fn main() {
    tls::connect_via_tls_13().await.expect("Could not connect via tls 1.3");
}
