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

package org.yes.cart.bulkimport.csv;

import org.yes.cart.bulkimport.model.ImportTuple;

/**
 * CSV tuple is a line of comma separated columns, with source identifier being
 * [filename:line number].
 *
 * User: denispavlov
 * Date: 12-07-31
 * Time: 8:03 AM
 */
public interface CsvImportTuple extends ImportTuple<String, String[]> {

}
