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
package com.alibaba.rsqldb.parser.parser.builder;

import com.alibaba.rsqldb.parser.parser.result.VarParseResult;
import org.apache.calcite.sql.SqlNode;
import org.apache.rocketmq.streams.script.function.model.FunctionType;
import org.apache.rocketmq.streams.script.operator.impl.ScriptOperator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LateralTableBuilder extends SelectSQLBuilder {

    /**
     * as 对应的表名
     */
    protected String tableAiasName;

    protected Set<String> fieldNames = new HashSet<>();

    @Override
    public void buildSQL() {
        if (scripts.size() == 0) {
            return;
        }
        StringBuilder scriptValue = new StringBuilder();
        for (String script : scripts) {
            scriptValue.append(script);
        }
        getPipelineBuilder().addChainStage(new ScriptOperator(scriptValue.toString()));
    }
    @Override
    public String getFieldName(String fieldName, boolean containsSelf) {
        String name=super.getFieldName(fieldName,containsSelf);
        if(name==null&&fieldName.toLowerCase().startsWith(FunctionType.UDTF.getName())){
            return fieldName;
        }
        return name;
    }
    /**
     * 如果有别名，必须加别名
     *
     * @param fieldName
     * @return
     */
    @Override
    public String getFieldName(String fieldName) {
        String name = doAllFieldName(fieldName);
        if (name != null) {
            return name;
        }
        int index = fieldName.indexOf(".");
        if (index != -1 && getAsName() == null) {
            //这里是特殊处理，如果udtf中有嵌套函数，此时用的是主表字段，需要可以直接返回
            return fieldName.substring(index + 1);
        }
        String asName = getAsName() == null ? "" : getAsName() + ".";
        if (index == -1) {
            if (getAsName() == null && (fieldNames == null
                || fieldNames.size() == 0)) {//这里是特殊处理，如果udtf中有嵌套函数，此时用的是主表字段，需要可以直接返回
                return fieldName;
            }
            if (fieldNames.contains(fieldName)) {
                return asName + fieldName;
            }
        }
        if (getAsName() == null) {
            return fieldName;
        }

        if (index != -1) {
            asName = fieldName.substring(0, index);
        }

        if (!asName.equals(getAsName())) {
            return null;
        } else {
            return fieldName;
        }

    }

    @Override
    public Set<String> parseDependentTables() {
        return new HashSet<>();
    }

    public String getTableAiasName() {
        return tableAiasName;
    }

    public void setTableAiasName(String tableAiasName) {
        this.tableAiasName = tableAiasName;
    }

    public void addFields(List<SqlNode> nodeList) {
        String varName = asName == null ? "" : asName + ".";
        for (int i = 2; i < nodeList.size(); i++) {
            String name = nodeList.get(i).toString();
            fieldNames.add(name);
            String script = varName + name + "=" + FunctionType.UDTF.getName()  + (i - 2) + ";";
            this.scripts.add(script);
            putSelectField(name, new VarParseResult(name));
        }
    }
}
