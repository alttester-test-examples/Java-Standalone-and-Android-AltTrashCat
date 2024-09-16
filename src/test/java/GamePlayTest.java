import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alttester.AltObject;
import com.alttester.AltDriver;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alttester.AltDriver;

import pages.GamePlayPage;
import pages.GetAnotherChancePage;
import pages.MainMenuPage;
import pages.PauseOverlayPage;

public class GamePlayTest {

    private static AltDriver driver;
    private static MainMenuPage mainMenuPage;
    private static PauseOverlayPage pauseOverlayPage;
    private static GetAnotherChancePage getAnotherChancePage;
    private static GamePlayPage gamePlayPage;

    @BeforeClass
    public static void setUp() throws IOException {
        driver = new AltDriver();
        mainMenuPage = new MainMenuPage(driver);
        gamePlayPage = new GamePlayPage(driver);
        pauseOverlayPage = new PauseOverlayPage(driver);
        getAnotherChancePage = new GetAnotherChancePage(driver);
    }

    @Before
    public void loadLevel() throws Exception {
        mainMenuPage.loadScene();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        driver.stop();
        Thread.sleep(1000);
    }

    @Test
    public void testGamePlayDisplayedCorrectly() {
        mainMenuPage.pressRun();
        assertTrue(gamePlayPage.isDisplayed());
    }

    @Test
    public void testGameCanBePausedAndResumed() {
        mainMenuPage.pressRun();
        gamePlayPage.pressPause();
        assertTrue(pauseOverlayPage.isDisplayed());

        pauseOverlayPage.pressResume();
        assertTrue(gamePlayPage.isDisplayed());
    }

    @Test
    public void testGameCanBePausedAndStopped() {
        mainMenuPage.pressRun();
        gamePlayPage.pressPause();
        pauseOverlayPage.pressMainMenu();
        assertTrue(mainMenuPage.isDisplayed());
    }

    @Test
    public void testAvoidingObstacles() throws Exception {
        mainMenuPage.pressRun();
        gamePlayPage.avoidObstacles(5);
        System.out.println("Current life after avoiding obstacles: " + gamePlayPage.getCurrentLife());
        assertTrue(gamePlayPage.getCurrentLife() > 0);
    }

    @Test
    public void testPlayerDiesWhenObstacleNotAvoided() throws Exception {
        mainMenuPage.pressRun();
        float timeout = 20;
        while (timeout > 0) {
            try {
                getAnotherChancePage.isDisplayed();
                break;
            } catch (Exception e) {
                timeout -= 1;
            }
        }
    }

    @Test
    public void testCollectFishesOnMiddleLane() throws Exception {
        mainMenuPage.pressRun();

        int collectedFishCount = 0;  // Contor pentru numărul de pești colectați manual
        int noOfLives = gamePlayPage.getCurrentLife();

        while (noOfLives > 0) {
            noOfLives = gamePlayPage.getCurrentLife();

            AltObject character = gamePlayPage.getCharacter();
            System.out.println("Pisica se află la Z: " + character.worldZ + ", X: " + character.worldX);

            List<AltObject> allFishbones = gamePlayPage.findAllFish();

            List<AltObject> toBeRemoved = new ArrayList<>();
            for (AltObject fish : allFishbones) {
                if (fish.worldZ < character.worldZ || fish.worldX != 0.0) {
                    toBeRemoved.add(fish);
                }
            }
            allFishbones.removeAll(toBeRemoved);

            System.out.println("   Pesti rămasi după filtrare: " + allFishbones.size());
            for (AltObject fish : allFishbones) {
                System.out.println("    Pesti rămasi după filtrare: " + fish.name + ", z: " + fish.worldZ + ", x: " + fish.worldX);
            }


            AltObject closestFish = allFishbones.get(0);
            System.out.println("   Closest fishbone: " + closestFish.name + ", z:" + closestFish.worldZ + ", x:" + closestFish.worldX);

            if (closestFish.worldX == character.worldX && closestFish.worldZ == character.worldZ) {
                collectedFishCount++;
                System.out.println("Pește colectat la Z: " + closestFish.worldZ + ", X: " + closestFish.worldX);
            }

        }
        int collectedCoins = gamePlayPage.getCollectedCoinsNumber();
        System.out.println("Numărul de pești colectați manual: " + collectedFishCount);
        System.out.println("Numărul de pești raportat de joc: " + collectedCoins);

        assertEquals("Numărul de pești colectați nu corespunde cu numărul raportat de joc!", collectedFishCount, collectedCoins);
    }


     @Test
     public void testDistanceRun() throws Exception {
         mainMenuPage.pressRun();

         AltObject characterStart = gamePlayPage.getCharacter();
         System.out.println("START: Pisica a pornit de la X: " + characterStart.worldX + ", Y: " + characterStart.worldY + ", Z: " + characterStart.worldZ);

         List<Float> zValues = new ArrayList<>();
         long startTime = System.currentTimeMillis();
         long duration = 25000; // 25 secunde
         long interval = 1000; // 1 secundă
         long nextTimestamp = startTime + interval;

         while (System.currentTimeMillis() - startTime < duration) {
             try {
                 getAnotherChancePage.isDisplayed();
                 break;
             } catch (Exception e) {
                 if (System.currentTimeMillis() >= nextTimestamp) {
                     AltObject character = gamePlayPage.getCharacter();
                     int distanceRun = gamePlayPage.getDistanceRun();
                     System.out.println("   Timestamp= " + (System.currentTimeMillis() - startTime) + "ms: Distanța afișată în joc:" + distanceRun + "; Pisica se află la X: " + character.worldX + ", Y: " + character.worldY + ", Z: " + character.worldZ);
                     zValues.add(character.worldZ);
                     nextTimestamp += interval;
                 }
             }
         }

         AltObject characterFinal = gamePlayPage.getCharacter();
         System.out.println("STOP: Pisica s-a oprit la X: " + characterFinal.worldX + ", Y: " + characterFinal.worldY + ", Z: " + characterFinal.worldZ);

         int distanceRun = gamePlayPage.getDistanceRun();
         System.out.println("Distanța afișată în joc: " + distanceRun);

         // Presupunem că valoarea de reset este de 100 și 200 (sau o valoare pe care o stabilești tu)
         float resetValue1 = 100.0f;
         float resetValue2 = 200.0f;

         // Determinăm dacă au avut loc reseturi
         int resetCount = 0;
         for (int i = 1; i < zValues.size(); i++) {
             if (zValues.get(i) < zValues.get(i - 1)) {
                 resetCount++;
                 System.out.println("Origin reset happened ");
             }
         }

         // Calculăm diferența între Z final și Z inițial, ținând cont de reseturi
         float zDistance = 0;
         if (resetCount == 1) {
             // Un singur reset
             zDistance = resetValue1 + characterFinal.worldZ - 2;
         } else if (resetCount == 2) {
             // Două reseturi
             zDistance = resetValue2 + characterFinal.worldZ - 2;
         } else {
             // Fără reset
             zDistance = characterFinal.worldZ - characterStart.worldZ;
         }

         System.out.println("Distanța calculată prin Z: " + zDistance);

         // Analizăm discrepanțele, dacă există
         if (Math.abs(zDistance - distanceRun) > 1) { // Toleranță mică pentru eroare
             System.out.println("Există o discrepanță între distanța calculată și cea afișată în joc.");
             System.out.println("Z Values during run: " + zValues);
         } else {
             System.out.println("Distanțele corespund.");
         }

         // Assert pentru a verifica distanța
         assertEquals("!!!Discrepanță între distanța calculată și cea afișată în joc.", distanceRun, Math.round(zDistance), 1);
 }

  /*  @Test
    public void testDistanceRunMultipleTimes() throws Exception {

        for (int i = 0; i < 10; i++) {
            mainMenuPage.loadScene();
            testDistanceRun();
        }
    }*/

}
