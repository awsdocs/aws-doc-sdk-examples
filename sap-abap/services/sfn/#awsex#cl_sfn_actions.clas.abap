" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_sfn_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    " State Machine methods
    METHODS create_state_machine
      IMPORTING
        !iv_name       TYPE /aws1/sfnname
        !iv_definition TYPE /aws1/sfndefinition
        !iv_role_arn   TYPE /aws1/sfnarn
      RETURNING
        VALUE(ov_state_machine_arn) TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_state_machines
      IMPORTING
        !iv_name TYPE /aws1/sfnname
      RETURNING
        VALUE(ov_state_machine_arn) TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_state_machine
      IMPORTING
        !iv_state_machine_arn TYPE /aws1/sfnarn
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_sfndscstatemachineout
      RAISING
        /aws1/cx_rt_generic.

    METHODS start_execution
      IMPORTING
        !iv_state_machine_arn TYPE /aws1/sfnarn
        !iv_input             TYPE /aws1/sfnsensitivedata
      RETURNING
        VALUE(ov_execution_arn) TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_execution
      IMPORTING
        !iv_execution_arn TYPE /aws1/sfnarn
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_sfndescribeexecoutput
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_state_machine
      IMPORTING
        !iv_state_machine_arn TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

    " Activity methods
    METHODS create_activity
      IMPORTING
        !iv_name TYPE /aws1/sfnname
      RETURNING
        VALUE(ov_activity_arn) TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_activities
      IMPORTING
        !iv_name TYPE /aws1/sfnname
      RETURNING
        VALUE(ov_activity_arn) TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_activity_task
      IMPORTING
        !iv_activity_arn TYPE /aws1/sfnarn
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_sfngetactivitytaskout
      RAISING
        /aws1/cx_rt_generic.

    METHODS send_task_success
      IMPORTING
        !iv_task_token    TYPE /aws1/sfntasktoken
        !iv_task_response TYPE /aws1/sfnsensitivedata
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_activity
      IMPORTING
        !iv_activity_arn TYPE /aws1/sfnarn
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_SFN_ACTIONS IMPLEMENTATION.


  METHOD create_state_machine.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.create_state_machine]
    TRY.
        DATA(lo_result) = lo_sfn->createstatemachine(
          iv_name = iv_name
          iv_definition = iv_definition
          iv_rolearn = iv_role_arn
        ).
        ov_state_machine_arn = lo_result->get_statemachinearn( ).
        MESSAGE 'State machine created successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfnstatemachinealrex.
        MESSAGE 'State machine already exists.' TYPE 'E'.
      CATCH /aws1/cx_sfninvaliddefinition.
        MESSAGE 'Invalid state machine definition.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidname.
        MESSAGE 'Invalid state machine name.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidarn.
        MESSAGE 'Invalid role ARN.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.create_state_machine]
  ENDMETHOD.


  METHOD list_state_machines.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.list_state_machines]
    TRY.
        DATA(lo_result) = lo_sfn->liststatemachines( ).
        DATA(lt_state_machines) = lo_result->get_statemachines( ).
        LOOP AT lt_state_machines INTO DATA(lo_state_machine).
          IF lo_state_machine->get_name( ) = iv_name.
            ov_state_machine_arn = lo_state_machine->get_statemachinearn( ).
            EXIT.
          ENDIF.
        ENDLOOP.
        MESSAGE 'State machines listed successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfninvalidtoken.
        MESSAGE 'Invalid pagination token.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.list_state_machines]
  ENDMETHOD.


  METHOD describe_state_machine.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.describe_state_machine]
    TRY.
        oo_result = lo_sfn->describestatemachine(
          iv_statemachinearn = iv_state_machine_arn
        ).
        MESSAGE 'State machine described successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfnstatemachinedoes00.
        MESSAGE 'State machine does not exist.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidarn.
        MESSAGE 'Invalid state machine ARN.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.describe_state_machine]
  ENDMETHOD.


  METHOD start_execution.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.start_execution]
    TRY.
        DATA(lo_result) = lo_sfn->startexecution(
          iv_statemachinearn = iv_state_machine_arn
          iv_input = iv_input
        ).
        ov_execution_arn = lo_result->get_executionarn( ).
        MESSAGE 'Execution started successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfnstatemachinedoes00.
        MESSAGE 'State machine does not exist.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidarn.
        MESSAGE 'Invalid state machine ARN.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidexecinput.
        MESSAGE 'Invalid execution input.' TYPE 'E'.
      CATCH /aws1/cx_sfnexeclimitexceeded.
        MESSAGE 'Execution limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.start_execution]
  ENDMETHOD.


  METHOD describe_execution.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.describe_execution]
    TRY.
        oo_result = lo_sfn->describeexecution(
          iv_executionarn = iv_execution_arn
        ).
        MESSAGE 'Execution described successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfnexecdoesnotexist.
        MESSAGE 'Execution does not exist.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidarn.
        MESSAGE 'Invalid execution ARN.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.describe_execution]
  ENDMETHOD.


  METHOD delete_state_machine.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.delete_state_machine]
    TRY.
        lo_sfn->deletestatemachine(
          iv_statemachinearn = iv_state_machine_arn
        ).
        MESSAGE 'State machine deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfninvalidarn.
        MESSAGE 'Invalid state machine ARN.' TYPE 'E'.
      CATCH /aws1/cx_sfnvalidationex.
        MESSAGE 'Validation error occurred.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.delete_state_machine]
  ENDMETHOD.


  METHOD create_activity.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.create_activity]
    TRY.
        DATA(lo_result) = lo_sfn->createactivity(
          iv_name = iv_name
        ).
        ov_activity_arn = lo_result->get_activityarn( ).
        MESSAGE 'Activity created successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfnactivityalrdyex.
        MESSAGE 'Activity already exists.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidname.
        MESSAGE 'Invalid activity name.' TYPE 'E'.
      CATCH /aws1/cx_sfnactivitylimitexcd.
        MESSAGE 'Activity limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.create_activity]
  ENDMETHOD.


  METHOD list_activities.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.list_activities]
    TRY.
        DATA(lo_result) = lo_sfn->listactivities( ).
        DATA(lt_activities) = lo_result->get_activities( ).
        LOOP AT lt_activities INTO DATA(lo_activity).
          IF lo_activity->get_name( ) = iv_name.
            ov_activity_arn = lo_activity->get_activityarn( ).
            EXIT.
          ENDIF.
        ENDLOOP.
        MESSAGE 'Activities listed successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfninvalidtoken.
        MESSAGE 'Invalid pagination token.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.list_activities]
  ENDMETHOD.


  METHOD get_activity_task.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.get_activity_task]
    TRY.
        oo_result = lo_sfn->getactivitytask(
          iv_activityarn = iv_activity_arn
        ).
        MESSAGE 'Activity task retrieved successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfnactivitydoesnotex.
        MESSAGE 'Activity does not exist.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidarn.
        MESSAGE 'Invalid activity ARN.' TYPE 'E'.
      CATCH /aws1/cx_sfnactivityworkerlm00.
        MESSAGE 'Activity worker limit exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.get_activity_task]
  ENDMETHOD.


  METHOD send_task_success.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.send_task_success]
    TRY.
        lo_sfn->sendtasksuccess(
          iv_tasktoken = iv_task_token
          iv_output = iv_task_response
        ).
        MESSAGE 'Task success sent successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfninvalidtoken.
        MESSAGE 'Invalid task token.' TYPE 'E'.
      CATCH /aws1/cx_sfntaskdoesnotexist.
        MESSAGE 'Task does not exist.' TYPE 'E'.
      CATCH /aws1/cx_sfninvalidoutput.
        MESSAGE 'Invalid task output.' TYPE 'E'.
      CATCH /aws1/cx_sfntasktimedout.
        MESSAGE 'Task timed out.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.send_task_success]
  ENDMETHOD.


  METHOD delete_activity.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_sfn) = /aws1/cl_sfn_factory=>create( lo_session ).

    " snippet-start:[sfn.abapv1.delete_activity]
    TRY.
        lo_sfn->deleteactivity(
          iv_activityarn = iv_activity_arn
        ).
        MESSAGE 'Activity deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_sfninvalidarn.
        MESSAGE 'Invalid activity ARN.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[sfn.abapv1.delete_activity]
  ENDMETHOD.
ENDCLASS.
