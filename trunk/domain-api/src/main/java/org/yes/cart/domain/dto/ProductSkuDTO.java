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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.util.Collection;
import java.util.Map;

/**
 * Product Sku light weight product object.
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 10:37:01 PM
 */
public interface ProductSkuDTO extends Identifiable {


    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getSkuId();

    /**
     * Set pk value
     *
     * @param skuId pk value
     */
    void setSkuId(final long skuId);

    /**
     * @return sku code
     */
    String getCode();

    /**
     * Set sku code.
     *
     * @param code sku code
     */
    void setCode(final String code);

    /**
     * Get name.
     *
     * @return sku name.
     */
    String getName();

    /**
     * Set sku name.
     *
     * @param name sku name.
     */
    void setName(final String name);

    /**
     * Display name.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayNames();

    /**
     * Get display name
     *
     * @param names localised locale => name pairs
     */
    void setDisplayNames(Map<String, String> names);

    /**
     * Get description.
     *
     * @return sku description.
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description sku description.
     */
    void setDescription(final String description);

    /**
     * Get pk value of product
     *
     * @return pk value of product
     */
    long getProductId();

    /**
     * set pk value of product.
     *
     * @param productId product pk value.
     */
    void setProductId(final long productId);

    /**
     * Get rank
     *
     * @return rank of sku
     */
    int getRank();

    /**
     * Set rank.
     *
     * @param rank rank
     */
    void setRank(final int rank);

    /**
     * Get bar code.
     *
     * @return bar code.
     */
    String getBarCode();

    /**
     * Set bar code.
     *
     * @param barCode bar code.
     */
    void setBarCode(final String barCode);

    /**
     * Get seo uri.
     * @return uri
     */
    String getUri();

    /**
     * Set seo uri;
     * @param uri  seo uri to  use
     */
    void setUri(String uri);

    /**
     * Get title.
     * @return  title
     */

    String getTitle();

    /**
     * Set seo title
     * @param title seo title to use
     */
    void setTitle(String title);

    /**
     * Display title.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayTitles();

    /**
     * Set display title
     *
     * @param titles localised locale => name pairs
     */
    void setDisplayTitles(Map<String, String> titles);

    /**
     * Get meta key words.
     * @return meta key words
     */

    String getMetakeywords();

    /**
     * Set meta key words to use.
      * @param metakeywords      key words
     */
    void setMetakeywords(String metakeywords);

    /**
     * Display metakeywords.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayMetakeywords();

    /**
     * Set display metakeywords
     *
     * @param metakeywords localised locale => name pairs
     */
    void setDisplayMetakeywords(Map<String, String> metakeywords);

    /**
     * Get seo description
     * @return seo description.
     */
    String getMetadescription();

    /**
     * Set seo description.
     * @param metadescription description to use
     */
    void setMetadescription(String metadescription);

    /**
     * Display metadescription.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayMetadescriptions();

    /**
     * Set display metadescription
     *
     * @param metadescription localised locale => name pairs
     */
    void setDisplayMetadescriptions(Map<String, String> metadescription);

    /**
     * @return price for this sku
     */
    Collection<SkuPriceDTO> getPrice();

    /**
     * Set sku prices
     *
     * @param price pice collection
     */
    void setPrice(Collection<SkuPriceDTO> price);

    /**
     * Get attribute collection.
     *
     * @return attribute collection.
     */
    Collection<AttrValueProductSkuDTO> getAttributes();

    /**
     * Set attribute collection.
     *
     * @param attribute attr collection to set.
     */
    void setAttributes(Collection<AttrValueProductSkuDTO> attribute);


}
