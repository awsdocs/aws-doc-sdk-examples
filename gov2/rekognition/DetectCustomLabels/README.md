### DetectCustomLabels/DetectCustomLabels.go


This example reads the specified image from the bucket, 
and detects custom labels using the DetectCustomLabels model specified.
The number of detected labels is output, along with the label names and confidence values

`go run DetectFaces.go -b BUCKET -i IMAGE -arn MODEL_ARN -min-confidence MIN_CONFIDENCE`

- _BUCKET_ is the name of the bucket containing the image.
- _IMAGE_ is the name of the JPEG, JPG, or PNG image as the fully-qualified path in the bucket.
  Other formats are not supported.
- _MODEL_ARN_ is the Rekogniton Custom Labels model
- _MIN_CONFIDENCE_ is the minimum confidence parameter supplied to the DetectCustomerLabels API