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

package org.yes.cart.ui.attributes.valuedialog {
import mx.controls.Button;

public interface ValueDialog {

    function get value():String;

    function set value(value:String):void;

    function get displayValues():Object;

    function set displayValues(value:Object):void;

    function get windowTitle():String;

    function set windowTitle(value:String):void;

    function get oldValue():String;

    function getButtonSave(): Button;

    /**
     * Product or sku code
     */
    function get code():String;

    /**
     * Product or sku code
     * @param value
     */
    function set code(value:String):void;

    /**
     * Attribute code.
     */
    function get attributeCode():String;

    /**
     * Attribute code.
     * @param value
     */
    function set attributeCode(value:String):void;

    function get attributeGroup():String;

    function set attributeGroup(value:String):void;

    function setInformation(value:String):void;


}
}