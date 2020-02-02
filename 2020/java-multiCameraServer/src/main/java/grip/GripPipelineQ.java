package grip;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Iterator;

import edu.wpi.first.vision.VisionPipeline;

import org.opencv.core.*;
import org.opencv.core.Core.*;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
import org.opencv.objdetect.*;

/**
* GripPipelineQ class.
*
* <p>An OpenCV pipeline generated by GRIP.
*
* @author GRIP
*/
public class GripPipelineQ implements VisionPipeline {

	//Outputs
	private Mat finalMat = new Mat();
	private Mat hsvThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
	double distance = 0;
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * This is the primary method that runs the entire pipeline and updates the outputs.
	 */
	@Override	public void process(Mat source0) {
		long  startTime=System.nanoTime();
		
		// Step HSV_Threshold0:
		Mat hsvThresholdInput = source0;
		double[] hsvThresholdHue = {71.2230215827338, 110.88737201365188};
		double[] hsvThresholdSaturation = {73.38129496402877, 255.0};
		double[] hsvThresholdValue = {123.83093525179855, 255.0};
		hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, hsvThresholdOutput);

		// Step Find_Contours0:
		Mat findContoursInput = hsvThresholdOutput;
		boolean findContoursExternalOnly = true;
		findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

		// Step Filter_Contours0:
		ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
		double filterContoursMinArea = 7.5;
		double filterContoursMinPerimeter = 0;
		double filterContoursMinWidth = 4.0;
		double filterContoursMaxWidth = 1000;
		double filterContoursMinHeight = 16.0;
		double filterContoursMaxHeight = 1000;
		double[] filterContoursSolidity = {0.0, 100.0};
		double filterContoursMaxVertices = 100.0;
		double filterContoursMinVertices = 0;
		double filterContoursMinRatio = 0.2;
		double filterContoursMaxRatio = 1.0;
		filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);


		//Step put some stuff on source0
		int xResolution = 320;
		int yResolution = 256;
		int cameraShift = 0;
		distance = 0;
		Point infoTextLocation= new Point((int)(0.035*xResolution),12);
		Scalar infoTextColor= new Scalar(0,255,255);
		Scalar targetWarningColor= new Scalar(20,20,255);
		Scalar white = new Scalar(255,255,255);		
		int targetCount = filterContoursOutput.size();
		int minTargets = 1;
		ArrayList<MatOfPoint> mainTargets = new ArrayList<MatOfPoint>();
		if (targetCount > 1) {
			mainTargets.add(filterContoursOutput.get(0));
			mainTargets.add(filterContoursOutput.get(1));
			/***********************************************/
			//Do all the post calculations
			int count=0;
			int frameCount = filterContoursOutput.size();
			List<MatOfPoint> hulls  = new ArrayList<MatOfPoint>();
			Iterator<MatOfPoint> each = mainTargets.iterator();
			double targetAreas [] = {0.0,0.0};
			double targetAspectRatio [] = {0.0,0.0};
			double targetX [] = {0.0,0.0};
			double targetY [] = {0.0,0.0};
			double centroidY [] = {0.0,0.0};
			double targetHeights [] = {0.0,0.0};
			double targetWidths [] = {0.0,0.0};
			double targetBleedover [] = {0.0,0.0};
			Scalar targetColor = new Scalar (0, 255, 255);
			while (each.hasNext()) {
				MatOfPoint wrapper = each.next();
				Moments moments = Imgproc.moments(wrapper);
				
				Point centroid = new Point();
				centroid.x = -cameraShift + (moments.get_m10() / moments.get_m00());
				centroid.y = moments.get_m01() / moments.get_m00();
				Rect rectangle = Imgproc.boundingRect(wrapper);
				
				//Countour area is not robust to poking it with your finger
				//double area = Imgproc.contourArea(wrapper);
				//Convex Hull or Rectangle area is robust to poking it with your finger 
				final MatOfInt hull = new MatOfInt();
				Imgproc.convexHull(wrapper, hull);
				MatOfPoint mopHull = new MatOfPoint();
				mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
				for (int j = 0; j < hull.size().height; j++) {
					int index = (int)hull.get(j, 0)[0];
					double[] point = new double[] { wrapper.get(index, 0)[0], wrapper.get(index, 0)[1]};
					mopHull.put(j, 0, point);
				}
				double area = Imgproc.contourArea(mopHull);
				//areas are a percentage of total image area
				targetAreas[count]= 100.0*area/(xResolution*yResolution);
				targetAspectRatio[count] = (double) rectangle.height / (double) rectangle.width;
				//X and Y are scaled from -1 to 1 in each direction, so distances in these coordinates
				//need to be divided by two to get percentages
				targetX[count]=(-1.0+ 2.0*centroid.x/(double)xResolution);
				targetY[count]=(-1.0+ 2.0*centroid.y/(double)yResolution);
				centroidY[count]=centroid.y;
				
				//The farther away you get, the worse the heights are
				targetBleedover[count]= rectangle.area()/area;
				//The height of the rectangle is in percentage of the y resolution
				targetHeights[count] = (rectangle.height)/((double)yResolution);
				targetWidths[count] = (rectangle.width);
				
				//Draw the rectangles, they are good to guide the eye
				Imgproc.rectangle(source0, rectangle.tl(), rectangle.br(), targetColor, 1);
				hulls.add(count,mopHull);
				count++;
			}
			//Imgproc.putText(source0, String.format("X: %2.3f X2:%2.3f",targetX[0], targetX[1]+1-1), new Point((int)(0.035*xResolution),150) ,1, 0.9, white, 1);
			//Imgproc.putText(source0, String.format("Y: %2.3f Y2:%2.3f",targetY[0], targetY[1]+1-1), new Point((int)(0.55*xResolution),130) ,1, 0.9, white, 1);
			//Imgproc.putText(source0, String.format("DX: %2.3f DY:%2.3f",targetX[0]-targetX[1], targetY[0]-targetY[1]), new Point((int)(0.55*xResolution),230) ,1, 0.9, targetWarningColor, 1);
			// Easy to fit the distace as ~ 1/target seperation
			distance = -0.2+ 10.8/Math.abs(targetX[0]-targetX[1]);
		}
		//Black box for top background
		Imgproc.rectangle(source0, new Point(0,0), new Point(xResolution,0.06*yResolution), new Scalar(0,0,0), -1);
		Imgproc.line(source0, new Point((int)(0.4*xResolution),0.07*yResolution), new Point((int)(0.4*xResolution),0.9*yResolution), targetWarningColor, 1);
		Imgproc.line(source0, new Point((int)(0.6*xResolution),0.07*yResolution), new Point((int)(0.6*xResolution),0.9*yResolution), targetWarningColor, 1);
		// Slow it down - limit the bandwidth a bit.  3B+ handles this pipeline very quickly
		try
		{
    		Thread.sleep(25);	
		}
		catch(InterruptedException ex)
		{
    		Thread.currentThread().interrupt();
		}
		long endTime = System.nanoTime();
		Imgproc.putText(source0, String.format("FPS: %03d   Bogeys:%2d      Distance:%2.1f",(int)(Math.pow(10.0,9)/(double)(endTime-startTime)),
				filterContoursOutput.size(), distance), infoTextLocation,1, 0.9, infoTextColor, 1);
		if (filterContoursOutput.size()>1){
			
		}
		
		finalMat=source0;
	}

	/**
	 * This method is a generated getter for the output of a HSV_Threshold.
	 * @return Mat output from HSV_Threshold.
	 */
	public Mat gripImage() {
		return finalMat;
	}
	public double getDistance(){
		return distance;
	}
	public Mat hsvThresholdOutput() {
		return hsvThresholdOutput;
	}

	/**
	 * This method is a generated getter for the output of a Find_Contours.
	 * @return ArrayList<MatOfPoint> output from Find_Contours.
	 */
	public ArrayList<MatOfPoint> findContoursOutput() {
		return findContoursOutput;
	}

	/**
	 * This method is a generated getter for the output of a Filter_Contours.
	 * @return ArrayList<MatOfPoint> output from Filter_Contours.
	 */
	public ArrayList<MatOfPoint> filterContoursOutput() {
		return filterContoursOutput;
	}


	/**
	 * Segment an image based on hue, saturation, and value ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param val The min and max value
	 * @param output The image in which to store the output.
	 */
	private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val,
	    Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
		Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
			new Scalar(hue[1], sat[1], val[1]), out);
	}

	/**
	 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
	 * @param input The image on which to perform the Distance Transform.
	 * @param type The Transform.
	 * @param maskSize the size of the mask.
	 * @param output The image in which to store the output.
	 */
	private void findContours(Mat input, boolean externalOnly,
		List<MatOfPoint> contours) {
		Mat hierarchy = new Mat();
		contours.clear();
		int mode;
		if (externalOnly) {
			mode = Imgproc.RETR_EXTERNAL;
		}
		else {
			mode = Imgproc.RETR_LIST;
		}
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(input, contours, hierarchy, mode, method);
	}


	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param inputContours is the input list of contours
	 * @param output is the the output list of contours
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum perimeter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param maxWidth maximum width
	 * @param minHeight minimum height
	 * @param maxHeight maximimum height
	 * @param Solidity the minimum and maximum solidity of a contour
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param maxVertexCount maximum vertex Count
	 * @param minRatio minimum ratio of width to height
	 * @param maxRatio maximum ratio of width to height
	 */
	private void filterContours(List<MatOfPoint> inputContours, double minArea,
		double minPerimeter, double minWidth, double maxWidth, double minHeight, double
		maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
		minRatio, double maxRatio, List<MatOfPoint> output) {
		final MatOfInt hull = new MatOfInt();
		output.clear();
		//operation
		for (int i = 0; i < inputContours.size(); i++) {
			final MatOfPoint contour = inputContours.get(i);
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
			if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int)hull.get(j, 0)[0];
				double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
			final double ratio = bb.width / (double)bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.add(contour);
		}
	}




}
