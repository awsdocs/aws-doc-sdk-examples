use std::collections::HashMap;

use aws_sdk_dynamodb::model::{AttributeValue, PutRequest};
use aws_smithy_client::SdkError;
use serde::{Deserialize, Serialize};
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

    #[error("failed to parse response into movies: {0}")]
    FromSerde(serde_dynamo::Error),

    #[error("aws_sdk_dynamodb error: {0}")]
    Dynamo(aws_sdk_dynamodb::Error),

    #[error("unknown DynamoDB movies error: {0}")]
    Unknown(String),
}

impl From<aws_sdk_dynamodb::Error> for MovieError {
    fn from(err: aws_sdk_dynamodb::Error) -> Self {
        MovieError::Dynamo(err)
    }
}

impl From<serde_dynamo::Error> for MovieError {
    fn from(err: serde_dynamo::Error) -> Self {
        MovieError::FromSerde(err)
    }
}

impl<E> From<SdkError<E>> for MovieError
where
    E: std::fmt::Debug,
{
    fn from(err: SdkError<E>) -> Self {
        MovieError::Unknown(format!("{err:?}"))
    }
}

#[derive(Debug, Serialize, Deserialize)]
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

fn as_string(val: Option<&AttributeValue>, default: &String) -> String {
    if let Some(v) = val {
        if let Ok(s) = v.as_s() {
            return s.to_owned();
        }
    }
    default.to_owned()
}

fn as_i32(val: Option<&AttributeValue>, default: i32) -> i32 {
    if let Some(v) = val {
        if let Ok(n) = v.as_n() {
            if let Ok(n) = n.parse::<i32>() {
                return n;
            }
        }
    }
    default
}

fn as_string_vec(val: Option<&AttributeValue>) -> Vec<String> {
    if let Some(val) = val {
        if let Ok(val) = val.as_l() {
            return val
                .iter()
                .map(|v| as_string(Some(v), &"".to_string()))
                .collect();
        }
    }
    // val
    //         .map(|v| v.as_l())
    //         .unwrap_or_else(|| Ok(&Vec::<AttributeValue>::new()))
    //         .unwrap_or_else(|_| &Vec::<AttributeValue>::new())
    //         .iter()
    //         .map(|v| as_string(Some(v), &"".to_string()))
    //         .collect();
    vec![]
}

impl From<&HashMap<String, AttributeValue>> for Movie {
    fn from(value: &HashMap<String, AttributeValue>) -> Self {
        let mut movie = Movie::new(
            as_i32(value.get("year"), 0),
            as_string(value.get("title"), &"".to_string()),
        );

        let mut genres: Vec<String> = as_string_vec(value.get("genres"));
        let mut cast: Vec<String> = as_string_vec(value.get("cast"));

        movie.genres_mut().append(&mut genres);
        movie.cast_mut().append(&mut cast);

        movie
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
