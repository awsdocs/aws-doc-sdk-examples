" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0
CLASS /awsex/cl_rds_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.
    METHODS get_parameter_group
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclustparamgroup
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_parameter_group
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
        !iv_param_group_family      TYPE /aws1/rdsstring
        !iv_description             TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclustparamgroup
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_parameter_group
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_parameters
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
        !iv_name_prefix             TYPE /aws1/rdsstring OPTIONAL
        !iv_source                  TYPE /aws1/rdsstring OPTIONAL
      RETURNING
        VALUE(ot_parameters)        TYPE /aws1/cl_rdsparameter=>tt_parameterslist
      RAISING
        /aws1/cx_rt_generic.

    METHODS update_parameters
      IMPORTING
        !iv_param_group_name        TYPE /aws1/rdsstring
        !it_update_parameters       TYPE /aws1/cl_rdsparameter=>tt_parameterslist
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclstprmgrnamemsg
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_engine_versions
      IMPORTING
        !iv_engine                  TYPE /aws1/rdsstring
        !iv_param_group_family      TYPE /aws1/rdsstring OPTIONAL
      RETURNING
        VALUE(ot_versions)          TYPE /aws1/cl_rdsdbengineversion=>tt_dbengineversionlist
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_orderable_instances
      IMPORTING
        !iv_db_engine               TYPE /aws1/rdsstring
        !iv_db_engine_version       TYPE /aws1/rdsstring
      RETURNING
        VALUE(ot_inst_opts)         TYPE /aws1/cl_rdsorderabledbinsto01=>tt_orderabledbinstoptionslist
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /awsex/cl_rds_actions IMPLEMENTATION.


  METHOD get_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.get_parameter_group]
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
    " snippet-end:[rds.abapv1.get_parameter_group]
  ENDMETHOD.


  METHOD create_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_parameter_group]
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
    " snippet-end:[rds.abapv1.create_parameter_group]
  ENDMETHOD.


  METHOD delete_parameter_group.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.delete_parameter_group]
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
    " snippet-end:[rds.abapv1.delete_parameter_group]
  ENDMETHOD.


  METHOD get_parameters.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.get_parameters]
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
    " snippet-end:[rds.abapv1.get_parameters]
  ENDMETHOD.


  METHOD update_parameters.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.update_parameters]
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
    " snippet-end:[rds.abapv1.update_parameters]
  ENDMETHOD.


  METHOD get_engine_versions.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.get_engine_versions]
    TRY.
        " iv_engine = 'aurora-mysql'
        " iv_param_group_family = 'aurora-mysql8.0' (optional)
        DATA(lo_output) = lo_rds->describedbengineversions(
          iv_engine = iv_engine
          iv_dbparametergroupfamily = iv_param_group_family
        ).
        ot_versions = lo_output->get_dbengineversions( ).
      CATCH /aws1/cx_rt_generic.
        " Re-raise exception
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic.
    ENDTRY.
    " snippet-end:[rds.abapv1.get_engine_versions]
  ENDMETHOD.


  METHOD get_orderable_instances.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.get_orderable_instances]
    TRY.
        " iv_db_engine = 'aurora-mysql'
        " iv_db_engine_version = '8.0.mysql_aurora.3.02.0'
        DATA lv_marker TYPE /aws1/rdsstring VALUE ''.
        DATA lt_all_options TYPE /aws1/cl_rdsorderabledbinsto01=>tt_orderabledbinstoptionslist.

        DO.
          DATA(lo_output) = lo_rds->descrorderabledbinstoptions(
            iv_engine = iv_db_engine
            iv_engineversion = iv_db_engine_version
            iv_marker = lv_marker
          ).

          APPEND LINES OF lo_output->get_orderabledbinstoptions( ) TO lt_all_options.

          lv_marker = lo_output->get_marker( ).
          IF lv_marker IS INITIAL.
            EXIT.
          ENDIF.
        ENDDO.

        ot_inst_opts = lt_all_options.
      CATCH /aws1/cx_rt_generic.
        " Re-raise exception
        RAISE EXCEPTION TYPE /aws1/cx_rt_generic.
    ENDTRY.
    " snippet-end:[rds.abapv1.get_orderable_instances]
  ENDMETHOD.
ENDCLASS.
