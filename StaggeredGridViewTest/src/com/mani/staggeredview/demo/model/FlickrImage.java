package com.mani.staggeredview.demo.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Holds the data for Flickr photo that is used to display Flickr Images in ListViews.
 * 
 * @author Mani Selvaraj
 *
 */
public class FlickrImage implements Serializable{
    public static final long serialVersionUID=-4899452726203839416L;
	String id;
	
	String secret;
	
	String server;
	
	String farm;
	
	String title;

	String owner;

    public String url;
    public long filesize;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getFarm() {
		return farm;
	}

	public void setFarm(String farm) {
		this.farm = farm;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getImageUrl() {
        if (TextUtils.isEmpty(url)) {
            String imageUrl = "http://farm" + getFarm() + ".static.flickr.com/" + getServer()
                    + "/" + getId() + "_" + getSecret() + "_b.jpg";
            return imageUrl;
        } else {
            return url;
        }
    }

    @Override
    public String toString() {
        return "FlickrImage{"+
            "id='"+id+'\''+
            ", url='"+getImageUrl()+'\''+
            ", secret='"+secret+'\''+
            ", server='"+server+'\''+
            ", farm='"+farm+'\''+
            ", title='"+title+'\''+
            ", owner='"+owner+'\''+
            '}';
    }
}
