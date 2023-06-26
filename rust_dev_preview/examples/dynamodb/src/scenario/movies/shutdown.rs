use aws_sdk_dynamodb::{Client, Error};
use tokio::signal;
use tracing::info;

use super::TABLE_NAME;

// Deletes a table.
// snippet-start:[dynamodb.rust.movies-delete_table]
pub async fn remove_table(client: &Client) -> Result<(), Error> {
    info!("Removing table {TABLE_NAME}");
    client.delete_table().table_name(TABLE_NAME).send().await?;
    Ok(())
}
// snippet-end:[dynamodb.rust.movies-delete_table]

pub async fn shutdown_signal(client: &Client) {
    let ctrl_c = async {
        signal::ctrl_c()
            .await
            .expect("failed to install Ctrl+C handler");
    };

    let terminate = async {
        signal::unix::signal(signal::unix::SignalKind::terminate())
            .expect("failed to install signal handler")
            .recv()
            .await;
    };

    tokio::select! {
        _ = ctrl_c => {},
        _ = terminate => {},
    }

    info!("signal received, starting graceful shutdown");
    remove_table(client)
        .await
        .expect("failed to clean up table");
}
