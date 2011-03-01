package de.zib.gndms.GORFX.service;
/*
 * Copyright 2008-2011 Zuse Institute Berlin (ZIB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import de.zib.gndms.model.gorfx.types.*;
import de.zib.gndms.rest.Facets;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author try ma ik jo rr a zib
 * @date 09.02.11, Time: 18:45
 *
 * @brief Interface for the taskflow service.
 *
 * The taskflow service acts as interface to instantiated taskflow
 * resources. The instantiation or creation happens through the GORFX
 * service itself.
 */
public interface TaskFlowService {

    /** @brief Delivers all facets of a task flow.
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * 
     * @return The facets of the task flow.
     */
    ResponseEntity<Facets> getFacets( String type, String id, String dn );

    /** @brief Deletes a task flow.
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return Possible HTTP-Status values:
     *     - 200 if the taskflow was deleted successful.
     *     - 404 if the taskflow doesn't exist.
     *     - 403 if the caller isn't allowed to remove the task.
     */
    ResponseEntity<Void> deleteTaskflow ( String type, String id, String dn, String wid );
    
    /** @brief Delivers the order of the task flow.
     * 
     * The order contains the parameters of the taskflow, i.e. which
     * data to stage, or the source and destination for a file
     * transfer...
     *
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return The order details of the task flow.
     */
    ResponseEntity<AbstractTF> getOrder( String type, String id, String dn, String wid );

    /** 
     * @brief Changes the order of a task flow.
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param orq The new order.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return A confirmation if the order was accepted, in the
     * HTTP-status of the response entity.
     */
    ResponseEntity<Void> setOrder( String type, String id, AbstractTF orq, String dn, String wid );

    /** 
     * @brief Delivers all quotes for the order.
     * 
     * Quotations are used for co-scheduling and describe temporal
     * constrains for the taskflow executions. Its possible that there
     * are more then one quotes for a task flow.
     *
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     *
     * @return A list of quotes.
     */
    ResponseEntity<List<Quote>> getQuotes( String type, String id, String dn, String wid );

    /** 
     * @brief Allows the client to provide a preferred quote.
     * 
     * If the preferred quote is valid, wrt. it can be satisfied, it is
     * added to the list of possible quotes.
     *
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param cont The preferred quote.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     *
     * @return A confirmation if the quote was accepted, in the
     * HTTP-status of the response entity.
     */
    ResponseEntity<Void> setQuote( String type, String id, Quote cont, String dn, String wid );

    /** 
     * @brief Delivers a single quote.
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param idx The index of the quote.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     *
     * @return The quote, or HTTP-status 404 if the index doesn't point
     * to a valid quote.
     */
    ResponseEntity<Quote> getQuote( String type, String id, int idx, String dn, String wid );

    /** 
     * @brief Removes a quote form the list, ^
     * 
     * @param type
     * @param id
     * @param idx
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return 
     *
     * \note I think this is required.
     */
    ResponseEntity<Void> deleteQuotes( String type, String id, int idx, String dn, String wid );

    /** 
     * @brief Requests the task of the taskflow.
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return The URL of the task resource or HTTP-status 404 the
     * task hasn't been created yet.
     */
    ResponseEntity<String> getTask( String type, String id, String dn, String wid );

    /** 
     * @brief Creates the task for a taskflow.
     * 
     * The creation of the task requires a valid order.
     *
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param quoteId The id of the quote which should be honored.
     * (OPTIONAL, can be null, String wid ).
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return HTTP-status 201 if the creation was successful,
     * together with the URL of the created resource. 
     */
	ResponseEntity<String> createTask(String type, String id,
			String quoteId, String dn, String wid );
	
    /** 
     * @brief Delivers the status of the taskflow execution.
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return The taskflow status, which might be HTTP-status 302
     * together with the URL of the task status if the task is
     * running or 200 together with the status description of the
     * taskflow. Note the 200 doesn't mean that the taskflow is okay,
     * the status can be failed.
     */
    ResponseEntity<TaskFlowStatus> getStatus( String type, String id, String dn, String wid );

    /** 
     * @brief Delivers the result of the taskflow execution.
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return The taskflow result, which might be HTTP-status 302
     * together with the URL of the task result or 404 if no result is
     * available.
     * */
    ResponseEntity<TaskResult> getResult( String type, String id, String dn, String wid );

    /** 
     * @brief Delivers possible errors from the task(flow) execution. 
     * 
     * @param type The type of the task flow.
     * @param id The id of the task flow.
     * @param dn The dn of the user invoking the method.
     * @param wid The id of the workflow, for logging purpose.
     * 
     * @return Possible values:
     *     - 404 if there haven't been errors yet,
     *     - 302 together with the task URL, note this doesn't mean
     *       that the task has failed, just that the task is running
     *       and the taskflow is still fine.
     *     - 200 together with the error of the taskflow, e.g. an
     *       unfulfillable order.
     */
    ResponseEntity<TaskFlowFailure> getErrors( String type, String id, String dn, String wid ); 
}
