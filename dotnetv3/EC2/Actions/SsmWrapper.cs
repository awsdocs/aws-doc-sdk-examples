// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace EC2Actions;

// snippet-start:[SSM.dotnetv3.SSMActionsClass]
/// <summary>
/// Methods that perform actions from the Simple Systems Management Service.
/// </summary>
public class SsmWrapper
{
    private readonly IAmazonSimpleSystemsManagement _amazonSSM;

    public SsmWrapper(IAmazonSimpleSystemsManagement amazonService)
    {
        _amazonSSM = amazonService;
    }

    /// <summary>
    /// Get a list of parameter values based on the service path.
    /// </summary>
    /// <param name="path">The path used to retrieve parameters.</param>
    /// <returns>Async task.</returns>
    public async Task<List<Parameter>> GetParametersByPath(string path)
    {
        var response = await _amazonSSM.GetParametersByPathAsync(new GetParametersByPathRequest
        {
            Path = path
        });
        var paramList =
            from parameter in response.Parameters
            where parameter.Name.Contains("amzn")
            select parameter;
        return paramList.ToList();
    }
}

// snippet-end:[SSM.dotnetv3.SSMActionsClass]