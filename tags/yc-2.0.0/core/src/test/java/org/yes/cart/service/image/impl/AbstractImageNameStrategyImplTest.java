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

package org.yes.cart.service.image.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.service.image.ImageNameStrategy;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class AbstractImageNameStrategyImplTest extends BaseCoreDBTestCase {

    private ImageNameStrategy imageNameStrategy;

    @Before
    public void setUp() {
        imageNameStrategy = (ImageNameStrategy) ctx().getBean(ServiceSpringKeys.PRODUCT_IMAGE_NAME_STRATEGY);
        super.setUp();
    }

    @Test
    public void testGetFullFileNamePath() {
        assertEquals("file-name_CODE_a.jpej",
                imageNameStrategy.getFullFileNamePath("file-name_CODE_a.jpej", null));
        assertEquals("C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpej",
                imageNameStrategy.getFullFileNamePath("file-name_CODE_a.jpej", "CODE"));
        assertEquals("10x30" + File.separator + "C" + File.separator + "CODE" + File.separator + "file-name_CODE_a.jpej",
                imageNameStrategy.getFullFileNamePath("file-name_CODE_a.jpej", "CODE", "10", "30"));
    }
}
