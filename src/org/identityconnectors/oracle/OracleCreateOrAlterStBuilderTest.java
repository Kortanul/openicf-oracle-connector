/**
 * 
 */
package org.identityconnectors.oracle;

import static org.junit.Assert.*;

import org.identityconnectors.common.security.GuardedString;
import org.junit.Test;

/**
 * @author kitko
 *
 */
public class OracleCreateOrAlterStBuilderTest {

    /**
     * Test method for {@link org.identityconnectors.oracle.OracleCreateOrAlterStBuilder#buildCreateUserSt(java.lang.StringBuilder, org.identityconnectors.oracle.OracleUserAttributes)}.
     */
    @Test
    public void testCreateLocalUserSt() {
        try{
            createDefaultBuilder().buildCreateUserSt(new OracleUserAttributes());
            fail("Not enough info, create must fail");
        }catch(Exception e){}
        OracleUserAttributes local = new OracleUserAttributes();
        local.userName = "user1";
        try{
            createDefaultBuilder().buildCreateUserSt(local);
            fail("Not enough info, create must fail");
        }catch(Exception e){}
        local.auth = OracleAuthentication.LOCAL;        
        assertEquals("create user \"user1\" identified by \"user1\"", createDefaultBuilder().buildCreateUserSt(local).toString());
        local.password = new GuardedString("password".toCharArray());
        assertEquals("create user \"user1\" identified by \"password\"", createDefaultBuilder().buildCreateUserSt(local).toString());
        
    }
    
    
    
    
    /** Test create table space */
    @Test
    public void testCreateTableSpace(){
        OracleUserAttributes attributes = new OracleUserAttributes();
        attributes.userName = "user1";
        attributes.defaultTableSpace = "users";
        attributes.tempTableSpace = "temp";
        attributes.auth = OracleAuthentication.LOCAL;
        attributes.password = new GuardedString("password".toCharArray());
        assertEquals("create user \"user1\" identified by \"password\" default tablespace \"users\" temporary tablespace \"temp\"", createDefaultBuilder().buildCreateUserSt(attributes).toString());
        attributes.profile = "MyProfile";
        assertEquals("create user \"user1\" identified by \"password\" default tablespace \"users\" temporary tablespace \"temp\" profile \"MyProfile\"", createDefaultBuilder().buildCreateUserSt(attributes).toString());
    }
    
    /** Test quotas */
    @Test
    public void testCreateQuota(){
        OracleUserAttributes attributes = new OracleUserAttributes();
        attributes.auth = OracleAuthentication.LOCAL;
        attributes.userName = "user1";
        attributes.defaultTableSpace = "users";
        attributes.tempTableSpace = "temp";
        attributes.password = new GuardedString("password".toCharArray());
        attributes.defaultTSQuota = new Quota();
        assertEquals(
                "create user \"user1\" identified by \"password\" default tablespace \"users\" temporary tablespace \"temp\" quota unlimited on \"users\"",
                createDefaultBuilder().buildCreateUserSt(attributes).toString());
        attributes.defaultTSQuota = new Quota("32K");
        assertEquals(
                "create user \"user1\" identified by \"password\" default tablespace \"users\" temporary tablespace \"temp\" quota 32K on \"users\"",
                createDefaultBuilder().buildCreateUserSt(attributes).toString());
        attributes.defaultTSQuota = null;
        attributes.tempTSQuota = new Quota();
        assertEquals(
                "create user \"user1\" identified by \"password\" default tablespace \"users\" temporary tablespace \"temp\" quota unlimited on \"temp\"",
                createDefaultBuilder().buildCreateUserSt(attributes).toString());
        attributes.tempTSQuota = new Quota("32M");
        assertEquals(
                "create user \"user1\" identified by \"password\" default tablespace \"users\" temporary tablespace \"temp\" quota 32M on \"temp\"",
                createDefaultBuilder().buildCreateUserSt(attributes).toString());
        
    }

    private OracleCreateOrAlterStBuilder createDefaultBuilder() {
        return new OracleCreateOrAlterStBuilder(new OracleCaseSensitivityBuilder().build());
    }
    
    /** Test create external */
    @Test
    public void testCreateExternallUserSt() {
        OracleUserAttributes external = new OracleUserAttributes();
        external.userName = "user1";
        external.auth = OracleAuthentication.EXTERNAL;
        assertEquals("create user \"user1\" identified externally", createDefaultBuilder().buildCreateUserSt(external).toString());
    }
    
    /** Test create global */
    @Test
    public void testCreateGlobalUserSt() {
        OracleUserAttributes global = new OracleUserAttributes();
        global.userName = "user1";
        global.auth = OracleAuthentication.GLOBAL;
        try{
            createDefaultBuilder().buildCreateUserSt(global);
            fail("GlobalName should be missed");
        }catch(Exception e){}
        global.globalName = "global";
        assertEquals("create user \"user1\" identified globally as 'global'", createDefaultBuilder().buildCreateUserSt(global).toString());
    }
    
    /** Test expire and lock/unlock */
    @Test
    public void testCreateExpireAndLock() {
        OracleUserAttributes attributes = new OracleUserAttributes();
        attributes.auth = OracleAuthentication.LOCAL;
        attributes.userName = "user1";
        attributes.expirePassword = true;
        attributes.enable = true;
        assertEquals("create user \"user1\" identified by \"user1\" password expire account unlock", createDefaultBuilder().buildCreateUserSt(attributes).toString());
        attributes.expirePassword = false;
        assertEquals("create user \"user1\" identified by \"user1\" account unlock", createDefaultBuilder().buildCreateUserSt(attributes).toString());
    }
    
    /** Test alter user */
    @Test
    public void testAlterUser() {
        OracleUserAttributes attributes = new OracleUserAttributes();
        attributes.auth = OracleAuthentication.LOCAL;
        attributes.userName = "user1";
        attributes.expirePassword = true;
        attributes.enable = true;
        attributes.defaultTSQuota = new Quota();
        UserRecord record = new UserRecord();
        record.defaultTableSpace = "users";
        assertEquals("alter user \"user1\" identified by \"user1\" quota unlimited on \"users\" password expire account unlock", createDefaultBuilder().buildAlterUserSt(attributes, record).toString());
        
    }
    
    

}
