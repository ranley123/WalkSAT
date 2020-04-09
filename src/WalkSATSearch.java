import java.util.*;

public class WalkSATSearch {
    private int numVar = 0;
    private int numClause = 0;
    private int flipTimes = 0;
    private double p;
    private Random random;
    private long avg_flip_frequency = 0;

    private ArrayList<Clause> clauses;
    private HashMap<Integer, Integer> assignmentMap;
    private ArrayList<Clause> falseClauses;
    private HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap;

    public static void main(String [] args){
        WalkSATSearch walkSAT = new WalkSATSearch();
        walkSAT.run();
    }

    public WalkSATSearch(){
        // initialise variables
        clauses = new ArrayList<>();
        falseClauses = new ArrayList<>();
        assignmentMap = new HashMap<>();
        clausesContainingLiteralMap = new HashMap<>();
        p = 0.2;
        random = new Random();
    }

    private void run(){
        // read input CNF
        Helper.readFile("input.txt");
        numVar = Helper.numVar;
        numClause = Helper.numClause;
        clauses = Helper.clauses;

        Helper.initialiseAssignmentMap(assignmentMap, numVar);
        Helper.initialiseClauseAssignment(clauses, assignmentMap);

        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);
        falseClauses = Helper.updateFalseClauses(clauses);

//        System.out.println(assignmentMap);
//        System.out.println(falseClauses);
//        System.out.println(clausesContainingLiteralMap);x
//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }

        while(!completed()){
            Clause curClause = Helper.getRandomFalseClause(falseClauses);
            double r = random.nextDouble();
            int var;

            if(r > p){
                var = pickVar(curClause);
            }
            else{
                var = random.nextInt(numVar) + 1;
            }
            flip(var);
        }
        System.out.println("Flips: " + flipTimes);
        System.out.println("Average flip frequency: " + avg_flip_frequency/flipTimes);

        System.out.println("Solution: " + assignmentMap);

//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }
        System.out.println("Verifier: " + Helper.verifier(assignmentMap, clauses));

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
        long startTime = System.nanoTime();

        flipTimes++;
        assignmentMap.put(var, assignmentMap.get(var) == 1? 0 : 1);
        assignmentMap.put(var * -1, assignmentMap.get(var * -1) == 1? 0 : 1);

//        initialiseClauseAssignment();
        for(Clause clause: clauses){
            clause.flipAt(var);
        }


        falseClauses = Helper.updateFalseClauses(clauses);
        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        long frequency = 1000000000/totalTime;
        System.out.println("flip frequency: " + frequency);
        avg_flip_frequency += frequency;
    }

    private int pickVar(Clause clause){
        int maxDiff = Integer.MIN_VALUE;
        int var = 0;

        for(Integer literal: clause.getLiterals()){
            int makeX = 0;
            int breakX = 0;
//            System.out.println(literal);
            for(Clause c: clausesContainingLiteralMap.get(literal)){
                if (c.getNumSatisfiedLiterals() == 0){
                    makeX++;
                }
            }
            for(Clause c: clausesContainingLiteralMap.get(literal * -1)){
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
