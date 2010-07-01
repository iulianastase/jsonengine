package com.jsonengine.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.jsonengine.common.JERequest;
import com.jsonengine.common.JEUtils;
import com.jsonengine.service.crud.CRUDRequest;

import net.arnx.jsonic.JSON;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

/**
 * Represents a JSON document posted by a client. 
 * 
 * @author @kazunori_279
 */
@Model(schemaVersion = 1)
public class JEDoc implements Serializable {

    /**
     * A registered property name for docId of each JSON document.
     */
    public static final String PROP_NAME_DOCID = "_docId";

    /**
     * A registered property name for updatedAt of each JSON document.
     */
    public static final String PROP_NAME_UPDATED_AT = "_updatedAt";

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance of JEDoc2 from specified {@link CRUDRequest}.
     * 
     * @condParam jeReq
     * @return {@link JEDoc2} instance
     */
    public static JEDoc createJEDoc(JERequest jeReq) {
        final JEDoc jeDoc = new JEDoc();
        jeDoc
            .setKey(Datastore.createKey(JEDoc.class, JEUtils.i.generateUUID()));
        jeDoc.setCreatedAt(jeReq.getRequestedAt());
        jeDoc.setCreatedBy(jeReq.getRequestedBy());
        jeDoc.setDocType(jeReq.getDocType());
        return jeDoc;
    }

    // a timestamp of document creation
    private long createdAt;

    // User ID of the creator of this document
    private String createdBy;

    // JSON document type
    private String docType;

    // a Map that holds all the content of the JSON document
    @Attribute(lob = true)
    private Map<String, Object> docValues;

    // all property values will be added here automatically
    // "<docType>:<propName>:<propValue>" e.g. "foo:updatedAt:123"
    private Set<String> indexEntries;

    @Attribute(primaryKey = true)
    private Key key;

    // a timestamp of document update
    private long updatedAt;

    // User ID of the updater of this document
    private String updatedBy;

    @Attribute(version = true)
    private Long version;

    /**
     * Encodes this document into a JSON document.
     * 
     * @return JSON document
     */
    public String encodeJSON() {
        return JSON.encode(getDocValues());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JEDoc other = (JEDoc) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getDocId() {
        return this.key.getName();
    }

    public String getDocType() {
        return docType;
    }

    public Map<String, Object> getDocValues() {
        return docValues;
    }

    public Set<String> getIndexEntries() {
        return indexEntries;
    }

    /**
     * Returns the key.
     *
     * @return the key
     */
    public Key getKey() {
        return key;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setDocType(String className) {
        this.docType = className;
    }

    public void setDocValues(Map<String, Object> objValue) {
        this.docValues = objValue;
    }

    public void setIndexEntries(Set<String> indexValues) {
        this.indexEntries = indexValues;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * Sets the version.
     *
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    
    
}