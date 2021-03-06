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

package org.yes.cart.domain.query.impl;

import org.yes.cart.constants.Constants;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.query.PriceNavigation;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * PriceNavigation responsible for compose/decompose url parameter to  java objects. 
 *
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-May-2011
 * Time: 09:48:51
 */
public class PriceNavigationImpl implements PriceNavigation {

    private final static String PRICE_REQUEST_DELIMITER = "-";


    /**
     * Expected value in following format CUR-LOW-HIGH format
     *
     * @param val value rfom request
     * @return filled price tier node.
     */
    public Pair<String, Pair<BigDecimal, BigDecimal>> decomposePriceRequestParams(final String val) {
        final String[] currencyPriceBorders = val.split(PRICE_REQUEST_DELIMITER);
        final Pair<BigDecimal, BigDecimal> priceBorders =
                new Pair<BigDecimal, BigDecimal>(
                        new BigDecimal(currencyPriceBorders[1]),
                        new BigDecimal(currencyPriceBorders[2]));
        return new Pair<String, Pair<BigDecimal, BigDecimal>>(
                currencyPriceBorders[0],
                priceBorders
        );
    }

    /**
     * Compoce price tier into string representation.
     *
     * @param currency   currency
     * @param lowBorder  low price
     * @param highBorder high price
     * @return string representation in CUR-LOW-HIGH format
     */
    public String composePriceRequestParams(final String currency, final BigDecimal lowBorder, final BigDecimal highBorder) {
        return composePriceRequestParams(currency, lowBorder, highBorder, PRICE_REQUEST_DELIMITER, PRICE_REQUEST_DELIMITER);
    }


    /**
     * Compoce price tier into string representation.
     *
     * @param currency           currency
     * @param lowBorder          low price
     * @param highBorder         high price
     * @param currencyDelimitter delimitter between currency and prices
     * @param priceDelimitter    price delimitter
     * @return string representation in CUR-LOW-HIGH format
     */
    public String composePriceRequestParams(final String currency,
                                            final BigDecimal lowBorder,
                                            final BigDecimal highBorder,
                                            final String currencyDelimitter,
                                            final String priceDelimitter) {

        DecimalFormat decimalFormat = new DecimalFormat(Constants.MONEY_FORMAT_PRICE_NAVIGATION);
        StringBuilder builder = new StringBuilder();
        builder.append(currency);
        builder.append(currencyDelimitter);
        builder.append(decimalFormat.format(lowBorder));
        builder.append(priceDelimitter);
        builder.append(decimalFormat.format(highBorder));
        return builder.toString();
    }

}
