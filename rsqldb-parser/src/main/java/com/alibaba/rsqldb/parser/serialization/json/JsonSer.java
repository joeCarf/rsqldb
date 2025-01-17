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
package com.alibaba.rsqldb.parser.serialization.json;

import com.alibaba.rsqldb.common.exception.SerializeException;
import com.alibaba.rsqldb.parser.serialization.Serializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSer implements Serializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
    }

    public byte[] serialize(Object obj) throws SerializeException {
        if (obj == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new SerializeException(e);
        }
    }

    @Override
    public byte[] serialize(Object key, Object value) throws SerializeException {
//        if (key == null) {
//            return this.serialize(value);
//        }

        return this.serialize(value);

//        try {
//            ObjectNode objectNode = objectMapper.createObjectNode();
//
//            String valueAsString = objectMapper.writeValueAsString(value);
//            JsonNode valueJsonNode = objectMapper.readTree(valueAsString);
//
//            if (key.getClass().isPrimitive()) {
//                objectNode.set(String.valueOf(key), valueJsonNode);
//            } else {
//                throw new UnsupportedOperationException("key is not primitive.");
//            }
//
//            String result = objectNode.toPrettyString();
//
//            return objectMapper.writeValueAsBytes(result);
//        } catch (Throwable t) {
//            throw new SerializeException(t);
//        }
    }
}
