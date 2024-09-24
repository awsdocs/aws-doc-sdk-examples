//
//  BucketsApp.swift
//  Buckets
//
//  Created by Shepherd, Eric on 8/2/24.
//

import SwiftUI
import ClientRuntime

@main
struct BucketsApp: App {

    /// Initialize the application.
    init() {
        // Create a synchronous task that configures logging then
        // immediately exits.
        Task.synchronous {
            SDKDefaultIO.setLogLevel(level: .error)
            await SDKLoggingSystem().initialize(logLevel: .error)
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView().environmentObject(ViewModel())
        }
    }
}
