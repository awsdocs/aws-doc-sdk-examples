//! End-to-end tests for the Rest SES happy-path API.
//! This uses real aws environments and real aws services.
//! It will result in billing for an AWS account!
//! All tests are ignored by default.
use reqwest::StatusCode;
use rest_ses::{params, work_item::WorkItem};
use uuid::Uuid;

use crate::{
    startup::spawn_app,
    work_item::{fake_description, fake_guide, fake_name},
};

/// Send JSON to the create endpoint, expect 200 back, and check the database for that item.
#[ignore]
#[tokio::test]
async fn post_workitem_returns_200() {
    let (app, rds) = spawn_app().await;

    let name = fake_name();
    let guide = fake_guide();
    let description = fake_description();

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json")
        .body(format!(
            r#"{{"name":"{name}","guide":"{guide}","description":"{description}"}}"#
        ))
        .send()
        .await
        .expect("Request failed");

    eprintln!("{response:?}");

    let status = response.status();
    let body = response.text().await.expect("response missing a body");

    assert!(
        status.is_success(),
        "Response did not return with success: {body}"
    );

    let work_item: WorkItem =
        serde_json::from_str(&body.as_str()).expect("failed to parse response body");

    let result = rds
        .execute_statement()
        .sql("SELECT idwork FROM Work WHERE idwork = :idwork;")
        .set_parameters(params![("idwork", work_item.idwork())])
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

    let name = fake_name();
    let guide = fake_guide();
    let description = fake_description();

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json")
        .body(format!(
            r#"{{"name":"{name}","guide":"{guide}","description":"{description}"}}"#
        ))
        .send()
        .await
        .expect("Request failed");

    let body = response.text().await.expect("response missing a body");
    let work_item: WorkItem =
        serde_json::from_str(body.as_str()).expect("failed to parse response body");
    let id = work_item.idwork();

    eprintln!("\n\nLooking for {id}\n\n");

    let response = client
        .get(format!("{app}/api/items/{id}",))
        .send()
        .await
        .expect("Failed to look up created item");

    let status = response.status();
    let body = response.text().await.expect("response missing a body");

    eprintln!("{}", body);

    assert!(
        status.is_success(),
        "Response did not return with success: {status} {body}"
    );
}

/// Look for an item that shouldn't exist, check for 404
#[ignore]
#[tokio::test]
async fn get_workitem_returns_404() {
    let (app, _) = spawn_app().await;

    let id = Uuid::new_v4().to_string();
    eprintln!("\n\nLooking for {id}\n\n");

    let client = reqwest::Client::new();
    let response = client
        .get(format!("{app}/api/items/{id}",))
        .send()
        .await
        .expect("Failed to look up created item");

    let status = response.status();
    let body = response.text().await.expect("response missing a body");

    eprintln!("{}", body);

    assert_eq!(
        status,
        StatusCode::NOT_FOUND,
        "Response returned as not missing: {status} {body}"
    );
}
