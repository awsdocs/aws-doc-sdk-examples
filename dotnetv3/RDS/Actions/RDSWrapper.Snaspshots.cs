// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.RDS.Model;

namespace RDSActions;

// snippet-start:[RDS.dotnetv3.RdsInstanceWrapperSnapshots]

/// <summary>
/// Wrapper methods to use Amazon Relational Database Service (Amazon RDS) with snapshots.
/// </summary>
public partial class RDSWrapper
{
    // snippet-start:[RDS.dotnetv3.CreateDBSnapshot]

    /// <summary>
    /// Create a snapshot of a DB instance.
    /// </summary>
    /// <param name="dbInstanceIdentifier">DB instance identifier.</param>
    /// <param name="snapshotIdentifier">Identifier for the snapshot.</param>
    /// <returns>DB snapshot object.</returns>
    public async Task<DBSnapshot> CreateDBSnapshot(string dbInstanceIdentifier, string snapshotIdentifier)
    {
        var response = await _amazonRDS.CreateDBSnapshotAsync(
            new CreateDBSnapshotRequest()
            {
                DBSnapshotIdentifier = snapshotIdentifier,
                DBInstanceIdentifier = dbInstanceIdentifier
            });

        return response.DBSnapshot;
    }

    // snippet-end:[RDS.dotnetv3.CreateDBSnapshot]

    // snippet-start:[RDS.dotnetv3.DescribeDBSnapshots]

    /// <summary>
    /// Return a list of DB snapshots for a particular instance.
    /// </summary>
    /// <param name="dbInstanceIdentifier">DB instance identifier.</param>
    /// <returns>List of DB snapshots.</returns>
    public async Task<List<DBSnapshot>> DescribeDBSnapshots(string dbInstanceIdentifier)
    {
        var results = new List<DBSnapshot>();
        var snapshotsPaginator = _amazonRDS.Paginators.DescribeDBSnapshots(
            new DescribeDBSnapshotsRequest()
            {
                DBInstanceIdentifier = dbInstanceIdentifier
            });

        // Get the entire list using the paginator.
        await foreach (var snapshots in snapshotsPaginator.DBSnapshots)
        {
            results.Add(snapshots);
        }
        return results;
    }

    // snippet-end:[RDS.dotnetv3.DescribeDBSnapshots]
    // snippet-end:[RDS.dotnetv3.RdsInstanceWrapperSnapshots]
}

