import java.util.*;

public class DNFBuilder {
    public static String generate(int numVars) {
        if (numVars < 1 || numVars > 26) {
            throw new IllegalArgumentException("Number of variables must be between 1 and 26");
        }

        List<String> variables = new ArrayList<>();
        for (int i = 0; i < numVars; i++) {
            variables.add(Character.toString((char)('A' + i)));
        }

        Collections.shuffle(variables);

        int numBlocks = 4 + (int)(Math.random() * 3); // Náhodne vyberie počet blokov medzi 4-7
        List<String> blocks = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            int blockSize = 1 + (int)(Math.random() * (numVars / numBlocks)); // Vyberie náhodnú veľkosť blokov
            List<String> blockVars = new ArrayList<>(variables.subList(i * blockSize, Math.min((i + 1) * blockSize, numVars)));
            Collections.sort(blockVars);
            blocks.add(String.join(".", blockVars));
        }

        // Checkne či sú všetky premenné použité a tie čo nie sú pridá do funkcie
        List<String> usedVariables = new ArrayList<>();
        for (String block : blocks) {
            String[] vars = block.split("\\.");
            usedVariables.addAll(Arrays.asList(vars));
        }
        for (String var : variables) {
            if (!usedVariables.contains(var)) {
                int randomBlockIndex = (int)(Math.random() * numBlocks);
                String block = blocks.get(randomBlockIndex);
                blocks.set(randomBlockIndex, block + "." + var);
            }
        }

        Collections.shuffle(blocks);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numBlocks; i++) {
            if (i > 0) {
                sb.append("+");
            }
            if (Math.random() < 0.5 && i < numBlocks - 1) { // Pridá negáciu ak to nie je posledný blok
                sb.append("!");
            }
            String block = blocks.get(i);
            if (block.startsWith("!")) { // Ak blok začína negáciou, odstráni ju a pridá ju osobitne.
                sb.append(block.substring(1));
                sb.append("+!").append(block.substring(1));
            } else {
                sb.append(block);
            }
        }

        return sb.toString();
    }
}