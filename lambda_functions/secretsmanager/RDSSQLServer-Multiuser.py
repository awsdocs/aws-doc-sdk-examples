#snippet-sourceauthor: [aws]
#snippet-sourcedescription:[Lambda rotation for AWS Secrets Manager - RDS SQLServer with separate Master secret]
#snippet-service:[secretsmanager]
#snippet-keyword:[rotation function]
#snippet-keyword:[python]
#snippet-keyword:[RDS SQLServer]
#snippet-keyword:[AWS Lambda]
#snippet-keyword:[AWS Secrets Manager]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-08-22]

# Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

import boto3
import json
import logging
import os
import pymssql

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event, context):
    """Secrets Manager RDS SQL Server Handler

    This handler uses the master-user rotation scheme to rotate an RDS SQL Server user credential. During the first rotation, this
    scheme logs into the database as the master user, creates a new user (appending _clone to the username), and grants the
    new user all of the permissions from the user being rotated. Once the secret is in the state, every subsequent rotation
    simply creates a new secret with the AWSPREVIOUS user credentials, adds any missing permissions that are in the current
    secret, changes that user's password, and then marks the latest secret as AWSCURRENT.

    The Secret SecretString is expected to be a JSON string with the following format:
    {
        'engine': <required: must be set to 'sqlserver'>,
        'host': <required: instance host name>,
        'username': <required: username>,
        'password': <required: password>,
        'dbname': <optional: database name, default to 'master'>,
        'port': <optional: if not specified, default port 1433 will be used>,
        'masterarn': <required: the arn of the master secret which will be used to create users/change passwords>
    }

    Args:
        event (dict): Lambda dictionary of event parameters. These keys must include the following:
            - SecretId: The secret ARN or identifier
            - ClientRequestToken: The ClientRequestToken of the secret version
            - Step: The rotation step (one of createSecret, setSecret, testSecret, or finishSecret)

        context (LambdaContext): The Lambda runtime information

    Raises:
        ResourceNotFoundException: If the secret with the specified arn and stage does not exist

        ValueError: If the secret is not properly configured for rotation

        KeyError: If the secret json does not contain the expected keys

    """
    arn = event['SecretId']
    token = event['ClientRequestToken']
    step = event['Step']

    # Setup the client
    service_client = boto3.client('secretsmanager', endpoint_url=os.environ['SECRETS_MANAGER_ENDPOINT'])

    # Make sure the version is staged correctly
    metadata = service_client.describe_secret(SecretId=arn)
    if "RotationEnabled" in metadata and not metadata['RotationEnabled']:
        logger.error("Secret %s is not enabled for rotation" % arn)
        raise ValueError("Secret %s is not enabled for rotation" % arn)
    versions = metadata['VersionIdsToStages']
    if token not in versions:
        logger.error("Secret version %s has no stage for rotation of secret %s." % (token, arn))
        raise ValueError("Secret version %s has no stage for rotation of secret %s." % (token, arn))
    if "AWSCURRENT" in versions[token]:
        logger.info("Secret version %s already set as AWSCURRENT for secret %s." % (token, arn))
        return
    elif "AWSPENDING" not in versions[token]:
        logger.error("Secret version %s not set as AWSPENDING for rotation of secret %s." % (token, arn))
        raise ValueError("Secret version %s not set as AWSPENDING for rotation of secret %s." % (token, arn))

    # Call the appropriate step
    if step == "createSecret":
        create_secret(service_client, arn, token)

    elif step == "setSecret":
        set_secret(service_client, arn, token)

    elif step == "testSecret":
        test_secret(service_client, arn, token)

    elif step == "finishSecret":
        finish_secret(service_client, arn, token)

    else:
        logger.error("lambda_handler: Invalid step parameter %s for secret %s" % (step, arn))
        raise ValueError("Invalid step parameter %s for secret %s" % (step, arn))


def create_secret(service_client, arn, token):
    """Generate a new secret

    This method first checks for the existence of a secret for the passed in token. If one does not exist, it will generate a
    new secret and put it with the passed in token.

    Args:
        service_client (client): The secrets manager service client

        arn (string): The secret ARN or other identifier

        token (string): The ClientRequestToken associated with the secret version

    Raises:
        ValueError: If the current secret is not valid JSON

        KeyError: If the secret json does not contain the expected keys

    """
    # Make sure the current secret exists
    current_dict = get_secret_dict(service_client, arn, "AWSCURRENT")

    # Now try to get the secret version, if that fails, put a new secret
    try:
        get_secret_dict(service_client, arn, "AWSPENDING", token)
        logger.info("createSecret: Successfully retrieved secret for %s." % arn)
    except service_client.exceptions.ResourceNotFoundException:
        # Get the alternate username swapping between the original user and the user with _clone appended to it
        current_dict['username'] = get_alt_username(current_dict['username'])

        # Generate a random password
        passwd = service_client.get_random_password(ExcludeCharacters='/@"\'\\', PasswordLength=30)
        current_dict['password'] = passwd['RandomPassword']

        # Put the secret
        service_client.put_secret_value(SecretId=arn, ClientRequestToken=token, SecretString=json.dumps(current_dict), VersionStages=['AWSPENDING'])
        logger.info("createSecret: Successfully put secret for ARN %s and version %s." % (arn, token))


def set_secret(service_client, arn, token):
    """Set the pending secret in the database

    This method tries to login to the database with the AWSPENDING secret and returns on success. If that fails, it
    tries to login with the master credentials from the masterarn in the current secret. If this succeeds, it adds all
    grants for AWSCURRENT user to the AWSPENDING user, creating the user and/or setting the password in the process.
    Else, it throws a ValueError.

    Args:
        service_client (client): The secrets manager service client

        arn (string): The secret ARN or other identifier

        token (string): The ClientRequestToken associated with the secret version

    Raises:
        ResourceNotFoundException: If the secret with the specified arn and stage does not exist

        ValueError: If the secret is not valid JSON or master credentials could not be used to login to DB

        KeyError: If the secret json does not contain the expected keys

    """
    # First try to login with the pending secret, if it succeeds, return
    pending_dict = get_secret_dict(service_client, arn, "AWSPENDING", token)
    conn = get_connection(pending_dict)
    if conn:
        conn.close()
        logger.info("setSecret: AWSPENDING secret is already set as password in SQL Server DB for secret arn %s." % arn)
        return

    # Before we do anything with the secret, make sure the AWSCURRENT secret is valid by logging in to the db
    current_dict = get_secret_dict(service_client, arn, "AWSCURRENT")
    conn = get_connection(current_dict)
    if not conn:
        logger.error("setSecret: Unable to log into database using current credentials for secret %s" % arn)
        raise ValueError("Unable to log into database using current credentials for secret %s" % arn)
    conn.close()

    # Now get the master arn from the current secret
    master_arn = current_dict['masterarn']
    master_dict = get_secret_dict(service_client, master_arn, "AWSCURRENT")
    if current_dict['host'] != master_dict['host']:
        logger.warn("setSecret: Master database host %s is not the same host as current %s" % (master_dict['host'], current_dict['host']))

    # Now log into the database with the master credentials
    conn = get_connection(master_dict)
    if not conn:
        logger.error("setSecret: Unable to log into database using credentials in master secret %s" % master_arn)
        raise ValueError("Unable to log into database using credentials in master secret %s" % master_arn)

    # Now set the password to the pending password
    try:
        with conn.cursor(as_dict=True) as cursor:
            # Get the current version and db
            cursor.execute("SELECT @@VERSION AS version")
            version = cursor.fetchall()[0]['version']
            cursor.execute("SELECT DB_NAME() AS name")
            current_db = cursor.fetchall()[0]['name']

            # Determine if we are in a contained DB
            containment = 0
            if not version.startswith("Microsoft SQL Server 2008"): # SQL Server 2008 does not support contained databases
                cursor.execute("SELECT containment FROM sys.databases WHERE name = %s", current_db)
                containment = cursor.fetchall()[0]['containment']

            # Set the user or login password (depending on database containment)
            if containment == 0:
                set_password_for_login(cursor, current_db, current_dict['username'], pending_dict)
            else:
                set_password_for_user(cursor, current_dict['username'], pending_dict)

            conn.commit()
            logger.info("setSecret: Successfully created user %s in SQL Server DB for secret arn %s." % (pending_dict['username'], arn))
    finally:
        conn.close()


def test_secret(service_client, arn, token):
    """Test the pending secret against the database

    This method tries to log into the database with the secrets staged with AWSPENDING and runs
    a permissions check to ensure the user has the correct permissions.

    Args:
        service_client (client): The secrets manager service client

        arn (string): The secret ARN or other identifier

        token (string): The ClientRequestToken associated with the secret version

    Raises:
        ResourceNotFoundException: If the secret with the specified arn and stage does not exist

        ValueError: If the secret is not valid JSON or pending credentials could not be used to login to the database

        KeyError: If the secret json does not contain the expected keys

    """
    # Try to login with the pending secret, if it succeeds, return
    conn = get_connection(get_secret_dict(service_client, arn, "AWSPENDING", token))
    if conn:
        # This is where the lambda will validate the user's permissions. Uncomment/modify the below lines to
        # tailor these validations to your needs
        try:
            with conn.cursor() as cur:
                cur.execute("SELECT @@VERSION AS version")
        finally:
            conn.close()

        logger.info("testSecret: Successfully signed into SQL Server DB with AWSPENDING secret in %s." % arn)
        return
    else:
        logger.error("testSecret: Unable to log into database with pending secret of secret ARN %s" % arn)
        raise ValueError("Unable to log into database with pending secret of secret ARN %s" % arn)


def finish_secret(service_client, arn, token):
    """Finish the rotation by marking the pending secret as current

    This method moves the secret from the AWSPENDING stage to the AWSCURRENT stage.

    Args:
        service_client (client): The secrets manager service client

        arn (string): The secret ARN or other identifier

        token (string): The ClientRequestToken associated with the secret version

    Raises:
        ResourceNotFoundException: If the secret with the specified arn does not exist

    """
    # First describe the secret to get the current version
    metadata = service_client.describe_secret(SecretId=arn)
    current_version = None
    for version in metadata["VersionIdsToStages"]:
        if "AWSCURRENT" in metadata["VersionIdsToStages"][version]:
            if version == token:
                # The correct version is already marked as current, return
                logger.info("finishSecret: Version %s already marked as AWSCURRENT for %s" % (version, arn))
                return
            current_version = version
            break

    # Finalize by staging the secret version current
    service_client.update_secret_version_stage(SecretId=arn, VersionStage="AWSCURRENT", MoveToVersionId=token, RemoveFromVersionId=current_version)
    logger.info("finishSecret: Successfully set AWSCURRENT stage to version %s for secret %s." % (version, arn))


def get_connection(secret_dict):
    """Gets a connection to SQL Server DB from a secret dictionary

    This helper function tries to connect to the database grabbing connection info
    from the secret dictionary. If successful, it returns the connection, else None

    Args:
        secret_dict (dict): The Secret Dictionary

    Returns:
        Connection: The pymssql.Connection object if successful. None otherwise

    Raises:
        KeyError: If the secret json does not contain the expected keys

    """
    # Parse and validate the secret JSON string
    port = str(secret_dict['port']) if 'port' in secret_dict else '1433'
    dbname = secret_dict['dbname'] if 'dbname' in secret_dict else 'master'

    # Try to obtain a connection to the db
    try:
        conn = pymssql.connect(server=secret_dict['host'],
                               user=secret_dict['username'],
                               password=secret_dict['password'],
                               database=dbname,
                               port=port,
                               login_timeout=5,
                               as_dict=True)
        return conn
    except pymssql.OperationalError:
        return None


def get_secret_dict(service_client, arn, stage, token=None):
    """Gets the secret dictionary corresponding for the secret arn, stage, and token

    This helper function gets credentials for the arn and stage passed in and returns the dictionary by parsing the JSON string

    Args:
        service_client (client): The secrets manager service client

        arn (string): The secret ARN or other identifier

        token (string): The ClientRequestToken associated with the secret version, or None if no validation is desired

        stage (string): The stage identifying the secret version

    Returns:
        SecretDictionary: Secret dictionary

    Raises:
        ResourceNotFoundException: If the secret with the specified arn and stage does not exist

        ValueError: If the secret is not valid JSON

        KeyError: If the secret json does not contain the expected keys

    """
    required_fields = ['host', 'username', 'password']

    # Only do VersionId validation against the stage if a token is passed in
    if token:
        secret = service_client.get_secret_value(SecretId=arn, VersionId=token, VersionStage=stage)
    else:
        secret = service_client.get_secret_value(SecretId=arn, VersionStage=stage)
    plaintext = secret['SecretString']
    secret_dict = json.loads(plaintext)

    # Run validations against the secret
    if 'engine' not in secret_dict or secret_dict['engine'] != 'sqlserver':
        raise KeyError("Database engine must be set to 'sqlserver' in order to use this rotation lambda")
    for field in required_fields:
        if field not in secret_dict:
            raise KeyError("%s key is missing from secret JSON" % field)

    # Parse and return the secret JSON string
    return secret_dict


def get_alt_username(current_username):
    """Gets the alternate username for the current_username passed in

    This helper function gets the username for the alternate user based on the passed in current username.

    Args:
        current_username (client): The current username

    Returns:
        AlternateUsername: Alternate username

    Raises:
        ValueError: If the new username length would exceed the maximum allowed

    """
    clone_suffix = "_clone"
    if current_username.endswith(clone_suffix):
        return current_username[:(len(clone_suffix) * -1)]
    else:
        new_username = current_username + clone_suffix
        if len(new_username) > 128:
            raise ValueError("Unable to clone user, username length with _clone appended would exceed 128 characters")
        return new_username


def set_password_for_login(cursor, current_db, current_login, pending_dict):
    """Runs various SQL statements in order to set the login password to that of the pending secret dictionary

    This helper function runs SQL statements in order to set the login password to that of the pending secret dictionary

    Args:
        cursor (pymssql.Cursor): The pymssql Cursor object

        current_db (string): The current database that we are connected to

        current_login (string): The current user login

        pending_dict (dict): The Secret Dictionary for the pending secret

    Raises:
        pymssql.OperationalError: If there are any errors running the SQL statements

    """
    # Check if the login exists, if not create it and grant it all permissions from the current user
    # If the user exists, just update the password
    cursor.execute("SELECT name FROM sys.server_principals WHERE name = %s", pending_dict['username'])
    if len(cursor.fetchall()) == 0:
        # Create the new login
        create_login = "CREATE LOGIN %s" % pending_dict['username']
        cursor.execute(create_login + " WITH PASSWORD = %s", pending_dict['password'])

        # Only handle server level permissions if we are connected the the master DB
        if current_db == 'master':
            # Loop through the types of server permissions and grant them to the new login
            query = "SELECT state_desc, permission_name FROM sys.server_permissions perm "\
                    "JOIN sys.server_principals prin ON perm.grantee_principal_id = prin.principal_id "\
                    "WHERE prin.name = '%s'" % current_login
            cursor.execute(query)
            for row in cursor.fetchall():
                if row['state_desc'] == 'GRANT_WITH_GRANT_OPTION':
                    cursor.execute("GRANT %s TO %s WITH GRANT OPTION" % (row['permission_name'], pending_dict['username']))
                else:
                    cursor.execute("%s %s TO %s" % (row['state_desc'], row['permission_name'], pending_dict['username']))

        # We do not create user objects in the master database
        else:
            # Get the user for the current login and generate the alt user
            cursor.execute("SELECT dbprin.name FROM sys.database_principals dbprin JOIN sys.server_principals sprin ON dbprin.sid = sprin.sid WHERE sprin.name = %s", current_login)
            cur_user = cursor.fetchall()[0]['name']
            alt_user = get_alt_username(cur_user)

            # Check if the user exists. If not, create it
            cursor.execute("SELECT name FROM sys.database_principals WHERE name = %s", alt_user)
            if len(cursor.fetchall()) == 0:
                cursor.execute("CREATE USER %s FOR LOGIN %s" % (alt_user, pending_dict['username']))

            apply_database_permissions(cursor, cur_user, pending_dict['username'])

    else:
        alter_stmt = "ALTER LOGIN %s" % pending_dict['username']
        cursor.execute(alter_stmt + " WITH PASSWORD = %s", pending_dict['password'])


def set_password_for_user(cursor, current_user, pending_dict):
    """Runs various SQL statements in order to set the user password to that of the pending secret dictionary

    This helper function runs SQL statements in order to set the user password to that of the pending secret dictionary

    Args:
        cursor (pymssql.Cursor): The pymssql Cursor object

        current_user (string): The current username

        pending_dict (dict): The Secret Dictionary for the pending secret

    Raises:
        pymssql.OperationalError: If there are any errors running the SQL statements

    """
    # Check if the user exists, if not create it and grant it all permissions from the current user
    # If the user exists, just update the password
    cursor.execute("SELECT name FROM sys.database_principals WHERE name = %s", pending_dict['username'])
    if len(cursor.fetchall()) == 0:
        # Create the new user
        create_login = "CREATE USER %s" % pending_dict['username']
        cursor.execute(create_login + " WITH PASSWORD = %s", pending_dict['password'])

        apply_database_permissions(cursor, current_user, pending_dict['username'])
    else:
        alter_stmt = "ALTER USER %s" % pending_dict['username']
        cursor.execute(alter_stmt + " WITH PASSWORD = %s", pending_dict['password'])


def apply_database_permissions(cursor, current_user, pending_user):
    """Runs various SQL statements to apply the database permissions from current_user to pending_user

    This helper function runs SQL statements to apply the database permissions from current_user to pending_user

    Args:
        cursor (pymssql.Cursor): The pymssql Cursor object

        current_user (string): The current username

        pending_user (string): The pending username

    Raises:
        pymssql.OperationalError: If there are any errors running the SQL statements

        ValueError: If any database values were unexpected/invalid

    """
    # Get the roles assigned to the current user and assign it to the pending user
    query = "SELECT roleprin.name FROM sys.database_role_members rolemems "\
            "JOIN sys.database_principals roleprin ON roleprin.principal_id = rolemems.role_principal_id "\
            "JOIN sys.database_principals userprin ON userprin.principal_id = rolemems.member_principal_id "\
            "WHERE userprin.name = '%s'" % current_user
    cursor.execute(query)
    for row in cursor.fetchall():
        sql_stmt = "ALTER ROLE %s ADD MEMBER %s" % (row['name'], pending_user)

    # Loop through the database permissions and grant them to the user
    query = "SELECT "\
                "class = perm.class, "\
                "state_desc = perm.state_desc, "\
                "perm_name = perm.permission_name, "\
                "schema_name = permschem.name, "\
                "obj_name = obj.name, "\
                "obj_schema_name = objschem.name, "\
                "col_name = col.name, "\
                "imp_name = imp.name, "\
                "imp_type = imp.type, "\
                "assembly_name = assembly.name, "\
                "type_name = types.name, "\
                "type_schema = typeschem.name, "\
                "schema_coll_name = schema_coll.name, "\
                "xml_schema = xmlschem.name, "\
                "msg_type_name = msg_type.name, "\
                "contract_name = contract.name, "\
                "svc_name = svc.name, "\
                "binding_name = binding.name, "\
                "route_name = route.name, "\
                "catalog_name = catalog.name, "\
                "symkey_name = symkey.name, "\
                "cert_name = cert.name, "\
                "asymkey_name = asymkey.name "\
            "FROM sys.database_permissions perm "\
            "JOIN sys.database_principals prin ON perm.grantee_principal_id = prin.principal_id "\
            "LEFT JOIN sys.schemas permschem ON permschem.schema_id = perm.major_id "\
            "LEFT JOIN sys.objects obj ON obj.object_id = perm.major_id "\
            "LEFT JOIN sys.schemas objschem ON objschem.schema_id = obj.schema_id "\
            "LEFT JOIN sys.columns col ON col.object_id = perm.major_id AND col.column_id = perm.minor_id "\
            "LEFT JOIN sys.database_principals imp ON imp.principal_id = perm.major_id "\
            "LEFT JOIN sys.assemblies assembly ON assembly.assembly_id = perm.major_id "\
            "LEFT JOIN sys.types types ON types.user_type_id = perm.major_id "\
            "LEFT JOIN sys.schemas typeschem ON typeschem.schema_id = types.schema_id "\
            "LEFT JOIN sys.xml_schema_collections schema_coll ON schema_coll.xml_collection_id = perm.major_id "\
            "LEFT JOIN sys.schemas xmlschem ON xmlschem.schema_id = schema_coll.schema_id "\
            "LEFT JOIN sys.service_message_types msg_type ON msg_type.message_type_id = perm.major_id "\
            "LEFT JOIN sys.service_contracts contract ON contract.service_contract_id = perm.major_id "\
            "LEFT JOIN sys.services svc ON svc.service_id = perm.major_id "\
            "LEFT JOIN sys.remote_service_bindings binding ON binding.remote_service_binding_id = perm.major_id "\
            "LEFT JOIN sys.routes route ON route.route_id = perm.major_id "\
            "LEFT JOIN sys.fulltext_catalogs catalog ON catalog.fulltext_catalog_id = perm.major_id "\
            "LEFT JOIN sys.symmetric_keys symkey ON symkey.symmetric_key_id = perm.major_id "\
            "LEFT JOIN sys.certificates cert ON cert.certificate_id = perm.major_id "\
            "LEFT JOIN sys.asymmetric_keys asymkey ON asymkey.asymmetric_key_id = perm.major_id "\
            "WHERE prin.name = '%s'" % current_user
    cursor.execute(query)
    for row in cursor.fetchall():
        # Determine which type of permission this is and create the sql statement accordingly
        if row['class'] == 0: # Database permission
            permission = row['perm_name']
        elif row['class'] == 1: # Object or Column
            permission = "%s ON OBJECT::%s.%s" % (row['perm_name'], row['obj_schema_name'], row['obj_name'])
            if row['col_name']:
                permission = "%s (%s) " % (permission, row['col_name'])
        elif row['class'] == 3: # Schema
            permission = "%s ON SCHEMA::%s" % (row['perm_name'], row['schema_name'])
        elif row['class'] == 4: # Impersonation (Database Principal)
            if row['imp_type'] == 'S': # SQL User
                permission = "%s ON USER::%s" % (row['perm_name'], row['imp_name'])
            elif row['imp_type'] == 'R': # Role
                permission = "%s ON ROLE::%s" % (row['perm_name'], row['imp_name'])
            elif row['imp_type'] == 'A': # Application Role
                permission = "%s ON APPLICATION ROLE::%s" % (row['perm_name'], row['imp_name'])
            else:
                raise ValueError("Invalid database principal permission type %s" % row['imp_type'])
        elif row['class'] == 5:  # Assembly
            permission = "%s ON ASSEMBLY::%s" % (row['perm_name'], row['assembly_name'])
        elif row['class'] == 6:  # Type
            permission = "%s ON TYPE::%s.%s" % (row['perm_name'], row['type_schema'], row['type_name'])
        elif row['class'] == 10:  # XML Schema Collection
            permission = "%s ON XML SCHEMA COLLECTION::%s.%s" % (row['perm_name'], row['xml_schema'], row['schema_coll_name'])
        elif row['class'] == 15:  # Message Type
            permission = "%s ON MESSAGE TYPE::%s" % (row['perm_name'], row['msg_type_name'])
        elif row['class'] == 16:  # Service Contract
            permission = "%s ON CONTRACT::%s" % (row['perm_name'], row['contract_name'])
        elif row['class'] == 17:  # Service
            permission = "%s ON SERVICE::%s" % (row['perm_name'], row['svc_name'])
        elif row['class'] == 18:  # Remote Service Binding
            permission = "%s ON REMOTE SERVICE BINDING::%s" % (row['perm_name'], row['binding_name'])
        elif row['class'] == 19:  # Route
            permission = "%s ON ROUTE::%s" % (row['perm_name'], row['route_name'])
        elif row['class'] == 23:  # Full-Text Catalog
            permission = "%s ON FULLTEXT CATALOG::%s" % (row['perm_name'], row['catalog_name'])
        elif row['class'] == 24:  # Symmetric Key
            permission = "%s ON SYMMETRIC KEY::%s" % (row['perm_name'], row['symkey_name'])
        elif row['class'] == 25:  # Certificate
            permission = "%s ON CERTIFICATE::%s" % (row['perm_name'], row['cert_name'])
        elif row['class'] == 26:  # Asymmetric Key
            permission = "%s ON ASYMMETRIC KEY::%s" % (row['perm_name'], row['asymkey_name'])
        else:
            raise ValueError("Invalid database permission class %s" % row['class'])

        # Add the state to the statement
        if row['state_desc'] == 'GRANT_WITH_GRANT_OPTION':
            sql_stmt = "GRANT %s TO %s WITH GRANT OPTION" % (permission, pending_user)
        else:
            sql_stmt = "%s %s TO %s" % (row['state_desc'], permission, pending_user)

        # Execute the sql
        cursor.execute(sql_stmt)
