package main_cilabo_ver1_gaussian;

import jfml.FuzzyInferenceSystem;
import jfml.knowledgebase.KnowledgeBaseType;
import jfml.knowledgebase.variable.FuzzyVariableType;
import jfml.knowledgebase.variable.KnowledgeBaseVariable;
import jfml.knowledgebase.variable.TskVariableType;
import jfml.rule.AntecedentType;
import jfml.rule.ClauseType;
import jfml.rule.TskConsequentType;
import jfml.rule.TskFuzzyRuleType;
import jfml.rulebase.FuzzySystemRuleBase;
import jfml.rulebase.TskRuleBaseType;
import jfml.term.FuzzyTermType;
import jfml.term.Term;
import jfml.term.TskTerm;
import jfml.term.TskTermType;
import main.Item;
import main.ItemSet;

public class TSK {

	public static FuzzyInferenceSystem makeFS(float[][][] controlPoints, int[][] antecedents, ItemSet Dtra) {
		// ************************************************************
		KnowledgeBaseType kb = makeKB(controlPoints, antecedents.length);
		FuzzyInferenceSystem fs = learning(kb, antecedents, Dtra, Main.iteration);
		// ************************************************************

		return fs;
	}

	public static FuzzyInferenceSystem learning(KnowledgeBaseType kb, int[][] antecedents, ItemSet Dtra, int iteration) {
		// ************************************************************
		/** The number of rules. */
		int ruleNum = antecedents.length;
		/** The number of instances */
		int itemSize = Dtra.getItemSize();

		// Antecedent part evaluation
		float[][] membershipValues = new float[itemSize][ruleNum];
		for(int p = 0; p < itemSize; p++) {
			Item item = Dtra.getItem(p);
			for(int rule = 0; rule < ruleNum; rule++) {
				membershipValues[p][rule] = 1;
				for(int n = 0; n < Main.dimension; n++) {
					Term term = kb.getVariable(Main.name[n]).getTerm(Main.termName[antecedents[rule][n]]);
					float value = item.getValue(Main.name[n]);
					membershipValues[p][rule] *= term.getMembershipValue(value);
				}
			}
		}

		// Learning
		float[] consequents = new float[ruleNum];
		for(int rule = 0; rule < ruleNum; rule++) {
			consequents[rule] = Main.initConsequent;
		}
		//Iteration
		for(int ite = 0; ite < iteration; ite++) {
			if(ite % 10 == 0) {
				System.out.print(".");
			}
			for(int p = 0; p < itemSize; p++) {
				float y = Dtra.getItem(p).getValue(Main.outputName);

				// Forward output
				float output = 0f;
				float memberSum = 0;
				for(int rule = 0; rule < ruleNum; rule++) {
					memberSum += membershipValues[p][rule];
					output += membershipValues[p][rule] * consequents[rule];
				}
				output /= memberSum;

				// Backpropagation
				float difference = y - output;
				for(int rule = 0; rule < ruleNum; rule++) {
					consequents[rule] = consequents[rule] + Main.eta * difference * (membershipValues[p][rule]/memberSum);
				}
			}
		}

		// ************************************************************
		// Remake the knowledge base with learned consequent parts (concList).
		KnowledgeBaseType newKB = new KnowledgeBaseType();

		// Input variables
		for(int i = 0; i < Main.dimension; i++) {
			newKB.addVariable(kb.getVariable(Main.name[i]));
		}
		// Output variables
		TskVariableType outputVariable = new TskVariableType(Main.outputName);
		outputVariable.setDefaultValue(0f);
		outputVariable.setCombination("WA");
		outputVariable.setType("output");
		for(int i = 0; i < ruleNum; i++) {
			TskTermType outputTerm = new TskTermType("rule-"+String.valueOf(i), TskTerm._ORDER_0, new float[] {consequents[i]});
			outputVariable.addTskTerm(outputTerm);
		}
		newKB.addVariable(outputVariable);

		// Remake the rule base with the learned knowledge base.
		TskRuleBaseType newRB = makeTSKRB(antecedents, newKB);

		FuzzyInferenceSystem fs = new FuzzyInferenceSystem();
		fs.setKnowledgeBase(newKB);
		fs.addRuleBase(newRB);

		return fs;
	}

	public static KnowledgeBaseType makeKB(float[][][] controlPoints, int ruleNum) {
		// ************************************************************
		KnowledgeBaseType kb = new KnowledgeBaseType();

		// Implements input variables with defined fuzzy terms (from the control points).
		for(int i = 0; i < Main.dimension; i++) {
			// Variable
			FuzzyVariableType variable = new FuzzyVariableType(Main.name[i], 0f, 1f);

			// Fuzzy Term
			for(int h = 0; h < Main.H; h++) {
				FuzzyTermType term = new FuzzyTermType(Main.termName[h], Main.termType, controlPoints[i][h]);
				variable.addFuzzyTerm(term);
			}

			// Don't Care
			FuzzyTermType dc = new FuzzyTermType(Main.termName[Main.H], FuzzyTermType.TYPE_rectangularShape, new float[] {0f, 1f});
			variable.addFuzzyTerm(dc);

			kb.addVariable(variable);
		}

		// Implements output variables based on TSK fuzzy inference system.
		TskVariableType outputVariable = new TskVariableType(Main.outputName);
		outputVariable.setDefaultValue(0f);
		outputVariable.setCombination("WA");	// Weighted Average
		outputVariable.setType("output");
		for(int i = 0; i < ruleNum; i++) {
			TskTermType outputTerm = new TskTermType("rule-"+String.valueOf(i), TskTerm._ORDER_0, new float[] {Main.initConsequent});
			outputVariable.addTskTerm(outputTerm);
		}
		kb.addVariable(outputVariable);

		return kb;
	}

	public static TskRuleBaseType makeTSKRB(int[][] antecedents, KnowledgeBaseType kb) {
		// ************************************************************
		/** The number of rules */
		int ruleNum = antecedents.length;
		// ************************************************************
		TskRuleBaseType rb = new TskRuleBaseType("rulebase", FuzzySystemRuleBase.TYPE_TSK);
		rb.setActivationMethod("PROD");	// The compatible grade is calculated by product of membership value for each attribute.

		// Implementation a set of TSK fuzzy if-then rules based on JFML.
		for(int i = 0; i < ruleNum; i++) {
			TskFuzzyRuleType rule = new TskFuzzyRuleType("rule"+String.valueOf(i), "and", "PROD", 1.0f);

			// Antecedent part
			AntecedentType ant = new AntecedentType();
			for(int dim_i = 0; dim_i < Main.dimension; dim_i++) {
				// Clause (ex: "variable" IS "term")
				KnowledgeBaseVariable variable = kb.getVariable(Main.name[dim_i]);
				Term term = kb.getVariable(Main.name[dim_i]).getTerm(Main.termName[antecedents[i][dim_i]]);
				ClauseType clause = new ClauseType(variable, term);
				ant.addClause(clause);
			}

			// TSK type of consequent part
			TskConsequentType con = new TskConsequentType();
			KnowledgeBaseVariable outputVariable = kb.getVariable(Main.outputName);
			TskTerm outputTerm =  (TskTerm)kb.getVariable(Main.outputName).getTerm("rule-"+String.valueOf(i));
			con.addTskThenClause(outputVariable, outputTerm);

			rule.setAntecedent(ant);
			rule.setTskConsequent(con);

			rb.addTskRule(rule);
		}

		return rb;
	}

}


