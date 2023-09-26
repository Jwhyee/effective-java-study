package ka.chapter3.item15.search;

import java.util.Stack;

public class DepthFirstSearch {
    private int[][] map;
    private boolean[][] visited;
    private int maxDir;
    private int[] dx = {1, 0, -1, 0, -1, 1, -1, 1};
    private int[] dy = {0, -1, 0, 1, -1, -1, 1, 1};
    private int H, W;

    public DepthFirstSearch(int height, int width, int maxDir) {
        this.H = height;
        this.W = width;
        this.map = new int[height][width];
        this.visited = new boolean[height][width];
        this.maxDir = maxDir;
    }

    public void recursionDfs(int y, int x) {
        visited[y][x] = true;

        for (int i = 0; i < maxDir; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];

            if (nx >= 0 && nx < W && ny >= 0 && ny < H) {
                if (!visited[ny][nx] && map[ny][nx] == 1) {
                    recursionDfs(ny, nx);
                }
            }
        }
    }

    public void stackDfs(int y, int x) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node(x, y));

        while (!stack.isEmpty()) {
            Node cur = stack.pop();

            visited[cur.y][cur.x] = true;

            for (int i = 0; i < maxDir; i++) {
                int nx = cur.x + dx[i];
                int ny = cur.y + dy[i];

                if (nx >= 0 && nx < W && ny >= 0 && ny < H) {
                    if (!visited[ny][nx] && map[ny][nx] == 1) {
                        stack.push(new Node(nx, ny));
                    }
                }
            }
        }
    }
}
