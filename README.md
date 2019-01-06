# client-image-processing

## Prerequisites
server, jdk/jre
## Overview
This project allows the client to upload one or more random image file of .png/.jpg/.jpeg extension and then generates three links for each uploaded image which will be shown in portrait mode in the client's screen. First link will show the image in full screen mode, secind will show the image in the 75% area from the top of the screen, and third one will be same as the second one but with 50% screen area.

All paths described below are relative and can/should be changed in the program

## Description
The form (index.php) sends the uploaded image and client's screen's width and height to the server and saves the width and height of the client screen in a file screen_dimen.txt, and uploaded images in "uploads/" directory. Then the php file on the server executes the java app to crop/resize the image according to the task as described in the Overview section. Java program reads the client's screen's width and height and processes the image accordingly. The uploaded image is searched in "uploads/" folder (relative path), the cropped images are saved into "Cropped/" directory which serves as the input image for resizing the image pixel-wise and then saves the final output to the "Processed/" directory. The three links generated for each images point to images stored in "Processed/" directory.
