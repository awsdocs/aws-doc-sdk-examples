import AWSS3
import AWSClientRuntime

@main
struct CreateBucket {
    static func main() async {
        if CommandLine.arguments.count < 2 {
            print("Specify at least the name of the bucket to create")
            return
        }

        let bucketName = CommandLine.arguments[1]

        do {
            print("Creating client")
            let s3Client = try await S3Client()

            print("Creating bucket")
            let output = try await s3Client.createBucket(input: CreateBucketInput(bucket: bucketName))
            print("Success.")
        } catch {
            dump(error, name: "Error")
        }
    }
}