package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Resource {

	private String path;

	private ClassLoader classLoader;

	private Class<?> clazz;
	
	private String webRoot;

	public Resource(String path) {
		this(path, (ClassLoader) null);
	}

	public Resource(String path, ClassLoader classLoader) {
		if (path.startsWith("/")) { // classLoader 加载资源 不已 / 开头
			path = path.substring(1);
		}
		this.path = path;
		this.classLoader = (classLoader != null ? classLoader : getDefaultClassLoader());
	}

	public Resource(String path, Class<?> clazz) {
		this.path = path;
		this.clazz = clazz;
	}
	
	

	public String getWebRoot() {
		return webRoot;
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public String getPhysicalwebapp() throws IOException {
		if(this.webRoot == null){
			URL url = getDefaultClassLoader().getResource("/");
			if(url == null) return null;
			webRoot = url.getFile();
			webRoot = new File(webRoot).getParentFile().getParentFile().getCanonicalPath();
			return webRoot;
		}
		return webRoot;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(new Resource("").getPhysicalwebapp());
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
		}
		if (cl == null) {
			cl = Resource.class.getClassLoader();
		}
		return cl;
	}

	public boolean exists() {
		URL url;
		if (this.clazz != null) {
			url = this.clazz.getResource(this.path);
		} else {
			url = this.classLoader.getResource(this.path);
		}
		return (url != null);
	}

	/**
	 * This implementation opens an InputStream for the given class path
	 * resource.
	 * 
	 * @see java.lang.ClassLoader#getResourceAsStream(String)
	 * @see java.lang.Class#getResourceAsStream(String)
	 */
	public InputStream getInputStream() throws IOException {
		InputStream is = null;
		if (this.clazz != null) {
			is = this.clazz.getResourceAsStream(this.path);
		} else if (this.classLoader != null) {
			is = this.classLoader.getResourceAsStream(this.path);
		} 
		if(is == null){
			is = new FileInputStream(new File(getPhysicalwebapp()+"/"+path));
//			is=Resource.class.getResourceAsStream("/"+path);
		}
		return is;
	}

	public URL getURL() throws IOException {
		URL url;
		if (this.clazz != null) {
			url = this.clazz.getResource(this.path);
		} else {
			url = this.classLoader.getResource(this.path);
		}
		if (url == null) {
			throw new FileNotFoundException(path + " cannot be resolved to URL because it does not exist");
		}
		return url;
	}

	/**
	 * Return the path for this resource (as resource path within the class
	 * path).
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * Return the ClassLoader that this resource will be obtained from.
	 */
	public final ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : this.clazz.getClassLoader());
	}

}
