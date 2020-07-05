package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.rule.AntecedentType;
import jfml.rule.ClauseType;
import jfml.rule.ConsequentType;
import jfml.rule.FuzzyRuleType;
import jfml.rulebase.MamdaniRuleBaseType;
import jfml.term.FuzzyTerm;
import jfml.term.FuzzyTermType;
import jfml.term.Term;

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
public class Main_Mamdani {

	/** The path of the training dataset */
	static String trainPath = "dataset/C2020_TrainData.csv";
	/** The path of the test dataset */
	static String testPath = "dataset/C2020_TestData.csv";

	/** The type of the shape of the membership function */
	static int termType = FuzzyTermType.TYPE_triangularShape;
	/** The number of control point for the shape of the membership function */
	static int paramLength = 3;	// The number of vertices of a triangle
	/** The number of divisions of the partition for the range of attribute value */
	static int H = 2;	//Small and Large
	/** The number of the dimension */
	static int dimension = 12;

	/** The min values of the range for each dimension (from data_range.csv) */
	static float[] min = {0, 0, 0, 0, 0, 0.4f, 0, 0, 0, 0, 0, 0.4f, 0, 0};
	/** The max values of the range for each dimension (from data_range.csv) */
	static float[] max = {22000, 22000, 1, 1, 1, 1, 22000, 22000, 1, 1, 1, 1, 1, 1};

	/** The name of each variable */
	static String[] name = {"DBSN_pre", "DWSN_pre", "DBWR_pre", "DWWR_pre", "DBTMR_pre", "DWTMR_pre",
							"DBSN", "DWSN", "DBWR", "DWWR", "DBTMR", "DWTMR"};

	/** The name of output variable */
	static String outputName = "EBWR";
//	static String outputName = "DBWR_post";

	/** The name of defined homogeneous fuzzy set */
	static String[] termName = {"Small", "Large"};
//	static String[] termName = {"Small", "Medium", "Large"};

	/** The number of iterations for consequent learning */
	static int iteration = 100;
	/** Coefficient of the learning */
	static float eta = 0.5f;

	/**
	 * Main Procedure
	 */
	public static void main(String[] args) {
		double start = System.nanoTime();

		/* 1. Load Dataset */
		ItemSet train_origin = loadCSV(trainPath);
		ItemSet test_origin = loadCSV(testPath);

		/* 2. Normalize Dataset */
		ItemSet train = normalize(train_origin);
		ItemSet test = normalize(test_origin);

		/* 3. Initialize Knowledge Base (Membership Functions) */
		float[][][] controlPoints = initControlPointsWithHomogeneousFuzzyPartition();
		KnowledgeBaseType kb = makeKnowledgeBase(controlPoints);

		/* 4. Initialize Rule Base */
		int[][] ruleIdx = initRuleIdxWithAllCombinationsOfAntecedent();
		MamdaniRuleBaseType rb = makeMamdaniRuleBase(ruleIdx, kb, train);

		/* 5. Make Inference System */
		FuzzyInferenceSystem fs = new FuzzyInferenceSystem();
		fs.setKnowledgeBase(kb);
		fs.addRuleBase(rb);

		/* 6. Inference Dataset */
		float traMSE = inference(fs, train, "mamdani_" + outputName + "_H" + H + "_ite" + iteration + "_train.csv");
		float tstMSE = inference(fs, test, "mamdani_" + outputName + "_H" + H + "_ite" + iteration + "_test.csv");

		System.out.println("Training: MSE = " + traMSE);
		System.out.println("    Test: MSE = " + tstMSE);

		/* 7. Output XML file */
		String xmlFile = "mamdani_" + "FML" + ".xml";
		outputXML(fs, xmlFile);

		double end = System.nanoTime();
		double sec = (end - start)/1000000000.0;
		System.out.println();
		System.out.println("TIME: " + sec);
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
	 * <h1>initControlPointsWithHomogeneousFuzzyPartition</h1>
	 * This function defines the control parameters for the knowledge base. <br/>
	 * The parameters determine the positions of the vertices of triangular membership functions. <br/>
	 * @param dimension : int
	 * @return float[dimension][H][paramLength]
	 */
	public static float[][][] initControlPointsWithHomogeneousFuzzyPartition() {
		// ************************************************************
		/** The objective for optimization knowledge base */
		float[][][] controlPoints = new float[dimension + 1][H][paramLength];

		// Initialize with homogeneous fuzzy partitions.
		// (This sample defines the same fuzzy partition for each attribute.)
		for(int dim_i = 0; dim_i < dimension+1; dim_i++) {
			// H = 2
			controlPoints[dim_i][0] = new float[] {0f, 0f, 1f};	//Term: Small
			controlPoints[dim_i][1] = new float[] {0f, 1f, 1f};	//Term: Large

//			// H = 3
//			controlPoints[dim_i][0] = new float[] {0f, 0f, 0.5f};	//Term: Small
//			controlPoints[dim_i][1] = new float[] {0f, 0.5f, 1f};	//Term: Medium
//			controlPoints[dim_i][1] = new float[] {0.5f, 1f, 1f};	//Term: Large
		}

		return controlPoints;
	}

	/**
	 * <h1>makeKnowledgeBase</h1>
	 * This function implements "KnowledeBaseType.class" based on JFML from the control points for membership functions. <br/>
	 * @param controlPoints : float[dimension][H][paramLength]
	 * @return KnowledgeBaseType
	 */
	public static KnowledgeBaseType makeKnowledgeBase(float[][][] controlPoints) {
		// ************************************************************
		KnowledgeBaseType kb = new KnowledgeBaseType();

		// ************************************************************


		// Implements input variables with defined fuzzy terms (from the control points).
		for(int dim_i = 0; dim_i < dimension; dim_i++) {
			// Variable
			FuzzyVariableType variable = new FuzzyVariableType(name[dim_i], 0, 1);

			// Fuzzy Term
			for(int h = 0; h < H; h++) {
				FuzzyTermType term = new FuzzyTermType(termName[h], termType, controlPoints[dim_i][h]);
				variable.addFuzzyTerm(term);
			}

			kb.addVariable(variable);
		}

		// Implements output variables based on Mamdani fuzzy inference system.
		FuzzyVariableType outputVariable = new FuzzyVariableType(outputName, 0, 1);
		outputVariable.setDefaultValue(0f);
		outputVariable.setAccumulation("MAX");
		outputVariable.setDefuzzifierName("COG");	//Center Of Gravity
		outputVariable.setType("output");
		// Fuzzy Term
		for(int h = 0; h < H; h++) {
			FuzzyTermType term = new FuzzyTermType(termName[h], termType, controlPoints[dimension][h]);
			outputVariable.addFuzzyTerm(term);
		}

		kb.addVariable(outputVariable);

		return kb;
	}

	/**
	 * <h1>initRuleIdxWithAllCombinationsOfAntecedent</h1>
	 * This function defines a set of indexes of antecedent parts. <br/>
	 * User can edit this function or can make new function, for user's wanted set of rules.<br/>
	 * @return int[ruleNum][dimenstion]
	 */
	public static int[][] initRuleIdxWithAllCombinationsOfAntecedent(){
		// ************************************************************
		/** The number of rules */
		int ruleNum = (int)Math.pow(H, dimension);

		int[][] ruleIdx = new int[ruleNum][dimension];

		for(int i = 0; i < dimension; i++) {
			int rule_i = 0;
			int repeatNum = 1;
			int interval = 1;
			int count = 0;
			for(int j = 0; j < i; j++) {
				repeatNum *= H;
			}
			for(int j = i+1; j < dimension; j++) {
				interval *= H;
			}
			for(int j = 0; j < repeatNum; j++) {
				count = 0;
				for(int k = 0; k < H; k++) {
					for(int l = 0; l < interval; l++) {
						ruleIdx[rule_i][i] = count;
						rule_i++;
					}
					count++;
				}
			}
		}

		return ruleIdx;
	}

	public static MamdaniRuleBaseType makeMamdaniRuleBase(int[][] ruleIdx, KnowledgeBaseType kb, ItemSet train) {
		// ************************************************************
		/** The number of rules */
		int ruleNum = ruleIdx.length;
		/** The number of instances */
		int itemSize = train.getItemSize();

		// ************************************************************
		MamdaniRuleBaseType rb = new MamdaniRuleBaseType("rulebase1");

		// Implementation a set of fuzzy if-then rules based on JFML.
		for(int i = 0; i < ruleNum; i++) {
			// Antecedent part
			AntecedentType ant = new AntecedentType();
			for(int dim_i = 0; dim_i < dimension; dim_i++) {
				// Clause (ex: "variable" IS "term")
				KnowledgeBaseVariable variable = kb.getVariable(name[dim_i]);
				Term term = kb.getVariable(name[dim_i]).getTerm(termName[ruleIdx[i][dim_i]]);
				ClauseType clause = new ClauseType(variable, term);
				ant.addClause(clause);
			}

			// Consequent part
			FuzzyRuleType rule = decisionConsequentFuzzySet(train, kb, ant);

			rb.addRule(rule);
		}

		return rb;
	}

	public static FuzzyRuleType decisionConsequentFuzzySet(ItemSet train, KnowledgeBaseType kb, AntecedentType ant) {

		FuzzyInferenceSystem[] fs = new FuzzyInferenceSystem[H];

		FuzzyRuleType[] rules = new FuzzyRuleType[H];
		for(int h = 0; h < H; h++) {
			fs[h] = new FuzzyInferenceSystem();
			fs[h].setKnowledgeBase(kb);

			MamdaniRuleBaseType rb = new MamdaniRuleBaseType();
			rb = new MamdaniRuleBaseType("rulebase_"+termName[h]);

			float initWeight = 1.0f;
			rules[h] = new FuzzyRuleType("rule", "or", "MAX", initWeight);

			ConsequentType con = new ConsequentType();
			KnowledgeBaseVariable outputVariable = kb.getVariable(outputName);
			FuzzyTerm term = (FuzzyTerm)kb.getVariable(outputName).getTerm(termName[h]);
			con.addThenClause(outputVariable, term);

			rules[h].setAntecedent(ant);
			rules[h].setConsequent(con);

			rb.addRule(rules[h]);
			fs[h].addRuleBase(rb);
		}

		// Decision
		float[] mse = new float[H];
		for(int p = 0; p < train.getItemSize(); p++) {
			Item item = train.getItem(p);
			float y = item.getValue(outputName);

			for(int h = 0; h < H; h++) {
				for(int n = 0; n < dimension; n++) {
					fs[h].getVariable(name[n]).setValue(item.getValue(name[n]));
				}
				fs[h].evaluate();

				float predicted = fs[h].getVariable(outputName).getValue();

				mse[h] += (float)Math.pow((predicted - y), 2);
			}
		}
		for(int h = 0; h < H; h++) {
			mse[h] /= (float)train.getItemSize();
		}

		// Decision Minimize MSE
		int minIdx = 0;
		float min = Float.MAX_VALUE;
		for(int h = 0; h < H; h++) {
			if(mse[h] < min) {
				min = mse[h];
				minIdx = h;
			}
		}

		return rules[minIdx];
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





















