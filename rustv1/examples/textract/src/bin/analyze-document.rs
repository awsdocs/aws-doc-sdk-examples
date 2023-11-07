/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_textract::config::Region;
use aws_sdk_textract::error::DisplayErrorContext;
use aws_sdk_textract::primitives::Blob;
use aws_sdk_textract::types::{BlockType, Document, FeatureType, QueriesConfig, Query};
use std::path::PathBuf;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    #[structopt(long)]
    document_path: PathBuf,

    #[structopt(
        short,
        long,
        default_value = "What is this customer's favorite pizza topping?"
    )]
    query: String,
}

#[tokio::main]
#[allow(clippy::result_large_err)]
async fn main() {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        query,
        document_path,
    } = Opt::from_args();
    let document_bytes = std::fs::read(document_path).expect("file exists and is readable");

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = aws_sdk_textract::Client::new(&shared_config);

    let res = client
        .analyze_document()
        .document(Document::builder().bytes(Blob::new(document_bytes)).build())
        .feature_types(FeatureType::Queries)
        .queries_config(
            QueriesConfig::builder()
                .queries(
                    Query::builder()
                        .text(&query)
                        .build()
                        .expect("Failed to build query"),
                )
                .build()
                .expect("Failed to build query config"),
        )
        .send()
        .await;

    match res {
        Ok(analyze_output) => {
            let pizza_topping = analyze_output
                .blocks()
                .iter()
                .filter_map(|block| match block.block_type()? {
                    BlockType::QueryResult => Some(block),
                    _ => None,
                })
                .filter_map(|block| block.text().map(ToOwned::to_owned))
                .next()
                .expect("found query result");

            println!("{query} {pizza_topping}");
        }
        Err(err) => {
            println!(
                "Could not answer query '{query}'\n\t{}",
                DisplayErrorContext(&err)
            );
        }
    };
}
