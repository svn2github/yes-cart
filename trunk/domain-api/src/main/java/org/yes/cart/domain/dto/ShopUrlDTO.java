package org.yes.cart.domain.dto;

import java.io.Serializable;

/**
 *
 * Shop URLs DTO.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ShopUrlDTO   extends Serializable {

    /**
     * @return primary key value.
     */
    public long getStoreUrlId();

    public void setStoreUrlId(long storeUrlId);

    /**
     * @return {@link org.yes.cart.domain.entity.Shop}
     */
    public long getShopId();

    public void setShopId(long shopId);


    /**
     * @return shop url.
     */
    public String getUrl();

    public void setUrl(String url);


}