/*
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.difi.sdp.client2.util;

import no.difi.sdp.client2.ExceptionMapper;
import no.difi.sdp.client2.domain.exceptions.EbmsException;
import no.difi.sdp.client2.domain.exceptions.SendException;
import no.difi.sdp.client2.domain.exceptions.SendIOException;
import no.difi.sdp.client2.domain.exceptions.SoapFaultException;
import no.difi.sdp.client2.domain.exceptions.ValideringException;
import no.digipost.api.exceptions.MessageSenderEbmsErrorException;
import no.digipost.api.exceptions.MessageSenderIOException;
import no.digipost.api.exceptions.MessageSenderSoapFaultException;
import no.digipost.api.exceptions.MessageSenderValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Description;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.springframework.ws.soap.SoapMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

public class ExceptionMapperTest {

    static ExceptionMapper exceptionMapper;

    @BeforeAll
    public static void before_tests() {
        exceptionMapper = new ExceptionMapper();
    }

    @Test
    public void returns_send_exception_untouched() {
        Exception source = new SendException(null, null, null);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, sameInstance(source));
    }

    @Test
    public void to_ebms_exception() {
        SoapMessage soapMessage = mock(SoapMessage.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        Error error = new Error();
        error.setDescription(new Description("Value", "nb-no"));
        Exception source = new MessageSenderEbmsErrorException(soapMessage, error);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(EbmsException.class));
    }

    @Test
    public void to_send_io_exception() {
        Exception source = new MessageSenderIOException(null, null);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(SendIOException.class));
    }

    @Test
    public void to_validering_exception() {
        Exception source = new MessageSenderValidationException(null);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(ValideringException.class));
    }

    @Test
    public void to_soap_fault_exception() {
        SoapMessage soapMessage = mock(SoapMessage.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        Exception source = new MessageSenderSoapFaultException(soapMessage);

        Exception actual = exceptionMapper.mapException(source);

        assertThat(actual, instanceOf(SoapFaultException.class));
    }
}
