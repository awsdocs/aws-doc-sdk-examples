//
// Created by Meyer, Steve on 6/11/24.
//

#ifndef EXAMPLES_REKOGNITION_SAMPLES_H
#define EXAMPLES_REKOGNITION_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace Rekognition {
        //! Detect instances of real-world entities within an image by using Amazon Rekognition
/*!
  \param image: The
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */

bool detectLabels(const Aws::String &imageBucket,
                  const Aws::String &imageKey,
                  const Aws::Client::ClientConfiguration &clientConfiguration);
    } // Rekognition

}// AwsDoc

#endif //EXAMPLES_REKOGNITION_SAMPLES_H
