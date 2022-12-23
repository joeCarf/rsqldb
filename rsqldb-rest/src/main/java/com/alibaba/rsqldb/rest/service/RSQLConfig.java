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
package com.alibaba.rsqldb.rest.service;

import org.springframework.stereotype.Service;

@Service
public class RSQLConfig {
    public static String SQL_TOPIC_NAME = System.getProperty("sqlTopicName","RSQLDB-SQL-TOPIC");
    public static String SQL_GROUP_NAME = System.getProperty("sqlGroupName","RSQLDB-SQL-GROUP");
    public static int SQL_QUEUE_NUM = Integer.parseInt(System.getProperty("queueNum","8"));

    public String namesrvAddr;
    public int threadNum;


    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }


    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
}
