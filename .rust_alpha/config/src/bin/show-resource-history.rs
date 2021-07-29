use aws_sdk_config::model::ResourceType;
use aws_sdk_config::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The ID of the resource.
    #[structopt(short, long)]
    id: String,

    /// The resource type.
    #[structopt(short, long)]
    type_: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Displays the configuration history for a resource.
///
/// NOTE: AWS Config must be enabled to discover resources.
/// # Arguments
///
/// * `-i ID` - The ID of the resource.
/// * `-t TYPE` - The resource type, such as `AWS::EC2::SecurityGroup`.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        id,
        type_,
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
        println!("Resource ID:           {}", &id);
        println!("Resource type:         {}", &type_);
        println!();
    }

    // Parse resource type from user input.
    let parsed = ResourceType::from(type_.as_str());

    // Make sure it's a known type.
    if matches!(parsed, ResourceType::Unknown(_)) {
        panic!(
            "unknown resource type: `{}`. Valid resource types: {:#?}",
            &type_,
            ResourceType::values()
        )
    }
    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let rsp = client
        .get_resource_config_history()
        .resource_id(&id)
        .resource_type(parsed)
        .send()
        .await?;
    println!("configuration history for {}:", id);
    for item in rsp.configuration_items.unwrap_or_default() {
        println!("item: {:?}", item);
    }

    Ok(())
}
