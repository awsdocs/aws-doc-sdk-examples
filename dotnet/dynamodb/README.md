# Amazon DynamoDB code examples in C#

This folder contains code examples for moving from SQL to NoSQL, 
specifically Amazon DynamoDB,
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

Read the 
[Migrating to Version 3.5 of the AWS SDK for .NET](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-v35.html) 
topic for details.

## Before you write any code

Read the
[Best Practices for Modeling Relational Data in DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/bp-relational-modeling.html)
topic in the Amazon DynamoDB Developer Guide for information about moving 
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

Given the data in *customers.csv*, *orders.csv*, and *products.csv*,
these queries return (as CSV):

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

- Scalar Types

  A scalar type can represent exactly one value.
  The scalar types are number, string, binary, Boolean, and null.

- Document Types
 
  A document type can represent a complex structure with nested attributes,
  such as you would find in a JSON document.
  The document types are list and map.

- Set Types

  A set type can represent multiple scalar values.
  The set types are string set, number set, and binary set.
  
Figure out how you want to access your data.
Many, if not most, stored procedures can be implemented using
[AWS Lambda](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Streams.Lambda.BestPracticesWithDynamoDB.html).

Determine the type of primary key you want:

- Partition key, which is a unique identifier for the item in the table.
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

Your Amazon DynamoDB schema to model these tables might look like:

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

We'll use three CSV (comma-separated value) files to define a set of customers,
orders, and products.
Then we'll load that data into a relational database and Amazon DynamoDB.
Finally, we'll run some SQL commands against the relational database,
and show you the corresponding queries or scan against an Amazon DynamoDB table.

The three sets of data are in:

- *customers.csv*, which defines six customers
- *orders.csv*, which defines 12 orders
- *products.csv*, which defines six products

## Default configuration

Every project has an *app.config* file that typically contains the following
configuration values:

```
key="Region" value="us-west-2"
key="Table" value="CustomersOrdersProducts"
```

Therefore, all of the projects that require a table name use the default table
**CustomersOrdersProducts** in the default region **us-west-2**.
Similar values exist for most variable values in all projects.
This means there are few command-line arguments for any executable.

## General code pattern

It's important that you understand the new async/await programming model in the
[AWS SDK for .NET](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide).

These code examples use the following NuGet packages:

- AWSSDK.Core, v3.5.0
- AWSSDK,DynamoDBv2, v3.5.0

## Listing all of the tables in a region

Use the **ListTables** project to list all of the tables in a region.

The default region is defined as **Region** in *app.config*.

## Listing the items in a table

Use the **ListItems** project to list the items in a table.

The default table name is defined as **Table**
and the default region is defined as **Region**
in *app.config*.

## Listing all of the items in a table

The **ListItems** project lists all of the items in a table.

The default table name is defined as **Table**,
and the default region is defined as **Region**
in *app.config*.

## Reading data from a table

You can read data from an Amazon DynamoDB table using a number of techniques.

- By the item's primary key
- By searching for a particular item or items based on the value of one or more keys

### Reading an item using its primary key

Use the **GetItem** project 
to retrieve information about the customer, order, 
or product with the given primary key.

The default table name is defined as **Table**,
and the default region is defined as **Region**
in *app.config*.

It requires the following command-line option:

- ```-i``` *ID*, which is the partition ID of the item in the table.

### Getting orders within a given date range

The **GetOrdersInDateRange** and **GetOrdersInDateRangeGSI** projects retrieve
a list of all orders for all customers within the date range from *start* to *end*.
The first project scans the table,
the second uses a global secondary index (GSI) to query the table.

The default table name is defined as **Table**,
the default region is defined as **Region**,
*start* is defined by **StartTime**,
and *end* is defined by **EndTime**
in *app.config*.
In addition,
the *app.config" file for the **GetLowProductStockGSI** 
project includes **Index**, which defines the GSI.

Note the required format of the dates: **yyyy-MM-dd HH:mm:ss**.

### Getting orders for a given product

The **GetOrdersForProduct** and **GetOrdersForProductGSI** projects retrieve
a list of all orders of the product with product ID *productId* for all customers.
The first project scans the table,
the second uses a global secondary index (GSI) to query the table.

The default table name is defined as **Table**,
the default region is defined as **Region**,
and *productId* is defined by **ProductID**
in *app.config*.
In addition,
the *app.config" file for the **GetLowProductStockGSI** 
project includes **Index**, which defines the GSI.

### Getting products with fewer than a given number in stock

The **GetLowProductStock** and **GetLowProductStockGSI** 
projects retrieve a list of 
all products with fewer than *minimum* items in stock.
The first project scans the table,
the second uses a global secondary index (GSI) to query the table.

The default table name is defined as **Table**,
the default region is defined as **Region**,
and *minimum* is defined by **Minimum**
in *app.config*.
In addition,
the *app.config" file for the **GetLowProductStockGSI** 
project includes **Index**, which defines the GSI.
