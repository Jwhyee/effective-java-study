package ka.chapter3.item15.search;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BreadthFirstSearch {
    private int[][] map;
    private boolean[][] visited;
    private int maxDir;
    private int[] dx = {1, 0, -1, 0, -1, 1, -1, 1};
    private int[] dy = {0, -1, 0, 1, -1, -1, 1, 1};
    private int H, W;

    public BreadthFirstSearch(int height, int width, int maxDir) {
        this.H = height;
        this.W = width;
        this.map = new int[height][width];
        this.visited = new boolean[height][width];
        this.maxDir = maxDir;
    }

    public void queueBfs(int y, int x) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node(x, y));

        while (!queue.isEmpty()) {
            Node cur = queue.poll();

            visited[cur.y][cur.x] = true;

            for (int i = 0; i < maxDir; i++) {
                int nx = cur.x + dx[i];
                int ny = cur.y + dy[i];

                if (nx >= 0 && nx < W && ny >= 0 && ny < H) {
                    if (!visited[ny][nx] && map[ny][nx] == 1) {
                        queue.offer(new Node(nx, ny));
                    }
                }
            }
        }
    }
}
