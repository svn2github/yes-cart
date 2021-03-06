/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.service.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import java.io.IOException;
import java.util.List;

/**
 * Back door administrative service.
 * Need to have ability:
 *
 * 1. reindex product on demand on storefront side instead of management side.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/28/12
 * Time: 9:50 AM
 */
@WebService
public interface BackdoorService {

    /**
     * @return true if service is online
     */
    @WebMethod
    @WebResult(name = "ping")
    boolean ping();

    @WebMethod
    @WebResult(name = "warmUp")
    void warmUp();

    /**
     * Reindex all products.
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexAllProducts();

    /**
     * Reindex single products.
     * @param productPk product pk.
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexProduct(@WebParam(name = "productPk") long productPk);

    /**
     * Reindex single products.
     * @param productPk product pk.
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexProductSku(@WebParam(name = "productPk") long productPk);

    /**
     * Reindex single products.
     * @param productCode product SKU code.
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexProductSkuCode(@WebParam(name = "productCode") String productCode);


    /**
     * Reindex given set of products.
     * @param productPks product PKs to reindex
     * @return quantity of objects in index
     */
    @WebMethod
    @WebResult(name = "quantity")
    int reindexProducts(@WebParam(name = "productPks") long[] productPks);


    /**
     * Execute sql and return result.
     * DML operating also allowed, in this case result has quantity of affected rows.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    @WebMethod
    @WebResult(name = "queryResult")
    List<Object[]> sqlQuery(String query);

    /**
     * Execute hsql and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    @WebMethod
    @WebResult(name = "queryResult")
    List<Object[]> hsqlQuery(String query);

    /**
     * Execute lucene and return result.
     *
     * @param query query ot execute.
     * @return list of rows
     */
    @WebMethod
    @WebResult(name = "queryResult")
    List<Object[]> luceneQuery(String query);


    /**
     * Get image vault path.
     * @return  image vault path
     */
    @WebMethod
    @WebResult(name = "imageVaultPath")
    String getImageVaultPath() throws IOException;

}
