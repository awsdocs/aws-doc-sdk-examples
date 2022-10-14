use std::net::TcpListener;

use rest_ses::client::RdsClient;
use rest_ses::configuration::get_settings;
use rest_ses::startup::run;

#[tokio::main]
async fn main() -> std::io::Result<()> {
    // App Settings come from configuration.yaml
    let settings = get_settings().expect("Failed to load configuration.");
    // AWS Settings (region & role) come from the environment
    let config = aws_config::from_env().load().await;
    let rds = RdsClient::new(settings.sdk_config, config);

    let address = format!("127.0.0.1:{}", settings.application_port);
    let listener = TcpListener::bind(address).expect("Failed to bind a TcpListener!");

    println!("Listening on {addr}", addr = listener.local_addr()?);

    run(listener, rds)?.await
}
