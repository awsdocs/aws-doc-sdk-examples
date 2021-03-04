### DetectLabels/DetectLabels.go

This example performs three tasks:

1. Saves the image in an Amazon Simple Storage Service (Amazon S3) bucket with an "uploads/" prefix.
1. Gets any ELIF information from the image and saves in the Amazon DynamoDB (DynamoDB) table.
1. Detects instances of real-world entities,
   such as flowers, weddings, and nature, within a JPEG or PNG image,
   and saves those instances as name/confidence pairs in the DynamoDB table.
1. Creates a thumbnail version of the image, no larger than 80 pixels by 80 pixels,
   and saves it in the same bucket with a "thumbs/" prefix and "thumb" suffix.

`go run DetectLabels.go -b BUCKET -t TABLE -f IMAGE`

- _BUCKET_ is the name of the bucket where the images are saved.
- _TABLE_ is the name of the bucket to which the item is copied.
- _IMAGE_ is the name of the JPG or PNG table.

The unit test accepts similar values in _config.json_.
