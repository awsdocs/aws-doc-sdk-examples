# AWS SDK for Rust code examples for Amazon QLDB

Amazon Quantum Ledger Database (Amazon QLDB) is a fully managed ledger database that provides a transparent, immutable, and cryptographically verifiable transaction log owned by a central trusted authority.

## create-ledger

This code example creates an Amazon QLDB ledger.

### Usage

```cargo run --bin create-ledger -l LEDGER [-r REGION] [-v]```

where:

- _LEDGER_ is the name of the ledger to create.
- _REGION_ is the region in which the client is created.
  If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- __-v__ enables displaying additional information.

## list-ledgers

This code example lists your Amazon QLDB ledgers.

### Usage

```cargo run --bin list-ledgers [-r REGION] [-v]```

where:

- _REGION_ is the region in which the client is created.
  If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- __-v__ enables displaying additional information.

## qldb-helloworld

This code example creates a low-level Amazon QLDB session against a ledger.

Avoid using the QldbSession API directly. Instead, use a higher-level driver, such as the Amazon QLDB Driver for Rust.

### Usage

cargo run --bin qldb-helloworld -l LEDGER [-r REGION] [-v]

where:

- _LEDGER_ is the name of the ledger to create the session against.
- _REGION_ is the region in which the client is created.
  If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- __-v__ enables displaying additional information.

## 