package com.github.j3t.apache;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;


/**
 * Checks that {@link ApacheWireLogHelper} works as expected.
 */
public class ApacheWireLogHelperTest {

    @Test
    public void decodeMessageNull() {
        assertThat(ApacheWireLogHelper.decodeMessage(null), nullValue());
    }

    @Test
    public void decodeMessageEmpty() {
        assertThat(ApacheWireLogHelper.decodeMessage(""), is(""));
    }

    @Test
    public void decodeMessagePlain() {
        assertThat(ApacheWireLogHelper.decodeMessage("plain text message"), is("plain text message"));
    }

    @Test
    public void decodeMessageCarriageReturnNewLine() {
        assertThat(ApacheWireLogHelper.decodeMessage("__[\\r]__[\\n]__"), is("__\r__\n__"));
    }

    @Test
    public void decodeMessagePrintableChar() {
        assertThat(ApacheWireLogHelper.decodeMessage("__[0x64]__"), is("__d__"));
    }

    @Test
    public void decodeMessageNonPrintableChar() {
        assertThat(ApacheWireLogHelper.decodeMessage("__[0xab]__"), is("__«__"));
    }
}
