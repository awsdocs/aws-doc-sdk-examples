# Developer guide

Elros is an opinionated boilerplate designed to speed up your development of a website that
integrates with Amazon Web Services.

## Features

- Authentication
- Common components
- Design system
- Development/build tooling

## Prerequisites

1. Install the latest Node.js LTS.
1. Install git.

## Get started

1. Copy and paste the `elros` directory to your workspace.
1. Rename your directory.
1. Initialize a new `git` repository with `git init`.

## Add new components

When you have a copy of the boilerplate code you can treat it like
any other React application. Add new components, change the folder
structure, make it yours.

## Run

1. Run `npm i` from the same directory as this readme.
1. Run `npm run dev`.

## Build

1. Run `npm run build`.

## Understand the features

### Authentication

Authentication can be difficult. AWS offers tools like `Amplify` and `amazon-cognito-identity-js` to
assist with authentication. Even with those tools, it's still a steep learning curve. This boilerplate
offers the following to get you going faster:

- A state-management system.
- Amazon Cognito hosted UI

The state-management system responds to redirects from Cognito Hosted UI. It tracks the current
user and tokens. Cognito hosted UI is an Amazon Cognito OAuth solution. The LoginNavigation component
provides a link to the hosted UI.

### Common components

- [FileUpload](./src/FileUpload.tsx) - A button the triggers the display of an upload modal.

#### Configure Amazon Cognito

The CognitoAuthManager requires a user pool. The user pool ID and client ID environment variables are set in `.env`

```
# Amazon Cognito configuration. Set these values if you want to enable authentication.
VITE_COGNITO_USER_POOL_ID=
VITE_COGNITO_USER_POOL_CLIENT_ID=
```

1. [Create](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-pool-as-user-directory.html) an Amazon Cognito user pool, or use an existing one.
2. Set the value of `VITE_COGNITO_USER_POOL_ID` to the user pool ID. You can find the user pool ID
   in the AWS Management Console for Amazon Cognito.
3. Set the value of `VITE_COGNITO_USER_POOL_CLIENT_ID` to the user pool ID. You can find the user pool ID
   in the AWS Management Console for Amazon Cognito under the "App integration" tab.

### Design system

This boilerplate uses the [Cloudscape Design System](https://cloudscape.design/get-started/guides/introduction/)
as a component library. Cloudscape was built for and is used by Amazon Web Services (AWS) products and services.

### Development/build tooling

[Vite](https://vitejs.dev/) is used to run the project locally and bundle it for deployment.

Vite also enables the use of `.env` described in [Configure Amazon Cognito](#configure-amazon-cognito). If you want
to test with certain environment variables, but not commit them, you can use [`.env.local`](https://vitejs.dev/guide/env-and-mode.html#env-files).
