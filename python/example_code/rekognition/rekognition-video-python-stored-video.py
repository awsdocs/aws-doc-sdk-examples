#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[rekognition-video-python-stored-video.py demonstrates how to use Amazon Rekognition videos stored in an Amazon S3 Bucket.]
#snippet-keyword:[Python]
#snippet-sourcesyntax:[python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon Rekognition]
#snippet-keyword:[Stored Video]
#snippet-keyword:[StartLabelDetection]
#snippet-keyword:[GetlabelDetection]
#snippet-keyword:[StartFaceDetection]
#snippet-keyword:[GetFaceDetection]
#snippet-keyword:[StartCelebrityRecognition]
#snippet-keyword:[GetCelebrityRecognition]
#snippet-keyword:[StartContentModeration]
#snippet-keyword:[GetContentModeration]
#snippet-keyword:[StartPersonTracking]
#snippet-keyword:[GetPersonTracking]
#snippet-keyword:[StartFaceSearch]
#snippet-keyword:[GetFaceSearch]
#snippet-keyword:[Bucket]
#snippet-service:[rekognition]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-3]
#snippet-sourceauthor:[reesch (AWS)]
#snippet-start:[rekognition.python.rekognition-video-python-stored-video.complete]

#Example code for calling Rekognition Video operations
#For more information, see https://docs.aws.amazon.com/rekognition/latest/dg/video.html

import boto3
import json
import sys

#Analyzes videos using the Rekognition Video API 
class VideoDetect:
    jobId = ''
    rek = boto3.client('rekognition')
    queueUrl = ''
    roleArn = ''
    topicArn = ''
    bucket = ''
    video = ''

    #Entry point. Starts analysis of video in specified bucket.
    def main(self):

        jobFound = False
        sqs = boto3.client('sqs')
       
        # Change active start function for the desired analysis. Also change the GetResults function later in this code.
        #=====================================
        response = self.rek.start_label_detection(Video={'S3Object': {'Bucket': self.bucket, 'Name': self.video}},
                                         NotificationChannel={'RoleArn': self.roleArn, 'SNSTopicArn': self.topicArn})

        #response = self.rek.start_face_detection(Video={'S3Object':{'Bucket':self.bucket,'Name':self.video}},
        #    NotificationChannel={'RoleArn':self.roleArn, 'SNSTopicArn':self.topicArn}) 

        #response = self.rek.start_face_search(Video={'S3Object':{'Bucket':self.bucket,'Name':self.video}},
        #    CollectionId='CollectionId',
        #    NotificationChannel={'RoleArn':self.roleArn, 'SNSTopicArn':self.topicArn})

        #response = self.rek.start_person_tracking(Video={'S3Object':{'Bucket':self.bucket,'Name':self.video}},
        #   NotificationChannel={'RoleArn':self.roleArn, 'SNSTopicArn':self.topicArn})

        #response = self.rek.start_celebrity_recognition(Video={'S3Object':{'Bucket':self.bucket,'Name':self.video}},
        #    NotificationChannel={'RoleArn':self.roleArn, 'SNSTopicArn':self.topicArn})

        #response = self.rek.start_content_moderation(Video={'S3Object':{'Bucket':self.bucket,'Name':self.video}},
        #    NotificationChannel={'RoleArn':self.roleArn, 'SNSTopicArn':self.topicArn})        


        #=====================================
        print('Start Job Id: ' + response['JobId'])
        dotLine=0
        while jobFound == False:
            sqsResponse = sqs.receive_message(QueueUrl=self.queueUrl, MessageAttributeNames=['ALL'],
                                          MaxNumberOfMessages=10)

            if sqsResponse:
                
                if 'Messages' not in sqsResponse:
                    if dotLine<20:
                        print('.', end='')
                        dotLine=dotLine+1
                    else:
                        print()
                        dotLine=0    
                    sys.stdout.flush()
                    continue

                for message in sqsResponse['Messages']:
                    notification = json.loads(message['Body'])
                    rekMessage = json.loads(notification['Message'])
                    print(rekMessage['JobId'])
                    print(rekMessage['Status'])
                    if str(rekMessage['JobId']) == response['JobId']:
                        print('Matching Job Found:' + rekMessage['JobId'])
                        jobFound = True
                        #Change to match the start function earlier in this code.
                        #=============================================
                        self.GetResultsLabels(rekMessage['JobId'])
                        #self.GetResultsFaces(rekMessage['JobId']) 
                        #self.GetResultsFaceSearchCollection(rekMessage['JobId']) 
                        #self.GetResultsPersons(rekMessage['JobId']) 
                        #self.GetResultsCelebrities(rekMessage['JobId']) 
                        #self.GetResultsModerationLabels(rekMessage['JobId'])                    
                                                
                        #=============================================

                        sqs.delete_message(QueueUrl=self.queueUrl,
                                       ReceiptHandle=message['ReceiptHandle'])
                    else:
                        print("Job didn't match:" +
                              str(rekMessage['JobId']) + ' : ' + str(response['JobId']))
                    # Delete the unknown message. Consider sending to dead letter queue
                    sqs.delete_message(QueueUrl=self.queueUrl,
                                   ReceiptHandle=message['ReceiptHandle'])

        print('done')


    # Gets the results of labels detection by calling GetLabelDetection. Label
    # detection is started by a call to StartLabelDetection.
    # jobId is the identifier returned from StartLabelDetection
    def GetResultsLabels(self, jobId):
        maxResults = 10
        paginationToken = ''
        finished = False

        while finished == False:
            response = self.rek.get_label_detection(JobId=jobId,
                                            MaxResults=maxResults,
                                            NextToken=paginationToken,
                                            SortBy='TIMESTAMP')

            print(response['VideoMetadata']['Codec'])
            print(str(response['VideoMetadata']['DurationMillis']))
            print(response['VideoMetadata']['Format'])
            print(response['VideoMetadata']['FrameRate'])

            for labelDetection in response['Labels']:
                label=labelDetection['Label']

                print("Timestamp: " + str(labelDetection['Timestamp']))
                print("   Label: " + label['Name'])
                print("   Confidence: " +  str(label['Confidence']))
                print("   Instances:")
                for instance in label['Instances']:
                    print ("      Confidence: " + str(instance['Confidence']))
                    print ("      Bounding box")
                    print ("        Top: " + str(instance['BoundingBox']['Top']))
                    print ("        Left: " + str(instance['BoundingBox']['Left']))
                    print ("        Width: " +  str(instance['BoundingBox']['Width']))
                    print ("        Height: " +  str(instance['BoundingBox']['Height']))
                    print()
                print()
                print ("   Parents:")
                for parent in label['Parents']:
                    print ("      " + parent['Name'])
                print ()

                if 'NextToken' in response:
                    paginationToken = response['NextToken']
                else:
                    finished = True

    # Gets person tracking information using the GetPersonTracking operation.
    # You start person tracking by calling StartPersonTracking
    # jobId is the identifier returned from StartPersonTracking
    def GetResultsPersons(self, jobId):
        maxResults = 10
        paginationToken = ''
        finished = False

        while finished == False:
            response = self.rek.get_person_tracking(JobId=jobId,
                                            MaxResults=maxResults,
                                            NextToken=paginationToken)

            print(response['VideoMetadata']['Codec'])
            print(str(response['VideoMetadata']['DurationMillis']))
            print(response['VideoMetadata']['Format'])
            print(response['VideoMetadata']['FrameRate'])

            for personDetection in response['Persons']:
                print('Index: ' + str(personDetection['Person']['Index']))
                print('Timestamp: ' + str(personDetection['Timestamp']))
                print()

            if 'NextToken' in response:
                paginationToken = response['NextToken']
            else:
                finished = True
    # Gets the results of unsafe content label detection by calling
    # GetContentModeration. Analysis is started by a call to StartContentModeration.
    # jobId is the identifier returned from StartContentModeration
    def GetResultsModerationLabels(self, jobId):
        maxResults = 10
        paginationToken = ''
        finished = False

        while finished == False:
            response = self.rek.get_content_moderation(JobId=jobId,
                                                MaxResults=maxResults,
                                                NextToken=paginationToken)

            print(response['VideoMetadata']['Codec'])
            print(str(response['VideoMetadata']['DurationMillis']))
            print(response['VideoMetadata']['Format'])
            print(response['VideoMetadata']['FrameRate'])

            for contentModerationDetection in response['ModerationLabels']:
                print('Label: ' +
                    str(contentModerationDetection['ModerationLabel']['Name']))
                print('Confidence: ' +
                    str(contentModerationDetection['ModerationLabel']['Confidence']))
                print('Parent category: ' +
                    str(contentModerationDetection['ModerationLabel']['ParentName']))
                print('Timestamp: ' + str(contentModerationDetection['Timestamp']))
                print()

            if 'NextToken' in response:
                paginationToken = response['NextToken']
            else:
                finished = True

    # Gets the results of face detection by calling GetFaceDetection. Face 
    # detection is started by calling StartFaceDetection.
    # jobId is the identifier returned from StartFaceDetection
    def GetResultsFaces(self, jobId):
        maxResults = 10
        paginationToken = ''
        finished = False

        while finished == False:
            response = self.rek.get_face_detection(JobId=jobId,
                                            MaxResults=maxResults,
                                            NextToken=paginationToken)

            print(response['VideoMetadata']['Codec'])
            print(str(response['VideoMetadata']['DurationMillis']))
            print(response['VideoMetadata']['Format'])
            print(response['VideoMetadata']['FrameRate'])

            for faceDetection in response['Faces']:
                print('Face: ' + str(faceDetection['Face']))
                print('Confidence: ' + str(faceDetection['Face']['Confidence']))
                print('Timestamp: ' + str(faceDetection['Timestamp']))
                print()

            if 'NextToken' in response:
                paginationToken = response['NextToken']
            else:
                finished = True

    # Gets the results of a collection face search by calling GetFaceSearch.
    # The search is started by calling StartFaceSearch.
    # jobId is the identifier returned from StartFaceSearch
    def GetResultsFaceSearchCollection(self, jobId):
        maxResults = 10
        paginationToken = ''

        finished = False

        while finished == False:
            response = self.rek.get_face_search(JobId=jobId,
                                        MaxResults=maxResults,
                                        NextToken=paginationToken)

            print(response['VideoMetadata']['Codec'])
            print(str(response['VideoMetadata']['DurationMillis']))
            print(response['VideoMetadata']['Format'])
            print(response['VideoMetadata']['FrameRate'])

            for personMatch in response['Persons']:
                print('Person Index: ' + str(personMatch['Person']['Index']))
                print('Timestamp: ' + str(personMatch['Timestamp']))

                if ('FaceMatches' in personMatch):
                    for faceMatch in personMatch['FaceMatches']:
                        print('Face ID: ' + faceMatch['Face']['FaceId'])
                        print('Similarity: ' + str(faceMatch['Similarity']))
                print()
            if 'NextToken' in response:
                paginationToken = response['NextToken']
            else:
                finished = True
            print()

    # Gets the results of a celebrity detection analysis by calling GetCelebrityRecognition.
    # Celebrity detection is started by calling StartCelebrityRecognition.
    # jobId is the identifier returned from StartCelebrityRecognition    
    def GetResultsCelebrities(self, jobId):
        maxResults = 10
        paginationToken = ''
        finished = False

        while finished == False:
            response = self.rek.get_celebrity_recognition(JobId=jobId,
                                                    MaxResults=maxResults,
                                                    NextToken=paginationToken)

            print(response['VideoMetadata']['Codec'])
            print(str(response['VideoMetadata']['DurationMillis']))
            print(response['VideoMetadata']['Format'])
            print(response['VideoMetadata']['FrameRate'])

            for celebrityRecognition in response['Celebrities']:
                print('Celebrity: ' +
                    str(celebrityRecognition['Celebrity']['Name']))
                print('Timestamp: ' + str(celebrityRecognition['Timestamp']))
                print()

            if 'NextToken' in response:
                paginationToken = response['NextToken']
            else:
                finished = True



if __name__ == "__main__":

    analyzer=VideoDetect()
    analyzer.main()


#snippet-end:[rekognition.python.rekognition-video-python-stored-video.complete]