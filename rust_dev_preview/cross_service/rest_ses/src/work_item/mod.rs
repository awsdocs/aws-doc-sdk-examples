/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

//! WorkItem domain entity and error wrapper.
pub mod collection;
pub mod repository;
pub mod work_item_archived;

pub use work_item_archived::WorkItemArchived;

use actix_web::{http::StatusCode, HttpResponse, ResponseError};
use chrono::NaiveDate;
use serde::{Deserialize, Serialize};
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
    /// This alias is for the frontened using `username` in some contexts, but the backend using `name`.
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
            WorkItemError::FromFields(_) => StatusCode::BAD_REQUEST,
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
