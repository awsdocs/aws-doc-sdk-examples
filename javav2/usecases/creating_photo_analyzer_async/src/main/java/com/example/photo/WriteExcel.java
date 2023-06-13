/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

@Component
public class WriteExcel {
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;

    // Returns an InputStream that represents the Excel Report.
    public InputStream exportExcel(List<List<WorkItem>> list) {
        try {
            return write(list);
        } catch (WriteException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Generates the report and returns an inputstream.
    public InputStream write(List<List<WorkItem>> list) throws IOException, WriteException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook = Workbook.createWorkbook(os, wbSettings);

        int size = list.size();
        for (int i = 0; i < size; i++) {
            // Get the WorkItem from each list.
            List<WorkItem> innerList = list.get(i);
            workbook.createSheet("Sheet " + (i + 1), i);
            WritableSheet excelSheet = workbook.getSheet(i);
            createLabel(excelSheet);
            createContent(excelSheet, innerList);
        }

        // Close the workbook.
        workbook.write();
        workbook.close();

        // Get an InputStream that represents the Report.
        byte[] myBytes = os.toByteArray();
        return new ByteArrayInputStream(myBytes);
    }

    // Create Headings in the Excel spreadsheet.
    private void createLabel(WritableSheet sheet) throws WriteException {
        // Create a times font.
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format.
        times = new WritableCellFormat(times10pt);
        // Let's automatically wrap the cells.
        times.setWrap(true);

        // Create a bold font with underlines.
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
            UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);

        // Let's automatically wrap the cells.
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        // Write a few headers.
        addCaption(sheet, 0, 0, "Photo");
        addCaption(sheet, 1, 0, "Label");
        addCaption(sheet, 2, 0, "Confidence");
    }

    // Write the WorkItem Data to the Excel Report.
    private void createContent(WritableSheet sheet, List<WorkItem> list) throws WriteException {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            WorkItem wi = list.get(i);
            String key = wi.getKey();
            String label = wi.getName();
            String confidence = wi.getConfidence();

            // First column.
            addLabel(sheet, 0, i + 1, key);

            // Second column.
            addLabel(sheet, 1, i + 1, label);

            // Third column.
            addLabel(sheet, 2, i + 1, confidence);
        }
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label = new Label(column, row, s, timesBoldUnderline);
        int cc = countString(s);
        sheet.setColumnView(column, cc);
        sheet.addCell(label);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s) throws WriteException {
        Label label = new Label(column, row, s, times);
        int cc = countString(s);
        if (cc > 200) {
            sheet.setColumnView(column, 150);
        } else {
            sheet.setColumnView(column, cc + 6);
        }
        sheet.addCell(label);
    }

    private int countString(String ss) {
        int count = 0;
        for (int i = 0; i < ss.length(); i++) {
            if (ss.charAt(i) != ' ') {
                count++;
            }
        }
        return count;
    }
}
