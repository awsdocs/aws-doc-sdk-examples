" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_agw_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_rest_api
      IMPORTING
        !iv_api_name     TYPE /aws1/agwstring
      EXPORTING
        !oo_result       TYPE REF TO /aws1/cl_agwrestapi
      RAISING
        /aws1/cx_rt_generic.

    METHODS add_rest_resource
      IMPORTING
        !iv_rest_api_id  TYPE /aws1/agwstring
        !iv_parent_id    TYPE /aws1/agwstring
        !iv_resource_path TYPE /aws1/agwstring
      EXPORTING
        !oo_result       TYPE REF TO /aws1/cl_agwresource
      RAISING
        /aws1/cx_rt_generic.

    METHODS add_integration_method
      IMPORTING
        !iv_rest_api_id          TYPE /aws1/agwstring
        !iv_resource_id          TYPE /aws1/agwstring
        !iv_rest_method          TYPE /aws1/agwstring
        !iv_service_endpt_prefix TYPE /aws1/agwstring
        !iv_service_action       TYPE /aws1/agwstring
        !iv_service_method       TYPE /aws1/agwstring
        !iv_role_arn             TYPE /aws1/agwstring
        !iv_mapping_template     TYPE /aws1/agwstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS deploy_api
      IMPORTING
        !iv_rest_api_id  TYPE /aws1/agwstring
        !iv_stage_name   TYPE /aws1/agwstring
      EXPORTING
        !oo_result       TYPE REF TO /aws1/cl_agwdeployment
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_rest_api_id
      IMPORTING
        !iv_api_name     TYPE /aws1/agwstring
      EXPORTING
        !ov_rest_api_id  TYPE /aws1/agwstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_rest_api
      IMPORTING
        !iv_rest_api_id  TYPE /aws1/agwstring
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_AGW_ACTIONS IMPLEMENTATION.


  METHOD create_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.create_rest_api]
    TRY.
        " iv_api_name = 'my-demo-api'
        oo_result = lo_agw->createrestapi( iv_name = iv_api_name ).
        MESSAGE 'REST API created.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - Invalid API configuration.' TYPE 'E'.
      CATCH /aws1/cx_agwlimitexceededex.
        MESSAGE 'Limit exceeded for REST APIs.' TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception.
        MESSAGE 'API with this name already exists.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.create_rest_api]
  ENDMETHOD.


  METHOD add_rest_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.add_rest_resource]
    TRY.
        " iv_rest_api_id = 'abc123xyz'
        " iv_parent_id = 'def456uvw'
        " iv_resource_path = 'users'
        oo_result = lo_agw->createresource(
          iv_restapiid = iv_rest_api_id
          iv_parentid  = iv_parent_id
          iv_pathpart  = iv_resource_path ).
        MESSAGE 'Resource created in REST API.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - Invalid resource configuration.' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'API or parent resource not found.' TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception.
        MESSAGE 'Resource already exists.' TYPE 'E'.
      CATCH /aws1/cx_agwlimitexceededex.
        MESSAGE 'Resource limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.add_rest_resource]
  ENDMETHOD.


  METHOD add_integration_method.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.add_integration_method]
    DATA lv_service_uri TYPE /aws1/agwstring.
    DATA lt_request_templates TYPE /aws1/cl_agwmapofstrtostr_w=>tt_mapofstringtostring.
    DATA ls_request_template TYPE /aws1/cl_agwmapofstrtostr_w=>ts_mapofstringtostring_maprow.

    TRY.
        " Create the HTTP method (e.g., GET, POST, etc.)
        " iv_rest_method = 'GET'
        lo_agw->putmethod(
          iv_restapiid        = iv_rest_api_id
          iv_resourceid       = iv_resource_id
          iv_httpmethod       = iv_rest_method
          iv_authorizationtype = 'NONE' ).

        " Create method response
        lo_agw->putmethodresponse(
          iv_restapiid  = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_rest_method
          iv_statuscode = '200' ).

        " Build the service URI
        " iv_service_endpt_prefix = 'dynamodb'
        " iv_service_action = 'Scan'
        lv_service_uri = |arn:aws:apigateway:{ lo_session->get_region( ) }:{ iv_service_endpt_prefix }:action/{ iv_service_action }|.

        " Create request templates map
        " iv_mapping_template = '{"TableName":"my-table"}'
        ls_request_template-key = 'application/json'.
        ls_request_template-value = NEW /aws1/cl_agwmapofstrtostr_w( iv_value = iv_mapping_template ).
        INSERT ls_request_template INTO TABLE lt_request_templates.

        " Create the integration
        " iv_service_method = 'POST'
        " iv_role_arn = 'arn:aws:iam::123456789012:role/APIGatewayDynamoDBRole'
        lo_agw->putintegration(
          iv_restapiid             = iv_rest_api_id
          iv_resourceid            = iv_resource_id
          iv_httpmethod            = iv_rest_method
          iv_type                  = 'AWS'
          iv_integrationhttpmethod = iv_service_method
          iv_credentials           = iv_role_arn
          it_requesttemplates      = lt_request_templates
          iv_uri                   = lv_service_uri
          iv_passthroughbehavior   = 'WHEN_NO_TEMPLATES' ).

        " Create integration response
        lo_agw->putintegrationresponse(
          iv_restapiid  = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_rest_method
          iv_statuscode = '200' ).

        MESSAGE 'Integration method added successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - Invalid integration configuration.' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'API or resource not found.' TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception.
        MESSAGE 'Method already exists.' TYPE 'E'.
      CATCH /aws1/cx_agwlimitexceededex.
        MESSAGE 'Method limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.add_integration_method]
  ENDMETHOD.



  METHOD deploy_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.deploy_api]
    TRY.
        " iv_rest_api_id = 'abc123xyz'
        " iv_stage_name = 'prod'
        oo_result = lo_agw->createdeployment(
          iv_restapiid = iv_rest_api_id
          iv_stagename = iv_stage_name ).
        MESSAGE 'API deployed successfully.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - Invalid deployment configuration.' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'API not found.' TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception.
        MESSAGE 'Deployment conflict.' TYPE 'E'.
      CATCH /aws1/cx_agwlimitexceededex.
        MESSAGE 'Deployment limit exceeded.' TYPE 'E'.
      CATCH /aws1/cx_agwserviceunavailex.
        MESSAGE 'Service unavailable.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.deploy_api]
  ENDMETHOD.


  METHOD get_rest_api_id.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.get_rest_api_id]
    DATA lo_apis TYPE REF TO /aws1/cl_agwrestapis.
    DATA lv_found TYPE abap_bool VALUE abap_false.

    TRY.
        " iv_api_name = 'my-demo-api'
        lo_apis = lo_agw->getrestapis( ).

        LOOP AT lo_apis->get_items( ) INTO DATA(lo_api).
          IF lo_api->get_name( ) = iv_api_name.
            ov_rest_api_id = lo_api->get_id( ).
            lv_found = abap_true.
            EXIT.
          ENDIF.
        ENDLOOP.

        IF lv_found = abap_true.
          MESSAGE 'Found REST API ID.' TYPE 'I'.
        ELSE.
          MESSAGE 'REST API not found.' TYPE 'E'.
        ENDIF.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request.' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'API not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.get_rest_api_id]
  ENDMETHOD.


  METHOD delete_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.delete_rest_api]
    TRY.
        " iv_rest_api_id = 'abc123xyz'
        lo_agw->deleterestapi( iv_restapiid = iv_rest_api_id ).
        MESSAGE 'REST API deleted.' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - Invalid API ID.' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'REST API not found.' TYPE 'E'.
      CATCH /aws1/cx_agwconflictexception.
        MESSAGE 'Conflict - Cannot delete API.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.delete_rest_api]
  ENDMETHOD.

ENDCLASS.
