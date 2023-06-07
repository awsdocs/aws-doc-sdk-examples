# Photo Asset Manager (PAM)

The Photo Asset Management (PAM) example app uses Amazon Rekognition to categorize images, which are stored with Amazon S3 Intelligent-Tiering for cost savings. Users can upload new images. Those images are analyzed with label detection and the labels are stored in an Amazon DynamoDB table. Users can later request a bundle of images matching those labels. When images are requested, they will be retrieved from Amazon S3, zipped, and the user is sent a link to the zip.

[Tech spec](./SPECIFICATION.md)
[Development](./DEVELOPMENT.md)
