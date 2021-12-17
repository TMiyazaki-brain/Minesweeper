interface MineSweeperGUI {
    public void setTextToTile(int x, int y, String text);

    public void setOpencoler(int x, int y);

    public void setBomscoler(int x, int y);

    public void setFragscoler(int x, int y);

    public void removeFragscoler(int x, int y);

    public void win();

    public void lose();
}

public class MineSweeper {

    private final int height;
    private final int width;
    private final int numberOfTiles;
    private final int numberOfBombs;
    private final int[][] table;
    private final boolean[][] ftable;
    private final boolean[][] saikitable;
    private int openOfTiles;
    private boolean firstTile;

    public MineSweeper(int height, int width, int numberOfBombs) {
        this.height = height;
        this.width = width;
        this.numberOfTiles = height * width;
        this.numberOfBombs = numberOfBombs;
        this.table = new int[height][width];
        this.ftable = new boolean[height][width];
        this.saikitable = new boolean[height][width];
        this.openOfTiles = 0;
        this.firstTile = false;

        initTable();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    void initTable() {
        // 最初に0以外を引いた際に再度初期化するが、その場合の不具合の防止
        for (int line = 0; line <= width - 1; line++) {
            for (int row = 0; row <= height - 1; row++) {
                this.table[line][row] = 0;
            }
        }
        setBombs();
        // 地雷の数を調べる
        for (int line = 0; line <= width - 1; line++) {
            for (int row = 0; row <= height - 1; row++) {
                // 地雷のマスは調べない
                if (this.table[line][row] != 100) {
                    // 周囲８マスを調べる
                    for (int x = line - 1; x <= line + 1; x++) {
                        for (int y = row - 1; y <= row + 1; y++) {
                            // ９ｘ９マス以外は見ない
                            if ((0 <= x && x <= width - 1) && (0 <= y && y <= height - 1)) {
                                // 地雷の数をカウント
                                if (this.table[x][y] == 100) {
                                    this.table[line][row]++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void setBombs() {
        int bomline, bomRow;
        for (int i = 1; i <= numberOfBombs;) {
            bomline = (int) (Math.random() * 10 - 1);
            bomRow = (int) (Math.random() * 10 - 1);
            if (this.table[bomline][bomRow] != 100) {
                i++;
            }
            this.table[bomline][bomRow] = 100;
        }
    }

    public void openTile(int x, int y, MineSweeperGUI gui) {
        // 最初は安全なマスになるように処理
        if (this.firstTile == false && this.table[x][y] != 0) {
            while (this.table[x][y] != 0) {
                initTable();
            }
        }

        this.firstTile = true;

        if(ftable[x][y] == false)
            // 地雷を引いた際
            if (this.table[x][y] == 100) {
                openAllTiles(gui);
                gui.lose();
                // 周囲に地雷のないパネルを開く際
            } else if (this.table[x][y] == 0) {

                if (saikitable[x][y] == false) {
                    this.saikitable[x][y] = true;
                    gui.setOpencoler(x, y);
                    openOfTiles++;
                }
                for (int a = x - 1; a <= x + 1; a++) {
                    for (int b = y - 1; b <= y + 1; b++) {
                        if (x != a || y != b) {
                            if ((0 <= a && a <= width - 1) && (0 <= b && b <= height - 1)) {
                                if (this.table[a][b] != 0) {
                                    if (saikitable[x][y] == false) {
                                        saikitable[a][b] = true;
                                        openOfTiles++;
                                    }
                                }
                                if (saikitable[a][b] == false) {
                                    openTile(a, b, gui);
                                }
                            }
                        }
                    }
                }
                // 周囲に地雷があるパネルを開く際の処理
            } else {
                String c = String.valueOf(this.table[x][y]);
                gui.setTextToTile(x, y, c);
                if (saikitable[x][y] == false) {
                    saikitable[x][y] = true;
                    openOfTiles++;
                }
            }

        // ゲーム終了の判定
        if (openOfTiles == numberOfTiles - numberOfBombs) {
            gui.win();
        }
    }

    // 旗を立てる
    public void setFlag(int x, int y, MineSweeperGUI gui) {
        if (saikitable[x][y] == false) {
            if (ftable[x][y] == false) {
                gui.setFragscoler(x, y);
                gui.setTextToTile(x, y, "∇");
                ftable[x][y] = true;
            } else {
                gui.removeFragscoler(x, y);
                gui.setTextToTile(x, y, " ");
                ftable[x][y] = false;
            }
        }
    }

    private void openAllTiles(MineSweeperGUI gui) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (this.table[x][y] == 100) {
                    gui.setBomscoler(x, y);
                    gui.setTextToTile(x, y, "●～*");
                } else if (0 < this.table[x][y] && this.table[x][y] <= 8) {
                    String a = String.valueOf(this.table[x][y]);
                    gui.setTextToTile(x, y, a);
                } else {
                    gui.setOpencoler(x, y);
                }
            }
        }
    }
}