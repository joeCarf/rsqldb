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
package com.alibaba.rsqldb.common;

public class RSQLConstant {

    public static final String STAR = "*";

    public static final String BODY_TYPE = "body.type";

    public static final String COMMAND_OPERATOR = "command_operator";

    public static final String TABLE_TYPE = "table.type";
    public enum TableType {
        SINK, SOURCE
    }

    public static final String COUNT = "COUNT_KEY";

    public static final String SUM = "SUM_KEY";


    public static class Properties {
        public static final String TYPE = "type";
        public static final String TOPIC = "topic";
        public static final String DATA_FORMAT = "data_format";
    }


}
