# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Implements a simplistic object-relational mapping to create PostgreSQL-compatible SQL
statements and Amazon RDS Data Service parameters from database table definitions.

This file is deployed to AWS Lambda as part of the Chalice deployment.

This code is intended for demonstration only and does not guarantee best practices.
"""

import datetime
import logging

logger = logging.getLogger(__name__)

# Maps from Python types to PostgreSQL columns types used in a CREATE TABLE statement.
COL_TYPES = {int: "int", str: "varchar", datetime.date: "date"}

# Maps from Python types to Amazon RDS Data Service types.
VALUE_KEYS = {
    bytes: "blobValue",
    bool: "booleanValue",
    float: "doubleValue",
    type(None): "isNull",
    int: "longValue",
    str: "stringValue",
    datetime.date: "stringValue",
}


class ForeignKey:
    """Defines a foreign key."""

    def __init__(self, table_name, column_name):
        self.table_name = table_name
        self.column_name = column_name

    def reference(self):
        return f"{self.table_name}({self.column_name})"


class Column:
    """Defines a column in a database table."""

    def __init__(
        self,
        name,
        data_type,
        nullable=True,
        # For a Postgres auto-increment column, set auto_increment to True and also use a data type
        # such as SERIAL or BIGSERIAL.
        auto_increment=False,
        primary_key=None,
        foreign_key=None,
    ):
        self.name = name
        self.data_type = data_type
        self.nullable = nullable
        self.auto_increment = auto_increment
        self.primary_key = primary_key
        self.foreign_key = foreign_key


class Table:
    """Defines a database table."""

    def __init__(self, name, cols):
        self.name = name
        self.cols = cols


def _make_params(values):
    """
    Makes an RDS Data Service parameter structure out of a Python dictionary.

    :param values: A Python dictionary of parameters.
    :return: The parameters as a list of dicts that can be passed to RDS Data Service.
    """
    params = []
    for key, val in values.items():
        param = {
            "name": f"{key}",
            "value": {
                VALUE_KEYS[type(val)]: (
                    str(val)
                    if isinstance(val, datetime.date)
                    else val
                    if val is not None
                    else True
                )
            },
        }
        if (
            isinstance(val, datetime.date)
            or isinstance(val, datetime.datetime)
            or isinstance(val, datetime.time)
        ):
            param["typeHint"] = "DATE"
        params.append(param)
    return params


def _make_where_parts(where_clauses):
    """
    Makes PostgreSQL-compatible WHERE clauses and associated RDS Data Service parameters
    from a Python list.

    The WHERE clause input is a list of Python dicts, each of which must be in the
    following format:
        {
            'table': 'table name',
            'column': 'column name',
            'op': 'comparison operator (such as = or >=)',
            'value': 'value of the parameter'
        }

    :param where_clauses: The list of WHERE clause dict definitions.
    :return The PostgreSQL WHERE statement and associated parameters that can be passed
            to the RDS Data Service.
    """
    sql = ""
    sql_params = None
    if where_clauses is not None:
        wheres = [
            f"{item['table']}.{item['column']} {item['op']} "
            f":{item['table']}_{item['column']}"
            for item in where_clauses
        ]
        sql = f" WHERE {' AND '.join(wheres)}"
        sql_params = _make_params(
            {
                f"{item['table']}_{item['column']}": item["value"]
                for item in where_clauses
            }
        )
    return sql, sql_params


def create_table(table):
    """
    Generates a CREATE TABLE PostgreSQL statement from a Table object.

    :param table: The Table object used to generate the PostgreSQL statement
    :return: The PostgreSQL CREATE TABLE statement.
    """
    create_clause = f"CREATE TABLE {table.name}"
    cols = []
    constraints = []
    for col in table.cols:
        # If a column is auto-incrementing, override the original data type like 'int' and coerce it to 'serial'.
        if col.auto_increment:
            clause = f"{col.name} SERIAL"
        else:
            clause = f"{col.name} {COL_TYPES[col.data_type]}"
        if not col.nullable:
            clause += " NOT NULL"
        cols.append(clause)
        if col.primary_key:
            constraints.append(f"PRIMARY KEY ({col.name})")
        if col.foreign_key is not None:
            constraints.append(
                f"FOREIGN KEY ({col.name}) REFERENCES {col.foreign_key.reference()}"
            )
    col_clause = ", ".join(cols)
    constraint_clause = ", ".join(constraints)
    sql = f"{create_clause} ({', '.join([col_clause, constraint_clause])})"
    return sql


def insert(table, value_sets):
    """
    Generates a PostgreSQL INSERT statement to insert values into a table. A single
    row can be used with execute_statement and multiple rows can be used with
    batch_execute_statement.

    :param table: The table where the values are inserted.
    :param value_sets: The rows to insert into the table. Each row is a Python dict
                       where the keys are column names and the values are the values
                       to insert into the table.
    :return: The PostgreSQL INSERT statement and parameter sets that can be passed to
             the RDS Data Service.
    """
    insert_clause = f"INSERT INTO {table.name}"
    returning_clause = "RETURNING *"
    cols = [col.name for col in table.cols if not col.auto_increment]
    vals = [f":{col}" for col in cols]
    # Currently, the RETURNING clause does not have a material effect on the result set as seen by the Data API.
    # That might not be a permanent limitation though. So set it up in case the response eventually includes
    # the columns mentioned in RETURNING.
    sql = f"{insert_clause} ({', '.join(cols)}) VALUES ({', '.join(vals)}) {returning_clause}"
    param_sets = [_make_params(values) for values in value_sets]
    return sql, param_sets


def insert_returning(table, value_sets):
    """
    Generates a PostgreSQL INSERT statement to insert values into a table, and
    use a RETURNING * clause to supply the inserted values back to the calling
    function. (The caller must treat the SQL statement like a query, and unpack
    the 'results' field of the return value.)

    Might not be needed forever, if Data API includes the RETURNING columns in the response.
    In that case, can revert back to the insert() function above.

    A single row can be used with execute_statement and multiple rows can be used with
    batch_execute_statement.

    :param table: The table where the values are inserted.
    :param value_sets: The rows to insert into the table. Each row is a Python dict
                       where the keys are column names and the values are the values
                       to insert into the table.
    :return: The PostgreSQL INSERT statement and parameter sets that can be passed to
             the RDS Data Service.
    """
    insert_clause = f"INSERT INTO {table.name}"
    returning_clause = "RETURNING *"
    cols = [col.name for col in table.cols if not col.auto_increment]
    vals = [f":{col}" for col in cols]
    sql = f"WITH derived AS ({insert_clause} ({', '.join(cols)}) VALUES ({', '.join(vals)}) {returning_clause}) SELECT * FROM derived"
    param_sets = [_make_params(values) for values in value_sets]
    return sql, param_sets


def insert_without_batch(table, values_clause):
    """
    Generates a PostgreSQL INSERT statement to insert values into a table. A single
    row can be used with execute_statement and multiple rows can be used with
    batch_execute_statement.

    :param table: The table where the values are inserted.
    :param value_sets: The rows to insert into the table. Each row is a Python dict
                       where the keys are column names and the values are the values
                       to insert into the table.
    :return: The PostgreSQL INSERT statement and parameter sets that can be passed to
             the RDS Data Service.
    """

    insert_clause = f"INSERT INTO {table.name}"
    returning_clause = "RETURNING *"
    cols = [col.name for col in table.cols if not col.auto_increment]
    # The RETURNING clause currently does not have a material effect on the result set as seen by the Data API.
    # This might not be a permanent limitation. If RETURNING does start to have an effect on the elements in
    # the response, the INSERT logic can be simplified to get rid of the surrounding
    # WITH ... (INSERT ... RETURNING) SELECT ... FROM syntax and just do INSERT ... RETURNING.
    sql = (
        f"{insert_clause} ({', '.join(cols)}) VALUES {values_clause} {returning_clause}"
    )

    return sql


def update(table_name, set_values, where_clauses):
    """
    Generates a PostgreSQL UPDATE statement to update rows in a table.

    :param table_name: The name of the table to update.
    :param set_values: The values to update as a Python dict where keys are column
                       names and values are values to update.
    :param where_clauses: A list of WHERE clauses that define which rows to update.
                          These clauses are a list of dicts as defined in the
                          _make_where_clauses function.
    :return: The PostgreSQL UPDATE statement and parameters that can be passed to the
             RDS Data Service.
    """
    set_clauses = [f"{key}=:set_{key}" for key in set_values.keys()]
    set_params = _make_params({f"set_{key}": val for key, val in set_values.items()})
    where_sql, where_params = _make_where_parts(where_clauses)
    sql = f"UPDATE {table_name} SET {', '.join(set_clauses)}{where_sql}"
    return sql, set_params + where_params


def query(primary_name, tables, where_clauses=None):
    """
    Generates a PostgreSQL SELECT statement to retrieve data. This function recursively
    walks the tree of foreign key relationships to build a query that joins all
    tables necessary to retrieve full data rows.

    :param primary_name: The name of the primary table to query.
    :param tables: The full list of tables in the database. These are used to
                   resolve foreign key relationships.
    :param where_clauses: A list of WHERE clauses that limit the data to retrieve.
                          These clauses are a list of dicts as defined in the
                          _make_where_clauses function.
    :return: The PostgreSQL SELECT statement, the list of columns that were included in
             the query, and the parameters that can be passed to the RDS Data Service.
    """
    columns = {}
    joins = []

    def build_query(table):
        for col in table.cols:
            if not col.foreign_key:
                columns[f"{table.name}.{col.name}"] = col
            else:
                joins.append(
                    f"INNER JOIN {col.foreign_key.table_name} "
                    f"ON {table.name}.{col.name}="
                    f"{col.foreign_key.table_name}.{col.foreign_key.column_name}"
                )
                build_query(tables[col.foreign_key.table_name])

    build_query(tables[primary_name])
    sql = f"SELECT {', '.join(columns.keys())} FROM {primary_name} {' '.join(joins)}"
    where_sql, sql_params = _make_where_parts(where_clauses)
    sql += where_sql

    return sql, columns, sql_params


def unpack_query_results(columns, results):
    """
    Unpacks the result of a SELECT query into a list of Python dicts.

    :param columns: The columns that map to the fields in each result record. These
                    must be in the same order as the fields in the result records,
                    and are returned as the `columns` part of the `query` function.
    :param results: The results returned from the SELECT query.
    :return: The query records as a list of Python dicts.
    """
    output = [
        {
            col_key: val.get(VALUE_KEYS[col.data_type], None)
            for col_key, col, val in zip(columns.keys(), columns.values(), record)
        }
        for record in results["records"]
    ]
    return output


def unpack_insert_results(results):
    """
    Unpacks the result of an INSERT statement.

    :param results: The results from the INSERT statement.
    :return: The ID of the inserted row.
    """
    try:
        return results["generatedFields"][0]["longValue"]
    except Exception:
        logger.exception(
            f"Error trying to unpack generatedFields value from result of INSERT statement: {str(results)}"
        )
        raise


def unpack_insert_results_v2(results):
    """
    Unpacks the result of an INSERT statement in Data API v2.
    This is a PostgreSQL INSERT statement that was processed as a query,
    with the results passed back via a RETURNING clause.

    :param results: The results from the INSERT statement.
    :return: The ID of the inserted row.
    """
    try:
        new_id = results["records"][0][0]["longValue"]
        return new_id
    except Exception:
        logger.exception(
            f"Error in unpack_insert_results_v2() trying to unpack generatedFields value from result of INSERT statement: {str(results)}"
        )
        raise


def delete(table, value_sets):
    """
    Generates a PostgreSQL DELETE statement used to delete rows from a table.

    :param table: The table to delete from.
    :param value_sets: A list of values that define the rows to delete. To delete
                       one row, specify a single value that contains the row's ID.
    :return: The PostgreSQL DELETE statement and parameter sets that can be passed to
             the RDS Data Service.
    """
    delete_clause = f"DELETE FROM {table.name}"
    wheres = [f"{col.name}=:{col.name}" for col in table.cols if col.primary_key]
    sql = f"{delete_clause} WHERE {' AND '.join(wheres)}"
    param_sets = [_make_params(values) for values in value_sets]
    return sql, param_sets
