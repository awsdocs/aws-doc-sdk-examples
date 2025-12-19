" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_emr_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS run_job_flow
      IMPORTING
        !iv_name               TYPE /aws1/emrxmlstringmaxlen256
        !iv_log_uri            TYPE /aws1/emrxmlstring
        !iv_keep_alive         TYPE /aws1/emrboolean
        !it_applications       TYPE /aws1/cl_emrapplication=>tt_applicationlist
        !iv_job_flow_role      TYPE /aws1/emrxmlstring
        !iv_service_role       TYPE /aws1/emrxmlstring
        !iv_master_sec_grp     TYPE /aws1/emrxmlstringmaxlen256
        !iv_slave_sec_grp      TYPE /aws1/emrxmlstringmaxlen256
        !it_steps              TYPE /aws1/cl_emrstepconfig=>tt_stepconfiglist
      RETURNING
        VALUE(ov_cluster_id)   TYPE /aws1/emrxmlstringmaxlen256
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_cluster
      IMPORTING
        !iv_cluster_id  TYPE /aws1/emrclusterid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_emrdescrclusteroutput
      RAISING
        /aws1/cx_rt_generic.

    METHODS terminate_job_flows
      IMPORTING
        !iv_cluster_id TYPE /aws1/emrclusterid
      RAISING
        /aws1/cx_rt_generic.

    METHODS add_job_flow_steps
      IMPORTING
        !iv_cluster_id  TYPE /aws1/emrclusterid
        !iv_name        TYPE /aws1/emrxmlstringmaxlen256
        !iv_script_uri  TYPE /aws1/emrxmlstring
        !it_script_args TYPE /aws1/cl_emrxmlstringlist_w=>tt_xmlstringlist
      RETURNING
        VALUE(ov_step_id) TYPE /aws1/emrstepid
      RAISING
        /aws1/cx_rt_generic.

    METHODS list_steps
      IMPORTING
        !iv_cluster_id  TYPE /aws1/emrclusterid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_emrliststepsoutput
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_step
      IMPORTING
        !iv_cluster_id  TYPE /aws1/emrclusterid
        !iv_step_id     TYPE /aws1/emrstepid
      RETURNING
        VALUE(oo_result) TYPE REF TO /aws1/cl_emrdescribestepoutput
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_EMR_ACTIONS IMPLEMENTATION.


  METHOD run_job_flow.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_emr) = /aws1/cl_emr_factory=>create( lo_session ).

    " snippet-start:[emr.abapv1.run_job_flow]
    TRY.
        " Create instances configuration
        DATA(lo_instances) = NEW /aws1/cl_emrjobflowinstsconfig(
          iv_masterinstancetype = 'm5.xlarge'
          iv_slaveinstancetype = 'm5.xlarge'
          iv_instancecount = 3
          iv_keepjobflowalivewhennos00 = iv_keep_alive
          iv_emrmanagedmastersecgroup = iv_master_sec_grp
          iv_emrmanagedslavesecgroup = iv_slave_sec_grp
        ).

        DATA(lo_result) = lo_emr->runjobflow(
          iv_name = iv_name
          iv_loguri = iv_log_uri
          iv_releaselabel = 'emr-5.30.1'
          io_instances = lo_instances
          it_steps = it_steps
          it_applications = it_applications
          iv_jobflowrole = iv_job_flow_role
          iv_servicerole = iv_service_role
          iv_ebsrootvolumesize = 10
          iv_visibletoallusers = abap_true
        ).

        ov_cluster_id = lo_result->get_jobflowid( ).
        MESSAGE 'EMR cluster created successfully.' TYPE 'I'.
      CATCH /aws1/cx_emrinternalservererr INTO DATA(lo_internal_error).
        DATA(lv_error) = lo_internal_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_emrclientexc INTO DATA(lo_client_error).
        lv_error = lo_client_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[emr.abapv1.run_job_flow]
  ENDMETHOD.


  METHOD describe_cluster.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_emr) = /aws1/cl_emr_factory=>create( lo_session ).

    " snippet-start:[emr.abapv1.describe_cluster]
    TRY.
        oo_result = lo_emr->describecluster(
          iv_clusterid = iv_cluster_id
        ).
        DATA(lo_cluster) = oo_result->get_cluster( ).
        DATA(lv_cluster_name) = lo_cluster->get_name( ).
        MESSAGE |Retrieved cluster information for { lv_cluster_name }| TYPE 'I'.
      CATCH /aws1/cx_emrinternalserverex INTO DATA(lo_internal_error).
        DATA(lv_error) = lo_internal_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_emrinvalidrequestex INTO DATA(lo_invalid_error).
        lv_error = lo_invalid_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[emr.abapv1.describe_cluster]
  ENDMETHOD.


  METHOD terminate_job_flows.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_emr) = /aws1/cl_emr_factory=>create( lo_session ).

    " snippet-start:[emr.abapv1.terminate_job_flows]
    TRY.
        DATA lt_cluster_ids TYPE /aws1/cl_emrxmlstringlist_w=>tt_xmlstringlist.
        APPEND NEW /aws1/cl_emrxmlstringlist_w( iv_cluster_id ) TO lt_cluster_ids.

        lo_emr->terminatejobflows(
          it_jobflowids = lt_cluster_ids
        ).
        MESSAGE 'EMR cluster terminated successfully.' TYPE 'I'.
      CATCH /aws1/cx_emrinternalservererr INTO DATA(lo_internal_error).
        DATA(lv_error) = lo_internal_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[emr.abapv1.terminate_job_flows]
  ENDMETHOD.


  METHOD add_job_flow_steps.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_emr) = /aws1/cl_emr_factory=>create( lo_session ).

    " snippet-start:[emr.abapv1.add_job_flow_steps]
    TRY.
        " Build args list for Spark submit
        DATA lt_args TYPE /aws1/cl_emrxmlstringlist_w=>tt_xmlstringlist.
        APPEND NEW /aws1/cl_emrxmlstringlist_w( 'spark-submit' ) TO lt_args.
        APPEND NEW /aws1/cl_emrxmlstringlist_w( '--deploy-mode' ) TO lt_args.
        APPEND NEW /aws1/cl_emrxmlstringlist_w( 'cluster' ) TO lt_args.
        APPEND NEW /aws1/cl_emrxmlstringlist_w( iv_script_uri ) TO lt_args.
        APPEND LINES OF it_script_args TO lt_args.

        " Create step configuration
        DATA(lo_hadoop_jar_step) = NEW /aws1/cl_emrhadoopjarstepcfg(
          iv_jar = 'command-runner.jar'
          it_args = lt_args
        ).

        DATA(lo_step_config) = NEW /aws1/cl_emrstepconfig(
          iv_name = iv_name
          iv_actiononfailure = 'CONTINUE'
          io_hadoopjarstep = lo_hadoop_jar_step
        ).

        DATA lt_steps TYPE /aws1/cl_emrstepconfig=>tt_stepconfiglist.
        APPEND lo_step_config TO lt_steps.

        DATA(lo_result) = lo_emr->addjobflowsteps(
          iv_jobflowid = iv_cluster_id
          it_steps = lt_steps
        ).

        " Get first step ID
        DATA(lt_step_ids) = lo_result->get_stepids( ).
        READ TABLE lt_step_ids INDEX 1 INTO DATA(lo_step_id_obj).
        IF sy-subrc = 0.
          ov_step_id = lo_step_id_obj->get_value( ).
          MESSAGE |Step added with ID { ov_step_id }| TYPE 'I'.
        ENDIF.
      CATCH /aws1/cx_emrinternalservererr INTO DATA(lo_internal_error).
        DATA(lv_error) = lo_internal_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[emr.abapv1.add_job_flow_steps]
  ENDMETHOD.


  METHOD list_steps.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_emr) = /aws1/cl_emr_factory=>create( lo_session ).

    " snippet-start:[emr.abapv1.list_steps]
    TRY.
        oo_result = lo_emr->liststeps(
          iv_clusterid = iv_cluster_id
        ).
        DATA(lt_steps) = oo_result->get_steps( ).
        DATA(lv_step_count) = lines( lt_steps ).
        MESSAGE |Retrieved { lv_step_count } steps for cluster| TYPE 'I'.
      CATCH /aws1/cx_emrinternalserverex INTO DATA(lo_internal_error).
        DATA(lv_error) = lo_internal_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_emrinvalidrequestex INTO DATA(lo_invalid_error).
        lv_error = lo_invalid_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[emr.abapv1.list_steps]
  ENDMETHOD.


  METHOD describe_step.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_emr) = /aws1/cl_emr_factory=>create( lo_session ).

    " snippet-start:[emr.abapv1.describe_step]
    TRY.
        oo_result = lo_emr->describestep(
          iv_clusterid = iv_cluster_id
          iv_stepid = iv_step_id
        ).
        DATA(lo_step) = oo_result->get_step( ).
        DATA(lv_step_name) = lo_step->get_name( ).
        MESSAGE |Retrieved step information for { lv_step_name }| TYPE 'I'.
      CATCH /aws1/cx_emrinternalserverex INTO DATA(lo_internal_error).
        DATA(lv_error) = lo_internal_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
      CATCH /aws1/cx_emrinvalidrequestex INTO DATA(lo_invalid_error).
        lv_error = lo_invalid_error->if_message~get_text( ).
        MESSAGE lv_error TYPE 'E'.
    ENDTRY.
    " snippet-end:[emr.abapv1.describe_step]
  ENDMETHOD.
ENDCLASS.
