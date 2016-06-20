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
	<script type="text/javascript">  
function reloadVerifyCode(){  
    document.getElementById('verifyCodeImage').setAttribute('src', '${pageContext.request.contextPath}/mydemo/getVerifyCodeImage');  
}  
</script> 
<body>
<div style="color:red; font-size:22px;">${message_login}</div>  
<form action="<%=request.getContextPath()%>/login/doLogin" method="POST">  
    姓名：<input type="text" name="username"/><br/>  
    密码：<input type="text" name="password"/><br/>  
    <input type="submit" value="确认"/>  
</form>
</body>
</html>