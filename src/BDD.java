import java.util.*;

public class BDD {
    private BDDNode root;
    private String order;
    private int nodes;
    public List<String> existingNodes = new ArrayList<>();

    public int getNodes() {
        return this.nodes;
    }

    public void setNodes(int nodes) {
        this.nodes = nodes;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public BDDNode getRoot() {
        return this.root;
    }

    public void setRoot(BDDNode root) {
        this.root = root;
    }

    public List<String> getExistingNodes() { return existingNodes; }

    public void setExistingNodes(List<String> existingNodes) { this.existingNodes = existingNodes; }

    public double existingNodesSize() {
        return this.existingNodes.size();
    }

    public void resetExistingNodes() {
        this.existingNodes = new ArrayList<>();
    }

    BDD() {
        this.root = null;
    }

    BDD(BDDNode root) {
        this.root = root;
    }
    public BDD BDD_create(String function, String order) {
        Map<String, BDDNode> hashMap = new HashMap<>();
        BDDNode root = new BDDNode(function);

        root = add(root, function, order, hashMap);

        BDD diagram = new BDD(root);

        setRoot(root);
        setNodes(hashMap.size());
        setOrder(order);

        return diagram;
    }

    public BDD BDD_create_with_best_order(String function) {
        BDDNode bestRoot = new BDDNode();
        int bestNodes = -1;
        String bestOrder = null;
        List<String> bestAllNodes = new ArrayList<>();

        BDDNode currentRoot = new BDDNode(function);
        int currentNodes;
        String currentOrder;
        List<String> currentAllNodes = new ArrayList<>();

        BDD currentDiagram = new BDD(currentRoot);
        String functionVariables = getVariables(function);

        for (int i = 0; i < 3*functionVariables.length(); i++) {
            currentOrder = shuffleString(functionVariables);
            currentDiagram.BDD_create(function, currentOrder);
            //System.out.println(currentOrder); PRINTNE MOMENTALNY ORDER

            currentNodes = (int) currentDiagram.existingNodesSize();
            //System.out.println(currentNodes); PRINTNE MOMENTALNY POCET NODOV PRE ORDER

            if (bestNodes == -1 || currentNodes < bestNodes) {
                bestNodes = currentDiagram.getNodes();
                bestOrder = currentDiagram.getOrder();
                bestRoot = currentDiagram.getRoot();
                bestAllNodes = currentDiagram.getExistingNodes();
            }
            currentDiagram.resetExistingNodes();
        }
        BDD bestOrderDiagram = new BDD(bestRoot);

        setRoot(bestRoot);
        setOrder(bestOrder);
        setNodes(bestNodes);
        setExistingNodes(bestAllNodes);

        return bestOrderDiagram;
    }

    public BDDNode add(BDDNode parent, String function, String order, Map<String, BDDNode> hashMap) {
        if (!hashMap.containsKey(parent.getCurrentExpression())) {
            hashMap.put(parent.getCurrentExpression(), parent);
            existingNodes.add(parent.getCurrentExpression());
        }
        else { // REDUKCIA I
            return hashMap.get(parent.getCurrentExpression());
        }
        if (parent.getCurrentExpression().equals("0") || parent.getCurrentExpression().equals("1")) {
            return parent;
        }
        if (order == null || order.isEmpty()) {
            return parent;
        }
        parent.setOrder(order);
        char variable = order.charAt(0);
        String newOrder = removeFirstCharacter(order);

        String functionVariableTrue = evaluateVariable(function, variable, 1);
        String functionVariableFalse = evaluateVariable(function, variable, 0);

        if (functionVariableFalse.equals(functionVariableTrue)) { // REDUKCIA S
            hashMap.remove(parent.getCurrentExpression());
            existingNodes.remove(parent.getCurrentExpression());
            parent.setCurrentExpression(functionVariableFalse);
            parent = add(parent, parent.getCurrentExpression(), newOrder, hashMap);
            return parent;
        }

        BDDNode childTrue = new BDDNode(functionVariableTrue);
        BDDNode childFalse = new BDDNode(functionVariableFalse);

        parent.setLeftChild(add(childFalse, functionVariableFalse, newOrder, hashMap));
        parent.setRightChild(add(childTrue, functionVariableTrue, newOrder, hashMap));

        return parent;
    }

    public char BDD_use(BDD diagram, String entries) {
        BDDNode root = diagram.getRoot();
        return use(root, entries);
    }

    public char use(BDDNode node, String entries) {
        if (node == null) {
            return 'X';
        }
        if (node.getCurrentExpression().equals("0")) {
            return '0';
        } else if (node.getCurrentExpression().equals("1")) {
            return '1';
        } else {
            while (entries.length() > node.getOrder().length()) {
                entries = removeFirstCharacter(entries);
            }

            char entry = entries.charAt(0);
            String newEntry = removeFirstCharacter(entries);

            if (entry == '0') {
                return use(node.getLeftChild(), newEntry);
            } else if (entry == '1') {
                return use(node.getRightChild(), newEntry);
            } else {
                System.out.println("Znak nebol 0 alebo 1.");
                return 'X';
            }
        }
    }

    // pomocne funkcie
    public static String removeFirstCharacter(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        return string.substring(1);
    }

    public String getVariables(String input) {
        Set<Character> uniqueVariables = new HashSet<>();
        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                uniqueVariables.add(c);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (char c : uniqueVariables) {
            sb.append(c);
        }
        return sb.toString();
    }


    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for(char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder sb = new StringBuilder();
        for(char c : characters) {
            sb.append(c);
        }
        return sb.toString();
    }

    public char evaluateDNF(String function, String order, String terminals) {
        String[] blocks = function.split("\\+");
        boolean functionValue = false;
        for (String block : blocks) {
            boolean blockValue = true;
            for (int i = 0; i < order.length(); i++) {
                char letter = order.charAt(i);
                int terminal;
                if (block.contains("" + letter)) {
                    if (block.contains("!" + letter)) {
                        terminal = terminals.charAt(i) == '0' ? 1 : 0;
                    }
                    else {
                        terminal = terminals.charAt(i) == '0' ? 0 : 1;
                    }
                    if (terminal == 0) {
                        blockValue = false;
                    }
                }
            }
            if (blockValue) {
                functionValue = true;
            }
        }
        return functionValue ? '1' : '0';
    }

    public void checkCorrect(BDD diagram) {
        BDDNode root = diagram.getRoot();
        String function = root.getCurrentExpression();
        String variables = getVariables(function);
        for (int i = 0; i < Math.pow(2, variables.length()); i++) {
            String binaryString = String.format("%" + variables.length() + "s", Integer.toBinaryString(i)).replace(' ', '0');
            char useResult = diagram.BDD_use(diagram, binaryString);
            char result = evaluateDNF(diagram.getRoot().getCurrentExpression(), diagram.getOrder(), binaryString);
            if (useResult != result) {
                System.out.println("Error for: " + binaryString + " in " + function + " for order " + root.getOrder() + ", useResult = " + useResult + ", result = " + result);
            }
        }
    }

    public String evaluateVariable(String function, char variable, int terminal) {
        String adjustedFunction = "";
        for (int i = 0; i < function.length(); i++) {
            char c = function.charAt(i);
            if (c == variable) {
                adjustedFunction += terminal;
            } else if (c == '!') {
                i++;
                if (function.charAt(i) != variable)
                    adjustedFunction += '!';
                c = function.charAt(i);
                if (c == variable) {
                    adjustedFunction += (terminal == 0 ? "1" : "0");
                } else {
                    adjustedFunction += c;
                }
            } else {
                adjustedFunction += c;
            }
        }

        String[] terms = adjustedFunction.split("\\+");

        List<String> adjustedTerms = new ArrayList<>();
        for (String term : terms) {
            if (term.equals(("1"))) {
                return "1";
            } else if (term.contains(".1")) {
                term = term.replace(".1", "");
                adjustedTerms.add(term);
            } else if (term.contains("1.")) {
                term = term.replace("1.", "");
                adjustedTerms.add(term);
            } else if (term.contains("0")) {
                continue;
            } else {
                adjustedTerms.add(term);
            }
        }

        if (adjustedTerms.isEmpty()) {
            adjustedTerms.add("0");
        }

        adjustedFunction = String.join("+", adjustedTerms);

        return adjustedFunction;
    }

    public void print(BDDNode node, Map<String, BDDNode> hashMap) {
        if (node == null) {
            return;
        }
        // Funguje len po určitý počet premenných.
        print(node.getLeftChild(), hashMap);
        System.out.println(node.getCurrentExpression() + " is " + hashMap.get(node.getCurrentExpression()));
        print(node.getRightChild(), hashMap);
    }
}
