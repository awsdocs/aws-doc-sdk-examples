### DescribeTable.go

This example lists the following properties of a DynamoDB table.

- Number of items
- Size, in bytes
- Status, such as Active

`go run DescribeTable.go -t TABLE`

- _TABLE_ is the name of the table.

The unit test accepts a similar value in _config.json_.
