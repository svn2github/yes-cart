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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.cache.Cacheable;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.CategoryService;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryServiceImpl extends BaseGenericServiceImpl<Category> implements CategoryService {

    private static final String CACHE_NAME = "categoryServiceImplMethodCache";

    private final GenericDAO<Category, Long> categoryDao;

    private final GenericDAO<ShopCategory, Long> shopCategoryDao;

    private final GenericDAO<Shop, Long> shopDao;

    /**
     * Construct service to manage categories
     *
     * @param categoryDao     category dao to use
     * @param shopCategoryDao shop category dao to use
     * @param shopDao         shop dao
     */
    public CategoryServiceImpl(
            final GenericDAO<Category, Long> categoryDao,
            final GenericDAO<ShopCategory, Long> shopCategoryDao,
            final GenericDAO<Shop, Long> shopDao) {
        super(categoryDao);
        this.categoryDao = categoryDao;
        this.shopCategoryDao = shopCategoryDao;
        this.shopDao = shopDao;
    }

    /**
     * Get the top level categories assigned to shop.
     *
     * @param shop given shop
     * @return ordered by rank list of assigned top level categories
     */
    @Cacheable(value = CACHE_NAME)
    public List<Category> getTopLevelCategories(final Shop shop) {
        return categoryDao.findByNamedQuery("TOPCATEGORIES.BY.SHOPID", shop.getShopId(), new Date());
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getAllByShopId(final long shopId) {
        return categoryDao.findByNamedQuery("ALL.TOPCATEGORIES.BY.SHOPID", shopId);
    }


    /**
     * {@inheritDoc}
     */
    public ShopCategory assignToShop(final long categoryId, final long shopId) {
        final ShopCategory shopCategory = shopCategoryDao.getEntityFactory().getByIface(ShopCategory.class);
        shopCategory.setCategory(categoryDao.findById(categoryId));
        shopCategory.setShop(shopDao.findById(shopId));
        return shopCategoryDao.create(shopCategory);
    }

    /**
     * {@inheritDoc}
     */
    public void unassignFromShop(final long categoryId, final long shopId) {
        ShopCategory shopCategory = shopCategoryDao.findSingleByNamedQuery(
                "SHOP.CATEGORY",
                categoryId,
                shopId);
        shopCategoryDao.delete(shopCategory);

    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public Category getRootCategory() {
        return categoryDao.findSingleByNamedQuery("ROOTCATEORY");
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public String getCategoryTemplateVariation(final Category category) {
        String variation = null;
        if (StringUtils.isBlank(category.getUitemplate())) {
            if (category.getParentId() != category.getCategoryId()) {
                Category parentCategory =
                        categoryDao.findById(category.getParentId());
                variation = getCategoryTemplateVariation(parentCategory);
            }
        } else {
            variation = category.getUitemplate();
        }
        return variation;
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public List<String> getItemsPerPage(final Category category) {
        final List<String> rez;
        if (category == null) {
            rez = Constants.DEFAULT_ITEMS_ON_PAGE;
        } else {
            final String val = getCategoryAttributeRecursive(null, category, AttributeNamesKeys.Category.CATEGORY_ITEMS_PER_PAGE, null);
            if (val == null) {
                rez = Constants.DEFAULT_ITEMS_ON_PAGE;
            } else {
                rez = Arrays.asList(val.split(","));
            }
        }
        return rez;
    }

    /**
     * Get the value of given attribute. If value not present in given category
     * failover to parent category will be used.
     *
     *
     * @param locale        locale for localisable value (or null for raw)
     * @param category      given category
     * @param attributeName attribute name
     * @param defaultValue  default value will be returned if value not found in hierarchy
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    @Cacheable(value = CACHE_NAME)
    public String getCategoryAttributeRecursive(final String locale, final Category category, final String attributeName, final String defaultValue) {
        final String value = getCategoryAttributeRecursive(locale, category, attributeName);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Get the values of given attributes. If value not present in given category
     * failover to parent category will be used.  In case if attribute value for first
     * attribute will be found, the rest values also will be collected form the same category.
     *
     *
     * @param locale           locale for localisable value (or null for raw)
     * @param incategory       given category
     * @param attributeNames set of attributes, to collect values.
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    @Cacheable(value = CACHE_NAME)
    public String[] getCategoryAttributeRecursive(final String locale, final Category incategory, final String[] attributeNames) {
        final String[] rez;
        final Category category;
        
        if (incategory == null) {
            category = getRootCategory();
        } else {
            category = incategory;
        }

        final AttrValue attrValue = category.getAttributeByCode(attributeNames[0]);
        if (attrValue == null
                ||
                StringUtils.isBlank(attrValue.getVal())) {
            if (category.getCategoryId() == category.getParentId()) {
                rez = null; //root of hierarchy
            } else {
                final Category parentCategory =
                        categoryDao.findById(category.getParentId());
                rez = getCategoryAttributeRecursive(null, parentCategory, attributeNames);
            }
        } else {
            rez = new String[attributeNames.length];
            int idx = 0;
            for (String attrName : attributeNames) {
                final AttrValue av = category.getAttributeByCode(attrName);
                if (av != null) {
                    rez[idx] = av.getVal();
                } else {
                    rez[idx] = null;

                }
                idx ++;


            }
        }

        return rez;
    }

    /**
     * Get the value of given attribute. If value not present in given category failover to parent category will be used.
     *
     * @param locale        locale for localisable value (or null for raw)
     * @param category      given category
     * @param attributeName attribute name
     * @return value of given attribute name or null if value not found in category hierarchy
     */
    private String getCategoryAttributeRecursive(final String locale, final Category category, final String attributeName) {

        if (category == null || attributeName == null) {
            return null;
        }

        final AttrValue attrValue = category.getAttributeByCode(attributeName);
        if (attrValue != null) {
            final String val;
            if (locale == null) {
                val = attrValue.getVal();
            } else {
                val = new FailoverStringI18NModel(attrValue.getDisplayVal(), attrValue.getVal()).getValue(locale);
            }
            if (!StringUtils.isBlank(val)) {
                return val;
            }
        }

        if (category.getCategoryId() == category.getParentId()) {
            return null; //root of hierarchy
        }
        final Category parentCategory =
                categoryDao.findById(category.getParentId());
        return getCategoryAttributeRecursive(locale, parentCategory, attributeName);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public String getCategoryTemplateVariation(final long categoryId) {
        return getCategoryTemplateVariation(
                categoryDao.findById(categoryId)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public int getProductQuantity(final long categoryId, final boolean includeChild) {
        int qty = 0;
        Category category = categoryDao.findById(categoryId);
        if (category != null) {
            qty = category.getProductCategory().size();
            if (includeChild) {
                List<Category> childs = getChildCategories(categoryId);
                for (Category childCategory : childs) {
                    qty += getProductQuantity(childCategory.getCategoryId(), includeChild);
                }

            }
        }
        return qty;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public List<Category> getChildCategories(final long categoryId) {
        return getChildCategoriesWithAvailability(categoryId, true);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getChildCategoriesWithAvailability(final long categoryId, final boolean withAvailability) {
        if (withAvailability) {
            return categoryDao.findByNamedQuery(
                    "CATEGORIES.BY.PARENTID",
                    categoryId,
                    new Date()
            );
        } else {
            return categoryDao.findByNamedQuery(
                    "CATEGORIES.BY.PARENTID.WITHOUT.DATE.FILTERING",
                    categoryId
            );
        }
    }


    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public Set<Category> getChildCategoriesRecursive(final long categoryId) {
        final Category thisCat = getById(categoryId);
        if (thisCat != null) {
            final Set<Category> all = new HashSet<Category>();
            all.add(thisCat);
            loadChildCategoriesRecursiveInternal(all, thisCat);
            return all;
        }
        return Collections.emptySet();
    }

    private void loadChildCategoriesRecursiveInternal(final Set<Category> result, final Category category) {
        List<Category> categories = getChildCategories(category.getCategoryId());
        result.addAll(categories);
        for (Category subCategory : categories) {
            loadChildCategoriesRecursiveInternal(result, subCategory);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public boolean isCategoryHasSubcategory(final long topCategoryId, final long subCategoryId) {
        final Category start = getById(subCategoryId);
        if (start != null) {
            if (subCategoryId == topCategoryId) {
                return true;
            } else {
                final List<Category> list = new ArrayList<Category>();
                list.add(start);
                addParent(list, topCategoryId);
                return list.get(list.size() - 1).getCategoryId() == topCategoryId;
            }
        }
        return false;
    }

    private void addParent(final List<Category> categoryChain, final long categoryIdStopAt) {
        final Category cat = categoryChain.get(categoryChain.size() - 1);
        if (cat.getParentId() != cat.getCategoryId()) {
            final Category parent = getById(cat.getParentId());
            if (parent != null) {
                categoryChain.add(parent);
                if (parent.getCategoryId() != categoryIdStopAt) {
                    addParent(categoryChain, categoryIdStopAt);
                }
            }
        }
    }

    /**
     * {@inheritDoc} Just to cache
     */
    @Cacheable(value = CACHE_NAME)
    public Category getById(final long pk) {
        return getGenericDao().findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public Set<Long> transform(final Collection<Category> categories) {
        final Set<Long> result = new LinkedHashSet<Long>(categories.size());
        for (Category category : categories) {
            result.add(category.getCategoryId());
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = CACHE_NAME)
    public Long getCategoryIdBySeoUri(final String seoUri) {
        List<Category> list = categoryDao.findByNamedQuery("CATEGORY.BY.SEO.URI", seoUri);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getCategoryId();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getByProductId(final long productId) {
        return categoryDao.findByNamedQuery(
                "CATEGORIES.BY.PRODUCTID",
                productId
        );
    }

}
