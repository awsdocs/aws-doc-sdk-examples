#  Track work items in an Aurora Serverless database with the SDK for Python

## Overview

This example shows you how to use the AWS SDK for Python (Boto3) to create a REST 
service that lets you do the following:

* Build a Flask REST service that integrates with AWS services.
* Read, write, and update work items that are stored in an Amazon Aurora Serverless database.
* Create an AWS Secrets Manager secret that contains database credentials and use it
  to authenticate calls to the database.
* Use Amazon Simple Email Service (Amazon SES) to send email reports of work items.

The REST service is used in conjunction with the [Elwing React client](../../../resources/clients/react/elwing)
to present a fully functional web application.

### ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum 
  permissions required to perform the task. For more information, see 
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see 
  [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

Prerequisites for running examples can be found in the 
[README](../../README.md#Prerequisites) in the Python folder.

In addition to the standard prerequisites, this example also requires:

* Flask 2.2.0 or later
* Flask-Cors 3.0.10 or later
* webargs 8.2.0 or later

You can install all of the prerequisites by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```
 
## Create the resources

### Aurora Serverless cluster and Secrets Manager secret

This example requires an Aurora Serverless cluster that contains a MySQL database. The
database must be configured to use credentials that are contained in a Secrets Manager
secret. 

Follow the instructions in the 
[README for the Aurora Serverless application](/resources/cdk/aurora_serverless_app/README.md) 
to use the AWS Cloud Development Kit (AWS CDK) or AWS Command Line Interface
(AWS CLI) to create and manage the resources. 

### Create the work items table

After you have created the Aurora DB cluster and database, you must create a table to
contain work items. You can do this by using either the AWS Command Line Interface 
(AWS CLI) or the AWS Management Console.

#### AWS CLI

Use the AWS CLI to create the `work_items` table by running the following command at a 
command prompt. Before you run, replace the following values with the output from the 
CloudFormation setup script:

* **CLUSTER_ARN** — Replace with the ARN of the Aurora DB cluster, such as 
`arn:aws:rds:us-west-2:123456789012:cluster:doc-example-aurora-app-docexampleauroraappcluster-15xfvaEXAMPLE`.
* **SECRET_ARN** — Replace with the ARN of the secret that contains your database
credentials, such as `arn:aws:secretsmanager:us-west-2:123456789012:secret:docexampleauroraappsecret8B-xI1R8EXAMPLE-hfDaaj`.
* **DATABASE** — Replace with the name of the database, such as `auroraappdb`.  

*Tip:* The caret `^` is the line continuation character for a Windows command prompt.
If you run this command on another platform, replace the caret with the line continuation
character for that platform.

```
aws rds-data execute-statement ^
    --resource-arn "CLUSTER_ARN" ^
    --database "DATABASE" ^
    --secret-arn "SECRET_ARN" ^
    --sql "create table work_items (iditem INT AUTO_INCREMENT PRIMARY KEY, description TEXT, guide VARCHAR(45), status TEXT, username VARCHAR(45), archive BOOL DEFAULT 0);"
```

#### AWS Management Console

Use the Console to create the `work_items` table with the following steps:

1. Browse to the [Amazon RDS console](https://console.aws.amazon.com/rds).
2. Select *Query Editor*.
3. For *Database instance or cluster*, choose your database instance. If you used the 
CloudFormation script to create your AWS resources, the name begins with 
`doc-example-aurora-app-`.
4. For *Database username*, choose *Connect with a Secrets Manager ARN*. 
5. Enter the ARN of the secret that contains your database credentials, such as 
`arn:aws:secretsmanager:us-west-2:123456789012:secret:docexampleauroraappsecret8B-xI1R8EXAMPLE-hfDaaj`. 
6. For *Enter the name of the database or schema*, enter the name of your database, such
as `auroraappdb`.
7. Select *Connect to database*.

This opens a SQL query console. You can run any SQL queries here that you want. Run the 
following to create the `work_items` table:

```sql
create table work_items ( 
  iditem INT AUTO_INCREMENT PRIMARY KEY, 
  description TEXT, 
  guide VARCHAR(45), 
  status TEXT, 
  username VARCHAR(45), 
  archive BOOL DEFAULT 0
);
```

### Verified email address

To email reports from the app, you must register at least one email address with 
Amazon SES. This verified email is specified as the sender for emailed reports.

1. In a browser, navigate to the [Amazon SES console](https://console.aws.amazon.com/ses/).
1. If necessary, select your AWS Region.
1. Select **Verified identities**.
1. Select **Create identity**.
1. Select **Email address**.
1. Enter an email address you own.
1. Select **Create identity**.
1. You will receive an email from Amazon Web Services that contains instructions on how
to verify the email with Amazon SES. Follow the instructions in the email to complete
verification.

*Tip:* For this example, you can use the same email account for both the sender and 
the recipient.

## Run the example 

### REST service

#### Configure the service

Before you run the service, enter your AWS resource values and verified email address
in `config.py`, similar to the following:

* **CLUSTER_ARN** — Replace with the ARN of the Aurora DB cluster, such as 
`arn:aws:rds:us-west-2:123456789012:cluster:doc-example-aurora-app-docexampleauroraappcluster-15xfvaEXAMPLE`.
* **SECRET_ARN** — Replace with the ARN of the secret that contains your database
credentials, such as `arn:aws:secretsmanager:us-west-2:123456789012:secret:docexampleauroraappsecret8B-xI1R8EXAMPLE-hfDaaj`.
* **DATABASE** — Replace with the name of the database, such as `auroraappdb`.  
* **TABLE_NAME** — Replace with the name of the work item table, such as `work_items`.
* **SENDER_EMAIL** — Replace with an email address that is registered with Amazon SES. 

#### Run the service

This example uses [Flask](https://flask.palletsprojects.com/en/2.0.x/) to host a local 
web server and REST service. With the web server running, you can send HTTP requests to
the service endpoint to list, add, and update work items and to send email reports.

Run the app at a command prompt to start the Flask web server. Specify the `--debug`
flag for more detailed output during development, and specify a port of 8080 to work
with the Elwing client.

```
flask --debug run -p 8080
```

### Webpage

The REST service is designed to work with the item tracker plugin in the Elwing web
client. The item tracker plugin is a JavaScript application that lets you manage work 
items, send requests to the REST service, and see the results.

#### Run Elwing and select the item tracker

1. Run Elwing by following the instructions in the [Elwing README](/resources/clients/react/elwing/README.md).
1. When Elwing starts, a web browser opens and browses to http://localhost:3000/.
1. Run the item tracker plugin by selecting **Item Tracker** in the left navigation bar.
1. This sends a request to the REST service to get any existing active items:
   ```
   GET http://localhost:8080/api/items?archived=false
   ```
1. At first, the table is empty.

    ![Work item tracker](images/item-tracker-start.png)

1. Select **Add item**, fill in the values, and select **Add** to add an item.

    ![Add item](images/item-tracker-add-item.png)

   This sends a POST request to the REST service with a JSON payload that contains the
   work item.

   ```
   POST http://localhost:8080/api/items
   {"name":"Me",
    "guide":"python",
    "description":"Show how to add an item",
    "status":"In progress",
    "archived":false}
   ```

1. After you've added items, they're displayed in the table. You can archive an active 
   item by selecting the **Archive** button next to the item.

    ![Work item tracker with items](images/item-tracker-all-items.png)

   This sends a PUT request to the REST service, specifying the item ID and the
   `archive` action. 
   
   ```
   PUT http://localhost:8080/api/items/8db8aaa4-6f04-4467-bd60-EXAMPLEGUID:archive
   ```
   
1. Select a filter in the dropdown list, such as **Archived**, to get and display
only items with the specified status.

    ![Work item tracker Archived items](images/item-tracker-archived-items.png)

   This sends a GET request to the REST service with an `archived` query parameter.
   
   ```
   GET http://localhost:8080/api/items?archived=true
   ```

1. Enter an email recipient and select **Send report** to send an email of active items.

    ![Work item tracker send report](images/item-tracker-send-report.png)

   This sends a POST request to the REST service with a `report` action.
   
   ```
   POST http://localhost:8080/api/items:report
   ```
    
   When your Amazon SES account is in the sandbox, both the sender and recipient
   email addresses must be registered with Amazon SES.

## Understand the example

This example uses the Flask web framework to host a local REST service and respond to
HTTP requests.

### Routing

The [app.py](app.py) file configures the app, creates Boto3 resources, and sets up 
URL routing. This example uses Flask's `MethodView` class to help with routing.

In this file, you can find route definitions like the following, which routes a GET
request to `/api/items` to the `ItemList.get` method:

```python
item_list_view = ItemList.as_view('item_list_api', storage)
app.add_url_rule(
    '/api/items', defaults={'iditem': None}, view_func=item_list_view, methods=['GET'],
    strict_slashes=False)
```  

### REST methods

HTTP requests are routed to methods in the [ItemList](item_list.py) and 
[Report](report.py) classes, which use webargs and marshmallow to handle argument 
parsing and data transformation.

For example, the work item schema includes a field that is named `id` in the web page,
but is named `iditem` in the data table. By defining a `data_key`, the marshmallow 
schema transforms this field automatically. 

```python
class WorkItemSchema(Schema):
    iditem = fields.Str(data_key='id')
``` 

The `ItemList` class contains methods that handle REST requests and use the
`@use_args` and `@use_kwargs` decorators from webargs to parse incoming arguments.

For example, the `get` method uses `@use_kwargs` to parse fields contained in the query 
string into arguments in the method signature, and then calls the underlying `storage`
object to get work items from the DynamoDB table.

```python
@use_kwargs(WorkItemSchema, location='query')
def get(self, iditem, archived=None):
    work_items = self.storage.get_work_items(archived)
```

### Aurora Serverless MySQL storage

The [storage.py](storage.py) file contains functions that get and set data in an
Aurora Serverless MySQL database by using a Boto3 Amazon RDS Data Service object. 
This object wraps low-level Amazon RDS Data Service actions.
 
For example, the `get_work_items` function constructs a `SELECT` statement and parameters 
and sends them to the data client to get work items with a specified `archived` status:

```python
def get_work_items(self, archived=None):
    if archived is not None:
        sql_where = "WHERE archived=:archived"
        sql_params = [{'name': 'archived', 'value': {'booleanValue': archived}}]
    sql = f"SELECT iditem, description, guide, status, username, archived FROM {self._table_name} {sql_where}"
    results = self._run_statement(sql, sql_params=sql_params)

def _run_statement(self, sql, sql_params=None):
    run_args = {
        'database': self._db_name,
        'resourceArn': self._cluster,
        'secretArn': self._secret,
        'sql': sql
    }
    if sql_params is not None:
        run_args['parameters'] = sql_params
    results = self._rdsdata_client.execute_statement(**run_args)
```

### Amazon SES report

The [report.py](report.py) file contains functions that send an email report of work 
items to a specified email address.

When 10 or fewer work items are included in the report, the work item list is included 
directly in the email body, with both HTML and text versions. This style of report is
sent by using the `send_email` service action, which lets you send the HTML and text
body as plain Python strings.

When the list is larger than 10 work items, it is rendered in CSV format and included 
as an attachment to the email. When you use Amazon SES to send an attachment, you must 
use the `send_raw_email` service action and send the email in MIME format. 

## Delete the resources

To avoid charges, delete all the resources that you created for this tutorial.

If you created the example resources by using the AWS CDK or AWS CLI,
you can destroy the resources by following the instructions in the 
[README for the Aurora Serverless application](/resources/cdk/aurora_serverless_app/README.md). 

If you created your resources through the AWS Management Console, or modified them by 
running the app, you must use the console to delete them.

## Next steps

Congratulations! You have built a REST service that reads, writes, and archives 
work items that are stored in an Aurora Serverless database, and that uses 
Amazon SES to send email to a registered user.

## Additional information

* [Amazon Aurora User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/AuroraUserGuide/CHAP_AuroraOverview.html)
* [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html)
* [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
* [Amazon RDS Data Service Boto3 API Reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/rds-data.html)
* [Amazon SES Boto3 API Reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ses.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
