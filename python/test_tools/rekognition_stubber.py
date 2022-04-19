# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Rekognition unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class RekognitionStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Rekognition unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Rekognition client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    @staticmethod
    def _face_to_dict(face):
        face_dict = {}
        if face.bounding_box is not None:
            face_dict['BoundingBox'] = face.bounding_box
        if face.confidence is not None:
            face_dict['Confidence'] = face.confidence
        if face.landmarks is not None:
            face_dict['Landmarks'] = face.landmarks
        if face.pose is not None:
            face_dict['Pose'] = face.pose
        if face.quality is not None:
            face_dict['Quality'] = face.quality
        if face.age_range is not None:
            face_dict.update({
                'AgeRange': {'Low': face.age_range[0], 'High': face.age_range[1]},
                'Smile': {'Value': face.smile},
                'Eyeglasses': {'Value': face.eyeglasses},
                'Sunglasses': {'Value': face.sunglasses},
                'Gender': {'Value': face.gender},
                'Beard': {'Value': face.beard},
                'Mustache': {'Value': face.mustache},
                'EyesOpen': {'Value': face.eyes_open},
                'MouthOpen': {'Value': face.mouth_open},
                'Emotions': [{'Type': emotion, 'Confidence': 80}
                             for emotion in face.emotions]})
        return face_dict

    @staticmethod
    def _celebrity_to_dict(celebrity):
        return {
            'Urls': celebrity.info_urls,
            'Name': celebrity.name,
            'Id': celebrity.id,
            'Face': RekognitionStubber._face_to_dict(celebrity.face)}

    @staticmethod
    def _person_to_dict(person):
        return {
            'Index': person.index,
            'Face': RekognitionStubber._face_to_dict(person.face)}

    @staticmethod
    def _label_to_dict(label):
        return {
            'Name': label.name,
            'Confidence': label.confidence,
            'Instances': label.instances,
            'Parents': label.parents
        }

    @staticmethod
    def _moderation_label_to_dict(label):
        return {
            'Name': label.name,
            'Confidence': label.confidence,
            'ParentName': label.parent_name
        }

    @staticmethod
    def _text_to_dict(text):
        return {
            'DetectedText': text.text,
            'Type': text.kind,
            'Id': text.id,
            'ParentId': text.parent_id,
            'Confidence': text.confidence,
            'Geometry': text.geometry
        }

    def stub_detect_faces(self, image, faces, error_code=None):
        expected_params = {'Image': image, 'Attributes': ['ALL']}
        response = {'FaceDetails': [self._face_to_dict(face) for face in faces]}
        self._stub_bifurcator(
            'detect_faces', expected_params, response, error_code=error_code)

    def stub_compare_faces(
            self, source_image, target_image, similarity, matches, unmatches,
            error_code=None):
        expected_params = {
            'SourceImage': source_image,
            'TargetImage': target_image,
            'SimilarityThreshold': similarity}
        response = {
            'FaceMatches': [{
                'Similarity': similarity,
                'Face': self._face_to_dict(match)
            } for match in matches],
            'UnmatchedFaces': [self._face_to_dict(unmatch) for unmatch in unmatches]}
        self._stub_bifurcator(
            'compare_faces', expected_params, response, error_code=error_code)

    def stub_detect_labels(
            self, image, max_labels, labels, error_code=None):
        expected_params = {}
        if image is not None:
            expected_params['Image'] = image
        if max_labels is not None:
            expected_params['MaxLabels'] = max_labels
        response = {'Labels': [self._label_to_dict(label) for label in labels]}
        self._stub_bifurcator(
            'detect_labels', expected_params, response, error_code=error_code)

    def stub_detect_moderation_labels(self, image, labels, error_code=None):
        expected_params = {'Image': image}
        response = {
            'ModerationLabels': [
                self._moderation_label_to_dict(label) for label in labels]}
        self._stub_bifurcator(
            'detect_moderation_labels', expected_params, response,
            error_code=error_code)

    def stub_detect_text(self, image, texts, error_code=None):
        expected_params = {'Image': image}
        response = {'TextDetections': [self._text_to_dict(text) for text in texts]}
        self._stub_bifurcator(
            'detect_text', expected_params, response, error_code=error_code)

    def stub_recognize_celebrities(self, image, celebrities, normals, error_code=None):
        expected_params = {'Image': image}
        response = {
            'CelebrityFaces': [
                self._celebrity_to_dict(celeb) for celeb in celebrities],
            'UnrecognizedFaces': [self._face_to_dict(face) for face in normals]}
        self._stub_bifurcator(
            'recognize_celebrities', expected_params, response, error_code=error_code)

    def stub_describe_collection(self, collection_id, collection, error_code=None):
        expected_params = {'CollectionId': collection_id}
        response = {
            'CollectionARN': collection.collection_arn,
            'FaceCount': collection.face_count,
            'CreationTimestamp': collection.created
        }
        self._stub_bifurcator(
            'describe_collection', expected_params, response, error_code=error_code)

    def stub_delete_collection(self, collection_id, error_code=None):
        expected_params = {'CollectionId': collection_id}
        self._stub_bifurcator(
            'delete_collection', expected_params, error_code=error_code)

    def stub_index_faces(
            self, collection_id, image, max_faces, indexed_faces, unindexed_faces,
            error_code=None):
        expected_params = {
            'CollectionId': collection_id, 'Image': image.image,
            'ExternalImageId': image.image_name, 'MaxFaces': max_faces,
            'DetectionAttributes': ['ALL']}
        response = {
            'FaceRecords': [{
                'Face': {'FaceId': face.face_id, 'ImageId': face.image_id},
                'FaceDetail': self._face_to_dict(face)
            } for face in indexed_faces],
            'UnindexedFaces': [{
                'FaceDetail': self._face_to_dict(face)
            }for face in unindexed_faces]}
        self._stub_bifurcator(
            'index_faces', expected_params, response, error_code=error_code)

    def stub_list_faces(self, collection_id, max_results, faces, error_code=None):
        expected_params = {'CollectionId': collection_id, 'MaxResults': max_results}
        response = {'Faces': [self._face_to_dict(face) for face in faces]}
        self._stub_bifurcator(
            'list_faces', expected_params, response, error_code=error_code)

    def stub_search_faces_by_image(
            self, collection_id, image, threshold, max_faces, image_face,
            collection_faces, error_code=None):
        expected_params = {
            'CollectionId': collection_id, 'Image': image.image,
            'FaceMatchThreshold': threshold, 'MaxFaces': max_faces}
        response = {
            'SearchedFaceBoundingBox': image_face.bounding_box,
            'SearchedFaceConfidence': image_face.confidence,
            'FaceMatches': [
                {'Face': self._face_to_dict(face)} for face in collection_faces]}
        self._stub_bifurcator(
            'search_faces_by_image', expected_params, response, error_code=error_code)

    def stub_search_faces(
            self, collection_id, face_id, threshold, max_faces, faces, error_code=None):
        expected_params = {
            'CollectionId': collection_id, 'FaceId': face_id,
            'FaceMatchThreshold': threshold, 'MaxFaces': max_faces}
        response = {
            'FaceMatches': [{'Face': self._face_to_dict(face)} for face in faces]}
        self._stub_bifurcator(
            'search_faces', expected_params, response, error_code=error_code)

    def stub_delete_faces(self, collection_id, face_ids, error_code=None):
        expected_params = {'CollectionId': collection_id, 'FaceIds': face_ids}
        response = {'DeletedFaces': face_ids}
        self._stub_bifurcator(
            'delete_faces', expected_params, response, error_code=error_code)

    def stub_create_collection(self, collection_id, collection, error_code=None):
        expected_params = {'CollectionId': collection_id}
        response = {
            'CollectionArn': collection.collection_arn
        }
        self._stub_bifurcator(
            'create_collection', expected_params, response, error_code=error_code)

    def stub_list_collections(self, max_results, collection_ids, error_code=None):
        expected_params = {'MaxResults': max_results}
        response = {'CollectionIds': collection_ids}
        self._stub_bifurcator(
            'list_collections', expected_params, response, error_code=error_code)

    def stub_start_detection(
            self, func_name, video, notification_channel, job_id, error_code=None):
        expected_params = {'Video': video, 'NotificationChannel': notification_channel}
        response = {'JobId': job_id}
        self._stub_bifurcator(
            func_name, expected_params, response, error_code=error_code)

    def stub_get_label_detection(
            self, job_id, job_status, labels, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'JobStatus': job_status, 'Labels': [{
            'Timestamp': label.timestamp,
            'Label': self._label_to_dict(label)} for label in labels]}
        self._stub_bifurcator(
            'get_label_detection', expected_params, response, error_code=error_code)

    def stub_get_face_detection(
            self, job_id, job_status, faces, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'JobStatus': job_status, 'Faces': [{
            'Timestamp': face.timestamp,
            'Face': self._face_to_dict(face)} for face in faces]}
        self._stub_bifurcator(
            'get_face_detection', expected_params, response, error_code=error_code)

    def stub_get_person_tracking(
            self, job_id, job_status, persons, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'JobStatus': job_status, 'Persons': [{
            'Timestamp': person.timestamp,
            'Person': self._person_to_dict(person)} for person in persons]}
        self._stub_bifurcator(
            'get_person_tracking', expected_params, response, error_code=error_code)

    def stub_get_celebrity_recognition(
            self, job_id, job_status, celebrities, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'JobStatus': job_status, 'Celebrities': [{
            'Timestamp': celebrity.timestamp,
            'Celebrity': self._celebrity_to_dict(celebrity)} for celebrity in celebrities]}
        self._stub_bifurcator(
            'get_celebrity_recognition', expected_params, response, error_code=error_code)

    def stub_get_content_moderation(
            self, job_id, job_status, labels, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'JobStatus': job_status, 'ModerationLabels': [{
            'Timestamp': label.timestamp,
            'ModerationLabel': self._moderation_label_to_dict(label)}
            for label in labels]}
        self._stub_bifurcator(
            'get_content_moderation', expected_params, response, error_code=error_code)
