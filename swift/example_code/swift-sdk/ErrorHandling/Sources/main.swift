import Foundation
import AwsCommonRuntimeKit
import ClientRuntime
import AWSS3

func test() async throws -> String {
    return "This is a string"
}

/// Main entry point.
@main
struct ErrorHandlingDemo {
    static func main() async {
        var client: S3Client

        SDKLoggingSystem.initialize(logLevel: .error)

        print("1. LISTBUCKETS")
        do {
            client = try S3Client(region: "un-real-1")

            let output = try await client.listBuckets(input: ListBucketsInput())
            print("Done")
        } catch let error as CommonRunTimeError {
            print("Common RunTime error: \(error.localizedDescription)")
        } catch let error as CRTError {
            print("CRT Error code \(error.code) (\(error.name)): \(error.message)")
        } catch let error as AwsCommonRuntimeKit.CRTError {
            print("CRT Kit CRT error \(error.code) (\(error.name)): \(error.message)")
        } catch {
            print("Some other error")
        }

        print("==============================================")
        print("2. GETOBJECT")

        do {
            client = try S3Client(region: "us-east-1")

            let output = try await client.getObject(input: GetObjectInput(
                bucket: "not-a-real-bucket",
                key: "not-a-real-key"
            ))
            print("Done")
        } catch let error as HTTPError {
            print("HTTP ERROR: \(error.httpResponse.statusCode.rawValue)")
        } catch {
            dump(error)
        }
    }
}