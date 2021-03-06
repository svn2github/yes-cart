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

package org.yes.cart.domain.message.consumer;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.message.RegistrationMessage;
import org.yes.cart.domain.message.impl.RegistrationMessageImpl;
import org.yes.cart.service.mail.MailComposer;

import java.util.HashSet;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CustomerRegistrationMessageListenerTest extends BaseCoreDBTestCase {

    @Test
    @Ignore("Need to refactor this, since this test just hangs, goes into infine loop to send mail")
    public void testOnMessage0() throws Exception {
        RegistrationMessage registrationMessage = createRegistrationMessage();
        SimpleSmtpServer server = SimpleSmtpServer.start(2525);
        new CustomerRegistrationMessageListener(
                (JavaMailSender) ctx().getBean("mailSender"),
                (MailComposer)ctx().getBean("mailComposer"),
                registrationMessage
        ).run();
        Thread.sleep(100);
        server.stop();
        assertThat(server.getReceivedEmailSize(), is(1));
        Iterator emailIter = server.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) emailIter.next();
        assertThat(email.getBody(),
                allOf(containsString("neWpaSswOrd"),
                        containsString("neWpaSswOrd"),
                        containsString("Bender"),
                        containsString("Rodrigez"),
                        containsString("Gadget universe"),
                        containsString("somegadget.com")));
        assertThat(email.getHeaderValue("From"), is("noreply@shop.com"));
        assertThat(email.getHeaderValue("Subject"), is("Password has been changed"));
    }

    private RegistrationMessage createRegistrationMessage() {
        RegistrationMessage registrationMessage = new RegistrationMessageImpl();
        registrationMessage.setFirstname("Bender");
        registrationMessage.setLastname("Rodrigez");
        registrationMessage.setEmail("bender@bar.localhost");
        registrationMessage.setPassword("neWpaSswOrd");
        registrationMessage.setShopId(10L);
        registrationMessage.setShopCode("SHOIP1");
        registrationMessage.setShopMailFrom("noreply@shop.com");
        registrationMessage.setShopName("Gadget universe");
        registrationMessage.setShopUrl(new HashSet<String>());
        registrationMessage.getShopUrl().add("www.somegadget.com");
        registrationMessage.getShopUrl().add("somegadget.com");
        registrationMessage.setPathToTemplateFolder("src/test/resources/mailtemplates/SHOIP1/");
        registrationMessage.setTemplateName("customerChangePassword");
        return registrationMessage;
    }
}
