# Work item tracker web client

## Overview

Shows how to use [React](https://reactjs.org/) to create a web page that connects to a
REST service that lets you do the following:

* Get a list of active or archived work items.
* Mark active work items as archived.
* Add new work items to the list of active items.
* Send a report of work items to a specified email recipient.

## Sample REST applications

The web client is designed to send requests to one of the following sample applications.
Each sample application shows you how to use an AWS SDK to store work items using AWS
resources:

* [Creating a React and Spring REST application that queries Amazon Aurora Serverless data](../../../../javav2/usecases/Creating_Spring_RDS_%20Rest)
* [Creating the Amazon Aurora Serverless backend using the AWS SDK for PHP](../../../../php/cross_service/aurora_item_tracker)
* [Tracking work items in an Aurora Serverless database with the SDK for Python](../../../../python/cross_service/aurora_item_tracker)

## Running the client

### Prerequisites

To build and run the web client, you must have [Node.js](https://nodejs.org) installed
on your computer. The web client was built and tested by using Node.js 16.14.2.

Install all of the packages needed to run the web client by running the following at
a command prompt in the `resources/clients/react/item-tracker` folder:

```
npm install
```

#### Configure the REST endpoint

Each sample application hosts a REST endpoint. After you set up and run one of the
sample REST applications, configure the web client to send requests to the endpoint by
updating [src/config.json](src/config.json).

* Replace the default `BASE_URL` value with the endpoint provided by your sample
application.

#### Run the web client

Run the web client in development mode by running the following at a command prompt in
the `resources/clients/react/item-tracker` folder:

```
npm start
```

This opens [http://localhost:3000](http://localhost:3000) in your browser. When
the web client starts, it sends a GET request to the configured REST endpoint to
retrieve any existing active work items and displays them in a table.

### REST requests

The web client sends the following REST requests to the REST endpoint:

#### GET /items
#### GET /items/&lt;state>

Retrieves a list of work items. The default route returns all active items. Optional states are `archive`
for archived items and `all` for all items regardless of state.
*
* Items are expected to be a JSON array of items that each have the following fields:

    ```
    [{
      "id": "<item ID>",
      "name": "<user name>",
      "guide": "<guide name>",
      "description": "<item description>",
      "status": "<item status>"
    }, {
      ...more items...
    }]
    ```

#### POST /items

Adds a work item to the list.

* The body of the request is a single item in JSON format.

    ```
    {
      "name": "<user name>",
      "guide": "<guide name>",
      "description": "<item description>",
      "status": "<item status>"
    }
    ```

#### PUT /items/&lt;itemId>

Archives an active work item.

* `itemId` is the ID of the item to archive.

#### POST /report

Sends an email report of work items.

* The body of the request has the target email and the currently shown state in JSON format.

    ```
    {
        "email": "<recipient's email address>",
        "status": "<blank>|all|archive"
    }
    ```

### Run the tests

The web client unit tests do not require a REST endpoint and do not create or use any
AWS resources. Run all of the tests by running the following at a command prompt in
the `resources/clients/react/item-tracker` folder:

```
npm test
```

This launches the test runner in interactive watch mode.

## Additional resources

* [.NET cross-service examples](../../../../dotnetv3/cross-service/README.md)
* [Go cross-service examples](../../../../gov2/cross_service)
* [JavaScript cross-service examples](../../../../javascriptv3/example_code/cross-services)
* [Java cross-service examples](../../../../javav2/usecases)
* [Kotlin cross-service examples](../../../../kotlin/usecases/Readme.md)
* [Python cross-service examples](../../../../python/cross_service/README.md)
* [Rust cross-service examples](../../../../rustv1/cross_service/README.md)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
