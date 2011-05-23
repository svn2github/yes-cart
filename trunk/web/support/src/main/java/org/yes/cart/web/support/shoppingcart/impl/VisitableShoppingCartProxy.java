package org.yes.cart.web.support.shoppingcart.impl;


import org.yes.cart.domain.dto.CartItem;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.ShoppingContext;
import org.yes.cart.domain.dto.OrderInfo;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.shoppingcart.RequestRuntimeContainer;
import org.yes.cart.web.support.shoppingcart.VisitableShoppingCart;
import org.yes.cart.web.support.util.cookie.CookieTuplizer;
import org.yes.cart.web.support.util.cookie.UnableToObjectizeCookieException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Proxy to Shopping Cart business object.
 * Initialize instance on first get.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:13:23 PM
 * <p/>
 * TODO dymamic proxy
 */
public class VisitableShoppingCartProxy implements VisitableShoppingCart {

    private static final long serialVersionUID = 20110227L;

    private VisitableShoppingCart visitableShoppingCart;
    private final VisitableShoppingCart defaultVisitableShoppingCart;
    private final HttpServletRequest httpRequest;
    private final CookieTuplizer tuplizer;
    private final RequestRuntimeContainer requestRuntimeContainer;
    private final Cookie[] cookies;


    /**
     * {@inheritDoc}
     */
    public VisitableShoppingCartProxy(
            final HttpServletRequest httpRequest,
            final VisitableShoppingCart defaultVisitableShoppingCart,
            final CookieTuplizer tuplizer) {
        this.httpRequest = httpRequest;
        this.cookies = httpRequest.getCookies();
        this.defaultVisitableShoppingCart = defaultVisitableShoppingCart;
        this.tuplizer = tuplizer;
        requestRuntimeContainer = (RequestRuntimeContainer) httpRequest.getAttribute(WebParametersKeys.SESSION_OBJECT_NAME);
        if (this.defaultVisitableShoppingCart.getCurrencyCode() == null) {
            //this.defaultVisitableShoppingCart.setCurrencyCode(requestRuntimeContainer.getShop().getDefaultCurrency());
        }
        if (this.defaultVisitableShoppingCart.getShopId() == 0) {
            this.defaultVisitableShoppingCart.setShopId(requestRuntimeContainer.getShop().getShopId());
        }

    }

    /**
     * {@inheritDoc}
     */
    public VisitableShoppingCart getVisitableShoppingCart() {

        synchronized (tuplizer) {
            if (visitableShoppingCart == null) {
                try {
                    visitableShoppingCart = tuplizer.toObject(
                            cookies,
                            defaultVisitableShoppingCart);
                } catch (UnableToObjectizeCookieException utoce) {
                    // do nothing as shopping cart default instance should have been already injected.
                    visitableShoppingCart = defaultVisitableShoppingCart;
                }
            }
            return visitableShoppingCart;
        }
    }

    /**
     * Clean current cart and prepare it to reuse.
     */
    public void clean() {
        getVisitableShoppingCart().clean();
    }

    /**
     * {@inheritDoc}
     */
    public void accept(ShoppingCartCommand command) {
        getVisitableShoppingCart().accept(command);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSeparateBillingAddress() {
        return getVisitableShoppingCart().isSeparateBillingAddress();
    }


    /**
     * {@inheritDoc}
     */
    public List<CartItem> getCartItemList() {
        return getVisitableShoppingCart().getCartItemList();
    }


    /**
     * {@inheritDoc}
     */
    public String getGuid() {
        return getVisitableShoppingCart().getGuid();
    }

    /**
     * {@inheritDoc}
     */
    public boolean addProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {
        return getVisitableShoppingCart().addProductSkuToCart(sku, quantity);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setProductSkuToCart(final ProductSkuDTO sku, final BigDecimal quantity) {
        return getVisitableShoppingCart().setProductSkuToCart(sku, quantity);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeCartItem(final ProductSkuDTO productSku) {
        return getVisitableShoppingCart().removeCartItem(productSku);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeCartItemQuantity(final ProductSkuDTO productSku, final BigDecimal quantity) {
        return getVisitableShoppingCart().removeCartItemQuantity(productSku, quantity);
    }

    /**
     * {@inheritDoc}
     */
    public int getCartItemsCount() {
        return getVisitableShoppingCart().getCartItemsCount();
    }


    /**
     * {@inheritDoc}
     */
    public BigDecimal getCartSubTotal(List<? extends CartItem> items) {
        return getVisitableShoppingCart().getCartSubTotal(items);
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrencyCode() {
        return getVisitableShoppingCart().getCurrencyCode();
    }




    /**
     * {@inheritDoc}
     */
    public String getCustomerName() {
        return getVisitableShoppingCart().getCustomerName();
    }




    /**
     * {@inheritDoc}
     */
    public Date getModifiedDate() {
        return getVisitableShoppingCart().getModifiedDate();
    }

    /**
     * {@inheritDoc}
     */
    public void setModifiedDate(final Date modified) {
        getVisitableShoppingCart().setModifiedDate(modified);
    }

    /**
     * {@inheritDoc}
     */
    public long getShopId() {
        return getVisitableShoppingCart().getShopId();
    }

    /**
     * {@inheritDoc}
     */
    public void setShopId(final long shopId) {
        getVisitableShoppingCart().setShopId(shopId);
    }

    /**
     * Get order message.
     *
     * @return order message
     */
    public String getOrderMessage() {
        return getVisitableShoppingCart().getOrderMessage();
    }


    /**
     * Set product sku price
     *
     * @param skuCode product sku  code
     * @param price   price to set
     * @return true if price has been set
     */
    public boolean setProductSkuPrice(final String skuCode, final BigDecimal price) {
        return getVisitableShoppingCart().setProductSkuPrice(skuCode, price);
    }

    /**
     * Is sku code present in cart
     *
     * @param skuCode product sku code
     * @return true if sku code present in cart
     */
    public boolean contains(final String skuCode) {
        return getVisitableShoppingCart().contains(skuCode);
    }

    /**
     * @param skuCode sku code
     * @return idex of cart item for this sku
     */
    public int indexOf(final String skuCode) {
        return getVisitableShoppingCart().indexOf(skuCode);
    }

    /**
     * Get customer email. In fact custome is logged on if email not null
     * and cart modification date not more that 30 minutes(configurable)
     *
     * @return customer name or null if customer is anonymous
     */
    public String getCustomerEmail() {
        return getVisitableShoppingCart().getCustomerEmail();
    }


    /**
     * Get logon state.
     *
     * @return Logon state
     */
    public int getLogonState() {
        return getVisitableShoppingCart().getLogonState();
    }

    /**
     * Get carrier shipping SLA.
     *
     * @return carries sla id.
     */
    public Integer getCarrierSlaId() {
        return getVisitableShoppingCart().getCarrierSlaId();
    }


    public ShoppingContext getShoppingContext() {
        return getVisitableShoppingCart().getShoppingContext();
    }

    public OrderInfo getOrderInfo() {
        return getVisitableShoppingCart().getOrderInfo();
    }
}
