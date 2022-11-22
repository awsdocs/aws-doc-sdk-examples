/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::error::Error as StdError;

#[derive(thiserror::Error, Debug)]
#[error("unhandled error")]
pub struct Error {
    #[source]
    source: Box<dyn StdError + Send + Sync + 'static>,
}

impl Error {
    pub fn unhandled(source: impl Into<Box<dyn StdError + Send + Sync + 'static>>) -> Self {
        Self {
            source: source.into(),
        }
    }
}

impl From<aws_sdk_iotsitewise::Error> for Error {
    fn from(source: aws_sdk_iotsitewise::Error) -> Self {
        Error::unhandled(source)
    }
}

impl<T> From<aws_sdk_iotsitewise::types::SdkError<T>> for Error
where
    T: StdError + Send + Sync + 'static,
{
    fn from(source: aws_sdk_iotsitewise::types::SdkError<T>) -> Self {
        Error::unhandled(source)
    }
}

impl From<aws_smithy_types_convert::date_time::Error> for Error {
    fn from(source: aws_smithy_types_convert::date_time::Error) -> Self {
        Self {
            source: source.into(),
        }
    }
}
