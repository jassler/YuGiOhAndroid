package com.kaasbrot.boehlersyugiohapp;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.kaasbrot.boehlersyugiohapp.history.History;
import com.kaasbrot.boehlersyugiohapp.history.Points;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.kaasbrot.boehlersyugiohapp", appContext.getPackageName());
    }

    /**
     * Test basic action history actions
     * - Setup for both players
     * - Points changing
     * - Undo / Redo
     */
    @Test
    public void actionHistoryTest() {
        History history = new History(8000, 8000);

        // check initial setup
        assertFalse(history.canUndo());
        assertFalse(history.canRedo());
        assertEquals(8000, ((Points) history.getCurrentEntry()).p1);
        assertEquals(8000, ((Points) history.getCurrentEntry()).p2);

        // undo shouldn't change anything
        Points p = history.undo();
        assertEquals(8000, p.p1);
        assertEquals(8000, p.p2);

        // redo shouldn't change anything
        p = history.redo();
        assertEquals(8000, p.p1);
        assertEquals(8000, p.p2);

        // player 2 lost points
        history.add(8000, 7000);
        p = (Points) history.getCurrentEntry();

        assertTrue(history.canUndo());
        assertFalse(history.canRedo());
        assertEquals(8000, ((Points) history.getCurrentEntry()).p1);
        assertEquals(7000, ((Points) history.getCurrentEntry()).p2);

        // undo last action
        p = history.undo();
        assertFalse(history.canUndo());
        assertTrue(history.canRedo());
        assertEquals(8000, ((Points) history.getCurrentEntry()).p1);
        assertEquals(8000, ((Points) history.getCurrentEntry()).p2);

        // redo last action
        p = history.redo();
        assertTrue(history.canUndo());
        assertFalse(history.canRedo());
        assertEquals(8000, ((Points) history.getCurrentEntry()).p1);
        assertEquals(7000, ((Points) history.getCurrentEntry()).p2);
    }
}
