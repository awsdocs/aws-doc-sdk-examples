" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_xl8_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.
    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-xl8-'.
    CONSTANTS cv_input_key TYPE /aws1/s3_objectkey VALUE 'translate/input/input.txt'.
    CONSTANTS cv_output_folder TYPE /aws1/s3_objectkey VALUE 'translate/output/'.
    CONSTANTS cv_input_folder TYPE /aws1/s3_objectkey VALUE 'translate/input/'.
    CONSTANTS cv_input_data_contenttype TYPE /aws1/xl8contenttype VALUE 'text/plain'.
    "   CONSTANTS cv_output_data_contenttype TYPE /aws1/xl8contenttype VALUE 'text/plain'.
    CONSTANTS cv_sourcelanguagecode TYPE /aws1/xl8languagecodestring VALUE 'fr'.
    CONSTANTS cv_targetlanguagecode TYPE /aws1/xl8languagecodestring VALUE 'en'.

    DATA ao_xl8 TYPE REF TO /aws1/if_xl8.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_xl8_actions TYPE REF TO zcl_aws1_xl8_actions.
    DATA av_file_content TYPE /aws1/s3_streamingblob.
    DATA av_lrole TYPE /aws1/xl8iamrolearn.

    METHODS setup RAISING /aws1/cx_rt_generic.
    METHODS translate_text FOR TESTING.
    METHODS start_text_translation_job FOR TESTING.
    METHODS stop_text_translation_job FOR TESTING.
    METHODS describe_text_translation_job FOR TESTING.
    METHODS list_text_translation_job FOR TESTING.
    METHODS cleanup_s3
      IMPORTING
                iv_bucket_name TYPE /aws1/s3_bucketname
      RAISING   /aws1/cx_rt_generic.
    METHODS job_waiter
      IMPORTING iv_jobid     TYPE /aws1/xl8jobid
                iv_jobstatus TYPE /aws1/xl8jobstatus
      RAISING   /aws1/cx_rt_generic.

ENDCLASS.       "ltc_Zcl_Aws1_Xl8_Actions

CLASS ltc_zcl_aws1_xl8_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_xl8 = /aws1/cl_xl8_factory=>create( ao_session ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).
    ao_xl8_actions = NEW zcl_aws1_xl8_actions( ).

    "Translate data
    av_file_content = /aws1/cl_rt_util=>string_to_xstring(
      |Que vous cherchiez à replateformer pour réduire les coûts,| &&
      |à migrer vers SAP S/4HANA ou à adopter l’offre RISE avec SAP, | &&
      |AWS propose des approches éprouvées, | &&
      |soutenues par une expérience inégalée dans la prise en charge des clients SAP dans le cloud. | &&
      |Obtenez plus de flexibilité et de valeur à partir de vos investissements SAP grâce à l'infrastructure cloud la plus sécurisée, | &&
      |fiable et évolutive au monde, aux plus de 200 services AWS qui vous permettent d'innover, | &&
      |et aux outils d'automatisation de SAP spécialement conçus, | &&
      |afin de réduire les risques et de simplifier les opérations. |
    ).

    "Define role arn
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

  ENDMETHOD.

  METHOD cleanup_s3.

    DATA(lo_list) = ao_s3->listobjectsv2( iv_bucket = iv_bucket_name ).
    LOOP AT lo_list->get_contents( ) INTO DATA(lo_object).
      ao_s3->deleteobject(
          iv_bucket = iv_bucket_name
          iv_key = lo_object->get_key( )
      ).
    ENDLOOP.

    ao_s3->deletebucket( iv_bucket = iv_bucket_name ).

  ENDMETHOD.

  METHOD job_waiter.

    DATA lo_des_translation_result TYPE REF TO /aws1/cl_xl8dsctextxlatjobrsp.

    lo_des_translation_result = ao_xl8->describetexttranslationjob( iv_jobid = iv_jobid ).
    WHILE lo_des_translation_result->get_textxlationjobproperties( )->get_jobstatus( ) <> iv_jobstatus.
      IF sy-index = 60.
        EXIT.               "maximum 900 seconds
      ENDIF.
      WAIT UP TO 15 SECONDS.
      lo_des_translation_result = ao_xl8->describetexttranslationjob( iv_jobid = iv_jobid ).
    ENDWHILE.
  ENDMETHOD.

  METHOD translate_text.

    DATA lo_output TYPE REF TO /aws1/cl_xl8translatetextrsp.
    DATA lv_translatedtext TYPE /aws1/xl8string.
    DATA lv_found TYPE abap_bool VALUE abap_false.

    CONSTANTS cv_text TYPE  /aws1/xl8boundedlengthstring VALUE 'AWS accélère la croissance de la France'.

    "Translate text
    ao_xl8_actions->translate_text(
      EXPORTING
        iv_text               = cv_text
        iv_sourcelanguagecode = cv_sourcelanguagecode
        iv_targetlanguagecode = cv_targetlanguagecode
      IMPORTING
        oo_result           = lo_output
      ).

    "Validation check
    lv_found = abap_false.
    IF lo_output->get_translatedtext( ) = 'AWS accelerates growth in France'.
      lv_found = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
      act = lv_found
      msg = |Translation failed|
    ).

    "Nothing to clean up

  ENDMETHOD.

  METHOD start_text_translation_job.

    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_translate_job_name TYPE /aws1/xl8jobname.
    DATA lv_input_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_folder TYPE /aws1/xl8s3uri.
    DATA lo_result TYPE REF TO /aws1/cl_xl8strttextxlatjobrsp.
    DATA lv_found TYPE abap_bool.
    DATA lv_jobid TYPE /aws1/xl8jobid.
    DATA lo_des_translation_result TYPE REF TO /aws1/cl_xl8dsctextxlatjobrsp.

    "Define job name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_translate_job_name = 'code-example-xl8-job-' && lv_uuid_16.
    TRANSLATE lv_translate_job_name TO LOWER CASE.

    "Create training data in S3
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_input_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_input_folder.
    lv_output_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_output_folder.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_input_key
      iv_body = av_file_content
    ).

    "Testing
    ao_xl8_actions->start_text_translation_job(
      EXPORTING
        iv_jobname                         = lv_translate_job_name
        iv_dataaccessrolearn               = av_lrole
        iv_input_data_s3uri                = lv_input_data_s3uri
        iv_input_data_contenttype          = cv_input_data_contenttype
        iv_output_data_s3uri               = lv_output_data_s3uri
        iv_sourcelanguagecode              = cv_sourcelanguagecode
        iv_targetlanguagecode              = cv_targetlanguagecode
      IMPORTING
       oo_result                           = lo_result
    ).

    "Validation
    lv_found = abap_false.
    IF lo_result->has_jobstatus( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Translation job cannot be found|
    ).

    "Get the job ID
    lv_jobid = lo_result->get_jobid( ).

    "Wait for translate job to complete
    CALL METHOD job_waiter
      EXPORTING
        iv_jobid     = lv_jobid
        iv_jobstatus = 'COMPLETED'.

    "Cleanup
    CALL METHOD cleanup_s3 EXPORTING iv_bucket_name = lv_bucket_name.

  ENDMETHOD.

  METHOD stop_text_translation_job.

    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_translate_job_name TYPE /aws1/xl8jobname.
    DATA lv_input_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_folder TYPE /aws1/xl8s3uri.
    DATA lo_result TYPE REF TO /aws1/cl_xl8strttextxlatjobrsp.
    DATA lv_found TYPE abap_bool.
    DATA lv_jobid TYPE /aws1/xl8jobid.
    DATA lo_list_translation_result TYPE REF TO /aws1/cl_xl8lsttextxlatjobsrsp.
    DATA lo_inputdataconfig  TYPE REF TO /aws1/cl_xl8inputdataconfig.
    DATA lo_outputdataconfig TYPE REF TO /aws1/cl_xl8outputdataconfig.
    DATA lt_targetlanguagecodes TYPE /aws1/cl_xl8tgtlanguagecodes00=>tt_targetlanguagecodestrlist.
    DATA lo_targetlanguagecodes TYPE REF TO /aws1/cl_xl8tgtlanguagecodes00.
    DATA lo_des_translation_result TYPE REF TO /aws1/cl_xl8dsctextxlatjobrsp.

    "Define job name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_translate_job_name = 'code-example-xl8-job-' && lv_uuid_16.
    TRANSLATE lv_translate_job_name TO LOWER CASE.

    "Create training data in S3
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_input_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_input_folder.
    lv_output_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_output_folder.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_input_key
      iv_body = av_file_content
    ).

    "Create an ABAP object for the input data config
    CREATE OBJECT lo_inputdataconfig
      EXPORTING
        iv_s3uri       = lv_input_data_s3uri
        iv_contenttype = cv_input_data_contenttype.

    "Create an ABAP object for the output data config
    CREATE OBJECT lo_outputdataconfig
      EXPORTING
        iv_s3uri = lv_output_data_s3uri.

    "Create an interal table for target languages
    CREATE OBJECT lo_targetlanguagecodes
      EXPORTING
        iv_value = cv_targetlanguagecode.
    INSERT lo_targetlanguagecodes  INTO TABLE lt_targetlanguagecodes.

    "Create a translate job
    lo_result = ao_xl8->starttexttranslationjob(
      EXPORTING
        io_inputdataconfig = lo_inputdataconfig
        io_outputdataconfig = lo_outputdataconfig
        it_targetlanguagecodes = lt_targetlanguagecodes
        iv_dataaccessrolearn = av_lrole
        iv_jobname = lv_translate_job_name
        iv_sourcelanguagecode = cv_sourcelanguagecode
    ).

    "Get the job ID
    lv_jobid = lo_result->get_jobid( ).

    WAIT UP TO 20 SECONDS.

    "Testing list_text_translation_job
    ao_xl8_actions->stop_text_translation_job(
      EXPORTING iv_jobid = lv_jobid
    ).

    "Wait for translate job to stop
    CALL METHOD job_waiter
      EXPORTING
        iv_jobid     = lv_jobid
        iv_jobstatus = 'STOPPED'.

    "Validation
    lv_found = abap_false.
    lo_des_translation_result = ao_xl8->describetexttranslationjob( iv_jobid = lv_jobid ).
    IF lo_des_translation_result->get_textxlationjobproperties( )->get_jobstatus( ) = 'STOPPED'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Stop text translation job failed|
    ).

    "Cleanup
    CALL METHOD cleanup_s3 EXPORTING iv_bucket_name = lv_bucket_name.

  ENDMETHOD.

  METHOD describe_text_translation_job.

    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_translate_job_name TYPE /aws1/xl8jobname.
    DATA lv_input_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_folder TYPE /aws1/xl8s3uri.
    DATA lo_result TYPE REF TO /aws1/cl_xl8strttextxlatjobrsp.
    DATA lv_found TYPE abap_bool.
    DATA lv_jobid TYPE /aws1/xl8jobid.
    DATA lo_des_translation_result TYPE REF TO /aws1/cl_xl8dsctextxlatjobrsp.
    DATA lo_inputdataconfig  TYPE REF TO /aws1/cl_xl8inputdataconfig.
    DATA lo_outputdataconfig TYPE REF TO /aws1/cl_xl8outputdataconfig.
    DATA lt_targetlanguagecodes TYPE /aws1/cl_xl8tgtlanguagecodes00=>tt_targetlanguagecodestrlist.
    DATA lo_targetlanguagecodes TYPE REF TO /aws1/cl_xl8tgtlanguagecodes00.

    "Define job name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_translate_job_name = 'code-example-xl8-job-' && lv_uuid_16.
    TRANSLATE lv_translate_job_name TO LOWER CASE.

    "Create training data in S3
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_input_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_input_folder.
    lv_output_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_output_folder.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_input_key
      iv_body = av_file_content
    ).

    "Create an ABAP object for the input data config
    CREATE OBJECT lo_inputdataconfig
      EXPORTING
        iv_s3uri       = lv_input_data_s3uri
        iv_contenttype = cv_input_data_contenttype.

    "Create an ABAP object for the output data config
    CREATE OBJECT lo_outputdataconfig
      EXPORTING
        iv_s3uri = lv_output_data_s3uri.

    "Create an interal table for target languages
    CREATE OBJECT lo_targetlanguagecodes
      EXPORTING
        iv_value = cv_targetlanguagecode.
    INSERT lo_targetlanguagecodes  INTO TABLE lt_targetlanguagecodes.

    "Create a translate job
    lo_result = ao_xl8->starttexttranslationjob(
      EXPORTING
        io_inputdataconfig = lo_inputdataconfig
        io_outputdataconfig = lo_outputdataconfig
        it_targetlanguagecodes = lt_targetlanguagecodes
        iv_dataaccessrolearn = av_lrole
        iv_jobname = lv_translate_job_name
        iv_sourcelanguagecode = cv_sourcelanguagecode
    ).

    "Get the job ID
    lv_jobid = lo_result->get_jobid( ).

    "Testing
    ao_xl8_actions->describe_text_translation_job( EXPORTING iv_jobid = lv_jobid IMPORTING oo_result = lo_des_translation_result ).
    WHILE lo_des_translation_result->get_textxlationjobproperties( )->get_jobstatus( ) <> 'COMPLETED'.
      IF sy-index = 90.
        EXIT.               "maximum 1350 seconds
      ENDIF.
      WAIT UP TO 15 SECONDS.
      ao_xl8_actions->describe_text_translation_job( EXPORTING iv_jobid = lv_jobid IMPORTING oo_result = lo_des_translation_result ).
    ENDWHILE.

    "Validation
    lv_found = abap_false.
    IF lo_des_translation_result->get_textxlationjobproperties( )->get_jobstatus( ) = 'COMPLETED'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |Describe job failed|
    ).

    "Cleanup
    CALL METHOD cleanup_s3 EXPORTING iv_bucket_name = lv_bucket_name.

  ENDMETHOD.

  METHOD list_text_translation_job.

    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_translate_job_name TYPE /aws1/xl8jobname.
    DATA lv_input_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_folder TYPE /aws1/xl8s3uri.
    DATA lo_result TYPE REF TO /aws1/cl_xl8strttextxlatjobrsp.
    DATA lv_found TYPE abap_bool.
    DATA lv_jobid TYPE /aws1/xl8jobid.
    DATA lo_list_translation_result TYPE REF TO /aws1/cl_xl8lsttextxlatjobsrsp.
    DATA lo_inputdataconfig  TYPE REF TO /aws1/cl_xl8inputdataconfig.
    DATA lo_outputdataconfig TYPE REF TO /aws1/cl_xl8outputdataconfig.
    DATA lt_targetlanguagecodes TYPE /aws1/cl_xl8tgtlanguagecodes00=>tt_targetlanguagecodestrlist.
    DATA lo_targetlanguagecodes TYPE REF TO /aws1/cl_xl8tgtlanguagecodes00.
    DATA lo_des_translation_result TYPE REF TO /aws1/cl_xl8dsctextxlatjobrsp.

    "Define job name
    lv_uuid_16 = cl_system_uuid=>create_uuid_x16_static( ).
    lv_translate_job_name = 'code-example-xl8-job-' && lv_uuid_16.
    TRANSLATE lv_translate_job_name TO LOWER CASE.

    "Create training data in S3
    lv_bucket_name = cv_bucket_name && lv_uuid_16.
    TRANSLATE lv_bucket_name TO LOWER CASE.
    ao_s3->createbucket( iv_bucket = lv_bucket_name ).

    lv_input_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_input_folder.
    lv_output_data_s3uri = 's3://' && lv_bucket_name && '/' && cv_output_folder.

    ao_s3->putobject(
      iv_bucket = lv_bucket_name
      iv_key = cv_input_key
      iv_body = av_file_content
    ).

    "Create an ABAP object for the input data config
    CREATE OBJECT lo_inputdataconfig
      EXPORTING
        iv_s3uri       = lv_input_data_s3uri
        iv_contenttype = cv_input_data_contenttype.

    "Create an ABAP object for the output data config
    CREATE OBJECT lo_outputdataconfig
      EXPORTING
        iv_s3uri = lv_output_data_s3uri.

    "Create an interal table for target languages
    CREATE OBJECT lo_targetlanguagecodes
      EXPORTING
        iv_value = cv_targetlanguagecode.
    INSERT lo_targetlanguagecodes  INTO TABLE lt_targetlanguagecodes.

    "Create a translate job
    lo_result = ao_xl8->starttexttranslationjob(
      EXPORTING
        io_inputdataconfig = lo_inputdataconfig
        io_outputdataconfig = lo_outputdataconfig
        it_targetlanguagecodes = lt_targetlanguagecodes
        iv_dataaccessrolearn = av_lrole
        iv_jobname = lv_translate_job_name
        iv_sourcelanguagecode = cv_sourcelanguagecode
    ).

    "Get the job ID
    lv_jobid = lo_result->get_jobid( ).

    "Testing list_text_translation_job
    ao_xl8_actions->list_text_translation_jobs(
      EXPORTING iv_jobname = lv_translate_job_name
      IMPORTING oo_result = lo_list_translation_result
    ).

    "Validation
    lv_found = abap_false.
    IF lo_list_translation_result->has_textxlationjobprpslist( ) = 'X'.
      lv_found               = abap_true.
    ENDIF.

    cl_abap_unit_assert=>assert_true(
       act                    = lv_found
       msg                    = |List job failed|
    ).

    "Wait for translate job to complete
    CALL METHOD job_waiter
      EXPORTING
        iv_jobid     = lv_jobid
        iv_jobstatus = 'COMPLETED'.

    "Cleanup
    CALL METHOD cleanup_s3 EXPORTING iv_bucket_name = lv_bucket_name.

  ENDMETHOD.

ENDCLASS.
