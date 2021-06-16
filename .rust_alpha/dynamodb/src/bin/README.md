# DynamoDB Hello World Example
This repo has a simple hello-world example for DynamoDB that will create a table if it doesn't exist & list tables present in the database.

By default, the code is written to target DynamoDB local—A docker compose file is provided for convenience. Usage:

```
docker-compose up -d
cargo run
```

The example can also be updated to run against production DynamoDB if credentials are provided.
