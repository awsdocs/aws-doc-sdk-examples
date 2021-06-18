/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_types::region::ProvideRegion;

use qldbsession::model::StartSessionRequest;
use qldbsession::{Client, Config, Error, Region};

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region. Overrides environment variable AWS_DEFAULT_REGION.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Specifies the ledger
    #[structopt(short, long)]
    ledger: String,

    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

/// Creates a low-level Amazon QLDB session.
/// # Arguments
///
/// * `-l LEDGER` - The name of the ledger to start a new session against.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt {
        ledger,
        default_region,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("OLDB client version: {}\n", qldb::PKG_VERSION);
        println!("Region:              {:?}", &region);
        println!("Ledger:              {}", ledger);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);
    let result = client
        .send_command()
        .start_session(StartSessionRequest::builder().ledger_name(ledger).build())
        .send()
        .await?;

    match result.start_session {
        Some(s) => {
            println!("Your session id: {:?}", s.session_token);
        }
        None => unreachable!("a start session will result in an Err or a start session result"),
    }

    Ok(())
}
