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
当前登录的用户为${currUser}  
<br/>  
<br/>  
<a href="<%=request.getContextPath()%>/mydemo/logout" target="_blank">Logout</a>
</body>
</html>