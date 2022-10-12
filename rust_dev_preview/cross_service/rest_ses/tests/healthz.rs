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

fn spawn_app() -> String {
    let listener =
        TcpListener::bind("127.0.0.1:0").expect("Failed to bind to unused port for testing");
    let port = listener.local_addr().unwrap().port();
    let server = rest_ses::run(listener).expect("Failed to initalize server!");
    let _ = tokio::spawn(server);
    format!("http://127.0.0.1:{port}")
}
