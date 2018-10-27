<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h2>上传文件</h2>
<form action="${pageContext.request.contextPath}/manage/product/upload.do" method="post" enctype="multipart/form-data" >

    <input type="file" name="file"/><br/>
    <input type="text" name="submit">
    <input  type="submit" value="提交"/>

</form>
<h1>文件下载</h1>
<a href="/download">下载</a>
</body>
</html>