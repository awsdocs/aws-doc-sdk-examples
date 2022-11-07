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

This example uses [Actix Web]() to host a local web server and REST service.
With the web server running, you can send HTTP requests to the service endpoint to list, add, and update work items and to send email reports.

```
cargo run
```

### Webpage

The REST service is designed to work with the item tracker plugin in the Elwing web
client. The item tracker plugin is a JavaScript application that lets you manage work
items, send requests to the REST service, and see the results.

#### Run Elwing and select the item tracker

1.  Run Elwing by following the instructions in the [Elwing README](/resources/clients/react/elwing/README.md).
1.  When Elwing starts, a web browser opens and browses to http://localhost:3000/.
1.  Run the item tracker plugin by selecting **Item Tracker** in the left navigation bar.
1.  This sends a request to the REST service to get any existing active items:
    ```
    GET http://localhost:8080/api/items?archived=false
    ```
1.  At first, the table is empty.

    ![Work item tracker](images/item-tracker-start.png)

1.  Select **Add item**, fill in the values, and select **Add** to add an item.

    ![Add item](images/item-tracker-add-item.png)

    This sends a POST request to the REST service with a JSON payload that contains the
    work item.

    ```
    POST http://localhost:8080/api/items
    {"name":"Me",
     "guide":"Rust",
     "description":"Show how to add an item",
     "status":"In progress",
     "archived":false}
    ```

1.  After you've added items, they're displayed in the table. You can archive an active
    item by selecting the **Archive** button next to the item.

    ![Work item tracker with items](images/item-tracker-all-items.png)

    This sends a PUT request to the REST service, specifying the item ID and the
    `archive` action.

    ```
    PUT http://localhost:8080/api/items/8db8aaa4-6f04-4467-bd60-EXAMPLEGUID:archive
    ```

1.  Select a filter in the dropdown list, such as **Archived**, to get and display
    only items with the specified status.

        ![Work item tracker Archived items](images/item-tracker-archived-items.png)

    This sends a GET request to the REST service with an `archived` query parameter.

    ```
    GET http://localhost:8080/api/items?archived=true
    ```

1.  Enter an email recipient and select **Send report** to send an email of active items.

    ![Work item tracker send report](images/item-tracker-send-report.png)

    This sends a POST request to the REST service with a `report` action.

    ```
    POST http://localhost:8080/api/items:report
    ```

    When your Amazon SES account is in the sandbox, both the sender and recipient
    email addresses must be registered with Amazon SES.

## Understand the example

This example uses the Actix web framework to host a local REST service and respond to HTTP requests.

### Start up

### Routing

Top level

```Rust
item_list_view = ItemList.as_view('item_list_api', storage)
app.add_url_rule(
    '/api/items', defaults={'iditem': None}, view_func=item_list_view, methods=['GET'],
    strict_slashes=False)
```

HTTP requests are routed to methods in the [ItemList](item_list.py) and
[Report](report.py) classes, which use webargs and marshmallow to handle argument
parsing and data transformation.

For example, the work item schema includes a field that is named `id` in the web page,
but is named `iditem` in the data table. By defining a `data_key`, the marshmallow
schema transforms this field automatically.

```Rust
class WorkItemSchema(Schema):
    iditem = fields.Str(data_key='id')
```

The `ItemList` class contains methods that handle REST requests and use the
`@use_args` and `@use_kwargs` decorators from webargs to parse incoming arguments.

For example, the `get` method uses `@use_kwargs` to parse fields contained in the query
string into arguments in the method signature, and then calls the underlying `storage`
object to get work items from the DynamoDB table.

```Rust
@use_kwargs(WorkItemSchema, location='query')
def get(self, iditem, archived=None):
    work_items = self.storage.get_work_items(archived)
```

### Aurora Serverless repository

The [src/work_item/repository.rs](repository.rs) file contains functions that get and set data in an Aurora Serverless database by using an Amazon RDS Data Service client.

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

The [src/report.rs](report.rs) file contains functions that route the report HTTP request and send an email report of work items to a specified email address.

The report is send as an xslx Excel spreadshee.
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
