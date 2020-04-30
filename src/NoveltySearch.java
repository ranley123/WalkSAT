import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class NoveltySearch {
    private int numVar = 0;
    private int numClause = 0;
    private int flipTimes = 0;
    double p;
    private Random random;
    private long avg_flip_frequency = 0;
    private String filename;
    private long runningTime;

    private ArrayList<Clause> clauses;
    private HashMap<Integer, Integer> assignmentMap;
    private ArrayList<Clause> falseClauses;
    private HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap;
    private ArrayList<int[]> phenotypeLib;
    private int[] curPhenotype;

    public static void main(String [] args){
        if(args.length != 1){
            System.out.println("Usage: java WalkSAT [inputFile]");
        }
        String filename = args[0];
        NoveltySearch noveltySearch = new NoveltySearch(filename);
        noveltySearch.run();
    }

    public int getFlipTimes(){
        return flipTimes;
    }

    public int getNumVar(){
        return numVar;
    }

    public NoveltySearch(String filename){
        // initialise variables
        clauses = new ArrayList<>();
        falseClauses = new ArrayList<>();
        assignmentMap = new HashMap<>();
        clausesContainingLiteralMap = new HashMap<>();
        phenotypeLib = new ArrayList<>();
        this.filename = filename;
        p = 0.4;
        random = new Random();

    }

    /**
     * for Eval.java
     * @param assignmentMap
     */
    public void initialiseForEval(HashMap<Integer, Integer> assignmentMap){
        this.assignmentMap = assignmentMap;
        Helper.initialiseClauseAssignment(clauses, assignmentMap);

//        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);
        falseClauses = Helper.updateFalseClauses(clauses);
    }

    /**
     * read file and set up
     */
    public void readFile(){
        Helper.readFile(filename);
        numVar = Helper.numVar;
        numClause = Helper.numClause;
        clauses = Helper.clauses;
        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);
        curPhenotype = new int[numClause];
    }

    /**
     * initialise the model before running
     */
    public void initialise(){
        Helper.initialiseAssignmentMap(assignmentMap, numVar);
        Helper.initialiseClauseAssignment(clauses, assignmentMap);

        falseClauses = Helper.updateFalseClauses(clauses);

        for(int i = 0; i < clauses.size(); i++){
            if(clauses.get(i).getNumSatisfiedLiterals() > 0){
                curPhenotype[i] = 1;
            }
            else{
                curPhenotype[i] = 0;
            }
        }
    }

    public void search(){
        int MAX_FLIPS = 20000;
        while(!completed()){
//            if(flipTimes > MAX_FLIPS)
//                break;
//            System.out.println(flipTimes);
            Clause curClause = getRandomFalseClause();
            double r = random.nextDouble();
            int var;

            if(r > p){
                var = pickVar(curClause);
            }
            else{
                var = random.nextInt(numVar) + 1;
            }
            int[] p = getPhenotype(var);
            phenotypeLib.add(p);
            curPhenotype = p;
            flip(var);
        }

        System.out.println("Flips: " + flipTimes);
        System.out.println("Average flip frequency: " + avg_flip_frequency/flipTimes);
        System.out.println("Solution: " + assignmentMap);
        System.out.println("Verifier: " + Helper.verifier(assignmentMap, clauses));
//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }
    }

    private void run(){
        readFile();
        long startTime = System.nanoTime();
        initialise();
        search();

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        runningTime = totalTime;
    }

    public long getRunningTime(){
        return runningTime;
    }


    /**
     * checks if all clauses are satisfied
     * @return complete
     */
    private boolean completed(){
        return falseClauses.size() == 0;
    }


    /**
     * flip the given var and update false clauses
     * @param var
     */
    private void flip(int var){
        long startTime = System.nanoTime();

        flipTimes++;
        assignmentMap.put(var, assignmentMap.get(var) == 1? 0 : 1);
        assignmentMap.put(var * -1, assignmentMap.get(var * -1) == 1? 0 : 1);

        // update falseClauses and flip variables
        for(Clause clause: clausesContainingLiteralMap.get(var)){
            if(clause.getVarAssignment(var) == 1 && clause.getNumSatisfiedLiterals() == 1)
                falseClauses.add(clause);
            else if(clause.getNumSatisfiedLiterals() == 0)
                falseClauses.remove(clause);
            clause.flipAt(var);
        }
        for(Clause clause: clausesContainingLiteralMap.get(var * -1)){
            if(clause.getVarAssignment(-1 * var) == 1 && clause.getNumSatisfiedLiterals() == 1)
                falseClauses.add(clause);
            else if(clause.getNumSatisfiedLiterals() == 0)
                falseClauses.remove(clause);
            clause.flipAt(var);
        }

//        falseClauses = Helper.updateFalseClauses(clauses);
//        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        long frequency = 1000000000/totalTime;
//        System.out.println("flip frequency: " + frequency);
        avg_flip_frequency += frequency;
    }

    private Clause getRandomFalseClause(){
        if(falseClauses.size() == 0)
            return null;
        int index = random.nextInt(falseClauses.size());
        return falseClauses.get(index);
    }

    private int pickVar(Clause clause){
        ArrayList<Integer> literals = clause.getLiterals();
        ArrayList<Integer> maxNoveltyIndices = new ArrayList<>();
        ArrayList<Integer> maxFitnessIndices = new ArrayList<>();
        int maxNovelty = 0;
        int maxNoveltyIndex = -1;
        int maxFitness = 0;
        int maxFitnessIndex = -1;

        for(int i = 0; i < literals.size(); i++){
            int[] p = getPhenotype(literals.get(i));
            int curNovelty = getNovelty(phenotypeLib, p);
            int curFitness = getFitness(p);

            // update max novelty
            if(curNovelty > maxNovelty){
                maxNovelty = curNovelty;
                maxNoveltyIndex = i;
            }
            // possible choices
            else if(curNovelty == maxNovelty){
                maxNoveltyIndices.add(i);
            }

            // update max fitness
            if(curFitness > maxFitness){
                maxFitness = curFitness;
                maxFitnessIndex = i;
            }
            // possible choices
            else if(curFitness == maxFitness){
                maxFitnessIndices.add(i);
            }
        }

        // get the variable with max novelty and max fitness
        int index = -1;
        for(Integer i: maxNoveltyIndices){
            for(Integer j: maxFitnessIndices){
                if(i == j) {
                    index = i;
                }
            }
        }
        if(index >= 0){
            return literals.get(index);
        }
        return literals.get(maxFitnessIndex);
    }

    /**
     * get the fitness: the number of satisfied clauses
     * @param phenotype
     * @return
     */
    private int getFitness(int[]  phenotype){
        int counter = 0;
        for(int i: phenotype){
            if(i == 1){
                counter++;
            }
        }
        return counter;
    }

    /**
     * flip the given var, returns its resulting phenotype
     * @param var - the variable to be flipped
     * @return
     */
    private int[] getPhenotype(int var){
        // get the current phenotype
        int[] phenotype = Arrays.copyOf(curPhenotype, curPhenotype.length);

        for(Clause clause: clausesContainingLiteralMap.get(var)){
            if(clause.getNumSatisfiedLiterals() == 0){
                phenotype[clause.getId()] = 1;
            }
            else if(clause.getNumSatisfiedLiterals() == 1 && clause.getVarAssignment(var) == 1){
                phenotype[clause.getId()] = 0;
            }
        }
        for(Clause clause: clausesContainingLiteralMap.get(-1 * var)){
            if(clause.getNumSatisfiedLiterals() == 0){
                phenotype[clause.getId()] = 1;
            }
            else if(clause.getNumSatisfiedLiterals() == 1 && clause.getVarAssignment(-1 * var) == 1){
                phenotype[clause.getId()] = 0;
            }
        }

        return phenotype;
    }

    /**
     * gets the novelty of a phenotype
     * @param phenotypeLib - all phenotypes we had before
     * @param p
     * @return
     */
    private int getNovelty(ArrayList<int[]> phenotypeLib, int[] p){
        int novelty = 1;
        if(phenotypeLib.size() == 0)
            return Integer.MAX_VALUE;
        // for each phenotype
        for(int[] phenotype: phenotypeLib){
            novelty *= getHammingDistance(phenotype, p);
        }
        return novelty;
    }

    /**
     * returns the hamming distance between two phenotypes
     * @param p1 - the first phenotype
     * @param p2 - the second phenotype
     * @return
     */
    private int getHammingDistance(int[] p1, int[] p2){
        int dist = 0;
        for(int i = 0; i < p1.length; i++){
            int temp = p1[i] ^ p2[i];
            if (temp == 1)
                dist++;
        }
        return dist;
    }

}
