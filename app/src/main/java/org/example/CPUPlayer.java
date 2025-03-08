package org.example;

import java.util.ArrayList;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait 
// être le cas)
public class CPUPlayer {

    // Contient le nombre de noeuds visités (le nombre
    // d'appel à la fonction MinMax ou Alpha Beta)
    // Normalement, la variable devrait être incrémentée
    // au début de votre MinMax ou Alpha Beta.
    private int numExploredNodes;
    private Mark mySide;


    // Le constructeur reçoit en paramètre le
    // joueur MAX (X ou O)
    public CPUPlayer(Mark cpu) {
    this.mySide=cpu;
    }

    // Ne pas changer cette méthode
    public int  getNumOfExploredNodes() {
        return numExploredNodes;
    }

    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveMinMax(Board board,Move lastMove) {
        numExploredNodes = 0;
        ArrayList<Move> meilleurMoves = new ArrayList<>();
        int highscore = Integer.MIN_VALUE;
        for (Move move: board.getPossibleMoves(mySide,lastMove)){
             board.play(move,this.mySide);
             numExploredNodes++;
             //this.turn=board.flip(turn);
             int score= minMax(board, 0,board.flip(mySide), move);
             //this.turn=board.flip(turn);
             board.undo(move);
 
             if(score>highscore){
                 highscore=score;
                 meilleurMoves.clear();
             meilleurMoves.add(move);
             }else if(highscore==score){
                 meilleurMoves.add(move);
             }
             
         }
     
 return meilleurMoves;
    }

    public int minMax(Board board,int profondeur,Mark turn, Move lastMove)
    {
     int score= board.evaluate(mySide);   
     if(score==100||score==-100 ) { //||board.isFull() pas oublier de ajouter le .isFull()
        return score;
    }

    if(mySide==turn){
    int highscore = Integer.MIN_VALUE;
    for (Move move: board.getPossibleMoves(mySide,lastMove)){
        board.play(move,this.mySide);
             this.numExploredNodes++;
            score=minMax(board,profondeur+1,board.flip(turn),move);
             board.undo(move);
             if (score>highscore) {
                 highscore=score;
             }
          
    }
             return highscore;
         }else{
            int lowscore = Integer.MAX_VALUE;
            for (Move move: board.getPossibleMoves(mySide,lastMove)){
                board.play(move,this.mySide);
                     this.numExploredNodes++;
                    score=minMax(board,profondeur+1,board.flip(turn),move);
                     board.undo(move);
                     if (score<lowscore) {
                         lowscore=score;
                     }
                
            }
                     return lowscore;
                    }
        }
     

    // Retourne la liste des coups possibles.  Cette liste contient
    // plusieurs coups possibles si et seuleument si plusieurs coups
    // ont le même score.
    public ArrayList<Move> getNextMoveAB(Board board,Move lastMove) {
        numExploredNodes = 0;
        ArrayList<Move> meilleurMoves = new ArrayList<>();
        int highscore = Integer.MIN_VALUE;
        for (Move move: board.getPossibleMoves(mySide,lastMove)){
         
        
             board.play(move,this.mySide);
             numExploredNodes++;
             //this.turn=board.flip(turn);
             int score= AB(board, 0,Integer.MIN_VALUE,Integer.MAX_VALUE,board.flip(this.mySide),move);
             //this.turn=board.flip(turn);
             board.undo(move);
             if(score>highscore){
                 highscore=score;
                 meilleurMoves.clear();
             meilleurMoves.add(move);
             }else if(highscore==score){
                 meilleurMoves.add(move);
             }
             }
             return meilleurMoves;
         }

         public int AB(Board board,int profondeur,int alpha,int beta,Mark turn,Move lastMove){
            int score= board.evaluate(this.mySide);   
            if(score==100||score==-100) { //||board.isFull() pas oublier de ajouter le .isFull()
               return score; 
           }
        
           if(mySide==turn){
           int highscore = Integer.MIN_VALUE;
           for (Move move: board.getPossibleMoves(mySide,lastMove)){
                    board.play(move,turn);
                    this.numExploredNodes++;
                   // this.turn=board.flip(turn);
                   score=AB(board,profondeur+1,alpha,beta,board.flip(turn),move);
                  // this.turn=board.flip(turn);
                    board.undo(move);
                 highscore=Math.max(highscore,score);
                 alpha=Math.max(alpha, highscore);
                    if(beta<=alpha){
                        
                        return highscore;
                    
                   }
               }
           
                    return highscore;
                }else{
                   int lowscore = Integer.MAX_VALUE;
                   for (Move move: board.getPossibleMoves(mySide,lastMove)){
                            board.play(move,turn);
                            this.numExploredNodes++;
                            //this.turn=board.flip(turn);
                           score=AB(board,profondeur+1,alpha,beta,board.flip(turn),move);
                           //this.turn=board.flip(turn);
                            board.undo(move);
                          lowscore=Math.min(score, lowscore);
                        beta=Math.min(lowscore,beta);
                            if(beta<=alpha){
                            
                                return lowscore;
                            }
                        }
                   
                       return lowscore;
                   }
                }
     

         
 
}
