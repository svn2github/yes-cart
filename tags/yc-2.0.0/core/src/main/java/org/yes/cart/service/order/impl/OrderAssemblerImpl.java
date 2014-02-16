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

package org.yes.cart.service.order.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.service.order.OrderNumberGenerator;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.text.MessageFormat;
import java.util.Date;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderAssemblerImpl implements OrderAssembler {

    private final OrderNumberGenerator orderNumberGenerator;
    private final GenericDAO<Customer, Long> customerDao;
    private final EntityFactory entityFactory;
    private final GenericDAO<Shop, Long> shopDao;
    private final ProductSkuService productSkuService;
    private final String addressFormat;

    /**
     * Create order assembler.
     *
     * @param orderNumberGenerator order number generator
     * @param customerDao          customer dao to get customer from email
     * @param shopDao              shop dao
     * @param productSkuService    product sku service
     * @param addressFormat        format string to create address in one string from {@link Address} entity.
     */
    public OrderAssemblerImpl(
            final OrderNumberGenerator orderNumberGenerator,
            final GenericDAO<Customer, Long> customerDao,
            final GenericDAO<Shop, Long> shopDao,
            final ProductSkuService productSkuService,
            final String addressFormat
    ) {
        this.entityFactory = customerDao.getEntityFactory();
        this.orderNumberGenerator = orderNumberGenerator;
        this.customerDao = customerDao;
        this.shopDao = shopDao;
        this.productSkuService = productSkuService;
        this.addressFormat = addressFormat;
    }

    /**
     * Create and fill {@link CustomerOrder} from   from {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @return not persisted but filled with data order
     */
    public CustomerOrder assembleCustomerOrder(final ShoppingCart shoppingCart) {
        return assembleCustomerOrder(shoppingCart, false);
    }

    /**
     * Create and fill {@link CustomerOrder} from   from {@link ShoppingCart}.
     *
     * @param shoppingCart given shopping cart
     * @return not persisted but filled with data order
     */
    public CustomerOrder assembleCustomerOrder(final ShoppingCart shoppingCart, final boolean temp) {

        final CustomerOrder customerOrder = entityFactory.getByIface(CustomerOrder.class);

        fillCustomerData(customerOrder, shoppingCart);

        fillOrderDetails(customerOrder, shoppingCart);

        customerOrder.setLocale(shoppingCart.getCurrentLocale());
        customerOrder.setCurrency(shoppingCart.getCurrencyCode());
        customerOrder.setOrderStatus(CustomerOrder.ORDER_STATUS_NONE);
        customerOrder.setOrderTimestamp(new Date());
        customerOrder.setGuid(shoppingCart.getGuid());
        customerOrder.setCartGuid(shoppingCart.getGuid());
        customerOrder.setListPrice(shoppingCart.getTotal().getListSubTotal());
        customerOrder.setPrice(shoppingCart.getTotal().getSubTotal());
        customerOrder.setPromoApplied(shoppingCart.getTotal().isOrderPromoApplied());
        customerOrder.setAppliedPromo(shoppingCart.getTotal().getAppliedOrderPromo());

        if (!temp) {
            customerOrder.setOrdernum(orderNumberGenerator.getNextOrderNumber());
            customerOrder.setShop(shopDao.findById(shoppingCart.getShoppingContext().getShopId()));
        }

        return customerOrder;
    }

    /**
     * Fill customer data
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     */
    private void fillCustomerData(final CustomerOrder customerOrder, final ShoppingCart shoppingCart) {

        final Customer customer = customerDao.findSingleByCriteria(
                Restrictions.eq("email", shoppingCart.getCustomerEmail()));

        if (customer != null) {
            long selectedBillingAddressId = shoppingCart.getOrderInfo().getBillingAddressId() != null ? shoppingCart.getOrderInfo().getBillingAddressId() : 0L;
            long selectedShippingAddressId = shoppingCart.getOrderInfo().getDeliveryAddressId()!= null ? shoppingCart.getOrderInfo().getDeliveryAddressId() : 0L;

            Address billingAddress = null;
            Address shippingAddress = null;

            for (final Address address : customer.getAddress()) {
                if (address.getAddressId() == selectedBillingAddressId) {
                    billingAddress = address;
                }
                if (address.getAddressId() == selectedShippingAddressId) {
                    shippingAddress = address;
                }
                if (billingAddress != null && shippingAddress != null) {
                    break;
                }
            }

            if (billingAddress == null) {
                billingAddress = customer.getDefaultAddress(Address.ADDR_TYPE_BILLING);
            }
            if (shippingAddress == null) {
                shippingAddress = customer.getDefaultAddress(Address.ADDR_TYPE_SHIPING);
            }

            customerOrder.setShippingAddress(formatAddress(shippingAddress));

            if (!shoppingCart.isSeparateBillingAddress() || billingAddress == null) {
                billingAddress = shippingAddress;
            }

            customerOrder.setBillingAddress(formatAddress(billingAddress));

            customerOrder.setCustomer(customer);
        }

        customerOrder.setOrderMessage(shoppingCart.getOrderMessage());

    }

    /**
     * Fill details records in order.
     *
     * @param customerOrder order to fill
     * @param shoppingCart  cart
     */
    private void fillOrderDetails(final CustomerOrder customerOrder, final ShoppingCart shoppingCart) {

        for (CartItem item : shoppingCart.getCartItemList()) {

            CustomerOrderDet customerOrderDet = entityFactory.getByIface(CustomerOrderDet.class);
            customerOrderDet.setCustomerOrder(customerOrder);
            customerOrder.getOrderDetail().add(customerOrderDet);

            customerOrderDet.setPrice(item.getPrice());
            customerOrderDet.setSalePrice(item.getSalePrice());
            customerOrderDet.setListPrice(item.getListPrice());
            customerOrderDet.setQty(item.getQty());
            customerOrderDet.setGift(item.isGift());
            customerOrderDet.setPromoApplied(item.isPromoApplied());
            customerOrderDet.setAppliedPromo(item.getAppliedPromo());

            customerOrderDet.setProductSkuCode(item.getProductSkuCode());

            // this is cached call so it should speed-up things
            final ProductSku sku = productSkuService.getProductSkuBySkuCode(item.getProductSkuCode());
            customerOrderDet.setProductName(
                    new FailoverStringI18NModel(sku.getDisplayName(), sku.getName()).getValue(customerOrder.getLocale()));

        }

    }

    /**
     * Format given address to string.
     *
     * @param defaultAddress given address
     * @return formated address
     */
    public String formatAddress(final Address defaultAddress) {
        if (defaultAddress != null) {

            return MessageFormat.format(
                    addressFormat,
                    StringUtils.defaultString(defaultAddress.getAddrline1()),
                    StringUtils.defaultString(defaultAddress.getAddrline2()),
                    StringUtils.defaultString(defaultAddress.getPostcode()),
                    StringUtils.defaultString(defaultAddress.getCity()),
                    StringUtils.defaultString(defaultAddress.getCountryCode()),
                    StringUtils.defaultString(defaultAddress.getStateCode()),
                    StringUtils.defaultString(defaultAddress.getFirstname()),
                    StringUtils.defaultString(defaultAddress.getLastname()),
                    StringUtils.defaultString(defaultAddress.getPhoneList())
            );
        }
        return StringUtils.EMPTY;
    }
}
