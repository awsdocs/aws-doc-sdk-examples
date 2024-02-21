// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <aws/core/Aws.h>
#include <aws/cloudformation/CloudFormationClient.h>
#include <aws/cloudformation/model/CreateStackRequest.h>
#include <aws/cloudformation/model/DeleteStackRequest.h>
#include <aws/cloudformation/model/DescribeStacksRequest.h>
#include <aws/medical-imaging/MedicalImagingClient.h>
#include <aws/medical-imaging/model/GetImageFrameRequest.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CopyObjectRequest.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/s3/model/ListObjectsV2Request.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/sts/STSClient.h>
#include <aws/sts/model/GetCallerIdentityRequest.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/threading/Semaphore.h>
#include <fstream>
#include <jsoncons/json.hpp>
#include <jsoncons_ext/jmespath/jmespath.hpp>
#include <gzip/decompress.hpp>
#include <openjpeg.h>
#include <boost/crc.hpp>  // for boost::crc_32_type
#include <utility>
#include <filesystem>
#include "medical-imaging_samples.h"

namespace AwsDoc::Medical_Imaging {

    // Path to the CloudFormation template used by this workflow.
    const char STACK_TEMPLATE_PATH[] = TEMPLATES_PATH
    "/cfn_template.yaml";

    // The following parameters and outputs must match those in the
    // CloudFormation template.
    const char DATASTORE_PARAMETER[] = "datastoreName";
    const char USER_ACCOUNT_ID_PARAMETER[] = "userAccountID";
    const char ROLE_ARN_OUTPUT[] = "RoleArn";
    const char BUCKET_NAME_OUTPUT[] = "InputBucketName";
    const char OUTPUT_BUCKET_NAME_OUTPUT[] = "OutputBucketName";
    const char DATASTORE_ID_OUTPUT[] = "DatastoreID";

    /*
    * This workflow uses DICOM files from National Cancer Institute Imaging Data Commons
    * (IDC) Collections. https://registry.opendata.aws/nci-imaging-data-commons/
    * The files are stored in a public Amazon S3 bucket.
    */
    const char IDC_S3_BucketName[] = "idc-open-data";

    // Struct for choices from the IDC collection.
    struct IDC_Choice {
        Aws::String mDescription;
        Aws::String mDirectory;
    };

    // Choices from the IDC collection.
    const Aws::Vector<IDC_Choice> IDC_ImageChoices = {
            {"CT of chest (2 images)",    "00029d25-fb18-4d42-aaa5-a0897d1ac8f7"},
            {"CT of pelvis (57 images)",  "00025d30-ef8f-4135-a35a-d83eff264fc1"},
            {"MRI of head (192 images)",  "0002d261-8a5d-4e63-8e2e-0cbfac87b904"},
            {"MRI of breast (92 images)", "0002dd07-0b7f-4a68-a655-44461ca34096"}
    };

    // Name of DICOM import job output manifest file.
    const char IMPORT_JOB_MANIFEST_FILE_NAME[] = "job-output-manifest.json";

    // A selection of values for an image frame extracted from the image set metadata.
    struct ImageFrameInfo {
        Aws::String mImageSetId;
        Aws::String mImageFrameId;
        Aws::String mRescaleIntercept;
        Aws::String mRescaleSlope;
        Aws::String MinPixelValue;
        Aws::String MaxPixelValue;
        uint32_t mFullResolutionChecksum = 0;
    };

    //! Routine which runs the HealthImaging workflow.
    /*!
       \param clientConfig: Aws client configuration.
       \return bool: Function succeeded.
    */
    bool workingWithHealthImagingImageSetsAndImageFrames(
            const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which gets the user's account ID.
    /*!
       \param clientConfig: Aws client configuration.
       \return bool: Function succeeded.
    */
    static Aws::String
    getUserAccountID(const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which waits until a CloudFormation stack is created.
    /*!
       \param cloudFormationClient: A CloudFormation client.
       \param stackName: The stack name.
       \return bool: Function succeeded.
    */
    static bool waitStackCreated(
            Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
            const std::string &stackName,
            Aws::Vector<Aws::CloudFormation::Model::Output> &outputs);

    //! Routine which waits until a CloudFormation stack is deleted.
    /*!
       \param cloudFormationClient: A CloudFormation client.
       \param stackName: The stack name.
       \return bool: Function succeeded.
    */
    static bool waitStackDeleted(
            Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
            const std::string &stackName);

    //! Routine which creates a CloudFormation stack.
    /*!
       \param stackName: The stack name.
       \param dataStoreName: A data store name passed as a parameter.
       \param clientConfiguration: Aws client configuration.
       \return ws::Map<Aws::String, Aws::String>: Map of outputs.
    */
    static Aws::Map<Aws::String, Aws::String>
    createCloudFormationStack(const Aws::String &stackName,
                              const Aws::String &dataStoreName,
                              const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which deletes a CloudFormation stack.
    /*!
       \param stackName: The stack name.
       \param clientConfiguration: Aws client configuration.
       \return bool: Function succeeded.
    */
    static bool
    deleteStack(const std::string &stackName,
                const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine copies a folder between 2 S3 buckets.
    /*!
     * @param fromBucket: The S3 bucket to copy from.
     * @param fromDirectory: The directory to copy from.
     * @param toBucket: The S3 bucket to copy to.
     * @param toDirectory: The directory to copy to.
     * @param clientConfiguration: Aws client configuration.
     * @return: Function succeeded.
     */
    bool copySeriesBetweenBuckets(const Aws::String &fromBucket,
                                  const Aws::String &fromDirectory,
                                  const Aws::String &toBucket,
                                  const Aws::String &toDirectory,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which deletes the image sets in a data store.
    /*!
     * @param datastoreID: The HealthImaging data store ID.
     * @param clientConfiguration: Aws client configuration.
     * @return bool: Function succeeded.
     */
    bool emptyDatastore(const Aws::String &datastoreID,
                        const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which starts a DICOM import.
    /*!
     * @param dataStoreID: The HealthImaging data store ID.
     * @param inputBucketName: The S3 bucket containing DICOM files to import.
     * @param inputDirectory: The directory in the S3 bucket containing DICOM files to import.
     * @param outputBucketName: The S3 bucket for the output.
     * @param outputDirectory: The directory in the S3 bucket for the output.
     * @param roleArn: The IAM role for the  import job.
     * @param importJobId: A string to receive the import job ID.
     * @param clientConfiguration : Aws client configuration.
     * @return  bool: Function succeeded.
     */
    bool
    startDicomImport(const Aws::String &dataStoreID, const Aws::String &inputBucketName,
                     const Aws::String &inputDirectory,
                     const Aws::String &outputBucketName,
                     const Aws::String &outputDirectory, const Aws::String &roleArn,
                     Aws::String &importJobId,
                     const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which waits for a DICOM import job to complete.
    /*!
     * @param dataStoreID: The HealthImaging data store ID.
     * @param importJobId: The import job ID.
     * @param clientConfiguration : Aws client configuration.
     * @return  bool: Function succeeded.
     */
    bool waitImportJobCompleted(const Aws::String &datastoreID,
                                const Aws::String &importJobId,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which retrieves the image sets created for an import job.
    /*!
     * @param dataStoreID: The HealthImaging data store ID.
     * @param importJobId: The import job ID.
     * @param imageSets: Array to receive the image set IDs.
     * @param clientConfiguration : Aws client configuration.
     * @return  bool: Function succeeded.
     */
    bool getImageSetsForDicomImportJob(const Aws::String &datastoreID,
                                       const Aws::String &importJobId,
                                       Aws::Vector<Aws::String> &imageSets,
                                       const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which retrieves image frame information for the image frames
    //! associated with an image set.
    /*!
     * @param dataStoreID: The HealthImaging data store ID.
     * @param importJobId: The import job ID.
     * @param imageSets: An image set ID.
     * @param imageSets: Array to receive structs of image frame in information.
     * @param clientConfiguration : Aws client configuration.
     * @return  bool: Function succeeded.
     */
    bool getImageFramesForImageSet(const Aws::String &dataStoreID,
                                   const Aws::String &imageSetID,
                                   const Aws::String &outDirectory,
                                   Aws::Vector<ImageFrameInfo> &imageFrames,
                                   const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which saves image frames, decodes them and uses the checksum to
    //! validate the decoded images.
    /*!
     * @param outcome: The outcome of a GetImageFrame request.
     * @param outDirectory: A directory for saved files.
     * @param imageFrameInfo: Info for this image frame.
      * @return  bool: Function succeeded.
     */
    bool handleGetImageFrameResult(
            const Aws::MedicalImaging::Model::GetImageFrameOutcome &outcome,
            const Aws::String &outDirectory,
            const ImageFrameInfo &imageFrameInfo);

    //! Routine which downloads image frames, decodes them and uses the checksum to
    //! validate the decoded images.
    /*!
     * @param dataStoreID: The HealthImaging data store ID.
     * @param importJobId: A list of structs containing image frame information.
     * @param imageSets: A directory for the downloaded images.
     * @param clientConfiguration : Aws client configuration.
     * @return  bool: Function succeeded.
     */
    bool downloadDecodeAndCheckImageFrames(const Aws::String &dataStoreID,
                                           const Aws::Vector<ImageFrameInfo> &imageFrames,
                                           const Aws::String &outDirectory,
                                           const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which deletes workflow resources after asking the user.
    /*!
     * @param stackName: The CloudFormation stack name.
     * @param dataStoreId: The HealthImaging data store ID.
      * @param clientConfiguration : Aws client configuration.
     * @return  bool: Function succeeded.
     */
    bool cleanup(const Aws::String &stackName,
                 const Aws::String &dataStoreId,
                 const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Routine which gets the CloudFormation stack outputs from a map.
    /*!
     * @param outputs: A map of string outputs.
     * @param dataStoreId: The HealthImaging data store ID.
     * @param inputBucketName: The S3 bucket for DICOM import input.
     * @param outputBucketName: The S3 bucket for DICOM import output.
     * @param roleArn: The IAM role for DICOM  import.
     * @return  bool: Function succeeded.
     */
    bool retrieveOutputs(
            const Aws::Map<Aws::String, Aws::String> &outputs,
            Aws::String &dataStoreId,
            Aws::String &inputBucketName,
            Aws::String &outputBucketName,
            Aws::String &roleArn);

    //! Routine which decodes an HTJ2K-encoded image and validates with a checksum.
    /*!
     * @param jphFile: The path to the image file.
     * @param crc32Checksum: The CRC32 checksum.
     * @return  bool: Function succeeded.
     */
    bool decodeJPHFileAndValidateWithChecksum(const Aws::String &jphFile,
                                              uint32_t crc32Checksum);

    //! Routine which decodes an HTJ2K-encoded image using the OpenJPEG library.
    /*!
     * @param jphFile: The path to the image file.
     * @return  opj_image_t: An OpenJPEG image struct or a null ptr.
     */
    opj_image_t *jphImageToOpjBitmap(const Aws::String &jphFile);

    //! Routine which verifies the checksum of an OpenJPEG image struct.
    /*!
     * @param image: The OpenJPEG image struct.
     * @param crc32Checksum: The CRC32 checksum.
     * @return  bool: Function succeeded.
     */
    bool verifyChecksumForImage(opj_image_t *image, uint32_t crc32Checksum);

    //! Routine to configure OpenJPEG logging.
    /*!
     * @param codec: An OpenJPEG codec.
     * @param level: The logging level.
     * @return  bool: Function succeeded.
     */
    bool setupCodecLogging(opj_codec_t *codec, int *level);

    //! Test routine passed as argument to askQuestion routine.
    /*!
     \param string: A string to test.
     \return bool: True if empty.
     */
    static bool testForEmptyString(const Aws::String &string);

    //! Command line prompt/response utility function.
    /*!
     \param string: A question prompt.
     \param test: Test function for response.
     \return Aws::String: User's response.
     */
    static Aws::String askQuestion(const Aws::String &string,
                                   const std::function<bool(
                                           Aws::String)> &test = testForEmptyString);

    //! Command line prompt/response for yes/no question.
    /*!
     \param string: A question prompt expecting a 'y' or 'n' response.
     \return bool: True if yes.
     */
    static bool askYesNoQuestion(const Aws::String &string);

    //! Command line prompt/response utility function for an int result confined to
    //! a range.
    /*!
     \param string: A question prompt.
     \param low: Low inclusive.
     \param high: High inclusive.
     \return int: User's response.
     */
    static int askQuestionForIntRange(const Aws::String &string, int low,
                                      int high);

    //! Utility routine to print a line of asterisks to standard out.
    /*!
    \return void:
     */
    static void printAsterisksLine() {
        std::cout << "\n" << std::setfill('*') << std::setw(88) << "\n"
                  << std::endl;
    }

    //! Test routine passed as argument to askQuestion routine.
    /*!
     \return bool: Always true.
     */
    static bool alwaysTrueTest(const Aws::String &) { return true; }

    static bool DEBUGGING = false;
} // namespace AwsDoc::Medical_Imaging

//! Routine which runs the HealthImaging workflow.
/*!
   \param clientConfig: Aws client configuration.
   \return bool: Function succeeded.
*/
bool AwsDoc::Medical_Imaging::workingWithHealthImagingImageSetsAndImageFrames(
        const Aws::Client::ClientConfiguration &clientConfiguration) {

    printAsterisksLine();
    std::cout << "Welcome to the AWS HealthImaging working with image sets and "
              << "frames workflow." << std::endl;
    printAsterisksLine();
    std::cout
            << "This workflow will import DICOM files into a HealthImaging data store."
            << std::endl;
    std::cout
            << "DICOM® — Digital Imaging and Communications in Medicine — is the international\n"
            << "standard for medical images and related information.\n" << std::endl;
    std::cout
            << "The workflow will then download all the image frames created during the DICOM import and decode\n"
            << "the image frames from their HTJ2K format to a bitmap format.\n"
            << "The bitmaps will then be validated with a checksum to ensure they are correct.\n"
            << std::endl;
    std::cout
            << "This workflow requires a number of AWS resources to run.\n"
            << std::endl;
    std::cout
            << "It requires a HealthImaging data store, an Amazon Simple Storage Service (Amazon S3)\n"
            << "bucket for uploaded DICOM files, an Amazon S3 bucket for the output of a DICOM import, and\n"
            << "an AWS Identity and Access Management (IAM) role for importing the DICOM files into\n"
            << "the data store.\n" << std::endl;
    std::cout
            << "These resources can be created for you using an AWS CloudFormation stack.\n"
            << std::endl;
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.createstack]
    Aws::String inputBucketName;
    Aws::String outputBucketName;
    Aws::String dataStoreId;
    Aws::String roleArn;
    Aws::String stackName;

    if (askYesNoQuestion(
            "Would you like to let this workflow create the resources for you? (y/n) ")) {
        stackName = askQuestion(
                "Enter a name for the AWS CloudFormation stack to create. ");
        Aws::String dataStoreName = askQuestion(
                "Enter a name for the HealthImaging datastore to create. ");

        Aws::Map<Aws::String, Aws::String> outputs = createCloudFormationStack(
                stackName,
                dataStoreName,
                clientConfiguration);

        if (!retrieveOutputs(outputs, dataStoreId, inputBucketName, outputBucketName,
                             roleArn)) {
            return false;
        }

        std::cout << "The following resources have been created." << std::endl;
        std::cout << "A HealthImaging datastore with ID: " << dataStoreId << "."
                  << std::endl;
        std::cout << "An Amazon S3 input bucket named: " << inputBucketName << "."
                  << std::endl;
        std::cout << "An Amazon S3 output bucket named: " << outputBucketName << "."
                  << std::endl;
        std::cout << "An IAM role with the ARN: " << roleArn << "." << std::endl;
        askQuestion("Enter return to continue.", alwaysTrueTest);
    }
    else {
        std::cout << "You have chosen to use preexisting resources:" << std::endl;
        dataStoreId = askQuestion(
                "Enter the data store ID of the HealthImaging datastore you wish to use: ");
        inputBucketName = askQuestion(
                "Enter the name of the S3 input bucket you wish to use: ");
        outputBucketName = askQuestion(
                "Enter the name of the S3 output bucket you wish to use: ");
        roleArn = askQuestion(
                "Enter the ARN for the IAM role with the proper permissions to import a DICOM series: ");
    }
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.createstack]

    printAsterisksLine();

    // snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.copy-dicom-files]
    std::cout
            << "This workflow uses DICOM files from the National Cancer Institute Imaging Data\n"
            << "Commons (IDC) Collections." << std::endl;
    std::cout << "Here is the link to their website." << std::endl;
    std::cout << "https://registry.opendata.aws/nci-imaging-data-commons/" << std::endl;
    std::cout << "We will use DICOM files stored in an S3 bucket managed by the IDC."
              << std::endl;
    std::cout
            << "First one of the DICOM folders in the IDC collection must be copied to your\n"
               "input S3 bucket."
            << std::endl;
    std::cout << "You have the choice of one of the following "
              << IDC_ImageChoices.size() << " folders to copy." << std::endl;

    int index = 1;
    for (auto &idcChoice: IDC_ImageChoices) {
        std::cout << index << " - " << idcChoice.mDescription << std::endl;
        index++;
    }
    int choice = askQuestionForIntRange("Choose DICOM files to import: ", 1, 4);

    Aws::String fromDirectory = IDC_ImageChoices[choice - 1].mDirectory;
    Aws::String inputDirectory = "input";

    std::cout << "The files in the directory '" << fromDirectory << "' in the bucket '"
              << IDC_S3_BucketName << "' will be copied " << std::endl;
    std::cout << "to the folder '" << inputDirectory << "/" << fromDirectory
              << "' in the bucket '" << inputBucketName << "'." << std::endl;
    askQuestion("Enter return to start the copy.", alwaysTrueTest);

    if (!AwsDoc::Medical_Imaging::copySeriesBetweenBuckets(
            IDC_S3_BucketName,
            fromDirectory,
            inputBucketName,
            inputDirectory, clientConfiguration)) {
        std::cerr << "This workflow will exit because of an error." << std::endl;
        cleanup(stackName, dataStoreId, clientConfiguration);
        return false;
    }
    // snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.copy-dicom-files]

    printAsterisksLine();

    std::cout << "Now the DICOM images will be imported into the datastore with ID '"
              << dataStoreId << "'" << std::endl;
    askQuestion("Enter return to start the DICOM import job.", alwaysTrueTest);

    Aws::String importJobId;
    if (!startDicomImport(dataStoreId, inputBucketName,
                          inputDirectory + "/" + fromDirectory,
                          outputBucketName, "output", roleArn, importJobId,
                          clientConfiguration)) {
        std::cerr << "This workflow will exit because of an error." << std::endl;
        cleanup(stackName, dataStoreId, clientConfiguration);
        return false;
    }

    std::cout << "The DICOM files were successfully imported. The import job ID is '"
              << importJobId << "'." << std::endl;
    std::cout
            << "Information about the import job, including the IDs of the created image sets,\n"
            << "is located in a file named '" << IMPORT_JOB_MANIFEST_FILE_NAME
            << "'. This file is located in a\n"
            << "folder specified by the import job's 'outputS3Uri'.\n"
            << "The 'outputS3Uri' is retrieved by calling the 'GetDICOMImportJob' action."
            << std::endl;
    printAsterisksLine();
    std::cout << "The image set IDs will be retrieved by downloading '"
              << IMPORT_JOB_MANIFEST_FILE_NAME << "' file from the output S3 bucket."
              << std::endl;
    askQuestion("Enter return to continue.", alwaysTrueTest);

    Aws::Vector<Aws::String> imageSets;
    if (!getImageSetsForDicomImportJob(dataStoreId,
                                       importJobId, imageSets,
                                       clientConfiguration)) {
        std::cerr << "This workflow will exit because of an error." << std::endl;
        cleanup(stackName, dataStoreId, clientConfiguration);
        return false;
    }

    std::cout << "The image sets created by this import job are: " << std::endl;
    for (auto &imageSet: imageSets) {
        std::cout << "Image set: " << imageSet << std::endl;
    }

    std::cout
            << "If you would like information about how HealthImaging organizes image sets,"
            << std::endl;
    std::cout
            << "go to the following link.\n" <<
            "https://docs.aws.amazon.com/healthimaging/latest/devguide/understanding-image-sets.html"
            << std::endl;
    askQuestion("Enter return to continue.", alwaysTrueTest);
    std::cout << std::endl;
    printAsterisksLine();
    std::cout
            << "Next this workflow will download all the image frames created in this import job."
            << std::endl;
    std::cout
            << "The IDs of all the image frames in an image set are stored in the image set metadata."
            << std::endl;
    std::cout
            << "The image set metadata will be downloaded and parsed for the image frame IDs."
            << std::endl;
    askQuestion("Enter return to continue.", alwaysTrueTest);

    Aws::String outDirectory = "output/import_job_" + importJobId;

    std::filesystem::create_directories(outDirectory);

    Aws::Vector<ImageFrameInfo> allImageFrameIDs;
    for (auto &imageSet: imageSets) {
        Aws::Vector<ImageFrameInfo> imageFrames;
        if (!getImageFramesForImageSet(dataStoreId, imageSet, outDirectory,
                                       imageFrames, clientConfiguration)) {
            std::cerr << "This workflow will exit because of an error." << std::endl;
            cleanup(stackName, dataStoreId, clientConfiguration);
            return false;
        }

        for (auto &imageFrame: imageFrames) {
            allImageFrameIDs.emplace_back(imageFrame);
        }
    }

    std::cout << allImageFrameIDs.size()
              << " image frames were created by this import job.\n" << std::endl;
    printAsterisksLine();
    std::cout
            << "The image frames are encoded in the HTJ2K format. This example will convert\n"
            << "the image frames to bitmaps. The decoded images will be verified using\n"
            << "a CRC32 checksum retrieved from the image set metadata," << std::endl;
    std::cout << "The OpenJPEG open-source library will be used for the conversion."
              << std::endl;
    std::cout
            << "The following link contains information about HTJ2K decoding libraries."
            << std::endl;
    std::cout
            << "https://docs.aws.amazon.com/healthimaging/latest/devguide/reference-htj2k.html"
            << std::endl;
    askQuestion("Enter return to download and convert the images.", alwaysTrueTest);

    bool result = downloadDecodeAndCheckImageFrames(dataStoreId,
                                                    allImageFrameIDs,
                                                    outDirectory, clientConfiguration);

    if (result) {
        std::cout << "The image files were successfully decoded and validated."
                  << std::endl;
        std::cout << "The HTJ2K image files are located in the directory\n"
                  << "'" << outDirectory << "' in the working directory\n"
                  << "of this application." << std::endl;
    }

    printAsterisksLine();
    std::cout << "This concludes this workflow." << std::endl;
    printAsterisksLine();

    return result & cleanup(stackName, dataStoreId, clientConfiguration);
}

// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.wait_import]
//! Routine which waits for a DICOM import job to complete.
/*!
 * @param dataStoreID: The HealthImaging data store ID.
 * @param importJobId: The import job ID.
 * @param clientConfiguration : Aws client configuration.
 * @return  bool: Function succeeded.
 */
bool AwsDoc::Medical_Imaging::waitImportJobCompleted(const Aws::String &datastoreID,
                                                     const Aws::String &importJobId,
                                                     const Aws::Client::ClientConfiguration &clientConfiguration) {

    Aws::MedicalImaging::Model::JobStatus jobStatus = Aws::MedicalImaging::Model::JobStatus::IN_PROGRESS;
    while (jobStatus == Aws::MedicalImaging::Model::JobStatus::IN_PROGRESS) {
        std::this_thread::sleep_for(std::chrono::seconds(1));

        Aws::MedicalImaging::Model::GetDICOMImportJobOutcome getDicomImportJobOutcome = getDICOMImportJob(
                datastoreID, importJobId,
                clientConfiguration);

        if (getDicomImportJobOutcome.IsSuccess()) {
            jobStatus = getDicomImportJobOutcome.GetResult().GetJobProperties().GetJobStatus();

            std::cout << "DICOM import job status: " <<
                      Aws::MedicalImaging::Model::JobStatusMapper::GetNameForJobStatus(
                              jobStatus) << std::endl;
        }
        else {
            std::cerr << "Failed to get import job status because "
                      << getDicomImportJobOutcome.GetError().GetMessage() << std::endl;
            return false;
        }
    }

    return jobStatus == Aws::MedicalImaging::Model::JobStatus::COMPLETED;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.wait_import]

//! Routine which retrieves the image sets created for an import job.
/*!
 * @param dataStoreID: The HealthImaging data store ID.
 * @param importJobId: The import job ID.
 * @param imageSets: Array to receive the image set IDs.
 * @param clientConfiguration : Aws client configuration.
 * @return  bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.get_image_sets]
bool
AwsDoc::Medical_Imaging::getImageSetsForDicomImportJob(const Aws::String &datastoreID,
                                                       const Aws::String &importJobId,
                                                       Aws::Vector<Aws::String> &imageSets,
                                                       const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::MedicalImaging::Model::GetDICOMImportJobOutcome getDicomImportJobOutcome = getDICOMImportJob(
            datastoreID, importJobId, clientConfiguration);
    bool result = false;
    if (getDicomImportJobOutcome.IsSuccess()) {
        auto outputURI = getDicomImportJobOutcome.GetResult().GetJobProperties().GetOutputS3Uri();
        Aws::Http::URI uri(outputURI);
        const Aws::String &bucket = uri.GetAuthority();
        Aws::String key = uri.GetPath();

        Aws::S3::S3Client s3Client(clientConfiguration);
        Aws::S3::Model::GetObjectRequest objectRequest;
        objectRequest.SetBucket(bucket);
        objectRequest.SetKey(key + "/" + IMPORT_JOB_MANIFEST_FILE_NAME);

        auto getObjectOutcome = s3Client.GetObject(objectRequest);
        if (getObjectOutcome.IsSuccess()) {
            auto &data = getObjectOutcome.GetResult().GetBody();

            std::stringstream stringStream;
            stringStream << data.rdbuf();

            try {
                // Use JMESPath to extract the image set IDs.
                // https://jmespath.org/specification.html
                std::string jmesPathExpression = "jobSummary.imageSetsSummary[].imageSetId";
                jsoncons::json doc = jsoncons::json::parse(stringStream.str());

                jsoncons::json imageSetsJson = jsoncons::jmespath::search(doc,
                                                                          jmesPathExpression);\
                for (auto &imageSet: imageSetsJson.array_range()) {
                    imageSets.push_back(imageSet.as_string());
                }

                result = true;
            }
            catch (const std::exception &e) {
                std::cerr << e.what() << '\n';
            }

        }
        else {
            std::cerr << "Failed to get object because "
                      << getObjectOutcome.GetError().GetMessage() << std::endl;
        }

    }
    else {
        std::cerr << "Failed to get import job status because "
                  << getDicomImportJobOutcome.GetError().GetMessage() << std::endl;
    }

    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.get_image_sets]

//! Routine which retrieves image frame information for the image frames
//! associated with an image set.
/*!
 * @param dataStoreID: The HealthImaging data store ID.
 * @param importJobId: The import job ID.
 * @param imageSets: An image set ID.
 * @param imageSets: Array to receive structs of image frame in information.
 * @param clientConfiguration : Aws client configuration.
 * @return  bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.get_image_frames]
bool AwsDoc::Medical_Imaging::getImageFramesForImageSet(const Aws::String &dataStoreID,
                                                        const Aws::String &imageSetID,
                                                        const Aws::String &outDirectory,
                                                        Aws::Vector<ImageFrameInfo> &imageFrames,
                                                        const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::String fileName = outDirectory + "/" + imageSetID + "_metadata.json.gzip";
    bool result = false;
    if (getImageSetMetadata(dataStoreID, imageSetID, "", // Empty string for version ID.
                            fileName, clientConfiguration)) {
        try {
            std::string metadataGZip;
            {
                std::ifstream inFileStream(fileName.c_str(), std::ios::binary);
                if (!inFileStream) {
                    throw std::runtime_error("Failed to open file " + fileName);
                }

                std::stringstream stringStream;
                stringStream << inFileStream.rdbuf();
                metadataGZip = stringStream.str();
            }
            std::string metadataJson = gzip::decompress(metadataGZip.data(),
                                                        metadataGZip.size());
            // Use JMESPath to extract the image set IDs.
            // https://jmespath.org/specification.html
            jsoncons::json doc = jsoncons::json::parse(metadataJson);
            std::string jmesPathExpression = "Study.Series.*.Instances[].*[]";
            jsoncons::json instances = jsoncons::jmespath::search(doc,
                                                                  jmesPathExpression);
            for (auto &instance: instances.array_range()) {
                jmesPathExpression = "DICOM.RescaleSlope";
                std::string rescaleSlope = jsoncons::jmespath::search(instance,
                                                                      jmesPathExpression).to_string();
                jmesPathExpression = "DICOM.RescaleIntercept";
                std::string rescaleIntercept = jsoncons::jmespath::search(instance,
                                                                          jmesPathExpression).to_string();

                jmesPathExpression = "ImageFrames[][]";
                jsoncons::json imageFramesJson = jsoncons::jmespath::search(instance,
                                                                            jmesPathExpression);

                for (auto &imageFrame: imageFramesJson.array_range()) {
                    ImageFrameInfo imageFrameIDs;
                    imageFrameIDs.mImageSetId = imageSetID;
                    imageFrameIDs.mImageFrameId = imageFrame.find(
                            "ID")->value().as_string();
                    imageFrameIDs.mRescaleIntercept = rescaleIntercept;
                    imageFrameIDs.mRescaleSlope = rescaleSlope;
                    imageFrameIDs.MinPixelValue = imageFrame.find(
                            "MinPixelValue")->value().as_string();
                    imageFrameIDs.MaxPixelValue = imageFrame.find(
                            "MaxPixelValue")->value().as_string();

                    jmesPathExpression = "max_by(PixelDataChecksumFromBaseToFullResolution, &Width).Checksum";
                    jsoncons::json checksumJson = jsoncons::jmespath::search(imageFrame,
                                                                             jmesPathExpression);
                    imageFrameIDs.mFullResolutionChecksum = checksumJson.as_integer<uint32_t>();

                    imageFrames.emplace_back(imageFrameIDs);
                }
            }

            result = true;
        }
        catch (const std::exception &e) {
            std::cerr << "getImageFramesForImageSet failed because " << e.what()
                      << std::endl;
        }
    }

    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.get_image_frames]

//! Routine which starts a DICOM import.
/*!
 * @param dataStoreID: The HealthImaging data store ID.
 * @param inputBucketName: The S3 bucket containing DICOM files to import.
 * @param inputDirectory: The directory in the S3 bucket containing DICOM files to import.
 * @param outputBucketName: The S3 bucket for the output.
 * @param outputDirectory: The directory in the S3 bucket for the output.
 * @param roleArn: The IAM role for the  import job.
 * @param importJobId: A string to receive the import job ID.
 * @param clientConfiguration : Aws client configuration.
 * @return  bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.dicom-import]
bool AwsDoc::Medical_Imaging::startDicomImport(const Aws::String &dataStoreID,
                                               const Aws::String &inputBucketName,
                                               const Aws::String &inputDirectory,
                                               const Aws::String &outputBucketName,
                                               const Aws::String &outputDirectory,
                                               const Aws::String &roleArn,
                                               Aws::String &importJobId,
                                               const Aws::Client::ClientConfiguration &clientConfiguration) {
    bool result = false;
    if (startDICOMImportJob(dataStoreID, inputBucketName, inputDirectory,
                            outputBucketName, outputDirectory, roleArn, importJobId,
                            clientConfiguration)) {
        std::cout << "DICOM import job started with job ID " << importJobId << "."
                  << std::endl;
        result = waitImportJobCompleted(dataStoreID, importJobId, clientConfiguration);
        if (result) {
            std::cout << "DICOM import job completed." << std::endl;

        }
    }

    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.dicom-import]

//! Routine which saves image frames, decodes them and uses the checksum to
//! validate the decoded images.
/*!
 * @param outcome: The outcome of a GetImageFrame request.
 * @param outDirectory: A directory for saved files.
 * @param imageFrameInfo: Info for this image frame.
  * @return  bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.handle_get_frame]
bool AwsDoc::Medical_Imaging::handleGetImageFrameResult(
        const Aws::MedicalImaging::Model::GetImageFrameOutcome &outcome,
        const Aws::String &outDirectory,
        const ImageFrameInfo &imageFrameInfo) {
    bool result = false;
    if (outcome.IsSuccess()) {
        Aws::String fileNameBase =
                outDirectory + "/imageSet_" + imageFrameInfo.mImageSetId +
                "_frame_" +
                imageFrameInfo.mImageFrameId;
        Aws::String jphFileName = fileNameBase + ".jph";
        auto &buffer = outcome.GetResult().GetImageFrameBlob();
        {
            std::ofstream outfile(jphFileName, std::ios::binary);
            outfile << buffer.rdbuf();
        }

        result = decodeJPHFileAndValidateWithChecksum(jphFileName,
                                                      imageFrameInfo.mFullResolutionChecksum);


        if (DEBUGGING) {
            std::cout << "Downloaded and converted image frame: "
                      << imageFrameInfo.mImageFrameId << " from image set: "
                      << imageFrameInfo.mImageSetId << std::endl;
        }
    }
    else {
        std::cerr << "Failed to get image frame " << "Image frame: "
                  << imageFrameInfo.mImageFrameId << " from image set: "
                  << imageFrameInfo.mImageSetId << " because "
                  << outcome.GetError().GetMessage() << std::endl;

    }

    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.handle_get_frame]

//! Routine which downloads image frames, decodes them and uses the checksum to
//! validate the decoded images.
/*!
 * @param dataStoreID: The HealthImaging data store ID.
 * @param importJobId: A list of structs containing image frame information.
 * @param imageSets: A directory for the downloaded images.
 * @param clientConfiguration : Aws client configuration.
 * @return  bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.download_frames]
bool AwsDoc::Medical_Imaging::downloadDecodeAndCheckImageFrames(
        const Aws::String &dataStoreID,
        const Aws::Vector<ImageFrameInfo> &imageFrames,
        const Aws::String &outDirectory,
        const Aws::Client::ClientConfiguration &clientConfiguration) {

    Aws::Client::ClientConfiguration clientConfiguration1(clientConfiguration);
    clientConfiguration1.executor = Aws::MakeShared<Aws::Utils::Threading::PooledThreadExecutor>(
            "executor", 25);
    Aws::MedicalImaging::MedicalImagingClient medicalImagingClient(
            clientConfiguration1);

    Aws::Utils::Threading::Semaphore semaphore(0, 1);
    std::atomic<size_t> count(imageFrames.size());

    bool result = true;
    for (auto &imageFrame: imageFrames) {
        Aws::MedicalImaging::Model::GetImageFrameRequest getImageFrameRequest;
        getImageFrameRequest.SetDatastoreId(dataStoreID);
        getImageFrameRequest.SetImageSetId(imageFrame.mImageSetId);

        Aws::MedicalImaging::Model::ImageFrameInformation imageFrameInformation;
        imageFrameInformation.SetImageFrameId(imageFrame.mImageFrameId);
        getImageFrameRequest.SetImageFrameInformation(imageFrameInformation);

        auto getImageFrameAsyncLambda = [&semaphore, &result, &count, imageFrame, outDirectory](
                const Aws::MedicalImaging::MedicalImagingClient *client,
                const Aws::MedicalImaging::Model::GetImageFrameRequest &request,
                Aws::MedicalImaging::Model::GetImageFrameOutcome outcome,
                const std::shared_ptr<const Aws::Client::AsyncCallerContext> &context) {

                if (!handleGetImageFrameResult(outcome, outDirectory, imageFrame)) {
                    std::cerr << "Failed to download and convert image frame: "
                              << imageFrame.mImageFrameId << " from image set: "
                              << imageFrame.mImageSetId << std::endl;
                    result = false;
                }

                count--;
                if (count <= 0) {

                    semaphore.ReleaseAll();
                }
        }; // End of 'getImageFrameAsyncLambda' lambda.

        medicalImagingClient.GetImageFrameAsync(getImageFrameRequest,
                                                getImageFrameAsyncLambda);
    }

    if (count > 0) {
        semaphore.WaitOne();
    }

    if (result) {
        std::cout << imageFrames.size() << " image files were downloaded."
                  << std::endl;
    }

    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.download_frames]

//! Routine which deletes the image sets in a data store.
/*!
 * @param datastoreID: The HealthImaging data store ID.
 * @param clientConfiguration: Aws client configuration.
 * @return bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.empty_data_store]
bool AwsDoc::Medical_Imaging::emptyDatastore(const Aws::String &datastoreID,
                                             const Aws::Client::ClientConfiguration &clientConfiguration) {

    Aws::MedicalImaging::Model::SearchCriteria emptyCriteria;
    Aws::Vector<Aws::String> imageSetIDs;
    bool result = false;
    if (searchImageSets(datastoreID, emptyCriteria, imageSetIDs,
                        clientConfiguration)) {
        result = true;
        for (auto &imageSetID: imageSetIDs) {
            result &= deleteImageSet(datastoreID, imageSetID, clientConfiguration);
        }
    }

    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.empty_data_store]

//! Routine which deletes workflow resources after asking the user.
/*!
 * @param stackName: The CloudFormation stack name.
 * @param dataStoreId: The HealthImaging data store ID.
  * @param clientConfiguration : Aws client configuration.
 * @return  bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.clean_up]
bool AwsDoc::Medical_Imaging::cleanup(const Aws::String &stackName,
                                      const Aws::String &dataStoreId,
                                      const Aws::Client::ClientConfiguration &clientConfiguration) {
    bool result = true;

    if (!stackName.empty() && askYesNoQuestion(
            "Would you like to delete the stack " + stackName + "? (y/n)")) {
        std::cout << "Deleting the image sets in the stack." << std::endl;
        result &= emptyDatastore(dataStoreId, clientConfiguration);
        printAsterisksLine();
        std::cout << "Deleting the stack." << std::endl;
        result &= deleteStack(stackName, clientConfiguration);
    }
    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.clean_up]

/*
 *
 * main function
*
 *  Usage: 'run_medical_image_sets_and_frames_workflow'
 *
*/
int main(int argc, char **argv) {
    (void) argc;
    (void) argv;
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::Medical_Imaging::workingWithHealthImagingImageSetsAndImageFrames(
                clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

//! Routine which creates a CloudFormation stack.
/*!
   \param stackName: The stack name.
   \param dataStoreName: A data store name passed as a parameter.
   \param clientConfiguration: Aws client configuration.
   \return ws::Map<Aws::String, Aws::String>: Map of outputs.
*/
Aws::Map<Aws::String, Aws::String>
AwsDoc::Medical_Imaging::createCloudFormationStack(const Aws::String &stackName,
                                                   const Aws::String &dataStoreName,
                                                   const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::Map<Aws::String, Aws::String> result;
    Aws::CloudFormation::CloudFormationClient cloudFormationClient(
            clientConfiguration);
    Aws::CloudFormation::Model::CreateStackRequest createStackRequest;
    createStackRequest.SetStackName(stackName);
    std::ifstream inFileStream(STACK_TEMPLATE_PATH);

    if (!inFileStream) {
        std::cerr << "Failed to open file" << std::endl;
        return result;
    }

    std::stringstream stringStream;
    stringStream << inFileStream.rdbuf();
    createStackRequest.SetTemplateBody(stringStream.str());

    Aws::String accountID = getUserAccountID(clientConfiguration);
    createStackRequest.SetParameters(
            {
                    Aws::CloudFormation::Model::Parameter().WithParameterKey(
                            DATASTORE_PARAMETER).WithParameterValue(dataStoreName),
                    Aws::CloudFormation::Model::Parameter().WithParameterKey(
                            USER_ACCOUNT_ID_PARAMETER).WithParameterValue(accountID)
            });
    createStackRequest.SetCapabilities(
            {Aws::CloudFormation::Model::Capability::CAPABILITY_IAM});

    auto outcome = cloudFormationClient.CreateStack(createStackRequest);

    Aws::Vector<Aws::CloudFormation::Model::Output> outputs;
    if (outcome.IsSuccess()) {
        std::cout << "Stack creation initiated." << std::endl;
        std::cout << "Waiting for the stack to be created." << std::endl;
        waitStackCreated(cloudFormationClient, stackName, outputs);
    }
    else {
        std::cerr << "Failed to create stack" << outcome.GetError().GetMessage()
                  << std::endl;
    }

    if (!outputs.empty()) {
        for (auto &output: outputs) {
            result[output.GetOutputKey()] = output.GetOutputValue();
        }
    }
    return result;
}

//! Routine which deletes a CloudFormation stack.
/*!
   \param stackName: The stack name.
   \param clientConfiguration: Aws client configuration.
   \return bool: Function succeeded.
*/
bool AwsDoc::Medical_Imaging::deleteStack(const std::string &stackName,
                                          const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::CloudFormation::CloudFormationClient cloudFormationClient(
            clientConfiguration);
    Aws::CloudFormation::Model::DeleteStackRequest deleteStackRequest;
    deleteStackRequest.SetStackName(stackName);
    auto outcome = cloudFormationClient.DeleteStack(deleteStackRequest);
    bool result = false;
    if (outcome.IsSuccess()) {
        std::cout << "Stack deletion initiated." << std::endl;
        result = waitStackDeleted(cloudFormationClient, stackName);
    }
    else {
        std::cerr << "Failed to delete stack" << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return result;
}

//! Routine to configure OpenJPEG logging.
/*!
 * @param codec: An OpenJPEG codec.
 * @param level: The logging level.
 * @return  bool: Function succeeded.
 */
bool AwsDoc::Medical_Imaging::setupCodecLogging(opj_codec_t *codec, int *levelPtr) {
    auto messageCallback = [](const char *msg, void *data) {
            int level = *reinterpret_cast<int *>(data);
            if (level > 1) {
                std::cout << msg << std::endl;
            }
            else {
                std::cerr << msg << std::endl;
            }
    };

    OPJ_BOOL result = true;
    if (*levelPtr > 0) {
        result &= opj_set_error_handler(codec, messageCallback, levelPtr);
    }
    if (*levelPtr > 1) {
        result &= opj_set_warning_handler(codec, messageCallback, levelPtr);
    }
    if (*levelPtr > 2) {
        result &= opj_set_info_handler(codec, messageCallback, levelPtr);
    }

    return result;
}

//! Routine which decodes an HTJ2K-encoded image and validates with a checksum.
/*!
 * @param jphFile: The path to the image file.
 * @param crc32Checksum: The CRC32 checksum.
 * @return  bool: Function succeeded.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.decode_and_check]
bool AwsDoc::Medical_Imaging::decodeJPHFileAndValidateWithChecksum(
        const Aws::String &jphFile,
        uint32_t crc32Checksum) {
    opj_image_t *outputImage = jphImageToOpjBitmap(jphFile);
    if (!outputImage) {
        return false;
    }

    bool result = true;
    if (!verifyChecksumForImage(outputImage, crc32Checksum)) {
        std::cerr << "The checksum for the image does not match the expected value."
                  << std::endl;
        std::cerr << "File :" << jphFile << std::endl;
        result = false;
    }

    opj_image_destroy(outputImage);

    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.decode_and_check]

//! Routine which decodes an HTJ2K-encoded image using the OpenJPEG library.
/*!
 * @param jphFile: The path to the image file.
 * @return  opj_image_t: An OpenJPEG image struct or a null ptr.
 */
// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.decode_jph]
opj_image *
AwsDoc::Medical_Imaging::jphImageToOpjBitmap(const Aws::String &jphFile) {
    opj_stream_t *inFileStream = nullptr;
    opj_codec_t *decompressorCodec = nullptr;
    opj_image_t *outputImage = nullptr;
    try {
        std::shared_ptr<opj_dparameters> decodeParameters = std::make_shared<opj_dparameters>();
        memset(decodeParameters.get(), 0, sizeof(opj_dparameters));

        opj_set_default_decoder_parameters(decodeParameters.get());

        decodeParameters->decod_format = 1; // JP2 image format.
        decodeParameters->cod_format = 2; // BMP image format.

        std::strncpy(decodeParameters->infile, jphFile.c_str(),
                     OPJ_PATH_LEN);

        inFileStream = opj_stream_create_default_file_stream(
                decodeParameters->infile, true);
        if (!inFileStream) {
            throw std::runtime_error(
                    "Unable to create input file stream for file '" + jphFile + "'.");
        }

        decompressorCodec = opj_create_decompress(OPJ_CODEC_JP2);
        if (!decompressorCodec) {
            throw std::runtime_error("Failed to create decompression codec.");
        }

        int decodeMessageLevel = 1;
        if (!setupCodecLogging(decompressorCodec, &decodeMessageLevel)) {
            std::cerr << "Failed to setup codec logging." << std::endl;
        }

        if (!opj_setup_decoder(decompressorCodec, decodeParameters.get())) {
            throw std::runtime_error("Failed to setup decompression codec.");
        }
        if (!opj_codec_set_threads(decompressorCodec, 4)) {
            throw std::runtime_error("Failed to set decompression codec threads.");
        }

        if (!opj_read_header(inFileStream, decompressorCodec, &outputImage)) {
            throw std::runtime_error("Failed to read header.");
        }

        if (!opj_decode(decompressorCodec, inFileStream,
                        outputImage)) {
            throw std::runtime_error("Failed to decode.");
        }

        if (DEBUGGING) {
            std::cout << "image width : " << outputImage->x1 - outputImage->x0
                      << std::endl;
            std::cout << "image height : " << outputImage->y1 - outputImage->y0
                      << std::endl;
            std::cout << "number of channels: " << outputImage->numcomps
                      << std::endl;
            std::cout << "colorspace : " << outputImage->color_space << std::endl;
        }

    } catch (const std::exception &e) {
        std::cerr << e.what() << std::endl;
        if (outputImage) {
            opj_image_destroy(outputImage);
            outputImage = nullptr;
        }
    }
    if (inFileStream) {
        opj_stream_destroy(inFileStream);
    }
    if (decompressorCodec) {
        opj_destroy_codec(decompressorCodec);
    }

    return outputImage;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.decode_jph]

// snippet-start:[cpp.example_code.medical-imaging.image-sets-workflow.verify_check_sum]
//! Template function which converts a planar image bitmap to an interleaved image bitmap and
//! then verifies the checksum of the bitmap.
/*!
 * @param image: The OpenJPEG image struct.
 * @param crc32Checksum: The CRC32 checksum.
 * @return  bool: Function succeeded.
 */
template<class myType>
bool verifyChecksumForImageForType(opj_image_t *image, uint32_t crc32Checksum) {
    uint32_t width = image->x1 - image->x0;
    uint32_t height = image->y1 - image->y0;
    uint32_t numOfChannels = image->numcomps;

    // Buffer for interleaved bitmap.
    std::vector<myType> buffer(width * height * numOfChannels);

    // Convert planar bitmap to interleaved bitmap.
    for (uint32_t channel = 0; channel < numOfChannels; channel++) {
        for (uint32_t row = 0; row < height; row++) {
            uint32_t fromRowStart = row / image->comps[channel].dy * width /
                                    image->comps[channel].dx;
            uint32_t toIndex = (row * width) * numOfChannels + channel;

            for (uint32_t col = 0; col < width; col++) {
                uint32_t fromIndex = fromRowStart + col / image->comps[channel].dx;

                buffer[toIndex] = static_cast<myType>(image->comps[channel].data[fromIndex]);

                toIndex += numOfChannels;
            }
        }
    }

    // Verify checksum.
    boost::crc_32_type crc32;
    crc32.process_bytes(reinterpret_cast<char *>(buffer.data()),
                        buffer.size() * sizeof(myType));

    bool result = crc32.checksum() == crc32Checksum;
    if (!result) {
        std::cerr << "verifyChecksumForImage, checksum mismatch, expected - "
                  << crc32Checksum << ", actual - " << crc32.checksum()
                  << std::endl;
    }

    return result;
}

//! Routine which verifies the checksum of an OpenJPEG image struct.
/*!
 * @param image: The OpenJPEG image struct.
 * @param crc32Checksum: The CRC32 checksum.
 * @return  bool: Function succeeded.
 */
bool AwsDoc::Medical_Imaging::verifyChecksumForImage(opj_image_t *image,
                                                     uint32_t crc32Checksum) {
    uint32_t channels = image->numcomps;
    bool result = false;
    if (0 < channels) {
        // Assume the precision is the same for all channels.
        uint32_t precision = image->comps[0].prec;
        bool signedData = image->comps[0].sgnd;
        uint32_t bytes = (precision + 7) / 8;

        if (signedData) {
            switch (bytes) {
                case 1 :
                    result = verifyChecksumForImageForType<int8_t>(image,
                                                                   crc32Checksum);
                    break;
                case 2 :
                    result = verifyChecksumForImageForType<int16_t>(image,
                                                                    crc32Checksum);
                    break;
                case 4 :
                    result = verifyChecksumForImageForType<int32_t>(image,
                                                                    crc32Checksum);
                    break;
                default:
                    std::cerr
                            << "verifyChecksumForImage, unsupported data type, signed bytes - "
                            << bytes << std::endl;
                    break;
            }
        }
        else {
            switch (bytes) {
                case 1 :
                    result = verifyChecksumForImageForType<uint8_t>(image,
                                                                    crc32Checksum);
                    break;
                case 2 :
                    result = verifyChecksumForImageForType<uint16_t>(image,
                                                                     crc32Checksum);
                    break;
                case 4 :
                    result = verifyChecksumForImageForType<uint32_t>(image,
                                                                     crc32Checksum);
                    break;
                default:
                    std::cerr
                            << "verifyChecksumForImage, unsupported data type, unsigned bytes - "
                            << bytes << std::endl;
                    break;
            }
        }

        if (!result) {
            std::cerr << "verifyChecksumForImage, error bytes " << bytes
                      << " signed "
                      << signedData << std::endl;
        }
    }
    else {
        std::cerr << "'verifyChecksumForImage', no channels in the image."
                  << std::endl;
    }
    return result;
}
// snippet-end:[cpp.example_code.medical-imaging.image-sets-workflow.verify_check_sum]

//! Routine which gets the user's account ID.
/*!
   \param clientConfig: Aws client configuration.
   \return bool: Function succeeded.
*/
Aws::String AwsDoc::Medical_Imaging::getUserAccountID(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::STS::STSClient stsClient(clientConfiguration);
    Aws::STS::Model::GetCallerIdentityRequest getCallerIdentityRequest;
    auto getCallerIdentityOutcome = stsClient.GetCallerIdentity(
            getCallerIdentityRequest);

    Aws::String result;
    if (getCallerIdentityOutcome.IsSuccess()) {
        result = getCallerIdentityOutcome.GetResult().GetAccount();
    }
    else {
        std::cerr << "Failed to get caller identity: "
                  << getCallerIdentityOutcome.GetError().GetMessage() << std::endl;
    }

    return result;
}

//! Routine which waits until a CloudFormation stack is created.
/*!
   \param cloudFormationClient: A CloudFormation client.
   \param stackName: The stack name.
   \return bool: Function succeeded.
*/
bool AwsDoc::Medical_Imaging::waitStackCreated(
        Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
        const std::string &stackName,
        Aws::Vector<Aws::CloudFormation::Model::Output> &outputs) {
    std::this_thread::sleep_for(std::chrono::seconds(1));
    Aws::CloudFormation::Model::DescribeStacksRequest describeStacksRequest;
    describeStacksRequest.SetStackName(stackName);
    Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS;

    while (stackStatus ==
           Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS) {
        stackStatus = Aws::CloudFormation::Model::StackStatus::NOT_SET;
        auto outcome = cloudFormationClient.DescribeStacks(describeStacksRequest);
        if (outcome.IsSuccess()) {
            const auto &stacks = outcome.GetResult().GetStacks();
            if (!stacks.empty()) {
                const auto &stack = stacks[0];
                stackStatus = stack.GetStackStatus();
                if (stackStatus ==
                    Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE) {
                    outputs = stack.GetOutputs();
                }
                else if (stackStatus !=
                         Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS) {
                    std::cerr << "Failed to create stack because "
                              << stack.GetStackStatusReason() << std::endl;
                }
            }
        }
    }

    if (stackStatus == Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE) {
        std::cout << "Stack creation completed." << std::endl;
    }

    return stackStatus == Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE;
}

//! Routine which waits until a CloudFormation stack is deleted.
/*!
   \param cloudFormationClient: A CloudFormation client.
   \param stackName: The stack name.
   \return bool: Function succeeded.
*/
bool AwsDoc::Medical_Imaging::waitStackDeleted(
        Aws::CloudFormation::CloudFormationClient &cloudFormationClient,
        const std::string &stackName) {
    std::this_thread::sleep_for(std::chrono::seconds(1));
    Aws::CloudFormation::Model::DescribeStacksRequest describeStacksRequest;
    describeStacksRequest.SetStackName(stackName);
    Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS;

    while (stackStatus ==
           Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS) {
        stackStatus = Aws::CloudFormation::Model::StackStatus::NOT_SET;
        auto outcome = cloudFormationClient.DescribeStacks(describeStacksRequest);
        if (outcome.IsSuccess()) {
            const auto &stacks = outcome.GetResult().GetStacks();
            if (!stacks.empty()) {
                const auto &stack = stacks[0];
                stackStatus = stack.GetStackStatus();
                if (stackStatus !=
                    Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS &&
                    stackStatus !=
                    Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE) {
                    std::cerr << "Failed to delete stack because "
                              << stack.GetStackStatusReason() << std::endl;
                }
            }
            else {
                stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE;
            }
        }
        else {
            auto &error = outcome.GetError();
            if (error.GetResponseCode() ==
                Aws::Http::HttpResponseCode::BAD_REQUEST &&
                (outcome.GetError().GetMessage().find("does not exist") !=
                 std::string::npos)) {
                stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE;
            }
            else {
                std::cerr << "Failed to describe stack. "
                          << outcome.GetError().GetMessage() << std::endl;
            }
        }
    }

    if (stackStatus == Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE) {
        std::cout << "Stack deletion completed." << std::endl;
    }

    return stackStatus == Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE;
}

//! Routine copies a folder between 2 S3 buckets.
/*!
 * @param fromBucket: The S3 bucket to copy from.
 * @param fromDirectory: The directory to copy from.
 * @param toBucket: The S3 bucket to copy to.
 * @param toDirectory: The directory to copy to.
 * @param clientConfiguration: Aws client configuration.
 * @return: Function succeeded.
 */
bool
AwsDoc::Medical_Imaging::copySeriesBetweenBuckets(const Aws::String &fromBucket,
                                                  const Aws::String &fromDirectory,
                                                  const Aws::String &toBucket,
                                                  const Aws::String &toDirectory,
                                                  const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::Client::ClientConfiguration clientConfiguration1(clientConfiguration);
    clientConfiguration1.executor = Aws::MakeShared<Aws::Utils::Threading::PooledThreadExecutor>(
            "executor", 25);


    Aws::S3::S3Client s3Client(clientConfiguration1);
    Aws::S3::Model::ListObjectsV2Request listObjectsRequest;
    listObjectsRequest.SetBucket(fromBucket);
    listObjectsRequest.SetPrefix(fromDirectory);

    std::vector<Aws::String> objectKeys;
    Aws::String delimiter; // Used for pagination.
    do {
        if (!delimiter.empty()) {
            listObjectsRequest.SetDelimiter(delimiter);
        }
        auto listObjectsOutcome = s3Client.ListObjectsV2(listObjectsRequest);
        if (listObjectsOutcome.IsSuccess()) {
            for (const auto &object: listObjectsOutcome.GetResult().GetContents()) {
                objectKeys.push_back(object.GetKey());
            }

            delimiter = listObjectsOutcome.GetResult().GetNextContinuationToken();
        }
        else {
            std::cerr << "Failed to list objects in bucket " << fromBucket <<
                      " because " << listObjectsOutcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    } while (!delimiter.empty());

    Aws::Utils::Threading::Semaphore semaphore(0, 1);
    std::atomic<size_t> count(objectKeys.size());

    bool result = true;

    for (auto &objectKey: objectKeys) {
        Aws::S3::Model::CopyObjectRequest copyObjectRequest;
        Aws::String source = fromBucket;
        source += "/" + objectKey;
        copyObjectRequest.SetCopySource(source);

        copyObjectRequest.SetBucket(toBucket);
        Aws::String key = toDirectory;
        key += "/" + objectKey;

        copyObjectRequest.SetKey(key);

        auto copyObjectLambda = [&result, &semaphore, &count, objectKey](
                const Aws::S3::S3Client *,
                const Aws::S3::Model::CopyObjectRequest &,
                const Aws::S3::Model::CopyObjectOutcome &outcome,
                const std::shared_ptr<const Aws::Client::AsyncCallerContext> &) {

                if (!outcome.IsSuccess()) {
                    result = false;
                    std::cerr << "Error copying an object "
                              << outcome.GetError().GetMessage() <<
                              std::endl;
                }
                else if (DEBUGGING) {
                    std::cout << "Copied the object " << objectKey << "."
                              << std::endl;
                }
                count--;
                if (0 >= count) {
                    semaphore.ReleaseAll();
                }
        };  // End of 'copyObjectLambda'

        s3Client.CopyObjectAsync(copyObjectRequest, copyObjectLambda);
    }

    if (0 < count) {
        semaphore.WaitOne();
    }

    if (result) {
        std::cout << objectKeys.size() << " DICOM files were copied." << std::endl;
    }

    return result;
}

//! Routine which gets the CloudFormation stack outputs from a map.
/*!
 * @param outputs: A map of string outputs.
 * @param dataStoreId: The HealthImaging data store ID.
 * @param inputBucketName: The S3 bucket for DICOM import input.
 * @param outputBucketName: The S3 bucket for DICOM import output.
 * @param roleArn: The IAM role for DICOM  import.
 * @return  bool: Function succeeded.
 */
bool AwsDoc::Medical_Imaging::retrieveOutputs(
        const Aws::Map<Aws::String, Aws::String> &outputs, Aws::String &dataStoreId,
        Aws::String &inputBucketName, Aws::String &outputBucketName,
        Aws::String &roleArn) {
    if (outputs.empty()) {
        std::cerr << "This workflow will now exit because of an error."
                  << std::endl;
        return false;
    }
    auto iter = outputs.find(DATASTORE_ID_OUTPUT);
    if (iter != outputs.end()) {
        dataStoreId = iter->second;
    }
    else {
        std::cerr << "Failed retrieve '" << DATASTORE_ID_OUTPUT << "' output."
                  << std::endl;
        return false;
    }

    iter = outputs.find(BUCKET_NAME_OUTPUT);
    if (iter != outputs.end()) {
        inputBucketName = iter->second;
    }
    else {
        std::cerr << "Failed retrieve '" << BUCKET_NAME_OUTPUT << "' output."
                  << std::endl;
        return false;
    }

    iter = outputs.find(OUTPUT_BUCKET_NAME_OUTPUT);
    if (iter != outputs.end()) {
        outputBucketName = iter->second;
    }
    else {
        std::cerr << "Failed retrieve '" << OUTPUT_BUCKET_NAME_OUTPUT << "' output."
                  << std::endl;
        return false;
    }


    iter = outputs.find(ROLE_ARN_OUTPUT);
    if (iter != outputs.end()) {
        roleArn = iter->second;
    }
    else {
        std::cerr << "Failed retrieve '" << ROLE_ARN_OUTPUT << "' output."
                  << std::endl;
        return false;
    }

    return true;
}


//! Test routine passed as argument to askQuestion routine.
/*!
\param string: A string to test.
\return bool: True if empty.
*/
bool AwsDoc::Medical_Imaging::testForEmptyString(const Aws::String &string) {
    if (string.empty()) {
        std::cout << "Enter some text." << std::endl;
        return false;
    }

    return true;
}

//! Command line prompt/response utility function.
/*!
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::Medical_Imaging::askQuestion(const Aws::String &string,
                                                 const std::function<bool(
                                                         Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
    } while (!test(result));

    return result;
}

//! Command line prompt/response for yes/no question.
/*!
 \param string: A question prompt expecting a 'y' or 'n' response.
 \return bool: True if yes.
 */
bool AwsDoc::Medical_Imaging::askYesNoQuestion(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](
            const Aws::String &string1) -> bool {
            bool result = false;
            if (string1.length() == 1) {
                int answer = std::tolower(string1[0]);
                result = (answer == 'y') || (answer == 'n');
            }

            if (!result) {
                std::cout << "Answer 'y' or 'n'." << std::endl;
            }

            return result;
    });

    return std::tolower(resultString[0]) == 'y';
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int
AwsDoc::Medical_Imaging::askQuestionForIntRange(const Aws::String &string, int low,
                                                int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cerr << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cerr << "\nNot a valid number." << std::endl;
                return false;
            }
    });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

