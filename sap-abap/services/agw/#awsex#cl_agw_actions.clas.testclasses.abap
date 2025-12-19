" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS ltc_awsex_cl_agw_actions DEFINITION FOR TESTING DURATION LONG RISK LEVEL DANGEROUS.

  PRIVATE SECTION.
    CLASS-DATA go_session TYPE REF TO /aws1/cl_rt_session_base.
    CLASS-DATA go_agw TYPE REF TO /aws1/if_agw.
    CLASS-DATA gv_rest_api_id TYPE /aws1/agwstring.
    CLASS-DATA gv_resource_id TYPE /aws1/agwstring.
    CLASS-DATA gv_root_resource_id TYPE /aws1/agwstring.

    CLASS-METHODS class_setup RAISING /aws1/cx_rt_generic.
    CLASS-METHODS class_teardown.

    METHODS create_rest_api FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS get_rest_apis FOR TESTING RAISING /aws1/cx_rt_generic.
    METHODS add_rest_resource FOR TESTING RAISING /aws1/cx_rt_generic.
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

    go_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    go_agw = /aws1/cl_agw_factory=>create( go_session ).

    " Create a test REST API for all tests
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-api-' && lv_uuid.

    TRY.
        DATA(lo_api) = go_agw->createrestapi(
          iv_name = lv_api_name
          iv_description = 'Test API for ABAP SDK examples' ).
        gv_rest_api_id = lo_api->get_id( ).

        " Get the root resource ID
        DATA(lo_resources) = go_agw->getresources( iv_restapiid = gv_rest_api_id ).
        DATA(lt_resources) = lo_resources->get_items( ).
        LOOP AT lt_resources INTO DATA(lo_resource).
          IF lo_resource->get_path( ) = '/'.
            gv_root_resource_id = lo_resource->get_id( ).
            EXIT.
          ENDIF.
        ENDLOOP.

        " Tag the API for cleanup
        DATA(lt_tags) = VALUE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring(
          ( VALUE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow(
              key = 'convert_test'
              value = NEW /aws1/cl_agwmapofstrtostr_w( 'true' ) ) ) ).
        go_agw->tagresource(
          iv_resourcearn = 'arn:aws:apigateway:' && go_session->get_region( ) &&
                          '::/restapis/' && gv_rest_api_id
          it_tags = lt_tags ).

      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        cl_abap_unit_assert=>fail( msg = |Failed to create test REST API: { lo_exception->get_text( ) }| ).
    ENDTRY.
  ENDMETHOD.

  METHOD class_teardown.
    IF gv_rest_api_id IS NOT INITIAL.
      TRY.
          go_agw->deleterestapi( iv_restapiid = gv_rest_api_id ).
        CATCH /aws1/cx_rt_generic.
          " Ignore cleanup errors
      ENDTRY.
    ENDIF.
  ENDMETHOD.

  METHOD create_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-create-' && lv_uuid.

    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->create_rest_api( iv_api_name = lv_api_name ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'REST API creation failed' ).

    DATA(lv_api_id) = lo_result->get_id( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_api_id
      msg = 'REST API ID should not be empty' ).

    " Tag for cleanup
    DATA(lt_tags) = VALUE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring(
      ( VALUE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow(
          key = 'convert_test'
          value = NEW /aws1/cl_agwmapofstrtostr_w( 'true' ) ) ) ).
    lo_agw->tagresource(
      iv_resourcearn = 'arn:aws:apigateway:' && lo_session->get_region( ) &&
                      '::/restapis/' && lv_api_id
      it_tags = lt_tags ).

    " Cleanup
    lo_agw->deleterestapi( iv_restapiid = lv_api_id ).
  ENDMETHOD.

  METHOD get_rest_apis.
    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->get_rest_apis( ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetRestApis result should not be null' ).

    DATA(lt_apis) = lo_result->get_items( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_apis
      msg = 'Should have at least one REST API' ).
  ENDMETHOD.

  METHOD add_rest_resource.
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_resource_path) = 'test' && lv_uuid(8).

    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->add_rest_resource(
      iv_rest_api_id = gv_rest_api_id
      iv_parent_id = gv_root_resource_id
      iv_resource_path = lv_resource_path ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'Resource creation failed' ).

    gv_resource_id = lo_result->get_id( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = gv_resource_id
      msg = 'Resource ID should not be empty' ).
  ENDMETHOD.

  METHOD get_resources.
    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->get_resources( iv_rest_api_id = gv_rest_api_id ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'GetResources result should not be null' ).

    DATA(lt_resources) = lo_result->get_items( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lt_resources
      msg = 'Should have at least one resource' ).
  ENDMETHOD.

  METHOD put_method.
    " First create a test resource if not already done
    IF gv_resource_id IS INITIAL.
      add_rest_resource( ).
    ENDIF.

    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->put_method(
      iv_rest_api_id = gv_rest_api_id
      iv_resource_id = gv_resource_id
      iv_http_method = 'GET' ).

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
    IF gv_resource_id IS INITIAL.
      add_rest_resource( ).
      put_method( ).
    ENDIF.

    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->put_method_response(
      iv_rest_api_id = gv_rest_api_id
      iv_resource_id = gv_resource_id
      iv_http_method = 'GET' ).

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
    " Ensure method exists
    IF gv_resource_id IS INITIAL.
      add_rest_resource( ).
      put_method( ).
    ENDIF.

    " Create a mock Lambda ARN
    DATA(lv_region) = go_session->get_region( ).
    DATA(lv_lambda_arn) = 'arn:aws:lambda:' && lv_region && ':123456789012:function:mock-function'.
    DATA(lv_integration_uri) = 'arn:aws:apigateway:' && lv_region &&
                               ':lambda:path/2015-03-31/functions/' && lv_lambda_arn && '/invocations'.

    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->put_integration(
      iv_rest_api_id = gv_rest_api_id
      iv_resource_id = gv_resource_id
      iv_http_method = 'GET'
      iv_integration_uri = lv_integration_uri ).

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
    " Ensure integration exists
    IF gv_resource_id IS INITIAL.
      add_rest_resource( ).
      put_method( ).
      put_integration( ).
    ENDIF.

    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->put_integration_response(
      iv_rest_api_id = gv_rest_api_id
      iv_resource_id = gv_resource_id
      iv_http_method = 'GET' ).

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
    " Ensure we have a complete API setup
    IF gv_resource_id IS INITIAL.
      add_rest_resource( ).
      put_method( ).
      put_method_response( ).
      put_integration( ).
      put_integration_response( ).
    ENDIF.

    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_stage_name) = 'test' && lv_uuid(8).

    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    DATA(lo_result) = lo_actions->create_deployment(
      iv_rest_api_id = gv_rest_api_id
      iv_stage_name = lv_stage_name ).

    cl_abap_unit_assert=>assert_bound(
      act = lo_result
      msg = 'CreateDeployment result should not be null' ).

    DATA(lv_deployment_id) = lo_result->get_id( ).
    cl_abap_unit_assert=>assert_not_initial(
      act = lv_deployment_id
      msg = 'Deployment ID should not be empty' ).
  ENDMETHOD.

  METHOD delete_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " Create a new API specifically for deletion test
    DATA(lv_uuid) = /awsex/cl_utils=>get_random_string( ).
    DATA(lv_api_name) = 'test-delete-' && lv_uuid.

    DATA(lo_api) = lo_agw->createrestapi(
      iv_name = lv_api_name
      iv_description = 'Test API for deletion' ).
    DATA(lv_api_id) = lo_api->get_id( ).

    " Tag for cleanup
    DATA(lt_tags) = VALUE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring(
      ( VALUE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow(
          key = 'convert_test'
          value = NEW /aws1/cl_agwmapofstrtostr_w( 'true' ) ) ) ).
    lo_agw->tagresource(
      iv_resourcearn = 'arn:aws:apigateway:' && lo_session->get_region( ) &&
                      '::/restapis/' && lv_api_id
      it_tags = lt_tags ).

    " Now test deletion
    DATA(lo_actions) = NEW /awsex/cl_agw_actions( ).
    lo_actions->delete_rest_api( iv_rest_api_id = lv_api_id ).

    " Verify deletion by trying to get the API (should fail)
    TRY.
        lo_agw->getrestapi( iv_restapiid = lv_api_id ).
        cl_abap_unit_assert=>fail( msg = 'API should have been deleted' ).
      CATCH /aws1/cx_agwnotfoundexc00.
        " Expected - API was successfully deleted
    ENDTRY.
  ENDMETHOD.

ENDCLASS.
