// snippet-sourcedescription:[rekognition-video-stored-detect-labels.java demonstrates how to use Amazon Rekognition with a stored video.]
// snippet-service:[rekognition]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon Rekognition]
// snippet-keyword:[Code Sample]
// snippet-keyword:[StartLabelDetection]
// snippet-keyword:[GetLabelDetection]
// snippet-keyword:[StartFaceDetection]
// snippet-keyword:[GetFaceDetection]
// snippet-keyword:[StartCelebrityRecognition]
// snippet-keyword:[GetCelebrityRecognition]
// snippet-keyword:[StartContentModeration]
// snippet-keyword:[GetContentModeration]
// snippet-keyword:[StartPersonTracking]
// snippet-keyword:[GetPersonTracking]
// snippet-keyword:[StartFaceSearch]
// snippet-keyword:[GetFaceSearch]
// snippet-keyword:[S3 Bucket]
// snippet-keyword:[Image]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-18]
// snippet-sourceauthor:[reesch(AWS)]
// snippet-start:[rekognition.java.rekognition-video-stored-detect-labels.complete]

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

//Example code for calling Rekognition Video operations
//For more information, see https://docs.aws.amazon.com/rekognition/latest/dg/video.html
package aws.example.rekognition.video.stored;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CelebrityDetail;
import com.amazonaws.services.rekognition.model.CelebrityRecognition;
import com.amazonaws.services.rekognition.model.CelebrityRecognitionSortBy;
import com.amazonaws.services.rekognition.model.ContentModerationDetection;
import com.amazonaws.services.rekognition.model.ContentModerationSortBy;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceDetection;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.FaceSearchSortBy;
import com.amazonaws.services.rekognition.model.GetCelebrityRecognitionRequest;
import com.amazonaws.services.rekognition.model.GetCelebrityRecognitionResult;
import com.amazonaws.services.rekognition.model.GetContentModerationRequest;
import com.amazonaws.services.rekognition.model.GetContentModerationResult;
import com.amazonaws.services.rekognition.model.GetFaceDetectionRequest;
import com.amazonaws.services.rekognition.model.GetFaceDetectionResult;
import com.amazonaws.services.rekognition.model.GetFaceSearchRequest;
import com.amazonaws.services.rekognition.model.GetFaceSearchResult;
import com.amazonaws.services.rekognition.model.GetLabelDetectionRequest;
import com.amazonaws.services.rekognition.model.GetLabelDetectionResult;
import com.amazonaws.services.rekognition.model.GetPersonTrackingRequest;
import com.amazonaws.services.rekognition.model.GetPersonTrackingResult;
import com.amazonaws.services.rekognition.model.Instance;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.LabelDetection;
import com.amazonaws.services.rekognition.model.LabelDetectionSortBy;
import com.amazonaws.services.rekognition.model.NotificationChannel;
import com.amazonaws.services.rekognition.model.Parent;
import com.amazonaws.services.rekognition.model.PersonDetection;
import com.amazonaws.services.rekognition.model.PersonMatch;
import com.amazonaws.services.rekognition.model.PersonTrackingSortBy;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.StartCelebrityRecognitionRequest;
import com.amazonaws.services.rekognition.model.StartCelebrityRecognitionResult;
import com.amazonaws.services.rekognition.model.StartContentModerationRequest;
import com.amazonaws.services.rekognition.model.StartContentModerationResult;
import com.amazonaws.services.rekognition.model.StartFaceDetectionRequest;
import com.amazonaws.services.rekognition.model.StartFaceDetectionResult;
import com.amazonaws.services.rekognition.model.StartFaceSearchRequest;
import com.amazonaws.services.rekognition.model.StartFaceSearchResult;
import com.amazonaws.services.rekognition.model.StartLabelDetectionRequest;
import com.amazonaws.services.rekognition.model.StartLabelDetectionResult;
import com.amazonaws.services.rekognition.model.StartPersonTrackingRequest;
import com.amazonaws.services.rekognition.model.StartPersonTrackingResult;
import com.amazonaws.services.rekognition.model.Video;
import com.amazonaws.services.rekognition.model.VideoMetadata;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

//Analyzes videos using the Rekognition Video API 
public class VideoDetect {
    // Change to your values
    private static String bucket = "";
    private static String video = ""; 
    private static String queueUrl =  "";
    private static String topicArn="";
    private static String roleArn="";  
    
    
    private static AmazonSQS sqs = null;
    private static AmazonRekognition rek = null;
    
        private static NotificationChannel channel= new NotificationChannel()
            .withSNSTopicArn(topicArn)
            .withRoleArn(roleArn);


    private static String startJobId = null;

    //Entry point. Starts analysis of video in specified bucket.
    public static void main(String[] args)  throws Exception{


        sqs = AmazonSQSClientBuilder.defaultClient();
        rek = AmazonRekognitionClientBuilder.defaultClient();
        
        //Change active start function for the desired analysis. Also change the GetResults function later in this code.
        //=================================================
        StartLabels(bucket, video);
        //StartFaces(bucket,video);
        //StartFaceSearchCollection(bucket,video);
        //StartPersons(bucket,video);
        //StartCelebrities(bucket,video);
        //StartModerationLabels(bucket,video);
        //=================================================
        System.out.println("Waiting for job: " + startJobId);
        //Poll queue for messages
        List<Message> messages=null;
        int dotLine=0;
        boolean jobFound=false;

        //loop until the job status is published. Ignore other messages in queue.
        do{
            messages = sqs.receiveMessage(queueUrl).getMessages();
            if (dotLine++<20){
                System.out.print(".");
            }else{
                System.out.println();
                dotLine=0;
            }

            if (!messages.isEmpty()) {
                //Loop through messages received.
                for (Message message: messages) {
                    String notification = message.getBody();

                    // Get status and job id from notification.
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonMessageTree = mapper.readTree(notification);
                    JsonNode messageBodyText = jsonMessageTree.get("Message");
                    ObjectMapper operationResultMapper = new ObjectMapper();
                    JsonNode jsonResultTree = operationResultMapper.readTree(messageBodyText.textValue());
                    JsonNode operationJobId = jsonResultTree.get("JobId");
                    JsonNode operationStatus = jsonResultTree.get("Status");
                    System.out.println("Job found was " + operationJobId);
                    // Found job. Get the results and display.
                    if(operationJobId.asText().equals(startJobId)){
                        jobFound=true;
                        System.out.println("Job id: " + operationJobId );
                        System.out.println("Status : " + operationStatus.toString());
                        if (operationStatus.asText().equals("SUCCEEDED")){
                            //Change to match the start function earlier in this code.
                            //============================================
                            GetResultsLabels();
                            //GetResultsFaces();
                            //GetResultsFaceSearchCollection();
                            //GetResultsPersons();
                            //GetResultsCelebrities();
                            //GetResultsModerationLabels();
                            //============================================
                        }
                        else{
                            System.out.println("Video analysis failed");
                        }

                        sqs.deleteMessage(queueUrl,message.getReceiptHandle());
                    }

                    else{
                        System.out.println("Job received was not job " +  startJobId);
                        //Delete unknown message. Consider moving message to dead letter queue
                        sqs.deleteMessage(queueUrl,message.getReceiptHandle());
                    }
                }
            }
        } while (!jobFound);


        System.out.println("Done!");
    }

    //Starts label detection by calling StartLabelDetection.
    //bucket is the S3 bucket that contains the video.
    //video is the video filename.
    private static void StartLabels(String bucket, String video) throws Exception{

        StartLabelDetectionRequest req = new StartLabelDetectionRequest()
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                                .withBucket(bucket)
                                .withName(video)))
                .withMinConfidence(50F)
                .withJobTag("DetectingLabels")
                .withNotificationChannel(channel);

        StartLabelDetectionResult startLabelDetectionResult = rek.startLabelDetection(req);
        startJobId=startLabelDetectionResult.getJobId();
        
        
    }
    
    
    //Gets the results of labels detection by calling GetLabelDetection. Label
    // detection is started by a call to StartLabelDetection.
    private static void GetResultsLabels() throws Exception{

        int maxResults=10;
        String paginationToken=null;
        GetLabelDetectionResult labelDetectionResult=null;

        do {
            if (labelDetectionResult !=null){
                paginationToken = labelDetectionResult.getNextToken();
            }

            GetLabelDetectionRequest labelDetectionRequest= new GetLabelDetectionRequest()
                    .withJobId(startJobId)
                    .withSortBy(LabelDetectionSortBy.TIMESTAMP)
                    .withMaxResults(maxResults)
                    .withNextToken(paginationToken);


            labelDetectionResult = rek.getLabelDetection(labelDetectionRequest);

            VideoMetadata videoMetaData=labelDetectionResult.getVideoMetadata();

            System.out.println("Format: " + videoMetaData.getFormat());
            System.out.println("Codec: " + videoMetaData.getCodec());
            System.out.println("Duration: " + videoMetaData.getDurationMillis());
            System.out.println("FrameRate: " + videoMetaData.getFrameRate());


            //Show labels, confidence and detection times
            List<LabelDetection> detectedLabels= labelDetectionResult.getLabels();

            for (LabelDetection detectedLabel: detectedLabels) {
                long seconds=detectedLabel.getTimestamp();
                Label label=detectedLabel.getLabel();
                System.out.println("Millisecond: " + Long.toString(seconds) + " ");
                
                System.out.println("   Label:" + label.getName()); 
                System.out.println("   Confidence:" + detectedLabel.getLabel().getConfidence().toString());
      
                List<Instance> instances = label.getInstances();
                System.out.println("   Instances of " + label.getName());
                if (instances.isEmpty()) {
                    System.out.println("        " + "None");
                } else {
                    for (Instance instance : instances) {
                        System.out.println("        Confidence: " + instance.getConfidence().toString());
                        System.out.println("        Bounding box: " + instance.getBoundingBox().toString());
                    }
                }
                System.out.println("   Parent labels for " + label.getName() + ":");
                List<Parent> parents = label.getParents();
                if (parents.isEmpty()) {
                    System.out.println("        None");
                } else {
                    for (Parent parent : parents) {
                        System.out.println("        " + parent.getName());
                    }
                }
                System.out.println();
            }
        } while (labelDetectionResult !=null && labelDetectionResult.getNextToken() != null);

    }    
    
    //Starts face detection in a stored video by calling StartFaceDetection.
    //bucket is the S3 bucket that contains the video.
    //video is the video filename.
    private static void StartFaces(String bucket, String video) throws Exception{
        
        StartFaceDetectionRequest req = new StartFaceDetectionRequest()
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                            .withBucket(bucket)
                            .withName(video)))
                .withNotificationChannel(channel);
                            
                            
        
        StartFaceDetectionResult startLabelDetectionResult = rek.startFaceDetection(req);
        startJobId=startLabelDetectionResult.getJobId();
        
    } 
    //Gets the results of face detection by calling GetFaceDetection. Face 
    // detection is started by calling StartFaceDetection.
    private static void GetResultsFaces() throws Exception{
        
        int maxResults=10;
        String paginationToken=null;
        GetFaceDetectionResult faceDetectionResult=null;
        
        do{
            if (faceDetectionResult !=null){
                paginationToken = faceDetectionResult.getNextToken();
            }
        
            faceDetectionResult = rek.getFaceDetection(new GetFaceDetectionRequest()
                 .withJobId(startJobId)
                 .withNextToken(paginationToken)
                 .withMaxResults(maxResults));
        
            VideoMetadata videoMetaData=faceDetectionResult.getVideoMetadata();
                
            System.out.println("Format: " + videoMetaData.getFormat());
            System.out.println("Codec: " + videoMetaData.getCodec());
            System.out.println("Duration: " + videoMetaData.getDurationMillis());
            System.out.println("FrameRate: " + videoMetaData.getFrameRate());
                
                
            //Show faces, confidence and detection times
            List<FaceDetection> faces= faceDetectionResult.getFaces();
         
            for (FaceDetection face: faces) { 
                long seconds=face.getTimestamp()/1000;
                System.out.print("Sec: " + Long.toString(seconds) + " ");
                System.out.println(face.getFace().toString());
                System.out.println();           
            }
        } while (faceDetectionResult !=null && faceDetectionResult.getNextToken() != null);
          
            
    }
    
    //Started face collection search in a video by calling StartFaceSearch.
    //bucket is the S3 bucket that contains the video.
    //video is the video filename.
    //Change CollectionId to the ID of the collection that you want to search
    private static void StartFaceSearchCollection(String bucket, String video) throws Exception{


        StartFaceSearchRequest req = new StartFaceSearchRequest()
                .withCollectionId("CollectionId")
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                                .withBucket(bucket)
                                .withName(video)))
                .withNotificationChannel(channel);



        StartFaceSearchResult startPersonCollectionSearchResult = rek.startFaceSearch(req);
        startJobId=startPersonCollectionSearchResult.getJobId();

    } 
    //Gets the results of a collection face search by calling GetFaceSearch.
    //The search is started by calling StartFaceSearch.
    // ==================================================================
    private static void GetResultsFaceSearchCollection() throws Exception{

       GetFaceSearchResult faceSearchResult=null;
       int maxResults=10;
       String paginationToken=null;

       do {

           if (faceSearchResult !=null){
               paginationToken = faceSearchResult.getNextToken();
           }


           faceSearchResult  = rek.getFaceSearch(
                   new GetFaceSearchRequest()
                   .withJobId(startJobId)
                   .withMaxResults(maxResults)
                   .withNextToken(paginationToken)
                   .withSortBy(FaceSearchSortBy.TIMESTAMP)
                   );


           VideoMetadata videoMetaData=faceSearchResult.getVideoMetadata();

           System.out.println("Format: " + videoMetaData.getFormat());
           System.out.println("Codec: " + videoMetaData.getCodec());
           System.out.println("Duration: " + videoMetaData.getDurationMillis());
           System.out.println("FrameRate: " + videoMetaData.getFrameRate());
           System.out.println();      


           //Show search results
           List<PersonMatch> matches= 
                   faceSearchResult.getPersons();

           for (PersonMatch match: matches) { 
               long milliSeconds=match.getTimestamp();
               System.out.print("Timestamp: " + Long.toString(milliSeconds));
               System.out.println(" Person number: " + match.getPerson().getIndex());
               List <FaceMatch> faceMatches = match.getFaceMatches();
               if (faceMatches != null) {
                   System.out.println("Matches in collection...");
                   for (FaceMatch faceMatch: faceMatches){
                       Face face=faceMatch.getFace();
                       System.out.println("Face Id: "+ face.getFaceId());
                       System.out.println("Similarity: " + faceMatch.getSimilarity().toString());
                       System.out.println();
                   }
               }
               System.out.println();           
           } 

           System.out.println(); 

       } while (faceSearchResult !=null && faceSearchResult.getNextToken() != null);

   }

    //Starts the tracking of people in a video by calling StartPersonTracking
    //bucket is the S3 bucket that contains the video.
    //video is the video filename.
    private static void StartPersons(String bucket, String video) throws Exception{
        
        int maxResults=10;
        String paginationToken=null;
        
     StartPersonTrackingRequest req = new StartPersonTrackingRequest()
             .withVideo(new Video()
                     .withS3Object(new S3Object()
                         .withBucket(bucket)
                         .withName(video)))
             .withNotificationChannel(channel);
                            
         
        
     StartPersonTrackingResult startPersonDetectionResult = rek.startPersonTracking(req);
     startJobId=startPersonDetectionResult.getJobId();
        
    } 
    
    
    //Gets person tracking information using the GetPersonTracking operation. Person tracking
    // is started by calling StartPersonTracking
    private static void GetResultsPersons() throws Exception{
        int maxResults=10;
        String paginationToken=null;
        GetPersonTrackingResult personTrackingResult=null;
        
        do{
            if (personTrackingResult !=null){
                paginationToken = personTrackingResult.getNextToken();
            }
            
            personTrackingResult = rek.getPersonTracking(new GetPersonTrackingRequest()
                 .withJobId(startJobId)
                 .withNextToken(paginationToken)
                 .withSortBy(PersonTrackingSortBy.TIMESTAMP)
                 .withMaxResults(maxResults));
      
            VideoMetadata videoMetaData=personTrackingResult.getVideoMetadata();
                
            System.out.println("Format: " + videoMetaData.getFormat());
            System.out.println("Codec: " + videoMetaData.getCodec());
            System.out.println("Duration: " + videoMetaData.getDurationMillis());
            System.out.println("FrameRate: " + videoMetaData.getFrameRate());
                
                
            //Show persons, confidence and detection times
            List<PersonDetection> detectedPersons= personTrackingResult.getPersons();
         
            for (PersonDetection detectedPerson: detectedPersons) { 
                
               long seconds=detectedPerson.getTimestamp()/1000;
               System.out.print("Sec: " + Long.toString(seconds) + " ");
               System.out.println("Person Identifier: "  + detectedPerson.getPerson().getIndex());
                  System.out.println();             
            }
        }  while (personTrackingResult !=null && personTrackingResult.getNextToken() != null);
        
    } 
    
    

    //Starts the detection of celebrities in a video by calling StartCelebrityRecognition.
    //bucket is the S3 bucket that contains the video.
    //video is the video filename.
    private static void StartCelebrities(String bucket, String video) throws Exception{

       StartCelebrityRecognitionRequest req = new StartCelebrityRecognitionRequest()
             .withVideo(new Video()
                   .withS3Object(new S3Object()
                         .withBucket(bucket)
                         .withName(video)))
             .withNotificationChannel(channel);

       StartCelebrityRecognitionResult startCelebrityRecognitionResult = rek.startCelebrityRecognition(req);
       startJobId=startCelebrityRecognitionResult.getJobId();

    } 
    
    // Gets the results of a celebrity detection analysis by calling GetCelebrityRecognition.
   // Celebrity detection is started by calling StartCelebrityRecognition.
    private static void GetResultsCelebrities() throws Exception{

       int maxResults=10;
       String paginationToken=null;
       GetCelebrityRecognitionResult celebrityRecognitionResult=null;

       do{
          if (celebrityRecognitionResult !=null){
             paginationToken = celebrityRecognitionResult.getNextToken();
          }
          celebrityRecognitionResult = rek.getCelebrityRecognition(new GetCelebrityRecognitionRequest()
                .withJobId(startJobId)
                .withNextToken(paginationToken)
                .withSortBy(CelebrityRecognitionSortBy.TIMESTAMP)
                .withMaxResults(maxResults));


          System.out.println("File info for page");
          VideoMetadata videoMetaData=celebrityRecognitionResult.getVideoMetadata();

          System.out.println("Format: " + videoMetaData.getFormat());
          System.out.println("Codec: " + videoMetaData.getCodec());
          System.out.println("Duration: " + videoMetaData.getDurationMillis());
          System.out.println("FrameRate: " + videoMetaData.getFrameRate());

          System.out.println("Job");

          System.out.println("Job status: " + celebrityRecognitionResult.getJobStatus());


          //Show celebrities
          List<CelebrityRecognition> celebs= celebrityRecognitionResult.getCelebrities();

          for (CelebrityRecognition celeb: celebs) { 
             long seconds=celeb.getTimestamp()/1000;
             System.out.print("Sec: " + Long.toString(seconds) + " ");
             CelebrityDetail details=celeb.getCelebrity();
             System.out.println("Name: " + details.getName());
             System.out.println("Id: " + details.getId());
             System.out.println(); 
          }
       } while (celebrityRecognitionResult !=null && celebrityRecognitionResult.getNextToken() != null);

    } 
    
    //Starts the moderation of content in a video by calling StartContentModeration.
    //bucket is the S3 bucket that contains the video.
    //video is the video filename.
    // ==================================================================
    private static void StartModerationLabels(String bucket, String video) throws Exception{
        
        StartContentModerationRequest req = new StartContentModerationRequest()
                .withVideo(new Video()
                        .withS3Object(new S3Object()
                            .withBucket(bucket)
                            .withName(video)))
                .withNotificationChannel(channel);
                             
                             
         
         StartContentModerationResult startModerationLabelDetectionResult = rek.startContentModeration(req);
         startJobId=startModerationLabelDetectionResult.getJobId();
         
     } 
     
    //Gets the results of unsafe content label detection by calling
   // GetContentModeration. Analysis is started by a call to StartContentModeration.    
     private static void GetResultsModerationLabels() throws Exception{
         
         int maxResults=10;
         String paginationToken=null;
         GetContentModerationResult moderationLabelDetectionResult =null;
         
         do{
             if (moderationLabelDetectionResult !=null){
                 paginationToken = moderationLabelDetectionResult.getNextToken();
             }
             
             moderationLabelDetectionResult = rek.getContentModeration(
                     new GetContentModerationRequest()
                         .withJobId(startJobId)
                         .withNextToken(paginationToken)
                         .withSortBy(ContentModerationSortBy.TIMESTAMP)
                         .withMaxResults(maxResults));
                     
             
    
             VideoMetadata videoMetaData=moderationLabelDetectionResult.getVideoMetadata();
                 
             System.out.println("Format: " + videoMetaData.getFormat());
             System.out.println("Codec: " + videoMetaData.getCodec());
             System.out.println("Duration: " + videoMetaData.getDurationMillis());
             System.out.println("FrameRate: " + videoMetaData.getFrameRate());
                 
                 
             //Show moderated content labels, confidence and detection times
             List<ContentModerationDetection> moderationLabelsInFrames= 
                     moderationLabelDetectionResult.getModerationLabels();
          
             for (ContentModerationDetection label: moderationLabelsInFrames) { 
                 long seconds=label.getTimestamp()/1000;
                 System.out.print("Sec: " + Long.toString(seconds));
                 System.out.println(label.getModerationLabel().toString());
                 System.out.println();           
             }  
         } while (moderationLabelDetectionResult !=null && moderationLabelDetectionResult.getNextToken() != null);
         
     }    
}
// snippet-end:[rekognition.java.rekognition-video-stored-detect-labels.complete]