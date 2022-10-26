//! The repository module includes data access components. It exposes Create,
//! Retrieve, List, Update, and Delete functions that work with an `&RdsClient`
//! to execute SQL that manages WorkItem persitence. Its interface encapsulates
//! RDS and data parsing errors behind the WorkItemError enum defined in the
//! parent work_item mod.
use aws_sdk_rdsdata::{
    error::ExecuteStatementError, model::RecordsFormatType, output::ExecuteStatementOutput,
    types::SdkError,
};
use serde_json::from_str;

use super::{WorkItem, WorkItemArchived, WorkItemError};
use crate::{client::RdsClient, params};

pub const RDS_DATE_FORMAT: &str = "%Y-%m-%d";

/// Create a new WorkItem using `INSERT INTO`.
/// After inserting the item, ensures its success by retrieving that item rather than the stored copy.
#[tracing::instrument(name = "Repository Create new WorkItem", skip(item, client))]
pub async fn create(item: WorkItem, client: &RdsClient) -> Result<WorkItem, WorkItemError> {
    client
        .execute_statement()
        .sql(
            r#"
            INSERT INTO Work
            (idwork, username, date, description, guide, status, archive)
            VALUES
            (:idwork, :username, :date, :description, :guide, :status, :archive)
        "#,
        )
        .set_parameters(params![
            ("idwork", item.idwork),
            ("username", item.name),
            ("date", format!("{}", item.date.format(RDS_DATE_FORMAT))),
            ("description", item.description),
            ("guide", item.guide),
            ("status", item.status),
            (
                "archive",
                format!("{}", u8::from(&WorkItemArchived::Active))
            )
        ])
        .send()
        .await
        .map_err(|err| {
            tracing::error!("Failed to insert user: {err:?}");
            WorkItemError::RDSError(err.into())
        })?;

    retrieve(item.idwork().to_string(), client).await
}

// Retrieve a single record, by ID.
#[tracing::instrument(name = "Repository Retrieve single WorkItem", skip(client))]
pub async fn retrieve(id: String, client: &RdsClient) -> Result<WorkItem, WorkItemError> {
    let statement = client
        .execute_statement()
        .sql(
            r#"SELECT
                idwork, username, date, description, guide, status, archive
            FROM Work
            WHERE idwork = :idwork;"#,
        )
        .set_parameters(params![("idwork", id)])
        .format_records_as(RecordsFormatType::Json)
        .send()
        .await;

    let items = parse_rds_output(statement)?;

    if items.len() > 1 {
        // Warn on too many records
        tracing::warn!("Received multiple results for id: {id}");
    }

    // Are there enough records?
    if items.len() == 0 {
        return Err(WorkItemError::MissingItem(id.clone()));
    }

    // Last chance for something to go wrong!
    let item = match items.get(0) {
        Some(item) => Ok(item),
        None => Err(WorkItemError::Other(
            "Somehow len() == 1 but get(0) is None".to_string(),
        )),
    }?;

    Ok(item.to_owned())
}

/// Retrieve a list of all records with a given WorkItemArchived state.
#[tracing::instrument(name = "Repository List all WorkItems", skip(client))]
pub async fn list(
    archive: WorkItemArchived,
    client: &RdsClient,
) -> Result<Vec<WorkItem>, WorkItemError> {
    let statement = client
        .execute_statement()
        .sql(
            r#"SELECT
                idwork, username, date, description, guide, status, archive
            FROM Work
            WHERE archive = :archive
            ;"#,
        )
        .set_parameters(params![("archive", format!("{}", u8::from(&archive)))])
        .format_records_as(RecordsFormatType::Json)
        .send()
        .await;

    parse_rds_output(statement)
}

/// Update a single item in the database, by ID.
/// Retrieves the value after update for its result.
#[tracing::instrument(name = "Repository Update WorkItem", skip(client))]
pub async fn update(item: &WorkItem, client: &RdsClient) -> Result<WorkItem, WorkItemError> {
    client
        .execute_statement()
        .sql(
            r#"
            UPDATE Work
            SET 
                username = :username,
                date = :date,
                description = :description,
                guide = :guide,
                status = :status,
                archive = :archive
            WEHRE idwork = :idwork;
        "#,
        )
        .set_parameters(params![
            ("idwork", item.idwork),
            ("username", item.name),
            ("date", format!("{}", item.date.format(RDS_DATE_FORMAT))),
            ("description", item.description),
            ("guide", item.guide),
            ("status", item.status),
            ("archive", format!("{}", u8::from(&item.archive)))
        ])
        .send()
        .await
        .map_err(|err| {
            tracing::error!("Failed to update user: {id} {err:?}", id = item.idwork);
            WorkItemError::RDSError(err.into())
        })?;
    retrieve(item.idwork().to_string(), client).await
}

/// Delete an item from the database, returning () on success.
#[tracing::instrument(name = "Repository Delete WorkItem", skip(client))]
pub async fn delete(id: String, client: &RdsClient) -> Result<(), WorkItemError> {
    client
        .execute_statement()
        .sql(r#"DELETE FROM Work WHERE idwork = :idwork"#)
        .set_parameters(params![("idwork", id)])
        .send()
        .await
        .map(|_| Ok(()))
        .map_err(|err| {
            tracing::error!("Failed to delete user: {id} {err:?}");
            WorkItemError::RDSError(err.into())
        })?
}

/// Attempt to parse an RDS Data SQL statement to a WorkItem.
/// This relies on formatting the SQL statement response in a way that matches serde annotations in WorkItem.
fn parse_rds_output(
    statement: Result<ExecuteStatementOutput, SdkError<ExecuteStatementError>>,
) -> Result<Vec<WorkItem>, WorkItemError> {
    // Did the request succeed? It could fail because the SQL is incorrect or the IAM permissions
    // are wrong, because of a failing network connection, or because of a problem in AWS itself.
    let data = match statement {
        Ok(data) => Ok(data),
        Err(err) => Err(WorkItemError::RDSError(err.into())),
    }?;

    // Are there records? Because the request specified `format_records_as(RecordFormatType::Json)`,
    // this field should be pressend. If it's not, RDS did somethign weird, so it gets returned
    // as an RDSError.
    let records = match data.formatted_records() {
        Some(records) => Ok(records),
        None => Err(WorkItemError::Other(
            "RDS Data did not include formattedRecords in their response.".to_string(),
        )),
    }?;

    // Can we parse the records? The formatted records have been mapped to a JSON array where each
    // row is one object, with keys named the same as the SELECT AS statements. Serde will pare the
    // formattedResults string as JSON, into an array with (hopefully) a single item.
    //
    // It is incumbent on the developer to match up the serde annotations with SELECT statement.
    match from_str::<Vec<WorkItem>>(records) {
        Ok(items) => Ok(items),
        Err(e) => Err(WorkItemError::FromFields(
            format!("Failed to parse formatted records: {e}").to_string(),
        )),
    }
}
