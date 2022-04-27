use aws_sdk_sfn::{Client, Error};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The Amazon Resource Name (ARN) of the state machine to execute.
    #[structopt(short, long)]
    arn: String,

    /// The string that contains the JSON input data for the execution, for example "{\"first_name\" : \"test\"}".
    #[structopt(short, long)]
    input: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Starts a state machine execution.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        arn,
        input,
        verbose,
    } = Opt::from_args();

    let shared_config = aws_config::load_from_env().await;
    let client = Client::new(&shared_config);

    println!();

    if verbose {
        println!("SF arn: {}", &arn);
        println!("Input: {}", &input);
        println!();
    }

    let rsp = client
        .start_execution()
        .state_machine_arn(&arn)
        .input("{\"input\": \"{}\"}")
        .send()
        .await?;

    println!("Step function response: `{:?}`", rsp);

    Ok(())
}
