use actix_web::{http::StatusCode, HttpResponse, ResponseError};
use chrono::NaiveDate;
use serde::{
    de::{self, Visitor},
    Deserialize, Serialize,
};
use serde_json::json;
use thiserror::Error;
use tracing::warn;
use uuid::Uuid;

#[derive(Clone, Debug, Default, Deserialize, Serialize)]
pub struct WorkItem {
    #[serde(default = "uuid::Uuid::new_v4")]
    idwork: Uuid,
    #[serde(default)]
    date: NaiveDate,
    #[serde(alias = "username")]
    name: String,
    #[serde(default)]
    description: String,
    #[serde(default)]
    guide: String,
    #[serde(default)]
    status: String,
    #[serde(default)]
    archive: WorkItemArchived,
}

impl WorkItem {
    pub fn idwork(&self) -> &Uuid {
        &self.idwork
    }
}

#[derive(Clone, Debug, Default, Deserialize, Serialize)]
pub struct WorkItemStatus {
    archived: WorkItemArchived,
}

#[derive(Clone, Debug, Default, Serialize)]
#[serde(untagged)]
pub enum WorkItemArchived {
    #[default]
    Active,
    Archived,
}
struct WorkItemArchivedVisitor;
impl<'de> Visitor<'de> for WorkItemArchivedVisitor {
    type Value = WorkItemArchived;

    fn visit_u64<E>(self, value: u64) -> Result<Self::Value, E>
    where
        E: de::Error,
    {
        warn!("\n\nVisiting u64 {value}\n\n");
        if value == 0 {
            Ok(WorkItemArchived::Active)
        } else if value == 1 {
            Ok(WorkItemArchived::Archived)
        } else {
            Err(E::custom(format!("Unrecognized archive number {value}")))
        }
    }

    fn visit_str<E>(self, v: &str) -> Result<Self::Value, E>
    where
        E: de::Error,
    {
        warn!("\n\nVisiting str {v}\n\n");
        if matches!(v, "active") {
            Ok(WorkItemArchived::Active)
        } else if matches!(v, "archived") {
            Ok(WorkItemArchived::Archived)
        } else {
            Err(E::custom(format!("Unrecognized archive string {v}")))
        }
    }

    fn expecting(&self, formatter: &mut std::fmt::Formatter) -> std::fmt::Result {
        formatter.write_str("Integer 0 or 1, or string 'active' or 'archive'")
    }
}

impl<'de> Deserialize<'de> for WorkItemArchived {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        warn!("\n\nDeserializing WorkItemArchived\n\n");
        deserializer.deserialize_i64(WorkItemArchivedVisitor)
    }
}

impl WorkItemArchived {
    pub fn db_int(&self) -> u8 {
        match self {
            WorkItemArchived::Active => 0,
            WorkItemArchived::Archived => 1,
        }
    }
}

#[derive(Debug, Error)]
pub enum WorkItemError {
    #[error("RDS Failed: {0}")]
    RDSError(aws_sdk_rdsdata::Error),

    #[error("Missing item: {0}")]
    MissingItem(String),

    #[error("Invalid Field: {0}")]
    FromFields(String),

    #[error("Other WorkItem Error: {0}")]
    Other(String),
}

impl ResponseError for WorkItemError {
    fn status_code(&self) -> reqwest::StatusCode {
        match self {
            WorkItemError::MissingItem(_) => StatusCode::NOT_FOUND,
            _ => StatusCode::INTERNAL_SERVER_ERROR,
        }
    }

    fn error_response(&self) -> actix_web::HttpResponse<actix_web::body::BoxBody> {
        HttpResponse::build(self.status_code()).json(json!({ "error": format!("{}", self) }))
    }
}

const RDS_DATE_FORMAT: &'static str = "%Y-%m-%d";
pub mod collection {
    use actix_web::{
        get, post,
        web::{self, Data, Json, Path},
        Scope,
    };

    use super::{WorkItem, WorkItemError};
    use crate::client::RdsClient;

    pub fn scope() -> Scope {
        web::scope("/items").service(create).service(retrieve)
    }

    #[post("")]
    #[tracing::instrument(
        name = "Request Create new WorkItem",
        skip(item, client),
        fields(work_item.user = %item.name, work_item.guide = %item.guide,)
    )]
    pub async fn create(
        item: Json<WorkItem>,
        client: Data<RdsClient>,
    ) -> Result<Json<WorkItem>, WorkItemError> {
        super::repository::create(item.0, &client).await.map(Json)
    }

    #[get("/{id}")]
    #[tracing::instrument(name = "Request Retrieve single WorkItem", skip(client))]
    pub async fn retrieve(
        item_id: Path<String>,
        client: Data<RdsClient>,
    ) -> Result<Json<WorkItem>, WorkItemError> {
        super::repository::retrieve(item_id.to_string(), &client)
            .await
            .map(Json)
    }
}

mod repository {
    use aws_sdk_rdsdata::model::RecordsFormatType;
    use serde_json::from_str;

    use super::{WorkItem, WorkItemError, RDS_DATE_FORMAT};
    use crate::{client::RdsClient, params};

    #[tracing::instrument(name = "Repository Create new WorkItem", skip(item, client))]
    pub async fn create(item: WorkItem, client: &RdsClient) -> Result<WorkItem, WorkItemError> {
        client
            .execute_statement(
                "INSERT INTO Work (idwork, username, date, description, guide, status, archive) VALUES
                (:idwork, :username, :date, :description, :guide, :status, :archive);",
            )
            .set_parameters(params!(
                ("idwork", item.idwork.to_string()),
                ("username", item.name),
                ("date", format!("{}", item.date.format(RDS_DATE_FORMAT))),
                ("description", item.description),
                ("guide", item.guide),
                ("status", item.status),
                ("archive", format!("{}", 0))
            ))
            .send()
            .await
            .map_err(|err| {
                tracing::error!("Failed to insert user: {err:?}");
                WorkItemError::RDSError(err.into())
            })?;

        retrieve(item.idwork().to_string(), client).await
    }

    #[tracing::instrument(name = "Repository Retrieve single WorkItem", skip(client))]
    pub async fn retrieve(id: String, client: &RdsClient) -> Result<WorkItem, WorkItemError> {
        let statement = client.execute_statement("SELECT idwork, username, date, description, guide, status, archive FROM Work WHERE idwork = :idwork;")
            .set_parameters(params!(("idwork", id)))
            .format_records_as(RecordsFormatType::Json)
            .send().await;

        // Did the request succeed?
        let data = match statement {
            Ok(data) => Ok(data),
            Err(err) => Err(WorkItemError::RDSError(err.into())),
        }?;

        // Are there records?
        let records = match data.formatted_records() {
            Some(records) => Ok(records),
            None => Err(WorkItemError::MissingItem(id.clone())),
        }?;

        // Can we parse the records?
        let items = match from_str::<Vec<WorkItem>>(records) {
            Ok(items) => Ok(items),
            Err(e) => Err(WorkItemError::FromFields(
                format!("Failed to parse formatted records: {e}").to_string(),
            )),
        }?;

        // Are there enough records?
        if items.len() == 0 {
            return Err(WorkItemError::MissingItem(id.clone()));
        }

        if items.len() > 1 {
            // Warn on too many records
            tracing::warn!("Received multiple results for id: {id}");
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
}
