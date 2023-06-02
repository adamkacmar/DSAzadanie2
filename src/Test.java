import java.sql.SQLOutput;

public class Test {
    public static void main(String[] args) {
        double start, end, elapsed;
        int minVariables = 13, maxVariables = 17;
        int sample = 100;

        System.out.println("----- BDD Create -----");

        for (int i = minVariables; i <= maxVariables; i++) {
            long nodes = 0;
            long allNodes = (long) Math.pow(2, i+1) - 1;
            start = System.nanoTime();
            for (int j = 0; j < sample; j++) {
                String testFunction = DNFBuilder.generate(i);

                BDDNode root = new BDDNode(testFunction);
                BDD test = new BDD(root);

                String testOrder = test.getVariables(testFunction);

                test.BDD_create(testFunction, testOrder);
                test.checkCorrect(test);

                nodes += test.getNodes();
            }
            end = System.nanoTime();
            elapsed = (end-start)/1000000/100;

            double reduction = (double) nodes/(allNodes*100);

            System.out.println("Variables: " + i + ", Time Elapsed: " + elapsed + "ms");
            System.out.println("Reduction: " + (1-reduction)*100 + "%\n");

        }

        System.out.println();
        System.out.println("----- BDD Create With Best Order -----");

        for (int i = minVariables; i <= maxVariables; i++) {
            long nodes = 0;
            long allNodes = (long) Math.pow(2, i+1) - 1;
            start = System.nanoTime();
            for (int j = 0; j < sample; j++) {
                String testFunction = DNFBuilder.generate(i);

                BDDNode root = new BDDNode(testFunction);
                BDD test = new BDD(root);

                test.BDD_create_with_best_order(testFunction);
                test.checkCorrect(test);

                nodes += test.getNodes();
            }
            end = System.nanoTime();
            elapsed = (end-start)/1000000/100;

            double reduction = (double) nodes/(allNodes*100);

            System.out.println("Variables: " + i + ", Time Elapsed: " + elapsed + "ms.");
            System.out.println("Reduction: " + (1-reduction)*100 + "%\n");
        }

        System.out.println();
        System.out.println("----- BDD Create With Best Order vs. BDD Create -----");

        for (int i = minVariables; i <= maxVariables; i++) {
            double nodes = 0, bestNodes = 0;
            long allNodes = (long) Math.pow(2, i+1) - 1;
            //start = System.nanoTime();
            for (int j = 0; j < sample; j++) {
                String testFunction = DNFBuilder.generate(i);

                BDDNode root = new BDDNode(testFunction);
                BDDNode bestRoot = new BDDNode(testFunction);
                BDD test = new BDD(root);
                BDD bestTest = new BDD(bestRoot);

                String testOrder = test.getVariables(testFunction);

                test.BDD_create(testFunction, testOrder);
                test.checkCorrect(test);

                bestTest.BDD_create_with_best_order(testFunction);
                bestTest.checkCorrect(bestTest);

                nodes += test.getNodes();
                bestNodes += bestTest.getNodes();
            }
            //end = System.nanoTime();
            //elapsed = Math.round((end-start)/1000000/100);

            double reduction = (double) nodes/(allNodes*100);
            double bestReduction = (double) bestNodes/(allNodes*100);

            System.out.println("Variables: " + i);
            System.out.println("BDD Create: " + (1-reduction)*100 + "%");
            System.out.println("BDD Create With Best Order: " + (1-bestReduction)*100 + "%");
            System.out.println("Differnce: " + (((1-bestReduction)*100)-((1-reduction)*100)) + "%\n");
            System.out.println("BDD Create Average Nodes: " + nodes/100);
            System.out.println("BDD Create With Best Order Average Nodes: " + bestNodes/100);
            System.out.println("Reduced Nodes With Best Order: " + (1-bestNodes/nodes)*100 + "%\n");
        }

    }
}