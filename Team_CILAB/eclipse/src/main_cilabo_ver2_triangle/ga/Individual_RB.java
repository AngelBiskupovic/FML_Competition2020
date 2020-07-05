package main_cilabo_ver2_triangle.ga;

import java.util.Arrays;

import jfml.FuzzyInferenceSystem;

public class Individual_RB {
	// ************************************************************
	// Fields
	int id;

	int ruleNum;
	int objectiveNum;

	int[][] antecedents;
	double[] fitness;

	FuzzyInferenceSystem fs;

	// ************************************************************
	// Constructor
	public Individual_RB() {}

	public Individual_RB(int ruleNum, int objectiveNum) {
		this.ruleNum = ruleNum;
		this.objectiveNum = objectiveNum;

		this.antecedents = new int[ruleNum][];
		this.fitness = new double[objectiveNum];
	}

	public Individual_RB(Individual_RB individual) {
		deepCopy(individual);
	}

	// ************************************************************
	// Methods
	public void deepCopy(Individual_RB individual) {
		this.id = individual.getID();
		this.ruleNum = individual.getRuleNum();
		this.objectiveNum = individual.getObjectiveNum();

		setAntecedents(individual.getAntecedents());
		setFitness(individual.getFitness());
	}


	// ************************************************************
	// GETter
	public int getID() {
		return this.id;
	}

	public int getRuleNum() {
		return this.ruleNum;
	}

	public int getObjectiveNum() {
		return this.objectiveNum;
	}

	public int[][] getAntecedents(){
		return this.antecedents;
	}

	/** To get i-th rule antecedent part */
	public int[] getAntecedent(int i) {
		return this.antecedents[i];
	}

	public double getFitness(int i) {
		return this.fitness[i];
	}

	public double[] getFitness() {
		return this.fitness;
	}

	public FuzzyInferenceSystem getFS() {
		return this.fs;
	}

	// ************************************************************
	// SETter
	public void setID(int id) {
		this.id = id;
	}

	public void setRuleNum(int ruleNum) {
		this.ruleNum = ruleNum;
	}

	public void setObjectiveNum(int objectiveNum) {
		this.objectiveNum = objectiveNum;
	}

	public void setAntecedent(int i, int[] antecedent) {
		this.antecedents[i] = Arrays.copyOf(antecedent, antecedent.length);
	}

	/** Deep Copy */
	public void setAntecedents(int[][] antecedents) {
		this.antecedents = new int[antecedents.length][];
		for(int i = 0; i < antecedents.length; i++) {
			this.antecedents[i] = Arrays.copyOf(antecedents[i], antecedents[i].length);

		}
	}

	public void setFitness(int i, double value) {
		this.fitness[i] = value;
	}

	/** Deep Copy */
	public void setFitness(double[] fitness) {
		this.fitness = Arrays.copyOf(fitness, fitness.length);
	}

	public void setFS(FuzzyInferenceSystem fs) {
		this.fs = fs;
	}
}
