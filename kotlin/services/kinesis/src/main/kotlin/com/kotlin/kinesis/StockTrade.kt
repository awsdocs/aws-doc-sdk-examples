// snippet-sourcedescription:[StockTrade.kt is a helper class.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Kinesis]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.kinesis

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

/**
 * Captures the key elements of a stock trade, such as the ticker symbol, price,
 * number of shares, the type of the trade (buy or sell), and an id uniquely identifying
 * the trade.
 */
class StockTrade {
    companion object {
        private val JSON = ObjectMapper()
        fun fromJsonAsBytes(bytes: ByteArray?): StockTrade? {
            return try {
                JSON.readValue(bytes, StockTrade::class.java)
            } catch (e: IOException) {
                null
            }
        }

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

    var tickerSymbol: String? = null
        private set

    var tradeType: TradeType? = null
        private set

    var price = 0.0
        private set

    var quantity: Long = 0
        private set

    var id: Long = 0
        private set

    constructor() {}
    constructor(tickerSymbol: String?, tradeType: TradeType?, price: Double, quantity: Long, id: Long) {
        this.tickerSymbol = tickerSymbol
        this.tradeType = tradeType
        this.price = price
        this.quantity = quantity
        this.id = id
    }

    fun getTheTickerSymbol(): String {
        return tickerSymbol!!
    }

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
