package org.soqqo.vannitator.annotation;

import junit.framework.Assert;

import org.junit.Test;
import org.soqqo.vannitator.support.OneToOneNameHandler;

public class PackageRenameTest {

    @Test
    public void testRenamer() {
        String p = "org.foo.baqz.bar";
        String r = "abc.{2}.foox.abxx.{1}.{2}.{0}";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("abc.baqz.foox.abxx.foo.baqz.org".equals(rename));
    }

    @Test
    public void testRenamer2() {
        String p = "org.foo.baqz.bar";
        String r = "a.s.s";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("a.s.s".equals(rename));
    }

    @Test
    public void testRenamer3() {
        String p = "org.foo.baqz.bar";
        String r = "{0-6}";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("org.foo.baqz.bar".equals(rename));
    }

    @Test
    public void testRenamer4() {
        String p = "org";
        String r = "{-2}";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("org".equals(rename));

        r = "{1-}";
        rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        Assert.assertTrue("org".equals(rename));

        r = "{8-}";
        rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        Assert.assertTrue("org".equals(rename));

    }

    @Test
    public void testRenamer5() {
        String p = "org.soqqo.foobar.model";
        String r = "{0-2}.client.request.{3}";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("org.soqqo.foobar.client.request.model".equals(rename));

    }

    @Test
    public void testRenamerSubPackage() {
        String p = "org.soqqo.foobar.model.blah.subbla";
        String r = "{0-2}.client.request.{3-}";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("org.soqqo.foobar.client.request.model.blah.subbla".equals(rename));
    }

        
    @Test
    public void testEmpty() {
        String p = "org.soqqo.foobar.model";
        String r = "";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("".equals(rename));
    }

    
    @Test
    public void testDefault() {
        String p = "org.soqqo.foobar.model";
        String r = "{0-}";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("org.soqqo.foobar.model".equals(rename));
    }

    @Test
    public void testGtrFirstUnlimitedBound() {
        String p = "org.soqqo.foobar.model";
        String r = "{2-}";
        String rename = OneToOneNameHandler.utilGetNewPackageName(r, p);
        System.out.println(rename);
        Assert.assertTrue("foobar.model".equals(rename));
    }

    
}
