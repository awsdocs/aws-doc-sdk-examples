### ScanItemsv2.go

This example retrieves the Amazon DynamoDB items with a rating above a specified value
in a specified year.

`go run ScanItemsv2.go -t TABLE -r RATING -y YEAR`

- _TABLE_ is the name of the table.
- _RATING_ is the rating of the item, from 0.0 to 10.0.
- _YEAR_ is the year of the item, which must be greater than 1900.

The unit test accepts similar values in _config.json_.
