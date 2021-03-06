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

import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttrValueProductSkuDTO;
import org.yes.cart.domain.dto.ProductSkuDTO;
import org.yes.cart.domain.dto.SkuPriceDTO;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.remote.service.ReindexService;
import org.yes.cart.remote.service.RemoteProductSkuService;
import org.yes.cart.service.dto.DtoProductSkuService;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 15-May-2011
 * Time: 17:22:15
 */
public class RemoteProductSkuServiceImpl
        extends AbstractRemoteService<ProductSkuDTO>
        implements RemoteProductSkuService {

    private final DtoProductSkuService dtoProductSkuService;

    private final ReindexService reindexService;

    /**
     * Construct remote service.
     *
     * @param dtoProductSkuService dto service.
     * @param reindexService product reindex service
     */
    public RemoteProductSkuServiceImpl(
            final DtoProductSkuService dtoProductSkuService,
            final ReindexService reindexService) {
        super(dtoProductSkuService);
        this.dtoProductSkuService = dtoProductSkuService;
        this.reindexService = reindexService;
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AttrValueDTO> getEntityAttributes(final long entityPk)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoProductSkuService.getEntityAttributes(entityPk);
    }



    /**
     * Get all product SKUs.
     *
     * @param productId product id
     * @return list of product skus.
     */
    public List<ProductSkuDTO> getAllProductSkus(final long productId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return dtoProductSkuService.getAllProductSkus(productId);
    }

    /**
     * Update attribute value.
     *
     * @param attrValueDTO value to update
     * @return updated value
     */
    public AttrValueDTO updateEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        AttrValueProductSkuDTO rez = (AttrValueProductSkuDTO) dtoProductSkuService.updateEntityAttributeValue(attrValueDTO);
        reindexService.reindexProductSku(rez.getSkuId());
        return rez;
    }

    /**
     * Create attribute value
     *
     * @param attrValueDTO value to persist
     * @return created value
     */
    public AttrValueDTO createEntityAttributeValue(final AttrValueDTO attrValueDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        AttrValueProductSkuDTO rez = (AttrValueProductSkuDTO) dtoProductSkuService.createEntityAttributeValue(attrValueDTO);
        reindexService.reindexProductSku(rez.getSkuId());
        return rez;
    }

    /**
     * Delete attribute value by given pk value.
     *
     * @param attributeValuePk given pk value.
     */
    public long deleteAttributeValue(final long attributeValuePk) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        final long productSkuId = dtoProductSkuService.deleteAttributeValue(attributeValuePk);
        reindexService.reindexProductSku(productSkuId);
        return productSkuId;
    }


    /**
     * Create sku price.
     *
     * @param skuPriceDTO to create in database
     * @return created sku dto
     */
    public long createSkuPrice(final SkuPriceDTO skuPriceDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        setNullPrices(skuPriceDTO);
        long rez = dtoProductSkuService.createSkuPrice(skuPriceDTO);
        reindexService.reindexProductSku(skuPriceDTO.getProductSkuId());
        return rez;

    }

    /**
     * Update sku price.
     *
     * @param skuPriceDTO to create in database
     * @return updated sku price dto
     */
    public long updateSkuPrice(final SkuPriceDTO skuPriceDTO) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        setNullPrices(skuPriceDTO);
        long rez = dtoProductSkuService.updateSkuPrice(skuPriceDTO);
        reindexService.reindexProductSku(skuPriceDTO.getProductSkuId());
        return rez;
    }

    /**
     * Set minimal & sale price to null if they are 0
     *
     * @param skuPriceDTO {@link SkuPriceDTO}
     */
    private void setNullPrices(final SkuPriceDTO skuPriceDTO) {
        if (skuPriceDTO.getMinimalPrice() != null
                && BigDecimal.ZERO.floatValue() == skuPriceDTO.getMinimalPrice().floatValue()) {
            skuPriceDTO.setMinimalPrice(null);
        }
        if (skuPriceDTO.getSalePrice() != null &&
                BigDecimal.ZERO.floatValue() == skuPriceDTO.getSalePrice().floatValue()) {
            skuPriceDTO.setSalePrice(null);
        }

    }

    /** {@inheritDoc} */
    public SkuPriceDTO getSkuPrice(final long skuPriceId) {
        return dtoProductSkuService.getSkuPrice(skuPriceId);
    }

    /** {@inheritDoc} */
    public void removeSkuPrice(final long skuPriceId) {
        final SkuPriceDTO skuPriceDTO = dtoProductSkuService.getSkuPrice(skuPriceId);
        dtoProductSkuService.removeSkuPrice(skuPriceId);
        reindexService.reindexProductSku(skuPriceDTO.getProductSkuId());
    }

    /** {@inheritDoc} */
    public void removeAllPrices(long productId) {
        dtoProductSkuService.removeAllPrices(productId);
        reindexService.reindexProduct(productId);
    }

    /** {@inheritDoc} */
    public void removeAllInventory(long productId) {
        dtoProductSkuService.removeAllInventory(productId);
        reindexService.reindexProduct(productId);
    }

    /** {@inheritDoc} */
    public ProductSkuDTO create(final ProductSkuDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductSkuDTO rez = super.create(instance);
        reindexService.reindexProductSku(rez.getSkuId());
        return rez;

    }

    /** {@inheritDoc} */
    public ProductSkuDTO update(final ProductSkuDTO instance) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ProductSkuDTO rez = super.update(instance);
        reindexService.reindexProductSku(rez.getSkuId());
        return rez;
    }

    /** {@inheritDoc} */
    public void remove(final long id) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        super.remove(id);
        reindexService.reindexProductSku(id);
    }

    /**
     * {@inheritDoc}
     */
    public AttrValueDTO createAndBindAttrVal(long entityPk, String attrName, String attrValue) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        throw new UnmappedInterfaceException("Not implemented");
    }
}
