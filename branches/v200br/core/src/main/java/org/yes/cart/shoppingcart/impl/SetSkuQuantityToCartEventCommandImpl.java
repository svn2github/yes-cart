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

import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.util.ShopCodeContext;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * Set sku quantity in cart command class.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SetSkuQuantityToCartEventCommandImpl  extends AbstractSkuCartCommandImpl {

    private static final long serialVersionUID = 20110312L;

    public SetSkuQuantityToCartEventCommandImpl(final PriceService priceService,
                                                final ProductService productService,
                                                final ShopService shopService) {
        super(priceService, productService, shopService);
    }

    /**
     * {@inheritDoc}
     */
    public String getCmdKey() {
        return CMD_SETQTYSKU;
    }

    /** {@inheritDoc} */
    @Override
    protected void execute(final ShoppingCart shoppingCart,
                           final ProductSku productSku, final Map<String, Object> parameters) {
        final String skuQty = (String) parameters.get(CMD_SETQTYSKU_P_QTY);
        if (productSku != null && skuQty != null) {
            shoppingCart.setProductSkuToCart(productSku.getCode(), new BigDecimal(skuQty));
            recalculatePrice(shoppingCart, productSku);
            ShopCodeContext.getLog(this).debug("Add product sku with code {} and qty {} to cart",
                    productSku.getCode(),
                    skuQty);
            markDirty(shoppingCart);
        }
    }

}
