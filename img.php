<!DOCTYPE html>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title></title>
</head>
<body>
	<style type="text/css">
		html, body
		{
			margin: 0;
			padding: 0;
			width: 100%;
			height: 100%;
		}
		.con
		{
			margin: 0;
			padding: 0;
			min-width: 100%;
			min-height: 100%;
			overflow: hidden;
		}
		img
		{
			max-width: 100%;
			max-height: 100%;
			display: block;
			margin: auto;
			vertical-align: middle;
			object-fit: contain;
		}
	</style>
	<div class="con">
		<img src="<?php echo $_GET['image_src']; ?>" />
	</div>
</body>
</html>