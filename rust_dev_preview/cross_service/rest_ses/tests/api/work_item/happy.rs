/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

//! End-to-end tests for the Rest SES happy-path API.
//! This uses real AWS environments and real AWS services.
//! It will result in billing for an AWS account!
//! All tests are ignored by default.
use std::collections::HashSet;

use reqwest::StatusCode;
use rest_ses::{
    params,
    work_item::{WorkItem, WorkItemArchived},
};
use uuid::Uuid;

use crate::{
    startup::spawn_app,
    work_item::{fake_description, fake_guide, fake_name},
};

async fn create_test_item(client: &reqwest::Client, app: &str) -> Uuid {
    let name = fake_name();
    let guide = fake_guide();
    let description = fake_description();

    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json")
        .body(format!(
            r#"{{"name":"{name}","guide":"{guide}","description":"{description}"}}"#
        ))
        .send()
        .await
        .expect("Request failed");

    let status = response.status();
    let body = response.text().await.expect("response missing a body");

    assert!(
        status.is_success(),
        "Response did not return with success: {body}"
    );

    let work_item: WorkItem =
        serde_json::from_str(body.as_str()).expect("failed to parse response body");

    work_item.idwork().to_owned()
}

async fn get_items(
    client: &reqwest::Client,
    app: &str,
    archived: WorkItemArchived,
) -> HashSet<Uuid> {
    let query: Option<bool> = match archived {
        WorkItemArchived::All => None,
        WorkItemArchived::Active => Some(false),
        WorkItemArchived::Archived => Some(true),
    };

    let mut request = client.get(format!("{app}/api/items/"));
    if let Some(query) = query {
        request = request.query(&[("archived", query)])
    }
    let response = request.send().await.expect("Could not get all items");

    assert_eq!(response.status(), StatusCode::OK);

    let body = response.text().await.expect("response missing body");

    let items: Vec<WorkItem> =
        serde_json::from_str(body.as_str()).expect("Failed to parse all_items_response");

    let item_ids: HashSet<Uuid> = items.iter().map(|item| item.idwork().to_owned()).collect();

    item_ids
}

/// Send JSON to the create endpoint, expect 200 back, and check the database for that item.
#[ignore]
#[tokio::test]
async fn post_workitem_returns_200() {
    let (app, rds) = spawn_app().await;

    let client = reqwest::Client::new();

    let id = create_test_item(&client, app.as_str()).await;

    let result = rds
        .execute_statement()
        .sql("SELECT idwork FROM Work WHERE idwork = :idwork;")
        .set_parameters(params![("idwork", id)])
        .send()
        .await
        .expect("failed to query rds");

    assert_eq!(
        result.records().unwrap().len(),
        1,
        "should have inserted one record"
    );
}

/// Create a single item, and assert we can get it back from the list endpoint.
#[ignore]
#[tokio::test]
async fn get_workitem_returns_200() {
    let (app, _) = spawn_app().await;

    let client = reqwest::Client::new();
    let id = create_test_item(&client, app.as_str()).await;

    let response = client
        .get(format!("{app}/api/items/{id}",))
        .send()
        .await
        .expect("Failed to look up created item");

    let status = response.status();
    let body = response.text().await.expect("response missing a body");

    assert!(
        status.is_success(),
        "Response did not return with success: {status} {body}"
    );
}

/// Look for an item that shouldn't exist, check for 404.
#[ignore]
#[tokio::test]
async fn get_workitem_returns_404() {
    let (app, _) = spawn_app().await;

    let id = Uuid::new_v4().to_string();

    let client = reqwest::Client::new();
    let response = client
        .get(format!("{app}/api/items/{id}",))
        .send()
        .await
        .expect("Failed to look up created item");

    let status = response.status();
    let body = response.text().await.expect("response missing a body");

    assert_eq!(
        status,
        StatusCode::NOT_FOUND,
        "Response returned as not missing: {status} {body}"
    );
}

/// Create two items. Archive one. Assert both are returned for all, and each in their respective status requests.
#[ignore]
#[tokio::test]
async fn archive_and_retrieve_by_status() {
    let (app, _) = spawn_app().await;

    let client = reqwest::Client::new();

    // Create first item
    let id_a = create_test_item(&client, app.as_str()).await;
    let id_b = create_test_item(&client, app.as_str()).await;

    // Archive id_b
    client
        .post(format!("{app}/api/items/{id_b}:archive"))
        .send()
        .await
        .expect("Failed to archive item");

    // Requst all
    let all_item_ids = get_items(&client, app.as_str(), WorkItemArchived::All).await;
    let active_item_ids = get_items(&client, app.as_str(), WorkItemArchived::Active).await;
    let archived_item_ids = get_items(&client, app.as_str(), WorkItemArchived::Archived).await;

    assert!(all_item_ids.contains(&id_a));
    assert!(all_item_ids.contains(&id_b));
    assert!(active_item_ids.contains(&id_a));
    assert!(archived_item_ids.contains(&id_b));
}
