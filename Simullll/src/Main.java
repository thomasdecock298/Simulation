import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        //First declare the variables used in the for loop and the variables K = amount of specified trials, and nrCardsRequired = the amount of cards that are drawn from the deck
        int K = 10000;
        int nrCardsRequired = 5;

        double countSuccess = 0;
        int nrCardsPicked;
        int numberHearts;
        int numberSpades;
        int numberClubs;
        int numberDiamonds;
        int numberCardsInDeck;
        int r;
        int[] cardsInHand = new int[5];
        int i2;
        for(int k = 1; k <= K; k++){

            //The deck is reset for every iteration of the for loop.
            nrCardsPicked = 0;
            numberHearts = 13;
            numberSpades = 13;
            numberClubs = 13;
            numberDiamonds = 13;
            numberCardsInDeck = numberClubs + numberDiamonds + numberHearts + numberSpades;
            Arrays.fill(cardsInHand, 4); //fills the cardsInHand array with a dummy variable. Technically not needed since the do-while loop will fill it afterwards anyway.


            do {
                r = (int)(Math.random()*((numberCardsInDeck-nrCardsPicked-1)-0+1)+0); //https://www.educative.io/edpresso/how-to-generate-random-numbers-in-java

                //The deck is ordered with first all the hearts, then the spades and so on. We don't need to shuffle the deck since we just pick a random card anyway.
                if(r < numberHearts){
                    numberHearts--;
                    cardsInHand[nrCardsPicked] = 0; //0 represents hearts
                } else if(r < numberHearts + numberSpades){
                    numberSpades--;
                    cardsInHand[nrCardsPicked] = 1; //1 represents spades
                } else if(r < numberHearts + numberSpades + numberClubs){
                    numberClubs--;
                    cardsInHand[nrCardsPicked] = 2; //2 represents clubs
                } else{
                    numberDiamonds--;
                    cardsInHand[nrCardsPicked] = 3; //3 represents diamonds
                }
                nrCardsPicked++;
            }while(nrCardsPicked < nrCardsRequired);
            //i2 is used in the next for-loop to count the amount of cards with the same suit
            i2 = 0;
            for(int i1 = 1; i1 < nrCardsRequired; i1++){
                if(cardsInHand[i1] == cardsInHand[0]){
                    i2++;
                } else break; //this breaks the forloop that checks for a flush once there is 1 card that doesn't have the same suit
            }
            //since the above for-loop only iterates 4 times, we want i2 to be equal to 4 instead of 5
            //if this is satisfied, we count it using countSuccess, after which we move on to the next trial.
            if(i2 == nrCardsRequired-1){
                countSuccess++;
                System.out.println("countSuccess at iteration: " + k + ": " + countSuccess); //prints the trials for which a flush was drawn (for testing)
            }
        }
        System.out.println("countSuccess after for-loop: " + countSuccess);
        double average;
        average = countSuccess/K; //The average success rate is then calculated and printed out.
        System.out.print(average); //test
    }
}
/*
2 simulations with K = 1,000,000,000
0.001979652
0.001981672
https://socratic.org/questions/suppose-five-cards-are-drawn-from-a-deck-what-is-the-probability-of-obtaining-a-
According to this site: it should be 0.001980792
 */
