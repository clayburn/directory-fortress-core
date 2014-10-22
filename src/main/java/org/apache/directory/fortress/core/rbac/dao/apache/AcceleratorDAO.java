/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.fortress.core.rbac.dao.apache;


import org.apache.directory.fortress.core.GlobalErrIds;
import org.openldap.accelerator.api.addRole.RbacAddRoleRequest;
import org.openldap.accelerator.api.addRole.RbacAddRoleRequestImpl;
import org.openldap.accelerator.api.addRole.RbacAddRoleResponse;
import org.openldap.accelerator.api.checkAccess.RbacCheckAccessRequest;
import org.openldap.accelerator.api.checkAccess.RbacCheckAccessRequestImpl;
import org.openldap.accelerator.api.checkAccess.RbacCheckAccessResponse;
import org.openldap.accelerator.api.createSession.RbacCreateSessionRequest;
import org.openldap.accelerator.api.createSession.RbacCreateSessionRequestImpl;
import org.openldap.accelerator.api.createSession.RbacCreateSessionResponse;
import org.openldap.accelerator.api.deleteSession.RbacDeleteSessionRequest;
import org.openldap.accelerator.api.deleteSession.RbacDeleteSessionRequestImpl;
import org.openldap.accelerator.api.deleteSession.RbacDeleteSessionResponse;
import org.openldap.accelerator.api.dropRole.RbacDropRoleRequest;
import org.openldap.accelerator.api.dropRole.RbacDropRoleRequestImpl;
import org.openldap.accelerator.api.dropRole.RbacDropRoleResponse;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;

import org.openldap.accelerator.api.sessionRoles.RbacSessionRolesRequest;
import org.openldap.accelerator.api.sessionRoles.RbacSessionRolesRequestImpl;
import org.openldap.accelerator.api.sessionRoles.RbacSessionRolesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.directory.fortress.core.SecurityException;
import org.apache.directory.fortress.core.ldap.ApacheDsDataProvider;
import org.apache.directory.fortress.core.rbac.Permission;
import org.apache.directory.fortress.core.rbac.Session;
import org.apache.directory.fortress.core.rbac.User;
import org.apache.directory.fortress.core.rbac.UserRole;
import org.apache.directory.fortress.core.util.attr.VUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Data access class for invoking RBAC Accelerator server-side operations.  This class utilizes the openldap accelerator component for LDAPv3 extended operations.
 * This class follows the pattern of {@link org.apache.directory.fortress.core.AccessMgr} except policy decisions are session state are made/stored on server-side and not client-side.
 * Its methods are not intended to be invoked by outside clients that should instead use {@link org.apache.directory.fortress.core.rbac.AccelMgrImpl}.
 *
 * This class is thread safe.
 *
 * @author Shawn McKinney
 */
public final class AcceleratorDAO extends ApacheDsDataProvider implements org.apache.directory.fortress.core.rbac.dao.AcceleratorDAO

{
    private static final Logger LOG = LoggerFactory.getLogger( AcceleratorDAO.class.getName() );

    /**
     * Authenticate user and return sessionId inside {@link Session#sessionId}.
     * This function follows the pattern from: {@link org.apache.directory.fortress.core.AccessMgr#createSession(org.apache.directory.fortress.core.rbac.User, boolean)}
     * Success will result in rbac session state, i.e. {@link org.apache.directory.fortress.core.rbac.Session}, to be stored on server-side.
     * Result may be stored inside RBAC server-side audit record and retrieved with {@link org.apache.directory.fortress.core.AuditMgr#searchBinds(org.apache.directory.fortress.core.rbac.UserAudit)}
     *
     * It uses the {@link RbacCreateSessionRequest} and {@link RbacCreateSessionResponse} accelerator APIs.
     *
     * todo: this method does not yet, but will soon, return password policy decisions.
     *
     * @param user
     * @return session contains a valid sessionId captured from accelerator createSession method.
     *
     * @throws SecurityException rethrows {@code LdapException} with {@code GlobalErrIds.ACEL_CREATE_SESSION_ERR}.
     *
     */
    @Override
    public final Session createSession( User user ) throws SecurityException
    {
        Session session = null;
        LdapConnection ld = null;
        try
        {
            ld = getAdminConnection();
            ld.setTimeOut( 0 );
            // Create a new RBAC session
            RbacCreateSessionRequest rbacCreateSessionRequest = new RbacCreateSessionRequestImpl();
            //rbacCreateSessionRequest.setTenantId( "jts" );
            rbacCreateSessionRequest.setTenantId( user.getContextId() );
            rbacCreateSessionRequest.setUserIdentity( user.getUserId() );
            rbacCreateSessionRequest.setPassword( new String(user.getPassword()) );
            if( VUtil.isNotNullOrEmpty( user.getRoles() ))
            {
                for ( UserRole userRole : user.getRoles())
                {
                    rbacCreateSessionRequest.addRole( userRole.getName() );
                }
            }
            // Send the request
            RbacCreateSessionResponse rbacCreateSessionResponse = ( RbacCreateSessionResponse ) ld.extended(
                rbacCreateSessionRequest );
            LOG.debug( "createSession userId: " + user.getUserId() + ", sessionId: " +  rbacCreateSessionResponse.getSessionId() + ", resultCode: " +   rbacCreateSessionResponse.getLdapResult().getResultCode().getResultCode());
            session = new Session( user, rbacCreateSessionResponse.getSessionId() );
            if(rbacCreateSessionResponse.getLdapResult().getResultCode().getResultCode() == 0)
            {
                session.setAuthenticated(true);
            }
            else
            {
                session.setAuthenticated(false);
                String info = "createSession UserId [" + user.getUserId() + "] failed: " + rbacCreateSessionResponse.getLdapResult() + " , resultCode: " + rbacCreateSessionResponse.getLdapResult().getResultCode().getResultCode();
                throw new SecurityException( GlobalErrIds.USER_PW_INVLD, info );
            }
        }
        catch ( LdapException e )
        {
            String error = "createSession userId [" + user.getUserId() + "] caught LDAPException=" + " msg=" + e
                .getMessage();
            throw new SecurityException( GlobalErrIds.ACEL_CREATE_SESSION_ERR, error, e );
        }
        finally
        {
            closeAdminConnection( ld );
        }
        return session;
    }


    /**
     * Perform user rbac authorization.  This function returns a Boolean value meaning whether the subject of a given session is
     * allowed or not to perform a given operation on a given object. The function is valid if and
     * only if the session is a valid Fortress session, the object is a member of the OBJS data set,
     * and the operation is a member of the OPS data set. The session's subject has the permission
     * to perform the operation on that object if and only if that permission is assigned to (at least)
     * one of the session's active roles. This implementation will verify the roles or userId correspond
     * to the subject's active roles are registered in the object's access control list.
     * It uses the {@link RbacCheckAccessRequest} and {@link RbacCheckAccessResponse} accelerator APIs.
     *
     * @param session This object must be instantiated by calling {@link #createSession} method before passing into the method.  No variables need to be set by client after returned from createSession.
     * @param perm  must contain the object, {@link org.apache.directory.fortress.core.rbac.Permission#objName}, and operation, {@link org.apache.directory.fortress.core.rbac.Permission#opName}, of permission User is trying to access.
     * @return True if user has access, false otherwise.
     * @throws SecurityException rethrows {@code LdapException} with {@code GlobalErrIds.ACEL_CHECK_ACCESS_ERR}.
     */
    @Override
    public boolean checkAccess( Session session, Permission perm )
        throws SecurityException
    {
        boolean result = false;
        LdapConnection ld = null;
        try
        {
            ld = getAdminConnection();
            RbacCheckAccessRequest rbacCheckAccessRequest = new RbacCheckAccessRequestImpl();
            rbacCheckAccessRequest.setSessionId( session.getSessionId() );
            rbacCheckAccessRequest.setObject( perm.getObjName() );
            // objectId is optional
            if(VUtil.isNotNullOrEmpty( perm.getObjId()))
            {
                rbacCheckAccessRequest.setObjectId( perm.getObjId() );
            }
            rbacCheckAccessRequest.setOperation( perm.getOpName() );
            // Send the request
            RbacCheckAccessResponse rbacCheckAccessResponse = ( RbacCheckAccessResponse ) ld.extended(
                rbacCheckAccessRequest );
            LOG.debug( "checkAccess result: {}", rbacCheckAccessResponse.getLdapResult().getResultCode().getResultCode());
            if(rbacCheckAccessResponse.getLdapResult().getResultCode().getResultCode() == 0)
            {
                result = true;
            }
            else
            {
                result = false;
            }
        }
        catch ( LdapException e )
        {
            String error = "checkAccess perm obj [" + perm.getObjName() + "], operation [" + perm.getOpName() + "] caught LDAPException=" + " msg=" + e
                .getMessage();
            throw new SecurityException( GlobalErrIds.ACEL_CHECK_ACCESS_ERR, error, e );
        }
        finally
        {
            closeAdminConnection( ld );
        }
        return result;
    }


    /**
     * Deactivate user role from rbac session
     * This function follows the pattern from: {@link org.apache.directory.fortress.core.AccessMgr#dropActiveRole(org.apache.directory.fortress.core.rbac.Session, org.apache.directory.fortress.core.rbac.UserRole)}.
     * Success will result in rbac session state to be modified inside server-side cache.
     * It uses the {@link RbacDropRoleRequest} and {@link RbacDropRoleResponse} accelerator APIs.
     *
     * @param session contains a valid sessionId captured from accelerator createSession method.
     * @param userRole both the {@link UserRole#userId} and {@link UserRole#name} fields must be set before invoking.
     * @throws SecurityException rethrows {@code LdapException} with {@code GlobalErrIds.ACEL_DROP_ROLE_ERR}.
     */
    public void dropActiveRole( Session session, UserRole userRole ) throws SecurityException
    {
        LdapConnection ld = null;
        try
        {
            ld = getAdminConnection();
            RbacDropRoleRequest dropRoleRequest = new RbacDropRoleRequestImpl();
            dropRoleRequest.setSessionId( session.getSessionId() );
            dropRoleRequest.setRole( userRole.getName() );
            dropRoleRequest.setUserIdentity( userRole.getUserId() );
            // Send the request
            RbacDropRoleResponse rbacDropRoleResponse = ( RbacDropRoleResponse ) ld.extended(
                dropRoleRequest );
            LOG.debug( "dropActiveRole result: {}", rbacDropRoleResponse.getLdapResult().getResultCode().getResultCode());
            if(rbacDropRoleResponse.getLdapResult().getResultCode().getResultCode() != 0)
            {
                String info = "dropActiveRole Role [" + userRole.getName() + "] User ["
                    + session.getUserId() + "], not previously activated.";
                throw new SecurityException( GlobalErrIds.URLE_NOT_ACTIVE, info );
            }
        }
        catch ( LdapException e )
        {
            String error = "dropActiveRole role name [" + userRole.getName() + "] caught LDAPException=" + " msg=" + e
                .getMessage();
            throw new SecurityException( GlobalErrIds.ACEL_DROP_ROLE_ERR, error, e );
        }
        finally
        {
            closeAdminConnection( ld );
        }
    }

    /**
     * Activate user role into rbac session
     * This function follows the pattern from: {@link org.apache.directory.fortress.core.AccessMgr#addActiveRole(org.apache.directory.fortress.core.rbac.Session, org.apache.directory.fortress.core.rbac.UserRole)}.
     * Success will result in rbac session state to be modified inside server-side cache.
     * It uses the {@link RbacAddRoleRequest} and {@link RbacAddRoleResponse} accelerator APIs.
     *
     * @param session contains a valid sessionId captured from accelerator createSession method.
     * @param userRole both the {@link UserRole#userId} and {@link UserRole#name} fields must be set before invoking.
     * @throws SecurityException rethrows {@code LdapException} with {@code GlobalErrIds.ACEL_ADD_ROLE_ERR}.
     */
    public void addActiveRole( Session session, UserRole userRole ) throws SecurityException
    {
        LdapConnection ld = null;
        try
        {
            ld = getAdminConnection();
            RbacAddRoleRequest addRoleRequest = new RbacAddRoleRequestImpl();
            addRoleRequest.setSessionId( session.getSessionId() );
            addRoleRequest.setRole( userRole.getName() );
            addRoleRequest.setUserIdentity( userRole.getUserId() );
            // Send the request
            RbacAddRoleResponse rbacAddRoleResponse = ( RbacAddRoleResponse ) ld.extended(
                addRoleRequest );
            LOG.debug( "addActiveRole result: {}", rbacAddRoleResponse.getLdapResult().getResultCode().getResultCode());
            if(rbacAddRoleResponse.getLdapResult().getResultCode().getResultCode() != 0)
            {
                String info;
                int rc;
                if(rbacAddRoleResponse.getLdapResult().getResultCode().getResultCode() == 20)
                {
                    info = "addActiveRole Role [" + userRole.getName() + "] User ["
                        + session.getUserId() + "], already activated.";
                    rc = GlobalErrIds.URLE_ALREADY_ACTIVE;
                }
                else
                {
                    info = "addActiveRole Role [" + userRole.getName() + "] User ["
                        + session.getUserId() + "], not authorized for user.";
                    rc = GlobalErrIds.URLE_ACTIVATE_FAILED;
                }
                throw new SecurityException( rc, info );
            }
        }
        catch ( LdapException e )
        {
            String error = "addActiveRole role name [" + userRole.getName() + "] caught LDAPException=" + " msg=" + e
                .getMessage();
            throw new SecurityException( GlobalErrIds.ACEL_ADD_ROLE_ERR, error, e );
        }
        finally
        {
            closeAdminConnection( ld );
        }
    }

    /**
     * Delete the stored session on rbac accelerator server.
     * It uses the {@link RbacDeleteSessionRequest} and {@link RbacDeleteSessionResponse} accelerator APIs.
     *
     * @param session contains a valid sessionId captured from accelerator createSession method.
     * @throws SecurityException rethrows {@code LdapException} with {@code GlobalErrIds.ACEL_DELETE_SESSION_ERR}.
     */
    public void deleteSession( Session session ) throws SecurityException
    {
        LdapConnection ld = null;
        try
        {
            ld = getAdminConnection();
            RbacDeleteSessionRequest deleteSessionRequest = new RbacDeleteSessionRequestImpl();
            deleteSessionRequest.setSessionId( session.getSessionId() );
            deleteSessionRequest.setUserIdentity( session.getUserId() );
            // Send the request
            RbacDeleteSessionResponse deleteSessionResponse = ( RbacDeleteSessionResponse ) ld.extended(
                deleteSessionRequest );
            LOG.debug( "deleteSession result: {}", deleteSessionResponse.getLdapResult().getResultCode().getResultCode());
        }
        catch ( LdapException e )
        {
            String error = "deleteSession caught LDAPException=" + " msg=" + e
                .getMessage();
            throw new SecurityException( GlobalErrIds.ACEL_DELETE_SESSION_ERR, error, e );
        }
        finally
        {
            closeAdminConnection( ld );
        }
    }


    /**
     * SessionRoles returns a list of UserRole's activated for user on rbac server.
     * It uses the {@link RbacSessionRolesRequest} and {@link RbacSessionRolesResponse} accelerator APIs.
     *
     * todo: This method does not yet, but will soon populate temporal constraints associated with entities returned.
     *
     * @param session contains a valid sessionId captured from accelerator createSession method.
     * @return List of type UserRole.  May be null if user has no roles activated in session stored - server side.
     * @throws SecurityException rethrows {@code LdapException} with {@code GlobalErrIds.ACEL_SESSION_ROLES_ERR}.
     */
    public List<UserRole> sessionRoles( Session session ) throws SecurityException
    {
        LdapConnection ld = null;
        List<UserRole> userRoleList = null;
        try
        {
            ld = getAdminConnection();
            RbacSessionRolesRequest sessionRolesRequest = new RbacSessionRolesRequestImpl();
            sessionRolesRequest.setSessionId( session.getSessionId() );
            sessionRolesRequest.setUserIdentity( session.getUserId() );
            // Send the request
            RbacSessionRolesResponse sessionRolesResponse = ( RbacSessionRolesResponse ) ld.extended(
                sessionRolesRequest );
            LOG.debug( "sessionRoles result: {}", sessionRolesResponse.getLdapResult().getResultCode().getResultCode());
            if(VUtil.isNotNullOrEmpty( sessionRolesResponse.getRoles() ) )
            {
                userRoleList = new ArrayList<>(  );
                for( String roleNm : sessionRolesResponse.getRoles() )
                {
                    userRoleList.add( new UserRole( session.getUserId(), roleNm ) );
                    // todo: add temporal constraints here
                }
            }
        }
        catch ( LdapException e )
        {
            String error = "sessionRoles caught LDAPException=" + " msg=" + e
                .getMessage();
            throw new SecurityException( GlobalErrIds.ACEL_SESSION_ROLES_ERR, error, e );
        }
        finally
        {
            closeAdminConnection( ld );
        }
        return userRoleList;
    }
}