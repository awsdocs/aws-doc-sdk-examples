" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_rds_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    " Get a DB parameter group
    METHODS describe_db_parameter_groups
      IMPORTING
        !iv_dbparametergroupname TYPE /aws1/rdsstring
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdsdbparamgroupsmsg
      RAISING
        /aws1/cx_rt_generic.

    " Create a DB parameter group
    METHODS create_db_parameter_group
      IMPORTING
        !iv_dbparametergroupname   TYPE /aws1/rdsstring
        !iv_dbparametergroupfamily TYPE /aws1/rdsstring
        !iv_description            TYPE /aws1/rdsstring
      EXPORTING
        !oo_result                 TYPE REF TO /aws1/cl_rdscredbparamgrprslt
      RAISING
        /aws1/cx_rt_generic.

    " Delete a DB parameter group
    METHODS delete_db_parameter_group
      IMPORTING
        !iv_dbparametergroupname TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    " Get parameters in a DB parameter group
    METHODS describe_db_parameters
      IMPORTING
        !iv_dbparametergroupname TYPE /aws1/rdsstring
        !iv_source               TYPE /aws1/rdsstring OPTIONAL
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdsdbparamgroupdets
      RAISING
        /aws1/cx_rt_generic.

    " Update parameters in a DB parameter group
    METHODS modify_db_parameter_group
      IMPORTING
        !iv_dbparametergroupname TYPE /aws1/rdsstring
        !it_parameters           TYPE /aws1/cl_rdsparameter=>tt_parameterslist
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdsdbparamgrpnamemsg
      RAISING
        /aws1/cx_rt_generic.

    " Create a DB snapshot
    METHODS create_db_snapshot
      IMPORTING
        !iv_dbsnapshotidentifier TYPE /aws1/rdsstring
        !iv_dbinstanceidentifier TYPE /aws1/rdsstring
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdscreatedbsnapresult
      RAISING
        /aws1/cx_rt_generic.

    " Get a DB snapshot
    METHODS describe_db_snapshots
      IMPORTING
        !iv_dbsnapshotidentifier TYPE /aws1/rdsstring
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdsdbsnapshotmessage
      RAISING
        /aws1/cx_rt_generic.

    " Get database engine versions
    METHODS describe_db_engine_versions
      IMPORTING
        !iv_engine                 TYPE /aws1/rdsstring
        !iv_dbparametergroupfamily TYPE /aws1/rdsstring OPTIONAL
      EXPORTING
        !oo_result                 TYPE REF TO /aws1/cl_rdsdbenginevrsmessage
      RAISING
        /aws1/cx_rt_generic.

    " Get orderable DB instance options
    METHODS descrorderabledbinstopts
      IMPORTING
        !iv_engine        TYPE /aws1/rdsstring
        !iv_engineversion TYPE /aws1/rdsstring
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_rdsorderabledbinsto00
      RAISING
        /aws1/cx_rt_generic.

    " Get a DB instance
    METHODS describe_db_instances
      IMPORTING
        !iv_dbinstanceidentifier TYPE /aws1/rdsstring
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdsdbinstancemessage
      RAISING
        /aws1/cx_rt_generic.

    " Create a DB instance
    METHODS create_db_instance
      IMPORTING
        !iv_dbname               TYPE /aws1/rdsstring
        !iv_dbinstanceidentifier TYPE /aws1/rdsstring
        !iv_dbparametergroupname TYPE /aws1/rdsstring
        !iv_engine               TYPE /aws1/rdsstring
        !iv_engineversion        TYPE /aws1/rdsstring
        !iv_dbinstanceclass      TYPE /aws1/rdsstring
        !iv_storagetype          TYPE /aws1/rdsstring
        !iv_allocatedstorage     TYPE /aws1/rdsintegeroptional
        !iv_masterusername       TYPE /aws1/rdsstring
        !iv_masteruserpassword   TYPE /aws1/rdssensitivestring
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdscreatedbinstresult
      RAISING
        /aws1/cx_rt_generic.

    " Delete a DB instance
    METHODS delete_db_instance
      IMPORTING
        !iv_dbinstanceidentifier TYPE /aws1/rdsstring
      EXPORTING
        !oo_result               TYPE REF TO /aws1/cl_rdsdeletedbinstresult
      RAISING
        /aws1/cx_rt_generic.
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_RDS_ACTIONS IMPLEMENTATION.


  METHOD describe_db_parameter_groups.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_db_parameter_groups]
    TRY.
        oo_result = lo_rds->describedbparametergroups(
          iv_dbparametergroupname = iv_dbparametergroupname ).
        MESSAGE 'DB parameter group retrieved.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_parameter_groups]
  ENDMETHOD.


  METHOD create_db_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_db_parameter_group]
    TRY.
        oo_result = lo_rds->createdbparametergroup(
          iv_dbparametergroupname   = iv_dbparametergroupname
          iv_dbparametergroupfamily = iv_dbparametergroupfamily
          iv_description            = iv_description ).
        MESSAGE 'DB parameter group created.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbparmgralrexfault.
        MESSAGE 'DB parameter group already exists.' TYPE 'E'.
      CATCH /aws1/cx_rdsdbprmgrquotaexcd00.
        MESSAGE 'DB parameter group quota exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_db_parameter_group]
  ENDMETHOD.


  METHOD delete_db_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.delete_db_parameter_group]
    TRY.
        lo_rds->deletedbparametergroup(
          iv_dbparametergroupname = iv_dbparametergroupname ).
        MESSAGE 'DB parameter group deleted.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        MESSAGE 'DB parameter group is in an invalid state.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.delete_db_parameter_group]
  ENDMETHOD.


  METHOD describe_db_parameters.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_db_parameters]
    TRY.
        oo_result = lo_rds->describedbparameters(
          iv_dbparametergroupname = iv_dbparametergroupname
          iv_source               = iv_source ).
        DATA(lv_param_count) = lines( oo_result->get_parameters( ) ).
        MESSAGE |Retrieved { lv_param_count } parameters.| TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_parameters]
  ENDMETHOD.


  METHOD modify_db_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.modify_db_parameter_group]
    TRY.
        oo_result = lo_rds->modifydbparametergroup(
          iv_dbparametergroupname = iv_dbparametergroupname
          it_parameters           = it_parameters ).
        MESSAGE 'DB parameter group modified.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        MESSAGE 'DB parameter group is in an invalid state.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.modify_db_parameter_group]
  ENDMETHOD.


  METHOD create_db_snapshot.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_db_snapshot]
    TRY.
        oo_result = lo_rds->createdbsnapshot(
          iv_dbsnapshotidentifier = iv_dbsnapshotidentifier
          iv_dbinstanceidentifier = iv_dbinstanceidentifier ).
        MESSAGE 'DB snapshot created.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbinstnotfndfault.
        MESSAGE 'DB instance not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsdbsnapalrdyexfault.
        MESSAGE 'DB snapshot already exists.' TYPE 'E'.
      CATCH /aws1/cx_rdsinvdbinststatefa00.
        MESSAGE 'DB instance is in an invalid state.' TYPE 'E'.
      CATCH /aws1/cx_rdssnapquotaexcdfault.
        MESSAGE 'Snapshot quota exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_db_snapshot]
  ENDMETHOD.


  METHOD describe_db_snapshots.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_db_snapshots]
    TRY.
        oo_result = lo_rds->describedbsnapshots(
          iv_dbsnapshotidentifier = iv_dbsnapshotidentifier ).
        MESSAGE 'DB snapshot retrieved.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbsnapnotfndfault.
        MESSAGE 'DB snapshot not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_snapshots]
  ENDMETHOD.


  METHOD describe_db_engine_versions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_db_engine_versions]
    TRY.
        oo_result = lo_rds->describedbengineversions(
          iv_engine                 = iv_engine
          iv_dbparametergroupfamily = iv_dbparametergroupfamily ).
        DATA(lv_version_count) = lines( oo_result->get_dbengineversions( ) ).
        MESSAGE |Retrieved { lv_version_count } engine versions.| TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_engine_versions]
  ENDMETHOD.


  METHOD descrorderabledbinstopts.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_orderable_db_instance_options]
    TRY.
        oo_result = lo_rds->descrorderabledbinstoptions(
          iv_engine        = iv_engine
          iv_engineversion = iv_engineversion ).
        DATA(lv_option_count) = lines( oo_result->get_orderabledbinstoptions( ) ).
        MESSAGE |Retrieved { lv_option_count } orderable DB instance options.| TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_orderable_db_instance_options]
  ENDMETHOD.


  METHOD describe_db_instances.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_db_instances]
    TRY.
        oo_result = lo_rds->describedbinstances(
          iv_dbinstanceidentifier = iv_dbinstanceidentifier ).
        MESSAGE 'DB instance retrieved.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbinstnotfndfault.
        MESSAGE 'DB instance not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_instances]
  ENDMETHOD.


  METHOD create_db_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_db_instance]
    TRY.
        oo_result = lo_rds->createdbinstance(
          iv_dbname               = iv_dbname
          iv_dbinstanceidentifier = iv_dbinstanceidentifier
          iv_dbparametergroupname = iv_dbparametergroupname
          iv_engine               = iv_engine
          iv_engineversion        = iv_engineversion
          iv_dbinstanceclass      = iv_dbinstanceclass
          iv_storagetype          = iv_storagetype
          iv_allocatedstorage     = iv_allocatedstorage
          iv_masterusername       = iv_masterusername
          iv_masteruserpassword   = iv_masteruserpassword ).
        MESSAGE 'DB instance created.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbinstalrdyexfault.
        MESSAGE 'DB instance already exists.' TYPE 'E'.
      CATCH /aws1/cx_rdsinstquotaexcdfault.
        MESSAGE 'DB instance quota exceeded.' TYPE 'E'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_db_instance]
  ENDMETHOD.


  METHOD delete_db_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.delete_db_instance]
    TRY.
        oo_result = lo_rds->deletedbinstance(
          iv_dbinstanceidentifier = iv_dbinstanceidentifier
          iv_skipfinalsnapshot    = abap_true
          iv_deleteautomatedbackups = abap_true ).
        MESSAGE 'DB instance deleted.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbinstnotfndfault.
        MESSAGE 'DB instance not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsinvdbinststatefa00.
        MESSAGE 'DB instance is in an invalid state.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.delete_db_instance]
  ENDMETHOD.
ENDCLASS.
