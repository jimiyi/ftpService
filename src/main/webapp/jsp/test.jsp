<%--
  Created by IntelliJ IDEA.
  User: BG309781
  Date: 2017/8/22
  Time: 16:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Test</title>
</head>
<body>
<form name="serForm" action="/ftpService/uploadFile" method="post"  enctype="multipart/form-data">
    <h1>采用流的方式上传文件</h1>
    <input type="file" name="file">
    <input name="storePath" value="test">
    <input type="submit" value="upload"/>
</form>
<a href="<%=request.getContextPath() %>/downloadFile?path=yiwei&fileName=8月19日考勤异常.png">下载文件</a>
</body>
</html>
