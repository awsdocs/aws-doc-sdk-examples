//
//  BucketsApp.swift
//  Buckets
//
//  Created by Shepherd, Eric on 8/2/24.
//

import SwiftUI

@main
struct BucketsApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView().environmentObject(ViewModel())
        }
    }
}
