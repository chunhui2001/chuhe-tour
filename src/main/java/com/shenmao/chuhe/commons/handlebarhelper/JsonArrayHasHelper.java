package com.shenmao.chuhe.commons.handlebarhelper;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.shenmao.chuhe.exceptions.PurposeException;
import io.vertx.core.json.JsonArray;

import java.io.IOException;
import java.util.Arrays;

public class JsonArrayHasHelper implements Helper<Object> {

    @Override
    public CharSequence apply(Object array, Options options) throws IOException {

        Object item = options.params[0];

        boolean result = ((JsonArray)array).contains(item);

        Options.Buffer buffer = options.buffer();

        if (!result) {
            buffer.append(options.inverse());
        } else {
            buffer.append(options.fn());
        }

        return buffer;

    }



}