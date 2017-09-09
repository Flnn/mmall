<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<body>
<h2>Hello World!</h2>

springmvc上传文件
<form name="fileForm" action="/mmall/manage/product/upload" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springMVC上传文件" />
</form>

富文本图片上传
<form name="richtextForm" action="manage/product/richtext_img_upload" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="富文本图片上传文件" />
</form>
</body>
</html>
