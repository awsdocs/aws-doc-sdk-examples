/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import XCTest
import Foundation
import AWSDynamoDB
import SwiftUtilities

@testable import MovieList

/// Perform tests on the S3Basics program. Call Amazon S3 service functions
/// using the global `BasicsTests.serviceHandler` property, and manage the demo
/// cleanup handler object using the global `BasicsTests.demoCleanup` property.
final class MovieTests: XCTestCase {
    /// Class-wide setup function for the test case, which is run *once* before
    /// any tests are run.
    /// 
    /// This function sets up the following:
    ///
    ///     Configures the AWS SDK log system to only log errors.
    ///     Instantiates the service handler, which is used to call
    ///     Amazon S3 functions.
    ///     Instantiates the demo cleanup handler, which is used to
    ///     track the names of the files and buckets created by the tests
    ///     in order to remove them after testing is complete.
    override class func setUp() {
        super.setUp()
    }

    func testSimpleCreateMovie() {
        // Test creating a movie with only the title and year.

        let movie1 = Movie(title: "Some Title", year: 2020)
        XCTAssertEqual("Some Title", movie1.title, "Movie 1 title mismatch.")
        XCTAssertEqual(2020, movie1.year, "Movie 1 year mismatch.")
        XCTAssertTrue(movie1.info.rating == nil && movie1.info.plot == nil,
                    "Movie 1 should have nil rating and plot but doesn't.")

        // Test creating a movie with title, year, and rating.

        let movie2 = Movie(title: "Some Other Title", year: 1955, rating: 5.0)
        XCTAssertEqual("Some Other Title", movie2.title, "Movie 2 title mismatch.")
        XCTAssertEqual(1955, movie2.year, "Movie 2 year mismatch.")
        XCTAssertEqual(5.0, movie2.info.rating, "Movie 2 rating mismatch.")
        XCTAssertEqual(nil, movie2.info.plot,
                    "Movie 2 should have nil plot but doesn't.")
        
        // Test creating a movie with all information.

        let movie3 = Movie(title: "Yet Another Title", year: 1955, rating: 5.0,
                    plot: "Some twisted storyline.")
        XCTAssertEqual("Yet Another Title", movie3.title, "Movie 3 title mismatch.")
        XCTAssertEqual(1955, movie3.year, "Movie 3 year mismatch.")
        XCTAssertEqual(5.0, movie3.info.rating, "Movie 3 rating mismatch.")
        XCTAssertEqual("Some twisted storyline.", movie3.info.plot,
                    "Movie 3 plot mismatch.")
    }

    func testCreateMovieWithDetails() {
        var deets = Details(rating: nil, plot: nil)

        // Create a movie with info set to nil.

        let movie1Title = "First Title"
        let movie1Year = 2001
        let movie1 = Movie(title: movie1Title, year: movie1Year,
                    info: nil)
        XCTAssertEqual(movie1Title, movie1.title, "Movie 1 title mismatch.")
        XCTAssertEqual(movie1Year, movie1.year, "Movie 1 year mismatch.")
        XCTAssertEqual(movie1.info.rating, nil, "Movie 1 rating should be nil.")
        XCTAssertEqual(movie1.info.plot, nil, "Movie 1 plot should be nil.")

        // Create a movie with info.rating and info.plot both nil.

        let movie2Title = "Second Title"
        let movie2Year = 1995
        let movie2 = Movie(title: movie2Title, year: movie2Year, info: deets)
        XCTAssertEqual(movie2Title, movie2.title, "Movie 2 title mismatch.")
        XCTAssertEqual(movie2Year, movie2.year, "Movie 2 year mismatch.")
        XCTAssertEqual(movie2.info.rating, nil, "Movie 2 rating should be nil.")
        XCTAssertEqual(movie2.info.plot, nil, "Movie 2 plot should be nil.")

        // Create a movie with a value for info.rating but nil for plot

        let movie3Title = "Third Title"
        let movie3Year = 1996
        deets.rating = 9.0
        let movie3 = Movie(title: movie3Title, year: movie3Year, info: deets)
        XCTAssertEqual(movie3Title, movie3.title, "Movie 3 title mismatch.")
        XCTAssertEqual(movie3Year, movie3.year, "Movie 3 year mismatch.")
        XCTAssertEqual(movie3.info.rating, deets.rating, "Movie 3 rating mismatch.")
        XCTAssertEqual(movie3.info.plot, deets.plot, "Movie 3 plot should be nil.")

        // Create a movie with values for both info.rating and info.plot

        let movie4Title = "Fourth Title"
        let movie4Year = 1999
        deets.rating = 8.1
        let movie4 = Movie(title: movie4Title, year: movie4Year, info: deets)
        XCTAssertEqual(movie4Title, movie4.title, "Movie 4 title mismatch.")
        XCTAssertEqual(movie4Year, movie4.year, "Movie 4 year mismatch.")
        XCTAssertEqual(movie4.info.rating, deets.rating, "Movie 4 rating mismatch.")
        XCTAssertEqual(movie4.info.plot, deets.plot, "Movie 4 plot mismatch.")
    }

    func testCreateMovieWithItem() throws {
        let movieYear = 1965
        let movieTitle = "Hay Hay Hay"
        let movieRating = 7.2
        let moviePlot = "An gripping search for hay."

        let item: [Swift.String:DynamoDBClientTypes.AttributeValue] = [
            "year": .n(String(movieYear)),
            "title": .s(movieTitle),
            "info": .m([
                "rating": .n(String(movieRating)),
                "plot": .s(moviePlot)
            ])
        ]
        let movie = try Movie(withItem: item)
        
        XCTAssertEqual(movieYear, movie.year, "Movie year mismatch.")
        XCTAssertEqual(movieTitle, movie.title, "Movie title mismatch.")
        XCTAssertEqual(movieRating, movie.info.rating, "Movie rating mismatch.")
        XCTAssertEqual(moviePlot, movie.info.plot, "Movie plot mismatch.")
    }

    func testGetAsItem() async throws {
        let movieYear = 1972
        let movieTitle = "Birthday"
        let movieRating = 9.7
        let moviePlot = "A slapstick birthday gone wrong romp."

        let movie = Movie(title: movieTitle, year: movieYear,
                    rating: movieRating, plot: moviePlot)

        // Get the movie as a DynamoDB item.

        let item = try await movie.getAsItem()

        // Get the item attributes.

        guard let titleAttr = item["title"] else {
            throw MoviesError.ItemNotFound
        }
        guard let yearAttr = item["year"] else {
            throw MoviesError.ItemNotFound
        }
        let infoAttr = item["info"] ?? nil

        // Get the values of the attributes.

        var gotTitle: String? = nil
        var gotYear: Int? = nil
        var gotRating: Double? = nil
        var gotPlot: String? = nil

        if case .s(let titleVal) = titleAttr {
            gotTitle = titleVal
        } else {
            XCTFail("Missing title value.")
        }

        if case .n(let yearVal) = yearAttr {
            gotYear = Int(yearVal)!
        } else {
            XCTFail("Missing year value.")
        }

        // Extract the rating and/or plot from the `info` attribute, if
        // they're present.

        if infoAttr != nil, case .m(let infoVal) = infoAttr {
            let ratingAttr = infoVal["rating"] ?? nil
            let plotAttr = infoVal["plot"] ?? nil

            if ratingAttr != nil, case .n(let ratingVal) = ratingAttr {
                gotRating = Double(ratingVal) ?? nil
            }
            if plotAttr != nil, case .s(let plotVal) = plotAttr {
                gotPlot = plotVal
            }
        }

        // Compare the retrieved values to the originals.

        XCTAssertEqual(movieTitle, gotTitle, "Title mismatch.")
        XCTAssertEqual(movieYear, gotYear, "Year mismatch.")
        XCTAssertEqual(movieRating, gotRating, "Rating mismatch.")
        XCTAssertEqual(moviePlot, gotPlot, "Plot mismatch.")
    }
}