// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[gamelift.cpp.game-server.unreal]

//This is an example of a simple integration with GameLift server SDK that makes game server
//processes go active on Amazon GameLift

// Include game project files. "GameLiftFPS" is a sample game name, replace with file names from your own game project
#include "GameLiftFPSGameMode.h"
#include "GameLiftFPS.h"
#include "Engine.h"
#include "EngineGlobals.h"
#include "GameLiftFPSHUD.h"
#include "GameLiftFPSCharacter.h"
#include "GameLiftServerSDK.h"

AGameLiftFPSGameMode::AGameLiftFPSGameMode()
    : Super()
{

    //Let's run this code only if GAMELIFT is enabled. Only with Server targets!
#if WITH_GAMELIFT

    //Getting the module first.
    FGameLiftServerSDKModule* gameLiftSdkModule = &FModuleManager::LoadModuleChecked<FGameLiftServerSDKModule>(FName("GameLiftServerSDK"));

    //InitSDK establishes a local connection with GameLift's agent to enable communication.
    gameLiftSdkModule->InitSDK();

    //Respond to new game session activation request. GameLift sends activation request
    //to the game server along with a game session object containing game properties
    //and other settings. Once the game server is ready to receive player connections,
    //invoke GameLiftServerAPI.ActivateGameSession()
    auto onGameSession = [=](Aws::GameLift::Server::Model::GameSession gameSession)
    {
        gameLiftSdkModule->ActivateGameSession();
    };

    FProcessParameters* params = new FProcessParameters();
    params->OnStartGameSession.BindLambda(onGameSession);

    //OnProcessTerminate callback. GameLift invokes this before shutting down the instance
    //that is hosting this game server to give it time to gracefully shut down on its own.
    //In this example, we simply tell GameLift we are indeed going to shut down.
    params->OnTerminate.BindLambda([=](){gameLiftSdkModule->ProcessEnding();});

    //HealthCheck callback. GameLift invokes this callback about every 60 seconds. By default,
    //GameLift API automatically responds 'true'. A game can optionally perform checks on
    //dependencies and such and report status based on this info. If no response is received
    //within 60 seconds, health status is recorded as 'false'.
    //In this example, we're always healthy!
    params->OnHealthCheck.BindLambda([](){return true; });

    //Here, the game server tells GameLift what port it is listening on for incoming player
    //connections. In this example, the port is hardcoded for simplicity. Since active game
    //that are on the same instance must have unique ports, you may want to assign port values
    //from a range, such as:
    //const int32 port = FURL::UrlConfig.DefaultPort;
    //params->port;
    params->port = 7777;

    //Here, the game server tells GameLift what set of files to upload when the game session
    //ends. GameLift uploads everything specified here for the developers to fetch later.
    TArray<FString> logfiles;
    logfiles.Add(TEXT("aLogFile.txt"));
    params->logParameters = logfiles;

    //Call ProcessReady to tell GameLift this game server is ready to receive game sessions!
    gameLiftSdkModule->ProcessReady(*params);
#endif
}
// snippet-end:[gamelift.cpp.game-server.unreal]