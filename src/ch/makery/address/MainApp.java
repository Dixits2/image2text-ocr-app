package ch.makery.address;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Light.Point;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MainApp extends Application {

	public Stage primaryStage;
    public BorderPane rootLayout;
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    double uniX = 0;
    double uniY = 0;
    double uniWidth = 0;
    double uniHeight = 0;
    
    
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TextCap");
        
        ImageView display = new ImageView();
        Point anchor = new Point();
        javafx.scene.shape.Rectangle selection = new javafx.scene.shape.Rectangle();
        javafx.scene.shape.Rectangle selectionFill = new javafx.scene.shape.Rectangle();
        
        
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            display.setOnMouseEntered(event -> {
            	display.getScene().setCursor(Cursor.CROSSHAIR);
            });
            
            display.setOnMouseExited(event -> {
            	display.getScene().setCursor(Cursor.DEFAULT);
            });
            
            display.setOnMousePressed(event -> {
            	int childCount = rootLayout.getChildren().size();
//            	System.out.println(rootLayout.getChildren());
            	if(childCount != 1) {
            		rootLayout.getChildren().remove(1);
            		rootLayout.getChildren().remove(1);
            	}
            	display.getScene().setCursor(Cursor.CROSSHAIR);
            	uniX = event.getX();
            	
                anchor.setX(event.getX());
                anchor.setY(event.getY());
                selection.setX(event.getX());
                selection.setY(event.getY());
                selection.setWidth(1);
                selection.setHeight(1);
                selection.setFill(null); // transparent
                selection.setStroke(Color.BLACK); // border
                selection.getStrokeDashArray().addAll(2d, 8d);
                selectionFill.setX(event.getX());
                selectionFill.setY(event.getY());
                selectionFill.setWidth(1);
                selectionFill.setHeight(1);
                selectionFill.setFill(Color.BLACK); // transparent
                selectionFill.setOpacity(0.1);
                selectionFill.setStroke(null); // border
                rootLayout.getChildren().addAll(selection, selectionFill);
            });
            
             
            display.setOnMouseDragged(event -> {
            	display.getScene().setCursor(Cursor.CROSSHAIR);
                selection.setWidth(Math.abs(event.getX() - anchor.getX()));
                selection.setHeight(Math.abs(event.getY() - anchor.getY()));
                selection.setX(Math.min(anchor.getX(), event.getX()));
                selection.setY(Math.min(anchor.getY(), event.getY()));
                uniX = Math.min(anchor.getX(), event.getX());
                uniY = Math.min(anchor.getY(), event.getY());
                uniWidth = Math.abs(event.getX() - anchor.getX());
                uniHeight = Math.abs(event.getY() - anchor.getY());
                selectionFill.setWidth(Math.abs(event.getX() - anchor.getX()));
                selectionFill.setHeight(Math.abs(event.getY() - anchor.getY()));
                selectionFill.setX(Math.min(anchor.getX(), event.getX()));
                selectionFill.setY(Math.min(anchor.getY(), event.getY()));
            });
            
            display.setOnMouseReleased(event -> {
            	BufferedImage image = screenshotHalf(display, uniX, uniY, uniWidth, uniHeight);
            	System.out.println(runPy());
            });
            
            // Show the scene containing the root layout.
            
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            
            Scene scene = new Scene(rootLayout);
            scene.setFill(Color.TRANSPARENT);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());
            primaryStage.setScene(scene);
            primaryStage.show();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/View.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();
            
            screenshot(display);
            display.setX(0);
            display.setY(0);
            display.setFitWidth(primaryStage.getWidth());
            display.setPreserveRatio(true);
            
            personOverview.getChildren().addAll(display);
            display.setOpacity(0.01);
            rootLayout.setCenter(personOverview);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
    
    public void screenshot(ImageView display) {
    	try {
			Robot robot = new Robot();
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(screenRect);
			Image finalImg = SwingFXUtils.toFXImage(image, null);
			display.setImage(finalImg);
			
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public String runPy() {
    	String result = null;
    	
    	 try{
             String tessPath = "C:\\Users\\techn\\eclipse-workspace\\TextCap5\\src\\ch\\makery\\address\\Tesseract-OCR\\tesseract.exe";
             String imagePath = "C:\\Users\\techn\\eclipse-workspace\\TextCap5\\image.jpg";
             String txtPath = "C:\\Users\\techn\\eclipse-workspace\\TextCap5\\src\\ch\\makery\\address\\file";
             ProcessBuilder pb = new ProcessBuilder(tessPath, imagePath, txtPath);
             Process process = pb.start();
             int errCode = process.waitFor();
             System.out.println(errCode);
             String output = output(process.getInputStream()); 
         } catch(Exception e) {
    		 System.out.println(e);
    	 }
    	 try {
    		 
			String content = readFile("C:\\Users\\techn\\eclipse-workspace\\TextCap5\\src\\ch\\makery\\address\\file.txt", StandardCharsets.UTF_8);
			result = content;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    	 
    	return result;
    	
    }
    
    static String readFile(String path, Charset encoding) 
    		  throws IOException 
    		{
    		  byte[] encoded = Files.readAllBytes(Paths.get(path));
    		  return new String(encoded, encoding);
    		}
    
    private static String output(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }
    
    
    public void screenshot(ImageView display, double x, double y, double width, double height) {
    	try {
			Robot robot = new Robot();
			Rectangle screenRect = new Rectangle((int)x, (int)y, (int)width, (int)height);
			BufferedImage image = robot.createScreenCapture(screenRect);
			Image finalImg = SwingFXUtils.toFXImage(image, null);
			display.setImage(finalImg);
			File outputfile = new File("image.jpg");
        	try {
				ImageIO.write(image, "jpg", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public BufferedImage screenshotHalf(ImageView display) {
    	BufferedImage ogImage = null;
    	try {
			Robot robot = new Robot();
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(screenRect);
        	File outputfile = new File("image.jpg");
        	try {
				ImageIO.write(image, "jpg", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ogImage = image;
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ogImage;
    }
    
    public BufferedImage screenshotHalf(ImageView display, double x, double y, double width, double height) {
    	BufferedImage ogImage = null;
    	try {
			Robot robot = new Robot();
			Rectangle screenRect = new Rectangle((int)x, (int)y, (int)width, (int)height);
			BufferedImage image = robot.createScreenCapture(screenRect);
			ogImage = image;
			File outputfile = new File("image.jpg");
        	try {
				ImageIO.write(image, "jpg", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ogImage;
    }
//    private void saveGridImage(File output) throws IOException {
//        output.delete();
//
//        final String formatName = "png";
//
//        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext();) {
//           ImageWriter writer = iw.next();
//           ImageWriteParam writeParam = writer.getDefaultWriteParam();
//           ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
//           IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
//           if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
//              continue;
//           }
//
//           setDPI(metadata);
//
//           final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
//           try {
//              writer.setOutput(stream);
//              writer.write(metadata, new IIOImage(gridImage, null, metadata), writeParam);
//           } finally {
//              stream.close();
//           }
//           break;
//        }
//     }
//
//     private void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {
//
//        // for PMG, it's dots per millimeter
//        double dotsPerMilli = 1.0 * DPI / 10 / INCH_2_CM;
//
//        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
//        horiz.setAttribute("value", Double.toString(dotsPerMilli));
//
//        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
//        vert.setAttribute("value", Double.toString(dotsPerMilli));
//
//        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
//        dim.appendChild(horiz);
//        dim.appendChild(vert);
//
//        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
//        root.appendChild(dim);
//
//        metadata.mergeTree("javax_imageio_1.0", root);
//     }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}