package main_cilabo_ver1_gaussian.ga;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.term.FuzzyTermType;
import main.Item;
import main.ItemSet;
import main_cilabo_ver1_gaussian.CILAB_Main;
import main_cilabo_ver1_gaussian.Main;
import main_cilabo_ver1_gaussian.TSK;

public class Optimization_KB {

	/** Population size in the knowledge-base optimization phase */
	static int N_KB = 20;
	/** The number of generation in the knowledge-base optimization phase */
	static int Generation_KB = 20;

	/** Probability of perturbation */
	static double Pp = 0.8;
	/** Probability of crossover */
	static double Px = 0.9;
	/** Probability of mutation */
	static double Pm = 0.1;

	public static float[][][] KnowledgeBase_Opt(int now, float[][][] KBbest, int[][] RBbest, ItemSet Dtra, ItemSet Deva) {
		// ************************************************************
		String sep = File.separator;
		String dirXML = Main.dirName + sep + String.format("%02d", 2*now+2) + "_KB";
		(new File(dirXML)).mkdirs();

		// ************************************************************
		/* 1. Population Initialization */
		System.out.print("5.1. Population Initialization ");
		ArrayList<Individual_KB> population = new ArrayList<>();
		initialize(population, KBbest);
		System.out.println("[done]");

		/* 2. Initial Population Evaluation */
		System.out.println("5.2. Initial Population Evaluation");
		System.out.print("Evaluation: ");
		for(int pop = 0; pop < population.size(); pop++) {
			System.out.print(pop+1);
			evaluate(population.get(pop), RBbest, Dtra, Deva);
		}
		System.out.println(" [done]");

		/* 2.5. Write Initial Population as XML files */
		System.out.print("Output Initial Population ");
		String dirGeneration = dirXML + sep + "00";
		outputXML(dirGeneration, population);
		System.out.println(" [done]");

		// ************************************************************
		/* 3. Optimization Loop */
		for(int generation = 1; generation <= Generation_KB; generation++) {
			System.out.println();
			System.out.println("************************");
			System.out.println("5.3. Knowledge Base GA");
			System.out.println("Generation: " + generation);
			System.out.print("Evaluation: ");

			ArrayList<Individual_KB> offspring = new ArrayList<>();
			for(int pop = 0; pop < N_KB; pop++) {
				System.out.print(pop+1);

				/* 1. Mating Selection */
				Individual_KB[] parents = matingSelection(population);

				/* 2. Crossover */
				Individual_KB child = crossover(parents);

				/* 3. Perturbation */
				float[][][] newParams = perturbing(child.getControlPoints());
				child.setControlPoints(newParams);

				/* 4. Offspring Evaluation */
				evaluate(child, RBbest, Dtra, Deva);

				offspring.add(child);
			}

			/* 5. Population Update */
			populationUpdate(population, offspring);

			/* 6. Write Population */
			System.out.print(" [writing] ");
			dirGeneration = dirXML + sep + String.format("%02d", generation);
			if(generation == Generation_KB) {
				// Population
				outputXML(dirGeneration, population);
			}
			else {
				// Best Individual
				outputXML(dirGeneration, population.get(0));
			}

			System.out.println(" [done]");
		}

		System.out.println("Knowledge Base GA End");
		System.out.println("************************");

		// ************************************************************
		/* 4. Get Best Individual */
		System.out.print("5.4. Choose Best Individual: ");
		Individual_KB best = population.get(0);
		System.out.println("Fitness = " + best.getFitness(0));

		return best.getControlPoints();
	}

	public static void initialize(ArrayList<Individual_KB> population, float[][][] KBbest) {
		// ************************************************************
		int objectiveNum = 1;

		// One is an individual with KBbest
		Individual_KB individual_KBbest = new Individual_KB(objectiveNum);
		individual_KBbest.setControlPoints(KBbest);
		population.add(individual_KBbest);

		for(int pop = 1; pop < N_KB; pop++) {
			Individual_KB individual = new Individual_KB(objectiveNum);

			float[][][] controlPoints = perturbing(KBbest);

			individual.setControlPoints(controlPoints);

			population.add(individual);
		}
	}

	public static void evaluate(Individual_KB individual, int[][] RBbest, ItemSet Dtra, ItemSet Deva) {
		// ************************************************************
		int ruleNum = RBbest.length;

		KnowledgeBaseType kb = TSK.makeKB(individual.getControlPoints(), ruleNum);

		// ************************************************************
		// Learning
		FuzzyInferenceSystem fs = TSK.learning(kb, RBbest, Dtra, Main.iteration);
		individual.setFS(fs);

		// Calculate Fitness based on MSE value for evaluation dataset (Deva).
		float MSE = 0f;
		for(int p = 0; p < Deva.getItemSize(); p++) {
			Item item = Deva.getItem(p);
			for(int n = 0; n < Main.dimension; n++) {
				fs.getVariable(Main.name[n]).setValue(item.getValue(Main.name[n]));
			}
			fs.evaluate();

			float predicted = fs.getVariable(Main.outputName).getValue();
			float y = item.getValue(Main.outputName);

			MSE += (float)Math.pow((predicted - y), 2);
		}
		MSE /= (float)Deva.getItemSize();

		double[] fitness = new double[] {MSE};
		individual.setFitness(fitness);
	}

	public static Individual_KB[] matingSelection(ArrayList<Individual_KB> population) {
		// Tournament Selection
		int parentSize = 2;
		int T = 2;
		Individual_KB[] parents = new Individual_KB[parentSize];
		for(int i = 0; i < parentSize; i++) {
			int[] candidates = new int[T];
			for(int t = 0; t < T; t++) {
				candidates[t] = CILAB_Main.rnd.nextInt(population.size());
			}

			double min = Double.MAX_VALUE;
			int winner = 0;
			for(int t = 0; t < T; t++) {
				if(population.get(candidates[t]).getFitness(0) < min) {
					min = population.get(candidates[t]).getFitness(0);
					winner = candidates[t];
				}
			}

			parents[i] = population.get(winner);
		}

		return parents;
	}

	public static Individual_KB crossover(Individual_KB[] parents) {
		Individual_KB child = null;
		if(CILAB_Main.rnd.nextDoubleIE() > Px) {
			// Don't Crossover
			if(CILAB_Main.rnd.nextBoolean()) child = new Individual_KB(parents[0]);
			else child = new Individual_KB(parents[1]);
		}
		else {
			// Do Crossover
			child = new Individual_KB(parents[0].getObjectiveNum());
			int crossPoint = CILAB_Main.rnd.nextInt(Main.dimension);
			Individual_KB mom, dad;
			if(CILAB_Main.rnd.nextBoolean()) {
				mom = parents[0];
				dad = parents[1];
			}
			else {
				mom = parents[1];
				dad = parents[0];
			}

			float[][][] newParams = new float[Main.dimension][Main.H][];
			for(int n = 0; n < Main.dimension; n++) {
				Individual_KB parent;
				if(n < crossPoint) parent = mom;
				else parent = dad;

				for(int h = 0; h < Main.H; h++) {
					newParams[n][h] = Arrays.copyOf(parent.getControlPoints()[n][h], parent.getControlPoints()[n][h].length);
				}
			}
			child.setControlPoints(newParams);
		}
		return child;
	}


	public static float[][][] perturbing(float[][][] original) {
		float[][][] newParams = null;
		if(Main.termType == FuzzyTermType.TYPE_gaussianShape) {
			newParams = perturbingForGaussianShape(original);
		}

		return newParams;
	}

	public static float[][][] perturbingForGaussianShape(float[][][] original) {
		// ************************************************************
		float[][][] newParams = new float[Main.dimension][Main.H][Main.paramLength];
		for(int n = 0; n < Main.dimension; n++) {
			// ************************************************************
			// Mean Perturbation
			float delta = rndDelta();
			float newValue = original[n][0][0] + delta;
			if(newValue < 0) newParams[n][0][0] = 0;
			else if(newValue > 1) newParams[n][0][0] = 1;
			else newParams[n][0][0] = newValue;

			for(int h = 1; h < (Main.H - 1); h++) {
				delta = rndDelta();
				newValue = original[n][h][0] + delta;
				if(newValue < original[n][h-1][0]) newParams[n][h][0] = original[n][h][0];
				else if(newValue > original[n][h+1][0]) newParams[n][h][0] = original[n][h][0];
				else newParams[n][h][0] = newValue;
			}

			delta = rndDelta();
			newValue = original[n][Main.H-1][0] + delta;
			if(newValue < original[n][Main.H-1-1][0]) newParams[n][Main.H-1][0] = original[n][Main.H-1][0];
			else if(newValue > 1) newParams[n][Main.H-1][0] = 1;
			else newParams[n][Main.H-1][0] = newValue;

			// ************************************************************
			// Sigma Perturbation
			for(int h = 0; h < Main.H; h++) {
				delta = rndDelta();
				newValue = original[n][h][1] + delta;
				if(newValue < 0) newParams[n][h][1] = original[n][h][1];
				else newParams[n][h][1] = newValue;
			}

			// ************************************************************
		}

		return newParams;
	}

	public static float rndDelta() {
		int direction = 1;
		if(CILAB_Main.rnd.nextBoolean()) {
			direction = -1;
		}

		float delta = 0f;
		if(CILAB_Main.rnd.nextFloatIE() < Pp) {
			// Do Perturbation
			delta = CILAB_Main.rnd.nextFloatII() / 50;	// The range of [0, 0.02]
		}

		delta *= direction;

		return delta;
	}

	public static void populationUpdate(ArrayList<Individual_KB> population, ArrayList<Individual_KB> offspring) {
		ArrayList<Individual_KB> marge = new ArrayList<>();
		for(int pop = 0; pop < population.size(); pop++) {
			marge.add(population.get(pop));
		}
		for(int pop = 0; pop < offspring.size(); pop++) {
			marge.add(offspring.get(pop));
		}

		Collections.sort(marge, new Comparator<Individual_KB>() {
			@Override
			public int compare(Individual_KB a, Individual_KB b) {
				double fitA = a.getFitness(0);
				double fitB = b.getFitness(0);
				if(fitA < fitB) {
					return -1;
				}
				else if(fitA > fitB) {
					return 1;
				}
				else {
					return 0;
				}
			}
		});

		population.clear();
		for(int pop = 0; pop < N_KB; pop++) {
			population.add(marge.get(pop));
		}
	}

	public static void outputXML(String dir, ArrayList<Individual_KB> population) {
		String sep = File.separator;
		int popSize = population.size();
		(new File(dir)).mkdirs();

		for(int pop = 0; pop < popSize; pop++) {
			System.out.print(".");
			String fileName = dir + sep + "pop_" + String.format("%02d", pop) + ".xml";
			File path = new File(fileName);
			JFML.writeFSTtoXML(population.get(pop).getFS(), path);
		}

	}

	public static void outputXML(String dir, Individual_KB individual) {
		String sep = File.separator;
		(new File(dir)).mkdirs();

		System.out.print(".");
		String fileName = dir + sep + "pop_00" + ".xml";
		File path = new File(fileName);
		JFML.writeFSTtoXML(individual.getFS(), path);
	}

}

























