// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[siwa-viewmodel.swift.imports]
import SwiftUI
import AuthenticationServices
import SimpleKeychain

// Import the AWS SDK for Swift modules the app requires.
import AWSS3
import AWSSDKIdentity
// snippet-end:[siwa-viewmodel.swift.imports]

/// A view model to manage a user's Sign In With Apple session and
/// its properties.
///
/// > Important: This example uses `@AppStorage` to store personal
///   identifiable information (PII). Shipping applications should
///   store any PII securely, such as by using the Keychain!
///
///   There are many useful packages available for this purpose that
///   you can find using the [Swift Package
///   Index](https://swiftpackageindex.com/).
@MainActor
class ViewModel: ObservableObject {
    // snippet-start:[siwa-auth-properties.swift]
    /// The unique string assigned by Sign In With Apple for this login
    /// session. This ID is valid across application launches until it
    /// is signed out from Sign In With Apple.
    var userID = ""
    
    /// The user's email address.
    ///
    /// This is only returned by SIWA if the user has just created
    /// the app's SIWA account link. Otherwise, it's returned as `nil`
    /// by SIWA and must be retrieved from local storage if needed.
    var email = ""
    
    /// The user's family (last) name.
    ///
    /// This is only returned by SIWA if the user has just created
    /// the app's SIWA account link. Otherwise, it's returned as `nil`
    /// by SIWA and must be retrieved from local storage if needed.
    var familyName = ""
    
    /// The user's given (first) name.
    ///
    /// This is only returned by SIWA if the user has just created
    /// the app's SIWA account link. Otherwise, it's returned as `nil` by SIWA
    /// and must be retrieved from local storage if needed.
    var givenName = ""
    
    /// The AWS account number provided by the user.
    var awsAccountNumber = ""
    
    /// The AWS IAM role name given by the user.
    var awsIAMRoleName = ""
    
    /// The credential identity resolver created by the AWS SDK for
    /// Swift. This resolves temporary credentials using
    /// `AssumeRoleWithWebIdentity`.
    var identityResolver: STSWebIdentityAWSCredentialIdentityResolver? = nil
    // snippet-end:[siwa-auth-properties.swift]
    
    //****** The variables below this point aren't involved in Sign in With
    //****** Apple or AWS authentication.
    
    /// An array of the user's bucket names.
    ///
    /// This is filled out once the user is signed into AWS.
    @Published var bucketList: [IDString] = []
    
    /// An error string to present in an alert panel if needed. If this is
    /// `nil`, no error alert is presented.
    @Published var error: Swift.Error?
    
    /// The title of the error alert's close button.
    @Published var errorButtonTitle: String = "Continue"
    
    init() {
        readUserData()
    }
    
    // MARK: - Authentication
    
    // snippet-start:[siwa-handle.swift]
    /// Called by the Sign In With Apple button when a JWT token has
    /// been returned by the Sign In With Apple service. This function
    /// in turn handles fetching AWS credentials using that token.
    ///
    /// - Parameters:
    ///   - result: The Swift `Result` object passed to the Sign In
    ///     With Apple button's `onCompletion` handler. If the sign
    ///     in request succeeded, this contains an `ASAuthorization`
    ///     object that contains the Apple ID sign in information.
    func handleSignInResult(_ result: Result<ASAuthorization, Error>) async throws {
        switch result {
        case .success(let auth):
            // Sign In With Apple returned a JWT identity token. Gather
            // the information it contains and prepare to convert the
            // token into AWS credentials.
            
            guard let credential = auth.credential as? ASAuthorizationAppleIDCredential,
                  let webToken = credential.identityToken,
                  let tokenString = String(data: webToken, encoding: .utf8)
            else {
                throw BucketsAppError.credentialsIncomplete
            }
            
            userID = credential.user
            
            // If the email field has a value, set the user's recorded email
            // address. Otherwise, keep the existing one.
            email = credential.email ?? self.email
            
            // Similarly, if the name is present in the credentials, use it.
            // Otherwise, the last known name is retained.
            if let name = credential.fullName {
                self.familyName = name.familyName ?? self.familyName
                self.givenName = name.givenName ?? self.givenName
            }
            
            // Use the JWT token to request a set of temporary AWS
            // credentials. Upon successful return, the
            // `credentialsProvider` can be used when configuring
            // any AWS service.
            
            try await authenticate(withWebIdentity: tokenString)
        case .failure(let error as ASAuthorizationError):
            if error.code == .canceled {
                throw BucketsAppError.signInWithAppleCanceled
            } else {
                throw BucketsAppError.signInWithAppleFailed
            }
        case .failure:
            throw BucketsAppError.signInWithAppleFailed
        }
        
        // Successfully signed in. Fetch the bucket list.
        do {
            try await self.getBucketList()
        } catch {
            throw BucketsAppError.bucketListMissing
        }
    }
    // snippet-end:[siwa-handle.swift]
    
    // snippet-start:[siwa-authenticate.swift]
    /// Convert the given JWT identity token string into the temporary
    /// AWS credentials needed to allow this application to operate, as
    /// specified using the Apple Developer portal and the AWS Identity
    /// and Access Management (IAM) service.
    ///
    /// - Parameters:
    ///   - tokenString: The string version of the JWT identity token
    ///     returned by Sign In With Apple.
    ///   - region: An optional string specifying the AWS Region to
    ///     access. If not specified, "us-east-1" is assumed.
    func authenticate(withWebIdentity tokenString: String,
                      region: String = "us-east-1") async throws {
        // If the role is empty, pass `nil` to use the default role for
        // the user.
        
        let roleARN = "arn:aws:iam::\(awsAccountNumber):role/\(awsIAMRoleName)"
        
        // Use the AWS Security Token Service (STS) action
        // `AssumeRoleWithWebIdentity` to convert the JWT token into a
        // set of temporary AWS credentials. The first step: write the token
        // to disk so it can be used by the
        // `STSWebIdentityAWSCredentialIdentityResolver`.
        
        let tokenFileURL = createTokenFileURL()
        let tokenFilePath = tokenFileURL.path
        do {
            try tokenString.write(to: tokenFileURL, atomically: true, encoding: .utf8)
        } catch {
            throw BucketsAppError.tokenFileError()
        }
        
        // Create an identity resolver that uses the JWT token received
        // from Apple to create AWS credentials.
        
        do {
            identityResolver = try STSWebIdentityAWSCredentialIdentityResolver(
                region: region,
                roleArn: roleARN,
                roleSessionName: "BucketsExample",
                tokenFilePath: tokenFilePath
            )
        } catch {
            throw BucketsAppError.assumeRoleFailed
        }
        
        // Save the user's data securely to local storage so it's available
        // in the future.
        //
        // IMPORTANT: Any potential Personally Identifiable Information _must_
        // be saved securely, such as by using the Keychain or an appropriate
        // encrypting technique.
        
        saveUserData()
    }
    // snippet-end:[siwa-authenticate.swift]
    
    // snippet-start:[siwa-sign-out.swift]
    /// "Sign out" of the user's account.
    ///
    /// All this does is erase the user ID to drop our ability to
    /// reference the AWS sign in, and empty the bucket list so a
    /// new sign-in won't already have a populated and possibly
    /// incorrect list.
    func signOut() {
        userID = ""
        bucketList = []
        
        identityResolver = nil
        
        do {
            try self.deleteTokenFile()
        } catch {
            self.error = error
        }
    }
    // snippet-end:[siwa-sign-out.swift]
    
    // snippet-start:[siwa-check-signed-in.swift]
    /// Determine whether or not the user is signed in by checking whether
    /// or not the userID is empty.
    func signedIn() -> Bool {
        return userID != ""
    }
    // snippet-end:[siwa-check-signed-in.swift]
    
    // MARK: - AWS access
    // snippet-start:[siwa-use-credentials.swift]
    /// Fetches a list of the user's Amazon S3 buckets.
    ///
    /// The bucket names are stored in the view model's `bucketList`
    /// property.
    func getBucketList() async throws {
        // If there's no identity resolver yet, return without doing anything.
        guard let identityResolver = identityResolver else {
            return
        }

        // Create an Amazon S3 client configuration that uses the
        // credential identity resolver created from the JWT token
        // returned by Sign In With Apple.
        let config = try await S3Client.S3ClientConfiguration(
            awsCredentialIdentityResolver: identityResolver,
            region: "us-east-1"
        )
        let s3 = S3Client(config: config)
        
        let output = try await s3.listBuckets(
            input: ListBucketsInput()
        )
        
        guard let buckets = output.buckets else {
            throw BucketsAppError.bucketListMissing
        }
        
        // Add the names of all the buckets to `bucketList`. Each
        // name is stored as a new `IDString` for use with the SwiftUI
        // `List`.
        for bucket in buckets {
            self.bucketList.append(IDString(bucket.name ?? "<unknown>"))
        }
    }
    // snippet-end:[siwa-use-credentials.swift]
    
    // MARK: - Token file management
    
    // snippet-start:[siwa-create-token-file.swift]
    /// Returns a URL providing the disk location of the JWT token file
    /// that gets written to disk during login attempts.
    ///
    /// - Returns: A URL matching the disk location of the JWT token file.
    func createTokenFileURL() -> URL {
        let tempDirURL = FileManager.default.temporaryDirectory
        return tempDirURL.appendingPathComponent("example-siwa-token.jwt")
    }
    // snippet-end:[siwa-create-token-file.swift]
    
    // snippet-start:[siwa-delete-token-file.swift]
    /// Delete the local JWT token file.
    func deleteTokenFile() throws {
        do {
            try FileManager.default.removeItem(at: createTokenFileURL())
        } catch {
            throw BucketsAppError.tokenFileError(reason: "Unable to delete the token file.")
        }
    }
    // snippet-end:[siwa-delete-token-file.swift]
    

    // MARK: - Secure storage
    
    let KEY_USER_ID = "buckets-user-id"
    let KEY_USER_EMAIL = "buckets-user-email"
    let KEY_USER_NAME_GIVEN = "buckets-user-given-name"
    let KEY_USER_NAME_FAMILY = "buckets-user-family-name"
    let KEY_AWS_ACCOUNT = "buckets-aws-account"
    let KEY_IAM_ROLE = "buckets-iam-role"

    /// Securely save user and account information to the Keychain.
    func saveUserData() {
        // Create a `SimpleKeychain` object to use for Keychain access. The
        // default service name (the bundle ID) is used.
        let simpleKeychain = SimpleKeychain()
        
        do {
            //try simpleKeychain.set(userID, forKey: KEY_USER_ID)
            try simpleKeychain.set(email, forKey: KEY_USER_EMAIL)
            try simpleKeychain.set(givenName, forKey: KEY_USER_NAME_GIVEN)
            try simpleKeychain.set(familyName, forKey: KEY_USER_NAME_FAMILY)
            try simpleKeychain.set(awsAccountNumber, forKey: KEY_AWS_ACCOUNT)
            try simpleKeychain.set(awsIAMRoleName, forKey: KEY_IAM_ROLE)
        } catch {
            // The way this example is written, if the data doesn't get saved,
            // it just means it won't be available later. So silently log
            // the problem and continue.
            print("Unable to save user data to keychain.")
        }
    }
    
    /// Read user data from the keychain.
    func readUserData() {
        // Create a `SimpleKeychain` object to use for Keychain access. The
        // default service name (the bundle ID) is used. Any item that isn't
        // found is set to an empty string.
        let simpleKeychain = SimpleKeychain()
        
        /*
         do {
            userID = try simpleKeychain.string(forKey: KEY_USER_ID)
        } catch {
            userID = ""
        }
         */
        
        do {
            email = try simpleKeychain.string(forKey: KEY_USER_EMAIL)
        } catch {
            email = ""
        }
        
        do {
            givenName = try simpleKeychain.string(forKey: KEY_USER_NAME_GIVEN)
        } catch {
            givenName = ""
        }
        
        do {
            familyName = try simpleKeychain.string(forKey: KEY_USER_NAME_FAMILY)
        } catch {
            familyName = ""
        }
        
        do {
            awsAccountNumber = try simpleKeychain.string(forKey: KEY_AWS_ACCOUNT)
        } catch {
            awsAccountNumber = ""
        }
        
        do {
            awsIAMRoleName = try simpleKeychain.string(forKey: KEY_IAM_ROLE)
        } catch {
            awsIAMRoleName = ""
        }
    }
}
