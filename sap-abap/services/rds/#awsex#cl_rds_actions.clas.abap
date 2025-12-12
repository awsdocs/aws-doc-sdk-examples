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

    METHODS get_db_cluster
      IMPORTING
        !iv_cluster_name            TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbcluster
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_db_cluster
      IMPORTING
        !iv_cluster_name            TYPE /aws1/rdsstring
        !iv_param_group_name        TYPE /aws1/rdsstring
        !iv_db_name                 TYPE /aws1/rdsstring
        !iv_db_engine               TYPE /aws1/rdsstring
        !iv_db_engine_version       TYPE /aws1/rdsstring
        !iv_admin_name              TYPE /aws1/rdsstring
        !iv_admin_password          TYPE /aws1/rdssensitivestring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbcluster
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_db_cluster
      IMPORTING
        !iv_cluster_name            TYPE /aws1/rdsstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_cluster_snapshot
      IMPORTING
        !iv_snapshot_id             TYPE /aws1/rdsstring
        !iv_cluster_id              TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclustersnapshot
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_cluster_snapshot
      IMPORTING
        !iv_snapshot_id             TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbclustersnapshot
      RAISING
        /aws1/cx_rt_generic.

    METHODS create_instance_cluster
      IMPORTING
        !iv_instance_id             TYPE /aws1/rdsstring
        !iv_cluster_id              TYPE /aws1/rdsstring
        !iv_db_engine               TYPE /aws1/rdsstring
        !iv_instance_class          TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbinstance
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
        VALUE(ot_inst_opts)         TYPE /aws1/cl_rdsorderabledbinsto01=>tt_orderabledbinstooptlist
      RAISING
        /aws1/cx_rt_generic.

    METHODS get_db_instance
      IMPORTING
        !iv_instance_id             TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbinstance
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_db_instance
      IMPORTING
        !iv_instance_id             TYPE /aws1/rdsstring
      RETURNING
        VALUE(oo_result)            TYPE REF TO /aws1/cl_rdsdbinstance
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
        MESSAGE 'Retrieved DB cluster parameter group.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'Parameter group does not exist.' TYPE 'I'.
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
        MESSAGE 'Created DB cluster parameter group.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbparmgralrexfault.
        MESSAGE 'DB parameter group already exists.' TYPE 'E'.
      CATCH /aws1/cx_rdsdbprmgrquotaexcd00.
        MESSAGE 'DB parameter group quota exceeded.' TYPE 'E'.
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
        MESSAGE 'Deleted DB cluster parameter group.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        MESSAGE 'Invalid DB parameter group state.' TYPE 'E'.
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
        MESSAGE 'Retrieved DB cluster parameters.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
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
        MESSAGE 'Updated DB cluster parameter group.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbprmgrnotfndfault.
        MESSAGE 'DB parameter group not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsinvdbprmgrstatef00.
        MESSAGE 'Invalid DB parameter group state.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.update_parameters]
  ENDMETHOD.


  METHOD get_db_cluster.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.get_db_cluster]
    TRY.
        DATA(lo_output) = lo_rds->describedbclusters(
          iv_dbclusteridentifier = iv_cluster_name
        ).
        DATA(lt_clusters) = lo_output->get_dbclusters( ).
        IF lines( lt_clusters ) > 0.
          oo_result = lt_clusters[ 1 ].
        ENDIF.
        MESSAGE 'Retrieved DB cluster information.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbclustnotfndfault.
        MESSAGE 'DB cluster not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.get_db_cluster]
  ENDMETHOD.


  METHOD create_db_cluster.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_db_cluster]
    TRY.
        " iv_db_name = 'MyDatabase'
        " iv_cluster_name = 'my-aurora-cluster'
        " iv_param_group_name = 'my-parameter-group'
        " iv_db_engine = 'aurora-mysql'
        " iv_db_engine_version = '8.0.mysql_aurora.3.02.0'
        " iv_admin_name = 'admin'
        " iv_admin_password = 'MySecurePassword123!'
        DATA(lo_output) = lo_rds->createdbcluster(
          iv_databasename = iv_db_name
          iv_dbclusteridentifier = iv_cluster_name
          iv_dbclusterparamgroupname = iv_param_group_name
          iv_engine = iv_db_engine
          iv_engineversion = iv_db_engine_version
          iv_masterusername = iv_admin_name
          iv_masteruserpassword = iv_admin_password
        ).
        oo_result = lo_output->get_dbcluster( ).
        MESSAGE 'Created DB cluster.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbclstalrexfault.
        MESSAGE 'DB cluster already exists.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbclstquotaexcdf00.
        MESSAGE 'DB cluster quota exceeded.' TYPE 'E'.
      CATCH /aws1/cx_rdsdbclstprmgrnotfn00.
        MESSAGE 'DB cluster parameter group not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsinsufficientstrg00.
        MESSAGE 'Insufficient storage capacity.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_db_cluster]
  ENDMETHOD.


  METHOD delete_db_cluster.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.delete_db_cluster]
    TRY.
        lo_rds->deletedbcluster(
          iv_dbclusteridentifier = iv_cluster_name
          iv_skipfinalsnapshot = abap_true
        ).
        MESSAGE 'Deleted DB cluster.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbclustnotfndfault.
        MESSAGE 'DB cluster not found.' TYPE 'I'.
      CATCH /aws1/cx_rdsinvdbclststatefa00.
        MESSAGE 'Invalid DB cluster state.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.delete_db_cluster]
  ENDMETHOD.


  METHOD create_cluster_snapshot.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_cluster_snapshot]
    TRY.
        " iv_snapshot_id = 'my-cluster-snapshot-001'
        " iv_cluster_id = 'my-aurora-cluster'
        DATA(lo_output) = lo_rds->createdbclustersnapshot(
          iv_dbclustersnapshotid = iv_snapshot_id
          iv_dbclusteridentifier = iv_cluster_id
        ).
        oo_result = lo_output->get_dbclustersnapshot( ).
        MESSAGE 'Created DB cluster snapshot.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbclustnotfndfault.
        MESSAGE 'DB cluster not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsdbclstsnapalrexf00.
        MESSAGE 'DB cluster snapshot already exists.' TYPE 'I'.
      CATCH /aws1/cx_rdsinvdbclststatefa00.
        MESSAGE 'Invalid DB cluster state.' TYPE 'E'.
      CATCH /aws1/cx_rdssnapquotaexcdfault.
        MESSAGE 'Snapshot quota exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_cluster_snapshot]
  ENDMETHOD.


  METHOD get_cluster_snapshot.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.get_cluster_snapshot]
    TRY.
        DATA(lo_output) = lo_rds->describedbclustersnapshots(
          iv_dbclustersnapshotid = iv_snapshot_id
        ).
        DATA(lt_snapshots) = lo_output->get_dbclustersnapshots( ).
        IF lines( lt_snapshots ) > 0.
          oo_result = lt_snapshots[ 1 ].
        ENDIF.
        MESSAGE 'Retrieved DB cluster snapshot.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbclstsnapnotfnd00.
        MESSAGE 'DB cluster snapshot not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.get_cluster_snapshot]
  ENDMETHOD.


  METHOD create_instance_cluster.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.create_instance_in_cluster]
    TRY.
        " iv_instance_id = 'my-db-instance-001'
        " iv_cluster_id = 'my-aurora-cluster'
        " iv_db_engine = 'aurora-mysql'
        " iv_instance_class = 'db.r5.large'
        DATA(lo_output) = lo_rds->createdbinstance(
          iv_dbinstanceidentifier = iv_instance_id
          iv_dbclusteridentifier = iv_cluster_id
          iv_engine = iv_db_engine
          iv_dbinstanceclass = iv_instance_class
        ).
        oo_result = lo_output->get_dbinstance( ).
        MESSAGE 'Created DB instance in cluster.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbinstalrdyexfault.
        MESSAGE 'DB instance already exists.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbclustnotfndfault.
        MESSAGE 'DB cluster not found.' TYPE 'E'.
      CATCH /aws1/cx_rdsinstquotaexcdfault.
        MESSAGE 'DB instance quota exceeded.' TYPE 'E'.
      CATCH /aws1/cx_rdsinsufficientdbin00.
        MESSAGE 'Insufficient DB instance capacity.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.create_instance_in_cluster]
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
        MESSAGE 'Retrieved DB engine versions.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Error retrieving DB engine versions.' TYPE 'E'.
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
        DATA lt_all_options TYPE /aws1/cl_rdsorderabledbinsto01=>tt_orderabledbinstooptlist.

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
        MESSAGE 'Retrieved orderable DB instance options.' TYPE 'I'.
      CATCH /aws1/cx_rt_generic.
        MESSAGE 'Error retrieving orderable DB instance options.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.get_orderable_instances]
  ENDMETHOD.


  METHOD get_db_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.get_db_instance]
    TRY.
        DATA(lo_output) = lo_rds->describedbinstances(
          iv_dbinstanceidentifier = iv_instance_id
        ).
        DATA(lt_instances) = lo_output->get_dbinstances( ).
        IF lines( lt_instances ) > 0.
          oo_result = lt_instances[ 1 ].
        ENDIF.
        MESSAGE 'Retrieved DB instance information.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbinstnotfndfault.
        MESSAGE 'DB instance not found.' TYPE 'I'.
    ENDTRY.
    " snippet-end:[rds.abapv1.get_db_instance]
  ENDMETHOD.


  METHOD delete_db_instance.
    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_rds) = /aws1/cl_rds_factory=>create( lo_session ).

    " snippet-start:[rds.abapv1.delete_db_instance]
    TRY.
        DATA(lo_output) = lo_rds->deletedbinstance(
          iv_dbinstanceidentifier = iv_instance_id
          iv_skipfinalsnapshot = abap_true
          iv_deleteautomatedbackups = abap_true
        ).
        oo_result = lo_output->get_dbinstance( ).
        MESSAGE 'Deleted DB instance.' TYPE 'I'.
      CATCH /aws1/cx_rdsdbinstnotfndfault.
        MESSAGE 'DB instance not found.' TYPE 'I'.
      CATCH /aws1/cx_rdsinvdbinststatefa00.
        MESSAGE 'Invalid DB instance state.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rds.abapv1.delete_db_instance]
  ENDMETHOD.
ENDCLASS.
