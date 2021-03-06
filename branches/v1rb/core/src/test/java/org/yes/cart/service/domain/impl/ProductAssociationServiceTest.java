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

package org.yes.cart.service.domain.impl;

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductAssociation;
import org.yes.cart.service.domain.ProductAssociationService;
import org.yes.cart.service.domain.ProductService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ProductAssociationServiceTest extends BaseCoreDBTestCase {

    @Test
    public void testGetProductAssociations() {
        ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        ProductAssociationService associationService = (ProductAssociationService) ctx().getBean(ServiceSpringKeys.PRODUCT_ASSOCIATIONS_SERVICE);
        Product product = productService.getProductById(11004L);
        List<ProductAssociation> associations = associationService.getProductAssociations(product.getProductId());
        assertNotNull(associations);
        assertEquals(5, associations.size());
        associations = associationService.getProductAssociations(product.getProductId(), "up");
        assertNotNull(associations);
        assertEquals(1, associations.size());
        assertEquals(12004L, associations.get(0).getProductAssociated().getProductId());
        associations = associationService.getProductAssociations(product.getProductId(), "cross");
        assertNotNull(associations);
        assertEquals(2, associations.size());
        assertEquals(13005L, associations.get(0).getProductAssociated().getProductId());
        assertEquals(13004L, associations.get(1).getProductAssociated().getProductId());
    }
}
