" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_rds_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS descr_db_clust_param_groups
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclustparamgroup
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_db_clust_param_group
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
        !iv_param_group_family      TYPE /aws1/rdsstring
        !iv_description             TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclustparamgroup
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_db_clust_param_group
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS descr_db_cluster_parameters
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
        !iv_name_prefix             TYPE /aws1/rdsstring OPTIONAL
        !iv_source                  TYPE /aws1/rdsstring OPTIONAL
      RETURNING
        VALUE(ot_parameters)        TYPE /aws1/cl_rdsparameter=>tt_parameterslist
      RAISING
        /aws1/cx_rt_generic.

    METHODS modify_db_clust_param_group
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
        !it_update_parameters       TYPE /aws1/cl_rdsparameter=>tt_parameterslist
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclstprmgrnamemsg
      RAISING
        /aws1/cx_rt_generic.

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
    METHODS descr_orderable_db_inst_opts
      IMPORTING
        !iv_engine        TYPE /aws1/rdsstring
        !iv_engineversion TYPE /aws1/rdsstring
      EXPORTING
        !oo_result        TYPE REF TO /aws1/cl_rdsorderabledbinsto00
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
    " iv_dbparametergroupname = 'mydbparametergroup'
    TRY.
        oo_result = lo_rds->describedbparametergroups(
          iv_dbparametergroupname = iv_dbparametergroupname ).
        MESSAGE 'DB parameter group retrieved.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_parameter_groups]
  ENDMETHOD.

  METHOD descr_db_clust_param_groups.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.descr_db_clust_param_groups]
    TRY.
        DATA(lo_output) = lo_rds->describedbclusterparamgroups(
          iv_dbclusterparamgroupname = iv_param_group_name
        ).
        DATA(lt_param_groups) = lo_output->get_dbclusterparametergroups( ).
        IF lines( lt_param_groups ) > 0.
          oo_result = lt_param_groups[ 1 ].
        ENDIF.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
    ENDTRY.
    " snippet-end:[rds.abapv1.descr_db_clust_param_groups]
  ENDMETHOD.

  METHOD create_db_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_db_parameter_group]
    " iv_dbparametergroupname   = 'mydbparametergroup'
    " iv_dbparametergroupfamily = 'mysql8.0'
    " iv_description            = 'My custom DB parameter group for MySQL 8.0'
    TRY.
        oo_result = lo_rds->createdbparametergroup(
          iv_dbparametergroupname   = iv_dbparametergroupname
          iv_dbparametergroupfamily = iv_dbparametergroupfamily
          iv_description            = iv_description ).
        MESSAGE 'DB parameter group created.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbparmgralrexfault.
        MESSAGE 'DB parameter group already exists.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrquotaexcd00.
        MESSAGE 'DB parameter group quota exceeded.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_db_parameter_group]
  ENDMETHOD.


  METHOD create_db_clust_param_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_db_clust_param_group]
    TRY.
        DATA(lo_output) = lo_rds->createdbclusterparamgroup(
          iv_dbclusterparamgroupname = iv_param_group_name
          iv_dbparametergroupfamily = iv_param_group_family
          iv_description = iv_description
        ).
        oo_result = lo_output->get_dbclusterparametergroup( ).
      CATCH /aws1/cx_rdsdbparmgralrexfault.
        " Re-raise exception - parameter group already exists
        RAISE EXCEPTION TYPE /aws1/cx_rdsdbparmgralrexfault.
      CATCH /aws1/cx_rdsdbprmgrquotaexcd00.
        " Re-raise exception - quota exceeded
        RAISE EXCEPTION TYPE /aws1/cx_rdsdbprmgrquotaexcd00.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_db_clust_param_group]
  ENDMETHOD.

  METHOD delete_db_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.delete_db_parameter_group]
    " iv_dbparametergroupname = 'mydbparametergroup'
    TRY.
        lo_rds->deletedbparametergroup(
          iv_dbparametergroupname = iv_dbparametergroupname ).
        MESSAGE 'DB parameter group deleted.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'I'.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        MESSAGE 'DB parameter group is in an invalid state.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.delete_db_parameter_group]
  ENDMETHOD.


  METHOD delete_db_clust_param_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.delete_db_clust_param_group]
    TRY.
        lo_rds->deletedbclusterparamgroup(
          iv_dbclusterparamgroupname = iv_param_group_name
        ).
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Re-raise exception - parameter group not found
        RAISE EXCEPTION TYPE /aws1/cx_rdsdbprmgrnotfndfault.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        " Re-raise exception - invalid state
        RAISE EXCEPTION TYPE /aws1/cx_rdsinvdbprmgrstatef00.
    ENDTRY.
    " snippet-end:[rds.abapv1.delete_db_clust_param_group]
  ENDMETHOD.

  METHOD describe_db_parameters.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_db_parameters]
    " iv_dbparametergroupname = 'mydbparametergroup'
    " iv_source               = 'user' (optional - filters by parameter source)
    TRY.
        oo_result = lo_rds->describedbparameters(
          iv_dbparametergroupname = iv_dbparametergroupname
          iv_source               = iv_source ).
        DATA(lv_param_count) = lines( oo_result->get_parameters( ) ).
        MESSAGE |Retrieved { lv_param_count } parameters.| TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_parameters]
  ENDMETHOD.

  METHOD descr_db_cluster_parameters.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.descr_db_cluster_parameters]
    TRY.
        DATA lv_marker TYPE /aws1/rdsstring VALUE ''.
        DATA lt_all_parameters TYPE /aws1/cl_rdsparameter=>tt_parameterslist.

        DO.
          DATA(lo_output) = lo_rds->describedbclusterparameters(
            iv_dbclusterparamgroupname = iv_param_group_name
            iv_source = iv_source
            iv_marker = lv_marker
          ).

          LOOP AT lo_output->get_parameters( ) INTO DATA(lo_param).
            IF iv_name_prefix IS INITIAL OR
               lo_param->get_parametername( ) CP |{ iv_name_prefix }*|.
              APPEND lo_param TO lt_all_parameters.
            ENDIF.
          ENDLOOP.

          lv_marker = lo_output->get_marker( ).
          IF lv_marker IS INITIAL.
            EXIT.
          ENDIF.
        ENDDO.

        ot_parameters = lt_all_parameters.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Re-raise exception - parameter group not found
        RAISE EXCEPTION TYPE /aws1/cx_rdsdbprmgrnotfndfault.
    ENDTRY.
    " snippet-end:[rds.abapv1.descr_db_cluster_parameters]
  ENDMETHOD.

  METHOD modify_db_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.modify_db_parameter_group]
    " iv_dbparametergroupname = 'mydbparametergroup'
    " it_parameters - table containing parameter objects with:
    "   - parametername = 'max_connections'
    "   - parametervalue = '100'
    "   - applymethod = 'immediate' or 'pending-reboot'
    TRY.
        oo_result = lo_rds->modifydbparametergroup(
          iv_dbparametergroupname = iv_dbparametergroupname
          it_parameters           = it_parameters ).
        MESSAGE 'DB parameter group modified.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'I'.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        MESSAGE 'DB parameter group is in an invalid state.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.modify_db_parameter_group]
  ENDMETHOD.

  METHOD modify_db_clust_param_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.modify_db_clust_param_group]
    TRY.
        oo_result = lo_rds->modifydbclusterparamgroup(
          iv_dbclusterparamgroupname = iv_param_group_name
          it_parameters = it_update_parameters
        ).
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        " Re-raise exception - parameter group not found
        RAISE EXCEPTION TYPE /aws1/cx_rdsdbprmgrnotfndfault.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        " Re-raise exception - invalid state
        RAISE EXCEPTION TYPE /aws1/cx_rdsinvdbprmgrstatef00.
    ENDTRY.
    " snippet-end:[rds.abapv1.modify_db_clust_param_group]
  ENDMETHOD.

  METHOD describe_db_engine_versions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_db_engine_versions]
    " iv_engine                 = 'mysql'
    " iv_dbparametergroupfamily = 'mysql8.0' (optional - filters by parameter group family)
    TRY.
        oo_result = lo_rds->describedbengineversions(
          iv_engine                 = iv_engine
          iv_dbparametergroupfamily = iv_dbparametergroupfamily ).
        DATA(lv_version_count) = lines( oo_result->get_dbengineversions( ) ).
        MESSAGE |Retrieved { lv_version_count } engine versions.| TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_db_engine_versions]
  ENDMETHOD.

  METHOD descr_orderable_db_inst_opts.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.describe_orderable_db_instance_options]
    " iv_engine        = 'mysql'
    " iv_engineversion = '8.0.35'
    TRY.
        oo_result = lo_rds->descrorderabledbinstoptions(
          iv_engine        = iv_engine
          iv_engineversion = iv_engineversion ).
        DATA(lv_option_count) = lines( oo_result->get_orderabledbinstoptions( ) ).
        MESSAGE |Retrieved { lv_option_count } orderable DB instance options.| TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.describe_orderable_db_instance_options]
  ENDMETHOD.
ENDCLASS.