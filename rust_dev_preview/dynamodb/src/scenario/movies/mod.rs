use aws_sdk_dynamodb::model::{AttributeValue, PutRequest};
use serde::Deserialize;
use serde_json::Value;
use thiserror::Error;

pub const TABLE_NAME: &str = "movies";

pub mod server;
pub mod shutdown;
pub mod startup;

#[derive(Error, Debug)]
pub enum MovieError {
    #[error("failed to parse serde_json::Value into Movie {0}")]
    FromValue(&'static Value),

    #[error("unknown DynamoDB movies error")]
    Unknown,
}

#[derive(Debug, Deserialize)]
pub struct Movie {
    year: i32,
    title: String,
    genres: Vec<String>,
    cast: Vec<String>,
}

impl Movie {
    pub fn new(year: i32, title: String) -> Self {
        Movie {
            year,
            title,
            genres: Vec::new(),
            cast: Vec::new(),
        }
    }

    pub fn cast_mut(&mut self) -> &mut Vec<String> {
        &mut self.cast
    }

    pub fn genres_mut(&mut self) -> &mut Vec<String> {
        &mut self.genres
    }
}

impl Into<PutRequest> for &Movie {
    fn into(self) -> PutRequest {
        PutRequest::builder()
            .item("year", AttributeValue::N(self.year.to_string()))
            .item("title", AttributeValue::S(self.title.clone()))
            .item(
                "cast",
                AttributeValue::L(
                    self.cast
                        .iter()
                        .map(|v| AttributeValue::S(v.clone()))
                        .collect(),
                ),
            )
            .item(
                "genre",
                AttributeValue::L(
                    self.genres
                        .iter()
                        .map(|v| AttributeValue::S(v.clone()))
                        .collect(),
                ),
            )
            .build()
    }
}
