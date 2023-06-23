import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

     static class Player {
        private final String name;
        private int roundWins = 0;
        private int score = 0;
        private int card;


        public Player(String name) {
            this.name = name;
        }

        void setCard(int card) {
            this.card = card;
        }

        void winRound() {
            this.roundWins++;
        }

        int getScore() {
            return this.score;
        }
        void addScore(int score) {
            this.score += score;
        }
        String getName() {
            return this.name;
        }
        int getCardNumber() {
            return this.card;
        }


    }


    static class Game {
        private ArrayList<Player> playOrder = new ArrayList<>();
        private final String myPlayer;
        private ArrayList<Integer> deck = new ArrayList<>();


        Game() throws IOException{
            System.out.println("사용할 플레이어 이름을 저장하세요:\n");
            BufferedReader inputName = new BufferedReader(new InputStreamReader(System.in));

            this.myPlayer = inputName.readLine();
            System.out.println("게임의 인원수를 설정해주세요:\n");
            int number = Integer.parseInt(inputName.readLine());
            for (int i = 0; i < number; i++) {
                addAiPlayer();
            }

        }

        void addAiPlayer() {
            Random random = new Random();
            String generated_name = random.ints(97, 123)
                    .limit(5).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            this.playOrder.add(new Player(generated_name));



        }

        void setPlayOrder(int round_num) {
            if (round_num == 1) {
                Collections.sort(this.playOrder, new PlayerNameComparator());
            }
            else {
                Collections.sort(this.playOrder, new PlayerScoreComparator());
            }
        }
        void deckShuffle() {
            Collections.shuffle(this.deck);
        }

        void generateCard() {

            Random random = new Random();
            for (int i = 0; i < 30; i++) {
                int newCard = random.nextInt(14)+1;
                this.deck.add(newCard);
            }
        }

        void drawCard() {
            for (int i = 0; i < playOrder.size(); i++) {

                this.playOrder.get(i).setCard(this.deck.get(this.deck.size()-1));
                this.deck.remove(this.deck.size()-1);
                System.out.format("%s : %d\n", this.playOrder.get(i).getName() , this.playOrder.get(i).getCardNumber());

            }
        }

        void calScore() {
            int maxCard = this.playOrder
                    .stream()
                    .max(Comparator.comparing(player -> player.getCardNumber()))
                    .get().getCardNumber();
            System.out.format("%d\n", maxCard);
            int minCard = this.playOrder
                    .stream()
                    .min(Comparator.comparing(player -> player.getCardNumber()))
                    .get().getCardNumber();
            List<Player> winners = this.playOrder.stream().filter(player -> player.getCardNumber() == maxCard).collect(Collectors.toList());

            for (Player winner : winners) {
                winner.addScore(maxCard-minCard);
                winner.winRound();
                System.out.format("%s: %d score\n", winner.getName(), maxCard-minCard);
            }
        }

        void showCurrentScores() {
            int i = 1;
            for (Player player : this.playOrder) {

                System.out.format("%d. %s : %d score\n", i, player.getName(), player.getScore());
                i++;
            }
        }


        void playRound(int roundNum) {
            setPlayOrder(roundNum);
            deckShuffle();
            drawCard();
            calScore();
            showCurrentScores();



        }


        void startGame() {
            generateCard();
            for (int roundNum = 1; roundNum <= 4; roundNum++) {
                playRound(roundNum);
            }
        }




    }








    static class PlayerScoreComparator implements Comparator<Player>{
        @Override
        public int compare(Player o1, Player o2) {

            return o1.getScore()-o2.getScore();



        }
    }
    static class PlayerNameComparator implements Comparator<Player> {

        @Override
        public int compare(Player o1, Player o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
    public static void main(String[] args) throws IOException {
        Game game = new Game();
        game.startGame();

    }
}

