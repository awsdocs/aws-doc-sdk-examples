use aws_sdk_iot::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The endpoint type.
    #[structopt(short, long)]
    endpoint_type: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/*
async fn is_good_type(t: String) -> bool {
    return t == "iot:Data" || t == "iot:Data-ATS" || t == "iot:CredentialProvider" || t == "iot:Jobs";
}
*/

/// Returns a unique endpoint specific to the AWS account making the call, in the Region.
///
/// # Arguments
///
/// * `-t ENDPOINT-TYPE - The type of endpoint.
///   Must be one of:
///   - iot:Data - Returns a VeriSign signed data endpoint.
///   - iot:Data-ATS - Returns an ATS signed data endpoint.
///   - iot:CredentialProvider - Returns an AWS IoT credentials provider API endpoint.
//    - iot:Jobs - Returns an AWS IoT device management Jobs API endpoint.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        endpoint_type,
        verbose,
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("IoT client version: {}", PKG_VERSION);
        println!("Region:             {}", region.region().unwrap().as_ref());
        println!("Endpoint type:      {}", &endpoint_type);

        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let resp = client
        .describe_endpoint()
        .endpoint_type(endpoint_type)
        .send()
        .await?;

    println!("Endpoint address: {}", resp.endpoint_address.unwrap());

    println!();

    Ok(())
}
