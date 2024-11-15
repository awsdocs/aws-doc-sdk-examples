﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace PartiQL_Batch_Scenario.Tests
{
    [TestCaseOrderer("PartiQLBatchMethods.Tests.PriorityOrderer", " PartiQLBatchMethods.Tests")]
    public class PartiQLBatchMethodsTests
    {
        const string tableName = "test_movie_table";

        public PartiQLBatchMethodsTests()
        {
            _ = DynamoDBMethods.CreateMovieTableAsync(tableName);
        }

        [Fact(Skip = "Quarantined test."), TestPriority(1)]
        [Trait("Category", "Integration")]
        public async Task InsertMoviesTest()
        {
            // Because InsertMovies calls ImportMovies, there is not
            // need to test ImportMovies separately.
            const string movieFileName = "moviedata.json";
            var success = await PartiQLBatchMethods.InsertMovies(tableName, movieFileName);
            Assert.True(success, "Could not insert the movies into the table.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(2)]
        [Trait("Category", "Integration")]
        public async Task GetBatchTest()
        {
            // Update multiple movies by using the BatchExecute statement.
            var title1 = "Star Wars";
            var year1 = 1977;
            var title2 = "Wizard of Oz";
            var year2 = 1939;

            var success = await PartiQLBatchMethods.GetBatch(tableName, title1, title2, year1, year2);
            Assert.True(success, $"Could not update {title1} or {title2}.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(3)]
        [Trait("Category", "Integration")]
        public async Task UpdateBatchTest()
        {
            // Update multiple movies by using the BatchExecute statement.
            var producer1 = "LucasFilm";
            var title1 = "Star Wars";
            var year1 = 1977;
            var producer2 = "MGM";
            var title2 = "Wizard of Oz";
            var year2 = 1939;

            var success = await PartiQLBatchMethods.UpdateBatch(tableName, producer1, title1, year1, producer2, title2, year2);
            Assert.True(success, $"Could not update {title1} or {title2}.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(4)]
        [Trait("Category", "Integration")]
        public async Task DeleteBatchTest()
        {
            // Update multiple movies by using the BatchExecute statement.
            var title1 = "Star Wars";
            var year1 = 1977;
            var title2 = "Wizard of Oz";
            var year2 = 1939;

            var success = await PartiQLBatchMethods.DeleteBatch(tableName, title1, year1, title2, year2);
            Assert.True(success, $"Could not delete {title1} or {title2}");
        }
    }
}