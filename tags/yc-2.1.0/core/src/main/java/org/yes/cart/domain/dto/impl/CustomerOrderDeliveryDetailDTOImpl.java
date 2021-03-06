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
package org.yes.cart.domain.dto.impl;





import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;

import java.math.BigDecimal;

/**
 * Customer order detail DTO interface implementation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/12/12
 * Time: 7:21 AM
 */
@Dto
public class CustomerOrderDeliveryDetailDTOImpl implements CustomerOrderDeliveryDetailDTO {

    private static final long serialVersionUID = 20120812L;

    @DtoField(value = "customerOrderDeliveryDetId", readOnly = true)
    private long customerOrderDeliveryDetId;

    @DtoField(value = "productSkuCode", readOnly = true)
    private String skuCode;

    @DtoField(value = "productName", readOnly = true)
    private String skuName;


    @DtoField(value = "qty", readOnly = true)
    private BigDecimal qty;

    @DtoField(value = "price", readOnly = true)
    private BigDecimal invoicePrice;

    @DtoField(value = "listPrice", readOnly = true)
    private BigDecimal listPrice;
    @DtoField(value = "salePrice", readOnly = true)
    private BigDecimal salePrice;
    @DtoField(value = "gift", readOnly = true)
    private boolean gift;
    @DtoField(value = "promoApplied", readOnly = true)
    private boolean promoApplied;
    @DtoField(value = "appliedPromo", readOnly = true)
    private String appliedPromo;


    private BigDecimal lineTotal;


    @DtoField(value = "delivery.deliveryNum", readOnly = true)
    private String deliveryNum;

    @DtoField(value = "delivery.deliveryStatus", readOnly = true)
    private String deliveryStatusLabel;


    /** {@inheritDoc} */
    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    /** {@inheritDoc} */
    public void setLineTotal(final BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }


    private void calculateLineTotal() {

        if (qty != null && invoicePrice != null) {
            lineTotal = qty.multiply(invoicePrice).setScale(Constants.DEFAULT_SCALE);
        }

    }

    /** {@inheritDoc} */
    public long getId() {
        return customerOrderDeliveryDetId;
    }

    /** {@inheritDoc} */
    public long getCustomerOrderDeliveryDetId() {
        return customerOrderDeliveryDetId;
    }

    /** {@inheritDoc} */
    public void setCustomerOrderDeliveryDetId(final long customerOrderDeliveryDetId) {
        this.customerOrderDeliveryDetId = customerOrderDeliveryDetId;
    }

    /** {@inheritDoc} */
    public String getSkuCode() {
        return skuCode;
    }

    /** {@inheritDoc} */
    public void setSkuCode(final String skuCode) {
        this.skuCode = skuCode;
    }

    /** {@inheritDoc} */
    public String getSkuName() {
        return skuName;
    }

    /** {@inheritDoc} */
    public void setSkuName(final String skuName) {
        this.skuName = skuName;
    }

    /** {@inheritDoc} */
    public BigDecimal getQty() {
        return qty;
    }

    /** {@inheritDoc} */
    public void setQty(final BigDecimal qty) {
        this.qty = qty;
        calculateLineTotal();
    }

    /** {@inheritDoc} */
    public BigDecimal getInvoicePrice() {
        return invoicePrice;
    }

    /** {@inheritDoc} */
    public void setInvoicePrice(final BigDecimal invoicePrice) {
        this.invoicePrice = invoicePrice;
        calculateLineTotal();
    }

    /** {@inheritDoc} */
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /** {@inheritDoc} */
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    /** {@inheritDoc} */
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    /** {@inheritDoc} */
    public void setSalePrice(final BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    /** {@inheritDoc} */
    public boolean isGift() {
        return gift;
    }

    /** {@inheritDoc} */
    public void setGift(final boolean gift) {
        this.gift = gift;
    }

    /** {@inheritDoc} */
    public boolean isPromoApplied() {
        return promoApplied;
    }

    /** {@inheritDoc} */
    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    /** {@inheritDoc} */
    public String getAppliedPromo() {
        return appliedPromo;
    }

    /** {@inheritDoc} */
    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    /** {@inheritDoc} */
    public String getDeliveryNum() {
        return deliveryNum;
    }

    /** {@inheritDoc} */
    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    /** {@inheritDoc} */
    public String getDeliveryStatusLabel() {
        return deliveryStatusLabel;
    }

    /** {@inheritDoc} */
    public void setDeliveryStatusLabel(final String deliveryStatusLabel) {
        this.deliveryStatusLabel = deliveryStatusLabel;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "CustomerOrderDeliveryDetailDTOImpl{" +
                "customerorderdeliveryId=" + customerOrderDeliveryDetId +
                ", skuCode='" + skuCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", qty=" + qty +
                ", invoicePrice=" + invoicePrice +
                ", listPrice=" + listPrice +
                ", deliveryNum='" + deliveryNum + '\'' +
                ", deliveryStatusLabel='" + deliveryStatusLabel + '\'' +
                '}';
    }
}
