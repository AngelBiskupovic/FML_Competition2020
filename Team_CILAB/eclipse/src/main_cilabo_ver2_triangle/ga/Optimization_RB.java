package main_cilabo_ver2_triangle.ga;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import jfml.FuzzyInferenceSystem;
import jfml.JFML;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.term.Term;
import main.Item;
import main.ItemSet;
import main_cilabo_ver2_triangle.CILAB_Main;
import main_cilabo_ver2_triangle.Main;
import main_cilabo_ver2_triangle.TSK;


public class Optimization_RB {

	/** Initial number of rules for an inference system */
	static int Nrule = 500;
	/** Max number of rules for an inference system */
	static int Nrule_max = 5000;
	/** Min number of rules for an inference system */
	static int Nrule_min = 500;
	/** Population size in the rule-base optimization phase */
	static int N_RB = 20;
	/** The number of generation in the rule-base optimization phase */
	static int Generation_RB = 20;

	/** Probability of assignment of "don't care". */
	static double dcRate = 2 / (double)Main.dimension;
	/** Probability of crossover */
	static double Px = 0.9;
	/** Probability of mutation */
	static double Pm = 0.1;

	public static int[][] RuleBase_Opt(int now, float[][][] KBbest, int[][] RBbest, ItemSet Dtra, ItemSet Deva) {
		// ************************************************************
		String sep = File.separator;
		String dirXML = Main.dirName + sep + String.format("%02d", 2*now+1) + "_RB";
		(new File(dirXML)).mkdirs();

		// ************************************************************
		/* 0. Knowledge Base Initialization */
		System.out.print("4.0. Knowledge Base Initialization ");
		KnowledgeBaseType kb = TSK.makeKB(KBbest, Nrule);
		System.out.println("[done]");

		/* 1. Population Initialization */
		System.out.print("4.1. Population Initialization ");
		ArrayList<Individual_RB> population = new ArrayList<>();
		initialize(population, RBbest, Dtra, kb);
		System.out.println("[done]");

		/* 2. Initial Population Evaluation */
		System.out.println("4.2. Initial Population Evaluation");
		System.out.print("Evaluation: ");
		for(int pop = 0; pop < population.size(); pop++) {
			System.out.print(pop+1);
			evaluate(population.get(pop), kb, Dtra, Deva);
		}
		System.out.println(" [done]");

		/* 2.5. Write Initial Population as XML files */
		System.out.print("Output Initial Population ");
		String dirGeneration = dirXML + sep + "00";
		outputXML(dirGeneration, population);
		System.out.println(" [done]");

		// ************************************************************
		/* 3. Optimization Loop */
		for(int generation = 1; generation <= Generation_RB; generation++) {
			System.out.println();
			System.out.println("************************");
			System.out.println("4.3. Rule Base GA");
			System.out.println("Generation: " + generation);
			System.out.print("Evaluation: ");

			ArrayList<Individual_RB> offspring = new ArrayList<>();
			for(int pop = 0; pop < N_RB; pop++) {
				System.out.print(pop+1);

				/* 1. Mating Selection */
				Individual_RB[] parents = matingSelection(population);

				/* 2. Crossover */
				Individual_RB child = crossover(parents);

				/* 3. Mutation */
				mutation(child);

				/* 4. Rule Pruning */
				rulePruning(child, Dtra, kb);
				uniqueRules(child);

				/* 5. Offspring Evaluation */
				evaluate(child, kb, Dtra, Deva);

				offspring.add(child);
			}

			/* 6. Population Update */
			populationUpdate(population, offspring);

			/* 7. Write Population */
			System.out.print(" [writing] ");
			dirGeneration = dirXML + sep + String.format("%02d", generation);
			if(generation == Generation_RB) {
				// Population
				outputXML(dirGeneration, population);
			}
			else {
				// Best Individual
				outputXML(dirGeneration, population.get(0));
			}

			System.out.println(" [done]");
		}

		System.out.println("Rule Base GA End");
		System.out.println("************************");

		// ************************************************************
		/* 4. Get Best Individual */
		System.out.print("4.4. Choose Best Individual: ");
		Individual_RB best = population.get(0);
		System.out.println("Fitness = " + best.getFitness(0));

		return best.getAntecedents();
	}

	/**
	 * <h1>1. Initialize population.</h1>
	 * @param population : {@literal ArrayList<Individual_RB>}
	 */
	public static void initialize(ArrayList<Individual_RB> population, int[][] RBbest, ItemSet Dtra, KnowledgeBaseType kb) {
		// ************************************************************
		int objectiveNum = 1;

		int POP_START = -1;
		if(Objects.isNull(RBbest)) {
			POP_START = 0;
		}
		else {
			// One is an individual with KBbest
			POP_START = 1;
			int ruleNum = RBbest.length;
			Individual_RB individual_RBbest = new Individual_RB(ruleNum, objectiveNum);
			individual_RBbest.setAntecedents(RBbest);
			population.add(individual_RBbest);
		}

		for(int pop = POP_START; pop < N_RB; pop++) {
			Individual_RB individual = new Individual_RB(Nrule, objectiveNum);

			int[] itemIdx = samplingWithout(Dtra.getItemSize(), Nrule);

			for(int rule = 0; rule < Nrule; rule++) {
				Item item = Dtra.getItem(itemIdx[rule]);
				int[] antecedent = heuristicRuleGeneration(item, kb);

				individual.setAntecedent(rule, antecedent);
			}

			population.add(individual);
		}
	}

	public static void evaluate(Individual_RB individual, KnowledgeBaseType kb, ItemSet Dtra, ItemSet Deva) {
		// ************************************************************
		// Learning
		FuzzyInferenceSystem fs = TSK.learning(kb, individual.getAntecedents(), Dtra, Main.iteration);
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

	public static Individual_RB[] matingSelection(ArrayList<Individual_RB> population) {
		// Tournament Selection
		int parentSize = 2;
		int T = 2;
		Individual_RB[] parents = new Individual_RB[parentSize];
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

	public static Individual_RB crossover(Individual_RB[] parents) {
		Individual_RB child = null;
		if(CILAB_Main.rnd.nextDoubleIE() > Px) {
			// Don't Crossover
			if(CILAB_Main.rnd.nextBoolean()) child = new Individual_RB(parents[0]);
			else child = new Individual_RB(parents[1]);
		}
		else {
			// Do Crossover
			int Nmom = CILAB_Main.rnd.nextInt(parents[0].getRuleNum());
			int Ndad = CILAB_Main.rnd.nextInt(parents[1].getRuleNum());

			// Verify max number of rules
			if( (Nmom + Ndad) > Nrule_max ) {
				int deleteNum = Nmom + Ndad - Nrule_max;
				for(int i = 0; i < deleteNum; i++) {
					if(Ndad <= 0) {
						Nmom--;
						continue;
					}
					else if(Nmom <= 0) {
						Ndad--;
						continue;
					}
					if(CILAB_Main.rnd.nextBoolean()) Nmom--;
					else Ndad--;
				}
			}

			// Rule Indexes will be inherited
			int[] pmom = samplingWithout(parents[0].getRuleNum(), Nmom);
			int[] pdad = samplingWithout(parents[1].getRuleNum(), Ndad);

			child = new Individual_RB(Nmom + Ndad, parents[0].getObjectiveNum());

			int rule = 0;
			for(int mom_i = 0; mom_i < Nmom; mom_i++) {
				child.setAntecedent(rule, parents[0].getAntecedent(pmom[mom_i]));
				rule++;
			}
			for(int dad_i = 0; dad_i < Ndad; dad_i++) {
				child.setAntecedent(rule, parents[1].getAntecedent(pdad[dad_i]));
				rule++;
			}
		}

		return child;
	}

	public static void mutation(Individual_RB individual) {
		int ruleNum = individual.getRuleNum();

		for(int rule = 0; rule < ruleNum; rule++) {
			if(CILAB_Main.rnd.nextDoubleIE() < Pm) {
				// Do Mutation
				int objectiveDimension = CILAB_Main.rnd.nextInt(Main.dimension);
				int nowIndex = individual.getAntecedents()[rule][objectiveDimension];

				ArrayList<Integer> list = new ArrayList<>();
				for(int h = 0; h < Main.H+1; h++) {
					if(h != nowIndex) list.add(h);
				}

				int newIndex = list.get(CILAB_Main.rnd.nextInt(list.size()));

				individual.getAntecedents()[rule][objectiveDimension] = newIndex;
			}
		}
	}

	public static void rulePruning(Individual_RB individual, ItemSet Dtra, KnowledgeBaseType kb) {
		// ************************************************************
		int itemSize = Dtra.getItemSize();
		int dimension = Main.dimension;
		int ruleNum = individual.getRuleNum();

		// ************************************************************
		/* 1. Alpha Cut */
		double alpha = 0.5;
		boolean[][][] alphaFlg = new boolean[itemSize][ruleNum][dimension];

		for(int p = 0; p < itemSize; p++) {
			Item item = Dtra.getItem(p);
			for(int rule = 0; rule < ruleNum; rule++) {
				for(int n = 0; n < dimension; n++) {
					int termIdx = individual.getAntecedents()[rule][n];
					Term term = kb.getVariable(Main.name[n]).getTerm(Main.termName[termIdx]);
					float membership = term.getMembershipValue(item.getValue(Main.name[n]));
					alphaFlg[p][rule][n] = (membership >= alpha);
				}
			}
		}

		// Aggregate
		boolean[][] alphaTable = new boolean[itemSize][ruleNum];
		for(int p = 0; p < itemSize; p++) {
			for(int rule = 0; rule < ruleNum; rule++) {
				alphaTable[p][rule] = alphaFlg[p][rule][0];
				for(int n = 1; n < dimension; n++) {
					alphaTable[p][rule] = (alphaTable[p][rule] && alphaFlg[p][rule][n]);
				}
			}
		}

		ArrayList<Integer> deleteRuleIdx = new ArrayList<>();
		for(int rule = 0; rule < ruleNum; rule++) {
			boolean flg = alphaTable[0][rule];
			for(int p = 1; p < itemSize; p++) {
				flg = (flg || alphaTable[p][rule]);
			}
			if(!flg) {
				deleteRuleIdx.add(rule);
			}
		}

		// ************************************************************
		/* 2. Heuristic Rule Genration */
		ArrayList<Item> heuristicList = new ArrayList<>();
		for(int p = 0; p < itemSize; p++) {
			boolean flg = alphaTable[p][0];
			for(int rule = 1; rule < ruleNum; rule++) {
				flg = (flg || alphaTable[p][rule]);
			}
			if(!flg) {
				heuristicList.add(Dtra.getItem(p));
			}
		}

		ArrayList<int[]> generatedAntecedents = new ArrayList<>();
		int generateNum = Nrule_min - (ruleNum - deleteRuleIdx.size());
		if(generateNum > 0) {
			generateNum += deleteRuleIdx.size();
		}
		else {
			generateNum = deleteRuleIdx.size();
		}
		int generatedNum = 0;
		for(int i = 0; i < generateNum; i++) {
			if(heuristicList.size() > 0) {
				Item item = heuristicList.remove(CILAB_Main.rnd.nextInt(heuristicList.size()));
				int[] generated = heuristicRuleGeneration(item, kb);
				generatedAntecedents.add(generated);
				generatedNum++;
			}
			else {
				break;
			}
		}

		// ************************************************************
		// New Antecedents
		int newRuleNum = ruleNum - deleteRuleIdx.size() + generatedNum;
		int[][] newAntecedents = new int[newRuleNum][];
		int rule = 0;
		for(int i = 0; i < individual.getRuleNum(); i++) {
			if(!deleteRuleIdx.contains(i)) {
				newAntecedents[rule] = Arrays.copyOf(individual.getAntecedent(i), individual.getAntecedent(i).length);
				rule++;
			}
		}
		for(int i = 0; i < generatedNum; i++) {
			newAntecedents[rule] = Arrays.copyOf(generatedAntecedents.get(i), generatedAntecedents.get(i).length);
			rule++;
		}
		individual.setAntecedents(newAntecedents);
		individual.setRuleNum(newRuleNum);
	}

	public static void uniqueRules(Individual_RB individual) {
		// Same Antecedent Judge
		ArrayList<Integer> sameList = new ArrayList<>();
		for(int i = 0; i < individual.getRuleNum(); i++) {
			for(int j = 0; j < i; j++) {
				if(!sameList.contains(j)) {
					if(Arrays.equals(individual.getAntecedent(i), individual.getAntecedent(j))) {
						sameList.add(i);
					}
				}
			}
		}

		int newRuleNum = individual.getRuleNum() - sameList.size();
		int[][] newAntecedents = new int[newRuleNum][];
		int rule = 0;
		for(int i = 0; i < individual.getRuleNum(); i++) {
			if(!sameList.contains(i)) {
				newAntecedents[rule] = Arrays.copyOf(individual.getAntecedent(i), individual.getAntecedent(i).length);
				rule++;
			}
		}
		individual.setAntecedents(newAntecedents);
		individual.setRuleNum(newRuleNum);
	}

	public static void populationUpdate(ArrayList<Individual_RB> population, ArrayList<Individual_RB> offspring) {
		ArrayList<Individual_RB> marge = new ArrayList<>();
		for(int pop = 0; pop < population.size(); pop++) {
			marge.add(population.get(pop));
		}
		for(int pop = 0; pop < offspring.size(); pop++) {
			marge.add(offspring.get(pop));
		}

		Collections.sort(marge, new Comparator<Individual_RB>() {
			@Override
			public int compare(Individual_RB a, Individual_RB b) {
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
		for(int pop = 0; pop < N_RB; pop++) {
			population.add(marge.get(pop));
		}
	}

	public static void outputXML(String dir, ArrayList<Individual_RB> population) {
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

	public static void outputXML(String dir, Individual_RB individual) {
		String sep = File.separator;
		(new File(dir)).mkdirs();

		System.out.print(".");
		String fileName = dir + sep + "pop_00" + ".xml";
		File path = new File(fileName);
		JFML.writeFSTtoXML(individual.getFS(), path);
	}


	/**
	 *
	 * @param item : Item
	 * @param kb : KnowledgeBaseType
	 * @return : int[dimension]
	 */
	public static int[] heuristicRuleGeneration(Item item, KnowledgeBaseType kb) {
		// ************************************************************
		int[] antecedent = new int[Main.dimension];

		for(int i = 0; i < Main.dimension; i++) {
			if(CILAB_Main.rnd.nextDoubleIE() < dcRate) {
				/* Assignment "don't care" for the dimension i. */
				antecedent[i] = Main.H;
			}
			else {
				float value = item.getValue(Main.name[i]);
				KnowledgeBaseVariable variable = kb.getVariable(Main.name[i]);
				antecedent[i] = decisionHeuristicFuzzyTerm(value, variable);
			}
		}

		return antecedent;
	}

	/**
	 *
	 * @param value : float
	 * @param variable : KnowledgeBaseVariable
	 * @return : int
	 */
	public static int decisionHeuristicFuzzyTerm(float value, KnowledgeBaseVariable variable) {
		float max = -Float.MAX_VALUE;
		int maxIdx = -1;

		for(int h = 0; h < Main.H; h++) {
			Term term = variable.getTerm(Main.termName[h]);
			float membershipValue = term.getMembershipValue(value);

			if(membershipValue > max) {
				max = membershipValue;
				maxIdx = h;
			}
		}

		return maxIdx;
	}

	/**
	 * <h1>Sampling without</h1><br>
	 * @param box : int : The size of original array (or list).
	 * @param want : int : The number of indexes wanted.
	 * @return Integer[] : The array of sampled indexes.
	 */
	public static int[] samplingWithout(int box, int want) {
		int[] answer = new int[want];

		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < box; i++) {
			list.add(i);
		}

		for(int i = 0; i < want; i++) {
			int index = CILAB_Main.rnd.nextInt(list.size());
			answer[i] = list.get(index);
			list.remove(index);
		}

		return answer;
	}

}
