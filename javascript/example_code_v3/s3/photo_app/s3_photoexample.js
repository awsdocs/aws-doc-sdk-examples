/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-photo-album.html

Purpose:
s3_photoexample.js demonstrates how to manipulate photos in albums stored in an Amazon S3 bucket.

Inputs:
- REGION
- BUCKET_NAME
- IDENTITY_POOL_ID

Running the code:
s3_photoexample.js REGION BUCKET_NAME IDENTITY_POOL_ID
 */
// snippet-start:[s3.JavaScript.photoAlbumExample.complete]
// snippet-start:[s3.JavaScript.photoAlbumExample.config]

//   Replace BUCKET_NAME with the bucket name.
var bucketRegion = process.argv[2];
var albumBucketName = process.argv[3];
var IdentityPoolId = process.argv[4];

// Initialize the Amazon Cognito credentials provider
const credentials = fromCognitoIdentityPool({
  client: new CognitoIdentityClient({bucketRegion}),
  identityPoolId: IdentityPoolId});

// Create a new service object
const s3 = new S3({credentials,
  region, params: {Bucket: albumBucketName}});
// snippet-end:[s3.JavaScript.photoAlbumExample.config]

// snippet-start:[s3.JavaScript.photoAlbumExample.listAlbums]
const listAlbums = async() =>{
  try {
    const data = await s3.listObjects({Delimiter: '/'});
    const albums = data.CommonPrefixes.map(function(commonPrefix) {
      const prefix = commonPrefix.Prefix;
      const albumName = decodeURIComponent(prefix.replace("/", ""));
      return getHtml([
        "<li>",
        "<span onclick=\"deleteAlbum('" + albumName + "')\">X</span>",
        "<span onclick=\"viewAlbum('" + albumName + "')\">",
        albumName,
        "</span>",
        "</li>"
      ]);
    });
    const message = albums.length
        ? getHtml([
          "<p>Click on an album name to view it.</p>",
          "<p>Click on the X to delete the album.</p>"
        ])
        : "<p>You do not have any albums. Please Create album.";
    const htmlTemplate = [
      "<h2>Albums</h2>",
      message,
      "<ul>",
      getHtml(albums),
      "</ul>",
      "<button onclick=\"createAlbum(prompt('Enter Album Name:'))\">",
      "Create New Album",
      "</button>"
    ];
    document.getElementById("app").innerHTML = getHtml(htmlTemplate);
  }
  catch{
    console.log('There was an error listing your albums:', err.message);
    }
}
// snippet-end:[s3.JavaScript.photoAlbumExample.listAlbums]

// snippet-start:[s3.JavaScript.photoAlbumExample.createAlbum]
const listAlbums = async(albumName) =>{
  albumName = albumName.trim();
  if (!albumName) {
    return alert("Album names must contain at least one non-space character.");
  }
  if (albumName.indexOf("/") !== -1) {
    return alert("Album names cannot contain slashes.");
  }
  var albumKey = encodeURIComponent(albumName) + "/";
  try {
    const data = await s3.headObject({Key: albumKey});
  }
  catch (err) {
    if (!err) {
      return alert("Album already exists.");
    }
    if (err.code !== "NotFound") {
      return alert("There was an error creating your album: " + err.message);
    }
  }
    try{
      const data = await s3.putObject({Key: albumKey});
    }
    catch (err) {
        return alert("There was an error creating your album: " + err.message);
      }
      alert("Successfully created album.");
      viewAlbum(albumName);
    };
// snippet-end:[s3.JavaScript.photoAlbumExample.createAlbum]

// snippet-start:[s3.JavaScript.photoAlbumExample.viewAlbum]
const viewAlbum = async (albumName) => {
  var albumPhotosKey = encodeURIComponent(albumName) + "//";
  try {
    const data = await s3.listObjects({Prefix: albumPhotosKey});
    // 'this' references the AWS.Response instance that represents the response
    const href = this.request.httpRequest.endpoint.href;
    const bucketUrl = href + albumBucketName + "/";

    const photos = data.Contents.map(function(photo) {
      const photoKey = photo.Key;
      const photoUrl = bucketUrl + encodeURIComponent(photoKey);
      return getHtml([
        "<span>",
        "<div>",
        '<img style="width:128px;height:128px;" src="' + photoUrl + '"/>',
        "</div>",
        "<div>",
        "<span onclick=\"deletePhoto('" +
        albumName +
        "','" +
        photoKey +
        "')\">",
        "X",
        "</span>",
        "<span>",
        photoKey.replace(albumPhotosKey, ""),
        "</span>",
        "</div>",
        "</span>"
      ]);
    });
    const message = photos.length
        ? "<p>Click on the X to delete the photo</p>"
        : "<p>You do not have any photos in this album. Please add photos.</p>";
    const htmlTemplate = [
      "<h2>",
      "Album: " + albumName,
      "</h2>",
      message,
      "<div>",
      getHtml(photos),
      "</div>",
      '<input id="photoupload" type="file" accept="image/*">',
      '<button id="addphoto" onclick="addPhoto(\'' + albumName + "')\">",
      "Add Photo",
      "</button>",
      '<button onclick="listAlbums()">',
      "Back To Albums",
      "</button>"
    ];
    document.getElementById("app").innerHTML = getHtml(htmlTemplate);
  }
  catch{
      return alert("There was an error viewing your album: " + err.message);
    }
};
// snippet-end:[s3.JavaScript.photoAlbumExample.viewAlbum]

// snippet-start:[s3.JavaScript.photoAlbumExample.addPhoto]
const addPhoto = async(albumName) => {
  const files = document.getElementById("photoupload").files;
  if (!files.length) {
    return alert("Please choose a file to upload first.");
  }
  const file = files[0];
  const fileName = file.name;
  const albumPhotosKey = encodeURIComponent(albumName) + "//";

  const photoKey = albumPhotosKey + fileName;

  // Use S3 ManagedUpload class as it supports multipart uploads
  const upload = new AWS.S3.ManagedUpload({
    params: {
      Bucket: albumBucketName,
      Key: photoKey,
      Body: file,
      ACL: "public-read"
    }
  });

  var promise = upload.promise();

  promise.then(
    function(data) {
      alert("Successfully uploaded photo.");
      viewAlbum(albumName);
    },
    function(err) {
      return alert("There was an error uploading your photo: ", err.message);
    }
  );
};
// snippet-end:[s3.JavaScript.photoAlbumExample.addPhoto]

// snippet-start:[s3.JavaScript.photoAlbumExample.deletePhoto]
 const deletePhoto =async (albumName, photoKey)=> {
  try{
    const data = await s3.deleteObject({ Key: photoKey })
  }
 catch{
      return alert("There was an error deleting your photo: ", err.message);
    }
    alert("Successfully deleted photo.");
    viewAlbum(albumName);
  };
// snippet-end:[s3.JavaScript.photoAlbumExample.deletePhoto]

// snippet-start:[s3.JavaScript.photoAlbumExample.deleteAlbum]
const deleteAlbum = async(albumName) => {
  var albumKey = encodeURIComponent(albumName) + "/";
  try{
    const data = await s3.listObjects({ Prefix: albumKey });
    var objects = data.Contents.map(function(object) {
      return { Key: object.Key };
    });
  }
  catch{
    return alert("There was an error deleting your album: ", err.message);
  }
  try {
    const data = await s3.deleteObjects({
      Delete: {Objects: objects, Quiet: true}
    })
    alert("Successfully deleted album.");
    listAlbums();
  }
    catch{
      return alert("There was an error deleting your album: ", err.message);
    }
}

// snippet-end:[s3.JavaScript.photoAlbumExample.deleteAlbum]
// snippet-end:[s3.JavaScript.photoAlbumExample.complete]
