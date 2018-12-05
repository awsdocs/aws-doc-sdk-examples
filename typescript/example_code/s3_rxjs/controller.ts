// Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of the
// License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied. See the License for the specific
// language governing permissions and limitations under the License.

// To install stable release of RxJS: npm install rxjs
// To install TypeStrong/ts-node: npm install -D ts-node
// To install AWS SDK: npm install aws-sdkservice

// This CRUD API can be used in an Angular 2+ service
// To create Angular service: ng generate service [Name of service]
// Please refer to https://angular.io/tutorial/toh-pt4 for more information on Angular services
// Can be implemented inside ReactJS or React Native components to easily manage asynchronous data streams 
// Please see https://hackernoon.com/what-happens-when-you-use-rxjs-in-react-11ae5163fc0a for more information

import * as AWS from 'aws-sdk/global';
import * as S3 from 'aws-sdk/clients/s3';
import { Observable } from 'rxjs';
import { of } from 'rxjs';

class S3Controller {

  FOLDER = '/* s3-folder-name */'
  BUCKET = '/* s3-bucket-name */';

  private getS3Bucket(): any {
    const bucket = new S3(
      {
        accessKeyId: '/* access key here */',
        secretAccessKey: '/* secret key here */',
        region: '/* region here */'
      }
    );

    return bucket;
  }

  uploadFile(file) {
    const bucket = new S3(
      {
        accessKeyId: '/* access key here */',
        secretAccessKey: '/* secret key here */',
        region: '/* region here */'
      }
    );

    const params = {
      Bucket: this.BUCKET,
      Key: this.FOLDER + file.name,
      Body: file,
      ACL: 'public-read'
    };

    bucket.upload(params, function (err, data) {
      if (err) {
        console.log('There was an error uploading your file: ', err);
        return false;
      }
      console.log('Successfully uploaded file.', data);
      return true;
    });
  }

  getFiles(): Observable<Array<FileUpload>> {
    const fileUploads = [];

    const params = {
      Bucket: this.BUCKET,
      Prefix: this.FOLDER
    };

    this.getS3Bucket().listObjects(params, function (err, data) {
      if (err) {
        console.log('There was an error getting your files: ' + err);
        return;
      }

      console.log('Successfully get files.', data);

      const fileDatas = data.Contents;

      fileDatas.forEach(function (file) {
        fileUploads.push(new FileUpload(
          file.Key,
          'https://s3.amazonaws.com/' + params.Bucket + '/' + file.Key
        ));
      });
    });

    return of(fileUploads);
  }

  deleteFile(file: FileUpload) {
    const params = {
      Bucket: this.BUCKET,
      Key: file.name
    };

    this.getS3Bucket().deleteObject(params, function (err, data) {
      if (err) {
        console.log('There was an error deleting your file: ', err.message);
        return;
      }
      console.log('Successfully deleted file.');
    });
  }
}
