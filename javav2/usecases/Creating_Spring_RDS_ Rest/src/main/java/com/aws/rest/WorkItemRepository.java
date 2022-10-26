/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.RdsDataException;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component()
public class WorkItemRepository implements CrudRepository<WorkItem, String> {
    static final String active = "0";
    static final String archived = "1";
    static final String database = "jobs";
    static final String secretArn = "arn:aws:secretsmanager:us-east-1:814548047983:secret:sqlscott2-WEJX1b";
    static final String resourceArn = "arn:aws:rds:us-east-1:814548047983:cluster:database-4";

    static RdsDataClient getClient() {
        return RdsDataClient.builder().region(App.region).build();
    }

    static ExecuteStatementResponse execute(String sqlStatement, List<SqlParameter> parameters) {
        var sqlRequest = ExecuteStatementRequest.builder()
            .resourceArn(resourceArn)
            .secretArn(secretArn)
            .database(database)
            .sql(sqlStatement)
            .parameters(parameters)
            .build();
        return getClient().executeStatement(sqlRequest);
    }

    static SqlParameter param(String name, String value) {
        return SqlParameter.builder().name(name).value(Field.builder().stringValue(value).build()).build();
    }

    @Override
    public <S extends WorkItem> S save(S item) {
        String name = item.getName();
        String guide = item.getGuide();
        String description = item.getDescription();
        String status = item.getStatus();
        String archived = "0";

        UUID uuid = UUID.randomUUID();
        String workId = uuid.toString();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String sDate1 = dtf.format(now);
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date1 != null;
        java.sql.Date sqlDate = new java.sql.Date(date1.getTime());

        String sql = "INSERT INTO work (idwork, username, date, description, guide, status, archive) VALUES" +
            "(:idwork, :username, :date, :description, :guide, :status, :archive);";
        List<SqlParameter> paremeters = List.of(
            param("idwork", workId),
            param("username", name),
            param("date", sqlDate.toString()),
            param("description", description),
            param("guide", guide),
            param("status", status),
            param("archive", archived)
        );

        ExecuteStatementResponse result = execute(sql, paremeters);
        System.out.println(result.toString());
        return (S) findById(workId).get();
    }

    @Override
    public <S extends WorkItem> Iterable<S> saveAll(Iterable<S> entities) {
        return StreamSupport.stream(entities.spliterator(), true).map(this::save)::iterator;
    }

    @Override
    public Optional<WorkItem> findById(String s) {
        String sqlStatement = "SELECT idwork, date, description, guide, status, username FROM work WHERE idwork = :id;";
        List<SqlParameter> parameters = List.of(param("id", s));
        var result = execute(sqlStatement, parameters)
            .records()
            .stream()
            .map(WorkItem::from)
            .collect(Collectors.toUnmodifiableList());
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    @Override
    public boolean existsById(String s) {
        return findById(s).isPresent();
    }

    @Override
    public Iterable<WorkItem> findAll() {
        return findAllWithStatus(active);
    }

    public void flipItemArchive(String id) {
        try {
            String sqlStatement = "UPDATE work SET archive = (:arch) WHERE idwork = (:id);";
            List<SqlParameter> parameters = List.of(
                param("id", id),
                param("arch", archived)
            );
           execute(sqlStatement, parameters);
        } catch (RdsDataException e) {
            e.printStackTrace();
        }
    }


    public Iterable<WorkItem> findAllWithStatus(String status) {
        String sqlStatement;
        String isArc;

        if (status.compareTo("true") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username " +
                "FROM work WHERE archive = :arch ;";
            isArc = "1";
            List<SqlParameter> parameters = List.of(
                param("arch", isArc)
            );
            return execute(sqlStatement, parameters)
                .records()
                .stream()
                .map(WorkItem::from)
                .collect(Collectors.toUnmodifiableList());

        } else if (status.compareTo("false") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username " +
                "FROM work WHERE archive = :arch ;";
            isArc = "0";
            List<SqlParameter> parameters = List.of(
                param("arch", isArc)
            );
            return execute(sqlStatement, parameters)
                .records()
                .stream()
                .map(WorkItem::from)
                .collect(Collectors.toUnmodifiableList());

        } else {
            sqlStatement = "SELECT idwork, date, description, guide, status, username FROM work ;";
            List<SqlParameter> parameters = List.of(

            );
            return execute(sqlStatement, parameters)
                .records()
                .stream()
                .map(WorkItem::from)
                .collect(Collectors.toUnmodifiableList());
        }
    }

    @Override
    public Iterable<WorkItem> findAllById(Iterable<String> strings) {
        var item = findById(strings.iterator().next());
        if (item.isPresent()) {
            return List.of(item.get());
        }
        return List.of();
    }

    @Override
    public long count() {
        String sqlStatement = "SELECT COUNT(idwork) AS count FROM work;";
        List<SqlParameter> parameters = List.of();
        return execute(sqlStatement, parameters)
            .records()
            .stream()
            .map(fields -> fields.get(0).longValue()).iterator().next();
    }

    @Override
    public void deleteById(String s) {
        String sqlStatement = "DELETE FROM work WHERE idwork = :id;";
        List<SqlParameter> parameters = List.of(param("id", s));
        execute(sqlStatement, parameters);
    }

    @Override
    public void delete(WorkItem entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        strings.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends WorkItem> entities) {
        deleteAllById(StreamSupport.stream(entities.spliterator(), false).map(WorkItem::getId)::iterator);
    }

    @Override
    public void deleteAll() {
        String sqlStatement = "DELETE FROM work;";
        List<SqlParameter> parameters = List.of();
        execute(sqlStatement, parameters);
    }
}
