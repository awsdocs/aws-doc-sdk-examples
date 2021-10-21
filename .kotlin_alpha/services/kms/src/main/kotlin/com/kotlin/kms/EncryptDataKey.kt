//snippet-sourcedescription:[EncryptDataKey.kt demonstrates how to encrypt and decrypt data by using an AWS Key Management Service (KMS) key.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/03/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.kms

// snippet-start:[kms.kotlin_encrypt_data.import]
import aws.sdk.kotlin.services.kms.KmsClient
import aws.sdk.kotlin.services.kms.model.DecryptRequest
import aws.sdk.kotlin.services.kms.model.EncryptRequest
import aws.sdk.kotlin.services.kms.model.KmsException
import java.io.FileOutputStream
import java.io.File
import java.io.OutputStream
import kotlin.system.exitProcess
// snippet-end:[kms.kotlin_encrypt_data.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main (args: Array<String>) {

    val usage = """
        Usage:
            <keyId> <path>
        Where:
            keyId - a key id value to describe (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). 
            path - the path of a text file where the data is written to (for example, C:\AWS\TextFile.txt). 
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val keyId = args[0]
    val path = args[1]
    val keyClient = KmsClient{region="us-west-2"}
    val encryptedData = encryptData(keyClient, keyId)
    decryptData(keyClient, encryptedData, keyId, path)
    keyClient.close()
}

// snippet-start:[kms.kotlin_encrypt_data.main]
suspend fun encryptData(kmsClient: KmsClient, keyIdValue: String): ByteArray? {
        try {

            val text = "This is the text to encrypt by using the AWS KMS Service"
            val myBytes: ByteArray = text.toByteArray()

            val encryptRequest = EncryptRequest {
                keyId = keyIdValue
                plaintext = myBytes
            }

            val response = kmsClient.encrypt(encryptRequest)
            val algorithm: String = response.encryptionAlgorithm.toString()
            println("The encryption algorithm is $algorithm")

            // Return the encrypted data.
            return response.ciphertextBlob

        } catch (ex: KmsException) {
            println(ex.message)
            kmsClient.close()
            exitProcess(0)
        }
     }

   suspend fun decryptData(kmsClient: KmsClient, encryptedDataVal: ByteArray?, keyIdVal: String?, path: String?) {
        try {
            val decryptRequest = DecryptRequest{
                ciphertextBlob = encryptedDataVal
                keyId=keyIdVal
            }
            val decryptResponse = kmsClient.decrypt(decryptRequest)
            val myVal = decryptResponse.plaintext

            // Write the decrypted data to a file.
            val myFile: File = File(path)
            val os: OutputStream = FileOutputStream(myFile)
            os.write(myVal)

        } catch (ex: KmsException) {
            println(ex.message)
            exitProcess(1)
        }
  }
// snippet-end:[kms.kotlin_encrypt_data.main]