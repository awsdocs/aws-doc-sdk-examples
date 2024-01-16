// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use aws_config::SdkConfig;
use aws_sdk_cloudwatchlogs::{Client, Error, GetQueryResultsRequest, StartQueryRequest};
use std::time::{Duration, SystemTime};

struct QueryConfig {
    log_group_names: Vec<String>,
    date_range: (SystemTime, SystemTime),
    limit: u32,
}

struct QueryResults {
    results: Vec<Vec<String>>,
    seconds_elapsed: u64,
}

impl QueryResults {
    fn new(results: Vec<Vec<String>>, elapsed: Duration) -> Self {
        QueryResults {
            results,
            seconds_elapsed: elapsed.as_secs(),
        }
    }
}

struct Query {
    client: Client,
    config: QueryConfig,
}

impl Query {
    fn new(config: SdkConfig, query_config: QueryConfig) -> Self {
        let client = Client::new(&config);
        Query { client, config } 
    }

    async fn run(&self) -> QueryResults {
        let start = SystemTime::now();
        let results = self.query_logs(self.config.date_range).await;
        let end = SystemTime::now();
        let elapsed = end.duration_since(start).unwrap();
        
        QueryResults::new(results, elapsed)
    }

    async fn query_logs(&self, date_range: (SystemTime, SystemTime)) -> Vec<Vec<String>> {
        // Implementation of recursive query
        todo!() 
    }

    async fn start_query(&self, date_range: (SystemTime, SystemTime)) -> Result<String, Error> {
        let start_ms = date_range.0.duration_since(SystemTime::UNIX_EPOCH)?.as_millis() as i64;
        let end_ms = date_range.1.duration_since(SystemTime::UNIX_EPOCH)?.as_millis() as i64;

        let request = StartQueryRequest::builder()
            .log_group_names(self.config.log_group_names.clone())
            .start_time(start_ms)
            .end_time(end_ms)
            .limit(self.config.limit)
            .query_string("fields @timestamp, @message | sort @timestamp asc".into())
            .build();

        self.client.start_query(request).await.map(|resp| resp.query_id)
    }

    async fn get_query_results(&self, query_id: &str) -> Result<Option<Vec<Vec<String>>>, Error> {
        let request = GetQueryResultsRequest::builder()
            .query_id(query_id.to_string())
            .build();

        let response = self.client.get_query_results(request).await?;

        Ok(response.results)
    }
}