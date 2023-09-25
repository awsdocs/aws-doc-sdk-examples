# Photo Asset Management Design

_Photo Asset Management went through a typical engineering design process. As an educational team, it began by brainstorming possible applications that would show using Rekognition & S3 lifecycles in a realistic scenario. The team selected a user persona and critical user journeys that would benefit from those services. The team then made engineering design and architectural decisions driven by the journeys. This is a lightly edited summary of the design decisions that were made while developing PAM._

## Personas 💖

Dan is a casual photographer (shooting in jpeg) who focuses on nature photography. He also takes some ad-hoc photos of his friends and family. He wants a website where he can upload all of his photos, store them indefinitely, and download bundles of images that match nature-related tags (“forest”, “lake”, “mountain”, etc). Dan is the end user of this application.

## User Flow

Dan visits PAM and completes “upload photos” flow. Dan sees a loading spinner while the photos are being analyzed by Rekognition. When analysis completes, the UI shows a list of tags & a count of photos with each tag. Dan selects the tag “mountain (32)”, adds his phone number or email address, and clicks Download. Dan later receives a message (text or email) with a link to a zip file containing his images. (The link is only valid for a certain amount of time.)

## User Stories 📖

1. Dan needs to upload a large number of “1024x768” jpeg photos. This is complete when 100s images are in S3 ~and have been moved to Glacier storage tier~, with metadata in DynamoDB.
   1. Uploads go directly to s3.
      1. Upload a couple images through PAM.
         1. Backends will need to handle file uploads
         2. Upload as form or base64 json?
      2. ~Copy hundreds of images from a bucket~ _Scrapped this idea in favor of using s3 sync, as syncing from one bucket to another is better handled by a dedicated tool & does not add much value to this example_.
         1. ~Copy any item with key ending in /\.jpe?g/i~
         2. ~Publish a “known good” bucket with a “large” number of images~
            1. ~Backends will need to handle file uploads~
               1. ~Upload via pre-signed URL, skipping our backends entirely~
            2. ~Upload as form or base64 json?~
         3. ~Copy hundreds of images from a bucket~
            1. ~Copy any item with key ending in /\.jpe?g/i~
            2. ~Publish a “known good” bucket with a “large” number of images~
      3. Sync from https://registry.opendata.aws/nj-imagery/
         1. `aws s3 sync s3://njogis-imagery/ s3://${STORAGE_BUCKET}`
   2. Uploads analyzed by Rekognition
   3. Image labels in DynamoDB.
   4. S3 lifecycle moves objects to ~Glacier~ Intelligent Tiering.
2. Dan needs to see tags that were detected by the analyzer, with a count of how many images meet that criteria. This information will be displayed in the React app. This is complete when the analyzer has completed a run against the images & written the tag counts to DynamoDB.
   1. NO thumbnails!
   2. Analyzer runs on the bucket when uploads or copies are “complete”.
   3. Later iterations: provide a mechanism to expose rekognition tuning. (Amazon Rekognition Custom Labels)
   4. Tags are stored in DynamoDB.
3. Dan needs to download a bundle of files by tag (“nature”, “lake”, “mountain”). He will select those tags, and submit them for processing. Later, he will receive an email text message with a link to download the files. This is complete when the tagged images have been retrieved from Glacier Storage, combined into a .zip file available for download using a PreSigned URL. The intermediate online storage has been cleaned.
   1. Disclaimer in our docs to devs that this is fragile - don’t touch the data storage outside the app.
   2. Policy to delete the zip file after some period of time.
   3. ~jpegs retrieved from glacier are removed from s3 immediately.~ S3 intelligent tiering handles short-term Bulk storage billing.

## Wireframe 🖼

This is an ASCII sketch of a wireframe.

```text
(Upload images) (Import Bucket)
Tags
[ ] Mountain (32)
[ ] Lake (27)
[ ] Clouds (18)
[Phone Number|Email] (Download)
Select tags → Click (Download) → Start User Story 3
Upload Images → <input type=“file” multiple /> to select images & Upload over form
~Import Bucket → [Bucket Name] (Copy) → Import jpegs from that button~
```

## Services ⚙️

This example will be entirely serverless first. While cross service apps have historically assumed a locally running monolith, the asynchronous nature of ~restoring from Glacier~ zipping a large number of files necessitates a change in architectural approach. Because notifications can’t reach back to the customer’s locally running ephemeral instance, this example must have a deployed instance “somewhere”.

Because the app isn’t intended as a real-time or latency sensitive workload, it defies frugality to leave an entire EC2 instance or ECS stack around to serve its requests. While this could be mitigated with auto scaling groups set to zero, it would incur significant management overhead deciding when to scale in to 1 instance. A serverless deployment with faster cold start and automated scale in is preferable. Lambda and Fargate are the two primary offerings. Lambda requires custom configuration & build steps via CFN, SAM, or CDK; has single-function handlers; and fits a cloud-first mental model. Fargate requires Docker containers for all applications and is lightly heavier. Technically, these solutions are of similar complexity and cost, just exposing the complexity in different ways.

This team decided based on coin toss to use Lambda.

|         | Pros                                | Cons                             | Wash |
| ------- | ----------------------------------- | -------------------------------- | ---- |
| Lambda  | Cloud-first mental model            | N independent lambdas            | Cost |
|         | Exciting and new                    | Managing library layers          |      |
|         | Having a "real" non-trivial example | Difficult to run & debug locally |      |
| Fargate | Monolith HTTP middleware            | Heavy weight containers          | Cost |
|         | "Lift & Shift" from EC2             | "Boring"                         |      |
|         | Traditional debugging tools         |                                  |      |

1. ~S3 / Glacier for long term storage. Show how to explicitly use the SDK to manage s3 storage classes for archival and later recovery. This example was predicated on building something that used the Glacier storage tier.~
   1. ~Local library for generating zip files (each language will have its own lib).~
   2. ~S3/Glacier lifecycle? - moving between cold & hot storage.~
      1. ~Upload to S3~
      2. ~Run image rekognition against S3 images (working in prototype app)~
      3. ~Move images to Glacier tiers (we can set the Storage Class on an object in S3. Once we do, object is marked as shown here)~
      4. ~On request, we restore the obects such as Sun22.jpg. Once all objects are restored, create ZIP, and notify user.~
      5. ~Create a manifest of objects from tags. Submit a Restore Job using the manifest. When the job is complete, create a zip file of the restored objects.~
         1. ~Stream the objects from S3, through an archiver, and to the destination object with a presigned url. This prevents~
      6. ~After zip / download, the archive zip should have a 24 hour retention policy S3 bucket (we can use a pre-signed URL. The maximum expiration time for a presigned URL is 7 days from the time of creation so 24 hours is supported)~
2. S3 intelligent tiering for storage.
   1. Two buckets: a storage bucket, and a working bucket. Photos are stored in the storage bucket. The working bucket is used for short term storage of zipped files.
   2. When the user initiates a download, we trigger an asynchronous lambda function that streams the selected images into a zip file, stores that zip file in the working bucket, and notifies the user with a presigned URL to the zip.
   3. The storage bucket will use Amazon’s new(er) Intelligent Tiering archival storage. This archival storage is Amazon’s “best of both worlds” system - items that have not been accessed in long periods of time are stored more cheaply, while customers don’t need to handle storage class lifecycles.
3. Rekognition for image analysis. (In my prototype, this functionality works fine. However, each image must only be counted once as to not create an inaccurate count in the DynamoDB table).
   1. To track image counting, add an object tag
   2. This tag will also be the trigger for Glacier to archive the image.
4. DynamoDB for data. The data model has no strong relational constraints, and is storing labels as keys with a set of keys that match the image. Lookup by key is tantamount. DynamoDB is the best fit over RDS/Aurora (SQL), RedShift (data lake / large data), and Elasticache (not ephemeral - job/topic is ephemeral, but not worth adding yet another tool).
   1. Amazon DynamoDB table with three columns. The partition key is the name of the label, as returned from Rekognition. We only want each label once so we can get an accurate count. The count column represents an updated count of the specific label that has been detected by Rekognition (for example, Mountain). That is, each time a specific label is detected in a unique image, the count is incremented by 1 (see below). The third column is a supported List Set that shows the images where the tag appears. The List is a supported type as described here: https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.NamingRulesDataTypes.html. By using a Number and Set, updates can take advantage of DynamoDB update expressions.
   2. Object IDs: /{guidv4}-imagename.jpg
   3. ~Job tracking:~
      1. ~ID JobId~
      2. ~NOTIFY TopicARN~
5. SNS to send SMS or Email for notifications.
   1. It will take several seconds to several hours to zip a large number of images~, pull images from Glacier to online S3~ and make a zip of them. SNS will manage providing the notification when it’s done.
   2. ~When initiating a restore job, create a topic & subscribe the notify token (phone or email). Store the topic’s ARN keyed to the job id. When the job is complete, delete the topic. This keeps PII to a minimum (only on flight from the restore request to the topic creation), and no PII is stored in this app.~ Create one notification channel per customer, to ensure 1:1 download bundle to received notification, and a single opt-in notification. The claim comes with the PII regardless, so we just don’t want to log or store it.
   3. This is a push operation, indicating SNS rather than SQS. The app doesn’t want to store the users’s address PII at rest, necessary for the async notification, so direct SMS is inappropriate.
6. Cognito to handle user authentication & security.
   1. Each deployment is an individual instance that must be secured separately.
   2. AWS IAM controls access to AWS resources; this app creates AWS resources during deployment and then works with objects within those resources, and is not an appropriate fit.
   3. AWS Cognito is the first-party offering for granting access to users from web and mobile properties, which fits the serverless scenario here. User email or phone number are available to Lambdas via token claims, and ties the notification process to a specific user.
   4. Cognito Hosted UI is used to simplify the login flow. The hosted UI can return either a authorization code which must be validated on the backend or the less secure authorization token which can be used directly to authenticate to resources. This app uses the authorization token for simplicity.

## Deployment 🛬

Two-click deployment - one for general resources and one for language specific portions.
Stretch goal: one click (One stack for common resources, N stacks for each language). Recommend languages use one layer for all functions, and use the Function Configuration “Handler” to choose between them per-function.

1. S3 Website for a serverless front end.
2. Cognito for user authentication.
   1. Only need a single user pool, with a hard-coded user, no need for a registration flow.
3. Lambda to host endpoint functions.
4. API Gateway to route API requests to lambdas.
   1. Must be a REST API for Cognito authentication.
5. CDK to mange resources in the stack.
   1. TypeScript CDK for reduced overhead from JSII, and slightly more ergonomic API.
