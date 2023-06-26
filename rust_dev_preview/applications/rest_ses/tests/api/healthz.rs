/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use crate::startup::spawn_app;

/// Verify that the healthz route works.
/// This is the "hello, world" of the test suite!
#[tokio::test]
#[ignore]
async fn healthz_works() {
    let (app, _) = spawn_app().await;

    let client = reqwest::Client::new();
    let response = client
        .get(format!("{app}/healthz"))
        .send()
        .await
        .expect("Request failed");

    assert!(
        response.status().is_success(),
        "Response did not return with success"
    );
    assert_eq!(response.content_length(), Some(3));
}
