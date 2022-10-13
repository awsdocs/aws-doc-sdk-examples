use std::net::TcpListener;

#[tokio::test]
async fn healthz_works() {
    let app = spawn_app();

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
    let app = spawn_app();

    let client = reqwest::Client::new();
    let response = client
        .post(format!("{app}/api/items"))
        .header("content-type", "application/json")
        .body(r#"{"username":"david","guide":"Rust","description":"A work item"}"#)
        .send()
        .await
        .expect("Request failed");

    eprintln!("{response:?}");

    assert!(
        response.status().is_success(),
        "Response did not return with success"
    );
}

#[tokio::test]
async fn post_workitem_returns_400_with_missing_username() {
    let app = spawn_app();

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

fn spawn_app() -> String {
    let listener =
        TcpListener::bind("127.0.0.1:0").expect("Failed to bind to unused port for testing");
    let port = listener.local_addr().unwrap().port();
    let server = rest_ses::run(listener).expect("Failed to initalize server!");
    let _ = tokio::spawn(server);
    format!("http://127.0.0.1:{port}")
}
