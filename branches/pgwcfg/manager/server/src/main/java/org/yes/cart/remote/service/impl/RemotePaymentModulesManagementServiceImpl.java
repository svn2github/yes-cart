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
package org.yes.cart.remote.service.impl;

import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.remote.service.RemotePaymentModulesManagementService;
import org.yes.cart.service.payment.PaymentModulesManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Remote service to manage payment gateways and his parameters.
 * Delete and add parameters operation are prohibited for security reason. This two operations are very rare
 * and can not be performed without tech personal support.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/22/12
 * Time: 8:59 PM
 */
public class RemotePaymentModulesManagementServiceImpl implements RemotePaymentModulesManagementService {


    private PaymentModulesManager paymentModulesManager;
    
    
    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAllowedPaymentGateways(final String lang) {
        
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(false);
        final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>(descriptors.size());
        for (PaymentGatewayDescriptor descr :  descriptors) {
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(descr.getLabel());
            rez.add(new Pair<String, String>(
                    paymentGateway.getLabel(),
                    descr.getName()
            ));
        }
        return rez;

    }

    /** {@inheritDoc}*/
    public List<Pair<String, String>> getAvailablePaymentGateways(final String lang) {

        final List<Pair<String, String>> allowed = getAllowedPaymentGateways(lang);
        final List<PaymentGatewayDescriptor> descriptors = paymentModulesManager.getPaymentGatewaysDescriptors(true);
        final List<Pair<String, String>> rez = new ArrayList<Pair<String, String>>(descriptors.size());

        for (PaymentGatewayDescriptor descr :  descriptors) {
            final PaymentGateway paymentGateway = paymentModulesManager.getPaymentGateway(descr.getLabel());
            final Pair<String, String> pairCandidate = new Pair<String, String>(
                    paymentGateway.getLabel(),
                    descr.getName()
            );
            if (!allowed.contains(pairCandidate) ) {
                rez.add(pairCandidate);
            }

        }

        return rez;
    }

    /** {@inheritDoc}*/
    public Collection<PaymentGatewayParameter> getPaymentGatewayParameters(final String gatewayLabel, final String lang) {
        return paymentModulesManager.getPaymentGateway(gatewayLabel).getPaymentGatewayParameters();
    }

    /** {@inheritDoc}*/
    public boolean updateConfigurationParameter(final String gatewayLabel, final String paramaterLabel, final String parameterValue) {
        final Collection<PaymentGatewayParameter> params = paymentModulesManager.getPaymentGateway(gatewayLabel).getPaymentGatewayParameters();
        for (PaymentGatewayParameter param : params ) {
            if (param.getLabel().equals(paramaterLabel)) {
                param.setValue(parameterValue);
                paymentModulesManager.getPaymentGateway(gatewayLabel).updateParameter(param);
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc}*/
    public void allowPaymentGateway(final String label) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc}*/
    public void disallowPaymentGateway(final String label) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
