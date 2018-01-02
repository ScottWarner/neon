//
// TODO: Determine if this is needed, since transitioning to the REST client

///*
// * Copyright 2014 Next Century Corporation
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// */
//
//package com.ncc.neon.services
//
//import javax.ws.rs.core.Response
//import javax.ws.rs.ext.ExceptionMapper
//import javax.ws.rs.ext.Provider
//
//import org.elasticsearch.client.transport.NoNodeAvailableException
//
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//
//@Provider
//class ElasticSearchNoNodeAvailableExceptionMapper implements ExceptionMapper<NoNodeAvailableException> {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchNoNodeAvailableExceptionMapper)
//    private static final String MESSAGE = "ElasticSearch node not available"
//
//    @Override
//    public Response toResponse(NoNodeAvailableException exception) {
//        LOGGER.error(exception.message, exception)
//        ExceptionMapperResponse response = new ExceptionMapperResponse(MESSAGE, exception)
//        return Response.status(404).entity(response).type("application/json").build()
//    }
//
//}
//
