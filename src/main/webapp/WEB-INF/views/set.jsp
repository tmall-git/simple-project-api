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
	<form action="/set/doset">
		<table>
			<tr>
				<td>商品</td>
				<td>添加免费数量</td>
			</tr>
			<c:forEach items="${productlist}" var = "product">
				<tr>
					<input type="hidden" name="productCode" value="${product.code}" />
					<td><img src="${product.image}"/>${product.name}</td>
					<td><input type="text" name="productNumber" value = "0" /></td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="2"><button>确定</button></td>
			</tr>
		</table>
	</form>
</body>
</html>