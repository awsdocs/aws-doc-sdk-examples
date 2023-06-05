# Example applications

This directory contains a collection of production-like examples designed to show the AWS SDKs in context.

## Example contents

Every example is different, but each will have the following:

- Automatic/semi-automatic resource creation - AWS CloudFormation (AWS CFN) and AWS Cloud Development Kit (AWS CDK) provide the scaffolding for the applications.
- Language implementations - Each example will have at least one feature that is implemented in multiple languages.
- A frontend. Many examples have a frontend component.
- README.md - A high level overview of the example with instructions for deployment.
- ARCHITECTURE.md - A operations level explanation of the different application components.
- SPECIFICATION.md - The business logic of the application.
- DEVELOPMENT.md - A guide on extending the application with another language-specific implementation. This includes a README.md template.
- DESIGN.md - A lightly-edited history of the decisions concerning the development of this example.

## List of examples

| Name                | Path                                         | Supported languages | Status                                          |
| ------------------- | -------------------------------------------- | ------------------- | ----------------------------------------------- |
| Photo Asset Manager | [photo-asset-manager](./photo-asset-manager) | Java                | ![[]](https://img.shields.io/badge/-wip-yellow) |
