[]: # Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
[]: # SPDX - License - Identifier: Apache - 2.0
# Amazon DynamoDB code examples in C\#

This folder contains code examples for moving from SQL to the Amazon DynamoDB service,
as described in the Amazon DynamoDB Developer Guide at
[From SQL to NoSQL](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SQLtoNoSQL.html).

All of these code examples are written in C#, 
using the V3.5 version of the AWS SDK for .NET.
Getting the 3.5 version of the SDK is straightforward using the command line 
from the same folder as your ```.csproj``` file.
For example, to add a reference to the latest (V3.5) version of Amazon DynamoDB
to your project:

```
dotnet add package AWSSDK.DynamoDBv2
```

## Using async/await

See
[Migrating to Version 3.5 of the AWS SDK for .NET](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-v35.html) 
for details.

## Before you write any code

See
[Best Practices for Modeling Relational Data in DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/bp-relational-modeling.html)
in the Amazon DynamoDB Developer Guide for information about moving 
from a relational database to Amazon DynamoDB.

**IMPORTANT**

NoSQL design requires a different mindset than RDBMS design. 
For an RDBMS, you can create a normalized data model without thinking about access patterns. 
You can then extend it later when new questions and query requirements arise. 
By contrast, in Amazon DynamoDB, 
you shouldn't start designing your schema until you know the questions that it needs to answer. 
Understanding the business problems and the application use cases up front is absolutely essential.

### A simple example

Let's take a simple order entry system,
with just three tables: Customers, Orders, and Products.
- Customer data includes a unique ID, their name, address, email address.
- Orders data includes a unique ID, customer ID, product ID, order date, and status.
- Products data includes a unique ID, description, quantity, and cost.

You might end up with access patterns including the following:

- Get all orders for all customers within a given date range
- Get all orders of a given product for all customers
- Get all products below a given quantity

In a relational database, these might be satisfied by the following SQL queries:

```
select * from Orders where Order_Date between '2020-05-04 05:00:00' and '2020-08-13 09:00:00'
select * from Orders where Order_Product = '3'
select * from Products where Product_Quantity < '100'
```

Given the data in **customers.csv**, **orders.csv**, and **products.csv**,
these queries return (as .csv):

```
Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status
1,1,6,"2020-07-04 12:00:00",pending
11,5,4,"2020-05-11 12:00:00",delivered
12,6,6,"2020-07-04 12:00:00",delivered

Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status
4,4,3,"2020-04-01 12:00:00",backordered
8,2,3,"2019-01-01 12:00:00",backordered

Product_ID,Product_Description,Product_Quantity,Product_Cost
4,"2'x50' plastic sheeting",45,450
```

## Modeling data in Amazon DynamoDB

Amazon DynamoDB supports the following data types,
so you might have to create a new data model:

- Scalar types

  A scalar type can represent exactly one value.
  The scalar types are number, string, binary, Boolean, and null.

- Document types
 
  A document type can represent a complex structure with nested attributes,
  such as you would find in a JSON document.
  The document types are list and map.

- Set types

  A set type can represent multiple scalar values.
  The set types are string set, number set, and binary set.
  
Figure out how you want to access your data.
Many, if not most, stored procedures can be implemented using
[AWS Lambda](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Streams.Lambda.BestPracticesWithDynamoDB.html).

Determine the type of primary key you want:

- Partition key. This is a unique identifier for the item in the table.
  If you use a partition key, every key must be unique.
  The table we create in these code examples will contain 
  a partition key that uniquely identifies a record,
  which can be a customer, an order, or a product.

  Therefore, we'll create some global seconday indices
  to query the table.
  
- Partition key and sort key.
  In this case, you need not have a unique partition key,
  however, the combination of partition key and sort key must be unique.

We'll show you how to create all of these when you create a table,
and how to use them when you access a table.

## Modeling Customers, Orders, and Products in Amazon DynamoDB

Your Amazon DynamoDB schema to model these tables might look like this:

| Key | Data Type | Description |
| --- | --- | ---
| ID | String | The unique ID of the item
| Type | String | Customer, Order, or Product
| Customer_ID | Number | The unique ID of a customer
| Customer_Name | String | The name of the customer
| Customer_Address | String | The address of the customer
| Customer_Email | String | The email address of the customer
| Order_ID | Number | The unique ID of an order
| Order_Customer | Number | The Customer_ID of a customer
| Order_Product | Number | The Product_ID of a product
| Order_Date | Number | When the order was made
| Order_Status | String | The status (open, in delivery, etc.) of the order
| Product_ID | Number | The unique ID of a product
| Product_Description | String | The description of the product
| Product_Quantity | Number | How many are in the warehouse
| Product_Cost | Number | The cost, in cents, of one product

## Creating the example databases

We'll use three .csv (comma-separated value) files to define a set of customers,
orders, and products.
Then we'll load that data into a relational database and Amazon DynamoDB.
Finally, we'll run some SQL commands against the relational database,
and show you the corresponding queries or scan against an Amazon DynamoDB table.

The three sets of data are in:

- **customers.csv**, which defines six customers
- **orders.csv**, which defines 12 orders
- **products.csv**, which defines six products

## Default configuration

Every project has an **app.config** file that typically contains the following
configuration values:

```
key="Region" value="us-west-2"
key="Table" value="CustomersOrdersProducts"
```

Therefore, all of the projects that require a table name use the default table
**CustomersOrdersProducts** in the default Region **us-west-2**.
Similar values exist for most variable values in all projects.
This means there are few command-line arguments for any executable file.

## General code pattern

It's important that you understand the new async/await programming model in the
[AWS SDK for .NET](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide).

These code examples use the following NuGet packages:

- AWSSDK.Core, v3.5.0
- AWSSDK,DynamoDBv2, v3.5.0

## Unit tests

We use [moq4](https://github.com/moq/moq4) to create unit tests with mocked objects.
All of the code example projects have a companion unit test,
where the name of the unit test project is the same as the tested project,
with a **Test** suffix.

You can install the **moq** and **Extensions** unit testing Nuget packages with
the following commands:

```
dotnet add package moq
dotnet add package Microsoft.UnitTestFramework.Extensions
```

A typical unit test looks something like the following,
which tests a call to **CreateTableAsync** in the
**CreateTable** project:

```
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

using Moq;

using Xunit;

namespace DynamoDBCRUD
{        
    public class CreateTableTest
    {
        readonly string _tableName = "testtable";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.CreateTableAsync(
                It.IsAny<CreateTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<CreateTableRequest, CancellationToken>((request, token) =>
                {
                    if (!string.IsNullOrEmpty(_tableName))
                    {
                        bool areEqual = _tableName == request.TableName;
                        Assert.True(areEqual, "The provided table name is not the one used to create the table");
                    }
                })
                .Returns((CreateTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new CreateTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckCreateTable()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await CreateTable.MakeTableAsync(client, _tableName);

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT create table " + _tableName);
        }
    }
}
```

To run this test,
navigate to the **CreateTableTest** folder and run:

```
dotnet test
```

If you want more information, run:

```
dotnet test -l "console;verbosity=detailed"
```

## Creating a table

Use the **CreateTable** project to create a table.

The default table name is defined as **Table**
and the default Region is defined as **Region**
in **app.config**.

You can create this table as an on-demand table,
which means that read/write capacity is not fixed
and you are billed by what you use,
by replacing:

```
ProvisionedThroughput = new ProvisionedThroughput
{
    ReadCapacityUnits = 10,
    WriteCapacityUnits = 5
}
```

with:

```
BillingMode = BillingMode.PAY_PER_REQUEST
```

See 
[Read/Write Capacity Mode](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.ReadWriteCapacityMode.html)
in the *Amazon DynamoDB Developer Guide* for details.

## Adding an item to the table

Use the **AddItem** project to add an item to a table.

The default table name is defined as **Table**
and the default Region is defined as **Region**
in **app.config**.

It requires the following options:

- ```-k``` *KEYS*, where *KEYS* is a a comma-separated list of keys
- ```-v``` *VALUES*, where *VALUES* is a a comma-separated list of values

There must be the same number of keys as values.

- If the item is a customer, the schema should match that in **customers.csv**,
  with one additional key, ID, which defines the partition ID for the item.
- If the item is an order, the schema should match that in **orders.csv**,
  with one additional key, ID, which defines the partition ID for the item*.
- If the item is a product, the schema should match that in **products.csv**,
  with one additional key, ID, which defines the partition ID for the item*.

It's up to you to determine the appropriate partition key value (ID).
If you provide the same value as an existing table item,
the values of that item are overwritten.

## Uploading items to a table

The **AddItems** project incorporates data from three comma-separated value 
(.csv) files to populate a table.

The default table name is defined as **Table**,
the default Region is defined as **Region**,
and the default table names are defined as
**Customers**, **Orders**, and **Products**
in **app.config**.

## Managing indexes

Global secondary indices give you the ability to treat a set of 
Amazon DynamoDB table keys as if they were a separate table.

### Creating an index

Use the **CreateIndex** project to create an index.

The default table name is defined as **Table**,
and the default Region is defined as **Region**
in **app.config**.

It requires the following command-line options:

- ```-i``` *INDEX-NAME*, where *INDEX-NAME* is the name of the index
- ```-m``` *MAIN-KEY*, where *MAIN-KEY* is the partition key of the index
- ```-k``` *MAIN-KEY-TYPE*, where *MAIN-KEY-TYPE* is one of string or number
- ```-s``` *SECONDARY-KEY*, where *SECONDARY-KEY* is the sort key of the index
- ```-t``` *SECONDARY-KEY-TYPE*, where *SECONDARY-KEY-TYPE* is one of string or number

To create a global secondary index (GSI) for the three queries we made in
*A simple example*,
execute the following commands:

```
CreateIndex.exe -i OrderDate       -m Area -k string -s Order_Date       -t number
CreateIndex.exe -i ProductOrdered  -m Area -k string -s Order_Product    -t number
CreateIndex.exe -i LowProduct      -m Area -k string -s Product_Quantity -t number
```

Note that you cannot execute these commands one after another.
You must wait until one GSI is created before you can attempt to create another GSI.
We recommend you use the Amazon DynamoDB console to monitor the progress
of creating a GSI to avoid errors.

## Reading data from a table

You can read data from an Amazon DynamoDB table using a number of techniques.

- By the item's primary key
- By searching for a particular item or items based on the value of one or more keys

## Modifying a table item

Use the **UpdateItem** project to modify the status of an order in the table.

The default table name is defined as **Table**,
and the default Region is defined as **Region**
in **app.config**.

It takes the following options:

- ```-i``` *ID*, where *ID* is the value of the order's ORDER_ID attribute
- ```-s``` *STATUS*, where *STATUS* is the new status value (backordered, delivered, delivering, or pending)

The update sets the **Order_Status** field of the item.
It does not check whether the *ID* applies to a customer, order, or product.

## Modifying a table item using the DynamoDB DataModel

Use the **UpdateItemDataModel** project to modify the status of an order in the table
using the DynamoDBContext class.

The default table name is defined as **Table**,
and the default Region is defined as **Region**
in **app.config**.

It takes the following options:

- ```-i``` *ID*, where *ID* is the value of the order's ORDER_ID attribute
- ```-s``` *STATUS*, where *STATUS* is the new status value (backordered, delivered, delivering, or pending)

The update sets the **Order_Status** field of the item.
It does not check whether the *ID* applies to a customer, order, or product.

### Running the unit test

The **UpdateItemDataModelTest** project is the unit test for the **UpdateItemDataModel** project.
It requires that you use the local, in-memory version of the DynamoDB service.
You can find further information on downloading and installing DynamoDB Local
[Setting Up DynamoDB Local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html).

Invoke the DynamoDB Local with the following command line from the folder that contains
**DynamoDBLocal.jar** and the **DynamoDBLocal_lib** folder:

```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory
```

## Deleting an item from a table

Use the **DeleteItem** project to delete an item from the table.

The default table name is defined as **Table**,
and the default Region is defined as **Region**
in **app.config**.

It takes the following option:

- ```-p``` *PARTITION*, where *PARTITION* is the value of the partition key
- ```-s``` *AREA*, where *AREA* is **Customer**, **Order**, or **Product**.

If you provide an *AREA* value that does not match that of the item,
the example silently fails to delete the item from the table.

## Deleting items from a table

Use the **** project to delete an item from the table.

The default table name is defined as **Table**,
and the default Region is defined as **Region**
in **app.config**.

- ```-a``` *AREA*, where *AREA* is **Customer**, **Order**, or **Product**.
- ```-i``` *IDS*, where *IDS* is a list of ID values, separated by spaces; all ID values must be for the associated *AREA*.

## Deleting a table

Use the **DeleteTable** project to delete a table.

The default table name is defined as **Table**,
and the default Region is defined as **Region**
in **app.config**.
