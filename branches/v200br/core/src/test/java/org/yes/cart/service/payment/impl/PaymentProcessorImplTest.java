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

package org.yes.cart.service.payment.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.impl.TestPaymentGatewayImpl;
import org.yes.cart.payment.persistence.entity.impl.PaymentGatewayParameterEntity;
import org.yes.cart.payment.service.CustomerOrderPaymentService;
import org.yes.cart.service.domain.*;
import org.yes.cart.service.order.OrderEvent;
import org.yes.cart.service.order.OrderException;
import org.yes.cart.service.order.OrderStateManager;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.service.payment.PaymentProcessor;
import org.yes.cart.service.payment.PaymentProcessorFactory;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.impl.ShoppingCartImpl;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class PaymentProcessorImplTest extends BaseCoreDBTestCase {

    private static final String PGLABEL = "testPaymentGatewayLabel";

    private PaymentProcessorFactory paymentProcessorFactory;
    private CustomerOrderService customerOrderService;
    private CustomerOrderPaymentService customerOrderPaymentService;
    private AddressService addressService;
    private CustomerService customerService;
    private ShopService shopService;
    private OrderStateManager orderStateManager;
    private CarrierSlaService carrierSlaService;

    private WarehouseService warehouseService;
    private SkuWarehouseService skuWarehouseService;
    private ProductSkuService productSkuService;

    @Before
    public void setUp() {
        paymentProcessorFactory = (PaymentProcessorFactory) ctx().getBean(ServiceSpringKeys.PAYMENT_PROCESSOR_FACTORY);
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        customerOrderPaymentService = (CustomerOrderPaymentService) ctx().getBean(ServiceSpringKeys.ORDER_PAYMENT_SERICE);
        addressService = (AddressService) ctx().getBean(ServiceSpringKeys.ADDRESS_SERVICE);
        customerService = (CustomerService) ctx().getBean(ServiceSpringKeys.CUSTOMER_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        orderStateManager = (OrderStateManager) ctx().getBean(ServiceSpringKeys.ORDER_STATE_MANAGER);
        carrierSlaService = (CarrierSlaService) ctx().getBean(ServiceSpringKeys.CARRIER_SLA_SERVICE);

        warehouseService = (WarehouseService) ctx().getBean(ServiceSpringKeys.WAREHOUSE_SERVICE);
        skuWarehouseService = (SkuWarehouseService) ctx().getBean(ServiceSpringKeys.SKU_WAREHOUSE_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);

        super.setUp();
    }

    /**
     * Test, to prove, that one order with one delivery produce one AUTH operation
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize1() throws Exception {

        final Warehouse warehouse = warehouseService.findById(1);
        final Pair<BigDecimal, BigDecimal> skuCcTest1Qty0 = skuWarehouseService.getQuantity(Collections.singletonList(warehouse) , "CC_TEST1");
        final Pair<BigDecimal, BigDecimal> skuCcTest3Qty0 = skuWarehouseService.getQuantity(Collections.singletonList(warehouse) , "CC_TEST3");

        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart1(customer.getEmail()), true);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDeliveryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.AUTH).size());


        customerOrder = customerOrderService.findByGuid(customerOrder.getCartGuid());


        assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_ON_FULLFILMENT, customerOrder.getDelivery().iterator().next().getDeliveryStatus());

        final Pair<BigDecimal, BigDecimal> skuCcTest1Qty1 = skuWarehouseService.getQuantity(Collections.singletonList(warehouse) , "CC_TEST1");
        final Pair<BigDecimal, BigDecimal> skuCcTest3Qty1 = skuWarehouseService.getQuantity(Collections.singletonList(warehouse) , "CC_TEST3");

        assertEquals(skuCcTest1Qty0.getFirst(), skuCcTest1Qty1.getFirst());
        assertEquals(skuCcTest1Qty0.getSecond(), skuCcTest1Qty1.getSecond());

    }

    /**
     * Test, to prove, that one order with one delivery produce one AUTH operation
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthCapture1() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart1(customer.getEmail()), true);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(false);
            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(1, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(),
                    null,
                    Payment.PAYMENT_STATUS_OK,
                    PaymentGateway.AUTH_CAPTURE).size());
        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(true);
        }
    }

    /**
     * Test, to prove, that one order with two shipments produce two AUTH operation, in case
     * of no errors on payment gateways
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize2() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        CustomerOrderDelivery delivery1 = iter.next();
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
    }

    /**
     * Test, to prove, that one order with two shipments produce two AUTH_CAPTURE operation, in case
     * of no errors on payment gateways
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthCapture2() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(false);
            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(2, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH_CAPTURE).size());
        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(true);
        }
    }

    /**
     * Test, to prove, that one order with two shipments produce one AUTH operation with failed status
     * in case of errors on payment gateway.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize3() throws Exception {
        try {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, new PaymentGatewayParameterEntity());
            Customer customer = createCustomer();
            PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
            CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
            Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
            CustomerOrderDelivery delivery0 = iter.next();
            assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals("No payment for second delivery, because first is failed",
                    1,
                    customerOrderPaymentService.findBy(
                            customerOrder.getOrdernum(),
                            delivery0.getDeliveryNum(),
                            Payment.PAYMENT_STATUS_FAILED,
                            PaymentGateway.AUTH).size()
            );
            assertEquals("No reverse auth operation, because nothing to reverse",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null).size());
        } finally {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);
        }
    }

    /**
     * One order , two shipment. Payment gateway failed second auth operation ,
     * hence three payment records must be present:
     * first auth with ok status;
     * second with failed status;
     * first reverse auth;
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize4() throws Exception {
        try {
            TestPaymentGatewayImpl.setAuthNum(0);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL_NO + "1", new PaymentGatewayParameterEntity());
            Customer customer = createCustomer();
            PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
            CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
            assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals("Two Auth with different statuses and reverse auth must be present",
                    3,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null).size());
            assertEquals("One auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
            assertEquals("One auth with failed status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_FAILED, PaymentGateway.AUTH).size());
            assertEquals("One reverse auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.REVERSE_AUTH).size());
        } finally {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL_NO + "1", null);
        }
    }

    /**
     * One order , two shipment. Payment gateway throws exception on second auth operation,
     * hence three payment records must be present:
     * first auth with ok status;
     * second with failed status;
     * first reverse auth;
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize5() throws Exception {
        try {
            TestPaymentGatewayImpl.setAuthNum(0);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_EXCEPTION_NO + "1", new PaymentGatewayParameterEntity());
            Customer customer = createCustomer();
            PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
            CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
            assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(
                    "Two Auth with different statuses and reverse auth must be present",
                    3,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, null, null).size());
            assertEquals(
                    "One auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
            assertEquals(
                    "One auth with failed status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_FAILED, PaymentGateway.AUTH).size());
            assertEquals(
                    "One reverse auth with ok status",
                    1,
                    customerOrderPaymentService.findBy(customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.REVERSE_AUTH).size());
        } finally {
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_FAIL, null);
            TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.AUTH_EXCEPTION_NO + "1", null);
        }
    }

    /**
     * Test, to prove, that one order with two shipments produce one AUTH operation, in case if payment gateway
     * not supports multiple payments for one order.
     * Amount of payments must be equals for two cases, supports and not supports.
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testAuthorize6() throws Exception {
        BigDecimal amountForOnePayment = BigDecimal.ZERO;
        BigDecimal amountForTwoPayments;
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CarrierSla carrier = carrierSlaService.findById(1L); // carrier set by shopping cart
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false); //multiple delivery
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        CustomerOrder customerOrder2 = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false); //multiple delivery
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder2, createParametersMap()));
        amountForTwoPayments = customerOrderPaymentService.findBy(
                customerOrder2.getOrdernum(), null, null, null).get(0).getPaymentAmount();
        amountForTwoPayments = amountForTwoPayments.add(customerOrderPaymentService.findBy(
                customerOrder2.getOrdernum(), null, null, null).get(1).getPaymentAmount());
        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorizePerShipment(false);
            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(1, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, null, null).size());
            assertEquals(1, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(),
                    customerOrder.getOrdernum(), // in single pay delivery num eq order num from payment point of view
                    Payment.PAYMENT_STATUS_OK,
                    PaymentGateway.AUTH).size());
            amountForOnePayment = customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, null, null).get(0).getPaymentAmount();
        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorizePerShipment(true);
        }
        // extra delivery adds extra shipping charge
        assertEquals(amountForOnePayment.add(carrier.getPrice()), amountForTwoPayments);
    }


    /**
     * Test , that perform fund capture when  shipment is completed.
     * Test case for single delivery and single payments.
     * Fund will be captured on delivery
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete1() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart1(customer.getEmail()), false); //multiple delivery enabled
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, null, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDeliveryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.AUTH).size());
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDeliveryNum()));
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDeliveryNum(),
                Payment.PAYMENT_STATUS_OK,
                PaymentGateway.CAPTURE).size());
        //total two records auth and capture
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(),
                delivery0.getDeliveryNum(),
                Payment.PAYMENT_STATUS_OK,
                null).size());
    }

    /**
     * Test , that perform fund capture when  shipment is completed.
     * Test case for single delivery and single payments.
     * Fund will be captured on delivery
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete2() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        CustomerOrderDelivery delivery1 = iter.next();
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDeliveryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDeliveryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());
        assertEquals(4, customerOrderPaymentService.findBy( //two auth and two captures
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(2, customerOrderPaymentService.findBy( //two captures
                customerOrder.getOrdernum(), null, null, PaymentGateway.CAPTURE).size());
    }

    /**
     * Test , to prove , that fund will captured on second delivery
     * when it failed on first
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete3() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        CustomerOrderDelivery delivery1 = iter.next();
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.CAPTURE_FAIL, new PaymentGatewayParameterEntity());
        assertEquals(Payment.PAYMENT_STATUS_FAILED, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDeliveryNum()));
        TestPaymentGatewayImpl.getGatewayConfig().put(TestPaymentGatewayImpl.CAPTURE_FAIL, null);
        assertEquals(2, customerOrderPaymentService.findBy(customerOrder.getOrdernum(), delivery1.getDeliveryNum(), null, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_FAILED, PaymentGateway.CAPTURE).size());
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDeliveryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());
        assertEquals(3, customerOrderPaymentService.findBy( //two auth and one capture
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(4, customerOrderPaymentService.findBy( //two auth and two capture
                customerOrder.getOrdernum(), null, null, null).size());
    }

    /**
     * Test, to prove, that shipment complete not impact auth_capture records
     *
     * @throws Exception in case of errors
     */
    @Test
    public void testShipmentComplete4() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        CustomerOrderDelivery delivery1 = iter.next();
        try {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(false);
            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
            assertEquals(2, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH_CAPTURE).size());
            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDeliveryNum()));
            assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDeliveryNum()));
            assertEquals(2, customerOrderPaymentService.findBy(
                    customerOrder.getOrdernum(), null, null, null).size());
        } finally {
            paymentProcessor.getPaymentGateway().getPaymentGatewayFeatures().setSupportAuthorize(true);
        }
    }





    @Test
    public void testCancelOrder1() throws Exception {
        Customer customer = createCustomer();
        PaymentProcessor paymentProcessor = paymentProcessorFactory.create(PGLABEL);
        CustomerOrder customerOrder = customerOrderService.createFromCart(getShoppingCart2(customer.getEmail()), false);
        Iterator<CustomerOrderDelivery> iter = customerOrder.getDelivery().iterator();
        CustomerOrderDelivery delivery0 = iter.next();
        CustomerOrderDelivery delivery1 = iter.next();
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.authorize(customerOrder, createParametersMap()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.AUTH).size());
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery1.getDeliveryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.shipmentComplete(customerOrder, delivery0.getDeliveryNum()));
        assertEquals(2, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.CAPTURE).size());
        assertEquals(4, customerOrderPaymentService.findBy( //two auth and two captures
                customerOrder.getOrdernum(), null, Payment.PAYMENT_STATUS_OK, null).size());
        assertEquals(2, customerOrderPaymentService.findBy( //two captures
                customerOrder.getOrdernum(), null, null, PaymentGateway.CAPTURE).size());
        //cancel order
        assertEquals(Payment.PAYMENT_STATUS_OK, paymentProcessor.cancelOrder(customerOrder));
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery0.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.REFUND).size());
        assertEquals(1, customerOrderPaymentService.findBy(
                customerOrder.getOrdernum(), delivery1.getDeliveryNum(), Payment.PAYMENT_STATUS_OK, PaymentGateway.REFUND).size());
    }

    /**
     * Test to prove, that only one shopper can buy product with standard availability
     * in case of concurrent shopping. The rest of shoppers get the exceptions.
     */
    @Test
    public void testMultipleShoppersOneItem() throws Exception {
        //Create several customers, each of them has the one product in
        //hos cart, but only one item is available
        final int customerQty = 10;
        final Customer[] customers = new Customer[customerQty];
        final ShoppingCart[] cart  = new ShoppingCart [customerQty];
        final OrderEvent[] events = new OrderEventImpl[customerQty];
        for (int i = 0; i < customerQty; i++) {
            customers[i] = createCustomer();
            cart[i] = getShoppingCartWithOneAvailableItem(customers[i].getEmail());

            CustomerOrder order  = customerOrderService.createFromCart(cart[i], false);
            order.setPgLabel(PGLABEL);
            customerOrderService.update(order);

            events[i] = new OrderEventImpl(
                    OrderStateManager.EVT_PENDING,
                    customerOrderService.findByGuid(cart[i].getGuid()),
                    null,
                    createParametersMap()
            );
        }

        // start several thread to process order
        for (int i = 0; i < customerQty; i++) {
            final int j  = i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        orderStateManager.fireTransition(events[j]);
                    } catch (OrderException e) {
                        assertTrue("" + e.getMessage(), false);
                    }
                }
            }.start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int okCnt = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < customerQty; i++) {
           stringBuilder.append(events[i].getCustomerOrder().getOrderStatus());
           stringBuilder.append( "," );
            if (CustomerOrder.ORDER_STATUS_IN_PROGRESS.equals(events[i].getCustomerOrder().getOrderStatus())) {
                okCnt ++;
            }
        }

        assertEquals("Only one order may be in progress state :" + stringBuilder.toString(), 1, okCnt);




    }

    protected Customer createCustomer() {
        Customer customer = customerService.getGenericDao().getEntityFactory().getByIface(Customer.class);
        customer.setEmail(UUID.randomUUID().toString() + "jd@domain.com");
        customer.setFirstname("John");
        customer.setLastname("Dou");
        customer.setPassword("rawpassword");
        customer = customerService.create(customer, shopService.getById(10L));
        assertTrue(customer.getCustomerId() > 0);
        Address address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Dou");
        address.setCity("LA");
        address.setAddrline1("line1");
        address.setCountryCode("US");
        address.setAddressType(Address.ADDR_TYPE_SHIPING);
        address.setCustomer(customer);
        addressService.create(address);
        address = addressService.getGenericDao().getEntityFactory().getByIface(Address.class);
        address.setFirstname("John");
        address.setLastname("Dou");
        address.setCity("New-Vasyki");
        address.setAddrline1("line0");
        address.setCountryCode("ZH");
        address.setAddressType(Address.ADDR_TYPE_BILLING);
        address.setCustomer(customer);
        addressService.create(address);
        return customer;
    }

    private Map<String, String> createParametersMap() {
        Map<String, String> rez = new HashMap<String, String>();
        rez.put("ccHolderName", "John Dou");
        rez.put("ccNumber", "4111111111111111");
        rez.put("ccExpireMonth", "12");
        rez.put("ccExpireYear", "2020");
        rez.put("ccSecCode", "111");
        rez.put("ccType", "Visa");
        return rez;
    }

    /**
     * Create simple cart with products, that have a standard availability and enough qty on warehouses.
     *
     * @return cart
     */
    protected ShoppingCart getShoppingCart1(String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart, (Map) Collections.singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1"));
        commands.execute(shoppingCart, (Map) Collections.singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));
        commands.execute(shoppingCart, (Map) Collections.singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3"));

        return shoppingCart;
    }

    /**
     * Both sku with standard availability , but one of the has not qty on warehouse
     *
     * @return cart
     */
    protected ShoppingCart getShoppingCart2(final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart, (Map) Collections.singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4"));
        commands.execute(shoppingCart, (Map) Collections.singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST4-M"));

        return shoppingCart;
    }




    /**
     * Both sku with standard availability , but one of the has not qty on warehouse
     *
     * @return cart
     */
    protected ShoppingCart getShoppingCartWithOneAvailableItem(final String customerEmail) {
        ShoppingCart shoppingCart = getEmptyCart(customerEmail);

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart, (Map) Collections.singletonMap(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST11"));

        return shoppingCart;
    }

    public ShoppingCart getEmptyCart(String customerEmail) {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ShoppingCartCommand.CMD_LOGIN_P_EMAIL, customerEmail);
        params.put(ShoppingCartCommand.CMD_LOGIN_P_NAME, "John Doe");
        params.put(ShoppingCartCommand.CMD_LOGIN, "1");
        params.put(ShoppingCartCommand.CMD_SETSHOP, "10");
        params.put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
        params.put(ShoppingCartCommand.CMD_CHANGECURRENCY, "USD");
        params.put(ShoppingCartCommand.CMD_SETCARRIERSLA, "1");

        commands.execute(shoppingCart, (Map) params);
        return shoppingCart;
    }
}