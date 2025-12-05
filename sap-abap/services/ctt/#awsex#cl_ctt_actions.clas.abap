" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

CLASS /awsex/cl_ctt_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS list_baselines
      IMPORTING
        !io_ctt              TYPE REF TO /aws1/if_ctt
      RETURNING
        VALUE(ot_baselines) TYPE /aws1/cl_cttbaselinesummary=>tt_baselines
      RAISING
        /aws1/cx_rt_generic .

    METHODS enable_baseline
      IMPORTING
        !io_ctt                        TYPE REF TO /aws1/if_ctt
        !iv_target_identifier          TYPE /aws1/cttarn
        !iv_identity_center_baseline   TYPE /aws1/cttstring OPTIONAL
        !iv_baseline_identifier        TYPE /aws1/cttarn
        !iv_baseline_version           TYPE /aws1/cttbaselineversion
      RETURNING
        VALUE(ov_enabled_baseline_arn) TYPE /aws1/cttarn
      RAISING
        /aws1/cx_rt_generic .

    METHODS list_controls
      IMPORTING
        !io_ccg            TYPE REF TO /aws1/if_ccg
      RETURNING
        VALUE(ot_controls) TYPE /aws1/cl_ccgcontrolsummary=>tt_controls
      RAISING
        /aws1/cx_rt_generic .

    METHODS enable_control
      IMPORTING
        !io_ctt                   TYPE REF TO /aws1/if_ctt
        !iv_control_arn           TYPE /aws1/cttcontrolidentifier
        !iv_target_identifier     TYPE /aws1/ctttargetidentifier
      RETURNING
        VALUE(ov_operation_id)   TYPE /aws1/cttoperationidentifier
      RAISING
        /aws1/cx_rt_generic .

    METHODS get_control_operation
      IMPORTING
        !io_ctt                 TYPE REF TO /aws1/if_ctt
        !iv_operation_id        TYPE /aws1/cttoperationidentifier
      RETURNING
        VALUE(ov_status)       TYPE /aws1/cttcontrolopstatus
      RAISING
        /aws1/cx_rt_generic .

    METHODS get_baseline_operation
      IMPORTING
        !io_ctt                 TYPE REF TO /aws1/if_ctt
        !iv_operation_id        TYPE /aws1/cttoperationidentifier
      RETURNING
        VALUE(ov_status)       TYPE /aws1/cttbaselineopstatus
      RAISING
        /aws1/cx_rt_generic .

    METHODS disable_control
      IMPORTING
        !io_ctt                   TYPE REF TO /aws1/if_ctt
        !iv_control_arn           TYPE /aws1/cttcontrolidentifier
        !iv_target_identifier     TYPE /aws1/ctttargetidentifier
      RETURNING
        VALUE(ov_operation_id)   TYPE /aws1/cttoperationidentifier
      RAISING
        /aws1/cx_rt_generic .

    METHODS list_landing_zones
      IMPORTING
        !io_ctt                  TYPE REF TO /aws1/if_ctt
      RETURNING
        VALUE(ot_landing_zones)  TYPE /aws1/cl_cttlandingzonesummary=>tt_landingzonesummaries
      RAISING
        /aws1/cx_rt_generic .

    METHODS list_enabled_baselines
      IMPORTING
        !io_ctt                       TYPE REF TO /aws1/if_ctt
      RETURNING
        VALUE(ot_enabled_baselines)   TYPE /aws1/cl_cttenbdbaselinesumm=>tt_enabledbaselines
      RAISING
        /aws1/cx_rt_generic .

    METHODS reset_enabled_baseline
      IMPORTING
        !io_ctt                            TYPE REF TO /aws1/if_ctt
        !iv_enabled_baseline_identifier    TYPE /aws1/cttarn
      RETURNING
        VALUE(ov_operation_id)            TYPE /aws1/cttoperationidentifier
      RAISING
        /aws1/cx_rt_generic .

    METHODS disable_baseline
      IMPORTING
        !io_ctt                            TYPE REF TO /aws1/if_ctt
        !iv_enabled_baseline_identifier    TYPE /aws1/cttarn
      RETURNING
        VALUE(ov_operation_id)            TYPE /aws1/cttoperationidentifier
      RAISING
        /aws1/cx_rt_generic .

    METHODS list_enabled_controls
      IMPORTING
        !io_ctt                      TYPE REF TO /aws1/if_ctt
        !iv_target_identifier        TYPE /aws1/ctttargetidentifier
      RETURNING
        VALUE(ot_enabled_controls)   TYPE /aws1/cl_cttenabledcontrolsumm=>tt_enabledcontrols
      RAISING
        /aws1/cx_rt_generic .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_CTT_ACTIONS IMPLEMENTATION.


  METHOD list_baselines.

    " snippet-start:[ctt.abapv1.list_baselines]
    TRY.
        DATA lt_baselines TYPE /aws1/cl_cttbaselinesummary=>tt_baselines.
        DATA lv_nexttoken TYPE /aws1/cttstring.

        " List all baselines using pagination
        DO.
          DATA(lo_output) = io_ctt->listbaselines(
            iv_nexttoken = lv_nexttoken
          ).

          APPEND LINES OF lo_output->get_baselines( ) TO lt_baselines.

          lv_nexttoken = lo_output->get_nexttoken( ).
          IF lv_nexttoken IS INITIAL.
            EXIT.
          ENDIF.
        ENDDO.

        ot_baselines = lt_baselines.
        MESSAGE 'Listed baselines successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttaccessdeniedex INTO DATA(lo_access_denied).
        DATA(lv_error) = |Access denied: { lo_access_denied->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.list_baselines]

  ENDMETHOD.


  METHOD enable_baseline.

    " snippet-start:[ctt.abapv1.enable_baseline]
    TRY.
        " Prepare parameters for enabling baseline
        DATA lt_parameters TYPE /aws1/cl_cttenbdbaselineparam=>tt_enabledbaselineparameters.

        " Add Identity Center baseline parameter if provided
        IF iv_identity_center_baseline IS NOT INITIAL.
          DATA(lo_param) = NEW /aws1/cl_cttenbdbaselineparam(
            iv_key = 'IdentityCenterEnabledBaselineArn'
            iv_value = iv_identity_center_baseline
          ).
          APPEND lo_param TO lt_parameters.
        ENDIF.

        " Enable the baseline
        DATA(lo_output) = io_ctt->enablebaseline(
          iv_baselineidentifier = iv_baseline_identifier
          iv_baselineversion    = iv_baseline_version
          iv_targetidentifier   = iv_target_identifier
          it_parameters         = lt_parameters
        ).

        DATA(lv_operation_id) = lo_output->get_operationidentifier( ).

        " Wait for operation to complete
        DATA lv_status TYPE /aws1/cttbaselineopstatus.
        DO 100 TIMES.
          lv_status = get_baseline_operation(
            io_ctt = io_ctt
            iv_operation_id = lv_operation_id
          ).

          DATA(lv_msg) = |Baseline operation status: { lv_status }|.
          MESSAGE lv_msg TYPE 'I'.

          IF lv_status = 'SUCCEEDED' OR lv_status = 'FAILED'.
            EXIT.
          ENDIF.

          " Wait 30 seconds
          WAIT UP TO 30 SECONDS.
        ENDDO.

        ov_enabled_baseline_arn = lo_output->get_arn( ).
        MESSAGE 'Baseline enabled successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttvalidationex INTO DATA(lo_validation).
        DATA(lv_error) = lo_validation->get_text( ).
        IF lv_error CS 'already enabled'.
          MESSAGE 'Baseline is already enabled for this target.' TYPE 'I'.
        ELSE.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.enable_baseline]

  ENDMETHOD.


  METHOD list_controls.

    " snippet-start:[ctt.abapv1.list_controls]
    TRY.
        DATA lt_controls TYPE /aws1/cl_ccgcontrolsummary=>tt_controls.
        DATA lv_nexttoken TYPE /aws1/ccgpaginationtoken.

        " List all controls using pagination
        DO.
          DATA(lo_output) = io_ccg->listcontrols(
            iv_nexttoken = lv_nexttoken
          ).

          APPEND LINES OF lo_output->get_controls( ) TO lt_controls.

          lv_nexttoken = lo_output->get_nexttoken( ).
          IF lv_nexttoken IS INITIAL.
            EXIT.
          ENDIF.
        ENDDO.

        ot_controls = lt_controls.
        MESSAGE 'Listed controls successfully.' TYPE 'I'.
      CATCH /aws1/cx_ccgaccessdeniedex INTO DATA(lo_access_denied).
        DATA(lv_error) = |Access denied: { lo_access_denied->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.list_controls]

  ENDMETHOD.


  METHOD enable_control.

    " snippet-start:[ctt.abapv1.enable_control]
    TRY.
        " Enable the control
        DATA(lo_output) = io_ctt->enablecontrol(
          iv_controlidentifier = iv_control_arn
          iv_targetidentifier  = iv_target_identifier
        ).

        DATA(lv_operation_id) = lo_output->get_operationidentifier( ).

        " Wait for operation to complete
        DATA lv_status TYPE /aws1/cttcontrolopstatus.
        DO 100 TIMES.
          lv_status = get_control_operation(
            io_ctt = io_ctt
            iv_operation_id = lv_operation_id
          ).

          DATA(lv_msg) = |Control operation status: { lv_status }|.
          MESSAGE lv_msg TYPE 'I'.

          IF lv_status = 'SUCCEEDED' OR lv_status = 'FAILED'.
            EXIT.
          ENDIF.

          " Wait 30 seconds
          WAIT UP TO 30 SECONDS.
        ENDDO.

        ov_operation_id = lv_operation_id.
        MESSAGE 'Control enabled successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttvalidationex INTO DATA(lo_validation).
        DATA(lv_error) = lo_validation->get_text( ).
        IF lv_error CS 'already enabled'.
          MESSAGE 'Control is already enabled for this target.' TYPE 'I'.
        ELSE.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_notfound).
        lv_error = lo_notfound->get_text( ).
        IF lv_error CS 'not registered with AWS Control Tower'.
          MESSAGE 'Control Tower must be enabled to work with controls.' TYPE 'E'.
        ELSE.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.enable_control]

  ENDMETHOD.


  METHOD get_control_operation.

    " snippet-start:[ctt.abapv1.get_control_operation]
    TRY.
        DATA(lo_output) = io_ctt->getcontroloperation(
          iv_operationidentifier = iv_operation_id
        ).

        ov_status = lo_output->get_controloperation( )->get_status( ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_notfound).
        DATA(lv_error) = |Operation not found: { lo_notfound->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.get_control_operation]

  ENDMETHOD.


  METHOD get_baseline_operation.

    " snippet-start:[ctt.abapv1.get_baseline_operation]
    TRY.
        DATA(lo_output) = io_ctt->getbaselineoperation(
          iv_operationidentifier = iv_operation_id
        ).

        ov_status = lo_output->get_baselineoperation( )->get_status( ).
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_notfound).
        DATA(lv_error) = |Operation not found: { lo_notfound->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.get_baseline_operation]

  ENDMETHOD.


  METHOD disable_control.

    " snippet-start:[ctt.abapv1.disable_control]
    TRY.
        " Disable the control
        DATA(lo_output) = io_ctt->disablecontrol(
          iv_controlidentifier = iv_control_arn
          iv_targetidentifier  = iv_target_identifier
        ).

        DATA(lv_operation_id) = lo_output->get_operationidentifier( ).

        " Wait for operation to complete
        DATA lv_status TYPE /aws1/cttcontrolopstatus.
        DO 100 TIMES.
          lv_status = get_control_operation(
            io_ctt = io_ctt
            iv_operation_id = lv_operation_id
          ).

          DATA(lv_msg) = |Control operation status: { lv_status }|.
          MESSAGE lv_msg TYPE 'I'.

          IF lv_status = 'SUCCEEDED' OR lv_status = 'FAILED'.
            EXIT.
          ENDIF.

          " Wait 30 seconds
          WAIT UP TO 30 SECONDS.
        ENDDO.

        ov_operation_id = lv_operation_id.
        MESSAGE 'Control disabled successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_notfound).
        DATA(lv_error) = |Control not found: { lo_notfound->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.disable_control]

  ENDMETHOD.


  METHOD list_landing_zones.

    " snippet-start:[ctt.abapv1.list_landing_zones]
    TRY.
        DATA lt_landing_zones TYPE /aws1/cl_cttlandingzonesummary=>tt_landingzonesummaries.
        DATA lv_nexttoken TYPE /aws1/cttstring.

        " List all landing zones using pagination
        DO.
          DATA(lo_output) = io_ctt->listlandingzones(
            iv_nexttoken = lv_nexttoken
          ).

          APPEND LINES OF lo_output->get_landingzones( ) TO lt_landing_zones.

          lv_nexttoken = lo_output->get_nexttoken( ).
          IF lv_nexttoken IS INITIAL.
            EXIT.
          ENDIF.
        ENDDO.

        ot_landing_zones = lt_landing_zones.
        MESSAGE 'Listed landing zones successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttaccessdeniedex INTO DATA(lo_access_denied).
        DATA(lv_error) = |Access denied: { lo_access_denied->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.list_landing_zones]

  ENDMETHOD.


  METHOD list_enabled_baselines.

    " snippet-start:[ctt.abapv1.list_enabled_baselines]
    TRY.
        DATA lt_enabled_baselines TYPE /aws1/cl_cttenbdbaselinesumm=>tt_enabledbaselines.
        DATA lv_nexttoken TYPE /aws1/cttlstenbdbaselinesnex00.

        " List all enabled baselines using pagination
        DO.
          DATA(lo_output) = io_ctt->listenabledbaselines(
            iv_nexttoken = lv_nexttoken
          ).

          APPEND LINES OF lo_output->get_enabledbaselines( ) TO lt_enabled_baselines.

          lv_nexttoken = lo_output->get_nexttoken( ).
          IF lv_nexttoken IS INITIAL.
            EXIT.
          ENDIF.
        ENDDO.

        ot_enabled_baselines = lt_enabled_baselines.
        MESSAGE 'Listed enabled baselines successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_notfound).
        DATA(lv_error) = |Target not found: { lo_notfound->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.list_enabled_baselines]

  ENDMETHOD.


  METHOD reset_enabled_baseline.

    " snippet-start:[ctt.abapv1.reset_enabled_baseline]
    TRY.
        " Reset the enabled baseline
        DATA(lo_output) = io_ctt->resetenabledbaseline(
          iv_enabledbaselineidentifier = iv_enabled_baseline_identifier
        ).

        DATA(lv_operation_id) = lo_output->get_operationidentifier( ).

        " Wait for operation to complete
        DATA lv_status TYPE /aws1/cttbaselineopstatus.
        DO 100 TIMES.
          lv_status = get_baseline_operation(
            io_ctt = io_ctt
            iv_operation_id = lv_operation_id
          ).

          DATA(lv_msg) = |Baseline operation status: { lv_status }|.
          MESSAGE lv_msg TYPE 'I'.

          IF lv_status = 'SUCCEEDED' OR lv_status = 'FAILED'.
            EXIT.
          ENDIF.

          " Wait 30 seconds
          WAIT UP TO 30 SECONDS.
        ENDDO.

        ov_operation_id = lv_operation_id.
        MESSAGE 'Baseline reset successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_notfound).
        DATA(lv_error) = |Target not found: { lo_notfound->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.reset_enabled_baseline]

  ENDMETHOD.


  METHOD disable_baseline.

    " snippet-start:[ctt.abapv1.disable_baseline]
    TRY.
        " Disable the baseline
        DATA(lo_output) = io_ctt->disablebaseline(
          iv_enabledbaselineidentifier = iv_enabled_baseline_identifier
        ).

        DATA(lv_operation_id) = lo_output->get_operationidentifier( ).

        " Wait for operation to complete
        DATA lv_status TYPE /aws1/cttbaselineopstatus.
        DO 100 TIMES.
          lv_status = get_baseline_operation(
            io_ctt = io_ctt
            iv_operation_id = lv_operation_id
          ).

          DATA(lv_msg) = |Baseline operation status: { lv_status }|.
          MESSAGE lv_msg TYPE 'I'.

          IF lv_status = 'SUCCEEDED' OR lv_status = 'FAILED'.
            EXIT.
          ENDIF.

          " Wait 30 seconds
          WAIT UP TO 30 SECONDS.
        ENDDO.

        ov_operation_id = lv_operation_id.
        MESSAGE 'Baseline disabled successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttconflictexception INTO DATA(lo_conflict).
        DATA(lv_error) = |Conflict disabling baseline: { lo_conflict->get_text( ) }. Skipping disable step.|.
        MESSAGE lv_error TYPE 'I'.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.disable_baseline]

  ENDMETHOD.


  METHOD list_enabled_controls.

    " snippet-start:[ctt.abapv1.list_enabled_controls]
    TRY.
        DATA lt_enabled_controls TYPE /aws1/cl_cttenabledcontrolsumm=>tt_enabledcontrols.
        DATA lv_nexttoken TYPE /aws1/cttstring.

        " List all enabled controls using pagination
        DO.
          DATA(lo_output) = io_ctt->listenabledcontrols(
            iv_targetidentifier = iv_target_identifier
            iv_nexttoken        = lv_nexttoken
          ).

          APPEND LINES OF lo_output->get_enabledcontrols( ) TO lt_enabled_controls.

          lv_nexttoken = lo_output->get_nexttoken( ).
          IF lv_nexttoken IS INITIAL.
            EXIT.
          ENDIF.
        ENDDO.

        ot_enabled_controls = lt_enabled_controls.
        MESSAGE 'Listed enabled controls successfully.' TYPE 'I'.
      CATCH /aws1/cx_cttaccessdeniedex INTO DATA(lo_access_denied).
        DATA(lv_error) = |Access denied: { lo_access_denied->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_cttresourcenotfoundex INTO DATA(lo_notfound).
        lv_error = lo_notfound->get_text( ).
        IF lv_error CS 'not registered with AWS Control Tower'.
          MESSAGE 'Control Tower must be enabled to work with controls.' TYPE 'E'.
        ELSE.
          MESSAGE lv_error TYPE 'E'.
        ENDIF.
      CATCH /aws1/cx_rt_generic INTO DATA(lo_exception).
        lv_error = |An exception occurred: { lo_exception->get_text( ) }|.
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[ctt.abapv1.list_enabled_controls]

  ENDMETHOD.
ENDCLASS.
