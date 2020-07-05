package main_cilabo_ver1_gaussian;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import main.Item;
import main.ItemSet;

public class Results {

	public static String outputName = "EBWR";
//	public static String outputName = "DBWR_next";

	public static int generalFrameRepeatTime = 3;

	public static int N_RB = 20;
	public static int N_KB = 20;


	public static void main(String[] args) {
		/* 1. Load Dataset */
		ItemSet train_origin = Main.loadCSV(Main.trainPath);
		ItemSet test_origin = Main.loadCSV(Main.testPath);

		/* 2. Normalize Dataset */
		ItemSet train = Main.normalize(train_origin);
		ItemSet test = Main.normalize(test_origin);

		/* 3. Trace */
		String sep = File.separator;
		ArrayList<FuzzyInferenceSystem> RBtrace = new ArrayList<>();
		ArrayList<FuzzyInferenceSystem> KBtrace = new ArrayList<>();
		for(int general = 0; general < generalFrameRepeatTime; general++) {
			// Rule Base Optimization Phase
			for(int rb = 0; rb < N_RB; rb++) {
				System.out.print(".");
				String time = String.format("%02d", 2*general + 1);
				String dirName = "result"
								+ sep + outputName
								+ sep + time + "_RB"
								+ sep + String.format("%02d", rb);
				//Load XML file
				File fml = new File(dirName + sep + "pop_00.xml");
				FuzzyInferenceSystem fs = JFML.load(fml);
				RBtrace.add(fs);
			}

			// Knowledge Base Optimization Phase
			for(int kb = 0; kb < N_KB; kb++) {
				System.out.print(".");
				String time = String.format("%02d", 2*general + 2);
				String dirName = "result"
								+ sep + outputName
								+ sep + time + "_KB"
								+ sep + String.format("%02d", kb);
				//Load XML file
				File fml = new File(dirName + sep + "pop_00.xml");
				FuzzyInferenceSystem fs = JFML.load(fml);
				KBtrace.add(fs);

			}
		}
		System.out.println();

		/* 4. Inference */
		ArrayList<Float> trainTrace = new ArrayList<>();
		ArrayList<Float> testTrace = new ArrayList<>();
		for(int general = 0; general < generalFrameRepeatTime; general++) {
			for(int rb = 0; rb < N_RB; rb++) {
				System.out.print(".");
				FuzzyInferenceSystem fs = RBtrace.remove(0);
				float mseTra = inference(fs, train);
				trainTrace.add(mseTra);
				float mseTst = inference(fs, test);
				testTrace.add(mseTst);
			}
			for(int kb = 0; kb < N_KB; kb++) {
				System.out.print(".");
				FuzzyInferenceSystem fs = KBtrace.remove(0);
				float mseTra = inference(fs, train);
				trainTrace.add(mseTra);
				float mseTst = inference(fs, test);
				testTrace.add(mseTst);
			}
		}

		/* 5. Output CSV */
		ArrayList<String> strs = new ArrayList<>();
		String str = "";

		// Header
		str = "generation,phase,train,test";
		strs.add(str);

		// Data
		int generation = 0;
		for(int general = 0; general < generalFrameRepeatTime; general++) {
			for(int rb = 0; rb < N_RB; rb++) {
				str = String.valueOf(generation++);
				str += "," + "RB";
				str += "," + trainTrace.remove(0);
				str += "," + testTrace.remove(0);
				strs.add(str);
			}
			for(int kb = 0; kb < N_KB; kb++) {
				str = String.valueOf(generation++);
				str += "," + "KB";
				str += "," + trainTrace.remove(0);
				str += "," + testTrace.remove(0);
				strs.add(str);
			}
		}
		String[] array = (String[]) strs.toArray(new String[0]);
		String fileName = "result" + sep + outputName + sep + "trace.csv";
		write(fileName, array);

	}


	public static float inference(FuzzyInferenceSystem fs, ItemSet itemSet) {
		// ************************************************************
		/* 1. Inference tracking */
		float[] y = new float[itemSet.getItemSize()];
		float[] predicted = new float[itemSet.getItemSize()];
		for(int p = 0; p < itemSet.getItemSize(); p++) {
			Item item = itemSet.getItem(p);
			for(int i = 0; i < item.getDimension(); i++) {
				fs.getVariable(Main.name[i]).setValue(item.getValue(Main.name[i]));
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

		return MSE;
	}

	public static void write(String fileName, String[] array){

		try {
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter( new BufferedWriter(fw) );
			for(int i=0; i<array.length; i++){
				 pw.println(array[i]);
			}
			pw.close();
	    }
		catch (IOException ex){
			ex.printStackTrace();
	    }
	}

}














