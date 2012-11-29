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

import org.junit.Test;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class LogoutCommandImplTest {

    @Test
    public void testExecute() {
        ShoppingCart shoppingCart = new ShoppingCartImpl();
        Map<String, String> params = new HashMap<String, String>();
        params.put(LoginCommandImpl.EMAIL, "test@test.com");
        params.put(LoginCommandImpl.NAME, "John Doe");
        new LoginCommandImpl(null, params)
                .execute(shoppingCart);
        assertNotNull(shoppingCart.getShoppingContext().getCustomerEmail());
        assertEquals("Test that auth in spring security context",
                "test@test.com",
                shoppingCart.getShoppingContext().getCustomerEmail());
        new LogoutCommandImpl(null, null)
                .execute(shoppingCart);
        assertNull(shoppingCart.getCustomerEmail());
        assertNull(shoppingCart.getCustomerName());
        assertEquals(ShoppingCart.NOT_LOGGED, shoppingCart.getLogonState());
    }
}
