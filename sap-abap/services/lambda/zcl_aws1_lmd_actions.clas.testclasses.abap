" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS ltc_zcl_aws1_lmd_actions DEFINITION DEFERRED.
CLASS zcl_aws1_lmd_actions DEFINITION LOCAL FRIENDS ltc_zcl_aws1_lmd_actions.

CLASS ltc_zcl_aws1_lmd_actions DEFINITION FOR TESTING DURATION SHORT RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA ao_lmd TYPE REF TO /aws1/if_lmd.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_lmd_actions TYPE REF TO zcl_aws1_lmd_actions.
    DATA av_lrole TYPE /aws1/lmdrolearn.
    CONSTANTS cv_create_function_name TYPE /aws1/lmdfunctionname VALUE 'code-example-create-function'.
    CONSTANTS cv_misc_function_name TYPE /aws1/lmdfunctionname VALUE 'code-example-misce-function'.
    CONSTANTS cv_del_function_name TYPE /aws1/lmdfunctionname VALUE 'code-example-delete-function'.

    METHODS: create_function FOR TESTING RAISING /aws1/cx_rt_generic,
      get_function FOR TESTING RAISING /aws1/cx_rt_generic,
      list_functions FOR TESTING RAISING /aws1/cx_rt_generic,
      invoke_function FOR TESTING RAISING /aws1/cx_rt_generic,
      update_function_code FOR TESTING RAISING /aws1/cx_rt_generic,
      update_function_configuration FOR TESTING RAISING /aws1/cx_rt_generic,
      delete_function FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS setup RAISING /aws1/cx_rt_generic zcx_aws1_ex_generic.
    METHODS teardown RAISING /aws1/cx_rt_generic zcx_aws1_ex_generic.
    METHODS:
      create_code
        RETURNING VALUE(oo_code) TYPE REF TO /aws1/cl_lmdfunctioncode
        RAISING   /aws1/cx_rt_generic,
      update_code
        RETURNING VALUE(oo_code) TYPE /aws1/lmdblob
        RAISING   /aws1/cx_rt_generic,
      create_lambda_function
        IMPORTING iv_function_name TYPE /aws1/lmdfunctionname
        RAISING   /aws1/cx_rt_generic,
      verify_lambda_state
        IMPORTING iv_function_name TYPE /aws1/lmdfunctionname
        RAISING   /aws1/cx_rt_generic,
      assert_lambda_result
        IMPORTING
                  iv_payload TYPE /aws1/lmdblob
                  iv_exp     TYPE i
        RAISING   /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_lmd_actions IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_lmd = /aws1/cl_lmd_factory=>create( ao_session ).
    ao_lmd_actions = NEW zcl_aws1_lmd_actions( ).

    DATA(lt_roles) = ao_session->get_configuration( )->get_logical_iam_roles( ).
    READ TABLE lt_roles INDEX 1 INTO DATA(lo_role). " use the first role we find in the profile, as our lambda role
    av_lrole = lo_role-iam_role_arn.

    create_lambda_function( cv_misc_function_name ).

  ENDMETHOD.
  METHOD teardown.
    TRY.
        ao_lmd->deletefunction( iv_functionname = cv_misc_function_name ).
      CATCH /aws1/cx_lmdresourcenotfoundex.
    ENDTRY.
    TRY.
        ao_lmd->deletefunction( iv_functionname = cv_create_function_name ).
      CATCH /aws1/cx_lmdresourcenotfoundex.
    ENDTRY.
    TRY.
        ao_lmd->deletefunction( iv_functionname = cv_create_function_name ).
      CATCH /aws1/cx_lmdresourcenotfoundex.
    ENDTRY.
  ENDMETHOD.

  METHOD create_function.
    ao_lmd_actions->create_function(
            iv_function_name = cv_create_function_name
            iv_role_arn      = av_lrole
            iv_handler       = |lambda_function.lambda_handler|
            io_zip_file      = create_code( ) ).
    DATA(lv_function_arn) = ao_lmd->getfunctionconfiguration( iv_functionname = cv_create_function_name )->get_functionarn( ).
    cl_abap_unit_assert=>assert_not_initial(
              act = lv_function_arn
              msg = |Failed to create Lambda function { cv_create_function_name }| ).
  ENDMETHOD.
  METHOD create_code.
    DATA(lo_zip) = NEW cl_abap_zip( ).
    DATA(lv_code) =
      |import logging\n| &&
      |import json\n| &&
      |\n| &&
      |logger = logging.getLogger()\n| &&
      |logger.setLevel(logging.INFO)\n| &&
      |\n| &&
      |def lambda_handler(event, context):\n| &&
      | # TODO implement\n| &&
      | action = event.get('action')\n| &&
      | if action == 'increment':\n| &&
      |  result = event.get('number', 0) + 1\n| &&
      |  logger.info('Calculated result of %s', result)\n| &&
      | else:\n| &&
      |  logger.error("%s is not a valid action.", action)\n| &&
      | return \{\n| &&
      |  'statusCode': 200,\n| &&
      |  'body': json.dumps(result)\n| &&
      | \}\n|.

    DATA(lv_xcode) = /aws1/cl_rt_util=>string_to_xstring( lv_code ).
    lo_zip->add( name = 'lambda_function.py'
                 content = lv_xcode ).
    DATA(lv_xzip) = lo_zip->save( ).
    oo_code = NEW /aws1/cl_lmdfunctioncode( iv_zipfile = lv_xzip ).
  ENDMETHOD.
  METHOD get_function.
    DATA(lo_result) = ao_lmd_actions->get_function( cv_misc_function_name ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = |Failed to retrieve information about Lambda function { cv_misc_function_name }| ).

    cl_abap_unit_assert=>assert_equals(
      exp = cv_misc_function_name
      act = lo_result->get_configuration( )->get_functionname( )
      msg = |Lambda function name did not match expected value { cv_misc_function_name }| ).

    cl_abap_unit_assert=>assert_equals(
      exp = `lambda_function.lambda_handler`
      act = lo_result->get_configuration( )->get_handler( )
      msg = |Handler did not match expected value| ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_lrole
      act = lo_result->get_configuration( )->get_role( )
      msg = |Function's execution role did not match expected value { av_lrole }| ).

    cl_abap_unit_assert=>assert_equals(
      exp = `python3.9`
      act = lo_result->get_configuration( )->get_runtime( )
      msg = |Function's runtime did not match expected value | ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result->get_code( )->get_location( )
      msg = |Failed to retrieve value of Lambda location/URL| ).
  ENDMETHOD.
  METHOD create_lambda_function.
    ao_lmd->createfunction(
        iv_functionname = iv_function_name
        iv_runtime = `python3.9`
        iv_role      = av_lrole
        iv_handler       = `lambda_function.lambda_handler`
        io_code      = create_code( ) ).
  ENDMETHOD.
  METHOD list_functions.
    DATA(lo_result) = ao_lmd_actions->list_functions( ).


    cl_abap_unit_assert=>assert_number_between(
      number = lines( lo_result->get_functions( ) )
      lower = 1
      upper = 1000000
      msg = |At least one function should have been found by ListFunctions| ).
  ENDMETHOD.
  METHOD invoke_function.
    CONSTANTS cv_function_name TYPE /aws1/lmdfunctionname VALUE 'code-example-invoke-function'.
    create_lambda_function( cv_function_name ).
    verify_lambda_state( cv_function_name ).

    DATA(lo_result) = ao_lmd_actions->invoke_function( cv_function_name ).

    cl_abap_unit_assert=>assert_initial(
      act = lo_result->get_functionerror( )
      msg = |Invoke function call failed with error { lo_result->get_functionerror( ) }| ).

    assert_lambda_result(
      iv_payload = lo_result->ask_payload( )
      iv_exp = 11 ).

    ao_lmd->deletefunction( iv_functionname = cv_function_name ).

  ENDMETHOD.
  METHOD verify_lambda_state.
    WHILE ao_lmd->getfunction( iv_functionname = iv_function_name )->get_configuration( )->ask_state( ) <> 'Active'.
      IF sy-index = 10.
        EXIT.
      ENDIF.
      WAIT UP TO 1 SECONDS.
    ENDWHILE.
  ENDMETHOD.
  METHOD assert_lambda_result.
    DATA(lo_doc) = cl_ixml=>create( )->create_document( ).
    CALL TRANSFORMATION id
      SOURCE XML iv_payload
      RESULT XML lo_doc.

    DATA(lo_iter) = lo_doc->get_first_child( )->get_children( )->create_iterator( ).
    DATA(lo_node) = lo_iter->get_next( ).


    WHILE lo_node IS NOT INITIAL.
      DATA(lv_name) = lo_node->get_attributes( )->get_named_item_ns( name = 'name' )->get_value( ).
      IF lv_name = 'body'.
        DATA(lv_value) = lo_node->get_value( ).
      ENDIF.
      lo_node = lo_iter->get_next( ).
    ENDWHILE.

    cl_abap_unit_assert=>assert_equals(
      exp = iv_exp
      act = lv_value
      msg = |Invoke function response ({ lv_value }) was not as expected ({ iv_exp })| ).
  ENDMETHOD.

  METHOD update_function_code.

    verify_lambda_state( cv_misc_function_name ).

    DATA(lo_update_result) = ao_lmd_actions->update_function_code(
      iv_function_name = cv_misc_function_name
      io_zip_file = update_code( ) ).
    WAIT UP TO 10 SECONDS.

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_update_result
      msg = |Failed to update Lambda function code| ).

    DATA(lv_json) = /aws1/cl_rt_util=>string_to_xstring(
      `{`  &&
        `"action": "decrement",`  &&
        `"number": 10` &&
      `}` ).

    DATA(lo_invoke_result) = ao_lmd->invoke(
         iv_functionname = cv_misc_function_name
         iv_payload = lv_json ).

    cl_abap_unit_assert=>assert_initial(
       act = lo_invoke_result->get_functionerror( )
       msg = |Invoke function call failed with error { lo_invoke_result->get_functionerror( ) }| ).

    assert_lambda_result(
      iv_payload = lo_invoke_result->ask_payload( )
      iv_exp = 9 ).
  ENDMETHOD.
  METHOD update_code.
    DATA(lo_zip) = NEW cl_abap_zip( ).
    DATA(lv_code) =
      |import logging\n| &&
      |import json\n| &&
      |\n| &&
      |logger = logging.getLogger()\n| &&
      |logger.setLevel(logging.INFO)\n| &&
      |\n| &&
      |def lambda_handler(event, context):\n| &&
      | # TODO implement\n| &&
      | action = event.get('action')\n| &&
      | if action == 'increment':\n| &&
      |  result = event.get('number', 0) + 1\n| &&
      |  logger.info('Calculated result of %s', result)\n| &&
      | elif action == 'decrement':\n| &&
      |  result = event.get('number', 0) - 1\n| &&
      |  logger.info('Calculated result of %s', result)\n| &&
      | else:\n| &&
      |  logger.error("%s is not a valid action.", action)\n| &&
      | return \{\n| &&
      |  'statusCode': 200,\n| &&
      |  'body': json.dumps(result)\n| &&
      | \}\n|.


    DATA(lv_xcode) = /aws1/cl_rt_util=>string_to_xstring( lv_code ).
    lo_zip->add( name = 'lambda_function.py'
                 content = lv_xcode ).
    oo_code = lo_zip->save( ).
  ENDMETHOD.
  METHOD update_function_configuration.
    verify_lambda_state( cv_misc_function_name ).

    DATA(lo_result) = ao_lmd_actions->update_function_configuration(
        iv_function_name = cv_misc_function_name
        iv_runtime = `python3.9`
        iv_memory_size = 150 ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lo_result
      msg = |Failed to update Lambda function configuration| ).

    cl_abap_unit_assert=>assert_equals(
      exp = `lambda_function.lambda_handler`
      act = lo_result->get_handler( )
      msg = |Handler did not match expected value| ).

    cl_abap_unit_assert=>assert_equals(
      exp = av_lrole
      act = lo_result->get_role( )
      msg = |Function's execution role did not match expected value { av_lrole }| ).

    cl_abap_unit_assert=>assert_equals(
      exp = `python3.9`
      act = lo_result->get_runtime( )
      msg = |Function's runtime did not match expected value | ).

    cl_abap_unit_assert=>assert_equals(
      exp = 'updated lambda function'
      act = to_lower( lo_result->get_description( ) )
      msg = |Function's description did not match expected value | ).

    cl_abap_unit_assert=>assert_equals(
      exp = 150
      act = lo_result->get_memorysize( )
      msg = |Function memory did not match expected value| ).
  ENDMETHOD.
  METHOD delete_function.
    create_lambda_function( cv_del_function_name ).
    ao_lmd_actions->delete_function( cv_del_function_name ).

    TRY.
        ao_lmd->getfunction( iv_functionname = cv_del_function_name ).
        " should not reach here
        cl_abap_unit_assert=>assert_true(
          act = abap_false
          msg = |Function { cv_del_function_name } should have been deleted| ).
      CATCH /aws1/cx_lmdresourcenotfoundex.
        " expected this exception
    ENDTRY.

  ENDMETHOD.
ENDCLASS.
