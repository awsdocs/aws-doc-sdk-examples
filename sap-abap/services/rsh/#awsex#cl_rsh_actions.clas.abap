CLASS /awsex/cl_rsh_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS create_cluster
      IMPORTING
        !iv_cluster_identifier TYPE /aws1/rshstring
        !iv_node_type          TYPE /aws1/rshstring
        !iv_master_username    TYPE /aws1/rshstring
        !iv_master_password    TYPE /aws1/rshsensitivestring
        !iv_publicly_accessible TYPE /aws1/rshbooleanoptional
        !iv_number_of_nodes    TYPE /aws1/rshintegeroptional
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_rshcreateclustresult
      RAISING
        /aws1/cx_rt_generic.

    METHODS delete_cluster
      IMPORTING
        !iv_cluster_identifier TYPE /aws1/rshstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS modify_cluster
      IMPORTING
        !iv_cluster_identifier  TYPE /aws1/rshstring
        !iv_pref_maintenance_wn TYPE /aws1/rshstring
      RAISING
        /aws1/cx_rt_generic.

    METHODS describe_clusters
      IMPORTING
        !iv_cluster_identifier TYPE /aws1/rshstring OPTIONAL
      RETURNING
        VALUE(oo_result)       TYPE REF TO /aws1/cl_rshclustersmessage
      RAISING
        /aws1/cx_rt_generic.

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_RSH_ACTIONS IMPLEMENTATION.


  METHOD create_cluster.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsh TYPE REF TO /aws1/if_rsh.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsh = /aws1/cl_rsh_factory=>create( lo_session ).

    " snippet-start:[rsh.abapv1.create_cluster]
    TRY.
        " Example values: iv_cluster_identifier = 'my-redshift-cluster'
        " Example values: iv_node_type = 'ra3.4xlarge'
        " Example values: iv_master_username = 'awsuser'
        " Example values: iv_master_password = 'AwsUser1000'
        " Example values: iv_publicly_accessible = abap_true
        " Example values: iv_number_of_nodes = 2
        oo_result = lo_rsh->createcluster(
          iv_clusteridentifier = iv_cluster_identifier
          iv_nodetype = iv_node_type
          iv_masterusername = iv_master_username
          iv_masteruserpassword = iv_master_password
          iv_publiclyaccessible = iv_publicly_accessible
          iv_numberofnodes = iv_number_of_nodes
        ).
        MESSAGE 'Redshift cluster created successfully.' TYPE 'I'.
      CATCH /aws1/cx_rshclustalrdyexfault.
        MESSAGE 'Cluster already exists.' TYPE 'E'.
      CATCH /aws1/cx_rshclstquotaexcdfault.
        MESSAGE 'Cluster quota exceeded.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rsh.abapv1.create_cluster]
  ENDMETHOD.


  METHOD delete_cluster.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsh TYPE REF TO /aws1/if_rsh.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsh = /aws1/cl_rsh_factory=>create( lo_session ).

    " snippet-start:[rsh.abapv1.delete_cluster]
    TRY.
        " Example values: iv_cluster_identifier = 'my-redshift-cluster'
        lo_rsh->deletecluster(
          iv_clusteridentifier = iv_cluster_identifier
          iv_skipfinalclustersnapshot = abap_true
        ).
        MESSAGE 'Redshift cluster deleted successfully.' TYPE 'I'.
      CATCH /aws1/cx_rshclustnotfoundfault.
        MESSAGE 'Cluster not found.' TYPE 'E'.
      CATCH /aws1/cx_rshinvcluststatefault.
        MESSAGE 'Invalid cluster state for deletion.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rsh.abapv1.delete_cluster]
  ENDMETHOD.


  METHOD modify_cluster.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsh TYPE REF TO /aws1/if_rsh.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsh = /aws1/cl_rsh_factory=>create( lo_session ).

    " snippet-start:[rsh.abapv1.modify_cluster]
    TRY.
        " Example values: iv_cluster_identifier = 'my-redshift-cluster'
        " Example values: iv_pref_maintenance_wn = 'wed:07:30-wed:08:00'
        lo_rsh->modifycluster(
          iv_clusteridentifier = iv_cluster_identifier
          iv_preferredmaintenancewin00 = iv_pref_maintenance_wn
        ).
        MESSAGE 'Redshift cluster modified successfully.' TYPE 'I'.
      CATCH /aws1/cx_rshclustnotfoundfault.
        MESSAGE 'Cluster not found.' TYPE 'E'.
      CATCH /aws1/cx_rshinvcluststatefault.
        MESSAGE 'Invalid cluster state for modification.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rsh.abapv1.modify_cluster]
  ENDMETHOD.


  METHOD describe_clusters.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA lo_session TYPE REF TO /aws1/cl_rt_session_base.
    DATA lo_rsh TYPE REF TO /aws1/if_rsh.
    DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
    DATA lv_cluster_count TYPE i.
    
    lo_session = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    lo_rsh = /aws1/cl_rsh_factory=>create( lo_session ).

    " snippet-start:[rsh.abapv1.describe_clusters]
    TRY.
        " Example values: iv_cluster_identifier = 'my-redshift-cluster' (optional)
        oo_result = lo_rsh->describeclusters(
          iv_clusteridentifier = iv_cluster_identifier
        ).
        lt_clusters = oo_result->get_clusters( ).
        lv_cluster_count = lines( lt_clusters ).
        MESSAGE |Retrieved { lv_cluster_count } cluster(s).| TYPE 'I'.
      CATCH /aws1/cx_rshclustnotfoundfault.
        MESSAGE 'Cluster not found.' TYPE 'E'.
    ENDTRY.
    " snippet-end:[rsh.abapv1.describe_clusters]
  ENDMETHOD.
ENDCLASS.
