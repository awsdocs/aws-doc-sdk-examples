//snippet-sourcedescription:[StockTrade.kt is a helper class.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kinesis Data Firehose]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon - aws]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.firehose

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

/**
 * Captures the key elements of a stock trade, such as the ticker symbol, price,
 * number of shares, the type of the trade (buy or sell), and an id uniquely identifying
 * the trade.
 */
class StockTrade(tickerSymbol: String?, tradeType: TradeType?, price: Double, quantity: Long, id: Long) {

    companion object {
        private val JSON = ObjectMapper()

        init {
            JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    /**
     * Represents the type of the stock trade, e.g. buy or sell.
     */
    enum class TradeType {
        BUY, SELL
    }

    var tickerSymbol: String? = tickerSymbol
        private set

    var tradeType: TradeType? = tradeType
        private set

    var price = price
        private set

    var quantity: Long = quantity
        private set

    var id: Long = id
        private set

    fun toJsonAsBytes(): ByteArray? {
        return try {
            JSON.writeValueAsBytes(this)
        } catch (e: IOException) {
            null
        }
    }

    override fun toString(): String {
        return String.format(
            "ID %d: %s %d shares of %s for $%.02f",
            id, tradeType, quantity, tickerSymbol, price
        )
    }
}