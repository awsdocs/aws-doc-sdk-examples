/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::error::Error as StdError;

#[derive(thiserror::Error, Debug)]
pub enum Error {
    #[error("table was not ready after several attempts: {0}")]
    TableNotReady(String),
    #[error("unhandled error")]
    Unhandled(#[source] Box<dyn StdError + Send + Sync + 'static>),
}

impl Error {
    pub fn table_not_ready(table_name: impl Into<String>) -> Self {
        Self::TableNotReady(table_name.into())
    }

    pub fn unhandled(source: impl Into<Box<dyn StdError + Send + Sync + 'static>>) -> Self {
        Self::Unhandled(source.into())
    }
}

impl From<aws_sdk_dynamodb::Error> for Error {
    fn from(source: aws_sdk_dynamodb::Error) -> Self {
        Error::unhandled(source)
    }
}

impl<T> From<aws_sdk_dynamodb::error::SdkError<T>> for Error
where
    T: StdError + Send + Sync + 'static,
{
    fn from(source: aws_sdk_dynamodb::error::SdkError<T>) -> Self {
        Error::unhandled(source)
    }
}
