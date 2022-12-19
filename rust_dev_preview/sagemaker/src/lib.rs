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

impl From<aws_sdk_sagemaker::Error> for Error {
    fn from(source: aws_sdk_sagemaker::Error) -> Self {
        Self {
            source: source.into(),
        }
    }
}

impl<T> From<aws_sdk_sagemaker::types::SdkError<T>> for Error
where
    T: StdError + Send + Sync + 'static,
{
    fn from(source: aws_sdk_sagemaker::types::SdkError<T>) -> Self {
        Self {
            source: source.into(),
        }
    }
}

impl From<aws_smithy_types_convert::date_time::Error> for Error {
    fn from(source: aws_smithy_types_convert::date_time::Error) -> Self {
        Self {
            source: source.into(),
        }
    }
}
