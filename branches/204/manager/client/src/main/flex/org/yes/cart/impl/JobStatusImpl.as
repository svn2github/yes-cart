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

/**
 * User: denispavlov
 * Date: 12-07-30
 * Time: 4:11 PM
 */
package org.yes.cart.impl {

[Bindable]
[RemoteClass(alias="org.yes.cart.service.async.model.impl.JobStatusImpl")]
public class JobStatusImpl {

    public var token:String;
    public var state:String;
    public var report:String;

    public function JobStatusImpl() {
    }
}
}
