/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::net::TcpListener;

use aws_smithy_http::endpoint::Endpoint;
use once_cell::sync::Lazy;
use rest_ses::{
    client::{RdsClient, SesClient},
    configuration::{get_settings, init_environment, Environment},
    telemetry::{get_subscriber, init_subscriber},
};
use wiremock::MockServer;

// Ensure that the `tracing` stack is only initialized once using `once_cell`.
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

/// Spawn the app against a MockServer resolved backend.
pub async fn spawn_app_mocked() -> (String, MockServer) {
    let mock_server = MockServer::builder().start().await;
    let config_loader = aws_config::from_env().endpoint_resolver(
        Endpoint::immutable(mock_server.uri())
            .expect("MockServer gave an invalid address, file a bug"),
    );
    let (app, _) = prep_app(config_loader).await;
    (app, mock_server)
}

/// Spawn the app using production AWS credentials.
pub async fn spawn_app() -> (String, RdsClient) {
    prep_app(aws_config::from_env()).await
}

/// Prepare the application for testing.
/// This is similar to, but not quite the same as, main in main.rs.
async fn prep_app(config: aws_config::ConfigLoader) -> (String, RdsClient) {
    let environment = Lazy::force(&TRACING);
    let settings = get_settings(environment).expect("failed to read configuration");

    let config = config.load().await;

    let rds = RdsClient::new(&settings.rds, &config);
    let ses = SesClient::new(&settings.ses, &config);
    let listener =
        TcpListener::bind("127.0.0.1:0").expect("Failed to bind to unused port for testing");
    let port = listener.local_addr().unwrap().port();
    let server =
        rest_ses::startup::run(listener, rds.clone(), ses).expect("Failed to initalize server!");
    let _ = tokio::spawn(server);
    let address = format!("http://127.0.0.1:{port}");
    (address, rds)
}
