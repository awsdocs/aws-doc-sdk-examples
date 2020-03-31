/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[GameLift-Unity-game-server.cs demonstrates a basic integration of Amazon GameLift into a Unity-built game server.]
// snippet-service:[gamelift]
// snippet-keyword:[Amazon GameLift]
//snippet-keyword:[dotNET]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[InitSDK]
//snippet-keyword:[ActivateGameSession]
//snippet-keyword:[ProcessReady]
//snippet-keyword:[ProcessEnding]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-12]
// snippet-sourceauthor:[AWS]
// snippet-start:[gamelift.dotnet.game-server.unity]
using UnityEngine;
using Aws.GameLift.Server;
using System.Collections.Generic;

public class GameLiftServerExampleBehavior : MonoBehaviour
{
    //This is an example of a simple integration with GameLift server SDK that makes game server 
    //processes go active on Amazon GameLift
    public void Start()
    {
        //Set the port that your game service is listening on for incoming player connections (hard-coded here for simplicity)
        var listeningPort = 7777;

        //InitSDK establishes a local connection with the Amazon GameLift agent to enable 
        //further communication.
        var initSDKOutcome = GameLiftServerAPI.InitSDK();
        if (initSDKOutcome.Success)
        {
            ProcessParameters processParameters = new ProcessParameters(
                (gameSession) => {
                    //Respond to new game session activation request. GameLift sends activation request 
                    //to the game server along with a game session object containing game properties 
                    //and other settings. Once the game server is ready to receive player connections, 
                    //invoke GameLiftServerAPI.ActivateGameSession()
                    GameLiftServerAPI.ActivateGameSession();
                },
                () => {
                    //OnProcessTerminate callback. GameLift invokes this callback before shutting down 
                    //an instance hosting this game server. It gives this game server a chance to save
                    //its state, communicate with services, etc., before being shut down. 
                    //In this case, we simply tell GameLift we are indeed going to shut down.
                    GameLiftServerAPI.ProcessEnding();
                }, 
                () => {
                    //This is the HealthCheck callback.
                    //GameLift invokes this callback every 60 seconds or so.
                    //Here, a game server might want to check the health of dependencies and such.
                    //Simply return true if healthy, false otherwise.
                    //The game server has 60 seconds to respond with its health status. 
                    //GameLift will default to 'false' if the game server doesn't respond in time.
                    //In this case, we're always healthy!
                    return true;
                },
                //Here, the game server tells GameLift what port it is listening on for incoming player 
                //connections. In this example, the port is hardcoded for simplicity. Active game
                //that are on the same instance must have unique ports.
                listeningPort, 
                new LogParameters(new List<string>()
                {
                    //Here, the game server tells GameLift what set of files to upload when the game session ends.
                    //GameLift uploads everything specified here for the developers to fetch later.
                    "/local/game/logs/myserver.log"
                }));

            //Calling ProcessReady tells GameLift this game server is ready to receive incoming game sessions!
            var processReadyOutcome = GameLiftServerAPI.ProcessReady(processParameters);
            if (processReadyOutcome.Success)
            {
                print("ProcessReady success.");
            }
            else
            {
                print("ProcessReady failure : " + processReadyOutcome.Error.ToString());
            }
        }
        else
        {
            print("InitSDK failure : " + initSDKOutcome.Error.ToString());
        }
    }

    void OnApplicationQuit()
    {
        //Make sure to call GameLiftServerAPI.Destroy() when the application quits. 
        //This resets the local connection with GameLift's agent.
        GameLiftServerAPI.Destroy();
    }
}
// snippet-end:[gamelift.dotnet.game-server.unity]