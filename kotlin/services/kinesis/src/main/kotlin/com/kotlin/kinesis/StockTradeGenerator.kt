// snippet-sourcedescription:[StockTradeGenerator.kt is a helper class.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Amazon Kinesis]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.kotlin.kinesis

import com.kotlin.kinesis.StockTrade.TradeType
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayList

/**
 * Generates random stock trades by picking randomly from a collection of stocks, assigning a
 * random price based on the mean, and picking a random quantity for the shares.
 *
 */
class StockTradeGenerator {

    var randomTrade: StockTrade = StockTrade()

    companion object {
        private val STOCK_PRICES: MutableList<StockPrice> = ArrayList()

        /** The ratio of the deviation from the mean price  */
        private const val MAX_DEVIATION = 0.2 // ie 20%

        /** The number of shares is picked randomly between 1 and the MAX_QUANTITY  */
        private const val MAX_QUANTITY = 10000

        /** Probability of trade being a sell  */
        private const val PROBABILITY_SELL = 0.4 // ie 40%

        init {
            STOCK_PRICES.add(StockPrice("AAPL", 119.72))
            STOCK_PRICES.add(StockPrice("XOM", 91.56))
            STOCK_PRICES.add(StockPrice("GOOG", 527.83))
            STOCK_PRICES.add(StockPrice("BRK.A", 223999.88))
            STOCK_PRICES.add(StockPrice("MSFT", 42.36))
            STOCK_PRICES.add(StockPrice("WFC", 54.21))
            STOCK_PRICES.add(StockPrice("JNJ", 99.78))
            STOCK_PRICES.add(StockPrice("WMT", 85.91))
            STOCK_PRICES.add(StockPrice("CHL", 66.96))
            STOCK_PRICES.add(StockPrice("GE", 24.64))
            STOCK_PRICES.add(StockPrice("NVS", 102.46))
            STOCK_PRICES.add(StockPrice("PG", 85.05))
            STOCK_PRICES.add(StockPrice("JPM", 57.82))
            STOCK_PRICES.add(StockPrice("RDS.A", 66.72))
            STOCK_PRICES.add(StockPrice("CVX", 110.43))
            STOCK_PRICES.add(StockPrice("PFE", 33.07))
            STOCK_PRICES.add(StockPrice("FB", 74.44))
            STOCK_PRICES.add(StockPrice("VZ", 49.09))
            STOCK_PRICES.add(StockPrice("PTR", 111.08))
            STOCK_PRICES.add(StockPrice("BUD", 120.39))
            STOCK_PRICES.add(StockPrice("ORCL", 43.40))
            STOCK_PRICES.add(StockPrice("KO", 41.23))
            STOCK_PRICES.add(StockPrice("T", 34.64))
            STOCK_PRICES.add(StockPrice("DIS", 101.73))
        }
    }

    private val random = Random()
    private val id = AtomicLong(1)

    /**
     * Return a random stock trade with a unique id every time.
     */
    fun getSampleData(): StockTrade {

        // pick a random stock
        val stockPrice = STOCK_PRICES[random.nextInt(STOCK_PRICES.size)]
        val deviation = (random.nextDouble() - 0.5) * 2.0 * MAX_DEVIATION
        var price = stockPrice.price * (1 + deviation)
        price = Math.round(price * 100.0) / 100.0

        // set the trade type to buy or sell depending on the probability of sell.
        var tradeType = TradeType.BUY
        if (random.nextDouble() < PROBABILITY_SELL) {
            tradeType = TradeType.SELL
        }

        // randomly pick a quantity of shares.
        val quantity =
            random.nextInt(MAX_QUANTITY) + 1.toLong() // add 1 because nextInt() will return between 0 (inclusive)
        return StockTrade(stockPrice.tickerSymbol, tradeType, price, quantity, id.getAndIncrement())
    }

    private class StockPrice constructor(var tickerSymbol: String, var price: Double)
}
