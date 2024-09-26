// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[siwa-button.swift.imports]
import SwiftUI
import AuthenticationServices
// snippet-end:[siwa-button.swift.imports]

struct ContentView: View {
    /// The view model used to access AWS and manipulate the user data.
    @EnvironmentObject private var viewModel: ViewModel
        
    var body: some View {
        // If the user isn't signed into AWS, show the Sign In form.
        // Otherwise, show the bucket list views.
        
        VStack {
            if !viewModel.signedIn() {
                VStack {
                    Text("Sign In to AWS using Apple")
                        .font(.title)
                        .padding(.bottom)
                    
                    Form {
                        VStack {
                            HStack {
                                Text("AWS account number:")
                                TextField(text: $viewModel.awsAccountNumber, prompt: Text("Account number")) { }
                            }
                            HStack {
                                Text("AWS IAM role name:")
                                TextField(text: $viewModel.awsIAMRoleName, prompt: Text("Role name")) { }
                            }
                        }
                    }
                    
                    // snippet-start:[siwa-button.swift]
                    // Show the "Sign In With Apple" button, using the
                    // `.continue` mode, which allows the user to create
                    // a new ID if they don't already have one. When SIWA
                    // is complete, the view model's `handleSignInResult()`
                    // function is called to turn the JWT token into AWS
                    // credentials.
                    SignInWithAppleButton(.continue) { request in
                        request.requestedScopes = [.email, .fullName ]
                    } onCompletion: { result in
                        Task {
                            do {
                                try await viewModel.handleSignInResult(result)
                            } catch BucketsAppError.signInWithAppleCanceled {
                                // The "error" is actually Sign In With Apple being
                                // canceled by the user, so end the sign in
                                // attempt.
                                return
                            } catch let error as BucketsAppError {
                                // Handle AWS errors.
                                viewModel.error = error
                                return
                            }
                        }
                    }
                    // snippet-end:[siwa-button.swift]
                    .frame(maxWidth: 340, maxHeight: 58)
                    .keyboardShortcut(.defaultAction)
                }
                .padding()
                .frame(minWidth: 500, minHeight: 260)
                
                Spacer()
            } else {
                // The user is signed into their AWS account, so show their
                // basic account information.
                
                VStack() {
                    Text("Welcome")
                        .font(.largeTitle)
                    Text("\(viewModel.givenName) \(viewModel.familyName)")
                        .font(.title)
                    Text("\(viewModel.email)")
                        .font(.subheadline)
                }
                .padding()
                
                // Show UI to allow the user to fetch and display a list
                // of their Amazon S3 buckets.
                
                VStack {
                    VStack {
                        List(viewModel.bucketList) { bucket in
                            Text(bucket.text)
                        }
                    }
                    .padding()
                    
                    // Show the user's ID, as assigned by Sign In With Apple.
                    
                    VStack {
                        Text("User ID:")
                            .font(.caption)
                        Text(viewModel.userID)
                            .font(.caption2)
                    }
                    .padding(.horizontal)
                    
                    // Show the action buttons.
                    
                    HStack {
                        Button("Reload") {
                            Task {
                                viewModel.bucketList = []
                                do {
                                    try await viewModel.getBucketList()
                                } catch let err {
                                    viewModel.error = err
                                }
                            }
                        }
                        Button("Sign Out") {
                            viewModel.signOut()
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    .padding()
                }
            }
        }.errorAlert(error: $viewModel.error, buttonTitle: viewModel.errorButtonTitle)
    }
}

#Preview {
    ContentView()
}
