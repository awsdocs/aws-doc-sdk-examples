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
