use std::net::TcpListener;

use rest_ses::{
    client::{param, RdsClient},
    configuration::get_settings,
    work_item::WorkItem,
};

#[tokio::test]
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

#[tokio::test]
async fn post_workitem_returns_200() {
    let (app, rds) = spawn_app().await;

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json")
        .body(r#"{"username":"david","guide":"Rust","description":"A work item"}"#)
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
        .execute_statement("SELECT idwork FROM Work WHERE idwork = :idwork;")
        .parameters(param("idwork", work_item.idwork.to_string()))
        .send()
        .await
        .expect("failed to query rds");

    assert_eq!(
        result.records().unwrap().len(),
        1,
        "should have inserted one record"
    );
}

#[tokio::test]
async fn post_workitem_returns_400_with_missing_username() {
    let (app, _) = spawn_app().await;

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json; charset=utf-8")
        .body(r#"{"guide":"Rust","description":"A work item"}"#)
        .send()
        .await
        .expect("Request failed");

    assert!(
        response.status().is_client_error(),
        "Response should not accept this body"
    );
}

async fn spawn_app() -> (String, RdsClient) {
    let settings = get_settings().expect("failed to read configuration");
    let config = aws_config::from_env().load().await;
    let rds = RdsClient::new(settings.sdk_config, config);
    let listener =
        TcpListener::bind("127.0.0.1:0").expect("Failed to bind to unused port for testing");
    let port = listener.local_addr().unwrap().port();
    let server =
        rest_ses::startup::run(listener, rds.clone()).expect("Failed to initalize server!");
    let _ = tokio::spawn(server);
    let address = format!("http://127.0.0.1:{port}");
    return (address, rds);
}
