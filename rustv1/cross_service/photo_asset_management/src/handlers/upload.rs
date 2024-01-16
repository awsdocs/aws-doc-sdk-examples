// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use std::time::Duration;

use anyhow::anyhow;
use aws_lambda_events::apigw::{ApiGatewayProxyRequest, ApiGatewayProxyResponse};
use aws_sdk_s3::presigning::PresigningConfig;
use lambda_runtime::LambdaEvent;
use serde::{Deserialize, Serialize};
use serde_json::json;

use crate::{apig_response, common::Common};

#[derive(Deserialize)]
pub struct UploadRequest {
    file_name: String,
}

#[derive(Serialize)]
struct Url {
    url: String,
}

impl std::fmt::Display for Url {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", json!(self))
    }
}

async fn make_put_url(common: &Common, file_name: String) -> Result<Url, anyhow::Error> {
    let uuid = uuid::Uuid::new_v4();
    let key_name = format!("{uuid}/{file_name}");
    let put_object = common
        .s3_client()
        .put_object()
        .bucket(common.storage_bucket())
        .key(key_name)
        .content_type("image/jpeg")
        .presigned(PresigningConfig::expires_in(Duration::from_secs(5 * 60))?)
        .await?;
    Ok(Url {
        url: put_object.uri().to_string(),
    })
}

#[tracing::instrument(skip(common))]
pub async fn handler(
    common: &Common,
    request: LambdaEvent<ApiGatewayProxyRequest>,
) -> Result<ApiGatewayProxyResponse, anyhow::Error> {
    let body = request
        .payload
        .body
        .ok_or_else(|| anyhow!("missing upload request"))?;
    let request: UploadRequest = serde_json::from_str(body.as_str())?;
    let url = make_put_url(common, request.file_name).await?;
    Ok(apig_response!(url))
}

#[cfg(test)]
mod test {
    use serde_json::json;

    use super::Url;

    #[test]
    pub fn test_url_serialization() {
        let url = Url {
            url: "https://localhost/object".to_string(),
        };
        let stringed_url = json!(url);
        assert_eq!(
            stringed_url.to_string(),
            r#"{"url":"https://localhost/object"}"#
        );
    }
}
