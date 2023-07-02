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

import org.apache.commons.lang.StringUtils;
import org.apache.directory.fortress.core.*;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.model.User;
import org.apache.directory.fortress.core.util.Config;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.impl.TestUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertNotNull;

/**
 * Fortress jmeter base class for common utilities and parameters.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class UserBase extends AbstractJavaSamplerClient
{
    protected AccessMgr accessMgr;
    protected AdminMgr adminMgr;
    protected ReviewMgr reviewMgr;
    protected static final Logger LOG = LoggerFactory.getLogger( UserBase.class );

    // global counter
    private static AtomicInteger count = new AtomicInteger(0);
    // ldap host
    protected String hostname;
    // used to differentiate data sets
    protected String qualifier;
    // enabled will perform further checks
    protected boolean verify = false;
    // will perform an update after an add
    protected boolean update = false;
    // used for user assignment
    protected String role = null;
    // used for check access test
    protected String perm = null;
    // required for user
    protected String ou = null;
    // between tests
    protected int sleep = 0;
    // size of test set
    protected int size = 0;
    // used for replication tests
    protected int duplicate = 0;

    protected enum Op
    {
        ADD,
        DEL,
        CHECK
    }

    /**
     * Does the user exist?
     * @param userId
     * @param op
     * @return
     */
    protected boolean verify( String userId, Op op )
    {
        boolean found = false;
        try
        {
            assertNotNull( adminMgr );
            User user = new User( userId );
            User outUser = reviewMgr.readUser( user );
            if( op == Op.DEL )
            {
                warn( "Failed del check, threadId: " + getThreadId() + ", user: " + userId );
            }
            assertNotNull( outUser );
            found = true;
        }
        catch ( org.apache.directory.fortress.core.SecurityException se )
        {
            if( op == Op.ADD || op == Op.CHECK )
            {
                warn( "Failed add check, threadId: " + getThreadId() + ", error reading user: " + se );
                se.printStackTrace();
            }
        }
        return found;
    }

    /**
     * Prepare the thread, initialize variables, instantiate managers.
     *
     * @param samplerContext Description of the Parameter
     */
    public void setupTest( JavaSamplerContext samplerContext )
    {
        init( samplerContext );
        String message = "FT SETUP User TID: " + getThreadId() + ", hostname: " + hostname + ", qualifier: " + qualifier + ", verify: " + verify + ", sleep: " + sleep + ", duplicate: " + duplicate + ", role: " + role;
        info( message );
        try
        {
            accessMgr = AccessMgrFactory.createInstance( TestUtils.getContext() );
            adminMgr = AdminMgrFactory.createInstance( TestUtils.getContext() );
            reviewMgr = ReviewMgrFactory.createInstance( TestUtils.getContext() );
        }
        catch ( SecurityException se )
        {
            warn( "ThreadId: " + getThreadId() + ", error setting up test: " + se );
            se.printStackTrace();
        }
    }

    private void init( JavaSamplerContext samplerContext )
    {
        // Can override hostname via system property:
        hostname = System.getProperty( "hostname" );
        if (! StringUtils.isEmpty( hostname ))
        {
            System.setProperty( "fortress.host", hostname );
        }
        else
        {
            hostname = Config.getInstance().getProperty( GlobalIds.LDAP_HOST );
        }
        qualifier = System.getProperty( "qualifier" );
        if (StringUtils.isEmpty( qualifier ))
        {
            qualifier = samplerContext.getParameter( "qualifier" );
        }
        ou = System.getProperty( "ou" );
        if (StringUtils.isEmpty( ou ))
        {
            ou = samplerContext.getParameter( "ou" );
        }
        role = System.getProperty( "role" );
        if (StringUtils.isEmpty( role ))
        {
            role = samplerContext.getParameter( "role" );
        }
        perm = System.getProperty( "perm" );
        if (StringUtils.isEmpty( perm ))
        {
            perm = samplerContext.getParameter( "perm" );
        }
        String szVerify = System.getProperty( "verify" );
        if (StringUtils.isEmpty( szVerify ))
        {
            verify = samplerContext.getParameter( "verify" ).equalsIgnoreCase( "true" );
        }
        else
        {
            verify = szVerify.equalsIgnoreCase( "true" );
        }
        String szUpdate = System.getProperty( "update" );
        if (StringUtils.isEmpty( szUpdate ))
        {
            update = samplerContext.getParameter( "update" ).equalsIgnoreCase( "true" );
        }
        else
        {
            update = szVerify.equalsIgnoreCase( "true" );
        }
        String szSleep = System.getProperty( "sleep" );
        if (StringUtils.isEmpty( szSleep ))
        {
            szSleep = samplerContext.getParameter( "sleep" );
        }
        if (!StringUtils.isEmpty( szSleep ))
        {
            sleep = Integer.valueOf( szSleep );
        }
        String szSize = System.getProperty( "size" );
        if (StringUtils.isEmpty( szSize ))
        {
            szSize = samplerContext.getParameter( "size" );
        }
        if (!StringUtils.isEmpty( szSize ))
        {
            size = Integer.valueOf(szSize);
        }
        String szDuplicate = System.getProperty( "duplicate" );
        if (!StringUtils.isEmpty( szDuplicate ))
        {
            duplicate = Integer.valueOf(szDuplicate);
        }
    }

    protected void info(String message )
    {
        LOG.info( message );
        System.out.println( message );
    }

    protected void warn( String message )
    {
        LOG.warn( message );
        System.out.println( message );
    }

    /**
     *
     * @return
     */
    protected int getKey( )
    {
        return count.incrementAndGet();
    }

    String getThreadId()
    {
        return "" + Thread.currentThread().getId();
    }

    /**
     * Description of the Method
     *
     * @param samplerContext Description of the Parameter
     */
    public void teardownTest( JavaSamplerContext samplerContext )
    {
        String message = "FT TEARDOWN User TID: " + getThreadId();
        info( message );
        System.exit(0);
    }

    protected int getRandomNumber()
    {
        int number = (int) ((Math.random() * (size - 1)) + 1);
        return number;
    }
}
