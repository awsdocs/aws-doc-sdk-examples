# Track work items in an Aurora Serverless database with the SDK for Rust

## Overview

This example shows you how to use the AWS SDK for Rust to create a REST service that lets you do the following:

- Build an Actix Web REST service that integrates with AWS services.
- Read, write, and update work items that are stored in an Amazon Aurora Serverless database.
- Create an AWS Secrets Manager secret that contains database credentials and use it to authenticate calls to the database.
- Use Amazon Simple Email Service (Amazon SES) to send email reports of work items.

The REST service is used in conjunction with the [Elwing React client](../../../resources/clients/react/elwing)
to present a fully functional web application.

### ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum
  permissions required to perform the task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see
  [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

Prerequisites for running examples can be found in the [README](../../README.md#Prerequisites) in the Rust folder.

## Create the resources

Follow the instructions in the [README for the Aurora Serverless application](/resources/cdk/aurora_serverless_app/README.md) to use the AWS Cloud Development Kit (AWS CDK) or AWS Command Line Interface (AWS CLI) to create and manage the resources.

## Run the example

### REST service

#### Configure the service

Before you run the service, enter your AWS resource values and verified email address
in `configuration/local.yaml`, similar to the following:

```yaml
rds:
  db_instance: auroraappdb
  secret_arn: "arn:aws:secretsmanager:us-east-1:1111222233334444:secret:docexampleauroraappsecret8B-EXAMPLE-Dz2N2y"
  cluster_arn: "arn:aws:rds:us-east-1:1111222233334444:cluster:docexampleauroraapp-docexampleauroraappclustereb7e-EXAMPLE"
ses:
  source: "report-account@amazondomains.com" # Replace with an email address that is registered with Amazon SES.
```

#### Run the service

This example uses [Actix Web](https://actix.rs/) to host a local web server and REST service.
With the web server running, you can send HTTP requests to the service endpoint to list, add, and update work items and to send email reports.

```
cargo run
```

### Webpage

The REST service is designed to work with the item tracker plugin in the Elwing web client.
The item tracker plugin is a JavaScript application that lets you manage work items, send requests to the REST service, and see the results.

#### Run Elwing and select the item tracker

1.  Run Elwing by following the instructions in the [Elwing README](/resources/clients/react/elwing/README.md).
1.  When Elwing starts, a web browser opens and browses to http://localhost:3000/.
1.  Run the item tracker plugin by selecting **Item Tracker** in the left navigation bar.
1.  Follow the Elwing [Item Tracker instructions](/resources/clients/react/elwing/src/plugins/item-tracker/README.md).

## Understand the example

This example uses the Actix Web framework to host a local REST service and respond to HTTP requests.

### Start up

When the program first starts, it loads environment and configuration information from `src/configuration.rs`.
It then prepares its actix web resources and SDK clients in `src/startup.rs`.
These helpers are also used in `test/startup.rs` to ensure the application is running in a consistent environment in development, testing, and production.

### Routing

Top level routing happens when creating the HTTP server in `src/startup.rs`, with specific routes registered in `src/healthz.rs`, `src/collection.rs`, and `src/report.rs`.
All routes are instrumented, and primarily serve as a facade between Actix's HTTP tooling and the SDK resources.

```Rust
/// Retrieve a single WorkItem, in a JSON body, specified by a URL parameter.
#[actix_web::get("/{id}")]
#[tracing::instrument(name = "Request Retrieve single WorkItem", skip(client))]
async fn retrieve(
    itemid: Path<String>,
    client: Data<crate::client::RdsClient>,
) -> Result<Json<crate::work_item::WorkItem>, crate::work_item::WorkItemError> {
    crate::work_item::repository::retrieve(itemid.to_string(), &client)
        .await
        .map(Json)
}
```

### Aurora Serverless repository

The [repository.rs](src/work_item/repository.rs) file contains functions that get and set data in an Aurora Serverless database by using an Amazon RDS Data Service client.

For example, the `retrieve` function constructs a `SELECT` statement and parameters and sends them to the data client to get a single work item.

```Rust
pub async fn retrieve(id: String, client: &crate::client::RdsClient) -> Result<crate::work_item::WorkItem, crate::work_item::WorkItemError> {
    let statement = client
        .execute_statement()
        .sql(format!(
            r#"SELECT {FIELDS} FROM Work WHERE idwork = :idwork;"#
        ))
        .set_parameters(params![("idwork", id)])
        .format_records_as(RecordsFormatType::Json)
        .send()
        .await;

    let items = parse_rds_output(statement)?;
    todo!("Further checks to ensure a single item was retrieved")
}
```

### Amazon SES report

The [report.rs](src/report.rs) file contains functions that route the report HTTP request and send an email report of work items to a specified email address.

The report is send as an XSLX Excel spreadshee.
When you use Amazon SES to send an attachment, you must use the `send_raw_email` service action and send the email in MIME format.

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial.

If you created the example resources by using the AWS CDK or AWS CLI, you can destroy the resources by following the instructions in the [README for the Aurora Serverless application](/resources/cdk/aurora_serverless_app/README.md).

If you created your resources through the AWS Management Console, or modified them by running the app, you must use the console to delete them.

## Next steps

Congratulations!
You have built a REST service that reads, writes, and archives work items that are stored in an Aurora Serverless database, and that uses Amazon SES to send email to a registered user.

## Additional information

- [Amazon Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
- [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
- [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon RDS Data Service Rust SDK API Reference](https://crates.io/crates/aws-sdk-rdsdata)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
