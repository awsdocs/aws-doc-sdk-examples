// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

use std::str::FromStr;

/*
The arithmetic handler is more complex:
1. It accepts a set of actions ['plus', 'minus', 'times', 'divided-by'] and two numbers, and returns the result of the calculation.
2. It uses an environment variable to control log level (such as DEBUG, INFO, WARNING, ERROR).
It logs a few things at different levels, such as:
    * DEBUG: Full event data.
    * INFO: The calculation result.
    * WARN~ING~: When a divide by zero error occurs.
    * This will be the typical `RUST_LOG` variable.
 */
use anyhow::anyhow;
use lambda_runtime::{service_fn, Error, LambdaEvent};
use serde::Deserialize;
use serde_json::Value;
use tracing::{debug, info, warn};
use tracing_subscriber::EnvFilter;

#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt()
        .json()
        .with_env_filter(EnvFilter::from_default_env())
        .init();
    let func = service_fn(arithmetic_handler);
    lambda_runtime::run(func).await
}

async fn arithmetic_handler(
    event: LambdaEvent<InvokeArgs>,
) -> Result<serde_json::Value, anyhow::Error> {
    let invoke_args = event.payload;

    info!(?invoke_args, "Arithmetic invoked");

    let result = calculate(invoke_args)?;

    Ok(Value::from(result))
}

#[derive(Debug, Deserialize)]
struct InvokeArgs {
    op: Operation,
    i: i32,
    j: i32,
}

#[derive(Clone, Copy, Debug, Deserialize)]
enum Operation {
    #[serde(rename = "plus")]
    Plus,
    #[serde(rename = "minus")]
    Minus,
    #[serde(rename = "times")]
    Times,
    #[serde(rename = "divided-by")]
    DividedBy,
}

impl FromStr for Operation {
    type Err = anyhow::Error;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "plus" => Ok(Operation::Plus),
            "minus" => Ok(Operation::Minus),
            "times" => Ok(Operation::Times),
            "divided-by" => Ok(Operation::DividedBy),
            _ => Err(anyhow!("Unknown operation {s}")),
        }
    }
}

fn calculate(args: InvokeArgs) -> Result<i32, anyhow::Error> {
    let result = match args.op {
        Operation::Plus => add(args.i, args.j),
        Operation::Minus => subtract(args.i, args.j),
        Operation::Times => multiply(args.i, args.j),
        Operation::DividedBy => divide(args.i, args.j),
    }?;

    debug!(?args, ?result, "Full event data",);
    info!("The result of the calculation: {}", result);

    Ok(result)
}

fn add(num1: i32, num2: i32) -> Result<i32, anyhow::Error> {
    Ok(num1 + num2)
}

fn subtract(num1: i32, num2: i32) -> Result<i32, anyhow::Error> {
    Ok(num1 - num2)
}

fn multiply(num1: i32, num2: i32) -> Result<i32, anyhow::Error> {
    Ok(num1 * num2)
}

fn divide(num1: i32, num2: i32) -> Result<i32, anyhow::Error> {
    if num2 == 0 {
        warn!("Attempted to divide by zero");
        return Err(anyhow::anyhow!("Cannot divide by zero"));
    }

    Ok(num1 / num2)
}
