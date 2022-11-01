/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

//! End-to-end tests for the Rest WorkItem app with mocked AWS resources.
//! This uses MockServer to hand-craft replies, and is run in normal tests.
//! It additionally tests many error cases, by having the mocked AWS resources respond with errors.
use wiremock::{matchers::any, Mock, ResponseTemplate};

use crate::{
    startup::spawn_app_mocked,
    work_item::{fake_description, fake_guide},
};

/// Send JSON to the create endpoint, mock a failing call to RDS, and expect a 500
#[tokio::test]
async fn post_workitem_returns_500_when_rds_fails() {
    let (app, mock_server) = spawn_app_mocked().await;

    Mock::given(any())
        .respond_with(ResponseTemplate::new(500))
        .mount(&mock_server)
        .await;

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json")
        .body(r#"{"name":"david","guide":"Rust","description":"A work item"}"#)
        .send()
        .await
        .expect("Request failed");

    eprintln!("{response:?}");

    let status = response.status();
    let body = response.text().await.expect("response missing a body");

    assert!(
        status.is_server_error(),
        "Expected the server to fail: {body}"
    );
}

#[tokio::test]
async fn post_workitem_returns_400_with_missing_username() {
    let (app, _) = spawn_app_mocked().await;

    let guide = fake_guide();
    let description = fake_description();

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json; charset=utf-8")
        .body(format!(
            r#"{{"guide":"{guide}","description":"{description}"}}"#
        ))
        .send()
        .await
        .expect("Request failed");

    assert!(
        response.status().is_client_error(),
        "Response should not accept this body"
    );
}
