package com.shenmao.chuhe.commons.handlebarhelper;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;

public class EllipsisHelper implements Helper<Object> {
    @Override
    public CharSequence apply(Object content, Options options) throws IOException {

        String _content = content + "";
        String _width = options.params[0] + "";

        return new Handlebars.SafeString(
                "<span style=\"" +
                        "display: inline-block;" +
                        "max-width: " + _width + ";" +
                        "overflow: hidden;text-overflow: " +
                        "ellipsis;white-space: nowrap;\">" +
                        _content + "</span>");
    }
}
