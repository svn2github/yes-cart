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
package org.yes.cart.domain.entity.impl;


import org.hibernate.annotations.Cascade;
import org.hibernate.search.annotations.*;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */

@Indexed(index = "luceneindex/product", interceptor = org.yes.cart.domain.interceptor.ProductEntityIndexingInterceptor.class)
@Entity
@Table(name = "TPRODUCT")
public class ProductEntity implements org.yes.cart.domain.entity.Product, java.io.Serializable {


    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    private String code;

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    private Date availablefrom;

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    private Date availableto;

    @Fields({@Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES), @Field(name = "name_sort", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)})
    private String name;

    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.DisplayNameBridge.class)
    private String displayName;

    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    private String description;

    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    private String tag;

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.BrandBridge.class)
    private Brand brand;

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductTypeValueBridge.class)

    private ProductType producttype;
    private int availability;

    @Field
    @ContainedIn
    @IndexedEmbedded(targetElement = AttrValueEntityProduct.class)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.AttributeValueBridge.class)
    private Set<AttrValueProduct> attributes = new HashSet<AttrValueProduct>(0);

    @Field
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductCategoryBridge.class)
    private Set<ProductCategory> productCategory = new HashSet<ProductCategory>(0);

    @Field
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductSkuBridge.class)
    private Collection<ProductSku> sku = new ArrayList<ProductSku>(0);

    private Set<ProductEnsebleOption> ensebleOption = new HashSet<ProductEnsebleOption>(0);
    private Set<ProductAssociation> productAssociations = new HashSet<ProductAssociation>(0);
    private Boolean featured;
    private SeoEntity seo;
    private Date createdTimestamp;
    private Date updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductEntity() {
    }



    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)

    @Column(name = "CODE", nullable = false)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AVAILABLEFROM")
    public Date getAvailablefrom() {
        return this.availablefrom;
    }

    public void setAvailablefrom(Date availablefrom) {
        this.availablefrom = availablefrom;
    }

    /**
     *      */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    /*
    */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AVAILABLETO")
    public Date getAvailableto() {
        return this.availableto;
    }

    public void setAvailableto(Date availableto) {
        this.availableto = availableto;
    }

    /**
     *      */
    @Fields({@Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES), @Field(name = "name_sort", index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)})
    /*
    */

    @Column(name = "NAME", nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *      */
    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.DisplayNameBridge.class)
    /*
    */

    @Column(name = "DISPLAYNAME", length = 4000)
    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *      */
    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    /*
    */

    @Transient
    String getLocale(final String attrCode) {
        return attrCode.substring(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX.length() );
    }

    /** {@inheritDoc} */
    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    @Column(name = "DESCRIPTION", length = 4000)
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *      */
    @Field(index = Index.YES, analyze = Analyze.YES, norms = Norms.YES, store = Store.YES)
    /*
    */

    @Column(name = "TAG")
    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     *      */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.BrandBridge.class)
    /*
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRAND_ID", nullable = false)
    public Brand getBrand() {
        return this.brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    /**
     *      */
    @Field(index = Index.YES, analyze = Analyze.NO, norms = Norms.NO, store = Store.YES)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductTypeValueBridge.class)
    /*
    */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCTTYPE_ID", nullable = false)
    public ProductType getProducttype() {
        return this.producttype;
    }

    public void setProducttype(ProductType producttype) {
        this.producttype = producttype;
    }

    @Column(name = "AVAILABILITY", nullable = false)
    public int getAvailability() {
        return this.availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    /**
     *      */
    @Field
    @ContainedIn
    @IndexedEmbedded(targetElement = AttrValueEntityProduct.class)
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.AttributeValueBridge.class)
    /*
    */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    public Set<AttrValueProduct> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Set<AttrValueProduct> attributes) {
        this.attributes = attributes;
    }

    /**
     *      */
    @Field
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductCategoryBridge.class)
    /*
    */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", updatable = false)
    public Set<ProductCategory> getProductCategory() {
        return this.productCategory;
    }

    public void setProductCategory(Set<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    /**
     *      */
    @Field
    @FieldBridge(impl = org.yes.cart.domain.entity.bridge.ProductSkuBridge.class)
    /*
    */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID", updatable = false)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    public Collection<ProductSku> getSku() {
        return this.sku;
    }

    public void setSku(Collection<ProductSku> sku) {
        this.sku = sku;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID", updatable = false)
    public Set<ProductEnsebleOption> getEnsebleOption() {
        return this.ensebleOption;
    }

    public void setEnsebleOption(Set<ProductEnsebleOption> ensebleOption) {
        this.ensebleOption = ensebleOption;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", updatable = false)
    public Set<ProductAssociation> getProductAssociations() {
        return this.productAssociations;
    }

    public void setProductAssociations(Set<ProductAssociation> productAssociations) {
        this.productAssociations = productAssociations;
    }

    @Column(name = "FEATURED", length = 1)
    public Boolean getFeatured() {
        return this.featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    @AttributeOverrides({
            @AttributeOverride(name = "uri", column = @Column(name = "URI")),
            @AttributeOverride(name = "title", column = @Column(name = "TITLE")),
            @AttributeOverride(name = "metakeywords", column = @Column(name = "METAKEYWORDS")),
            @AttributeOverride(name = "metadescription", column = @Column(name = "METADESCRIPTION"))})
    public SeoEntity getSeo() {
        return this.seo;
    }

    public void setSeo(SeoEntity seo) {
        this.seo = seo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_TIMESTAMP")
    public Date getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_TIMESTAMP")
    public Date getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    public void setUpdatedTimestamp(Date updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Column(name = "CREATED_BY", length = 64)
    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_BY", length = 64)
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "GUID", unique = true, nullable = false, length = 36)
    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    // The following is extra code specified in the hbm.xml files


    private ProductSku defaultProductSku = null;
    private long productId;

    //@GenericGenerator(name="generator", strategy="native", parameters={@Parameter(name="column", value="value"), @Parameter(name="table", value="HIBERNATE_UNIQUE_KEYS")})
    @DocumentId
    @Id
    @GeneratedValue
    /*(generator="generator")*/
    @Column(name = "PRODUCT_ID", nullable = false)
    public long getProductId() {
        return this.productId;
    }

    @Transient
    public long getId() {
        return this.productId;
    }


    public void setProductId(long productId) {
        this.productId = productId;
    }


    @Transient
    public BigDecimal getQtyOnWarehouse() {
        BigDecimal rez = BigDecimal.ZERO.setScale(2);
        for (ProductSku sku : getSku()) {
            for (SkuWarehouse swe : sku.getQuantityOnWarehouse()) {
                rez = rez.add(swe.getQuantity());
            }
        }
        return rez;
    }


    @Transient
    public ProductSku getDefaultSku() {
        if (defaultProductSku == null) {
            if (this.getSku() != null && !this.getSku().isEmpty()) {
                if (isMultiSkuProduct()) { //multisku
                    for (ProductSku productSku : this.getSku()) {
                        if (productSku.getCode().endsWith(this.getCode())) {
                            defaultProductSku = productSku;
                        }
                    }
                    if (defaultProductSku == null) { // if there is no matching one - then take the first one
                        defaultProductSku = this.getSku().iterator().next();
                    }
                } else { //single sku
                    defaultProductSku = this.getSku().iterator().next();
                }
            }
        }
        return defaultProductSku;
    }

    @Transient
    public Collection<AttrValueProduct> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueProduct> result = new ArrayList<AttrValueProduct>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueProduct attrValue : this.attributes) {
                if (attrValue.getAttribute() != null && attrValue.getAttribute().getCode() != null && attrValue.getAttribute().getCode().equals(attributeCode)) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    @Transient
    public Map<String, AttrValue> getAllAttibutesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<String, AttrValue>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttribute() != null) {
                    rez.put(attrValue.getAttribute().getCode(), attrValue);
                }
            }
        }
        return rez;
    }

    @Transient
    public AttrValueProduct getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueProduct attrValue : this.attributes) {
                if (attrValue.getAttribute() != null && attrValue.getAttribute().getCode() != null && attrValue.getAttribute().getCode().equals(attributeCode)) {
                    return attrValue;
                }
            }
        }
        return null;
    }

    @Transient
    public Collection<AttrValue> getAllAttibutes() {
        return new ArrayList<AttrValue>(attributes);
    }

    @Transient
    public ProductSku getSku(final String skuCode) {
        return getDefaultSku();
    }

    @Transient
    public boolean isMultiSkuProduct() {
        return sku.size() > 1;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + this.getProductId();
    }

    /**
     * {@inheritDoc}
     */
    public void setSeo(final Seo seo) {
        this.seo = (SeoEntity) seo;
    }


    // end of extra code specified in the hbm.xml files

}


