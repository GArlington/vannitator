package org.soqqo.vannitator.cache;

import java.util.List;
import java.util.zip.ZipEntry;

import junit.framework.Assert;

import org.junit.Test;
import org.soqqo.vannitator.annotation.VannitationType;

public class PersistingMultiKeyCacheTest {

    @Test
    public void testWriteThenRead() {

        // ctor using the default cache filename
        MultiKeyDiskCache pmmc = new MultiKeyDiskCache("target" , MultiKeyDiskCache.VANNI_CACHE_FILENAME);
        // / remove the old cache.
        pmmc.destroy();

        ClassAnnotationProcessorEntry cape = new ClassAnnotationProcessorEntry();
        // any old name for our test
        cape.setAnnotatedFQClassName(ZipEntry.class.getName());
        // ConfigBean<GenRequestProxy> b = new ConfigBean<GenRequestProxy>();
        // cape.setConfigurationBean(b);
        System.out.println(">>putt");
        pmmc.put(VannitationType.VannitateManyToOne, cape);
        pmmc.persist();

        System.out.println(">>persist");
        MultiKeyDiskCache pmmc2 = new MultiKeyDiskCache("target" , MultiKeyDiskCache.VANNI_CACHE_FILENAME);
        System.out.println(">>loaded");

        List<ClassAnnotationProcessorEntry> coll = pmmc2.getAll(VannitationType.VannitateManyToOne);
        System.out.println("returned coll as size=" + coll.size());
        Assert.assertTrue("Size is not 1!", coll.size() == 1);
        Assert.assertTrue(coll.get(0).getAnnotatedFQClassName().equals(ZipEntry.class.getName()));
        // Assert.assertTrue(coll.get(0).getConfigurationBean().equals(b));

    }
}
