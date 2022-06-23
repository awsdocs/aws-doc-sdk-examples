/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.photo

import jxl.CellView
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.UnderlineStyle
import jxl.write.*
import jxl.write.Number
import org.springframework.stereotype.Component
import java.io.*
import java.util.*

@Component
class WriteExcel {
    private var timesBoldUnderline: WritableCellFormat? = null
    private var times: WritableCellFormat? = null

    // Returns an InputStream that represents the Excel Report.
    fun exportExcel(list: List<List<*>>): InputStream? {
        try {
            return write(list)
        } catch (e: WriteException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // Generates the report and returns an inputstream.
    @Throws(IOException::class, WriteException::class)
    fun write(list: List<List<*>>): InputStream {
        val os: OutputStream = ByteArrayOutputStream()
        val wbSettings = WorkbookSettings()
        wbSettings.locale = Locale("en", "EN")

        // Create a Workbook - pass the OutputStream.
        val workbook = Workbook.createWorkbook(os, wbSettings)
        val size = list.size
        for (i in 0 until size) {

            // Get the WorkItem from each list.
            val innerList = list[i]
            val wi = innerList[i] as WorkItem
            workbook.createSheet(wi.key.toString() + " Sheet ", 0)
            val excelSheet = workbook.getSheet(0)
            createLabel(excelSheet)
            createContent(excelSheet, innerList)
        }

        // Close the workbook.
        workbook.write()
        workbook.close()

        // Get an inputStram that represents the Report.
        var stream = ByteArrayOutputStream()
        stream = os as ByteArrayOutputStream
        val myBytes = stream.toByteArray()
        return ByteArrayInputStream(myBytes)
    }

    // Create Headings in the Excel spreadsheet.
    @Throws(WriteException::class)
    private fun createLabel(sheet: WritableSheet) {
        // Create a times font.
        val times10pt = WritableFont(WritableFont.TIMES, 10)
        // Define the cell format.
        times = WritableCellFormat(times10pt)
        // Lets automatically wrap the cells.
        times!!.wrap = true

        // Create create a bold font with unterlines.
        val times10ptBoldUnderline = WritableFont(
            WritableFont.TIMES, 10, WritableFont.BOLD, false,
            UnderlineStyle.SINGLE
        )
        timesBoldUnderline = WritableCellFormat(times10ptBoldUnderline)

        // Lets automatically wrap the cells.
        timesBoldUnderline!!.wrap = true
        val cv = CellView()
        cv.format = times
        cv.format = timesBoldUnderline
        cv.isAutosize = true

        // Write a few headers.
        addCaption(sheet, 0, 0, "Photo")
        addCaption(sheet, 1, 0, "Label")
        addCaption(sheet, 2, 0, "Confidence")
    }

    // Write the Work Item Data to the Excel Report.
    @Throws(WriteException::class)
    private fun createContent(sheet: WritableSheet, list: List<*>): Int {
        val size = list.size
        for (i in 0 until size) {
            val wi = list[i] as WorkItem

            // Get tne work item values.
            val key = wi.key
            val label = wi.name
            val confidence= wi.confidence

            // First column.
            addLabel(sheet, 0, i + 2, key.toString())

            // Second column.
            addLabel(sheet, 1, i + 2, label.toString())

            // Third column.
            addLabel(sheet, 2, i + 2, confidence.toString())
        }
        return size
    }

    @Throws(WriteException::class)
    private fun addCaption(sheet: WritableSheet, column: Int, row: Int, s: String) {
        val label: Label
        label = Label(column, row, s, timesBoldUnderline)
        val cc = countString(s)
        sheet.setColumnView(column, cc)
        sheet.addCell(label)
    }

    @Throws(WriteException::class)
    private fun addNumber(
        sheet: WritableSheet, column: Int, row: Int,
        integer: Int
    ) {
        val number: Number
        number = Number(column, row, integer.toDouble(), times)
        sheet.addCell(number)
    }

    @Throws(WriteException::class)
    private fun addLabel(sheet: WritableSheet, column: Int, row: Int, s: String) {
        val label: Label
        label = Label(column, row, s, times)
        val cc = countString(s)
        if (cc > 200) sheet.setColumnView(column, 150) else sheet.setColumnView(column, cc + 6)
        sheet.addCell(label)
    }

    private fun countString(ss: String): Int {
        var count = 0

        //Counts each character except space.
        for (i in 0 until ss.length) {
            if (ss[i] != ' ') count++
        }
        return count
    }
}