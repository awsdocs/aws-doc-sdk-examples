# Get started with Node.js

This guide shows you how to initialize an NPM package, add a service client to your package, and use the JavaScript SDK to call a service action.

## ⚠ Important

- Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).

## Scenario

Create a new NPM package with one main file that does the following:

- Creates an Amazon Simple Storage Service (Amazon S3) bucket.
- Puts an object in the bucket.
- Reads the object.
- Confirms if the user wants to delete resources.

### Prerequisites

Before you can run the example, you must do the following:

- Configure your [SDK authentication](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-your-credentials.html)
- [Install Node.js](https://nodejs.org/en/download)

### Step 1: Set up the package structure

1. Create a new folder to contain the package.
2. From the command line, navigate to the new folder.
3. Run `npm init -y`. This creates a default `package.json` file.
4. Add `"type": "module"` to the `package.json`. This tells Node we're using modern ESM syntax.

The final package.json should look similar to this:

```json
{
  "name": "example-javascriptv3-get-started-node",
  "version": "1.0.0",
  "description": "This guide shows you how to initialize an NPM package, add a service client to your package, and use the JavaScript SDK to call a service action.",
  "main": "index.js",
  "scripts": {
    "test": "vitest run **/*.unit.test.js"
  },
  "author": "Corey Pyle <corepyle@amazon.com>",
  "license": "Apache-2.0",
  "dependencies": {
    "@aws-sdk/client-s3": "^3.420.0"
  },
  "type": "module"
}
```

### Step 2: Install the Amazon S3 client package

1. Run `npm i @aws-sdk/client-s3`.

### Step 3: Add necessary imports and SDK code

1. Refer to [index.js](./index.js).

### Step 4: Run the example

1. `node index.js`
2. Choose whether to empty and delete the bucket.
3. If you don't delete the bucket, be sure to manually empty and delete it later.
