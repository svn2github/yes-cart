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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
//TODO: YC-64 refactor to param test
public class TestSkuWarehouseServiceImpl extends BaseCoreDBTestCase {

    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;
    private ProductSkuService productSkuService;
    private CustomerOrderService customerOrderService;
    private OrderStateManager orderStateManager;

    @Before
    public void setUp() throws Exception {
        warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        skuWarehouseService = (SkuWarehouseService) ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        orderStateManager = (OrderStateManager) ctx().getBean(ServiceSpringKeys.ORDER_STATE_MANAGER);
    }

    @Test
    public void testCreate() {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(11006L));
        skuWarehouse.setWarehouse(warehouseService.getById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        assertTrue(skuWarehouse.getSkuWarehouseId() > 0);
        skuWarehouseService.delete(skuWarehouse);
    }

    // TODO: YC-64 fix to not depend on order or running
    @Test
    public void testGetQuantity() {
        SkuWarehouse skuWarehouse;
        skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(11006L));
        skuWarehouse.setWarehouse(warehouseService.getById(2L));
        skuWarehouse.setQuantity(new BigDecimal("10.00"));
        skuWarehouse.setReserved(new BigDecimal("5.00"));
        skuWarehouseService.create(skuWarehouse);
        skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(11006L));
        skuWarehouse.setWarehouse(warehouseService.getById(3L));
        skuWarehouse.setQuantity(new BigDecimal("4.00"));
        skuWarehouse.setReserved(new BigDecimal("0.00"));
        skuWarehouseService.create(skuWarehouse);
        List<Warehouse> warehouses = new ArrayList<Warehouse>();
        warehouses.add(warehouseService.getById(3L));
        warehouses.add(warehouseService.getById(2L));
        warehouses.add(warehouseService.getById(1L));
        ProductSku psku = productSkuService.getById(11006L);
        Pair<BigDecimal, BigDecimal> rez = skuWarehouseService.getQuantity(warehouses, psku);
        assertEquals(new BigDecimal("14.00"), rez.getFirst());
        assertEquals(new BigDecimal("5.00"), rez.getSecond());
    }

    /**
     * No records about quantity on warehouses.
     */
    @Test
    public void testGetQuantity2() {
        List<Warehouse> warehouses = new ArrayList<Warehouse>();
        warehouses.add(warehouseService.getById(3L));
        warehouses.add(warehouseService.getById(2L));
        warehouses.add(warehouseService.getById(1L));
        ProductSku psku = productSkuService.getById(11007L);
        Pair<BigDecimal, BigDecimal> rez = skuWarehouseService.getQuantity(warehouses, psku);
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), rez.getFirst());
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), rez.getSecond());
    }

    @Test
    public void testUpdate() {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(11006L));
        skuWarehouse.setWarehouse(warehouseService.getById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        assertTrue(skuWarehouse.getSkuWarehouseId() > 0);
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouse = skuWarehouseService.update(skuWarehouse);
        assertEquals(BigDecimal.ONE, skuWarehouse.getQuantity());
        long pk = skuWarehouse.getSkuWarehouseId();
        skuWarehouseService.delete(skuWarehouse);
        skuWarehouse = skuWarehouseService.getById(pk);
        assertNull(skuWarehouse);
    }

    @Test
    public void testDelete() {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(11006L));
        skuWarehouse.setWarehouse(warehouseService.getById(1L));
        skuWarehouse.setQuantity(BigDecimal.TEN);
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        assertTrue(skuWarehouse.getSkuWarehouseId() > 0);
        long pk = skuWarehouse.getSkuWarehouseId();
        skuWarehouseService.delete(skuWarehouse);
        skuWarehouse = skuWarehouseService.getById(pk);
        assertNull(skuWarehouse);
    }

    @Test
    public void testReserveQuantity() {
        final Warehouse warehouse = warehouseService.getById(1L);
        ProductSku productSku = productSkuService.getById(10004L); // 4 items on 1 warehouse
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), skuWarehouseService.reservation(warehouse, productSku, new BigDecimal("3.00"))); // 4 total and 3 reserved
        Pair<BigDecimal, BigDecimal> rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);

        assertEquals(new BigDecimal("4.00"), rez.getFirst());
        assertEquals(new BigDecimal("3.00"), rez.getSecond());
        assertEquals(new BigDecimal("10.00"), skuWarehouseService.reservation(warehouse, productSku, new BigDecimal("11.00"))); // 4 total and 4 reserved 10 to reserve
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("4.00"), rez.getFirst());
        assertEquals(new BigDecimal("4.00"), rez.getSecond());
    }

    /**
     * Need to reduce reverved quantity and reduct available quantity
     */
    @Test
    public void testReservationandVoidReservation() {
        final Warehouse warehouse = warehouseService.getById(1L);
        ProductSku productSku = productSkuService.getById(10004L); // 4 items on 1 warehouse
        //3 items reservation
        BigDecimal toReserve = skuWarehouseService.reservation(warehouse, productSku, new BigDecimal("3.00"));
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), toReserve); // 4 total and 3 reserved
        Pair<BigDecimal, BigDecimal> rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("4.00"), rez.getFirst());
        assertEquals(new BigDecimal("3.00"), rez.getSecond());
        // void 2 items
        BigDecimal toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku, new BigDecimal("2.00"));
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), toVoidReserve); // 4 total and 3 reserved
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("4.00"), rez.getFirst());
        assertEquals(new BigDecimal("1.00"), rez.getSecond());
        // void 1 item
        toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku, new BigDecimal("1.00"));
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), toVoidReserve); // 4 total and 3 reserved
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("4.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
        // void 5 items
        toVoidReserve = skuWarehouseService.voidReservation(warehouse, productSku, new BigDecimal("5.00"));
        assertEquals(new BigDecimal("5.00"), toVoidReserve);
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("4.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
    }

    @Test
    public void testDebitCredit() {
        final Warehouse warehouse = warehouseService.getById(1L);
        ProductSku productSku = productSkuService.getById(10004L); // 4 items on 1 warehouse
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), skuWarehouseService.debit(warehouse, productSku, new BigDecimal("3.00"))); // 2 total and 1 reserved
        Pair<BigDecimal, BigDecimal> rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("1.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
        assertEquals(BigDecimal.ZERO.setScale(Constants.DEFAULT_SCALE), skuWarehouseService.debit(warehouse, productSku, new BigDecimal("1.00"))); // 2 total and 1 reserved
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("0.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
        assertEquals(new BigDecimal("7.00"), skuWarehouseService.debit(warehouse, productSku, new BigDecimal("7.00")));
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("0.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
        assertEquals(new BigDecimal("0.00"), skuWarehouseService.debit(warehouse, productSku, new BigDecimal("0.00")));
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("0.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
        skuWarehouseService.credit(warehouse, productSku, new BigDecimal("74.00"));
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("74.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
        skuWarehouseService.credit(warehouse, productSku, new BigDecimal("0.00"));
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("74.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
        assertEquals(new BigDecimal("6.00"), skuWarehouseService.debit(warehouse, productSku, new BigDecimal("80.00")));
        rez = skuWarehouseService.getQuantity(new ArrayList<Warehouse>() {{
            add(warehouse);
        }}, productSku);
        assertEquals(new BigDecimal("0.00"), rez.getFirst());
        assertEquals(new BigDecimal("0.00"), rez.getSecond());
    }

    @Test
    public void testFindProductSkusOnWarehouse() {
        //10000 product id - sobot has 4 skus on 1 warehouse
        List<SkuWarehouse> skusWarehouseList = skuWarehouseService.findProductSkusOnWarehouse(10000L, 1L);
        assertEquals(4, skusWarehouseList.size());
        for (SkuWarehouse skuWarehouse : skusWarehouseList) {
            if (skuWarehouse.getSku().getSkuId() == 10000L) {
                assertEquals(1, skuWarehouse.getQuantity().intValue());
            }
            if (skuWarehouse.getSku().getSkuId() == 10001L) {
                assertEquals(2, skuWarehouse.getQuantity().intValue());
            }
            if (skuWarehouse.getSku().getSkuId() == 10003L) {
                assertEquals(3, skuWarehouse.getQuantity().intValue());
            }
            if (skuWarehouse.getSku().getSkuId() == 10004L) {
                assertEquals(4, skuWarehouse.getQuantity().intValue());
            }
        }
    }


    @Test
    public void testIsSkuAvilabilityPreorder () {
        assertFalse(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15300, true));
        assertFalse(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15301, true));
        assertTrue(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15310, true));
        assertTrue(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15320, true));
        assertFalse(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15300, false));
        assertFalse(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15301, false));
        assertTrue(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15310, false));
        assertTrue(skuWarehouseService.isSkuAvailabilityPreorderOrBackorder(15320, false));
    }


    @Test
    public void testPushOrdersAwaitingForInventory() throws OrderException {

        Customer cust = createCustomer("preorder-tester");

        ShoppingCart shoppingCart = getShoppingCartWithPreorderItems("pre-test2", 0);

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart, false);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, order.getOrderStatus());
        order.setPgLabel("testPaymentGatewayLabel");
        customerOrderService.update(order);

        orderStateManager.fireTransition(
                new OrderEventImpl(OrderStateManager.EVT_PENDING,
                        order,
                        null,
                        new HashMap()
                )

        );

        customerOrderService.update(order);

        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(15320L)); //need 2 items to push order back to life cycle
        skuWarehouse.setWarehouse(warehouseService.getById(1L));
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouseService.create(skuWarehouse);
        skuWarehouseService.updateOrdersAwaitingForInventory(skuWarehouse.getSku().getSkuId());


        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals("One item not enough to continue order processing . Must still in progress 0", CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals("One item not enough to continue order processing. Must still waiting for inventory 0", CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        skuWarehouse.setQuantity(BigDecimal.ONE.add(BigDecimal.ONE));
        skuWarehouseService.update(skuWarehouse);
        skuWarehouseService.updateOrdersAwaitingForInventory(skuWarehouse.getSku().getSkuId());
        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals("One item not enough to continue order processing . Must still in progress 1", CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals("One item not enough to continue order processing. Must still waiting for inventory 1", CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_WAIT, delivery.getDeliveryStatus());
        }

        skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        skuWarehouse.setSku(productSkuService.getById(15310L)); //need 2 items to push order back to life cycle
        skuWarehouse.setWarehouse(warehouseService.getById(1L));
        skuWarehouse.setQuantity(BigDecimal.ONE);
        skuWarehouseService.create(skuWarehouse);
        skuWarehouseService.updateOrdersAwaitingForInventory(skuWarehouse.getSku().getSkuId());

        order = customerOrderService.findByGuid(order.getCartGuid());
        assertEquals("One item not enough to continue order processing . Must still in progress 2", CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals("Delivery can be started", CustomerOrderDelivery.DELIVERY_STATUS_INVENTORY_ALLOCATED, delivery.getDeliveryStatus());
        }



    }
}
