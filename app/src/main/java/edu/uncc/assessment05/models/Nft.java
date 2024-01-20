package edu.uncc.assessment05.models;

public class Nft {
    public String id, image_thumbnail_url, name, collection_name, collection_banner_image_url;

    public Nft() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Nft{" +
                "id='" + id + '\'' +
                ", image_thumbnail_url='" + image_thumbnail_url + '\'' +
                ", name='" + name + '\'' +
                ", collection_name='" + collection_name + '\'' +
                ", collection_banner_image_url='" + collection_banner_image_url + '\'' +
                '}';
    }
}
