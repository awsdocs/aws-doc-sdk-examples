CLASS /awsex/cl_cfs_actions DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    METHODS put_config_rule
      IMPORTING
        !iv_rule_name TYPE /aws1/cfsconfigrulename .

    METHODS describe_config_rule
      IMPORTING
        !iv_rule_name        TYPE /aws1/cfsconfigrulename
      RETURNING
        VALUE(ot_cfg_rules) TYPE /aws1/cl_cfsconfigrule=>tt_configrules .

    METHODS delete_config_rule
      IMPORTING
        !iv_rule_name TYPE /aws1/cfsconfigrulename .

  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS /AWSEX/CL_CFS_ACTIONS IMPLEMENTATION.


  METHOD put_config_rule.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cfs) = /aws1/cl_cfs_factory=>create( lo_session ).

    "snippet-start:[cfs.abapv1.put_config_rule]
    " Create a config rule for S3 bucket public read prohibition
    lo_cfs->putconfigrule(
      io_configrule = NEW /aws1/cl_cfsconfigrule(
        iv_configrulename = iv_rule_name
        iv_description = |S3 Public Read Prohibited Bucket Rule|
        io_scope = NEW /aws1/cl_cfsscope(
          it_complianceresourcetypes = VALUE /aws1/cl_cfscplncresrctypes_w=>tt_complianceresourcetypes(
            ( NEW /aws1/cl_cfscplncresrctypes_w( |AWS::S3::Bucket| ) )
          )
        )
        io_source = NEW /aws1/cl_cfssource(
          iv_owner = |AWS|
          iv_sourceidentifier = |S3_BUCKET_PUBLIC_READ_PROHIBITED|
        )
        iv_inputparameters = '{}'
        iv_configrulestate = |ACTIVE|
      )
    ).
    MESSAGE 'Created AWS Config rule.' TYPE 'I'.
    "snippet-end:[cfs.abapv1.put_config_rule]

  ENDMETHOD.


  METHOD describe_config_rule.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cfs) = /aws1/cl_cfs_factory=>create( lo_session ).

    "snippet-start:[cfs.abapv1.describe_config_rule]
    DATA(lo_result) = lo_cfs->describeconfigrules(
      it_configrulenames = VALUE /aws1/cl_cfsconfigrulenames_w=>tt_configrulenames(
        ( NEW /aws1/cl_cfsconfigrulenames_w( iv_rule_name ) )
      )
    ).
    ot_cfg_rules = lo_result->get_configrules( ).
    MESSAGE 'Retrieved AWS Config rule data.' TYPE 'I'.
    "snippet-end:[cfs.abapv1.describe_config_rule]

  ENDMETHOD.


  METHOD delete_config_rule.

    CONSTANTS cv_pfl TYPE /aws1/rt_profile_id VALUE 'ZCODE_DEMO'.

    DATA(lo_session) = /aws1/cl_rt_session_aws=>create( cv_pfl ).
    DATA(lo_cfs) = /aws1/cl_cfs_factory=>create( lo_session ).

    "snippet-start:[cfs.abapv1.delete_config_rule]
    lo_cfs->deleteconfigrule( iv_rule_name ).
    MESSAGE 'Deleted AWS Config rule.' TYPE 'I'.
    "snippet-end:[cfs.abapv1.delete_config_rule]

  ENDMETHOD.
ENDCLASS.
