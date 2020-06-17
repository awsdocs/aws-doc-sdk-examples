/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-photos-view.html
Purpose:
s3_photoexample.js demonstrates how to manipulate photos in albums stored in an Amazon S3 bucket.

Inputs:
- REGION
- BUCKET_NAME
- IDENTITY_POOL_ID

Running the code:
s3_photoviewer.js REGION BUCKET_NAME IDENTITY_POOL_ID

 */
// snippet-start:[s3.JavaScript.s3_PhotoViewer.complete]
// snippet-start:[s3.JavaScript.s3_PhotoViewer.config]
// **DO THIS**:
//   Replace BUCKET_NAME with the bucket name.
var albumBucketName = process.argv[2];
var bucketRegion = process.argv[3];
var IdentityPoolId = process.argv[4];

// Initialize the Amazon Cognito credentials provider
const credentials = fromCognitoIdentityPool({
  client: new CognitoIdentityClient({bucketRegion}),
  identityPoolId: IdentityPoolId});

// Create a new service object
const s3 = new S3({credentials,
  region, params: {Bucket: albumBucketName}});

// A utility function to create HTML.
const getHtml = (template) => {
  return template.join('\n');
}
// snippet-end:[s3.JavaScript.s3_PhotoViewer.config]
// snippet-start:[s3.JavaScript.s3_PhotoViewer.listAlbums]

// List the photo albums that exist in the bucket.
const listAlbums = async() => {
  try {
    const data = await s3.listObjects({Delimiter: '/'});
    const albums = data.CommonPrefixes.map(function (commonPrefix) {
      const prefix = commonPrefix.Prefix;
      const albumName = decodeURIComponent(prefix.replace('/', ''));
      return getHtml([
        '<li>',
        '<button style="margin:5px;" onclick="viewAlbum(\'' + albumName + '\')">',
        albumName,
        '</button>',
        '</li>'
      ]);
    });
    const message = albums.length ?
        getHtml([
          '<p>Click on an album name to view it.</p>',
        ]) :
        '<p>You do not have any albums. Please Create album.';
    const htmlTemplate = [
      '<h2>Albums</h2>',
      message,
      '<ul>',
      getHtml(albums),
      '</ul>',
    ]
    document.getElementById('viewer').innerHTML = getHtml(htmlTemplate);
  } catch (err) {
    console.log('There was an error listing your albums:', err.message);
  }
};
window.listAlbums = listAlbums
// snippet-end:[s3.JavaScript.s3_PhotoViewer.listAlbums]

// snippet-start:[s3.JavaScript.s3_PhotoViewer.viewAlbum]
// Show the photos that exist in an album.
const viewAlbum = async (albumName) => {
  console.log('viewing')
  try {
    const albumPhotosKey = encodeURIComponent(albumName) + '/';
    const data = await s3.listObjects({Prefix: albumPhotosKey})
    const albums = data.CommonPrefixes.map(function(commonPrefix) {
      const prefix = commonPrefix.Prefix;
      const albumName = decodeURIComponent(prefix.replace('/', ''));
      return getHtml([
        '<li>',
        '<button style="margin:5px;" onclick="viewAlbum(\'' + albumName + '\')">',
        albumName,
        '</button>',
        '</li>'
      ]);
    });
    const message = albums.length ?
        getHtml([
          '<p>Click on an album name to view it.</p>',
        ]) :
        '<p>You do not have any albums. Please Create album.';
    const htmlTemplate = [
      '<h2>Albums</h2>',
      message,
      '<ul>',
      getHtml(albums),
      '</ul>',
    ]
    document.getElementById('viewer').innerHTML = getHtml(htmlTemplate);
  }
  catch (err) {
    console.log('There was an error listing your albums:', err.message);
  }
}
window.viewAlbum = viewAlbum
// snippet-end:[s3.JavaScript.s3_PhotoViewer.viewAlbum]
// snippet-end:[s3.JavaScript.s3_PhotoViewer.complete]
