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
package no.difi.sdp.client2.domain.exceptions;

import no.digipost.api.exceptions.MessageSenderValidationException;

import static no.difi.sdp.client2.domain.exceptions.SendException.AntattSkyldig.SERVER;


/**
 * Indikerer at server har returnert data som ikke validerer i henhold til forventning
 * Eksempler: feil org nummer, manglende ebMS Messaging header
 */
public class ValideringException extends SendException {

    public ValideringException(MessageSenderValidationException e) {
        super(e.getMessage(), SERVER, e);
    }

}
