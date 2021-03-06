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
package org.yes.cart.domain.dto;


import org.yes.cart.domain.entity.Identifiable;

import java.math.BigDecimal;

/**
 * Customer order detail DTO interface.
 * Represent single order line in order with delivery details.
 *
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/12/12
 * Time: 7:21 AM
 */

public interface CustomerOrderDeliveryDetailDTO extends Identifiable {


    /**
     * Get pk value of order detail record.
     * @return pk value
     */
    long getCustomerOrderDeliveryDetId();

    /**
     * Set pk value of related order detail record.
     * @param customerorderdeliveryId pk value
     */
    void setCustomerOrderDeliveryDetId(long customerorderdeliveryId) ;

    /**
     * Get sku code .
     * @return sku code .
     */
    String getSkuCode();

    /**
     * Set sku code .
     * @param skuCode sku code .
     */
    void setSkuCode(String skuCode);

    /**
     * Get sku name.
     * @return  sku name.
     */
    String getSkuName();

    /**
     * Set  sku name.
     * @param skuName sku name.
     */
    void setSkuName(String skuName);

    /**
     * Get quantity.
     * @return  quantity.
     */
    BigDecimal getQty();

    /**
     * Set product qty.
     * @param qty       quantity.
     */
    void setQty(BigDecimal qty) ;

    /**
     * Get price of product, which is in delivery.
     * @return deal price.
     */
    BigDecimal getInvoicePrice() ;

    /**
     * Set deal price.
     * @param invoicePrice deal price.
     */
    void setInvoicePrice(BigDecimal invoicePrice);

    /**
     * Get price in catalog.
     * @return proce in catalog.
     */
    BigDecimal getListPrice();

    /**
     * Set price in catalog.
     * @param listPrice price in catalog.
     */
    void setListPrice(BigDecimal listPrice) ;

    /**
     * Get delivery number.
     * @return delivery nimber.
     */
    String getDeliveryNum();

    /**
     * Set delivery number.
     * @param deliveryNum  delivery num.
     */
    void setDeliveryNum(String deliveryNum) ;

    /**
     * Get devivery status label for more details look at {@link org.yes.cart.domain.entity.CustomerOrderDelivery}
     * @return delivery detail .
     */
    String getDeliveryStatusLabel();

    /**
     * Set delivery status label.
     * @param deliveryStatusLabel delivery status label.
     */
    void setDeliveryStatusLabel(String deliveryStatusLabel) ;



}
