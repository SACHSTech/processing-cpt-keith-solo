import processing.core.PApplet;

public class TetrisGame extends PApplet {
    private final int cols = 10;
    private final int rows = 20;
    private final int blockSize = 30;

    private int[][] grid;
    private int currentShape;
    private int currentRotation;
    private int currentX, currentY;

    private int score;
    private int level;
    private int speed;

    private int lastMoveTime;

    public static void main(String[] args) {
        PApplet.main("TetrisGame");
    }

    public void settings() {
        size(cols * blockSize, rows * blockSize);
    }

    public void setup() {
        grid = new int[cols][rows];
        newShape();

        score = 0;
        level = 1;
        speed = 1000; // Initial speed in milliseconds (1 second)
        lastMoveTime = millis();
    }

    public void draw() {
        background(0);

        drawGrid();
        drawShape();

        // Move the shape based on the speed
        if (millis() - lastMoveTime > speed) {
            if (canMove(0, 1)) {
                move(0, 1);
            } else {
                placeShape();
                clearLines();
                newShape();
            }
            lastMoveTime = millis();
        }

        // Display score and level
        fill(255);
        textSize(20);
        text("Score: " + score, 20, 30);
        text("Level: " + level, 20, 60);
    }

    public void keyPressed() {
        if (keyCode == LEFT && canMove(-1, 0)) {
            move(-1, 0);
        } else if (keyCode == RIGHT && canMove(1, 0)) {
            move(1, 0);
        } else if (keyCode == DOWN && canMove(0, 1)) {
            move(0, 1);
        } else if (keyCode == UP) {
            rotate();
        }
    }

    private void drawGrid() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                int x = i * blockSize;
                int y = j * blockSize;
                if (grid[i][j] != 0) {
                    fill(255);
                    rect(x, y, blockSize, blockSize);
                }
            }
        }
    }

    private void drawShape() {
        for (int i = 0; i < 4; i++) {
            int x = (currentX + shapes[currentShape][currentRotation][i][0]) * blockSize;
            int y = (currentY + shapes[currentShape][currentRotation][i][1]) * blockSize;
            fill(255, 0, 0);
            rect(x, y, blockSize, blockSize);
        }
    }

    private boolean canMove(int xOffset, int yOffset) {
        for (int i = 0; i < 4; i++) {
            int x = currentX + xOffset + shapes[currentShape][currentRotation][i][0];
            int y = currentY + yOffset + shapes[currentShape][currentRotation][i][1];

            if (x < 0 || x >= cols || y >= rows) {
                return false;
            }

            if (y >= 0 && grid[x][y] != 0) {
                return false;
            }
        }

        return true;
    }

    private void move(int xOffset, int yOffset) {
        if (canMove(xOffset, yOffset)) {
            currentX += xOffset;
            currentY += yOffset;
        }
    }

    private void placeShape() {
        for (int i = 0; i < 4; i++) {
            int x = currentX + shapes[currentShape][currentRotation][i][0];
            int y = currentY + shapes[currentShape][currentRotation][i][1];
            grid[x][y] = 1;
        }
    }

    private void clearLines() {
        int linesCleared = 0;
        for (int j = rows - 1; j >= 0; j--) {
            boolean lineFull = true;
            for (int i = 0; i < cols; i++) {
                if (grid[i][j] == 0) {
                    lineFull = false;
                    break;
                }
            }

            if (lineFull) {
                linesCleared++;
                for (int k = j; k > 0; k--) {
                    for (int i = 0; i < cols; i++) {
                        grid[i][k] = grid[i][k - 1];
                    }
                }
                // Clear the top line
                for (int i = 0; i < cols; i++) {
                    grid[i][0] = 0;
                }
            }
        }

        // Update score and level
        score += linesCleared * 100;
        if (score >= level * 500) {
            level++;
            speed -= 100; // Decrease speed by 100 milliseconds
        }
    }

    private void rotate() {
        int newRotation = (currentRotation + 1) % 4;
        if (canRotate(newRotation)) {
            currentRotation = newRotation;
        }
    }

    private boolean canRotate(int newRotation) {
        for (int i = 0; i < 4; i++) {
            int x = currentX + shapes[currentShape][newRotation][i][0];
            int y = currentY + shapes[currentShape][newRotation][i][1];

            if (x < 0 || x >= cols || y >= rows) {
                return false;
            }

            if (y >= 0 && grid[x][y] != 0) {
                return false;
            }
        }

        return true;
    }

    private void newShape() {
        currentShape = (int) random(7);
        currentRotation = 0;
        currentX = cols / 2 - 1;
        currentY = 0;

        if (!canMove(0, 0)) {
            // Game over, restart
            grid = new int[cols][rows];
            score = 0;
            level = 1;
            speed = 1000; // Reset speed
            lastMoveTime = millis();
        }
    }

    // Tetris shapes
    private final int[][][][] shapes = {
            // I
            {
                    {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
                    {{-1, 0}, {0, 0}, {1, 0}, {2, 0}},
                    {{0, -2}, {0, -1}, {0, 0}, {0, 1}},
                    {{-2, 0}, {-1, 0}, {0, 0}, {1, 0}}
            },
            // J
            {
                    {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
                    {{-1, 0}, {0, 0}, {1, 0}, {1, -1}},
                    {{0, -1}, {0, 0}, {0, 1}, {1, 1}},
                    {{-1, 1}, {-1, 0}, {0, 0}, {1, 0}}
            },
            // L
            {
                    {{-1, 1}, {0, 1}, {0, 0}, {0, -1}},
                    {{-1, 0}, {0, 0}, {1, 0}, {1, 1}},
                    {{0, -1}, {0, 0}, {0, 1}, {1, -1}},
                    {{-1, -1}, {-1, 0}, {0, 0}, {1, 0}}
            },
            // O
            {
                    {{0, -1}, {0, 0}, {1, -1}, {1, 0}},
                    {{0, -1}, {0, 0}, {1, -1}, {1, 0}},
                    {{0, -1}, {0, 0}, {1, -1}, {1, 0}},
                    {{0, -1}, {0, 0}, {1, -1}, {1, 0}}
            },
            // S
            {
                    {{-1, 0}, {-1, 1}, {0, -1}, {0, 0}},
                    {{-1, -1}, {0, -1}, {0, 0}, {1, 0}},
                    {{-1, 0}, {-1, 1}, {0, -1}, {0, 0}},
                    {{-1, -1}, {0, -1}, {0, 0}, {1, 0}}
            },
            // T
            {
                    {{-1, 0}, {0, 0}, {1, 0}, {0, -1}},
                    {{0, -1}, {0, 0}, {0, 1}, {1, 0}},
                    {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
                    {{-1, 0}, {0, 0}, {0, -1}, {0, 1}}
            },
            // Z
            {
                    {{-1, -1}, {0, -1}, {0, 0}, {1, 0}},
                    {{-1, 1}, {-1, 0}, {0, 0}, {0, -1}},
                    {{-1, -1}, {0, -1}, {0, 0}, {1, 0}},
                    {{-1, 1}, {-1, 0}, {0, 0}, {0, -1}}
            }
    };
}
