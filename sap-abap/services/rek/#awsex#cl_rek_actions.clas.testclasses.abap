" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_rek_actions DEFINITION DEFERRED.
CLASS /awsex/cl_rek_actions DEFINITION LOCAL FRIENDS ltc_awsex_cl_rek_actions.

CLASS ltc_awsex_cl_rek_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    CLASS-DATA av_collection_id TYPE /aws1/rekcollectionid.
    CLASS-DATA av_collection_id_for_delete TYPE /aws1/rekcollectionid.
    CLASS-DATA av_test_bucket TYPE /aws1/s3_bucketname.
    CLASS-DATA av_test_image_key TYPE /aws1/s3_objectkey VALUE 'test-image.jpg'.
    CLASS-DATA av_test_image_key2 TYPE /aws1/s3_objectkey VALUE 'test-image2.jpg'.
    CLASS-DATA av_indexed_face_id TYPE /aws1/rekfaceid.

    CLASS-DATA ao_rek TYPE REF TO /aws1/if_rek.
    CLASS-DATA ao_s3 TYPE REF TO /aws1/if_s3.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_rek_actions TYPE REF TO /awsex/cl_rek_actions.

    METHODS:
      create_collection FOR TESTING RAISING /aws1/cx_rt_generic,
      describe_collection FOR TESTING RAISING /aws1/cx_rt_generic,
      list_collections FOR TESTING RAISING /aws1/cx_rt_generic,
      detect_faces FOR TESTING RAISING /aws1/cx_rt_generic,
      compare_faces FOR TESTING RAISING /aws1/cx_rt_generic,
      detect_labels FOR TESTING RAISING /aws1/cx_rt_generic,
      detect_moderation_labels FOR TESTING RAISING /aws1/cx_rt_generic,
      detect_text FOR TESTING RAISING /aws1/cx_rt_generic,
      recognize_celebrities FOR TESTING RAISING /aws1/cx_rt_generic,
      index_faces FOR TESTING RAISING /aws1/cx_rt_generic,
      list_faces FOR TESTING RAISING /aws1/cx_rt_generic,
      search_faces_by_image FOR TESTING RAISING /aws1/cx_rt_generic,
      search_faces FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_faces FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_collection FOR TESTING RAISING /aws1/cx_rt_generic.

    CLASS-METHODS:
      class_setup RAISING /aws1/cx_rt_generic,
      class_teardown RAISING /aws1/cx_rt_generic.

    METHODS:
      upload_test_image
        IMPORTING
          iv_key TYPE /aws1/s3_objectkey
        RAISING
          /aws1/cx_rt_generic,
      delete_test_image
        IMPORTING
          iv_key TYPE /aws1/s3_objectkey
        RAISING
          /aws1/cx_rt_generic,
      wait_for_collection_ready
        IMPORTING
          iv_collection_id TYPE /aws1/rekcollectionid
        RAISING
          /aws1/cx_rt_generic,
      create_test_collection
        IMPORTING
          iv_collection_id TYPE /aws1/rekcollectionid
        RAISING
          /aws1/cx_rt_generic,
      delete_test_collection
        IMPORTING
          iv_collection_id TYPE /aws1/rekcollectionid
        RAISING
          /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_awsex_cl_rek_actions IMPLEMENTATION.

  METHOD class_setup.
    DATA lv_uuid TYPE string.
    DATA lv_uuid2 TYPE string.
    DATA lv_uuid3 TYPE string.
    DATA lt_tagset TYPE /aws1/cl_s3_tag=>tt_tagset.
    DATA lo_tagging TYPE REF TO /aws1/cl_s3_tagging.
    DATA lt_tags TYPE /aws1/cl_rektagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_rektagmap_w=>ts_tagmap_maprow.

    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_rek = /aws1/cl_rek_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_rek_actions = NEW /awsex/cl_rek_actions( ).

    " Generate unique collection IDs with random string
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid TO LOWER CASE.
    av_collection_id = |sap-abap-rek-{ lv_uuid }|.

    lv_uuid2 = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid2 TO LOWER CASE.
    av_collection_id_for_delete = |sap-abap-rek-del-{ lv_uuid2 }|.

    " Create test bucket for images with unique name
    lv_uuid3 = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid3 TO LOWER CASE.
    av_test_bucket = |sap-abap-rek-{ lv_uuid3 }|.

    " Create S3 bucket with convert_test tag
    /awsex/cl_utils=>create_bucket(
      iv_bucket = av_test_bucket
      io_s3 = ao_s3
      io_session = ao_session ).

    " Tag the bucket
    TRY.
        APPEND NEW /aws1/cl_s3_tag( iv_key = 'convert_test' iv_value = 'true' ) TO lt_tagset.
        lo_tagging = NEW /aws1/cl_s3_tagging( it_tagset = lt_tagset ).
        ao_s3->putbuckettagging(
          iv_bucket = av_test_bucket
          io_tagging = lo_tagging ).
      CATCH /aws1/cx_rt_generic.
        " Tagging failed but continue
    ENDTRY.

    " Create the main collection for testing with convert_test tag
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_rektagmap_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    TRY.
        ao_rek->createcollection(
          iv_collectionid = av_collection_id
          it_tags = lt_tags ).
      CATCH /aws1/cx_rekresrcalrdyexistsex.
        " Collection already exists, that's OK
    ENDTRY.

    " Wait for collection to be ready
    DATA lv_max_attempts TYPE i VALUE 30.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_wait_time TYPE i VALUE 2.
    DATA lo_desc_result TYPE REF TO /aws1/cl_rekdescrcollresponse.

    WHILE lv_attempt < lv_max_attempts.
      TRY.
          lo_desc_result = ao_rek->describecollection(
            iv_collectionid = av_collection_id ).
          EXIT.
        CATCH /aws1/cx_rekresourcenotfoundex.
          WAIT UP TO lv_wait_time SECONDS.
          lv_attempt = lv_attempt + 1.
      ENDTRY.
    ENDWHILE.

    " Create collection for delete test
    CLEAR lt_tags.
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_rektagmap_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    TRY.
        ao_rek->createcollection(
          iv_collectionid = av_collection_id_for_delete
          it_tags = lt_tags ).
      CATCH /aws1/cx_rekresrcalrdyexistsex.
        " Collection already exists, that's OK
    ENDTRY.

    " Wait for second collection to be ready
    lv_attempt = 0.
    WHILE lv_attempt < lv_max_attempts.
      TRY.
          lo_desc_result = ao_rek->describecollection(
            iv_collectionid = av_collection_id_for_delete ).
          EXIT.
        CATCH /aws1/cx_rekresourcenotfoundex.
          WAIT UP TO lv_wait_time SECONDS.
          lv_attempt = lv_attempt + 1.
      ENDTRY.
    ENDWHILE.

    " Upload test images to S3 for use in tests
    " Create a minimal valid JPEG file (1x1 pixel black image) - split into parts
    DATA lv_jpeg_part1 TYPE xstring.
    DATA lv_jpeg_part2 TYPE xstring.
    DATA lv_jpeg_data TYPE xstring.
    
    lv_jpeg_part1 = 'FFD8FFE000104A46494600010100000100010000FFDB004300080606070605080707070909080A0C140D0C0B0B0C1912130F141D1A1F1E1D1A1C1C20242E2720222C231C1C28'.
    lv_jpeg_part2 = '37292C30313434341F27393D38323C2E333432FFDB0043010909090C0B0C180D0D1832211C213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232FFC00011080001000103012200021101031101FFC4001500010100000000000000000000000000000000FFC400140301010000000000000000000000000000000000FFD9'.
    CONCATENATE lv_jpeg_part1 lv_jpeg_part2 INTO lv_jpeg_data IN BYTE MODE.

    ao_s3->putobject(
      iv_bucket = av_test_bucket
      iv_key = av_test_image_key
      iv_body = lv_jpeg_data
      iv_tagging = 'convert_test=true' ).

    ao_s3->putobject(
      iv_bucket = av_test_bucket
      iv_key = av_test_image_key2
      iv_body = lv_jpeg_data
      iv_tagging = 'convert_test=true' ).

    " Wait for S3 eventual consistency
    WAIT UP TO 2 SECONDS.

  ENDMETHOD.

  METHOD class_teardown.
    " Clean up test images from S3
    TRY.
        ao_s3->deleteobject(
          iv_bucket = av_test_bucket
          iv_key = av_test_image_key ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    TRY.
        ao_s3->deleteobject(
          iv_bucket = av_test_bucket
          iv_key = av_test_image_key2 ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    " Clean up S3 bucket using util function
    TRY.
        /awsex/cl_utils=>cleanup_bucket(
          io_s3 = ao_s3
          iv_bucket = av_test_bucket ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    " Clean up collections
    TRY.
        ao_rek->deletecollection( iv_collectionid = av_collection_id ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.

    TRY.
        ao_rek->deletecollection( iv_collectionid = av_collection_id_for_delete ).
      CATCH /aws1/cx_rt_generic.
    ENDTRY.
  ENDMETHOD.

  METHOD create_test_collection.
    DATA lt_tags TYPE /aws1/cl_rektagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_rektagmap_w=>ts_tagmap_maprow.

    TRY.
        " Create collection with tags
        ls_tag-key = 'convert_test'.
        ls_tag-value = NEW /aws1/cl_rektagmap_w( iv_value = 'true' ).
        INSERT ls_tag INTO TABLE lt_tags.

        ao_rek->createcollection(
          iv_collectionid = iv_collection_id
          it_tags = lt_tags ).

      CATCH /aws1/cx_rekresrcalrdyexistsex.
        " Collection already exists, that's OK
    ENDTRY.
  ENDMETHOD.

  METHOD delete_test_collection.
    TRY.
        ao_rek->deletecollection( iv_collectionid = iv_collection_id ).
      CATCH /aws1/cx_rekresourcenotfoundex.
        " Collection already deleted, that's OK
    ENDTRY.
  ENDMETHOD.

  METHOD upload_test_image.
    " Create a minimal valid JPEG file (1x1 pixel black image) - split into parts
    DATA lv_jpeg_part1 TYPE xstring.
    DATA lv_jpeg_part2 TYPE xstring.
    DATA lv_jpeg_data TYPE xstring.

    lv_jpeg_part1 = 'FFD8FFE000104A46494600010100000100010000FFDB004300080606070605080707070909080A0C140D0C0B0B0C1912130F141D1A1F1E1D1A1C1C20242E2720222C231C1C28'.
    lv_jpeg_part2 = '37292C30313434341F27393D38323C2E333432FFDB0043010909090C0B0C180D0D1832211C213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232FFC00011080001000103012200021101031101FFC4001500010100000000000000000000000000000000FFC400140301010000000000000000000000000000000000FFD9'.
    CONCATENATE lv_jpeg_part1 lv_jpeg_part2 INTO lv_jpeg_data IN BYTE MODE.

    " Upload the test image to S3 with tags
    ao_s3->putobject(
      iv_bucket = av_test_bucket
      iv_key = iv_key
      iv_body = lv_jpeg_data
      iv_tagging = 'convert_test=true' ).

    " Wait for S3 eventual consistency
    WAIT UP TO 2 SECONDS.
  ENDMETHOD.

  METHOD delete_test_image.
    TRY.
        ao_s3->deleteobject(
          iv_bucket = av_test_bucket
          iv_key = iv_key ).
      CATCH /aws1/cx_s3_nosuchkey.
        " Image already deleted, that's OK
    ENDTRY.
  ENDMETHOD.

  METHOD wait_for_collection_ready.
    " Poll for collection readiness
    DATA lv_max_attempts TYPE i VALUE 30.
    DATA lv_attempt TYPE i VALUE 0.
    DATA lv_wait_time TYPE i VALUE 2.
    DATA lo_result TYPE REF TO /aws1/cl_rekdescrcollresponse.

    WHILE lv_attempt < lv_max_attempts.
      TRY.
          lo_result = ao_rek->describecollection(
            iv_collectionid = iv_collection_id ).
          " Collection found and ready
          RETURN.
        CATCH /aws1/cx_rekresourcenotfoundex.
          " Collection not yet ready, wait and retry
          WAIT UP TO lv_wait_time SECONDS.
          lv_attempt = lv_attempt + 1.
      ENDTRY.
    ENDWHILE.

    " Collection didn't become ready in time
    cl_abap_unit_assert=>fail(
      msg = |Collection { iv_collection_id } did not become ready within { lv_max_attempts * lv_wait_time } seconds| ).
  ENDMETHOD.

  METHOD create_collection.
    DATA lo_result TYPE REF TO /aws1/cl_rekcreatecollresponse.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lv_uuid TYPE string.
    DATA lv_test_collection_id TYPE /aws1/rekcollectionid.
    DATA lv_status_code TYPE i.
    DATA lt_tags TYPE /aws1/cl_rektagmap_w=>tt_tagmap.
    DATA ls_tag TYPE /aws1/cl_rektagmap_w=>ts_tagmap_maprow.

    " Create a new collection specifically for this test
    lv_uuid = /awsex/cl_utils=>get_random_string( ).
    TRANSLATE lv_uuid TO LOWER CASE.
    lv_test_collection_id = |sap-abap-rek-crt-{ lv_uuid }|.

    " Create collection with tags
    ls_tag-key = 'convert_test'.
    ls_tag-value = NEW /aws1/cl_rektagmap_w( iv_value = 'true' ).
    INSERT ls_tag INTO TABLE lt_tags.

    TRY.
        ao_rek_actions->create_collection(
          EXPORTING
            iv_collection_id = lv_test_collection_id
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Create collection should return result' ).

        " Verify collection was created
        wait_for_collection_ready( iv_collection_id = lv_test_collection_id ).

        lv_status_code = lo_result->get_statuscode( ).
        cl_abap_unit_assert=>assert_equals(
          exp = 200
          act = lv_status_code
          msg = 'Create collection should return status code 200' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to create collection: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Clean up test collection
    delete_test_collection( iv_collection_id = lv_test_collection_id ).
  ENDMETHOD.

  METHOD describe_collection.
    DATA lo_result TYPE REF TO /aws1/cl_rekdescrcollresponse.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lv_collection_arn TYPE /aws1/string.

    " Use the pre-created collection from class_setup
    TRY.
        ao_rek_actions->describe_collection(
          EXPORTING
            iv_collection_id = av_collection_id
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Describe collection should return result' ).

        lv_collection_arn = lo_result->get_collectionarn( ).
        cl_abap_unit_assert=>assert_not_initial(
          act = lv_collection_arn
          msg = 'Collection ARN should not be empty' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to describe collection: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD list_collections.
    DATA lo_result TYPE REF TO /aws1/cl_reklistcollsresponse.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lt_collections TYPE /aws1/cl_rekcollectionidlist_w=>tt_collectionidlist.
    DATA lv_collection_id TYPE /aws1/rekcollectionid.
    DATA lv_found TYPE abap_bool.

    TRY.
        ao_rek_actions->list_collections(
          EXPORTING
            iv_max_results = 100
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'List collections should return result' ).

        lt_collections = lo_result->get_collectionids( ).
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_collections
          msg = 'Collection list should not be empty' ).

        " Verify our test collection is in the list
        lv_found = abap_false.
        LOOP AT lt_collections INTO lv_collection_id.
          IF lv_collection_id = av_collection_id.
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        cl_abap_unit_assert=>assert_true(
          act = lv_found
          msg = |Collection { av_collection_id } should be in the list| ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to list collections: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD detect_faces.
    DATA lo_result TYPE REF TO /aws1/cl_rekdetectfacesrsp.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.

    " Use pre-uploaded test image from class_setup
    TRY.
        ao_rek_actions->detect_faces(
          EXPORTING
            iv_s3_bucket = av_test_bucket
            iv_s3_key = av_test_image_key
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Detect faces should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to detect faces: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD compare_faces.
    DATA lo_result TYPE REF TO /aws1/cl_rekcomparefacesrsp.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.

    " Use pre-uploaded test images from class_setup
    TRY.
        ao_rek_actions->compare_faces(
          EXPORTING
            iv_source_s3_bucket = av_test_bucket
            iv_source_s3_key = av_test_image_key
            iv_target_s3_bucket = av_test_bucket
            iv_target_s3_key = av_test_image_key2
            iv_similarity = '80'
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Compare faces should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to compare faces: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD detect_labels.
    DATA lo_result TYPE REF TO /aws1/cl_rekdetectlabelsrsp.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.

    " Use pre-uploaded test image from class_setup
    TRY.
        ao_rek_actions->detect_labels(
          EXPORTING
            iv_s3_bucket = av_test_bucket
            iv_s3_key = av_test_image_key
            iv_max_labels = 10
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Detect labels should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to detect labels: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD detect_moderation_labels.
    DATA lo_result TYPE REF TO /aws1/cl_rekdetectmderationl01.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.

    " Use pre-uploaded test image from class_setup
    TRY.
        ao_rek_actions->detect_moderation_labels(
          EXPORTING
            iv_s3_bucket = av_test_bucket
            iv_s3_key = av_test_image_key
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Detect moderation labels should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to detect moderation labels: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD detect_text.
    DATA lo_result TYPE REF TO /aws1/cl_rekdetecttextresponse.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.

    " Use pre-uploaded test image from class_setup
    TRY.
        ao_rek_actions->detect_text(
          EXPORTING
            iv_s3_bucket = av_test_bucket
            iv_s3_key = av_test_image_key
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Detect text should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to detect text: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD recognize_celebrities.
    DATA lo_result TYPE REF TO /aws1/cl_rekrecognizecelebri01.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.

    " Use pre-uploaded test image from class_setup
    TRY.
        ao_rek_actions->recognize_celebrities(
          EXPORTING
            iv_s3_bucket = av_test_bucket
            iv_s3_key = av_test_image_key
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Recognize celebrities should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to recognize celebrities: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD index_faces.
    DATA lo_result TYPE REF TO /aws1/cl_rekindexfacesresponse.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lt_face_records TYPE /aws1/cl_rekfacerecord=>tt_facerecordlist.
    DATA lo_face_record TYPE REF TO /aws1/cl_rekfacerecord.
    DATA lo_face TYPE REF TO /aws1/cl_rekface.

    " Use pre-created collection and pre-uploaded test image
    TRY.
        ao_rek_actions->index_faces(
          EXPORTING
            iv_collection_id = av_collection_id
            iv_s3_bucket = av_test_bucket
            iv_s3_key = av_test_image_key
            iv_external_id = 'test-face-1'
            iv_max_faces = 10
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Index faces should return result' ).

        " Store face ID for later tests
        lt_face_records = lo_result->get_facerecords( ).
        IF lt_face_records IS NOT INITIAL.
          READ TABLE lt_face_records INDEX 1 INTO lo_face_record.
          IF lo_face_record IS BOUND.
            lo_face = lo_face_record->get_face( ).
            IF lo_face IS BOUND.
              av_indexed_face_id = lo_face->get_faceid( ).
            ENDIF.
          ENDIF.
        ENDIF.

        " Wait for indexing to propagate
        WAIT UP TO 3 SECONDS.

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to index faces: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD list_faces.
    DATA lo_result TYPE REF TO /aws1/cl_reklistfacesresponse.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lo_index_result TYPE REF TO /aws1/cl_rekindexfacesresponse.
    DATA lo_s3object TYPE REF TO /aws1/cl_reks3object.
    DATA lo_image TYPE REF TO /aws1/cl_rekimage.
    DATA lt_faces TYPE /aws1/cl_rekface=>tt_facelist.

    " Ensure we have indexed faces first
    IF av_indexed_face_id IS INITIAL.
      " Index a face if not already done
        TRY.
            lo_s3object = NEW /aws1/cl_reks3object(
              iv_bucket = av_test_bucket
              iv_name = av_test_image_key ).
            lo_image = NEW /aws1/cl_rekimage( io_s3object = lo_s3object ).
            lo_index_result = ao_rek->indexfaces(
              iv_collectionid = av_collection_id
              io_image = lo_image
              iv_externalimageid = 'test-face-list'
              iv_maxfaces = 1 ).
            WAIT UP TO 3 SECONDS.
          CATCH /aws1/cx_rt_generic.
            " Face might already be indexed
        ENDTRY.
    ENDIF.

    TRY.
        ao_rek_actions->list_faces(
          EXPORTING
            iv_collection_id = av_collection_id
            iv_max_results = 100
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'List faces should return result' ).

        lt_faces = lo_result->get_faces( ).
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_faces
          msg = 'Face list should not be empty after indexing' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to list faces: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD search_faces_by_image.
    DATA lo_result TYPE REF TO /aws1/cl_reksrchfacesbyimage01.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lo_index_result TYPE REF TO /aws1/cl_rekindexfacesresponse.
    DATA lo_s3object TYPE REF TO /aws1/cl_reks3object.
    DATA lo_image TYPE REF TO /aws1/cl_rekimage.

    " Ensure we have indexed faces first
    IF av_indexed_face_id IS INITIAL.
      TRY.
          lo_s3object = NEW /aws1/cl_reks3object(
            iv_bucket = av_test_bucket
            iv_name = av_test_image_key ).
          lo_image = NEW /aws1/cl_rekimage( io_s3object = lo_s3object ).
          lo_index_result = ao_rek->indexfaces(
            iv_collectionid = av_collection_id
            io_image = lo_image
            iv_externalimageid = 'test-face-search'
            iv_maxfaces = 1 ).
          WAIT UP TO 3 SECONDS.
        CATCH /aws1/cx_rt_generic.
          " Face might already be indexed
      ENDTRY.
    ENDIF.

    " Use pre-uploaded test image from class_setup
    TRY.
        ao_rek_actions->search_faces_by_image(
          EXPORTING
            iv_collection_id = av_collection_id
            iv_s3_bucket = av_test_bucket
            iv_s3_key = av_test_image_key
            iv_threshold = '80'
            iv_max_faces = 10
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Search faces by image should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to search faces by image: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD search_faces.
    DATA lo_result TYPE REF TO /aws1/cl_reksearchfacesrsp.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lo_index_result TYPE REF TO /aws1/cl_rekindexfacesresponse.
    DATA lo_s3object TYPE REF TO /aws1/cl_reks3object.
    DATA lo_image TYPE REF TO /aws1/cl_rekimage.
    DATA lt_face_records TYPE /aws1/cl_rekfacerecord=>tt_facerecordlist.
    DATA lo_face_record TYPE REF TO /aws1/cl_rekfacerecord.
    DATA lo_face TYPE REF TO /aws1/cl_rekface.

    " Ensure we have an indexed face
    IF av_indexed_face_id IS INITIAL.
      TRY.
          lo_s3object = NEW /aws1/cl_reks3object(
            iv_bucket = av_test_bucket
            iv_name = av_test_image_key ).
          lo_image = NEW /aws1/cl_rekimage( io_s3object = lo_s3object ).
          lo_index_result = ao_rek->indexfaces(
            iv_collectionid = av_collection_id
            io_image = lo_image
            iv_externalimageid = 'test-face-search-by-id'
            iv_maxfaces = 1 ).

          lt_face_records = lo_index_result->get_facerecords( ).
          IF lt_face_records IS NOT INITIAL.
            READ TABLE lt_face_records INDEX 1 INTO lo_face_record.
            IF lo_face_record IS BOUND.
              lo_face = lo_face_record->get_face( ).
              IF lo_face IS BOUND.
                av_indexed_face_id = lo_face->get_faceid( ).
              ENDIF.
            ENDIF.
          ENDIF.
          WAIT UP TO 3 SECONDS.
        CATCH /aws1/cx_rt_generic INTO lo_error.
          cl_abap_unit_assert=>fail( msg = |Failed to create face for search test: { lo_error->get_text( ) }| ).
      ENDTRY.
    ENDIF.

    " Verify we have a face ID
    IF av_indexed_face_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Could not obtain face ID for search_faces test' ).
    ENDIF.

    TRY.
        ao_rek_actions->search_faces(
          EXPORTING
            iv_collection_id = av_collection_id
            iv_face_id = av_indexed_face_id
            iv_threshold = '80'
            iv_max_faces = 10
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Search faces should return result' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to search faces: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_faces.
    DATA lo_result TYPE REF TO /aws1/cl_rekdeletefacesrsp.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.
    DATA lv_face_id_to_delete TYPE /aws1/rekfaceid.
    DATA lo_index_result TYPE REF TO /aws1/cl_rekindexfacesresponse.
    DATA lo_s3object TYPE REF TO /aws1/cl_reks3object.
    DATA lo_image TYPE REF TO /aws1/cl_rekimage.
    DATA lt_face_records TYPE /aws1/cl_rekfacerecord=>tt_facerecordlist.
    DATA lo_face_record TYPE REF TO /aws1/cl_rekfacerecord.
    DATA lo_face TYPE REF TO /aws1/cl_rekface.
    DATA lt_face_ids TYPE /aws1/cl_rekfaceidlist_w=>tt_faceidlist.
    DATA lt_deleted_faces TYPE /aws1/cl_rekfaceidlist_w=>tt_faceidlist.

    " Create a dedicated face for deletion test
    TRY.
        lo_s3object = NEW /aws1/cl_reks3object(
          iv_bucket = av_test_bucket
          iv_name = av_test_image_key2 ).
        lo_image = NEW /aws1/cl_rekimage( io_s3object = lo_s3object ).
        lo_index_result = ao_rek->indexfaces(
          iv_collectionid = av_collection_id
          io_image = lo_image
          iv_externalimageid = 'test-face-for-deletion'
          iv_maxfaces = 1 ).

        lt_face_records = lo_index_result->get_facerecords( ).
        IF lt_face_records IS NOT INITIAL.
          READ TABLE lt_face_records INDEX 1 INTO lo_face_record.
          IF lo_face_record IS BOUND.
            lo_face = lo_face_record->get_face( ).
            IF lo_face IS BOUND.
              lv_face_id_to_delete = lo_face->get_faceid( ).
            ENDIF.
          ENDIF.
        ENDIF.
        WAIT UP TO 3 SECONDS.
      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to create face for deletion test: { lo_error->get_text( ) }| ).
    ENDTRY.

    " Verify we have a face ID to delete
    IF lv_face_id_to_delete IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Could not obtain face ID for delete_faces test' ).
    ENDIF.

    " Create face ID list
    APPEND lv_face_id_to_delete TO lt_face_ids.

    TRY.
        ao_rek_actions->delete_faces(
          EXPORTING
            iv_collection_id = av_collection_id
            it_face_ids = lt_face_ids
          IMPORTING
            oo_result = lo_result ).

        cl_abap_unit_assert=>assert_bound(
          act = lo_result
          msg = 'Delete faces should return result' ).

        lt_deleted_faces = lo_result->get_deletedfaces( ).
        cl_abap_unit_assert=>assert_not_initial(
          act = lt_deleted_faces
          msg = 'Deleted faces list should not be empty' ).

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to delete faces: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD delete_collection.
    DATA lo_error TYPE REF TO /aws1/cx_rt_generic.

    " Use the pre-created collection designated for deletion
    TRY.
        ao_rek_actions->delete_collection(
          iv_collection_id = av_collection_id_for_delete ).

        " Verify collection was deleted by trying to describe it
        TRY.
            ao_rek->describecollection( iv_collectionid = av_collection_id_for_delete ).
            cl_abap_unit_assert=>fail( msg = 'Collection should have been deleted' ).
          CATCH /aws1/cx_rekresourcenotfoundex.
            " Expected - collection was deleted successfully
        ENDTRY.

      CATCH /aws1/cx_rt_generic INTO lo_error.
        cl_abap_unit_assert=>fail( msg = |Failed to delete collection: { lo_error->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
