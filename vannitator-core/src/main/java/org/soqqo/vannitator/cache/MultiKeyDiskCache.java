package org.soqqo.vannitator.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soqqo.vannitator.annotation.VannitationType;

public class MultiKeyDiskCache implements MultiKeyCacheWrapper<VannitationType, ClassAnnotationProcessorEntry>, VannitationCrossRunCache {

    private final Logger log = LoggerFactory.getLogger(MultiKeyDiskCache.class.getName());

    /*
     * have to do this because MultiValueMap is not serializable yet and a Cache
     * (like ehcache/jcache etc is just too big for what I need).
     * https://issues.apache.org/jira/browse/COLLECTIONS-240
     * 
     * Plus ehcache no longer acts as a Disk Only Cache from 2.0 onwards.
     */
    HashMap<VannitationType, ArrayList<ClassAnnotationProcessorEntry>> entries;

    public final static String VANNI_CACHE_FILENAME = ".vannitation-processor.cache";
    // we are letting the file be created into the current working directory
    private final static String DEFAULT_CACHEFILENAME = VANNI_CACHE_FILENAME;

    private String cacheFileName;

    private File cacheFile;

    public MultiKeyDiskCache(String baseDir, String filename) {
        this.cacheFileName = baseDir + File.separator + filename;
        this.cacheFile = new File(this.cacheFileName);
        if (cacheFile.exists()) {
            loadCache();
        } else {
            createNew();
        }
    }

    public MultiKeyDiskCache() {
        this("target", DEFAULT_CACHEFILENAME);
    }

    private void createNew() {
        entries = new HashMap<VannitationType, ArrayList<ClassAnnotationProcessorEntry>>();
        try {
            this.cacheFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create the cache file", e);
        }
        log.debug("Created a new cache file - [" + cacheFile.getAbsolutePath() + "]");
    }

    @SuppressWarnings("unchecked")
    private void loadCache() {
        log.debug("Loading cache from existing file - " + cacheFile.getAbsolutePath());
        ObjectInputStream objectInputStream = null;
        try {
            FileInputStream fout = new FileInputStream(this.cacheFile);
            objectInputStream = new ObjectInputStream(fout);
            this.entries = (HashMap<VannitationType, ArrayList<ClassAnnotationProcessorEntry>>) objectInputStream.readObject();

            // dump out some statistics
            int annotationTypeCount = entries.size();
            int annotationClassEntries = 0;
            for (VannitationType key : entries.keySet()) {
                annotationClassEntries += entries.get(key).size();
            }

            log.debug("Loaded the cache with " + annotationTypeCount + " Vannitation Type(s), " + annotationClassEntries + " annotated classes from [" + this.cacheFile.getAbsolutePath() + "]");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load the cache: " + this.cacheFile.getAbsolutePath(), e);
        } finally {
            try {
                objectInputStream.close();
            } catch (IOException e) {}
        }

    }

    /*
     * custom put.
     * Ensures that a new array (per VannitationType) is created before a new entry is loaded.
     * Ensures that in that array, only one of Annotation/Annotated ClassAnnotationProcessorEntry exists.
     * 
     * @see org.soqqo.vannitator.cache.VannitationCrossRunCache#put(org.soqqo.vannitator.annotation.VannitationType, org.soqqo.vannitator.cache.ClassAnnotationProcessorEntry)
     */
    @Override
    public void put(VannitationType key, ClassAnnotationProcessorEntry value) {
        ArrayList<ClassAnnotationProcessorEntry> list = entries.get(key);

        if (list == null) {
            list = new ArrayList<ClassAnnotationProcessorEntry>();
            entries.put(key, list);
        }

        /*
         * pushed this to the .equals() of ClassAnnotationProcessorEntry
         * Iterator<ClassAnnotationProcessorEntry> iter = list.iterator();
         * while (iter.hasNext()) {
         * ClassAnnotationProcessorEntry checkEntry = iter.next();
         * if (checkEntry.getAnnotatedFQClassName().equals(value.getAnnotatedFQClassName()) && checkEntry.getAnnotationFQClassName().equals(value.getAnnotationFQClassName())) {
         * iter.remove();
         * }
         * }
         */

        if (list.contains(value)) {
            list.remove(value); // removes it based on the ClassAnnotationProcessorEntry.equals() - it checks the AnnotationFQN and the ClassFQN
        }
        list.add(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.cache.VannitationCrossRunCache#persist()
     */
    @Override
    public void persist() {
        ObjectOutputStream objectOutputStream = null;
        try {
            FileOutputStream fout = new FileOutputStream(this.cacheFile);
            objectOutputStream = new ObjectOutputStream(fout);
            objectOutputStream.writeObject(entries);
            objectOutputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException("failed to serialise", e);
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
            } catch (IOException e) {}
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.cache.VannitationCrossRunCache#destroy()
     */
    @Override
    public void destroy() {
        if (this.cacheFile.exists()) {
            this.cacheFile.delete();
        }
        createNew();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.cache.VannitationCrossRunCache#getAll(org.soqqo.vannitator.annotation.VannitationType)
     */

    @Override
    public List<ClassAnnotationProcessorEntry> getAll(VannitationType key) {
        ArrayList<ClassAnnotationProcessorEntry> list = entries.get(key);
        return list == null ? new ArrayList<ClassAnnotationProcessorEntry>(0) : list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.soqqo.vannitator.cache.VannitationCrossRunCache#getByAnnotationClass(java.lang.String)
     */
    @Override
    public List<ClassAnnotationProcessorEntry> getByAnnotationClass(String annotationClassName) {
        ArrayList<ClassAnnotationProcessorEntry> returnList = new ArrayList<ClassAnnotationProcessorEntry>();
        for (ArrayList<ClassAnnotationProcessorEntry> aList : entries.values()) {
            // use iterator so we can remove while iterating
            Iterator<ClassAnnotationProcessorEntry> iter = aList.iterator();
            while (iter.hasNext()) {
                ClassAnnotationProcessorEntry entry = iter.next();

                // if the class does not exist, remove it from our cache

                try {
                    @SuppressWarnings("unused")
                    // we do use it, We are checking if the class exists and removing it if it doesn't (see the catch)
                    Class<?> clazz = Class.forName(entry.getAnnotatedFQClassName());
                    if (entry.getAnnotationFQClassName().equals(annotationClassName)) {
                        returnList.add(entry);
                    }
                } catch (ClassNotFoundException e) {
                    // remove it
                    iter.remove();
                }
            }
        }
        return returnList;
    }

}
