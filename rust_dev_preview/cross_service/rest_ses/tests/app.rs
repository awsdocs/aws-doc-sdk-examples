use std::net::TcpListener;

use once_cell::sync::Lazy;
use rest_ses::{
    client::{param, RdsClient},
    configuration::{get_settings, init_environment, Environment},
    telemetry::{get_subscriber, init_subscriber},
    work_item::WorkItem,
};

// Ensure that the `tracing` stack is only initialised once using `once_cell`
static TRACING: Lazy<Environment> = Lazy::new(|| {
    let environment = init_environment().expect("Failed to initialize test environment");
    let default_filter_level = "info".to_string();
    let subscriber_name = "test".to_string();
    if std::env::var("TEST_LOG").is_ok() {
        let subscriber = get_subscriber(subscriber_name, default_filter_level, std::io::stdout);
        init_subscriber(subscriber);
    };
    environment
});

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
        .body(r#"{"name":"david","guide":"Rust","description":"A work item"}"#)
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
        .parameters(param("idwork", work_item.idwork().to_string()))
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

#[tokio::test]
async fn get_workitem_returns_200() {
    let (app, _) = spawn_app().await;

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json")
        .body(r#"{"name":"david","guide":"Rust","description":"A work item"}"#)
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

async fn spawn_app() -> (String, RdsClient) {
    let environment = Lazy::force(&TRACING);
    let settings = get_settings(environment).expect("failed to read configuration");
    let config = aws_config::from_env().load().await;
    let rds = RdsClient::new(&settings.sdk_config, &config);
    let listener =
        TcpListener::bind("127.0.0.1:0").expect("Failed to bind to unused port for testing");
    let port = listener.local_addr().unwrap().port();
    let server =
        rest_ses::startup::run(listener, rds.clone()).expect("Failed to initalize server!");
    let _ = tokio::spawn(server);
    let address = format!("http://127.0.0.1:{port}");
    return (address, rds);
}
