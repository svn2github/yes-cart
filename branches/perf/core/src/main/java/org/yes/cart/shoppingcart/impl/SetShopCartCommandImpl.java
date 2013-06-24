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

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.ApplicationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 22-May-2011
 * Time: 14:12:54
 */
public class SetShopCartCommandImpl  extends AbstractCartCommandImpl implements ShoppingCartCommand {

    private static final long serialVersionUID = 2010522L;

    public static final String CMD_KEY = "setShopIdCmd";

    private final long value;

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     */
    public void execute(final ShoppingCart shoppingCart) {
        shoppingCart.getShoppingContext().setShopId(value);
        setModifiedDate(shoppingCart);
    }

    /**
     * @return command key
     */
    public String getCmdKey() {
        return CMD_KEY;
    }

    /**
     * @param applicationContext application context
     * @param parameters         page parameters
     */
    public SetShopCartCommandImpl(
            final ApplicationContext applicationContext, final Map parameters) {
        super();
        value = NumberUtils.createLong(String.valueOf(parameters.get(CMD_KEY)));
    }

}
