// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.sqs.model.Message
import com.example.sns.DASHES
import com.example.sns.createFIFO
import com.example.sns.createQueue
import com.example.sns.createSNSTopic
import com.example.sns.deleteMessages
import com.example.sns.deleteSNSTopic
import com.example.sns.deleteSQSQueue
import com.example.sns.getSQSQueueAttrs
import com.example.sns.pubMessage
import com.example.sns.pubMessageFIFO
import com.example.sns.receiveMessages
import com.example.sns.setQueueAttr
import com.example.sns.subQueue
import com.example.sns.unSub
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AWSSNSTest {
    @Test
    @Order(1)
    fun testWorkflowFIFO() = runBlocking {
        val duplication = "n"
        val topicName: String
        val deduplicationID: String?
        val groupId: String?
        val topicArnVal: String?
        val sqsQueueName: String
        val sqsQueueUrl: String?
        val sqsQueueArn: String
        val subscriptionArn: String?
        val selectFIFO = true
        val randomNum = (1..10000).random()

        val messageVal: String
        val messageList: List<Message?>?
        val filterList = ArrayList<String>()
        val msgAttValue = ""
        deduplicationID = "dup100"
        groupId = "group100"
        println(DASHES)

        println(DASHES)
        println("2. Create a topic.")
        println("Enter a name for your SNS topic.")
        topicName = "topic$randomNum.fifo"
        println("The name of the topic is $topicName")
        topicArnVal = createFIFO(topicName, duplication)
        println("The ARN of the FIFO topic is $topicArnVal")
        println(DASHES)

        println(DASHES)
        println("3. Create an SQS queue.")
        sqsQueueName = "queue$randomNum.fifo"
        sqsQueueUrl = createQueue(sqsQueueName, selectFIFO)
        println("The queue URL is $sqsQueueUrl")
        println(DASHES)

        println(DASHES)
        println("4. Get the SQS queue ARN attribute.")
        sqsQueueArn = getSQSQueueAttrs(sqsQueueUrl)
        println("The ARN of the new queue is $sqsQueueArn")
        println(DASHES)

        println(DASHES)
        println("5. Attach an IAM policy to the queue.")
        // Define the policy to use.
        val policy = """{
     "Statement": [
     {
         "Effect": "Allow",
                 "Principal": {
             "Service": "sns.amazonaws.com"
         },
         "Action": "sqs:SendMessage",
                 "Resource": "$sqsQueueArn",
                 "Condition": {
             "ArnEquals": {
                 "aws:SourceArn": "$topicArnVal"
             }
         }
     }
     ]
     }"""
        setQueueAttr(sqsQueueUrl, policy)
        println(DASHES)

        println(DASHES)
        println("6. Subscribe to the SQS queue.")
        subscriptionArn = subQueue(topicArnVal, sqsQueueArn, filterList)
        println(DASHES)

        println(DASHES)
        println("7. Publish a message to the topic.")
        delay(1000)
        messageVal = "A message sent from a test."
        pubMessageFIFO(messageVal, topicArnVal, msgAttValue, duplication, groupId, deduplicationID)
        println(DASHES)

        println(DASHES)
        println("8. Display the message.")
        messageList = receiveMessages(sqsQueueUrl, msgAttValue)
        if (messageList != null) {
            for (mes in messageList) {
                println("Message Id: ${mes.messageId}")
                println("Full Message: ${mes.body}")
            }
        }
        println(DASHES)

        println(DASHES)
        println("9. Delete the received message.")
        if (messageList != null) {
            deleteMessages(sqsQueueUrl, messageList)
        }
        println(DASHES)

        println(DASHES)
        println("10. Unsubscribe from the topic and delete the queue")
        unSub(subscriptionArn)
        deleteSQSQueue(sqsQueueName)
        println(DASHES)

        println(DASHES)
        println("11. Delete the topic")
        deleteSNSTopic(topicArnVal)
        println(DASHES)
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun testWorkflowNonFIFO() = runBlocking {
        val accountId = ""
        val topicName: String
        val topicArnVal: String?
        val sqsQueueName: String
        val sqsQueueUrl: String?
        val sqsQueueArn: String
        val subscriptionArn: String?
        val selectFIFO = false
        val randomNum = (1..10000).random()
        val messageList: List<Message?>?
        val filterList = ArrayList<String>()
        val msgAttValue = ""
        println(DASHES)

        println("2. Create a topic.")
        topicName = "topic$randomNum"
        println("The name of the topic is $topicName")
        topicArnVal = createSNSTopic(topicName)
        println("The ARN of the non-FIFO topic is $topicArnVal")
        println(DASHES)

        println(DASHES)
        println("3. Create an SQS queue.")
        sqsQueueName = "queue$randomNum"
        sqsQueueUrl = createQueue(sqsQueueName, selectFIFO)
        println("The queue URL is $sqsQueueUrl")
        println(DASHES)

        println(DASHES)
        println("4. Get the SQS queue ARN attribute.")
        sqsQueueArn = getSQSQueueAttrs(sqsQueueUrl)
        println("The ARN of the new queue is $sqsQueueArn")
        println(DASHES)

        println(DASHES)
        println("5. Attach an IAM policy to the queue.")
        // Define the policy to use.
        val policy = """{
     "Statement": [
     {
         "Effect": "Allow",
                 "Principal": {
             "Service": "sns.amazonaws.com"
         },
         "Action": "sqs:SendMessage",
                 "Resource": "arn:aws:sqs:us-east-1:$accountId:$sqsQueueName",
                 "Condition": {
             "ArnEquals": {
                 "aws:SourceArn": "arn:aws:sns:us-east-1:$accountId:$topicName"
             }
         }
     }
     ]
     }"""
        setQueueAttr(sqsQueueUrl, policy)
        println(DASHES)

        println(DASHES)
        println("6. Subscribe to the SQS queue.")
        subscriptionArn = subQueue(topicArnVal, sqsQueueArn, filterList)
        println(DASHES)

        println(DASHES)
        println("7. Publish a message to the topic.")
        val message = "Hello this is a test."
        pubMessage(message, topicArnVal)
        println(DASHES)

        println(DASHES)
        println("8. Display the message.")
        delay(1000)
        messageList = receiveMessages(sqsQueueUrl, msgAttValue)
        if (messageList != null) {
            for (mes in messageList) {
                println("Message Id: ${mes.messageId}")
                println("Full Message: ${mes.body}")
            }
        }
        println(DASHES)

        println(DASHES)
        println("9. Delete the received message.")
        if (messageList != null) {
            deleteMessages(sqsQueueUrl, messageList)
        }
        println(DASHES)

        println(DASHES)
        println("10. Unsubscribe from the topic and delete the queue")
        unSub(subscriptionArn)
        deleteSQSQueue(sqsQueueName)
        println(DASHES)

        println(DASHES)
        println("11. Delete the topic")
        deleteSNSTopic(topicArnVal)
        println(DASHES)
        println("Test 2 passed")
    }
}
