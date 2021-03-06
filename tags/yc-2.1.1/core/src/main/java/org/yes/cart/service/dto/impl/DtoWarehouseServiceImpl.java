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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.dto.SkuWarehouseDTO;
import org.yes.cart.domain.dto.WarehouseDTO;
import org.yes.cart.domain.dto.adapter.impl.EntityFactoryToBeanFactoryAdaptor;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.ShopWarehouseDTOImpl;
import org.yes.cart.domain.dto.impl.WarehouseDTOImpl;
import org.yes.cart.domain.entity.ShopWarehouse;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.GenericService;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.dto.DtoWarehouseService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoWarehouseServiceImpl
        extends AbstractDtoServiceImpl<WarehouseDTO, WarehouseDTOImpl, Warehouse>
        implements DtoWarehouseService {

    private final SkuWarehouseService skuWarehouseService;

    private final Assembler dtoSkuWarehouseAssembler;

    private final Assembler shopWarehouseAssembler;



    /**
     * Construct dto service.
     *
     * @param dtoFactory              {@link org.yes.cart.domain.dto.factory.DtoFactory}
     * @param warehouseGenericService {@link org.yes.cart.service.domain.GenericService}
     * @param adaptersRepository      value converter
     * @param skuWarehouseService     service to manage sku qty on warehouses
     */
    public DtoWarehouseServiceImpl(final GenericService<Warehouse> warehouseGenericService,
                                   final DtoFactory dtoFactory,
                                   final AdaptersRepository adaptersRepository,
                                   final SkuWarehouseService skuWarehouseService) {
        super(dtoFactory, warehouseGenericService, adaptersRepository);
        this.skuWarehouseService = skuWarehouseService;
        dtoSkuWarehouseAssembler = DTOAssembler.newAssembler(
                dtoFactory.getImplClass(SkuWarehouseDTO.class),
                SkuWarehouse.class);

        shopWarehouseAssembler = DTOAssembler.newAssembler(
                ShopWarehouseDTOImpl.class, ShopWarehouse.class);
    }

    /**
     * {@inheritDoc}
     */
    public Class<WarehouseDTO> getDtoIFace() {
        return WarehouseDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<WarehouseDTOImpl> getDtoImpl() {
        return WarehouseDTOImpl.class;
    }

    /**
     * {@inheritDoc}
     */
    public Class<Warehouse> getEntityIFace() {
        return Warehouse.class;
    }

    /**
     * {@inheritDoc}
     */
    public List<WarehouseDTO> findByShopId(final long shopId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        List<Warehouse> warehouses = ((WarehouseService) service).getByShopId(shopId);
        List<WarehouseDTO> dtos = new ArrayList<WarehouseDTO>();
        fillDTOs(warehouses, dtos);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    public void setShopWarehouseRank(final long shopWarehouseId, final int newRank) {
        ((WarehouseService) service).updateShopWarehouseRank(shopWarehouseId, newRank);
    }

    /**
     * {@inheritDoc}
     */
    public ShopWarehouseDTO assignWarehouse(
            final long warehouseId,
            final long shopId)
            throws UnmappedInterfaceException, UnableToCreateInstanceException {
        ShopWarehouse shopWarehouse = ((WarehouseService) service).assignWarehouse(warehouseId, shopId);
        ShopWarehouseDTO dto = dtoFactory.getByIface(ShopWarehouseDTO.class);
        shopWarehouseAssembler.assembleDto(dto, shopWarehouse, getAdaptersRepository(), dtoFactory);
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    public void unassignWarehouse(final long warehouseId, final long shopId) {
        ((WarehouseService) service).unassignWarehouse(warehouseId, shopId);
    }


    /**
     * Dind product skus quantity objects on given warehouse.
     *
     * @param productId   given product id
     * @param warehouseId given warehouse id.
     * @return list of founded {@link SkuWarehouseDTO}
     */
    public List<SkuWarehouseDTO> findProductSkusOnWarehouse(final long productId, final long warehouseId) {
        final List<SkuWarehouse> skuWarehouses = skuWarehouseService.getProductSkusOnWarehouse(productId, warehouseId);
        final List<SkuWarehouseDTO> result = new ArrayList<SkuWarehouseDTO>(skuWarehouses.size());
        for (SkuWarehouse sw : skuWarehouses) {
            result.add(assembleSkuWarehouseDTO(sw));
        }
        return result;
    }

    /**
     * Create given {@link SkuWarehouseDTO}
     *
     * @param skuWarehouseDTO given {@link SkuWarehouseDTO}
     * @return created SkuWarehouseDTO.
     */
    public SkuWarehouseDTO createSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) {
        SkuWarehouse skuWarehouse = skuWarehouseService.getGenericDao().getEntityFactory().getByIface(SkuWarehouse.class);
        dtoSkuWarehouseAssembler.assembleEntity(
                skuWarehouseDTO,
                skuWarehouse,
                getAdaptersRepository(),
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        skuWarehouse = skuWarehouseService.create(skuWarehouse);
        skuWarehouseService.updateOrdersAwaitingForInventory(skuWarehouseDTO.getSkuCode());
        return assembleSkuWarehouseDTO(skuWarehouse);
    }

    /**
     * Update given {@link SkuWarehouseDTO}
     *
     * @param skuWarehouseDTO given {@link SkuWarehouseDTO}
     * @return updated SkuWarehouseDTO.
     */
    public SkuWarehouseDTO updateSkuOnWarehouse(final SkuWarehouseDTO skuWarehouseDTO) {
        SkuWarehouse skuWarehouse = skuWarehouseService.findById(skuWarehouseDTO.getSkuWarehouseId());
        dtoSkuWarehouseAssembler.assembleEntity(
                skuWarehouseDTO,
                skuWarehouse,
                getAdaptersRepository(),
                new EntityFactoryToBeanFactoryAdaptor(service.getGenericDao().getEntityFactory()));
        skuWarehouse = skuWarehouseService.update(skuWarehouse);
        skuWarehouseService.updateOrdersAwaitingForInventory(skuWarehouseDTO.getSkuCode());
        return assembleSkuWarehouseDTO(skuWarehouse);

    }

    private SkuWarehouseDTO assembleSkuWarehouseDTO(final SkuWarehouse skuWarehouse) {
        SkuWarehouseDTO result = getDtoFactory().getByIface(SkuWarehouseDTO.class);
        dtoSkuWarehouseAssembler.assembleDto(
                result,
                skuWarehouse,
                getAdaptersRepository(),
                getDtoFactory());
        return result;
    }

    /**
     * Remove sku warehouse object by given pk value
     *
     * @param skuWarehouseId given pk value.
     */
    public void removeSkuOnWarehouse(final long skuWarehouseId) {
        final SkuWarehouse skuWarehouse = skuWarehouseService.findById(skuWarehouseId);
        skuWarehouse.setQuantity(BigDecimal.ZERO);
        skuWarehouse.setReserved(BigDecimal.ZERO);
        skuWarehouseService.update(skuWarehouse);
    }

    /**
     * Get the {@link SkuWarehouseService}. Test usage only.
     *
     * @return {@link org.yes.cart.service.domain.SkuWarehouseService}
     */
    public SkuWarehouseService getSkuWarehouseService() {
        return skuWarehouseService;
    }
}
