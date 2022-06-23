/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.awsapp

import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.sns.SnsClient
import kotlinx.coroutines.runBlocking
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun submitData() = runBlocking{

        val dollarField: EditText =  findViewById(R.id.dollarText)
        val nameField: EditText =  findViewById(R.id.personName)
        val emailField: EditText =  findViewById(R.id.emailAddress)
        val dateField: EditText = findViewById(R.id.editDate)

        val data = Database()

        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = "<Enter your key>"
            secretAccessKey = "<Enter your secret key>"
        }

        val ddb = DynamoDbClient{
            region = "us-east-1"
            credentialsProvider = staticCredentials
        }

        // Set values to save in the Amazon DynamoDB table.
        val uuid: UUID = UUID.randomUUID()
        val tableName = "Android"
        val key = "id"
        val keyVal = uuid.toString()
        val moneyTotal = "Value"
        val moneyTotalValue = dollarField.text.toString()
        val name = "Name"
        val NameVal = nameField.text.toString()
        val email = "email"
        val emailVal = emailField.text.toString()
        val date = "date"
        val dateVal = dateField.text.toString()

        data.putItemInTable2(ddb, tableName, key, keyVal, moneyTotal, moneyTotalValue, name, NameVal, email, emailVal, date, dateVal)
        showToast("Item added")

        // Notify user.
        val snsClient = SnsClient{
            region = "us-east-1"
            credentialsProvider = staticCredentials
        }

        val sendMSG = SendMessage()
        val mobileNum = "<ENTER MOBILE NUMBER>"
        val message = "Item $uuid was added!"
        sendMSG.pubTextSMS( snsClient,message, mobileNum )
    }

    fun showToast(value:String){
        val toast = Toast.makeText(applicationContext, value, Toast.LENGTH_SHORT)
        toast.setMargin(50f, 50f)
        toast.show()
    }
}
