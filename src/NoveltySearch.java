import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class NoveltySearch {
    private int numVar = 0;
    private int numClause = 0;
    private int flipTimes = 0;
    private double p;
    private Random random;

    private ArrayList<Clause> clauses;
    private HashMap<Integer, Integer> assignmentMap;
    private ArrayList<Clause> falseClauses;
    private HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap;
    private ArrayList<ArrayList<Integer>> phenotypeLib;

    public static void main(String [] args){
        NoveltySearch noveltySearch = new NoveltySearch();
        noveltySearch.run();
    }

    public NoveltySearch(){
        // initialise variables
        clauses = new ArrayList<>();
        falseClauses = new ArrayList<>();
        assignmentMap = new HashMap<>();
        clausesContainingLiteralMap = new HashMap<>();
        phenotypeLib = new ArrayList<>();

        p = 0.2;
        random = new Random();
    }

    private void run(){
        // read input CNF
        Helper.readFile("input.txt");
        numVar = Helper.numVar;
        numClause = Helper.numClause;
        clauses = Helper.clauses;

        initialiseAssignmentMap();
        initialiseClauseAssignment();

        initialiseClausesContainingLiteral();
        updateFalseClauses();

        while(!completed()){
            Clause curClause = getRandomFalseClause();
            double r = random.nextDouble();
            int var;

            if(r > p){
                var = pickVar(curClause);
            }
            else{
                var = random.nextInt(numVar) + 1;
            }
            ArrayList<Integer> p = getPhenotype(var);
            phenotypeLib.add(p);
            flip(var);
        }
        System.out.println("Flips: " + flipTimes);

        System.out.println("Solution: " + assignmentMap);
        System.out.println("Verifier: " + Helper.verifier(assignmentMap, clauses));
//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }
    }

    /**
     * allocates a random truth assignment to literal
     */
    private void initialiseAssignmentMap(){
        Random random = new Random();
        for(int i = 1; i < numVar + 1; i++){
            int temp = random.nextInt(2);
            assignmentMap.put(i, temp);
        }
        for(int i = -1 * numVar; i < 0; i++){
            assignmentMap.put(i, assignmentMap.get(-1 * i) == 1? 0 : 1);
        }
    }

    /**
     * initialises clause assignments based on the current assignmentMap
     */
    private void initialiseClauseAssignment(){
        for(Clause clause: clauses){
            clause.updateAssignment(assignmentMap);
        }
    }

    /**
     * gives the list of clauses that contains each literal.
     * lists for x and -x are separated
     */
    private void initialiseClausesContainingLiteral(){
        for(int i = -1 * numVar; i < numVar + 1; i++){
            if(i == 0)
                continue;

            ArrayList<Clause> temp = new ArrayList<>();
            for(Clause clause: clauses){
                if (clause.hasLiteral(i)){
                    temp.add(clause);
                }
            }
            clausesContainingLiteralMap.put(i, temp);
        }
    }

    /**
     *
     */
    private void updateFalseClauses(){
        falseClauses = new ArrayList<>();

        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
            if(clause.getNumSatisfiedLiterals() == 0){
                falseClauses.add(clause);
            }

        }
    }

    /**
     * checks if all clauses are satisfied
     * @return complete
     */
    private boolean completed(){
//        for(Clause clause: clauses){
//            if(clause.getNumSatisfiedLiterals() == 0)
//                return false;
//        }
//        return true;
        return falseClauses.size() == 0;
    }


    /**
     *
     * @param var
     */
    private void flip(int var){
        flipTimes++;
        assignmentMap.put(var, assignmentMap.get(var) == 1? 0 : 1);
        assignmentMap.put(var * -1, assignmentMap.get(var * -1) == 1? 0 : 1);

//        initialiseClauseAssignment();
        for(Clause clause: clauses){
            clause.flipAt(var);
        }

        updateFalseClauses();
        initialiseClausesContainingLiteral();
    }

    private Clause getRandomFalseClause(){
        if(falseClauses.size() == 0)
            return null;
        int index = random.nextInt(falseClauses.size());
        return falseClauses.get(index);
    }

    private int pickVar(Clause clause){
        ArrayList<Integer> literals = clause.getLiterals();
        int maxNovelty = 0;
        int maxNoveltyIndex = -1;
        int maxFitness = 0;
        int maxFitnessIndex = -1;

        for(int i = 0; i < literals.size(); i++){
            ArrayList<Integer> p = getPhenotype(literals.get(i));
            int curNovelty = getNovelty(phenotypeLib, p);
            int curFitness = getFitness(p);
            if(curNovelty > maxNovelty){
                maxNovelty = curNovelty;
                maxNoveltyIndex = i;
            }
            if(curFitness > maxFitness){
                maxFitness = curFitness;
                maxFitnessIndex = i;
            }
        }
        return literals.get(maxFitnessIndex);
    }

    private int getFitness(ArrayList<Integer> phenotype){
        int counter = 0;
        for(Integer i: phenotype){
            if(i == 1){
                counter++;
            }
        }
        return counter;
    }

    private ArrayList<Integer> getPhenotype(int var){
        // result assignment
        ArrayList<Integer> res = new ArrayList<>();
        for(Clause clause: clauses){
            int target = 0;
            if(clause.getLiterals().contains(var)){
                target = var;
            }
            else if(clause.getLiterals().contains(-1 * var)){
                target = -1 * var;
            }
            else{
                res.add(clause.numSatisfiedLiterals == 0? 0 : 1);
                continue;
            }

            if(clause.assignment.get(clause.literals.indexOf(target)) == 1){
                if(clause.getNumSatisfiedLiterals() == 1){
                    res.add(0);
                }
                else{
                    res.add(1);
                }
            }
            else{
                res.add(1);
            }
        }

        return res;
    }

    private int getNovelty(ArrayList<ArrayList<Integer>> phenotypeLib, ArrayList<Integer> p){
        int novelty = 1;
        if(phenotypeLib.size() == 0)
            return Integer.MAX_VALUE;
        for(ArrayList<Integer> phenotype: phenotypeLib){
            novelty *= getHammingDistance(phenotype, p);
        }
        return novelty;
    }

    private int getHammingDistance(ArrayList<Integer> p1, ArrayList<Integer> p2){
        int dist = 0;
        for(int i = 0; i < p1.size(); i++){
            int temp = p1.get(i) ^ p2.get(i);
            if (temp == 1)
                dist++;
        }
        return dist;
    }


}
