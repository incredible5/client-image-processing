import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.RasterFormatException;

public class ImgResizer
{
    static Rectangle clip;
    static int requiredHeight;
    static int requiredWidth;
    public static void main(String[] args)
    {
        File f = new File(PATH); //replace "PATH" to directory in which images are to be searched
        int i = 0;
        for (File file : f.listFiles())
        {
            if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".jpg")))
            {
                i++;
                String fileName = file.getName();
                String inputImagePath = PATH + fileName; //replace "PATH" with the absolute path to the directory (optional, can be relative path too)
                //replace "PATH" in the following three strings with the relative or absolute path where the cropped images wpuld be saved
                //Naming the three output images according to their sizes by inserting a descriptive word (or number) in the original image name
                String croppedOutputImagePath1 = PATH + fileName.substring(0, fileName.lastIndexOf("."))+"_full"+fileName.substring(fileName.lastIndexOf("."));
                String croppedOutputImagePath2 = PATH + fileName.substring(0, fileName.lastIndexOf("."))+"_75"+fileName.substring(fileName.lastIndexOf("."));
                String croppedOutputImagePath3 = PATH + fileName.substring(0, fileName.lastIndexOf("."))+"_50"+fileName.substring(fileName.lastIndexOf("."));
 
                try
                {
                    // resize to a fixed width (not proportional)
                    File fl = new File(PATH + "/screen_dimen.txt"); //reading file containing user's screen's width and height
                    Scanner sc = new Scanner(fl);
                    sc.useDelimiter("\\Z");
                    String info = sc.next();
                    int screen_width = Integer.parseInt(info.substring((info.indexOf("width")+6), info.indexOf("height")-1));
                    int screen_height = Integer.parseInt(info.substring((info.indexOf("height")+7)));

                    BufferedImage originalImage = ImageIO.read(new File(inputImagePath));;//obtaining uploaded Image's width and height
                    int imageWidth = originalImage.getWidth();
                    int imageHeight = originalImage.getHeight();

                    scale(originalImage, screen_width, screen_height);

                    //Setting up required width and height in which image is to be proportioned
                    if(imageWidth < screen_width)
                        requiredWidth = imageWidth;
                    if(imageHeight < screen_height)
                        requiredHeight = imageHeight;

                    int cropWidth = requiredWidth;
                    int cropHeight = requiredHeight;
                    int cropStartX = (imageWidth/2)-(cropWidth/2);
                    int cropStartY = (imageHeight/2)-(cropHeight/2);
                    BufferedImage croppedImage = cropImage(originalImage, cropWidth, cropHeight, cropStartX, cropStartY); //cropping the image to required width and height
                    writeImage(croppedImage, croppedOutputImagePath1, inputImagePath.substring(inputImagePath.lastIndexOf(".")+1)); //writing the image

                    cropStartY = (imageHeight/2) - (int)(cropHeight*0.375);                    
                    croppedImage = cropImage(originalImage, cropWidth, (int)(cropHeight*0.75), cropStartX, cropStartY);
                    writeImage(croppedImage, croppedOutputImagePath2, inputImagePath.substring(inputImagePath.lastIndexOf(".")+1));
                    
                    cropStartY = (imageHeight/2) - (int)(cropHeight*0.25);
                    croppedImage = cropImage(originalImage, cropWidth, (int)(cropHeight*0.5), cropStartX, cropStartY);
                    writeImage(croppedImage, croppedOutputImagePath3, inputImagePath.substring(inputImagePath.lastIndexOf(".")+1));


                    //input paths fot resizing the images will be the path where cropped images are stored (for, in case, cropped image does fit in the required dimension)
                    String inputImagePath1 = croppedOutputImagePath1;
                    String inputImagePath2 = croppedOutputImagePath2;
                    String inputImagePath3 = croppedOutputImagePath3;

                    //replace "PATH" in the following three strings with the relative or absolute path where the resized images wpuld be saved
                    String processedOutputImagePath1 = PATH + fileName.substring(0, fileName.lastIndexOf("."))+"_full"+fileName.substring(fileName.lastIndexOf("."));
                    String processedOutputImagePath2 = PATH + fileName.substring(0, fileName.lastIndexOf("."))+"_75"+fileName.substring(fileName.lastIndexOf("."));
                    String processedOutputImagePath3 = PATH + fileName.substring(0, fileName.lastIndexOf("."))+"_50"+fileName.substring(fileName.lastIndexOf("."));

                    //resizing the image to required width and height if the cropped width/height is not proportional to  the client's width and height

                    resize(inputImagePath1, processedOutputImagePath1, requiredWidth, requiredHeight);

                    resize(inputImagePath2, processedOutputImagePath2, requiredWidth, (int)(requiredHeight*0.75)); //scales image to 3-4th of the image

                    resize(inputImagePath3, processedOutputImagePath3, requiredWidth, (int)(requiredHeight/2)); //scales image to its half
                }
                catch (IOException ex)
                {
                    System.out.println("Error resizing the image.");
                    ex.printStackTrace();
                }
                catch(Exception e)
                {
                    System.out.println("Some error occurred during processing");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
    *Sets the required width and height of the image in the same proportion as the client's screen
    *@param originalImage = BufferedImage object for the original  uploaded image with its path
    *@param Width = width of the client's screen
    *@param Height = height of the client's screen
    */

    public static void scale(BufferedImage originalImage, int Width, int Height) throws IOException
    {
        float sWidth = (float)Width;
        float sHeight = (float)Height;
        float iWidth = (float)originalImage.getWidth();
        float iHeight = (float)originalImage.getHeight();
        float sRatio = precise(sHeight/sWidth);
        float iRatio = precise(iHeight/iWidth);
        if(sRatio > iRatio)
        {
            while(sRatio != iRatio)
            {
                iWidth--;
                iRatio = precise(iHeight/iWidth);
                if((int)iWidth == 1)
                    return;
            }
        }
        else
        {
            while(sRatio != iRatio)
            {
                iHeight--;
                iRatio = precise(iHeight/iWidth);
                if((int)iHeight == 1)
                    return;
            }
        }
        requiredWidth = (int)iWidth;
        requiredHeight = (int)iHeight;
    }
    public static float precise(float value) //for converting float value upto a precison of 2 digits after the decimal
    {
        String val = String.format("%.2f",value);
        return Float.parseFloat(val);
    }

    public static BufferedImage cropImage(BufferedImage img, int cropWidth, int cropHeight, int cropStartX, int cropStartY) throws Exception
    {
        BufferedImage clipped = null;
        Dimension size = new Dimension(cropWidth, cropHeight);
 
        createClip(img, size, cropStartX, cropStartY);
 
        try
        {
            int w = clip.width;
            int h = clip.height;
            
            clipped = img.getSubimage(clip.x, clip.y, w, h);
 
            System.out.println("Image Cropped. New Image Dimension: " + clipped.getWidth() + "w X " + clipped.getHeight() + "h");
        }
        catch (RasterFormatException rfe)
        {
            System.out.println("Raster format error: " + rfe.getMessage());
            return null;
        }
        return clipped;
    }
    /**
    * This method crops an original image to the crop parameters provided.
    *
    * If the crop rectangle lies outside the rectangle (even if partially),
    * adjusts the rectangle to be included within the image area.
    *
    * @param img = Original Image To Be Cropped
    * @param size = Crop area rectangle
    * @param clipX = Starting X-position of crop area rectangle
    * @param clipY = Strating Y-position of crop area rectangle
    * @throws Exception
    */
    private static void createClip(BufferedImage img, Dimension size, int clipX, int clipY) throws Exception
    {
        /**
        * Some times clip area might lie outside the original image,
        * fully or partially. In such cases, this program will adjust
        * the crop area to fit within the original image.
        *
        * isClipAreaAdjusted flas is usded to denote if there was any
        * adjustment made.
        */
        boolean isClipAreaAdjusted = false;
 
        /**Checking for negative X Co-ordinate**/
        if (clipX < 0)
        {
            clipX = 0;
            isClipAreaAdjusted = true;
        }
        /**Checking for negative Y Co-ordinate**/
        if (clipY < 0)
        {
            clipY = 0;
            isClipAreaAdjusted = true;
        }
 
        /**Checking if the clip area lies outside the rectangle**/
        if ((size.width + clipX) <= img.getWidth() && (size.height + clipY) <= img.getHeight())
        {
 
            /**
            * Setting up a clip rectangle when clip area
            * lies within the image.
            */
 
            clip = new Rectangle(size);
            clip.x = clipX;
            clip.y = clipY;
        }
        else
        {
 
            /**
            * Checking if the width of the clip area lies outside the image.
            * If so, making the image width boundary as the clip width.
            */
            if ((size.width + clipX) > img.getWidth())
                size.width = img.getWidth() - clipX;
 
            /**
            * Checking if the height of the clip area lies outside the image.
            * If so, making the image height boundary as the clip height.
            */
            if ((size.height + clipY) > img.getHeight())
                size.height = img.getHeight() - clipY;
 
            /**Setting up the clip are based on our clip area size adjustment**/
            clip = new Rectangle(size);
            clip.x = clipX;
            clip.y = clipY;
 
            isClipAreaAdjusted = true;
 
        }
        if (isClipAreaAdjusted)
            System.out.println("Crop Area Lied Outside The Image." + " Adjusted The Clip Rectangle");
    }
    /**
    * This method writes a buffered image to a file
    *
    * @param img -- > BufferedImage
    * @param fileLocation --> e.g. "C:/testImage.jpg"
    * @param extension --> e.g. "jpg","gif","png"
    */
    public static void writeImage(BufferedImage img, String fileLocation, String extension)
    {
        try
        {
            BufferedImage bi = img;
            File outputfile = new File(fileLocation);
            if(bi != null)
            {
                ImageIO.write(bi, extension, outputfile);
                System.out.println("Image Cropped and saved to "+outputfile);
            }
            else
                System.out.println("Image was null");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional)
     * @param inputImagePath Path of the original image
     * @param outputImagePath Path to save the resized image
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @throws IOException
     */
    public static void resize(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight) throws IOException
    {
        //reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
 
        //creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());
 
        //scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
 
        //extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);
 
        //writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));

        System.out.println("Image resized");
    }
}