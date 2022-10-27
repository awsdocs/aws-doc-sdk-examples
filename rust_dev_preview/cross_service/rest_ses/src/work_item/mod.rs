//! WorkItem domain entity and error wrapper.
pub mod collection;
pub mod repository;

use std::convert::TryFrom;

use actix_web::{http::StatusCode, HttpResponse, ResponseError};
use chrono::NaiveDate;
use serde::{
    de::{self, Visitor},
    Deserialize, Serialize,
};
use serde_json::json;
use thiserror::Error;
use uuid::Uuid;

/// A WorkItem, following the spec definition.
/// Many fields are default, both for default as well as serde.
/// `name` has an alias of `username`, to match the FE spec with the table defition.
#[derive(Clone, Debug, Default, Deserialize, Serialize)]
pub struct WorkItem {
    #[serde(default = "uuid::Uuid::new_v4")]
    idwork: Uuid,
    #[serde(default)]
    date: NaiveDate,
    /// This ...
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
    pub fn date(&self) -> &NaiveDate {
        &self.date
    }
    pub fn name(&self) -> &str {
        self.name.as_str()
    }
    pub fn description(&self) -> &str {
        self.description.as_str()
    }
    pub fn guide(&self) -> &str {
        self.guide.as_str()
    }
    pub fn status(&self) -> &str {
        self.status.as_str()
    }
}

/// An enum to represent the archival status of a WorkItem.
/// This field has varying representations at different parts of the stack, so needs some specialized serde visitors.
#[derive(Clone, Copy, Debug, Default, PartialEq)]
pub enum WorkItemArchived {
    Active,
    Archived,
    #[default]
    All,
}

const ACTIVE: &str = "active";
const ARCHIVED: &str = "archive";

impl From<&WorkItemArchived> for u8 {
    fn from(value: &WorkItemArchived) -> Self {
        match value {
            WorkItemArchived::Active => 0,
            WorkItemArchived::Archived => 1,
            WorkItemArchived::All => 255,
        }
    }
}

impl From<&WorkItemArchived> for &str {
    fn from(value: &WorkItemArchived) -> Self {
        match value {
            WorkItemArchived::Active => ACTIVE,
            WorkItemArchived::Archived => ARCHIVED,
            WorkItemArchived::All => ACTIVE,
        }
    }
}

impl TryFrom<u8> for WorkItemArchived {
    type Error = WorkItemError;

    fn try_from(value: u8) -> Result<Self, Self::Error> {
        match value {
            0 => Ok(WorkItemArchived::Active),
            1 => Ok(WorkItemArchived::Archived),
            _ => Err(WorkItemError::Archival(format!(
                "Unrecognized archive number {value}"
            ))),
        }
    }
}

impl TryFrom<&str> for WorkItemArchived {
    type Error = WorkItemError;
    fn try_from(value: &str) -> Result<Self, Self::Error> {
        match value {
            "" => Ok(WorkItemArchived::All),
            ACTIVE | "0" => Ok(WorkItemArchived::Active),
            ARCHIVED | "1" => Ok(WorkItemArchived::Archived),
            _ => Err(WorkItemError::Archival(format!(
                "Unrecognized archive string {value}"
            ))),
        }
    }
}

impl TryFrom<Option<String>> for WorkItemArchived {
    type Error = WorkItemError;

    fn try_from(value: Option<String>) -> Result<Self, Self::Error> {
        value.unwrap_or_default().as_str().try_into()
    }
}

impl Serialize for WorkItemArchived {
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        serializer.serialize_str(self.into())
    }
}

/// The front end specifies archived status with the strings "archived" and "active".
/// The RDS table encodes status as 1 and 0.
/// This serde visit handles those disparate cases, using the try_from impls.
struct WorkItemArchivedVisitor;
impl<'de> Visitor<'de> for WorkItemArchivedVisitor {
    type Value = WorkItemArchived;

    fn visit_unit<E>(self) -> Result<Self::Value, E>
    where
        E: de::Error,
    {
        Ok(WorkItemArchived::default())
    }

    fn visit_u64<E>(self, value: u64) -> Result<Self::Value, E>
    where
        E: de::Error,
    {
        (value as u8).try_into().map_err(E::custom)
    }

    fn visit_str<E>(self, value: &str) -> Result<Self::Value, E>
    where
        E: de::Error,
    {
        value.try_into().map_err(E::custom)
    }

    fn expecting(&self, formatter: &mut std::fmt::Formatter) -> std::fmt::Result {
        formatter.write_str("int 0 or 1, or string 'active' or 'archived'")
    }
}

impl<'de> Deserialize<'de> for WorkItemArchived {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        deserializer.deserialize_u64(WorkItemArchivedVisitor)
    }
}

/// The various WorkItem specific errors that can occur.
#[derive(Debug, Error)]
pub enum WorkItemError {
    /// An error when the requested itemid is missing.
    #[error("Missing item: {0}")]
    MissingItem(String),

    /// An error in the underlying RDSError service.
    #[error("RDS Failed: {0}")]
    RDSError(aws_sdk_rdsdata::Error),

    /// An error when parsing a request or response body from Json to WorkItem.
    #[error("Invalid Field: {0}")]
    FromFields(String),

    /// An unknown archive value was sent.
    #[error("Unknown archive state: {0}")]
    Archival(String),

    /// Some other error.
    #[error("Other WorkItem Error: {0}")]
    Other(String),
}

impl ResponseError for WorkItemError {
    /// MissingItem is a 404, everything else is a server error.
    fn status_code(&self) -> reqwest::StatusCode {
        match self {
            WorkItemError::MissingItem(_) => StatusCode::NOT_FOUND,
            _ => StatusCode::INTERNAL_SERVER_ERROR,
        }
    }

    /// All errors get formatted using their display formtting, and put into the `error` response body field.
    fn error_response(&self) -> actix_web::HttpResponse<actix_web::body::BoxBody> {
        HttpResponse::build(self.status_code()).json(json!({ "error": format!("{}", self) }))
    }
}

#[cfg(test)]
mod test {
    use crate::work_item::{WorkItem, WorkItemArchived};

    #[test]
    fn deser_work_item_database() {
        let work_item: WorkItem = serde_json::from_str(
            r#"{
            "idwork":"d060bafa-5cf4-486e-8e0f-2fc97a54382e",
            "date":"1970-01-01",
            "description":"A test item",
            "guide":"Rust",
            "status":"",
            "username":"David",
            "archive":0
        }"#,
        )
        .unwrap();

        assert_eq!(work_item.archive, WorkItemArchived::Active);
    }

    #[test]
    fn ser_work_item() {
        let work_item: WorkItem = serde_json::from_str(
            r#"{
            "idwork":"d060bafa-5cf4-486e-8e0f-2fc97a54382e",
            "date":"1970-01-01",
            "description":"A test item",
            "guide":"Rust",
            "status":"",
            "username":"David",
            "archive":0
        }"#,
        )
        .unwrap();

        let ser_work_item = serde_json::to_string_pretty(&work_item).unwrap();
        assert!(ser_work_item.contains(r#""archive": "active""#));
    }

    #[test]
    fn deser_work_item_database_collection() {
        let work_item: WorkItem = serde_json::from_str(
            r#"{
            "idwork":"d060bafa-5cf4-486e-8e0f-2fc97a54382e",
            "date":"1970-01-01",
            "description":"A test item",
            "guide":"Rust",
            "status":"",
            "username":"David",
            "archive":"active"
        }"#,
        )
        .unwrap();

        assert_eq!(work_item.archive, WorkItemArchived::Active);
    }
}
