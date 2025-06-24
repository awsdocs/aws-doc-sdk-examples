// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ControlTower.dotnetv4.ControlTowerWrapper]
using System.Net;

namespace ControlTowerActions;

/// <summary>
/// Methods to perform AWS Control Tower actions.
/// </summary>
public class ControlTowerWrapper
{
    private readonly IAmazonControlTower _controlTowerService;

    /// <summary>
    /// Constructor for the wrapper class containing AWS Control Tower actions.
    /// </summary>
    /// <param name="controlTowerService">The AWS Control Tower client object.</param>
    public ControlTowerWrapper(IAmazonControlTower controlTowerService)
    {
        _controlTowerService = controlTowerService;
    }

    // snippet-start:[ControlTower.dotnetv4.ListLandingZones]
    /// <summary>
    /// List the AWS Control Tower landing zones for an account.
    /// </summary>
    /// <returns>A list of LandingZoneSummary objects.</returns>
    public async Task<List<LandingZoneSummary>> ListLandingZonesAsync()
    {
        var landingZones = new List<LandingZoneSummary>();

        var landingZonesPaginator = _controlTowerService.Paginators.ListLandingZones(new ListLandingZonesRequest());

        await foreach (var response in landingZonesPaginator.Responses)
        {
            landingZones.AddRange(response.LandingZones);
        }

        return landingZones;
    }

    // snippet-end:[ControlTower.dotnetv4.ListLandingZones]

    // snippet-start:[ControlTower.dotnetv4.GetLandingZone]
    /// <summary>
    /// Get details about a specific landing zone.
    /// </summary>
    /// <param name="landingZoneIdentifier">The landing zone identifier.</param>
    /// <returns>The landing zone details.</returns>
    public async Task<LandingZoneDetail> GetLandingZoneAsync(string landingZoneIdentifier)
    {
        var request = new GetLandingZoneRequest
        {
            LandingZoneIdentifier = landingZoneIdentifier
        };

        var response = await _controlTowerService.GetLandingZoneAsync(request);
        return response.LandingZone;
    }

    // snippet-end:[ControlTower.dotnetv4.GetLandingZone]

    // snippet-start:[ControlTower.dotnetv4.ListEnabledControls]
    /// <summary>
    /// List enabled controls for a target organizational unit.
    /// </summary>
    /// <param name="targetIdentifier">The target organizational unit identifier.</param>
    /// <returns>A list of enabled control summaries.</returns>
    public async Task<List<EnabledControlSummary>> ListEnabledControlsAsync(string targetIdentifier)
    {
        var request = new ListEnabledControlsRequest
        {
            TargetIdentifier = targetIdentifier
        };

        var enabledControls = new List<EnabledControlSummary>();

        var enabledControlsPaginator = _controlTowerService.Paginators.ListEnabledControls(request);

        await foreach (var response in enabledControlsPaginator.Responses)
        {
            enabledControls.AddRange(response.EnabledControls);
        }

        return enabledControls;
    }

    // snippet-end:[ControlTower.dotnetv4.ListEnabledControls]
}

// snippet-end:[ControlTower.dotnetv4.ControlTowerWrapper]