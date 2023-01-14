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
package com.alibaba.rsqldb.rest.store;

import com.alibaba.rsqldb.parser.model.Node;

import java.util.concurrent.CompletableFuture;

public class CommandResult {
    private String jobId;
    private CommandStatus status;
    private Node node;

    private CompletableFuture<Throwable> putCommandFuture;

    public CommandResult(String jobId, CommandStatus status, Node node, CompletableFuture<Throwable> putCommandFuture) {
        this.jobId = jobId;
        this.status = status;
        this.node = node;
        this.putCommandFuture = putCommandFuture;
    }

    public CommandResult(String jobId, CommandStatus status, Node node) {
        this.jobId = jobId;
        this.status = status;
        this.node = node;

    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public CommandStatus getStatus() {
        return status;
    }

    public void setStatus(CommandStatus status) {
        this.status = status;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void onCompleted() {
        if (this.putCommandFuture != null) {
            this.putCommandFuture.complete(null);
        }
    }

    public void onError(Throwable attachment) {
        if (this.putCommandFuture != null) {
            this.putCommandFuture.complete(attachment);
        }
    }

    public CompletableFuture<Throwable> getPutCommandFuture() {
        return putCommandFuture;
    }

    public void setPutCommandFuture(CompletableFuture<Throwable> putCommandFuture) {
        this.putCommandFuture = putCommandFuture;
    }

    @Override
    public String toString() {
        return "Node=["
                + node.getContent()
                + "]";
    }
}
