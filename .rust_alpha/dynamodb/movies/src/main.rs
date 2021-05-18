/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_http::AwsErrorRetryPolicy;
use aws_hyper::{SdkError, SdkSuccess};
use dynamodb::client::fluent_builders::Query;
use dynamodb::error::DescribeTableError;
use dynamodb::input::DescribeTableInput;
use dynamodb::model::{
    AttributeDefinition, AttributeValue, KeySchemaElement, KeyType, ProvisionedThroughput,
    ScalarAttributeType, TableStatus,
};
use dynamodb::operation::DescribeTable;
use dynamodb::output::DescribeTableOutput;
use dynamodb::{Config, Region};
use serde_json::Value;
use smithy_http::operation::Operation;
use smithy_http::retry::ClassifyResponse;
use smithy_types::retry::RetryKind;
use std::collections::HashMap;
use std::time::Duration;

/// A partial reimplementation of https://docs.amazonaws.cn/en_us/amazondynamodb/latest/developerguide/GettingStarted.Ruby.html
/// in Rust
///
/// - Create table
/// - Wait for table to be ready
/// - Add a couple of rows
/// - Query for those rows
#[tokio::main]
async fn main() {
    let table_name = "dynamo-movies-example";
    let conf = dynamodb::Config::builder()
        .region(Region::new("us-east-1"))
        .build();
    let conn = aws_hyper::conn::Standard::https();
    let client = dynamodb::Client::from_conf_conn(conf, conn);
    let raw_client = aws_hyper::Client::https();

    let table_exists = client
        .list_tables()
        .send()
        .await
        .expect("should succeed")
        .table_names
        .as_ref()
        .unwrap()
        .contains(&table_name.to_string());

    if !table_exists {
        create_table(&client, table_name)
            .send()
            .await
            .expect("failed to create table");
    }

    raw_client
        .call(wait_for_ready_table(table_name, client.conf()))
        .await
        .expect("table should become ready");

    // data.json contains 2 movies from 2013
    let data = match serde_json::from_str(include_str!("data.json")).expect("should be valid JSON")
    {
        Value::Array(inner) => inner,
        data => panic!("data must be an array, got: {:?}", data),
    };
    for value in data {
        client
            .put_item()
            .table_name(table_name)
            .set_item(Some(parse_item(value)))
            .send()
            .await
            .expect("failed to insert item");
    }
    let films_2222 = movies_in_year(&client, table_name, 2222)
        .send()
        .await
        .expect("query should succeed");
    // this isn't back to the future, there are no movies from 2022
    assert_eq!(films_2222.count, 0);

    let films_2013 = movies_in_year(&client, table_name, 2013)
        .send()
        .await
        .expect("query should succeed");
    assert_eq!(films_2013.count, 2);
    let titles: Vec<AttributeValue> = films_2013
        .items
        .unwrap()
        .into_iter()
        .map(|mut row| row.remove("title").expect("row should have title"))
        .collect();
    assert_eq!(
        titles,
        vec![
            AttributeValue::S("Rush".to_string()),
            AttributeValue::S("Turn It Down, Or Else!".to_string())
        ]
    );
}

fn create_table(
    client: &dynamodb::Client,
    table_name: &str,
) -> dynamodb::client::fluent_builders::CreateTable {
    client
        .create_table()
        .table_name(table_name)
        .key_schema(
            KeySchemaElement::builder()
                .attribute_name("year")
                .key_type(KeyType::Hash)
                .build(),
        )
        .key_schema(
            KeySchemaElement::builder()
                .attribute_name("title")
                .key_type(KeyType::Range)
                .build(),
        )
        .attribute_definitions(
            AttributeDefinition::builder()
                .attribute_name("year")
                .attribute_type(ScalarAttributeType::N)
                .build(),
        )
        .attribute_definitions(
            AttributeDefinition::builder()
                .attribute_name("title")
                .attribute_type(ScalarAttributeType::S)
                .build(),
        )
        .provisioned_throughput(
            ProvisionedThroughput::builder()
                .read_capacity_units(10)
                .write_capacity_units(10)
                .build(),
        )
}

fn parse_item(value: Value) -> HashMap<String, AttributeValue> {
    match value_to_item(value) {
        AttributeValue::M(map) => map,
        other => panic!("can only insert top level values, got {:?}", other),
    }
}

fn value_to_item(value: Value) -> AttributeValue {
    match value {
        Value::Null => AttributeValue::Null(true),
        Value::Bool(b) => AttributeValue::Bool(b),
        Value::Number(n) => AttributeValue::N(n.to_string()),
        Value::String(s) => AttributeValue::S(s),
        Value::Array(a) => AttributeValue::L(a.into_iter().map(value_to_item).collect()),
        Value::Object(o) => {
            AttributeValue::M(o.into_iter().map(|(k, v)| (k, value_to_item(v))).collect())
        }
    }
}

fn movies_in_year(client: &dynamodb::Client, table_name: &str, year: u16) -> Query {
    client
        .query()
        .table_name(table_name)
        .key_condition_expression("#yr = :yyyy")
        .expression_attribute_names("#yr", "year")
        .expression_attribute_values(":yyyy", AttributeValue::N(year.to_string()))
}

/// Hand-written waiter to retry every second until the table is out of `Creating` state
#[derive(Clone)]
struct WaitForReadyTable<R> {
    inner: R,
}

impl<R> ClassifyResponse<SdkSuccess<DescribeTableOutput>, SdkError<DescribeTableError>>
    for WaitForReadyTable<R>
where
    R: ClassifyResponse<SdkSuccess<DescribeTableOutput>, SdkError<DescribeTableError>>,
{
    fn classify(
        &self,
        response: Result<&SdkSuccess<DescribeTableOutput>, &SdkError<DescribeTableError>>,
    ) -> RetryKind {
        match self.inner.classify(response) {
            RetryKind::NotRetryable => (),
            other => return other,
        };
        match response {
            Ok(SdkSuccess { parsed, .. }) => {
                if parsed
                    .table
                    .as_ref()
                    .unwrap()
                    .table_status
                    .as_ref()
                    .unwrap()
                    == &TableStatus::Creating
                {
                    RetryKind::Explicit(Duration::from_secs(1))
                } else {
                    RetryKind::NotRetryable
                }
            }
            _ => RetryKind::NotRetryable,
        }
    }
}

/// Construct a `DescribeTable` request with a policy to retry every second until the table
/// is ready
fn wait_for_ready_table(
    table_name: &str,
    conf: &Config,
) -> Operation<DescribeTable, WaitForReadyTable<AwsErrorRetryPolicy>> {
    let operation = DescribeTableInput::builder()
        .table_name(table_name)
        .build()
        .expect("valid input")
        .make_operation(&conf)
        .expect("valid operation");
    let waiting_policy = WaitForReadyTable {
        inner: operation.retry_policy().clone(),
    };
    operation.with_retry_policy(waiting_policy)
}
