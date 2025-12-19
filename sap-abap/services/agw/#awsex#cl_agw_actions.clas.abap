" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_agw_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
  PROTECTED SECTION.
  PRIVATE SECTION.

    METHODS create_rest_api
      IMPORTING
        VALUE(iv_api_name) TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)   TYPE REF TO /aws1/cl_agwrestapi
      RAISING
        /aws1/cx_rt_generic.

    METHODS add_rest_resource
      IMPORTING
        VALUE(iv_rest_api_id)   TYPE /aws1/agwstring
        VALUE(iv_parent_id)     TYPE /aws1/agwstring
        VALUE(iv_resource_path) TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)        TYPE REF TO /aws1/cl_agwresource
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_method
      IMPORTING
        VALUE(iv_rest_api_id) TYPE /aws1/agwstring
        VALUE(iv_resource_id) TYPE /aws1/agwstring
        VALUE(iv_http_method) TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)      TYPE REF TO /aws1/cl_agwmethod
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_method_response
      IMPORTING
        VALUE(iv_rest_api_id) TYPE /aws1/agwstring
        VALUE(iv_resource_id) TYPE /aws1/agwstring
        VALUE(iv_http_method) TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)      TYPE REF TO /aws1/cl_agwmethodresponse
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_integration
      IMPORTING
        VALUE(iv_rest_api_id)   TYPE /aws1/agwstring
        VALUE(iv_resource_id)   TYPE /aws1/agwstring
        VALUE(iv_http_method)   TYPE /aws1/agwstring
        VALUE(iv_integration_uri) TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)        TYPE REF TO /aws1/cl_agwintegration
      RAISING
        /aws1/cx_rt_generic.

    METHODS put_integration_response
      IMPORTING
        VALUE(iv_rest_api_id) TYPE /aws1/agwstring
        VALUE(iv_resource_id) TYPE /aws1/agwstring
        VALUE(iv_http_method) TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)      TYPE REF TO /aws1/cl_agwintegrationrsp
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_deployment
      IMPORTING
        VALUE(iv_rest_api_id) TYPE /aws1/agwstring
        VALUE(iv_stage_name)  TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)      TYPE REF TO /aws1/cl_agwdeployment
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_rest_apis
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_agwrestapis
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_resources
      IMPORTING
        VALUE(iv_rest_api_id) TYPE /aws1/agwstring
      RETURNING
        VALUE(oo_result)      TYPE REF TO /aws1/cl_agwresources
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_rest_api
      IMPORTING
        VALUE(iv_rest_api_id) TYPE /aws1/agwstring
      RAISING
        /aws1/cx_rt_generic.
ENDCLASS.



CLASS /AWSEX/CL_AGW_ACTIONS IMPLEMENTATION.


  METHOD create_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.create_rest_api]
    TRY.
        oo_result = lo_agw->createrestapi(
          iv_name = iv_api_name
          iv_description = 'Sample REST API created by ABAP SDK' ).
        DATA(lv_api_id) = oo_result->get_id( ).
        MESSAGE 'REST API created with ID: ' && lv_api_id TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
      CATCH /aws1/cx_agwunauthorizedex.
        MESSAGE 'Unauthorized - check credentials' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.create_rest_api]
  ENDMETHOD.


  METHOD add_rest_resource.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.add_rest_resource]
    TRY.
        oo_result = lo_agw->createresource(
          iv_restapiid = iv_rest_api_id
          iv_parentid = iv_parent_id
          iv_pathpart = iv_resource_path ).
        DATA(lv_resource_id) = oo_result->get_id( ).
        MESSAGE 'Resource created with ID: ' && lv_resource_id TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'REST API or parent resource not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.add_rest_resource]
  ENDMETHOD.


  METHOD put_method.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_method]
    TRY.
        oo_result = lo_agw->putmethod(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_authorizationtype = 'NONE' ).
        MESSAGE 'Method ' && iv_http_method && ' added to resource' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'Resource not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_method]
  ENDMETHOD.


  METHOD put_method_response.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_method_response]
    TRY.
        oo_result = lo_agw->putmethodresponse(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_statuscode = '200' ).
        MESSAGE 'Method response configured for status 200' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'Method not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_method_response]
  ENDMETHOD.


  METHOD put_integration.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_integration]
    TRY.
        oo_result = lo_agw->putintegration(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_type = 'AWS_PROXY'
          iv_integrationhttpmethod = 'POST'
          iv_uri = iv_integration_uri ).
        MESSAGE 'Integration configured for method' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'Method not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_integration]
  ENDMETHOD.


  METHOD put_integration_response.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.put_integration_response]
    TRY.
        oo_result = lo_agw->putintegrationresponse(
          iv_restapiid = iv_rest_api_id
          iv_resourceid = iv_resource_id
          iv_httpmethod = iv_http_method
          iv_statuscode = '200' ).
        MESSAGE 'Integration response configured for status 200' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'Integration not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.put_integration_response]
  ENDMETHOD.


  METHOD create_deployment.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.create_deployment]
    TRY.
        oo_result = lo_agw->createdeployment(
          iv_restapiid = iv_rest_api_id
          iv_stagename = iv_stage_name
          iv_description = 'Deployment created by ABAP SDK' ).
        DATA(lv_deployment_id) = oo_result->get_id( ).
        MESSAGE 'Deployment created with ID: ' && lv_deployment_id TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'REST API not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.create_deployment]
  ENDMETHOD.


  METHOD get_rest_apis.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.get_rest_apis]
    TRY.
        oo_result = lo_agw->getrestapis( ).
        DATA(lt_apis) = oo_result->get_items( ).
        DATA(lv_count) = lines( lt_apis ).
        MESSAGE 'Found ' && lv_count && ' REST APIs' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.get_rest_apis]
  ENDMETHOD.


  METHOD get_resources.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.get_resources]
    TRY.
        oo_result = lo_agw->getresources(
          iv_restapiid = iv_rest_api_id ).
        DATA(lt_resources) = oo_result->get_items( ).
        DATA(lv_count) = lines( lt_resources ).
        MESSAGE 'Found ' && lv_count && ' resources' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'REST API not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.get_resources]
  ENDMETHOD.


  METHOD delete_rest_api.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_agw) = /aws1/cl_agw_factory=>create( lo_session ).

    " snippet-start:[agw.abapv1.delete_rest_api]
    TRY.
        lo_agw->deleterestapi(
          iv_restapiid = iv_rest_api_id ).
        MESSAGE 'REST API deleted successfully' TYPE 'I'.
      CATCH /aws1/cx_agwbadrequestex.
        MESSAGE 'Bad request - invalid parameters' TYPE 'E'.
      CATCH /aws1/cx_agwnotfoundexception.
        MESSAGE 'REST API not found' TYPE 'E'.
      CATCH /aws1/cx_agwtoomanyrequestsex.
        MESSAGE 'Too many requests - rate limit exceeded' TYPE 'E'.
    ENDTRY.
    " snippet-end:[agw.abapv1.delete_rest_api]
  ENDMETHOD.
ENDCLASS.
