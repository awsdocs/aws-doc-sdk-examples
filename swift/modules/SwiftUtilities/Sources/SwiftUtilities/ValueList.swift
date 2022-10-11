// ValueOutput
//
// Output a set of data formatted neatly.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0.

import Foundation

public struct ValueItem {
    var title: String
    var value: String

    // What to print for values of true and false
    var trueValue: String = ""
    var falseValue: String = ""

    init(title: String, value: Int = 0) {
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

public class ValueList {
    var header: String?
    var items: [ValueItem] = []
    let gap = 4

    /// Initialize a new `ValueList`.
    ///
    /// - Parameters:
    ///   - header: A title string for the entire list.
    ///   - gap: Minimum number of spaces that should separate the columns.
    init(header: String? = nil) {
        self.header = header
    }

    /// Add a new integer item to the list.
    ///
    /// - Parameters:
    ///   - title: A title for the value.
    ///   - value: The `Int` value.
    func addItem(title: String, value: Int) {
        items.append(ValueItem(title: title, value: value))
    }

    /// Add a new string item to the list.
    ///
    /// - Parameters:
    ///   - title: A title for the value.
    ///   - value: The `String` value.
    func addItem(title: String, value: String) {
        items.append(ValueItem(title: title, value: value))
    }

    /// Add a new Boolean item to the list.
    ///
    /// - Parameters:
    ///   - title: A title for the value.
    ///   - value: The `Bool` value.
   func addItem(title: String, value: Bool) {
        items.append(ValueItem(title: title, value: value))
    }

    /// Return the number of items in the list.
    func getItemCount() -> Int {
        return self.items.count
    }

    /// The width of the title column, not including the gap.
    private var titleWidth: Int {
        var width = 0

        for item in self.items {
            if item.title.count > width {
                width = item.title.count
            }
        }
        
        return width
    }

    /// The width of the gap. Zero if no gap. This is computed to take into
    /// account whether or not the list is empty, and any other factors.
    var gapWidth: Int {
        get {
            if self.items.count == 0 {
                return 0
            } else {
                return self.gap
            }
        }
    }

    /// The width of the value column in characters.
    private var valueWidth: Int {
        get {
            var width = 0

            for item in self.items {
                if item.value.count > width {
                    width = item.value.count
                }
            }
            return width
        }
    }

    // The computed total width of the table's contents, including the title
    // and value columns and the gap.
    private var tableWidth: Int {
        let dataWidth = self.titleWidth + self.gapWidth + self.valueWidth

        guard let header = self.header else {
            return dataWidth
        }

        if dataWidth > header.count {
            return dataWidth
        }
        return header.count
    }

    /// A divider string the width of the entire output area.
    private var divider: String {
        if self.tableWidth > 0 {
            return String(repeating: "=", count: self.tableWidth)
        }

        return ""
    }

    /// Return a string containing the formatted data ready for printing.
    /// 
    /// - Returns: A `String` containing the entire formatted output, ready to
    ///   be displayed on the console.
    func getFormattedOutput() -> String {
        var output = ""

        if self.header != nil {
            output += self.header! + "\n" + self.divider + "\n"
        }

        for item in items {
            let titleLength = item.title.count

            let padding = self.titleWidth + self.gapWidth - titleLength
            
            output += "\(item.title)\(String(repeating: " ", count: padding))\(item.value)\n"
        }
        return output
    }
}