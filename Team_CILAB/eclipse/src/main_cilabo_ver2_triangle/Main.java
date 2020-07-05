package main_cilabo_ver2_triangle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import jfml.term.FuzzyTermType;
import main.Item;
import main.ItemSet;

/**
 * This file is a sample source code for Java Fuzzy Markup Language (JFML) library. <br/>
 * This main procedure is structured as follows: <br/>
 * 1. Load training/test datasets. <br/>
 * 2. Normalize the datasets. <br/>
 * 3. Initialization the knowledge base based on JFML. <br/>
 * 4. Initialization the rule base based on JFML. <br/>
 * 5. Leaning the consequent part of the rule base. <br/>
 * 6. Evaluation the inference systems based on inference the training/test datasets. <br/>
 * 7. Output the inference systems as XML files with JFML.
 *
 */
public class Main {

	/** The path of the training dataset */
	public static String trainPath = "dataset/C2020_TrainData.csv";
	/** The path of the test dataset */
	public static String testPath = "dataset/C2020_TestData.csv";

	/** The type of the shape of the membership function */
	public static int termType = FuzzyTermType.TYPE_triangularShape;
	/** The number of control point for the shape of the membership function */
	public static int paramLength = 3;	// The number of vertices of a triangle
	/** The number of the dimension */
	public static int dimension = 12;

	/** The min values of the range for each dimension (from data_range.csv) */
	public static float[] min = {0, 0, 0, 0, 0, 0.4f, 0, 0, 0, 0, 0, 0.4f, 0, 0};
	/** The max values of the range for each dimension (from data_range.csv) */
	public static float[] max = {22000, 22000, 1, 1, 1, 1, 22000, 22000, 1, 1, 1, 1, 1, 1};

	/** The name of each variable */
	public static String[] name = {"DBSN_pre", "DWSN_pre", "DBWR_pre", "DWWR_pre", "DBTMR_pre", "DWTMR_pre",
							"DBSN", "DWSN", "DBWR", "DWWR", "DBTMR", "DWTMR"};

	/** The name of output variable */
//	public static String outputName = "EBWR";
	public static String outputName = "DBWR_next";

	/** The number of divisions of the partition for the range of attribute value */
	public static int H = 5;

	/** The name of defined homogeneous fuzzy set */
//	public static String[] termName = {"Small", "Large"};
//	public static String[] termName = {"Small", "Medium", "Large"};
	public static String[] termName = {"VerySmall", "Small", "Medium", "Large", "VeryLarge", "Don'tCare"};

	/** Initial control points for fuzzy sets */
	public static float[][] params = {
								{0.00f, 0.00f, 0.25f},	// VerySmall
								{0.00f, 0.25f, 0.50f},	// Small
								{0.25f, 0.50f, 0.75f},	// Medium
								{0.50f, 0.75f, 1.00f},	// Large
								{0.75f, 1.00f, 1.00f},	// VeryLarge
							};

	/** The number of iterations for consequent learning */
	public static int iteration = 100;
	/** Coefficient of the learning */
	public static float eta = 0.5f;
	/** Initial consequent value */
	public static float initConsequent = 0.5f;

	/** The directory name of place storing results */
	public static String dirName;

	/**
	 * Main Procedure
	 */
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");
		String id = outputName + "_" + format.format(calendar.getTime());
		String sep = File.separator;
		dirName = "result" + sep + id;
		File newdir = new File(dirName);
		newdir.mkdirs();

		/* 1. Load Dataset */
		ItemSet train_origin = loadCSV(trainPath);
		ItemSet test_origin = loadCSV(testPath);

		/* 2. Normalize Dataset */
		ItemSet train = normalize(train_origin);
		ItemSet test = normalize(test_origin);

		/* 3-5. Optimizing Fuzzy Inference System based on Genetic Algorithm */
		FuzzyInferenceSystem fs = CILAB_Main.generalFramework(train);

		/* 6. Inference Dataset */
		float traMSE = inference(fs, train, dirName + sep + outputName + "_train.csv");
		float tstMSE = inference(fs, test, dirName + sep + outputName + "_test.csv");

		System.out.println("Training: MSE = " + traMSE);
		System.out.println("    Test: MSE = " + tstMSE);

		/* 7. Output XML file */
		String xmlFile = outputName + "_cilabo_FML" + ".xml";
		outputXML(fs, xmlFile);
	}

	/**
	 * <h1>loadCSV</h1>
	 * @param fileName : String
	 * @return ItemSet
	 */
	public static ItemSet loadCSV(String fileName) {
		ItemSet itemSet = new ItemSet();

		try {
			Path path = Paths.get(fileName);
			List<String> lines = Files.readAllLines(path);

			//Header
			lines.remove(0);

			//Data
			for(String line : lines) {
				String[] split = line.split(",");

				float[] array = new float[split.length - 1];
				for(int i = 0; i < array.length; i++) {
					array[i] = Float.parseFloat(split[i+1]);
				}

				Item item = new Item(array);
				itemSet.addItem(item);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return itemSet;
	}

	/**
	 * <h1>normalize</h1>
	 * This function normalizes the dataset (origin) based on the ranges from "data_range.csv" file. <br/>
	 * @param origin : ItemSet
	 * @return ItemSet : Normalized dataset
	 */
	public static ItemSet normalize(ItemSet origin) {
		ItemSet newItemSet = new ItemSet();

		for(int p = 0; p < origin.getItemSize(); p++) {
			float[] newList = new float[dimension + 2];
			for(int i = 0; i < dimension; i++) {
				float ma = max[i];
				float mi = min[i];

				newList[i] = (origin.getItem(p).getValue(name[i]) - mi) / (ma - mi);
			}
			newList[dimension + 0] = origin.getItem(p).getConsequent(0);
			newList[dimension + 1] = origin.getItem(p).getConsequent(1);

			Item newItem = new Item(newList);
			newItemSet.addItem(newItem);
		}
		return newItemSet;
	}

	/**
	 * <h1>inference</h1>
	 * This function evaluate the given fuzzy inference system with a given dataset. <br/>
	 * This function returns the MSE value of the fuzzy inference system. <br/>
	 * This also output the inference track for each line of the given dataset. <br/>
	 * @param fs : FuzzyInferenceSystem : learned fuzzy inference system
	 * @param itemSet : ItemSet : the dataset wanted to track.
	 * @param fileName : String : the name of an output csv file.
	 * @return float : The MSE value of given fs.
	 */
	public static float inference(FuzzyInferenceSystem fs, ItemSet itemSet, String fileName) {
		// ************************************************************
		/* 1. Inference tracking */
		float[] y = new float[itemSet.getItemSize()];
		float[] predicted = new float[itemSet.getItemSize()];
		for(int p = 0; p < itemSet.getItemSize(); p++) {
			Item item = itemSet.getItem(p);
			for(int i = 0; i < dimension; i++) {
				fs.getVariable(name[i]).setValue(item.getValue(name[i]));
			}
			fs.evaluate();

			predicted[p] = fs.getVariable(outputName).getValue();
			y[p] = item.getValue(outputName);
		}

		// ************************************************************
		/* 2. Calculation of MSE */
		float MSE = 0f;
		for(int p = 0; p < itemSet.getItemSize(); p++) {
			MSE += (float)Math.pow((predicted[p] - y[p]), 2);
		}
		MSE /= (float)itemSet.getItemSize();

		// ************************************************************
		/* 3. Output results as CSV file */
		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter( new BufferedWriter(fw) );

			// Inference tracking
			pw.println("predicted,y");
			for(int p = 0; p < predicted.length; p++) {
				pw.println(String.valueOf(predicted[p]) + "," + String.valueOf(y[p]));
			}
			pw.close();
	    }
		catch (IOException ex){
			ex.printStackTrace();
	    }

		return MSE;
	}

	/**
	 * <h1>outputXML</h1>
	 * This function output "FuzzyInferenceSystem.class" as an XML file. <br/>
	 * @param fs : FuzzyInferenceSystem
	 * @param fileName : String
	 */
	public static void outputXML(FuzzyInferenceSystem fs, String fileName) {
		// ************************************************************
		File path = new File(fileName);
		JFML.writeFSTtoXML(fs, path);
	}


}


