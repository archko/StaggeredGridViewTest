package com.mani.staggeredview.demo.model;

import java.io.Serializable;

public class FlickrResponsePhotos implements Serializable{
    public static final long serialVersionUID=-4899452726203839418L;
	FlickrGetImagesResponse photos;
		
	public FlickrGetImagesResponse getPhotos() {
		return photos;
	}

	public void setPhotos(FlickrGetImagesResponse photos) {
		this.photos = photos;
	}

    @Override
    public String toString() {
        return "FlickrResponsePhotos{"+
            "photos="+photos+
            '}';
    }
}
