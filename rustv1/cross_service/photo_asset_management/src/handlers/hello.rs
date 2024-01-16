// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use aws_lambda_events::apigw::{ApiGatewayProxyRequest, ApiGatewayProxyResponse};
use lambda_runtime::LambdaEvent;

use crate::{apig_response, common::Common};

#[tracing::instrument(skip(_common))]
pub async fn handler(
    _common: &Common,
    request: LambdaEvent<ApiGatewayProxyRequest>,
) -> Result<ApiGatewayProxyResponse, anyhow::Error> {
    Ok(apig_response!(format!(
        "Hello, {:?}",
        request.payload.body.unwrap_or_default()
    )))
}
