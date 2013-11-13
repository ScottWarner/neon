package com.ncc.neon.services

import com.ncc.neon.query.filter.*
import groovy.mock.interceptor.MockFor
import org.junit.Before
import org.junit.Test
/*
 * ************************************************************************
 * Copyright (c), 2013 Next Century Corporation. All Rights Reserved.
 *
 * This software code is the exclusive property of Next Century Corporation and is
 * protected by United States and International laws relating to the protection
 * of intellectual property.  Distribution of this software code by or to an
 * unauthorized party, or removal of any of these notices, is strictly
 * prohibited and punishable by law.
 *
 * UNLESS PROVIDED OTHERWISE IN A LICENSE AGREEMENT GOVERNING THE USE OF THIS
 * SOFTWARE, TO WHICH YOU ARE AN AUTHORIZED PARTY, THIS SOFTWARE CODE HAS BEEN
 * ACQUIRED BY YOU "AS IS" AND WITHOUT WARRANTY OF ANY KIND.  ANY USE BY YOU OF
 * THIS SOFTWARE CODE IS AT YOUR OWN RISK.  ALL WARRANTIES OF ANY KIND, EITHER
 * EXPRESSED OR IMPLIED, INCLUDING, WITHOUT LIMITATION, IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, ARE HEREBY EXPRESSLY
 * DISCLAIMED.
 *
 * PROPRIETARY AND CONFIDENTIAL TRADE SECRET MATERIAL NOT FOR DISCLOSURE OUTSIDE
 * OF NEXT CENTURY CORPORATION EXCEPT BY PRIOR WRITTEN PERMISSION AND WHEN
 * RECIPIENT IS UNDER OBLIGATION TO MAINTAIN SECRECY.
 *
 * @author tbrooks
 */

class FilterServiceTest {

    private static final String UUID_STRING = "1af29529-86bb-4f2c-9928-7f4484b9cc49"
    private FilterService filterService
    private FilterKey filterKey
    private DataSet dataSet

    @Before
    void before() {
        filterService = new FilterService()
        dataSet = new DataSet(databaseName: "testDB", tableName: "testTable")
        filterKey = new FilterKey(uuid: UUID.fromString(UUID_STRING),
                dataSet: dataSet)
    }

    @Test
    void "register for filter key"() {
        FilterEvent event = filterService.registerForFilterKey(dataSet)
        assert event.dataSet == dataSet
        assert event.uuid
    }

    @Test
    void "replace filter"() {
        def filter = new Filter(databaseName: dataSet.databaseName, tableName: dataSet.tableName)

        def filterStateMock = new MockFor(FilterState)
        filterStateMock.demand.removeFilter { key -> assert key == filterKey }
        filterStateMock.demand.addFilter { key, f -> assert key == filterKey; f.is(filter)}
        def filterState = filterStateMock.proxyInstance()

        filterService.filterState = filterState
        filterService.replaceFilter(new FilterContainer(filterKey: filterKey, filter: filter))
        filterStateMock.verify(filterState)
    }

}