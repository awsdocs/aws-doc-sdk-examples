# Developer guide

This is the front-end of the Photo Asset Manager (PAM) cross-service example. You can
run this in isolation, but it is meant to be deployed using the companion [AWS CDK script](../cdk/README.md).

## PAM Usage

When you build PAM, the CDK outputs the URL for the frontend. You can use this URL to access the frontend.

You will also receive two emails when you build the app. One email contains a temporary password, and the other will contains a link to confirm a subscription to an SNS topic. You must confirm the subscription to receive the link to the zipped files.

### Sign in

1. Click the sign in button in the top right corner.
   <img src="./screenshots/sign_in_1.png">
2. Enter the email you provided when building PAM and the temporary password you received.
   <img src="./screenshots/sign_in_2.png">
3. Upload a photo.
   <img src="./screenshots/upload.png">
4. Click the refresh button until you see labels appear.
5. Select one or more labels and click on "download".
   <img src="./screenshots/download.png">
6. After a few minutes you'll receive another email with a link to download the zipped files. The link is broken up by some whitespace. SNS does this with long strings. Trim the whitespace and paste the link into your browser.
   <img src="./screenshots/link.png">

## Prerequisites

1. Install the latest Node.js LTS.
1. Install git.

## Get started

1. Copy and paste the `elros` directory to your workspace.
1. Rename your directory.
1. Initialize a new `git` repository with `git init`.

## Run

1. Run `npm i` from the same directory as this readme.
1. Run `npm run dev`.

## Build

1. Run `npm run build`.
