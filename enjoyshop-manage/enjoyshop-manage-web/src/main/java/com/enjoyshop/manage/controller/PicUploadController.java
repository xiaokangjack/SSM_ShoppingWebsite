package com.enjoyshop.manage.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.enjoyshop.manage.bean.PicUploadResult;
import com.enjoyshop.manage.service.PropertieService;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 图片上传
 */
@Controller
@RequestMapping("/pic")
public class PicUploadController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PicUploadController.class);
   
	private static final ObjectMapper mapper = new ObjectMapper();

	/*
	 * 以下这种方式无法正确注入属性值。
	 * @Value作用：获取配置文件的值。
	 * 注入值：在Spring容器初始化（所有的bean）之后，
	 * 只能从当前的所在容器中获取值，然后注入。
	 * Spring容器  --  父容器
	 * SpringMVC容器  -- 子容器
	 * 父子容器的关系：
	 * 1、	子容器能够访问父容器的资源（bean）
	 * a)	示例：Controller(springMVC容器)可以注入Service（spring容器）
	 * 2、	父容器不能访问子容器的资源（bean）
     *因为属性文件都是在spring的配置文件中导入，如下
     *<!-- 配置资源文件 -->
		<property name="locations">
			<list>
				<value>classpath:jdbc.properties</value>
				<value>classpath:env.properties</value>
				<value>classpath:redis.properties</value>
				<value>classpath:httpclient.properties</value>
				<value>classpath:rabbitmq.properties</value>
			</list>
		</property>
		而如果这里使用@Value注解获取属性值时，就无法获取。
		因为@Value只能从获取当前容器中的获取值，不会去查找父容器（虽然子容器可以访问到父容器）但REPOSITORY_PATH在父容器中
		如果我们将<value>classpath:env.properties</value>放到springMVC的配置文件中，则@value可以正常注入
	 */
	//@Value("${REPOSITORY_PATH}")
    //public String REPOSITORY_PATH;
	
	//解决@value无法注入的问题
	@Autowired
	private PropertieService propertieService;//propertieService中读取了外部配置文件中的一些参数
	
	// 允许上传的格式
	private static final String[] IMAGE_TYPE = new String[] { ".bmp", ".jpg", ".jpeg", ".gif", ".png" };
    //produces指定响应类型为text/plain
	@RequestMapping(value = "/upload", method = RequestMethod.POST,produces=MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	//这里返回了string类型，但并不代表视图名，因为@ResponseBody注解使得输出为json
	//因为上传文件的返回数据是文本类型的json数据即text/plain
	/*在common.js中定义有如下参数
	 // 编辑器参数
	kingEditorParams : {
		filePostName  : "uploadFile", //上传表单名称
		uploadJson : '/rest/pic/upload', // 上传的路径
		dir : "image" //类型
	}
	@RequestParam("uploadFile")就是拿到这个上传表单名称
	 */
	public String upload(@RequestParam("uploadFile") MultipartFile uploadFile , HttpServletResponse response) throws Exception {

		// 校验图片格式
		boolean isLegal = false;
		for (String type : IMAGE_TYPE) {
			if (StringUtils.endsWithIgnoreCase(uploadFile.getOriginalFilename(), type)) {
				isLegal = true;
				break;
			}
		}

		// 封装Result对象，并且将文件的byte数组放置到result对象中
		PicUploadResult fileUploadResult = new PicUploadResult();

		// 状态
		fileUploadResult.setError(isLegal ? 0 : 1);

		// 文件新路径
		String filePath = getFilePath(uploadFile.getOriginalFilename());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Pic file upload .[{}] to [{}] .", uploadFile.getOriginalFilename(), filePath);
		}

		// 生成图片的绝对引用地址
		String picUrl = StringUtils.replace(StringUtils.substringAfter(filePath, propertieService.REPOSITORY_PATH), "\\", "/");
		fileUploadResult.setUrl(propertieService.IMAGE_BASE_URL + picUrl);

		File newFile = new File(filePath);

		// 写文件到磁盘
		uploadFile.transferTo(newFile);

		// 校验图片是否合法，尝试读取文件的宽和高（只有图片存在宽和高）
		isLegal = false;
		try {
			BufferedImage image = ImageIO.read(newFile);
			if (image != null) {
				fileUploadResult.setWidth(image.getWidth() + "");
				fileUploadResult.setHeight(image.getHeight() + "");
				isLegal = true;
			}
		} catch (IOException e) {
		}

		// 状态
		fileUploadResult.setError(isLegal ? 0 : 1);

		if (!isLegal) {
			// 不合法，将磁盘上的文件删除
			newFile.delete();
		}

		//将一个java对象序列化为一个json字符串
		return mapper.writeValueAsString(fileUploadResult);
	}

	//生成文件路径和文件名
	private String getFilePath(String sourceFileName) {
		String baseFolder = propertieService.REPOSITORY_PATH + File.separator + "images";
		Date nowDate = new Date();
		// yyyy/MM/dd
		String fileFolder = baseFolder + File.separator + new DateTime(nowDate).toString("yyyy") + File.separator + new DateTime(nowDate).toString("MM") + File.separator
				+ new DateTime(nowDate).toString("dd");
		File file = new File(fileFolder);
		if (!file.isDirectory()) {
			// 如果目录不存在，则创建目录
			file.mkdirs();
		}
		// 生成新的文件名
		String fileName = new DateTime(nowDate).toString("yyyyMMddhhmmssSSSS") + RandomUtils.nextInt(100, 9999) + "." + StringUtils.substringAfterLast(sourceFileName, ".");
		return fileFolder + File.separator + fileName;
	}

}
