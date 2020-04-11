import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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
}
