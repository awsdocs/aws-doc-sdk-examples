/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::error::Error as StdError;

#[derive(thiserror::Error, Debug)]
#[error("unhandled error")]
pub struct Error {
    #[from]
    source: Box<dyn StdError + Send + Sync + 'static>,
}

impl Error {
    pub fn unhandled(source: impl Into<Box<dyn StdError + Send + Sync + 'static>>) -> Self {
        Self {
            source: source.into(),
        }
    }
}

impl From<aws_sdk_s3::Error> for Error {
    fn from(source: aws_sdk_s3::Error) -> Self {
        Self::unhandled(source)
    }
}

impl<T> From<aws_sdk_s3::types::SdkError<T>> for Error
where
    T: StdError + Send + Sync + 'static,
{
    fn from(source: aws_sdk_s3::types::SdkError<T>) -> Self {
        Self::unhandled(source)
    }
}
