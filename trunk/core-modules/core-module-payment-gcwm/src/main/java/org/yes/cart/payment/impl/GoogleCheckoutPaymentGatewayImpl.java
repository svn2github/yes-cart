package org.yes.cart.payment.impl;

import com.google.checkout.sdk.commands.ApiContext;
import com.google.checkout.sdk.commands.CartPoster;
import com.google.checkout.sdk.commands.Environment;
import com.google.checkout.sdk.domain.*;
import com.google.checkout.sdk.notifications.BaseNotificationDispatcher;
import com.google.checkout.sdk.notifications.Notification;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.codec.Base64;
import org.springframework.util.Assert;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.service.domain.CarrierSlaService;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;

/**
 * Google checkout payment gateway
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 12/25/11
 * Time: 5:13 PM
 */
public class GoogleCheckoutPaymentGatewayImpl
        extends AbstractGswmPaymentGatewayImpl
        implements PaymentGatewayExternalForm, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleCheckoutPaymentGatewayImpl.class);


    private ApplicationContext applicationContext;

    private CarrierSlaService carrierSlaService;


    public static final String ORDER_GUID = "orderGuid";  //this id our order guid

    //test or lice env
    static final String GC_ENVIRONMENT = "GC_ENVIRONMENT";

    // merchant id
    static final String GC_MERCHANT_ID = "GC_MERCHANT_ID";

    // key
    static final String GC_MERCHANT_KEY = "GC_MERCHANT_KEY";

    // Where we have to post signed xml
    static final String GC_POST_URL = "GC_POST_URL";

    // html code for submit btn
    static final String GC_SUBMIT_BTN = "GC_SUBMIT_BTN";

    private final ObjectFactory objectFactory = new ObjectFactory();


    private final static PaymentGatewayFeature paymentGatewayFeature = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false, false,
            true, true,
            null
    );


    /**
     * {@inheritDoc}
     */
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return paymentGatewayFeature;
    }

    /**
     * {@inheritDoc}
     */
    public Payment authorizeCapture(final Payment payment) {
        return payment;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Shipment not included. Will be added at capture operation.
     */
    public Payment authorize(final Payment paymentIn) {
        LOG.info("#authorize");
        System.out.println("#authorize");

        return (Payment) SerializationUtils.clone(paymentIn);


        //final CheckoutRedirect checkoutRedirect = apiContext.cartPoster().postCart(checkoutShoppingCart);
        //payment.setTransactionReferenceId(checkoutRedirect.getSerialNumber());
        // payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_OK);

    }

    /**
     * {@inheritDoc}
     */
    public Payment reverseAuthorization(final Payment payment) {
        LOG.info("#reverseAuthorization");
        System.out.println("#reverseAuthorization");

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment capture(final Payment payment) {
        LOG.info("#capture");
        System.out.println("#capture");

        payment.setTransactionOperation(CAPTURE);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment voidCapture(final Payment payment) {
        LOG.info("#voidCapture");
        System.out.println("#voidCapture");

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    public Payment refund(final Payment payment) {
        LOG.info("#refund");
        System.out.println("#refund");

        return payment;
    }

    /**
     * {@inheritDoc}
     * All fields are hidden, hence not need to localize and etc.
     */
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderGuid, final Payment payment) {

        Assert.notNull(payment, "The payment details require for google checkout payment gateway");

        final CheckoutShoppingCart checkoutShoppingCart = createGoogleCart(payment, orderGuid);

        final String cartXml = checkoutShoppingCart.toString();

        //System.out.println(">>>>> " + cartXml);

        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getHiddenFiled("cart", new String(Base64.encode(cartXml.getBytes()))));

        stringBuilder.append(getHiddenFiled("signature", getSignature(cartXml, getParameterValue(GC_MERCHANT_KEY))));

        return stringBuilder.toString();

    }


    /** {@inheritDoc} */
    public void handleNotification(final HttpServletRequest request, final HttpServletResponse response) {

        final ApiContext apiContext = new ApiContext(
                "PRODUCTION".equalsIgnoreCase(getParameterValue(GC_ENVIRONMENT)) ? Environment.PRODUCTION : Environment.SANDBOX,
                getParameterValue(GC_MERCHANT_ID),
                getParameterValue(GC_MERCHANT_KEY),
                "USD" //wtf
        );

        dumpRequest(request);

        apiContext.handleNotification(

                new BaseNotificationDispatcher(request, response) {

                    /** {@inheritDoc} */
                    @Override
                    protected void onNewOrderNotification(final OrderSummary orderSummary, final NewOrderNotification notification) throws Exception {
                        final String msg = "#onNewOrderNotification order summary is : " + orderSummary.toString() + " notification is  " + notification;
                        LOG.info(msg);
                        System.out.println(msg);
                    }

                    @Override
                    public void onAllNotifications(final OrderSummary orderSummary,
                                                   final Notification notification) {
                        final String msg = "#onAllNotifications order summary is : " + orderSummary.toString() + " notification is  " + notification;
                        LOG.info(msg);
                        System.out.println(msg);

                    }

                    @Override
                    public void onAuthorizationAmountNotification(final OrderSummary orderSummary,
                                                                  final AuthorizationAmountNotification notification) {
                        final String msg = "#onAuthorizationAmountNotification order summary is : " + orderSummary.toString() + " notification is  " + notification;
                        LOG.info(msg);
                        System.out.println(msg);

                        System.out.println(
                                "Order " + notification.getGoogleOrderNumber()
                                        + " authorized and ready to ship to:"
                                        + orderSummary.getBuyerShippingAddress().getContactName());
                    }

                    @Override
                    protected void onOrderStateChangeNotification(final OrderSummary orderSummary, final OrderStateChangeNotification notification) throws Exception {
                        final String msg = "#onOrderStateChangeNotification order summary is : " + orderSummary.toString() + " notification is  " + notification;
                        LOG.info(msg);
                        System.out.println(msg);

                    }

                    @Override
                    public boolean hasAlreadyHandled(final String serialNumber,
                                                     final OrderSummary orderSummary,
                                                     final Notification notification) {
                        final String msg = "#hasAlreadyHandled order summary is : " + orderSummary.toString() + " notification is  " + notification + " serialNumber " + serialNumber;
                        LOG.info(msg);
                        System.out.println(msg);

                        // NOTE: We'll have to look up serial numbers in our database
                        // before using this for real
                        return false;
                    }

                    @Override
                    protected void rememberSerialNumber(final String serialNumber,
                                                        final OrderSummary orderSummary, Notification notification) {
                        final String msg = "#rememberSerialNumber order summary is : " + orderSummary.toString() + " notification is  " + notification + " serialNumber " + serialNumber;
                        LOG.info(msg);
                        System.out.println(msg);

                        // NOTE: We'll have to remember serial numbers in our database,
                        // before using this for real
                    }
                }

        );


    }

    /**
     * Create {@link CheckoutShoppingCart} from given payment and payment details.
     *
     * @param payment   given {@link Payment}
     * @param orderGuid order guid.
     * @return created {@link CheckoutShoppingCart}
     */
    CheckoutShoppingCart createGoogleCart(final Payment payment, final String orderGuid) {

        final ApiContext apiContext = new ApiContext(
                "PRODUCTION".equalsIgnoreCase(getParameterValue(GC_ENVIRONMENT)) ? Environment.PRODUCTION : Environment.SANDBOX,
                getParameterValue(GC_MERCHANT_ID),
                getParameterValue(GC_MERCHANT_KEY),
                payment.getOrderCurrency()
        );


        final CartPoster.CheckoutShoppingCartBuilder cartBuilder = apiContext.cartPoster().makeCart();

        final MerchantCheckoutFlowSupport flowSupport = objectFactory.createMerchantCheckoutFlowSupport();

        /* ATM i am not going to support partner tracking
        flowSupport.setParameterizedUrls(objectFactory.createMerchantCheckoutFlowSupportParameterizedUrls());
        flowSupport.getParameterizedUrls().getParameterizedUrl().add()
        final ParameterizedUrl.Parameters params = objectFactory.createParameterizedUrlParameters();
        params.getUrlParameter();
        final MerchantCheckoutFlowSupport.ParameterizedUrls   parameterizedUrls    = ;
        final ParameterizedUrl purl = objectFactory.createParameterizedUrl();
        purl.setParameters(params);
        parameterizedUrls.getParameterizedUrl().add(purl); */


        final CheckoutShoppingCart.CheckoutFlowSupport cartFlowSupport = objectFactory.createCheckoutShoppingCartCheckoutFlowSupport();

        cartFlowSupport.setMerchantCheckoutFlowSupport(flowSupport);

        final String currency = payment.getOrderCurrency();

        for (PaymentLine paymentLine : payment.getOrderItems()) {
            //Products only
            if (!paymentLine.isShipment()) {
                cartBuilder.addItem(
                        createShipmentItem(
                                currency,
                                apiContext,
                                paymentLine)
                );
            }
        }

        //set shipping methods to select on google
        flowSupport.setShippingMethods(
                createShipmentMethod(
                        currency,
                        objectFactory)
        );


        final AnyMultiple anyMultiple = objectFactory.createAnyMultiple(); //just put our order id

        anyMultiple.getContent().add(orderGuid);

        final CheckoutShoppingCart checkoutShoppingCart = cartBuilder.build();

        checkoutShoppingCart.setCheckoutFlowSupport(cartFlowSupport);

        /**
         * The <merchant-private-data> tag contains any well-formed XML sequence that should accompany an order.
         * Google Checkout will return this XML in the <merchant-calculation-callback> and the <new-order-notification> for the order.
         */
        checkoutShoppingCart.getShoppingCart().setMerchantPrivateData(anyMultiple);

        return checkoutShoppingCart;
    }

    /**
     * Create item in cart.
     *
     * @param currency    currency
     * @param apiContext  api context
     * @param paymentLine payment line, that represent item in cart
     * @return item for google cart
     */
    private Item createShipmentItem(final String currency, final ApiContext apiContext, final PaymentLine paymentLine) {

        final Money money = apiContext.makeMoney(paymentLine.getUnitPrice());
        money.setCurrency(currency);

        final Item item = new Item();
        item.setItemName(paymentLine.getSkuName());
        item.setItemDescription(paymentLine.getSkuName());
        item.setMerchantItemId(paymentLine.getSkuCode());
        item.setQuantity(paymentLine.getQuantity().intValue());
        item.setUnitPrice(money);
        return item;

    }

    /**
     * Create shipment method. CPOINT shipment method already selected by customer, so
     * is sme cases need customise how it should be represent in google cart.
     *
     * @param currency      order curreny
     * @param objectFactory object factory
     * @return {@link MerchantCheckoutFlowSupport.ShippingMethods}
     */
    MerchantCheckoutFlowSupport.ShippingMethods createShipmentMethod(final String currency,
                                                                     final ObjectFactory objectFactory) {

        final MerchantCheckoutFlowSupport.ShippingMethods shippingMethods =
                objectFactory.createMerchantCheckoutFlowSupportShippingMethods();

        for (CarrierSla sla : getUniqueNames(getCarrierSlaService().findByCurrency(currency))) {

            final FlatRateShipping.Price price = objectFactory.createFlatRateShippingPrice();
            price.setCurrency(currency);
            price.setValue(sla.getPrice());

            final FlatRateShipping flatRateShipping = objectFactory.createFlatRateShipping();
            flatRateShipping.setName(sla.getName());
            flatRateShipping.setPrice(price);

            shippingMethods.getFlatRateShippingOrMerchantCalculatedShippingOrPickup().add(flatRateShipping);

        }


        return shippingMethods;
    }

    private List<CarrierSla> getUniqueNames(final List<CarrierSla> slas) {
        final Set<String> stringSet = new HashSet<String>(slas.size());
        final List<CarrierSla> carrierSlas = new ArrayList<CarrierSla>(slas.size());
        for (CarrierSla sla : slas) {
            if (!stringSet.contains(sla.getName())) {
                stringSet.add(sla.getName());
                carrierSlas.add(sla);
            }
        }
        return carrierSlas;
    }


    /**
     * {@inheritDoc}
     */
    public Payment createPaymentPrototype(final Map parametersMap) {
        return new PaymentImpl();
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return "googleCheckoutPaymentGateway";
    }


    /**
     * Create signature for given xml request to google checkout.
     *
     * @param xmlToSign   given xml.
     * @param merchantKey the merchant key
     * @return xml signature.
     */
    String getSignature(final String xmlToSign, final String merchantKey) {

        Assert.notNull(xmlToSign, "XML to sign must be provided");
        Assert.notNull(merchantKey, "the merchant key must be provided");
        final SecretKey secretKey = new SecretKeySpec(merchantKey.getBytes(), "HmacSHA1");

        try {
            final Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            final byte[] text = xmlToSign.getBytes();
            return new String(Base64.encode(mac.doFinal(text))).trim();
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Cant create signature", e);
        } catch (InvalidKeyException e) {
            LOG.error("Cant create signature", e);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getPostActionUrl() {
        return getParameterValue(GC_POST_URL);
    }

    /**
     * {@inheritDoc}
     */
    public String getSubmitButton() {
        return getParameterValue(GC_SUBMIT_BTN);
    }

    /**
     * {@inheritDoc}
     */
    public String restoreOrderGuid(Map privateCallBackParameters) {
        return getSingleValue(privateCallBackParameters.get(ORDER_GUID));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSuccess(Map<String, String> nvpCallResult) {
        return true;  //todo
    }


    public static void dumpRequest(final ServletRequest request) {
        Enumeration en = request.getParameterNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            System.out.println(MessageFormat.format("HttpUtil#dumpRequest local at gc param key = [{0}] value = [{1}]",
                    key,
                    request.getParameter((String) key)));
        }

        en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            final Object key = en.nextElement();
            System.out.println(MessageFormat.format("HttpUtil#dumpRequest local at gc attr  key = [{0}] value = [{1}]",
                    key,
                    request.getAttribute((String) key)));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * Set the {@link CarrierSlaService} to work with sla.
     *
     * @return {@link CarrierSlaService}
     */
    private CarrierSlaService getCarrierSlaService() {
        if (carrierSlaService == null) {
            carrierSlaService = applicationContext.getBean("carrierSlaService", CarrierSlaService.class);
        }
        return carrierSlaService;
    }
}
