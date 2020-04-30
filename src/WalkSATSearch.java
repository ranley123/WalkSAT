import java.util.*;

public class WalkSATSearch {
    private String filename; // the file to be read
    private int numVar = 0; // the number of variables
    private int numClause = 0; // the number of clauses
    private int flipTimes = 0; // the number of filps made
    double p; // the p value for pick var
    private Random random;
    private long avg_flip_frequency = 0; // flip times per second
    private long runningTime = 0;

    private ArrayList<Clause> clauses; // all clauses
    private HashMap<Integer, Integer> assignmentMap; // map literal to its truth value
    private ArrayList<Clause> falseClauses; // a list of false clauses
    private HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap;

    public static void main(String [] args){
        if(args.length != 1){
            System.out.println("Usage: java WalkSAT [inputFile]");
        }
        String filename = args[0];
        WalkSATSearch walkSAT = new WalkSATSearch(filename);
        walkSAT.run();
    }

    public WalkSATSearch(String filename){
        // initialise variables
        clauses = new ArrayList<>();
        falseClauses = new ArrayList<>();
        assignmentMap = new HashMap<>();
        clausesContainingLiteralMap = new HashMap<>();
        p = 0.4;
        random = new Random();
        this.filename = filename;
    }

    public int getNumVar(){
        return numVar;
    }

    public int getNumClause(){
        return numClause;
    }

    public int getFlipTimes(){
        return flipTimes;
    }

    public HashMap<Integer, Integer> getSolution(){
        return assignmentMap;
    }

    /**
     * set the assignmentMap for evaluation
     * @param assignmentMap
     */
    public void initialiseForEval(HashMap<Integer, Integer> assignmentMap){
        this.assignmentMap = assignmentMap;
        Helper.initialiseClauseAssignment(clauses, assignmentMap);

        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);
        falseClauses = Helper.updateFalseClauses(clauses);
    }

    /**
     * read a file and extract information
     */
    public void readFile(){
        Helper.readFile(filename);
        numVar = Helper.numVar;
        numClause = Helper.numClause;
        clauses = Helper.clauses;
    }

    public void initialise(){
        Helper.initialiseAssignmentMap(assignmentMap, numVar);
        Helper.initialiseClauseAssignment(clauses, assignmentMap);

        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);
        falseClauses = Helper.updateFalseClauses(clauses);
    }

    /**
     * starts to search for a solution
     */
    public void search(){
        while(!completed()){
            // 1. get a random false clause
            Clause curClause = Helper.getRandomFalseClause(falseClauses);
            // 2. get a possibility for pickVar or random choosing var
            double r = random.nextDouble();
            int var;

            // 3. starts to choose var
            if(r > p){
                var = pickVar(curClause);
            }
            else{
                var = random.nextInt(numVar) + 1;
            }

            // 4. starts to flip that var in all clauses
            flip(var);
        }
        System.out.println("Flips: " + flipTimes);
//        System.out.println("p: " + p);
        System.out.println("Average flip frequency: " + avg_flip_frequency);

        System.out.println("Solution: " + assignmentMap);

//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }
        System.out.println("Verifier: " + Helper.verifier(assignmentMap, clauses));
    }

    /**
     * the whole process of searching
     */
    public void run(){
        readFile();
        long startTime = System.nanoTime();
        initialise();
        search();

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        runningTime = totalTime;
//        System.out.println("runningtime: " + runningTime);


//        System.out.println(assignmentMap);
//        System.out.println(falseClauses);
//        System.out.println(clausesContainingLiteralMap);x
//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }
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
     *
     * @param var
     */
    private void flip(int var){
        long startTime = System.nanoTime(); // starts to record time

        flipTimes++;
        // update assignmentMap
        assignmentMap.put(var, assignmentMap.get(var) == 1? 0 : 1);
        assignmentMap.put(var * -1, assignmentMap.get(var * -1) == 1? 0 : 1);

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

        // update all data structures
//        falseClauses = Helper.updateFalseClauses(clauses);

        // calculate running time
        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        long frequency = 1000000000/totalTime;
//        System.out.println("flip frequency: " + frequency);
//        avg_flip_frequency += frequency;
        if(flipTimes == 1){
            avg_flip_frequency = frequency;
        }
    }

    /**
     * pick the variable that leads to greatest (make - break) in the current clause
     * @param clause    - the variable is chosen from this clause
     * @return          - the variable that to be flipped
     */
    private int pickVar(Clause clause){
        int maxDiff = Integer.MIN_VALUE; // the score
        int var = 0; // the variable to be flipped

        for(Integer literal: clause.getLiterals()){
            int makeX = 0;
            int breakX = 0;
//            System.out.println(literal);
            for(Clause c: clausesContainingLiteralMap.get(literal)){
                // the flip leads to a new satisfied clause
                if (c.getNumSatisfiedLiterals() == 0){
                    makeX++;
                }
            }
            for(Clause c: clausesContainingLiteralMap.get(literal * -1)){
                // the flip leads to a new unsatisfied clause
                if(c.getNumSatisfiedLiterals() == 1){
                    breakX++;
                }
            }
            int diff = makeX - breakX;
            if(maxDiff < diff){
                maxDiff = diff;
                var = literal;
            }
        }
        return var;

    }

}
