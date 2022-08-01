// snippet-sourcedescription:[PollyDemo.kt demonstrates how to convert text into speech.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Polly]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.polly

// snippet-start:[polly.kotlin.demo.import]
import aws.sdk.kotlin.services.polly.PollyClient
import aws.sdk.kotlin.services.polly.model.DescribeVoicesRequest
import aws.sdk.kotlin.services.polly.model.Engine
import aws.sdk.kotlin.services.polly.model.OutputFormat
import aws.sdk.kotlin.services.polly.model.SynthesizeSpeechRequest
import aws.smithy.kotlin.runtime.content.toByteArray
import javazoom.jl.player.FactoryRegistry
import javazoom.jl.player.advanced.AdvancedPlayer
import javazoom.jl.player.advanced.PlaybackEvent
import javazoom.jl.player.advanced.PlaybackListener
import java.io.ByteArrayInputStream
// snippet-end:[polly.kotlin.demo.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/
suspend fun main() {
    talkPolly()
}

// snippet-start:[polly.kotlin.demo.main]
suspend fun talkPolly() {

    val sample = "Congratulations. You have successfully built this working demo " +
        " of Amazon Polly in Kotlin. Have fun building voice enabled apps with Amazon Polly (that's me!), and always " +
        " look at the AWS website for tips and tricks on using Amazon Polly and other great services from AWS"

    val describeVoiceRequest = DescribeVoicesRequest {
        engine = Engine.Standard
    }

    PollyClient { region = "us-west-2" }.use { polly ->
        val describeVoicesResult = polly.describeVoices(describeVoiceRequest)
        val voice = describeVoicesResult.voices?.get(26)
        polly.synthesizeSpeech(
            SynthesizeSpeechRequest {
                text = sample
                voiceId = voice?.id
                outputFormat = OutputFormat.Mp3
            }
        ) { resp ->

            // inside this block you can access `resp` and play the audio stream.
            val audioData = resp.audioStream?.toByteArray()
            val targetStream = ByteArrayInputStream(audioData)
            val player = AdvancedPlayer(targetStream, FactoryRegistry.systemRegistry().createAudioDevice())
            player.playBackListener = object : PlaybackListener() {
                override fun playbackStarted(evt: PlaybackEvent) {
                    println("Playback started")
                    println(sample)
                }

                override fun playbackFinished(evt: PlaybackEvent) {
                    println("Playback finished")
                }
            }
            // play it!
            player.play()
        }
    }
}
// snippet-end:[polly.kotlin.demo.main]
