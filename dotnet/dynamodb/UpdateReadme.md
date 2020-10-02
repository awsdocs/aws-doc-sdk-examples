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
It requires that you use the local, in memory version of the DynamoDB service.
You can find further information on downloading and installing DynamoDB Local
[here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html).

Invoke the DynamoDB Local with the following command line from the folder that contains
**DynamoDBLocal.jar** and the **DynamoDBLocal_lib** folder:

```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb -inMemory
```
