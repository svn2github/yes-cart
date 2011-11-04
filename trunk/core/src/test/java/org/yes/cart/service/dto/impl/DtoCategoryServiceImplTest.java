package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueCategoryDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.CategoryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoCategoryService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoCategoryServiceImplTest extends BaseCoreDBTestCase {

    private DtoFactory dtoFactory;
    private DtoCategoryService dtoService;
    private DtoAttributeService dtoAttrService;

    @Before
    public void setUp() throws Exception {
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoCategoryService) ctx.getBean(ServiceSpringKeys.DTO_CATEGORY_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
    }

    @Test
    public void testGetAll() throws Exception {
        List<CategoryDTO> list = dtoService.getAll();
        assertFalse(list.isEmpty());
    }

    @Test
    public void testGetAllWithAvailabilityFilter() throws Exception {
        List<CategoryDTO> list = dtoService.getAllWithAvailabilityFilter(true);
        assertFalse(list.isEmpty());
        assertFalse(isCategoryPresent(list, 141L));  //xmas category 2008
        assertFalse(isCategoryPresent(list, 142L));  //xmas category 2108
    }

    private boolean isCategoryPresent(final List<CategoryDTO> list, final long pk) {
        for (CategoryDTO dto : list) {
            if (dto.getCategoryId() == pk) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testCreate() throws Exception {
        CategoryDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getCategoryId() > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        CategoryDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getCategoryId() > 0);
        dto.setDescription("description");
        dto = dtoService.update(dto);
        assertEquals("description", dto.getDescription());
    }

    @Test
    public void testRemove() throws Exception {
        CategoryDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getCategoryId() > 0);
        long id = dto.getCategoryId();
        dtoService.remove(id);
        dto = dtoService.getById(id);
        assertNull(dto);
    }

    @Test
    public void testGetAllByShopId() throws Exception {
        List<CategoryDTO> list = dtoService.getAllByShopId(50L);
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }

    @Test
    public void testAssignToShop() throws Exception {
        CategoryDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getCategoryId() > 0);
        dtoService.assignToShop(dto.getCategoryId(), 50L);
        List<CategoryDTO> list = dtoService.getAllByShopId(50L);
        assertEquals(2, list.size());
        dtoService.unassignFromShop(dto.getCategoryId(), 50L);
        dtoService.remove(dto.getCategoryId());
    }

    @Test
    public void testUnassignFromShop() throws Exception {
        CategoryDTO dto = getDto();
        dto = dtoService.create(dto);
        assertTrue(dto.getCategoryId() > 0);
        dtoService.assignToShop(dto.getCategoryId(), 50L);
        List<CategoryDTO> list = dtoService.getAllByShopId(50L);
        assertEquals(2, list.size());
        dtoService.unassignFromShop(dto.getCategoryId(), 50L);
        list = dtoService.getAllByShopId(50L);
        assertEquals(1, list.size());
    }

    @Test
    public void testGetEntityAttributes() throws Exception {
        // Add your code here
        //<TCATEGORYATTRVALUE ATTRVALUE_ID="20" CODE="CATEGORY_ITEMS_PER_PAGE" VAL="6,12,24" CATEGORY_ID="100"/>
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
        assertEquals(3, list.size());
    }

    @Test
    public void testUpdateEntityAttributeValue() throws Exception {
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
        for (AttrValueDTO dto : list) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                dto.setVal("5,15,35"); // default value in test data is 6,12,24
                dtoService.updateEntityAttributeValue(dto);
                break;
            }
        }
        list = dtoService.getEntityAttributes(100L);
        for (AttrValueDTO dto : list) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                assertEquals("5,15,35", dto.getVal());
            }
        }
    }

    @Test
    public void testDeleteAttributeValue() throws Exception {
        final int qty = 3;
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
        assertEquals(qty, list.size());
        for (int i = 0; i < qty; i++) {
            AttrValueDTO dto = list.get(i);
            if (dto.getVal() != null) {
                dtoService.deleteAttributeValue(dto.getAttrvalueId());
            }
        }
        list = dtoService.getEntityAttributes(100L);
        assertEquals(qty, list.size());
        for (AttrValueDTO dto : list) {
            assertNull(dto.getVal());
        }
    }

    @Test
    public void testCreateEntityAttributeValue() throws Exception {
        final int qty = 3;
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(100L);
        assertEquals(3, list.size());
        for (int i = 0; i < qty; i++) {
            AttrValueDTO dto = list.get(i);
            if (dto.getVal() != null) {
                dtoService.deleteAttributeValue(dto.getAttrvalueId());
            }
        }
        list = dtoService.getEntityAttributes(100L);
        assertEquals(qty, list.size());
        for (AttrValueDTO dto : list) {
            assertNull(dto.getVal());
        }
        AttrValueCategoryDTO attrValueCategory = dtoFactory.getByIface(AttrValueCategoryDTO.class);
        attrValueCategory.setAttributeDTO(dtoAttrService.getById(1002L));//CATEGORY_ITEMS_PER_PAGE
        attrValueCategory.setVal("1,2,3");
        attrValueCategory.setCategoryId(100L);
        dtoService.createEntityAttributeValue(attrValueCategory);
        attrValueCategory = dtoFactory.getByIface(AttrValueCategoryDTO.class);
        attrValueCategory.setAttributeDTO(dtoAttrService.getById(1004L));//URI
        attrValueCategory.setVal("val2");
        attrValueCategory.setCategoryId(100L);
        dtoService.createEntityAttributeValue(attrValueCategory);
        list = dtoService.getEntityAttributes(100L);
        assertEquals(qty, list.size());
        for (AttrValueDTO dto : list) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                assertEquals("1,2,3", dto.getVal());
            } else if (dto.getAttributeDTO().getCode().equals("URI")) {
                assertEquals("val2", dto.getVal());

            }
        }
    }

    private CategoryDTO getDto() {
        CategoryDTO dto = dtoFactory.getByIface(CategoryDTO.class);
        dto.setName("testcategory");
        dto.setParentId(100L);
        return dto;
    }
}