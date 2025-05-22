/**
 * Hlavná trieda aplikácie, ktorá slúži ako vstupný bod programu.
 * Vytvára a spúšťa hlavné okno aplikácie.
 *
 * @author Simon Kyselica
 */
package core;

public class Main {
    /**
     * Hlavná metóda, ktorá sa spustí pri štarte aplikácie.
     * Vytvorí nové okno s definovanou šírkou a výškou a následne ho spustí.
     *
     * @param args
     */
    public static void main(String[] args) {
        new Window(800, 600).run();
    }
}
