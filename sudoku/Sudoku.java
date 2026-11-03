import java.util.ArrayDeque;

/**
 * Sudoku solver me Breadth-First Search (BFS) + backtracking.
 *
 * Si funksionon kerkimi:
 *  - Nje "nyje" eshte nje tabele Sudoku e mbushur pjeserisht (81 numra, 0 = qelize bosh).
 *  - Nga çdo nyje marrim qelizen e pare bosh dhe krijojme nje nyje-femije per çdo vlere
 *    qe lejohet aty (qe s'perseritet ne rresht, kolone, bllok 3x3).
 *  - BFS perdor FIFO queue: zgjeron te gjitha nyjet e nje niveli para se te kaloje te tjetri.
 *    (Niveli = sa qeliza jane mbushur.) Nyja e pare pa qeliza boshe eshte zgjidhja.
 *  - Backtracking: nese nje qelize nuk ka asnje vlere te lejuar, ajo nyje nuk krijon femije
 *    dhe dega braktiset.
 *
 * Qeliza e radhes eshte gjithmone e para bosh (rresht pas rreshti); nuk perdoret asnje heuristike.
 */
public class Sudoku {
    static final int SIZE = 9;
    static final int CELLS = SIZE * SIZE;
    static final int BOX = 3;

    // Tabelat e shembullit; 0 = qelize bosh.
    static final String EASY =
            "530070000600195000098000060800060003400803001700020006060000280000419005000080079";
    static final String MEDIUM =
            "009040030080090720100000000804000006003700000200600500007903000000020000000000068";
    static final String HARD =
            "800000000003600000070090200050007000000045700000100030001000068008500010090000400";

    static long expandedNodes;       // sa nyje u zgjeruan
    static int largestQueueSize;     // sa nyje maksimumi ne queue (tregon memorien e perdorur)

    /** Kthen bitmasken e vlerave te lejuara per qelizen: bit v = vlera v lejohet. */
    static int allowedValues(byte[] board, int cell) {
        int row = cell / SIZE, col = cell % SIZE;
        int usedMask = 0;
        for (int i = 0; i < SIZE; i++) {
            usedMask |= 1 << board[row * SIZE + i]; // e njejta rresht
            usedMask |= 1 << board[i * SIZE + col]; // e njejta kolone
        }
        int boxRow = row / BOX * BOX, boxCol = col / BOX * BOX;
        for (int i = 0; i < BOX; i++)
            for (int j = 0; j < BOX; j++)
                usedMask |= 1 << board[(boxRow + i) * SIZE + boxCol + j]; // i njejti bllok 3x3
        return ~usedMask & 0b1111111110; // mbahen vetem bitet 1..9
    }

    /** A eshte tabela fillestare pa konflikte? */
    static boolean hasNoConflicts(byte[] board) {
        for (int cell = 0; cell < CELLS; cell++) {
            if (board[cell] == 0) continue;
            byte value = board[cell];
            board[cell] = 0; // e heqim perkohesisht qe te mos konfliktoje me veten
            boolean allowed = (allowedValues(board, cell) & (1 << value)) != 0;
            board[cell] = value;
            if (!allowed) return false;
        }
        return true;
    }

    /** Kthen tabelen e zgjidhur, ose null nese nuk ka zgjidhje. */
    static byte[] solveWithBfs(byte[] startBoard) {
        if (!hasNoConflicts(startBoard)) return null;

        ArrayDeque<byte[]> queue = new ArrayDeque<>();
        queue.add(startBoard);
        expandedNodes = 0;
        largestQueueSize = 1;

        while (!queue.isEmpty()) {
            byte[] board = queue.poll();
            expandedNodes++;

            // qeliza e radhes: e para bosh ne rend rresht pas rreshti
            int chosenCell = -1, chosenMask = 0;
            for (int cell = 0; cell < CELLS; cell++) {
                if (board[cell] == 0) {
                    chosenCell = cell;
                    chosenMask = allowedValues(board, cell);
                    break;
                }
            }

            if (chosenCell == -1) return board; // asnje qelize bosh: zgjidhja u gjet

            for (int value = 1; value <= SIZE; value++) {
                if ((chosenMask & (1 << value)) == 0) continue;
                byte[] child = board.clone();
                child[chosenCell] = (byte) value;
                queue.add(child);
            }
            largestQueueSize = Math.max(largestQueueSize, queue.size());
        }
        return null;
    }

    /** Kontroll i pavarur: çdo rresht, kolone dhe bllok 3x3 permban 1..9 saktesisht nje here. */
    static boolean isValidSolution(byte[] board) {
        for (int unit = 0; unit < SIZE; unit++) {
            int rowMask = 0, colMask = 0, boxMask = 0;
            for (int i = 0; i < SIZE; i++) {
                rowMask |= 1 << board[unit * SIZE + i];
                colMask |= 1 << board[i * SIZE + unit];
                int boxRow = unit / BOX * BOX + i / BOX, boxCol = unit % BOX * BOX + i % BOX;
                boxMask |= 1 << board[boxRow * SIZE + boxCol];
            }
            if (rowMask != 0b1111111110 || colMask != 0b1111111110 || boxMask != 0b1111111110) return false;
        }
        return true;
    }

    /** Kthen tekstin 81-karakterësh ne tabele (çdo karakter tjeter pervec 1-9 = qelize bosh). */
    static byte[] parseBoard(String text) {
        byte[] board = new byte[CELLS];
        for (int i = 0; i < CELLS; i++) {
            char c = text.charAt(i);
            board[i] = (byte) (c >= '1' && c <= '9' ? c - '0' : 0);
        }
        return board;
    }

    static void printBoard(byte[] board) {
        for (int row = 0; row < SIZE; row++) {
            if (row % BOX == 0 && row > 0) System.out.println("------+-------+------");
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < SIZE; col++) {
                if (col % BOX == 0 && col > 0) line.append("| ");
                byte value = board[row * SIZE + col];
                line.append(value == 0 ? "." : String.valueOf(value)).append(' ');
            }
            System.out.println(line.toString().trim());
        }
    }

    static void solveAndPrint(String levelName, String puzzleText) {
        System.out.println("=== " + levelName + " ===");
        byte[] startBoard = parseBoard(puzzleText);
        printBoard(startBoard);

        long startTime = System.nanoTime();
        byte[] solution;
        try {
            solution = solveWithBfs(startBoard);
        } catch (OutOfMemoryError e) {
            // BFS ruan gjithe nivelin ne memorie; per tabela me shume qeliza boshe mund te mos mjaftoje.
            System.out.println("Memoria mbaroi: BFS ruan gjithe nivelin ne queue (kufizim i algoritmit).");
            return;
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        System.out.println();
        if (solution == null) {
            System.out.println("Nuk ka zgjidhje.");
        } else {
            printBoard(solution);
            System.out.println("Verifikimi: " + (isValidSolution(solution) ? "OK" : "GABIM"));
        }
        System.out.println("Nyje te zgjeruara: " + expandedNodes + ", queue maksimale: " + largestQueueSize
                + ", koha: " + elapsedMs + " ms\n");
    }

    public static void main(String[] args) {
        // Perdorimi: java Sudoku [easy|medium|hard|<81 karaktere>]; pa argument ekzekutohen te treja.
        if (args.length == 0) {
            solveAndPrint("Easy", EASY);
            solveAndPrint("Medium", MEDIUM);
            solveAndPrint("Hard", HARD);
        } else if (args[0].equalsIgnoreCase("easy")) {
            solveAndPrint("Easy", EASY);
        } else if (args[0].equalsIgnoreCase("medium")) {
            solveAndPrint("Medium", MEDIUM);
        } else if (args[0].equalsIgnoreCase("hard")) {
            solveAndPrint("Hard", HARD);
        } else if (args[0].length() == CELLS) {
            solveAndPrint("Custom", args[0]);
        } else {
            System.out.println("Argument i pavlefshem. Jepni easy, medium, hard ose 81 karaktere (0 ose . = bosh).");
        }
    }
}