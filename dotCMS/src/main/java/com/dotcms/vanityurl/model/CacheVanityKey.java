package com.dotcms.vanityurl.model;

import java.io.Serializable;
import org.apache.commons.collections.keyvalue.MultiKey;

/**
 * Encapsulates the keys for cache vanities.
 * @author jsanca
 */
public class CacheVanityKey extends MultiKey implements Serializable {

    public CacheVanityKey(final String hostId, final long languageId, final String uri) {
        super(hostId, languageId, uri);
    }

    @Override
    public String toString() {

        return this.getKey(0) + "|" +
                this.getKey(2).toString().replace('/', '|') +
                "|lang_" + this.getKey(1);
    }
} // E:O:F:CacheVanityKey.
