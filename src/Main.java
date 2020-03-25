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
    private int flipTimes;

    public static void main(String [] args){
        Main walkSAT = new Main();
        walkSAT.run();

    }

    public Main(){
        clauses = new ArrayList<>();
        falseClauses = new ArrayList<>();
        assignmentMap = new HashMap<>();
        clausesContainingLiteralMap = new HashMap<>();
    }

    private void run(){
        readFile("./src/input.txt");
        initialiseAssignmentMap();
        updateClauseAssignment();

        initialiseClausesContainingLiteral();
        initialiseFalseClauses();

        System.out.println(assignmentMap);
        System.out.println(falseClauses);
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
            assignmentMap.put(i, random.nextInt(2));
        }
    }

    private void initialiseClausesContainingLiteral(){
        for(int i = 1; i < numVar + 1; i++){
            ArrayList<Clause> temp = new ArrayList<>();
            for(Clause clause: clauses){
                if (clause.hasLiteral(i) || clause.hasLiteral(i * -1)){
                    temp.add(clause);
                }
            }
            clausesContainingLiteralMap.put(i, temp);
        }
    }

    private void initialiseFalseClauses(){
        for(Clause clause: clauses){
            System.out.println(clause.assignment);
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
        int counter = 0;
        for(Clause clause: clauses){
            if(clause.getNumSatisfiedLiterals() == 0)
                return false;
        }
        return true;
    }

    private void flip(int var){
        flipTimes++;
        assignmentMap.put(var, assignmentMap.get(var) == 1? 0 : 1);
        updateClauseAssignment();
    }

}
