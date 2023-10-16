use std::collections::HashMap;

use anyhow::anyhow;
use aws_lambda_events::apigw::{ApiGatewayProxyRequest, ApiGatewayProxyResponse};
use aws_sdk_dynamodb::types::AttributeValue;
use lambda_runtime::LambdaEvent;
use serde::Serialize;
use serde_json::json;

use crate::{apig_response, common::Common};

#[derive(Serialize, PartialEq, Eq)]
struct Label {
    count: u32,
}

impl Label {
    fn new(count: u32) -> Self {
        Label { count }
    }
}

#[derive(Serialize, PartialEq, Eq)]
struct Labels {
    labels: HashMap<String, Label>,
}

struct LabelEntry {
    label: String,
    count: u32,
}

impl TryFrom<&HashMap<String, AttributeValue>> for LabelEntry {
    type Error = anyhow::Error;

    fn try_from(value: &HashMap<String, AttributeValue>) -> Result<Self, Self::Error> {
        let label = {
            value
                .get("Label")
                .ok_or_else(|| anyhow!("found item Label attribute"))?
                .as_s()
                .map_err(|e| anyhow!("Could not get Label as string {e:?}"))?
                .clone()
        };
        let count = {
            value
                .get("Count")
                .ok_or_else(|| anyhow!("found item Count attribute"))?
                .as_n()
                .map_err(|e| anyhow!("Could not get Count as number {e:?}"))?
                .clone()
                .parse::<u32>()?
        };
        Ok(LabelEntry { label, count })
    }
}

impl Labels {
    fn new() -> Self {
        Labels {
            labels: HashMap::new(),
        }
    }

    fn add(&mut self, label: String, count: u32) {
        self.labels.insert(label, Label::new(count));
    }

    fn insert(&mut self, entry: LabelEntry) {
        self.add(entry.label, entry.count);
    }
}

async fn get_labels(
    client: &aws_sdk_dynamodb::Client,
    table: String,
) -> Result<Labels, anyhow::Error> {
    let scan = client
        .scan()
        .table_name(table)
        .select(aws_sdk_dynamodb::types::Select::SpecificAttributes)
        .projection_expression("Label, #c")
        .expression_attribute_names("#c", "Count")
        .send()
        .await?;

    let mut labels = Labels::new();
    for item in scan.items() {
        labels.insert(item.try_into()?);
    }

    Ok(labels)
}

#[tracing::instrument(skip(common))]
pub async fn handler(
    common: &Common,
    _request: LambdaEvent<ApiGatewayProxyRequest>,
) -> Result<ApiGatewayProxyResponse, anyhow::Error> {
    let labels = get_labels(common.dynamodb_client(), common.labels_table().clone()).await?;

    Ok(apig_response!(json!(labels)))
}

#[cfg(test)]
mod test {
    use sdk_examples_test_utils::single_shot_client;
    use serde_json::json;

    use super::{get_labels, Labels};

    #[tokio::test]
    async fn test_get_labels() {
        let client: aws_sdk_dynamodb::Client = single_shot_client! {
            sdk: aws_sdk_dynamodb,
            status: 200,
            response: r#"{"Count":2,"Items":[{"Label":{"S":"Mountain"},"Count":{"N":"3"}},{"Label":{"S":"Lake"},"Count":{"N":"2"}}],"ScannedCount":2}"#
        };
        let labels = get_labels(&client, "test".to_string())
            .await
            .map_err(|e| {
                eprintln!("{e}");
                e
            })
            .expect("got labels")
            .labels;
        assert_eq!(labels.get("Mountain").expect("has Mountain").count, 3);
        assert_eq!(labels.get("Lake").expect("has Lake").count, 2);
    }

    #[test]
    fn test_labels_response() {
        let mut labels = Labels::new();
        labels.add("Mountain".to_string(), 3);
        labels.add("River".to_string(), 5);
        labels.add("Lake".to_string(), 2);
        let labels_json = json!(labels);
        assert_eq!(
            labels_json.to_string(),
            r#"{"labels":{"Lake":{"count":2},"Mountain":{"count":3},"River":{"count":5}}}"#
        )
    }
}
