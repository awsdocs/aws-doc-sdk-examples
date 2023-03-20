// A structure to represent the information about a movie.
//
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ddb.swift.batchgetitem.movie]
import Foundation
import AWSDynamoDB

// snippet-start:[ddb.swift.batchgetitem.info]
/// The optional details about a movie.
public struct Info: Codable {
    /// The movie's rating, if available.
    var rating: Double?
    /// The movie's plot, if available.
    var plot: String?
}
// snippet-end:[ddb.swift.batchgetitem.info]

public struct Movie: Codable {
    /// The year in which the movie was released.
    var year: Int
    /// The movie's title.
    var title: String
    /// An `Info` object providing the optional movie rating and plot
    /// information.
    var info: Info

    // snippet-start:[ddb.swift.batchgetitem.movie.init]
    /// Create a `Movie` object representing a movie, given the movie's
    /// details.
    ///
    /// - Parameters:
    ///   - title: The movie's title (`String`).
    ///   - year: The year in which the movie was released (`Int`).
    ///   - rating: The movie's rating (optional `Double`).
    ///   - plot: The movie's plot (optional `String`).
    init(title: String, year: Int, rating: Double? = nil, plot: String? = nil) {
        self.title = title
        self.year = year

        self.info = Info(rating: rating, plot: plot)
    }
    // snippet-end:[ddb.swift.batchgetitem.movie.init]

    // snippet-start:[ddb.swift.batchgetitem.movie.init-info]
    /// Create a `Movie` object representing a movie, given the movie's
    /// details.
    ///
    /// - Parameters:
    ///   - title: The movie's title (`String`).
    ///   - year: The year in which the movie was released (`Int`).
    ///   - info: The optional rating and plot information for the movie in an
    ///     `Info` object.
    init(title: String, year: Int, info: Info?) {
        self.title = title
        self.year = year

        if info != nil {
            self.info = info!
        } else {
            self.info = Info(rating: nil, plot: nil)
        }
    }
    // snippet-end:[ddb.swift.batchgetitem.movie.init-info]

    // snippet-start:[ddb.swift.batchgetitem.movie.init-withitem]
    ///
    /// Return a new `MovieTable` object, given an array mapping string to Amazon
    /// DynamoDB attribute values.
    /// 
    /// - Parameter item: The item information provided in the form used by
    ///   DynamoDB. This is an array of strings mapped to
    ///   `DynamoDBClientTypes.AttributeValue` values.
    init(withItem item: [Swift.String:DynamoDBClientTypes.AttributeValue]) throws {
        // Read the attributes.

        guard let titleAttr = item["title"],
              let yearAttr = item["year"] else {
            throw MovieError.ItemNotFound
        }
        let infoAttr = item["info"] ?? nil

        // Extract the values of the title and year attributes.

        if case .s(let titleVal) = titleAttr {
            self.title = titleVal
        } else {
            throw MovieError.InvalidAttributes
        }

        if case .n(let yearVal) = yearAttr {
            self.year = Int(yearVal)!
        } else {
            throw MovieError.InvalidAttributes
        }

        // Extract the rating and/or plot from the `info` attribute, if
        // they're present.

        var rating: Double? = nil
        var plot: String? = nil

        if case .m(let infoVal) = infoAttr {
            let ratingAttr = infoVal["rating"] ?? nil
            let plotAttr = infoVal["plot"] ?? nil

            if ratingAttr != nil, case .n(let ratingVal) = ratingAttr {
                rating = Double(ratingVal) ?? nil
            }
            if plotAttr != nil, case .s(let plotVal) = plotAttr {
                plot = plotVal
            }
        }

        self.info = Info(rating: rating, plot: plot)
    }
    // snippet-end:[ddb.swift.batchgetitem.movie.init-withitem]

    // snippet-start:[ddb.swift.batchgetitem.movie.getasitem]
    ///
    /// Return an array mapping attribute names to Amazon DynamoDB attribute
    /// values, representing the contents of the `Movie` record as a DynamoDB
    /// item.
    ///
    /// - Returns: The movie item as an array of type
    ///   `[Swift.String:DynamoDBClientTypes.AttributeValue]`.
    ///
    func getAsItem() async throws -> [Swift.String:DynamoDBClientTypes.AttributeValue]  {
        // Build the item record, starting with the year and title, which are
        // always present.

        var item: [Swift.String:DynamoDBClientTypes.AttributeValue] = [
            "year": .n(String(self.year)),
            "title": .s(self.title)
        ]

        // Add the `info` field with the rating and/or plot if they're
        // available.

        var info: [Swift.String:DynamoDBClientTypes.AttributeValue] = [:]
        if self.info.rating != nil {
            info["rating"] = .n(String(self.info.rating!))
        }
        if self.info.plot != nil {
            info["plot"] = .s(self.info.plot!)
        }
        item["info"] = .m(info)

        return item
    }
    // snippet-end:[ddb.swift.batchgetitem.movie.getasitem]
}
// snippet-end:[ddb.swift.batchgetitem.movie]
