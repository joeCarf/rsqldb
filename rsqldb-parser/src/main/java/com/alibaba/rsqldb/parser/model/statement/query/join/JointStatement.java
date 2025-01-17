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
package com.alibaba.rsqldb.parser.model.statement.query.join;

import com.alibaba.rsqldb.common.exception.RSQLServerException;
import com.alibaba.rsqldb.common.exception.SyntaxErrorException;
import com.alibaba.rsqldb.parser.impl.BuildContext;
import com.alibaba.rsqldb.parser.impl.ParserConstant;
import com.alibaba.rsqldb.parser.model.Calculator;
import com.alibaba.rsqldb.parser.model.Field;
import com.alibaba.rsqldb.parser.model.statement.query.QueryStatement;
import com.alibaba.rsqldb.parser.model.statement.query.phrase.JoinCondition;
import com.alibaba.rsqldb.parser.model.statement.query.phrase.JoinType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.streams.core.rstream.JoinedStream;
import org.apache.rocketmq.streams.core.rstream.RStream;
import org.apache.rocketmq.streams.core.util.Pair;
import org.apache.rocketmq.streams.core.window.Time;
import org.apache.rocketmq.streams.core.window.WindowBuilder;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JointStatement extends QueryStatement {
    private JoinType joinType;
    private String asSourceTableName;
    private String joinTableName;
    private String asJoinTableName;

    private JoinCondition joinCondition;

    @JsonCreator
    public JointStatement(@JsonProperty("content") String content, @JsonProperty("tableName") String tableName,
                          @JsonProperty("selectFieldAndCalculator") Map<Field, Calculator> selectFieldAndCalculator,
                          @JsonProperty("joinType") JoinType joinType, @JsonProperty("asSourceTableName") String asSourceTableName,
                          @JsonProperty("joinTableName") String joinTableName, @JsonProperty("asJoinTableName") String asJoinTableName,
                          @JsonProperty("joinCondition") JoinCondition joinCondition) {
        super(content, tableName, selectFieldAndCalculator);
        this.joinType = joinType;
        this.asSourceTableName = asSourceTableName;
        this.joinTableName = joinTableName;
        this.asJoinTableName = asJoinTableName;
        this.joinCondition = joinCondition;
        validator();
    }

    public String getAsSourceTableName() {
        return asSourceTableName;
    }

    public void setAsSourceTableName(String asSourceTableName) {
        this.asSourceTableName = asSourceTableName;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public void setJoinTableName(String joinTableName) {
        this.joinTableName = joinTableName;
    }

    public String getAsJoinTableName() {
        return asJoinTableName;
    }

    public void setAsJoinTableName(String asJoinTableName) {
        this.asJoinTableName = asJoinTableName;
    }

    public JoinCondition getJoinCondition() {
        return joinCondition;
    }

    public void setJoinCondition(JoinCondition joinCondition) {
        this.joinCondition = joinCondition;
    }

    public void validator() {
        //joinCondition 中key的tablename=asSourceTableName/table
        //value的tableName = asJoinTableName/joinTableName
        List<Pair<Field, Field>> conditionHolder = joinCondition.getHolder();
        for (Pair<Field, Field> pair : conditionHolder) {
            Field left = pair.getKey();
            String leftTableName = left.getTableName();

            if (!StringUtils.isEmpty(leftTableName) && (!leftTableName.equals(this.getTableName()) && !leftTableName.equals(asSourceTableName))
            && !(leftTableName.equals(joinTableName)) && !(leftTableName.equals(asJoinTableName))) {
                throw new SyntaxErrorException("left table " + leftTableName +
                        " not equals to table name=" + this.getTableName() + " or asTable name=" + asSourceTableName + ".sql=" + this.getContent());
            }

            Field right = pair.getValue();
            String rightTableName = right.getTableName();
            if (!StringUtils.isEmpty(rightTableName) && (!rightTableName.equals(joinTableName) && !rightTableName.equals(asJoinTableName))
            && !(rightTableName.equals(this.getTableName())) && !(rightTableName.equals(asSourceTableName))) {
                throw new SyntaxErrorException("right table " + rightTableName +
                        " not equals to join table name=" + joinTableName + " or join asTable name=" + asJoinTableName + ".sql=" + this.getContent());
            }
        }

        for (Field field : this.getSelectFieldAndCalculator().keySet()) {
            String tableName = field.getTableName();

            if (StringUtils.isEmpty(tableName)) {
                throw new SyntaxErrorException("table in join select is null. sql=" + this.getContent());
            }

            if (!tableName.equals(this.getTableName())
                    && !tableName.equals(asSourceTableName)
                    && !tableName.equals(joinTableName)
                    && !tableName.equals(asJoinTableName)) {
                throw new SyntaxErrorException("table in field=" + tableName
                        + " not equals to source table name=" + this.getTableName() + ", not equals to source asTable name=" + asSourceTableName
                        + ".or not equals to join table name=" + joinTableName + ", not equals to join asTable name=" + asJoinTableName
                        + ".sql=" + this.getContent());
            }
        }
    }

    @Override
    public BuildContext build(BuildContext context) throws Throwable {
        RStream<JsonNode> leftStream = context.getRStreamSource(this.getTableName());
        RStream<JsonNode> rightStream = context.getRStreamSource(this.joinTableName);

        //join
        RStream<JsonNode> rStream = join(leftStream, rightStream);

        //select
        buildSelectItem(rStream, context);

        return context;
    }

    protected RStream<JsonNode> join(RStream<JsonNode> leftStream, RStream<JsonNode> rightStream) {
        JoinedStream<JsonNode, JsonNode> joinedStream;
        switch (joinType) {
            case LEFT_JOIN: {
                joinedStream = leftStream.leftJoin(rightStream);
                break;
            }
            case INNER_JOIN: {
                joinedStream = leftStream.join(rightStream);
                break;
            }
            default: {
                throw new RSQLServerException("unknown join type=" + joinType);
            }
        }

        List<Pair<Field, Field>> conditionHolder = joinCondition.getHolder();

        return joinedStream.where(value -> {
                    ObjectNode result = JsonNodeFactory.instance.objectNode();

                    for (Pair<Field, Field> pair : conditionHolder) {
                        Field leftField = pair.getKey();
                        String fieldName = leftField.getFieldName();

                        JsonNode node = value.get(fieldName);

                        result.set(fieldName, node);
                    }

                    return result;
                }).equalTo(value -> {
                    ObjectNode result = JsonNodeFactory.instance.objectNode();

                    for (Pair<Field, Field> pair : conditionHolder) {
                        Field leftField = pair.getValue();
                        String fieldName = leftField.getFieldName();

                        JsonNode node = value.get(fieldName);

                        result.set(fieldName, node);
                    }

                    return result;
                }).window(WindowBuilder.tumblingWindow(Time.seconds(10)))
                .apply((value1, value2) -> {
                    ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
                    //新建临时表

                    //放入出现在select中的所有字段，字段名为fieldName，如果字段上有运算，先不做运算
                    for (Field field : JointStatement.this.getSelectFieldAndCalculator().keySet()) {
                        String tableName = field.getTableName();
                        String fieldName = field.getFieldName();

                        JsonNode node;
                        if (tableName.equals(JointStatement.this.getTableName()) || tableName.equals(JointStatement.this.asSourceTableName)) {
                            //左侧流 从value1中取值
                            node = value1.get(fieldName);
                        } else if (tableName.equals(JointStatement.this.joinTableName) || tableName.equals(JointStatement.this.asJoinTableName)) {
                            //右侧流，从value2中取值
                            node = value2.get(fieldName);
                        } else {
                            throw new SyntaxErrorException("can not find a match table name.tableName=" + tableName + ", sql=" + JointStatement.this.getContent());
                        }

                        objectNode.set(buildKey(tableName, fieldName), node);

                        //使用临时表名代替Field中的tableName,防止误用
//                        field.setTableName(ParserConstant.JOIN_TEMPORARY);
                    }

                    //父类中再做最后计算；
                    return objectNode;
                });

    }

    private String buildKey(String tableName, String fieldName) {
        return String.join("@", tableName, fieldName);
    }
}
