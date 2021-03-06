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

package org.yes.cart.service.mail.impl;

import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.yes.cart.domain.entity.Mail;
import org.yes.cart.domain.entity.MailPart;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.mail.MailComposer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible to compose mail from given:
 * templates (txt and html);
 * set of variables;
 * current shop context;
 * current spring context.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class MailComposerImpl implements MailComposer {

    /**
     * Default mail template folder.
     */
    private final static String DEFAULT_TEMPLATE_FOLDER = "default";


    /**
     * Default regular expression.
     */
    private final static String RE_EXPRESSION = "cid\\:([^'\"]+)[\\'|\\\"]";

    private final SystemService systemService;

    private final ShopService shopService;

    private String resourceExpression = RE_EXPRESSION;

    private Pattern resourcePattern;

    private String mailResourceDirectory;

    private final GStringTemplateEngine templateEngine;


    /**
     * Construct mail composer
     *
     * @param systemService to getByKey the path to mail templates.
     * @param shopService   shop service.
     */
    public MailComposerImpl(final SystemService systemService, final ShopService shopService) throws ClassNotFoundException {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        classLoader.loadClass(DecimalFormat.class.getName());
        this.templateEngine = new GStringTemplateEngine(classLoader);
        this.systemService = systemService;
        this.shopService = shopService;
    }


    /**
     * Get mail resource directory for given shop .
     *
     * @param shopCode given shop code.
     * @return mail resource directory.
     */
    private String getMailResourceDirectory(final String shopCode) {
        return shopService.getShopByCode(shopCode).getMailFolder();
    }


    /**
     * Set mail resource directory.
     *
     * @param mailResourceDirectory mail resource directory.
     */
    public void setMailResourceDirectory(final String mailResourceDirectory) {
        this.mailResourceDirectory = mailResourceDirectory;
    }

    /**
     * Merge model with template.
     *
     * @param view  groovy string template
     * @param model model
     * @return merged view.
     * @throws java.io.IOException    in case of inline resources can not be found
     * @throws ClassNotFoundException in case if something wrong with template engine
     */
    String merge(final String view, final Map<String, Object> model)
            throws IOException, ClassNotFoundException {
        final Writable writable = templateEngine.createTemplate(view).make(model);
        final StringWriter stringWriter = new StringWriter();
        writable.writeTo(stringWriter);
        stringWriter.close();
        return stringWriter.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void composeMessage(final MimeMessage message,
                               final String shopCode,
                               final String pathToTemplateFolder,
                               final String templateName,
                               final String from,
                               final String toEmail,
                               final String ccEmail,
                               final String bccEmail,
                               final Map<String, Object> model)
            throws MessagingException, IOException, ClassNotFoundException {


        final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);

        helper.setSentDate(new Date());

        if (ccEmail != null) {
            helper.setCc(ccEmail);
        }

        if (bccEmail != null) {
            helper.setBcc(bccEmail);
        }

        final String fullPath = pathToTemplateFolder + templateName + File.separator;


        final String textTemplate = getTemplate(fullPath, templateName + ".txt");
        final String htmlTemplate = getTemplate(fullPath, templateName + ".html");
        final String pathToResources = fullPath + "resources" + File.separator;
        final String propString = getTemplate(fullPath, templateName + ".properties");
        final Properties prop = new Properties();
        if (propString != null) {

            prop.load(new StringReader(propString));

        }
        helper.setSubject(prop.getProperty("subject") );

        if (from == null) {
            helper.setFrom(prop.getProperty("from"));
        } else {
            helper.setFrom(from);
        }

        composeMessage(helper, textTemplate, htmlTemplate, pathToResources, model);

    }


    /**
     * Fill mail message. At least one of the templates must be given.
     *
     * @param textTemplate    optional text template
     * @param htmlTemplate    optional html template
     * @param model           model
     * @param pathToResources optional path to inline resources. Used if htmlTemplate is set and has "cid" resoures.
     * @param helper          mail message helper
     * @throws MessagingException     in case if message can not be composed
     * @throws java.io.IOException    in case of inline resources can not be found
     * @throws ClassNotFoundException in case if something wrong with template engine
     */
    void composeMessage(final MimeMessageHelper helper,
                        final String textTemplate,
                        final String htmlTemplate,
                        final String pathToResources,
                        final Map<String, Object> model)
            throws MessagingException, ClassNotFoundException, IOException {

        if (textTemplate == null || htmlTemplate == null) {
            if (textTemplate != null) {
                helper.setText(merge(textTemplate, model), false);
            }

            if (htmlTemplate != null) {
                helper.setText(merge(htmlTemplate, model), true);
                inlineResources(helper, htmlTemplate, pathToResources);
            }

        } else {
            helper.setText(
                    merge(textTemplate, model),
                    merge(htmlTemplate, model)
            );
            inlineResources(helper, htmlTemplate, pathToResources);
        }

    }


    /**
     * Add inline resource to mail message.
     * Resource id will be interpreted as file name in following fashion: filename_ext.
     *
     * @param helper          MimeMessageHelper, that has mail message
     * @param htmlTemplate    html message template
     * @param pathToResources physical path to resources
     * @throws javax.mail.MessagingException in case if resource can not be inlined
     */
    void inlineResources(final MimeMessageHelper helper,
                         final String htmlTemplate,
                         final String pathToResources) throws MessagingException {

        if (StringUtils.isNotBlank(htmlTemplate)) {
            final List<String> resourcesIds = getResourcesId(htmlTemplate);
            if (!resourcesIds.isEmpty()) {
                for (String resourceId : resourcesIds) {
                    final String fileName = pathToResources + transformResourceIdToFileName(resourceId);
                    final File file = new File(fileName);
                    if (file.exists()) {
                        FileSystemResource res = new FileSystemResource(file);
                        helper.addInline(resourceId, res);
                    }
                }
            }
        }

    }

    /**
     * Transform resource id in filename_extension format to filename.extension
     *
     * @param resourceName resource id
     * @return filename
     */
    String transformResourceIdToFileName(final String resourceName) {
        return resourceName.replace('_', '.');
    }


    /**
     * Collect resource ids
     *
     * @param htmlTemplate given html template
     * @return list of resource ids in template order.
     */
    List<String> getResourcesId(final String htmlTemplate) {
        final List<String> resourceIds = new ArrayList<String>();
        final Matcher matcher = getResourcePattern().matcher(htmlTemplate);
        while (matcher.find()) {
            resourceIds.add(matcher.group(1));
        }
        return resourceIds;
    }


    /**
     * Get regular expression to collect resources to inline.
     *
     * @return regular expression
     */
    public String getResourceExpression() {
        return resourceExpression;
    }

    /**
     * Set regular expression  to collect resources to inline.
     * Also set patternt to null.
     *
     * @param resourceExpression regular expression
     */
    public void setResourceExpression(final String resourceExpression) {
        this.resourceExpression = resourceExpression;
        this.resourcePattern = null;
    }


    private Pattern getResourcePattern() {
        if (resourcePattern == null) {
            resourcePattern = Pattern.compile(getResourceExpression(), Pattern.CASE_INSENSITIVE);
        }
        return resourcePattern;
    }


    /**
     * Get template as string.
     *
     * @param pathToTemplate path to template folder
     * @param fileName       file name
     * @return template if exists
     * @throws IOException in case of io errors.
     */
    String getTemplate(final String pathToTemplate, final String fileName) throws IOException {
        final String fullFileName = pathToTemplate + fileName;
        final File file = new File(fullFileName);
        if (file.exists()) {
            return FileUtils.readFileToString(file, "UTF-8");
        }
        return null;
    }

    /**
     * Get path to template folder terminated with File.separator.
     *
     * @param shopCode     optional shop code
     * @param templateName template name
     * @return path to template folder
     */
    String getPathToTemplate(final String shopCode, final String templateName) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getMailResourceDirectory(shopCode));

        //stringBuilder.append(File.separator);
        stringBuilder.append(templateName);
        stringBuilder.append(File.separator);

        return stringBuilder.toString();

    }

    /** {@inheritDoc} */
    @Override
    public void composeMessage(final Mail mail,
                               final String shopCode,
                               final String pathToTemplateFolder,
                               final String templateName,
                               final String from,
                               final String toEmail,
                               final String ccEmail,
                               final String bccEmail,
                               final Map<String, Object> model)
            throws MessagingException, IOException, ClassNotFoundException {

        mail.setShopCode(shopCode);
        mail.setRecipients(toEmail);

        if (ccEmail != null) {
            mail.setCc(ccEmail);
        }

        if (bccEmail != null) {
            mail.setBcc(bccEmail);
        }

        final String fullPath = pathToTemplateFolder + templateName + File.separator;


        final String textTemplate = getTemplate(fullPath, templateName + ".txt");
        final String htmlTemplate = getTemplate(fullPath, templateName + ".html");
        final String pathToResources = fullPath + "resources" + File.separator;
        final String propString = getTemplate(fullPath, templateName + ".properties");
        final Properties prop = new Properties();
        if (propString != null) {

            prop.load(new StringReader(propString));

        }
        mail.setSubject(prop.getProperty("subject") );

        if (from == null) {
            mail.setFrom(prop.getProperty("from"));
        } else {
            mail.setFrom(from);
        }


        composeMessage(mail,
                textTemplate,
                htmlTemplate,
                pathToResources,
                model);


    }


    /**
     * Fill mail message. At least one of the templates must be given.
     *
     * @param textTemplate    optional text template
     * @param htmlTemplate    optional html template
     * @param model           model
     * @param pathToResources optional path to inline resources. Used if htmlTemplate is set and has "cid" resoures.
     * @param mail          mail message
     *
     * @throws MessagingException     in case if message can not be composed
     * @throws java.io.IOException    in case of inline resources can not be found
     * @throws ClassNotFoundException in case if something wrong with template engine
     */
    void composeMessage(final Mail mail,
                        final String textTemplate,
                        final String htmlTemplate,
                        final String pathToResources,
                        final Map<String, Object> model)
            throws MessagingException, ClassNotFoundException, IOException {

        if (textTemplate == null || htmlTemplate == null) {
            if (textTemplate != null) {
                mail.setTextVersion(merge(textTemplate, model));
            }
            if (htmlTemplate != null) {
                mail.setHtmlVersion(merge(htmlTemplate, model));
                inlineResources(mail, htmlTemplate, pathToResources);
            }

        } else {
            mail.setTextVersion(merge(textTemplate, model));
            mail.setHtmlVersion(merge(htmlTemplate, model));
            inlineResources(mail, htmlTemplate, pathToResources);
        }

    }


    /**
     * Add inline resource to mail message.
     * Resource id will be interpreted as file name in following fashion: filename_ext.
     *
     * @param mail          MimeMessageHelper, that has mail message
     * @param htmlTemplate    html message template
     * @param pathToResources physical path to resources
     * @throws javax.mail.MessagingException in case if resource can not be inlined
     */
    void inlineResources(final Mail mail,
                         final String htmlTemplate,
                         final String pathToResources) throws MessagingException, IOException {

        if (StringUtils.isNotBlank(htmlTemplate)) {
            final List<String> resourcesIds = getResourcesId(htmlTemplate);
            if (!resourcesIds.isEmpty()) {
                for (String resourceId : resourcesIds) {
                    final String fileName = pathToResources + transformResourceIdToFileName(resourceId);
                    final File file = new File(fileName);
                    if (file.exists()) {
                        final MailPart part = mail.addPart();
                        part.setResourceId(resourceId);
                        part.setFilename(fileName);
                        part.setData(FileUtils.readFileToByteArray(file));
                    }
                }
            }
        }

    }



    /** {@inheritDoc} */
    @Override
    public void convertMessage(final Mail mail, final MimeMessage mimeMessage)
            throws MessagingException, IOException, ClassNotFoundException {

        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(mail.getRecipients());

        helper.setSentDate(new Date());

        if (mail.getCc() != null) {
            helper.setCc(mail.getCc());
        }

        if (mail.getBcc() != null) {
            helper.setBcc(mail.getBcc());
        }

        final String textTemplate = mail.getTextVersion();
        final String htmlTemplate = mail.getHtmlVersion();

        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        if (textTemplate == null || htmlTemplate == null) {
            if (textTemplate != null) {
                helper.setText(textTemplate, false);
            }

            if (htmlTemplate != null) {
                helper.setText(htmlTemplate, true);
                inlineResources(helper, mail);
            }

        } else {
            helper.setText(
                    textTemplate,
                    htmlTemplate
            );
            inlineResources(helper, mail);
        }

    }


    /**
     * Add inline resource to mail message.
     * Resource id will be interpreted as file name in following fashion: filename_ext.
     *
     * @param helper          MimeMessageHelper, that has mail message
     * @param mail            html message template
     *
     * @throws javax.mail.MessagingException in case if resource can not be inlined
     */
    void inlineResources(final MimeMessageHelper helper,
                         final Mail mail) throws MessagingException {

        if (CollectionUtils.isNotEmpty(mail.getParts())) {
            for (final MailPart part : mail.getParts()) {
                final String fileName = part.getFilename();
                final String resourceId = part.getResourceId();
                helper.addInline(resourceId, new ByteArrayResource(part.getData()) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                });
            }
        }

    }


}
