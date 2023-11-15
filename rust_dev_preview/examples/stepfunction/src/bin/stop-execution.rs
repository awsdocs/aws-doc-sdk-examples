/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

use aws_sdk_sfn::{Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The Amazon Resource Name (ARN) of the state machine to execute.
    #[structopt(short, long)]
    arn: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Starts a state machine execution.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { arn, verbose } = Opt::parse();

    let shared_config = aws_config::load_from_env().await;
    let client = Client::new(&shared_config);

    println!();

    if verbose {
        println!("SF arn: {}", &arn);
        println!();
    }

    let rsp = client.stop_execution().execution_arn(&arn).send().await?;

    println!("Step function response: `{:?}`", rsp);

    Ok(())
}
