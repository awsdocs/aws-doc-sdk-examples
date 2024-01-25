// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use std::{collections::HashSet, env, str::from_utf8, time::Duration};

use aws_config::BehaviorVersion;
use aws_sdk_cloudwatchlogs::{
    config::{
        interceptors::InterceptorContext,
        retry::{ClassifyRetry, RetryAction},
    },
    error::ProvideErrorMetadata,
    operation::{
        get_query_results::GetQueryResultsOutput,
        start_query::{StartQueryError, StartQueryInput, StartQueryOutput},
    },
    types::ResultField,
    Client, Config, Error,
};
use aws_types::sdk_config::RetryConfig;
use chrono::{DateTime, Utc};

#[derive(Debug)]
enum LargeQueryError {
    CloudwatchLogsError(Error),
    DateOutOfBoundsError,
}

#[derive(Debug)]
struct CloudWatchLongQueryQueryResultRetryClassifier {
    status_done: HashSet<String>,
}

impl CloudWatchLongQueryQueryResultRetryClassifier {
    fn new() -> Self {
        CloudWatchLongQueryQueryResultRetryClassifier {
            status_done: vec!["Complete", "Failed", "Cancelled", "Timeout", "Unknown"]
                .into_iter()
                .map(String::from)
                .collect(),
        }
    }
}

impl ClassifyRetry for CloudWatchLongQueryQueryResultRetryClassifier {
    fn classify_retry(&self, ctx: &InterceptorContext) -> RetryAction {
        match ctx.response() {
            None => RetryAction::NoActionIndicated,
            Some(response) => {
                let body = from_utf8(response.body().bytes().unwrap_or_default());
                match body {
                    Err(_) => {
                        RetryAction::retryable_error(aws_config::retry::ErrorKind::ServerError)
                    }
                    Ok(body) => {
                        if self.status_done.iter().any(|status| body.contains(status)) {
                            RetryAction::RetryForbidden
                        } else {
                            RetryAction::retryable_error(aws_config::retry::ErrorKind::ServerError)
                        }
                    }
                }
            }
        }
    }

    fn name(&self) -> &'static str {
        "CloudWatch Long Query QueryResult Retry Classifier"
    }
}

struct CloudWatchLongQuery {
    client: Client,
    log_group_name: String,
    date_range: (DateTime<Utc>, DateTime<Utc>),
    limit: i32,
    results: Vec<Vec<ResultField>>,
}

impl CloudWatchLongQuery {
    fn new(
        client: Client,
        log_group_name: String,
        date_range: (DateTime<Utc>, DateTime<Utc>),
    ) -> Self {
        Self {
            client,
            log_group_name,
            date_range,
            limit: 10_000,
            results: vec![],
        }
    }

    async fn run(&mut self) -> Result<(), LargeQueryError> {
        Ok(())
    }

    async fn get_query_results(
        &self,
        query_id: &str,
    ) -> Result<GetQueryResultsOutput, LargeQueryError> {
        self.client
            .get_query_results()
            .query_id(query_id)
            .customize()
            .config_override(
                Config::builder()
                    .retry_config(
                        RetryConfig::standard()
                            .with_max_attempts(60)
                            .with_initial_backoff(Duration::from_secs(1)),
                    )
                    .retry_classifier(CloudWatchLongQueryQueryResultRetryClassifier::new()),
            )
            .send()
            .await
            .map_err(|err| LargeQueryError::CloudwatchLogsError(err.into()))
    }

    async fn query(
        &self,
        date_range: (DateTime<Utc>, DateTime<Utc>),
    ) -> Result<Vec<Vec<ResultField>>, LargeQueryError> {
        let query_id = self.start_query(date_range).await?;
        Ok(vec![])
    }

    async fn start_query(
        &self,
        date_range: (DateTime<Utc>, DateTime<Utc>),
    ) -> Result<String, LargeQueryError> {
        let response = self
            .client
            .start_query()
            .log_group_name(self.log_group_name.clone())
            .query_string("fields @timestamp, @message | sort @timestamp asc")
            .start_time(date_range.0.timestamp())
            .end_time(date_range.1.timestamp())
            .limit(self.limit)
            .send()
            .await;
        match response {
            Ok(start) => Ok(start.query_id.expect("start query query_id")),
            Err(err) => {
                if err
                    .message()
                    .unwrap_or_default()
                    .starts_with("Query's end date and time")
                {
                    Err(LargeQueryError::DateOutOfBoundsError)
                } else {
                    Err(LargeQueryError::CloudwatchLogsError(err.into()))
                }
            }
        }
    }
}

#[tokio::main]
async fn main() -> Result<(), LargeQueryError> {
    tracing_subscriber::fmt::init();

    let group = env::var("QUERY_GROUP").expect("lookup QUERY_GROUP");
    let start_date = DateTime::parse_from_rfc3339(
        env::var("QUERY_START_DATE")
            .expect("lookup QUERY_START_DATE")
            .as_str(),
    )
    .expect("parse state date")
    .with_timezone(&Utc);
    let end_date = DateTime::parse_from_rfc3339(
        env::var("QUERY_END_DATE")
            .expect("lookup QUERY_END_DATE")
            .as_str(),
    )
    .expect("parse end date")
    .with_timezone(&Utc);

    println!("{group}");
    println!("{start_date}");
    println!("{end_date}");

    let shared_config = aws_config::from_env()
        .behavior_version(BehaviorVersion::latest())
        .load()
        .await;
    let client = Client::new(&shared_config);

    let mut query = CloudWatchLongQuery::new(client, group, (start_date, end_date));

    query.run().await.expect("run query to completion");

    eprintln!("Total results: {}", query.results.len());

    Ok(())
}
