 /*
  * Copyright 1999-2018 Alibaba Group Holding Ltd.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 package com.alibaba.rsqldb.parser.query;

 import com.alibaba.rsqldb.parser.SerDer;
 import com.alibaba.rsqldb.parser.model.Calculator;
 import com.alibaba.rsqldb.parser.model.Field;
 import com.alibaba.rsqldb.parser.model.statement.query.QueryStatement;
 import org.junit.Test;

 import java.util.Map;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;

 public class QueryStatementParser extends SerDer {
     @Test
     public void query1() throws Throwable {
         String sql = "select field_1\n" +
                 "     , field_2\n" +
                 "     , field_3\n" +
                 "     , field_4\n" +
                 "from rocketmq_source;";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(4, fieldAndCalculator.size());

         for (Calculator calculator : fieldAndCalculator.values()) {
             assertNull(calculator);
         }
     }

     @Test
     public void query2() throws Throwable {
         String sql = "select field_1\n" +
                 "     , avg(field_2)\n" +
                 "     , max(field_3)\n" +
                 "     , count(field_4)\n" +
                 "from rocketmq_source;";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(4, fieldAndCalculator.size());

         for (Field field : fieldAndCalculator.keySet()) {
             String fieldName = field.getFieldName();
             if (fieldName.equals("field_1")) {
                 Calculator calculator = fieldAndCalculator.get(field);
                 assertNull(calculator);
             } else if (fieldName.equals("field_2")) {
                 Calculator calculator = fieldAndCalculator.get(field);
                 assertEquals(Calculator.AVG, calculator);
             } else if (fieldName.equals("field_3")) {
                 Calculator calculator = fieldAndCalculator.get(field);
                 assertEquals(Calculator.MAX, calculator);
             } else if (fieldName.equals("field_4")) {
                 Calculator calculator = fieldAndCalculator.get(field);
                 assertEquals(Calculator.COUNT, calculator);
             } else {
                 throw new IllegalStateException("unknown fieldName:" + fieldName);
             }
         }
     }

     @Test
     public void query3() throws Throwable {
         String sql = "select * from rocketmq_source;";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(1, fieldAndCalculator.size());

         for (Field field : fieldAndCalculator.keySet()) {
             String tableName = field.getTableName();
             String fieldName = field.getFieldName();
             String asFieldName = field.getAsFieldName();

             assertNull(tableName);
             assertNull(asFieldName);
             assertEquals("*", fieldName);
         }
     }

     @Test
     public void query4() throws Throwable {
         String sql = "select oldName as newName from rocketmq_source;";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(1, fieldAndCalculator.size());

         for (Field field : fieldAndCalculator.keySet()) {
             String tableName = field.getTableName();
             String fieldName = field.getFieldName();
             String asFieldName = field.getAsFieldName();

             assertNull(tableName);

             assertEquals("oldName", fieldName);
             assertEquals("newName", asFieldName);
         }
     }


     @Test
     public void query5() throws Throwable {
         String sql = "select `tableName`.`fei_e3` as newName from rocketmq_source";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(1, fieldAndCalculator.size());

         for (Field field : fieldAndCalculator.keySet()) {
             String tableName = field.getTableName();
             String fieldName = field.getFieldName();
             String asFieldName = field.getAsFieldName();

             assertEquals("tableName", tableName);
             assertEquals("fei_e3", fieldName);
             assertEquals("newName", asFieldName);
         }
     }

     @Test
     public void query6() throws Throwable {
         String sql = "select count(`fieldName`) as newName from rocketmq_source;";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(1, fieldAndCalculator.size());

         for (Field field : fieldAndCalculator.keySet()) {
             String tableName = field.getTableName();
             String fieldName = field.getFieldName();
             String asFieldName = field.getAsFieldName();

             assertNull(tableName);

             assertEquals("fieldName", fieldName);
             assertEquals("newName", asFieldName);

             Calculator calculator = fieldAndCalculator.get(field);
             assertSame(Calculator.COUNT, calculator);
         }
     }

     @Test
     public void query7() throws Throwable {
         String sql = "select count(*) as newName from rocketmq_source;";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(1, fieldAndCalculator.size());

         for (Field field : fieldAndCalculator.keySet()) {
             String tableName = field.getTableName();
             String fieldName = field.getFieldName();
             String asFieldName = field.getAsFieldName();

             assertNull(tableName);

             assertEquals("*", fieldName);
             assertEquals("newName", asFieldName);

             Calculator calculator = fieldAndCalculator.get(field);
             assertSame(Calculator.COUNT, calculator);
         }
     }

     @Test
     public void query8() throws Throwable {
         String sql = "select count(tableName.fieldName) as newName from rocketmq_source";

         QueryStatement queryStatement = parser(sql, QueryStatement.class);
         assertEquals("rocketmq_source", queryStatement.getTableName());

         Map<Field, Calculator> fieldAndCalculator = queryStatement.getSelectFieldAndCalculator();
         assertEquals(1, fieldAndCalculator.size());

         for (Field field : fieldAndCalculator.keySet()) {
             String tableName = field.getTableName();
             String fieldName = field.getFieldName();
             String asFieldName = field.getAsFieldName();

             assertEquals("tableName", tableName);
             assertEquals("fieldName", fieldName);
             assertEquals("newName", asFieldName);

             Calculator calculator = fieldAndCalculator.get(field);
             assertSame(Calculator.COUNT, calculator);
         }
     }
 }