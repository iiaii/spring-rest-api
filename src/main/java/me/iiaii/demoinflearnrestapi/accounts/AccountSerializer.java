/* Copyright (c) 2020 ZUM Internet, Inc.
 * All right reserved.
 * http://www.zum.com
 * This software is the confidential and proprietary information of ZUM
 * , Inc. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with ZUM.
 *
 * Revision History
 * Author                    Date                     Description
 * ------------------       --------------            ------------------
 *   iiaii                2020-08-11
 */
package me.iiaii.demoinflearnrestapi.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account account, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", account.getId());
        gen.writeEndObject();
    }
}