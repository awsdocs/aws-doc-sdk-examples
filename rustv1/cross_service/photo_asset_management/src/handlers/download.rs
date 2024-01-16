// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use std::collections::HashSet;

use crate::{apig_response, common::Common, uploader::ZipUpload};
use aws_lambda_events::apigw::ApiGatewayProxyResponse;
use aws_sdk_s3::presigning::PresigningConfig;
use chrono::Duration;
use lambda_runtime::LambdaEvent;
use serde::Deserialize;

#[derive(Deserialize, Debug)]
pub struct DownloadRequest {
    labels: Vec<String>,
}

async fn get_images_for_labels(
    labels: Vec<String>,
    common: &Common,
) -> Result<HashSet<String>, anyhow::Error> {
    let mut image_set = HashSet::<String>::new();

    for label in labels {
        let images_for_label = get_images_for_label(common, label).await?;
        for image in images_for_label {
            image_set.insert(image);
        }
    }

    Ok(image_set)
}

async fn get_images_for_label(
    common: &Common,
    label: String,
) -> Result<Vec<String>, anyhow::Error> {
    tracing::info!("Getting images for {label}");
    let response = common
        .dynamodb_client()
        .get_item()
        .table_name(common.labels_table())
        .key(
            "Label",
            aws_sdk_dynamodb::types::AttributeValue::S(label.to_string()),
        )
        .attributes_to_get("Images")
        .send()
        .await?;

    let images: Vec<String> = if let Some(images) = response.item {
        if let Some(images) = images.get("Images") {
            match images.as_l() {
                Ok(images) => images
                    .iter()
                    .map(|a| a.as_s())
                    .filter(|s| s.is_ok())
                    .map(|s| s.unwrap().clone())
                    .collect(),
                Err(err) => {
                    tracing::warn!(label, ?err, "Did not get list of images for label");
                    vec![]
                }
            }
        } else {
            vec![]
        }
    } else {
        vec![]
    };

    tracing::info!(label, ?images, "got images for label");
    Ok(images)
}

async fn send_notification(
    common: &Common,
    destination: (String, String),
) -> Result<(), anyhow::Error> {
    let topic = common.notification_topic();
    let get_object = common
        .s3_client()
        .get_object()
        .bucket(destination.0)
        .key(destination.1)
        .presigned(
            PresigningConfig::builder()
                .expires_in(Duration::days(1).to_std()?)
                .build()?,
        )
        .await?;

    let uri = get_object.uri();
    tracing::info!(?uri, "prepared presigned URI");
    let message = format!("Retrieve your photos {}", uri);

    common
        .sns_client()
        .publish()
        .topic_arn(topic)
        .message(message)
        .send()
        .await?;

    tracing::info!(topic, "notified channel");

    Ok(())
}

async fn do_upload(
    common: &Common,
    images: HashSet<String>,
) -> Result<(String, String), anyhow::Error> {
    let mut zip_upload = ZipUpload::builder(common).build().await?;

    for image in images {
        tracing::info!(?image, "adding image to bundle");
        zip_upload.add_object(image).await?;
    }

    tracing::info!("added all images to bundle");
    let destination = zip_upload.finish().await?;
    tracing::info!(?destination, "Uploaded zip");

    Ok(destination)
}

async fn do_download(common: &Common, labels: Vec<String>) -> Result<(), anyhow::Error> {
    let images = get_images_for_labels(labels, common).await?;
    let destination = do_upload(common, images).await?;

    send_notification(common, destination).await?;

    Ok(())
}

#[tracing::instrument(skip(common, request), fields(request.payload))]
pub async fn handler(
    common: &Common,
    request: LambdaEvent<DownloadRequest>,
) -> Result<ApiGatewayProxyResponse, anyhow::Error> {
    let body = request.payload;
    // let body: DownloadRequest = serde_json::from_str(body.as_str())?;
    let labels = body.labels;
    tracing::info!("Downloading labels {labels:?}");
    do_download(common, labels).await?;
    Ok(apig_response!("ok"))
}
