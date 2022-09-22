// ValueOutput
//
// Output a set of data formatted neatly.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

import Foundation

public struct ValueRow {
    var title: String
    var value: String

    // What to print for values of true and false
    var trueValue: String = ""
    var falseValue: String = ""

    init(title: String, value: Int) {
        self.title = title
        self.value = String(value)
    }

    init(title: String, value: String) {
        self.title = title
        self.value = value
    }

    init(title: String, value: Bool, trueValue: String = "Yes", falseValue: String = "No") {
        self.trueValue = trueValue
        self.falseValue = falseValue

        self.title = title
        
        if value == true {
            self.value = self.trueValue
        } else {
            self.value = self.falseValue
        }
    }
}

public class ValueOutput {
    var header: String?
    var rows: [ValueRow] = []
    var gapWidth: Int

    init(header: String? = nil, gap: Int = 4) {
        self.header = header
        self.gapWidth = gap
    }

    func addRow(title: String, value: Int) {
        rows.append(ValueRow(title: title, value: value))
    }

    func addRow(title: String, value: String) {
        rows.append(ValueRow(title: title, value: value))
    }

    func addRow(title: String, value: Bool) {
        rows.append(ValueRow(title: title, value: value))
    }

    var titleWidth: Int {
        var width = 0

        for row in self.rows {
            if row.title.count > width {
                width = row.title.count + self.gapWidth
            }
        }
        return width
    }

    var valueWidth: Int {
        var width = 0

        for row in self.rows {
            if row.value.count > width {
                width = row.value.count
            }
        }
        return width
    }

    var divider: String {
        return String(repeating: "=", count: self.titleWidth + self.valueWidth)
    }

    func output() {
        print("Title width: \(self.titleWidth)")
        if self.header != nil {
            print(self.header!)
            print(self.divider)
        }

        for row in rows {
            let titleLength = row.title.length
            let padding = self.titleWidth - titleLength
            print("\(row.title)\(String(repeating: " ", count: padding))\(row.value)")
        }
    }
}