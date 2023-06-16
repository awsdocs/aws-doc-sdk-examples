/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
    This piece of code serves as an example for serde-support feature as described on RFC30.
    When features are not enabled, it will simply print how you can enable said feature.
*/

#[cfg(not(all(
    aws_sdk_unstable,
    all(feature = "serde-serialize", feature = "serde-deserialize")
)))]
fn main() {
    println!(
        r#"
    serde features are not enabled!
    e.g.

    export RUSTFLAGS="--cfg aws_sdk_unstable";
    cargo run --all-features --bin serde-support

    "#
    )
}

#[cfg(all(
    aws_sdk_unstable,
    all(feature = "serde-serialize", feature = "serde-deserialize")
))]
fn main() {
    use aws_sdk_dynamodb::{
        model::{
            AttributeValue, ConditionalOperator, ExpectedAttributeValue, ReturnConsumedCapacity,
            ReturnItemCollectionMetrics, ReturnValue,
        },
        operation::PutItem,
    };

    // based on the json thing found here https://docs.aws.amazon.com/cli/latest/userguide/cli-services-dynamodb.html
    let putitem = PutItem::builder()
        .table_name("MusicCollection")
        .item("Artists", AttributeValue::S("No One You Know".to_string()))
        .item("SongTitle", AttributeValue::S("Call Me Today".to_string()))
        .item(
            "AlbumTitle",
            AttributeValue::S("Somewhat Famous".to_string()),
        )
        .return_consumed_capacity(ReturnConsumedCapacity::Total)
        .expected(
            "key",
            ExpectedAttributeValue::builder()
                .exists(true)
                .attribute_value_list(AttributeValue::Bool(true))
                .attribute_value_list(AttributeValue::Null(false))
                .build(),
        )
        .return_values(ReturnValue::AllNew)
        .return_item_collection_metrics(ReturnItemCollectionMetrics::Size)
        .conditional_operator(ConditionalOperator::And)
        .condition_expression("stuff")
        .expression_attribute_names("key", "names")
        .expression_attribute_values(
            "key",
            AttributeValue::Ns(
                [
                    "42.2".to_string(),
                    "-19".to_string(),
                    "7.5".to_string(),
                    "3.14".to_string(),
                ]
                .to_vec(),
            ),
        )
        .build()
        .unwrap();
    assert_eq!(
        serde_json::to_string_pretty(&putitem).ok(),
        Some(include_str!("../../serialize_document_example.json").to_string())
    );
}
