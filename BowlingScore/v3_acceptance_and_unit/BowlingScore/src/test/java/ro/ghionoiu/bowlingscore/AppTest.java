/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.ghionoiu.bowlingscore;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Ignore;

/**
 *
 * @author Iulian Ghionoiu <iulian.ghionoiu@exenne.ro>
 */
public class AppTest {
    
    //~~~~~~~~~~~~~~ Integration Tests ~~~~~~~~
    
    @Test
    public void IT_gutter_game_score_is_0() {
        int[] rolls = rollAllAs(0);
        
        int gameScore = computeGameScore(rolls);
        
        assertThat(gameScore, is(0));
    }
    
    @Ignore
    @Test
    public void IT_game_with_all_one_score_is_20() {
        int[] rolls = rollAllAs(1);
        
        int gameScore = computeGameScore(rolls);
        
        assertThat(gameScore, is(20));
    }
    
    //~~~~~~~~~~~~~~ Unit Tests ~~~~~~~~
    
    
    
    
    //~~~~~~~~~~~~~~ Test helpers ~~~~~~~~

    protected int[] rollAllAs(int rollValue) {
        int[] rolls = new int[10];
        for (int i = 0; i < rolls.length; i++) {
            rolls[i] = rollValue;
        }
        return rolls;
    }
    
    //~~~~~~~~~~~~~~ Production ~~~~~~~~
    
    protected int computeGameScore(int[] rolls) {
        return 0;
    }
}