package com.ncc.neon.services

import com.ncc.neon.metadata.model.widget.WidgetInitializationMetadata
import com.ncc.neon.result.MetadataResolver
import com.ncc.neon.state.WidgetStates
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
 * 
 */

class WidgetStateServiceTest {

    private WidgetStateService service

    @Before
    void setup(){
        service = new WidgetStateService()
        service.widgetStates = new WidgetStates()
    }

    @Test
    void "add and restore widget state"() {
        service.saveState("id", "state")
        assert service.restoreState("id") == "state"
    }

    @Test
    void "restore widget state that doesn't exist"() {
        assert !service.restoreState("id")
    }

    @Test
    void "object is not found in metadata store"() {
        def resolver = [getWidgetInitializationData : {
            widgetName -> new WidgetInitializationMetadata(widgetName: widgetName)
        }] as MetadataResolver
        service.metadataResolver = resolver
        assert !service.getWidgetInitialization("widget")
    }

    @Test
    void "object is found in metadata store"() {
        String data = "data"
        def resolver = [getWidgetInitializationData : {
            widgetName -> new WidgetInitializationMetadata(widgetName: widgetName, initDataJson: data)
        }] as MetadataResolver
        service.metadataResolver = resolver
        assert service.getWidgetInitialization("widget") == data
    }
}
