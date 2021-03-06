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

package org.yes.cart.shoppingcart.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.shoppingcart.AmountCalculationResult;
import org.yes.cart.shoppingcart.CartItem;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 13-04-23
 * Time: 7:53 AM
 */
public class DefaultAmountCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();

    private final BigDecimal TAX = new BigDecimal("20.00");

    @Test
    public void testCalculateDeliveryNull() throws Exception {

        final BigDecimal delivery = new DefaultAmountCalculationStrategy(TAX, true).calculateDelivery(null);
        assertEquals(BigDecimal.ZERO.compareTo(delivery), 0);

    }

    @Test
    public void testCalculateDeliveryPriceNull() throws Exception {

        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            one(orderDelivery).getPrice(); will(returnValue(null));
        }});

        final BigDecimal delivery = new DefaultAmountCalculationStrategy(TAX, true).calculateDelivery(orderDelivery);
        assertEquals(BigDecimal.ZERO.compareTo(delivery), 0);

    }

    @Test
    public void testCalculateDeliveryPrice() throws Exception {

        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("9.99")));
        }});

        final BigDecimal delivery = new DefaultAmountCalculationStrategy(TAX, true).calculateDelivery(orderDelivery);
        assertEquals(new BigDecimal("9.99").compareTo(delivery), 0);

    }

    @Test
    public void testCalculateTaxNull() throws Exception {

        final BigDecimal amount = null;

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxIncluded), 0);

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, false).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxExcluded), 0);

    }

    @Test
    public void testCalculateTaxNone() throws Exception {

        final BigDecimal amount = new BigDecimal("100.00");

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxIncluded), 0);

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, false).calculateTax(amount);
        assertEquals(new BigDecimal("0").compareTo(taxExcluded), 0);

    }

    @Test
    public void testCalculateTax() throws Exception {

        final BigDecimal amount = new BigDecimal("100.00");

        final BigDecimal taxIncluded = new DefaultAmountCalculationStrategy(TAX, true).calculateTax(amount);
        assertEquals(new BigDecimal("16.67").compareTo(taxIncluded), 0);

        final BigDecimal taxExcluded = new DefaultAmountCalculationStrategy(TAX, false).calculateTax(amount);
        assertEquals(new BigDecimal("20.00").compareTo(taxExcluded), 0);

    }

    @Test
    public void testCalculateAmountNull() throws Exception {

        final BigDecimal amount = null;
        final BigDecimal taxIncluded = new BigDecimal("16.67");
        final BigDecimal taxExcluded = new BigDecimal("20.00");

        final BigDecimal amountTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true).calculateAmount(amount, taxIncluded);
        assertEquals(BigDecimal.ZERO.compareTo(amountTaxIncluded), 0);

        final BigDecimal amountTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false).calculateAmount(amount, taxExcluded);
        assertEquals(BigDecimal.ZERO.compareTo(amountTaxExcluded), 0);

    }

    @Test
    public void testCalculateAmountTaxNull() throws Exception {

        final BigDecimal amount = new BigDecimal("100.00");
        final BigDecimal taxIncluded = null;
        final BigDecimal taxExcluded = null;

        final BigDecimal amountTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true).calculateAmount(amount, taxIncluded);
        assertEquals(new BigDecimal("100").compareTo(amountTaxIncluded), 0);

        final BigDecimal amountTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false).calculateAmount(amount, taxExcluded);
        assertEquals(new BigDecimal("100").compareTo(amountTaxExcluded), 0);

    }

    @Test
    public void testCalculateAmount() throws Exception {

        final BigDecimal amount = new BigDecimal("100.00");
        final BigDecimal taxIncluded = new BigDecimal("16.67");
        final BigDecimal taxExcluded = new BigDecimal("20.00");

        final BigDecimal amountTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true).calculateAmount(amount, taxIncluded);
        assertEquals(new BigDecimal("100").compareTo(amountTaxIncluded), 0);

        final BigDecimal amountTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false).calculateAmount(amount, taxExcluded);
        assertEquals(new BigDecimal("120.00").compareTo(amountTaxExcluded), 0);

    }

    @Test
    public void testCalculateSubTotal() throws Exception {

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});


        final BigDecimal subTotalSale = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true)
                .calculateSubTotal(Arrays.asList(item1, item2), false);
        assertEquals(new BigDecimal("80").compareTo(subTotalSale), 0);

        final BigDecimal subTotalList = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true)
                .calculateSubTotal(Arrays.asList(item1, item2), true);
        assertEquals(new BigDecimal("100").compareTo(subTotalList), 0);

    }

    @Test
    public void testCalculateSubTotalWithNullPrice() throws Exception {

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(item1).getPrice(); will(returnValue(null));
            allowing(item1).getListPrice(); will(returnValue(null));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});


        final BigDecimal subTotalSale = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true)
                .calculateSubTotal(Arrays.asList(item1, item2), false);
        assertEquals(new BigDecimal("40").compareTo(subTotalSale), 0);

        final BigDecimal subTotalList = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true)
                .calculateSubTotal(Arrays.asList(item1, item2), true);
        assertEquals(new BigDecimal("60").compareTo(subTotalList), 0);

    }


    @Test
    public void testCalculateSubTotalQtyNull() throws Exception {

        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        context.checking(new Expectations() {{
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getListPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(null));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getListPrice(); will(returnValue(new BigDecimal("60.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});


        final BigDecimal subTotalSale = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true)
                .calculateSubTotal(Arrays.asList(item1, item2), false);
        assertEquals(new BigDecimal("40").compareTo(subTotalSale), 0);

        final BigDecimal subTotalList = new DefaultAmountCalculationStrategy(BigDecimal.ZERO, true)
                .calculateSubTotal(Arrays.asList(item1, item2), true);
        assertEquals(new BigDecimal("60").compareTo(subTotalList), 0);

    }

    @Test
    public void testCalculateInternal() throws Exception {


        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getDetail(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final AmountCalculationResult rezTaxIncluded = new AmountCalculationResultImpl();

        new DefaultAmountCalculationStrategy(TAX, true).calculate(null, orderDelivery, false, rezTaxIncluded);

        assertEquals(new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotal()), 0);
        assertEquals(new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotalAmount()), 0);
        assertEquals(new BigDecimal("13.33").compareTo(rezTaxIncluded.getSubTotalTax()), 0);
        assertEquals(new BigDecimal("10").compareTo(rezTaxIncluded.getDelivery()), 0);
        assertEquals(new BigDecimal("10").compareTo(rezTaxIncluded.getDeliveryAmount()), 0);
        assertEquals(new BigDecimal("1.67").compareTo(rezTaxIncluded.getDeliveryTax()), 0);
        assertEquals(new BigDecimal("90").compareTo(rezTaxIncluded.getTotal()), 0);
        assertEquals(new BigDecimal("90").compareTo(rezTaxIncluded.getTotalAmount()), 0);
        assertEquals(new BigDecimal("15").compareTo(rezTaxIncluded.getTotalTax()), 0);

        final AmountCalculationResult rezTaxExcluded = new AmountCalculationResultImpl();

        new DefaultAmountCalculationStrategy(TAX, false).calculate(null, orderDelivery, false, rezTaxExcluded);

        assertEquals(new BigDecimal("80").compareTo(rezTaxExcluded.getSubTotal()), 0);
        assertEquals(new BigDecimal("96").compareTo(rezTaxExcluded.getSubTotalAmount()), 0);
        assertEquals(new BigDecimal("16").compareTo(rezTaxExcluded.getSubTotalTax()), 0);
        assertEquals(new BigDecimal("10").compareTo(rezTaxExcluded.getDelivery()), 0);
        assertEquals(new BigDecimal("12").compareTo(rezTaxExcluded.getDeliveryAmount()), 0);
        assertEquals(new BigDecimal("2").compareTo(rezTaxExcluded.getDeliveryTax()), 0);
        assertEquals(new BigDecimal("90").compareTo(rezTaxExcluded.getTotal()), 0);
        assertEquals(new BigDecimal("108").compareTo(rezTaxExcluded.getTotalAmount()), 0);
        assertEquals(new BigDecimal("18").compareTo(rezTaxExcluded.getTotalTax()), 0);

    }

    @Test
    public void testCalculateSingleDelivery() throws Exception {


        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrderDelivery orderDelivery = context.mock(CustomerOrderDelivery.class, "orderDelivery");

        context.checking(new Expectations() {{
            allowing(orderDelivery).getDetail(); will(returnValue(Arrays.asList(item1, item2)));
            allowing(orderDelivery).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final AmountCalculationResult rezTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true).calculate(null, orderDelivery);

        assertEquals(new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotal()), 0);
        assertEquals(new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotalAmount()), 0);
        assertEquals(new BigDecimal("13.33").compareTo(rezTaxIncluded.getSubTotalTax()), 0);
        assertEquals(new BigDecimal("10").compareTo(rezTaxIncluded.getDelivery()), 0);
        assertEquals(new BigDecimal("10").compareTo(rezTaxIncluded.getDeliveryAmount()), 0);
        assertEquals(new BigDecimal("1.67").compareTo(rezTaxIncluded.getDeliveryTax()), 0);
        assertEquals(new BigDecimal("90").compareTo(rezTaxIncluded.getTotal()), 0);
        assertEquals(new BigDecimal("90").compareTo(rezTaxIncluded.getTotalAmount()), 0);
        assertEquals(new BigDecimal("15").compareTo(rezTaxIncluded.getTotalTax()), 0);

        final AmountCalculationResult rezTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false).calculate(null, orderDelivery);

        assertEquals(new BigDecimal("80").compareTo(rezTaxExcluded.getSubTotal()), 0);
        assertEquals(new BigDecimal("96").compareTo(rezTaxExcluded.getSubTotalAmount()), 0);
        assertEquals(new BigDecimal("16").compareTo(rezTaxExcluded.getSubTotalTax()), 0);
        assertEquals(new BigDecimal("10").compareTo(rezTaxExcluded.getDelivery()), 0);
        assertEquals(new BigDecimal("12").compareTo(rezTaxExcluded.getDeliveryAmount()), 0);
        assertEquals(new BigDecimal("2").compareTo(rezTaxExcluded.getDeliveryTax()), 0);
        assertEquals(new BigDecimal("90").compareTo(rezTaxExcluded.getTotal()), 0);
        assertEquals(new BigDecimal("108").compareTo(rezTaxExcluded.getTotalAmount()), 0);
        assertEquals(new BigDecimal("18").compareTo(rezTaxExcluded.getTotalTax()), 0);

    }

    @Test
    public void testCalculateMultiDelivery() throws Exception {


        final CartItem item1 = context.mock(CartItem.class, "item1");
        final CartItem item2 = context.mock(CartItem.class, "item2");

        final CustomerOrderDelivery orderDelivery1 = context.mock(CustomerOrderDelivery.class, "orderDelivery1");
        final CustomerOrderDelivery orderDelivery2 = context.mock(CustomerOrderDelivery.class, "orderDelivery2");

        context.checking(new Expectations() {{
            allowing(orderDelivery1).getDetail(); will(returnValue(Arrays.asList(item1)));
            allowing(orderDelivery1).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(orderDelivery2).getDetail(); will(returnValue(Arrays.asList(item2)));
            allowing(orderDelivery2).getPrice(); will(returnValue(new BigDecimal("10.00")));
            allowing(item1).getPrice(); will(returnValue(new BigDecimal("20.00")));
            allowing(item1).getQty(); will(returnValue(new BigDecimal("2")));
            allowing(item2).getPrice(); will(returnValue(new BigDecimal("40.00")));
            allowing(item2).getQty(); will(returnValue(new BigDecimal("1")));
        }});

        final AmountCalculationResult rezTaxIncluded = new DefaultAmountCalculationStrategy(TAX, true)
                .calculate(null, Arrays.asList(orderDelivery1, orderDelivery2));

        assertEquals(new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotal()), 0);
        assertEquals(new BigDecimal("80").compareTo(rezTaxIncluded.getSubTotalAmount()), 0);
        assertEquals(new BigDecimal("13.33").compareTo(rezTaxIncluded.getSubTotalTax()), 0);
        assertEquals(new BigDecimal("20").compareTo(rezTaxIncluded.getDelivery()), 0);
        assertEquals(new BigDecimal("20").compareTo(rezTaxIncluded.getDeliveryAmount()), 0);
        assertEquals(new BigDecimal("3.33").compareTo(rezTaxIncluded.getDeliveryTax()), 0);
        assertEquals(new BigDecimal("100").compareTo(rezTaxIncluded.getTotal()), 0);
        assertEquals(new BigDecimal("100").compareTo(rezTaxIncluded.getTotalAmount()), 0);
        assertEquals(new BigDecimal("16.67").compareTo(rezTaxIncluded.getTotalTax()), 0);

        final AmountCalculationResult rezTaxExcluded = new DefaultAmountCalculationStrategy(TAX, false)
                .calculate(null, Arrays.asList(orderDelivery1, orderDelivery2));

        assertEquals(new BigDecimal("80").compareTo(rezTaxExcluded.getSubTotal()), 0);
        assertEquals(new BigDecimal("96").compareTo(rezTaxExcluded.getSubTotalAmount()), 0);
        assertEquals(new BigDecimal("16").compareTo(rezTaxExcluded.getSubTotalTax()), 0);
        assertEquals(new BigDecimal("20").compareTo(rezTaxExcluded.getDelivery()), 0);
        assertEquals(new BigDecimal("24").compareTo(rezTaxExcluded.getDeliveryAmount()), 0);
        assertEquals(new BigDecimal("4").compareTo(rezTaxExcluded.getDeliveryTax()), 0);
        assertEquals(new BigDecimal("100").compareTo(rezTaxExcluded.getTotal()), 0);
        assertEquals(new BigDecimal("120").compareTo(rezTaxExcluded.getTotalAmount()), 0);
        assertEquals(new BigDecimal("20").compareTo(rezTaxExcluded.getTotalTax()), 0);

    }
}
