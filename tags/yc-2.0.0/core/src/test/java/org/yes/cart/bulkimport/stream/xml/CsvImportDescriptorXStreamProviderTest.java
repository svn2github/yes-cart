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

package org.yes.cart.bulkimport.stream.xml;

import org.junit.Test;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportFile;
import org.yes.cart.bulkimport.model.FieldTypeEnum;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 12-08-03
 * Time: 9:35 AM
 */
public class CsvImportDescriptorXStreamProviderTest {

    @Test
    public void testProvide() throws Exception {
        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream("src/test/resources/import/attributenames.xml");
        final ImportDescriptor desc = provider.fromXML(inputStream);

        assertNotNull(desc);
        assertEquals("org.yes.cart.domain.entity.Attribute", desc.getEntityType());
        assertEquals("src/test/resources/import", desc.getImportDirectory());
        assertNotNull(desc.getImportFileDescriptor());

        assertEquals("UTF-8", desc.getImportFileDescriptor().getFileEncoding());
        assertEquals("attributenames.csv", desc.getImportFileDescriptor().getFileNameMask());
        assertTrue(((CsvImportFile) desc.getImportFileDescriptor()).isIgnoreFirstLine());
        assertEquals(';', ((CsvImportFile) desc.getImportFileDescriptor()).getColumnDelimiter());
        assertEquals('"', ((CsvImportFile) desc.getImportFileDescriptor()).getTextQualifier());

        assertNotNull(desc.getImportColumns());
        assertEquals(11, desc.getImportColumns().size());

        final CsvImportColumn col0 = (CsvImportColumn) desc.getImportColumns().iterator().next();
        assertNotNull(col0);
        assertEquals(0, col0.getColumnIndex());
        assertEquals(FieldTypeEnum.FK_FIELD, col0.getFieldType());
        assertEquals("attributeGroup", col0.getName());
        assertNull(col0.getValueRegEx());
        assertEquals(Integer.valueOf(1), col0.getValueRegExGroup());
        assertEquals("select b from AttributeGroupEntity b where b.code = {attributeGroup}", col0.getLookupQuery());




    }

}
