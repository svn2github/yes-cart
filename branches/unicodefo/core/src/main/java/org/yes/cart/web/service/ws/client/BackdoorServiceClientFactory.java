package org.yes.cart.web.service.ws.client;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.yes.cart.web.service.ws.BackdoorService;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Facrory responsible to get back door service.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/29/12
 * Time: 7:56 PM
 */
public class BackdoorServiceClientFactory implements CallbackHandler {

    private final static ConcurrentHashMap<String, String> concurrentHashMap;

    private final static JaxWsProxyFactoryBean factory;

    static {
        concurrentHashMap = new ConcurrentHashMap<String, String>();
        factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(BackdoorService.class);
    }


    /**
     * Get back door service.
     *
     * @param userName user name
     * @param password password
     * @param url      url
     * @return {@link BackdoorService}
     */
    public BackdoorService getBackdoorService(final String userName, final String password, final String url) {

        final BackdoorService backdoorService;

        synchronized (factory) {
            factory.setAddress(url);
            //factory.setAddress("http://localhost:8080/yes-shop/services/backdoor");
            backdoorService = (BackdoorService) factory.create();

            concurrentHashMap.put(userName, password);
        }

        final Client client = ClientProxy.getClient(backdoorService);

        final Endpoint endpoint = client.getEndpoint();

        final WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(new HashMap<String, Object>() {
            {
                put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);

                put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);

                put(WSHandlerConstants.USER, userName);

                put(WSHandlerConstants.PW_CALLBACK_CLASS, "org.yes.cart.web.service.ws.client.BackdoorServiceClientFactory");


            }
        });
        endpoint.getOutInterceptors().add(wssOut);

        return backdoorService;

    }

    /**
     * {@inheritDoc}
     */
    public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
            pc.setPassword(concurrentHashMap.get(pc.getIdentifier()));
            return;
        }
    }
}
