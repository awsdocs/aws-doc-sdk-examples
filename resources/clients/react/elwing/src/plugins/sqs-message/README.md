# Amazon SQS messaging demo

## Overview

Shows how to use [React](https://reactjs.org/) to create a web page that connects to a
REST service that lets you do the following:

- Display a list of messages from an Amazon Simple Queue Service (Amazon SQS) queue
- Send a message
- Poll for messages
- Purge messages

## Sample REST applications

The web client is designed to send requests to one of the following sample applications.
Each sample application shows you how to use an AWS SDK to manage SQS queues:

- [Create a React and Spring REST application that handles Amazon SQS messages](../../../../javav2/usecases/creating_message_application/README.md)

## Run the client

### Prerequisites

To build and run the web client, you must install [Node.js](https://nodejs.org)
on your computer. The web client was built and tested by using Node.js 18.7.0.

Install all of the packages needed to run the web client by running the following at the root of this project:

```
npm install
```

#### Configure the REST endpoint

Each sample application hosts a REST endpoint. After you set up and run one of the
sample REST applications, configure the web client to send requests to the endpoint by
updating [src/config.json](src/config.json).

- Replace the default `BASE_URL` value with the endpoint provided by your sample
  application.

#### Run the web client

Run the web client in development mode by running the following at the root of this project:

```
npm start
```

This opens [http://localhost:3000](http://localhost:3000) in your browser. When
the web client starts, it begins polling for new messages.

### REST requests

The web client sends the following REST requests to the REST endpoint:

#### POST /chat/add?user={username}&message={message}

Sends a new message to the SQS queue configured in the sample application. The _username_ and _message_ fields
are plaintext, and can be any value.

#### GET /chat/purge

Purges your SQS queue.

### Run the tests

The web client unit tests do not require a REST endpoint and do not create or use any
AWS resources. Run all of the tests by running the following at the root of this project:

```
npm test
```

This launches the test runner in interactive watch mode.

## Additional resources

- [.NET cross-service examples](../../../../dotnetv3/cross-service/README.md)
- [Go cross-service examples](../../../../gov2/cross_service)
- [JavaScript cross-service examples](../../../../javascriptv3/example_code/cross-services)
- [Java cross-service examples](../../../../javav2/usecases)
- [Kotlin cross-service examples](../../../../kotlin/usecases/Readme.md)
- [Python cross-service examples](../../../../python/cross_service/README.md)
- [Rust cross-service examples](../../../../rust_dev_preview/cross_service/README.md)
- [C++ cross-service examples](../../../../../../../cpp/example_code/cross-service/README.md)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
