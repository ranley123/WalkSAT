import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Eval {

    public static void main(String[] args){


//        gsatSearch.readFile();
//        Helper.initialiseAssignmentMap(assignmentMap, gsatSearch.getNumVar());
//        System.out.println("Random Assignment: " + assignmentMap);
//        gsatSearch.initialiseForEval(new HashMap<>(assignmentMap));
//        System.out.println("-----------GSAT Search-----------");
//        gsatSearch.search();
//        System.out.println();
        Eval eval = new Eval();
        eval.runMultipleP();

//        System.out.println("-----------Summary-----------");
//        System.out.println("Minimum Flips: " + eval.getMinFlips(assignmentMap, walkSATSearch.getSolution()));
    }

    private int getMinFlips(HashMap<Integer, Integer> randomAssignment, HashMap<Integer, Integer> assignment){
        ArrayList<Integer> randomValues = new ArrayList<>(randomAssignment.values());
        ArrayList<Integer> trueValues = new ArrayList<>(assignment.values());
        int counter = 0;

        for(int i = 0; i < randomValues.size(); i++){
            if(randomValues.get(i) != trueValues.get(i))
                counter++;
        }
        return counter / 2;
    }

    private void writeOutput(String filename, String line){
        try{
            FileWriter writer = new FileWriter(filename, true);
            writer.append(line);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void runMultipleSearch(){
        int i = 0;
        while(i < 20) {
            i++;
            StringBuilder sb = new StringBuilder();

            WalkSATSearch walkSATSearch = new WalkSATSearch("input.txt");
            GSATSearch gsatSearch = new GSATSearch();
            NoveltySearch noveltySearch = new NoveltySearch();
            HashMap<Integer, Integer> assignmentMap = new HashMap<>();

            System.out.println("-----------WalkSAT Search-----------");
            walkSATSearch.readFile();
            Helper.initialiseAssignmentMap(assignmentMap, walkSATSearch.getNumVar());
            walkSATSearch.initialiseForEval(new HashMap<>(assignmentMap));
            walkSATSearch.search();
            System.out.println();
//
            System.out.println("-----------Novelty Search-----------");
            noveltySearch.readFile();
            noveltySearch.initialiseForEval(new HashMap<>(assignmentMap));
            noveltySearch.search();
            System.out.println();

            sb.append(walkSATSearch.getFlipTimes() + ",");
            sb.append(noveltySearch.getFlipTimes() + "");
            sb.append("\n");
            writeOutput("eval.csv", sb.toString());
        }
    }

    private void runMultipleP(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i < 20) {
//                StringBuilder sb = new StringBuilder();
            double p = 0.1;
            WalkSATSearch walkSATSearch = new WalkSATSearch("input.txt");
            HashMap<Integer, Integer> assignmentMap = new HashMap<>();
            System.out.println("-----------WalkSAT Search-----------");
            walkSATSearch.readFile();
            Helper.initialiseAssignmentMap(assignmentMap, walkSATSearch.getNumVar());
            walkSATSearch.initialiseForEval(new HashMap<>(assignmentMap));
            sb = new StringBuilder();

            while(p < 0.9){
                walkSATSearch = new WalkSATSearch("input.txt");
                walkSATSearch.p = p;
                System.out.println("p value: " + p);
                walkSATSearch.readFile();
                walkSATSearch.initialiseForEval(new HashMap<>(assignmentMap));

                walkSATSearch.search();
                sb.append(walkSATSearch.getFlipTimes() + ",");
                System.out.println();

                p += 0.1;
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
            writeOutput("p-eval.csv", sb.toString());
            i++;
        }
    }
}
