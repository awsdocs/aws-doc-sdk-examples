" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
" "  Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights
" "  Reserved.
" "  SPDX-License-Identifier: MIT-0
" """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

CLASS ltc_zcl_aws1_lmd_scenario DEFINITION DEFERRED.
CLASS zcl_aws1_lmd_scenario DEFINITION LOCAL FRIENDS ltc_zcl_aws1_lmd_scenario.

CLASS ltc_zcl_aws1_lmd_scenario DEFINITION FOR TESTING  DURATION SHORT RISK LEVEL HARMLESS.

  PRIVATE SECTION.
    CONSTANTS: cv_pfl           TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO',
               cv_function_name TYPE /aws1/lmdfunctionname VALUE 'code-example-function-scenario'.

    DATA ao_lmd TYPE REF TO /aws1/if_lmd.
    DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA ao_lmd_scenario TYPE REF TO zcl_aws1_lmd_scenario.

    METHODS getting_started_scenario FOR TESTING RAISING /aws1/cx_rt_generic.

    METHODS: setup RAISING /aws1/cx_rt_generic ycx_aws1_mit_generic,
      create_code
        RETURNING VALUE(oo_code) TYPE REF TO /aws1/cl_lmdfunctioncode
        RAISING   /aws1/cx_rt_generic,
      update_code
        RETURNING VALUE(oo_code) TYPE /aws1/lmdblob
        RAISING   /aws1/cx_rt_generic,
      assert_lambda_result
        IMPORTING
                  iv_payload TYPE /aws1/lmdblob
                  iv_exp     TYPE i
        RAISING   /aws1/cx_rt_generic.

ENDCLASS.

CLASS ltc_zcl_aws1_lmd_scenario IMPLEMENTATION.

  METHOD setup.
    ao_session = /aws1/cl_rt_session_aws=>create( iv_profile_id = cv_pfl ).
    ao_lmd = /aws1/cl_lmd_factory=>create( ao_session ).
    ao_lmd_scenario = NEW zcl_aws1_lmd_scenario( ).

  ENDMETHOD.
  METHOD getting_started_scenario.
    DATA lv_initial_invoke_payload TYPE /aws1/lmdblob.
    DATA lv_updated_invoke_payload TYPE /aws1/lmdblob.
    ao_lmd_scenario->getting_started_with_functions(
      EXPORTING
        iv_role_name = 'code-example-lambda-role-write-logs'
        iv_function_name =  cv_function_name
        iv_handler          = |lambda_function.lambda_handler|
        io_initial_zip_file = create_code( )
        io_updated_zip_file = update_code( )
        IMPORTING
          ov_initial_invoke_payload = lv_initial_invoke_payload
          ov_updated_invoke_payload = lv_updated_invoke_payload
    ).
    assert_lambda_result(
       iv_payload = lv_initial_invoke_payload
       iv_exp = 11
     ).

    assert_lambda_result(
       iv_payload = lv_updated_invoke_payload
       iv_exp = 9
     ).

    DATA lv_found TYPE abap_bool VALUE abap_false.
    LOOP AT ao_lmd->listfunctions( )->get_functions( ) INTO DATA(lo_function).
      IF lo_function->get_functionname( ) = cv_function_name.
        lv_found = abap_true.
      ENDIF.
    ENDLOOP.

    cl_abap_unit_assert=>assert_false(
      act = lv_found
      msg =  |Function { cv_function_name } should have been deleted|
    ).

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
    lo_zip->add( name = 'lambda_function.py' content = lv_xcode ).
    DATA(lv_xzip) = lo_zip->save( ).
    oo_code = NEW /aws1/cl_lmdfunctioncode( iv_zipfile = lv_xzip ).
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
    lo_zip->add( name = 'lambda_function.py' content = lv_xcode ).
    oo_code = lo_zip->save( ).
  ENDMETHOD.
  METHOD assert_lambda_result.
    DATA(lo_doc) = cl_ixml=>create( )->create_document( ).
    CALL TRANSFORMATION id
    SOURCE XML iv_payload
    RESULT XML lo_doc.

    DATA(lo_iter) = lo_doc->get_first_child( )->get_children( )->create_iterator( ).
    DATA(lo_node) = lo_iter->get_next( ).
    DATA lv_value TYPE i.

    WHILE lo_node IS NOT INITIAL.
      DATA(lv_name) = lo_node->get_attributes( )->get_named_item_ns( name = 'name' )->get_value( ).
      IF lv_name = 'body'.
        lv_value = lo_node->get_value( ).
      ENDIF.
      lo_node = lo_iter->get_next( ).
    ENDWHILE.

    cl_abap_unit_assert=>assert_equals(
      exp = iv_exp
      act = lv_value
      msg = |Invoke function response ({ lv_value }) was not as expected ({ iv_exp })|
    ).
  ENDMETHOD.
ENDCLASS.
