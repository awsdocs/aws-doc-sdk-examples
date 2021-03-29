### DetectFaces/DetectFaces.go

This example performs 1 tasks:

1. Reads the specified image from the bucket. Runs facial recognitions and returns the results. The example shows properties like position, age, emotion and gender of the faces detected.

`go run DetectFaces.go -b BUCKET -i IMAGE`

- _BUCKET_ is the name of the bucket where the images are saved.
- _IMAGE_ is the name of the JPG or PNG image along with the proper path in the bucket.