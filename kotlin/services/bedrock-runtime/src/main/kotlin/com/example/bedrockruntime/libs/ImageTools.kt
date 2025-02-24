// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs

import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

object ImageTools {
    fun displayImage(imageData: ByteArray) {
        try {
            val image = ImageIO.read(ByteArrayInputStream(imageData))
            JFrame("Image").apply {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                contentPane.add(JLabel(ImageIcon(image)))
                pack()
                isVisible = true
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}