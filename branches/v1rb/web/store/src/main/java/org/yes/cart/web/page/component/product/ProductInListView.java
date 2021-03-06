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

package org.yes.cart.web.page.component.product;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.PriceService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.shoppingcart.impl.AddSkuToCartEventCommandImpl;
import org.yes.cart.util.MoneyUtils;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.ProductAvailabilityModel;
import org.yes.cart.web.support.entity.decorator.ProductDecorator;
import org.yes.cart.web.support.service.AttributableImageService;
import org.yes.cart.web.support.service.ProductAvailabilityStrategy;
import org.yes.cart.web.util.WicketUtil;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 8/8/11
 * Time: 11:43 AM
 */
public class ProductInListView extends BaseComponent {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String PRODUCT_LINK_SKU = "productLinkSku";
    private final static String SKU_CODE_LABEL = "skuCode";
    private final static String PRODUCT_LINK_NAME = "productLinkName";
    private final static String NAME_LABEL = "name";
    private final static String DESCRIPTION_LABEL = "description";
    private final static String ADD_TO_CART_LINK = "addToCartLink";
    private final static String ADD_TO_CART_LINK_LABEL = "addToCartLinkLabel";
    private final static String PRICE_VIEW = "priceView";
    private final static String PRODUCT_LINK_IMAGE = "productLinkImage";
    private final static String PRODUCT_IMAGE = "productDefaultImage";
    // ------------------------------------- MARKUP IDs END ------------------------------------ //

    private final ProductDecorator product;

    private final Category category;


    @SpringBean(name = StorefrontServiceSpringKeys.ATTRIBUTABLE_IMAGE_SERVICE)
    private AttributableImageService attributableImageService;

    @SpringBean(name = ServiceSpringKeys.CATEGORY_SERVICE)
    private CategoryService categoryService;

    @SpringBean(name = ServiceSpringKeys.PRICE_SERVICE)
    protected PriceService priceService;

    @SpringBean(name = ServiceSpringKeys.IMAGE_SERVICE)
    protected ImageService imageService;

    @SpringBean(name = ServiceSpringKeys.PRODUCT_SERVICE)
    protected ProductService productService;

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_AVAILABILITY_STRATEGY)
    private ProductAvailabilityStrategy productAvailabilityStrategy;


    private final String [] defImgSize;


    /**
     * Construct product view, that show product in grid.
     *
     * @param id       view od
     * @param product  product model
     * @param category product in category, optional parameter
     * @param defImgSize image size in given category
     */
    public ProductInListView(final String id, final Product product, final Category category, final String [] defImgSize) {
        super(id);
        if (category == null) {
            this.category = categoryService.getRootCategory();
        } else {
            this.category = category;
        }
        this.product = getDecoratorFacade().decorate(
                product,
                WicketUtil.getHttpServletRequest().getContextPath(),
                getI18NSupport(), false);
        this.defImgSize = defImgSize;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final String selectedLocale = getLocale().getLanguage();
        final PageParameters linkToProductParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters());
        linkToProductParameters.set(WebParametersKeys.PRODUCT_ID, product.getId());


        final String width = defImgSize[0];
        final String height = defImgSize[1];

        final Class homePage = Application.get().getHomePage();

        add(
                new BookmarkablePageLink(PRODUCT_LINK_SKU, homePage, linkToProductParameters).add(
                        new Label(SKU_CODE_LABEL, product.getCode())
                )
        );
        
        add (
            new Label(DESCRIPTION_LABEL, product.getDescription(selectedLocale)).setEscapeModelStrings(false)
        );

        add(
                new BookmarkablePageLink(PRODUCT_LINK_NAME, homePage, linkToProductParameters).add(
                        new Label(NAME_LABEL, product.getName(selectedLocale))
                )
        );



        add(
                new BookmarkablePageLink(PRODUCT_LINK_IMAGE, homePage, linkToProductParameters).add(
                        new ContextImage(PRODUCT_IMAGE, product.getDefaultImage(width, height))
                                .add(new AttributeModifier(HTML_WIDTH, width))
                                .add(new AttributeModifier(HTML_HEIGHT, height))
                )
        );

        final ProductAvailabilityModel productPam = productAvailabilityStrategy.getAvailabilityModel(product);
        final ProductSku defSku = getDefault(product, productPam);

        final PageParameters addToCartParameters = WicketUtil.getFilteredRequestParameters(getPage().getPageParameters())
                .set(AddSkuToCartEventCommandImpl.CMD_KEY, defSku.getCode());

        final ProductAvailabilityModel skuPam = productAvailabilityStrategy.getAvailabilityModel(defSku);

        add(
                new BookmarkablePageLink(ADD_TO_CART_LINK, homePage, addToCartParameters)
                        .add(new Label(ADD_TO_CART_LINK_LABEL, skuPam.isInStock() || skuPam.isPerpetual() ?
                                getLocalizer().getString("add.to.cart", this) :
                                getLocalizer().getString("preorder.cart", this)))
                        .setVisible(skuPam.isAvailable())
        );

        add(
                new PriceView(PRICE_VIEW, new Model<SkuPrice>(getSkuPrice(defSku)), true, true)
        );


        super.onBeforeRender();
    }

    /*
     * Return first available sku rather than default to improve customer experience.
     */
    private ProductSku getDefault(final Product product, final ProductAvailabilityModel productPam) {
        if (productPam.isAvailable()) {
            if (product.isMultiSkuProduct()) {
                for (final ProductSku sku : product.getSku()) {
                    final ProductAvailabilityModel skuPam = productAvailabilityStrategy.getAvailabilityModel(sku);
                    if (skuPam.isAvailable()) {
                        return sku;
                    }
                }
            }
        }
        // single SKU and N/A product just use default
        return product.getDefaultSku();
    }


    /**
     * Get product or his sku price.
     * In case of multisku product the minimal regular price from multiple sku was used for single item.
     *
     * @param defaultSku default sku
     *
     * @return {@link org.yes.cart.domain.entity.SkuPrice}
     */
    private SkuPrice getSkuPrice(final ProductSku defaultSku) {
        return priceService.getMinimalRegularPrice(
                product.getSku(),
                defaultSku.getCode(),
                ApplicationDirector.getCurrentShop(),
                ApplicationDirector.getShoppingCart().getCurrencyCode(),
                BigDecimal.ONE
        );
    }

}
