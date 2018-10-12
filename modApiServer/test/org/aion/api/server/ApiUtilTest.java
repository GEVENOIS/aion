/*
 * Copyright (c) 2017-2018 Aion foundation.
 *
 *     This file is part of the aion network project.
 *
 *     The aion network project is free software: you can redistribute it
 *     and/or modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation, either version 3 of
 *     the License, or any later version.
 *
 *     The aion network project is distributed in the hope that it will
 *     be useful, but WITHOUT ANY WARRANTY; without even the implied
 *     warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *     See the GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with the aion network project source files.
 *     If not, see <https://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Aion foundation.
 */

package org.aion.api.server;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class ApiUtilTest {

    private byte vers, retCode;

    private byte errorLength;

    private byte[] hash;
    private byte[] error;
    private byte[] result;

    public ApiUtilTest() {
        errorLength = 7;
        byte resultLength = 6;
        vers = RandomUtils.nextBytes(1)[0];
        retCode = RandomUtils.nextBytes(1)[0];
        hash = RandomUtils.nextBytes(ApiUtil.HASH_LEN);
        error = RandomUtils.nextBytes(errorLength);
        result = RandomUtils.nextBytes(resultLength);
        System.out.println("vers set to " + vers);
        System.out.println("retCode set to " + retCode);
        System.out.println("hash set to " + hash.toString());
        System.out.println("error set to " + error.toString());
        System.out.println("result set to " + result.toString());
    }

    @Test
    public void testNulls() {

        assertNull(ApiUtil.toReturnHeader(vers, retCode, null));
        assertNull(ApiUtil.toReturnHeader(vers, retCode, null, null));
        assertNull(ApiUtil.toReturnHeader(vers, retCode, null, null, null));
        assertNull(ApiUtil.combineRetMsg(null, null));
        assertNull(ApiUtil.combineRetMsg(null, (byte) 1));
        assertNull(ApiUtil.getApiMsgHash(null));
    }

    @Test
    public void testHeader() {

        byte[] header = ApiUtil.toReturnHeader(vers, retCode);
        assertEquals(vers, header[0]);
        assertEquals(retCode, header[1]);
        assertEquals(0, header[2]);
    }


    @Test
    public void testHeaderHash() {

        byte[] header = ApiUtil.toReturnHeader(vers, retCode, hash);
        assertEquals(vers, header[0]);
        assertEquals(retCode, header[1]);
        assertEquals(1, header[2]);
        assertArrayEquals(hash, Arrays.copyOfRange(header, 3, header.length));

    }

    @Test
    public void testHeaderHashError() {

        byte[] header = ApiUtil.toReturnHeader(vers, retCode, hash, error);
        assertEquals(vers, header[0]);
        assertEquals(retCode, header[1]);
        assertEquals(1, header[2]);
        assertArrayEquals(hash, Arrays.copyOfRange(header, 3, ApiUtil.HASH_LEN + 3));
        assertEquals(error.length, header[ApiUtil.HASH_LEN + 3]);
        assertArrayEquals(error, Arrays.copyOfRange(header, ApiUtil.HASH_LEN + 4, header.length));

        error = new byte[0];
        header = ApiUtil.toReturnHeader(vers, retCode, hash, error);
        assertEquals(vers, header[0]);
        assertEquals(retCode, header[1]);
        assertEquals(1, header[2]);
        assertArrayEquals(hash, Arrays.copyOfRange(header, 3, header.length - 1));
        assertEquals(0, header[header.length - 1]);

    }

    @Test
    public void testHeaderHashErrorResult() {

        byte[] header = ApiUtil.toReturnHeader(vers, retCode, hash, error, result);
        assertEquals(vers, header[0]);
        assertEquals(retCode, header[1]);
        assertEquals(1, header[2]);
        assertArrayEquals(hash, Arrays.copyOfRange(header, 3, ApiUtil.HASH_LEN + 3));
        assertEquals(error.length, header[ApiUtil.HASH_LEN + 3]);
        assertArrayEquals(error,
            Arrays.copyOfRange(header, ApiUtil.HASH_LEN + 4, ApiUtil.HASH_LEN + errorLength + 4));
        assertArrayEquals(result,
            Arrays.copyOfRange(header, ApiUtil.HASH_LEN + errorLength + 4, header.length));

        error = new byte[0];
        header = ApiUtil.toReturnHeader(vers, retCode, hash, error, result);
        assertEquals(vers, header[0]);
        assertEquals(retCode, header[1]);
        assertEquals(1, header[2]);
        assertArrayEquals(hash, Arrays.copyOfRange(header, 3, ApiUtil.HASH_LEN + 3));
        assertEquals(0, header[ApiUtil.HASH_LEN + 3]);
        assertArrayEquals(result, Arrays.copyOfRange(header, ApiUtil.HASH_LEN + 4, header.length));
    }

    @Test
    public void testReturnEvtHeader() {

        byte[] header = ApiUtil.toReturnEvtHeader(vers, result);
        assertEquals(vers, header[0]);
        assertEquals(106, header[1]);
        assertEquals(0, header[2]);
        assertArrayEquals(result, Arrays.copyOfRange(header, 3, header.length));
    }

    @Test
    public void testCombineRetMsg() {

        byte[] header = ApiUtil.combineRetMsg(hash, (byte) 5);
        assertArrayEquals(hash, Arrays.copyOfRange(header, 0, hash.length));
        assertEquals(5, header[hash.length]);

        header = ApiUtil.combineRetMsg(hash, result);
        assertArrayEquals(hash, Arrays.copyOfRange(header, 0, hash.length));
        assertArrayEquals(result, Arrays.copyOfRange(header, hash.length, header.length));
    }

}