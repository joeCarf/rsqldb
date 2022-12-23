/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.rsqldb.parser.model.expression;

import com.alibaba.rsqldb.parser.model.Field;
import com.alibaba.rsqldb.parser.model.Operator;
import com.alibaba.rsqldb.parser.model.baseType.Literal;
import com.alibaba.rsqldb.parser.model.baseType.MultiLiteral;
import com.fasterxml.jackson.databind.JsonNode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

//in("123", "1122", "221")
public class MultiValueExpression extends SingleExpression {
    private MultiLiteral values;


    public MultiValueExpression(String content, Field field, MultiLiteral values) {
        super(content, field, Operator.IN);
        this.values = values;
    }

    public MultiLiteral getValues() {
        return values;
    }

    public void setValues(MultiLiteral values) {
        this.values = values;
    }

    @Override
    public Operator getOperator() {
        return Operator.IN;
    }

    @Override
    public boolean isTrue(JsonNode jsonNode) {
        String fieldName = this.getFieldName().getFieldName();
        JsonNode node = jsonNode.get(fieldName);

        String value = node.asText();

        List<Literal<?>> literals = values.getLiterals();
        for (Literal<?> literal : literals) {
            String target = String.valueOf(literal.getResult());
            if (StringUtils.equalsIgnoreCase(value, target)) {
                return true;
            }
        }
        return false;
    }
}
