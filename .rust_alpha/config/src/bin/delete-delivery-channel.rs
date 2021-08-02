use aws_sdk_config::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The channel to delete.
    #[structopt(short, long)]
    channel: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Deletes an AWS Config delivery channel.
///
/// # Arguments
///
/// * `-c CHANNEL` - The name of the channel to delete.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        channel,
        region,
        verbose,
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Config client version: {}", PKG_VERSION);
        println!(
            "Region:                {}",
            region.region().unwrap().as_ref()
        );
        println!("Delivery channel:      {}", channel);

        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    client
        .delete_delivery_channel()
        .delivery_channel_name(channel)
        .send()
        .await?;

    println!("Done");

    Ok(())
}
