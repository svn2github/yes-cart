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

package org.yes.cart.payment.impl;

import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.payment.service.PaymentGatewayParameterService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 25-Dec-2011
 * Time: 14:12:54
 */
public abstract class AbstractGswmPaymentGatewayImpl implements PaymentGateway {

    private PaymentGatewayParameterService paymentGatewayParameterService;

    private Collection<PaymentGatewayParameter> allParameters = null;


    /**
     * {@inheritDoc}
     */
    public String getName(final String locale) {
        String pgName = getParameterValue("name_" + locale);
        if (pgName == null) {
            pgName = getParameterValue("name");
        }
        if (pgName == null) {
            pgName = getLabel();
        }
        return pgName;
    }


    /**
     * {@inheritDoc}
     */

    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters() {
        if (allParameters == null) {
            allParameters = paymentGatewayParameterService.findAll(getLabel());
        }
        return allParameters;
    }

    /**
     * {@inheritDoc}
     */

    public void deleteParameter(final String parameterLabel) {
        paymentGatewayParameterService.deleteByLabel(getLabel(), parameterLabel);
    }

    /**
     * {@inheritDoc}
     */

    public void addParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.create(paymentGatewayParameter);
    }

    /**
     * {@inheritDoc}
     */

    public void updateParameter(final PaymentGatewayParameter paymentGatewayParameter) {
        paymentGatewayParameterService.update(paymentGatewayParameter);
    }

    /**
     * Work around promlem with wicket param values, when it can return
     * parameter value as string or as array of strings with single value.
     * This behavior depends from url encoding strategy
     * @param param parameters
     * @return value
     */
    public static String getSingleValue(final Object param) {
        if (param instanceof String) {
            return (String) param;
        } else if (param instanceof String[]) {
            if (((String[])param).length > 0) {
                return ((String [])param)[0];
            }
        }
        return null;

    }


    /**
     * Set parameter service.
     * @param paymentGatewayParameterService service to use.
     */
    public void setPaymentGatewayParameterService(
            final PaymentGatewayParameterService paymentGatewayParameterService) {
        this.paymentGatewayParameterService = paymentGatewayParameterService;
    }


    /**
     * Get the parameter value from given collection.
     *
     * @param valueLabel key to search
     * @return value or null if not found
     */
    public String getParameterValue(final String valueLabel) {
        final Collection<PaymentGatewayParameter> values = getPaymentGatewayParameters();
        for (PaymentGatewayParameter keyValue : values) {
            if (keyValue.getLabel().equals(valueLabel)) {
                return keyValue.getValue();
            }
        }
        return null;
    }


    /**
     * Get hidden fields to construct html.
     * @param fieldName filed name
     * @param value filed value
     * @return html string
     */
    protected String getHiddenFiled(final String fieldName, final Object value) {
        return "<input type='hidden' name='" + fieldName + "' value='" + value + "'>\n";
    }

    /**
     * Set express checkout method.
     * @param amount amount to checkout
     * @param currencyCode currency code
     * @return   result of call
     * @throws IOException in case of error
     */
    public Map<String, String> setExpressCheckoutMethod(BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    /**
     * Make express checkout.
     * @param token token
     * @param payerId payer identificator
     * @param amount amount
     * @param currencyCode currency code
     * @return   result of call
     * @throws IOException in case of error
     */
    public Map<String, String> doDoExpressCheckoutPayment(String token, String payerId, BigDecimal amount, String currencyCode) throws IOException {
        return null;  //nothing
    }

    /**
     * Obtain express checkout details.
     * @param token token
     * @return   result of call
     * @throws IOException in case of error
     */
    public Map<String, String> getExpressCheckoutDetails(String token) throws IOException {
        return null;  //nothing
    }

    /**
     * Dump map value into String.
     *
     * @param map given map
     * @return dump map as string
     */
    public static String dump(Map<?, ?> map) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(" : ");
            stringBuilder.append(entry.getValue());
        }

        return stringBuilder.toString();
    }


}
