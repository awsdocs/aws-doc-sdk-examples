pub mod collection;
pub mod repository;

use actix_web::{http::StatusCode, HttpResponse, ResponseError};
use chrono::NaiveDate;
use serde::{
    de::{self, Visitor},
    Deserialize, Serialize,
};
use serde_json::json;
use thiserror::Error;
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

const ARCHIVED: &str = &"archive";

impl std::convert::From<&str> for WorkItemArchived {
    fn from(archived: &str) -> Self {
        if matches!(archived, ARCHIVED) {
            WorkItemArchived::Archived
        } else {
            WorkItemArchived::Active
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
