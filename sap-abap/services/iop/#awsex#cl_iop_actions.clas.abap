" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_iop_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    " Updates the shadow for an AWS IoT thing
    " @parameter iv_thing_name | The name of the thing (e.g., 'MyIoTDevice')
    " @parameter iv_shadow_state | The shadow state as JSON string
    " @raising /aws1/cx_rt_generic | Thrown when operation fails
    METHODS update_thing_shadow
      IMPORTING
        !iv_thing_name      TYPE /aws1/iopthingname
        !iv_shadow_state    TYPE string
      RAISING
        /aws1/cx_rt_generic.

    " Gets the shadow for an AWS IoT thing
    " @parameter iv_thing_name | The name of the thing (e.g., 'MyIoTDevice')
    " @parameter ov_shadow | The shadow state as JSON string
    " @raising /aws1/cx_rt_generic | Thrown when operation fails
    METHODS get_thing_shadow
      IMPORTING
        !iv_thing_name    TYPE /aws1/iopthingname
      RETURNING
        VALUE(ov_shadow)  TYPE string
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_iop_actions IMPLEMENTATION.

  METHOD update_thing_shadow.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iop) = /aws1/cl_iop_factory=>create( lo_session ).

    " snippet-start:[iot.abapv1.update_thing_shadow]
    TRY.
        " Convert JSON string to xstring for payload
        DATA(lv_payload) = /aws1/cl_rt_util=>string_to_xstring( iv_shadow_state ).

        lo_iop->updatethingshadow(
          iv_thingname = iv_thing_name
          iv_payload = lv_payload ).
        MESSAGE 'Thing shadow updated successfully.' TYPE 'I'.
      CATCH /aws1/cx_iopresourcenotfoundex.
        MESSAGE 'Thing not found.' TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        DATA(lv_error) = |{ lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[iot.abapv1.update_thing_shadow]
  ENDMETHOD.

  METHOD get_thing_shadow.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_iop) = /aws1/cl_iop_factory=>create( lo_session ).

    " snippet-start:[iot.abapv1.get_thing_shadow]
    TRY.
        DATA(lo_result) = lo_iop->getthingshadow( iv_thingname = iv_thing_name ).

        " Convert xstring payload to JSON string
        DATA(lv_payload) = lo_result->get_payload( ).
        ov_shadow = /aws1/cl_rt_util=>xstring_to_string( lv_payload ).
        MESSAGE 'Thing shadow retrieved successfully.' TYPE 'I'.
      CATCH /aws1/cx_iopresourcenotfoundex.
        MESSAGE 'Thing shadow not found.' TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        DATA(lv_error) = |{ lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[iot.abapv1.get_thing_shadow]
  ENDMETHOD.

ENDCLASS.
