<!DOCTYPE  html>
<!DOCTYPE html>
<html>
<head>
	<title></title>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<style type="text/css">
		h1, h2, a
		{
			display: block;
			text-align: center;
		}
		.links
		{
			width: max-content;
			margin: auto;
		}
		a
		{
			text-decoration: none;
			display: inline-block;
			padding: 1em;
			font-size: 1.5em;
		}
		a:hover
		{
			border: 1px solid black;
			border-radius: 5px;
			color: red;
		}
		.frm
		{
			display: block;
			border: 1px solid black;
			width: 50%;
			margin: auto;
			padding: 2%;
		}
		.frm input
		{
			display: block;
			width: max-content;
			margin: auto;
		}
		input[type="submit"]
		{
			border: 1px solid black;
		}
		input:hover
		{
			cursor: pointer;
		}
	</style>
</head>
<body>
	<div class="frm">
		<form action = "<?php echo $_SERVER['PHP_SELF']; ?>" method = "POST" enctype = "multipart/form-data">
        	<label>Select one or more images to upload: </label>
            <input type="file" name="images[]" id="images" multiple ><br>
            <input type="hidden" id="wd" name="width" value="" >
            <input type="hidden" id="ht" name="height" value="" >
            <input type="submit" value="Upload Image" name="submit">
    	</form>
	</div>
	<script type="text/javascript">
		document.getElementById("wd").setAttribute("value",window.innerWidth);
		document.getElementById("ht").setAttribute("value",window.innerHeight);
	</script>
	<?php
	$success = 0;
    if(isset($_POST['submit']))
    {
        $targetDir = "uploads/";
        $allowedTypes = array('jpg','png','jpeg');
        $src =  array();
        $i = 1;
        foreach($_FILES['images']['name'] as $key=>$val)
        {
            $image_name = $_FILES['images']['name'][$key];
            $tmp_name = $_FILES['images']['tmp_name'][$key];
            $size = $_FILES['images']['size'][$key];
            $type = $_FILES['images']['type'][$key];
            $error = $_FILES['images']['error'][$key];
      
            $fileName = basename($_FILES['images']['name'][$key]);
            $targetFilePath = $targetDir . $fileName;
        
            $fileType = pathinfo($fileName,PATHINFO_EXTENSION);
            $targetFilePath = $targetDir."img".$i.".".$fileType;
            if(in_array($fileType, $allowedTypes))
                if(move_uploaded_file($_FILES['images']['tmp_name'][$key],$targetFilePath))
                {
                	$src[] = "img".$i."_full".".".$fileType;
                	$src[] = "img".$i."_50".".".$fileType;
                	$src[] = "img".$i."_75".".".$fileType;
                    $success = 1;
                }
            $i++;
            $file = fopen("uploads/screen_dimen.txt","w") or die("An error occurred");
            $data = "width=".$_POST['width'].";height=".$_POST['height'];
            fwrite($file, $data);
            fclose($file);
        }
        if($success === 1)
        {
        	echo "<h1>Images uploaded to the server </h1><br>
        	<h2>Select an image to see in other tab:</h2>
        	<div class=\"links\">";
        	$j = 1;
            $i = 1;
        	foreach($src as $val)
        	{
        		$val = "uploads/Processed/".$val;
        		switch ($i) {
        			case 1:
        				$image_name = "Full screen, portrait";
        				break;
        			case 2:
        				$image_name = "3/4th of screen";
        				break;
        		
        			default:
        				$image_name = "50% of screen, portrait";
        				break;
        		}
        		echo "<a href=\"img.php?image_src=$val\" target=\"_blank\">Image $j: $image_name</a>";
                $i++;
                if($i === 4)
                {
                    $i = 1;
                    $j++;
                    echo "<br>";
                }
        	}
        	echo "</div>";
        }
    }
    exec("java ImageResizer 2>&10");
?>
</body>
</html>