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

import no.digipost.api.exceptions.MessageSenderEbmsErrorException;

public class EbmsException extends SendException {

    private final String errorCode;
    private final String errorDescription;

    public EbmsException(MessageSenderEbmsErrorException e) {
        super(createMessage(e), AntattSkyldig.fraSoapFaultCode(e.getSoapFault().getFaultCode()), e);

        errorCode = e.getError().getErrorCode();
        errorDescription = e.getError().getDescription().getValue();
    }

    private static String createMessage(MessageSenderEbmsErrorException e) {
        String message = "";
        if (e.getError() != null) {
            message += e.getError().getErrorCode();

            if (e.getError().getDescription() != null) {
                message += " - " + e.getMessage();
            }

            return message;
        }
        return "An unknown ebMS error has occured.";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
