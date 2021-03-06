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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.domain.queryobject.FilteredNavigationRecord;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Price service.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface PriceService extends GenericService<SkuPrice> {


    /**
     * Get minimal price for given product skus (all), shop, currency and quantity.
     * Exchange rate will be used for recalculate price if price does not present
     * in price list for given currency.
     *
     * @param productSkus  product skus
     * @param shop         shop            sku will be c
     * @param selectedSku  optional sku to filter the prices. if null all product skus will be  considered to determinate minimal price
     * @param currencyCode desirable currency
     * @param quantity     quantity
     * @return SkuPrice
     */
    SkuPrice getMinimalRegularPrice(
            final Collection<ProductSku> productSkus,
            final String selectedSku,
            final Shop shop,
            final String currencyCode,
            final BigDecimal quantity);




    /**
     * Atm we can have different price definitions (lowest in list with high priority):
     * price without any time limitations;
     * price, which starts in infinitive past and will be end at some date;
     * price, which has the start date but no end date;
     * price with start and end date.
     *
     * @param skuPrices all prices filtered by currency, and quantity for all skus
     * @return the list of sku prices, which is filtered by time frame
     */
    List<SkuPrice> getSkuPricesFilteredByTimeFrame(Collection<SkuPrice> skuPrices);


    /**
     * Get the sku prices filtered by shop.
     * Exchange rate will be used if shop has not prices
     * for gived curency.
     *
     * @param productSkus  product skus
     * @param shop         shop filter
     * @param currencyCode currency code
     * @return list of sku prices
     */
    List<SkuPrice> getSkuPrices(Collection<ProductSku> productSkus, Shop shop, String currencyCode);


    /**
     * Get the sku prices filtered by quantity. Example:
     * ProductSKU1 has defined price ties 1 - 100 USD, 2 - 87 USD, 5 - 85 USD
     * ProductSKU2 has defined price ties 1 - 100 USD, 2 - 98 USD, 3 - 90 USD
     * <p/>
     * For quantity 4 result will hold only two SkuPrice:
     * ProductSKU1 87 USD
     * ProductSKU2 90 USD
     *
     * @param prices   sku prices
     * @param quantity
     * @return list of sku prices filtered by quantity
     */
    List<SkuPrice> getSkuPricesFilteredByQuantity(List<SkuPrice> prices, BigDecimal quantity);


    /**
     * Get the list of skus prices filtered by currency.
     *
     * @param skuPrices    sku prices with all currencies
     * @param currencyCode currency code filter
     * @return list of skus prices filtered by currency code
     */
    List<SkuPrice> getSkuPriceFilteredByCurrency(List<SkuPrice> skuPrices, String currencyCode);

    /**
     * Get the list of skus prices filtered by shop.
     *
     * @param productSkus produc skus
     * @param shop        shop
     * @return list of skus prices filtered by shop
     */
    List<SkuPrice> getSkuPriceFilteredByShop(Collection<ProductSku> productSkus, Shop shop);

    /**
     * Get navigation records for prices
     *
     * @param priceTierTree given price tier tree
     * @param currency      currence code
     * @param shop          currenct shop
     * @return list of navigation records for given price tree and currecny
     */
    List<FilteredNavigationRecord> getPriceNavigationRecords(
            PriceTierTree priceTierTree,
            String currency,
            Shop shop);

    /**
     * Recalculate derived prices. Derived prices - prices not in default currency, for example default shop currency is
     * USD and shop support EUR also, but has not price lists for EUR currency and used currency exchange rate instead.
     * Use delete / insert paragigm instead of insert/update.
     *
     * @param shop            shop
     * @param derivedCurrency target currency
     * @return quantity of created records.
     */
    int updateDerivedPrices(Shop shop, String derivedCurrency);


    /**
     * Delete derived prices in given shop.
     *
     * @param shop            given shop id
     * @param derivedCurrency derived currency
     */
    void deleteDerivedPrices(Shop shop, String derivedCurrency);

}
