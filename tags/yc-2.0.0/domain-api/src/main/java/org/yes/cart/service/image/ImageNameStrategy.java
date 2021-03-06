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

package org.yes.cart.service.image;

/**
 * Strategy to getByKey the filenames for store/retrieve images from image repository.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ImageNameStrategy {

    /**
     * Get the file name from url with particular prefix.
     * Prefix - part of path to image repository.
     *
     * @param url fiven url
     * @return image file name.
     */
    String getFileName(final String url);

    /**
     * Get the file name in image repository.
     *
     * @param fileName file name without the full path
     * @return full name with path to file.
     */
    // String getFullFileNamePath(String fileName);

    /**
     * Get the file name of resized in image repository.
     *
     * @param fileName file name without the full path
     * @param width    image width
     * @param height   image height
     * @return full name with path to file.
     */
    //String getFullFileNamePath(String fileName, String width, String height);


    /**
     * Get the file name in image repository.
     *
     * @param fileName file name without the full path
     * @param code     product or sku  code
     * @return full name with path to file.
     * @depricated
     */
    String getFullFileNamePath(String fileName, String code);

    /**
     * Get the file name of resized in image repository.
     *
     * @param fileName file name without the full path
     * @param code     product code
     * @param width    image width
     * @param height   image height
     * @return full name with path to file.
     * @depricated
     */
    String getFullFileNamePath(String fileName, String code, String width, String height);

    /**
     * Get the code (sku or product) for given url.
     *
     * @param url filename
     * @return code for given image name or null if particular strategy not support code.
     */
    String getCode(String url);


}
