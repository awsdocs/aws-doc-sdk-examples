/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

export const MESSAGES = {
  introduction: `Welcome to the demonstration of How to Build and Manage a Resilient Service!
For this demo, we'll use an AWS SDK to create several AWS resources to set up a load-balanced web service endpoint and explore some ways to make it resilient against various kinds of failures.
Some of the resources create by this demo are:
  - A DynamoDB table that the web service depends on to provide book, movie, and song recommendations.
  - An EC2 launch template that defines EC2 instances that each contain a Python web server.
  - An EC2 Auto Scaling group that manages EC2 instances across several Availability Zones.
  - An Elastic Load Balancing (ELB) load balancer that targets the Auto Scaling group to distribute requests.`,
  confirmDeployment: "Are you ready to deploy resources?",
  creatingTable: "Creating table: ${TABLE_NAME}",
  createdTable: "Created table: ${TABLE_NAME}",
  destroy: "Destroy resources?",
  deletedTable: "Deleted table: ${TABLE_NAME}",
  deleteTableError: "Error deleting table: ${TABLE_NAME}",
};
