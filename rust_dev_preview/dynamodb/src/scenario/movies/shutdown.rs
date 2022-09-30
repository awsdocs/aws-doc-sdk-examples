use aws_sdk_dynamodb::{Client, Error};

// Deletes a table.
// snippet-start:[dynamodb.rust.movies-delete_table]
pub async fn remove_table(client: &Client, table_name: &str) -> Result<(), Error> {
    client.delete_table().table_name(table_name).send().await?;
    Ok(())
}
// snippet-end:[dynamodb.rust.movies-delete_table]
