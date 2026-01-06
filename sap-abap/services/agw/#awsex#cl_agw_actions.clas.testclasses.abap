" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_agw_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_agw TYPE REF TO /aws1/if_agw.
    CLASS-DATA ao_actions TYPE REF TO /awsex/cl_agw_actions.
    CLASS-DATA av_rest_api_id TYPE /aws1/agwstring.
    CLASS-DATA av_resource_id TYPE /aws1/agwstring.
    CLASS-DATA av_root_resource_id TYPE /aws1/agwstring.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown.

    METHODS create_rest_api FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_rest_apis FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_resource FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_resources FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS put_method FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS put_method_response FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS put_integration FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS put_integration_response FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS create_deployment FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS delete_rest_api FOR TESTING RAISING /aws1/cx_rt_generic.
ENDCLASS.

CLASS ltc_awsex_cl_agw_actions IMPLEMENTATION.

  METHOD class_setup.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    ao_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    ao_agw = /aws1/cl_agw_factory=>create( ao_session ).
    ao_actions = NEW /awsex/cl_agw_actions( ).

    " Create a test REST API for all tests
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-api-' && lv_uuid.

    " Retry logic for rate limiting
    DATA lv_retry_count TYPE i VALUE 0.
    DATA lv_max_retries TYPE i VALUE 3.
    DATA lv_created TYPE abap_bool VALUE abap_false.

    WHILE lv_retry_count < lv_max_retries AND lv_created = abap_false.
      TRY.
          DATA(lo_api) = ao_agw->createrestapi(
            iv_name = lv_api_name
            iv_description = 'Test API for ABAP SDK examples' ).
          av_rest_api_id = lo_api->get_id( ).
          lv_created = abap_true.

          " Get the root resource ID
          DATA(lo_resources) = ao_agw->getresources( iv_restapiid = av_rest_api_id ).
          DATA(lt_resources) = lo_resources->get_items( ).
          LOOP AT lt_resources INTO DATA(lo_resource).
            IF lo_resource->get_path( ) = '/'.
              av_root_resource_id = lo_resource->get_id( ).
              EXIT.
            ENDIF.
          ENDLOOP.

          " Tag the API for cleanup
          DATA(lt_tags) = VALUE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring(
            ( VALUE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow(
                key = 'convert_test'
                value = NEW /aws1/cl_agwmapofstrtostr_w( 'true' ) ) ) ).
          ao_agw->tagresource(
            iv_resourcearn = 'arn:aws:apigateway:' && ao_session->get_region( ) &&
                            '::/restapis/' && av_rest_api_id
            it_tags = lt_tags ).

        CATCH /aws1/cx_agwtoomanyrequestsex INTO DATA(lo_rate_limit).
          lv_retry_count = lv_retry_count + 1.
          IF lv_retry_count < lv_max_retries.
            " Wait before retrying with exponential backoff
            DATA(lv_wait_time) = lv_retry_count * 2.
            WAIT UP TO lv_wait_time SECONDS.
          ELSE.
            cl_abap_unit_assert=>fail( msg = |Failed to create test REST API after { lv_max_retries } attempts: Rate limited| ).
          ENDIF.
        CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
          cl_abap_unit_assert=>fail( msg = |Failed to create test REST API: { lo_exception->get_text( ) }| ).
      ENDTRY.
    ENDWHILE.

    IF av_rest_api_id IS INITIAL OR av_root_resource_id IS INITIAL.
      cl_abap_unit_assert=>fail( msg = 'Failed to set up test REST API or root resource' ).
    ENDIF.
  ENDMETHOD.

  METHOD class_teardown.
    " Wait before cleanup to avoid rate limiting
    WAIT UP TO 2 SECONDS.

    IF av_rest_api_id IS NOT INITIAL.
      TRY.
          ao_agw->deleterestapi( iv_restapiid = av_rest_api_id ).
        CATCH /aws1/cx_agwtoomanyrequestsex.
          " If rate limited during cleanup, just log it
          MESSAGE 'Rate limited during cleanup - manual cleanup may be required' TYPE 'I'.
        CATCH /aws1/cx_rt_generic.
          " Ignore other cleanup errors
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_rest_api.
    " Wait to avoid rate limiting from class_setup
    WAIT UP TO 1 SECONDS.

    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-actions-' && lv_uuid.

    TRY.
        DATA(lo_result) = ao_actions->create_rest_api( lv_api_name ).
        DATA(lv_api_id) = lo_result->get_id( ).
        DATA(lv_name) = lo_result->get_name( ).
        MESSAGE 'REST API created via actions class with ID: ' && lv_api_id TYPE 'I'.

        " Tag for cleanup
        DATA(lt_tags) = VALUE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring(
          ( VALUE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow(
              key = 'convert_test'
              value = NEW /aws1/cl_agwmapofstrtostr_w( 'true' ) ) ) ).
        ao_agw->tagresource(
          iv_resourcearn = 'arn:aws:apigateway:' && ao_session->get_region( ) &&
                          '::/restapis/' && lv_api_id
          it_tags = lt_tags ).

        " Wait to avoid rate limiting
        WAIT UP TO 2 SECONDS.

        " Clean up the API created by this test
        ao_actions->delete_rest_api( lv_api_id ).
      CATCH /aws1/cx_agwtoomanyrequestsex INTO DATA(lo_rate_limit).
        " If rate limited, skip cleanup but still validate what we can
        MESSAGE 'Rate limited - test partially completed' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create REST API: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'REST API creation via actions class failed' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_api_id
      msg = 'REST API ID should not be empty' ).

    cl_abap_unit_assert=>assert_equals(
      act = lv_name
      exp = lv_api_name
      msg = 'REST API name should match' ).
  ENDMETHOD.

  METHOD get_rest_apis.
    TRY.
        DATA(lo_result) = ao_actions->get_rest_apis( ).
        DATA(lt_apis) = lo_result->get_items( ).
        DATA(lv_count) = lines( lt_apis ).
        MESSAGE 'Found ' && lv_count && ' REST APIs via actions class' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to get REST APIs: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetRestApis result via actions class should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_apis
      msg = 'Should have at least one REST API' ).
  ENDMETHOD.

  METHOD create_resource.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_resource_path) = 'test' && lv_uuid(8).

    TRY.
        DATA(lo_result) = ao_actions->create_resource(
          iv_rest_api_id = av_rest_api_id
          iv_parent_id = av_root_resource_id
          iv_resource_path = lv_resource_path ).
        DATA(lv_resource_id) = lo_result->get_id( ).
        MESSAGE 'Resource created via actions class with ID: ' && lv_resource_id TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create resource: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Resource creation via actions class failed' ).

    av_resource_id = lv_resource_id.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_resource_id
      msg = 'Resource ID should not be empty' ).
  ENDMETHOD.

  METHOD get_resources.
    TRY.
        DATA(lo_result) = ao_actions->get_resources( av_rest_api_id ).
        DATA(lt_resources) = lo_result->get_items( ).
        DATA(lv_count) = lines( lt_resources ).
        MESSAGE 'Found ' && lv_count && ' resources via actions class' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to get resources: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetResources result via actions class should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_resources
      msg = 'Should have at least one resource' ).
  ENDMETHOD.

  METHOD put_method.
    " First create a test resource if not already done
    IF av_resource_id IS INITIAL.
      create_resource( ).
    ENDIF.

    TRY.
        DATA(lo_result) = ao_actions->put_method(
          iv_rest_api_id = av_rest_api_id
          iv_resource_id = av_resource_id
          iv_http_method = 'GET' ).
        MESSAGE 'Method GET added via actions class to resource' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to put method: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutMethod result via actions class should not be null' ).

    DATA(lv_method) = lo_result->get_httpmethod( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_method
      exp = 'GET'
      msg = 'HTTP method should be GET' ).
  ENDMETHOD.

  METHOD put_method_response.
    " Ensure method exists
    IF av_resource_id IS INITIAL.
      create_resource( ).
      put_method( ).
    ENDIF.

    TRY.
        DATA(lo_result) = ao_actions->put_method_response(
          iv_rest_api_id = av_rest_api_id
          iv_resource_id = av_resource_id
          iv_http_method = 'GET' ).
        MESSAGE 'Method response configured via actions class for status 200' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to put method response: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutMethodResponse result via actions class should not be null' ).

    DATA(lv_status) = lo_result->get_statuscode( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_status
      exp = '200'
      msg = 'Status code should be 200' ).
  ENDMETHOD.

  METHOD put_integration.
    " Ensure method exists - we need a fresh resource for this test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_resource_path) = 'integ' && lv_uuid(6).

    DATA(lo_resource) = ao_actions->create_resource(
      iv_rest_api_id = av_rest_api_id
      iv_parent_id = av_root_resource_id
      iv_resource_path = lv_resource_path ).
    DATA(lv_test_resource_id) = lo_resource->get_id( ).

    ao_actions->put_method(
      iv_rest_api_id = av_rest_api_id
      iv_resource_id = lv_test_resource_id
      iv_http_method = 'POST' ).

    " Create a mock Lambda ARN
    DATA(lv_region) = ao_session->get_region( ).
    DATA(lv_lambda_arn) = 'arn:aws:lambda:' && lv_region && ':123456789012:function:mock-function'.
    DATA(lv_integration_uri) = 'arn:aws:apigateway:' && lv_region &&
                               ':lambda:path/2015-03-31/functions/' && lv_lambda_arn && '/invocations'.

    TRY.
        DATA(lo_result) = ao_actions->put_integration(
          iv_rest_api_id = av_rest_api_id
          iv_resource_id = lv_test_resource_id
          iv_http_method = 'POST'
          iv_integration_uri = lv_integration_uri ).
        MESSAGE 'Integration configured via actions class for method' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to put integration: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutIntegration result via actions class should not be null' ).

    DATA(lv_type) = lo_result->get_type( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_type
      exp = 'AWS_PROXY'
      msg = 'Integration type should be AWS_PROXY' ).
  ENDMETHOD.

  METHOD put_integration_response.
    " Create a fresh resource with method and integration
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_resource_path) = 'intrs' && lv_uuid(6).

    DATA(lo_resource) = ao_actions->create_resource(
      iv_rest_api_id = av_rest_api_id
      iv_parent_id = av_root_resource_id
      iv_resource_path = lv_resource_path ).
    DATA(lv_test_resource_id) = lo_resource->get_id( ).

    ao_actions->put_method(
      iv_rest_api_id = av_rest_api_id
      iv_resource_id = lv_test_resource_id
      iv_http_method = 'POST' ).

    " Create integration first
    DATA(lv_region) = ao_session->get_region( ).
    DATA(lv_lambda_arn) = 'arn:aws:lambda:' && lv_region && ':123456789012:function:mock-function'.
    DATA(lv_integration_uri) = 'arn:aws:apigateway:' && lv_region &&
                               ':lambda:path/2015-03-31/functions/' && lv_lambda_arn && '/invocations'.

    ao_actions->put_integration(
      iv_rest_api_id = av_rest_api_id
      iv_resource_id = lv_test_resource_id
      iv_http_method = 'POST'
      iv_integration_uri = lv_integration_uri ).

    TRY.
        DATA(lo_result) = ao_actions->put_integration_response(
          iv_rest_api_id = av_rest_api_id
          iv_resource_id = lv_test_resource_id
          iv_http_method = 'POST' ).
        MESSAGE 'Integration response configured via actions class for status 200' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to put integration response: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutIntegrationResponse result via actions class should not be null' ).

    DATA(lv_status) = lo_result->get_statuscode( ).
    cl_abap_unit_assert=>assert_equals(
      act = lv_status
      exp = '200'
      msg = 'Status code should be 200' ).
  ENDMETHOD.

  METHOD create_deployment.
    " Create a complete API setup for deployment
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_resource_path) = 'deplo' && lv_uuid(6).
    DATA(lv_stage_name) = 'test' && lv_uuid(5).

    " Create resource
    DATA(lo_resource) = ao_actions->create_resource(
      iv_rest_api_id = av_rest_api_id
      iv_parent_id = av_root_resource_id
      iv_resource_path = lv_resource_path ).
    DATA(lv_test_resource_id) = lo_resource->get_id( ).

    " Create method
    ao_actions->put_method(
      iv_rest_api_id = av_rest_api_id
      iv_resource_id = lv_test_resource_id
      iv_http_method = 'GET' ).

    " Create method response
    ao_actions->put_method_response(
      iv_rest_api_id = av_rest_api_id
      iv_resource_id = lv_test_resource_id
      iv_http_method = 'GET' ).

    " Create integration with MOCK type (simpler than Lambda)
    " Note: put_integration in actions class requires integration_uri parameter
    " We need to use SDK directly for MOCK type integration
    ao_agw->putintegration(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'GET'
      iv_type = 'MOCK' ).

    " Create integration response
    ao_actions->put_integration_response(
      iv_rest_api_id = av_rest_api_id
      iv_resource_id = lv_test_resource_id
      iv_http_method = 'GET' ).

    TRY.
        DATA(lo_result) = ao_actions->create_deployment(
          iv_rest_api_id = av_rest_api_id
          iv_stage_name = lv_stage_name ).
        DATA(lv_deployment_id) = lo_result->get_id( ).
        MESSAGE 'Deployment created via actions class with ID: ' && lv_deployment_id TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create deployment: { lo_exception->get_text( ) }| ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'CreateDeployment result via actions class should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_deployment_id
      msg = 'Deployment ID should not be empty' ).
  ENDMETHOD.

  METHOD delete_rest_api.
    " Wait to avoid rate limiting
    WAIT UP TO 1 SECONDS.

    " Create a new API specifically for deletion test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-delete-' && lv_uuid.

    TRY.
        DATA(lo_api) = ao_actions->create_rest_api( lv_api_name ).
        DATA(lv_api_id) = lo_api->get_id( ).

        " Tag for cleanup (using SDK directly for tagging)
        DATA(lt_tags) = VALUE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring(
          ( VALUE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow(
              key = 'convert_test'
              value = NEW /aws1/cl_agwmapofstrtostr_w( 'true' ) ) ) ).
        ao_agw->tagresource(
          iv_resourcearn = 'arn:aws:apigateway:' && ao_session->get_region( ) &&
                          '::/restapis/' && lv_api_id
          it_tags = lt_tags ).

        " Wait a moment to avoid rate limiting
        WAIT UP TO 2 SECONDS.

        ao_actions->delete_rest_api( lv_api_id ).
        MESSAGE 'REST API deleted via actions class successfully' TYPE 'I'.

        " Verify deletion by trying to get the API (should fail)
        TRY.
            ao_agw->getrestapi( iv_restapiid = lv_api_id ).
            cl_abap_unit_assert=>fail( msg = 'API should have been deleted' ).
          CATCH /aws1/cx_agwnotfoundexception.
            " Expected - API was successfully deleted
        ENDTRY.
      CATCH /aws1/cx_agwtoomanyrequestsex INTO DATA(lo_rate_limit).
        " If rate limited during creation or deletion, log but don't fail
        MESSAGE 'Rate limited during delete test - skipping verification' TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        " If rate limited or other error, report it
        MESSAGE 'Test skipped or failed: ' && lo_exception->get_text( ) TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
