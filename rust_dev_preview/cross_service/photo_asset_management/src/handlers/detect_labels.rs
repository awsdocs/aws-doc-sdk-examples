use std::collections::HashMap;

use anyhow::anyhow;
use aws_lambda_events::{
    apigw::ApiGatewayProxyResponse,
    s3::{S3Event, S3EventRecord},
};
use aws_sdk_dynamodb::operation::update_item::builders::UpdateItemFluentBuilder;
use aws_sdk_rekognition::types::{Image, Label, S3Object};
use lambda_runtime::LambdaEvent;

use crate::{apig_response, common::Common};

fn prepare_update_expression(
    update: UpdateItemFluentBuilder,
    object: &String,
    label: &Label,
) -> UpdateItemFluentBuilder {
    update
        .key(
            "Label",
            aws_sdk_dynamodb::types::AttributeValue::S(
                label.name().expect("found label name").to_string(),
            ),
        )
        // Using an update expression ensures that the count and list are updated atomically.
        // This does require passing `:one` as a value.
        .update_expression("SET #Count = if_not_exists(#Count, :zero) + :one, Images = list_append(if_not_exists(Images, :empty), :image)")
        .expression_attribute_names("#Count", "Count")
        .expression_attribute_values(
            ":zero",
            aws_sdk_dynamodb::types::AttributeValue::N("0".to_string()),
        )
        .expression_attribute_values(
            ":one",
            aws_sdk_dynamodb::types::AttributeValue::N("1".to_string()),
        )
        .expression_attribute_values(
            ":image",
            aws_sdk_dynamodb::types::AttributeValue::L(vec![
                aws_sdk_dynamodb::types::AttributeValue::S(object.to_string()),
            ]),
        )
        .expression_attribute_values(
            ":empty",
            aws_sdk_dynamodb::types::AttributeValue::L(vec![ ]),
        )
}

async fn detect_record<'a>(
    common: &Common,
    bucket: &String,
    object: &String,
) -> Result<Vec<Label>, anyhow::Error> {
    let labels = common
        .rekognition_client()
        .detect_labels()
        .image(
            Image::builder()
                .s3_object(S3Object::builder().bucket(bucket).name(object).build())
                .build(),
        )
        .max_labels(10)
        .send()
        .await?
        .labels()
        .iter()
        .map(|l| l.to_owned())
        .collect();

    Ok(labels)
}

pub async fn find_labels(
    common: &Common,
    records: Vec<S3EventRecord>,
) -> Result<HashMap<String, Vec<Label>>, anyhow::Error> {
    let mut object_labels_map = HashMap::<String, Vec<Label>>::with_capacity(records.len());

    for record in records {
        let object = record
            .s3
            .object
            .key
            .ok_or_else(|| anyhow!("missing object key"))?;
        let labels = detect_record(common, common.storage_bucket(), &object).await?;
        object_labels_map.insert(object, labels);
    }

    Ok(object_labels_map)
}

pub async fn apply_updates(
    common: &Common,
    updates: HashMap<String, Vec<Label>>,
) -> Result<usize, anyhow::Error> {
    let mut count = 0;
    for (object, labels) in updates {
        tracing::info!(object, ?labels, "Adding labels for image");
        for label in labels {
            let update = common
                .dynamodb_client()
                .update_item()
                .table_name(common.labels_table());
            let expression = prepare_update_expression(update, &object, &label);
            let result = expression.send().await?;
            tracing::info!(?result, "Updated image with labels");
        }
        count += 1;
    }

    Ok(count)
}

#[tracing::instrument(skip(common, request))]
pub async fn handler(
    common: &Common,
    request: LambdaEvent<S3Event>,
) -> Result<ApiGatewayProxyResponse, anyhow::Error> {
    let updates = find_labels(common, request.payload.records).await?;
    let count = apply_updates(common, updates).await?;

    tracing::trace!("Handled {count} records");
    Ok(apig_response!(format!("Handled {count} records")))
}

#[cfg(test)]
mod test {
    use super::prepare_update_expression;
    use aws_config::SdkConfig;

    #[tokio::test]
    async fn test_prepare_update_statement() {
        let object = "object".to_string();
        let label = aws_sdk_rekognition::types::Label::builder()
            .name("label")
            .build();

        let client = aws_sdk_dynamodb::Client::new(&SdkConfig::builder().build());
        let update = client.update_item();
        let update = prepare_update_expression(update, &object, &label);

        // TODO: This test would be better if it could get an UpdateItemInput directly, but that's
        // hidden inside the SDK. Waiting for smithy-rs to expose it more directly.
        let update_debug = format!("{:?}", update);
        let split = update_debug
            .split(", inner: UpdateItemInputBuilder ")
            .map(|s| s.to_string())
            .collect::<Vec<String>>();
        let update_inner_debug = split.get(1).expect("inner as Debug");

        assert!(update_inner_debug.contains("table_name: None"));
        assert!(update_inner_debug.contains("key: Some({\"Label\": S(\"label\")})"));
        assert!(update_inner_debug.contains("update_expression: Some(\"SET #Count = if_not_exists(#Count, :zero) + :one, Images = list_append(if_not_exists(Images, :empty), :image)\")"));
        assert!(update_inner_debug
            .contains("expression_attribute_names: Some({\"#Count\": \"Count\"})"));
        assert!(update_inner_debug.contains("\":empty\": L([])"));
        assert!(update_inner_debug.contains("\":image\": L([S(\"object\")])"));
        assert!(update_inner_debug.contains("\":one\": N(\"1\")"));
        assert!(update_inner_debug.contains("\":zero\": N(\"0\")"));
    }
}
