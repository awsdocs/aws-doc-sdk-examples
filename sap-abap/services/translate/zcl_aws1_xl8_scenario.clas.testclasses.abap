" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_xl8_scenario DEFINITION FOR TESTING DURATION LONG RISK LEVEL HARMLESS.

  PRIVATE SECTION.

    CONSTANTS: cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_xl8 TYPE REF TO /aws1/if_xl8.
    DATA ao_s3 TYPE REF TO /aws1/if_s3.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_xl8_scenario TYPE REF TO zcl_aws1_xl8_scenario.
    DATA av_lrole TYPE /aws1/xl8iamrolearn.
    DATA av_file_content TYPE /aws1/s3_streamingblob.

    METHODS getting_started_with_xl8 FOR TESTING.
    METHODS setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic.


ENDCLASS.       "ltc_Zcl_Aws1_Xl8_Scenario


CLASS ltc_zcl_aws1_xl8_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_xl8 = /aws1/cl_xl8_factory=>create( ao_session ).
    ao_xl8_scenario = NEW zcl_aws1_xl8_scenario( ).
    ao_s3 = /aws1/cl_s3_factory=>create( ao_session ).

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
  ENDMETHOD.

  METHOD getting_started_with_xl8.

    DATA lv_uuid_16 TYPE sysuuid_x16.
    DATA lv_bucket_name TYPE /aws1/s3_bucketname.
    DATA lv_translate_job_name TYPE /aws1/xl8jobname.
    DATA lv_input_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_data_s3uri TYPE /aws1/xl8s3uri.
    DATA lv_output_folder TYPE /aws1/xl8s3uri.
    DATA lv_found TYPE abap_bool.
    DATA lv_jobid TYPE /aws1/xl8jobid.
    DATA lo_des_translation_result TYPE REF TO /aws1/cl_xl8dsctextxlatjobrsp.
    DATA lv_out_key1 TYPE /aws1/s3_objectkey.
    DATA lv_out_key2 TYPE /aws1/s3_objectkey.
    DATA lv_out_key3 TYPE /aws1/s3_objectkey.
    DATA lv_out_key4 TYPE /aws1/s3_objectkey.
    DATA lv_out_key5 TYPE /aws1/s3_objectkey.
    DATA lv_out_key6 TYPE /aws1/s3_objectkey.
    DATA lv_obj1 TYPE /aws1/s3_objectkey.
    DATA lv_obj2 TYPE /aws1/s3_objectkey.
    DATA lv_obj3 TYPE /aws1/s3_objectkey.

    CONSTANTS cv_bucket_name TYPE /aws1/s3_bucketname VALUE 'code-example-xl8-'.
    CONSTANTS cv_input_key TYPE /aws1/s3_objectkey VALUE 'translate/input/input.txt'.
    CONSTANTS cv_output_folder TYPE /aws1/s3_objectkey VALUE 'translate/output/'.
    CONSTANTS cv_input_folder TYPE /aws1/s3_objectkey VALUE 'translate/input/'.
    CONSTANTS cv_input_data_contenttype TYPE /aws1/xl8contenttype VALUE 'text/plain'.
    CONSTANTS cv_output_data_contenttype TYPE /aws1/xl8contenttype VALUE 'text/plain'.
    CONSTANTS cv_sourcelanguagecode TYPE /aws1/xl8languagecodestring VALUE 'fr'.
    CONSTANTS cv_targetlanguagecode TYPE /aws1/xl8languagecodestring VALUE 'en'.

    "Define role arn
    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles WITH KEY profile_id = cv_pfl INTO DATA(lo_role).
    av_lrole = lo_role-iam_role_arn.

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
    ao_xl8_scenario->getting_started_with_xl8(
      EXPORTING
        iv_jobname                         = lv_translate_job_name
        iv_dataaccessrolearn               = av_lrole
        iv_input_data_s3uri                = lv_input_data_s3uri
        iv_input_data_contenttype          = cv_input_data_contenttype
        iv_output_data_s3uri               = lv_output_data_s3uri
        iv_sourcelanguagecode              = cv_sourcelanguagecode
        iv_targetlanguagecode              = cv_targetlanguagecode
      IMPORTING
       oo_result                           = lo_des_translation_result
    ).

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
    DATA(lo_list) = ao_s3->listobjectsv2( iv_bucket = lv_bucket_name ).
    LOOP AT lo_list->get_contents( ) INTO DATA(lo_object).
      ao_s3->deleteobject(
          iv_bucket = lv_bucket_name
          iv_key = lo_object->get_key( )
      ).
    ENDLOOP.

    ao_s3->deletebucket( iv_bucket = lv_bucket_name ).
  ENDMETHOD.
ENDCLASS.
