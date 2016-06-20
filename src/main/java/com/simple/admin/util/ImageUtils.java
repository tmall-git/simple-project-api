package com.simple.admin.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.simple.common.config.EnvPropertiesConfiger;


public class ImageUtils {

	/**
	 * 上传图片,返回全路径，带域名
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String uploadImage(MultipartFile file) throws Exception {
		String fileUrl = uploadImageReturnPath(file);
		return getAllPath(fileUrl);
	}
	
	public static String uploadImage(File file) throws Exception {
		String fileUrl = uploadImageReturnPath(file);
		return getAllPath(fileUrl);
	}
	
	public static String uploadImageReturnPath(File file) throws Exception {
		//return FDFS4Upload.lefileUpload(file,file.getName());
		return null;
	}
	
	private static String uploadImageReturnPath(MultipartFile file) throws Exception {
		//return FDFS4Upload.lefileUpload(file);
		return null;
	}
	
	private static String getAllPath(String fileUrl) {
		if (StringUtils.isBlank(fileUrl)) {
			return null;
		}
		return getPicDomain() +"/"+ fileUrl;
	}
	
	public static String getPicDomain() {
		long index = Math.round(Math.random()*(4-1)+1);
	    return String.format(EnvPropertiesConfiger.getValue("picPath"), index);
	}
	
}
