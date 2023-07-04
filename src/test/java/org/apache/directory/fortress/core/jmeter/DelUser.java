/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.fortress.core.jmeter;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.directory.fortress.core.model.User;

import static org.junit.Assert.*;

/**
 * Delete user entry tests.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DelUser extends UserBase
{
    /**
     * This test case deletes a user. It also can perform a duplicate delete (for replication testing) and verifies.
     *
     * @param samplerContext Description of the Parameter
     * @return Description of the Return Value
     */
    public SampleResult runTest( JavaSamplerContext samplerContext )
    {
        int count = getKey();
        String userId  = hostname + '-' + qualifier + '-' + count;
        SampleResult sampleResult = new SampleResult();
        try
        {
            sampleResult.sampleStart();
            assertNotNull( adminMgr );
            User user = new User();
            user.setUserId( userId );
            LOG.debug( "threadid: {}, userId: {}", getThreadId(), userId );
            adminMgr.deleteUser( user );
            // This tests replication, ability to handle conflicts:
            if ( duplicate > 0 && count > duplicate && ( count % duplicate ) == 0 )
            {
                warn( "DUPLICATE DEL[" + count + "]: " + user.getUserId() );
                adminMgr.deleteUser( user );
            }
            if ( verify )
            {
                assertFalse( verify( userId, Op.DEL ) );
            }
            if( sleep > 0 )
            {
                try
                {
                    Thread.sleep( sleep );
                }
                catch (InterruptedException ie)
                {
                    Thread.currentThread().interrupt();
                }
            }
            sampleResult.setSampleCount( 1 );
            sampleResult.sampleEnd();
            sampleResult.setResponseMessage("test completed TID: " + getThreadId() + " UID: " + userId);
            sampleResult.setSuccessful(true);
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            warn( "ThreadId: " + getThreadId() + ", error running test: " + se );
            se.printStackTrace();
            sampleResult.setSuccessful( false );
        }
        return sampleResult;
    }
}
