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
package org.aion.api.server.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aion.zero.impl.blockchain.AionImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RpcMethodsTest {

    private static final String WEB3 = "web3";
    private static final String BLOCK_NUMBER = "eth_blockNumber";
    private static final String PING = "ping";
    private static final String CLIENT_VERSION = "web3_clientVersion";
    private static final String SHA3 = "web3_sha3";
    private static final String NEW_ACCOUNT = "personal_newAccount";
    private static final String INVALID_GROUP = "foo";
    private static final String INVALID_METHOD_NAME = "foo_invalid_method_name";
    private static final List<String> EMPTY = new ArrayList<>();
    private RpcMethods methods;

    @BeforeClass
    public static void setup() {
        // Initialize this instance at the start to make the tests run a little faster
        AionImpl.inst();
    }

    @After
    public void tearDown() {
        if (methods != null) {
            methods.shutdown();
        }
    }

    @Test
    public void testEmpty() {
        methods = new RpcMethods(EMPTY, EMPTY, EMPTY);

        // ping should be the only thing available
        Assert.assertNotNull(methods.get(PING));
        Assert.assertEquals(1, methods.enabledEndpoints.size());
    }

    @Test
    public void testGroupsOnly() {
        List<String> enabledGroups = Arrays.asList(WEB3);
        methods = new RpcMethods(enabledGroups, EMPTY, EMPTY);

        // Make sure these methods are not available
        Assert.assertNull(methods.get("foo"));
        Assert.assertNull(methods.get(BLOCK_NUMBER));

        // ping and web3 methods should be available
        Assert.assertNotNull(methods.get(PING));
        Assert.assertNotNull(methods.get(CLIENT_VERSION));
        Assert.assertNotNull(methods.get(SHA3));
    }

    @Test
    public void testInvalidGroup() {
        List<String> enabledGroups = Arrays.asList(INVALID_GROUP, WEB3);
        methods = new RpcMethods(enabledGroups, EMPTY, EMPTY);

        // Make sure that the other methods are still available
        Assert.assertNotNull(methods.get(CLIENT_VERSION));
        Assert.assertNotNull(methods.get(SHA3));
    }

    @Test
    public void testEnableMethods() {
        List<String> enabledMethods = Arrays.asList(BLOCK_NUMBER, CLIENT_VERSION);
        methods = new RpcMethods(EMPTY, enabledMethods, EMPTY);

        Assert.assertEquals(3, methods.enabledEndpoints.size());
        Assert.assertNotNull(methods.get(PING));
        Assert.assertNotNull(methods.get(CLIENT_VERSION));
        Assert.assertNotNull(methods.get(BLOCK_NUMBER));

        Assert.assertNull(methods.get(NEW_ACCOUNT));
    }

    @Test
    public void testDisableMethods() {
        List<String> enabledGroups = Arrays.asList(WEB3);
        List<String> disabledMethods = Arrays.asList(CLIENT_VERSION);
        methods = new RpcMethods(enabledGroups, EMPTY, disabledMethods);

        // web3_clientVersion should be disabled
        Assert.assertNull(methods.get(CLIENT_VERSION));

        // sha3 should still be around because it was added by the group
        Assert.assertNotNull(methods.get(SHA3));
    }

    @Test
    public void testDisablePing() {
        List<String> disabledMethods = Arrays.asList(PING);
        methods = new RpcMethods(EMPTY, EMPTY, disabledMethods);

        Assert.assertTrue(methods.enabledEndpoints.isEmpty());
    }

    @Test
    public void testEnableInvalidMethod() {
        List<String> enabledMethods = Arrays.asList(CLIENT_VERSION, INVALID_METHOD_NAME);
        methods = new RpcMethods(EMPTY, enabledMethods, EMPTY);

        Assert.assertNotNull(methods.get(CLIENT_VERSION));
        Assert.assertNull(methods.get(INVALID_METHOD_NAME));
    }

    @Test
    public void testDisableInvalidMethod() {
        List<String> enabledGroups = Arrays.asList(WEB3);
        List<String> disabledMethods = Arrays.asList(INVALID_METHOD_NAME, CLIENT_VERSION);
        methods = new RpcMethods(enabledGroups, EMPTY, disabledMethods);

        Assert.assertNull(methods.get(CLIENT_VERSION));
        Assert.assertNotNull(methods.get(SHA3));
    }
}
