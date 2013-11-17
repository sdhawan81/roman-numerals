/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.ghionoiu.bowlingscore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

/**
 *
 * @author Iulian Ghionoiu <iulian.ghionoiu@exenne.ro>
 */
public class AppTest {
    public static final int NORMAL_ROLL = 2;
    public static final int MAXIMUM_ROLL = 10;


    
    //~~~~~~~~~~~~~~ Integration Tests ~~~~~~~~
    

    
    
    
    @Test
    public void IT_gutter_game() {
        RollsBuilder rollsBuilder = new RollsBuilder();
        rollsBuilder.rollMany(0, 20);
        
        int gameScore = computeGameScore(rollsBuilder);
        
        assertThat(gameScore, is(0));
    }
    
    @Test
    public void IT_all_ones_game() {
        RollsBuilder rollsBuilder = new RollsBuilder();
        rollsBuilder.rollMany(1, 20);
        
        int gameScore = computeGameScore(rollsBuilder);
        
        assertThat(gameScore, is(20));
    }
    
    @Ignore
    @Test
    public void IT_first_frame_spare_rest_is_one() {
        RollsBuilder rollsBuilder = new RollsBuilder();
        rollsBuilder.rollMany(5, 2);
        rollsBuilder.rollMany(1, 18);
        
        int gameScore = computeGameScore(rollsBuilder);
        
        assertThat(gameScore, is(29));
    }
    
    //~~~~~~~~~~~~~~ Integration Test helpers ~~~~~~~~
    
    protected int computeGameScore(RollsBuilder builder) {
        Game game = new Game();
        return game.computeScore(builder.build());
    }
    
    //~~~~~~~~~~~~~~ Unit Tests ~~~~~~~~
    
    @Test
    public void game_score_equals_sum_of_frames_score() {
        int[] frameScores = { 1, 10 };
        FrameExtractor frameExtractor = createMockedFrames(frameScores);
        Game game = new Game(frameScores.length, frameExtractor);
        
        int gameScore = game.computeScore(anyRolls());
        
        assertThat(gameScore, is(11));
    }
    
    //  __________ Frames definition _____
    
    
    @Test
    public void first_frame_starts_at_index_0() {
        Rolls rolls = someRolls();
        FrameExtractor frameExtractor = new FrameExtractor();
        
        Frame firstFrame = frameExtractor.getFrame(rolls, 0);
        
        assertThat(firstFrame.getStartingIndex(), is(0));
    }
    
    @Test
    public void first_frame_has_a_length_of_1_if_its_first_roll_is_maximum_value() {
        Rolls rolls = firstRollIsMaximumRoll();
        FrameExtractor frameExtractor = new FrameExtractor();
        
        Frame firstFrame = frameExtractor.getFrame(rolls, 0);
        
        assertThat(firstFrame.getLength(), is(1));
    }
        
    @Test
    public void first_frame_has_a_length_of_2_if_its_first_roll_is_maximum_value() {
        Rolls rolls = firstRollNotMaximumRoll();
        FrameExtractor frameExtractor = new FrameExtractor();
        
        Frame firstFrame = frameExtractor.getFrame(rolls, 0);
        
        assertThat(firstFrame.getLength(), is(2));
    }
    
        
    @Test
    public void frame_ends_at_starting_index_plus_length() {
        int startingIndex = 1;
        int length = 2;
        
        Frame frame = new Frame(someRolls(), startingIndex, length);
        
        assertThat(frame.getEndingIndex(), is(startingIndex+length));
    }
    
    @Test
    public void the_Nth_frame_starts_at_previous_frame_ending_index() {
        Rolls rolls = someRolls();
        FrameExtractor frameExtractor = new FrameExtractor();
        int currentFrameNumber = 1;
        
        Frame currentFrame = frameExtractor.getFrame(rolls, currentFrameNumber);
        
        Frame previousFrame = frameExtractor.getFrame(rolls, currentFrameNumber-1);
        assertThat(currentFrame.getStartingIndex(), is(previousFrame.getEndingIndex()));
    }
    
    //~~~~ Rolls subsets
    
    @Test
    public void frame_normal_score_is_sum_of_rolls_between_starting_and_ending_index() {
        Rolls rolls = Rolls.create(3, 2, 1, 0);
        Frame frame = new Frame(rolls, 0, 2);
        int expectedScore = 5;
        
        int normalScore = frame.getNormalScore();
        
        assertThat(normalScore, is(expectedScore));
    }
    
    @Test
    public void frame_score_equals_its_normal_score() {
        Rolls rolls = Rolls.create(3, 2, 1, 0);
        Frame frame = new Frame(rolls, 0, 2);
        
        int frameScore = frame.getScore();
        
        assertThat(frameScore, is(frame.getNormalScore()));
    }
         
    //~~~~~~~~~~~~~~ Unit Test helpers ~~~~~~~~

    protected Rolls anyRolls() {
        return mock(Rolls.class);
    }

    protected FrameExtractor createMockedFrames(int[] frameScores) {
        FrameExtractor frameExtractor = mock(FrameExtractor.class);
        for (int i = 0; i < frameScores.length; i++) {
            int frameScore = frameScores[i];
            Frame frame = mock(Frame.class);
            when(frame.getScore()).thenReturn(frameScore);
            when(frameExtractor.getFrame(any(Rolls.class), eq(i)))
                    .thenReturn(frame);
        }
        return frameExtractor;
    }
    
    protected Rolls someRolls() {
        return Rolls.create(NORMAL_ROLL, NORMAL_ROLL, NORMAL_ROLL, NORMAL_ROLL);
    }
    
    protected Rolls firstRollIsMaximumRoll() {
        return Rolls.create(MAXIMUM_ROLL, NORMAL_ROLL);
    }
    
    protected Rolls firstRollNotMaximumRoll() {
        return Rolls.create(NORMAL_ROLL, NORMAL_ROLL);
    }

    //~~~~~~~~~~~~~~ Production ~~~~~~~~
    
    static class Game {
        public static final int DEFAUT_NUMBER_OF_FRAMES = 10;
        private int numberOfFrames;
        private FrameExtractor frameExtractor;
        
        public Game() {
            this(DEFAUT_NUMBER_OF_FRAMES, new FrameExtractor());
        }
        
        public Game(FrameExtractor frameExtractor) {
            this(DEFAUT_NUMBER_OF_FRAMES, frameExtractor);
        }
        
        public Game(int numberOfFrames, FrameExtractor frameExtractor) {
            this.numberOfFrames = numberOfFrames;
            this.frameExtractor = frameExtractor;
        }
        
        public int computeScore(Rolls rolls) {
            int gameScore = 0;
            for (int i = 0; i < numberOfFrames; i++) {
                Frame frame = frameExtractor.getFrame(rolls, i);
                gameScore += frame.getScore();
            }

            return gameScore;
        }
        
        
        
    }
    
    static class Frame {
        Rolls rolls;
        int startingIndex;
        int length;

        public Frame(Rolls rolls, int startingIndex, int length) {
            this.rolls = rolls;
            this.startingIndex = startingIndex;
            this.length = length;
        }
        
        //~~ Location

        public int getStartingIndex() {
            return startingIndex;
        }

        public int getLength() {
            return length;
        }
        
        public int getEndingIndex() {
            return startingIndex + length;
        }
        
        //~~ Score
        
        public int getScore() {
            return getNormalScore();
        }
        
        protected int getNormalScore() {
            int startingPosition = this.getStartingIndex();
            int endingPosition = this.getEndingIndex();
            return rolls.getSumOfRolls(startingPosition, endingPosition);
        }
    }
    
    class RollsBuilder {
        List<Integer> rollsList;

        public RollsBuilder() {
            rollsList = new ArrayList<Integer>();
        }
        
        public void roll(int number) {
            rollsList.add(number);
        }
        
        
        public void rollMany(int number, int times) {
            for (int i = 0; i < times; i++) {
                roll(number);
            }
        }
        
        public Rolls build() {
           return Rolls.create(rollsList);
        }
    }
    
    static class Rolls {
        private List<Integer> backingList;
        
        private Rolls(List<Integer> rolls) {
            this.backingList = rolls;
        }
        
        public static Rolls create(Integer ... rolls) {
            List<Integer> list = Arrays.asList(rolls);
            return new Rolls(list);
        }
        
        public static Rolls create(List<Integer> rolls) {
            return new Rolls(rolls);
        }
        
        public int getValueAt(int index) {
            return backingList.get(index);
        }
        
        public int getSumOfRolls(int startingPosition, int endingPosition) {
            int sum = 0;
            for (int i = startingPosition; i < endingPosition; i++) {
                sum += this.getValueAt(i);
            }
            return sum;
        }
                
    }
    
    static class FrameExtractor {
        public Frame getFrame(Rolls rolls, int frameNumber) {
            int startingIndex;
            if (frameNumber == 0) {
                startingIndex = 0;
            } else {
                Frame previousFrame = getFrame(rolls, frameNumber-1);
                startingIndex = previousFrame.getEndingIndex();
            }
            
            return createFrame(rolls, startingIndex);
        }
        
        public int computeFrameLength(Rolls rolls, int startingPosition) {
            if (rolls.getValueAt(startingPosition) == MAXIMUM_ROLL) {
                return 1;
            } else {
                return 2;
            }
        }

        protected Frame createFrame(Rolls rolls,int startingIndex) {
            int length = computeFrameLength(rolls, startingIndex);
            return new Frame(rolls, startingIndex, length);
        }
    }
    
}