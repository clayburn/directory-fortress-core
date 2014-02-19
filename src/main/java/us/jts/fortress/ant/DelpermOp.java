/*
 * Copyright (c) 2009-2014, JoshuaTree. All Rights Reserved.
 */

package us.jts.fortress.ant;

import java.util.ArrayList;
import java.util.List;


/**
 * The class is used by {@link FortressAntTask} to load {@link PermAnt}s used to drive {@link us.jts.fortress.AdminMgr#deletePermission(us.jts.fortress.rbac.Permission)}.
 * It is not intended to be callable by programs outside of the Ant load utility.  The class name itself maps to the xml tag used by load utility.
 * <p>This class name, 'DelpermOp', is used for the xml tag in the load script.</p>
 * <pre>
 * {@code
 * <target name="all">
 *     <FortressAdmin>
 *         <delpermop>
 *           ...
 *         </delpermop>
 *     </FortressAdmin>
 * </target>
 * }
 * </pre>
 *
 * @author Shawn McKinney
 */
public class DelpermOp
{
    final private List<PermAnt> permissions = new ArrayList<>();


    /**
     * All Ant data entities must have a default constructor.
     */
    public DelpermOp()
    {
    }


    /**
     * <p>This method name, 'addPermOp', is used for derived xml tag 'permop' in the load script.</p>
     * <pre>
     * {@code
     * <delpermop>
     *     <permop opName="main" objName="/jsp/cal/cal1.jsp"/>
     *     <permop opName="8am" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="9am" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="10am" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="11am" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="12pm" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="1pm" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="2pm" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="3pm" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="4pm" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="5pm" objName="/jsp/cal/cal2.jsp"/>
     *     <permop opName="6pm" objName="/jsp/cal/cal2.jsp"/>
     * </delpermop>
     * }
     * </pre>
     *
     * @param permission contains reference to data element targeted for removal..
     */
    public void addPermOp(PermAnt permission)
    {
        this.permissions.add(permission);
    }


    /**
     * Used by {@link FortressAntTask#addPermOps()} to retrieve list of Permissions as defined in input xml file.
     *
     * @return collection containing {@link PermAnt}s targeted for removal.
     */
    public List<PermAnt> getPermOps()
    {
        return this.permissions;
    }
}

