<html>
<body>
<h2>Hello World!</h2>

<h3>springmvc file upload testing</h3>
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="uploadFile" />
    <input type="submit" value="file upload" />
</form>

<h3>springmvc richtext file upload testing</h3>
<form name="form2" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="uploadFile" />
    <input type="submit" value="richtext file upload" />
</form>
</body>
</html>
