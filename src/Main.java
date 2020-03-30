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
    private ArrayList<Clause> clauses;
    private boolean initialised = false;
    private HashMap<Integer, Integer> assignmentMap;
    private ArrayList<Clause> falseClauses;
    private HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap;
    private int flipTimes = 0;
    private double p;
    private Random random;

    public static void main(String [] args){
        Main walkSAT = new Main();
        walkSAT.run();

    }

    public Main(){
        clauses = new ArrayList<>();
        falseClauses = new ArrayList<>();
        assignmentMap = new HashMap<>();
        clausesContainingLiteralMap = new HashMap<>();
        p = 0.1;
        random = new Random();
    }

    private void run(){
        readFile("./src/input.txt");
        initialiseAssignmentMap();
        updateClauseAssignment();

        initialiseClausesContainingLiteral();
        updateFalseClauses();

//        System.out.println(assignmentMap);
        System.out.println(falseClauses);
//        System.out.println(clausesContainingLiteralMap);
        for(Clause clause: clauses){
            System.out.println(clause.assignment);
        }
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



    private void readFile(String filename){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = "";
            int index = 0;

            while((line = reader.readLine()) != null){
                String[] words = line.split(" ");
                if(words[0].equals("c")){
                    continue;
                }
                else if(!words[0].equals("c") && !initialised){
                    initialised = true;
                    numVar = Integer.parseInt(words[2]);
                    numClause = Integer.parseInt(words[3]);
                }
                else{
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

    private void initialiseAssignmentMap(){
        Random random = new Random();
        for(int i = 1; i < numVar + 1; i++){
            int temp = random.nextInt(2);
            assignmentMap.put(i, temp);
        }
    }

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

    private void updateFalseClauses(){
        falseClauses = new ArrayList<>();

        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
            if(clause.getNumSatisfiedLiterals() == 0){
                falseClauses.add(clause);
            }

        }
    }

    private void updateClauseAssignment(){
        for(Clause clause: clauses){
            clause.updateAssignment(assignmentMap);
        }
    }

    private boolean completed(){
        for(Clause clause: clauses){
            if(clause.getNumSatisfiedLiterals() == 0)
                return false;
        }
        return true;
    }

    private void flip(int var){
        flipTimes++;
        assignmentMap.put(Math.abs(var), assignmentMap.get(Math.abs(var)) == 1? 0 : 1);

        updateClauseAssignment();
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
