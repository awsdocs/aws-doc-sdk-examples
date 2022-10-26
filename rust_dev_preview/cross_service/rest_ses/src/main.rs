//! Main that loads environments & prepares clients, and hands them to `startup`.
use std::net::TcpListener;

use rest_ses::client::{RdsClient, SesClient};
use rest_ses::configuration::{get_settings, init_environment};
use rest_ses::startup::run;
use rest_ses::telemetry::{get_subscriber, init_subscriber};
use tracing::{debug, info};

/// A tokio main for our app!
/// It loads the environment & settings, prepares subscribers, AWS Clients, and the Actix server.
#[tokio::main]
async fn main() -> std::io::Result<()> {
    // App Settings come from configuration.yaml
    let environment = init_environment().expect("Failed to initialize environment.");
    let settings = get_settings(&environment).expect("Failed to load configuration.");

    let subscriber = get_subscriber(
        settings.application.name.clone(),
        settings.log_level.clone(),
        std::io::stdout,
    );
    init_subscriber(subscriber);

    // AWS Settings (region & role) come from the environment
    let config = aws_config::from_env().load().await;
    let rds = RdsClient::new(&settings.rds, &config);
    let ses = SesClient::new(&settings.ses, &config);

    let listener = TcpListener::bind(settings.address()).expect("Failed to bind a TcpListener!");
    debug!(?settings, "App configured");
    info!("\nListening on {addr}\n", addr = listener.local_addr()?);

    run(listener, rds, ses)?.await
}
