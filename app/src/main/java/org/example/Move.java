package org.example;

public class Move {
    private int row;
    private int col;

    public Move(){
        row = -1;
        col = -1;
    }

    public Move(int r, int c){
        row = r;
        col = c;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public void setRow(int r){
        row = r;
    }

    public void setCol(int c){
        col = c;
    }

    @Override
    public String toString() {
        return "" + (char)(row + 'A') + (char)('1' - (col - 8));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (o instanceof Move move) {
            return row == move.row && col == move.col;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + row;
        result = prime * result + col;
        return result;
    }

}
