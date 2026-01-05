" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_agw_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CLASS-DATA ao_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA ao_agw TYPE REF TO /aws1/if_agw.
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

    " Create a test REST API for all tests
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-api-' && lv_uuid.

    TRY.
        DATA(lo_api) = ao_agw->createrestapi(
          iv_name = lv_api_name
          iv_description = 'Test API for ABAP SDK examples' ).
        av_rest_api_id = lo_api->get_id( ).

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

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create test REST API: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    IF av_rest_api_id IS NOT INITIAL.
      TRY.
          ao_agw->deleterestapi( iv_restapiid = av_rest_api_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_rest_api.
    TRY.
        DATA(lo_result) = ao_agw->getrestapi( iv_restapiid = av_rest_api_id ).
        DATA(lv_api_id) = lo_result->get_id( ).
        DATA(lv_api_name) = lo_result->get_name( ).
        MESSAGE 'REST API retrieved with ID: ' && lv_api_id && ' and name: ' && lv_api_name TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
      CATCH /aws1/cx_agwunauthorizedex.
        cl_abap_unit_assert=>fail( msg = 'Unauthorized - check credentials' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'REST API retrieval failed' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_api_id
      msg = 'REST API ID should not be empty' ).
  ENDMETHOD.

  METHOD get_rest_apis.
    TRY.
        DATA(lo_result) = ao_agw->getrestapis( ).
        DATA(lt_apis) = lo_result->get_items( ).
        DATA(lv_count) = lines( lt_apis ).
        MESSAGE 'Found ' && lv_count && ' REST APIs' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetRestApis result should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lt_apis
      msg = 'Should have at least one REST API' ).
  ENDMETHOD.

  METHOD create_resource.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_resource_path) = 'test' && lv_uuid(8).

    TRY.
        DATA(lo_result) = ao_agw->createresource(
          iv_restapiid = av_rest_api_id
          iv_parentid = av_root_resource_id
          iv_pathpart = lv_resource_path ).
        DATA(lv_resource_id) = lo_result->get_id( ).
        MESSAGE 'Resource created with ID: ' && lv_resource_id TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwnotfoundexception.
        cl_abap_unit_assert=>fail( msg = 'REST API or parent resource not found' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Resource creation failed' ).

    av_resource_id = lv_resource_id.
    cl_abap_unit_assert=>assert_not_initial(
      act = av_resource_id
      msg = 'Resource ID should not be empty' ).
  ENDMETHOD.

  METHOD get_resources.
    TRY.
        DATA(lo_result) = ao_agw->getresources(
          iv_restapiid = av_rest_api_id ).
        DATA(lt_resources) = lo_result->get_items( ).
        DATA(lv_count) = lines( lt_resources ).
        MESSAGE 'Found ' && lv_count && ' resources' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwnotfoundexception.
        cl_abap_unit_assert=>fail( msg = 'REST API not found' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetResources result should not be null' ).

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
        DATA(lo_result) = ao_agw->putmethod(
          iv_restapiid = av_rest_api_id
          iv_resourceid = av_resource_id
          iv_httpmethod = 'GET'
          iv_authorizationtype = 'NONE' ).
        MESSAGE 'Method GET added to resource' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwnotfoundexception.
        cl_abap_unit_assert=>fail( msg = 'Resource not found' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutMethod result should not be null' ).

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
        DATA(lo_result) = ao_agw->putmethodresponse(
          iv_restapiid = av_rest_api_id
          iv_resourceid = av_resource_id
          iv_httpmethod = 'GET'
          iv_statuscode = '200' ).
        MESSAGE 'Method response configured for status 200' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwnotfoundexception.
        cl_abap_unit_assert=>fail( msg = 'Method not found' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutMethodResponse result should not be null' ).

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

    DATA(lo_resource) = ao_agw->createresource(
      iv_restapiid = av_rest_api_id
      iv_parentid = av_root_resource_id
      iv_pathpart = lv_resource_path ).
    DATA(lv_test_resource_id) = lo_resource->get_id( ).

    ao_agw->putmethod(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'POST'
      iv_authorizationtype = 'NONE' ).

    " Create a mock Lambda ARN
    DATA(lv_region) = ao_session->get_region( ).
    DATA(lv_lambda_arn) = 'arn:aws:lambda:' && lv_region && ':123456789012:function:mock-function'.
    DATA(lv_integration_uri) = 'arn:aws:apigateway:' && lv_region &&
                               ':lambda:path/2015-03-31/functions/' && lv_lambda_arn && '/invocations'.

    TRY.
        DATA(lo_result) = ao_agw->putintegration(
          iv_restapiid = av_rest_api_id
          iv_resourceid = lv_test_resource_id
          iv_httpmethod = 'POST'
          iv_type = 'AWS_PROXY'
          iv_integrationhttpmethod = 'POST'
          iv_uri = lv_integration_uri ).
        MESSAGE 'Integration configured for method' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwnotfoundexception.
        cl_abap_unit_assert=>fail( msg = 'Method not found' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutIntegration result should not be null' ).

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

    DATA(lo_resource) = ao_agw->createresource(
      iv_restapiid = av_rest_api_id
      iv_parentid = av_root_resource_id
      iv_pathpart = lv_resource_path ).
    DATA(lv_test_resource_id) = lo_resource->get_id( ).

    ao_agw->putmethod(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'POST'
      iv_authorizationtype = 'NONE' ).

    " Create integration first
    DATA(lv_region) = ao_session->get_region( ).
    DATA(lv_lambda_arn) = 'arn:aws:lambda:' && lv_region && ':123456789012:function:mock-function'.
    DATA(lv_integration_uri) = 'arn:aws:apigateway:' && lv_region &&
                               ':lambda:path/2015-03-31/functions/' && lv_lambda_arn && '/invocations'.

    ao_agw->putintegration(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'POST'
      iv_type = 'AWS_PROXY'
      iv_integrationhttpmethod = 'POST'
      iv_uri = lv_integration_uri ).

    TRY.
        DATA(lo_result) = ao_agw->putintegrationresponse(
          iv_restapiid = av_rest_api_id
          iv_resourceid = lv_test_resource_id
          iv_httpmethod = 'POST'
          iv_statuscode = '200' ).
        MESSAGE 'Integration response configured for status 200' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwnotfoundexception.
        cl_abap_unit_assert=>fail( msg = 'Integration not found' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'PutIntegrationResponse result should not be null' ).

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
    DATA(lo_resource) = ao_agw->createresource(
      iv_restapiid = av_rest_api_id
      iv_parentid = av_root_resource_id
      iv_pathpart = lv_resource_path ).
    DATA(lv_test_resource_id) = lo_resource->get_id( ).

    " Create method
    ao_agw->putmethod(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'GET'
      iv_authorizationtype = 'NONE' ).

    " Create method response
    ao_agw->putmethodresponse(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'GET'
      iv_statuscode = '200' ).

    " Create integration with MOCK type (simpler than Lambda)
    ao_agw->putintegration(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'GET'
      iv_type = 'MOCK' ).

    " Create integration response
    ao_agw->putintegrationresponse(
      iv_restapiid = av_rest_api_id
      iv_resourceid = lv_test_resource_id
      iv_httpmethod = 'GET'
      iv_statuscode = '200' ).

    TRY.
        DATA(lo_result) = ao_agw->createdeployment(
          iv_restapiid = av_rest_api_id
          iv_stagename = lv_stage_name
          iv_description = 'Deployment created by ABAP SDK' ).
        DATA(lv_deployment_id) = lo_result->get_id( ).
        MESSAGE 'Deployment created with ID: ' && lv_deployment_id TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        cl_abap_unit_assert=>fail( msg = 'Bad request - invalid parameters' ).
      CATCH /aws1/cx_agwnotfoundexception.
        cl_abap_unit_assert=>fail( msg = 'REST API not found' ).
      CATCH /aws1/cx_agwtoomanyrequestsex.
        cl_abap_unit_assert=>fail( msg = 'Too many requests - rate limit exceeded' ).
    ENDTRY.

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'CreateDeployment result should not be null' ).

    cl_abap_unit_assert=>assert_not_initial(
      act = lv_deployment_id
      msg = 'Deployment ID should not be empty' ).
  ENDMETHOD.

  METHOD delete_rest_api.
    " Create a new API specifically for deletion test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-delete-' && lv_uuid.

    TRY.
        DATA(lo_api) = ao_agw->createrestapi(
          iv_name = lv_api_name
          iv_description = 'Test API for deletion' ).
        DATA(lv_api_id) = lo_api->get_id( ).

        " Tag for cleanup
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

        ao_agw->deleterestapi( iv_restapiid = lv_api_id ).
        MESSAGE 'REST API deleted successfully' TYPE 'I'.

        " Verify deletion by trying to get the API (should fail)
        TRY.
            ao_agw->getrestapi( iv_restapiid = lv_api_id ).
            cl_abap_unit_assert=>fail( msg = 'API should have been deleted' ).
          CATCH /aws1/cx_agwnotfoundexception.
            " Expected - API was successfully deleted
        ENDTRY.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        " If rate limited, skip this test
        MESSAGE 'Test skipped due to rate limiting' TYPE 'I'.
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
