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

package org.yes.cart.payment.service;


import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public interface CustomerOrderPaymentService extends PaymentModuleGenericService<CustomerOrderPayment> {


    /**
     * Find all payments by given parameters.
     * Warning order number or shipment number must be present.
     *
     * @param orderNumber            given order number. optional
     * @param shipmentNumber         given shipment/delivery number. optional
     * @param paymentProcessorResult status   of payment at payment processor . optional
     * @param transactionOperation   operation name at payment gateway. optional
     * @return list of payments
     */
    List<CustomerOrderPayment> findBy(
            String orderNumber,
            String shipmentNumber,
            String paymentProcessorResult,
            String transactionOperation);

    /**
     * Get order amount
     *
     * @param orderNumber given order number
     * @return order amount
     */
    BigDecimal getOrderAmount(String orderNumber);


}
