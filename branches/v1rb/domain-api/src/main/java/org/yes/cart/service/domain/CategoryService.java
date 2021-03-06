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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.entity.ShopCategory;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CategoryService extends GenericService<Category> {

    /**
     * Get the top level categories assigned to shop.
     *
     * @param shop given shop
     * @return ordered by rank list of assigned top level categories
     */
    List<Category> getTopLevelCategories(Shop shop);

    /**
     * Get all assigned to shop categories.
     *
     * @param shopId shop id
     * @return list of assigned categories
     */
    List<Category> getAllByShopId(long shopId);


    /**
     * Assign category to shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     * @return {@link ShopCategory}
     */
    ShopCategory assignToShop(long categoryId, long shopId);

    /**
     * Unassign category from shop.
     *
     * @param categoryId category id
     * @param shopId     shop id
     */
    void unassignFromShop(long categoryId, long shopId);


    /**
     * Get the root category.
     *
     * @return root cateory.
     */
    Category getRootCategory();

    /**
     * Get the "template variation" for given category with failover to parent category.
     *
     * @param category given category
     * @return Template variation
     */
    String getCategoryTemplateVariation(Category category);

    /**
     * Get the "template variation" for given category with failover to parent category.
     *
     * @param categoryId given categoryId
     * @return Template variation
     */
    String getCategoryTemplateVariation(long categoryId);

    /**
     * Count products in given category.
     *
     * @param categoryId   given categoryId
     * @param includeChild true if need include child categories
     * @return quantity of products
     */
    int getProductQuantity(long categoryId, boolean includeChild);

    /**
     * Get the child categories without recursion, only one level down.
     *
     * @param categoryId given categoryId
     * @return list of child categories
     */
    List<Category> getChildCategories(long categoryId);

    /**
     * Get the child categories without recursion, only one level down.
     *
     * @param categoryId       given categoryId
     * @param withAvailability with availability date range filtering or not
     * @return list of child categories
     */
    List<Category> getChildCategoriesWithAvailability(long categoryId, boolean withAvailability);

    /**
     * Get the child categories with recursion.
     * Category from parameter will be included also.
     *
     * @param categoryId given categoryId
     * @return list of child categories
     */
    Set<Category> getChildCategoriesRecursive(long categoryId);

    /**
     * Transform collection of categories into collection of his IDs.
     *
     * @return list of category IDs
     */
    Set<Long> transform(Collection<Category> categories);


    /**
     * Get the items per page for particular category.
     * See CATEGORY_ITEMS_PER_PAGE settings
     * Failover to parent category if value does not exist
     *
     * @param category given category
     * @return list of items per page settings
     */
    List<String> getItemsPerPage(Category category);


    /**
     * Get the value of given attribute. If value not present in given category
     * failover to parent category will be used.
     *
     *
     * @param locale        locale for localised value (or null for raw value)
     * @param category      given category
     * @param attributeName attribute name
     * @param defaultValue  default value will be returned if value not found in hierarcht
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    String getCategoryAttributeRecursive(String locale, Category category, String attributeName, String defaultValue);

    /**
     * Get the values of given attributes. If value not present in given category
     * failover to parent category will be used.  In case if attribute value for first
     * attribute will be found, the rest values also will be collected form the same category.
     *
     *
     * @param locale         locale for localised value (or null for raw value)
     * @param category       given category
     * @param attributeNames set of attributes, to collect values.
     * @return value of given attribute name or defaultValue if value not found in category hierarchy
     */
    String[] getCategoryAttributeRecursive(final String locale, Category category, String[] attributeNames);



    /**
     * Get category by id.
     *
     * @param categoryId given category id
     * @return category
     */
    Category getById(long categoryId);

    /**
     * Get category id by given seo uri
     *
     * @param seoUri given seo uri
     * @return category id if found otherwise null
     */
    Long getCategoryIdBySeoUri(String seoUri);

    /**
     * Get all categories, that contains product with given id.
     *
     * @param productId given product id.
     * @return list of categories, that contains product.
     */
    List<Category> getByProductId(long productId);


    /**
     * Is given sub category belong to tree, that start from <code>topCategory</code>.
     * @param topCategoryId given root for category tree.
     * @param subCategoryId candidate to check.
     * @return true in case if category belong to tree, that start from <code>topCategory</code>
     */
    boolean isCategoryHasSubcategory(long topCategoryId, long subCategoryId);


}
