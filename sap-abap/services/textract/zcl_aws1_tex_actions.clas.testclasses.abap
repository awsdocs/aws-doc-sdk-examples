" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_zcl_aws1_tex_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_tex TYPE REF TO /aws1/if_tex.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_tex_actions TYPE REF TO zcl_aws1_tex_actions.

    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.
    METHODS analyze_document FOR TESTING.
    METHODS detect_document_text FOR TESTING.
    METHODS start_document_analysis FOR TESTING.
    METHODS start_document_text_detection FOR TESTING.
    METHODS get_document_analysis FOR TESTING.

ENDCLASS.       "ltc_Zcl_Aws1_Tex_Actions


CLASS ltc_zcl_aws1_tex_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).

    ao_tex = /aws1/cl_tex_factory=>create(
      io_session = ao_session
      iv_region = 'us-east-1' ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_tex_actions = NEW zcl_aws1_tex_actions( ).
  ENDMETHOD.

  METHOD analyze_document.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Analyze document.
    DATA(lo_output) = ao_tex_actions->analyze_document(
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name ).

    "Validation check.
    DATA(lv_found) = abap_false.
    DATA(lt_blocks) = lo_output->get_blocks( ).

    LOOP AT lt_blocks INTO DATA(lo_block).
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed| ).

  ENDMETHOD.


  METHOD detect_document_text.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Testing.
    DATA(lo_output) = ao_tex_actions->detect_document_text(
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name ).

    "Validation check.
    DATA(lv_found) = abap_false.
    DATA(lt_blocks) = lo_output->get_blocks( ).

    LOOP AT lt_blocks INTO DATA(lo_block).
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed| ).

  ENDMETHOD.

  METHOD start_document_analysis.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Testing.
    DATA(lo_output) = ao_tex_actions->start_document_analysis(
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name ).

    "Wait for job to complete.
    DATA(lv_jobid) = lo_output->get_jobid( ).

    DATA(lo_document_analysis_output) = ao_tex->getdocumentanalysis( iv_jobid = lv_jobid ).
    WHILE lo_document_analysis_output->get_jobstatus( ) <> 'SUCCEEDED'.
      IF sy-index = 10.
        EXIT.               "Maximum 300 seconds.
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_document_analysis_output = ao_tex->getdocumentanalysis( iv_jobid = lv_jobid ).
    ENDWHILE.

    "Validation check.
    DATA(lv_found) = abap_false.
    DATA(lt_blocks) = lo_document_analysis_output->get_blocks( ).
    LOOP AT lt_blocks INTO DATA(lo_block).
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed| ).

  ENDMETHOD.

  METHOD start_document_text_detection.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Testing.
    DATA(lo_output) = ao_tex_actions->start_document_text_detection(
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name ).

    DATA(lv_jobid) = lo_output->get_jobid( ).

    "Wait for job to complete.
    DATA(lo_text_detection_output) = ao_tex->getdocumenttextdetection( iv_jobid = lv_jobid ).
    WHILE lo_text_detection_output->get_jobstatus( ) <> 'SUCCEEDED'.
      IF sy-index = 10.
        EXIT.               "Maximum 300 seconds.
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_text_detection_output = ao_tex->getdocumenttextdetection( iv_jobid = lv_jobid ).
    ENDWHILE.

    "Validation check.
    DATA(lv_found) = abap_false.
    DATA(lt_blocks) = lo_text_detection_output->get_blocks( ).
    LOOP AT lt_blocks INTO DATA(lo_block).
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed| ).

  ENDMETHOD.

  METHOD get_document_analysis.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    DATA(lt_featuretypes) = VALUE /aws1/cl_texfeaturetypes_w=>tt_featuretypes(
      ( NEW /aws1/cl_texfeaturetypes_w( iv_value = 'FORMS' ) )
      ( NEW /aws1/cl_texfeaturetypes_w( iv_value = 'TABLES' ) ) ).

    "Create a ABAP object for the Amazon Simple Storage Service (Amazon S3) object.
    DATA(lo_s3object) = NEW /aws1/cl_texs3object( iv_bucket = cv_bucket_name
      iv_name   = cv_key_name ).

    "Create a ABAP object for the document.
    DATA(lo_documentlocation) = NEW /aws1/cl_texdocumentlocation( io_s3object = lo_s3object ).

    "Start document analysis.
    DATA(lo_output) = ao_tex->startdocumentanalysis(
        io_documentlocation     = lo_documentlocation
        it_featuretypes         = lt_featuretypes ).

    "Get job ID.
    DATA(lv_jobid) = lo_output->get_jobid( ).

    "Testing.
    DATA(lo_document_analysis_output) = ao_tex_actions->get_document_analysis( lv_jobid ).
    WHILE lo_document_analysis_output->get_jobstatus( ) <> 'SUCCEEDED'.
      IF sy-index = 10.
        EXIT.               "Maximum 300 seconds.
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_document_analysis_output = ao_tex_actions->get_document_analysis( lv_jobid ).
    ENDWHILE.

    "Validation check.
    DATA(lv_found) = abap_false.
    DATA(lt_blocks) = lo_document_analysis_output->get_blocks( ).
    LOOP AT lt_blocks INTO DATA(lo_block).
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed| ).

  ENDMETHOD.

ENDCLASS.
