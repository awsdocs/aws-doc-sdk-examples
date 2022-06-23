/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
     package com.example.androidsubpub

     import androidx.appcompat.app.AppCompatActivity
     import android.os.Bundle
     import android.view.View
     import android.widget.*
     import kotlinx.coroutines.runBlocking
     import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
     import aws.sdk.kotlin.services.sns.SnsClient
     import aws.sdk.kotlin.services.sns.model.*
     import java.util.regex.Pattern
     import kotlin.system.exitProcess
     import aws.sdk.kotlin.services.sns.model.SnsException
     import aws.sdk.kotlin.services.sns.model.ListSubscriptionsByTopicRequest
     import aws.sdk.kotlin.services.translate.TranslateClient
     import aws.sdk.kotlin.services.translate.model.TranslateTextRequest

    class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

     var topicArnVal = "<Enter topic ARN>"
     val items = arrayOf("En", "Fr", "Sp")
     var chosenLan: String =""

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dropdown =  findViewById<Spinner>(R.id.spinner)
        dropdown.onItemSelectedListener = this
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter
     }

     override fun onItemSelected(parent: AdapterView<*>?,
                                view: View, position: Int,
                                id: Long) {

        chosenLan = parent?.getItemAtPosition(position).toString()
        val toast = Toast.makeText(applicationContext, chosenLan, Toast.LENGTH_SHORT)
        toast.setMargin(50f, 50f)
        toast.show()
     }

     override fun onNothingSelected(parent: AdapterView<*>?) {
      
     }

    // Publish a message.
    fun pubTopic(view: View) = runBlocking {

        val snsClient = getClient()
        val translateClient = getTranslateClient()
        val bodyMessage: EditText = findViewById(R.id.txtMessage)
        val body = bodyMessage.text.toString()
        var translateBody: String

        // Need to translate the message if user selected another language.
        if (chosenLan == "Fr" ) {

            val textRequest = TranslateTextRequest {
                sourceLanguageCode = "en"
                targetLanguageCode = "fr"
                text = body
            }

            val textResponse = translateClient.translateText(textRequest)
            translateBody = textResponse.translatedText.toString()

        } else if (chosenLan == "Sp" ) {

            val textRequest = TranslateTextRequest {
                    sourceLanguageCode = "en"
                    targetLanguageCode = "es"
                    text = body
                }

            val textResponse = translateClient.translateText(textRequest)
            translateBody = textResponse.translatedText.toString()

        } else
            translateBody = body

        try {

            val request = PublishRequest {
                message = translateBody
                topicArn = topicArnVal
            }

            val result = snsClient.publish(request)
            showToast("{$result.messageId.toString()} published!")

        } catch (e: SnsException) {
            println(e.message)
            snsClient.close()
        }
      }

     // Get all subscriptions.
     fun getSubs(view: View) = runBlocking {

        val subList = mutableListOf<String>()
        val snsClient: SnsClient = getClient()
        try {

            val request = ListSubscriptionsByTopicRequest {
                topicArn = topicArnVal
            }
            val response = snsClient.listSubscriptionsByTopic(request)
            response.subscriptions?.forEach { sub ->
                subList.add(sub.endpoint.toString())
            }

            val listString = java.lang.String.join(", ", subList)
            showToast(listString)

       } catch (e: SnsException) {
            println(e.message)
            snsClient.close()
        }
       }

      // Remove a subscription based on an email.
      fun unSubUser(view: View) = runBlocking {

        val snsClient = getClient()
        val emailVal: EditText = findViewById(R.id.txtEmail)
        val emailStr = emailVal.text.toString()
        val isValidEmail = checkEmail(emailStr)

        // Make sure that the email is valid.
        if (!isValidEmail) {
            showToast("Email not valid")
        } else {
             try {
                 var subArn = ""
                 val listRequest = ListSubscriptionsByTopicRequest {
                     topicArn = topicArnVal
                 }
                 val response = snsClient.listSubscriptionsByTopic(listRequest)
                 response.subscriptions?.forEach { sub ->

                     if (sub.endpoint?.compareTo(emailStr) == 0) {
                         subArn = sub.subscriptionArn.toString()
                     }
                 }

                    val request = UnsubscribeRequest {
                        subscriptionArn = subArn
                    }

                    snsClient.unsubscribe(request)
                    showToast("$emailStr was unsubscribed")

                } catch (e: SnsException) {
                    println(e.message)
                    snsClient.close()
                    exitProcess(0)
                }
          }
       }

      // Create a new subscription.
     fun subUser(view: View) = runBlocking{

        val snsClient = getClient()

        val emailVal: EditText =  findViewById(R.id.txtEmail)
        val emailStr = emailVal.text.toString()
        val isValidEmail = checkEmail(emailStr)

        // Make sure that the email is valid.
        if (!isValidEmail) {
            showToast("Email not valid")
        }
        else {
            try {
                val request = SubscribeRequest {
                    protocol = "email"
                    endpoint = emailStr
                    returnSubscriptionArn = true
                    topicArn = topicArnVal
                }

               val result = snsClient.subscribe(request)
               showToast(result.subscriptionArn.toString())

            } catch (e: SnsException) {
                println(e.message)
                snsClient.close()
                exitProcess(0)
            }
        }
      }

      // Returns the Sub Amazon Resource Name (ARN) based on the given endpoint used for unSub.
      suspend fun getTopicArnValue(snsClient: SnsClient, endpoint: String): String? {

                try {
                    var subArn: String

                    val request = ListSubscriptionsByTopicRequest {
                        topicArn = topicArnVal
                    }

                    val response = snsClient.listSubscriptionsByTopic(request)
                    response.subscriptions?.forEach { sub ->

                        if (sub.endpoint?.compareTo(endpoint) ==0 ) {
                            subArn = sub.subscriptionArn.toString()
                            return subArn
                        }
                    }

                    return ""
                } catch (e: SnsException) {
                    println(e.message)
                    snsClient.close()
                    exitProcess(0)
                }
            }


      fun getTranslateClient() : TranslateClient{

        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = "<Enter key>"
            secretAccessKey = "<Enter key>"
        }

        val translateClient = TranslateClient{
            region = "us-west-2"
            credentialsProvider = staticCredentials
        }

        return translateClient
     }

    fun getClient() : SnsClient{

        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = "<Enter key>"
            secretAccessKey = "<Enter key>"
        }

        val snsClient = SnsClient{
            region = "us-west-2"
            credentialsProvider = staticCredentials
        }

        return snsClient
    }

    fun showToast(value:String){
        val toast = Toast.makeText(applicationContext, value, Toast.LENGTH_SHORT)
        toast.setMargin(50f, 50f)
        toast.show()
    }

    fun checkEmail(email: String?): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
      )
    }
