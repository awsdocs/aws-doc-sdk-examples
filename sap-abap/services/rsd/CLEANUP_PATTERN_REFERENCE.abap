" Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
" SPDX-License-Identifier: Apache-2.0

" ============================================================================
" CLEANUP PATTERN REFERENCE - Redshift Cluster Deletion with Verification
" ============================================================================
"
" This pattern demonstrates how to properly clean up Redshift clusters in
" unit tests by deleting the cluster and verifying deletion status.
"
" Use this pattern for services that support deletion verification through
" describe/list operations.
" ============================================================================

METHOD class_teardown.
  DATA lv_drop_sql TYPE string.
  DATA lo_drop_result TYPE REF TO /aws1/cl_rsdexecutestmtoutput.
  DATA lv_statement_id TYPE /aws1/rsduuid.
  DATA lo_delete_cluster_result TYPE REF TO /aws1/cl_rshdeleteclustresult.
  DATA lo_describe_result TYPE REF TO /aws1/cl_rshclustersmessage.
  DATA lt_clusters TYPE /aws1/cl_rshcluster=>tt_clusterlist.
  DATA lo_cluster TYPE REF TO /aws1/cl_rshcluster.
  DATA lv_status TYPE /aws1/rshstring.
  DATA lv_cleanup_successful TYPE abap_bool VALUE abap_false.

  " Step 1: Clean up dependent resources first (tables, etc.)
  " ---------------------------------------------------------
  TRY.
      lv_drop_sql = |DROP TABLE IF EXISTS { av_table_name }|.
      lo_drop_result = ao_rsd_actions->execute_statement(
        iv_cluster_identifier = av_cluster_id
        iv_database_name      = av_database_name
        iv_user_name          = av_user_name
        iv_sql                = lv_drop_sql
      ).
      lv_statement_id = lo_drop_result->get_id( ).
      wait_for_statement_finished( lv_statement_id ).
    CATCH /aws1/cx_rt_generic.
      " Ignore dependent resource cleanup errors
      " Continue with primary resource deletion
  ENDTRY.

  " Step 2: Delete the primary resource (cluster)
  " ----------------------------------------------
  TRY.
      lo_delete_cluster_result = ao_rsh->deletecluster(
        iv_clusteridentifier        = av_cluster_id
        iv_skipfinalclustersnapshot = abap_true  " For test cleanup
      ).

      " Step 3: Verify deletion status
      " -------------------------------
      " Best practice: Check deletion status immediately after deletion request
      TRY.
          lo_describe_result = ao_rsh->describeclusters(
            iv_clusteridentifier = av_cluster_id
          ).

          lt_clusters = lo_describe_result->get_clusters( ).

          " Success Condition 1: No cluster returned
          IF lines( lt_clusters ) = 0.
            lv_cleanup_successful = abap_true.
            MESSAGE 'Redshift cluster successfully deleted.' TYPE 'I'.
          ELSE.
            " Success Condition 2: Cluster status is 'deleting'
            READ TABLE lt_clusters INDEX 1 INTO lo_cluster.
            IF lo_cluster IS BOUND.
              lv_status = lo_cluster->get_clusterstatus( ).
              IF lv_status = 'deleting'.
                lv_cleanup_successful = abap_true.
                MESSAGE 'Redshift cluster deletion in progress.' TYPE 'I'.
              ELSE.
                " Unexpected status - may need manual intervention
                MESSAGE |Cluster status: { lv_status }. Manual cleanup may be required.| TYPE 'I'.
              ENDIF.
            ENDIF.
          ENDIF.

        " Success Condition 3: Resource not found
        CATCH /aws1/cx_rshclustnotfoundfault.
          lv_cleanup_successful = abap_true.
          MESSAGE 'Redshift cluster successfully deleted (not found).' TYPE 'I'.
      ENDTRY.

    " Handle deletion exceptions
    CATCH /aws1/cx_rshclustnotfoundfault.
      " Resource already deleted - this is success
      lv_cleanup_successful = abap_true.
      MESSAGE 'Redshift cluster not found (already deleted).' TYPE 'I'.

    CATCH /aws1/cx_rt_generic INTO DATA(lo_delete_ex).
      " Deletion failed - log error
      MESSAGE |Cluster deletion error: { lo_delete_ex->get_text( ) }. Manual cleanup required.| TYPE 'I'.
  ENDTRY.

  " Step 4: Provide fallback information if automated cleanup failed
  " -----------------------------------------------------------------
  IF lv_cleanup_successful = abap_false.
    MESSAGE 'Redshift cluster tagged with convert_test=true. Manual cleanup required.' TYPE 'I'.
  ENDIF.
ENDMETHOD.

" ============================================================================
" KEY PRINCIPLES
" ============================================================================
"
" 1. DELETE DEPENDENT RESOURCES FIRST
"    - Drop tables, delete objects, etc.
"    - Ignore errors to ensure primary deletion proceeds
"
" 2. DELETE PRIMARY RESOURCE
"    - Use appropriate deletion flags (e.g., skip final snapshots for tests)
"    - Catch resource-not-found exceptions as success
"
" 3. VERIFY DELETION STATUS
"    - Call describe/list operation immediately after deletion
"    - Accept multiple success conditions:
"      a) Resource not found in list
"      b) Resource status indicates deletion in progress
"      c) NotFound exception thrown
"
" 4. PROVIDE CLEAR FEEDBACK
"    - Different messages for each outcome
"    - Include resource identifiers in messages
"    - Mention manual cleanup requirements if automated cleanup fails
"
" 5. USE RESOURCE TAGGING
"    - Tag all test resources with 'convert_test=true'
"    - Enables manual identification and cleanup if needed
"
" ============================================================================
" APPLICABLE TO SERVICES
" ============================================================================
"
" This pattern works for any AWS service that:
" - Supports resource deletion operations
" - Provides describe/list operations to check status
" - Has deletion status indicators (e.g., 'deleting', 'deleted')
"
" Examples:
" - Redshift clusters (RSH)
" - RDS instances (RDS)
" - EMR clusters (EMR)
" - ECS clusters (ECS)
" - EKS clusters (EKS)
" - EC2 instances (EC2)
"
" ============================================================================
" COST OPTIMIZATION
" ============================================================================
"
" Immediate deletion verification is crucial for cost management:
" - Prevents orphaned resources that continue to incur charges
" - Provides early warning if deletion fails
" - Enables rapid response to cleanup issues
"
" ============================================================================
