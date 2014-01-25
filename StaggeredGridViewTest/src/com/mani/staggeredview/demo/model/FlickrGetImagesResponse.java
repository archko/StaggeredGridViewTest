package com.mani.staggeredview.demo.model;

import java.io.Serializable;
import java.util.List;

public class FlickrGetImagesResponse implements Serializable{
    public static final long serialVersionUID=-4899452726203839417L;
	public String id;
	
	List<FlickrImage> photo;

	public List<FlickrImage> getPhotos() {
		return photo;
	}

    @Override
    public String toString() {
        return "FlickrGetImagesResponse{"+
            "id='"+id+'\''+
            ", photo="+photo+
            '}';
    }
}
