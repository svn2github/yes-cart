package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * Product type DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface ProductTypeDTO extends Unique {

    /**
     * Primary key.
     *
     * @return primary key.
     */
    long getProducttypeId();

    /**
     * Set primary key
     *
     * @param producttypeId primary key to set.
     */
    void setProducttypeId(long producttypeId);

    /**
     * Name of product type.
     *
     * @return name of product type
     */
    String getName();

    /**
     * Set product type name
     *
     * @param name name to set
     */
    void setName(String name);

    /**
     * Description of product type.
     *
     * @return description
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description to set
     */
    void setDescription(String description);


    /**
     * Get the product type template variation.
     *
     * @return template variation.
     */
    String getUitemplate();

    /**
     * Set template variation.
     *
     * @param uitemplate template variation.
     */
    void setUitemplate(String uitemplate);

    /**
     * Get search template variation.
     *
     * @return search template variation.
     */
    String getUisearchtemplate();

    /**
     * Set  search template variation.
     *
     * @param uisearchtemplate search template variation.
     */
    void setUisearchtemplate(String uisearchtemplate);

    /**
     * Is this product type service.
     *
     * @return true is this product service
     */
    boolean isService();

    /**
     * Set this product type as service. For example - Gift wrap
     *
     * @param service service flag to set.
     */
    void setService(boolean service);

    /**
     * Is this product type ensemble.
     *
     * @return true if ensemble
     */
    boolean isEnsemble();

    /**
     * Set product type to ensemble
     *
     * @param ensemble true is ensemble
     */
    void setEnsemble(boolean ensemble);

    /**
     * Is this product type can be shipped
     *
     * @return true if product shippable
     */
    boolean isShipable();


    /**
     * Set product type to shippable.
     *
     * @param shipable true if shippable
     */
    void setShipable(boolean shipable);

    /**
     * Is product digital.
     *
     * @return true if product digital.
     */
    boolean isDigital();

    /**
     * Set digital flag to product.
     *
     * @param digital flag to set
     */
    void setDigital(boolean digital);

    /**
     * Is digital product donwloadable ?
     *
     * @return true in case if digital product can be donwloaded.
     */
    boolean isDownloadable();

    /**
     * Set downloadable flag.
     *
     * @param downloadable flag to set.
     */
    void setDownloadable(boolean downloadable);


}
