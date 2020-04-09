import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Helper {
    public static int numVar = 0;
    public static int numClause = 0;
    public static ArrayList<Clause> clauses = new ArrayList<>();

    public static void readFile(String filename){
        try{
            InputStream in = Helper.class.getResourceAsStream(filename);
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
                else if(words[0].equals("p")){
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

    public static boolean verifier(HashMap<Integer, Integer> solution, ArrayList<Clause> clauses){
        for(Clause clause: clauses){
            if(is_satisfied(solution, clause) == false){
                return false;
            }
        }
        return true;
    }

    public static boolean is_satisfied(HashMap<Integer, Integer> solution, Clause clause){
        for(Integer i: clause.getLiterals()){
            if(solution.get(i) == 1)
                return true;
        }
        return false;
    }

    public static void initialiseAssignmentMap(HashMap<Integer, Integer> assignmentMap, int numVar){
        Random random = new Random();
        for(int i = 1; i < numVar + 1; i++){
            int temp = random.nextInt(2);
            assignmentMap.put(i, temp);
        }
        for(int i = -1 * numVar; i < 0; i++){
            assignmentMap.put(i, assignmentMap.get(-1 * i) == 1? 0 : 1);
        }
    }

    public static void initialiseClauseAssignment(ArrayList<Clause> clauses, HashMap<Integer, Integer>assignmentMap){
        for(Clause clause: clauses){
            clause.updateAssignment(assignmentMap);
        }
    }

    public static void initialiseClausesContainingLiteral(int numVar, ArrayList<Clause> clauses, HashMap<Integer, ArrayList<Clause>> clausesContainingLiteralMap){
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

    public static ArrayList<Clause> updateFalseClauses(ArrayList<Clause> clauses){
        ArrayList<Clause> falseClauses = new ArrayList<>();
        for(Clause clause: clauses){
//            System.out.println(clause.assignment);
            if(clause.getNumSatisfiedLiterals() == 0){
                falseClauses.add(clause);
            }
        }
        return falseClauses;
    }

    public static Clause getRandomFalseClause(ArrayList<Clause> falseClauses){
        Random random = new Random();
        if(falseClauses.size() == 0)
            return null;
        int index = random.nextInt(falseClauses.size());
        return falseClauses.get(index);
    }
}
