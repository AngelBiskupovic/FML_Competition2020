package main_cilabo_ver2_triangle;

import java.util.ArrayList;

import jfml.FuzzyInferenceSystem;
import main.ItemSet;
import main_cilabo_ver2_triangle.ga.Optimization_KB;
import main_cilabo_ver2_triangle.ga.Optimization_RB;
import main_cilabo_ver2_triangle.rnd.MersenneTwisterFast;


public class CILAB_Main {

	public static int seed = 2020;
	public static MersenneTwisterFast rnd = new MersenneTwisterFast(seed);
	public static int evaSize = 500;
	public static int generalFrameRepeatTime = 3;

	public static FuzzyInferenceSystem generalFramework(ItemSet train) {
		// ************************************************************
		System.out.println("------------------------");
		System.out.println("Genetic Algorithm Start");
		System.out.println("------------------------");

		// ************************************************************
		/* 1. Initialization Knowledge Base & Rule Base*/
		System.out.print("1. Initialization Knowledge Base & Rule Base");
		float[][][] KBbest = initKB();
		int[][] RBbest = null;
		System.out.println("[done]");

		// ************************************************************
		/* 2. Prepare evaluation dataset Deva */
		System.out.print("2. Prepare evaluation dataset ");
		ItemSet[] divided = samplingWithout(train);
		ItemSet Dtra = divided[0];
		ItemSet Deva = divided[1];
//		ItemSet Dtra = train;
//		ItemSet Deva = train;
		System.out.println("[done]");

		// ************************************************************
		/* 3. GA Loop */
		for(int i = 0; i < generalFrameRepeatTime; i++) {
			System.out.println();
			System.out.println("************************");
			System.out.println("3. General Framwork Loop: " + (i+1));

			System.out.println();
			System.out.println("4. Rule Base Optimization Phase");
			RBbest = Optimization_RB.RuleBase_Opt(i, KBbest, RBbest, Dtra, Deva);
			System.out.println();

			System.out.println();
			System.out.println("5. Knowledge Base Optimization Phase");
			KBbest = Optimization_KB.KnowledgeBase_Opt(i, KBbest, RBbest, Dtra, Deva);
			System.out.println();

			System.out.println("************************");
		}
		System.out.println("6. Loop End");

		FuzzyInferenceSystem fs = TSK.makeFS(KBbest, RBbest, train);

		return fs;
	}

	public static float[][][] initKB() {
		// ************************************************************
		float[][][] controlPoints = new float[Main.dimension][][];

		for(int dim = 0; dim < Main.dimension; dim++) {
			controlPoints[dim] = Main.params;
		}

		// ************************************************************
		return controlPoints;
	}

	/**
	 *
	 * @param train : ItemSet
	 * @return ItemSet[2] : 0:Dtra, 1:Deva
	 */
	public static ItemSet[] samplingWithout(ItemSet train) {
		ItemSet[] divided = new ItemSet[2];

		ArrayList<Integer> list = new ArrayList<>();
		for(int p = 0; p < train.getItemSize(); p++) {
			list.add(p);
		}

		// Deva
		divided[1] = new ItemSet();
		for(int p = 0; p < evaSize; p++) {
			int index = list.remove(rnd.nextInt(list.size()));
			divided[1].addItem(train.getItem(index));
		}

		// Dtra
		divided[0] = new ItemSet();
		for(int p = 0; p < list.size(); p++) {
			divided[0].addItem(train.getItem( list.get(p) ));
		}

		return divided;
	}




}









