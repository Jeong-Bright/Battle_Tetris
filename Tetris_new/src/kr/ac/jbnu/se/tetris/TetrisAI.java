package kr.ac.jbnu.se.tetris;

import java.util.LinkedList;
import java.util.Queue;

public class TetrisAI {

    private BoardAI board;

    private Queue<Shape> shapeQueue;
    private Queue<String> routeQueue;

    private boolean[][][] visited;
    
    private Shape curPiece;

    // 0 : 왼쪽 / 1 : 오른쪽 / 2 : 아래 / 3 : right 회전
    private String bestRoute;

    private int maxWeight;
    
    private int[] dx, dy;
    
    public TetrisAI(BoardAI board) {
        this.board = board;
        dx = new int[] { -1, 1, 0, 0 };
        dy = new int[] { 0, 0, -1, 1 };
    }

    public String findBestRoute() throws CloneNotSupportedException {
        initFindRoute();

        while(!shapeQueue.isEmpty()) {
            Shape piece = (Shape) shapeQueue.poll().clone();
            String curRoute = routeQueue.poll();

            int curX = piece.curX();
            int curY = piece.curY();

            findRoute_move(piece, curRoute, curX, curY);
            fineRoute_rotate(piece, curRoute, curX, curY);
        }
        
        return bestRoute;
    }

    
    private void initFindRoute() throws CloneNotSupportedException {
        shapeQueue = new LinkedList<>();
        routeQueue = new LinkedList<>();
        
        maxWeight = -1000;
        bestRoute = "";
        visited = new boolean[board.BoardHeight][board.BoardWidth][4];
        curPiece = (Shape) board.curPiece.clone();

        for(int i = 0; i < 4; i++){
            visited[curPiece.curY()][curPiece.curX()][curPiece.getRotateIndex()] = true;

            routeQueue.add(bestRoute);
            bestRoute += "3";
            
            shapeQueue.add(curPiece);
            curPiece = curPiece.rotateRight();
        }
    }

    private void findRoute_move(Shape piece, String curRoute, int curX, int curY) throws CloneNotSupportedException {
        for(int i = 0; i < 3; i++) {
            Shape nPiece = (Shape) piece.clone();
            int nX = curX + dx[i];
            int nY = curY + dy[i];
            
            if(nX < 0 || nX >= board.BoardWidth || nY < 0 || nY >= board.BoardHeight)
                continue;
            if(visited[nY][nX][nPiece.getRotateIndex()])
                continue;
            if(!board.tryMove(nPiece, nX, nY)){
                int weight = getWeight(nPiece);
                if(maxWeight <= weight){
                    bestRoute = curRoute;
                    maxWeight = weight;
                }
                continue;
            }

            nPiece.moveTo(nX, nY);
            visited[nY][nX][nPiece.getRotateIndex()] = true;

            routeQueue.add(curRoute + Integer.toString(i));
            shapeQueue.add(nPiece);
        }
    }

    private void fineRoute_rotate(Shape piece, String curRoute, int curX, int curY) {
        Shape rPiece = piece;
        for(int i = 0; i < 3; i++) {
            rPiece = rPiece.rotateRight();
        
            if(visited[curY][curX][rPiece.getRotateIndex()])
                continue;
            if(!board.tryMove(rPiece, curX, curY)){
                rPiece = rPiece.rotateLeft();
                int weight = getWeight(rPiece);
                if(maxWeight < weight){
                    bestRoute = curRoute;
                    maxWeight = weight;
                }
                break;
            }

            visited[curY][curX][rPiece.getRotateIndex()] = true;

            routeQueue.add(curRoute + "3");
            shapeQueue.add(rPiece);
        }
    }

    private int getWeight(Shape nPiece){
        boolean[][] isCurPiece = new boolean[board.BoardHeight][board.BoardWidth];

        for(int i = 0; i < 4; i++){
            int blockX = nPiece.curX() + nPiece.x(i);
            int blockY = nPiece.curY() - nPiece.y(i);

            if(blockX < 0 || blockX >= board.BoardWidth || blockY < 0 || blockY >= board.BoardHeight)
                return -1001;
            
            isCurPiece[blockY][blockX] = true;
        }

        int weight = 0;
        int[][] boardWeight = board.getBoardWeight();

        for(int i = 0; i < 4; i++){
            int blockX = nPiece.curX() + nPiece.x(i);
            int blockY = nPiece.curY() - nPiece.y(i);
            
            for(int j = 0; j < 4; j++){ 
                int x = blockX + dx[j];
                int y = blockY + dy[j];

                if(x + 1 < 0 || x > board.BoardWidth || y + 1 < 0 || y > board.BoardHeight)
                    return -1001;

                if(x + 1 == 0 || x == board.BoardWidth || y + 1 == 0 || y == board.BoardHeight) {
                    weight += boardWeight[y + 1][x + 1];
                    continue;
                }

                if(board.board[y][x] == Tetrominoes.NoShape) {
                    weight -= boardWeight[y + 1][x + 1];
                }
                else if(!isCurPiece[y][x] && board.board[y][x] != Tetrominoes.NoShape) {
                    weight += boardWeight[y + 1][x + 1];
                }
            }
        }
        
        return weight;
    }
}
