package com.simple.admin.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.common.util.AjaxWebUtil;
import com.simple.common.util.FileUploadUtil;
import com.simple.common.util.ImageHandleUtil;
@Controller
@RequestMapping(value = "/image")
public class ImageUploadController {
	
	private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);

	
	@RequestMapping(value = "upload",method=RequestMethod.POST)
	@ResponseBody
	public String uploadStudent(HttpServletRequest request, HttpServletResponse response) {
		try {
			//欢迎登录安全教育平台图片
			String image = AjaxWebUtil.getRequestPayload(request);
			String imagewidth = request.getParameter("width");
			String suffix = request.getParameter("suffix");
			String filepath = getfilepath(image,suffix,imagewidth);
			return AjaxWebUtil.sendAjaxResponse(request, response, true,"上传成功", filepath);
		}catch(Exception e) {
			log.error("上传失败",e);
			return AjaxWebUtil.sendAjaxResponse(request, response, false,"上传失败:"+e.getLocalizedMessage(), e.getLocalizedMessage());
		}
	}
	
	private String getfilepath(String imageData,String subfix,String imagewidth) throws IOException {
		if (StringUtils.isEmpty(subfix)) {
			subfix = "jpg";
		}
		File b1SrcFile = FileUploadUtil.getFileByHex(imageData,subfix, FileUploadUtil.UPLOAD_IMAGE_DIR);
		if ( null != b1SrcFile) {
			if ( null != imagewidth) {
				int width = 0;
				try {
					width = Integer.parseInt(imagewidth);
				}catch(Exception e) {
				}
				if (width>0) {
					ImageHandleUtil.cutImage(b1SrcFile, width);
					return ImageHandleUtil.getScaleFilePath(b1SrcFile.getPath(),width);
				}
			}
			return b1SrcFile.getPath();
		}
		return null;
	}
}
