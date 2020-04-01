import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main {
    private int numVar = 0;
    private int numClause = 0;
    private boolean initialised = false;
    private int flipTimes = 0;
    private double p;
    private Random random;

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
        readFile("./src/input.txt");

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

        System.out.println("Solution: " + assignmentMap.values());

        for(Clause clause: clauses){
            System.out.println(clause.assignment);
        }
    }


    /**
     * reads input file which contains CNF formula
     * @param filename
     */
    private void readFile(String filename){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = "";
            int index = 0;

            // read line by line
            while((line = reader.readLine()) != null){
                String[] words = line.split(" ");
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
        assignmentMap.put(Math.abs(var), assignmentMap.get(Math.abs(var)) == 1? 0 : 1);

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
        int max = Integer.MIN_VALUE;
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
            int temp = makeX - breakX;
            if(max < temp){
                max = temp;
                var = literal;
            }
        }
        return var;

    }

}
