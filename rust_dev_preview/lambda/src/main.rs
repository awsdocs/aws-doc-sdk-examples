/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use lambda_runtime::handler_fn;
use log::{error, info};
use serde::{Deserialize, Serialize};

// snippet-start:[lambda.rust.main]
#[derive(Deserialize)]
struct Request {
    pub body: String,
}

#[derive(Debug, Serialize)]
struct SuccessResponse {
    pub body: String,
}

#[derive(Debug, Serialize)]
struct FailureResponse {
    pub body: String,
}

// Implement Display for the Failure response so that we can then implement Error.
impl std::fmt::Display for FailureResponse {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.body)
    }
}

// Implement Error for the FailureResponse so that we can `?` (try) the Response
// returned by `lambda_runtime::run(func).await` in `fn main`.
impl std::error::Error for FailureResponse {}

type Response = Result<SuccessResponse, FailureResponse>;

#[tokio::main]
async fn main() -> Result<(), lambda_runtime::Error> {
    let func = handler_fn(handler);
    lambda_runtime::run(func).await?;

    Ok(())
}

async fn handler(req: Request, _ctx: lambda_runtime::Context) -> Response {
    info!("handling a request...");
    let bucket_name = std::env::var("BUCKET_NAME")
        .expect("A BUCKET_NAME must be set in this app's Lambda environment variables.");

    // No extra configuration is needed as long as your Lambda has
    // the necessary permissions attached to its role.
    let config = aws_config::load_from_env().await;
    let s3_client = aws_sdk_s3::Client::new(&config);
    // Generate a filename based on when the request was received.
    let filename = format!("{}.txt", time::OffsetDateTime::now_utc().unix_timestamp());

    let _ = s3_client
        .put_object()
        .bucket(bucket_name)
        .body(req.body.as_bytes().to_owned().into())
        .key(&filename)
        .content_type("text/plain")
        .send()
        .await
        .map_err(|err| {
            // In case of failure, log a detailed error to CloudWatch.
            error!(
                "failed to upload file '{}' to S3 with error: {}",
                &filename, err
            );
            // The sender of the request receives this message in response.
            FailureResponse {
                body: "The lambda encountered an error and your message was not saved".to_owned(),
            }
        })?;

    info!(
        "Successfully stored the incoming request in S3 with the name '{}'",
        &filename
    );

    Ok(SuccessResponse {
        body: format!(
            "the lambda has successfully stored the your request in S3 with name '{}'",
            filename
        ),
    })
}
// snippet-end:[lambda.rust.main]
