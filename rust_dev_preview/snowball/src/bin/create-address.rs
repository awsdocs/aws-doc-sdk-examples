/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_snowball::model::Address;
use aws_sdk_snowball::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    // Address information.
    #[structopt(long)]
    city: String,

    #[structopt(long)]
    company: Option<String>,

    #[structopt(long)]
    country: String,

    #[structopt(long)]
    landmark: Option<String>,

    #[structopt(long)]
    name: String,

    #[structopt(long)]
    phone_number: String,

    #[structopt(long)]
    postal_code: String,

    #[structopt(long)]
    prefecture_or_district: Option<String>,

    #[structopt(long)]
    state: String,

    #[structopt(long)]
    street1: String,

    #[structopt(long)]
    street2: Option<String>,

    #[structopt(long)]
    street3: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Create an address.
// snippet-start:[snowball.rust.create-address]
async fn add_address(client: &Client, address: Address) -> Result<(), Error> {
    let result = client.create_address().address(address).send().await?;

    println!();
    println!("Address: {:?}", result.address_id().unwrap());

    Ok(())
}
// snippet-end:[snowball.rust.create-address]

/// Creates an AWS Snowball address.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `--city CITY` - The required city portion of the address.
/// * `[--company COMPANY]` - The company portion of the address.
/// * `--country COUNTRY` - The required country portion of the address.
/// * `[--landmark LANDMARK]` - The landmark portion of the address.
/// * `--name NAME` - The required name portion of the address.
/// * `--phone-number PHONE-NUMBER` - The required phone number portion of the address.
/// * `--postal-code POSTAL-CODE` - The required postal code (zip in USA) portion of the address.
/// * `[--prefecture-or-district PREFECTURE-OR-DISTRICT]` - The prefecture or district portion of the address.
/// * `--state STATE` - The required state portion of the address. It must be (two is best) upper-case letters.
/// * `--street1 STREET1` - The required first street portion of the address.
/// * `[--street2 STREET2]` - The second street portion of the address.
/// * `[--street3 STREET3]` - The third street portion of the address.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        city,
        company,
        country,
        landmark,
        name,
        phone_number,
        postal_code,
        prefecture_or_district,
        state,
        street1,
        street2,
        street3,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Snowball version:       {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("City:                   {}", &city);
        println!("Company:                {:?}", &company);
        println!("Country:                {}", &country);
        println!("Landmark:               {:?}", &landmark);
        println!("Name:                   {}", &name);
        println!("Phone number:           {}", &phone_number);
        println!("Postal code:            {}", &postal_code);
        println!("Prefecture or district: {:?}", &prefecture_or_district);
        println!("State:                  {}", &state);
        println!("Street1:                {}", &street1);
        println!("Street2:                {:?}", &street2);
        println!("Street3:                {:?}", &street3);
    }

    let new_address = Address::builder()
        .set_address_id(None)
        .name(name)
        .set_company(company)
        .street1(street1)
        .set_street2(street2)
        .set_street3(street3)
        .city(city)
        .state_or_province(state)
        .set_prefecture_or_district(prefecture_or_district)
        .set_landmark(landmark)
        .country(country)
        .postal_code(postal_code)
        .phone_number(phone_number)
        .set_is_restricted(Some(false))
        .build();

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    add_address(&client, new_address).await
}
