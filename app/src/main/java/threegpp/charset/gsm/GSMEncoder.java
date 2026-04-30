/*
 * Copyright © 2017-2018 Constantin Roganov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package threegpp.charset.gsm;

import threegpp.charset.Util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class GSMEncoder extends CharsetEncoder {
    private static final float AVG_BYTES_PER_CHAR = 1.0f;
    private static final float MAX_BYTES_PER_CHAR = 1.0f;





    protected GSMEncoder(GSMCharset cs) {
        super(cs, AVG_BYTES_PER_CHAR, MAX_BYTES_PER_CHAR);
    }

    @Override
    protected CoderResult implFlush(ByteBuffer out) {

        return CoderResult.UNDERFLOW;
    }

    @Override
    protected void implReset() {
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {

        while(in.hasRemaining()) {
            if(!out.hasRemaining()) {
                return CoderResult.OVERFLOW;
            }
            out.put((byte)GSMCharset.GSM_CHARACTERS.indexOf(in.get()));
        }
        return CoderResult.UNDERFLOW;
    }



}
