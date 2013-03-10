/*
 * Copyright (c) 2009-2013, JoshuaTree. All Rights Reserved.
 */

/*
 *  This class is used for testing purposes.
 */
package us.jts.fortress;

import us.jts.fortress.cfg.Config;
import us.jts.fortress.example.Example;
import us.jts.fortress.example.ExampleAdminMgr;
import us.jts.fortress.example.ExampleAdminMgrFactory;
import us.jts.fortress.rbac.MyAnnotation;
import us.jts.fortress.rbac.PolicyTestData;
import us.jts.fortress.rbac.Address;
import us.jts.fortress.rbac.Hier;
import us.jts.fortress.rbac.PermObj;
import us.jts.fortress.rbac.Relationship;
import us.jts.fortress.rbac.Role;
import us.jts.fortress.rbac.SDSet;
import us.jts.fortress.rbac.Session;
import us.jts.fortress.rbac.TestUtils;
import us.jts.fortress.rbac.Permission;
import us.jts.fortress.rbac.User;
import us.jts.fortress.rbac.UserRole;
import us.jts.fortress.util.attr.VUtil;
import org.apache.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Description of the Class
 *
 * @author Shawn McKinney
 */
class AdminMgrConsole
{
    private AdminMgr am = null;
    private static final String CLS_NM = AdminMgrConsole.class.getName();
    private static final Logger log = Logger.getLogger(CLS_NM);

    /**
     * put your documentation comment here
     */
    public AdminMgrConsole()
    {
        try
        {
            am = AdminMgrFactory.createInstance(TestUtils.getContext());
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + " constructor caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage());
        }
    }


    void addRole()
    {
        Role re = new Role();

        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter role name:");
            re.setName(ReaderUtil.readLn());
            System.out.println("Enter Role's description field");
            re.setDescription(ReaderUtil.readLn());

            Role re2 = am.addRole(re);
            System.out.println("name [" + re2.getName() + "]");
            System.out.println("internalId [" + re2.getId() + "]");
            System.out.println("name description [" + re2.getDescription() + "]");
            System.out.println("has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addRole caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void updateRole()
    {
        Role re = new Role();

        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter role name:");
            re.setName(ReaderUtil.readLn());
            System.out.println("Enter Role's description field");
            re.setDescription(ReaderUtil.readLn());

            Role re2 = am.updateRole(re);
            System.out.println("name [" + re2.getName() + "]");
            System.out.println("internalId [" + re2.getId() + "]");
            System.out.println("name description [" + re2.getDescription() + "]");
            System.out.println("has been updated");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".updateRole caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void deleteRole()
    {
        Role re = new Role();

        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter role name:");
            re.setName(ReaderUtil.readLn());
            am.deleteRole(re);
            System.out.println("name [" + re.getName() + "]");
            System.out.println("has been deleted");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".deleteRole caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     *
     */
    void addRoleInheritance()
    {
        try
        {
            Role cre = new Role();
            Role pre = new Role();
            ReaderUtil.clearScreen();
            System.out.println("Enter child role name:");
            cre.setName(ReaderUtil.readLn());
            System.out.println("Enter parent role name:");
            pre.setName(ReaderUtil.readLn());

            am.addInheritance(pre, cre);
            System.out.println("child role [" + cre.getName() + "]");
            System.out.println("parent role [" + pre.getName() + "]");
            System.out.println("inheritance relationship has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addRoleInheritance caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     *
     */
    void removeRoleInheritance()
    {
        try
        {
            Role cre = new Role();
            Role pre = new Role();
            ReaderUtil.clearScreen();
            System.out.println("Enter child role name:");
            cre.setName(ReaderUtil.readLn());
            System.out.println("Enter parent role name:");
            pre.setName(ReaderUtil.readLn());
            am.deleteInheritance(pre, cre);
            System.out.println("child role [" + cre.getName() + "]");
            System.out.println("parent role [" + pre.getName() + "]");
            System.out.println("inheritance relationship has been removed");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".removeRoleInheritance caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     *
     */
    void addRoleAscendant()
    {
        try
        {
            Role cre = new Role();
            Role pre = new Role();
            ReaderUtil.clearScreen();
            System.out.println("Enter child role name:");
            cre.setName(ReaderUtil.readLn());
            System.out.println("Enter parent role name to add to repo:");
            pre.setName(ReaderUtil.readLn());
            System.out.println("Enter parent role description:");
            pre.setDescription(ReaderUtil.readLn());

            am.addAscendant(cre, pre);
            System.out.println("child role [" + cre.getName() + "]");
            System.out.println("parent role [" + pre.getName() + "]");
            System.out.println("parent role and inheritance relationship has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addRoleAscendant caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     *
     */
    void addRoleDescendant()
    {
        try
        {
            Role cre = new Role();
            Role pre = new Role();
            ReaderUtil.clearScreen();
            System.out.println("Enter child role name to add to repo:");
            cre.setName(ReaderUtil.readLn());
            System.out.println("Enter child role description:");
            cre.setDescription(ReaderUtil.readLn());
            System.out.println("Enter parent role name:");
            pre.setName(ReaderUtil.readLn());
            am.addDescendant(pre, cre);
            System.out.println("child role [" + cre.getName() + "]");
            System.out.println("parent role [" + pre.getName() + "]");
            System.out.println("child role and inheritance relationship has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addRoleDescendant caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     * Adds a feature to the User attribute of the AdminMgrConsole object
     */
    void addUser()
    {
        User ue = new User();

        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId:");
            ue.setUserId(ReaderUtil.readLn());
            System.out.println("Enter user's common name (cn):");
            String cn = ReaderUtil.readLn();
            System.out.println("Enter user's surname (sn):");
            String sn = ReaderUtil.readLn();
            ue.setSn(sn);
            ue.setCn(cn);
            System.out.println("Enter pw");
            ue.setPassword(ReaderUtil.readLn().toCharArray());
            System.out.println("Enter User's description field");
            ue.setDescription(ReaderUtil.readLn());
            System.out.println("Enter organization unit, blank for default");
            ue.setOu(ReaderUtil.readLn());

            System.out.println("Do you want to set temporal constraints on User - Y or N");
            String choice = ReaderUtil.readLn();
            if (choice != null && choice.equalsIgnoreCase("Y"))
            {
                System.out.println("Enter beginTime (alpha HHMM):");
                ue.setBeginTime(ReaderUtil.readLn());
                System.out.println("Enter endTime (alpha HHMM):");
                ue.setEndTime(ReaderUtil.readLn());
                System.out.println("Enter beginDate (alpha YYYYMMDD):");
                ue.setBeginDate(ReaderUtil.readLn());
                System.out.println("Enter endDate (alpha YYYYMMDD):");
                ue.setEndDate(ReaderUtil.readLn());
                System.out.println("Enter beginLockDate (alpha YYYYMMDD):");
                ue.setBeginLockDate(ReaderUtil.readLn());
                System.out.println("Enter endLockDate (alpha YYYYMMDD):");
                ue.setEndLockDate(ReaderUtil.readLn());
                System.out.println("Enter dayMask (numeric 1234567):");
                ue.setDayMask(ReaderUtil.readLn());
                System.out.println("Enter timeout (integer 0 - ...):");
                String timeout = ReaderUtil.readLn();
                try
                {
                    ue.setTimeout(Integer.parseInt(timeout));
                }
                catch (java.lang.NumberFormatException nfe)
                {
                    System.out.println("number format exception=" + nfe);
                    ue.setTimeout(0);
                }
            }

            System.out.println("Enter Role name (or NULL to skip):");
            String val = ReaderUtil.readLn();
            for (int i = 0; val != null && val.length() > 0; i++)
            {
                UserRole userRole = new UserRole();
                userRole.setName(val);
                ue.setRole(userRole);
                System.out.println("Enter next name (or NULL if done entering roles):");
                val = ReaderUtil.readLn();
            }

            System.out.println("Enter prop key (or NULL to skip):");
            String key = ReaderUtil.readLn();
            for (int i = 0; key != null && key.length() > 0; i++)
            {
                System.out.println("Enter prop val:");
                val = ReaderUtil.readLn();
                ue.addProperty(key, val);
                System.out.println("Enter next prop key (or NULL if done entering properties)");
                key = ReaderUtil.readLn();
            }

            System.out.println("Enter password policy (or NULL to skip):");
            String policy = ReaderUtil.readLn();
            if(VUtil.isNotNullOrEmpty(policy))
            {
                ue.setPwPolicy(policy);
            }

            ue.setAddress(new Address());
            ue.getAddress().setAddress("123 Test Ln");
            ue.getAddress().setAddress("Suite 1");
            ue.getAddress().setAddress("c/o resident");
            ue.getAddress().setCity("TestCity");
            ue.getAddress().setCountry("US");
            ue.getAddress().setPostalCode("72113");
            ue.getAddress().setState("AR");

            ue.setPhone("111-222-3333");
            ue.setPhone("222-222-3333");
            ue.setMobile("333-222-3333");
            ue.setMobile("444-222-3333");

            User ue2 = am.addUser(ue);
            System.out.println("userId [" + ue2.getUserId() + "]");
            System.out.println("internalId [" + ue2.getInternalId() + "]");
            System.out.println("user description [" + ue2.getDescription() + "]");
            System.out.println("user common name [" + ue2.getCn() + "]");
            System.out.println("user surname [" + ue2.getSn() + "]");
            System.out.println("organizational unit [" + ue2.getOu() + "]");
            System.out.println("has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     * Description of the Method
     */
    protected void updateUser()
    {
        User ue = new User();

        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            ue.setUserId(ReaderUtil.readLn());
            System.out.println("Enter pw");
            ue.setPassword(ReaderUtil.readLn().toCharArray());

            System.out.println("Do you want to test Admin User update - Y or N");
            String choice = ReaderUtil.readLn();
            if (choice != null && choice.equalsIgnoreCase("Y"))
            {
                AccessMgr accessMgr = AccessMgrFactory.createInstance(GlobalIds.HOME);
                User admin = new User();
                System.out.println("Enter userId");
                admin.setUserId(ReaderUtil.readLn());
                System.out.println("Enter pw");
                admin.setPassword(ReaderUtil.readLn().toCharArray());
                Session session = accessMgr.createSession(admin, false);
                am.setAdmin(session);
            }

            System.out.println("Enter user's description field");
            ue.setDescription(ReaderUtil.readLn());
            //System.out.println("Enter User's common name");
            //ue.cn = ReaderUtil.readLn();
            //System.out.println("Enter User's surname");
            //ue.sn = ReaderUtil.readLn();
            System.out.println("Enter organization unit, blank for default");
            ue.setOu(ReaderUtil.readLn());

            System.out.println("Enter Role name (or NULL to skip):");
            String val = ReaderUtil.readLn();
            for (int i = 0; val != null && val.length() > 0; i++)
            {
                UserRole userRole = new UserRole();
                userRole.setName(val);
                userRole.setBeginTime("0800");
                userRole.setEndTime("1500");
                userRole.setBeginDate("20090101");
                userRole.setEndDate("21000101");
                userRole.setDayMask("1234567");
                userRole.setBeginLockDate("none");
                userRole.setEndLockDate("none");
                userRole.setTimeout(0);
                ue.setRole(userRole);
                System.out.println("Enter next name (or NULL if done entering roles):");
                val = ReaderUtil.readLn();
            }

            System.out.println("Enter prop key (or NULL to skip):");
            String key = ReaderUtil.readLn();
            for (int i = 0; key != null && key.length() > 0; i++)
            {
                System.out.println("Enter prop val:");
                val = ReaderUtil.readLn();
                ue.addProperty(key, val);
                System.out.println("Enter next prop key (or NULL if done entering properties)");
                key = ReaderUtil.readLn();
            }
            System.out.println("Enter OpenLDAP password policy name or NULL to skip");
            String plcyNm = ReaderUtil.readLn();
            if (plcyNm != null && plcyNm.length() > 0)
                ue.setPwPolicy(plcyNm);

            am.updateUser(ue);
            System.out.println("userId [" + ue.getUserId() + "]");
            System.out.println("internalId [" + ue.getInternalId() + "]");
            System.out.println("user description [" + ue.getDescription() + "]");
            //System.out.println("user common name [" + ue.cn + "]");
            //System.out.println("user surname [" + ue.sn + "]");
            System.out.println("organizational unit [" + ue.getOu() + "]");
            System.out.println("has been updated");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".updateUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     * Description of the Method
     */
    protected void deleteUser()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            User user = new User();
            user.setUserId(userId);
            System.out.println("Is Force Delete?  Y/N");
            String flag = ReaderUtil.readLn();
            if (flag.equalsIgnoreCase("Y"))
            {
                am.deleteUser(user);
                System.out.println("userId [" + userId + "]");
                System.out.println("has been deleted");
            }
            else
            {
                am.disableUser(user);
                System.out.println("userId [" + userId + "]");
                System.out.println("has been disabled but not deleted");
            }
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".deleteUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     * Description of the Method
     */
    protected void forceDeleteUser()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            User user = new User();
            user.setUserId(userId);
            am.deleteUser(user);
            System.out.println("userId [" + userId + "]");
            System.out.println("has been force deleted");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".forceDeleteUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     * A unit test for JUnit
     */
    protected void testConfig()
    {
        ReaderUtil.clearScreen();
        System.out.println("Enter config name");
        String name = ReaderUtil.readLn();
        String value = Config.getProperty(name);
        //ra.addUser(ue);
        System.out.println("AdminMgrConsole.testConfig name [" + name + "] value [" + value + "]");
        ReaderUtil.readChar();
    }


    void unlockUser()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            User user = new User();
            user.setUserId(userId);
            am.unlockUserAccount(user);
            System.out.println("userId [" + userId + "]");
            System.out.println("has been unlocked");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".unlockUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void lockUser()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            User user = new User();
            user.setUserId(userId);
            am.lockUserAccount(user);
            System.out.println("userId [" + userId + "]");
            System.out.println("has been locked");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".lockUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void assignUser()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            UserRole uRole = new UserRole();
            uRole.setUserId(userId);
            System.out.println("Enter role name");
            String roleNm = ReaderUtil.readLn();
            uRole.setName(roleNm);
            am.assignUser(uRole);
            System.out.println("userId [" + userId + "] name [" + roleNm + "]");
            System.out.println("has been assigned");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".assignUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void deassignUser()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            UserRole uRole = new UserRole();
            uRole.setUserId(userId);
            System.out.println("Enter role name");
            String roleNm = ReaderUtil.readLn();
            uRole.setName(roleNm);
            am.deassignUser(uRole);
            System.out.println("userId [" + userId + "] name [" + roleNm + "]");
            System.out.println("has been deassigned");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".deassignUser caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void grantPermission(boolean isRole)
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter perm object");
            String object = ReaderUtil.readLn();
            System.out.println("Enter perm operation");
            String operation = ReaderUtil.readLn();
            Permission pOp = new Permission(object, operation);

            String name;
            if (isRole)
            {
                System.out.println("Enter role name");
                name = ReaderUtil.readLn();
                am.grantPermission(pOp, new Role(name));

            }
            else
            {
                System.out.println("Enter userId");
                name = ReaderUtil.readLn();
                am.grantPermission(pOp, new User(name));
            }

            System.out.println("perm object [" + object + "] operation [" + operation + "] has been granted to [" + name + "]");
            System.out.println("has been granted");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".grantPermission caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void revokePermission(boolean isRole)
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter perm object");
            String object = ReaderUtil.readLn();
            System.out.println("Enter perm operation");
            String operation = ReaderUtil.readLn();
            Permission pOp = new Permission(object, operation);

            String name;
            if (isRole)
            {
                System.out.println("Enter role name");
                name = ReaderUtil.readLn();
                am.revokePermission(pOp, new Role(name));

            }
            else
            {
                System.out.println("Enter userId");
                name = ReaderUtil.readLn();
                am.revokePermission(pOp, new User(name));
            }

            System.out.println("perm object [" + object + "] operation [" + operation + "] has been granted to [" + name + "]");
            System.out.println("has been revoked.");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".revokePermission caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void changePassword()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            System.out.println("Enter old password");
            String oldPw = ReaderUtil.readLn();
            User user = new User();
            user.setUserId(userId);
            user.setPassword(oldPw.toCharArray());
            System.out.println("Enter new password");
            String newPw = ReaderUtil.readLn();
            am.changePassword(user, newPw.toCharArray());
            System.out.println("userId [" + userId + "]");
            System.out.println("password has been changed");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".changePassword caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void resetPassword()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter userId");
            String userId = ReaderUtil.readLn();
            User user = new User();
            user.setUserId(userId);
            System.out.println("Enter new password");
            String newPw = ReaderUtil.readLn();
            am.resetPassword(user, newPw.toCharArray());
            System.out.println("userId [" + userId + "]");
            System.out.println("password has been reset");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".resetPassword caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     * Description of the Method
     */
    void deletePermission()
    {
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter object name");
            String name = ReaderUtil.readLn();
            //System.out.println("Enter object id (or enter for NULL)");
            //String object = ReaderUtil.readLn();
            PermObj pObj = new PermObj();
            pObj.setObjectName(name);
            am.deletePermObj(pObj);
            System.out.println("perm object deleted: [" + name + "]");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".deletePermission caught SecurityException rc=" + e.getErrorId() + " msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void addPermObject()
    {
        PermObj pe = new PermObj();
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter perm object name:");
            pe.setObjectName(ReaderUtil.readLn());
            System.out.println("Enter Perm's description field");
            pe.setDescription(ReaderUtil.readLn());
            System.out.println("Enter organization unit, blank for default");
            pe.setOu(ReaderUtil.readLn());

            System.out.println("Enter prop key (or NULL to skip):");
            String key = ReaderUtil.readLn();
            for (int i = 0; key != null && key.length() > 0; i++)
            {
                System.out.println("Enter prop val:");
                String val = ReaderUtil.readLn();
                pe.addProperty(key, val);
                System.out.println("Enter next prop key (or NULL if done entering properties)");
                key = ReaderUtil.readLn();
            }

            pe = am.addPermObj(pe);
            System.out.println("perm object name [" + pe.getObjectName() + "]");
            System.out.println("internalId [" + pe.getInternalId() + "]");
            System.out.println("description [" + pe.getDescription() + "]");
            System.out.println("organizational unit [" + pe.getOu() + "]");


            System.out.println("has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addPermObject caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    void addPermOperation()
    {
        Permission pe = new Permission();
        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter perm object name:");
            pe.setObjectName(ReaderUtil.readLn());
            System.out.println("Enter perm object id (or NULL to skip):");
            String oid = ReaderUtil.readLn();
            if (oid != null && oid.length() > 0)
                pe.setObjectId(oid);
            System.out.println("Enter Perm operation name:");
            pe.setOpName(ReaderUtil.readLn());
            //System.out.println("Enter Perm abstract name:");
            //pe.setAbstractName(ReaderUtil.readLn());

            System.out.println("Enter role name (or NULL to skip):");
            String val = ReaderUtil.readLn();
            for (int i = 0; val != null && val.length() > 0; i++)
            {
                pe.setRole(val);
                System.out.println("Enter next role name (or NULL if done entering roles):");
                val = ReaderUtil.readLn();
            }
            System.out.println("Enter user (or NULL to skip):");
            val = ReaderUtil.readLn();
            for (int i = 0; val != null && val.length() > 0; i++)
            {
                pe.setUser(val);
                System.out.println("Enter next user (or NULL if done entering users):");
                val = ReaderUtil.readLn();
            }
            System.out.println("Enter prop key (or NULL to skip):");
            String key = ReaderUtil.readLn();
            for (int i = 0; key != null && key.length() > 0; i++)
            {
                System.out.println("Enter prop val:");
                val = ReaderUtil.readLn();
                pe.addProperty(key, val);
                System.out.println("Enter next prop key (or NULL if done entering properties)");
                key = ReaderUtil.readLn();
            }

            pe = am.addPermission(pe);
            System.out.println("perm object name [" + pe.getObjectName() + "]");
            System.out.println("perm operation name [" + pe.getOpName() + "]");
            System.out.println("perm abstract name [" + pe.getAbstractName() + "]");
            System.out.println("internalId [" + pe.getInternalId() + "]");
            if (pe.getUsers() != null && pe.getUsers().size() > 0)
            {
                int ctr = 0;
                for (String user : pe.getUsers())
                {
                    System.out.println("user[" + ctr++ + "]=" + user);
                }
            }
            if (pe.getRoles() != null && pe.getRoles().size() > 0)
            {
                int ctr = 0;
                for (String role : pe.getRoles())
                {
                    System.out.println("name[" + ctr++ + "]=" + role);
                }
            }
            if (pe.getProperties() != null && pe.getProperties().size() > 0)
            {
                int ctr = 0;
                for (Enumeration e = pe.getProperties().propertyNames(); e.hasMoreElements();)
                {
                    key = (String) e.nextElement();
                    val = pe.getProperty(key);
                    System.out.println("prop key[" + ctr + "]=" + key);
                    System.out.println("prop value[" + ctr++ + "]=" + val);
                }
            }
            System.out.println("has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addPermOperation caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     *
     */
    void addSsd()
    {
        SDSet se = new SDSet();

        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter SSD Set name:");
            se.setName(ReaderUtil.readLn());
            System.out.println("Enter SSD's description field");
            se.setDescription(ReaderUtil.readLn());
            System.out.println("Enter role member, or null to skip:");
            String role = ReaderUtil.readLn();
            for (; ;)
            {
                if (role != null && role.length() > 0)
                {
                    se.addMember(role);
                }
                else
                {
                    break;
                }
                System.out.println("Enter another role member, or null to skip:");
                role = ReaderUtil.readLn();
            }
            System.out.println("Enter SSD Set cardinality:");
            int cardinality = ReaderUtil.readInt();
            se.setCardinality(cardinality);
            SDSet se2 = am.createSsdSet(se);
            System.out.println("name [" + se2.getName() + "]");
            System.out.println("internalId [" + se2.getId() + "]");
            System.out.println("name description [" + se2.getDescription() + "]");
            System.out.println("has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addSsd caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     *
     */
    void addDsd()
    {
        SDSet se = new SDSet();

        try
        {
            ReaderUtil.clearScreen();
            System.out.println("Enter DSD Set name:");
            se.setName(ReaderUtil.readLn());
            System.out.println("Enter DSD's description field");
            se.setDescription(ReaderUtil.readLn());
            System.out.println("Enter role member, or null to skip:");
            String role = ReaderUtil.readLn();
            for (; ;)
            {
                if (role != null && role.length() > 0)
                {
                    se.addMember(role);
                }
                else
                {
                    break;
                }
                System.out.println("Enter another role member, or null to skip:");
                role = ReaderUtil.readLn();
            }
            System.out.println("Enter DSD Set cardinality:");
            int cardinality = ReaderUtil.readInt();
            se.setCardinality(cardinality);
            SDSet se2 = am.createDsdSet(se);
            System.out.println("name [" + se2.getName() + "]");
            System.out.println("internalId [" + se2.getId() + "]");
            System.out.println("name description [" + se2.getDescription() + "]");
            System.out.println("has been added");
            System.out.println("ENTER to continue");
        }
        catch (SecurityException e)
        {
            log.error(CLS_NM + ".addDsd caught SecurityException rc=" + e.getErrorId() + ", msg=" + e.getMessage(), e);
        }
        ReaderUtil.readChar();
    }


    /**
     * @param g
     */
    private static String getParents(Map vertex, SimpleDirectedGraph<String, Relationship> g, List<String> parents)
    {
        String v;
        //ReaderUtil.clearScreen();
        //System.out.println("addJGraph");
        //String vertex = "Max";
        v = (String) vertex.get("Vertex");
        if (g == null)
        {
            System.out.println("getAscendants simple directed graph is null");
            return null;
        }
        if (v == null)
        {
            System.out.println("getAscendants simple directed graph is null");
            return null;
        }
        System.out.println("getAscendants V [" + v + "]");

        Set<Relationship> edges = g.outgoingEdgesOf(v);
        for (Relationship edge : edges)
        {
            if (v == null)
                return null;
            else
            {
                System.out.println("Edge:" + edge);
                //parents.add(edge.toString());
                v = edge.toString();
                //Max : Super
                //getAscendants V <Super)>
                int indx = v.indexOf(':');
                int indx2 = v.indexOf(')');
                if (indx >= 0)
                {
                    v = v.substring(indx + 2, indx2);
                }

                //String parent =
                vertex.put("Vertex", v);
                if (!v.equalsIgnoreCase("Root"))
                    parents.add(v);

                v = getParents(vertex, g, parents);
            }
        }
        return v;
    }


    /**
     * @param graph
     * @return
     * @throws us.jts.fortress.SecurityException
     *
     */
    public static Hier toHierTest(UndirectedGraph<String, Relationship> graph)
    {
        Hier he = new Hier();
        Set<Relationship> eSet = graph.edgeSet();
        for (Relationship edge : eSet)
        {
            //Edge:(User : Root)
            he.setRelationship(edge);
        }

        Set<String> vSet = graph.vertexSet();
        for (String vertice : vSet)
        {
            //he.addRole(vertice);

        }
        return he;
    }


    /**
     * @param hier
     * @return
     * @throws us.jts.fortress.SecurityException
     *
     */
    public static SimpleDirectedGraph<String, Relationship> toGraphNotUsed(Hier hier)
    {
        log.info(CLS_NM + ".toGraphX");
        SimpleDirectedGraph<String, Relationship> graph =
            new SimpleDirectedGraph<>(Relationship.class);
        //List<String> roles = hier.getRoles();
        //if (roles != null)
        //{
        //    for (String role : roles)
        //    {
        //        graph.addVertex(role);
        //    }
        //}
        List<Relationship> edges = hier.getRelationships();
        if (edges != null && edges.size() > 0)
        {
            for (Relationship edge : edges)
            {

                String child = edge.getChild();
                String parent = edge.getParent();
                graph.addVertex(child);
                graph.addVertex(parent);
                graph.addEdge(child, parent, edge);
                if (log.isDebugEnabled())
                    log.debug(CLS_NM + ".toGraphX child=" + child + " parent=" + parent);
            }
        }
        return graph;
    }

    public static void addJGraph2()
    {
        ReaderUtil.clearScreen();
        System.out.println("addJGraph");
        UndirectedGraph<String, DefaultEdge> g =
            new SimpleGraph<>(DefaultEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        // add edges to create a circuit
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v4);
        g.addEdge(v4, v1);

        System.out.println("graph=" + g.toString());
        System.out.println("ENTER to continue");
        ReaderUtil.readChar();
    }

    private static String getTestDataLabel2()
    {
        System.out.println("getTestDataLabel2");
        String fieldName = null;
        try
        {
            //Field field = inClass.getField(fieldLabel);
            Field field = PolicyTestData.class.getField("POLICIES_TP1");
            Annotation annotation = field.getAnnotation(MyAnnotation.class);

            if (annotation instanceof MyAnnotation)
            {
                MyAnnotation myAnnotation = (MyAnnotation) annotation;
                System.out.println("name: " + myAnnotation.name());
                System.out.println("value: " + myAnnotation.value());
                fieldName = myAnnotation.name() + " " + myAnnotation.value();
            }
        }
        catch (NoSuchFieldException e)
        {
            System.out.println("annotation excep=" + e);
        }

        return fieldName;
    }

    private static String getTestDataLabel3()
    {
        System.out.println("getTestDataLabel3");
        String fieldName = null;
        try
        {
            //Field field =... //obtain field object
            Field field = PolicyTestData.class.getField("POLICIES_TP1");
            //POLICIES_TP1
            Annotation[] annotations = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations)
            {
                if (annotation instanceof MyAnnotation)
                {
                    MyAnnotation myAnnotation = (MyAnnotation) annotation;
                    System.out.println("name: " + myAnnotation.name());
                    System.out.println("value: " + myAnnotation.value());
                    fieldName = myAnnotation.name() + " " + myAnnotation.value();
                }
            }

        }
        catch (NoSuchFieldException e)
        {
            System.out.println("annotation excep=" + e);
        }

        return fieldName;
    }


    private static String getTestDataLabel4()
    {
        System.out.println("getTestDataLabel4");
        String fieldName = null;
        try
        {
            Field field = PolicyTestData.class.getField("POLICIES_TP1");
            Annotation annotation = field.getAnnotation(MyAnnotation.class);
            System.out.println("gettin er done...");
            if (annotation instanceof MyAnnotation)
            {
                MyAnnotation myAnnotation = (MyAnnotation) annotation;
                //System.out.println("name: " + "dd");
                System.out.println("*************** name: " + myAnnotation.name());
                System.out.println("*************** value: " + myAnnotation.value());
                fieldName = myAnnotation.name() + " " + myAnnotation.value();
            }
            else
            {
                System.out.println("didn't get er done...");
            }
        }
        catch (NoSuchFieldException e)
        {
            System.out.println("annotation excep=" + e);
        }
        return fieldName;
    }

    void addAnnotation()
    {
        ReaderUtil.clearScreen();
        String label = getTestDataLabel2();
        System.out.println(AdminMgrConsole.class.getName() + ".addAnnotation label2=" + label);
        label = getTestDataLabel3();
        System.out.println(AdminMgrConsole.class.getName() + ".addAnnotation label3=" + label);
        label = getTestDataLabel4();
        System.out.println(AdminMgrConsole.class.getName() + ".addAnnotation label4=" + label);
        label = TestUtils.getDataLabel(PolicyTestData.class, "POLICIES_TP1");
        System.out.println(AdminMgrConsole.class.getName() + ".addAnnotation label5=" + label);
        label = TestUtils.getDataLabel(PolicyTestData.class, "POLICIES_TP1");
        System.out.println(AdminMgrConsole.class.getName() + ".addAnnotation label6=" + label);
        ReaderUtil.readChar();
    }

    void addExample()
    {
        Example ee = new Example();

        try
        {
            ExampleAdminMgr eAm = ExampleAdminMgrFactory.createInstance();
            ReaderUtil.clearScreen();
            System.out.println("Enter Example name:");
            ee.setName(ReaderUtil.readLn());
            System.out.println("Enter Example's description field");
            ee.setDescription(ReaderUtil.readLn());
            Example ee2 = eAm.addExample(ee);
            System.out.println("name [" + ee2.getName() + "]");
            System.out.println("internalId [" + ee2.getId() + "]");
            System.out.println("example description [" + ee2.getDescription() + "]");
            System.out.println("has been added");
            System.out.println("ENTER to continue");
        }
        catch (us.jts.fortress.SecurityException e)
        {
            log.error(CLS_NM + ".addExample caught SecurityException=" + e, e);
        }
        ReaderUtil.readChar();
    }
}

