import java.io.*;
import java.util.*;

public class Main {
    private int numVar = 0;
    private int numClause = 0;
    private boolean initialised = false;
    private int flipTimes = 0;
    private double p;
    private Random random;
    private long avg_flip_frequency = 0;

    private ArrayList<Clause> clauses;
    private HashMap<Integer, Integer> assignmentMap;
    private ArrayList<Clause> falseClauses;
    private HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap;

    public static void main(String [] args){
        Main walkSAT = new Main();
        walkSAT.run();
    }

    public Main(){
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
        readFile("input.txt");

        initialiseAssignmentMap();
        initialiseClauseAssignment();

        initialiseClausesContainingLiteral();
        updateFalseClauses();

//        System.out.println(assignmentMap);
//        System.out.println(falseClauses);
//        System.out.println(clausesContainingLiteralMap);x
//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }

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
            flip(var);
        }
        System.out.println("Flips: " + flipTimes);
        System.out.println("Average flip frequency: " + avg_flip_frequency/flipTimes);

        System.out.println("Solution: " + assignmentMap);

//        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
//        }
        System.out.println("Verifier: " + verifier(assignmentMap, clauses));

    }



    /**
     * reads input file which contains CNF formula
     * @param filename
     */
    private void readFile(String filename){
        try{
            InputStream in = getClass().getResourceAsStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            int index = 0;

            // read line by line
            while((line = reader.readLine()) != null){
                if(line.length() == 0)
                    continue;
                line = line.strip();
                String[] words = line.split("\\s+");
                if(words[0].equals("c")){ // extra information so don't need to be processed
                    continue;
                }
                else if(!words[0].equals("c") && !initialised){
                    initialised = true;
                    numVar = Integer.parseInt(words[2]);
                    numClause = Integer.parseInt(words[3]);
                }
                else{ // construct a new clause
                    Clause curClause = new Clause(index++);
                    for(String word: words){
                        int num = Integer.parseInt(word);
                        if(num != 0)
                            curClause.addLiteral(num);
                    }
                    clauses.add(curClause);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        long startTime = System.nanoTime();

        flipTimes++;
        assignmentMap.put(var, assignmentMap.get(var) == 1? 0 : 1);
        assignmentMap.put(var * -1, assignmentMap.get(var * -1) == 1? 0 : 1);

//        initialiseClauseAssignment();
        for(Clause clause: clauses){
            clause.flipAt(var);
        }

        updateFalseClauses();
        initialiseClausesContainingLiteral();

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        long frequency = 1000000000/totalTime;
        System.out.println("flip frequency: " + frequency);
        avg_flip_frequency += frequency;
    }

    private Clause getRandomFalseClause(){
        if(falseClauses.size() == 0)
            return null;
        int index = random.nextInt(falseClauses.size());
        return falseClauses.get(index);
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

    private boolean verifier(HashMap<Integer, Integer> solution, ArrayList<Clause> clauses){
        for(Clause clause: clauses){
            if(is_satisfied(solution, clause) == false){
                return false;
            }
        }
        return true;
    }

    private boolean is_satisfied(HashMap<Integer, Integer> solution, Clause clause){
        for(Integer i: clause.getLiterals()){
            if(solution.get(i) == 1)
                return true;
        }
        return false;
    }

}
