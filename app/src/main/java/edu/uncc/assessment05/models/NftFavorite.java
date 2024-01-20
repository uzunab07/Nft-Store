package edu.uncc.assessment05.models;

import com.google.firebase.Timestamp;

public class NftFavorite {
    public String docId, ownerId, image_thumbnail_url, name, collection_name, collection_banner_image_url;
    public Timestamp created_at;

    public NftFavorite() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getImage_thumbnail_url() {
        return image_thumbnail_url;
    }

    public void setImage_thumbnail_url(String image_thumbnail_url) {
        this.image_thumbnail_url = image_thumbnail_url;
    }

    public String getCollection_name() {
        return collection_name;
    }

    public void setCollection_name(String collection_name) {
        this.collection_name = collection_name;
    }

    public String getCollection_banner_image_url() {
        return collection_banner_image_url;
    }

    public void setCollection_banner_image_url(String collection_banner_image_url) {
        this.collection_banner_image_url = collection_banner_image_url;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
