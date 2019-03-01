//snippet-sourcedescription:[TranscoderSampleUtilities.java provides basic useful utils for transcoding jobs.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic Transcoder]
//snippet-service:[elastictranscoder]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
// snippet-start:[elastictranscoder.java.sample_utilities.import]
package com.amazonaws.services.elastictranscoder.samples.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class TranscoderSampleUtilities {

    private static final char[] HEX_ENCODING = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
                                                            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    
    /**
     * Method converts a byte array into a hex string.
     * @param input
     * @return String representing hex encoded byte array.
     */
    public static String toHexString(byte[] input) {
        StringBuilder builder = new StringBuilder(input.length * 2);
        for (byte b : input) {
            builder.append(HEX_ENCODING[((int)b & 0xff) >> 4]);
            builder.append(HEX_ENCODING[b & 0x0f]);
        }
        return builder.toString();
    }
    
    /**
     * Method converts an input key into an output key but computing
     * BASE16(SHA256(UTF-8(inputKey))).
     * @param inputKey
     * @return Output key generated from the input key.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String inputKeyToOutputKey(String inputKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return toHexString(digest.digest(inputKey.getBytes("UTF-8")));
    }
}
// snippet-end:[elastictranscoder.java.sample_utilities.import]
