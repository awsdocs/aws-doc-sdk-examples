/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/// Lists your AWS Elemental MediaPackage endpoint URLs.
#[tokio::main]
async fn main() -> Result<(), mediapackage::Error> {
    let client = mediapackage::Client::from_env();
    let or_endpoints = client.list_origin_endpoints().send().await?;

    for e in or_endpoints.origin_endpoints.unwrap_or_default() {
        let endpoint_url = e.url.as_deref().unwrap_or_default();
        let endpoint_description = e.description.as_deref().unwrap_or_default();
        println!(
            "Endpoint Description: {}, Endpoint URL : {}",
            endpoint_description, endpoint_url
        );
    }

    Ok(())
}
