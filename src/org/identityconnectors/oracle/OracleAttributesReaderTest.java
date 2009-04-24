/**
 * 
 */
package org.identityconnectors.oracle;

import static org.identityconnectors.oracle.OracleConnector.ORACLE_AUTHENTICATION_ATTR_NAME;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_AUTH_GLOBAL;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_AUTH_LOCAL;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_DEF_TS_ATTR_NAME;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_DEF_TS_QUOTA_ATTR_NAME;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_GLOBAL_ATTR_NAME;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_PROFILE_ATTR_NAME;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_TEMP_TS_ATTR_NAME;
import static org.identityconnectors.oracle.OracleConnector.ORACLE_TEMP_TS_QUOTA_ATTR_NAME;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.test.common.TestHelpers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for OracleCreateAttributesReader
 * @author kitko
 *
 */
public class OracleAttributesReaderTest {

    /**
     * Test method for {@link org.identityconnectors.oracle.OracleAttributesReader#readAuthAttributes(java.util.Set, org.identityconnectors.oracle.OracleUserAttributes)}.
     */
    @Test
    public final void testReadCreateAuthAttributes() {
        final OracleAttributesReader reader = new OracleAttributesReader(TestHelpers.createDummyMessages());
        OracleUserAttributes caAttributes = new OracleUserAttributes();
        caAttributes.userName = "testUser";
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_AUTHENTICATION_ATTR_NAME, ORACLE_AUTH_LOCAL));
        attributes.add(AttributeBuilder.buildPassword("myPassword".toCharArray()));
        reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        Assert.assertEquals(OracleAuthentication.LOCAL, caAttributes.auth);
        Assert.assertNotNull("Password must not be null",caAttributes.password);
        
        attributes.clear();
        caAttributes = new OracleUserAttributes();
        caAttributes.userName = "testUser";
        reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        Assert.assertEquals(OracleAuthentication.LOCAL, caAttributes.auth);
        Assert.assertNull("Password must be null",caAttributes.password);
        
        
        //Test for failures
        attributes.clear();
        reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        attributes.add(AttributeBuilder.build(ORACLE_AUTHENTICATION_ATTR_NAME, "invalid authentication"));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
            fail("Must fail for invalid authentication");
        }
        catch(RuntimeException e){}

        attributes.clear();
        attributes.add(AttributeBuilder.build(ORACLE_AUTHENTICATION_ATTR_NAME, ORACLE_AUTH_GLOBAL));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
            fail("Must fail for missing global name");
        }
        catch(RuntimeException e){}
        
        attributes.clear();
        attributes.add(AttributeBuilder.build(ORACLE_AUTHENTICATION_ATTR_NAME, ORACLE_AUTH_GLOBAL));
        attributes.add(AttributeBuilder.build(ORACLE_GLOBAL_ATTR_NAME, ""));

        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
            fail("Must fail for empty global name");
        }
        catch(RuntimeException e){}

    }
    
    
    /**
     * Test method for {@link org.identityconnectors.oracle.OracleAttributesReader#readRestAttributes(java.util.Set, org.identityconnectors.oracle.OracleUserAttributes)}.
     */
    @Test
    public final void testReadCreateRestAttributes() {
        final OracleAttributesReader reader = new OracleAttributesReader(TestHelpers.createDummyMessages());
        OracleUserAttributes caAttributes = new OracleUserAttributes();
        caAttributes.userName = "testUser";
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.buildPasswordExpired(true));
        attributes.add(AttributeBuilder.build(ORACLE_DEF_TS_ATTR_NAME, "defts"));
        attributes.add(AttributeBuilder.build(ORACLE_TEMP_TS_ATTR_NAME, "tempts"));
        attributes.add(AttributeBuilder.build(ORACLE_PROFILE_ATTR_NAME, "myprofile"));
        attributes.add(AttributeBuilder.buildEnabled(true));
        attributes.add(AttributeBuilder.build(ORACLE_DEF_TS_QUOTA_ATTR_NAME,"30M"));
        attributes.add(AttributeBuilder.build(ORACLE_TEMP_TS_QUOTA_ATTR_NAME,"100M"));
        reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        Assert.assertEquals("defts", caAttributes.defaultTableSpace);
        Assert.assertEquals(true, caAttributes.expirePassword);
        Assert.assertEquals("tempts", caAttributes.tempTableSpace);
        Assert.assertEquals("myprofile", caAttributes.profile);
        Assert.assertEquals(true, caAttributes.enable);
        Assert.assertEquals("30M", caAttributes.defaultTSQuota);
        Assert.assertEquals("100M", caAttributes.tempTSQuota);
        
        //Test for failures
        attributes.clear();
        reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        attributes.add(AttributeBuilder.build(OperationalAttributes.PASSWORD_EXPIRED_NAME, "invalid"));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for invalid PASSWORD_EXPIRED_NAME");
        }
        catch(RuntimeException e){}
        
        attributes.clear();
        attributes.add(AttributeBuilder.build(OperationalAttributes.PASSWORD_EXPIRED_NAME));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for null PASSWORD_EXPIRED_NAME");
        }
        catch(RuntimeException e){}
        
        attributes.clear();
        attributes.add(AttributeBuilder.build(ORACLE_DEF_TS_ATTR_NAME));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for null ORACLE_DEF_TS_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes.clear();
        attributes.add(AttributeBuilder.build(ORACLE_DEF_TS_ATTR_NAME,""));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for empty ORACLE_DEF_TS_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_TEMP_TS_ATTR_NAME));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for null ORACLE_TEMP_TS_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_TEMP_TS_ATTR_NAME,""));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for empty ORACLE_TEMP_TS_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_PROFILE_ATTR_NAME));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for null ORACLE_PROFILE_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_PROFILE_ATTR_NAME,""));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for empty ORACLE_PROFILE_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_DEF_TS_QUOTA_ATTR_NAME));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for null ORACLE_PROFILE_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_DEF_TS_QUOTA_ATTR_NAME,""));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for empty ORACLE_PROFILE_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_TEMP_TS_QUOTA_ATTR_NAME));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for null ORACLE_TEMP_TS_QUOTA_ATTR_NAME");
        }
        catch(RuntimeException e){}

        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_TEMP_TS_QUOTA_ATTR_NAME,""));
        try{
        	reader.readCreateAttributes(AttributeUtil.toMap(attributes), caAttributes);
        	fail("Must fail for empty ORACLE_TEMP_TS_QUOTA_ATTR_NAME");
        }
        catch(RuntimeException e){}
        
    }
    
    
    @Test
    public void testReadAlterAttributes(){
        final OracleAttributesReader reader = new OracleAttributesReader(TestHelpers.createDummyMessages());
        OracleUserAttributes caAttributes = new OracleUserAttributes();
        caAttributes.userName = "testUser";
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ORACLE_AUTHENTICATION_ATTR_NAME, ORACLE_AUTH_LOCAL));
        attributes.add(AttributeBuilder.buildPassword("myPassword".toCharArray()));
        reader.readAlterAttributes(AttributeUtil.toMap(attributes), caAttributes);
        Assert.assertEquals(OracleAuthentication.LOCAL, caAttributes.auth);
        Assert.assertNotNull("Password must not be null",caAttributes.password);

        //verify that password is not set for alter when not set
        caAttributes = new OracleUserAttributes();
        caAttributes.userName = "testUser";        
        attributes.clear();
        attributes.add(AttributeBuilder.buildPasswordExpired(true));
        reader.readAlterAttributes(AttributeUtil.toMap(attributes), caAttributes);
        Assert.assertNull("Password must be null",caAttributes.password);
        
        
    }


}

