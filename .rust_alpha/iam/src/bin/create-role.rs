use aws_sdk_iam::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
//use serde_json;
use std::fs;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
  /// The AWS Region.
  #[structopt(short, long)]
  region: Option<String>,

  /// Your account ID.
  #[structopt(short, long)]
  account: String,

  /// The name of the bucket.
  #[structopt(short, long)]
  bucket: String,

  /// The name of the role.
  #[structopt(short, long)]
  name: String,

  /// The name of the file containing the policy document.
  #[structopt(short, long)]
  policy_file: String,

  /// Whether to display additional information.
  #[structopt(short, long)]
  verbose: bool,
}

/// Creates an IAM role in the Region.
///
/// # Arguments
///
/// * `-a ACCOUNT-ID` - Your account ID.
/// * `-b BUCKET` - The name of the bucket where Config stores information about resources.
/// * `-n NAME` - The name of the role.
/// * `-p POLICY-NAME` - The name of the JSON file containing the policy document.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
  tracing_subscriber::fmt::init();
  let Opt {
    account,
    bucket,
    name,
    policy_file,
    region,
    verbose,
  } = Opt::from_args();

  let region = region::ChainProvider::first_try(region.map(Region::new))
    .or_default_provider()
    .or_else(Region::new("us-west-2"));

  println!();

  if verbose {
    println!("IAM client version: {}", PKG_VERSION);
    println!("Region:             {}", region.region().unwrap().as_ref());
    println!("Account ID:         {}", &account);
    println!("Bucket:             {}", &bucket);
    println!("Role name:          {}", &name);
    println!("Policy doc filename {}", &policy_file);
    println!();
  }

  // Read policy doc from file as a string
  let doc = fs::read_to_string(policy_file).expect("Unable to read file");
  //let doc: serde_json::Value = serde_json::from_str(&data).expect("Unable to parse");

  let conf = Config::builder().region(region).build();
  let client = Client::from_conf(conf);

  let resp = client
    .create_role()
    .assume_role_policy_document(doc)
    .role_name(name)
    .send()
    .await?;

  println!("Created role with ARN {}", resp.role.unwrap().arn.unwrap());

  Ok(())
}
