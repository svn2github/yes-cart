package org.yes.cart.domain.entity;
// Generated 25-Sep-2009 4:08:12 PM by Hibernate Tools 3.2.2.GA


/**
 * ShopDiscountRule generated by hbm2java
 */

public interface ShopDiscountRule extends Auditable {

    /**
     */
    long getShopDiscountRuleId();

    void setShopDiscountRuleId(long shopDiscountRuleId);

    /**
     */
    String getRule();

    void setRule(String rule);

    /**
     */
    String getName();

    void setName(String name);

    /**
     */
    String getDescription();

    void setDescription(String description);

}


