/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * InventoryCountServices.groovy
 *
 * Services for managing InventoryCount entities, including:
 * - Detail retrieval (count + items + active lock)
 * - List/filter queries
 * - Status lifecycle transitions (append-only history)
 */

import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.util.EntityQuery
import org.apache.ofbiz.service.ServiceUtil

/**
 * getInventoryCountDetail
 *
 * Fetches a single InventoryCount record along with its items.
 */
Map getInventoryCountDetail() {
    String inventoryCountId = parameters.inventoryCountId

    def inventoryCount = EntityQuery.use(delegator)
            .from("InventoryCount")
            .where("inventoryCountId", inventoryCountId)
            .queryOne()

    if (!inventoryCount) {
        return ServiceUtil.returnError("InventoryCount not found: ${inventoryCountId}")
    }

    def countItems = EntityQuery.use(delegator)
            .from("InventoryCountItem")
            .where("inventoryCountId", inventoryCountId)
            .orderBy("countItemSeqId")
            .queryList()

    Map result = ServiceUtil.returnSuccess()
    result.inventoryCount = inventoryCount
    result.countItems     = countItems ?: []
    return result
}

/**
 * findInventoryCounts
 *
 * Returns a filtered list of InventoryCount records.
 * All filter parameters are optional and combined with AND.
 */
Map findInventoryCounts() {
    def conditions = []

    if (parameters.facilityId) {
        conditions << EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, parameters.facilityId)
    }
    if (parameters.statusId) {
        conditions << EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, parameters.statusId)
    }
    if (parameters.assignedUserLoginId) {
        conditions << EntityCondition.makeCondition("assignedUserLoginId", EntityOperator.EQUALS, parameters.assignedUserLoginId)
    }

    def query = EntityQuery.use(delegator).from("InventoryCount").orderBy("-createdDate")

    if (conditions) {
        query = query.where(EntityCondition.makeCondition(conditions, EntityOperator.AND))
    }

    Map result = ServiceUtil.returnSuccess()
    result.inventoryCounts = query.queryList() ?: []
    return result
}

