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

package org.yes.cart.service.domain.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.SeoImage;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.service.image.ImageNameStrategyResolver;
import org.yes.cart.util.ShopCodeContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Image service to resize and store resized image.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ImageServiceImpl
        extends BaseGenericServiceImpl<SeoImage>
        implements ImageService {

    private final String allowedSizes;

    private final boolean cropToFit;
    private final int forceCropToFitOnSize;

    private final Color defaultBorder;
    private final Color alphaBorder = new Color(0, 0, 0, 0);

    private final ImageNameStrategyResolver imageNameStrategyResolver;

    private final GenericDAO<SeoImage, Long> seoImageDao;


    /**
     * Construct image service.
     *
     * @param seoImageDao   image seo dao
     * @param imageNameStrategyResolver the image name strategy resolver
     * @param allowedSizes  sizes allowed for resizing
     * @param borderColorR  red border density
     * @param borderColorG  green border color density
     * @param borderColorB  blue border color density
     * @param cropToFit     setting this to true will crop the image to proper ratio
     *                      prior to scaling so that scaled image fills all the space.
     *                      This is useful for those who wish to have images that fill
     *                      all space dedicated for image without having border around
     *                      the image. For those who wish images of products in the middle
     * @param forceCropToFitOnSize forcefully use cropping if scale is below given size
     *                             This option is very useful for small thumbs (<100px)
     *                             as you really cannot see much with added padding for
     *                             panoramic or tall images
     */
    public ImageServiceImpl(
            final GenericDAO<SeoImage, Long> seoImageDao,
            final ImageNameStrategyResolver imageNameStrategyResolver,
            final String allowedSizes,
            final int borderColorR,
            final int borderColorG,
            final int borderColorB,
            final boolean cropToFit, final int forceCropToFitOnSize) {

        super(seoImageDao);

        this.seoImageDao = seoImageDao;
        this.imageNameStrategyResolver = imageNameStrategyResolver;
        this.allowedSizes = allowedSizes;
        defaultBorder = new Color(borderColorR, borderColorG, borderColorB);

        this.cropToFit = cropToFit;
        this.forceCropToFitOnSize = forceCropToFitOnSize;
    }

    /**
     * {@inheritDoc}
     */
    public void resizeImage(
            final String original,
            final String resized,
            final String width,
            final String height) {
        resizeImage(original, resized, width, height, cropToFit);
    }

    /**
     * {@inheritDoc}
     */
    public void resizeImage(
            final String original,
            final String resized,
            final String width,
            final String height,
            final boolean cropToFit) {

        try {

            createFolder(resized);

            final BufferedImage originalImg = ImageIO.read(new File(original));
            final String codec = getCodecFromFilename(original);
            final boolean supportsAlpha = hasAlphaSupport(codec);

            int x = NumberUtils.toInt(width);
            int y = NumberUtils.toInt(height);
            int originalX = originalImg.getWidth();
            int originalY = originalImg.getHeight();

            boolean doCropToFit = cropToFit || x < forceCropToFitOnSize || y < forceCropToFitOnSize;

            final int imageType = supportsAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

            final Image resizedImg;
//            final BufferedImage resizedImg;
            final int padX, padY;
            if (doCropToFit) {
                // crop the original to best fit of target size
                int[] cropDims = cropImageToCenter(x, y, originalX, originalY);
                padX = 0;
                padY = 0;

                final BufferedImage croppedImg = originalImg.getSubimage(cropDims[0], cropDims[1], cropDims[2], cropDims[3]);
                resizedImg = croppedImg.getScaledInstance(x, y, Image.SCALE_SMOOTH);

//                final BufferedImage croppedImg = originalImg.getSubimage(cropDims[0], cropDims[1], cropDims[2], cropDims[3]);
//                resizedImg = new BufferedImage(y, x, imageType);
//                Graphics2D graphics = resizedImg.createGraphics();
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                graphics.drawImage(croppedImg, 0, 0, x, y, null);
            } else {
                int[] scaleDims = scaleImageToCenter(x, y, originalX, originalY);
                padX = scaleDims[0];
                padY = scaleDims[1];

                resizedImg = originalImg.getScaledInstance(scaleDims[2], scaleDims[3], Image.SCALE_SMOOTH);

//                resizedImg = new BufferedImage(scaleDims[3], scaleDims[2], imageType);
//                Graphics2D graphics = resizedImg.createGraphics();
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                graphics.drawImage(originalImg, 0, 0, scaleDims[2], scaleDims[3], null);
            }

            // base canvas
            final BufferedImage resizedImgFinal = new BufferedImage(x, y, imageType);

            // fill base color
            final Graphics2D graphics = resizedImgFinal.createGraphics();
            graphics.setPaint(supportsAlpha ? alphaBorder : defaultBorder);
            graphics.fillRect(0, 0, x, y);

            // insert scaled image
            graphics.drawImage(resizedImg, padX, padY, null);

            ImageIO.write(resizedImgFinal, codec, new File(resized));

        } catch (Exception exp) {
            ShopCodeContext.getLog(this).error("Unable to resize image " + original, exp);
        }

    }

    /**
     * Get the image codec from filename's given extension
     * <p/>
     * FIXME: This is a very primitive check - need something more robust, MIME Types maybe?
     *
     * @param filename file name
     * @return codec name
     */
    private String getCodecFromFilename(final String filename) {
        final String fileExt = filename.substring(filename.lastIndexOf('.') + 1);
        final String ext = fileExt.toLowerCase();
        if ("jpg".equals(ext)) {
            return "jpeg";
        } else if ("tif".equals(ext)) {
            return "tiff";
        }
        return ext;
    }

    /**
     * Check if image codec supports alpha channel.
     *
     * FIXME: simple check based on codec name
     *
     * @param codec code
     * @return true if alpha is supported
     */
    private boolean hasAlphaSupport(final String codec) {
        return "png".equals(codec);
    }

    int[] cropImageToCenter(final int targetX, final int targetY, final int originalX, final int originalY) {

        // calculate crop so that we can scale to fit
        BigDecimal sourceRatio = new BigDecimal(originalX).divide(new BigDecimal(originalY), 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal targetRatio = new BigDecimal(targetX).divide(new BigDecimal(targetY), 10, BigDecimal.ROUND_HALF_UP);
        final int cropWidth, cropHeight, cropX, cropY;
        if (sourceRatio.compareTo(targetRatio) < 0) { // need to crop by height
            cropWidth = originalX;
            cropHeight = new BigDecimal(originalY).divide((targetRatio.divide(sourceRatio, 10, BigDecimal.ROUND_HALF_UP)), 0, BigDecimal.ROUND_HALF_UP).intValue();
            cropX = 0;
            cropY = (originalY - cropHeight) / 2;
        } else { // need to crop by width
            cropHeight = originalY;
            cropWidth = new BigDecimal(originalX).divide((sourceRatio.divide(targetRatio, 10, BigDecimal.ROUND_HALF_UP)), 0, BigDecimal.ROUND_HALF_UP).intValue();
            cropX = (originalX - cropWidth) / 2;
            cropY = 0;
        }

        return new int[] { cropX, cropY, cropWidth, cropHeight };

    }

    int[] scaleImageToCenter(final int targetX, final int targetY, final int originalX, final int originalY) {

        final BigDecimal xScale = new BigDecimal(targetX).divide(new BigDecimal(originalX), 10, BigDecimal.ROUND_HALF_UP);
        final BigDecimal yScale = new BigDecimal(targetY).divide(new BigDecimal(originalY), 10, BigDecimal.ROUND_HALF_UP);

        final int scaleWidth, scaleHeight, padX, padY;
        if (xScale.compareTo(yScale) < 0) { // need to scale by height
            scaleWidth = targetX;
            scaleHeight = new BigDecimal(originalY).multiply(xScale).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            padX = 0;
            padY = (targetY - scaleHeight) / 2;
        } else { // need to scale by width
            scaleHeight = targetY;
            scaleWidth = new BigDecimal(originalX).multiply(yScale).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            padX = (targetX - scaleWidth) / 2;
            padY = 0;
        }

        return new int[] { padX, padY, scaleWidth, scaleHeight };

    }


    /**
     * Create folder(s) to store scaled image.
     *
     * @param fullPath full file name of scaled image
     * @return true if folder created
     */
    private boolean createFolder(final String fullPath) {
        int idx = fullPath.lastIndexOf(File.separator);
        if (idx == -1) {
            idx = fullPath.lastIndexOf('/');
            if (idx == -1) {
                idx = fullPath.lastIndexOf('\\');
            }
        }

        final String dirs = fullPath.substring(0, idx);

        File dir = new File(dirs);
        return dir.mkdirs();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSizeAllowed(final String size) {
        return allowedSizes.contains(size);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSizeAllowed(final String width, final String height) {
        return isSizeAllowed(width + "x" + height);
    }

    /**
     * Get the image name strategy.
     *
     * @return image name strategy
     */
    public ImageNameStrategy getImageNameStrategy(final String url) {
        return imageNameStrategyResolver.getImageNameStrategy(url);
    }

    /**
     * Add the given file to image repository during bulk import.
     * At this momen only product images can be imported.
     *
     * @param fullFileName full path to image file.
     * @param code         product or sku code.
     * @return true if file was added successfully
     */
    public String addImageToRepository(final String fullFileName,
                                        final String code,
                                        final String pathToRepository) throws IOException {
        File file = new File(fullFileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(StringUtils.EMPTY).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(createRepositoryUniqueName(pathInRepository));
        FileUtils.copyFile(file, destinationFile);
        return destinationFile.getName();
    }

    /** {@inheritDoc} */
    public String addImageToRepository(final String fullFileName, final String code,
                                        final byte[] imgBody, final String storagePrefix) throws IOException {
        return addImageToRepository(fullFileName, code, imgBody, storagePrefix, StringUtils.EMPTY);
    }

    /** {@inheritDoc} */
    public String addImageToRepository(final String fullFileName, final String code,
                                        final byte[] imgBody, final String storagePrefix,
                                        final String pathToRepository) throws IOException {
        File file = new File(fullFileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(storagePrefix).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(createRepositoryUniqueName(pathInRepository));
        FileUtils.writeByteArrayToFile(destinationFile, imgBody);
        return destinationFile.getName();
    }


    /**
     * Check is given file name present on disk and create new if necessary.
     * @param fileName given file name
     * @return sequential file name.
     */
    String createRepositoryUniqueName(final String fileName) {
        final File destinationFile = new File(fileName);
        if (destinationFile.exists()) {
            final String newFileName = createRollingFileName(fileName);
            return createRepositoryUniqueName(newFileName);
        }
        return fileName;
    }

    /**
     * Create new sequential file name, which will have _seqnumber before file extension.
     * Example /tmp/file.txt -> /tmp/file_1.txt
     *  /tmp/file_1.txt -> /tmp/file_2.txt
     * @param fullFileName given file name
     * @return sequential file name.
     */
    String createRollingFileName(final String fullFileName) {
        Assert.notNull(fullFileName, "file name must be not null");
        final int posExt = fullFileName.lastIndexOf('.');
        final String fileName;
        final String fileExt;
        if (posExt == -1) {
            fileName = fullFileName;
            fileExt = "";
        } else {
            fileName = fullFileName.substring(0, posExt);
            fileExt = fullFileName.substring(posExt); // including '.'
        }
        final int posSuffix = fileName.lastIndexOf('_');
        final String mainPart;
        final String suffix;
        if (posSuffix == -1) {
            mainPart = fileName;
            suffix = "";
        } else {
            mainPart = fileName.substring(0, posSuffix);
            suffix = fileName.substring(posSuffix + 1); // excluding '_'
        }
        if (NumberUtils.isDigits(suffix)) {
            return mainPart + "_" + (NumberUtils.toInt(suffix) + 1) + fileExt;
        } else if (suffix.length() > 0) {
            return mainPart + "_" + suffix + "_1" + fileExt;
        }
        return mainPart + "_1" + fileExt;
    }

    /** {@inheritDoc} */
    public byte[] imageToByteArray(final String fileName,
                                   final String code,
                                   final String storagePrefix,
                                   final String pathToRepository) throws IOException {
        final File file = new File(fileName);
        String pathInRepository = pathToRepository + getImageNameStrategy(storagePrefix).getFullFileNamePath(file.getName(), code);
        File destinationFile = new File(pathInRepository);
        return FileUtils.readFileToByteArray(destinationFile);
    }

    /** {@inheritDoc}*/
    public boolean deleteImage(final String imageFileName) {
        final SeoImage seoImage = getSeoImage(imageFileName);
        if (seoImage != null) {
            getGenericDao().delete(seoImage);
        }
        final File file = new File(imageFileName);
        return file.delete();
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "imageService-seoImage")
    public SeoImage getSeoImage(final String imageName) {
        java.util.List<SeoImage> seoImages = seoImageDao.findByCriteria(Restrictions.eq("imageName", imageName));
        if (seoImages == null || seoImages.isEmpty()) {
            return null;
        }
        return seoImages.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage"
    }, allEntries = true)
    public SeoImage update(SeoImage instance) {
        return super.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @CacheEvict(value = {
            "imageService-seoImage"
    }, allEntries = true)
    public void delete(SeoImage instance) {
        super.delete(instance);
    }
}
