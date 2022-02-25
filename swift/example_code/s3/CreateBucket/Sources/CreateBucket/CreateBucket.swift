import Foundation
import AWSS3
import ClientRuntime
import AWSClientRuntime


@main
struct CreateBucket {
    static func main() async {
        let s3Client: S3Client

        if CommandLine.arguments.count < 2 {
            print("Specify at least the name of the bucket to create")
            return
        }

        let bucketName = CommandLine.arguments[1]

        do {
            print("Creating S3 client")

            // Create S3 client with logging
            #if DEBUG
                let s3DefaultConfig = try DefaultSDKRuntimeConfiguration("S3Client", clientLogMode: .requestAndResponse)
                let s3Config = try await S3Client.S3ClientConfiguration(runtimeConfig: s3DefaultConfig)
                s3Client = S3Client(config: s3Config)
            #else
                s3Client = try await S3Client()
            #endif
        } catch {
            print("ERROR: ", dump(error, name: "Initializing S3 Client"))
            exit(1)
        }
        print("S3 client created")

        var output: CreateBucketOutputResponse
        do {
            print("Creating bucket \(bucketName)")
            output = try await s3Client.createBucket(input: CreateBucketInput(bucket: bucketName))
            print("Success.")
        } catch {
            dump(error, name: "Error creating the bucket \(bucketName)")
        }
    }
}