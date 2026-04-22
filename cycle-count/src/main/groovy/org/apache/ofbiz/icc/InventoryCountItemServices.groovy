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
 * InventoryCountItemServices.groovy
 *
 * Services for managing InventoryCountItem records:
 * - Filtered item queries
 * - Idempotent bulk sync for PWA offline-first devices
 */

import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.condition.EntityOperator
import org.apache.ofbiz.entity.util.EntityQuery
import org.apache.ofbiz.service.ServiceUtil

/**
 * getInventoryCountItems
 *
 * Returns the count items for a given InventoryCount.
 */
Map getInventoryCountItems() {
    String inventoryCountId = parameters.inventoryCountId

    def countItems = EntityQuery.use(delegator)
            .from("InventoryCountItem")
            .where("inventoryCountId", inventoryCountId)
            .orderBy("countItemSeqId")
            .queryList()

    Map result = ServiceUtil.returnSuccess()
    result.countItems = countItems ?: []
    return result
}

