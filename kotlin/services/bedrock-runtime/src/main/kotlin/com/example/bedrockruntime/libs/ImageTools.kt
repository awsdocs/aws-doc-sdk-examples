// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.libs

import java.io.ByteArrayInputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

/**
 * Utility object for handling image-related operations.
 */
object ImageTools {
    /**
     * Displays a byte array as an image in a new window.
     *
     * Creates a new JFrame window that displays the image represented by the provided byte array.
     * The window will close the application when closed (EXIT_ON_CLOSE).
     *
     * @param imageData The image data as a byte array
     * @throws RuntimeException if there is an error reading the image data
     */
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
