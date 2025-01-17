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
package com.alibaba.rsqldb.parser.model.expression;

import com.alibaba.rsqldb.parser.model.Field;
import com.alibaba.rsqldb.parser.model.Operator;
import com.alibaba.rsqldb.parser.model.baseType.Literal;
import com.alibaba.rsqldb.parser.model.baseType.MultiLiteral;
import com.alibaba.rsqldb.parser.model.baseType.StringType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

//in("123", "1122", "221")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultiValueExpression extends SingleExpression {
    private MultiLiteral values;

    @JsonCreator
    public MultiValueExpression(@JsonProperty("content") String content,
                                @JsonProperty("fieldName") Field field,
                                @JsonProperty("values") MultiLiteral values) {
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
        String fieldName = this.getField().getFieldName();
        JsonNode node = jsonNode.get(fieldName);
        if (node == null) {
            return this.values == null;
        }

        boolean value = false;

        List<Literal<?>> literals = values.getLiterals();
        for (Literal<?> literal : literals) {
            value = super.isEqual(node, literal);
            if (value) {
                break;
            }
        }

        return value;
    }
}
