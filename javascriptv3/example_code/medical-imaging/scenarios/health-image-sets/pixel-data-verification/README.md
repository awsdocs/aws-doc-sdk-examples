# AWS HealthImaging Pixel Data Verification

This example demonstrates how to use the AWS HealthImaging (AHI) Pixel Data Verification feature to ensure the image you
decoded matches the original DICOM P10 Image.

## Dependencies

This example is written in JavaScript and uses NodeJS. It depends on the following libraries:

-   [AWS SDK for JavaScript v3](https://github.com/aws/aws-sdk-js-v3)
-   [CRC32 Algorithm](https://www.npmjs.com/package/crc-32)
-   [HTJ2K Decoding](https://github.com/chafey/openjphjs)

## Deployment

1. Check out the project.
2. Change the current directory to this project: `cd pixel-data-verification`
3. Install dependencies: `npm install`

## How to use

1. Create a datastore as per the AHLI developer guide.
2. Import the DICOM file `test/fixtures/CT1_UNC` as per the AHLI developer guide.
3. Retrieve the ImageSetId from the job output manifest file in S3.
4. Run this tool passing in your datastore (from step 1), ImageSetId (from step 3), and series and SOP instance UIDs listed below.

```
$ node index.js $DATASTOREID $IMAGESETID 1.3.6.1.4.1.5962.1.3.1.1.20040826185059.5457 1.3.6.1.4.1.5962.1.1.1.1.1.20040826185059.5457
CRC32 match!
```
