<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<meta name="description" content="联想官网商城后台管理系统" />
	<title>设置</title>

</head>
<body>
普通用户可访问<a href="<%=request.getContextPath()%>/mydemo/getUserInfo" target="_blank">用户信息页面</a>  
<br/>  
<br/>  
管理员可访问<a href="<%=request.getContextPath()%>/admin/listUser.jsp" target="_blank">用户列表页面</a>  
<br/>  
<br/>  
<a href="<%=request.getContextPath()%>/mydemo/logout" target="_blank">Logout</a>
</body>
</html>