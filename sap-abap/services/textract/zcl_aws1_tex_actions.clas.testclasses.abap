" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_tex_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_tex TYPE REF TO /aws1/if_tex.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_tex_actions TYPE REF TO zcl_aws1_tex_actions.

    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.
    METHODS: analyze_document FOR TESTING.
    METHODS: detect_document_text FOR TESTING.
    METHODS: start_document_analysis FOR TESTING.
    METHODS: start_document_text_detection FOR TESTING.
    METHODS: get_document_analysis FOR TESTING.

ENDCLASS.       "ltc_Zcl_Aws1_Tex_Actions


CLASS ltc_zcl_aws1_tex_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_tex = /aws1/cl_tex_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_tex_actions = NEW zcl_aws1_tex_actions( ).
  ENDMETHOD.

  METHOD analyze_document.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_output TYPE REF TO /aws1/cl_texanalyzedocresponse.
    DATA lt_blocks TYPE /aws1/cl_texblock=>tt_blocklist.
    DATA lo_block TYPE REF TO  /aws1/cl_texblock.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Analyze document.
    ao_tex_actions->analyze_document(
      EXPORTING
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name
      IMPORTING
        oo_result           = lo_output
      ).

    "Validation check.
    lv_found = abap_false.
    lt_blocks = lo_output->get_blocks( ).

    LOOP AT lt_blocks INTO lo_block.
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed|
    ).

  ENDMETHOD.


  METHOD detect_document_text.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_output TYPE REF TO /aws1/cl_texdetectdoctextrsp.
    DATA lt_blocks TYPE /aws1/cl_texblock=>tt_blocklist.
    DATA lo_block TYPE REF TO  /aws1/cl_texblock.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Testing.
    ao_tex_actions->detect_document_text(
      EXPORTING
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name
      IMPORTING
        oo_result           = lo_output
      ).

    "Validation check.
    lv_found = abap_false.
    lt_blocks = lo_output->get_blocks( ).

    LOOP AT lt_blocks INTO lo_block.
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed|
    ).

  ENDMETHOD.

  METHOD start_document_analysis.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_output TYPE REF TO /aws1/cl_texstartdocalyrsp.
    DATA lv_jobid TYPE /aws1/texjobid.
    DATA lo_document_analysis_output TYPE REF TO /aws1/cl_texgetdocalyresponse.
    DATA lo_jobstatus TYPE /aws1/texjobstatus.
    DATA lt_blocks TYPE /aws1/cl_texblock=>tt_blocklist.
    DATA lo_block TYPE REF TO  /aws1/cl_texblock.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Testing.
    ao_tex_actions->start_document_analysis(
      EXPORTING
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name
      IMPORTING
        oo_result           = lo_output
      ).

    "Wait for job to complete.
    lv_jobid = lo_output->get_jobid( ).

    lo_document_analysis_output = ao_tex->getdocumentanalysis( iv_jobid = lv_jobid ).
    WHILE lo_document_analysis_output->get_jobstatus( ) <> 'SUCCEEDED'.
      IF sy-index = 10.
        EXIT.               "Maximum 300 seconds.
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_document_analysis_output = ao_tex->getdocumentanalysis( iv_jobid = lv_jobid ).
    ENDWHILE.

    "Validation check.
    lv_found = abap_false.
    lt_blocks = lo_document_analysis_output->get_blocks( ).
    LOOP AT lt_blocks INTO lo_block.
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed|
    ).

  ENDMETHOD.

  METHOD start_document_text_detection.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_output TYPE REF TO /aws1/cl_texstartdoctextdetrsp.
    DATA lv_jobid TYPE /aws1/texjobid.
    DATA lo_text_detection_output TYPE REF TO /aws1/cl_texgetdoctxtdetectrsp.
    DATA lo_jobstatus TYPE /aws1/texjobstatus.
    DATA lt_blocks TYPE /aws1/cl_texblock=>tt_blocklist.
    DATA lo_block TYPE REF TO  /aws1/cl_texblock.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    "Testing.
    ao_tex_actions->start_document_text_detection(
      EXPORTING
        iv_s3object          = cv_key_name
        iv_s3bucket          = cv_bucket_name
      IMPORTING
        oo_result           = lo_output
      ).

    lv_jobid = lo_output->get_jobid( ).

    "Wait for job to complete.
    lo_text_detection_output = ao_tex->getdocumenttextdetection( iv_jobid = lv_jobid ).
    WHILE lo_text_detection_output->get_jobstatus( ) <> 'SUCCEEDED'.
      IF sy-index = 10.
        EXIT.               "Maximum 300 seconds.
      ENDIF.
      WAIT UP TO 30 SECONDS.
      lo_text_detection_output = ao_tex->getdocumenttextdetection( iv_jobid = lv_jobid ).
    ENDWHILE.

    "Validation check.
    lv_found = abap_false.
    lt_blocks = lo_text_detection_output->get_blocks( ).
    LOOP AT lt_blocks INTO lo_block.
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed|
    ).

  ENDMETHOD.

  METHOD get_document_analysis.

    DATA lv_found TYPE abap_bool VALUE abap_false.
    DATA lo_output TYPE REF TO /aws1/cl_texstartdocalyrsp.
    DATA lv_jobid TYPE /aws1/texjobid.
    DATA lo_document_analysis_output TYPE REF TO /aws1/cl_texgetdocalyresponse.
    DATA lo_jobstatus TYPE /aws1/texjobstatus.
    DATA lt_blocks TYPE /aws1/cl_texblock=>tt_blocklist.
    DATA lo_block TYPE REF TO  /aws1/cl_texblock.
    DATA lo_documentlocation TYPE REF TO /aws1/cl_texdocumentlocation.
    DATA lo_s3object TYPE REF TO /aws1/cl_texs3object.
    DATA lo_featuretypes TYPE REF TO /aws1/cl_texfeaturetypes_w.
    DATA lt_featuretypes TYPE /aws1/cl_texfeaturetypes_w=>tt_featuretypes.

    "Using an image from the Public Amazon Berkeley Objects Dataset.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'amazon-berkeley-objects'.
    CONSTANTS cv_key_name TYPE /aws1/s3_bucketname VALUE 'images/small/e0/e0feb1eb.jpg'.

    CREATE OBJECT lo_featuretypes
      EXPORTING
        iv_value = 'FORMS'.
    INSERT lo_featuretypes INTO TABLE lt_featuretypes.

    CREATE OBJECT lo_featuretypes
      EXPORTING
        iv_value = 'TABLES'.
    INSERT lo_featuretypes INTO TABLE lt_featuretypes.

    "Create a ABAP object for the Amazon Simple Storage Service (Amazon S3) object.
    CREATE OBJECT lo_s3object
      EXPORTING
        iv_bucket = cv_bucket_name
        iv_name   = cv_key_name.

    "Create a ABAP object for the document.
    CREATE OBJECT lo_documentlocation
      EXPORTING
        io_s3object = lo_s3object.

    "Start document analysis.
    lo_output = ao_tex->startdocumentanalysis(
      EXPORTING
        io_documentlocation     = lo_documentlocation
        it_featuretypes         = lt_featuretypes
      ).

    "Get job ID.
    lv_jobid = lo_output->get_jobid( ).

    "Testing.
    ao_tex_actions->get_document_analysis( EXPORTING iv_jobid = lv_jobid IMPORTING oo_result = lo_document_analysis_output ).
    WHILE lo_document_analysis_output->get_jobstatus( ) <> 'SUCCEEDED'.
      IF sy-index = 10.
        EXIT.               "Maximum 300 seconds.
      ENDIF.
      WAIT UP TO 30 SECONDS.
      ao_tex_actions->get_document_analysis( EXPORTING iv_jobid = lv_jobid IMPORTING oo_result = lo_document_analysis_output ).
    ENDWHILE.

    "Validation check.
    lv_found = abap_false.
    lt_blocks = lo_document_analysis_output->get_blocks( ).
    LOOP AT lt_blocks INTO lo_block.
      IF lo_block->get_text( ) = 'INGREDIENTS: POWDERED SUGAR* (CANE SUGAR,'.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Analyze document failed|
    ).

  ENDMETHOD.

ENDCLASS.
