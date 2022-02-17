### DetectFaces/DetectFaces.go

This example reads the specified image from the bucket, 
runs facial recognition on the faces in the image, 
and display attributes of each face, 
such as position, age, emotion, and gender.

`go run DetectFaces.go -b BUCKET -i IMAGE`

- _BUCKET_ is the name of the bucket containing the image.
- _IMAGE_ is the name of the JPEG, JPG, or PNG image as the fully-qualified path in the bucket.
  Other formats are not supported.

The unit test accepts similar values in _config.json_.