import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Eval {

    public static void main(String[] args){
        Eval eval = new Eval();
//        eval.runMultipleSearch();
        eval.runMultipleP();
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
        int avg1 = 0;
        int avg2 = 0;
        long runningTime1 = 0;
        long runningTime2 = 0;
        int limit = 5;
        int numVar = 0;
        int numClause = 0;

        while(i < limit) {
            i++;

            WalkSATSearch walkSATSearch = new WalkSATSearch("input.txt");
            NoveltySearch noveltySearch = new NoveltySearch("input.txt");
            HashMap<Integer, Integer> assignmentMap = new HashMap<>();

            System.out.println("-----------WalkSAT Search-----------");
            walkSATSearch.readFile();
            numVar = walkSATSearch.getNumVar();
            numClause = walkSATSearch.getNumClause();

            Helper.initialiseAssignmentMap(assignmentMap, walkSATSearch.getNumVar());
            long startTime = System.nanoTime();
            walkSATSearch.initialiseForEval(new HashMap<>(assignmentMap));
            walkSATSearch.search();
            long endTime   = System.nanoTime();
            runningTime1 += (endTime - startTime);
            System.out.println();
//
            System.out.println("-----------Novelty Search-----------");
            noveltySearch.readFile();
            startTime = System.nanoTime();
            noveltySearch.initialiseForEval(new HashMap<>(assignmentMap));
            noveltySearch.search();
            endTime   = System.nanoTime();
            runningTime2 += (endTime - startTime);
            System.out.println();

            avg1 += walkSATSearch.getFlipTimes();
            avg2 += noveltySearch.getFlipTimes();

        }
        // avg flips of walk, avg flips of novelty, avg running time of walk, avg running time of novelty
        StringBuilder sb = new StringBuilder();
        sb.append(numVar + "," + numClause + ",");
        sb.append((avg1/limit) + ",");
        sb.append(avg2/limit);
        sb.append("\n");
        writeOutput("eval.csv", sb.toString());

        runningTime1 = runningTime1/1000000; // to milliseconds
        runningTime2 = runningTime2/1000000;
        sb = new StringBuilder();
        sb.append(numVar + "," + numClause + ",");
        sb.append(runningTime1/limit + ",");
        sb.append(runningTime2/limit);
        sb.append("\n");
        writeOutput("runningtime.csv", sb.toString());
    }

    private void runMultipleP(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i < 20) {
//                StringBuilder sb = new StringBuilder();
            double p = 0.1;
//            WalkSATSearch walkSATSearch = new WalkSATSearch("input.txt");
            HashMap<Integer, Integer> assignmentMap = new HashMap<>();
            NoveltySearch noveltySearch = new NoveltySearch("input.txt");
//            System.out.println("-----------WalkSAT Search-----------");
//            walkSATSearch.readFile();
            noveltySearch.readFile();
//            Helper.initialiseAssignmentMap(assignmentMap, walkSATSearch.getNumVar());
            Helper.initialiseAssignmentMap(assignmentMap, noveltySearch.getNumVar());
//            walkSATSearch.initialiseForEval(new HashMap<>(assignmentMap));
            noveltySearch.initialiseForEval(new HashMap<>(assignmentMap));
            sb = new StringBuilder();

            while(p < 0.9){
//                walkSATSearch = new WalkSATSearch("input.txt");
//                walkSATSearch.p = p;
//                System.out.println("p value: " + p);
//                walkSATSearch.readFile();
//                walkSATSearch.initialiseForEval(new HashMap<>(assignmentMap));
//
//                walkSATSearch.search();
//                sb.append(walkSATSearch.getFlipTimes() + ",");
//                System.out.println();
//
//                p += 0.1;

                noveltySearch = new NoveltySearch("input.txt");
                noveltySearch.p = p;
                System.out.println("p value: " + p);
                noveltySearch.readFile();
                noveltySearch.initialiseForEval(new HashMap<>(assignmentMap));

                noveltySearch.search();
                sb.append(noveltySearch.getFlipTimes() + ",");
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
