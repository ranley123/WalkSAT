import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GSATSearch {
    private int numVar = 0;
    private int numClause = 0;
    private int flipTimes = 0;
    private Random random;
    private long avg_flip_frequency = 0;

    private ArrayList<Clause> clauses;
    private HashMap<Integer, Integer> assignmentMap;
    private ArrayList<Clause> falseClauses;
    private HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap;

    public static void main(String[] args){
        GSATSearch gsat = new GSATSearch();
        gsat.run();
    }

    public GSATSearch(){
        clauses = new ArrayList<>();
        falseClauses = new ArrayList<>();
        assignmentMap = new HashMap<>();
        clausesContainingLiteralMap = new HashMap<>();
        random = new Random();
    }

    private void run(){
        Helper.readFile("input.txt");
        numVar = Helper.numVar;
        numClause = Helper.numClause;
        clauses = Helper.clauses;

        Helper.initialiseAssignmentMap(assignmentMap, numVar);
        Helper.initialiseClauseAssignment(clauses, assignmentMap);

        Helper.initialiseClausesContainingLiteral(numVar, clauses, clausesContainingLiteralMap);
        falseClauses = Helper.updateFalseClauses(clauses);

        while(!completed()){
            int var = pickVar();
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

    private boolean completed(){
        return falseClauses.size() == 0;
    }

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

    private int pickVar(){
        int maxDiff = Integer.MIN_VALUE;
        int var = 0;

        for(int literal = 1; literal < numVar + 1; literal++){
            int before = 0;
            int after = 0;
//            System.out.println(literal);
            for(Clause c: clausesContainingLiteralMap.get(literal)){
                if (c.getNumSatisfiedLiterals() == 0){
                    after++;
                }
                else if(c.getVarAssignment(literal) ==  1 && c.getNumSatisfiedLiterals() == 1){
                    before++;
                }
            }
            for(Clause c: clausesContainingLiteralMap.get(literal * -1)){
                if (c.getNumSatisfiedLiterals() == 0){
                    after++;
                }
                else if(c.getVarAssignment(literal * -1) ==  1 && c.getNumSatisfiedLiterals() == 1){
                    before++;
                }
            }
            int diff = after - before;
            if(maxDiff < diff){
                maxDiff = diff;
                var = literal;
            }
        }
        return var;

    }
}
