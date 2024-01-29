// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use std::collections::HashMap;

use aws_sdk_dynamodb::types::{AttributeDefinition, KeySchemaElement, ProvisionedThroughput};
use aws_sdk_rekognition::types::Label;
use photo_asset_management::{
    common::{init_tracing_subscriber, Common},
    handlers::detect_labels::apply_updates,
};

fn make_label(l: &str) -> Label {
    Label::builder().name(l).build()
}

async fn create_table(common: &Common) -> Result<(), impl std::error::Error> {
    let create = common
        .dynamodb_client()
        .create_table()
        .table_name(common.labels_table())
        .key_schema(
            KeySchemaElement::builder()
                .attribute_name("Label")
                .key_type(aws_sdk_dynamodb::types::KeyType::Hash)
                .build(),
        )
        .attribute_definitions(
            AttributeDefinition::builder()
                .attribute_name("Label")
                .attribute_type(aws_sdk_dynamodb::types::ScalarAttributeType::S)
                .build(),
        )
        .provisioned_throughput(
            ProvisionedThroughput::builder()
                .write_capacity_units(1)
                .read_capacity_units(1)
                .build(),
        )
        .send()
        .await;

    match create {
        Ok(_) => {
            tracing::info!("Created table");
            Ok(())
        }
        Err(e) => {
            let e = e.into_service_error();
            if e.is_resource_in_use_exception() {
                tracing::info!("Table already exists");
                Ok(())
            } else {
                Err(e)
            }
        }
    }
}

#[tokio::test]
async fn main() {
    init_tracing_subscriber();
    let sdk_config = aws_config::load_from_env().await;
    let common = Common::new(
        sdk_config,
        "UNUSED".into(),
        "UNUSED".into(),
        "TEST_LABELS_TABLE".into(),
        "UNUSED".into(),
    );

    create_table(&common).await.unwrap();

    let mut updates: HashMap<String, Vec<Label>> = HashMap::new();
    updates.insert(
        "image_1".into(),
        vec!["label1", "label2"]
            .into_iter()
            .map(make_label)
            .collect(),
    );
    updates.insert(
        "image_2".into(),
        vec!["label2", "label3"]
            .into_iter()
            .map(make_label)
            .collect(),
    );

    let updates = apply_updates(&common, updates).await;
    tracing::info!(?updates, "updates result");
    assert!(updates.is_ok(), "expect updates to succeed");

    let scan_count = common
        .dynamodb_client()
        .scan()
        .table_name(common.labels_table())
        .select(aws_sdk_dynamodb::types::Select::Count)
        .send()
        .await;

    assert!(scan_count.is_ok());
    let scan_count = scan_count.unwrap();
    assert_eq!(scan_count.scanned_count(), 3, "expected 3 labels");

    let _delete = common
        .dynamodb_client()
        .delete_table()
        .table_name(common.labels_table())
        .send()
        .await
        .unwrap();
}
